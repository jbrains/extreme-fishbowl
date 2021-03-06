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
import org.apache.commons.vfs.Selectors;

import java.io.OutputStream;

/**
 * File system test that check that a file system can be renamed.
 *
 * @author <a href="mailto:imario@apache.org">Mario Ivankovits</a>
 */
public class ProviderRenameTests
    extends AbstractProviderTestCase
{
    /**
     * Returns the capabilities required by the tests of this test case.
     */
    protected Capability[] getRequiredCaps()
    {
        return new Capability[]
        {
            Capability.CREATE,
            Capability.DELETE,
            Capability.GET_TYPE,
            Capability.LIST_CHILDREN,
            Capability.READ_CONTENT,
            Capability.WRITE_CONTENT,
            Capability.RENAME
        };
    }

    /**
     * Sets up a scratch folder for the test to use.
     */
    protected FileObject createScratchFolder() throws Exception
    {
        FileObject scratchFolder = getWriteFolder();

        // Make sure the test folder is empty
        scratchFolder.delete(Selectors.EXCLUDE_SELF);
        scratchFolder.createFolder();

        return scratchFolder;
    }

    /**
     * Tests create-delete-create-a-file sequence on the same file system.
     */
    public void testRenameFile() throws Exception
    {
        final FileObject scratchFolder = createScratchFolder();

        // Create direct child of the test folder
        final FileObject file = scratchFolder.resolveFile("file1.txt");
        assertTrue(!file.exists());

        // Create the source file
        final String content = "Here is some sample content for the file.  Blah Blah Blah.";

        final OutputStream os = file.getContent().getOutputStream();
        try
        {
            os.write(content.getBytes("utf-8"));
        }
        finally
        {
            os.close();
        }
        assertSameContent(content, file);


        // Make sure we can move the new file to another file on the same filesystem

        FileObject fileMove = scratchFolder.resolveFile("file1move.txt");
        assertTrue(!fileMove.exists());

        file.moveTo(fileMove);

        assertTrue(!file.exists());
        assertTrue(fileMove.exists());

        assertSameContent(content, fileMove);

        // Delete the file.
        assertTrue(fileMove.exists());
        assertTrue(fileMove.delete());
    }
}
