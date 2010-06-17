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
package org.apache.commons.vfs.provider.gzip;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.provider.compressed.CompressedFileFileObject;
import org.apache.commons.vfs.provider.compressed.CompressedFileFileSystem;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * the gzip file
 *
 * @author <a href="mailto:imario@apache.org">Mario Ivankovits</a>
 * @version $Revision: 480428 $ $Date: 2006-11-29 07:15:24 +0100 (Mi, 29 Nov 2006) $
 */
public class GzipFileObject extends CompressedFileFileObject
{
    protected GzipFileObject(FileName name, FileObject container, CompressedFileFileSystem fs)
    {
        super(name, container, fs);
    }

    protected InputStream doGetInputStream() throws Exception
    {
        InputStream is = getContainer().getContent().getInputStream();
        return new GZIPInputStream(is);
    }

    protected OutputStream doGetOutputStream(boolean bAppend) throws Exception
    {
        OutputStream os = getContainer().getContent().getOutputStream(false);
        return new GZIPOutputStream(os);
    }
}
