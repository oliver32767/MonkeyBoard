package net.brtly.monkeyboard.plugin.core.panel;

import javax.swing.Icon;

import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.action.CAction;


public class PluginPanelDockable extends DefaultMultipleCDockable {

	public PluginPanelDockable(MultipleCDockableFactory<?, ?> factory,
			Icon icon, String title, CAction[] actions) {
		super(factory, icon, title, actions);
	}

}
