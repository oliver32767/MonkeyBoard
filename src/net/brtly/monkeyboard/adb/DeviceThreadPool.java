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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.brtly.monkeyboard.api.DeviceTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class DeviceThreadPool {
	private static final Log LOG = LogFactory.getLog(DeviceThreadPool.class);

	private Map<String, ExecutorService> _threads;

	protected DeviceThreadPool() {
		_threads = new ConcurrentHashMap<String, ExecutorService>();
	}

	protected void addThread(String serial) {
		LOG.trace("Adding thread: Device-thread-" + serial + "-%d");
		_threads.put(serial, Executors
				.newSingleThreadExecutor(new ThreadFactoryBuilder()
						.setNameFormat("Device-thread-" + serial + "-%d")
						.build()));
	}

	protected void removeThread(String serial) {
		LOG.trace("Removing thread: Device-thread-" + serial + "-%d");
		_threads.get(serial).shutdownNow();
		_threads.remove(serial);
	}
	
	protected void shutdown() {
		for (ExecutorService thread : _threads.values()) {
			thread.shutdown();
		}
	}

	protected ExecutorService getDeviceExecutor(String serial) {
		if (!_threads.containsKey(serial)) {
			throw new IllegalArgumentException("No thread for device " + serial);
		}
		return _threads.get(serial);
	}

}
