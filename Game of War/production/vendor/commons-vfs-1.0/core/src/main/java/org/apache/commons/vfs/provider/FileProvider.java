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

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemConfigBuilder;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;

import java.util.Collection;


/**
 * A file provider.  Each file provider is responsible for handling files for
 * a particular URI scheme.
 * <p/>
 * <p>A file provider may also implement {@link VfsComponent}.
 *
 * @author <a href="mailto:adammurdoch@apache.org">Adam Murdoch</a>
 * @version $Revision: 480428 $ $Date: 2006-11-29 07:15:24 +0100 (Mi, 29 Nov 2006) $
 */
public interface FileProvider
{
    /**
     * Locates a file object, by absolute URI.
     *
     * @param baseFile          The base file to use for resolving the individual parts of
     *                          a compound URI.
     * @param uri               The absolute URI of the file to find.
     * @param fileSystemOptions
     */
    FileObject findFile(final FileObject baseFile, final String uri, final FileSystemOptions fileSystemOptions)
        throws FileSystemException;

    /**
     * Creates a layered file system.
     *
     * @param scheme            The URI scheme for the layered file system.
     * @param file              The file to build the file system on.
     * @param fileSystemOptions
     */
    FileObject createFileSystem(String scheme, FileObject file, FileSystemOptions fileSystemOptions)
        throws FileSystemException;

    /**
     * Gets the configbuilder useable to collect the needed fileSystemOptions.
     */
    public FileSystemConfigBuilder getConfigBuilder();

    /**
     * Get the filesystem capabilities.<br>
     * These are the same as on the filesystem, but available before the first filesystem was
     * instanciated.
     */
    public Collection getCapabilities();

    public FileName parseUri(FileName root, String uri) throws FileSystemException;
}
