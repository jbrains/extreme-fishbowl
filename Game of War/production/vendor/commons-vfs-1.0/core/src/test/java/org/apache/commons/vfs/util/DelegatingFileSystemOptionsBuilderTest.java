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
package org.apache.commons.vfs.util;

import junit.framework.TestCase;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.impl.StandardFileSystemManager;
import org.apache.commons.vfs.provider.http.HttpFileSystemConfigBuilder;
import org.apache.commons.vfs.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.commons.vfs.provider.sftp.TrustEveryoneUserInfo;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Some tests for the DelegatingFileSystemOptionsBuilder
 * 
 * @author <a href="mailto:imario@apache.org">Mario Ivankovits</a>
 * @version $Revision: 480428 $ $Date: 2006-11-29 07:15:24 +0100 (Mi, 29 Nov 2006) $
 */
public class DelegatingFileSystemOptionsBuilderTest extends TestCase
{
    private StandardFileSystemManager fsm = null;

    protected void setUp() throws Exception
    {
        super.tearDown();

        // get a full blown, fully functional manager
        fsm = new StandardFileSystemManager();
        fsm.init();
    }


    protected void tearDown() throws Exception
    {
        if (fsm != null)
        {
            fsm.close();
        }

        super.tearDown();
    }

    public void testDelegatingGood() throws Throwable
    {
        final String[] identityPaths = new String[]
        {
            "/file1",
            "/file2",
        };

        FileSystemOptions opts = new FileSystemOptions();
        DelegatingFileSystemOptionsBuilder delgate = new DelegatingFileSystemOptionsBuilder(fsm);

        delgate.setConfigString(opts, "http", "proxyHost", "proxy");
        delgate.setConfigString(opts, "http", "proxyPort", "8080");
        delgate.setConfigClass(opts, "sftp", "userinfo", TrustEveryoneUserInfo.class);
        delgate.setConfigStrings(opts, "sftp", "identities", identityPaths);

        assertEquals("http.proxyHost", HttpFileSystemConfigBuilder.getInstance().getProxyHost(opts), "proxy");
        assertEquals("http.proxyPort", HttpFileSystemConfigBuilder.getInstance().getProxyPort(opts), 8080);
        assertEquals("sftp.userInfo", SftpFileSystemConfigBuilder.getInstance().getUserInfo(opts).getClass(), TrustEveryoneUserInfo.class);

        File identities[] = SftpFileSystemConfigBuilder.getInstance().getIdentities(opts);
        assertNotNull("sftp.identities", identities);
        assertEquals("sftp.identities size", identities.length, identityPaths.length);
        for (int iterIdentities = 0; iterIdentities < identities.length; iterIdentities++)
        {
            assertEquals("sftp.identities #" + iterIdentities,
                identities[iterIdentities].getAbsolutePath(),
                new File(identityPaths[iterIdentities]).getAbsolutePath());
        }
    }

    public void testDelegatingBad() throws Throwable
    {
        FileSystemOptions opts = new FileSystemOptions();
        DelegatingFileSystemOptionsBuilder delgate = new DelegatingFileSystemOptionsBuilder(fsm);

        try
        {
            delgate.setConfigString(opts, "http", "proxyPort", "wrong_port");
            fail();
        }
        catch (FileSystemException e)
        {
            assertEquals(e.getCause().getClass(), InvocationTargetException.class);
            assertEquals(((InvocationTargetException) e.getCause()).getTargetException().getClass(), NumberFormatException.class);
        }

        try
        {
            delgate.setConfigClass(opts, "sftp", "userinfo", String.class);
            fail();
        }
        catch (FileSystemException e)
        {
            assertEquals(e.getCode(), "vfs.provider/config-value-invalid.error");
        }
    }
}
