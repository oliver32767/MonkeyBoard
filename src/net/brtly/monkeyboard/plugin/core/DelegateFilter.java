package net.brtly.monkeyboard.plugin.core;

/**
 * Filters the plugin list to contain plugins that a registered by a
 * PluginDelegate with the given ID.
 * 
 * @author obartley
 * 
 */
public class DelegateFilter implements IPluginFilter {

	private final String _id;
	public DelegateFilter(String delegateId) {
		_id = delegateId;
	}

	@Override
	public boolean appliesTo(PluginLoader loader) {
		if (_id != null && _id.equals(loader.getDelegateId())) {
			return true;
		}
		return false;
	}

}
