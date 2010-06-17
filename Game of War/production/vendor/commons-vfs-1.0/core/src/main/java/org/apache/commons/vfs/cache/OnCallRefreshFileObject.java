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
package org.apache.commons.vfs.cache;

import java.util.List;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelector;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.NameScope;
import org.apache.commons.vfs.impl.DecoratedFileObject;

/**
 * This decorator refreshes the fileObject data on every call
 *
 * @author <a href="mailto:imario@apache.org">Mario Ivankovits</a>
 * @version $Revision: 480428 $ $Date: 2006-11-29 07:15:24 +0100 (Mi, 29 Nov 2006) $
 */
public class OnCallRefreshFileObject extends DecoratedFileObject
{
	public OnCallRefreshFileObject(FileObject fileObject)
	{
		super(fileObject);		
	}

	public void close() throws FileSystemException
	{
		refresh();
		super.close();
	}

	public void copyFrom(FileObject srcFile, FileSelector selector) throws FileSystemException
	{
		refresh();
		super.copyFrom(srcFile, selector);
	}

	public void createFile() throws FileSystemException
	{
		refresh();
		super.createFile();
	}

	public void createFolder() throws FileSystemException
	{
		refresh();
		super.createFolder();
	}

	public boolean delete() throws FileSystemException
	{
		refresh();
		return super.delete();
	}

	public int delete(FileSelector selector) throws FileSystemException
	{
		refresh();
		return super.delete(selector);
	}

	public boolean exists() throws FileSystemException
	{
		refresh();
		return super.exists();
	}

	public void findFiles(FileSelector selector, boolean depthwise, List selected) throws FileSystemException
	{
		refresh();
		super.findFiles(selector, depthwise, selected);
	}

	public FileObject[] findFiles(FileSelector selector) throws FileSystemException
	{
		refresh();
		return super.findFiles(selector);
	}

	public FileObject getChild(String name) throws FileSystemException
	{
		refresh();
		return super.getChild(name);
	}

	public FileObject[] getChildren() throws FileSystemException
	{
		refresh();
		return super.getChildren();
	}

	public FileContent getContent() throws FileSystemException
	{
		refresh();
		return super.getContent();
	}

	public FileType getType() throws FileSystemException
	{
		refresh();
		return super.getType();
	}

	public boolean isHidden() throws FileSystemException
	{
		refresh();
		return super.isHidden();
	}

	public boolean isReadable() throws FileSystemException
	{
		refresh();
		return super.isReadable();
	}

	public boolean isWriteable() throws FileSystemException
	{
		refresh();
		return super.isWriteable();
	}

	public void moveTo(FileObject destFile) throws FileSystemException
	{
		refresh();
		super.moveTo(destFile);
	}

	public FileObject resolveFile(String name, NameScope scope) throws FileSystemException
	{
		refresh();
		return super.resolveFile(name, scope);
	}

	public FileObject resolveFile(String path) throws FileSystemException
	{
		refresh();
		return super.resolveFile(path);
	}
}
