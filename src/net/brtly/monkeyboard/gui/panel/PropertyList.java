package net.brtly.monkeyboard.gui.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import net.brtly.monkeyboard.api.DeviceTask;
import net.brtly.monkeyboard.api.IDeviceController;
import net.brtly.monkeyboard.api.Plugin;
import net.brtly.monkeyboard.api.PluginPanel;
import net.brtly.monkeyboard.api.event.DeviceFocusedEvent;
import net.brtly.monkeyboard.api.event.DeviceUnfocusedEvent;
import net.brtly.monkeyboard.gui.widget.JHintTextField;
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
	private JHintTextField textField;
	private JTable table;
	private EventList<Property> propertyList = new BasicEventList<Property>();
	private List<String> propertyEditorHistory = new ArrayList<String>();

	@Override
	public void onCreate() {

		setLayout(new MigLayout("inset 5", "[grow][24:n:24][24:n:24]",
				"[][grow]"));

		textField = new JHintTextField();
		textField.setHint("Search properties");
		textField.setLayout(new BorderLayout());

		JLabel label = new JLabel(new ImageIcon(
				ConsolePanel.class.getResource("/img/clear.png")));
		label.setToolTipText("Clear filter");
		textField.add(label, BorderLayout.EAST);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textField.setText("");
			}
		});

		add(textField, "cell 0 0,growx");

		JButton btnReload = new JButton();
		btnReload.setToolTipText("Reload");
		btnReload.setIcon(new ImageIcon(ConsolePanel.class
				.getResource("/img/reload.png")));
		btnReload.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				fetchDeviceProperties(getDeviceManager().getFocusedDevice());
			}

		});
		add(btnReload, "cell 1 0,wmax 24,hmax 26");

		JButton btnEdit = new JButton();
		btnEdit.setToolTipText("Edit");
		btnEdit.setIcon(new ImageIcon(ConsolePanel.class
				.getResource("/img/edit-property.png")));
		btnEdit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String serial = getDeviceManager().getFocusedDevice();
				if (serial == null) {
					JOptionPane.showMessageDialog(PropertyList.this,
							"No device selected!", null,
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				showPropertyEditor(serial, null);
			}

		});
		add(btnEdit, "cell 2 0,wmax 24,hmax 26");

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 1 3 1,grow");

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
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					String serial = getDeviceManager().getFocusedDevice();
					if (serial == null) {
						return;
					}
					JTable target = (JTable) e.getSource();
					int row = target.getSelectedRow();
					int column = target.getSelectedColumn();
					String defaultValue = target.getValueAt(row, 0) + " "
							+ target.getValueAt(row, 1);
					showPropertyEditor(serial, defaultValue);
				}
			}
		});
		scrollPane.setViewportView(table);

		getEventBus().register(this);

		// Set up a timer event to refresh the property table
		// ScheduledExecutorService exec =
		// Executors.newSingleThreadScheduledExecutor();
		// exec.scheduleAtFixedRate(new Runnable() {
		// @Override
		// public void run() {
		// fetchDeviceProperties(getDeviceManager().getFocusedDevice());
		// }
		// }, 0, 1000, TimeUnit.MILLISECONDS);
	}

	@Subscribe
	public void onDeviceFocusedEvent(DeviceFocusedEvent event) {
		setTitle("Property List [" + event.getSerialNumber() + "]");
		fetchDeviceProperties(event.getSerialNumber());
	}

	@Subscribe
	public void onDeviceUnfocusedEvent(DeviceUnfocusedEvent event) {
		if (event.getFocusedDevice() == null) {
			setTitle("Property List");
			updateData(null);
		}
	}

	private void fetchDeviceProperties(String serial) {

		if (serial == null) {
			updateData(null);
			return;
		}

		DeviceTask<Void, String> task = new DeviceTask<Void, String>() {
			@Override
			public String run(IDeviceController device) throws Exception {
				return device.executeShellCommand("getprop");
			}

			@Override
			public void onSuccess(String result) {
				Map<String, String> properties = new HashMap<String, String>();
				Pattern p = Pattern
						.compile("\\[([\\w.]+)\\]:\\s\\[([\\w.]+)\\]");
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
		if (properties == null) {
			return;
		}
		for (String k : properties.keySet()) {
			propertyList.add(new Property(k, properties.get(k)));
		}
	}

	private void showPropertyEditor(String serial, String defaultValue) {
//		if (defaultValue == null) {
//			defaultValue = "";
//		}

		JPanel message = new JPanel();
		message.setLayout(new MigLayout("inset 5", "[]", "[][]"));
		message.setPreferredSize(new Dimension(400, 100));
		
		JLabel label = new JLabel(
				"<html>"
						+ "Enter the key of the property you wish to edit followed by the desired value, separated with a space.<br>"
						+ "To unset a property, enter the property key without supplying a value."
						+ "</html>");
		message.add(label, "cell 0 0");

		final JComboBox jcb = new JComboBox(propertyEditorHistory.toArray());
		jcb.setEditable(true);
		jcb.getEditor().setItem(defaultValue);
		jcb.addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent arg0) {
				arg0.getComponent().requestFocusInWindow();
			}

			@Override
			public void ancestorMoved(AncestorEvent arg0) {
			}

			@Override
			public void ancestorRemoved(AncestorEvent arg0) {
			}

		});
		message.add(jcb, "cell 0 1, growx");
		if (JOptionPane.showConfirmDialog(PropertyList.this, message,
				"setprop property.key [value]", JOptionPane.OK_CANCEL_OPTION) != 0) {
			// then OK was not pressed, exit without doing anything
			return;
		}
		
		String rv = (String) jcb.getSelectedItem();
		rv = rv.trim();
		if (rv == null) {
			return;
		}
		
		if (!propertyEditorHistory.contains(rv)) {
			// put unique items at the beginning of the list
			propertyEditorHistory.add(0, rv);
		}
		String[] kv = rv.split(" ", 2);

		if (kv.length == 1) {
			setProperty(serial, kv[0], "");
		} else {
			setProperty(serial, kv[0], kv[1]);
		}
	}

	private void setProperty(final String serial, String key, String value) {
		final String command = "setprop " + key + " \"" + value + "\"";
		DeviceTask<Void, Void> task = new DeviceTask<Void, Void>() {

			@Override
			public Void run(IDeviceController device) throws Exception {
				device.executeShellCommand(command);
				return null;
			}

			@Override
			public void onSuccess(Void result) {
				fetchDeviceProperties(serial);
			}

		};
		getDeviceManager().submitTask(serial, task);
	}
}
