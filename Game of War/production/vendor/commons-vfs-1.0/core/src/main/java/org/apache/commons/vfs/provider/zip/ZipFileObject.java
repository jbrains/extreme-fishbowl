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
package org.apache.commons.vfs.provider.zip;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.AbstractFileObject;

import java.io.InputStream;
import java.util.HashSet;
import java.util.zip.ZipEntry;

/**
 * A file in a Zip file system.
 *
 * @author <a href="mailto:adammurdoch@apache.org">Adam Murdoch</a>
 * @version $Revision: 480428 $ $Date: 2006-11-29 07:15:24 +0100 (Mi, 29 Nov 2006) $
 */
public class ZipFileObject
    extends AbstractFileObject
    implements FileObject
{
    private final HashSet children = new HashSet();
    private final ZipFileSystem fs;
    // protected final ZipFile file;
    protected ZipEntry entry;
    private FileType type;

    protected ZipFileObject(FileName name,
                            ZipEntry entry,
                            ZipFileSystem fs,
                            boolean zipExists) throws FileSystemException
    {
        super(name, fs);
        this.fs = fs;
        setZipEntry(entry);
        if (!zipExists)
        {
            type = FileType.IMAGINARY;
        }
    }

    /**
     * Sets the details for this file object.
     */
    protected void setZipEntry(final ZipEntry entry)
    {
        if (this.entry != null)
        {
            return;
        }

        if ((entry == null) || (entry.isDirectory()))
        {
            type = FileType.FOLDER;
        }
        else
        {
            type = FileType.FILE;
        }

        this.entry = entry;
    }

    /**
     * Attaches a child
     */
    public void attachChild(FileName childName)
    {
        children.add(childName.getBaseName());
    }

    /**
     * Determines if this file can be written to.
     *
     * @return <code>true</code> if this file is writeable, <code>false</code> if not.
     */
    public boolean isWriteable() throws FileSystemException
    {
        return false;
    }

    /**
     * Returns the file's type.
     */
    protected FileType doGetType()
    {
        return type;
    }

    /**
     * Lists the children of the file.
     */
    protected String[] doListChildren()
    {
        return (String[]) children.toArray(new String[children.size()]);
    }

    /**
     * Returns the size of the file content (in bytes).  Is only called if
     * {@link #doGetType} returns {@link FileType#FILE}.
     */
    protected long doGetContentSize()
    {
        return entry.getSize();
    }

    /**
     * Returns the last modified time of this file.
     */
    protected long doGetLastModifiedTime() throws Exception
    {
        return entry.getTime();
    }

    /**
     * Creates an input stream to read the file content from.  Is only called
     * if  {@link #doGetType} returns {@link FileType#FILE}.  The input stream
     * returned by this method is guaranteed to be closed before this
     * method is called again.
     */
    protected InputStream doGetInputStream() throws Exception
    {
        return fs.getZipFile().getInputStream(entry);
    }
}
