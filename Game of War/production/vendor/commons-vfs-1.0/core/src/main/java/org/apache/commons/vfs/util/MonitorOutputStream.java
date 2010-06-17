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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An OutputStream that provides buffering and end-of-stream monitoring.
 *
 * @author <a href="mailto:adammurdoch@apache.org">Adam Murdoch</a>
 * @version $Revision: 480428 $ $Date: 2006-11-29 07:15:24 +0100 (Mi, 29 Nov 2006) $
 */
public class MonitorOutputStream
    extends BufferedOutputStream
{
    private boolean finished;

    public MonitorOutputStream(final OutputStream out)
    {
        super(out);
    }

    /**
     * Closes this output stream.
     */
    public void close() throws IOException
    {
        if (finished)
        {
            return;
        }

        // Close the output stream
        IOException exc = null;
        try
        {
            super.close();
        }
        catch (final IOException ioe)
        {
            exc = ioe;
        }

        // Notify of end of output
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
     * Called after this stream is closed.  This implementation does nothing.
     */
    protected void onClose() throws IOException
    {
    }
}
