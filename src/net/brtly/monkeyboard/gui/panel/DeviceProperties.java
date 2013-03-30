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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import net.brtly.monkeyboard.api.DeviceTask;
import net.brtly.monkeyboard.api.IDeviceController;
import net.brtly.monkeyboard.api.IPluginContext;
import net.brtly.monkeyboard.api.Plugin;
import net.brtly.monkeyboard.api.PluginPanel;
import net.brtly.monkeyboard.api.event.DeviceEvent;
import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.action.actions.SelectableDockActionGroup;
import bibliothek.gui.dock.action.actions.SimpleSelectableAction;

import com.google.common.eventbus.Subscribe;

@Plugin(title = "Device Properties", icon = "res/img/properties.png")
public class DeviceProperties extends PluginPanel {

	class MyTreeModelListener implements TreeModelListener {
		public void treeNodesChanged(TreeModelEvent e) {
			DefaultMutableTreeNode node;
			node = (DefaultMutableTreeNode) (e.getTreePath()
					.getLastPathComponent());

			/*
			 * If the event lists children, then the changed node is the child
			 * of the node we have already gotten. Otherwise, the changed node
			 * and the specified node are the same.
			 */
			try {
				int index = e.getChildIndices()[0];
				node = (DefaultMutableTreeNode) (node.getChildAt(index));
			} catch (NullPointerException exc) {
			}

			System.out.println("The user has finished editing the node.");
			System.out.println("New value: " + node.getUserObject());
		}

		public void treeNodesInserted(TreeModelEvent e) {
		}

		public void treeNodesRemoved(TreeModelEvent e) {
		}

		public void treeStructureChanged(TreeModelEvent e) {
		}
	}

	private static final Log LOG = LogFactory.getLog(DeviceProperties.class);

	private JTree _tree;
	private DefaultMutableTreeNode _root;
	private DefaultTreeModel _model;

	private String _currentDevice;

	public DeviceProperties(IPluginContext runtime) {
		super(runtime);
		setLayout(new MigLayout("inset 5", "[grow]", "[grow]"));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 0,grow");

		_root = new DefaultMutableTreeNode("Root Node");
		_model = new DefaultTreeModel(_root);
		_model.addTreeModelListener(new MyTreeModelListener());

		_tree = new JTree(_model);
		_tree.setEditable(true);
		_tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		_tree.setShowsRootHandles(true);
		_tree.setRootVisible(false);
		scrollPane.setViewportView(_tree);
		
		getRuntime().getEventBus().register(this);
	}

	@Subscribe
	public void onDeviceEvent(DeviceEvent event) {
		switch (event.getEventType()) {
		case DEVICE_FOCUSED:
			LOG.debug("Switched focus:" + event.getSerialNumber());
			_currentDevice = event.getSerialNumber();
			reloadProperties();
			break;
		case DEVICE_BUILD_INFO_CHANGED:
			if (event.getSerialNumber().equals(_currentDevice)) {
				reloadProperties();
			}
			break;
		default:
			// nuthin'
		}
	}

	/**
	 * Reloads the device properties and update the tree model
	 */
	public void reloadProperties() {

		if (_currentDevice != null) {
			DeviceTask<Void, Map<String, String>> task = new DeviceTask<Void, Map<String, String>>() {

				@Override
				protected Map<String, String> doInBackground(
						IDeviceController device) throws Exception {
					return device.getProperties();
				}

				@Override
				protected void onPostExecute(Map<String, String> result) {
					updateTree(result);
				}

			};
			getRuntime().getDeviceManager().submitTask(_currentDevice, task);
		} else {
			updateTree(new HashMap<String, String>());
		}
	}

	public void updateTree(final Map<String, String> properties) {
		_root.setUserObject(_currentDevice);
		_root.removeAllChildren();
		for (String s : properties.keySet()) {
			createNodeForProperty(_root, s, properties.get(s));
		}

		_model.reload(_root);

	}

	private void createNodeForProperty(DefaultMutableTreeNode parent,
			String path, String value) {

		String[] split = path.split("\\.", 2);

		if (split.length == 1) {
			DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(
					String.format("%s = %s", split[0], value));
			leaf.setAllowsChildren(false);

			sortedNodeInsert(parent, leaf);
			return;
		}

		// "foo.bar.spam" == {"foo", "bar.spam"}
		// "bar" == {"bar"}
		DefaultMutableTreeNode newParent = null;
		Enumeration<?> children = parent.children();
		while (children.hasMoreElements()) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) children
					.nextElement();
			if (child.getUserObject().equals(split[0])) {
				newParent = child;
			}
		}
		if (newParent == null) {
			// the parent wasn't found, create it
			newParent = new DefaultMutableTreeNode(split[0]);
			sortedNodeInsert(parent, newParent);
		}
		createNodeForProperty(newParent, split[1], value);

	}

	private void sortedNodeInsert(DefaultMutableTreeNode parent,
			DefaultMutableTreeNode child) {
		Enumeration<?> children = parent.children();
		int index = 0;
		int insert = parent.getChildCount();
		while (children.hasMoreElements()) {
			DefaultMutableTreeNode curr = (DefaultMutableTreeNode) children
					.nextElement();

			if (!child.getAllowsChildren() && curr.getAllowsChildren()) {
				// then it's a leaf node, so we automatically sort it after an
				// inner node. skip the next comparison
				// this empty block is because (!(! is ugly
			} else {
				if (((String) curr.getUserObject()).compareTo((String) child
						.getUserObject()) > 0) {
					insert = index;
					break;
				}
			}
			index++;
		}
		parent.insert(child, insert);
	}

}
