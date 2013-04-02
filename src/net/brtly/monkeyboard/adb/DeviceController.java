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
package net.brtly.monkeyboard.adb;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;

import net.brtly.monkeyboard.api.IDeviceController;

import com.android.chimpchat.ChimpManager;
import com.android.chimpchat.adb.CommandOutputCapture;
import com.android.chimpchat.core.IChimpDevice;
import com.android.chimpchat.core.IChimpImage;
import com.android.chimpchat.core.IChimpView;
import com.android.chimpchat.core.IMultiSelector;
import com.android.chimpchat.core.ISelector;
import com.android.chimpchat.core.PhysicalButton;
import com.android.chimpchat.core.TouchPressType;
import com.android.chimpchat.hierarchyviewer.HierarchyViewer;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.Client;
import com.android.ddmlib.DdmPreferences;
import com.android.ddmlib.FileListingService;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.InstallException;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.SyncException;
import com.android.ddmlib.SyncService;
import com.android.ddmlib.TimeoutException;
import com.android.ddmlib.log.LogReceiver;

public class DeviceController implements IDeviceController {
	
	private IDevice _device;
	private IChimpDevice _chimpDevice;
	
	protected DeviceController(IDevice device, IChimpDevice chimpDevice) {
		_device = device;
		_chimpDevice = chimpDevice;
	}
	
	@Override
	public String getSerialNumber() {
		return _device.getSerialNumber();
	}

	@Override
	public String getAvdName() {
		return _device.getAvdName();
	}

	@Override
	public com.android.ddmlib.IDevice.DeviceState getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getProperties() {
		return _device.getProperties();
	}

	@Override
	public int getPropertyCount() {
		return _device.getPropertyCount();
	}

	@Override
	public String getProperty(String name) {
		return _device.getProperty(name);
	}

	@Override
	public String getMountPoint(String name) {
		return _device.getMountPoint(name);
	}

	@Override
	public boolean isOnline() {
		return _device.isOnline();
	}

	@Override
	public boolean isEmulator() {
		return _device.isEmulator();
	}

	@Override
	public boolean isOffline() {
		return _device.isOffline();
	}

	@Override
	public boolean isBootLoader() {
		return _device.isBootLoader();
	}

	@Override
	public boolean hasClients() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Client[] getClients() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Client getClient(String applicationName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SyncService getSyncService() throws TimeoutException,
			AdbCommandRejectedException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileListingService getFileListingService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RawImage getScreenshot() throws TimeoutException,
			AdbCommandRejectedException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String executeShellCommand(String command) throws TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
		return executeShellCommand(command, DdmPreferences.getTimeOut());
	}
	
	@Override
	public String executeShellCommand(String command, int maxTimeToOutputResponse) throws TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
		CommandOutputCapture capture = new CommandOutputCapture();
		_device.executeShellCommand(command, capture, maxTimeToOutputResponse);
		return capture.toString();
	}
	
	@Override
	public void executeShellCommand(String command,
			IShellOutputReceiver receiver) throws TimeoutException,
			AdbCommandRejectedException, ShellCommandUnresponsiveException,
			IOException {
		_device.executeShellCommand(command, receiver);
		
	}

	@Override
	public void executeShellCommand(String command,
			IShellOutputReceiver receiver, int maxTimeToOutputResponse)
			throws TimeoutException, AdbCommandRejectedException,
			ShellCommandUnresponsiveException, IOException {
		_device.executeShellCommand(command, receiver, maxTimeToOutputResponse);
	}

	@Override
	public void runEventLogService(LogReceiver receiver)
			throws TimeoutException, AdbCommandRejectedException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runLogService(String logname, LogReceiver receiver)
			throws TimeoutException, AdbCommandRejectedException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createForward(int localPort, int remotePort)
			throws TimeoutException, AdbCommandRejectedException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeForward(int localPort, int remotePort)
			throws TimeoutException, AdbCommandRejectedException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getClientName(int pid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String installPackage(String packageFilePath, boolean reinstall)
			throws InstallException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String syncPackageToDevice(String localFilePath)
			throws TimeoutException, AdbCommandRejectedException, IOException,
			SyncException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String installRemotePackage(String remoteFilePath, boolean reinstall)
			throws InstallException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeRemotePackage(String remoteFilePath)
			throws InstallException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String uninstallPackage(String packageName) throws InstallException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reboot(String into) {
		
	}

	@Override
	public void broadcastIntent(@Nullable String arg0, @Nullable String arg1,
			@Nullable String arg2, @Nullable String arg3,
			Collection<String> arg4, Map<String, Object> arg5,
			@Nullable String arg6, int arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drag(int arg0, int arg1, int arg2, int arg3, int arg4, long arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HierarchyViewer getHierarchyViewer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChimpManager getManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getPropertyList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IChimpView getRootView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSystemProperty(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IChimpView getView(ISelector arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getViewIdList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IChimpView> getViews(IMultiSelector arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean installPackage(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, Object> instrument(String arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void press(String arg0, TouchPressType arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void press(PhysicalButton arg0, TouchPressType arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean removePackage(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String shell(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startActivity(@Nullable String arg0, @Nullable String arg1,
			@Nullable String arg2, @Nullable String arg3,
			Collection<String> arg4, Map<String, Object> arg5,
			@Nullable String arg6, int arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IChimpImage takeSnapshot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void touch(int arg0, int arg1, TouchPressType arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void type(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wake() {
		// TODO Auto-generated method stub
		
	}

}
