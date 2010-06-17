/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * A custom URLStreamHandler to test loading and saving configurations to non
 * standard URLs. This handler acts like a file handler with write support.
 *
 * @author Emmanuel Bourg
 * @version $Revision: 541407 $, $Date: 2007-05-24 22:03:09 +0200 (Do, 24 Mai 2007) $
 */
public class FileURLStreamHandler extends URLStreamHandler
{
    protected URLConnection openConnection(URL u) throws IOException
    {
        final File file = new File(u.getFile());

        return new URLConnection(u) {

            public void connect() throws IOException
            {
            }

            public InputStream getInputStream() throws IOException
            {
                return new FileInputStream(file);
            }

            public OutputStream getOutputStream() throws IOException
            {
                return new FileOutputStream(file);
            }
        };
    }
}
