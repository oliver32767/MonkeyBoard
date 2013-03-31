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

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Subclasses of PluginPanel annotated with
 * {@link net.brtly.monkeyboard.api.Plugin} will be available to users in the
 * View menu.
 * 
 * @author obartley
 * 
 */
public class PluginPanel extends JPanel {

	private static final Log LOG = LogFactory.getLog(PluginPanel.class);
	private PluginDockable _dockable;
	private IDeviceManager _deviceManager;
	private IEventBus _eventBus;

	private String _title = null;
	private Icon _icon = null;
	private boolean _allowDuplicates = true;

	/**
	 * Create an instance of a PluginPanel. There should be no need to provide a
	 * subclass of PluginPanel with a constructor, as {@link #onCreate()} is
	 * called implicitly. You should really do your initialization there.
	 * 
	 * @param context
	 *            the context object that allows a plugin to access runtime
	 *            objects
	 * @param dockable
	 *            the dockable frame that this PluginPanel is hosted in
	 */
	public PluginPanel() {
		Plugin anno = getClass().getAnnotation(Plugin.class);
		if (anno != null) {
			_title = anno.title();
			try {
				_icon = new ImageIcon(anno.icon());
			} catch (Exception e) {
				_icon = new ImageIcon("res/img/android.png");
			}
			_allowDuplicates = anno.allowDuplicates();
		}
	}

	/**
	 * Get the dockable's current title. The default title is set with the @Plugin
	 * annotation.
	 * 
	 * @return
	 */
	public final String getTitle() {
		return _title;
	}

	/**
	 * Set the dockable's title.
	 * 
	 * @param title
	 */
	public final void setTitle(String title) {
		_title = title;
		if (_dockable != null) {
			_dockable.setTitleText(_title);			
		}
	}

	/**
	 * Get the dockable's current icon. The default icon is set with the @Plugin
	 * annotation.
	 * 
	 * @return
	 */
	public final Icon getIcon() {
		return _icon;
	}

	/**
	 * Set the dockable's icon.
	 * 
	 * @param icon
	 */
	public final void setIcon(Icon icon) {
		_icon = icon;
		if (_dockable != null) {
			_dockable.setTitleIcon(_icon);
		}
	}

	/**
	 * Ask whether this plugin allows duplicate instances or not
	 * 
	 * @return
	 */
	public final boolean allowDuplicates() {
		return _allowDuplicates;
	}

	/**
	 * Return a PluginDockable object, which allows plugins to customize their
	 * dockable "window" and listen to user events.
	 * 
	 * @return
	 */
	public final PluginDockable getDockable() {
		return _dockable;
	}

	/**
	 * Return a DeviceManager object, which allows plugins to obtain information
	 * about connected devices and submit DeviceTasks.
	 * 
	 * @return
	 */
	public final IDeviceManager getDeviceManager() {
		return _deviceManager;
	}

	/**
	 * Return an EventBus object, which allows plugins to subscribe to posted
	 * events
	 * 
	 * @return
	 */
	public final IEventBus getEventBus() {
		return _eventBus;
	}

	// LIFECYCLE METHODS ///////////////////////////////////////////////////////

	/**
	 * Attach this activity to a dockable and provide interfaces to runtime
	 * objects
	 * 
	 * @param dockable the PluginDockable that this plugin is hosted in
	 * @param deviceManager an interface to the global DeviceManager
	 * @param eventBus as interface to the global EventBus
	 */
	public final void attach(PluginDockable dockable,
			IDeviceManager deviceManager, IEventBus eventBus) {
		
		_dockable = dockable;
		setTitle(_title);
		setIcon(_icon);
		
		_deviceManager = deviceManager;
		_eventBus = eventBus;

		onCreate();
	}

	/**
	 * Override this method to perform initialization tasks, instead of in a
	 * constructor.
	 */
	public void onCreate() {

	}

	/**
	 * Override this method to perform finalization tasks before the Plugin is
	 * destroyed.
	 */
	public void onDestroy() {

	}
}
