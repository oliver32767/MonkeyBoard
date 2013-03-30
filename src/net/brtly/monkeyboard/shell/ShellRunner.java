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
package net.brtly.monkeyboard.shell;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import net.brtly.monkeyboard.MonkeyBoard;
import net.brtly.monkeyboard.adb.DeviceThreadPool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Fake AndroidDebugBridge class
 * 
 * @author obartley
 * 
 */
public class ShellRunner {
	private static final Log LOG = LogFactory.getLog(ShellRunner.class);

	private static ShellRunner _instance = null;
	private ExecutorService _commandPool;

	private ShellRunner() {
		_commandPool = Executors.newCachedThreadPool();
	}
	
	/**
	 * Shutdown the device manager
	 */
	public static void shutdown() {
		_instance.shutdownInstance(false);
		_instance = null;
	}
	/**
	 * Shutdown the device manager now
	 */	
	public static void shutdownNow() {
		_instance.shutdownInstance(true);
		_instance = null;		
	}
	
	private void shutdownInstance(boolean now) {
		if (now) {
			_commandPool.shutdownNow();
		} else {
			_commandPool.shutdown();
		}
	}
	
	public void runCommandAsync(ShellCommand command) {
		_commandPool.submit(command);
	}
}
