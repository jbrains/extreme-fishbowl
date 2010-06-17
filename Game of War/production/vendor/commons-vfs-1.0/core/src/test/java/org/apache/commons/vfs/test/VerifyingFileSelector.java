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
package org.apache.commons.vfs.test;

import junit.framework.Assert;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSelector;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A file selector that asserts that all files are visited, in the correct
 * order.
 *
 * @author <a href="mailto:adammurdoch@apache.org">Adam Murdoch</a>
 * @version $Revision: 480428 $ $Date: 2006-11-29 07:15:24 +0100 (Mi, 29 Nov 2006) $
 */
public class VerifyingFileSelector
    extends Assert
    implements FileSelector
{
    private final FileInfo rootFile;
    private final List files = new ArrayList();

    private FileInfo currentFolderInfo;
    private FileObject currentFolder;
    private Set children;
    private List stack = new ArrayList();

    public VerifyingFileSelector(final FileInfo fileInfo)
    {
        this.rootFile = fileInfo;
        children = new HashSet();
        children.add(rootFile.baseName);
    }

    /**
     * Determines if a file or folder should be selected.
     */
    public boolean includeFile(final FileSelectInfo fileInfo)
        throws FileSystemException
    {
        final FileObject file = fileInfo.getFile();
        if (file == currentFolder)
        {
            // Pop current folder
            assertEquals(0, children.size());
            currentFolder = currentFolder.getParent();
            currentFolderInfo = currentFolderInfo.getParent();
            children = (Set) stack.remove(0);
        }

        final String baseName = file.getName().getBaseName();

        final FileInfo childInfo = getChild(baseName);
        assertSame(childInfo.type, file.getType());

        final boolean isChild = children.remove(baseName);
        assertTrue(isChild);

        files.add(file);
        return true;
    }

    /**
     * Determines whether a folder should be traversed.
     */
    public boolean traverseDescendents(final FileSelectInfo fileInfo)
        throws FileSystemException
    {
        // Check that the given file is a folder
        final FileObject folder = fileInfo.getFile();
        assertSame(FileType.FOLDER, folder.getType());

        // Locate the info for the folder
        final String baseName = folder.getName().getBaseName();
        if (currentFolder == null)
        {
            assertEquals(rootFile.baseName, baseName);
            currentFolderInfo = rootFile;
        }
        else
        {
            assertSame(currentFolder, folder.getParent());

            // Locate the info for the child, and make sure it is folder
            currentFolderInfo = getChild(baseName);
            assertSame(FileType.FOLDER, currentFolderInfo.type);
        }

        // Push the folder
        stack.add(0, children);
        children = new HashSet(currentFolderInfo.children.keySet());
        currentFolder = folder;

        return true;
    }

    /**
     * Finds a child of the current folder.
     */
    private FileInfo getChild(final String baseName)
    {
        if (currentFolderInfo == null)
        {
            assertEquals(rootFile.baseName, baseName);
            return rootFile;
        }
        else
        {
            final FileInfo child = (FileInfo) currentFolderInfo.children.get(baseName);
            assertNotNull(child);
            return child;
        }
    }

    /**
     * Asserts that the selector has seen all the files.
     *
     * @return The files in the order they where visited.
     */
    public List finish()
    {
        assertEquals(0, children.size());
        return files;
    }
}
