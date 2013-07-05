package net.brtly.monkeyboard.api.plugin.action;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import net.brtly.monkeyboard.util.Util;

public class ActionMenu extends ActionButton implements List<ActionNode>,
		TreeModelListener {

	// use a thread-safe Vector instead of a List
	private final Vector<ActionNode> _children = new Vector<ActionNode>();

	public ActionMenu(Action action) {

	}

	public ActionMenu(String text) {
		super(text);
	}

	public ActionMenu(Icon icon) {
		super(icon);
	}

	public ActionMenu(String text, Icon icon) {
		super(text, icon);
	}

	// TreeNode Interface /////////////////////////////////////////////////////
	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public ActionNode getChildAt(int childIndex) {
		return _children.get(childIndex);
	}

	@Override
	public int getChildCount() {
		return _children.size();
	}
	
	// List Interface /////////////////////////////////////////////////////////
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(ActionNode child) {
		if (child == null) {
			throw new IllegalArgumentException("child can't be null");
		}
		ActionNode[] c = { child };
		int[] i = { size() };
		boolean rv = _children.add(child);
		adoptChildren(c, false);
		fireTreeNodesInserted(this, getPath(), i, c);
		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(int index, ActionNode child) {
		if (child == null) {
			throw new IllegalArgumentException("child can't be null");
		}
		ActionNode[] c = { child };
		int[] i = { index };
		_children.add(index, child);
		adoptChildren(c, false);
		fireTreeNodesInserted(this, getPath(), i, c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addAll(Collection<? extends ActionNode> children) {
		if (children.contains(null)) {
			throw new IllegalArgumentException("child can't be null");
		}
		ActionNode[] c = toArray(children);
		int[] i = Util.range(size(), size() + c.length);
		boolean rv = _children.addAll(children);
		if (rv) {
			adoptChildren(c, false);
			fireTreeNodesInserted(this, getPath(), i, c);
		}
		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addAll(int index, Collection<? extends ActionNode> children) {
		if (children.contains(null)) {
			throw new IllegalArgumentException("child can't be null");
		}
		ActionNode[] c = toArray(children);
		int[] i = Util.range(index, index + c.length);
		boolean rv = _children.addAll(index, children);
		if (rv) {
			adoptChildren(c, false);
			fireTreeNodesInserted(this, getPath(), i, c);
		}
		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		if (size() == 0) {
			return;
		}
		ActionNode[] c = (ActionNode[]) _children.toArray();
		int[] i = Util.range(0, size());
		_children.clear();
		fireTreeNodesRemoved(this, getPath(), i, c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Object obj) {
		return _children.contains(obj);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return _children.containsAll(c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ActionNode get(int index) {
		return _children.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int indexOf(Object obj) {
		return _children.indexOf(obj);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return _children.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<ActionNode> iterator() {
		return _children.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int lastIndexOf(Object obj) {
		return _children.lastIndexOf(obj);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListIterator<ActionNode> listIterator() {
		return _children.listIterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListIterator<ActionNode> listIterator(int index) {
		return _children.listIterator(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(Object obj) {
		if (!(obj instanceof ActionNode)) {
			return false;
		}
		ActionNode[] c = { (ActionNode) obj };
		int[] i = { indexOf(obj) };
		boolean rv = _children.remove(obj);
		if (rv) {
			fireTreeNodesRemoved(this, getPath(), i, c);
		}
		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ActionNode remove(int index) {
		int[] i = { index };
		ActionNode rv = _children.remove(index);
		ActionNode[] c = { rv };
		if (rv != null) {
			fireTreeNodesRemoved(this, getPath(), i, c);
		}
		return rv;
	}

	/**
	 * Unsupported, DO NOT USE!
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	@Deprecated
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported, DO NOT USE!
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	@Deprecated
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ActionNode set(int index, ActionNode child) {
		return _children.set(index, child);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return _children.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ActionNode> subList(int fromIndex, int toIndex) {
		return _children.subList(fromIndex, toIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] toArray() {
		return _children.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		return _children.toArray(a);
	}

	// TreeModelListener interface ////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void treeNodesChanged(TreeModelEvent e) {
		for (TreeModelListener listener : getTreeModelListeners()) {
			listener.treeNodesChanged(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void treeNodesInserted(TreeModelEvent e) {
		for (TreeModelListener listener : getTreeModelListeners()) {
			listener.treeNodesInserted(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void treeNodesRemoved(TreeModelEvent e) {
		for (TreeModelListener listener : getTreeModelListeners()) {
			listener.treeNodesRemoved(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void treeStructureChanged(TreeModelEvent e) {
		for (TreeModelListener listener : getTreeModelListeners()) {
			listener.treeStructureChanged(e);
		}
	}

	/**
	 * Convert/cast a collection of ActionNodes to an array
	 * 
	 * @param collection
	 * @return
	 */
	private ActionNode[] toArray(Collection<? extends ActionNode> collection) {
		ActionNode[] rv = new ActionNode[collection.size()];
		int i = 0;
		for (ActionNode c : collection) {
			rv[i] = c;
			i++;
		}
		return rv;
	}

	/**
	 * Call setParent(this) on an array of ActionNodes.
	 * 
	 * @param children
	 *            array of ActionNodes to call setParent(this) on.
	 * @param withEvents
	 *            if false, no TreeModelEvents are generated
	 */
	private void adoptChildren(ActionNode[] children, boolean withEvents) {
		for (ActionNode b : children) {
			b.setParent(this, withEvents);
		}
	}

	/**
	 * Call setParent(null) on an array of ActionNodes.
	 * 
	 * @param children
	 *            array of ActionNodes to call setParent(null) on.
	 * @param withEvents
	 *            if false, no TreeModelEvents are generated
	 */
	private void disownChildren(ActionNode[] children, boolean withEvents) {
		for (ActionNode b : children) {
			b.setParent(null, withEvents);
		}
	}


	/**
	 * Returns the depth of the tree rooted at this node -- the longest distance
	 * from this node to a leaf. If this node has no children, returns 0. This
	 * operation is much more expensive than getLevel() because it must
	 * effectively traverse the entire tree rooted at this node.
	 * 
	 * @return the depth of the tree whose root is this node
	 */
	public int getDepth() {
		if (_children.size() == 0) {
			return 0;
		}

		Stack<Integer> stack = new Stack<Integer>();
		stack.push(new Integer(0));
		ActionNode node = get(0);
		int depth = 0;
		int current = 1;

		while (!stack.empty()) {
			if (node.getChildCount() != 0) {
				node = node.getChildAt(0);
				stack.push(new Integer(0));
				current++;
			} else {
				if (current > depth)
					depth = current;

				int size;
				int index;

				do {
					node = node.getParent();
					size = node.getChildCount();
					index = ((Integer) stack.pop()).intValue() + 1;
					current--;
				} while (index >= size && node != this);

				if (index < size) {
					node = node.getChildAt(index);
					stack.push(new Integer(index));
					current++;
				}
			}
		}

		return depth;
	}
}
