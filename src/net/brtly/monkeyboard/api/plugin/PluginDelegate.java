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
package net.brtly.monkeyboard.api.plugin;

import net.brtly.monkeyboard.api.IDeviceManager;

import org.ini4j.Ini;

import com.google.common.eventbus.EventBus;

public class PluginDelegate {
	
	private final IPluginContext _context;
	
	public PluginDelegate(IPluginContext context) {
		_context = context;
	}
	
	public final String getPluginName() {
		return _context.getPluginName();
	}
	
	public final IDeviceManager getDeviceManager() {
		return _context.getDeviceManager();
	}
	
	public final EventBus getEventBus() {
		return _context.getEventBus();
	}
	
	// Lifecycle Methods ///////////////////////////////////////////////////////
	
	public void onPluginLoaded() {
		
	}
	
	public void onRestorePluginState(Ini preferences) {
		
	}

	public Ini onSavePluginState() {
		return null;
	}
	
	public void onPluginUnloaded() {
		
	}
}
