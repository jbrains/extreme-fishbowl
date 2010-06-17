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
package org.apache.commons.vfs.provider.ram;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.RandomAccessContent;
import org.apache.commons.vfs.provider.AbstractFileObject;
import org.apache.commons.vfs.util.RandomAccessMode;

/**
 * A RAM File contains a single RAM FileData instance, it provides methods to
 * access the data by implementing FileObject interface.
 */
public class RamFileObject extends AbstractFileObject implements FileObject
{
	/**
	 * File System
	 */
	RamFileSystem fs;

	/**
	 * RAM File Object Data
	 */
	private RamFileData data;

	private void save() throws FileSystemException
	{
		this.fs.save(this);
	}

	/**
	 * @param name
	 * @param fs
	 */
	protected RamFileObject(FileName name, RamFileSystem fs)
	{
		super(name, fs);
		this.fs = fs;
		this.fs.attach(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.vfs.provider.AbstractFileObject#doGetType()
	 */
	protected FileType doGetType() throws Exception
	{
		return data.getType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.vfs.provider.AbstractFileObject#doListChildren()
	 */
	protected String[] doListChildren() throws Exception
	{
		return this.fs.listChildren(this.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.vfs.provider.AbstractFileObject#doGetContentSize()
	 */
	protected long doGetContentSize() throws Exception
	{
		return this.data.getBuffer().length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.vfs.provider.AbstractFileObject#doGetInputStream()
	 */
	protected InputStream doGetInputStream() throws Exception
	{
		return new ByteArrayInputStream(this.data.getBuffer());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.vfs.provider.AbstractFileObject#doGetOutputStream(boolean)
	 */
	protected OutputStream doGetOutputStream(boolean bAppend) throws Exception
	{
		if (!bAppend)
		{
			this.data.setBuffer(new byte[0]);
		}
		return new RamFileOutputStream(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.vfs.provider.AbstractFileObject#doDelete()
	 */
	protected void doDelete() throws Exception
	{
		fs.delete(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.vfs.provider.AbstractFileObject#doGetLastModifiedTime()
	 */
	protected long doGetLastModifiedTime() throws Exception
	{
		return data.getLastModified();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.vfs.provider.AbstractFileObject#doSetLastModifiedTime(long)
	 */
	protected void doSetLastModifiedTime(long modtime) throws Exception
	{
		data.setLastModified(modtime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.vfs.provider.AbstractFileObject#doCreateFolder()
	 */
	protected void doCreateFolder() throws Exception
	{
		this.injectType(FileType.FOLDER);
		this.save();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.vfs.provider.AbstractFileObject#doRename(org.apache.commons.vfs.FileObject)
	 */
	protected void doRename(FileObject newfile) throws Exception
	{
		fs.rename(this, (RamFileObject) newfile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.vfs.provider.AbstractFileObject#doGetRandomAccessContent(org.apache.commons.vfs.util.RandomAccessMode)
	 */
	protected RandomAccessContent doGetRandomAccessContent(RandomAccessMode mode)
			throws Exception
	{
		return new RamFileRandomAccessContent(this, mode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.vfs.provider.AbstractFileObject#doAttach()
	 */
	protected void doAttach() throws Exception
	{
		this.fs.attach(this);
	}

	/**
	 * @return Returns the data.
	 */
	RamFileData getData()
	{
		return data;
	}

	/**
	 * @param data
	 *            The data to set.
	 */
	void setData(RamFileData data)
	{
		this.data = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.vfs.provider.AbstractFileObject#injectType(org.apache.commons.vfs.FileType)
	 */
	protected void injectType(FileType fileType)
	{
		this.data.setType(fileType);
		super.injectType(fileType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.vfs.provider.AbstractFileObject#endOutput()
	 */
	protected void endOutput() throws Exception
	{
		super.endOutput();
		this.save();
	}

	/**
	 * @return Returns the size of the RAMFileData
	 */
	int size()
	{
		if (data == null)
		{
			return 0;
		}
		return data.size();
	}

	/**
	 * @param newSize
	 * @throws IOException
	 *             if the new size exceeds the limit
	 */
	synchronized void resize(int newSize) throws IOException
	{
		if (fs.getFileSystemOptions() != null)
		{
			int maxSize = RamFileSystemConfigBuilder.getInstance().getMaxSize(
					fs.getFileSystemOptions());
			if (fs.size() + newSize - this.size() > maxSize)
			{
				throw new IOException("FileSystem capacity (" + maxSize
						+ ") exceeded.");
			}
		}
		this.data.resize(newSize);
	}

}
