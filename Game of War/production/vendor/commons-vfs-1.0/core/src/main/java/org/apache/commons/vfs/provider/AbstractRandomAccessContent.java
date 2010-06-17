/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.vfs.provider;

import org.apache.commons.vfs.RandomAccessContent;
import org.apache.commons.vfs.util.RandomAccessMode;

import java.io.IOException;

/**
 * Implements the DataOutput part of the RandomAccessContent interface and throws
 * UnsupportedOperationException if one of those methods are called.
 * (for read-only random access implementations)
 * 
 * @author <a href="mailto:imario@apache.org">Mario Ivankovits</a>
 * @version $Revision: 480428 $ $Date: 2006-11-29 07:15:24 +0100 (Mi, 29 Nov 2006) $
 */
public abstract class AbstractRandomAccessContent implements RandomAccessContent
{
    private final RandomAccessMode mode;

    protected AbstractRandomAccessContent(final RandomAccessMode mode)
    {
        this.mode = mode;
    }

    public void writeDouble(double v) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    public void writeFloat(float v) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    public void write(int b) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    public void writeByte(int v) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    public void writeChar(int v) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    public void writeInt(int v) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    public void writeShort(int v) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    public void writeLong(long v) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    public void writeBoolean(boolean v) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    public void write(byte b[]) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    public void write(byte b[], int off, int len) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    public void writeBytes(String s) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    public void writeChars(String s) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    public void writeUTF(String str) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated see {@link java.io.DataInputStream#readLine()}
     */
    public String readLine() throws IOException
    {
        throw new UnsupportedOperationException("deprecated");
    }
}
