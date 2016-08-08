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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import com.sun.me.web.atom.Feed;
import com.sun.me.web.atom.FeedEntry;
import com.sun.me.web.atom.Storable;
import com.sun.me.web.atom.Subscription;

public final class Store {
    
    private static final boolean DEBUG = true;
    
    public static final int MAX_STORE_NAME_LENGTH = 32;
    
    // db names
    public static final String SUBSCRIPTIONS = "subs";
    public static final String FEED = "feed";
    public static final String ENTRIES = "entries";
    
    private static String feedStoreName(final String feedURL) {
        return FEED + (feedURL == null ? "" : Integer.toHexString(feedURL.hashCode()));
    }
    
    private static String feedEntriesStoreName(final String feedURL) {
        return ENTRIES + (feedURL == null ? "" : Integer.toHexString(feedURL.hashCode()));
    }
    
    public static Subscription[] getSubscriptions()
    throws RecordStoreException, IOException,
            ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        Subscription[] subs = new Subscription[getSize(SUBSCRIPTIONS)];
        Store.get(SUBSCRIPTIONS, Subscription.class, subs);

//#mdebug
//#         // in the emulator, the subscription entries come back in the reverse order
//#         final Subscription[] reversed = new Subscription[subs.length];
//#         for (int j = 0, i = subs.length - 1; i >= 0; i--) {
//#             reversed[j++] = subs[i];
//#         }
//#         subs = reversed;
//#enddebug
        
        return subs;
    }
    
    public static Feed getFeed(final String feedURL)
    throws RecordStoreException, IOException,
            ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        final Feed[] feeds = new Feed[1];
        Store.get(feedStoreName(feedURL), Feed.class, feeds);
        return feeds[0];
    }
    
    public static FeedEntry[] getFeedEntries(final String feedURL)
    throws RecordStoreException, IOException,
            ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        final String entryStoreName = feedEntriesStoreName(feedURL);
        FeedEntry[] feedEntries = new FeedEntry[getSize(entryStoreName)];
        Store.get(entryStoreName, FeedEntry.class, feedEntries);

//#mdebug
//#         // in the emulator, the entries come back in the reverse order
//#         final FeedEntry[] reversed = new FeedEntry[feedEntries.length];
//#         for (int j = 0, i = feedEntries.length - 1; i >= 0; i--) {
//#             reversed[j++] = feedEntries[i];
//#         }
//#         feedEntries = reversed;
//#enddebug

        return feedEntries;
    }
    
    private static void get(final String storeName, final Class type, final Storable[] items)
    throws RecordStoreException, IOException,
            ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        RecordStore store = null;
        try {
            store = RecordStore.openRecordStore(storeName, false);
            doGet(store, type, items);
        } finally {
            if (store != null) { store.closeRecordStore(); }
        }
    }
    
    public static int getSize(final String storeName) throws RecordStoreException {
        RecordStore store = null;
        try {
            store = RecordStore.openRecordStore(storeName, false);
            return doGetSize(store);
        } finally {
            if (store != null) { store.closeRecordStore(); }
        }
    }
    
    public static void deleteFeed(final String feedURL) throws RecordStoreException {
        RecordStore.deleteRecordStore(feedStoreName(feedURL));
    }
    
    public static void deleteFeedEntries(final String feedURL) throws RecordStoreException {
        RecordStore.deleteRecordStore(feedEntriesStoreName(feedURL));
    }
    
    public static void store(final Subscription[] subs) throws RecordStoreException, IOException {
        store(SUBSCRIPTIONS, subs, false);
    }
    
    public static void store(final String feedURL, final Feed feed) throws RecordStoreException, IOException {
        final Feed[] feeds = { feed };
        store(feedStoreName(feedURL), feeds, false);
    }
    
    public static void store(final String feedURL, final FeedEntry feedEntry) throws RecordStoreException, IOException {
        final FeedEntry[] feedEntries = { feedEntry };
        store(feedEntriesStoreName(feedURL), feedEntries, true);
    }
    
    private static void store(final String storeName, final Storable[] list, final boolean append) 
    throws RecordStoreException, IOException {
        
        if (!append) {
            try {
                RecordStore.deleteRecordStore(storeName);
            } catch (RecordStoreNotFoundException nfx) {
                // ignore
            }
        }
        
        RecordStore store = null;
        try {
            store = RecordStore.openRecordStore(storeName, true);
            doStore(store, list);
        } finally {
            if (store != null) { store.closeRecordStore(); }
        }
    }
    
    private static void doGet(final RecordStore store, final Class type, final Storable[] items)
    throws RecordStoreException, IOException,
            ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        if (!Storable.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("Desired class must implement Storable: " +
                    type.getName());
        }
        
        int i=0;
        final RecordEnumeration en = store.enumerateRecords(null, null, false);
        while (en.hasNextElement() && i < items.length) {
            final byte[] bits = en.nextRecord();
            final ByteArrayInputStream bis = new ByteArrayInputStream(bits);
            final DataInputStream dis = new DataInputStream(bis);
            final Storable obj = (Storable) type.newInstance();
            obj.read(dis);
            items[i++] = obj;
        }
    }
    
    private static int doGetSize(final RecordStore store) throws RecordStoreException {
        return store.getNumRecords();
    }
    
    private static void doStore(final RecordStore store, final Storable[] list)
    throws RecordStoreException, IOException {
        
        for (int i=0; i < list.length; i++) {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final DataOutputStream dos = new DataOutputStream(bos);
            if (list[i] != null) {
                list[i].write(dos);
            }
            final byte[] bits = bos.toByteArray();
            store.addRecord(bits, 0, bits.length);
        }
    }
}
