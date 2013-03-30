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

import java.io.IOException;

import net.brtly.monkeyboard.shell.StreamMonitor.StreamListener;
import net.brtly.monkeyboard.shell.StreamMonitor.StreamType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Defines a shell command to be run that can be added to a thread pool for
 * asynchronous execution, or executed on the current thread with an optional
 * timeout
 * 
 * @author obartley
 * 
 */
public class ShellCommand implements Runnable {
	/**
	 * Objects of this type can receive events from a running command
	 * 
	 * @author obartley
	 */
	public class ShellCommandListener {
		public void onStdOut(ShellCommand command, String line) {
		};

		public void onStdErr(ShellCommand command, String line) {
		};

		public void onExit(ShellCommand command, int status) {
		};
	}

	/**
	 * Basic subclass of CommandEventListener that prints output to
	 * STDOUT/STDERR
	 * 
	 * @author obartley
	 * 
	 */
	public class StandardShelCommandListener extends ShellCommandListener {
		@Override
		public void onStdOut(ShellCommand command, String line) {
			System.out.println(command.getCommand() + ":" + line);
		}

		@Override
		public void onStdErr(ShellCommand command, String line) {
			System.err.println(command.getCommand() + ":" + line);
		}
	}

	/**
	 * Thread class to be used as a worker to timeout ShellCommands
	 */
	private static class Worker extends Thread {
		private final Process process;
		private Integer exitValue;

		Worker(final Process process) {
			this.process = process;
		}

		public Integer getExitValue() {
			return exitValue;
		}

		@Override
		public void run() {
			try {
				LOG.trace(this.getName());
				exitValue = process.waitFor();
			} catch (InterruptedException ignore) {
				LOG.trace("interrupted!");
				return;
			}
			LOG.trace("exited (" + Integer.toString(exitValue) + ")");
		}
	}

	protected static final Log LOG = LogFactory.getLog(ShellCommand.class);

	private String _command;

	private boolean _isRunning = false;
	private String _output = null;
	private String _error = null;
	private Integer _status = 0;

	private ShellCommandListener _listener;

	protected ShellCommand() {
	};

	/**
	 * Initialize an object that represents a single shell command
	 * 
	 * @param A
	 *            String containing the full command to be executed
	 */
	public ShellCommand(String command) {
		_command = command.trim();
		_listener = new ShellCommandListener();
	}

	/**
	 * Set the CommandEventListener that will receive output events
	 * 
	 * @param listener
	 *            the CommandEventListener that will receive output events
	 */
	public void setListener(ShellCommandListener listener) {
		synchronized (_listener) {
			_listener = listener;
		}
	}

	/**
	 * Get the CommandEventListener that will receive output events
	 * 
	 * @return the CommandEventListener that will receive output events
	 */
	public ShellCommandListener getListener() {
		synchronized (_listener) {
			return _listener;
		}
	}

	/**
	 * Get the full command, including the path to the adb executable
	 * 
	 * @return
	 */
	public String getCommand() {
		return _command;
	}

	/**
	 * Gets the output of the command
	 * 
	 * @return the output of the command once it has run, null otherwise
	 */
	public String getOutput() {
		return _output;
	}

	/**
	 * Gets the error output of the command
	 * 
	 * @return the error output of the command once it has run, null otherwise
	 */
	public String getError() {
		return _error;
	}

	/**
	 * Gets the exit status of the command once it has run
	 * 
	 * @return null if the command has not been run or if an exception was
	 *         encountered while running, otherwise the command's exit status
	 */
	public Integer getStatus() {
		return _status;
	}

	/**
	 * Determine if the command is currently being executed
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return _isRunning;
	}

	@Override
	public void run() {
		execute(0);
	}

	/**
	 * Execute the command on the current thread with a timeout
	 * @param timeOut milliseconds for this command to timeout. A timeout of 0 means to wait forever 
	 * @return the exit status of the command, or null if there was an exception
	 */
	public Integer execute(final long timeOut) {
		_isRunning = true;
		_status = -1;
		_output = null;
		_error = null;
		final ShellCommand sender = this;
		final String command = getCommand();
		_isRunning = true;
		Process proc = null;
		try {
			// create the process which will run the command
			proc = Runtime.getRuntime().exec(command);

			// consume and record the error and output streams
			final StringBuilder out = new StringBuilder();
			final StringBuilder err = new StringBuilder();
			StreamListener streamListener = new StreamListener() {
				@Override
				public void onOutput(StreamType type, String output) {
					if (output != null) {
						switch (type) {
						case STDOUT:
							out.append(output + '\n');
							_output = out.toString();
							getListener().onStdOut(sender, output);
							break;
						case STDERR:
							err.append(output + '\n');
							_error = err.toString();
							getListener().onStdErr(sender, output);
						}
					}
				}
			};
			StreamMonitor osm = new StreamMonitor(proc.getInputStream(),
					StreamType.STDOUT, streamListener);
			StreamMonitor esm = new StreamMonitor(proc.getErrorStream(),
					StreamType.STDERR, streamListener);
			osm.start();
			esm.start();

			// create and start a Worker thread which this thread will join for
			// the timeout period
			Worker worker = new Worker(proc);
			worker.setName(command);
			worker.start();
			try {
				worker.join(timeOut);
				_status = worker.getExitValue();
				if (_status == null) {
					// if we get this far then we never got an exit value from the
					// worker thread as a result of a timeout
					String errorMessage = "The command [" + command
							+ "] timed out.";
					LOG.warn(errorMessage);
				}

				
//				throw new RuntimeException(errorMessage);
			} catch (InterruptedException ex) {
				worker.interrupt();
				Thread.currentThread().interrupt();
//				throw ex;
			}
		} catch (IOException ex) {
			String errorMessage = "The command [" + command
					+ "] did not complete due to an IO error.";
			LOG.warn(errorMessage, ex);
//			throw new RuntimeException(errorMessage, ex);
		}
		
		return _status;
	}
}
