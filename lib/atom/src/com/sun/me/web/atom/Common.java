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

public class Common implements Storable {
    
    public static final String XML_NS = "http://www.w3.org/XML/1998/namespace";
    
    public static final String LANG = "lang";
    public static final String BASE = "base";
    
    String base = null;
    String lang = null;
    Feed container = null;
    
    protected static String escapeXML(final String xml) {
        if (xml == null) {
            return "";
        }
        final int xlen = xml.length();
        final char[] src = new char[xlen];
        xml.getChars(0, xlen, src, 0);
        final char[] copy = new char[src.length * 2];
        int i, j;
        for (i = 0, j = 0; i < src.length; i++) {
            final char ch = src[i];
            // TODO: might want to escape others
            if (ch == '&') {
                copy[j++] = '&';
                copy[j++] = 'a';
                copy[j++] = 'm';
                copy[j++] = 'p';
                copy[j++] = ';';
            } else if (ch == '<') {
                copy[j++] = '&';
                copy[j++] = 'l';
                copy[j++] = 't';
                copy[j++] = ';';
            } else if (ch == '>') {
                copy[j++] = '&';
                copy[j++] = 'g';
                copy[j++] = 't';
                copy[j++] = ';';
            } else {
                copy[j++] = ch;
            }
        }
        return new String(copy, 0, j);
    }
    
    Common() {
    }
    
    Common(final Attributes attrs, final Feed outer) {
        if (attrs != null) {
            lang = attrs.getValue(XML_NS, LANG);
            base = attrs.getValue(XML_NS, BASE);
        }
        container = outer;
    }
    
    public String getBase() {
        return base;
    }
    
    public String getBaseURI() {
        if (base == null) {
            return null;
        }
        
        final int slash = base.lastIndexOf('/');
        if (slash > 0) {
            return base.substring(0, slash + 1);
        }
        return base;
    }
    
    // recursively accumulate the parent base URIs, if present
    public String accumulateBaseURI() {
        final StringBuffer basebuf = new StringBuffer();
        if (container != null) {
            basebuf.append(container.accumulateBaseURI());
        }
        if (base != null) {
            basebuf.append(getBaseURI());
        }
        return basebuf.toString();
    }
    
    public String getLang() {
        return lang;
    }

    public void read(final DataInput in) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        base = (String) StoreHelper.read(in);
        lang = (String) StoreHelper.read(in);
        
        // TODO: read the container's lang and base
        // otherwise links that use base will not resolve after a roundtrip to storage
    }

    public void write(final DataOutput out) throws IOException {
        StoreHelper.write(out, base);
        StoreHelper.write(out, lang);
        
        // TODO: persist the container's lang and base
        // otherwise links that use base will not resolve after a roundtrip to storage
    }
}
