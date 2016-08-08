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
import java.util.Calendar;
//#if IncludeDateTime
//# import sun.javax.xml.datatype.DatatypeFactory;
//# import sun.javax.xml.datatype.XMLGregorianCalendar;
//#else
//#endif
import org.xml.sax.Attributes;

public final class Date extends Common {
    
//#if IncludeDateTime
//#     private static final DatatypeFactory factory = DatatypeFactory.newInstance();
//#else
//#endif
    
    private static final char SPACE = ' ';
    
    String datetime = null;
    
    Date() {
    }
    
    Date(final Feed parent) {
        super(null, parent);
        
//#if IncludeDateTime
//#         datetime = factory.newXMLGregorianCalendar(Calendar.getInstance()).toString();
//#else
//#endif
    }
    
    Date(final Attributes attrs, final Feed parent) {
        super(attrs, parent);
    }
    
    public String getDatetime() {
        return datetime;
    }
    
//#if IncludeDateTime
//#     public XMLGregorianCalendar getCalendar() {
//#         return factory.newXMLGregorianCalendar(datetime);
//#     }
//#else
//#endif
    
    // convert Date.toString to a date string compatible with HTTP-Date in rfc2616 Section 3.3
    // "Mon Sep 18 22:25:01 UTC 2006" -> "Mon, 18 Sep 22:25:01 2006 GMT"
    public static final String toHttpString(final java.util.Date date) {
        final String input = date.toString();
        final StringBuffer output = new StringBuffer(input.length());
        
        final int wkday = 0;
        final int month = input.indexOf(SPACE, wkday + 1);
        final int day = input.indexOf(SPACE, month + 1);
        final int time = input.indexOf(SPACE, day + 1);
        final int tz = input.indexOf(SPACE, time + 1);
        final int year = input.indexOf(SPACE, tz + 1);
        
        output.append(input.substring(wkday, month));
        output.append(',');
        output.append(input.substring(day, time));
        output.append(input.substring(month, day));
        output.append(input.substring(year, input.length()));
        output.append(input.substring(time, tz));
        output.append(" GMT");
        
        return output.toString();
    }
    
    public void read(final DataInput in) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        super.read(in);
        datetime = (String) StoreHelper.read(in);
    }
    
    public void write(final DataOutput out) throws IOException {
        super.write(out);
        StoreHelper.write(out, datetime);
    }
}
