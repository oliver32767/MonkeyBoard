package net.brtly.monkeyboard.gui.binder;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.tree.TreeModel;

public class TreeModelMenuBinder extends TreeModelBinder {

	public TreeModelMenuBinder(JComponent component, TreeModel model,
			IComponentProvider provider) {
		super(component, model, provider);
	}

	protected JComponent getChild(JComponent parent, int index) {
		return ((JMenu)parent).getItem(index);
	}
}
