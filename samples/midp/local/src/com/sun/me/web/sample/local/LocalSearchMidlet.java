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

package com.sun.me.web.sample.local;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import javax.microedition.io.ConnectionNotFoundException;
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
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import com.sun.me.web.path.Result;
import com.sun.me.web.path.ResultException;
import com.sun.me.web.request.Arg;
import com.sun.me.web.request.ProgressInputStream;
import com.sun.me.web.request.Request;
import com.sun.me.web.request.RequestListener;
import com.sun.me.web.request.Response;
import java.io.InputStream;

/**
 * Sample MIDlet for Local Search
 */
public class LocalSearchMidlet extends MIDlet implements CommandListener, RequestListener {
    
    private static final boolean DEBUG = true;
    
    /* TODO: Get own APP ID for Yahoo Local and Maps APIs */
    private static final String APPID = "";
    
    private static final int DEFAULT_RESULT_COUNT = 10;
    private static final int MAX_MAP_ZOOM = 12;
    private static final int DEFAULT_MAP_ZOOM = 6;
    private static final int MIN_MAP_ZOOM = 1;
    
    private Hashtable imageCache = new Hashtable(MAX_MAP_ZOOM - MIN_MAP_ZOOM);
    private int mapZoomLevel = DEFAULT_MAP_ZOOM;
    private Item selectedItem = null;
    
    private static final String LOCAL_BASE = "http://local.yahooapis.com/LocalSearchService/V2/localSearch";
    
    private static final String MAP_BASE = "http://api.local.yahoo.com/MapsService/V1/mapImage";

    /* Demo mode is activiated if network connection fails */
    private boolean demoMode = false;
    
    private String mapAllLink = null;
    
    private static class Links {
        String title;
        String business;
        String listing;
        String map;
        String tel;
        String latitude;
        String longitude;
    };
    private Links[] links = null;
    
    private int start = 1;
    private int totalResultsAvailable = start;
    
    public LocalSearchMidlet() {
    }
    
    private Form queryForm;//GEN-BEGIN:MVDFields
    private Command exitCommand;
    private Form resultForm;
    private Command searchCommand;
    private Alert alert;
    private TextField searchFor;
    private TextField street;
    private TextField location;
    private Command mapCommand;
    private Command listingCommand;
    private Command businessCommand;
    private ChoiceGroup sortGroup;
    private Command mapAllCommand;
    private Command backCommand;
    private Command callCommand;
    private Form mapForm;
    private ImageItem mapImageItem;
    private Command mapBackCommand;
    private Command zoomInCommand;
    private Command zoomOutCommand;
    private Command selectCommand;
    private Command nextCommand;
    private Command prevCommand;//GEN-END:MVDFields
    
//GEN-LINE:MVDMethods
    
    /** This method initializes UI of the application.//GEN-BEGIN:MVDInitBegin
     */
    private void initialize() {//GEN-END:MVDInitBegin
        // Insert pre-init code here
        getDisplay().setCurrent(get_queryForm());//GEN-LINE:MVDInitInit
        // Insert post-init code here
    }//GEN-LINE:MVDInitEnd
    
