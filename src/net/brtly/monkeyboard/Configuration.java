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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

public class Configuration {
	private static final Log LOG = LogFactory.getLog(Configuration.class);
	private Ini _ini;

	protected Configuration() {
		if (!getPreferencesFile().exists()) {
			createDefaultPreferencesFile();
		}
		try {
			_ini = new Ini(getPreferencesFile());
		} catch (InvalidFileFormatException e) {
			LOG.debug(
					"Error reading config "
							+ String.valueOf(getPreferencesFile()), e);
		} catch (IOException e) {
			LOG.debug(
					"Error reading config "
							+ String.valueOf(getPreferencesFile()), e);
		}
	}

	public File getPreferencesFile() {
		String rv;
		if (System.getProperty("act.config") != null) {
			rv = System.getProperty("act.config");
		} else {
			if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
				rv = System.getenv("APPDATA") + "\\AndroidCommandTool.ini";
			} else {
				rv = System.getenv("HOME") + "/.AndroidCommandTool.ini";
			}
		}
		return new File(rv);
	}

	public boolean openPreferencesFile() {
		Desktop dt = Desktop.getDesktop();
		try {
			dt.open(getPreferencesFile());
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private void createDefaultPreferencesFile() {
		// TODO
	}

	public String getSdkDir() {
		return _ini.fetch("general", "androidSdkPath");
	}

	public Level getLogLevel() {
		return Level.toLevel(String.valueOf(_ini.fetch("general", "logLevel"))
				.toUpperCase(), Level.INFO);
	}
}
