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

/**
 * Subclasses of PluginPanel annotated with
 * {@link net.brtly.monkeyboard.api.Plugin} will be available to users in the
 * View menu.
 * 
 * @author obartley
 * 
 */
public class PluginPanel extends JPanel {

	private IPluginContext _runtime;

	private String _title = null;
	private Icon _icon = null;
	private boolean _allowDuplicates = true;

	public PluginPanel(IPluginContext runtime) {
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
		_runtime = runtime;
	}

	public final IPluginContext getRuntime() {
		return _runtime;
	}

	public final String getTitle() {
		return _title;
	}

	public final void setTitle(String title) {
		// TODO: find a way to update the dockable's title when this is invoked
		_title = title;
	}
	
	public final Icon getIcon() {
		return _icon;
	}
	
	public final void setIcon(Icon icon) {
		// TODO: find a way to update the dockable's icon when this is invoked
		_icon = icon;
	}
	
	public final boolean allowDuplicates() {
		return _allowDuplicates;
	}
}
