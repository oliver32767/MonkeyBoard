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
package net.brtly.monkeyboard.gui.panel;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;

import net.brtly.monkeyboard.api.Plugin;
import net.brtly.monkeyboard.api.PluginPanel;
import net.brtly.monkeyboard.gui.widget.JLogTable;
import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

@Plugin(title = "Console Log", icon = "res/img/console.png")
public class ConsolePanel extends PluginPanel {
	public ConsolePanel() {
	}

	private class JLogTableAppender extends WriterAppender {
		@Override
		public void append(LoggingEvent event) {
			_table.log(event.getLevel().toInt(), 
					event.getTimeStamp() - LoggingEvent.getStartTime(), 
					String.format("[%s] %s",
						event.getThreadName(),
						event.getLocationInformation().fullInfo),
					event.getMessage());
		}
	}

	private static final Log LOG = LogFactory.getLog(ConsolePanel.class);

	private JToggleButton tglbtnV;
	private JScrollPane scrollPane;
	private JLogTable _table;
	private JLogTableAppender _appender;
	
	@Override
	public void onCreate() {
		setLayout(new MigLayout("inset 5",
				"[grow][:100:100][24:n:24][24:n:24]", "[::24][grow]"));

		JComboBox comboBox = new JComboBox();
		comboBox.setToolTipText("Log Level");
		comboBox.setMaximumRowCount(6);
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Fatal", "Error", "Warn", "Info", "Debug", "Trace"}));
		comboBox.setSelectedIndex(5);
		add(comboBox, "cell 1 0,growx");

		JButton btnC = new JButton("");
		btnC.setToolTipText("Clear Buffer");
		btnC.setIcon(new ImageIcon(ConsolePanel.class.getResource("/img/clear-document.png")));
		btnC.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				logPangrams();
			}
		});
		add(btnC, "cell 2 0,wmax 24,hmax 26");

		tglbtnV = new JToggleButton("");
		tglbtnV.setToolTipText("Auto Scroll");
		tglbtnV.setIcon(new ImageIcon(ConsolePanel.class.getResource("/img/auto-scroll.png")));
		tglbtnV.setSelected(true);
		tglbtnV.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ev) {
				if (ev.getStateChange() == ItemEvent.SELECTED) {
					_table.setAutoScroll(true);
				} else if (ev.getStateChange() == ItemEvent.DESELECTED) {
					_table.setAutoScroll(false);
				}
			}
		});

		add(tglbtnV, "cell 3 0,wmax 24,hmax 26");

		scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {
					public void adjustmentValueChanged(AdjustmentEvent e) {
					    // TODO figure out what to do with this event?
					}
				});

		add(scrollPane, "cell 0 1 4 1,grow");

		_table = new JLogTable("Time", "Source", "Message");

		_table.getColumnModel().getColumn(0).setMinWidth(50);
		_table.getColumnModel().getColumn(0).setPreferredWidth(50);
		_table.getColumnModel().getColumn(0).setMaxWidth(100);

		_table.getColumnModel().getColumn(1).setMinWidth(50);
		_table.getColumnModel().getColumn(1).setPreferredWidth(50);
		_table.getColumnModel().getColumn(1).setMaxWidth(100);

		_table.getColumnModel().getColumn(2).setMinWidth(50);
		_table.getColumnModel().getColumn(2).setWidth(255);

		_table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		scrollPane.setViewportView(_table);

		_appender = new JLogTableAppender();
		_appender.setThreshold(Level.ALL);
		Logger.getRootLogger().addAppender(_appender);		
	}

	private void logPangrams() {
		LOG.fatal("Portez ce vieux whisky au juge blond qui fume");
		LOG.error("Victor jagt zwölf Boxkämpfer quer über den großen Sylter Deich");
		LOG.warn("Lynx c.q. vos prikt bh: dag zwemjuf!");
		LOG.info("Pójdźże, kiń tę chmurność w głąb flaszy!");
		LOG.debug("Quick hijinx swiftly revamped gazebo");
		LOG.trace("The quick brown fox jumps over the lazy dog");
	}
}
