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
package net.brtly.monkeyboard.adb;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.brtly.monkeyboard.api.DeviceTask;
import net.brtly.monkeyboard.api.IDeviceController;
import net.brtly.monkeyboard.api.IDeviceController.DeviceState;
import net.brtly.monkeyboard.api.IDeviceManager;
import net.brtly.monkeyboard.api.event.DeviceBuildInfoChangedEvent;
import net.brtly.monkeyboard.api.event.DeviceConnectedEvent;
import net.brtly.monkeyboard.api.event.DeviceDisconnectedEvent;
import net.brtly.monkeyboard.api.event.DeviceFocusedEvent;
import net.brtly.monkeyboard.api.event.DeviceListChangedEvent;
import net.brtly.monkeyboard.api.event.DeviceManagerStateChangedEvent;
import net.brtly.monkeyboard.api.event.DeviceStateChangedEvent;
import net.brtly.monkeyboard.api.event.DeviceUnfocusedEvent;
import net.brtly.monkeyboard.gui.EventQueueExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.android.chimpchat.adb.AdbChimpDevice;
import com.android.chimpchat.core.IChimpDevice;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IClientChangeListener;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.Client;
import com.android.ddmlib.IDevice;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Encapsulates AndroidDebugBridge AdbAdapter listens to events and posts
 * corresponding events to the event bus
 * 
 * @author obartley
 * 
 */
