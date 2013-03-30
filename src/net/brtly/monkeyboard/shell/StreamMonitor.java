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
package net.brtly.monkeyboard.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StreamMonitor extends Thread {
	private static final Log LOG = LogFactory.getLog(StreamMonitor.class);

	/**
	 * Used to define the type of output coming from the stream
	 * 
	 */
	public enum StreamType {
		STDOUT, STDERR
	}

	/**
	 * Implement this interface to receive output events from the stream being
	 * monitored
	 * 
	 */
	public static interface StreamListener {
		public abstract void onOutput(StreamType type, String output);
	}

	private InputStream _is = null;
	private StreamType _type = null;
	private StreamListener _listener = null;

	/**
	 * Initialize a StreamMonitor of type STDOUT with the default listener
	 * 
	 * @param is
	 *            the InputStream to monitor
	 */
	public StreamMonitor(InputStream is) {
		init(is, null, null);
	}

	/**
	 * Initialize a StreamMonitor with a custom type with the default listener
	 * 
	 * @param is
	 *            the input stream to monitor
	 * @param type
	 *            the type of InputStream being monitored
	 */
	public StreamMonitor(InputStream is, StreamType type) {
		init(is, type, null);
	}

	/**
	 * Initialize a StreamMonitor with a custom type with a custom listener
	 * 
	 * @param is
	 *            the input stream to monitor
	 * @param listener
	 *            instance of StreamMonitorListener to receive output events
	 */
	public StreamMonitor(InputStream is,
			StreamListener listener) {
		init(is, null, listener);
	}
	
	/**
	 * Initialize a StreamMonitor with a custom type with a custom listener
	 * 
	 * @param is
	 *            the input stream to monitor
	 * @param type
	 *            the type of InputStream being monitored
	 * @param listener
	 *            instance of StreamMonitorListener to receive output events
	 */
	public StreamMonitor(InputStream is,
			StreamType type,
			StreamListener listener) {
		init(is, type, listener);
	}
	

	private void init(InputStream is, StreamType type, StreamListener listener) {
		_is = is;

		_type = type != null ? type : StreamType.STDERR;

		if (listener == null) {
			_listener = StreamMonitor.getDefaultListener();
		} else {
			_listener = listener;
		}
	}

	/**
	 * Initialize an instance of StreamMonitorListener that prints all output to
	 * System.out or System.err, depending on the StreamType
	 * 
	 * @return an instance of StreamMonitorListener that prints all output to
	 *         System.out or System.err, depending on the StreamType
	 */
	public static StreamListener getDefaultListener() {
		return new StreamListener() {
			public void onOutput(StreamType type, String output) {
				switch (type) {
				case STDOUT:
					System.out.println(output);
					break;
				case STDERR:
					System.err.println(output);
				}
			}
		};
	}

	/**
	 * Start monitoring the InputStream
	 */
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(_is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (_listener != null) {
					_listener.onOutput(_type, line);
				}
			}
			_listener.onOutput(null, null);
		} catch (IOException ioe) {
			LOG.trace(ioe.getMessage());
		}
	}
}
