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
package org.apache.commons.vfs.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An InputStream that provides buffering and end-of-stream monitoring.
 *
 * @author <a href="mailto:adammurdoch@apache.org">Adam Murdoch</a>
 * @version $Revision: 480428 $ $Date: 2006-11-29 07:15:24 +0100 (Mi, 29 Nov 2006) $
 */
public class MonitorInputStream
    extends BufferedInputStream
{
    private boolean finished;
    private long count;

    public MonitorInputStream(final InputStream in)
    {
        super(in);
        count = 0;
    }

    /**
     * Reads a character.
     */
    public int read() throws IOException
    {
        if (finished)
        {
            return -1;
        }

        final int ch = super.read();
        if (ch != -1)
        {
            count++;
            return ch;
        }

        // End-of-stream
        close();
        return -1;
    }

    /**
     * Reads bytes from this input stream.error occurs.
     */
    public int read(final byte[] buffer, final int offset, final int length)
        throws IOException
    {
        if (finished)
        {
            return -1;
        }

        final int nread = super.read(buffer, offset, length);
        if (nread != -1)
        {
            count += nread;
            return nread;
        }

        // End-of-stream
        close();
        return -1;
    }

    /**
     * Closes this input stream and releases any system resources
     * associated with the stream.
     */
    public void close() throws IOException
    {
        if (finished)
        {
            return;
        }

        // Close the stream
        IOException exc = null;
        try
        {
            super.close();
        }
        catch (final IOException ioe)
        {
            exc = ioe;
        }

        // Notify that the stream has been closed
        try
        {
            onClose();
        }
        catch (final IOException ioe)
        {
            exc = ioe;
        }

        finished = true;
        if (exc != null)
        {
            throw exc;
        }
    }

    /**
     * Called after the stream has been closed.  This implementation does
     * nothing.
     */
    protected void onClose() throws IOException
    {
    }

    /**
     * Get the nuber of bytes read by this input stream
     */
    public long getCount()
    {
        return count;
    }
}
