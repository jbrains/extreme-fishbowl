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
package org.apache.commons.vfs.provider.temp;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.AbstractFileProvider;
import org.apache.commons.vfs.provider.FileProvider;
import org.apache.commons.vfs.provider.UriParser;
import org.apache.commons.vfs.provider.local.DefaultLocalFileProvider;
import org.apache.commons.vfs.provider.local.LocalFileSystem;

import java.io.File;
import java.util.Collection;

/**
 * A provider for temporary files.
 *
 * @author <a href="mailto:adammurdoch@apache.org">Adam Murdoch</a>
 * @version $Revision: 480428 $ $Date: 2006-11-29 07:15:24 +0100 (Mi, 29 Nov 2006) $
 */
public class TemporaryFileProvider
    extends AbstractFileProvider
    implements FileProvider, Comparable
{
    private File rootFile;

    /*
    private final static FileName tmpFileName = new AbstractFileName("tmp", "/")
    {
        protected FileName createName(String absPath)
        {
            return null;
        }

        protected void appendRootUri(StringBuffer buffer)
        {
        }
    };
*/

    public TemporaryFileProvider(final File rootFile)
    {
        this();

        this.rootFile = rootFile;
    }

    public TemporaryFileProvider()
    {
        super();
    }

    public int compareTo(Object o)
    {
        int h1 = hashCode();
        int h2 = o.hashCode();
        if (h1 < h2)
        {
            return -1;
        }
        if (h1 > h2)
        {
            return 1;
        }

        return 0;
    }

    /**
     * Locates a file object, by absolute URI.
     */
    public synchronized FileObject findFile(final FileObject baseFile, final String uri, final FileSystemOptions properties)
        throws FileSystemException
    {
        // Parse the name
        final StringBuffer buffer = new StringBuffer(uri);
        final String scheme = UriParser.extractScheme(uri, buffer);

        UriParser.fixSeparators(buffer);
        
        FileType fileType = UriParser.normalisePath(buffer);
        final String path = buffer.toString();

        // Create the temp file system if it does not exist
        // FileSystem filesystem = findFileSystem( this, (Properties) null);
        FileSystem filesystem = findFileSystem(this, properties);
        if (filesystem == null)
        {
            if (rootFile == null)
            {
                rootFile = getContext().getTemporaryFileStore().allocateFile("tempfs");
            }
            final FileName rootName =
                getContext().parseURI(scheme + ":" + FileName.ROOT_PATH);
            // final FileName rootName =
            //    new LocalFileName(scheme, scheme + ":", FileName.ROOT_PATH);
            filesystem = new LocalFileSystem(rootName, rootFile.getAbsolutePath(), properties);
            addFileSystem(this, filesystem);
        }

        // Find the file
        return filesystem.resolveFile(path);
    }

    public Collection getCapabilities()
    {
        return DefaultLocalFileProvider.capabilities;
    }
}
