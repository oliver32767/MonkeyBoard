/*******************************************************************************
 * This file is part of AndroidCommandTool.
 * Copyright © 2013 Oliver Bartley
 * 
 * AndroidCommandTool is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.brtly.monkeyboard;

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

	private static UserPreferences _prefs;

	public static void main(String[] args) {
		// configure logging
		BasicConfigurator.configure();
		_prefs = new UserPreferences();
		initLogging();
		LOG.info("MonkeyBoard v0.1");
		
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
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MasterControlPanel mcp = new MasterControlPanel();
				mcp.setVisible(true);
			}
		});
	}
	
	public static UserPreferences getPreferences() {
		return _prefs;
	}

	private static void initLogging() {
		Logger.getRootLogger().getLoggerRepository().resetConfiguration();
		Logger.getRootLogger().setLevel(Level.ALL);
		
		ConsoleAppender console = new ConsoleAppender(); //create appender
		  //configure the appender
		String PATTERN = "%d [%p|%c|%C{1}] %m%n";
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(Level.INFO);
		console.activateOptions();
		// add appender to any Logger (here is root)
		Logger.getRootLogger().addAppender(console);
	}
}
