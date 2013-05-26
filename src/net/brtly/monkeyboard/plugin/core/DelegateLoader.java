package net.brtly.monkeyboard.plugin.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.brtly.monkeyboard.api.plugin.IPluginContext;
import net.brtly.monkeyboard.api.plugin.PluginDelegate;
import net.brtly.monkeyboard.api.plugin.annotation.Metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DelegateLoader {

	private static final Log LOG = LogFactory.getLog(DelegateLoader.class);
	private final PluginDelegate _delegate;

	private final String _title;
	private final Icon _icon;

	protected DelegateLoader(Class<? extends PluginDelegate> clazz,
			IPluginContext context) throws InstantiationException {
		// TODO: Each delegate has a separate classloader

		Metadata m = clazz.getAnnotation(Metadata.class);

		if (m == null) {
			_title = clazz.getSimpleName();
			_icon = null;
		} else {
			_title = m.title();
			if (m.icon().equals("")) {
				_icon = null;
			} else {
				_icon = loadIcon("img/android.png");
			}
		}
		try {
			Constructor c = clazz.getConstructor(IPluginContext.class);
			_delegate = (PluginDelegate) c.newInstance(context);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Couldn't add " + clazz.getName(), e);
		} catch (SecurityException e) {
			throw new RuntimeException("Couldn't add " + clazz.getName(), e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Couldn't add " + clazz.getName(), e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Couldn't add " + clazz.getName(), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Couldn't add " + clazz.getName(), e);
		}
	}

	private Icon loadIcon(String locator) {
		return new ImageIcon(this.getClass().getClassLoader()
				.getResource(locator));
	}

	public PluginDelegate getDelegate() {
		return _delegate;
	}

	public String getTitle() {
		return _title;
	}

	public Icon getIcon() {
		return _icon;
	}
}