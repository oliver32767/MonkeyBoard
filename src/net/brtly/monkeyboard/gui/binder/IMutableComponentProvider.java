package net.brtly.monkeyboard.gui.binder;

import javax.swing.JComponent;


public interface IMutableComponentProvider extends IComponentProvider {
	public void updateComponent(JComponent component, Object newValue);
}
