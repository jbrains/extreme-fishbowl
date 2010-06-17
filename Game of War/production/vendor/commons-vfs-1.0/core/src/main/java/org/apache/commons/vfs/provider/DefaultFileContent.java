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

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileContentInfo;
import org.apache.commons.vfs.FileContentInfoFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.RandomAccessContent;
import org.apache.commons.vfs.util.MonitorInputStream;
import org.apache.commons.vfs.util.MonitorOutputStream;
import org.apache.commons.vfs.util.MonitorRandomAccessContent;
import org.apache.commons.vfs.util.RandomAccessMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * The content of a file.
 *
 * @author <a href="mailto:adammurdoch@apache.org">Adam Murdoch</a>
 * @version $Revision: 480428 $ $Date: 2006-11-29 07:15:24 +0100 (Mi, 29 Nov 2006) $
 */
public final class DefaultFileContent implements FileContent
{
    /*
    static final int STATE_NONE = 0;
    static final int STATE_READING = 1;
    static final int STATE_WRITING = 2;
    static final int STATE_RANDOM_ACCESS = 3;
    */

    static final int STATE_CLOSED = 0;
    static final int STATE_OPENED = 1;

    private final AbstractFileObject file;
    private Map attrs;
    private Map roAttrs;
    private FileContentInfo fileContentInfo;
    private final FileContentInfoFactory fileContentInfoFactory;

    private final ThreadLocal threadData = new ThreadLocal();

    /**
     * open streams counter for this file
     */
    private int openStreams = 0;

    public DefaultFileContent(final AbstractFileObject file, final FileContentInfoFactory fileContentInfoFactory)
    {
        this.file = file;
        this.fileContentInfoFactory = fileContentInfoFactory;
    }

    private FileContentThreadData getThreadData()
    {
        FileContentThreadData data = (FileContentThreadData) this.threadData.get();
        if (data == null)
        {
            data = new FileContentThreadData();
            this.threadData.set(data);
        }
        return data;
    }

    void streamOpened()
    {
        synchronized (this)
        {
            openStreams++;
        }
        ((AbstractFileSystem) file.getFileSystem()).streamOpened();
    }

    void streamClosed()
    {
        synchronized (this)
        {
            if (openStreams > 0)
            {
                openStreams--;
                if (openStreams < 1)
                {
                    file.notifyAllStreamsClosed();
                }
            }
        }
        ((AbstractFileSystem) file.getFileSystem()).streamClosed();
    }

    /**
     * Returns the file that this is the content of.
     */
    public FileObject getFile()
    {
        return file;
    }

    /**
     * Returns the size of the content (in bytes).
     */
    public long getSize() throws FileSystemException
    {
        // Do some checking
        if (!file.getType().hasContent())
        {
            throw new FileSystemException("vfs.provider/get-size-not-file.error", file);
        }
        /*
        if (getThreadData().getState() == STATE_WRITING || getThreadData().getState() == STATE_RANDOM_ACCESS)
        {
            throw new FileSystemException("vfs.provider/get-size-write.error", file);
        }
        */

        try
        {
            // Get the size
            return file.doGetContentSize();
        }
        catch (final Exception exc)
        {
            throw new FileSystemException("vfs.provider/get-size.error", new Object[]{file}, exc);
        }
    }

    /**
     * Returns the last-modified timestamp.
     */
    public long getLastModifiedTime() throws FileSystemException
    {
        /*
        if (getThreadData().getState() == STATE_WRITING || getThreadData().getState() == STATE_RANDOM_ACCESS)
        {
            throw new FileSystemException("vfs.provider/get-last-modified-writing.error", file);
        }
        */
        if (!file.getType().hasAttributes())
        {
            throw new FileSystemException("vfs.provider/get-last-modified-no-exist.error", file);
        }
        try
        {
            return file.doGetLastModifiedTime();
        }
        catch (final Exception e)
        {
            throw new FileSystemException("vfs.provider/get-last-modified.error", file, e);
        }
    }

    /**
     * Sets the last-modified timestamp.
     */
    public void setLastModifiedTime(final long modTime) throws FileSystemException
    {
        /*
        if (getThreadData().getState() == STATE_WRITING || getThreadData().getState() == STATE_RANDOM_ACCESS)
        {
            throw new FileSystemException("vfs.provider/set-last-modified-writing.error", file);
        }
        */
        if (!file.getType().hasAttributes())
        {
            throw new FileSystemException("vfs.provider/set-last-modified-no-exist.error", file);
        }
        try
        {
            file.doSetLastModifiedTime(modTime);
        }
        catch (final Exception e)
        {
            throw new FileSystemException("vfs.provider/set-last-modified.error", file, e);
        }
    }

