package net.brtly.monkeyboard.plugin;

import com.google.common.eventbus.EventBus;

import net.brtly.monkeyboard.api.IDeviceManager;
import net.brtly.monkeyboard.api.plugin.IPluginContext;
import net.brtly.monkeyboard.api.plugin.IPluginManager;

public class PluginContext implements IPluginContext {

	private final IDeviceManager _deviceManager;
	private final IPluginManager _pluginManager;
	private final EventBus _eventBus;
	
	public PluginContext(IDeviceManager d, IPluginManager p, EventBus e) {
		_deviceManager = d;
		_pluginManager = p;
		_eventBus = e;
	}

	@Override
	public IDeviceManager getDeviceManager() {
		return _deviceManager;
	}

	@Override
	public IPluginManager getPluginManager() {
		return _pluginManager;
	}

	@Override
	public EventBus getEventBus() {
		return _eventBus;
	}

}
