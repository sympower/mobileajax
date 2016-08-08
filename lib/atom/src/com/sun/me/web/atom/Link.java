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

public final class Link extends Common {
    
    public static final String HREF = "href";
    public static final String REL = "rel";
    public static final String HREFLANG = "hreflang";
    public static final String LENGTH = "length";
    
    public static final String TITLE = "title";
    public static final String TYPE = "type";
    
    public static final String ATOM_TYPE = "application/atom+xml";

    String href = null;
    String hreflang = null;
    String type = null;
    String title = null;
    int length = 0;
    Relation rel = null;
    String any = null;
    
    Link() {
        super();
    }
    
    Link(final Attributes attrs, final Feed parent) {
        super(attrs, parent);
        
        href = attrs.getValue(HREF);
        hreflang = attrs.getValue(HREFLANG);
        title = attrs.getValue(TITLE);
        type = attrs.getValue(TYPE);
        
        final String rels = attrs.getValue(REL);
        rel = rels == null ? null : new Relation(rels);
        final String sl = attrs.getValue(LENGTH);
        if (sl != null) {
            try {
                length = Integer.parseInt(sl);
            } catch (NumberFormatException nx) {
                // ignore
            }
        }
    }
    
    public String getHRef() {
        return href;
    }
    
    public String getHRefLang() {
        return hreflang;
    }
    
    public String getType() {
        return type;
    }
    
    public String getTitle() {
        return title;
    }
    
    public int getLength() {
        return length;
    }
    
    public Relation getRelation() {
        return rel;
    }
    
    public String getAny() {
        return any;
    }

    public void read(final DataInput in) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        super.read(in);
        href = (String) StoreHelper.read(in);
        hreflang = (String) StoreHelper.read(in);
        type = (String) StoreHelper.read(in);
        title = (String) StoreHelper.read(in);
        length = ((Integer) StoreHelper.read(in)).intValue();
        rel = (Relation) StoreHelper.read(in);
        any = (String) StoreHelper.read(in);
    }

    public void write(final DataOutput out) throws IOException {
        super.write(out);
        StoreHelper.write(out, href);
        StoreHelper.write(out, hreflang);
        StoreHelper.write(out, type);
        StoreHelper.write(out, title);
        StoreHelper.write(out, new Integer(length));
        StoreHelper.write(out, rel);
        StoreHelper.write(out, any);
    }
    
    public static String follow(final Link[] links) {
        if (links == null) {
            return null;
        }
        
        // try the links in the following order:
        //   0. link.href is null, try to use the contents - rss compatibility mode
        //   1. link.rel not specified or is 'alternate'
        //   2. link.rel is 'enclosure'
        //   3. link.rel is 'via'
        
        for (int i = 0; i < links.length; i++) {
            final Link link = links[i];
            if (link.href == null) {
                return link.accumulateBaseURI() + link.getAny();
            }
        }
        
        for (int i = 0; i < links.length; i++) {
            final Link link = links[i];
            if (link.rel == null || Relation.ALTERNATE.equals(link.rel)) {
                return link.accumulateBaseURI() + link.href;
            }
        }
        
        for (int i = 0; i < links.length; i++) {
            final Link link = links[i];
            if (Relation.ENCLOSURE.equals(link.rel)) {
                return link.accumulateBaseURI() + link.href;
            }
        }
        
        for (int i = 0; i < links.length; i++) {
            final Link link = links[i];
            if (Relation.VIA.equals(link.rel)) {
                return link.accumulateBaseURI() + link.href;
            }
        }
        
        return null;
    }
}
