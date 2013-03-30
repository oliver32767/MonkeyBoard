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
 * This type of event is posted when a device loses focus, that is, it is no longer the "primary" device
 * @author obartley
 *
 */
public class DeviceUnfocusedEvent extends DeviceEvent {

	private String _focusedDevice;

	/**
	 * Create an instance of this event
	 * @param serial the serial number of the device losing focus
	 * @param focusedDevice the serial number of the device that will gain focus
	 */
	public DeviceUnfocusedEvent(String serial, String focusedDevice) {
		super(serial, Type.DEVICE_UNFOCUSED);
	}

	/**
	 * Get the serial number of the device that gained focus
	 * 
	 * @return The serial number of the device that gained focus. Can be null if
	 *         no device gained focus, such as when the focus device gets
	 *         disconnected.
	 */
	public String getFocusedDevice() {
		return _focusedDevice;
	}
}
