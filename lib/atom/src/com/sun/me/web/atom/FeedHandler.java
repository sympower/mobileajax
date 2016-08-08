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

import java.util.Hashtable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public final class FeedHandler extends DefaultHandler {
    
    private static final int DEFAULT_MAX_CHARS = 64 * 1024;
    
    private static final String ENTRY_LIMIT_EXCEPTION_MESSAGE =
        "Reached entry limit, stopping.";
    
    private static final String EMPTY = "";
    private static final String ATOM_NS = "http://www.w3.org/2005/Atom";
    private static final String XHTML_NS = "http://www.w3.org/1999/xhtml";
    
    // supported RSS 2.0 tags - see
    // http://www.tbray.org/ongoing/When/200x/2005/07/27/Atomic-RSS
    private static final String RSS = "rss";
    private static final String CHANNEL = "channel";
    private static final String DESCRIPTION = "description";
    private static final String COPYRIGHT = "copyright";
    private static final String ITEM = "item";
    private static final String LAST_BUILD_DATE = "lastBuildDate";
    private static final String PUB_DATE = "pubDate";
    private static final String GUID = "guid";
    private static final String ENCLOSURE = "enclosure";
    private static final String URL = "url";
    
    // skipped RSS tags
    private static final String IMAGE = "image";
    
    private static final String CREATOR = "creator";
    private static final String DC_NS = "http://purl.org/dc/elements/1.1/";
    
    private static final String FEED = "feed";
    private static final String TITLE = "title";
    private static final String SUBTITLE = "subtitle";
    private static final String RIGHTS = "rights";
    private static final String ID = "id";
    private static final String ENTRY = "entry";
    private static final String SUMMARY = "summary";
    private static final String CONTENT = "content";
    private static final String SOURCE = "source";
    
    private static final String AUTHOR = "author";
    private static final String CONTRIBUTOR = "contributor";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String URI = "uri";
    
    private static final String LINK = "link";
    
    private static final String LOGO = "logo";
    private static final String ICON = "icon";
    private static final String UPDATED = "updated";
    private static final String PUBLISHED = "published";
    private static final String GENERATOR = "generator";
    
    private static final String CATEGORY = "category";
    
    private final FeedListener listener;
    private final int maxFeedEntries;
    private final int maxChars;
    
    private Feed feed;
    private Feed source;
    private FeedEntry entry;
    private Content content;
    private Person person;
    private Generator generator;
    private Link link;
    private Date date;
    private Category category;
    private Icon icon;
    private Id id;
    private Logo logo;
    private StringBuffer chars;
    private Hashtable namespaces = new Hashtable();
    
    private boolean entryMode;
    private boolean contentMode;
    private boolean personMode;
    private boolean generatorMode;
    private boolean linkMode;
    private boolean dateMode;
    private boolean categoryMode;
    private boolean iconMode;
    private boolean idMode;
    private boolean logoMode;
    private boolean sourceMode;
    private boolean imageMode;
    
    private int extElement;
    private int entryCount;
    private boolean done;
    
    public static FeedHandler createInstance(final FeedListener listener,
        final int maxFeedEntries, final int maxChars) {
        return new FeedHandler(listener, maxFeedEntries, maxChars);
    }
    
    public void cancel() {
        done = true;
    }
    
    private FeedHandler(final FeedListener listener,
        final int maxFeedEntries, final int maxChars) {
        this.listener = listener;
        this.maxFeedEntries = maxFeedEntries;
        this.maxChars = maxChars > 0 ? maxChars : DEFAULT_MAX_CHARS;
    }
    
    private String readAccumulatedChars() {
        // truncate, if necessary
        if (chars.length() > maxChars) {
            chars.setLength(maxChars);
        }
        final String ch = chars.toString().trim();
        clearAccumulatedChars();
        return ch;
    }
    
    private void clearAccumulatedChars() {
        // the following causes OOM errors
        // chars.delete(0, maxChars);
        // it would be nice to have a chars.reset() or chars.clear()
        chars = new StringBuffer(maxChars);
    }
    
    public void startDocument() throws SAXException {
        extElement = 0;
        entryCount = 0;
        done = false;
        entryMode = false;
        contentMode = false;
        personMode = false;
        generatorMode = false;
        linkMode = false;
        dateMode = false;
        categoryMode = false;
        iconMode = false;
        logoMode = false;
        sourceMode = false;
        imageMode = false;
        chars = new StringBuffer(maxChars);
    }
    
    public void endDocument() throws SAXException {
    }
    
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        namespaces.put(prefix, uri);
        listener.startPrefixMapping(prefix, uri);

//#mdebug debug
//#         if (EMPTY.equals(prefix)) {
//#             System.out.println("xmlns=" + uri);
//#         } else {
//#             System.out.println("xmlns " + prefix + "=" + uri);
//#         }
//#enddebug
    }
    
    public void endPrefixMapping(final String prefix) throws SAXException {
        namespaces.remove(prefix);
        listener.endPrefixMapping(prefix);
    }
    
    private boolean isInNamespace(final String uri, final String ns) {
        return ns.equals(uri) || (EMPTY.equals(uri) && ns.equals(namespaces.get(EMPTY)));
    }
    
    public void startElement(final String uri, final String localName,
        final String qName, final Attributes attrs)
        throws SAXException {
        
//#mdebug debug
//#         System.out.print("<" + localName);
//#         for (int i = attrs.getLength() - 1; i >= 0; i--) {
//#             System.out.print(" " + attrs.getLocalName(i) + "=\"" + attrs.getValue(i) + "\"");
//#         }
//#         System.out.print(">");
//#enddebug
        
        final boolean isAtom = isInNamespace(uri, ATOM_NS);
        final boolean isRSS = EMPTY.equals(uri) && namespaces.get(EMPTY) == null;
        final boolean isXHTML = isInNamespace(uri, XHTML_NS);
        final boolean isForeign = !(isAtom || isRSS);
        if (isXHTML) {
            return;
        } else if (isForeign) {
            clearAccumulatedChars();
            listener.startElement(uri, localName, qName, attrs);
            extElement++;
            return;
        }
        
        final Feed feedRef = entryMode ? (sourceMode ? source : entry) : feed;
        
        if (FEED.equals(localName) || CHANNEL.equals(localName)) {
            feed = new Feed(attrs, null);
            listener.startFeed(feed);
        } else if (ENTRY.equals(localName) || ITEM.equals(localName)) {
            entry = new FeedEntry(attrs, feed);
            entryMode = true;
            listener.startFeedEntry(entry);
        } else if (entryMode && SOURCE.equals(localName)) {
            source = new Feed(attrs, entry);
            sourceMode = true;
        } else if (LINK.equals(localName) || ENCLOSURE.equals(localName)) {
            link = new Link(attrs, feedRef);
            linkMode = true;
            if (ENCLOSURE.equals(localName)) {
                link.href = attrs.getValue(URL);
            }
        } else if (TITLE.equals(localName) ||
            SUBTITLE.equals(localName) ||
            RIGHTS.equals(localName) ||
            COPYRIGHT.equals(localName) ||
            SUMMARY.equals(localName) ||
            CONTENT.equals(localName) ||
            DESCRIPTION.equals(localName)) {
            content = new Content(attrs, feedRef);
            contentMode = true;
        } else if (AUTHOR.equals(localName) || CONTRIBUTOR.equals(localName) ||
            (CREATOR.equals(localName) && DC_NS.equals(uri))) {
            person = new Person(attrs, feedRef);
            personMode = true;
        } else if (GENERATOR.equals(localName)) {
            generator = new Generator(attrs, feedRef);
            generatorMode = true;
        } else if (UPDATED.equals(localName) || PUBLISHED.equals(localName) ||
            LAST_BUILD_DATE.equals(localName) || PUB_DATE.equals(localName)) {
            date = new Date(attrs, feedRef);
            dateMode = true;
        } else if (CATEGORY.equals(localName)) {
            category = new Category(attrs, feedRef);
            categoryMode = true;
        } else if (ICON.equals(localName)) {
            icon = new Icon(attrs, feedRef);
            iconMode = true;
        } else if (ID.equals(localName) || GUID.equals(localName)) {
            id = new Id(attrs, feedRef);
            idMode = true;
        } else if (LOGO.equals(localName)) {
            logo = new Logo(attrs, feedRef);
            logoMode = true;
        } else if (IMAGE.equals(localName)) {
            imageMode = true;
        }
    }
    
    public void endElement(final String uri, final String localName, final String qName)
    throws SAXException {
        
//#mdebug debug
//#         System.out.println("</" + localName + ">");
//#enddebug
        
        final boolean isAtom = isInNamespace(uri, ATOM_NS);
        final boolean isRSS = EMPTY.equals(uri) && namespaces.get(EMPTY) == null;
        final boolean isXHTML = isInNamespace(uri, XHTML_NS);
        final boolean isForeign = !(isAtom || isRSS);
        if (isXHTML) {
            return;
        } else if (isForeign) {
            listener.endElement(uri, localName, qName);
            extElement--;
        } else {
            handleEndElement(uri, localName);
        }
        clearAccumulatedChars();
    }
    
    private void handleEndElement(final String uri, final String localName) throws StopParsingException {
        
        final Feed feedRef = entryMode ? (sourceMode ? source : entry) : feed;
        if (feedRef == null) {
            throw new StopParsingException("Invalid feed");
        }
        
        if (contentMode &&
            TITLE.equals(localName) ||
            SUBTITLE.equals(localName) ||
            RIGHTS.equals(localName) ||
            COPYRIGHT.equals(localName) ||
            SUMMARY.equals(localName) ||
            CONTENT.equals(localName) ||
            DESCRIPTION.equals(localName)) {
            content.text = readAccumulatedChars();
            contentMode = false;
        }
        
        if (!entryMode && (FEED.equals(localName) || CHANNEL.equals(localName))) {
            listener.endFeed(feed);
        } else if (entryMode && (ENTRY.equals(localName) || ITEM.equals(localName))) {
            listener.endFeedEntry(entry);
            entryMode = false;
            if (++entryCount > maxFeedEntries) {
                // TODO: maybe call truncatedFeed() or endFeed(truncated=true) in this case?
                listener.endFeed(feed);
                done = true;
                // thrown to force the parser to stop
                throw new StopParsingException(ENTRY_LIMIT_EXCEPTION_MESSAGE);
            }
        } else if (entryMode && sourceMode && SOURCE.equals(localName)) {
            entry.source = source;
            sourceMode = false;
        } else if (TITLE.equals(localName)) {
            // RSS feed might have an image title for the feed, don't want it overwriting the feed title
            if (!imageMode) {
                feedRef.title = content;
            }
        } else if (SUBTITLE.equals(localName) || DESCRIPTION.equals(localName)) {
            feedRef.subtitle = content;
        } else if (RIGHTS.equals(localName) || COPYRIGHT.equals(localName)) {
            feedRef.rights = content;
        } else if (SUMMARY.equals(localName)) {
            entry.summary = content;
        } else if (CONTENT.equals(localName)) {
            entry.content = content;
        } else if (idMode && (ID.equals(localName) || GUID.equals(localName))) {
            id.uri = readAccumulatedChars();
            feedRef.id = id;
            idMode = false;
        } else if (iconMode && ICON.equals(localName)) {
            icon.uri = readAccumulatedChars();
            feedRef.icon = icon;
            iconMode = false;
        } else if (logoMode && LOGO.equals(localName)) {
            logo.uri = readAccumulatedChars();
            feedRef.logo = logo;
            logoMode = false;
        } else if (generatorMode && GENERATOR.equals(localName)) {
            generator.text = readAccumulatedChars();
            feedRef.generator = generator;
            generatorMode = false;
        } else if (dateMode && (UPDATED.equals(localName) || LAST_BUILD_DATE.equals(localName))) {
            date.datetime = readAccumulatedChars();
            feedRef.updated = date;
            dateMode = false;
        } else if (entryMode && dateMode && (PUBLISHED.equals(localName) || PUB_DATE.equals(localName))) {
            date.datetime = readAccumulatedChars();
            entry.published = date;
            dateMode = false;
        } else if (categoryMode && CATEGORY.equals(localName)) {
            category.any = readAccumulatedChars();
            feedRef.categories.addElement(category);
            categoryMode = false;
        } else if (linkMode && (LINK.equals(localName) || ENCLOSURE.equals(localName))) {
            // RSS feed might have an image link for the feed, don't want it overwriting the feed link
            if (imageMode) {
                clearAccumulatedChars();
            } else {
                link.any = readAccumulatedChars();
                feedRef.links.addElement(link);
            }
            linkMode = false;
        } else if (entryMode && SOURCE.equals(localName)) {
            entry.source = feed;
        } else if (personMode) {
            if (AUTHOR.equals(localName)) {
                feedRef.authors.addElement(person);
                personMode = false;
            } else if (CONTRIBUTOR.equals(localName)) {
                feedRef.contributors.addElement(person);
                personMode = false;
            } else if (NAME.equals(localName)) {
                person.name = readAccumulatedChars();
            } else if (EMAIL.equals(localName)) {
                person.email = readAccumulatedChars();
            } else if (URI.equals(localName)) {
                person.uri = readAccumulatedChars();
            } else if (CREATOR.equals(localName) && DC_NS.equals(uri)) {
                person.any = readAccumulatedChars();
                feedRef.authors.addElement(person);
                personMode = false;
            }
        } else if (imageMode) {
            if (IMAGE.equals(localName)) {
                imageMode = false;
            }
            // sub-elements of <image> are skipped
        }
    }
    
    public void characters(final char[] ch, final int start, final int length)
    throws SAXException {
        
        if (done) {
            return;
        }
        
//#mdebug debug
//#         System.out.print(new String(ch, start, length).trim());
//#enddebug
        
        if (extElement > 0) {
            listener.characters(ch, start, length);
        } else {
            // make sure we don't accumulate more than maxChars
            final int more = maxChars - chars.length();
            if (more > 0) {
                chars.append(ch, start, Math.min(more, length));
            }
        }
    }
    
    public void warning(final SAXParseException e) throws SAXException {
        listener.errorProcessingFeed(e.getMessage());
    }
    
    public void error(final SAXParseException e) throws SAXException {
        listener.errorProcessingFeed(e.getMessage());
    }
    
    public void fatalError(final SAXParseException e) throws SAXException {
        listener.errorProcessingFeed(e.getMessage());
    }
}
