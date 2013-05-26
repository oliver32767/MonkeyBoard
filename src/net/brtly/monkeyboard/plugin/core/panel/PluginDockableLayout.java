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

package net.brtly.monkeyboard.plugin.core.panel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.brtly.monkeyboard.api.plugin.Bundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bibliothek.gui.dock.common.MultipleCDockableLayout;
import bibliothek.util.xml.XElement;

public class PluginDockableLayout implements MultipleCDockableLayout {

	private static final Log LOG = LogFactory.getLog(PluginDockableLayout.class);
	
	private static final String ID = "ID";
	private static final String BUNDLE = "BUNDLE";
	private static final String BUNDLE_SIZE = "BUNDLE_SIZE";
	
	private String _id;
	private Bundle _bundle;
	
	public PluginDockableLayout(String id, Bundle bundle) {
		_id = id;
		_bundle = bundle;
	}
	
	public String getId() {
		return _id;	
	}
	
	public Bundle getBundle() {
		return _bundle;
	}
	
	@Override
	public void readStream(DataInputStream in) throws IOException {
		_id = in.readUTF(); // get the plugin ID
		int bs = in.readInt(); // get the size, in bytes, of the Bundle
		
		byte[] b = new byte[bs]; // read the Bundle
		in.read(b);
	}
	
	@Override
	public void writeStream(DataOutputStream out) throws IOException {
		out.writeUTF(_id); // wirthe plugin id
		
		byte[] b = Bundle.toByteArray(_bundle); // write the size in bytes of the Bundle
		out.writeInt(b.length);
		
		out.write(b); // write the Bundle
	}

	@Override
	public void readXML(XElement element) {
		_id = element.getString(ID);
		try {
			_bundle = Bundle.fromByteArray(element.getByteArray(BUNDLE));
		} catch (IOException e) {
			LOG.warn("Exception reading Bundle for: " + _id, e);
			_bundle = null;
		} catch (ClassNotFoundException e) {
			LOG.warn("Exception reading Bundle for: " + _id, e);
			_bundle = null;
		}
	}

	@Override
	public void writeXML(XElement element) {
		element.addString(ID, _id);
		try {
			element.addByteArray(BUNDLE, Bundle.toByteArray(_bundle));
		} catch (IOException e) {
			LOG.warn("Exception writing Bundle for: " + _id, e);
			element.addByteArray(BUNDLE, null);
		}
	}		
}