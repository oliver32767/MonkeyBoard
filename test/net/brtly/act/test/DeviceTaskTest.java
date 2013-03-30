package net.brtly.act.test;

import java.net.SocketException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.TestCase;
import net.brtly.monkeyboard.api.DeviceTask;
import net.brtly.monkeyboard.api.DeviceTask.Status;
import net.brtly.monkeyboard.api.IDeviceController;

import org.junit.Test;

public class DeviceTaskTest extends TestCase {

	private static final long DEFAULT_SLEEP = 500;
	
	private int _currentProgress;
	private boolean _finalResult;
	private Exception _error;
	ExecutorService _background = Executors.newSingleThreadExecutor();
	Executor _callback = Executors.newSingleThreadExecutor();
	
	@Override
	public void setUp() {
		System.out.println("Setting up...");
		_background = Executors.newSingleThreadExecutor();
		_callback = Executors.newSingleThreadExecutor();
		
		_currentProgress = 0;
		_finalResult = false;
		_error = null;
	}
	
	@Override
	public void tearDown() {
		System.out.println("Tearing down...");
		_background.shutdownNow();
	}
	
	public void nop() {
		nop(0);
	}
	public void nop(long sleep) {
		if (sleep > 0) {
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			assert Boolean.TRUE;
		}
	}
	
	@Test
	public void testCallbacks() {

		DeviceTask<Integer, Boolean> task = new DeviceTask<Integer, Boolean>() {

			@Override
			protected Boolean doInBackground(IDeviceController device) throws Exception {
				System.out.println("Running task for: " + device.getSerialNumber());
				for (int i = 0; i <=100; i++) {
					updateProgress(i);
				}
				return true;
			}
			
			@Override
			protected void onProgressUpdate(Integer progress) {
				_currentProgress = progress;
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				_finalResult = true;
			}
			
		};
		
		task.execute(_background, _callback, (IDeviceController) new IDeviceStub("stub-0"));
		
		while (task.getStatus() == Status.PENDING || task.getStatus() == Status.RUNNING) {
			nop(DEFAULT_SLEEP);
		}
		System.out.println(_currentProgress);
	}
	
	public void testError() {
		DeviceTask<Integer, Boolean> task = new DeviceTask<Integer, Boolean>() {

			@Override
			protected Boolean doInBackground(IDeviceController device)
					throws Exception {
				throw new SocketException();
			}
			
			@Override 
			protected void onFailure(Exception err) {
				err.printStackTrace();
			}
			
		};
		
		task.execute(_background, _callback, (IDeviceController) new IDeviceStub("stub-0"));
		
		while (task.getStatus() == Status.PENDING || task.getStatus() == Status.RUNNING) {
			System.out.println(task.getStatus().toString());
			nop(DEFAULT_SLEEP);
		}
	}
	
	public void testTimeout() {
		DeviceTask<Integer, Boolean> task = new DeviceTask<Integer, Boolean>() {

			@Override
			protected Boolean doInBackground(IDeviceController device)
					throws Exception {
				// infinite loop
				while (true) {
					assert Boolean.TRUE;
				}
			}
		};
		
		task.execute(_background, _callback, (IDeviceController) new IDeviceStub("stub-0"), 1000);
		
		while (task.getStatus() == Status.PENDING || task.getStatus() == Status.RUNNING) {
			nop(DEFAULT_SLEEP);
		}
	}
	
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
		task.execute(_background, _callback, (IDeviceController) new IDeviceStub("stub-0"), 5000);

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
		task2.execute(background2, _callback, (IDeviceController) new IDeviceStub("stub-0"), 5000);
		
		while (task.getStatus() == Status.PENDING || task.getStatus() == Status.RUNNING) {
			nop(DEFAULT_SLEEP);
			System.out.println(_currentProgress);
		}
		System.out.println("Concurrency score: " + _currentProgress);
	}

}
