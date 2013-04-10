/*******************************************************************************
 * This file is part of MonkeyBoard
 * Copyright � 2013 Oliver Bartley
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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.eventbus.AsyncEventBus;

/**
 * A guava EventBus that dispatches events on the AWT Event Thread
 * @author obartley
 *
 */
public class SwingEventBus extends AsyncEventBus {

	private static final Log LOG = LogFactory.getLog(SwingEventBus.class);

	public SwingEventBus() {
		super(new EventQueueExecutor());
	}

}
