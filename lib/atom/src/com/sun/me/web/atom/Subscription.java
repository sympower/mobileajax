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
import java.util.Date;

public final class Subscription implements Storable {
    
    private String title = null;
    private String uri = null;    
    private Date lastUpdated = null;
    private String etag = null;
    private PublishInfo pubInfo = null;

    public Subscription() {
    }
    
    public Subscription(final String title, final String uri, 
            final Date lastUpdated, final String etag,
            final PublishInfo pubInfo) {
        this.title = title;
        this.uri = uri;
        this.lastUpdated = lastUpdated;
        this.etag = etag;
        this.pubInfo = pubInfo;
    }
    
    public void read(final DataInput in) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        title = (String) StoreHelper.read(in);
        uri = (String) StoreHelper.read(in);
        lastUpdated = (Date) StoreHelper.read(in);
        etag = (String) StoreHelper.read(in);
        pubInfo = (PublishInfo) StoreHelper.read(in);
    }
    
    public void write(final DataOutput out) throws IOException {
        StoreHelper.write(out, title);
        StoreHelper.write(out, uri);
        StoreHelper.write(out, lastUpdated);
        StoreHelper.write(out, etag);
        StoreHelper.write(out, pubInfo);
    }
    
    public String getTitle() { return title; }
    public String getURI() { return uri; }
    public Date getLastUpdated() { return lastUpdated; }
    public String getETag() { return etag; }
    public PublishInfo getPublishInfo() { return pubInfo; }

    public void setLastUpdated(final Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public void setETag(final String etag) {
        this.etag = etag;
    }
}
