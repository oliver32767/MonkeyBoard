package net.brtly.monkeyboard.gui;

import javax.swing.AbstractAction;

import net.brtly.monkeyboard.plugin.core.PluginLoader;

@SuppressWarnings("serial")
public abstract class ViewMenuAction extends AbstractAction {
	private final PluginLoader _loader;;

	public ViewMenuAction(PluginLoader loader) {
		super(loader.getTitle(), loader.getIcon());
		_loader = loader;
	}
	
	protected PluginLoader getLoader() {
		return _loader;
	}
}
