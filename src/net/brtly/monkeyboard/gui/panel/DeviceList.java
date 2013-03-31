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

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import net.brtly.monkeyboard.adb.DeviceManager;
import net.brtly.monkeyboard.api.DeviceTask;
import net.brtly.monkeyboard.api.IDeviceController;
import net.brtly.monkeyboard.api.Plugin;
import net.brtly.monkeyboard.api.PluginPanel;
import net.brtly.monkeyboard.api.event.DeviceEvent;
import net.brtly.monkeyboard.api.event.DeviceStateChangedEvent;
import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.eventbus.Subscribe;

@Plugin(title = "Device List", icon = "res/img/device.png")
public class DeviceList extends PluginPanel {

	private class SortedArrayList<T> extends ArrayList<T> {

		@SuppressWarnings("unchecked")
		public void insertSorted(T value) {
			add(value);
			Comparable<T> cmp = (Comparable<T>) value;
			for (int i = size() - 1; i > 0 && cmp.compareTo(get(i - 1)) < 0; i--)
				Collections.swap(this, i, i - 1);
		}
	}

	private class DeviceListModel extends AbstractTableModel {

		private String[] _columns = { "", "Serial Number", "OS", "Name" };

		private SortedArrayList<String> _devices;
		private Map<String, String> _names;
		private Map<String, String> _versions;

		public DeviceListModel() {
			_devices = new SortedArrayList<String>();
			_names = new HashMap<String, String>();
			_versions = new HashMap<String, String>();
			if (getDeviceManager() != null) {
				Set<String> dev = getDeviceManager()
						.getDeviceSerialNumbers();
				for (String s : dev) {
					_devices.insertSorted(s);
				}
			}
		}

		public String getSerialNumberForRow(int row) {
			try {
				return _devices.get(row);
			} catch (Exception e) {
				return null;
			}
		}

		public int getRowForSerialNumber(String serial) {
			return _devices.indexOf(serial);
		}

		public void fetchDeviceInfo(final String serial) {
			if (getDeviceManager() == null) {
				return;
			}

			DeviceTask<Void, Map<String, String>> task = new DeviceTask<Void, Map<String, String>>() {
				@Override
				public Map<String, String> run(IDeviceController device)
						throws Exception {
					Map<String, String> rv = new HashMap<String, String>();
					
					if (device.isEmulator()) {
						rv.put("name", device.getAvdName());
					} else {
						rv.put("name", device.getProperty("ro.product.manufacturer")
								+ " " + device.getProperty("ro.product.model"));
					}
					rv.put("version", device.getProperty("ro.build.version.release"));
					return rv;
				}

				@Override
				public void onSuccess(Map<String, String> result) {
					if (result.get("name") == null) {
						_names.put(serial, "Unknown");
					} else {
						_names.put(serial, result.get("name"));
					}
					if (result.get("version") == null) {
						_versions.put(serial, "?.?.?");
					} else {
						_versions.put(serial, result.get("version"));
					}
					fireTableDataChanged();
				}
			};
			getDeviceManager().submitTask(serial, task);
		}

		@Subscribe
		public void onDeviceEvent(DeviceEvent event) {
			LOG.debug("Caught event: " + event.toString());
			switch (event.getEventType()) {
			case DEVICE_CONNECTED:
				LOG.debug("Device added:" + event.getSerialNumber());
				_devices.insertSorted(event.getSerialNumber());
				fireTableDataChanged();
				break;
			case DEVICE_DISCONNECTED:
				_devices.remove(event.getSerialNumber());
				fireTableDataChanged();
				break;
			case DEVICE_STATE_CHANGED:
				LOG.debug("Status: "
						+ ((DeviceStateChangedEvent) event).getState()
								.toString());
				fireTableDataChanged();
				break;
			case DEVICE_BUILD_INFO_CHANGED:
				// lookup the name, and if it isn't in the name map, fetch it
				if (!_names.containsKey(event.getSerialNumber())) {
					fetchDeviceInfo(event.getSerialNumber());
				}
				fireTableDataChanged();
				break;
			case DEVICE_FOCUSED:
				selectFocusedDevice(event.getSerialNumber());
				break;
			}
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					selectFocusedDevice(DeviceManager.getDeviceManager().getFocusedDevice());	
				}
			});
		}
		
		public void selectFocusedDevice(String serial) {
			if (serial == null) {
				_table.clearSelection();
			} else {
				_table.setRowSelectionInterval(
					getRowForSerialNumber(serial),
					getRowForSerialNumber(serial));
			}
		}

		@Override
		public String getColumnName(int col) {
			return _columns[col];
		}

		@Override
		public Class<?> getColumnClass(int col) {
			switch (col) {
			case 0:
				return Icon.class;
			default:
				return String.class;
			}
		}

		@Override
		public int getColumnCount() {
			return _columns.length;
		}

		@Override
		public int getRowCount() {
			return getDeviceManager().getDeviceSerialNumbers()
					.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			// was seeing a lot of IooB exceptions, so be safe and fail to null
			try {
				switch (col) {
				case 0: // Status
					return new ImageIcon(String.format(
							"res/img/android-%s.png",
							getDeviceManager()
									.getDeviceState(_devices.get(row))
									.toString().toLowerCase()));
				case 1: // Serial
					return _devices.get(row);
				case 2: // OS
					return _versions.get(_devices.get(row));
				case 3: // Name
					return _names.get(_devices.get(row));
				default:
					return null;
				}
			} catch (IndexOutOfBoundsException e) {
				return null;
			}
		}
	}

	private class DeviceListListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			if (!arg0.getValueIsAdjusting()) {
				int row = _table.getSelectedRow();
				if (row != -1) {
					String serial = _model.getSerialNumberForRow(row);
					DeviceManager.getDeviceManager().setFocusedDevice(serial);
				}
			}
		}

	}

	private static final Log LOG = LogFactory.getLog(DeviceList.class);

	private JTable _table;
	private DeviceListModel _model;

	@Override
	public void onCreate() {
		setLayout(new MigLayout("inset 5", "[grow]", "[grow]"));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 0,grow");

		_table = new JTable();
		scrollPane.setViewportView(_table);

		_model = new DeviceListModel();
		_model.addTableModelListener(_table);
		_table.getSelectionModel().addListSelectionListener(
				new DeviceListListener());

		_table.setModel(_model);
		_table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		_table.getColumnModel().getColumn(0).setMinWidth(24);
		_table.getColumnModel().getColumn(0).setMaxWidth(24);
		_table.getColumnModel().getColumn(1).setMinWidth(24);
		_table.getColumnModel().getColumn(1).setMaxWidth(300);
		_table.getColumnModel().getColumn(1).setPreferredWidth(150);
//		_table.getColumnModel().getColumn(2).setMinWidth(48);
		_table.getColumnModel().getColumn(2).setMaxWidth(48);

		getEventBus().register(_model);
	}

}
