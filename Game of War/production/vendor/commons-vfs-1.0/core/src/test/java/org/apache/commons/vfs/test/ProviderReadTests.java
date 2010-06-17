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

import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Read-only test cases for file providers.
 *
 * @author <a href="mailto:adammurdoch@apache.org">Adam Murdoch</a>
 * @version $Revision: 480428 $ $Date: 2006-11-29 07:15:24 +0100 (Mi, 29 Nov 2006) $
 * @todo Test getLastModified(), getAttribute()
 */
public class ProviderReadTests
    extends AbstractProviderTestCase
{
    /**
     * Returns the capabilities required by the tests of this test case.
     */
    protected Capability[] getRequiredCaps()
    {
        return new Capability[]
        {
            Capability.GET_TYPE,
            Capability.LIST_CHILDREN,
            Capability.READ_CONTENT
        };
    }

    /**
     * Walks the base folder structure, asserting it contains exactly the
     * expected files and folders.
     */
    public void testStructure() throws Exception
    {
        final FileInfo baseInfo = buildExpectedStructure();
        assertSameStructure(getReadFolder(), baseInfo);
    }

    /**
     * Walks a folder structure, asserting it contains exactly the
     * expected files and folders.
     */
    protected void assertSameStructure(final FileObject folder,
                                       final FileInfo expected)
        throws Exception
    {
        // Setup the structure
        final List queueExpected = new ArrayList();
        queueExpected.add(expected);

        final List queueActual = new ArrayList();
        queueActual.add(folder);

        while (queueActual.size() > 0)
        {
            final FileObject file = (FileObject) queueActual.remove(0);
            final FileInfo info = (FileInfo) queueExpected.remove(0);

            // Check the type is correct
            assertSame(info.type, file.getType());

            if (info.type == FileType.FILE)
            {
                continue;
            }

            // Check children
            final FileObject[] children = file.getChildren();

            // Make sure all children were found
            assertNotNull(children);
            assertEquals("count children of \"" + file.getName() + "\"", info.children.size(), children.length);

            // Recursively check each child
            for (int i = 0; i < children.length; i++)
            {
                final FileObject child = children[i];
                final FileInfo childInfo = (FileInfo) info.children.get(child.getName().getBaseName());

                // Make sure the child is expected
                assertNotNull(childInfo);

                // Add to the queue of files to check
                queueExpected.add(childInfo);
                queueActual.add(child);
            }
        }
    }

    /**
     * Tests type determination.
     */
    public void testType() throws Exception
    {
        // Test a file
        FileObject file = getReadFolder().resolveFile("file1.txt");
        assertSame(FileType.FILE, file.getType());

        // Test a folder
        file = getReadFolder().resolveFile("dir1");
        assertSame(FileType.FOLDER, file.getType());

        // Test an unknown file
        file = getReadFolder().resolveFile("unknown-child");
        assertSame(FileType.IMAGINARY, file.getType());
    }

    /**
     * Tests the contents of root of file system can be listed.
     */
    public void testRoot() throws FileSystemException
    {
        final FileObject file = getReadFolder().getFileSystem().getRoot();
        file.getChildren();
    }

    /**
     * Tests that folders have no content.
     */
    public void testFolderContent() throws Exception
    {
        // Try getting the content of a folder
        FileObject folder = getReadFolder().resolveFile("dir1");
        try
        {
            folder.getContent().getInputStream();
            fail();
        }
        catch (FileSystemException e)
        {
            assertSameMessage("vfs.provider/read-not-file.error", folder, e);
        }
    }

    /**
     * Tests can perform operations on a folder while reading from a different files.
     */
    public void testConcurrentReadFolder() throws Exception
    {
        final FileObject file = getReadFolder().resolveFile("file1.txt");
        assertTrue(file.exists());
        final FileObject folder = getReadFolder().resolveFile("dir1");
        assertTrue(folder.exists());

        // Start reading from the file
        final InputStream instr = file.getContent().getInputStream();
        try
        {
            // Do some operations
            folder.exists();
            folder.getType();
            folder.getChildren();
        }
        finally
        {
            instr.close();
        }
    }

    /**
     * Tests that findFiles() works.
     */
    public void testFindFiles() throws Exception
    {
        final FileInfo fileInfo = buildExpectedStructure();
        final VerifyingFileSelector selector = new VerifyingFileSelector(fileInfo);

        // Find the files
        final FileObject[] actualFiles = getReadFolder().findFiles(selector);

        // Compare actual and expected list of files
        final List expectedFiles = selector.finish();
        assertEquals(expectedFiles.size(), actualFiles.length);
        final int count = expectedFiles.size();
        for (int i = 0; i < count; i++)
        {
            final FileObject expected = (FileObject) expectedFiles.get(i);
            final FileObject actual = actualFiles[i];
            assertEquals(expected, actual);
        }
    }
}
