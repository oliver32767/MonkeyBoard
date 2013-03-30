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

import java.util.Set;

import net.brtly.monkeyboard.api.IDeviceController.DeviceState;

public interface IDeviceManager {
	
	public enum DeviceManagerState {
		STARTING,
		RUNNING,
		STOPPING,
		STOPPED,
		FAILED
	}
	
	/**
	 * Get the status of the DeviceManager
	 * @return the status of the device manager, either running or stopped
	 */
	public DeviceManagerState getState();

	
	/**
	 * Get a set of Strings representing the connected device's serial numbers,
	 * which can be used to run tasks on a device and query information about a
	 * device.
	 * 
	 * @return
	 */
	public Set<String> getDeviceSerialNumbers();

	/**
	 * 
	 * @param serialNumber
	 *            the serial number of the device whose status will be returned
	 * @return the status of the specified device
	 */
	public DeviceState getDeviceState(String serialNumber);

	/**
	 * Get the device that is currently focused, that is, the device considered
	 * "default"
	 * 
	 * @return the serial number of the focused, default device
	 */
	public String getFocusedDevice();

	/**
	 * Give focus to the device specified by the supplied serial number
	 * 
	 * @param serial
	 *            device serial number that should receive focus
	 */
	public void setFocusedDevice(String serial);

	/**
	 * Add a DeviceTask to the associated device's task queue.
	 * 
	 * @param serial the serial number of the device on which this task will run
	 * @param task 
	 */
	public void submitTask(String serial, DeviceTask<?, ?> task);

}