    /**
     * Returns a read-only map of this file's attributes.
     */
    public Map getAttributes() throws FileSystemException
    {
        if (!file.getType().hasAttributes())
        {
            throw new FileSystemException("vfs.provider/get-attributes-no-exist.error", file);
        }
        if (roAttrs == null)
        {
            try
            {
                attrs = file.doGetAttributes();
                roAttrs = Collections.unmodifiableMap(attrs);
            }
            catch (final Exception e)
            {
                throw new FileSystemException("vfs.provider/get-attributes.error", file, e);
            }
        }
        return roAttrs;
    }

    /**
     * Lists the attributes of this file.
     */
    public String[] getAttributeNames() throws FileSystemException
    {
        getAttributes();
        final Set names = attrs.keySet();
        return (String[]) names.toArray(new String[names.size()]);
    }

    /**
     * Gets the value of an attribute.
     */
    public Object getAttribute(final String attrName)
        throws FileSystemException
    {
        getAttributes();
		if (attrs.containsKey(attrName))
		{
			return attrs.get(attrName);
		}
		return attrs.get(attrName.toLowerCase());
    }

    /**
     * Sets the value of an attribute.
     */
    public void setAttribute(final String attrName, final Object value)
        throws FileSystemException
    {
        if (!file.getType().hasAttributes())
        {
            throw new FileSystemException("vfs.provider/set-attribute-no-exist.error", new Object[]{attrName, file});
        }
        try
        {
            file.doSetAttribute(attrName, value);
        }
        catch (final Exception e)
        {
            throw new FileSystemException("vfs.provider/set-attribute.error", new Object[]{attrName, file}, e);
        }

        if (attrs != null)
        {
            attrs.put(attrName, value);
        }
    }

    /**
     * Returns the certificates used to sign this file.
     */
    public Certificate[] getCertificates() throws FileSystemException
    {
        if (!file.exists())
        {
            throw new FileSystemException("vfs.provider/get-certificates-no-exist.error", file);
        }
        /*
        if (getThreadData().getState() == STATE_WRITING || getThreadData().getState() == STATE_RANDOM_ACCESS)
        {
            throw new FileSystemException("vfs.provider/get-certificates-writing.error", file);
        }
        */

        try
        {
            final Certificate[] certs = file.doGetCertificates();
            if (certs != null)
            {
                return certs;
            }
            else
            {
                return new Certificate[0];
            }
        }
        catch (final Exception e)
        {
            throw new FileSystemException("vfs.provider/get-certificates.error", file, e);
        }
    }

    /**
     * Returns an input stream for reading the content.
     */
    public InputStream getInputStream() throws FileSystemException
    {
        /*
        if (getThreadData().getState() == STATE_WRITING || getThreadData().getState() == STATE_RANDOM_ACCESS)
        {
            throw new FileSystemException("vfs.provider/read-in-use.error", file);
        }
        */

        // Get the raw input stream
        final InputStream instr = file.getInputStream();
        final InputStream wrappedInstr = new FileContentInputStream(file, instr);

        this.getThreadData().addInstr(wrappedInstr);
        streamOpened();

        // setState(STATE_OPENED);
        return wrappedInstr;
    }

    /**
     * Returns an input/output stream to use to read and write the content of the file in an
     * random manner.
     */
    public RandomAccessContent getRandomAccessContent(final RandomAccessMode mode) throws FileSystemException
    {
        /*
        if (getThreadData().getState() != STATE_NONE)
        {
            throw new FileSystemException("vfs.provider/read-in-use.error", file);
        }
        */

        // Get the content
        final RandomAccessContent rastr = file.getRandomAccessContent(mode);

		FileRandomAccessContent rac = new FileRandomAccessContent(file, rastr);
		this.getThreadData().addRastr(rac);
        streamOpened();

        // setState(STATE_OPENED);
        return rac;
    }

    /**
     * Returns an output stream for writing the content.
     */
    public OutputStream getOutputStream() throws FileSystemException
    {
        return getOutputStream(false);
    }

    /**
     * Returns an output stream for writing the content in append mode.
     */
    public OutputStream getOutputStream(boolean bAppend) throws FileSystemException
    {
        /*
        if (getThreadData().getState() != STATE_NONE)
        */
        if (this.getThreadData().getOutstr() != null)
        {
            throw new FileSystemException("vfs.provider/write-in-use.error", file);
        }

        // Get the raw output stream
        final OutputStream outstr = file.getOutputStream(bAppend);

        // Create wrapper
        this.getThreadData().setOutstr(new FileContentOutputStream(file, outstr));
        streamOpened();

        // setState(STATE_OPENED);
        return this.getThreadData().getOutstr();
    }

