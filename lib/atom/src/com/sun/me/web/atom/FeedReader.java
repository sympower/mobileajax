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

package com.sun.me.web.atom;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class FeedReader extends DefaultHandler {
    
    private static final String ETAG = "ETag";
    private static final String IF_MODIFIED_SINCE = "If-Modified-Since";
    private static final String IF_NONE_MATCH = "If-None-Match";
    
    private final String feedURL;
    private final FeedListener listener;
    private final int maxFeedEntries;
    private final int maxChars;
    
    private FeedHandler handler = null;
    private SAXParserFactory factory = null;
    private SAXParser parser = null;
    private Subscription sub = null;
    
    public static FeedReader createInstance(
            final String feedURL,
            final FeedListener listener,
            final int maxFeedEntries,
            final int maxChars) {
        return new FeedReader(feedURL, listener, maxFeedEntries, maxChars);
    }
    
    private FeedReader(final String feedURL, final FeedListener listener,
            final int maxFeedEntries, final int maxChars) {
        this.feedURL = feedURL;
        this.listener = listener;
        this.maxFeedEntries = maxFeedEntries;
        this.maxChars = maxChars;
    }
    
    public void initiate(final Subscription sub) throws ParserConfigurationException, SAXException {
        handler = FeedHandler.createInstance(listener, maxFeedEntries, maxChars);
        factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        parser = factory.newSAXParser();
        this.sub = sub;
    }
    
    public boolean read() throws IOException, SAXException, StopParsingException {
        final HttpConnection conn = (HttpConnection) Connector.open(feedURL);

        final Date lastUpdated = sub.getLastUpdated();
        if (lastUpdated != null) {
            final String httpDate = com.sun.me.web.atom.Date.toHttpString(lastUpdated);
            conn.setRequestProperty(IF_MODIFIED_SINCE, httpDate);
        }
        final String etag = sub.getETag();
        if (etag != null) {
            conn.setRequestProperty(IF_NONE_MATCH, etag);
        }
        
        final int responseCode = conn.getResponseCode();
        if (responseCode == HttpConnection.HTTP_NOT_MODIFIED) {
            return false;
        }
        
        final long lastModified = conn.getLastModified();
        if (lastModified != 0) {
            sub.setLastUpdated(new Date(lastModified));
        }
        sub.setETag(conn.getHeaderField(ETAG));

        final InputStream is = conn.openInputStream();
        parser.parse(is, handler);
        return true;
    }
    
    public void cancel() {
        handler.cancel();
    }
}
