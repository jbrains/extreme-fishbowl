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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The main entry point for the VFS.  Used to create {@link FileSystemManager}
 * instances.
 *
 * @author <a href="mailto:adammurdoch@apache.org">Adam Murdoch</a>
 * @version $Revision: 480428 $ $Date: 2006-11-29 07:15:24 +0100 (Mi, 29 Nov 2006) $
 */
public class VFS
{
    private static Boolean URI_STYLE = null;

    private static FileSystemManager instance;

    private VFS()
    {
    }

    /**
     * Returns the default {@link FileSystemManager} instance
     */
    public static synchronized FileSystemManager getManager()
        throws FileSystemException
    {
        if (instance == null)
        {
            instance = createManager("org.apache.commons.vfs.impl.StandardFileSystemManager");
        }
        return instance;
    }

    /**
     * Creates a file system manager instance.
     */
    private static FileSystemManager createManager(final String managerClassName)
        throws FileSystemException
    {
        try
        {
            // Create instance
            final Class mgrClass = Class.forName(managerClassName);
            final FileSystemManager mgr = (FileSystemManager) mgrClass.newInstance();

            /*
            try
            {
                // Set the logger
                final Method setLogMethod = mgrClass.getMethod("setLogger", new Class[]{Log.class});
                final Log logger = LogFactory.getLog(VFS.class);
                setLogMethod.invoke(mgr, new Object[]{logger});
            }
            catch (final NoSuchMethodException e)
            {
                // Ignore; don't set the logger
            }
            */

            try
            {
                // Initialise
                final Method initMethod = mgrClass.getMethod("init", null);
                initMethod.invoke(mgr, null);
            }
            catch (final NoSuchMethodException e)
            {
                // Ignore; don't initialize
            }

            return mgr;
        }
        catch (final InvocationTargetException e)
        {
            throw new FileSystemException("vfs/create-manager.error",
                managerClassName,
                e.getTargetException());
        }
        catch (final Exception e)
        {
            throw new FileSystemException("vfs/create-manager.error",
                managerClassName,
                e);
        }
    }

    public static boolean isUriStyle()
    {
        if (URI_STYLE == null)
        {
            URI_STYLE = Boolean.FALSE;
        }
        return URI_STYLE.booleanValue();
    }

    public static void setUriStyle(boolean uriStyle)
    {
        if (URI_STYLE != null && URI_STYLE.booleanValue() != uriStyle)
        {
            throw new IllegalStateException("URI STYLE ALREADY SET TO");
        }
        URI_STYLE = uriStyle ? Boolean.TRUE : Boolean.FALSE;
    }
}
