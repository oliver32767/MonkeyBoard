/*******************************************************************************
 * This file is part of MonkeyBoard
 * Copyright © 2013 Oliver Bartley
 * 
 * MonkeyBoard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * MonkeyBoard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MonkeyBoard.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.brtly.monkeyboard.gui;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import net.brtly.monkeyboard.api.Plugin;
import net.miginfocom.swing.MigLayout;

@Plugin(
		title="LogCat",
		icon="res/img/droid-cat.png"
		)
public class LogCatPanel extends DockablePanel {
	private static final long serialVersionUID = 7748144642075096994L;
	private JTextField textFilter;
	private JComboBox comboLevel;
	public LogCatPanel() {
		setLayout(new MigLayout("insets 5", "[::32][200:200.00,grow][100][::32.00][::32.00][::32.00]", "[][grow]"));
		
		JButton btnDevice = new JButton("D");
		add(btnDevice, "cell 0 0,wmax 32");
		
		textFilter = new JTextField();
		add(textFilter, "cell 1 0,growx");
		textFilter.setColumns(10);
		
		comboLevel = new JComboBox();
		comboLevel.setModel(new DefaultComboBoxModel(new String[] {"verbose", "debug", "info", "warning", "error", "assert", "wtf"}));
		add(comboLevel, "cell 2 0,growx");
		
		JButton btnClear = new JButton("C");
		add(btnClear, "cell 3 0,wmax 32");
		
		JButton btnSave = new JButton("S");
		add(btnSave, "cell 4 0,wmax 32");
		
		JButton btnScroll = new JButton("V");
		add(btnScroll, "cell 5 0,wmax 32");
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 1 6 1,grow");
		
		JTextPane textPane = new JTextPane();
		scrollPane.setViewportView(textPane);
	}

}
