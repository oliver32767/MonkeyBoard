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

import com.android.ddmlib.IDevice;

/**
 * Extends the ddmlib IDevice interface and adds additional values for DeviceState
 * @author obartley
 *
 */
public interface IDeviceController extends com.android.ddmlib.IDevice, com.android.chimpchat.core.IChimpDevice {

	public static enum DeviceState {
		BOOTLOADER,
		RECOVERY,
		OFFLINE,
		ONLINE,
		MONKEY,
		UNKNOWN;
		
		public static DeviceState fromIDeviceState(IDevice.DeviceState state) {
			switch (state) {
			case BOOTLOADER:
				return DeviceState.BOOTLOADER;
			case RECOVERY:
				return DeviceState.RECOVERY;
			case OFFLINE:
				return DeviceState.OFFLINE;
			case ONLINE:
				return DeviceState.ONLINE;
			default:
				return DeviceState.UNKNOWN;
			}
		}
	}
}