public final class DeviceManager implements IDeviceManager,
		AndroidDebugBridge.IDebugBridgeChangeListener, IClientChangeListener,
		IDeviceChangeListener {

	private static final Log LOG = LogFactory.getLog(DeviceManager.class);

	private static final DeviceManager INSTANCE = new DeviceManager();
	private static boolean _isInit = false;

	private DeviceManagerState _state;
	private EventBus _eventBus;
	private DeviceThreadPool _deviceThreadPool;
	private Map<String, DeviceState> _devices;
	private Map<String, IChimpDevice> _chimpDevices;
	private String _focusedDevice;
	private Object _focusedDeviceLock = new Object(); // FIXME is this a smell?

	private String _osLocation;

	ExecutorService _workerExecutor;

	private DeviceManager() {
		LOG.trace("created DeviceManager");
	}

	/**
	 * Initialize the main DeviceManager instance. #init() can only be called
	 * once, otherwise an IllegalStateException is thrown.
	 * 
	 * @param eventBus
	 */
	public static void init(EventBus eventBus) {
		synchronized (INSTANCE) {
			if (_isInit) {
				throw new IllegalStateException("DeviceManager already inited!");
			}

			LOG.debug("DeviceManager INIT");
			INSTANCE._eventBus = eventBus;
			INSTANCE._deviceThreadPool = new DeviceThreadPool();
			INSTANCE._devices = new ConcurrentHashMap<String, DeviceState>();
			INSTANCE._chimpDevices = new ConcurrentHashMap<String, IChimpDevice>();
			INSTANCE._focusedDevice = null;

			// This mess makes a single threaded executor capable of executing
			// INSTANCE._workerExecutor =
			// MoreExecutors.listeningDecorator(Executors
			// .newSingleThreadExecutor(new ThreadFactoryBuilder()
			// .setNameFormat("DeviceManager-Worker-%d").build()));
			
			INSTANCE._workerExecutor = Executors
					.newSingleThreadExecutor(new ThreadFactoryBuilder()
							.setNameFormat("DeviceManager").build());
//			INSTANCE._workerExecutor = Executors
//					.newCachedThreadPool(new ThreadFactoryBuilder()
//							.setNameFormat("DeviceManager-worker-%d").build());
			INSTANCE._state = DeviceManagerState.STOPPED;
			_isInit = true;
		}
	}

	/**
	 * Shutdown DeviceManager This is intended to ONLY be called when the
	 * appplication is exiting
	 */
	public static void shutdown() {
		AndroidDebugBridge.terminate();
	}

	/**
	 * Get an instance of device manager
	 * 
	 * @return
	 */
	public static IDeviceManager getDeviceManager() {
		return INSTANCE;
	}

	/**
	 * Prepare AndroidDebugBridge and start the DeviceManager
	 * 
	 * @param osLocation
	 * @param clientSupport
	 */
	public static void start(final String osLocation) {
		if (!_isInit) {
			throw new IllegalStateException("DeviceManager not inited!");
		}

		if (INSTANCE._state == DeviceManagerState.STARTING
				|| INSTANCE._state == DeviceManagerState.RUNNING) {
			throw new IllegalStateException("DeviceManager already running!");
		}
		INSTANCE._workerExecutor.submit(new Runnable() {
			@Override
			public void run() {
				startAdb(osLocation);
				return;
			}
		});
	}

	/**
	 * This method is executed in a worker thread started in {@link #start()}
	 * 
	 * @param osLocation
	 * @param clientSupport
	 */
	private static void startAdb(String osLocation) {
		synchronized (INSTANCE) {
			INSTANCE.setState(DeviceManagerState.STARTING);

			INSTANCE._osLocation = osLocation;

			try {
				AndroidDebugBridge.init(false); // true == Mode 1:The library
				// monitors the devices and the
				// applications running on them.
				// false == Mode 2: The library only
				// monitors devices

			} catch (IllegalStateException e) {
				LOG.trace("AndroidDebugBridge.init() has already been called. Caught exception.");
			}

			AndroidDebugBridge.addClientChangeListener(INSTANCE);
			AndroidDebugBridge.addDebugBridgeChangeListener(INSTANCE);
			AndroidDebugBridge.addDeviceChangeListener(INSTANCE);

			LOG.trace("Attempting to connect to the currently running adb server");
			AndroidDebugBridge.createBridge();

			// FIXME: make this more fault tolerant (i.e. handle the condition
			// where adb is not currently running)

			// if (!AndroidDebugBridge.getBridge().isConnected()) {
			// LOG.debug("failed to connect. attempting to start a server with "
			// + osLocation);
			// AndroidDebugBridge.createBridge(osLocation, false);
			// if (!AndroidDebugBridge.getBridge().isConnected()) {
			// LOG.warn("failed to connect!");
			// stopAdb();
			// INSTANCE.setState(DeviceManagerState.FAILED);
			// return;
			// }
			// }
			INSTANCE.setState(DeviceManagerState.RUNNING);
		}
	}

	/**
	 * Stops the DeviceManager and terminates AndroidDebugBridge
	 */
	public static void stop() {
		if (INSTANCE.getState() == DeviceManagerState.STOPPED
				|| INSTANCE.getState() == DeviceManagerState.STOPPING) {
			throw new IllegalStateException("DeviceManager already shutdown");
		}
		INSTANCE._workerExecutor.submit(new Runnable() {
			@Override
			public void run() {

				stopAdb();
				return;
			}
		});
	}

	/**
	 * This method is executed in a worker thread started in {@link #stop()}
	 * 
	 * @param osLocation
	 * @param clientSupport
	 */
	private static void stopAdb() {
		synchronized (INSTANCE) {
			INSTANCE.setState(DeviceManagerState.STOPPING);

			INSTANCE._deviceThreadPool.shutdown();
			INSTANCE.removeAllDevices();
			// LOG.trace("Devices removed");
			AndroidDebugBridge.removeClientChangeListener(INSTANCE);
			AndroidDebugBridge.removeDebugBridgeChangeListener(INSTANCE);
			AndroidDebugBridge.removeDeviceChangeListener(INSTANCE);

			AndroidDebugBridge.disconnectBridge();
			// AndroidDebugBridge.terminate();
			INSTANCE.setState(DeviceManagerState.STOPPED);
		}
	}

	private void addDevice(IDevice device) {
		synchronized (_devices) {
			LOG.trace("Adding device:" + device.getSerialNumber());
			_deviceThreadPool.addThread(device.getSerialNumber());
			_devices.put(device.getSerialNumber(),
					DeviceState.fromIDeviceState(device.getState()));
			_eventBus.post(new DeviceConnectedEvent(device.getSerialNumber()));
			_eventBus.post(new DeviceStateChangedEvent(
					device.getSerialNumber(), IDeviceController.DeviceState
							.fromIDeviceState(device.getState())));
			_eventBus.post(new DeviceListChangedEvent(getDeviceSerialNumbers()));
		}
	}

	private IDeviceController getDeviceController(String serial) {
		for (IDevice device : AndroidDebugBridge.getBridge().getDevices()) {
			if (serial.equals(device.getSerialNumber())) {
				if (_chimpDevices.containsKey(serial)) {
					return new DeviceController(device,
							_chimpDevices.get(serial));
				} else {
					return new DeviceController(device, null);
				}
			}
		}
		throw new IllegalArgumentException("Device " + String.valueOf(serial)
				+ " is not connected!");
	}

	private void removeDevice(String serial) {
		synchronized (_devices) {
			LOG.trace("Removing device:" + serial);
			_deviceThreadPool.removeThread(serial);
			if (getFocusedDevice() != null && getFocusedDevice().equals(serial)) {
				setFocusedDevice(null);
			}
			_devices.remove(serial);
			_eventBus.post(new DeviceDisconnectedEvent(serial));
			_eventBus.post(new DeviceListChangedEvent(getDeviceSerialNumbers()));
		}
	}

	private void removeAllDevices() {
		for (String serial : _devices.keySet()) {
			removeDevice(serial);
		}
	}

	/**
	 * This method will check the status of an IDevice and see we need to create
	 * an IChimpDevice or to refresh the ChimpDevice. This method is responsible
	 * for sending state change events from ONLINE to MONKEY and vice versa.
	 * 
	 * Eventually, this method will be called at regular intervals to ensure
	 * that the AdbChimpDevice doesn't get in to a funnky state without alerting
	 * someone
	 * 
	 * @param device
	 */
	private void addChimpDevice(final IDevice device) {
		
		final Future<Void> future = _workerExecutor.submit(new Callable<Void>() {
			@Override
			public Void call() {
				LOG.trace("Creating IChimpDevice for " + device.getSerialNumber());
				IChimpDevice chimp = new AdbChimpDevice(device);
				synchronized (_devices) {
					_chimpDevices.put(device.getSerialNumber(), chimp);
					_devices.put(device.getSerialNumber(), DeviceState.MONKEY);
				}
				_eventBus.post(new DeviceStateChangedEvent(device
						.getSerialNumber(), DeviceState.MONKEY));				
				return null;
			}
		});
		
		Thread timeout = new Thread() {
			@Override
			public void run() {
				try {
					future.get(10, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		timeout.start();
	}

	private void setState(DeviceManagerState state) {
		_state = state;
		LOG.debug("DeviceManager " + String.valueOf(state));
		_eventBus.post(new DeviceManagerStateChangedEvent(state));
	}

	// IDeviceManager methods /////////////////////////////////////////////////
	//
	// These methods need to be thread safe, as they represent the interface
	// between
	// Plugins and the DeviceManager internal state
	//

	@Override
	public DeviceManagerState getState() {
		return _state;
	}

	@Override
	public Set<String> getDeviceSerialNumbers() {
		return ImmutableSet.copyOf(_devices.keySet());
	}

	@Override
	public net.brtly.monkeyboard.api.IDeviceController.DeviceState getDeviceState(
			String serialNumber) {
		return _devices.get(serialNumber);
	}

	@Override
	public String getFocusedDevice() {
		return _focusedDevice;
	}

	@Override
	public void setFocusedDevice(String serial) {
		synchronized (_focusedDeviceLock) {
			LOG.trace("Setting focused device:" + serial);

			// this may be smelly, but it's better than a bunch of null checks
			if (String.valueOf(serial).equals(String.valueOf(_focusedDevice))) {
				LOG.debug("Device already focused");
				return;
			}

			if (serial != null && !_devices.containsKey(serial)) {
				LOG.debug("Device not found!");
				return;
			}

			String unfocusedDevice = _focusedDevice;
			_focusedDevice = serial;

			if (_focusedDevice != null) {
				_eventBus.post(new DeviceFocusedEvent(_focusedDevice,
						unfocusedDevice));
			}
			if (unfocusedDevice != null) {
				_eventBus.post(new DeviceUnfocusedEvent(unfocusedDevice,
						_focusedDevice));
			}
		}
	}

	@Override
	public void submitTask(String serial, DeviceTask<?, ?> task) {
		LOG.trace("Submitting task: " + String.valueOf(task));
		task.execute(_deviceThreadPool.getDeviceExecutor(serial),
				new EventQueueExecutor(), getDeviceController(serial));
	}

	// ////////////////////////////////////////////////////////////////////////
	// These methods listen to AndroidDebugBridge events and dispatch the
	// corresponding
	// events to the notify methods
	@Override
	public void deviceConnected(IDevice device) {
		LOG.trace("(adb) Device connected:" + device.getSerialNumber());
		addDevice(device);
		if (device.getState() == IDevice.DeviceState.ONLINE) {
			addChimpDevice(device);
		}
	}

	@Override
	public void deviceDisconnected(IDevice device) {
		LOG.trace("(adb) Device disconnected:" + device.getSerialNumber());
		removeDevice(device.getSerialNumber());
	}

	@Override
	public void deviceChanged(IDevice device, int changeMask) {
		LOG.trace("(adb) Device changed:" + device.toString() + " ["
				+ changeMask + "]");
		if (changeMask == IDevice.CHANGE_STATE) {
			_eventBus.post(new DeviceStateChangedEvent(_focusedDevice,
					DeviceState.fromIDeviceState(device.getState())));
			_devices.put(device.getSerialNumber(),
					DeviceState.fromIDeviceState(device.getState()));
			if (device.getState() == IDevice.DeviceState.ONLINE) {
				addChimpDevice(device);
			}
		} else if (changeMask == IDeviceController.CHANGE_BUILD_INFO) {
			_eventBus.post(new DeviceBuildInfoChangedEvent(device
					.getSerialNumber()));
		}
	}

	@Override
	public void bridgeChanged(AndroidDebugBridge bridge) {
		LOG.trace("(adb) Bridge changed:" + String.valueOf(bridge));
	}

	@Override
	public void clientChanged(Client client, int changeMask) {
		LOG.trace("(adb) Client changed:" + String.valueOf(client) + " ["
				+ changeMask + "]");
	}
}
