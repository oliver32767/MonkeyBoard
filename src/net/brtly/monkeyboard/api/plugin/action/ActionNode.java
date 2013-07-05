package net.brtly.monkeyboard.api.plugin.action;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeNode;

public abstract class ActionNode implements TreeNode {
	
	private ActionMenu _parent;
	private final EventListenerList _listeners = new EventListenerList();
	
	// TreeNode interface

	@Override
	public Enumeration<ActionNode> children() {
		return new ActionNodeEnumeration(this);
	}

	public abstract boolean getAllowsChildren();

	public abstract ActionNode getChildAt(int childIndex);

	public abstract int getChildCount();

	@Override
	public int getIndex(TreeNode node) {
		if (node != null) {
			for (int i = 0; i < getChildCount(); i++) {
				if (node == getChildAt(i)) {
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public boolean isLeaf() {
		if (getChildCount() > 0) {
			return false;
		} else {
			return true;
		}
	}

	// TreeNode/Data Structure /////////////////////////////////////////////////

	/**
	 * Get the parent ActionGroup of this node.
	 * 
	 * @return the parent ActionGroup of this node, null if this is a root node.
	 */
	@Override
	public ActionMenu getParent() {
		return _parent;
	}

	/**
	 * Set the parent of this ActionNode. Does not fire treeNodeInserted events
	 * if given parent is null. Does not fire treeNodeRemoved events if current
	 * parent is null. Fires relevant TreeModelEvents.
	 * 
	 * @param parent
	 *            the new parent of this ActionNode
	 */

	protected void setParent(ActionMenu parent) {
		setParent(parent, true);
	}

	/**
	 * Set the parent of this ActionNode. Does not fire treeNodeInserted events
	 * if given parent is null. Does not fire treeNodeRemoved events if current
	 * parent is null.
	 * 
	 * @param parent
	 *            the new parent of this ActionNode
	 * 
	 * @param withEvents
	 *            if false, no TreeModelEvents are generated
	 */
	protected void setParent(ActionMenu parent, boolean withEvents) {
		ActionMenu oldParent = _parent;
		int[] oldIndex = new int[1];
		if (oldParent != null) {
			removeTreeModelListener(oldParent);
			oldIndex[0] = oldParent.indexOf(this);
		}

		_parent = parent;
		addTreeModelListener(_parent);

		if (withEvents) {
			ActionNode[] c = { this };
			if (oldParent != null) {
				this.fireTreeNodesRemoved(this, oldParent.getPath(), oldIndex,
						c);
			}

			if (parent != null) {
				int[] i = { parent.indexOf(this) };
				this.fireTreeNodesInserted(this, parent.getPath(), i, c);
			}
		}

	}

	/**
	 * Returns the path from the root, to get to this node. The last element in
	 * the path is this node.
	 * 
	 * @return an array of ActionNode objects giving the path, where the first
	 *         element in the path is the root and the last element is this
	 *         node.
	 */
	public ActionNode[] getPath() {
		return getPathToRoot(this, 0);
	}

	/**
	 * Returns the number of levels above this node -- the distance from the
	 * root to this node. If this node is the root, returns 0.
	 * 
	 * @return the number of levels above this node
	 */
	public int getLevel() {
		return getPath().length - 1;
	}

	/**
	 * Builds the parents of node up to and including the root node, where the
	 * original node is the last element in the returned array. The length of
	 * the returned array gives the node's depth in the tree.
	 * 
	 * @param node
	 *            the ActionNode to get the path for
	 * @param depth
	 *            an int giving the number of steps already taken towards the
	 *            root (on recursive calls), used to size the returned array
	 * @return an array of TreeNodes giving the path from the root to the
	 *         specified node
	 */
	protected ActionNode[] getPathToRoot(ActionNode node, int depth) {
		ActionNode[] rv;
		/*
		 * Check for null, in case someone passed in a null node, or they passed
		 * in an element that isn't rooted at root.
		 */
		if (node == null) {
			if (depth == 0)
				// FIXME should this return a zero-length array instead of null?
				return null;
			else
				rv = new ActionNode[depth];
		} else {
			depth++;
			rv = getPathToRoot(node.getParent(), depth);
			rv[rv.length - depth] = node;
		}
		return rv;
	}

	/**
	 * Build a list of all child nodes from a preorder depth-first traversal
	 * 
	 * @return
	 */
	public List<ActionNode> preOrderTraversal() {
		return buildPreOrderTraversal(this);

	}

	private List<ActionNode> buildPreOrderTraversal(ActionNode node) {
		List<ActionNode> rv = new ArrayList<ActionNode>();
		rv.add(node);
		for (int i = 0; i < node.getChildCount(); i++) {
			rv.addAll(buildPreOrderTraversal(node.getChildAt(i)));
		}
		return rv;
	}

	// ActionEvent listeners //////////////////////////////////////////////////

	/**
	 * Returns an array of all the ActionListeners added to this ActionButton
	 * with addActionListener().
	 * 
	 * @return all of the ActionListeners added or an empty array if no
	 *         listeners have been added
	 */
	public ActionListener[] getActionListeners() {
		return (ActionListener[]) _listeners.getListeners(ActionListener.class);
	}

	/**
	 * Adds an ActionListener to the button
	 * 
	 * @param listener
	 *            the ctionListener to be added
	 */
	public void addActionListener(ActionListener listener) {
		_listeners.add(ActionListener.class, listener);
	}

	/**
	 * Removes an ActionListener from the button. If the listener is the
	 * currently set Action for the button, then the Action is set to null.
	 * 
	 * @param listener
	 *            the ActionListener to be removed
	 */
	public void removeActionListener(ActionListener listener) {
		_listeners.remove(ActionListener.class, listener);
	}

	// internal event propagation //////////////////////////////////////////////

	protected TreeModelListener[] getTreeModelListeners() {
		return (TreeModelListener[]) _listeners
				.getListeners(TreeModelListener.class);
	}

	/**
	 * Register a listener to listen for changes to this ActionNode's subtree
	 * 
	 * @param listener
	 */
	public void addTreeModelListener(TreeModelListener listener) {
		_listeners.add(TreeModelListener.class, listener);
	}

	/**
	 * Unregister a listener
	 * 
	 * @param listener
	 */
	public void removeTreeModelListener(TreeModelListener listener) {
		_listeners.remove(TreeModelListener.class, listener);
	}

	/**
	 * Fire an event notifying listeners that this node has changed
	 */
	protected void fireTreeNodeChanged() {
		ActionNode[] child = { this };
		if (getParent() == null) {
			// root node
			fireTreeNodesChanged(this, null, null, child);
		} else {
			// child node
			ActionNode[] path = getParent().getPath();
			int[] index = { getParent().indexOf(this) };
			fireTreeNodesChanged(this, path, index, child);
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @param source
	 *            the node being changed
	 * @param path
	 *            the path to the root node
	 * @param childIndices
	 *            the indices of the changed elements
	 * @param children
	 *            the changed elements
	 */
	protected void fireTreeNodesChanged(ActionNode source, ActionNode[] path,
			int[] childIndices, ActionNode[] nodes) {
		TreeModelEvent event = new TreeModelEvent(source, path, childIndices,
				nodes);
		for (TreeModelListener listener : getTreeModelListeners()) {
			listener.treeNodesChanged(event);
		}
	}

	/**
	 * fireTreeNodesInserted
	 * 
	 * @param source
	 *            the node where new nodes got inserted
	 * @param path
	 *            the path to the root node
	 * @param childIndices
	 *            the indices of the new elements
	 * @param children
	 *            the new elements
	 */
	protected void fireTreeNodesInserted(ActionNode source, ActionNode[] path,
			int[] childIndices, ActionNode[] nodes) {
		TreeModelEvent event = new TreeModelEvent(source, path, childIndices,
				nodes);
		for (TreeModelListener listener : getTreeModelListeners()) {
			listener.treeNodesInserted(event);
		}
	}

	/**
	 * fireTreeNodesRemoved
	 * 
	 * @param source
	 *            the node where nodes got removed-
	 * @param path
	 *            the path to the root node
	 * @param childIndices
	 *            the indices of the removed elements
	 * @param children
	 *            the removed elements
	 */
	protected void fireTreeNodesRemoved(ActionNode source, ActionNode[] path,
			int[] childIndices, ActionNode[] nodes) {
		TreeModelEvent event = new TreeModelEvent(source, path, childIndices,
				nodes);
		for (TreeModelListener listener : getTreeModelListeners()) {
			listener.treeNodesRemoved(event);
		}
	}

	/**
	 * fireTreeStructureChanged
	 * 
	 * @param source
	 *            the node where the model has changed
	 * @param path
	 *            the path to the root node
	 * @param childIndices
	 *            the indices of the affected elements
	 * @param children
	 *            the affected elements
	 */
	protected void fireTreeStructureChanged(ActionNode source,
			ActionNode[] path, int[] childIndices, ActionNode[] nodes) {
		TreeModelEvent event = new TreeModelEvent(source, path, childIndices,
				nodes);
		for (TreeModelListener listener : getTreeModelListeners()) {
			listener.treeStructureChanged(event);
		}
	}
}
