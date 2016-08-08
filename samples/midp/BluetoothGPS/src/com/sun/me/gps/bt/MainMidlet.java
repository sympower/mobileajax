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

import com.sun.me.gps.bt.MapCanvas;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;

public class MainMidlet extends MIDlet implements CommandListener {
    
    private static final boolean DEBUG = true;
    
    private Alert alert = new Alert(null);
    private Discovery discovery = null;
    private MapCanvas map = null;
    
    public MainMidlet() {
        initialize();
    }
    
    private Form mainForm;//GEN-BEGIN:MVDFields
    private Command exitCommand;
    private Command connectCommand;
    private ChoiceGroup deviceGroup;
    private StringItem status;//GEN-END:MVDFields
    
//GEN-LINE:MVDMethods
    
    /** Called by the system to indicate that a command has been invoked on a particular displayable.//GEN-BEGIN:MVDCABegin
     * @param command the Command that ws invoked
     * @param displayable the Displayable on which the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:MVDCABegin
        // Insert global pre-action code here
        if (displayable == mainForm) {//GEN-BEGIN:MVDCABody
            if (command == exitCommand) {//GEN-END:MVDCABody
                // Insert pre-action code here
                exitMIDlet();//GEN-LINE:MVDCAAction4
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase4
        }//GEN-END:MVDCACase4
        // Insert global post-action code here
        if (command == get_connectCommand()) {
            doConnect();
        }
}//GEN-LINE:MVDCAEnd
    
    /** This method initializes UI of the application.//GEN-BEGIN:MVDInitBegin
     */
    private void initialize() {//GEN-END:MVDInitBegin
        // Insert pre-init code here
        getDisplay().setCurrent(get_mainForm());//GEN-LINE:MVDInitInit
        // Insert post-init code here
        doDiscovery();
    }//GEN-LINE:MVDInitEnd
    
    /**
     * This method should return an instance of the display.
     */
    public Display getDisplay() {//GEN-FIRST:MVDGetDisplay
        return Display.getDisplay(this);
    }//GEN-LAST:MVDGetDisplay
    
    /**
     * This method should exit the midlet.
     */
    public void exitMIDlet() {//GEN-FIRST:MVDExitMidlet
        getDisplay().setCurrent(null);
        destroyApp(true);
        notifyDestroyed();
    }//GEN-LAST:MVDExitMidlet
    
    /** This method returns instance for mainForm component and should be called instead of accessing mainForm field directly.//GEN-BEGIN:MVDGetBegin2
     * @return Instance for mainForm component
     */
    public Form get_mainForm() {
        if (mainForm == null) {//GEN-END:MVDGetBegin2
            // Insert pre-init code here
            mainForm = new Form("GPS client", new Item[] {//GEN-BEGIN:MVDGetInit2
                get_deviceGroup(),
                get_status()
            });
            mainForm.addCommand(get_exitCommand());
            mainForm.setCommandListener(this);//GEN-END:MVDGetInit2
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd2
        return mainForm;
    }//GEN-END:MVDGetEnd2
    
    /** This method returns instance for exitCommand component and should be called instead of accessing exitCommand field directly.//GEN-BEGIN:MVDGetBegin3
     * @return Instance for exitCommand component
     */
    public Command get_exitCommand() {
        if (exitCommand == null) {//GEN-END:MVDGetBegin3
            // Insert pre-init code here
            exitCommand = new Command("Exit", Command.EXIT, 1);//GEN-LINE:MVDGetInit3
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd3
        return exitCommand;
    }//GEN-END:MVDGetEnd3
    
    
    
    /** This method returns instance for connectCommand component and should be called instead of accessing connectCommand field directly.//GEN-BEGIN:MVDGetBegin9
     * @return Instance for connectCommand component
     */
    public Command get_connectCommand() {
        if (connectCommand == null) {//GEN-END:MVDGetBegin9
            // Insert pre-init code here
            connectCommand = new Command("Connect", Command.ITEM, 1);//GEN-LINE:MVDGetInit9
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd9
        return connectCommand;
    }//GEN-END:MVDGetEnd9
    
    /** This method returns instance for deviceGroup component and should be called instead of accessing deviceGroup field directly.//GEN-BEGIN:MVDGetBegin11
     * @return Instance for deviceGroup component
     */
    public ChoiceGroup get_deviceGroup() {
        if (deviceGroup == null) {//GEN-END:MVDGetBegin11
            // Insert pre-init code here
            deviceGroup = new ChoiceGroup("Devices:", Choice.POPUP, new String[0], new Image[0]);//GEN-BEGIN:MVDGetInit11
            deviceGroup.setSelectedFlags(new boolean[0]);
            deviceGroup.setFitPolicy(Choice.TEXT_WRAP_OFF);//GEN-END:MVDGetInit11
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd11
        return deviceGroup;
    }//GEN-END:MVDGetEnd11
    
    
    
    
    /** This method returns instance for status component and should be called instead of accessing status field directly.//GEN-BEGIN:MVDGetBegin17
     * @return Instance for status component
     */
    public StringItem get_status() {
        if (status == null) {//GEN-END:MVDGetBegin17
            // Insert pre-init code here
            status = new StringItem("Status:", "");//GEN-LINE:MVDGetInit17
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd17
        return status;
    }//GEN-END:MVDGetEnd17
    
    public void startApp() {
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional) {
    }
    
    public void alert(final AlertType type, final String message, final Throwable th) {
        alert.setType(type);
        alert.setTimeout(Alert.FOREVER);
        final String exMsg = th == null ? null : th.getMessage();
        final String text = exMsg == null ? message : message + ": " + exMsg;
        alert.setString(text);
        if (DEBUG) {
            System.err.println(text);
            status.setText(text);
            if (th != null) {
                th.printStackTrace();
            }
        }
        getDisplay().setCurrent(alert, mainForm);
    }
    
    public void status(final String message) {
        alert.setString(message);
        if (DEBUG) {
            System.out.println(message);
            status.setText(message);
        }
    }
    
    private void doDiscovery() {
        discovery = new Discovery(this);
        new Thread(new Runnable() {
            public void run() {
                try {
                    discovery.performDiscovery();
                } catch (Exception ex) {
                    alert(AlertType.ERROR, "discovery", ex);
                }
            }
        }).start();
    }
    
    private void doConnect() {
        if (map == null) {
            map = new MapCanvas(this);
        }
        new Thread(new Runnable() {
            public void run() {
                try {
                    discovery.searchServices(map);
                } catch (Exception ex) {
                    alert(AlertType.ERROR, "connection", ex);
                }
            }
        }).start();
    }
}
