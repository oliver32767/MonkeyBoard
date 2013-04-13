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

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import net.brtly.monkeyboard.api.plugin.annotation.View;
import net.brtly.monkeyboard.plugin.PluginDockable;

@SuppressWarnings("serial")
public abstract class PluginView extends JPanel implements IPlugin {

	private final PluginDelegate _delegate;
	
	private JPanel _rootView;
	private PluginDockable _dockable;
	private String _title;
	private Icon _icon;
	private final boolean _allowDuplicates;

	public PluginView(PluginDelegate delegate) {
		_delegate = delegate;

		View anno = getClass().getAnnotation(View.class);
		if (anno != null) {
			_title = anno.title();
			try {
				_icon = new ImageIcon(anno.icon());
			} catch (Exception e) {
				_icon = null;
			}
			_allowDuplicates = anno.allowDuplicates();
		} else {
			_title = this.toString();
			_icon = null;
			_allowDuplicates = true;
		}
	}
	
	public PluginDelegate getDelegate() {
		return _delegate;
	}

	public void setTitle(String title) {
		_title = title;
	}
	
	public String getTitle() {
		return _title;
	}
	
	public void setIcon(Icon icon) {
		_icon = icon;
	}
	
	public Icon getIcon() {
		return _icon;
	}

	public final void attach(PluginDockable dockable) {
		_dockable = dockable;
		_dockable.setTitleText(getTitle());
		_dockable.setTitleIcon(getIcon());
		_dockable.add(this);
	}
	

	public final Component findComponentWithName(String name) {
		for (Component c : _rootView.getComponents()) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}
}