    /** Called by the system to indicate that a command has been invoked on a particular displayable.//GEN-BEGIN:MVDCABegin
     * @param command the Command that ws invoked
     * @param displayable the Displayable on which the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:MVDCABegin
        // Insert global pre-action code here
        if (displayable == queryForm) {//GEN-BEGIN:MVDCABody
            if (command == exitCommand) {//GEN-END:MVDCABody
                // Insert pre-action code here
                exitMIDlet();//GEN-LINE:MVDCAAction3
                // Insert post-action code here
            } else if (command == searchCommand) {//GEN-LINE:MVDCACase3
                // Insert pre-action code here
                getDisplay().setCurrent(get_alert(), get_queryForm());//GEN-LINE:MVDCAAction13
                // Insert post-action code here
                search();
            }//GEN-BEGIN:MVDCACase13
        } else if (displayable == resultForm) {
            if (command == exitCommand) {//GEN-END:MVDCACase13
                // Insert pre-action code here
                exitMIDlet();//GEN-LINE:MVDCAAction16
                // Insert post-action code here
            } else if (command == businessCommand) {//GEN-LINE:MVDCACase16
                // Insert pre-action code here
                showBusiness();
                // Do nothing//GEN-LINE:MVDCAAction38
                // Insert post-action code here
            } else if (command == listingCommand) {//GEN-LINE:MVDCACase38
                // Insert pre-action code here
                showListing();
                // Do nothing//GEN-LINE:MVDCAAction36
                // Insert post-action code here
            } else if (command == mapAllCommand) {//GEN-LINE:MVDCACase36
                // Insert pre-action code here
                showAllOnMap();
                // Do nothing//GEN-LINE:MVDCAAction48
                // Insert post-action code here
            } else if (command == backCommand) {//GEN-LINE:MVDCACase48
                // Insert pre-action code here
                getDisplay().setCurrent(get_queryForm());//GEN-LINE:MVDCAAction50
                // Insert post-action code here
            } else if (command == callCommand) {//GEN-LINE:MVDCACase50
                // Insert pre-action code here
                call();
                // Do nothing//GEN-LINE:MVDCAAction52
                // Insert post-action code here
            } else if (command == mapCommand) {//GEN-LINE:MVDCACase52
                // Insert pre-action code here
                getDisplay().setCurrent(get_alert(), get_resultForm());//GEN-LINE:MVDCAAction89
                // Insert post-action code here
                map();
            } else if (command == prevCommand) {//GEN-LINE:MVDCACase89
                // Insert pre-action code here
                start = Math.max(1, start - DEFAULT_RESULT_COUNT);
                getDisplay().setCurrent(get_alert());
                search();
                // Do nothing//GEN-LINE:MVDCAAction98
                // Insert post-action code here
            } else if (command == nextCommand) {//GEN-LINE:MVDCACase98
                // Insert pre-action code here
                start = Math.min(totalResultsAvailable, start + DEFAULT_RESULT_COUNT);
                getDisplay().setCurrent(get_alert());
                search();
                // Do nothing//GEN-LINE:MVDCAAction96
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase96
        } else if (displayable == mapForm) {
            if (command == mapBackCommand) {//GEN-END:MVDCACase96
                // Insert pre-action code here
                getDisplay().setCurrent(get_resultForm());//GEN-LINE:MVDCAAction65
                // Insert post-action code here
            } else if (command == exitCommand) {//GEN-LINE:MVDCACase65
                // Insert pre-action code here
                exitMIDlet();//GEN-LINE:MVDCAAction67
                // Insert post-action code here
            } else if (command == zoomOutCommand) {//GEN-LINE:MVDCACase67
                // Insert pre-action code here
                mapZoomLevel = Math.min(++mapZoomLevel, MAX_MAP_ZOOM);
                switchZoomLevel();
                // Do nothing//GEN-LINE:MVDCAAction94
                // Insert post-action code here
            } else if (command == zoomInCommand) {//GEN-LINE:MVDCACase94
                // Insert pre-action code here
                mapZoomLevel = Math.max(--mapZoomLevel, MIN_MAP_ZOOM);
                switchZoomLevel();
                // Do nothing//GEN-LINE:MVDCAAction92
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase92
        }//GEN-END:MVDCACase92
        // Insert global post-action code here
}//GEN-LINE:MVDCAEnd
    
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
    
    /** This method returns instance for queryForm component and should be called instead of accessing queryForm field directly.//GEN-BEGIN:MVDGetBegin2
     * @return Instance for queryForm component
     */
    public Form get_queryForm() {
        if (queryForm == null) {//GEN-END:MVDGetBegin2
            // Insert pre-init code here
            queryForm = new Form("Local Search", new Item[] {//GEN-BEGIN:MVDGetInit2
                get_searchFor(),
                get_location(),
                get_street(),
                get_sortGroup()
            });
            queryForm.addCommand(get_exitCommand());
            queryForm.addCommand(get_searchCommand());
            queryForm.setCommandListener(this);//GEN-END:MVDGetInit2
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd2
        return queryForm;
    }//GEN-END:MVDGetEnd2
    
    
    /** This method returns instance for exitCommand component and should be called instead of accessing exitCommand field directly.//GEN-BEGIN:MVDGetBegin5
     * @return Instance for exitCommand component
     */
    public Command get_exitCommand() {
        if (exitCommand == null) {//GEN-END:MVDGetBegin5
            // Insert pre-init code here
            exitCommand = new Command("Exit", Command.EXIT, 1);//GEN-LINE:MVDGetInit5
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd5
        return exitCommand;
    }//GEN-END:MVDGetEnd5
    
    /** This method returns instance for resultForm component and should be called instead of accessing resultForm field directly.//GEN-BEGIN:MVDGetBegin6
     * @return Instance for resultForm component
     */
    public Form get_resultForm() {
        if (resultForm == null) {//GEN-END:MVDGetBegin6
            // Insert pre-init code here
            resultForm = new Form("Results", new Item[0]);//GEN-BEGIN:MVDGetInit6
            resultForm.addCommand(get_mapCommand());
            resultForm.addCommand(get_listingCommand());
            resultForm.addCommand(get_businessCommand());
            resultForm.addCommand(get_mapAllCommand());
            resultForm.addCommand(get_callCommand());
            resultForm.addCommand(get_backCommand());
            resultForm.addCommand(get_exitCommand());
            resultForm.addCommand(get_nextCommand());
            resultForm.addCommand(get_prevCommand());
            resultForm.setCommandListener(this);//GEN-END:MVDGetInit6
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd6
        return resultForm;
    }//GEN-END:MVDGetEnd6
    
    
    /** This method returns instance for searchCommand component and should be called instead of accessing searchCommand field directly.//GEN-BEGIN:MVDGetBegin12
     * @return Instance for searchCommand component
     */
    public Command get_searchCommand() {
        if (searchCommand == null) {//GEN-END:MVDGetBegin12
            // Insert pre-init code here
            searchCommand = new Command("Search", Command.OK, 1);//GEN-LINE:MVDGetInit12
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd12
        return searchCommand;
    }//GEN-END:MVDGetEnd12
    
    /** This method returns instance for alert component and should be called instead of accessing alert field directly.//GEN-BEGIN:MVDGetBegin14
     * @return Instance for alert component
     */
    public Alert get_alert() {
        if (alert == null) {//GEN-END:MVDGetBegin14
            // Insert pre-init code here
            alert = new Alert(null, "", null, AlertType.INFO);//GEN-BEGIN:MVDGetInit14
            alert.setTimeout(-2);//GEN-END:MVDGetInit14
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd14
        return alert;
    }//GEN-END:MVDGetEnd14
    
    
    
    /** This method returns instance for searchFor component and should be called instead of accessing searchFor field directly.//GEN-BEGIN:MVDGetBegin20
     * @return Instance for searchFor component
     */
    public TextField get_searchFor() {
        if (searchFor == null) {//GEN-END:MVDGetBegin20
            // Insert pre-init code here
            searchFor = new TextField("Search For:", "coffee", 120, TextField.ANY);//GEN-LINE:MVDGetInit20
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd20
        return searchFor;
    }//GEN-END:MVDGetEnd20
    
    /** This method returns instance for street component and should be called instead of accessing street field directly.//GEN-BEGIN:MVDGetBegin21
     * @return Instance for street component
     */
    public TextField get_street() {
        if (street == null) {//GEN-END:MVDGetBegin21
            // Insert pre-init code here
            street = new TextField("Street:", null, 120, TextField.ANY);//GEN-LINE:MVDGetInit21
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd21
        return street;
    }//GEN-END:MVDGetEnd21
    
    
    /** This method returns instance for location component and should be called instead of accessing location field directly.//GEN-BEGIN:MVDGetBegin24
     * @return Instance for location component
     */
    public TextField get_location() {
        if (location == null) {//GEN-END:MVDGetBegin24
            // Insert pre-init code here
            location = new TextField("Location:", "95054", 120, TextField.ANY);//GEN-LINE:MVDGetInit24
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd24
        return location;
    }//GEN-END:MVDGetEnd24
    
    
    /** This method returns instance for mapCommand component and should be called instead of accessing mapCommand field directly.//GEN-BEGIN:MVDGetBegin33
     * @return Instance for mapCommand component
     */
    public Command get_mapCommand() {
        if (mapCommand == null) {//GEN-END:MVDGetBegin33
            // Insert pre-init code here
            mapCommand = new Command("Map", Command.OK, 1);//GEN-LINE:MVDGetInit33
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd33
        return mapCommand;
    }//GEN-END:MVDGetEnd33
    
    /** This method returns instance for listingCommand component and should be called instead of accessing listingCommand field directly.//GEN-BEGIN:MVDGetBegin35
     * @return Instance for listingCommand component
     */
    public Command get_listingCommand() {
        if (listingCommand == null) {//GEN-END:MVDGetBegin35
            // Insert pre-init code here
            listingCommand = new Command("Listing", Command.ITEM, 1);//GEN-LINE:MVDGetInit35
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd35
        return listingCommand;
    }//GEN-END:MVDGetEnd35
    
    /** This method returns instance for businessCommand component and should be called instead of accessing businessCommand field directly.//GEN-BEGIN:MVDGetBegin37
     * @return Instance for businessCommand component
     */
    public Command get_businessCommand() {
        if (businessCommand == null) {//GEN-END:MVDGetBegin37
            // Insert pre-init code here
            businessCommand = new Command("Business", Command.ITEM, 1);//GEN-LINE:MVDGetInit37
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd37
        return businessCommand;
    }//GEN-END:MVDGetEnd37
    
    /** This method returns instance for sortGroup component and should be called instead of accessing sortGroup field directly.//GEN-BEGIN:MVDGetBegin39
     * @return Instance for sortGroup component
     */
    public ChoiceGroup get_sortGroup() {
        if (sortGroup == null) {//GEN-END:MVDGetBegin39
            // Insert pre-init code here
            sortGroup = new ChoiceGroup("Sort Results By:", Choice.POPUP, new String[] {//GEN-BEGIN:MVDGetInit39
                "Distance",
                "Title",
                "Rating",
                "Relevance"
            }, new Image[] {
                null,
                null,
                null,
                null
            });
            sortGroup.setSelectedFlags(new boolean[] {
                true,
                false,
                false,
                false
            });//GEN-END:MVDGetInit39
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd39
        return sortGroup;
    }//GEN-END:MVDGetEnd39
    
    /** This method returns instance for mapAllCommand component and should be called instead of accessing mapAllCommand field directly.//GEN-BEGIN:MVDGetBegin47
     * @return Instance for mapAllCommand component
     */
    public Command get_mapAllCommand() {
        if (mapAllCommand == null) {//GEN-END:MVDGetBegin47
            // Insert pre-init code here
            mapAllCommand = new Command("Map All", Command.ITEM, 1);//GEN-LINE:MVDGetInit47
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd47
        return mapAllCommand;
    }//GEN-END:MVDGetEnd47
    
    /** This method returns instance for backCommand component and should be called instead of accessing backCommand field directly.//GEN-BEGIN:MVDGetBegin49
     * @return Instance for backCommand component
     */
    public Command get_backCommand() {
        if (backCommand == null) {//GEN-END:MVDGetBegin49
            // Insert pre-init code here
            backCommand = new Command("Back", Command.BACK, 1);//GEN-LINE:MVDGetInit49
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd49
        return backCommand;
    }//GEN-END:MVDGetEnd49
    
    /** This method returns instance for callCommand component and should be called instead of accessing callCommand field directly.//GEN-BEGIN:MVDGetBegin51
     * @return Instance for callCommand component
     */
    public Command get_callCommand() {
        if (callCommand == null) {//GEN-END:MVDGetBegin51
            // Insert pre-init code here
            callCommand = new Command("Call", Command.ITEM, 1);//GEN-LINE:MVDGetInit51
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd51
        return callCommand;
    }//GEN-END:MVDGetEnd51
    
    /** This method returns instance for mapForm component and should be called instead of accessing mapForm field directly.//GEN-BEGIN:MVDGetBegin53
     * @return Instance for mapForm component
     */
    public Form get_mapForm() {
        if (mapForm == null) {//GEN-END:MVDGetBegin53
            // Insert pre-init code here
            mapForm = new Form("Map", new Item[] {get_mapImageItem()});//GEN-BEGIN:MVDGetInit53
            mapForm.addCommand(get_zoomInCommand());
            mapForm.addCommand(get_zoomOutCommand());
            mapForm.addCommand(get_mapBackCommand());
            mapForm.addCommand(get_exitCommand());
            mapForm.setCommandListener(this);//GEN-END:MVDGetInit53
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd53
        return mapForm;
    }//GEN-END:MVDGetEnd53
    
    /** This method returns instance for mapImageItem component and should be called instead of accessing mapImageItem field directly.//GEN-BEGIN:MVDGetBegin54
     * @return Instance for mapImageItem component
     */
    public ImageItem get_mapImageItem() {
        if (mapImageItem == null) {//GEN-END:MVDGetBegin54
            // Insert pre-init code here
            mapImageItem = new ImageItem("", null, ImageItem.LAYOUT_DEFAULT, null);//GEN-LINE:MVDGetInit54
            // Insert post-init code here
            mapImageItem.setLayout(Item.LAYOUT_CENTER | Item.LAYOUT_VCENTER);
        }//GEN-BEGIN:MVDGetEnd54
        return mapImageItem;
    }//GEN-END:MVDGetEnd54
    
    /** This method returns instance for mapBackCommand component and should be called instead of accessing mapBackCommand field directly.//GEN-BEGIN:MVDGetBegin55
     * @return Instance for mapBackCommand component
     */
    public Command get_mapBackCommand() {
        if (mapBackCommand == null) {//GEN-END:MVDGetBegin55
            // Insert pre-init code here
            mapBackCommand = new Command("Back", Command.BACK, 1);//GEN-LINE:MVDGetInit55
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd55
        return mapBackCommand;
    }//GEN-END:MVDGetEnd55
    
    /** This method returns instance for zoomInCommand component and should be called instead of accessing zoomInCommand field directly.//GEN-BEGIN:MVDGetBegin58
     * @return Instance for zoomInCommand component
     */
    public Command get_zoomInCommand() {
        if (zoomInCommand == null) {//GEN-END:MVDGetBegin58
            // Insert pre-init code here
            zoomInCommand = new Command("Zoom In", Command.ITEM, 1);//GEN-LINE:MVDGetInit58
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd58
        return zoomInCommand;
    }//GEN-END:MVDGetEnd58
    
    /** This method returns instance for zoomOutCommand component and should be called instead of accessing zoomOutCommand field directly.//GEN-BEGIN:MVDGetBegin60
     * @return Instance for zoomOutCommand component
     */
    public Command get_zoomOutCommand() {
        if (zoomOutCommand == null) {//GEN-END:MVDGetBegin60
            // Insert pre-init code here
            zoomOutCommand = new Command("Zoom Out", Command.ITEM, 1);//GEN-LINE:MVDGetInit60
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd60
        return zoomOutCommand;
    }//GEN-END:MVDGetEnd60
    
    /** This method returns instance for selectCommand component and should be called instead of accessing selectCommand field directly.//GEN-BEGIN:MVDGetBegin90
     * @return Instance for selectCommand component
     */
    public Command get_selectCommand() {
        if (selectCommand == null) {//GEN-END:MVDGetBegin90
            // Insert pre-init code here
            selectCommand = new Command("Select", Command.OK, 1);//GEN-LINE:MVDGetInit90
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd90
        return selectCommand;
    }//GEN-END:MVDGetEnd90
    
    /** This method returns instance for nextCommand component and should be called instead of accessing nextCommand field directly.//GEN-BEGIN:MVDGetBegin95
     * @return Instance for nextCommand component
     */
    public Command get_nextCommand() {
        if (nextCommand == null) {//GEN-END:MVDGetBegin95
            // Insert pre-init code here
            nextCommand = new Command("Next", Command.ITEM, 1);//GEN-LINE:MVDGetInit95
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd95
        return nextCommand;
    }//GEN-END:MVDGetEnd95
    
    /** This method returns instance for prevCommand component and should be called instead of accessing prevCommand field directly.//GEN-BEGIN:MVDGetBegin97
     * @return Instance for prevCommand component
     */
    public Command get_prevCommand() {
        if (prevCommand == null) {//GEN-END:MVDGetBegin97
            // Insert pre-init code here
            prevCommand = new Command("Previous", Command.ITEM, 1);//GEN-LINE:MVDGetInit97
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd97
        return prevCommand;
    }//GEN-END:MVDGetEnd97
    
    private void switchZoomLevel() {
        final Image img = (Image) imageCache.get(new Integer(mapZoomLevel));
        if (img == null) {
            getDisplay().setCurrent(get_alert());
            map();
        } else {
            get_mapImageItem().setImage(img);
            getDisplay().setCurrent(get_mapForm());
        }
    }
    
    public void startApp() {
        initialize();
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional) {
    }
    
    private void errorAlert(final String message, final Exception ex, final Displayable next) {
        final Alert alert = get_alert();
        final String exMsg = ex == null ? null : ex.getMessage();
        final String text = exMsg == null ? message : message + ": " + exMsg;
        alert.setString(text);
        
        // establish a lower bound on the alert display time
        alert.setTimeout(Math.max(3000, alert.getDefaultTimeout()));
        
        if (DEBUG) {
            System.err.println("ERROR: " + text);
        }
        getDisplay().setCurrent(alert, next);
    }
    
    private Links getSelectedLink() {
        if (selectedItem == null || links == null) {
            errorAlert("No item selected!", null, get_resultForm());
            return null;
        }
        
        final String label = selectedItem.getLabel();
        if (label == null) {
            errorAlert("No item selected!", null, get_resultForm());
            return null;
        }
        
        for (int i=0; i < links.length; i++) {
            if (label.equals(links[i].title)) {
                return links[i];
            }
        }
        
        return null;
    }
    
    private void map() {
        final Links link = getSelectedLink();
        if (link == null) {
            return;
        }

        Arg[] args = null;
        String url = null;
        
        if (!demoMode) {
            url = MAP_BASE;
            args = new Arg[] {
                new Arg("output", "json"),
                new Arg("appid", APPID),
                new Arg("latitude", link.latitude),
                new Arg("longitude", link.longitude),
                new Arg("image_height", Integer.toString(get_mapForm().getHeight())),
                new Arg("image_width", Integer.toString(get_mapForm().getWidth())),
                new Arg("zoom", Integer.toString(mapZoomLevel))
            };
        }
        else {
            String content = readResource("/res/mapDemoData.txt");
            url = Request.DEMO_URL;
            args = new Arg[] {
                new Arg("demo", content)
            };
        }

        get_alert().setString("Getting map image location...");
        Request.get(url, args, null, this, get_mapForm());
        
    }
    
    private void mapResponse(final Response response) {
        
        final Exception ex = response.getException();
        if (ex != null || response.getCode() != HttpConnection.HTTP_OK) {
            errorAlert("Error connecting to map service", ex, get_resultForm());
            return;
        }
        
        final Result result = response.getResult();
        final String location;
        try {
            location = result.getAsString("ResultSet.Result");
        } catch (ResultException rex) {
            errorAlert("Error extracting map location", rex, get_resultForm());
            return;
        }
        
        get_alert().setString("Getting map image...");
        
        Image mapImage = null;
        
        if (!demoMode) {
            final int imgResponseCode;
            final HttpConnection imgConn;
            try {
                imgConn = (HttpConnection) Connector.open(location);
                imgConn.setRequestProperty("Accept", "image/png");
                imgResponseCode = imgConn.getResponseCode();
            } catch (IOException iex) {
                errorAlert("Error downloading map image", iex, get_resultForm());
                return;
            }

            if (imgResponseCode != HttpConnection.HTTP_OK) {
                errorAlert("HTTP error downloading map image: " + imgResponseCode, null, get_resultForm());
                return;
            }


            try {
                final int totalToReceive = imgConn.getHeaderFieldInt(Arg.CONTENT_LENGTH, 0);
                final InputStream is = 
                    new ProgressInputStream(imgConn.openInputStream(), totalToReceive, this, null, 512);        
                mapImage = Image.createImage(is);
            } catch (IOException iex) {
                errorAlert("Failed to create map image", iex, get_resultForm());
                return;
            } finally {
                try { imgConn.close(); } catch (IOException ignore) {}
            }
        }
        else {
            InputStream is = null;
            try {
                is = getClass().getResourceAsStream("/res/mapimage.png");        
                mapImage = Image.createImage(is);
            } catch (IOException iex) {
                errorAlert("Failed to create map image", iex,  get_resultForm());
                return;
            }
            finally {
                try { if (is != null) { is.close(); } } catch (IOException ignore) { }
            }
        }
        
        imageCache.put(new Integer(mapZoomLevel), mapImage);
        get_mapImageItem().setImage(mapImage);
        getDisplay().setCurrent(get_mapForm());
    }
    
    private void showAllOnMap() {
        launch(mapAllLink);
    }
    
    private void showListing() {
        final Links link = getSelectedLink();
        if (link == null) {
            return;
        }
        
        launch(link.listing);
    }
    
    private void showBusiness() {
        final Links link = getSelectedLink();
        if (link == null) {
            return;
        }
        
        launch(link.business);
    }
    
    private void call() {
        final Links link = getSelectedLink();
        if (link == null) {
            return;
        }
        
        launch("tel:" + stripPhoneNumber(link.tel));
    }
    
    private void launch(final String url) {
        final boolean exitNeeded;
        try {
            if (DEBUG) {
                System.out.println("Launching Platform Request for: " + url);
            }
            exitNeeded = platformRequest(url);
            if (exitNeeded) {
                errorAlert("Please exit the MIDlet to access the page or to make the call",
                    null, resultForm);
            }
        } catch (ConnectionNotFoundException cx) {
            errorAlert(url.substring(0, url.indexOf(':')) +
                " connection not found for platformRequest", cx, resultForm);
        }
    }
    
    private String readResource(String res) {
        
        final int BUFFER_SIZE = 512;
        byte[] cbuf = new byte[BUFFER_SIZE];
        ByteArrayOutputStream bos = null;
        InputStream is = null; 
        String content = null;

        try {
            is = getClass().getResourceAsStream(res);
            bos = new ByteArrayOutputStream();
            int nread = 0;
            while ((nread = is.read(cbuf)) > 0) {
                bos.write(cbuf, 0, nread);
            }
            content = bos.toString().trim();
        }
        catch (IOException ignore) { } 
        finally {
            try {
                if (is != null) { is.close(); }
                if (bos != null) { bos.close(); }
            } 
            catch (IOException ignore) { }
        }
        
        return content;
        
    }
    
    private void search() {
        
        Arg[] args = null;
        String url = null;
        
        if (!demoMode) {
            url = LOCAL_BASE;
            args = new Arg[] {
                new Arg("output", "json"),
                new Arg("appid", APPID),
                new Arg("query", searchFor.getString()),
                new Arg("location", location.getString()),
                new Arg("sort", sortGroup.getString(sortGroup.getSelectedIndex()).toLowerCase()),
                new Arg("start", Integer.toString(start)),
                null
            };
        }
        else {
            String content = readResource("/res/resultDemoData.txt");
            url = Request.DEMO_URL;
            args = new Arg[] {
                new Arg("demo", content)
            };
        }
        
        final String str = street.getString();
        if (!"".equals(str)) {
            args[6] = new Arg("street", str);
        }
        
        get_alert().setString("Searching...");
        Request.get(url, args, null, this, get_resultForm());
    }
    
    private void searchResponse(final Response response) {
        
        final Exception ex = response.getException();
        if (ex != null || response.getCode() != HttpConnection.HTTP_OK) {
            errorAlert("Error connecting to search service - Turning on DEMO MODE", null, get_queryForm());
            demoMode = true;
            return;
        }
        
        final Result result = response.getResult();
        try {
            mapAllLink = result.getAsString("ResultSet.ResultSetMapUrl");
            totalResultsAvailable = result.getAsInteger("ResultSet.totalResultsAvailable");
            final int resultCount = result.getSizeOfArray("ResultSet.Result");
            links = new Links[resultCount];
            get_resultForm().deleteAll();
            for (int i=0; i < resultCount; i++) {
                final String title = result.getAsString("ResultSet.Result["+i+"].Title");
                final String address = result.getAsString("ResultSet.Result["+i+"].Address");
                final String phone = result.getAsString("ResultSet.Result["+i+"].Phone");
                final String distance = result.getAsString("ResultSet.Result["+i+"].Distance");
                
                links[i] = new Links();
                links[i].title = title;
                links[i].map = result.getAsString("ResultSet.Result["+i+"].MapUrl");
                links[i].listing = result.getAsString("ResultSet.Result["+i+"].ClickUrl");
                links[i].business = result.getAsString("ResultSet.Result["+i+"].BusinessClickUrl");
                links[i].tel = result.getAsString("ResultSet.Result["+i+"].Phone");
                links[i].latitude = result.getAsString("ResultSet.Result["+i+"].Latitude");
                links[i].longitude = result.getAsString("ResultSet.Result["+i+"].Longitude");
                
                final String avgRating = result.getAsString("ResultSet.Result["+i+"].Rating.AverageRating");
                
                final String displayString = 
                    address + "\n" +
                    distance + " miles" +
                    ("".equals(avgRating) ? "" : ", " + avgRating + "*") + "\n" +
                    phone;
                
                final StringItem stringItem = new StringItem(title, displayString, StringItem.PLAIN);
                stringItem.setLayout(Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_LEFT);
                stringItem.setDefaultCommand(get_selectCommand());
                stringItem.setItemCommandListener(new ItemCommandListener() {
                    public void commandAction(Command command, Item item) {
                        if (selectedItem != item) {
                            selectedItem = item;
                            imageCache.clear();
                        }
                    }
                });
                get_resultForm().append(stringItem);
            }
        } catch (ResultException rex) {
            errorAlert("Error extracting result information", rex, get_resultForm());
            return;
        }
        
        getDisplay().setCurrent(get_resultForm());
    }
    
    private String stripPhoneNumber(final String str) {
        final char[] buf = new char[str.length()];
        str.getChars(0, buf.length, buf, 0);
        final StringBuffer sbuf = new StringBuffer(buf.length);
        for (int i=0; i < buf.length; i++) {
            if (buf[i] == '(') {
            } else if (buf[i] == ')') {
            } else if (buf[i] == '-') {
            } else {
                sbuf.append(buf[i]);
            }
        }
        return sbuf.toString();
    }
    
    public void readProgress(final Object context, final int bytes, final int total) {
        get_alert().setString("Read " + bytes + (total > 0 ? "/" + total : ""));
    }
    
    public void writeProgress(final Object context, final int bytes, final int total) {
        get_alert().setString("Wrote " + bytes + (total > 0 ? "/" + total : ""));
    }
    
    public void done(final Object context, final Response response) throws Exception {

        get_alert().setString(null);
        
        if (context == mapForm) {
            mapResponse(response);
        } else if (context == resultForm) {
            searchResponse(response);
        } else {
            throw new IllegalArgumentException("unknown context returned: " + context.toString());
        }
    }
}
