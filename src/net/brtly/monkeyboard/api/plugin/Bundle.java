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

package net.brtly.monkeyboard.api.plugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Bundle implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(Bundle.class);

	private Map<String, Object> _map = null;

	/**
	 * Construct a new, empty Bundle
	 */
	public Bundle() {
		_map = new HashMap<String, Object>();
	}

	/**
	 * Create a Bundle containing a copy of the mappings from the given Bundle
	 * 
	 * @param b
	 */
	public Bundle(Bundle b) {
		if (b._map != null) {
			_map = new HashMap<String, Object>(b._map);
		} else {
			_map = null;
		}
	}

	/**
	 * Clones the current Bundle. The internal map is cloned, but the keys and
	 * values to which it refers are copied by reference.
	 */
	@Override
	public Object clone() {
		return new Bundle(this);
	}

	/**
	 * Factory method that creates a Bundle by deserializing a byte array
	 * produced by {@link #toByteArray(Bundle)}
	 * 
	 * @param b
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Bundle fromByteArray(byte[] b) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(b);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
			return (Bundle) in.readObject();
		} finally {
			bis.close();
			in.close();
		}
	}

	/**
	 * Convert a Bundle object to a byte array that can be deserialized by
	 * {@link #fromByteArray(byte[])}
	 * 
	 * @param b
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray(Bundle b) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(b);
			return bos.toByteArray();
		} finally {
			out.close();
			bos.close();
		}
	}

	/**
	 * Return the number of mappings in this Bundle
	 */
	public int size() {
		return _map.size();
	}

	/**
	 * Return true if the Bundle contains no mappings
	 */
	public boolean isEmpty() {
		return _map.isEmpty();
	}

	/**
	 * Remove all mappings from this Bundle
	 */
	public void clear() {
		_map.clear();
	}

	/**
	 * Return true if this Bundle contains a mapping for the given key
	 */
	public boolean containsKey(String key) {
		return _map.containsKey(key);
	}

	/**
	 * Return a Set containing all the keys in this Bundle
	 */
	public Set<String> keySet() {
		return null;
	}
	
	/**
	 * Return the Object mapped to the given key
	 */
	public Object get(String key) {
		return _map.get(key);
	}

	/**
	 * Remove the mapping for the given key
	 */
	public void remove(String key) {
		_map.remove(key);
	}

	private void typeWarning(String className, String key, Object actual,
			Object defaultValue, ClassCastException e) {
		StringBuilder sb = new StringBuilder();
		sb.append("Key '");
		sb.append(key);
		sb.append("' expected ");
		sb.append(className);
		sb.append(" but value was a ");
		sb.append(actual.getClass().getName());
		sb.append(". The default value ");
		sb.append(defaultValue);
		sb.append(" was returned instead.");
		LOG.warn(sb.toString(), e);
	}

	/**
	 * Return the boolean mapped to the given key, false if the key is not
	 * present
	 */
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	/**
	 * Return the boolean mapped to the given key, or the default value if the
	 * key is not present
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		Object o = _map.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return (Boolean) o;
		} catch (ClassCastException e) {
			typeWarning("Boolean", key, o, defaultValue, e);
			return defaultValue;
		}
	}

	/**
	 * Return the Byte mapped to the given key, false if the key is not present
	 */
	public byte getByte(String key) {
		return getByte(key, (byte) 0);
	}

	/**
	 * Return the Byte mapped to the given key, or the default value if the key
	 * is not present
	 */
	public byte getByte(String key, byte defaultValue) {
		Object o = _map.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return (Byte) o;
		} catch (ClassCastException e) {
			typeWarning("Byte", key, o, defaultValue, e);
			return defaultValue;
		}
	}

	/**
	 * Return the byte array mapped to the given key, null if the key is not
	 * present
	 */
	public byte[] getByteArray(String key) {
		Object o = _map.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (byte[]) o;
		} catch (ClassCastException e) {
			typeWarning("byte[]", key, o, null, e);
			return null;
		}
	}

	/**
	 * Return the Char mapped to the given key, (char) 0 if the key is not
	 * present
	 */
	public char getChar(String key) {
		return getChar(key, (char) 0);
	}

	/**
	 * Return the Char mapped to the given key, or the default value if the key
	 * is not present
	 */
	public char getChar(String key, char defaultValue) {
		Object o = _map.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return (Character) o;
		} catch (ClassCastException e) {
			typeWarning("Character", key, o, defaultValue, e);
			return defaultValue;
		}
	}

	/**
	 * Return the double mapped to the given key, 0 if the key is not present
	 */
	public double getDouble(String key) {
		return getDouble(key, (double) 0);
	}

	/**
	 * Return the double mapped to the given key, or the default value if the
	 * key is not present
	 */
	public double getDouble(String key, double defaultValue) {
		Object o = _map.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return (Double) o;
		} catch (ClassCastException e) {
			typeWarning("Double", key, o, defaultValue, e);
			return defaultValue;
		}
	}

	/**
	 * Return the Float mapped to the given key, 0.0f if the key is not present
	 */
	public Float getFloat(String key) {
		return getFloat(key, 0.0f);
	}

	/**
	 * Return the Float mapped to the given key, or the default value if the key
	 * is not present
	 */
	public Float getFloat(String key, Float defaultValue) {
		Object o = _map.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return (Float) o;
		} catch (ClassCastException e) {
			typeWarning("Float", key, o, defaultValue, e);
			return defaultValue;
		}
	}

	/**
	 * Return the int mapped to the given key, 0 if the key is not present
	 */
	public int getInt(String key) {
		return getInt(key, 0);
	}

	/**
	 * Return the int mapped to the given key, or the default value if the key
	 * is not present
	 */
	public int getInt(String key, int defaultValue) {
		Object o = _map.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return (Integer) o;
		} catch (ClassCastException e) {
			typeWarning("Integer", key, o, defaultValue, e);
			return defaultValue;
		}
	}

	/**
	 * Return the Long mapped to the given key, (long) 0 if the key is not
	 * present
	 */
	public Long getLong(String key) {
		return getLong(key, (long) 0);
	}

	/**
	 * Return the Long mapped to the given key, or the default value if the key
	 * is not present
	 */
	public Long getLong(String key, Long defaultValue) {
		Object o = _map.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return (Long) o;
		} catch (ClassCastException e) {
			typeWarning("Long", key, o, defaultValue, e);
			return defaultValue;
		}
	}

	/**
	 * Return the Short mapped to the given key, (short) 0 if the key is not
	 * present
	 */
	public Short getShort(String key) {
		return getShort(key, (short) 0);
	}

	/**
	 * Return the Short mapped to the given key, or the default value if the key
	 * is not present
	 */
	public Short getShort(String key, Short defaultValue) {
		Object o = _map.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return (Short) o;
		} catch (ClassCastException e) {
			typeWarning("Short", key, o, defaultValue, e);
			return defaultValue;
		}
	}

	/**
	 * Return the String mapped to the given key, null if the key is not present
	 */
	public String getString(String key) {
		return getString(key, null);
	}

	/**
	 * Return the String mapped to the given key, or the default value if the
	 * key is not present
	 */
	public String getString(String key, String defaultValue) {
		Object o = _map.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return (String) o;
		} catch (ClassCastException e) {
			typeWarning("String", key, o, defaultValue, e);
			return defaultValue;
		}
	}

	/**
	 * Map the supplied value to the given key, replacing the value if the key
	 * already exists
	 * 
	 * @return this
	 */
	public Bundle putBoolean(String key, boolean value) {
		_map.put(key, value);
		return this;
	}

	/**
	 * Map the supplied value to the given key, replacing the value if the key
	 * already exists
	 * 
	 * @return this
	 */
	public Bundle putByte(String key, byte value) {
		_map.put(key, value);
		return this;
	}

	/**
	 * Map the supplied value to the given key, replacing the value if the key
	 * already exists
	 * 
	 * @return this
	 */
	public Bundle putByteArray(String key, byte[] value) {
		_map.put(key, value);
		return this;
	}

	/**
	 * Map the supplied value to the given key, replacing the value if the key
	 * already exists
	 * 
	 * @return this
	 */
	public Bundle putChar(String key, char value) {
		_map.put(key, value);
		return this;
	}

	/**
	 * Map the supplied value to the given key, replacing the value if the key
	 * already exists
	 * 
	 * @return this
	 */
	public Bundle putDouble(String key, double value) {
		_map.put(key, value);
		return this;
	}

	/**
	 * Map the supplied value to the given key, replacing the value if the key
	 * already exists
	 * 
	 * @return this
	 */
	public Bundle putFloat(String key, float value) {
		_map.put(key, value);
		return this;
	}

	/**
	 * Map the supplied value to the given key, replacing the value if the key
	 * already exists
	 * 
	 * @return this
	 */
	public Bundle putInt(String key, int value) {
		_map.put(key, value);
		return this;
	}

	/**
	 * Map the supplied value to the given key, replacing the value if the key
	 * already exists
	 * 
	 * @return this
	 */
	public Bundle putLong(String key, long value) {
		_map.put(key, value);
		return this;
	}

	/**
	 * Map the supplied value to the given key, replacing the value if the key
	 * already exists
	 * 
	 * @return this
	 */
	public Bundle putShort(String key, short value) {
		_map.put(key, value);
		return this;
	}

	/**
	 * Map the supplied value to the given key, replacing the value if the key
	 * already exists
	 * 
	 * @return this
	 */
	public Bundle putString(String key, String value) {
		_map.put(key, value);
		return this;
	}
	
	/**
	 * Return a string representation of this Object
	 */
	@Override
	public String toString() {
		String rv;
		try {
			rv = String.format("%s (%d items,  %d bytes)",
					this.getClass().getName(),
					_map.size(),
					Bundle.toByteArray(this).length);
		} catch (IOException e) {
			rv = String.format("%s (%d items,  UNKNOWN bytes)",
					this.getClass().getName(),
					_map.size());
		}
		return rv;
	}
	
	/**
	 * Compare two Bundles by comparing resulting serialized byte arrays
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Bundle) {
			Bundle b = (Bundle) obj;
			try {
				byte[] ba1 = Bundle.toByteArray(this);
				byte[] ba2 = Bundle.toByteArray(b);
				return Arrays.equals(ba1, ba2);
			} catch (IOException e) {
				return false;
			}
		} else {
			return false;
		}
	}
}
