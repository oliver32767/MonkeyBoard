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

import net.brtly.monkeyboard.api.IDeviceController;

/**
 * This event is posted when a device changes state. NOTE: there are additional
 * states provided by IDeviceController that are different than those in
 * com.android.ddmlib.IDevice.DeviceState
 * 
 * @author obartley
 * 
 */
public class DeviceStateChangedEvent extends DeviceEvent {
	private IDeviceController.DeviceState _state;

	public DeviceStateChangedEvent(String serial,
			IDeviceController.DeviceState state) {
		super(serial, Type.DEVICE_STATE_CHANGED);
		_state = state;
	}

	public IDeviceController.DeviceState getState() {
		return _state;
	}
}
