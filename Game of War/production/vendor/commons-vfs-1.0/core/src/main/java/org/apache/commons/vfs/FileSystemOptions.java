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
package org.apache.commons.vfs;

import java.util.Map;
import java.util.TreeMap;

/**
 * Container for FileSystemOptions.<br>
 * You have to use *FileSystemConfigBuilder.getInstance() to fill this container<br>
 * * = the filesystem provider short name
 *
 * @author <a href="mailto:imario@apache.org">Mario Ivankovits</a>
 * @version $Revision: 480428 $ $Date: 2006-11-29 07:15:24 +0100 (Mi, 29 Nov 2006) $
 * @see org.apache.commons.vfs.provider.sftp.SftpFileSystemConfigBuilder
 * @see org.apache.commons.vfs.provider.ftp.FtpFileSystemConfigBuilder
 */
public class FileSystemOptions
{
    private Map options = new TreeMap();

    private class FileSystemOptionKey implements Comparable
    {
        private final Class fileSystemClass;
        private final String name;

        private FileSystemOptionKey(Class fileSystemClass, String name)
        {
            this.fileSystemClass = fileSystemClass;
            this.name = name;
        }

        public int compareTo(Object o)
        {
            FileSystemOptionKey k = (FileSystemOptionKey) o;

            int ret = k.fileSystemClass.getName().compareTo(k.fileSystemClass.getName());
            if (ret != 0)
            {
                return ret;
            }

            return name.compareTo(k.name);
        }

        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            final FileSystemOptionKey that = (FileSystemOptionKey) o;

            if (!fileSystemClass.equals(that.fileSystemClass))
            {
                return false;
            }
            if (!name.equals(that.name))
            {
                return false;
            }

            return true;
        }

        public int hashCode()
        {
            int result;
            result = fileSystemClass.hashCode();
            result = 29 * result + name.hashCode();
            return result;
        }
    }

    public FileSystemOptions()
    {
    }

    void setOption(Class fileSystemClass, String name, Object value)
    {
        options.put(new FileSystemOptionKey(fileSystemClass, name), value);
    }

    Object getOption(Class fileSystemClass, String name)
    {
        FileSystemOptionKey key = new FileSystemOptionKey(fileSystemClass, name);
        return options.get(key);
    }

    boolean hasOption(Class fileSystemClass, String name)
    {
        FileSystemOptionKey key = new FileSystemOptionKey(fileSystemClass, name);
        return options.containsKey(key);
    }

    public int compareTo(FileSystemOptions other)
    {
        if (this == other)
        {
            // the same instance
            return 0;
        }

        int propsSz = options == null ? 0 : options.size();
        int propsFkSz = other.options == null ? 0 : other.options.size();
        if (propsSz < propsFkSz)
        {
            return -1;
        }
        if (propsSz > propsFkSz)
        {
            return 1;
        }
        if (propsSz == 0)
        {
            // props empty
            return 0;
        }

        int hash = options.hashCode();
        int hashFk = other.options.hashCode();
        if (hash < hashFk)
        {
            return -1;
        }
        if (hash > hashFk)
        {
            return 1;
        }

        // bad props not the same instance, but looks like the same
        // TODO: compare Entry by Entry
        return 0;
    }
}
