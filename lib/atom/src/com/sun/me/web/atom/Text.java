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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class Text extends Common {
    
    private static final SAXParserFactory factory = SAXParserFactory.newInstance();
    private static final byte[] DIV_START = "<div>".getBytes();
    private static final byte[] DIV_END = "</div>".getBytes();
    
    public abstract String getType();
    public abstract String getText();
    
    static {
        factory.setNamespaceAware(false);
        factory.setValidating(false);
    }
    
    Text() {
        super();
    }
    
    Text(final Attributes attrs, final Feed parent) {
        super(attrs, parent);
    }
    
    public String getTextSanitized() {
        final String text = getText();
        if (text == null) {
            return "";
        }
        final int clen = text.length();
        final char[] chars = new char[clen];
        text.getChars(0, clen, chars, 0);
        final char[] copy = new char[chars.length * 2];
        char last = ' ';
        int i, j;
        for (i = 0, j = 0; i < chars.length; i++) {
            final char ch = chars[i];
            // TODO: might want to strip other whitespace chars 
            if (ch == '\n' || ch == '\t') {
                // compress consecutive whitespace
                if (last != ' ') {
                    last = copy[j++] = ' ';
                }
            } else if (ch == '&') {
                last = copy[j++] = '&';
                last = copy[j++] = 'a';
                last = copy[j++] = 'm';
                last = copy[j++] = 'p';
                last = copy[j++] = ';';
            } else {
                last = copy[j++] = ch;
            }
        }
        return new String(copy, 0, j);
    }
    
    public static String extractAsText(final Text text, final boolean stripWhitespace) {
        if (text == null) {
            return null;
        }
        return extractAsText(
            stripWhitespace ? text.getTextSanitized() : text.getText(), 
            text.getType());
    }

    private static String extractAsText(final String contents, final String type) {
        if (type == null) {
            // it's plain text if type is not specified
            return contents;
        }
        
        if (TextType.TEXT.toString().equals(type)) {
            return contents;
        }
        
        // TODO: add test for other XML MIME types, if needed
        if (TextType.HTML.toString().equals(type) || TextType.XHTML.toString().equals(type)) {
            // wrap content in <div> just in case is is'nt already wrapped in a root element
            // otherwise the XML parser chokes on the missing root element
            // we could be smart and wrap only if the first character is not '<' but that is fragile
            final byte[] cbuf = contents.getBytes();
            final byte[] buffer = new byte[DIV_START.length + cbuf.length + DIV_END.length];
            System.arraycopy(DIV_START, 0, buffer, 0, DIV_START.length);
            System.arraycopy(cbuf, 0, buffer, DIV_START.length, cbuf.length);
            System.arraycopy(DIV_END, 0, buffer, DIV_START.length + cbuf.length, DIV_END.length);
            final StringBuffer sbuf = new StringBuffer(buffer.length);
            final InputStream is = new ByteArrayInputStream(buffer);
            try {
                final SAXParser parser = factory.newSAXParser();
                parser.parse(is, new StripMarkup(sbuf));
            } catch (Exception sx) {
                // ignore, probably due to truncated or non-well-formed html
//#mdebug
//#                 sx.printStackTrace();
//#enddebug
            }
            return sbuf.toString().trim();
        }
        
        // type not handled
        return contents;
    }
    
    private static class StripMarkup extends DefaultHandler {
        
        private final StringBuffer sbuf;
        
        StripMarkup(final StringBuffer sbuf) {
            this.sbuf = sbuf;
        }
        
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
            sbuf.append(ch, start, length);
        }
    }
}
