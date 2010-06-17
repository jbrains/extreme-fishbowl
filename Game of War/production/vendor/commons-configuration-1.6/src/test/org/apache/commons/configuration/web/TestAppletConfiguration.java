/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.configuration.web;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.configuration.TestAbstractConfiguration;

import java.applet.Applet;
import java.util.Properties;

/**
 * Test case for the {@link AppletConfiguration} class.
 *
 * @author Emmanuel Bourg
 * @version $Revision: 515306 $, $Date: 2005-02-26 13:56:39 +0100 (Sa, 26 Feb
 * 2005) $
 */
public class TestAppletConfiguration extends TestAbstractConfiguration
{
    /** A flag whether tests with an applet can be run. */
    boolean supportsApplet;

    /**
     * Initializes the tests. This implementation checks whether an applet can
     * be used. Some environments, which do not support a GUI, don't allow
     * creating an <code>Applet</code> instance. If we are in such an
     * environment, some tests need to behave differently or be completely
     * dropped.
     */
    protected void setUp() throws Exception
    {
        try
        {
            new Applet();
            supportsApplet = true;
        }
        catch (Exception ex)
        {
            // cannot use applets
            supportsApplet = false;
        }
    }

    protected AbstractConfiguration getConfiguration()
    {
        final Properties parameters = new Properties();
        parameters.setProperty("key1", "value1");
        parameters.setProperty("key2", "value2");
        parameters.setProperty("list", "value1, value2");
        parameters.setProperty("listesc", "value1\\,value2");

        if (supportsApplet)
        {
            Applet applet = new Applet()
            {
                public String getParameter(String key)
                {
                    return parameters.getProperty(key);
                }

                public String[][] getParameterInfo()
                {
                    return new String[][]
                    {
                    { "key1", "String", "" },
                    { "key2", "String", "" },
                    { "list", "String[]", "" },
                    { "listesc", "String", "" } };
                }
            };

            return new AppletConfiguration(applet);
        }
        else
        {
            return new MapConfiguration(parameters);
        }
    }

    protected AbstractConfiguration getEmptyConfiguration()
    {
        if (supportsApplet)
        {
            return new AppletConfiguration(new Applet());
        }
        else
        {
            return new BaseConfiguration();
        }
    }

    public void testAddPropertyDirect()
    {
        if (supportsApplet)
        {
            try
            {
                super.testAddPropertyDirect();
                fail("addPropertyDirect should throw an UnsupportedException");
            }
            catch (UnsupportedOperationException e)
            {
                // ok
            }
        }
    }

    public void testClearProperty()
    {
        if (supportsApplet)
        {
            try
            {
                super.testClearProperty();
                fail("testClearProperty should throw an UnsupportedException");
            }
            catch (UnsupportedOperationException e)
            {
                // ok
            }
        }
    }
}
