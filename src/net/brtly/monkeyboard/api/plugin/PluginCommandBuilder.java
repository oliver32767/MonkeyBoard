package net.brtly.monkeyboard.api.plugin;

import javax.swing.Icon;

public class PluginCommandBuilder {

	private PluginCommand _command;
	
	public PluginCommandBuilder(String id) {
		_command = new PluginCommand(id, null,null, null);
	}
	
	private PluginCommandBuilder(String id, String name, Icon icon, IPluginCommandHandler handler) {
		_command = new PluginCommand(id, name, icon, handler);
	}
	
	public PluginCommandBuilder withName(String name) {
		return new PluginCommandBuilder(_command.getId(), name, _command.getIcon(), _command.getHandler());
	}
	
	public PluginCommandBuilder withIcon(Icon icon) {
		return new PluginCommandBuilder(_command.getId(), _command.getName(), icon, _command.getHandler());
	}
	
	public PluginCommandBuilder withHandler(IPluginCommandHandler handler) {
		return new PluginCommandBuilder(_command.getId(), _command.getName(), _command.getIcon(), handler);
	}

	public PluginCommand build() {
		return _command;
	}
}
