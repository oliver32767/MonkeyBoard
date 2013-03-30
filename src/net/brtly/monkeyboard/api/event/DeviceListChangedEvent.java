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

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * This event is posted when the list of devices connected to adb changes
 * 
 * @author obartley
 * 
 */
public class DeviceListChangedEvent {

	private Set<String> _deviceList;

	/**
	 * Create an instance of this event
	 * 
	 * @param deviceList
	 *            a Collection of strings representing the connected device's
	 *            serial numbers
	 */
	public DeviceListChangedEvent(Collection<String> deviceList) {
		_deviceList = ImmutableSet.copyOf(deviceList);
	}

	public Set<String> getDeviceSerialNumbers() {
		return _deviceList;
	}
}
