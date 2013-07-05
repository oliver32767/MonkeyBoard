package net.brtly.monkeyboard.api.plugin.action;

import java.util.Enumeration;
import java.util.NoSuchElementException;

class ActionNodeEnumeration implements Enumeration<ActionNode> {

	/**
	 * 
	 */
	private final ActionNode node;

	/**
	 * @param actionNode
	 */
	ActionNodeEnumeration(ActionNode actionNode) {
		node = actionNode;
	}

	private int _index = 0;

	@Override
	public boolean hasMoreElements() {
		if (_index >= node.getChildCount()) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public ActionNode nextElement() {
		if (hasMoreElements()) {
			_index++;
			return node.getChildAt(_index - 1);
		} else {
			throw new NoSuchElementException("element " + _index + "/"
					+ node.getChildCount() + " does not exist");
		}
	}

}