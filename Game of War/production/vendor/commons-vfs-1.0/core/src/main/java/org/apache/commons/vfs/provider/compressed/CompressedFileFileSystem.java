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
package org.apache.commons.vfs.provider.compressed;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.provider.AbstractFileSystem;

import java.util.Collection;

/**
 * A read-only file system for compressed files.
 *
 * @author <a href="mailto:imario@apache.org">Mario Ivankovits</a>
 * @version $Revision: 480428 $ $Date: 2006-11-29 07:15:24 +0100 (Mi, 29 Nov 2006) $
 */
public abstract class CompressedFileFileSystem
    extends AbstractFileSystem
    implements FileSystem
{
    protected CompressedFileFileSystem(final FileName rootName,
                                       final FileObject parentLayer,
                                       final FileSystemOptions fileSystemOptions)
        throws FileSystemException
    {
        super(rootName, parentLayer, fileSystemOptions);
    }

    public void init() throws FileSystemException
    {
        super.init();

    }

    /**
     * Returns the capabilities of this file system.
     */
    protected abstract void addCapabilities(final Collection caps);

    /**
     * Creates a file object.
     */
    protected abstract FileObject createFile(final FileName name) throws FileSystemException;
}
