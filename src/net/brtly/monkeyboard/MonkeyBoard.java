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
package net.brtly.monkeyboard;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.brtly.monkeyboard.gui.MasterControlPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.kohsuke.args4j.Argument;

import com.android.ddmlib.AndroidDebugBridge;

public class MonkeyBoard {
	private static final Log LOG = LogFactory.getLog(MonkeyBoard.class);

	@Argument(metaVar = "COMMAND", required = false, index = 0)
	String command;

	private static Configuration _prefs;

	public static void main(String[] args) {
		// configure logging

		BasicConfigurator.configure();
		_prefs = new Configuration();
		initLogging();
		LOG.info("Monkey Board v0.1");

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				AndroidDebugBridge.terminate();
				LOG.debug("[finished]");
			}
		});
		createAndShowGui();
	}

	private static void createAndShowGui() {

		if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {

			LOG.debug("MAC OS detected ("
					+ System.getProperty("os.name")
					+ "), applying extra UIManager configuration");
//			UIManager.put("TabbedPaneUI",
//					"javax.swing.plaf.metal.MetalTabbedPaneUI");
//			UIManager.put("TabbedPane.selectedTabPadInsets", new Insets(2, 2,
//					2, 1));
//			UIManager.put("TabbedPane.tabsOpaque", Boolean.TRUE);
//			UIManager.put("TabbedPane.darkShadow", new Color(122, 138, 153));
//			// UIManager.put("TabbedPane.background", new Color(184,207,229));
//			UIManager.put("TabbedPane.selectHighlight",
//					new Color(255, 255, 255));
//			// UIManager.put("TabbedPane.foreground", new Color(51,51,51));
//			UIManager.put("TabbedPane.textIconGap", 4);
//			UIManager.put("TabbedPane.highlight", new Color(255, 255, 255));
//			UIManager.put("TabbedPane.unselectedBackground", new Color(238,
//					238, 238));
//			UIManager.put("TabbedPane.tabRunOverlay", 2);
//			UIManager.put("TabbedPane.light", new Color(238, 238, 238));
//			UIManager.put("TabbedPane.tabsOverlapBorder", Boolean.FALSE);
//			UIManager.put("TabbedPane.selected", new Color(200, 221, 242));
//			UIManager.put("TabbedPane.contentBorderInsets", new Insets(4, 2, 3,
//					3));
//			UIManager.put("TabbedPane.contentAreaColor", new Color(220, 221,
//					242));
//			UIManager.put("TabbedPane.tabAreaInsets", new Insets(2, 2, 0, 6));
//			UIManager.put("TabbedPane.contentOpaque", Boolean.TRUE);
//			UIManager.put("TabbedPane.focus", new Color(99, 130, 191));
//			UIManager.put("TabbedPane.tabAreaBackground", new Color(218, 218,
//					218));
//			UIManager.put("TabbedPane.shadow", new Color(184, 207, 229));
//			UIManager.put("TabbedPane.tabInsets", new Insets(0, 9, 1, 9));
//			UIManager.put("TabbedPane.borderHightlightColor", new Color(99,
//					130, 191));

			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty(
					"com.apple.mrj.application.apple.menu.about.name",
					"Monkey Board");
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Monkey Board");
				frame.setBounds(120, 80, 600, 400);
				
				MasterControlPanel mcp = new MasterControlPanel(frame);
				frame.setVisible(true);
			}
		});
	}

	public static Configuration getPreferences() {
		return _prefs;
	}

	private static void initLogging() {
		Logger.getRootLogger().getLoggerRepository().resetConfiguration();
		Logger.getRootLogger().setLevel(Level.ALL);

		ConsoleAppender console = new ConsoleAppender(); // create appender
		// configure the appender
		String PATTERN = "%d [%p|%c|%C{1}] %m%n";
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(_prefs.getLogLevel());
		console.activateOptions();
		// add appender to any Logger (here is root)
		Logger.getRootLogger().addAppender(console);
	}
}
