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
import org.xml.sax.Attributes;

public final class FeedEntry extends Feed {
    
    Content content = null;
    Text summary = null;
    Date published = null;
    Feed source = null;
    
    FeedEntry() {
        super();
    }
    
    FeedEntry(final Attributes attrs, final Feed parent) {
        super(attrs, parent);
    }
    
    public FeedEntry(final String title, final String content, final Feed parent) {
        super(null, parent);
        this.title = new Content(title, parent);
        this.content = new Content(content, parent);
        this.published = new Date(parent);
    }
    
    public FeedEntry(final String title, final String content, final String imageLocation, final Feed parent) {
        this(title, content, parent);
        this.published = new Date(parent);
        
        this.content.type = TextType.HTML.toString();
        this.content.text = "<p>" + this.content.text + "</p><p><img border=\"0\" src=\"" + imageLocation + "\"/></p>";
    }
    
    public void update(final String title, final String content) {
        this.title = new Content(title, container);
        this.content = new Content(content, container);
        this.updated = new Date(container);
    }
    
    // recommended
    
    public Content getContent() {
        return content;
    }
    
    public Text getSummary() {
        return summary;
    }
    
    // optional
    
    public Date getPublished() {
        return published;
    }
    
    public Feed getSource() {
        return source;
    }
    
    public String toXML() {
        return
            "<?xml version=\"1.0\" encoding=\"" + Feed.DEFAULT_CHARSET + "\"?>\n" +
            "<entry xmlns=\"http://www.w3.org/2005/Atom\">\n" +
            "  <title>" + title.getText() + "</title>\n" +
            "  <content type=\"html\">" + escapeXML(content.getText()) + "</content>\n" +
            "</entry>";
    }
    
    public void read(final DataInput in) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        super.read(in);
        content = (Content) StoreHelper.read(in);
        summary = (Text) StoreHelper.read(in);
        published = (Date) StoreHelper.read(in);
        source = (Feed) StoreHelper.read(in);
    }
    
    public void write(final DataOutput out) throws IOException {
        super.write(out);
        StoreHelper.write(out, content);
        StoreHelper.write(out, summary);
        StoreHelper.write(out, published);
        StoreHelper.write(out, source);
    }
}
