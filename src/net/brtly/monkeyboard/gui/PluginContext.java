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
package net.brtly.monkeyboard.gui;

import net.brtly.monkeyboard.api.IDeviceManager;
import net.brtly.monkeyboard.api.IEventBus;
import net.brtly.monkeyboard.api.IPluginContext;
/**
 * @see IPluginContext
 * @author obartley
 *
 */
public class PluginContext implements IPluginContext {

	private IDeviceManager _deviceManager;
	private IEventBus _eventBus;
	
	public PluginContext(IDeviceManager deviceManager, IEventBus eventBus) {
		_deviceManager = deviceManager;
		_eventBus = eventBus;
	}
	
	@Override
	public IDeviceManager getDeviceManager() {
		return _deviceManager;
	}

	@Override
	public IEventBus getEventBus() {
		return _eventBus;
	}

}
