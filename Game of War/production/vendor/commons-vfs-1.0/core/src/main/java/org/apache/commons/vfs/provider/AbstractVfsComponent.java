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

import org.apache.commons.logging.Log;
import org.apache.commons.vfs.FileSystemException;

/**
 * A partial {@link VfsComponent} implementation.
 *
 * @author <a href="mailto:adammurdoch@apache.org">Adam Murdoch</a>
 * @version $Revision: 480428 $ $Date: 2006-11-29 07:15:24 +0100 (Mi, 29 Nov 2006) $
 */
public abstract class AbstractVfsComponent
    implements VfsComponent
{
    private VfsComponentContext context;
    private Log log;

    /**
     * Sets the Logger to use for the component.
     */
    public final void setLogger(final Log log)
    {
        this.log = log;
    }

    /**
     * Sets the context for this file system provider.
     */
    public final void setContext(final VfsComponentContext context)
    {
        this.context = context;
    }

    /**
     * Initialises the component.  This implementation does nothing.
     */
    public void init() throws FileSystemException
    {
    }

    /**
     * Closes the provider.  This implementation does nothing.
     */
    public void close()
    {
    }

    /**
     * Returns the logger for this file system to use.
     */
    protected final Log getLogger()
    {
        return log;
    }

    /**
     * Returns the context for this provider.
     */
    protected final VfsComponentContext getContext()
    {
        return context;
    }
}
