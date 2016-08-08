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

package com.sun.me.web.sample.syndicator;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.PushRegistry;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
//#if CameraPhone && MMAPI
//# import javax.microedition.media.Manager;
//# import javax.microedition.media.Player;
//# import javax.microedition.media.control.VideoControl;
//#endif
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStoreException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import com.sun.me.web.atom.Feed;
import com.sun.me.web.atom.FeedEntry;
import com.sun.me.web.atom.FeedListener;
import com.sun.me.web.atom.FeedReader;
import com.sun.me.web.atom.Id;
import com.sun.me.web.atom.Link;
import com.sun.me.web.atom.PublishInfo;
import com.sun.me.web.atom.StopParsingException;
import com.sun.me.web.atom.Subscription;
import com.sun.me.web.atom.Text;

/**
 * A sample mobile feed reader and publisher
 */
public class Syndicator extends MIDlet implements CommandListener, FeedListener {
    
    private static final String TODO = "Feature not yet implemented, contributions welcome :-)";
    
    private static final boolean DEBUG = true;
    public static Subscription[] DEBUG_FEEDS;
    
    // TODO: these probably should be app settings/properties
    private static final int MAX_FEED_ENTRIES = 10;
    private static final int MAX_FEED_CHARS = 4096;
    private static final int MAX_FEED_DISPLAY_CHARS = 128;
    private static final long FEED_UPDATE_INTERVAL_MILLIS = 90 * 60 * 1000;
    
    private Item selectedFeed;
    private Item selectedEntry;
    
    private Image feedImage;
    private Timer timer;
    
    private Alert alert;
    private Displayable alertNextDisplayable;
    
    private int feedEntryCount;
    private Subscription[] subscriptions;
    
    private Feed feed;
    private FeedEntry[] entries;
    private Hashtable namespaces = new Hashtable();
    
//#if CameraPhone && MMAPI
//#     private Form captureForm;
//#     private Command photoCommand;
//#     private Player player;
//#     private VideoControl control;
//#     private byte[] photoBits;
//#     private Image photo;
//#endif
    
    public Syndicator() {
    }
    
    private Form subscriptionsForm;//GEN-BEGIN:MVDFields
    private Form feedForm;
    private Form entryForm;
    private Form newEntryForm;
    private Command exitCommand;
    private Command readCommand;
    private org.netbeans.microedition.lcdui.WaitScreen readWaitScreen;
    private org.netbeans.microedition.lcdui.WaitScreen updateWaitScreen;
    private Alert errorAlert;
    private Command okCommand;
    private Command gotoCommand;
    private Command selectCommand;
    private TextField titleField;
    private TextField contentField;
    private org.netbeans.microedition.util.SimpleCancellableTask updateCancellableTask;
    private org.netbeans.microedition.util.SimpleCancellableTask readCancellableTask;
    private org.netbeans.microedition.lcdui.WaitScreen goWaitScreen;
    private org.netbeans.microedition.util.SimpleCancellableTask goCancellableTask;
    private Command backCommand;
    private Command newEntryCommand;
    private Command postCommand;
    private org.netbeans.microedition.lcdui.WaitScreen postWaitScreen;
    private org.netbeans.microedition.util.SimpleCancellableTask postCancellableTask;
    private Command updateCommand;//GEN-END:MVDFields
    
//GEN-LINE:MVDMethods
    
