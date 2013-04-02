package net.brtly.monkeyboard.gui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import net.brtly.monkeyboard.api.DeviceTask;
import net.brtly.monkeyboard.api.IDeviceController;
import net.brtly.monkeyboard.api.Plugin;
import net.brtly.monkeyboard.api.PluginPanel;
import net.brtly.monkeyboard.api.event.DeviceFocusedEvent;
import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.google.common.eventbus.Subscribe;

@Plugin(title = "Property List", icon = "res/img/properties.png")
public class PropertyList extends PluginPanel {

	private static final long serialVersionUID = 3666981395231189022L;

	private class Property implements Comparable<Property> {
		String key;
		String value;

		public Property(String key, String value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public int compareTo(Property arg0) {
			return key.compareTo(arg0.key);
		}

	}

	private class PropertyTableFormat implements TableFormat<Property> {

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(int column) {
			if (column == 0)
				return "Key";
			else if (column == 1)
				return "Value";

			throw new IllegalStateException();
		}

		@Override
		public Object getColumnValue(Property baseObject, int column) {
			if (column == 0)
				return baseObject.key;
			else if (column == 1)
				return baseObject.value;

			throw new IllegalStateException();
		}

	}

	private class PropertyTextFilterator implements TextFilterator<Property> {

		@Override
		public void getFilterStrings(List<String> baseList, Property element) {
			baseList.add(element.key);
			baseList.add(element.value);
		}

	}

	private static final Log LOG = LogFactory.getLog(PropertyList.class);
	private JTextField textField;
	private JTable table;
	private EventList<Property> propertyList = new BasicEventList<Property>();

	@Override
	public void onCreate() {

		setLayout(new MigLayout("inset 5", "[grow][]", "[][grow]"));

		textField = new JTextField();
		add(textField, "cell 0 0,growx");
		textField.setColumns(10);

		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				synchronized (getDeviceManager().getFocusedDevice()) {
					if (getDeviceManager().getFocusedDevice() != null) {
						fetchDeviceProperties(getDeviceManager()
								.getFocusedDevice());
					}
				}
				textField.setText("");
			}

		});
		add(btnReset, "cell 1 0");

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 1 2 1,grow");

		SortedList<Property> sortedPropertyList = new SortedList<Property>(
				propertyList);

		MatcherEditor<Property> textMatcherEditor = new TextComponentMatcherEditor<Property>(
				textField, new PropertyTextFilterator());
		FilterList<Property> filteredPropertyList = new FilterList<Property>(
				sortedPropertyList, textMatcherEditor);

		AdvancedTableModel<Property> propertyTableModel = GlazedListsSwing
				.eventTableModelWithThreadProxyList(filteredPropertyList,
						new PropertyTableFormat());

		table = new JTable(propertyTableModel);
		scrollPane.setViewportView(table);

		getEventBus().register(this);
	}

	@Subscribe
	public void onDeviceFocusedEvent(DeviceFocusedEvent event) {
		setTitle("Property List [" + event.getSerialNumber() + "]");
		fetchDeviceProperties(event.getSerialNumber());
	}

	private void fetchDeviceProperties(String serial) {
		
	

		DeviceTask<Void, String> task = new DeviceTask<Void, String>() {
			@Override
			public String run(IDeviceController device)
					throws Exception {
				return device.executeShellCommand("getprop");
			}

			@Override
			public void onSuccess(String result) {
				Map<String, String> properties = new HashMap<String, String>();
				Pattern p = Pattern.compile("\\[([\\w.]+)\\]:\\s\\[([\\w.]+)\\]");
				Matcher m = p.matcher(result);
				while (m.find()) {
					properties.put(m.group(1), m.group(2));
				}
				updateData(properties);
			}
		};
		getDeviceManager().submitTask(serial, task);
	}

	private void updateData(Map<String, String> properties) {
		propertyList.clear();
		for (String k : properties.keySet()) {
			propertyList.add(new Property(k, properties.get(k)));
		}
	}
}
