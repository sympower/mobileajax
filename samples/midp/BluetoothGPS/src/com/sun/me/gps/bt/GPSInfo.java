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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.AlertType;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 *
 */
public class GPSInfo implements Runnable {
    
    public interface Listener {
        void fix(String utc, double lat, double lon, int nsat, double accuracy, double altitude);
        void failed(Throwable th);
    }
    
    private static final String STORE_NAME = "lastpos";
    
    private final InputStream is;
    
    private String gps_utc = null;
    private String gps_lat = null;
    private String gps_ns = null;
    private String gps_lon = null;
    private String gps_ew = null;
    private String gps_q = null;
    private String gps_nsat = null;
    private String gps_precision = null;
    private String gps_alt = null;

    private String utc = "";
    private double lat = 0;
    private double lon = 0;
    private int nsat = 0;
    private double accuracy = 0;
    private double alt = 0;
    
    private Listener listener = null;
    
    public GPSInfo(final Listener listener, final InputStream is) {
        this.listener = listener;
        this.is = is;
    }
    
    public void run() {
        try {
            for (String line = readLine(); line.length() > 0; line = readLine()) {
                if (parse(line)) {
                    try {
                        listener.fix(getUTC(), getLatitude(), getLongitude(), 
                            getSatelliteCount(), getAccuracy(), getAltitude());
                    } catch (Throwable th) {
                        try { listener.failed(th); } catch (Throwable ignore) {}
                    }
                }
            }
        } catch (Throwable th) {
            try { listener.failed(th); } catch (Throwable ignore) {}
        } finally {
            try { is.close(); } catch (IOException ignore) {}
        }
    }
    
    private String readLine() throws IOException {
        final StringBuffer sb = new StringBuffer();
        for (int c = is.read(); c >= 0 && c != '\n'; c = is.read()) {
            sb.append((char) c);
        }
        return sb.toString();
    }
    
    private boolean parse(final String gps_line) {
        
        if (gps_line == null) return false;
        if (!gps_line.startsWith("$GPGGA")) return false;
        
        final int utcPos = gps_line.indexOf(",", 0);
        if (utcPos < 0) return false;
        
        final int latPos = gps_line.indexOf(",", utcPos + 1);
        if (latPos < 0) return false;
        
        final int nsPos = gps_line.indexOf(",", latPos + 1);
        if (nsPos < 0) return false;
        
        final int lonPos = gps_line.indexOf(",", nsPos + 1);
        if (lonPos < 0) return false;
        
        final int ewPos = gps_line.indexOf(",", lonPos + 1);
        if (ewPos < 0) return false;
        
        final int qPos = gps_line.indexOf(",", ewPos + 1);
        if (qPos < 0) return false;
        
        final int nsatPos = gps_line.indexOf(",", qPos + 1);
        if (nsatPos < 0) return false;
        
        final int precisionPos = gps_line.indexOf(",", nsatPos + 1);
        if (precisionPos < 0) return false;
        
        final int altPos = gps_line.indexOf(",", precisionPos + 1);
        if (altPos < 0) return false;
        
        final int mPos = gps_line.indexOf(",", altPos + 1);
        if (mPos < 0) return false;
        
        gps_utc = gps_line.substring(utcPos + 1, latPos);
        gps_lat = gps_line.substring(latPos + 1, nsPos);
        gps_ns = gps_line.substring(nsPos + 1, lonPos);
        gps_lon = gps_line.substring(lonPos + 1, ewPos);
        gps_ew = gps_line.substring(ewPos + 1, qPos);
        gps_q = gps_line.substring(qPos + 1, nsatPos);
        gps_nsat = gps_line.substring(nsatPos + 1, precisionPos);
        gps_precision = gps_line.substring(precisionPos + 1, altPos);
        gps_alt = gps_line.substring(altPos + 1, mPos);

        utc = gps_utc.substring(0, 2)  + ":" + gps_utc.substring(2, 4) + ":" + gps_utc.substring(4, gps_utc.length());
        try { lat = coords(gps_lat, gps_ns); } catch (NumberFormatException ignore) {}
        try { lon = coords(gps_lon, gps_ew); } catch (NumberFormatException ignore) {}
        try { nsat = Integer.parseInt(gps_nsat); } catch (NumberFormatException ignore) {}
        try { accuracy = Double.parseDouble(gps_precision); } catch (NumberFormatException ignore) {}
        try { alt = Double.parseDouble(gps_alt); } catch (NumberFormatException ignore) {}

        return true;
    }
    
    private static double coords(final String raw, final String hemi) throws NumberFormatException {
        final int dotPos = raw.indexOf(".");
        if (dotPos < 0) return Double.NaN;
        
        // convert from DDMM.mmmm to DD.dddddd, for example, 3724.4637 to 37.407808
        final double dd = Double.parseDouble(raw.substring(dotPos - 2)) / 60.0;
        final double degrees = Double.parseDouble(raw.substring(0, dotPos - 2)) + dd;
        return "W".equals(hemi) || "S".equals(hemi) ? -degrees : degrees;
    }
    
    private boolean saveLastFix(final String gps_line) {
        RecordStore store = null;
        try {
            store = RecordStore.openRecordStore(STORE_NAME, true);
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final DataOutput dos = new DataOutputStream(bos);
            dos.writeUTF(gps_line);
            final byte[] bits = bos.toByteArray();
            final RecordEnumeration en = store.enumerateRecords(null, null, false);
            if (en.hasNextElement()) {
                store.setRecord(en.nextRecordId(), bits, 0, bits.length);
            } else {
                store.addRecord(bits, 0, bits.length);
            }
            return true;
        } catch (RecordStoreException rse) {
            return false;
        } catch (IOException ie) {
            return false;
        } finally {
            if (store != null) { try { store.closeRecordStore(); } catch (Exception ignore) {}}
        }
    }
    
    public String readLastFix() {
        RecordStore store = null;
        try {
            store = RecordStore.openRecordStore(STORE_NAME, false);
            final RecordEnumeration en = store.enumerateRecords(null, null, false);
            if (!en.hasNextElement()) {
                return null;
            }
            final ByteArrayInputStream bis = new ByteArrayInputStream(en.nextRecord());
            final DataInputStream dis = new DataInputStream(bis);
            return dis.readUTF();
        } catch (RecordStoreException rse) {
            return null;
        } catch (IOException ie) {
            return null;
        } finally {
            if (store != null) { try { store.closeRecordStore(); } catch (Exception ignore) {}}
        }
    }
    
    public String getUTC() {
        return utc;
    }
    
    public double getLatitude() {
        return lat;
    }
    
    public double getLongitude() {
        return lon;
    }
    
    public int getSatelliteCount() {
        return nsat;
    }
    
    public double getAccuracy() {
        return accuracy;
    }
    
    public double getAltitude() {
        return alt;
    }
}
