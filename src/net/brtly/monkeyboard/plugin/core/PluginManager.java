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
package net.brtly.monkeyboard.plugin.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.brtly.monkeyboard.adb.DeviceManager;
import net.brtly.monkeyboard.api.plugin.IPluginContext;
import net.brtly.monkeyboard.api.plugin.IPluginManager;
import net.brtly.monkeyboard.api.plugin.IPluginProvider;
import net.brtly.monkeyboard.api.plugin.PluginDelegate;
import net.brtly.monkeyboard.plugin.PluginContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;

public class PluginManager implements IPluginManager {

	private static final Log LOG = LogFactory.getLog(PluginManager.class);

	private final Map<String, DelegateLoader> _delegates;
	private final Map<String, PluginLoader> _plugins;
	private final Multimap<String, String> _pluginTable; // maps a delegate ID
															// to its registered
															// plugins

	private final EventBus _eventBus;

	public PluginManager(EventBus eventBus) {
		_delegates = new HashMap<String, DelegateLoader>();
		_plugins = new HashMap<String, PluginLoader>();
		_pluginTable = ArrayListMultimap.create();

		_eventBus = eventBus;

		LOG.debug("PluginManager initialized");
	}

	public void loadPlugins() {
		LOG.debug("Loading Plugins");
		Reflections reflections = new Reflections(
				"net.brtly.monkeyboard.plugin");

		Set<Class<? extends PluginDelegate>> delegateClasses = reflections
				.getSubTypesOf(PluginDelegate.class);

		LOG.trace("Adding delegates");
		for (Class<? extends PluginDelegate> c : delegateClasses) {
			LOG.trace("+" + c.getName());
			addDelegate(c);
		}

		LOG.trace("Loading delegates");
		for (String id : _delegates.keySet()) {
			LOG.trace(">" + id);
			_delegates.get(id).getDelegate().onLoad(null);
		}

	}

	private void addDelegate(Class<? extends PluginDelegate> clazz) {
		String id = clazz.getName();

		if (_delegates.containsKey(id)) {
			LOG.warn("Ignoring duplicate PluginDelegate class name:" + id);
			return;
		}
		try {
			_delegates.put(id, new DelegateLoader(clazz, getPluginContext()));
		} catch (InstantiationException e) {
			LOG.warn("Couldn't add:" + id, e);
		}
	}

	protected IPluginContext getPluginContext() {
		// Instead of handing out the same reference, we'll start by creating a
		// new one each time
		// because one day this will require us to know which classloader to get
		// the context for
		return new PluginContext(DeviceManager.getDeviceManager(), this,
				_eventBus);
	}

	@Override
	public void register(PluginDelegate delegate, IPluginProvider<?> provider) {
		// TODO: verify the delegate instance given is a valid delegate

		// TODO: ensure that the classloader of the provider is the same as the
		// one for the delegate loader

		PluginLoader loader;
		try {
			loader = new PluginLoader(delegate, provider);
		} catch (SecurityException e) {
			throw new RuntimeException("Couldn't get IPlugin type", e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Couldn't get IPlugin type", e);
		} catch (ClassCastException e) {
			throw new RuntimeException("Couldn't get IPlugin type", e);
		}

		if (_plugins.containsKey(loader.getProvidedClass().getName())) {
			LOG.warn("Ignoring duplicate IPluginProvider for class name:"
					+ loader.getProvidedClass().getName());
			return;
		}

		LOG.trace("+" + loader.getPluginId());
		_plugins.put(loader.getPluginId(), loader);
		_pluginTable.put(delegate.getID(), loader.getPluginId());

		LOG.debug("Registered plugin:" + loader.getPluginId());
	}

	@Override
	public void unregister(IPluginProvider<?> provider) {
		// TODO Auto-generated method stub
		LOG.error("NYI!");
	}

	@Override
	public void unregister(PluginDelegate delegate) {
		// TODO Auto-generated method stub
		LOG.error("NYI!");

	}

	public Set<String> getDelegatesIDs() {
		// TODO
		return Collections.unmodifiableSet(_delegates.keySet());
	}

	public DelegateLoader getDelegateLoader(String id) {
		return _delegates.get(id);
	}

	public Set<String> getPluginIDs() {
		return getPluginIDs((IPluginFilter[]) null);
	}

	/**
	 * Return a set of plugin IDs that map to available plugins matching a list of filters.
	 * If any given filter DOES NOT apply to a plugin, it is removed from the set.
	 * @param filters
	 * @return
	 */
	public Set<String> getPluginIDs(IPluginFilter... filters) {
		if (filters == null || filters.length == 0) {
			return Collections.unmodifiableSet(_plugins.keySet());
		}

		Set<String> rv;
		synchronized (_plugins) {
			rv = new HashSet<String>(_plugins.keySet());

			for (String id : _plugins.keySet()) {
				PluginLoader loader = getPluginLoader(id);
				if (loader == null) {
					rv.remove(id);
				} else {
					for (IPluginFilter filter : filters) {
						if (!filter.appliesTo(loader)) {
							rv.remove(id);
						}
					}
				}
			}
		}
		return Collections.unmodifiableSet(rv);
	}

	public PluginLoader getPluginLoader(String id) {
		return _plugins.get(id);
	}

}