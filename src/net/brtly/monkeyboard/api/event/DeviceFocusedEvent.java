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
package net.brtly.monkeyboard.api.event;

/**
 * This event is posted when a device is given focus, that is, becomes the
 * "primary" device
 * 
 * @author obartley
 * 
 */
public class DeviceFocusedEvent extends DeviceEvent {

	private String _unfocusedDevice;

	/**
	 * Create an instance of this event to post
	 * 
	 * @param serial
	 *            the serial number of the device gaining focus
	 * @param unfocusedDevice
	 *            the serial number of the device that has lost focus, can be
	 *            null
	 */
	public DeviceFocusedEvent(String serial, String unfocusedDevice) {
		super(serial, Type.DEVICE_FOCUSED);
		_unfocusedDevice = unfocusedDevice;
	}

	/**
	 * Get the serial number of the device that lost focus
	 * 
	 * @return The serial number of the device that lost focus. Can be null if
	 *         no device gained focus, such as when the only connected device
	 *         gets focus.
	 */
	public String getUnfocusedDevice() {
		return _unfocusedDevice;
	}

}
