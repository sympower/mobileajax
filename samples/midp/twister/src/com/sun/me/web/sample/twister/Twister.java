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

package com.sun.me.web.sample.twister;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
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
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import com.sun.me.web.request.Arg;
import com.sun.me.web.request.BasicAuth;
import com.sun.me.web.request.Request;
import com.sun.me.web.request.RequestListener;
import com.sun.me.web.request.Response;
import com.sun.me.web.path.Result;
import com.sun.me.web.path.ResultException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

/**
 * Sample Java ME client for Twitter
 */
public class Twister extends MIDlet implements CommandListener, RequestListener {
    
    private static final boolean DEBUG = true;
    
    private static final String STORE_NAME = "Settings";
    
    private static final String TIMELINE = "http://twitter.com/statuses/friends_timeline.json";
    private static final String FRIENDS = "http://twitter.com/statuses/friends.json";
    private static final String FOLLOWERS = "http://twitter.com/statuses/followers.json";
    private static final String UPDATE = "http://twitter.com/statuses/update.json";
    private static final String DIRECT = "http://twitter.com/direct_messages/new.json";
    
    private static class User {
        Status status;
        String location;
        String url;
        String name;
        String profile_image_url;
        String description;
        String screen_name;
        String id;
    }
    private User[] friends = new User[0];
    private User[] followers = new User[0];
    
    private static class Status {
        User user;
        String created_at;
        String text;
        String id;
    }
    private Status[] timeline = new Status[0];
    private String timelineETag = null;
    private User directMessageTo = null;
    
    private String username = "";
    private String credentials = "";
    
    private static final int MAX_IMAGE_CACHE_SIZE = 10;
    private Hashtable imageCache = new Hashtable();
    
    private boolean updatingTimeline = false;
    private final Timer timer = new Timer();
    private long updateDelayMillis = getTimerUpdateInterval();
    private TimerTask updateTask = new TimerTask() {
        public void run() {
            if (!updatingTimeline && getDisplay().getCurrent() == get_timelineForm()) {
                doGetTimeline();
            }
        }
    };
    
    public Twister() {
        initialize();
        timer.schedule(updateTask, 0, updateDelayMillis);
    }
    
    private List friendsList;//GEN-BEGIN:MVDFields
    private Alert alert;
    private Form home;
    private Command exitCommand;
    private Command friendsCommand;
    private Command timelineCommand;
    private Command followersCommand;
    private Command backCommand;
    private List followersList;
    private Command postCommand;
    private Command okCommand;
    private Form timelineForm;
    private Form statusDetail;
    private Command detailCommand;
    private TextField createdAtField;
    private TextField usernameField;
    private TextField textField;
    private TextField usernameSetting;
    private TextField passwordSetting;
    private ChoiceGroup updateGroup;
    private Form settings;
    private Command settingsCommand;
    private Form postForm;
    private TextField statusUpdate;
    private Command messageCommand;
    private StringItem to;//GEN-END:MVDFields
    
//GEN-LINE:MVDMethods
    
