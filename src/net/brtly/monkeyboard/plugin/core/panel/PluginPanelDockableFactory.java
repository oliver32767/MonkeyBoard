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

package net.brtly.monkeyboard.plugin.core.panel;

import java.util.HashMap;
import java.util.Map;

import net.brtly.monkeyboard.api.plugin.panel.PluginPanel;
import net.brtly.monkeyboard.plugin.core.PluginLoader;
import net.brtly.monkeyboard.plugin.core.PluginManager;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.event.CDockableStateListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;

public class PluginPanelDockableFactory implements
		MultipleCDockableFactory<PluginPanelDockable, PluginDockableLayout> {

	public static final String ID = PluginPanelDockableFactory.class.getName();
	private final Map<String, Integer> _instanceCounts;

	private final PluginManager _pluginManager;

	public PluginPanelDockableFactory(PluginManager pluginManager) {
		_pluginManager = pluginManager;
		_instanceCounts = new HashMap<String, Integer>();
	}

	@Override
	public PluginDockableLayout write(PluginPanelDockable dockable) {
		// TODO
		return null;
	}

	@Override
	public PluginPanelDockable read(PluginDockableLayout layout) {
		final PluginLoader loader = _pluginManager.getPluginLoader(layout
				.getId());
		// loader.getPluginId() == layout.getId()

		Integer instances = _instanceCounts.get(loader.getPluginId());
		if (instances == null || instances <= 0) {
			_instanceCounts.put(loader.getPluginId(), 0);
			instances = 0;
		}
		final int i = instances;
		if (loader.shouldLoadPlugin(instances)) {
			PluginPanelDockable rv = new PluginPanelDockable(this,
					loader.getIcon(), loader.getTitle(), null);

			rv.add((PluginPanel) loader.getProvider().loadPlugin(null));
			rv.setCloseable(true);
			rv.addCDockableStateListener(new CDockableStateListener() {

				@Override
				public void visibilityChanged(CDockable dockable) {
					if (!dockable.isVisible()) {
						if (i <= 0) {
							_instanceCounts.put(loader.getPluginId(), 0);
						} else {
							_instanceCounts.put(loader.getPluginId(), i - 1);
						}
					}
				}

				@Override
				public void extendedModeChanged(CDockable dockable,
						ExtendedMode mode) {
				}

			});
			// rv.add(new
			// PropertyList(_pluginManager.getDelegateLoader(loader.getDelegateId()).getDelegate()));
			_instanceCounts.put(loader.getPluginId(), instances + 1);
			return rv;
		}
		return null;
	}

	public int getInstanceCount(String id) {
		Integer rv = _instanceCounts.get(id);
		if (rv == null) {
			return 0;
		}
		return rv;
	}

	@Override
	public boolean match(PluginPanelDockable dockable,
			PluginDockableLayout layout) {
		// TODO
		return false;
	}

	@Override
	public PluginDockableLayout create() {
		return new PluginDockableLayout(null, null);
	}
}