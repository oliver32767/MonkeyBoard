package net.brtly.monkeyboard.api.plugin;

import javax.swing.Icon;

public final class PluginCommand {

	public static PluginCommandBuilder makeCommand(String id) {
		return new PluginCommandBuilder(id);
	}
	
	private final String _id;
	private String _name;
	private Icon _icon;
	
	// TODO: this might leak references to client objects, see if a Reference<T>
	// might help
	private IPluginCommandHandler _handler;

	public PluginCommand(String id, String name, Icon icon, IPluginCommandHandler handler) {
		if (id == null) {
			throw new IllegalArgumentException("id can't be null");
		}
		_id = id;
		_name = name;
		_icon = icon;
		_handler = handler;
	}

	public String getId() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	public Icon getIcon() {
		return _icon;
	}

	public IPluginCommandHandler getHandler() {
		return _handler;
	}
	
	public PluginCommand copy() {
		return new PluginCommand(_id, _name, _icon, _handler);
	}
	
	public void handle(Object source) {
		if (_handler != null) {
			_handler.handle(source, this);
		}
	}
}
