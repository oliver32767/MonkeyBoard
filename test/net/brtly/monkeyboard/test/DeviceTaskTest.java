package net.brtly.monkeyboard.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.SocketException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.TestCase;
import net.brtly.monkeyboard.api.DeviceTask;
import net.brtly.monkeyboard.api.IDeviceController;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DeviceTaskTest extends TestCase {

	private int _currentProgress;
	private boolean _finalResult;
	private Exception _error;

	private CountDownLatch _endSignal;
	
	ExecutorService _background = Executors.newSingleThreadExecutor();
	Executor _callback = Executors.newSingleThreadExecutor();

	@Before
	public void setUp() {
		_background = Executors.newSingleThreadExecutor();
		_callback = Executors.newSingleThreadExecutor();
		
		_endSignal = new CountDownLatch(1);
		_currentProgress = 0;
		_finalResult = false;
		_error = null;
	}

	@After
	public void tearDown() {
		_background.shutdownNow();
	}

	@Test
	public void testProgressAndSuccess() throws Exception {

		DeviceTask<Integer, Boolean> task = new DeviceTask<Integer, Boolean>() {

			@Override
			public Boolean doInBackground(IDeviceController device)
					throws Exception {
				updateProgress(100);
				return true;
			}

			@Override
			public void onProgressUpdate(Integer progress) {
				_currentProgress = progress;
			}

			@Override
			public void onPostExecute(Boolean result) {
				_finalResult = result;
				_endSignal.countDown();
			}

		};

		task.execute(_background, _callback, new IDeviceStub("stub-0"));

		_endSignal.await(1000, TimeUnit.MILLISECONDS);
		
		assertThat(_finalResult, is(true));
		assertThat(_currentProgress, is(100));
	}

	@Test
	public void testError() throws Exception {
		DeviceTask<Integer, Boolean> task = new DeviceTask<Integer, Boolean>() {

			@Override
			public Boolean doInBackground(IDeviceController device)
					throws Exception {
				throw new SocketException();
			}

			@Override
			public void onFailure(Exception err) {
				_error = err;
				_endSignal.countDown();
			}

		};

		task.execute(_background, _callback,
				(IDeviceController) new IDeviceStub("stub-0"));
		
		_endSignal.await(1000, TimeUnit.MILLISECONDS);
		
		assertTrue(_error instanceof SocketException);
	}

	@Test
	public void testTimeout() throws Exception {
		DeviceTask<Integer, Boolean> task = new DeviceTask<Integer, Boolean>() {

			@Override
			public Boolean doInBackground(IDeviceController device)
					throws Exception {
				// infinite loop
				while (true) {
					assert Boolean.TRUE;
				}
			}
			
			@Override
			public void onFailure(Exception err) {
				_error = err;
				_endSignal.countDown();
			}
		};

		task.execute(_background, _callback,
				(IDeviceController) new IDeviceStub("stub-0"), 1000);
		
		_endSignal.await(1500, TimeUnit.MILLISECONDS);
		
		assertTrue(_error instanceof TimeoutException);

	}
	
	@Test
	public void testCancel() throws Exception {
		final CountDownLatch startSignal = new CountDownLatch(10);
		
		DeviceTask<Integer, Boolean> task = new DeviceTask<Integer, Boolean>() {

			@Override
			public Boolean doInBackground(IDeviceController device)
					throws Exception {
				// infinite loop
				while (true) {
					assert Boolean.TRUE;
					startSignal.countDown();
				}
			}
			
			@Override
			public void onFailure(Exception err) {
				_error = err;
				_endSignal.countDown();
			}
		};
		
		task.execute(_background, _callback,
				(IDeviceController) new IDeviceStub("stub-0"));
		
		startSignal.await(1000, TimeUnit.MILLISECONDS);
		task.cancel();
		_endSignal.await(500, TimeUnit.MILLISECONDS);
		assertTrue(_error instanceof InterruptedException);
	}
	
	/**
	public void testMemoryAccess() {
		DeviceTask<Integer, Boolean> task = new DeviceTask<Integer, Boolean>() {

			@Override
			protected Boolean doInBackground(IDeviceController device)
					throws Exception {
				// infinite loop
				while (true) {
					updateProgress(1);
				}
			}

			@Override
			protected void onProgressUpdate(Integer progress) {
				_currentProgress += progress;
			}
		};
		task.execute(_background, _callback,
				(IDeviceController) new IDeviceStub("stub-0"), 5000);

		ExecutorService background2 = Executors.newSingleThreadExecutor();
		DeviceTask<Integer, Boolean> task2 = new DeviceTask<Integer, Boolean>() {

			@Override
			protected Boolean doInBackground(IDeviceController device)
					throws Exception {
				// infinite loop
				while (true) {
					updateProgress(-1);
				}
			}

			@Override
			protected void onProgressUpdate(Integer progress) {
				_currentProgress += progress;
			}
		};
		task2.execute(background2, _callback,
				(IDeviceController) new IDeviceStub("stub-0"), 5000);


		System.out.println("Concurrency score: " + _currentProgress);
	}
	*/

}
