/*******************************************************************************
 * This file is part of MonkeyBoard
 * Copyright © 2013 Oliver Bartley
 * 
 * MonkeyBoard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * MonkeyBoard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MonkeyBoard.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.brtly.monkeyboard.api;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Utility class to help you define tasks to be run on a device thread, with
 * optional callbacks and a timeout.
 * 
 * To use, create an instance and Override
 * {@link #doInBackground(IDeviceController)}. This method will be executed on a
 * background thread once execute has been called.
 * 
 * To get callbacks and error notifications, Override
 * {@link #onProgressUpdate(Object)}, {@link #onPostExecute(Object)} and
 * {@link #onFailure(Exception)}.
 * 
 * 
 * @author obartley
 * 
 * @param <Progress>
 *            the type of Object passed to {@link #onProgressUpdate(Object)}.
 * @param <Result>
 *            the type of Object passed to {@link #onPostExecute(Object)} and
 *            represents the finished result of the Task.
 */
public abstract class DeviceTask<Progress, Result> {

	public enum Status {
		PENDING, RUNNING, CANCELLED, FAILED, FINISHED
	}

	private UUID _uuid = UUID.randomUUID();
	
	private Executor _callbackExecutor;

	private ExecutorService _workerExecutor;

	private Future<Result> _future;
	private volatile Status _status = Status.PENDING;

	/**
	 * Get a UUID that uniquely identifies this task
	 * @return
	 */
	public UUID getUUID() {
		return _uuid;
	}
	
	/**
	 * Execute this task with the given Executors
	 * 
	 * @param backgroundExecutor
	 *            the executor to which {@link #doInBackground(IDeviceController)}
	 *            will be added as a Runnable
	 * @param callbackExecutor
	 *            the executor to which callback methods will be added as
	 *            Runnables
	 * @param device
	 *            the IDevice that this task will be run against. Cannot be
	 *            null.
	 */
	public final void execute(ExecutorService backgroundExecutor,
			Executor callbackExecutor, final IDeviceController device) {
		execute(backgroundExecutor, callbackExecutor, device, 0);
	}

	/**
	 * Execute this task with the given Executors with an optional timeout.
	 * 
	 * @param backgroundExecutor
	 *            the executor to which {@link #doInBackground(IDeviceController)}
	 *            will be added as a Runnable
	 * @param callbackExecutor
	 *            the executor to which callback methods will be added as
	 *            Runnables
	 * @param device
	 *            the IDevice that this task will be run against. Cannot be
	 *            null.
	 * @param timeout
	 *            the timeout in milliseconds. pass 0 to run this task
	 *            indefinitely
	 */
	public final void execute(ExecutorService backgroundExecutor,
			Executor callbackExecutor, final IDeviceController device,
			final long timeout) {
		if (getStatus() == Status.RUNNING) {
			throw new IllegalStateException("DeviceTask is already running!");
		}
		if (getStatus() != Status.PENDING) {
			throw new IllegalStateException(
					"DeviceTask has already been executed!");
		}
		if (device == null) {
			throw new IllegalArgumentException(
					"Can't execute a task with no device!");
		}

		_callbackExecutor = callbackExecutor;

		// Add the task to the supplied executor thread pool, typically a
		// specific executor per device
		_future = backgroundExecutor.submit(new Callable<Result>() {
			@Override
			public Result call() throws Exception {
				return doInBackground(device);
			}
		});

		// Now add a monitor task to the worker thread
		/*
		 * TODO: if performance becomes an issue, look in to making
		 * workerExecutor a static instance of a cached thread pool
		 */
		_status = Status.RUNNING;
		_workerExecutor = Executors
				.newSingleThreadExecutor(new ThreadFactoryBuilder()
						.setNameFormat(
								"Device Thread Worker ("
										+ device.getSerialNumber() + ")-%d")
						.build());
		_workerExecutor.submit(new Runnable() {
			@Override
			public void run() {
				// now we can block this worker thread until the task completes
				// or we hit the (optional) timeout
				try {
					Result result;
					if (timeout > 0) {
						result = _future.get(timeout, TimeUnit.MILLISECONDS);
					} else {
						result = _future.get();
					}
					notifyFinished(result);
				} catch (InterruptedException e) {
					notifyFailed(e);
				} catch (TimeoutException e) {
					notifyFailed(e);
				} catch (ExecutionException e) {
					notifyFailed((Exception) e.getCause());
				}
				return;
			}

		});
	}

	/**
	 * Cancel this task
	 * 
	 * @return true if succeeded in cancelling the task. false otherwise. This
	 *         usually happens when the task has already been cancelled.
	 */
	public final boolean cancel() {
		if (getStatus() != Status.RUNNING) {
			throw new IllegalStateException("DeviceTask is not running!");
		}
		boolean rv = _future.cancel(true);
		if (rv) {
			notifyFailed(new InterruptedException("Task cancelled"));
			_status = Status.CANCELLED;
		}
		return (rv);
	}

	/**
	 * Get the current status of the task.
	 * 
	 * @return the current status of the task
	 */
	public final Status getStatus() {
		return _status;
	}

	/**
	 * Called from {@link #doInBackground(IDeviceController)} in order to send
	 * progress updates on the Callback thread
	 * 
	 * @param progress
	 */
	protected final void updateProgress(final Progress progress) {
		_callbackExecutor.execute(new Runnable() {
			@Override
			public void run() {
				onProgressUpdate(progress);
			}
		});
	}

	/**
	 * The only method that has to be overridden. This method defines to task to
	 * be done on a specific device.
	 * 
	 * @param device
	 *            a reference to the specific device this task is running
	 *            against
	 * @return the Result of the task
	 * @throws Exception
	 */
	protected abstract Result doInBackground(IDeviceController device)
			throws Exception;

	/**
	 * Override this method if you want to receive progress updates from the
	 * background thread. This method is run on the Callback thread when
	 * {@link #updateProgress()} is invoked from
	 * {@link #doInBackground(IDeviceController)}
	 * 
	 * @param progress
	 */
	protected void onProgressUpdate(Progress progress) {
	}

	/**
	 * Takes care of wrapping {@link #onPostExecute(Object)} in a Runnable and
	 * submiting it to the callback service
	 * 
	 * @param result
	 */
	private void notifyFinished(final Result result) {
		_status = Status.FINISHED;
		// System.out.println(_future.cancel(true));
		_callbackExecutor.execute(new Runnable() {
			@Override
			public void run() {
				onPostExecute(result);
			}
		});
		_workerExecutor.shutdownNow();
	}

	/**
	 * Override this method if you want to receive a result from
	 * {@link #doInBackground(IDeviceController)} This method is automatically
	 * called on the callback thread when {@link #doInBackground(IDeviceController)}
	 * completes.
	 * 
	 * @param result
	 */
	protected void onPostExecute(Result result) {
	}

	/**
	 * Takes care of wrapping {@link #onFailure(Exception)} in a Runnable and
	 * submiting it to the callback service
	 * 
	 * @param result
	 */
	private void notifyFailed(final Exception throwable) {
		_status = Status.FAILED;
		_callbackExecutor.execute(new Runnable() {
			@Override
			public void run() {
				onFailure(throwable);
			}
		});
		_workerExecutor.shutdownNow();
	}

	/**
	 * Override this method if you want to receive notification that the task
	 * failed. This method is automatically called on the callback thread when
	 * {@link #doInBackground(IDeviceController)} throws an Exception
	 * 
	 * @param err
	 *            the exception thrown in {@link #doInBackground(IDeviceController)}
	 */
	protected void onFailure(Exception err) {
	}
}
