/*******************************************************************************
 * This file is part of MonkeyBoard
 * Copyright � 2013 Oliver Bartley
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

/**
 * Objects implementing IPluginContext provide access to runtime objects,
 * typically to a Plugin instance.
 * 
 * @author obartley
 * 
 */
public interface IPluginContext {

	/**
	 * Access the DeviceManager
	 * 
	 * @return
	 */
	public IDeviceManager getDeviceManager();

	/**
	 * Access the EventBus in order to post and subscribe to events
	 * 
	 * @return
	 */
	public IEventBus getEventBus();

	/**
	 * Get the current plugins Dockable frame.
	 * 
	 * @return
	 */
	public PluginDockable getDockable();

}
