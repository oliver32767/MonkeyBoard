package net.brtly.monkeyboard.api.plugin.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Icon;

import net.brtly.monkeyboard.api.plugin.PluginCommand;

public class ActionButton extends ActionNode {

	static class DefaultActionListener implements ActionListener {

		private final Action _action;

		public DefaultActionListener(Action action) {
			_action = action;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			_action.actionPerformed(e);
		}

	}
	
	static class CommandActionListener implements ActionListener {

		private final PluginCommand _command;
		private final Object _source;
		public CommandActionListener(ActionButton source, PluginCommand command) {
			_command = command;
			_source = source;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			_command.handle(_source);
			
		}
		
	}

	private String _text;
	private Icon _icon;
	private boolean _enabled;

	public ActionButton() {

	}

	public ActionButton(Action action) {
		addActionListener(new DefaultActionListener(action));
	}

	public ActionButton(String text) {
		_text = text;
	}

	public ActionButton(Icon icon) {
		_icon = icon;
	}

	public ActionButton(String text, Icon icon) {
		_text = text;
		_icon = icon;
	}

	/**
	 * Sets the properties on this button to match those in the specified
	 * Action.
	 * 
	 * @param action
	 *            the Action from which to get the properties, or null
	 */
	protected void configurePropertiesFromAction(Action action) {

	}

	/**
	 * Get the text of this ActionButton
	 * 
	 * @return the text of this ActionButton
	 */
	public String getText() {
		return _text;
	}

	/**
	 * Set the text of this ActionButton
	 * 
	 * @param text
	 *            the text of this ActionButton.
	 */
	public void setText(String text) {
		if (text == null) {
			if (_text == null) {
				return;
			}
		} else if (text.equals(_text)) {
			return;
		}
		_text = text;
		fireTreeNodeChanged();
	}

	/**
	 * Get the icon for this ActionButton
	 * 
	 * @return the icon for this ActionButton
	 */
	public Icon getIcon() {
		return _icon;
	}

	/**
	 * Get the icon for this ActionButton
	 * 
	 * @param icon
	 *            the icon for this ActionButton
	 */
	public void setIcon(Icon icon) {
		if (icon == null) {
			if (_icon == null) {
				return;
			}
		} else if (icon.equals(_icon)) {
			return;
		}
		_icon = icon;
		fireTreeNodeChanged();
	}

	/**
	 * Check if this ActionButton is enabled or not
	 * 
	 * @return if this ActionButton is enabled or not
	 */
	public boolean isEnabled() {
		return _enabled;
	}

	/**
	 * Change this ActionButton's enabled status
	 * 
	 * @param enabled
	 *            this ActionButton's enabled status
	 */
	public void setEnabled(boolean enabled) {
		if (_enabled == enabled) {
			return;
		}
		_enabled = enabled;
		fireTreeNodeChanged();
	}

	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	@Override
	public ActionNode getChildAt(int childIndex) {
		return null;
	}

	@Override
	public int getChildCount() {
		return 0;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " ("
				+ String.valueOf(getText()) + ")";
	}
}
