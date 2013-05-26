package net.brtly.monkeyboard.plugin.core;

import java.lang.reflect.Method;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.brtly.monkeyboard.api.plugin.Bundle;
import net.brtly.monkeyboard.api.plugin.IPlugin;
import net.brtly.monkeyboard.api.plugin.IPluginProvider;
import net.brtly.monkeyboard.api.plugin.PluginDelegate;
import net.brtly.monkeyboard.api.plugin.annotation.Metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.portable.Delegate;

public class PluginLoader {
	
	private static final Log LOG = LogFactory.getLog(PluginLoader.class);
	
	private final String _title;
	private final Icon _icon;
	private final IPluginProvider<? extends IPlugin> _provider;
	private final PluginDelegate _delegate;
	private final Class<? extends IPlugin> _clazz;
		
	@SuppressWarnings("unchecked")
	public PluginLoader(PluginDelegate delegate, IPluginProvider<? extends IPlugin> provider) throws SecurityException, NoSuchMethodException {
		_provider = provider;
		_delegate = delegate;
		
		Method m = provider.getClass().getMethod("loadPlugin", Bundle.class);
		_clazz = (Class<? extends IPlugin>) m.getReturnType();
		
		Metadata anno = _clazz.getAnnotation(Metadata.class);

		if (anno == null) {
			_title = _clazz.getSimpleName();
			_icon = null;
		} else {
			_title = anno.title();
			if (anno.icon().equals("")) {
				_icon = null;
			} else {
				_icon = loadIcon(anno.icon());
			}
		}
	}
	
	private Icon loadIcon(String locator) {
		System.out.println(locator);
		try {
			return new ImageIcon(this.getClass().getClassLoader().getResource(locator));
		} catch (Exception e) {
			return new ImageIcon(this.getClass().getClassLoader().getResource("img/android.png"));
		}
	}
	
	public String getTitle() {
		return _title;
	}
	
	public Icon getIcon() {
		return _icon;
	}
	
	public String getDelegateId() {
		return _delegate.getClass().getName();
	}
	
	public String getPluginId() {
		return _delegate.getClass().getName() + "$" + _clazz.getName();
	}
	
	public boolean shouldLoadPlugin(int instanceCount) {
		return _provider.shouldLoadPlugin(instanceCount);
	}
	
	public IPluginProvider<? extends IPlugin> getProvider() {
		return _provider;
	}
	
	public final Class<? extends IPlugin> getProvidedClass() {
		return _clazz;
	}
}