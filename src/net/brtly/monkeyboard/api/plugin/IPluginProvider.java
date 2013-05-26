package net.brtly.monkeyboard.api.plugin;

public interface IPluginProvider<T extends IPlugin> {

	public abstract boolean shouldLoadPlugin(int instanceCount);
	
	public abstract T loadPlugin(Bundle properties);
}
