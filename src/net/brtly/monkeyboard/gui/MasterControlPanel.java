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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import net.brtly.monkeyboard.adb.DeviceManager;
import net.brtly.monkeyboard.api.IDeviceManager.DeviceManagerState;
import net.brtly.monkeyboard.api.IEventBus;
import net.brtly.monkeyboard.api.PluginPanel;
import net.brtly.monkeyboard.api.event.DeviceManagerStateChangedEvent;
import net.brtly.monkeyboard.gui.panel.ConsolePanel;
import net.brtly.monkeyboard.gui.panel.DeviceList;
import net.brtly.monkeyboard.gui.panel.DeviceProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.station.split.SplitDockProperty;

import com.google.common.eventbus.Subscribe;

public class MasterControlPanel extends JFrame {
	private static final long serialVersionUID = -9025534330767761624L;
	private static final Log LOG = LogFactory.getLog(MasterControlPanel.class);
	private List<Runnable> _runOnClose = new ArrayList<Runnable>();

	private DockController _controller;
	private IEventBus _eventBus;

	JButton btnAdb;
	StatusBar statusPanel;

	public MasterControlPanel() {
		setBounds(120, 80, 600, 400);
		setTitle("Android Control Tool");
		initWindowListener();
		_eventBus = new SwingEventBus();

		_controller = new DockController();
		_controller.setRootWindow(this);

		destroyOnClose(_controller);

//		JToolBar toolBar = new JToolBar();
//		getContentPane().add(toolBar, BorderLayout.NORTH);
//
//		btnAdb = new JButton("adb");
//		btnAdb.setIcon(new ImageIcon(MasterControlPanel.class
//				.getResource("/img/start.png")));
//		btnAdb.setHorizontalTextPosition(SwingConstants.LEADING);
//		btnAdb.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent arg0) {
//				if (DeviceManager.getDeviceManager().getState() == DeviceManagerState.STOPPED ||
//						DeviceManager.getDeviceManager().getState() == DeviceManagerState.FAILED) {
//					DeviceManager
//							.start("/Users/obartley/Library/android-sdk-macosx/platform-tools/adb");
//				} else {
//					DeviceManager.stop();
//				}
//			}
//		});
//		toolBar.add(btnAdb);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnActions = new JMenu("Actions");
		menuBar.add(mnActions);

		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);

		JMenu mnViews = new JMenu("Views");
		menuBar.add(mnViews);

		// create the status bar panel and shove it down the bottom of the frame
		statusPanel = new StatusBar(this);
		this.add(statusPanel, BorderLayout.SOUTH);

		DeviceManager.init(_eventBus);

		createDefaultLayout();

		
		_eventBus.register(this);

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				DeviceManager.start(null);
			}
		});

		runOnClose(new Runnable() {
			public void run() {
				DeviceManager.stop();
				DeviceManager.shutdown();
				System.exit(0);
			}
		});
	}

	private void createDefaultLayout() {
		SplitDockStation station = new SplitDockStation();
		station.drop(loadPlugin(new DeviceList(new PluginContext(DeviceManager
				.getDeviceManager(), _eventBus))));
		station.drop(loadPlugin(new DeviceProperties(new PluginContext(
				DeviceManager.getDeviceManager(), _eventBus))),
				SplitDockProperty.EAST);
		station.drop(loadPlugin(new ConsolePanel(new PluginContext(
				DeviceManager.getDeviceManager(), _eventBus))),
				SplitDockProperty.SOUTH);
		_controller.add(station);
		getContentPane().add(station);
	}

	private DefaultDockable loadPlugin(PluginPanel plugin) {
		DefaultDockable rv = new DefaultDockable();
		rv.setTitleText(plugin.getTitle());
		rv.setTitleIcon(plugin.getIcon());
		rv.add(plugin);
		return rv;
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

	private void initWindowListener() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {

			}

			public void windowClosing(WindowEvent e) {
				dispose();
			}

			public void windowClosed(WindowEvent e) {
				for (Runnable onClose : _runOnClose) {
					onClose.run();
				}
			}
		});
	}

	// private void startAdb() {
	// Thread t = new Thread(new Runnable() {
	// public void run() {
	// try {
	// LOG.debug("Starting DeviceManager");
	// DeviceManager
	// .start("/Users/obartley/Library/android-sdk-macosx/platform-tools/adb");
	// } catch (SocketException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IllegalStateException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// });
	// t.setName("DeviceManager");
	// t.start();
	// }

//	@Subscribe
//	public void onDeviceManagerStateChangedEvent(
//			DeviceManagerStateChangedEvent event) {
//		Icon icon = null;
//		switch (event.getState()) {
//		case STARTING:
//			icon = new ImageIcon(
//					MasterControlPanel.class.getResource("/img/starting.png"));
//			break;
//		case RUNNING:
//			icon = new ImageIcon(
//					MasterControlPanel.class.getResource("/img/stop.png"));
//			break;
//		case STOPPING:
//			icon = new ImageIcon(
//					MasterControlPanel.class.getResource("/img/starting.png"));
//			break;
//		case STOPPED:
//			icon = new ImageIcon(
//					MasterControlPanel.class.getResource("/img/start.png"));
//			break;
//		case FAILED:
//			icon = new ImageIcon(
//					MasterControlPanel.class.getResource("/img/start.png"));
//			break;
//		}
//		btnAdb.setIcon(icon);
//	}

	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Add an instance of Runnable to be run when this window closes
	 * 
	 * @param run
	 */
	public void runOnClose(Runnable run) {
		_runOnClose.add(run);
	}

	/**
	 * Add an instance of CControl to have #destroy() call on when this window
	 * closes
	 * 
	 * @param control
	 */
	public void destroyOnClose(final CControl control) {
		runOnClose(new Runnable() {
			public void run() {
				control.destroy();
			}
		});
	}

	/**
	 * Add an instance of DockController to have #kill() called on when this
	 * window closes
	 * 
	 * @param controller
	 */
	public void destroyOnClose(final DockController controller) {
		runOnClose(new Runnable() {
			public void run() {
				controller.kill();
			}
		});
	}

	/**
	 * Add an instance of DockFrontend to have #kill() on when this window
	 * closes
	 * 
	 * @param frontend
	 */
	public void destroyOnClose(final DockFrontend frontend) {
		runOnClose(new Runnable() {
			public void run() {
				frontend.kill();
			}
		});
	}
}
