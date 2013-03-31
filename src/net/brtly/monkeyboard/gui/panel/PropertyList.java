package net.brtly.monkeyboard.gui.panel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import net.brtly.monkeyboard.api.DeviceTask;
import net.brtly.monkeyboard.api.IDeviceController;
import net.brtly.monkeyboard.api.Plugin;
import net.brtly.monkeyboard.api.PluginPanel;
import net.brtly.monkeyboard.api.event.DeviceFocusedEvent;
import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;

@Plugin(title = "Property List", icon = "res/img/properties.png")
public class PropertyList extends PluginPanel {
	private JTextField textField;
	private JTable table;

	private class PropertyModel extends AbstractTableModel {

		private Map<String, String> _properties;
		private List<String> _sortedKeys;

		public PropertyModel() {
			_properties = new HashMap<String, String>();
			_sortedKeys = new ArrayList<String>();
			
			getEventBus().register(this);
			String serial = getDeviceManager().getFocusedDevice();
			if (serial != null) {
				fetchDeviceProperties(serial);
			}
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public int getRowCount() {
			return _properties.size();
		}

		@Override
		public String getColumnName(int col) {
	        if (col == 0) {
	        	return "Key";
	        } else {
	        	return "Value";
	        }
	    }
		
		@Override
		public Object getValueAt(int row, int col) {
			if (col == 0) {
				return _sortedKeys.get(row);
			} else {
				return _properties.get(_sortedKeys.get(row));
			}
		}

		@Subscribe
		public void onDeviceFocused(DeviceFocusedEvent event) {
			setTitle("Property List [" + event.getSerialNumber() + "]");
			fetchDeviceProperties(event.getSerialNumber());
		}

		private void fetchDeviceProperties(String serial) {
			DeviceTask<Void, Map<String, String>> task = new DeviceTask<Void, Map<String, String>>() {

				@Override
				public Map<String, String> run(IDeviceController device)
						throws Exception {
					return device.getProperties();
				}

				@Override
				public void onSuccess(Map<String, String> result) {
					updateData(result);
				}

			};
			
			getDeviceManager().submitTask(serial, task);
		}

		private void updateData(Map<String, String> properties) {
			_properties = properties;
			_sortedKeys = asSortedList(properties.keySet());
			fireTableDataChanged();
		}

		public <T extends Comparable<? super T>> List<T> asSortedList(
				Collection<T> c) {
			List<T> list = new ArrayList<T>(c);
			java.util.Collections.sort(list);
			return list;
		}
	}

	@Override
	public void onCreate() {
		setLayout(new MigLayout("inset 5", "[grow][]", "[][grow]"));

		textField = new JTextField();
		add(textField, "cell 0 0,growx");
		textField.setColumns(10);

		JButton btnClear = new JButton("Clear");
		add(btnClear, "cell 1 0");

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 1 2 1,grow");

		table = new JTable();
		table.setModel(new PropertyModel());
		scrollPane.setViewportView(table);
	}
}
