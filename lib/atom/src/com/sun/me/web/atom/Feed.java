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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;

public class Feed extends Common {
    
    public static final String ATOM_CONTENT_TYPE = "application/atom+xml";
    public static final String DEFAULT_CHARSET = "utf-8";
    
    Vector authors = new Vector();
    Vector categories = new Vector();
    Vector contributors = new Vector();
    Generator generator = null;
    Icon icon = null;
    Id id = null;
    Vector links = new Vector();
    Logo logo = null;
    Text rights = null;
    Text subtitle = null;
    Text title = null;
    Date updated = null;
    
    private String mediaPostErrorMessage = null;
    private FeedEntry mediaEntry = null;
    
    Feed() {
        super();
    }
    
    Feed(final Attributes attrs, final Feed parent) {
        super(attrs, parent);
    }
    
    public Person[] getAuthors() {
        final Person[] persons = new Person[authors.size()];
        for (int i = 0; i < persons.length; i++) {
            persons[i] = (Person) authors.elementAt(i);
        }
        return persons;
    }
    
    public Category[] getCategories() {
        final Category[] cat = new Category[categories.size()];
        for (int i = 0; i < cat.length; i++) {
            cat[i] = (Category) categories.elementAt(i);
        }
        return cat;
    }
    
    public Generator[] getContributors() {
        final Generator[] persons = new Generator[contributors.size()];
        for (int i = 0; i < persons.length; i++) {
            persons[i] = (Generator) contributors.elementAt(i);
        }
        return persons;
    }
    
    public Generator getGenerator() {
        return generator;
    }
    
    public Icon getIcon() {
        return icon;
    }
    
    public Id getId() {
        return id;
    }
    
    public Link[] getLinks() {
        final Link[] linkArray = new Link[links.size()];
        for (int i = 0; i < linkArray.length; i++) {
            linkArray[i] = (Link) links.elementAt(i);
        }
        return linkArray;
    }
    
    public Logo getLogo() {
        return logo;
    }
    
    public Text getRights() {
        return rights;
    }
    
    public Text getSubTitle() {
        return subtitle;
    }
    
    public Text getTitle() {
        return title;
    }
    
    public String getTitleAsString() {
        return title == null ? null : title.getText();
    }
    
    public Date getUpdated() {
        return updated;
    }
    
    public String getSelfURL() {
        final int nlinks = links.size();
        for (int i = 0; i < nlinks; i++) {
            final Link link = (Link) links.elementAt(i);
            if (Relation.SELF.equals(link.rel)) {
                return link.accumulateBaseURI() + link.href;
            }
        }
        return null;
    }
    
    private String extractMediaURL(final InputStream is) {
        final FeedHandler handler = FeedHandler.createInstance(new FeedListener() {
            public void characters(char[] ch, int start, int length) {}
            public void endElement(String uri, String localName, String qName) {}
            public void endFeed(Feed feed) {}
            public void endFeedEntry(FeedEntry feedEntry) { mediaEntry = feedEntry; }
            public void endPrefixMapping(String prefix) {}
            public void errorProcessingFeed(String message) { mediaPostErrorMessage = message; }
            public void startElement(String uri, String localName, String qName, Attributes attrs) {}
            public void startFeed(Feed feed) {}
            public void startFeedEntry(FeedEntry feedEntry) {}
            public void startPrefixMapping(String prefix, String uri) {}
        }, 1, -1);
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            final SAXParser parser = factory.newSAXParser();
            parser.parse(is, handler);
            return mediaEntry == null ? mediaPostErrorMessage :
                mediaEntry.content == null ? mediaPostErrorMessage :
                    mediaEntry.content.src == null ? mediaPostErrorMessage :
                        mediaEntry.content.src;
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
    
    public String postMedia(final byte[] data, final String contentType, final PublishInfo pub) throws IOException {
        final HttpConnection conn = (HttpConnection) Connector.open(pub.getMediaCollectionLocation());
        conn.setRequestMethod(HttpConnection.POST);
        conn.setRequestProperty("Authorization", "Basic " +
            BasicAuth.encode(pub.getUser(), pub.getPassword()));
        conn.setRequestProperty("Content-Type", contentType);
        final OutputStream os = conn.openOutputStream();
        os.write(data);
        final int responseCode = conn.getResponseCode();
        if (responseCode == HttpConnection.HTTP_CREATED) {
            return extractMediaURL(conn.openInputStream());
        } else {
            throw new IOException("HTTP " + responseCode);
        }
    }
    
    public int createEntry(final FeedEntry entry, final PublishInfo pub) throws IOException {
        final HttpConnection conn = (HttpConnection) Connector.open(pub.getEntryCollectionLocation());
        conn.setRequestMethod(HttpConnection.POST);
        conn.setRequestProperty("Authorization", "Basic " +
            BasicAuth.encode(pub.getUser(), pub.getPassword()));
        conn.setRequestProperty("Content-Type", ATOM_CONTENT_TYPE + ";charset=" + DEFAULT_CHARSET);
        final OutputStream os = conn.openOutputStream();
        os.write(entry.toXML().getBytes(DEFAULT_CHARSET));
        return conn.getResponseCode();
    }
    
    public int updateEntry(final FeedEntry entry, final PublishInfo pub) throws IOException {
        final HttpConnection conn = (HttpConnection) Connector.open(getSelfURL());
        // TODO: verify if PUT is implemented
        conn.setRequestMethod("PUT");
        return conn.getResponseCode();
    }
    
    public int deleteEntry(final FeedEntry entry, final PublishInfo pub) throws IOException {
        final HttpConnection conn = (HttpConnection) Connector.open(getSelfURL());
        // TODO: verify if DELETE is implemented
        conn.setRequestMethod("DELETE");
        return conn.getResponseCode();
    }
    
    public void read(final DataInput in) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        super.read(in);
        // TODO: it is probably not necessary to persist everything in a real app
        // only the elements actually being used could be persisted, saving memory
        authors = (Vector) StoreHelper.read(in);
        categories = (Vector) StoreHelper.read(in);
        contributors = (Vector) StoreHelper.read(in);
        generator = (Generator) StoreHelper.read(in);
        icon = (Icon) StoreHelper.read(in);
        id = (Id) StoreHelper.read(in);
        links = (Vector) StoreHelper.read(in);
        logo = (Logo) StoreHelper.read(in);
        rights = (Text) StoreHelper.read(in);
        subtitle = (Text) StoreHelper.read(in);
        title = (Text) StoreHelper.read(in);
        updated = (Date) StoreHelper.read(in);
    }
    
    public void write(final DataOutput out) throws IOException {
        super.write(out);
        // TODO: it is probably not necessary to persist everything in a real app
        // only the elements actually being used could be persisted, saving memory
        StoreHelper.write(out, authors);
        StoreHelper.write(out, categories);
        StoreHelper.write(out, contributors);
        StoreHelper.write(out, generator);
        StoreHelper.write(out, icon);
        StoreHelper.write(out, id);
        StoreHelper.write(out, links);
        StoreHelper.write(out, logo);
        StoreHelper.write(out, rights);
        StoreHelper.write(out, subtitle);
        StoreHelper.write(out, title);
        StoreHelper.write(out, updated);
    }
}