    /** Called by the system to indicate that a command has been invoked on a particular displayable.//GEN-BEGIN:MVDCABegin
     * @param command the Command that ws invoked
     * @param displayable the Displayable on which the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:MVDCABegin
        // Insert global pre-action code here
        if (displayable == readWaitScreen) {//GEN-BEGIN:MVDCABody
            if (command == readWaitScreen.SUCCESS_COMMAND) {//GEN-END:MVDCABody
                // Insert pre-action code here
                getDisplay().setCurrent(get_feedForm());//GEN-LINE:MVDCAAction17
                // Insert post-action code here
            } else if (command == readWaitScreen.FAILURE_COMMAND) {//GEN-LINE:MVDCACase17
                // Insert pre-action code here
                getDisplay().setCurrent(get_updateWaitScreen());//GEN-LINE:MVDCAAction18
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase18
        } else if (displayable == subscriptionsForm) {
            if (command == readCommand) {//GEN-END:MVDCACase18
                // Insert pre-action code here
                getDisplay().setCurrent(get_readWaitScreen());//GEN-LINE:MVDCAAction14
                // Insert post-action code here
            } else if (command == exitCommand) {//GEN-LINE:MVDCACase14
                // Insert pre-action code here
                exitMIDlet();//GEN-LINE:MVDCAAction12
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase12
        } else if (displayable == updateWaitScreen) {
            if (command == updateWaitScreen.FAILURE_COMMAND) {//GEN-END:MVDCACase12
                // Insert pre-action code here
                getDisplay().setCurrent(get_subscriptionsForm());//GEN-LINE:MVDCAAction21
                // Insert post-action code here
            } else if (command == updateWaitScreen.SUCCESS_COMMAND) {//GEN-LINE:MVDCACase21
                // Insert pre-action code here
                getDisplay().setCurrent(get_feedForm());//GEN-LINE:MVDCAAction20
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase20
        } else if (displayable == errorAlert) {
            if (command == okCommand) {//GEN-END:MVDCACase20
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction25
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase25
        } else if (displayable == goWaitScreen) {
            if (command == goWaitScreen.SUCCESS_COMMAND) {//GEN-END:MVDCACase25
                // Insert pre-action code here
                getDisplay().setCurrent(get_entryForm());//GEN-LINE:MVDCAAction36
                // Insert post-action code here
            } else if (command == goWaitScreen.FAILURE_COMMAND) {//GEN-LINE:MVDCACase36
                // Insert pre-action code here
                getDisplay().setCurrent(get_entryForm());//GEN-LINE:MVDCAAction37
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase37
        } else if (displayable == entryForm) {
            if (command == backCommand) {//GEN-END:MVDCACase37
                // Insert pre-action code here
                getDisplay().setCurrent(get_feedForm());//GEN-LINE:MVDCAAction41
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase41
        } else if (displayable == feedForm) {
            if (command == backCommand) {//GEN-END:MVDCACase41
                // Insert pre-action code here
                getDisplay().setCurrent(get_subscriptionsForm());//GEN-LINE:MVDCAAction40
                // Insert post-action code here
            } else if (command == updateCommand) {//GEN-LINE:MVDCACase40
                // Insert pre-action code here
                getDisplay().setCurrent(get_updateWaitScreen());//GEN-LINE:MVDCAAction52
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase52
        } else if (displayable == newEntryForm) {
            if (command == backCommand) {//GEN-END:MVDCACase52
                // Insert pre-action code here
//#if CameraPhone && MMAPI
//#                 photo = null;
//#                 photoBits = null;
//#endif
                getDisplay().setCurrent(get_feedForm());//GEN-LINE:MVDCAAction44
                // Insert post-action code here
            } else if (command == postCommand) {//GEN-LINE:MVDCACase44
                // Insert pre-action code here
                getDisplay().setCurrent(get_postWaitScreen());//GEN-LINE:MVDCAAction46
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase46
        } else if (displayable == postWaitScreen) {
            if (command == postWaitScreen.FAILURE_COMMAND) {//GEN-END:MVDCACase46
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction49
                // Insert post-action code here
            } else if (command == postWaitScreen.SUCCESS_COMMAND) {//GEN-LINE:MVDCACase49
                // Insert pre-action code here
                getDisplay().setCurrent(get_feedForm());//GEN-LINE:MVDCAAction48
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase48
        }//GEN-END:MVDCACase48
        // Insert global post-action code here
        if (displayable == alert) {
            if (command == okCommand && alertNextDisplayable != null) {
                getDisplay().setCurrent(alertNextDisplayable);
            }
        }
//#if CameraPhone && MMAPI
//#         if (displayable == newEntryForm) {
//#             if (command == photoCommand) {
//#                 try {
//#                     initCamera();
//#                 } catch (Exception ex) {
//#                     errorAlert("Error accessing camera", ex, newEntryForm);
//#                 }
//#             }
//#         } else if (displayable == captureForm) {
//#             if (command == okCommand) {
//#                 new Thread(new Runnable() {
//#                     public void run() {
//#                         takePhoto();
//#                     }
//#                 }).start();
//#             }
//#         }
//#endif
}//GEN-LINE:MVDCAEnd
    
    /** This method initializes UI of the application.//GEN-BEGIN:MVDInitBegin
     */
    private void initialize() {//GEN-END:MVDInitBegin
        // Insert pre-init code here
        loadSubscriptions();
        
        // schedule feed updates at the specified interval
        timer = new Timer();
        timer.schedule(new TimerTask() {public void run() {doUpdateAll();}},
            FEED_UPDATE_INTERVAL_MILLIS, FEED_UPDATE_INTERVAL_MILLIS);
        getDisplay().setCurrent(get_subscriptionsForm());//GEN-LINE:MVDInitInit
        // Insert post-init code here
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
    
    
    
    
    /** This method returns instance for subscriptionsForm component and should be called instead of accessing subscriptionsForm field directly.//GEN-BEGIN:MVDGetBegin6
     * @return Instance for subscriptionsForm component
     */
    public Form get_subscriptionsForm() {
        if (subscriptionsForm == null) {//GEN-END:MVDGetBegin6
            // Insert pre-init code here
            subscriptionsForm = new Form(null, new Item[0]);//GEN-BEGIN:MVDGetInit6
            subscriptionsForm.addCommand(get_exitCommand());
            subscriptionsForm.addCommand(get_readCommand());
            subscriptionsForm.setCommandListener(this);//GEN-END:MVDGetInit6
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd6
        return subscriptionsForm;
    }//GEN-END:MVDGetEnd6
    
    /** This method returns instance for feedForm component and should be called instead of accessing feedForm field directly.//GEN-BEGIN:MVDGetBegin7
     * @return Instance for feedForm component
     */
    public Form get_feedForm() {
        if (feedForm == null) {//GEN-END:MVDGetBegin7
            // Insert pre-init code here
            feedForm = new Form(null, new Item[0]);//GEN-BEGIN:MVDGetInit7
            feedForm.addCommand(get_backCommand());
            feedForm.addCommand(get_updateCommand());
            feedForm.setCommandListener(this);//GEN-END:MVDGetInit7
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd7
        return feedForm;
    }//GEN-END:MVDGetEnd7
    
    /** This method returns instance for entryForm component and should be called instead of accessing entryForm field directly.//GEN-BEGIN:MVDGetBegin8
     * @return Instance for entryForm component
     */
    public Form get_entryForm() {
        if (entryForm == null) {//GEN-END:MVDGetBegin8
            // Insert pre-init code here
            entryForm = new Form(null, new Item[0]);//GEN-BEGIN:MVDGetInit8
            entryForm.addCommand(get_backCommand());
            entryForm.setCommandListener(this);//GEN-END:MVDGetInit8
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd8
        return entryForm;
    }//GEN-END:MVDGetEnd8
    
    /** This method returns instance for newEntryForm component and should be called instead of accessing newEntryForm field directly.//GEN-BEGIN:MVDGetBegin9
     * @return Instance for newEntryForm component
     */
    public Form get_newEntryForm() {
        if (newEntryForm == null) {//GEN-END:MVDGetBegin9
            // Insert pre-init code here
            newEntryForm = new Form(null, new Item[] {//GEN-BEGIN:MVDGetInit9
                get_titleField(),
                get_contentField()
            });
            newEntryForm.addCommand(get_backCommand());
            newEntryForm.addCommand(get_postCommand());
            newEntryForm.setCommandListener(this);//GEN-END:MVDGetInit9
            // Insert post-init code here
//#if CameraPhone && MMAPI
//#             newEntryForm.addCommand(get_photoCommand());
//#endif
        }//GEN-BEGIN:MVDGetEnd9
        return newEntryForm;
    }//GEN-END:MVDGetEnd9
    
    /** This method returns instance for exitCommand component and should be called instead of accessing exitCommand field directly.//GEN-BEGIN:MVDGetBegin11
     * @return Instance for exitCommand component
     */
    public Command get_exitCommand() {
        if (exitCommand == null) {//GEN-END:MVDGetBegin11
            // Insert pre-init code here
            exitCommand = new Command("Exit", Command.EXIT, 1);//GEN-LINE:MVDGetInit11
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd11
        return exitCommand;
    }//GEN-END:MVDGetEnd11
    
    /** This method returns instance for readCommand component and should be called instead of accessing readCommand field directly.//GEN-BEGIN:MVDGetBegin13
     * @return Instance for readCommand component
     */
    public Command get_readCommand() {
        if (readCommand == null) {//GEN-END:MVDGetBegin13
            // Insert pre-init code here
            readCommand = new Command("Read", Command.OK, 1);//GEN-LINE:MVDGetInit13
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd13
        return readCommand;
    }//GEN-END:MVDGetEnd13
    
    /** This method returns instance for readWaitScreen component and should be called instead of accessing readWaitScreen field directly.//GEN-BEGIN:MVDGetBegin16
     * @return Instance for readWaitScreen component
     */
    public org.netbeans.microedition.lcdui.WaitScreen get_readWaitScreen() {
        if (readWaitScreen == null) {//GEN-END:MVDGetBegin16
            // Insert pre-init code here
            readWaitScreen = new org.netbeans.microedition.lcdui.WaitScreen(getDisplay());//GEN-BEGIN:MVDGetInit16
            readWaitScreen.setCommandListener(this);
            readWaitScreen.setTask(get_readCancellableTask());//GEN-END:MVDGetInit16
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd16
        return readWaitScreen;
    }//GEN-END:MVDGetEnd16
    
    /** This method returns instance for updateWaitScreen component and should be called instead of accessing updateWaitScreen field directly.//GEN-BEGIN:MVDGetBegin19
     * @return Instance for updateWaitScreen component
     */
    public org.netbeans.microedition.lcdui.WaitScreen get_updateWaitScreen() {
        if (updateWaitScreen == null) {//GEN-END:MVDGetBegin19
            // Insert pre-init code here
            updateWaitScreen = new org.netbeans.microedition.lcdui.WaitScreen(getDisplay());//GEN-BEGIN:MVDGetInit19
            updateWaitScreen.setCommandListener(this);
            updateWaitScreen.setTask(get_updateCancellableTask());//GEN-END:MVDGetInit19
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd19
        return updateWaitScreen;
    }//GEN-END:MVDGetEnd19
    
    /** This method returns instance for errorAlert component and should be called instead of accessing errorAlert field directly.//GEN-BEGIN:MVDGetBegin22
     * @return Instance for errorAlert component
     */
    public Alert get_errorAlert() {
        if (errorAlert == null) {//GEN-END:MVDGetBegin22
            // Insert pre-init code here
            errorAlert = new Alert(null, "", null, null);//GEN-BEGIN:MVDGetInit22
            errorAlert.addCommand(get_okCommand());
            errorAlert.setCommandListener(this);
            errorAlert.setTimeout(2);//GEN-END:MVDGetInit22
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd22
        return errorAlert;
    }//GEN-END:MVDGetEnd22
    
    /** This method returns instance for okCommand component and should be called instead of accessing okCommand field directly.//GEN-BEGIN:MVDGetBegin24
     * @return Instance for okCommand component
     */
    public Command get_okCommand() {
        if (okCommand == null) {//GEN-END:MVDGetBegin24
            // Insert pre-init code here
            okCommand = new Command("Ok", Command.OK, 1);//GEN-LINE:MVDGetInit24
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd24
        return okCommand;
    }//GEN-END:MVDGetEnd24
    
    /** This method returns instance for gotoCommand component and should be called instead of accessing gotoCommand field directly.//GEN-BEGIN:MVDGetBegin26
     * @return Instance for gotoCommand component
     */
    public Command get_gotoCommand() {
        if (gotoCommand == null) {//GEN-END:MVDGetBegin26
            // Insert pre-init code here
            gotoCommand = new Command("Go", Command.ITEM, 1);//GEN-LINE:MVDGetInit26
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd26
        return gotoCommand;
    }//GEN-END:MVDGetEnd26
    
    /** This method returns instance for selectCommand component and should be called instead of accessing selectCommand field directly.//GEN-BEGIN:MVDGetBegin28
     * @return Instance for selectCommand component
     */
    public Command get_selectCommand() {
        if (selectCommand == null) {//GEN-END:MVDGetBegin28
            // Insert pre-init code here
            selectCommand = new Command("Select", Command.OK, 1);//GEN-LINE:MVDGetInit28
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd28
        return selectCommand;
    }//GEN-END:MVDGetEnd28
    
    /** This method returns instance for titleField component and should be called instead of accessing titleField field directly.//GEN-BEGIN:MVDGetBegin30
     * @return Instance for titleField component
     */
    public TextField get_titleField() {
        if (titleField == null) {//GEN-END:MVDGetBegin30
            // Insert pre-init code here
            titleField = new TextField("Title", null, 120, TextField.ANY);//GEN-LINE:MVDGetInit30
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd30
        return titleField;
    }//GEN-END:MVDGetEnd30
    
    
    /** This method returns instance for contentField component and should be called instead of accessing contentField field directly.//GEN-BEGIN:MVDGetBegin32
     * @return Instance for contentField component
     */
    public TextField get_contentField() {
        if (contentField == null) {//GEN-END:MVDGetBegin32
            // Insert pre-init code here
            contentField = new TextField("Content", null, 4096, TextField.ANY);//GEN-LINE:MVDGetInit32
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd32
        return contentField;
    }//GEN-END:MVDGetEnd32
    
    /** This method returns instance for updateCancellableTask component and should be called instead of accessing updateCancellableTask field directly.//GEN-BEGIN:MVDGetBegin33
     * @return Instance for updateCancellableTask component
     */
    public org.netbeans.microedition.util.SimpleCancellableTask get_updateCancellableTask() {
        if (updateCancellableTask == null) {//GEN-END:MVDGetBegin33
            // Insert pre-init code here
            updateCancellableTask = new org.netbeans.microedition.util.SimpleCancellableTask();//GEN-BEGIN:MVDGetInit33
            updateCancellableTask.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {
                    updateTask();
                }
            });//GEN-END:MVDGetInit33
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd33
        return updateCancellableTask;
    }//GEN-END:MVDGetEnd33
    
    /** This method returns instance for readCancellableTask component and should be called instead of accessing readCancellableTask field directly.//GEN-BEGIN:MVDGetBegin34
     * @return Instance for readCancellableTask component
     */
    public org.netbeans.microedition.util.SimpleCancellableTask get_readCancellableTask() {
        if (readCancellableTask == null) {//GEN-END:MVDGetBegin34
            // Insert pre-init code here
            readCancellableTask = new org.netbeans.microedition.util.SimpleCancellableTask();//GEN-BEGIN:MVDGetInit34
            readCancellableTask.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {
                    readTask();
                }
            });//GEN-END:MVDGetInit34
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd34
        return readCancellableTask;
    }//GEN-END:MVDGetEnd34
    
    /** This method returns instance for goWaitScreen component and should be called instead of accessing goWaitScreen field directly.//GEN-BEGIN:MVDGetBegin35
     * @return Instance for goWaitScreen component
     */
    public org.netbeans.microedition.lcdui.WaitScreen get_goWaitScreen() {
        if (goWaitScreen == null) {//GEN-END:MVDGetBegin35
            // Insert pre-init code here
            goWaitScreen = new org.netbeans.microedition.lcdui.WaitScreen(getDisplay());//GEN-BEGIN:MVDGetInit35
            goWaitScreen.setCommandListener(this);
            goWaitScreen.setTask(get_goCancellableTask());//GEN-END:MVDGetInit35
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd35
        return goWaitScreen;
    }//GEN-END:MVDGetEnd35
    
    /** This method returns instance for goCancellableTask component and should be called instead of accessing goCancellableTask field directly.//GEN-BEGIN:MVDGetBegin38
     * @return Instance for goCancellableTask component
     */
    public org.netbeans.microedition.util.SimpleCancellableTask get_goCancellableTask() {
        if (goCancellableTask == null) {//GEN-END:MVDGetBegin38
            // Insert pre-init code here
            goCancellableTask = new org.netbeans.microedition.util.SimpleCancellableTask();//GEN-BEGIN:MVDGetInit38
            goCancellableTask.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {
                    goTask();
                }
            });//GEN-END:MVDGetInit38
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd38
        return goCancellableTask;
    }//GEN-END:MVDGetEnd38
    
    /** This method returns instance for backCommand component and should be called instead of accessing backCommand field directly.//GEN-BEGIN:MVDGetBegin39
     * @return Instance for backCommand component
     */
    public Command get_backCommand() {
        if (backCommand == null) {//GEN-END:MVDGetBegin39
            // Insert pre-init code here
            backCommand = new Command("Back", Command.BACK, 1);//GEN-LINE:MVDGetInit39
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd39
        return backCommand;
    }//GEN-END:MVDGetEnd39
    
    /** This method returns instance for newEntryCommand component and should be called instead of accessing newEntryCommand field directly.//GEN-BEGIN:MVDGetBegin42
     * @return Instance for newEntryCommand component
     */
    public Command get_newEntryCommand() {
        if (newEntryCommand == null) {//GEN-END:MVDGetBegin42
            // Insert pre-init code here
            newEntryCommand = new Command("New", Command.ITEM, 1);//GEN-LINE:MVDGetInit42
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd42
        return newEntryCommand;
    }//GEN-END:MVDGetEnd42
    
    /** This method returns instance for postCommand component and should be called instead of accessing postCommand field directly.//GEN-BEGIN:MVDGetBegin45
     * @return Instance for postCommand component
     */
    public Command get_postCommand() {
        if (postCommand == null) {//GEN-END:MVDGetBegin45
            // Insert pre-init code here
            postCommand = new Command("Post", Command.ITEM, 1);//GEN-LINE:MVDGetInit45
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd45
        return postCommand;
    }//GEN-END:MVDGetEnd45
    
    /** This method returns instance for postWaitScreen component and should be called instead of accessing postWaitScreen field directly.//GEN-BEGIN:MVDGetBegin47
     * @return Instance for postWaitScreen component
     */
    public org.netbeans.microedition.lcdui.WaitScreen get_postWaitScreen() {
        if (postWaitScreen == null) {//GEN-END:MVDGetBegin47
            // Insert pre-init code here
            postWaitScreen = new org.netbeans.microedition.lcdui.WaitScreen(getDisplay());//GEN-BEGIN:MVDGetInit47
            postWaitScreen.setCommandListener(this);
            postWaitScreen.setTask(get_postCancellableTask());//GEN-END:MVDGetInit47
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd47
        return postWaitScreen;
    }//GEN-END:MVDGetEnd47
    
    /** This method returns instance for postCancellableTask component and should be called instead of accessing postCancellableTask field directly.//GEN-BEGIN:MVDGetBegin50
     * @return Instance for postCancellableTask component
     */
    public org.netbeans.microedition.util.SimpleCancellableTask get_postCancellableTask() {
        if (postCancellableTask == null) {//GEN-END:MVDGetBegin50
            // Insert pre-init code here
            postCancellableTask = new org.netbeans.microedition.util.SimpleCancellableTask();//GEN-BEGIN:MVDGetInit50
            postCancellableTask.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {
                    postTask();
                }
            });//GEN-END:MVDGetInit50
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd50
        return postCancellableTask;
    }//GEN-END:MVDGetEnd50
    
    /** This method returns instance for updateCommand component and should be called instead of accessing updateCommand field directly.//GEN-BEGIN:MVDGetBegin51
     * @return Instance for updateCommand component
     */
    public Command get_updateCommand() {
        if (updateCommand == null) {//GEN-END:MVDGetBegin51
            // Insert pre-init code here
            updateCommand = new Command("Update", Command.ITEM, 1);//GEN-LINE:MVDGetInit51
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd51
        return updateCommand;
    }//GEN-END:MVDGetEnd51
    
//#if CameraPhone && MMAPI
//#     private Command get_photoCommand() {
//#         if (photoCommand == null) {
//#             photoCommand = new Command("Photo", Command.ITEM, 1);
//#         }
//#         return photoCommand;
//#     }
//#endif
    
//#if CameraPhone && MMAPI
//#     private void initCamera() throws Exception {
//#         player = Manager.createPlayer("capture://video");
//#         player.realize();
//#         control = (VideoControl) player.getControl("VideoControl");
//#         if (control != null) {
//#             captureForm = new Form("Take Photo");
//#             captureForm.append((Item) control.initDisplayMode(control.USE_GUI_PRIMITIVE, null));
//#             captureForm.addCommand(get_okCommand());
//#             captureForm.setCommandListener(this);
//#             getDisplay().setCurrent(captureForm);
//#         }
//#         player.start();
//#     }
//#endif
    
//#if CameraPhone && MMAPI
//#     private void takePhoto() {
//#         if (player.getState() == Player.STARTED) {
//#             try {
//#                 photoBits = control.getSnapshot(null);
//#                 player.stop();
//#                 photo = Image.createImage(photoBits, 0, photoBits.length);
//#                 newEntryForm.append(photo);
//#                 getDisplay().setCurrent(newEntryForm);
//#             } catch (Exception ex) {
//#                 errorAlert("Error taking photo", ex, newEntryForm);
//#             } finally {
//#                 player.close();
//#             }
//#         }
//#     }
//#endif
    
    private Image get_feedImage() {
        if (feedImage == null) {
            try {
                feedImage = Image.createImage("/feed.png");
            } catch (java.io.IOException exception) {
            }
        }
        return feedImage;
    }
    
    public void startApp() {
        initialize();
    }
    
    public void pauseApp() {
//#if CameraPhone && MMAPI
//#         if (control != null) {
//#             control.setVisible(false);
//#         }
//#         if (player != null) {
//#             try { player.stop(); } catch (Exception ignore) {}
//#         }
//#         photo = null;
//#         photoBits = null;
//#endif
    }
    
    public void destroyApp(boolean unconditional) {
    }
    
    private void errorAlert(final String message) {
        errorAlert(message, null, getDisplay().getCurrent());
    }
    
    private void errorAlert(final String message, final Exception ex, final Displayable next) {
        alert = get_errorAlert();
        final String exMsg = ex == null ? null : ex.getClass().getName() + ":" + ex.getMessage();
        final String text = exMsg == null ? message : message + ": " + exMsg;
        alert.setString(text);
        alert.setTimeout(Math.min(2000, alert.getDefaultTimeout()));
        alert.addCommand(get_okCommand());
        
//#mdebug warn
        System.err.println("ERROR: " + message);
        if (ex != null) {
            ex.printStackTrace();
        }
//#enddebug
        alertNextDisplayable = next;
        getDisplay().setCurrent(alert, next);
    }
    
    private Subscription getSelectedSubscription() {
        final String message = "Please select a subscription";
        if (selectedFeed == null) {
            errorAlert(message);
            return null;
        }
        
        final String label = selectedFeed.getLabel();
        if (label == null) {
            errorAlert(message);
            return null;
        }
        
        for (int i=0; i < subscriptions.length; i++) {
            if (label.equals(subscriptions[i].getTitle())) {
                return subscriptions[i];
            }
        }
        
        errorAlert(message);
        return null;
    }
    
    private FeedEntry getSelectedEntry() {
        final String message = "Please select an entry";
        if (selectedEntry == null) {
            errorAlert(message);
            return null;
        }
        
        final String label = selectedEntry.getLabel();
        if (label == null) {
            errorAlert(message);
            return null;
        }
        
        for (int i=0; i < entries.length; i++) {
            if (label.equals(entries[i].getTitleAsString())) {
                return entries[i];
            }
        }
        
        errorAlert(message);
        return null;
    }
    
    public void startFeed(final Feed feed) {
        this.feed = feed;
        
        // release memory
        entries = new FeedEntry[MAX_FEED_ENTRIES];
        feedForm = get_feedForm();
        feedForm.deleteAll();
        
        feedEntryCount = 0;
        final Subscription sub = getSelectedSubscription();
        final String uri = sub.getURI();
        
        // delete saved feed, if any
        // TODO: optimize, saving only new entries, deleting obsolete ones
        try {
            Store.deleteFeed(uri);
            Store.deleteFeedEntries(uri);
        } catch (RecordStoreException rx) {
            // ignore
        }
        
        final String status = "Start loading...";
        get_updateWaitScreen().setText(status);
    }
    
    public void endFeed(final Feed feed) {
        final Subscription sub = getSelectedSubscription();
        final String status = "Done loading, processing...";
        get_updateWaitScreen().setText(status);
        
        // resize entries to the exact number of entries read
        final FeedEntry[] copy = new FeedEntry[feedEntryCount];
        System.arraycopy(entries, 0, copy, 0, feedEntryCount);
        entries = copy;
        
        saveFeed(sub.getURI(), feed);
        saveSubscriptions();
        
        showFeed();
    }
    
    public void startFeedEntry(final FeedEntry feedEntry) {
        final Subscription sub = getSelectedSubscription();
        final String status = "Loading entry " +
            (feedEntryCount) + "/" + MAX_FEED_ENTRIES + "...";
        get_updateWaitScreen().setText(status);
    }
    
    public void endFeedEntry(final FeedEntry feedEntry) {
        if (feedEntryCount >= entries.length) {
            return;
        }
        final Subscription sub = getSelectedSubscription();
        entries[feedEntryCount++] = feedEntry;
        saveFeedEntry(sub.getURI(), feedEntry);
    }
    
    public void startPrefixMapping(final String prefix, final String uri) {
        namespaces.put(prefix, uri);
//#mdebug info
        if ("".equals(prefix)) {
            System.out.println("xmlns=" + uri);
        } else {
            System.out.println("xmlns " + prefix + "=" + uri);
        }
//#enddebug
    }
    
    public void endPrefixMapping(final String prefix) {
        namespaces.remove(prefix);
    }
    
    private String getPrefix(final String uri) {
        final Enumeration keys = namespaces.keys();
        while (keys.hasMoreElements()) {
            final String key = (String) keys.nextElement();
            final String value = (String) namespaces.get(key);
            if (uri.equals(value)) {
                return "".equals(key) ? "" : key + ":";
            }
        }
        return null;
    }
    
    public void startElement(final String uri, final String localName,
        final String qName, final Attributes attrs) {
//#mdebug info
        final String prefix = getPrefix(uri);
        System.out.print("<" + (prefix == null ? "" : prefix) + localName);
        for (int i = attrs.getLength() - 1; i >= 0; i--) {
            System.out.print(" " + attrs.getLocalName(i) + "=\"" + attrs.getValue(i) + "\"");
        }
        System.out.print(">");
//#enddebug
    }
    
    public void endElement(final String uri, final String localName, final String qName) {
//#mdebug info
        final String prefix = getPrefix(uri);
        System.out.println("</" + (prefix == null ? "" : prefix) + localName + ">");
//#enddebug
    }
    
    public void characters(final char[] ch, final int start, final int length) {
//#mdebug info
        System.out.print(new String(ch, start, length));
//#enddebug
    }
    
    public void errorProcessingFeed(final String message) {
        errorAlert(message, null, feedForm);
    }
    
    private void doReadEntry() {
        get_entryForm().deleteAll();
        entryForm.setTitle(feed.getTitleAsString());
        final FeedEntry entry = getSelectedEntry();
        
        final StringItem stringItem = new StringItem(entry.getTitleAsString(), null, StringItem.PLAIN);
        stringItem.setDefaultCommand(get_gotoCommand());
        stringItem.setItemCommandListener(new ItemCommandListener() {
            public void commandAction(Command command, Item item) {
                getDisplay().setCurrent(get_goWaitScreen());
            }
        });
        entryForm.append(stringItem);
        
        final String summary = Text.extractAsText(entry.getSummary(), true);
        if (summary != null && !"".equals(summary)) {
            entryForm.append(summary);
        } else {
            final String content = Text.extractAsText(entry.getContent(), true);
            if (content != null && !"".equals(content)) {
                entryForm.append(content);
            }
        }
        getDisplay().setCurrent(entryForm);
    }
    
    private void saveFeed(final String feedURL, final Feed feed) {
        try {
            Store.store(feedURL, feed);
        } catch (Exception ex) {
            errorAlert("Cannot save feed", ex, feedForm);
        }
    }
    
    private void saveFeedEntry(final String feedURL, final FeedEntry feedEntry) {
        try {
            Store.store(feedURL, feedEntry);
        } catch (Exception ex) {
            errorAlert("Cannot save feed", ex, feedForm);
        }
    }
    
    private void showFeed() {
        feedForm = get_feedForm();
        feedForm.deleteAll();
        feedForm.setTitle(feed.getTitleAsString());
        if (getSelectedSubscription().getPublishInfo() != null) {
            final ImageItem pubIcon = new ImageItem(null,
                get_feedImage(),
                Item.LAYOUT_NEWLINE_AFTER,
                "New Entry", Item.BUTTON);
            pubIcon.setDefaultCommand(get_newEntryCommand());
            pubIcon.setItemCommandListener(new ItemCommandListener() {
                public void commandAction(Command command, Item item) {
                    getDisplay().setCurrent(get_newEntryForm());
                }
            });
            feedForm.append(pubIcon);
        }
        for (int i=0; entries != null && i < entries.length; i++) {
            final StringItem stringItem = new StringItem(
                entries[i].getTitleAsString(), null, StringItem.PLAIN);
            stringItem.setDefaultCommand(get_selectCommand());
            stringItem.setItemCommandListener(new ItemCommandListener() {
                public void commandAction(Command command, Item item) {
                    selectedEntry = item;
                    doReadEntry();
                }
            });
            feedForm.append(stringItem);
            final String summary = Text.extractAsText(entries[i].getSummary(), true);
            final String content = Text.extractAsText(entries[i].getContent(), true);
            if (summary != null && !"".equals(summary)) {
                feedForm.append(summary.substring(0,
                    Math.min(summary.length(), MAX_FEED_DISPLAY_CHARS)));
            } else if (content != null && !"".equals(content)) {
                feedForm.append(content.substring(0,
                    Math.min(content.length(), MAX_FEED_DISPLAY_CHARS)));
            }
        }
        getDisplay().setCurrent(feedForm);
    }
    
    private void doUpdateAll() {
        for (int i=0; i < subscriptions.length; i++) {
            doUpdate(subscriptions[i]);
        }
    }
    
    private void doUpdate(final Subscription sub) {
        getDisplay().setCurrent(get_updateWaitScreen());
        updateWaitScreen.setText("Connecting...");
    }
    
    private void doRead() {
        final Subscription sub = getSelectedSubscription();
        final String uri = sub.getURI();
        try {
            feed = Store.getFeed(uri);
            entries = Store.getFeedEntries(uri);
            showFeed();
        } catch (Exception ex) {
            // since the saved feed is unreadble, ensure we get the entire feed at the next update
            sub.setLastUpdated(null);
            doUpdate(sub);
        }
    }
    
    private void saveSubscriptions() {
        try {
            Store.store(subscriptions);
        } catch (Exception ex) {
            errorAlert("Cannot save subscriptions", ex, feedForm);
        }
    }
    
    private void loadSubscriptions() {
        try {
            subscriptions = Store.getSubscriptions();
        } catch (Exception ex) {
            // start up with an empty list
            subscriptions = new Subscription[0];
        }
        
        // pre-populate with some feeds for debugging
        if (DEBUG) {
            if (subscriptions.length == 0) {
                subscriptions = new Subscription[4];
                subscriptions[0] = new Subscription("webservices.xml.com", "http://www.oreillynet.com/pub/feed/21", null, null, null);
                subscriptions[1] = new Subscription("ongoing", "http://www.tbray.org/ongoing/ongoing.atom", null, null, null);
                subscriptions[2] = new Subscription("Planet Intertwingly", "http://planet.intertwingly.net/atom.xml", null, null, null);
                subscriptions[3] = new Subscription("Jonathan's Weblog", "http://blogs.sun.com/jonathan/feed/entries/atom", null, null, null);
            }
        }
        
        for (int i=0; i < subscriptions.length; i++) {
            final StringItem stringItem =
                new StringItem(subscriptions[i].getTitle(), null, StringItem.PLAIN);
            stringItem.setDefaultCommand(get_selectCommand());
            stringItem.setItemCommandListener(new ItemCommandListener() {
                public void commandAction(Command command, Item item) {
                    selectedFeed = item;
                    getDisplay().setCurrent(get_readWaitScreen());
                }
            });
            get_subscriptionsForm().append(stringItem);
        }
        get_subscriptionsForm().setTitle("Subscriptions");
    }
    
    private void scheduleWakeup() {
        try {
            PushRegistry.registerAlarm(getClass().getName(),
                System.currentTimeMillis() + FEED_UPDATE_INTERVAL_MILLIS);
        } catch (Exception ex) {
            errorAlert("Unable to schedule wakeup", ex, get_subscriptionsForm());
        }
    }
    
    private void updateTask() throws Exception {
        get_updateWaitScreen().setText(null);
        final Subscription sub = getSelectedSubscription();
        try {
            final FeedReader reader =
                FeedReader.createInstance(sub.getURI(), this, MAX_FEED_ENTRIES, MAX_FEED_CHARS);
            reader.initiate(sub);
            if (!reader.read()) {
                // no update available, load the saved feed if available and show it
                final String uri = sub.getURI();
                if (feed != null) {
                    final Id id = feed.getId();
                    if (id != null && !uri.equals(id.getURI())) {
                        feed = Store.getFeed(uri);
                        entries = Store.getFeedEntries(uri);
                    }
                    showFeed();
                }
            }
        } catch (StopParsingException stop) {
            // not an error -
            // this is thrown to indicate that we've processed the
            // max number of entries specified and to stop the parser
        } catch (IOException ix) {
            errorAlert("Network error while reading feed", ix, subscriptionsForm);
            throw ix;
        } catch (SAXException sx) {
            errorAlert("Error parsing feed", sx, subscriptionsForm);
            throw sx;
        } catch (Exception ex) {
            errorAlert("Exception while reading feed", ex, subscriptionsForm);
            throw ex;
        }
    }
    
    private void readTask() throws Exception {
        get_readWaitScreen().setText(null);
        final Subscription sub = getSelectedSubscription();
        final String uri = sub.getURI();
        feed = Store.getFeed(uri);
        entries = Store.getFeedEntries(uri);
        showFeed();
    }
    
    private void goTask() throws Exception {
        final FeedEntry entry = getSelectedEntry();
        final String url = Link.follow(entry.getLinks());
        if (url != null) {
//#mdebug info
            System.out.println("Launching Platform Request for: " + url);
//#enddebug
            final boolean exitNeeded = platformRequest(url);
            if (exitNeeded) {
                errorAlert("Please exit the MIDlet to access the page or to make the call",
                    null, feedForm);
            }
        }
    }
    
    private void postTask() {
        get_postWaitScreen().setText(null);
        final Subscription sub = getSelectedSubscription();
        final PublishInfo pub = sub.getPublishInfo();
        String photoURL = null;
//#if CameraPhone && MMAPI
//#         if (photo != null) {
//#             // TODO: show wait screen
//#             try {
//#                 photoURL = feed.postMedia(photoBits, "image/png", pub);
//#             } catch (IOException ex) {
//#                 // the call to postMedia is overloaded to return the error message if things go wrong
//#                 errorAlert("Cannot post media: error " + photoURL, ex, newEntryForm);
//#                 photoURL = null;
//#             }
//#             photo = null;
//#             photoBits = null;
//#         }
//#endif
        final String title = get_titleField().getString();
        final String text = get_contentField().getString();
        final FeedEntry newEntry = photoURL == null ?
            new FeedEntry(title, text, feed) :
            new FeedEntry(title, text, photoURL, feed);
        int responseCode = 0;
        try {
            responseCode = feed.createEntry(newEntry, pub);
        } catch (IOException ex) {
            errorAlert("Cannot create entry: HTTP " + responseCode, ex, newEntryForm);
            return;
        }
        if (responseCode == HttpConnection.HTTP_CREATED) {
            // TODO: optimize - do not update the entire feed, only insert the new entry
            get_updateWaitScreen().setText(null);
            getDisplay().setCurrent(updateWaitScreen);
        } else {
            errorAlert("Cannot create entry: HTTP " + responseCode, null, newEntryForm);
        }
    }
    
    private void putTask() {
        // get_putWaitScreen().setText(null);
        final Subscription sub = getSelectedSubscription();
        final PublishInfo pub = sub.getPublishInfo();
        final String title = get_titleField().getString();
        final String content = get_contentField().getString();
        int responseCode = 0;
        final FeedEntry entry = getSelectedEntry();
        entry.update(title, content);
        try {
            responseCode = feed.updateEntry(entry, pub);
        } catch (IOException ex) {
            errorAlert("Cannot update entry: HTTP " + responseCode, ex, newEntryForm);
        }
        // TODO: update the feed display with the new entry
        getDisplay().setCurrent(feedForm);
    }
    
    private void deleteTask() {
        // get_deleteWaitScreen().setText(null);
        final Subscription sub = getSelectedSubscription();
        final FeedEntry entry = getSelectedEntry();
        int responseCode = 0;
        try {
            responseCode = feed.deleteEntry(entry, sub.getPublishInfo());
        } catch (IOException ex) {
            errorAlert("Cannot delete entry: HTTP " + responseCode, ex, feedForm);
        }
        getDisplay().setCurrent(feedForm);
    }
}