    /** Called by the system to indicate that a command has been invoked on a particular displayable.//GEN-BEGIN:MVDCABegin
     * @param command the Command that ws invoked
     * @param displayable the Displayable on which the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:MVDCABegin
        // Insert global pre-action code here
        if (displayable == home) {//GEN-BEGIN:MVDCABody
            if (command == timelineCommand) {//GEN-END:MVDCABody
                // Insert pre-action code here
                doGetTimeline();
                getDisplay().setCurrent(get_alert(), get_home());//GEN-LINE:MVDCAAction15
                // Insert post-action code here
            } else if (command == friendsCommand) {//GEN-LINE:MVDCACase15
                // Insert pre-action code here
                doGetFriends();
                getDisplay().setCurrent(get_alert(), get_home());//GEN-LINE:MVDCAAction13
                // Insert post-action code here
            } else if (command == exitCommand) {//GEN-LINE:MVDCACase13
                // Insert pre-action code here
                exitMIDlet();//GEN-LINE:MVDCAAction11
                // Insert post-action code here
            } else if (command == followersCommand) {//GEN-LINE:MVDCACase11
                // Insert pre-action code here
                doGetFollowers();
                getDisplay().setCurrent(get_alert(), get_home());//GEN-LINE:MVDCAAction17
                // Insert post-action code here
            } else if (command == postCommand) {//GEN-LINE:MVDCACase17
                // Insert pre-action code here
                if (!validateSettings()) { return; }
                selectFriend();
                getDisplay().setCurrent(get_postForm());//GEN-LINE:MVDCAAction43
                // Insert post-action code here
            } else if (command == settingsCommand) {//GEN-LINE:MVDCACase43
                // Insert pre-action code here
                getDisplay().setCurrent(get_settings());//GEN-LINE:MVDCAAction80
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase80
        } else if (displayable == friendsList) {
            if (command == backCommand) {//GEN-END:MVDCACase80
                // Insert pre-action code here
                getDisplay().setCurrent(get_home());//GEN-LINE:MVDCAAction19
                // Insert post-action code here
            } else if (command == exitCommand) {//GEN-LINE:MVDCACase19
                // Insert pre-action code here
                exitMIDlet();//GEN-LINE:MVDCAAction21
                // Insert post-action code here
            } else if (command == messageCommand) {//GEN-LINE:MVDCACase21
                // Insert pre-action code here
                selectFriend();
                getDisplay().setCurrent(get_postForm());//GEN-LINE:MVDCAAction88
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase88
        } else if (displayable == followersList) {
            if (command == backCommand) {//GEN-END:MVDCACase88
                // Insert pre-action code here
                getDisplay().setCurrent(get_home());//GEN-LINE:MVDCAAction41
                // Insert post-action code here
            } else if (command == exitCommand) {//GEN-LINE:MVDCACase41
                // Insert pre-action code here
                exitMIDlet();//GEN-LINE:MVDCAAction39
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase39
        } else if (displayable == settings) {
            if (command == okCommand) {//GEN-END:MVDCACase39
                // Insert pre-action code here
                saveSettings();
                // Do nothing//GEN-LINE:MVDCAAction53
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase53
        } else if (displayable == timelineForm) {
            if (command == backCommand) {//GEN-END:MVDCACase53
                // Insert pre-action code here
                getDisplay().setCurrent(get_home());//GEN-LINE:MVDCAAction56
                // Insert post-action code here
            } else if (command == exitCommand) {//GEN-LINE:MVDCACase56
                // Insert pre-action code here
                exitMIDlet();//GEN-LINE:MVDCAAction58
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase58
        } else if (displayable == statusDetail) {
            if (command == backCommand) {//GEN-END:MVDCACase58
                // Insert pre-action code here
                getDisplay().setCurrent(get_timelineForm());//GEN-LINE:MVDCAAction61
                // Insert post-action code here
            } else if (command == exitCommand) {//GEN-LINE:MVDCACase61
                // Insert pre-action code here
                exitMIDlet();//GEN-LINE:MVDCAAction63
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase63
        } else if (displayable == postForm) {
            if (command == postCommand) {//GEN-END:MVDCACase63
                // Insert pre-action code here
                doPostUpdate();
                getDisplay().setCurrent(get_alert(), get_home());//GEN-LINE:MVDCAAction83
                // Insert post-action code here
            } else if (command == backCommand) {//GEN-LINE:MVDCACase83
                // Insert pre-action code here
                if (directMessageTo == null) {
                    getDisplay().setCurrent(get_home());
                } else {
                    getDisplay().setCurrent(get_friendsList());
                }
                // Do nothing//GEN-LINE:MVDCAAction91
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase91
        }//GEN-END:MVDCACase91
        // Insert global post-action code here
}//GEN-LINE:MVDCAEnd
    
    /** This method initializes UI of the application.//GEN-BEGIN:MVDInitBegin
     */
    private void initialize() {//GEN-END:MVDInitBegin
        // Insert pre-init code here
        readSettings();
        getDisplay().setCurrent(get_home());//GEN-LINE:MVDInitInit
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
    
    
    
    /** This method returns instance for friendsList component and should be called instead of accessing friendsList field directly.//GEN-BEGIN:MVDGetBegin6
     * @return Instance for friendsList component
     */
    public List get_friendsList() {
        if (friendsList == null) {//GEN-END:MVDGetBegin6
            // Insert pre-init code here
            friendsList = new List("Friends", Choice.IMPLICIT, new String[0], new Image[0]);//GEN-BEGIN:MVDGetInit6
            friendsList.addCommand(get_backCommand());
            friendsList.addCommand(get_exitCommand());
            friendsList.addCommand(get_messageCommand());
            friendsList.setCommandListener(this);
            friendsList.setSelectedFlags(new boolean[0]);
            friendsList.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);//GEN-END:MVDGetInit6
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd6
        return friendsList;
    }//GEN-END:MVDGetEnd6
    
    /** This method returns instance for alert component and should be called instead of accessing alert field directly.//GEN-BEGIN:MVDGetBegin8
     * @return Instance for alert component
     */
    public Alert get_alert() {
        if (alert == null) {//GEN-END:MVDGetBegin8
            // Insert pre-init code here
            alert = new Alert(null, "<Enter Text>", null, null);//GEN-BEGIN:MVDGetInit8
            alert.setTimeout(-2);//GEN-END:MVDGetInit8
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd8
        return alert;
    }//GEN-END:MVDGetEnd8
    
    /** This method returns instance for home component and should be called instead of accessing home field directly.//GEN-BEGIN:MVDGetBegin9
     * @return Instance for home component
     */
    public Form get_home() {
        if (home == null) {//GEN-END:MVDGetBegin9
            // Insert pre-init code here
            home = new Form("Twitter", new Item[0]);//GEN-BEGIN:MVDGetInit9
            home.addCommand(get_exitCommand());
            home.addCommand(get_friendsCommand());
            home.addCommand(get_timelineCommand());
            home.addCommand(get_followersCommand());
            home.addCommand(get_postCommand());
            home.addCommand(get_settingsCommand());
            home.setCommandListener(this);//GEN-END:MVDGetInit9
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd9
        return home;
    }//GEN-END:MVDGetEnd9
    
    /** This method returns instance for exitCommand component and should be called instead of accessing exitCommand field directly.//GEN-BEGIN:MVDGetBegin10
     * @return Instance for exitCommand component
     */
    public Command get_exitCommand() {
        if (exitCommand == null) {//GEN-END:MVDGetBegin10
            // Insert pre-init code here
            exitCommand = new Command("Exit", Command.EXIT, 1);//GEN-LINE:MVDGetInit10
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd10
        return exitCommand;
    }//GEN-END:MVDGetEnd10
    
    /** This method returns instance for friendsCommand component and should be called instead of accessing friendsCommand field directly.//GEN-BEGIN:MVDGetBegin12
     * @return Instance for friendsCommand component
     */
    public Command get_friendsCommand() {
        if (friendsCommand == null) {//GEN-END:MVDGetBegin12
            // Insert pre-init code here
            friendsCommand = new Command("List Friends", Command.ITEM, 1);//GEN-LINE:MVDGetInit12
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd12
        return friendsCommand;
    }//GEN-END:MVDGetEnd12
    
    /** This method returns instance for timelineCommand component and should be called instead of accessing timelineCommand field directly.//GEN-BEGIN:MVDGetBegin14
     * @return Instance for timelineCommand component
     */
    public Command get_timelineCommand() {
        if (timelineCommand == null) {//GEN-END:MVDGetBegin14
            // Insert pre-init code here
            timelineCommand = new Command("Get Timeline", Command.ITEM, 1);//GEN-LINE:MVDGetInit14
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd14
        return timelineCommand;
    }//GEN-END:MVDGetEnd14
    
    /** This method returns instance for followersCommand component and should be called instead of accessing followersCommand field directly.//GEN-BEGIN:MVDGetBegin16
     * @return Instance for followersCommand component
     */
    public Command get_followersCommand() {
        if (followersCommand == null) {//GEN-END:MVDGetBegin16
            // Insert pre-init code here
            followersCommand = new Command("List Followers", Command.ITEM, 1);//GEN-LINE:MVDGetInit16
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd16
        return followersCommand;
    }//GEN-END:MVDGetEnd16
    
    /** This method returns instance for backCommand component and should be called instead of accessing backCommand field directly.//GEN-BEGIN:MVDGetBegin18
     * @return Instance for backCommand component
     */
    public Command get_backCommand() {
        if (backCommand == null) {//GEN-END:MVDGetBegin18
            // Insert pre-init code here
            backCommand = new Command("Back", Command.BACK, 1);//GEN-LINE:MVDGetInit18
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd18
        return backCommand;
    }//GEN-END:MVDGetEnd18
    
    
    
    
    
    
    /** This method returns instance for followersList component and should be called instead of accessing followersList field directly.//GEN-BEGIN:MVDGetBegin32
     * @return Instance for followersList component
     */
    public List get_followersList() {
        if (followersList == null) {//GEN-END:MVDGetBegin32
            // Insert pre-init code here
            followersList = new List("Followers", Choice.IMPLICIT, new String[0], new Image[0]);//GEN-BEGIN:MVDGetInit32
            followersList.addCommand(get_exitCommand());
            followersList.addCommand(get_backCommand());
            followersList.setCommandListener(this);
            followersList.setSelectedFlags(new boolean[0]);//GEN-END:MVDGetInit32
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd32
        return followersList;
    }//GEN-END:MVDGetEnd32
    
    /** This method returns instance for postCommand component and should be called instead of accessing postCommand field directly.//GEN-BEGIN:MVDGetBegin42
     * @return Instance for postCommand component
     */
    public Command get_postCommand() {
        if (postCommand == null) {//GEN-END:MVDGetBegin42
            // Insert pre-init code here
            postCommand = new Command("Post Update", Command.OK, 1);//GEN-LINE:MVDGetInit42
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd42
        return postCommand;
    }//GEN-END:MVDGetEnd42
    
    
    
    
    /** This method returns instance for settings component and should be called instead of accessing settings field directly.//GEN-BEGIN:MVDGetBegin49
     * @return Instance for settings component
     */
    public Form get_settings() {
        if (settings == null) {//GEN-END:MVDGetBegin49
            // Insert pre-init code here
            settings = new Form("Settings", new Item[] {//GEN-BEGIN:MVDGetInit49
                get_usernameSetting(),
                get_passwordSetting(),
                get_updateGroup()
            });
            settings.addCommand(get_okCommand());
            settings.setCommandListener(this);//GEN-END:MVDGetInit49
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd49
        return settings;
    }//GEN-END:MVDGetEnd49
    
    /** This method returns instance for usernameSetting component and should be called instead of accessing usernameSetting field directly.//GEN-BEGIN:MVDGetBegin50
     * @return Instance for usernameSetting component
     */
    public TextField get_usernameSetting() {
        if (usernameSetting == null) {//GEN-END:MVDGetBegin50
            // Insert pre-init code here
            usernameSetting = new TextField("User:", "", 120, TextField.ANY | TextField.NON_PREDICTIVE);//GEN-LINE:MVDGetInit50
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd50
        return usernameSetting;
    }//GEN-END:MVDGetEnd50
    
    /** This method returns instance for passwordSetting component and should be called instead of accessing passwordSetting field directly.//GEN-BEGIN:MVDGetBegin51
     * @return Instance for passwordSetting component
     */
    public TextField get_passwordSetting() {
        if (passwordSetting == null) {//GEN-END:MVDGetBegin51
            // Insert pre-init code here
            passwordSetting = new TextField("Password:", "", 120, TextField.ANY | TextField.PASSWORD | TextField.SENSITIVE | TextField.NON_PREDICTIVE);//GEN-LINE:MVDGetInit51
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd51
        return passwordSetting;
    }//GEN-END:MVDGetEnd51
    
    /** This method returns instance for okCommand component and should be called instead of accessing okCommand field directly.//GEN-BEGIN:MVDGetBegin52
     * @return Instance for okCommand component
     */
    public Command get_okCommand() {
        if (okCommand == null) {//GEN-END:MVDGetBegin52
            // Insert pre-init code here
            okCommand = new Command("Ok", Command.OK, 1);//GEN-LINE:MVDGetInit52
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd52
        return okCommand;
    }//GEN-END:MVDGetEnd52
    
    /** This method returns instance for timelineForm component and should be called instead of accessing timelineForm field directly.//GEN-BEGIN:MVDGetBegin54
     * @return Instance for timelineForm component
     */
    public Form get_timelineForm() {
        if (timelineForm == null) {//GEN-END:MVDGetBegin54
            // Insert pre-init code here
            timelineForm = new Form("Timeline", new Item[0]);//GEN-BEGIN:MVDGetInit54
            timelineForm.addCommand(get_backCommand());
            timelineForm.addCommand(get_exitCommand());
            timelineForm.setCommandListener(this);//GEN-END:MVDGetInit54
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd54
        return timelineForm;
    }//GEN-END:MVDGetEnd54
    
    /** This method returns instance for statusDetail component and should be called instead of accessing statusDetail field directly.//GEN-BEGIN:MVDGetBegin59
     * @return Instance for statusDetail component
     */
    public Form get_statusDetail() {
        if (statusDetail == null) {//GEN-END:MVDGetBegin59
            // Insert pre-init code here
            statusDetail = new Form("Status", new Item[] {//GEN-BEGIN:MVDGetInit59
                get_createdAtField(),
                get_usernameField(),
                get_textField()
            });
            statusDetail.addCommand(get_backCommand());
            statusDetail.addCommand(get_exitCommand());
            statusDetail.setCommandListener(this);//GEN-END:MVDGetInit59
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd59
        return statusDetail;
    }//GEN-END:MVDGetEnd59
    
    
    
    /** This method returns instance for detailCommand component and should be called instead of accessing detailCommand field directly.//GEN-BEGIN:MVDGetBegin64
     * @return Instance for detailCommand component
     */
    public Command get_detailCommand() {
        if (detailCommand == null) {//GEN-END:MVDGetBegin64
            // Insert pre-init code here
            detailCommand = new Command("Details", Command.OK, 1);//GEN-LINE:MVDGetInit64
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd64
        return detailCommand;
    }//GEN-END:MVDGetEnd64
    
    /** This method returns instance for createdAtField component and should be called instead of accessing createdAtField field directly.//GEN-BEGIN:MVDGetBegin66
     * @return Instance for createdAtField component
     */
    public TextField get_createdAtField() {
        if (createdAtField == null) {//GEN-END:MVDGetBegin66
            // Insert pre-init code here
            createdAtField = new TextField("Created:", null, 120, TextField.ANY | TextField.UNEDITABLE);//GEN-LINE:MVDGetInit66
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd66
        return createdAtField;
    }//GEN-END:MVDGetEnd66
    
    /** This method returns instance for usernameField component and should be called instead of accessing usernameField field directly.//GEN-BEGIN:MVDGetBegin67
     * @return Instance for usernameField component
     */
    public TextField get_usernameField() {
        if (usernameField == null) {//GEN-END:MVDGetBegin67
            // Insert pre-init code here
            usernameField = new TextField("User:", null, 120, TextField.ANY | TextField.UNEDITABLE);//GEN-LINE:MVDGetInit67
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd67
        return usernameField;
    }//GEN-END:MVDGetEnd67
    
    /** This method returns instance for textField component and should be called instead of accessing textField field directly.//GEN-BEGIN:MVDGetBegin69
     * @return Instance for textField component
     */
    public TextField get_textField() {
        if (textField == null) {//GEN-END:MVDGetBegin69
            // Insert pre-init code here
            textField = new TextField("Text:", null, 1024, TextField.ANY | TextField.UNEDITABLE);//GEN-LINE:MVDGetInit69
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd69
        return textField;
    }//GEN-END:MVDGetEnd69
    
    /** This method returns instance for updateGroup component and should be called instead of accessing updateGroup field directly.//GEN-BEGIN:MVDGetBegin70
     * @return Instance for updateGroup component
     */
    public ChoiceGroup get_updateGroup() {
        if (updateGroup == null) {//GEN-END:MVDGetBegin70
            // Insert pre-init code here
            updateGroup = new ChoiceGroup("Auto Update Interval (in Minutes):", Choice.EXCLUSIVE, new String[] {//GEN-BEGIN:MVDGetInit70
                "1",
                "2",
                "5",
                "10",
                "15",
                "30",
                "60"
            }, new Image[] {
                null,
                null,
                null,
                null,
                null,
                null,
                null
            });
            updateGroup.setSelectedFlags(new boolean[] {
                false,
                true,
                true,
                false,
                false,
                false,
                false
            });//GEN-END:MVDGetInit70
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd70
        return updateGroup;
    }//GEN-END:MVDGetEnd70
    
    /** This method returns instance for settingsCommand component and should be called instead of accessing settingsCommand field directly.//GEN-BEGIN:MVDGetBegin79
     * @return Instance for settingsCommand component
     */
    public Command get_settingsCommand() {
        if (settingsCommand == null) {//GEN-END:MVDGetBegin79
            // Insert pre-init code here
            settingsCommand = new Command("Settings", Command.ITEM, 1);//GEN-LINE:MVDGetInit79
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd79
        return settingsCommand;
    }//GEN-END:MVDGetEnd79
    
    /** This method returns instance for postForm component and should be called instead of accessing postForm field directly.//GEN-BEGIN:MVDGetBegin81
     * @return Instance for postForm component
     */
    public Form get_postForm() {
        if (postForm == null) {//GEN-END:MVDGetBegin81
            // Insert pre-init code here
            postForm = new Form("Status Update", new Item[] {//GEN-BEGIN:MVDGetInit81
                get_to(),
                get_statusUpdate()
            });
            postForm.addCommand(get_postCommand());
            postForm.addCommand(get_backCommand());
            postForm.setCommandListener(this);//GEN-END:MVDGetInit81
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd81
        return postForm;
    }//GEN-END:MVDGetEnd81
    
    /** This method returns instance for statusUpdate component and should be called instead of accessing statusUpdate field directly.//GEN-BEGIN:MVDGetBegin86
     * @return Instance for statusUpdate component
     */
    public TextField get_statusUpdate() {
        if (statusUpdate == null) {//GEN-END:MVDGetBegin86
            // Insert pre-init code here
            statusUpdate = new TextField("Update:", null, 140, TextField.ANY);//GEN-BEGIN:MVDGetInit86
            statusUpdate.setLayout(Item.LAYOUT_CENTER | Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_EXPAND | Item.LAYOUT_VEXPAND);//GEN-END:MVDGetInit86
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd86
        return statusUpdate;
    }//GEN-END:MVDGetEnd86
    
    /** This method returns instance for messageCommand component and should be called instead of accessing messageCommand field directly.//GEN-BEGIN:MVDGetBegin87
     * @return Instance for messageCommand component
     */
    public Command get_messageCommand() {
        if (messageCommand == null) {//GEN-END:MVDGetBegin87
            // Insert pre-init code here
            messageCommand = new Command("Direct Message", Command.ITEM, 1);//GEN-LINE:MVDGetInit87
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd87
        return messageCommand;
    }//GEN-END:MVDGetEnd87
    
    
    /** This method returns instance for to component and should be called instead of accessing to field directly.//GEN-BEGIN:MVDGetBegin93
     * @return Instance for to component
     */
    public StringItem get_to() {
        if (to == null) {//GEN-END:MVDGetBegin93
            // Insert pre-init code here
            to = new StringItem("To: ", "");//GEN-BEGIN:MVDGetInit93
            to.setLayout(Item.LAYOUT_EXPAND);//GEN-END:MVDGetInit93
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd93
        return to;
    }//GEN-END:MVDGetEnd93
    
    public void startApp() {
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional) {
    }
    
    private boolean saveSettings() {
        updateDelayMillis = getTimerUpdateInterval();
        
        final String usr = get_usernameSetting().getString();
        final String pwd = get_passwordSetting().getString();
        if (usr == null || "".equals(usr) || pwd == null || "".equals(pwd)) {
            alert(AlertType.ERROR, "Please set username and password in Settings", null, Alert.FOREVER, get_home());
            return false;
        }
        
        username = usr;
        credentials = "Basic " + BasicAuth.encode(usr, pwd);
        
        RecordStore store = null;
        try {
            try { RecordStore.deleteRecordStore(STORE_NAME); } catch (Exception ignore) {}
            store = RecordStore.openRecordStore(STORE_NAME, true);
            
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final DataOutput dos = new DataOutputStream(bos);
            
            dos.writeUTF(username);
            dos.writeUTF(credentials);
            dos.writeLong(updateDelayMillis);
            
            final byte[] bits = bos.toByteArray();
            store.addRecord(bits, 0, bits.length);
            
            alert(AlertType.INFO, "Saved Settings", null, Alert.FOREVER, get_home());
            return true;
        } catch (Exception ex) {
            alert(AlertType.ERROR, "Error saving Settings - Memory full?", ex, Alert.FOREVER, get_home());
            return false;
        } finally {
            if (store != null) { try { store.closeRecordStore(); } catch (Exception ignore) {}}
        }
    }
    
    private boolean readSettings() {
        RecordStore store = null;
        try {
            store = RecordStore.openRecordStore(STORE_NAME, false);
            final RecordEnumeration en = store.enumerateRecords(null, null, false);
            if (!en.hasNextElement()) {
                return false;
            }
            
            final ByteArrayInputStream bis = new ByteArrayInputStream(en.nextRecord());
            final DataInputStream dis = new DataInputStream(bis);
            
            username = dis.readUTF();
            credentials = dis.readUTF();
            updateDelayMillis = dis.readLong();
            
            return true;
        } catch (Exception ex) {
            alert(AlertType.ERROR, "Error reading saved Settings", ex, Alert.FOREVER, get_home());
            return false;
        } finally {
            if (store != null) { try { store.closeRecordStore(); } catch (Exception ignore) {}}
        }
    }
    
    private long getTimerUpdateInterval() {
        final int selected = get_updateGroup().getSelectedIndex();
        // update delay in the UI is in minutes
        return 60 * 1000 * Long.parseLong(get_updateGroup().getString(selected));
    }
    
    private String getDisplayStatus(final Status status) {
        return status.user.screen_name +
            " " + status.created_at + "\n  " +
            status.text;
    }
    
    private Status findMatchingStatus(final String label) {
        if (timeline == null) {
            alert(AlertType.ERROR, "No items in timeline", null, Alert.FOREVER, get_timelineForm());
            return null;
        }
        
        if (label == null) {
            alert(AlertType.ERROR, "No item selected", null, Alert.FOREVER, get_timelineForm());
            return null;
        }
        
        for (int i=0; i < timeline.length; i++) {
            if (label.equals(getDisplayStatus(timeline[i]))) {
                return timeline[i];
            }
        }
        
        return null;
    }
    
    private Image loadImage(final String url) {
        
        final Image cachedImage = (Image) imageCache.get(url);
        if (cachedImage != null) {
            return cachedImage;
        }
        
        HttpConnection conn = null;
        Image img = null;
        try {
            conn = (HttpConnection) Connector.open(url);
            conn.setRequestProperty("Accept", "image/*");
            final int responseCode = conn.getResponseCode();
            if (responseCode != HttpConnection.HTTP_OK) {
                return null;
            }
            img = Image.createImage(conn.openInputStream());
        } catch (IOException ex) {
            return null;
        } finally {
            if (conn != null) { try { conn.close(); } catch (IOException ignore) {} }
        }
        
        if (img != null) {
            if (imageCache.size() > MAX_IMAGE_CACHE_SIZE) {
                imageCache.remove(imageCache.keys().nextElement());
            }
            imageCache.put(url, img);
        }
        
        return img;
    }
    
    private void alert(final AlertType type, final String message,
        final Exception ex, final int timeout, final Displayable next) {
        
        final Alert alert = get_alert();
        alert.setType(type);
        final String exMsg = ex == null ? null : ex.getMessage();
        final String text = exMsg == null ? message : message + ": " + exMsg;
        alert.setString(text);
        alert.setTimeout(timeout);
        if (DEBUG) {
            if (ex != null) {
                ex.printStackTrace();
            }
        }
        getDisplay().setCurrent(alert, next);
    }
    
    private Arg[] getHttpArgs(final String eTag) {
        final Arg authArg = new Arg(Arg.AUTHORIZATION, credentials);
        final Arg etagArg = new Arg(Arg.IF_NONE_MATCH, eTag);
        
        final Arg[] argsWithETag = { authArg, etagArg };
        final Arg[] argsWithoutETag = { authArg };
        
        return eTag == null ? argsWithoutETag : argsWithETag;
    }
    
    private boolean validateSettings() {
        if ("".equals(username) || "".equals(credentials)) {
            alert(AlertType.ERROR, "Please set username and password in Settings", null, Alert.FOREVER, get_home());
            return false;
        }
        return true;
    }
    
    private Result validateResponse(final Response response, final String errorMessage) {
        
        final int responseCode = response.getCode();
        if (responseCode == HttpConnection.HTTP_NOT_MODIFIED) {
            return null;
        }
        
        if (responseCode != HttpConnection.HTTP_OK) {
            get_alert().setString("Error " + errorMessage + ": HTTP response code " + responseCode);
            return null;
        }
        
        final Exception rex = response.getException();
        if (rex != null) {
            get_alert().setString("Error " + errorMessage + ": " + rex.getMessage());
            if (DEBUG) {
                rex.printStackTrace();
            }
            return null;
        }
        
        final Result result = response.getResult();
        if (DEBUG) {
            System.out.println(result);
        }
        
        return result;
    }
    
    private void selectFriend() {
        directMessageTo = null;
        if (getDisplay().getCurrent() == get_friendsList()) {
            final int index = friendsList.getSelectedIndex();
            if (index >= 0) {
                final String str = friendsList.getString(index);
                for (int i=0; i < friends.length; i++) {
                    if (str.equals(friends[i].name)) {
                        directMessageTo = friends[i];
                        break;
                    }
                }
            }
        }
        get_to().setText(directMessageTo == null ? "all" : directMessageTo.name);
    }
    
    private void doGetFriends() {
        if (!validateSettings()) {
            return;
        }
        get_alert().setString("getting friends...");
        Request.get(FRIENDS, null, getHttpArgs(null), this, get_friendsList());
    }
    
    private void handleGetFriendsResponse(final Response response) {
        
        final Result result = validateResponse(response, "getting friends");
        if (result == null) {
            return;
        }
        
        int size = 0;
        try {
            size = result.getSizeOfArray("");
        } catch (ResultException ignore) {
        }
        
        try {
            friends = new User[size];
            for (int i=0; i < size; i++) {
                final String basePath = "[" + i + "].";
                final User user = new User();
                user.location = result.getAsString(basePath + "location");
                user.url = result.getAsString(basePath + "url");
                user.name = result.getAsString(basePath + "name");
                user.profile_image_url = result.getAsString(basePath + "profile_image_url");
                user.description = result.getAsString(basePath + "description");
                user.screen_name = result.getAsString(basePath + "screen_name");
                user.id = result.getAsString(basePath + "id");
                friends[i] = user;
                
                final String statusBasePath = basePath + "status.";
                final Status status = new Status();
                status.created_at = result.getAsString(statusBasePath + "created_at");
                status.text = result.getAsString(statusBasePath + "text");
                status.id = result.getAsString(statusBasePath + "id");
                friends[i].status = status;
            }
        } catch (Exception ex) {
            get_alert().setString("Error extracting friends info: " + ex.getMessage());
            return;
        }
        
        final List list = get_friendsList();
        list.deleteAll();
        for (int i = 0, j = 0; i < friends.length; i++) {
            list.insert(j++, friends[i].name, loadImage(friends[i].profile_image_url));
            if (friends[i].status != null) {
                if (friends[i].status.text != null) {
                    list.insert(j++, friends[i].status.text, null);
                }
            }
        }
        getDisplay().setCurrent(list);
    }
    
    private void doGetTimeline() {
        if (!validateSettings()) {
            return;
        }
        updatingTimeline = true;
        get_alert().setString("getting timeline...");
        Request.get(TIMELINE, null, getHttpArgs(timelineETag), this, get_timelineForm());
    }
    
    private void handleGetTimelineResponse(final Response response) {
        
        final Result result = validateResponse(response, "getting timeline");
        if (result == null) {
            // result will also be null in case of an HTTP 304 - Not Modified response
            return;
        }
        
        final Arg[] headers = response.getHeaders();
        final String etag = Arg.ETAG.toLowerCase();
        for (int i=0; i < headers.length; i++) {
            if (etag.equals(headers[i].getKey().toLowerCase())) {
                timelineETag = headers[i].getValue();
            }
        }
        
        int size = 0;
        try {
            size = result.getSizeOfArray("");
        } catch (Exception ignore) {
        }
        
        if (size == 0) {
            alert(AlertType.INFO, "No updates", null, Alert.FOREVER, get_home());
            return;
        }
        
        try {
            timeline = new Status[size];
            for (int i=0; i < size; i++) {
                final String timelineBasePath = "[" + i + "].";
                timeline[i] = new Status();
                timeline[i].created_at = result.getAsString(timelineBasePath + "created_at");
                timeline[i].text = result.getAsString(timelineBasePath + "text");
                timeline[i].id = result.getAsString(timelineBasePath + "id");
                
                final String userBasePath = timelineBasePath + "user.";
                timeline[i].user = new User();
                timeline[i].user.location = result.getAsString(userBasePath + "location");
                timeline[i].user.url = result.getAsString(userBasePath + "url");
                timeline[i].user.name = result.getAsString(userBasePath + "name");
                timeline[i].user.profile_image_url = result.getAsString(userBasePath + "profile_image_url");
                timeline[i].user.description = result.getAsString(userBasePath + "description");
                timeline[i].user.screen_name = result.getAsString(userBasePath + "screen_name");
                timeline[i].user.id = result.getAsString(userBasePath + "id");
            }
        } catch (Exception ex) {
            get_alert().setString("Error extracting friends status: " + ex.getMessage());
            return;
        }
        
        final Form form = get_timelineForm();
        form.deleteAll();
        for (int i=0; i < timeline.length; i++) {
            final StringItem sitem = new StringItem(null, getDisplayStatus(timeline[i]), Item.HYPERLINK);
            sitem.setLayout(Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_LEFT);
            sitem.setDefaultCommand(get_detailCommand());
            sitem.setItemCommandListener(new ItemCommandListener() {
                public void commandAction(Command command, Item item) {
                    final Status status = findMatchingStatus(((StringItem) item).getText());
                    if (status != null) {
                        get_createdAtField().setString(status.created_at);
                        get_usernameField().setString(status.user.name);
                        get_textField().setString(status.text);
                    }
                    getDisplay().setCurrent(get_statusDetail());
                }
            });
            form.append(sitem);
        }
        getDisplay().setCurrent(form);
    }
    
    private void doGetFollowers() {
        if (!validateSettings()) {
            return;
        }
        get_alert().setString("getting followers...");
        Request.get(FOLLOWERS, null, getHttpArgs(null), this, get_followersList());
    }
    
    private void handleGetFollowersResponse(final Response response) {
        
        final Result result = validateResponse(response, "getting followers");
        if (result == null) {
            return;
        }
        
        int size = 0;
        try {
            size = result.getSizeOfArray("");
        } catch (ResultException ignore) {
        }
        
        try {
            followers = new User[size];
            for (int i=0; i < size; i++) {
                final String basePath = "[" + i + "].";
                final User user = new User();
                user.location = result.getAsString(basePath + "location");
                user.url = result.getAsString(basePath + "url");
                user.name = result.getAsString(basePath + "name");
                user.profile_image_url = result.getAsString(basePath + "profile_image_url");
                user.description = result.getAsString(basePath + "description");
                user.screen_name = result.getAsString(basePath + "screen_name");
                user.id = result.getAsString(basePath + "id");
                followers[i] = user;
                
                final String statusBasePath = basePath + "status.";
                final Status status = new Status();
                status.created_at = result.getAsString(statusBasePath + "created_at");
                status.text = result.getAsString(statusBasePath + "text");
                status.id = result.getAsString(statusBasePath + "id");
                followers[i].status = status;
            }
        } catch (Exception ex) {
            get_alert().setString("Error extracting followers info: " + ex.getMessage());
            return;
        }
        
        final List list = get_followersList();
        list.deleteAll();
        for (int i=0, j=0; i < followers.length; i++) {
            list.insert(j++, followers[i].name, loadImage(followers[i].profile_image_url));
            if (followers[i].status != null) {
                if (followers[i].status.text != null) {
                    list.insert(j++, followers[i].status.text, null);
                }
            }
        }
        getDisplay().setCurrent(list);
    }
    
    private void doPostUpdate() {
        if (!validateSettings()) {
            return;
        }
        get_alert().setString("posting update...");
        if (directMessageTo == null) {
            final Arg[] args = {
                new Arg("source", "twister"),
                new Arg("status", get_statusUpdate().getString())
            };
            Request.post(UPDATE, args, getHttpArgs(null), this, null, get_home());
        } else {
            final Arg[] args = {
                new Arg("source", "twister"),
                new Arg("user", directMessageTo.id),
                new Arg("text", get_statusUpdate().getString())
            };
            Request.post(DIRECT, args, getHttpArgs(null), this, null, get_home());
        }
        directMessageTo = null;
    }
    
    private void handlePostResponse(final Response response) {
        
        final Result result = validateResponse(response, "posting update");
        if (result == null) {
            return;
        }
        
        final Status status = new Status();
        try {
            final String statusBasePath = "";
            status.created_at = result.getAsString(statusBasePath + "created_at");
            status.text = result.getAsString(statusBasePath + "text");
            status.id = result.getAsString(statusBasePath + "id");
            
            final String userBasePath = "user.";
            status.user = new User();
            status.user.location = result.getAsString(userBasePath + "location");
            status.user.url = result.getAsString(userBasePath + "url");
            status.user.name = result.getAsString(userBasePath + "name");
            status.user.profile_image_url = result.getAsString(userBasePath + "profile_image_url");
            status.user.description = result.getAsString(userBasePath + "description");
            status.user.screen_name = result.getAsString(userBasePath + "screen_name");
            status.user.id = result.getAsString(userBasePath + "id");
        } catch (Exception ex) {
            get_alert().setString("Error extracting status: " + ex.getMessage());
            return;
        }
        
        final Form form = get_timelineForm();
        
        final StringItem sitem = new StringItem(null, getDisplayStatus(status), Item.HYPERLINK);
        sitem.setLayout(Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_LEFT);
        sitem.setDefaultCommand(get_detailCommand());
        sitem.setItemCommandListener(new ItemCommandListener() {
            public void commandAction(Command command, Item item) {
                final Status status = findMatchingStatus(((StringItem) item).getText());
                if (status != null) {
                    get_createdAtField().setString(status.created_at);
                    get_usernameField().setString(status.user.name);
                    get_textField().setString(status.text);
                }
                getDisplay().setCurrent(get_statusDetail());
            }
        });
        form.insert(0, sitem);
        
        getDisplay().setCurrent(form);
    }
    
    public void readProgress(final Object context, final int bytes, final int total) {
        get_alert().setString("Read " + bytes + (total > 0 ? "/" + total : ""));
    }
    
    public void writeProgress(final Object context, final int bytes, final int total) {
        get_alert().setString("Wrote " + bytes + (total > 0 ? "/" + total : ""));
    }
    
    public void done(final Object context, final Response response) throws Exception {
        if (context == friendsList) {
            handleGetFriendsResponse(response);
        } else if (context == timelineForm) {
            handleGetTimelineResponse(response);
            updatingTimeline = false;
        } else if (context == followersList) {
            handleGetFollowersResponse(response);
        } else if (context == home) {
            handlePostResponse(response);
        } else {
            throw new IllegalArgumentException("unknown context returned: " + context.toString());
        }
    }
}
