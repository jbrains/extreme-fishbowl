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

package org.apache.commons.configuration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;
import junitx.framework.ListAssert;

/**
 * Abstract TestCase for implementations of {@link AbstractConfiguration}.
 *
 * @author Emmanuel Bourg
 * @version $Revision: 515306 $, $Date: 2007-03-06 22:15:00 +0100 (Di, 06 Mrz 2007) $
 */
public abstract class TestAbstractConfiguration extends TestCase
{
    /**
     * Return an abstract configuration with the following data:<br>
     * <pre>
     * key1 = value1
     * key2 = value2
     * list = value1, value2
     * listesc = value1\\,value2
     * </pre>
     */
    protected abstract AbstractConfiguration getConfiguration();

    /**
     * Return an empty configuration.
     */
    protected abstract AbstractConfiguration getEmptyConfiguration();

    public void testGetProperty()
    {
        Configuration config = getConfiguration();
        assertEquals("key1", "value1", config.getProperty("key1"));
        assertEquals("key2", "value2", config.getProperty("key2"));
        assertNull("key3", config.getProperty("key3"));
    }

    public void testList()
    {
        Configuration config = getConfiguration();

        List list = config.getList("list");
        assertNotNull("list not found", config.getProperty("list"));
        assertEquals("list size", 2, list.size());
        assertTrue("'value1' is not in the list", list.contains("value1"));
        assertTrue("'value2' is not in the list", list.contains("value2"));
    }

    /**
     * Tests whether the escape character for list delimiters is recocknized and
     * removed.
     */
    public void testListEscaped()
    {
        assertEquals("Wrong value for escaped list", "value1,value2",
                getConfiguration().getString("listesc"));
    }

    public void testAddPropertyDirect()
    {
        AbstractConfiguration config = getConfiguration();
        config.addPropertyDirect("key3", "value3");
        assertEquals("key3", "value3", config.getProperty("key3"));

        config.addPropertyDirect("key3", "value4");
        config.addPropertyDirect("key3", "value5");
        List list = config.getList("key3");
        assertNotNull("no list found for the 'key3' property", list);

        List expected = new ArrayList();
        expected.add("value3");
        expected.add("value4");
        expected.add("value5");

        ListAssert.assertEquals("values for the 'key3' property", expected, list);
    }

    public void testIsEmpty()
    {
        Configuration config = getConfiguration();
        assertFalse("the configuration is empty", config.isEmpty());
        assertTrue("the configuration is not empty", getEmptyConfiguration().isEmpty());
    }

    public void testContainsKey()
    {
        Configuration config = getConfiguration();
        assertTrue("key1 not found", config.containsKey("key1"));
        assertFalse("key3 found", config.containsKey("key3"));
    }

    public void testClearProperty()
    {
        Configuration config = getConfiguration();
        config.clearProperty("key2");
        assertFalse("key2 not cleared", config.containsKey("key2"));
    }

    public void testGetKeys()
    {
        Configuration config = getConfiguration();
        Iterator keys = config.getKeys();

        List expectedKeys = new ArrayList();
        expectedKeys.add("key1");
        expectedKeys.add("key2");
        expectedKeys.add("list");
        expectedKeys.add("listesc");

        assertNotNull("null iterator", keys);
        assertTrue("empty iterator", keys.hasNext());

        List actualKeys = new ArrayList();
        while (keys.hasNext())
        {
            actualKeys.add(keys.next());
        }

        ListAssert.assertEquals("keys", expectedKeys, actualKeys);
    }

    /**
     * Tests accessing the configuration's logger.
     */
    public void testSetLogger()
    {
        AbstractConfiguration config = getEmptyConfiguration();
        assertNotNull("Default logger is null", config.getLogger());
        Log log = LogFactory.getLog(config.getClass());
        config.setLogger(log);
        assertSame("Logger was not set", log, config.getLogger());
    }
}
