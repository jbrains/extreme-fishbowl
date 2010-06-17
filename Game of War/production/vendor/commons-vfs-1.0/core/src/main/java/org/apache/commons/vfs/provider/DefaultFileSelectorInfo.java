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

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;

/**
 * A default {@link FileSelectInfo} implementation.
 *
 * @author <a href="mailto:adammurdoch@apache.org">Adam Murdoch</a>
 * @version $Revision: 480428 $ $Date: 2006-11-29 07:15:24 +0100 (Mi, 29 Nov 2006) $
 */
final class DefaultFileSelectorInfo
    implements FileSelectInfo
{
    private FileObject baseFolder;
    private FileObject file;
    private int depth;

    public FileObject getBaseFolder()
    {
        return baseFolder;
    }

    public void setBaseFolder(final FileObject baseFolder)
    {
        this.baseFolder = baseFolder;
    }

    public FileObject getFile()
    {
        return file;
    }

    public void setFile(final FileObject file)
    {
        this.file = file;
    }

    public int getDepth()
    {
        return depth;
    }

    public void setDepth(final int depth)
    {
        this.depth = depth;
    }
}
