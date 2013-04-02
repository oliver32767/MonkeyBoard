package net.brtly.monkeyboard.gui.widget;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.FocusManager;
import javax.swing.JTextField;

public class JHintTextField extends JTextField {

	private static final long serialVersionUID = 672731309440727363L;
	private String hint;

	public void setHint(String hint) {
		this.hint = hint;
	}

	public String getHint() {
		return hint;
	}

	@Override
	protected void paintComponent(java.awt.Graphics g) {
		super.paintComponent(g);

		if (getText().isEmpty()
				&& !(FocusManager.getCurrentKeyboardFocusManager()
						.getFocusOwner() == this)) {
			Graphics2D g2 = (Graphics2D) g.create();
			// g2.setBackground(Color.gray);
			// g2.setPaint(UIManager.getDefaults().getColor("TextField.shadow"));
//			g2.setPaint(this.getForeground().brighter().brighter().brighter()
//					.brighter().brighter().brighter().brighter());
			 g2.setPaint(Color.gray);
			g2.setFont(getFont().deriveFont(Font.ITALIC));
			g2.drawString(hint, 5, 20); // figure out x, y from font's
										// FontMetrics and size of component.
			g2.dispose();
		}
	}
}
