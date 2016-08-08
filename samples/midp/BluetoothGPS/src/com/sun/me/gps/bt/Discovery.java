/*
Copyright (c) 2007, Sun Microsystems, Inc.
 
All rights reserved.
 
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
 
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in
      the documentation and/or other materials provided with the
      distribution.
 * Neither the name of Sun Microsystems, Inc. nor the names of its
      contributors may be used to endorse or promote products derived
      from this software without specific prior written permission.
 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.sun.me.gps.bt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.AlertType;

/**
 *
 */
public class Discovery implements DiscoveryListener {
    
    private static final UUID[] SPP_SET = { new UUID(0x1101) };
    
    private LocalDevice localDevice = null;
    private DiscoveryAgent discoveryAgent = null;
    private Vector devices = new Vector();
    private Vector services = new Vector();
    private boolean deviceSearchInProgress = false;
    private boolean serviceSearchInProgress = false;
    private int serviceSearchId = 0;
    
    private MainMidlet midlet = null;
    private GPSInfo.Listener listener = null;
    
    public Discovery(final MainMidlet midlet) {
        this.midlet = midlet;
    }
        
    public void performDiscovery() throws BluetoothStateException {
        localDevice = LocalDevice.getLocalDevice();
        
        discoveryAgent = localDevice.getDiscoveryAgent();
        
        retrieveDevices(DiscoveryAgent.CACHED);
        retrieveDevices(DiscoveryAgent.PREKNOWN);
        
        deviceSearchInProgress = discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this);
        midlet.status(deviceSearchInProgress ? "searching" : "search failed");
    }
    
    public void searchServices(final GPSInfo.Listener listener) throws BluetoothStateException, IOException {
        this.listener = listener;
        final RemoteDevice selectedDevice = findSelectedDevice();
        if (selectedDevice == null) {
            midlet.status("no device selected");
            return;
        }
        
        for (int i=0; i < devices.size(); i++) {
            final RemoteDevice device = (RemoteDevice) devices.elementAt(i);
            final String friendlyName = device.getFriendlyName(false);
            if (friendlyName.equals(selectedDevice.getFriendlyName(false))) {
                serviceSearchId = discoveryAgent.searchServices(null, SPP_SET, device, this);
                serviceSearchInProgress = true;
                midlet.status("connecting to " + friendlyName + " ...");
            }
        }
    }
    
    private void connect() throws BluetoothStateException, IOException {
        final RemoteDevice selectedDevice = findSelectedDevice();
        if (selectedDevice == null) {
            midlet.status("no device selected");
            return;
        }
        
        if (services.size() == 0) {
            midlet.status("no services found");
            return;
        }
        
        for (int i=0; i < services.size(); i++) {
            final ServiceRecord sr = (ServiceRecord) services.elementAt(i);
            if (sr.getHostDevice() == selectedDevice) {
                final String url = sr.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                if (url != null) {
                    midlet.status("connecting to " + selectedDevice.getFriendlyName(false));
                    final StreamConnection sconn = (StreamConnection) Connector.open(url);
                    midlet.status("connected");
                    
                    final InputStream is = sconn.openInputStream();
                    final GPSInfo gps = new GPSInfo(listener, is);
                    new Thread(gps).start();
                }
            }
        }
    }
    
    private void retrieveDevices(final int option) {
        final RemoteDevice[] remoteDevice = discoveryAgent.retrieveDevices(option);
        if (remoteDevice == null) {
            return;
        }
        for (int i=0; i < remoteDevice.length; i++) {
            devices.addElement(remoteDevice[i]);
        }
    }
    
    private static String getFriendlyName(final RemoteDevice remoteDevice) {
        String friendlyName = null;
        try {
            friendlyName = remoteDevice.getFriendlyName(false);
        } catch (IOException iex) {
            // fall-back to the address
            friendlyName = remoteDevice.getBluetoothAddress();
        }
        return friendlyName;
    }
    
    public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
        final String friendlyName = getFriendlyName(remoteDevice);
        midlet.status("found " + friendlyName);
        devices.addElement(remoteDevice);
        midlet.get_deviceGroup().append(friendlyName, null);
    }
    
    public void inquiryCompleted(int status) {
        deviceSearchInProgress = false;
        midlet.status("done searching");
        if (devices.size() > 0) {
            midlet.get_mainForm().addCommand(midlet.get_connectCommand());
        }
    }
    
    public void serviceSearchCompleted(int i, int i0) {
        serviceSearchInProgress = false;
        midlet.status("done searching");
        try {
            connect();
        } catch (Exception ex) {
            midlet.alert(AlertType.ERROR, "connect", ex);
        }
    }
    
    public void servicesDiscovered(int i, ServiceRecord[] serviceRecord) {
        midlet.status("found service");
        for (int j=0; j < serviceRecord.length; j++) {
            services.addElement(serviceRecord[j]);
        }
    }
    
    private RemoteDevice findSelectedDevice() throws IOException {
        final int selectedIndex = midlet.get_deviceGroup().getSelectedIndex();
        if (selectedIndex < 0) {
            midlet.status("no device selected");
            return null;
        }
        final String selectedDeviceName = midlet.get_deviceGroup().getString(selectedIndex);
        
        for (int i=0; i < devices.size(); i++) {
            final RemoteDevice device = (RemoteDevice) devices.elementAt(i);
            final String friendlyName = device.getFriendlyName(false);
            if (selectedDeviceName.equals(friendlyName)) {
                return device;
            }
        }
        return null;
    }
}
