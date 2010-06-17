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

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

import com.mockobjects.servlet.MockServletConfig;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.TestAbstractConfiguration;

/**
 * Test case for the {@link ServletConfiguration} class.
 *
 * @author Emmanuel Bourg
 * @version $Revision: 515306 $, $Date: 2007-03-06 22:15:00 +0100 (Di, 06 Mrz 2007) $
 */
public class TestServletConfiguration extends TestAbstractConfiguration
{
    protected AbstractConfiguration getConfiguration()
    {
        final MockServletConfig config = new MockServletConfig();
        config.setInitParameter("key1", "value1");
        config.setInitParameter("key2", "value2");
        config.setInitParameter("list", "value1, value2");
        config.setInitParameter("listesc", "value1\\,value2");

        Servlet servlet = new HttpServlet() {
            public ServletConfig getServletConfig()
            {
                return config;
            }
        };

        return new ServletConfiguration(servlet);
    }

    protected AbstractConfiguration getEmptyConfiguration()
    {
        return new ServletConfiguration(new MockServletConfig());
    }

    public void testAddPropertyDirect()
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

    public void testClearProperty()
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
