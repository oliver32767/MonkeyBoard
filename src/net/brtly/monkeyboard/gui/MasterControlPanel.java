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
package net.brtly.monkeyboard.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import net.brtly.monkeyboard.adb.DeviceManager;
import net.brtly.monkeyboard.gui.widget.StatusBar;
import net.brtly.monkeyboard.plugin.CorePluginDelegate;
import net.brtly.monkeyboard.plugin.core.DelegateFilter;
import net.brtly.monkeyboard.plugin.core.PluginLoader;
import net.brtly.monkeyboard.plugin.core.PluginManager;
import net.brtly.monkeyboard.plugin.core.panel.PluginDockableLayout;
import net.brtly.monkeyboard.plugin.core.panel.PluginPanelDockable;
import net.brtly.monkeyboard.plugin.core.panel.PluginPanelDockableFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bibliothek.gui.dock.common.CControl;

import com.google.common.eventbus.EventBus;

public class MasterControlPanel extends JPanel {
	private static final long serialVersionUID = -9025534330767761624L;
	private static final Log LOG = LogFactory.getLog(MasterControlPanel.class);
	private List<Runnable> _runOnClose = new ArrayList<Runnable>();

	private final EventBus _eventBus;
	private final PluginManager _pluginManager;
	private final JFrame _frame;

	private final JMenu _viewMenu;
	private final CControl _dockController;
	private final PluginPanelDockableFactory _panelFactory;

	StatusBar statusPanel;

	public MasterControlPanel(JFrame frame) {
		_frame = frame;
		initWindowListener(frame);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnActions = new JMenu("Actions");
		menuBar.add(mnActions);

		JMenu mnDebug = new JMenu("Debug");
		mnActions.add(mnDebug);

		JMenuItem mntmAddPluginpanel = new JMenuItem("Request null PluginPanel");
		mntmAddPluginpanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println("Requesting null PluginPanel");
				// TODO
			}
		});
		mnDebug.add(mntmAddPluginpanel);

		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);

		_viewMenu = new JMenu("Views");

		_viewMenu.addMenuListener(new MenuListener() {

			@Override
			public void menuCanceled(MenuEvent arg0) {}

			@Override
			public void menuDeselected(MenuEvent arg0) {}

			@Override
			public void menuSelected(MenuEvent arg0) {
				updateViewMenu();
			}
			
		});
		menuBar.add(_viewMenu);

		// INITIALIZE MANAGERS
		// TODO: maybe call this in the Runnable and fire an event when finished
		_eventBus = new SwingEventBus();
		_eventBus.register(this);
		DeviceManager.init(_eventBus);
		
		_pluginManager = new PluginManager(_eventBus);
		_pluginManager.loadPlugins();

		// create the status bar panel and shove it down the bottom of the frame
		statusPanel = new StatusBar(frame);
		_frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);

		_dockController = new CControl(frame);
		_frame.getContentPane().add(_dockController.getContentArea(),
				BorderLayout.CENTER);
		_panelFactory = new PluginPanelDockableFactory(_pluginManager);
		_dockController
				.addMultipleDockableFactory(PluginPanelDockableFactory.ID,
						_panelFactory);
		_dockController.createWorkingArea("root");
		
		updateViewMenu();
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				DeviceManager.start(null);
			}
		});

		_runOnClose.add(new Runnable() {
			public void run() {
				_dockController.destroy();
				DeviceManager.stop();
				DeviceManager.shutdown();
				System.exit(0);
			}
		});
	}

	private void updateViewMenu() {
		_viewMenu.removeAll();
		Set<String> plugins = _pluginManager.getPluginIDs(new DelegateFilter(CorePluginDelegate.class.getName()));
		
		for (String s : plugins) {
			PluginLoader loader = _pluginManager.getPluginLoader(s);
			ViewMenuAction a = new ViewMenuAction(loader) {
				@Override
				public void actionPerformed(ActionEvent e) {
					loadPlugin(this.getLoader());
				}
			};
			a.setEnabled(loader.shouldLoadPlugin(_panelFactory.getInstanceCount(loader.getPluginId())));
			_viewMenu.add(a);
		}
	}
	
	private void loadPlugin(PluginLoader loader) {
		LOG.debug("loading:" + loader.getTitle());
		PluginDockableLayout layout = new PluginDockableLayout(loader.getPluginId(), null);
		PluginPanelDockable dockable = _panelFactory.read(layout);
		if (dockable != null) {
			_dockController.addDockable(dockable);
			dockable.setVisible(true);
			LOG.debug("done");
		} else {
			LOG.warn("Can't load duplicates");
		}
		
	}
	
	private static String createAndShowSdkChooser() {
		String sdk;
		System.setProperty("apple.awt.fileDialogForDirectories", "true");
		FileDialog d = new FileDialog(new JFrame());
		d.setVisible(true);
		System.setProperty("apple.awt.fileDialogForDirectories", "false");
		sdk = d.getDirectory();
		return sdk;
	}

	private void initWindowListener(final JFrame frame) {
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {

			}

			public void windowClosing(WindowEvent e) {
				frame.dispose();
			}

			public void windowClosed(WindowEvent e) {
				for (Runnable onClose : _runOnClose) {
					onClose.run();
				}
			}
		});
	}

}
