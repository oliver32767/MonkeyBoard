package net.brtly.monkeyboard.plugin.core;

import java.util.Set;

import net.brtly.monkeyboard.api.plugin.PluginView;
import net.brtly.monkeyboard.api.plugin.annotation.View;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;

import com.google.common.eventbus.EventBus;

public class PluginManager {
	private static final Log LOG = LogFactory.getLog(PluginManager.class);

	private static PluginManager INSTANCE;
	
	public PluginManager() {
		LOG.debug("PluginManager initialized");
	}

	public static void init(EventBus eventBus) {
		
	}
	
	public void loadPlugins() {
		Reflections ref = new Reflections("net.brtly.monkeyboard.plugin");

		// TODO: Load delegate instances into Map. There should only be ONE
		// instance of
		// a delegate, unless the Plugin hasn't specified a delegate, at which
		// point
		// a default implementation will be instatiated for that specific
		// plugin. The default PluginDelegate is NOT a shared delegate, a new
		// instance will be created for each plugin that does not specify a
		// delegate!

		// get list of Delegate subclasses, add the class objects to a set
		
		// Load classes annotated with View
		Set<Class<?>> anno = ref.getTypesAnnotatedWith(View.class);

		for (Class<?> c : anno) {
			if (PluginView.class.isAssignableFrom(c)) {
				LOG.debug("Loaded plugin:" + c.getName());
				View a = c.getAnnotation(View.class);
				LOG.debug("title:" + a.title());
				LOG.debug("icon:" + a.icon());
				LOG.debug("allowDuplicates:" + a.allowDuplicates());
				LOG.debug("delegate:" + a.delegate().getName());

			} else {
				LOG.warn("Couldn't load plugin:" + c.getName()
						+ " because it is not a subclass of PluginView");
			}
		}

	}

}
