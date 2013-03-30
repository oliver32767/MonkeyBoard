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


import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import net.brtly.monkeyboard.api.Plugin;


import com.google.common.eventbus.EventBus;

public abstract class DockablePanel extends JPanel {

	private static final long serialVersionUID = 5455255487992756612L;
	
	private String _title = null;
	private Icon _icon = null;
	private boolean _allowDuplicates = true;
	private EventBus _eventBus;
	
	public DockablePanel() {
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
	
	public final String getTitle() {
		return _title;
	}
	
	public final Icon getIcon() {
		return _icon;
	}
	
	public final boolean allowDuplicates() {
		return _allowDuplicates;
	}
	
	protected final void setEventBus(EventBus eventBus) {
		_eventBus = eventBus;	
	}
	
	/**
	 * Register an object annotated with @Subscribe methods with the event bus
	 * @param subscriber
	 */
	final void registerEventSubscriber(Object subscriber) {
		_eventBus.register(subscriber);	
	}
	
}
