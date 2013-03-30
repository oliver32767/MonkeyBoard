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
 * Base class from which all other events associated with a specific device are
 * derived. Subscribe to these evens to receive all device events.
 * 
 * @author obartley
 * 
 */
public abstract class DeviceEvent {

	/**
	 * Used to determine the type of event if your subscriber is subscribed to
	 * a common superclass.
	 * 
	 * @author obartley
	 * 
	 */
	public static enum Type {
		DEVICE_CONNECTED, DEVICE_DISCONNECTED, DEVICE_FOCUSED, DEVICE_UNFOCUSED, DEVICE_STATE_CHANGED, DEVICE_BUILD_INFO_CHANGED
	}
	
	private String _serial;
	private Type _type;
	
	/**
	 * An event that indicates that a device has changed state in some way.
	 * @param serial the originating device's serial number
	 * @param type the type of change that occurred
	 */
	public DeviceEvent(String serial, Type type) {
		_serial = serial;
		_type = type;
	}

	/**
	 * Get the serial number of the originating device
	 * 
	 * @return the device's serial number
	 */
	public final String getSerialNumber() {
		return _serial;
	}

	/**
	 * Get the type of the event. This is useful if you want to subscribe to a
	 * common superclass of an event, i.e. Subscribing to
	 * DeviceConnectionChangedEvents instead of Subscribing to
	 * DeviceConnectedEvent and DeviceDisconnectedEvent separately.
	 * 
	 * @return the type of event
	 */
	public final Type getEventType() {
		return _type;
	}
	
	@Override
	public String toString() {
		return String.format("%s:%s@%s", this.getClass().toString(), getEventType().toString(), getSerialNumber());
	}
}
