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
package net.brtly.monkeyboard.api.plugin.panel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.Icon;

public class PluginFrame {
	
	private PropertyChangeSupport _pcs;
	
	private String _title;
	private Icon _icon;
	private String _toolTip;
	
	public PluginFrame() {
		_pcs = new PropertyChangeSupport(this);
	}
	
	public PluginFrame(String title, Icon icon) {
		_pcs = new PropertyChangeSupport(this);
		_title = title;
		_icon = icon;
	}
	
	public void setTitle(String title) {
		String oldValue = _title;
		_title = title;
		_pcs.firePropertyChange("title", oldValue, _title);
	}
	
	public String getTitle() {
		return _title;
	}
	
	public void setIcon(Icon icon) {
		Icon oldValue = _icon;
		_icon = icon;
		_pcs.firePropertyChange("icon", oldValue, _icon);
	}
	
	public Icon getIcon() {
		return _icon;
	}

	public void setToolTip(String toolTip) {
		String oldValue = _toolTip;
		_toolTip = toolTip;
		_pcs.firePropertyChange("toolTip", oldValue, _toolTip);
		
	}

	public String getToolTip() {
		return _toolTip;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		_pcs.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		_pcs.removePropertyChangeListener(listener);
	}
}
