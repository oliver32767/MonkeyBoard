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
 * This event is posted when a device is connected to ADB. It is important to
 * remember that a connected device is not implicitly ONLINE, you still need to
 * listen for {@link DeviceStateChangedEvent} to determine when the device comes
 * ONLINE
 * 
 * @author obartley
 * 
 */
public class DeviceConnectedEvent extends DeviceEvent {

	public DeviceConnectedEvent(String serial) {
		super(serial, Type.DEVICE_CONNECTED);
	}

}
