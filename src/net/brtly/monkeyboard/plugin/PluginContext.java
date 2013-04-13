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
package net.brtly.monkeyboard.plugin;

import com.google.common.eventbus.EventBus;

import net.brtly.monkeyboard.api.IDeviceManager;
import net.brtly.monkeyboard.api.plugin.IPluginContext;

public class PluginContext implements IPluginContext {

	private final IDeviceManager _deviceManager;
	private final EventBus _eventBus;
	
	public PluginContext(IDeviceManager deviceManager, EventBus eventBus) {
		_deviceManager = deviceManager;
		_eventBus = eventBus;
	}

	@Override
	public IDeviceManager getDeviceManager() {
		return _deviceManager;
	}

	@Override
	public EventBus getEventBus() {
		return _eventBus;
	}

}
