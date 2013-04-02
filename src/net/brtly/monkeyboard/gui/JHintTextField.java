package net.brtly.monkeyboard.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.UIManager;

public class JHintTextField extends JTextField implements FocusListener {

	private static final long serialVersionUID = 672731309440727363L;
	private final String hint;

    public JHintTextField(final String hint) {
        super(hint);
        this.hint = hint;
        super.setForeground(UIManager.getDefaults().getColor("TextField.inactiveForeground"));
        super.addFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent e) {
        if(this.getText().isEmpty()) {
        	// FIXME remove listeners, change the text and re-add listeners
        	super.setForeground(UIManager.getDefaults().getColor("TextField.foreground"));
            super.setText("");
        }
    }
    @Override
    public void focusLost(FocusEvent e) {
        if(this.getText().isEmpty()) {
        	// FIXME remove listeners, change the text and re-add listeners
        	super.setForeground(UIManager.getDefaults().getColor("TextField.inactiveForeground"));
            super.setText(hint);
        }
    }

    @Override
    public String getText() {
    	System.out.println("DERP");
        String typed = super.getText();
        return typed.equals(hint) ? "" : typed;
    }

}
