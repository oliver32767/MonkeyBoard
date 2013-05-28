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

import java.awt.Component;
import java.awt.Container;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import net.brtly.monkeyboard.api.plugin.IPlugin;
import net.brtly.monkeyboard.api.plugin.PluginDelegate;
import net.brtly.monkeyboard.api.plugin.annotation.Metadata;

@SuppressWarnings("serial")
public abstract class PluginPanel extends JPanel implements IPlugin {
	
	private final PluginDelegate _service;
	
	private JPanel _rootView;
	private final PluginFrame _frame;

	public PluginPanel(PluginDelegate service) {
		_service = service;
		
		String title = null;
		Icon icon = null;
		Metadata anno = getClass().getAnnotation(Metadata.class);
		if (anno != null) {
			title = anno.title();
			try {
				icon = new ImageIcon(anno.icon());
			} catch (Exception e) {
				icon = null;
			}
		}
		_frame = new PluginFrame(title, icon);
	}
	
	@Override
	public final PluginDelegate getDelegate() {
		return _service;
	}
	
	public final PluginFrame getFrame() {
		return _frame;
	}
	
	/**
	 * Overridden to always return null.
	 */
	@Override
	public Container getParent() {
		return null;
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
