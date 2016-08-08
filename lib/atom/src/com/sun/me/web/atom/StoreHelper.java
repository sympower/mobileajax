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
import java.util.Vector;
import java.util.Date;

public final class StoreHelper {
    
    private static final int NULL_TYPE = 1;
    private static final int STRING_TYPE = 2;
    private static final int INTEGER_TYPE = 3;
    // TODO: add other types as needed
    private static final int DATE_TYPE = 125;
    private static final int VECTOR_TYPE = 126;
    private static final int STORABLE_TYPE = 127;
    
    public static void write(final DataOutput out, final Object obj) 
    throws IOException {
        
        if (obj == null) {
            out.writeByte(NULL_TYPE);
        } else if (obj instanceof String) {
            out.writeByte(STRING_TYPE);
            out.writeUTF((String) obj);
        } else if (obj instanceof Integer) {
            out.writeByte(INTEGER_TYPE);
            out.writeInt(((Integer) obj).intValue());
        } else if (obj instanceof Date) {
            out.writeByte(DATE_TYPE);
            out.writeLong(((Date) obj).getTime());
        } else if (obj instanceof Vector) {
            final Vector list = (Vector) obj;
            out.writeByte(VECTOR_TYPE);
            final int size = list.size();
            out.writeShort(size);
            for (int i=0; i < size; i++) {
                write(out, list.elementAt(i));
            }
        } else if (obj instanceof Storable) {
            out.writeByte(STORABLE_TYPE);
            out.writeUTF(obj.getClass().getName());
            ((Storable) obj).write(out);
        } else {
            throw new IllegalArgumentException("Cannot serialize type: " +
                    obj.getClass().getName());
        }
    }
    
    public static Object read(final DataInput in) 
    throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        final int type = in.readByte();
        switch (type) {
            case NULL_TYPE:
                return null;
                
            case STRING_TYPE:
                return in.readUTF();
                
            case INTEGER_TYPE:
                return new Integer(in.readInt());
                
            case DATE_TYPE:
                return new Date(in.readLong());
                
            case VECTOR_TYPE:
                final int size = in.readShort();
                final Vector list = new Vector(size);
                for (int i=0; i < size; i++) {
                    list.addElement(read(in));
                }
                return list;
                
            case STORABLE_TYPE:
                final String className = in.readUTF();
                final Storable storable = (Storable) Class.forName(className).newInstance();
                storable.read(in);
                return storable;
        }
        
        throw new IllegalArgumentException("Cannot deserialize type: " + type);
    }
}
