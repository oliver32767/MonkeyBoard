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
package net.brtly.monkeyboard.gui.widget;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;

public class StatusBar extends JPanel {

	JLabel threadCount;
	JLabel memoryStats;

	public StatusBar(Container container) {
//		this.setBorder(new BevelBorder(BevelBorder.LOWERED));
		this.setPreferredSize(new Dimension(container.getWidth(), 18));
		setLayout(new MigLayout("inset 2", "[75px][75px]", "[]"));
		
		threadCount = new JLabel("Threads: -");
		threadCount.setHorizontalAlignment(SwingConstants.LEFT);
		this.add(threadCount, "cell 0 0,alignx left,aligny center");

		memoryStats = new JLabel("Mem: -MB");
		memoryStats.setHorizontalAlignment(SwingConstants.LEFT);
		this.add(memoryStats, "cell 1 0,alignx left,aligny center");

		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				updateStatus();
			}
		};
		Timer displayTimer = new Timer(1000, listener);
		displayTimer.start();

	}

	private void updateStatus() {
		threadCount.setText("Threads:" + Thread.activeCount());
		Runtime rt = Runtime.getRuntime();
		long usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
		memoryStats.setText("Mem: " + usedMB + "MB");
	}
}
