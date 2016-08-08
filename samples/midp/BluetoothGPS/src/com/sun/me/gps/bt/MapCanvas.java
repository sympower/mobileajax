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

import com.sun.me.web.path.Result;
import com.sun.me.web.path.ResultException;
import com.sun.me.web.request.Arg;
import com.sun.me.web.request.ProgressInputStream;
import com.sun.me.web.request.ProgressListener;
import com.sun.me.web.request.Request;
import com.sun.me.web.request.RequestListener;
import com.sun.me.web.request.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 */
public class MapCanvas extends Canvas implements GPSInfo.Listener {

    private static final int MAX_MAP_ZOOM = 12;
    private static final int DEFAULT_MAP_ZOOM = 6;
    private static final int MIN_MAP_ZOOM = 1;    

    private static final String MAP_BASE = "http://api.local.yahoo.com/MapsService/V1/mapImage";

    /* TODO: Get own APP ID for Yahoo Maps APIs */
    private static final String MAP_APPID = "";

    private int mapZoomLevel = DEFAULT_MAP_ZOOM;
    
    private MainMidlet midlet = null;
    private Display display = null;
    private Font font = null;
    private Image mapImage = null;
    private int textHeight = 0;
    private int width = 0;
    private int height = 0;
    
    private String utc;
    private double lat;
    private double lon;
    private int nsat;
    private double accuracy;
    private double altitude;

    private int percent = 0;
    private String status = "";
    
    public MapCanvas(final MainMidlet midlet) {
        this.midlet = midlet;
        display = Display.getDisplay(midlet);
        font = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        textHeight = font.getHeight();
        setFullScreenMode(true);
    }
    
    public void fix(String utc, double lat, double lon, int nsat, double accuracy, double altitude) {
        this.utc = utc;
        this.lat = lat;
        this.lon = lon;
        this.nsat = nsat;
        this.accuracy = accuracy;
        this.altitude = altitude;
        
        status = Integer.toString(nsat) + " sat " + Float.toString((float) accuracy) + "m";
        
        if (display.getCurrent() != this) {
            display.setCurrent(this);
        }
        
        if (mapImage == null) {
            map();
        }
        
        repaint();
    }

    public void failed(Throwable th) {
        midlet.alert(AlertType.ERROR, null, th);
    }
    
    public void paint(Graphics g) {
        
        // erase background
        g.setColor(255, 255, 255);
        g.fillRect(0, 0, width, textHeight);
        g.setColor(0, 0, 0);
        
        if (mapImage != null) {
            g.drawImage(mapImage, 0, 0, Graphics.TOP | Graphics.LEFT);
        }
        
        g.drawString(status, width - font.stringWidth(status) - font.stringWidth("mm"), 0, Graphics.TOP | Graphics.LEFT);
    }
    
    protected void showNotify() {
        width = getWidth();
        height = getHeight();
    }
    
    protected void sizeChanged(int w, int h) {
        width = w;
        height = h;
    }
    
    protected void keyPressed(int key) {
        switch (key) {
            case Canvas.KEY_NUM5: 
                mapZoomLevel = DEFAULT_MAP_ZOOM;
                map(); 
                break;
                
            case Canvas.KEY_NUM0:
                midlet.exitMIDlet();
                map();
                break;
                
            case Canvas.KEY_NUM2:
                mapZoomLevel = Math.min(++mapZoomLevel, MAX_MAP_ZOOM);
                map();
                break;
                
            case Canvas.KEY_NUM8:
                mapZoomLevel = Math.max(--mapZoomLevel, MIN_MAP_ZOOM);
                map();
                break;
        }
    }

    private void map() {
        final Arg[] args = {
            new Arg("output", "json"),
            new Arg("appid", MAP_APPID),
            new Arg("latitude", Float.toString((float) lat)),
            new Arg("longitude", Float.toString((float) lon)),
            new Arg("image_height", Integer.toString(height)),
            new Arg("image_width", Integer.toString(width)),
            new Arg("zoom", Integer.toString(mapZoomLevel))
        };
        
        Request.get(MAP_BASE, args, null, new RequestListener() {
            public void done(Object context, Response response) throws Exception {
                mapResponse(response);
            }
            public void readProgress(Object context, int bytes, int total) {
                // TODO: draw map loading animation on some corner of the canvas
            }
            public void writeProgress(Object context, int bytes, int total) {
            }
        }, null);
    }
    
    private void mapResponse(final Response response) {
        
        final Exception ex = response.getException();
        final int code = response.getCode();
        if (ex != null || code != HttpConnection.HTTP_OK) {
            midlet.alert(AlertType.ERROR, "Error connecting to map service: " + code, ex);
            return;
        }
        
        final Result result = response.getResult();
        final String location;
        try {
            location = result.getAsString("ResultSet.Result");
        } catch (ResultException rex) {
            midlet.alert(AlertType.ERROR, "Error extracting map location", rex);
            return;
        }
        
        final int imgResponseCode;
        final HttpConnection imgConn;
        try {
            imgConn = (HttpConnection) Connector.open(location);
            imgConn.setRequestProperty("Accept", "image/png");
            imgResponseCode = imgConn.getResponseCode();
        } catch (IOException iex) {
            midlet.alert(AlertType.ERROR, "Error downloading map image", iex);
            return;
        }
        
        if (imgResponseCode != HttpConnection.HTTP_OK) {
            midlet.alert(AlertType.ERROR, "HTTP error downloading map image: " + imgResponseCode, null);
            return;
        }
        
        try {
            final int totalToReceive = imgConn.getHeaderFieldInt(Arg.CONTENT_LENGTH, 0);
            final InputStream is = 
                new ProgressInputStream(imgConn.openInputStream(), totalToReceive, new ProgressListener() {
                public void readProgress(Object context, int bytes, int total) {
                }
                public void writeProgress(Object context, int bytes, int total) {
                }
            }, null, 1024);        
            mapImage = Image.createImage(is);
        } catch (IOException iex) {
            midlet.alert(AlertType.ERROR, "Failed to create map image", iex);
            return;
        } finally {
            try { imgConn.close(); } catch (IOException ignore) {}
        }
    }
}
