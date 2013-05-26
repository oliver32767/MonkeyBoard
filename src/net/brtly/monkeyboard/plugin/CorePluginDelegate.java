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

package net.brtly.monkeyboard.plugin;

import net.brtly.monkeyboard.api.plugin.Bundle;
import net.brtly.monkeyboard.api.plugin.IPluginContext;
import net.brtly.monkeyboard.api.plugin.IPluginProvider;
import net.brtly.monkeyboard.api.plugin.PluginDelegate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CorePluginDelegate extends PluginDelegate {
	
	private static final Log LOG = LogFactory.getLog(CorePluginDelegate.class);
	
	public CorePluginDelegate(IPluginContext context) {
		super(context);
	}

	@Override
	public void onLoad(Bundle properties) {
		getContext().getPluginManager().register(this,
			new IPluginProvider<DeviceList>() {
			@Override
			public boolean shouldLoadPlugin(int instanceCount) {
				return (instanceCount == 0);
			}

			@Override
			public DeviceList loadPlugin(Bundle properties) {
				return new DeviceList(CorePluginDelegate.this);
			}
		});

		getContext().getPluginManager().register(this,
			new IPluginProvider<ConsolePanel>() {
				@Override
				public boolean shouldLoadPlugin(int instanceCount) {
					return true;
				}

				@Override
				public ConsolePanel loadPlugin(Bundle properties) {
					return new ConsolePanel(CorePluginDelegate.this);
				}
			}
		);
		
		getContext().getPluginManager().register(this,
			new IPluginProvider<PropertyList>() {

				@Override
				public boolean shouldLoadPlugin(int instanceCount) {
					return true;
				}

				@Override
				public PropertyList loadPlugin(Bundle properties) {
					return new PropertyList(CorePluginDelegate.this);
				}
			}	
		);
		
		LOG.trace("Loaded");
	}

	@Override
	public Bundle onUnload() {
		// TODO Auto-generated method stub
		return null;
	}
}
