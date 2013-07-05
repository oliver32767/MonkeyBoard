package net.brtly.monkeyboard.gui.binder;

import javax.swing.JComponent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;


public class TreeModelBinder implements TreeModelListener {
	private JComponent component;
	private IComponentProvider provider;
	private TreeModel model;

	public TreeModelBinder(JComponent component, TreeModel model,
			IComponentProvider provider) {
		this.component = component;
		this.model = model;
		this.provider = provider;

		buildComponent(component, model.getRoot());
		model.addTreeModelListener(this);
	}

	public void treeNodesChanged(TreeModelEvent e) {
		if (provider instanceof IMutableComponentProvider) {
			Object path[] = e.getPath();
			Object root = path[path.length - 1];
			int[] indices = e.getChildIndices();
			if (indices == null) {
				((IMutableComponentProvider) provider).updateComponent(
						component, root);
			} else {
				IMutableComponentProvider updater = (IMutableComponentProvider) provider;
				JComponent rootComponent = getComponentForPath(path);
				for (int i = 0; i < indices.length; i++) {
					Object child = model.getChild(root, indices[i]);
					updater.updateComponent(
							getChild(rootComponent, indices[i]), child);
				}
			}
		}
	}

	public void treeNodesInserted(TreeModelEvent e) {
		Object path[] = e.getPath();
		Object root = path[path.length - 1];
		JComponent rootComponent = getComponentForPath(path);
		int[] indices = e.getChildIndices();
		for (int i = indices.length - 1; i >= 0; i--) {
			rootComponent.add(provider.createComponent(model.getChild(root,
					indices[i])));
		}
	}

	public void treeNodesRemoved(TreeModelEvent e) {
		JComponent rootComponent = getComponentForPath(e.getPath());
		int[] indices = e.getChildIndices();
		for (int i = indices.length - 1; i >= 0; i--) {
			rootComponent.remove(indices[i]);
		}
	}

	public void treeStructureChanged(TreeModelEvent e) {
		Object[] path = e.getPath();
		JComponent comp = getComponentForPath(path);
		comp.removeAll();
		buildComponent(comp, path[path.length - 1]);
	}

	protected JComponent getComponentForPath(Object[] path) {
		JComponent comp = component;
		for (int i = 0; i < path.length - 1; i++) {
			int index = model.getIndexOfChild(path[i], path[i + 1]);
			comp = getChild(comp, index);
		}
		return comp;
	}

	protected void buildComponent(JComponent comp, Object parent) {
		int size = model.getChildCount(parent);
		for (int i = 0; i < size; i++) {
			Object child = model.getChild(parent, i);
			JComponent childComp = provider.createComponent(child);
			if (!model.isLeaf(child)) {
				buildComponent(childComp, child);
			}
			comp.add(childComp);
		}
	}

	protected JComponent getChild(JComponent parent, int index) {
		return (JComponent) parent.getComponent(index);
	}
}
