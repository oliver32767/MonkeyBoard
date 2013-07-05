package net.brtly.monkeyboard.api.plugin.action;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class PluginActionTree implements TreeModel, TreeModelListener {

	// TreeModel interface ////////////////////////////////////////////////////
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ActionButton getChild(Object arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getChildCount(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getIndexOfChild(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ActionButton getRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLeaf(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	// TreeModelListener interface ////////////////////////////////////////////
	@Override
	public void treeNodesChanged(TreeModelEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void treeNodesInserted(TreeModelEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void treeNodesRemoved(TreeModelEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void treeStructureChanged(TreeModelEvent e) {
		// TODO Auto-generated method stub

	}

}