    /**
     * Closes all resources used by the content, including all streams, readers
     * and writers.
     */
    public void close() throws FileSystemException
    {
        try
        {
            // Close the input stream
            while (getThreadData().getInstrsSize() > 0)
            {
                final FileContentInputStream instr = (FileContentInputStream) getThreadData().removeInstr(0);
                instr.close();
            }

			// Close the randomAccess stream
			while (getThreadData().getRastrsSize() > 0)
			{
				final RandomAccessContent ra = (RandomAccessContent) getThreadData().removeRastr(0);
				try
				{
					ra.close();
				}
				catch (IOException e)
				{
					throw new FileSystemException(e);
				}
			}

			// Close the output stream
            if (this.getThreadData().getOutstr() != null)
            {
                this.getThreadData().closeOutstr();
            }
        }
        finally
        {
            threadData.set(null);
        }
    }

    /**
     * Handles the end of input stream.
     */
    private void endInput(final FileContentInputStream instr)
    {
        getThreadData().removeInstr(instr);
        streamClosed();
        /*
        if (!getThreadData().hasStreams())
        {
            setState(STATE_CLOSED);
        }
        */
    }

    /**
     * Handles the end of random access.
     */
    private void endRandomAccess(RandomAccessContent rac)
    {
		getThreadData().removeRastr(rac);
        streamClosed();
        // setState(STATE_CLOSED);
    }

    /**
     * Handles the end of output stream.
     */
    private void endOutput() throws Exception
    {
        streamClosed();

        this.getThreadData().setOutstr(null);
        // setState(STATE_CLOSED);

        file.endOutput();
    }

    /*
    private void setState(int state)
    {
        getThreadData().setState(state);
    }
    */

    /**
     * check if a input and/or output stream is open.<br />
     * This checks only the scope of the current thread.
     *
     * @return true if this is the case
     */
    public boolean isOpen()
    {
        // return getThreadData().getState() == STATE_OPENED;
        return getThreadData().hasStreams();
    }

    /**
     * check if a input and/or output stream is open.<br />
     * This checks all threads.
     *
     * @return true if this is the case
     */
    public boolean isOpenGlobal()
    {
        synchronized (this)
        {
            return openStreams > 0;
        }
    }

    /**
     * An input stream for reading content.  Provides buffering, and
     * end-of-stream monitoring.
     */
    private final class FileContentInputStream
        extends MonitorInputStream
    {
        // avoid gc
        private final FileObject file;

        FileContentInputStream(final FileObject file, final InputStream instr)
        {
            super(instr);
            this.file = file;
        }

        /**
         * Closes this input stream.
         */
        public void close() throws FileSystemException
        {
            try
            {
                super.close();
            }
            catch (final IOException e)
            {
                throw new FileSystemException("vfs.provider/close-instr.error", file, e);
            }
        }

        /**
         * Called after the stream has been closed.
         */
        protected void onClose() throws IOException
        {
            try
            {
                super.onClose();
            }
            finally
            {
                endInput(this);
            }
        }
    }

    /**
     * An input/output stream for reading/writing content on random positions
     */
    private final class FileRandomAccessContent extends MonitorRandomAccessContent
    {
        // avoid gc
        private final FileObject file;
		private final RandomAccessContent content;

		FileRandomAccessContent(final FileObject file, final RandomAccessContent content)
        {
            super(content);
            this.file = file;
			this.content = content;
		}

        /**
         * Called after the stream has been closed.
         */
        protected void onClose() throws IOException
        {
            try
            {
                super.onClose();
            }
            finally
            {
                endRandomAccess(content);
            }
        }
    }

    /**
     * An output stream for writing content.
     */
    final class FileContentOutputStream extends MonitorOutputStream
    {
        // avoid gc
        private final FileObject file;

        FileContentOutputStream(final FileObject file, final OutputStream outstr)
        {
            super(outstr);
            this.file = file;
        }

        /**
         * Closes this output stream.
         */
        public void close() throws FileSystemException
        {
            try
            {
                super.close();
            }
            catch (final IOException e)
            {
                throw new FileSystemException("vfs.provider/close-outstr.error", file, e);
            }
        }

        /**
         * Called after this stream is closed.
         */
        protected void onClose() throws IOException
        {
            try
            {
                super.onClose();
            }
            finally
            {
                try
                {
                    endOutput();
                }
                catch (Exception e)
                {
                    throw new FileSystemException("vfs.provider/close-outstr.error", file, e);
                }
            }
        }
    }

    /**
     * get the content info. e.g. content-type, content-encoding
     */
    public FileContentInfo getContentInfo() throws FileSystemException
    {
        if (fileContentInfo == null)
        {
            fileContentInfo = fileContentInfoFactory.create(this);
        }

        return fileContentInfo;
    }
}
