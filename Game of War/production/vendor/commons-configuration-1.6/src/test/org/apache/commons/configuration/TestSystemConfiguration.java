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

import java.util.Properties;

import junit.framework.TestCase;

/**
 * Tests for MapConfiguration.
 *
 * @author Emmanuel Bourg
 * @version $Revision: 727834 $, $Date: 2008-12-18 23:16:32 +0100 (Do, 18 Dez 2008) $
 */
public class TestSystemConfiguration extends TestCase
{
    public void testSystemConfiguration()
    {
        Properties props = System.getProperties();
        props.put("test.number", "123");

        Configuration conf = new SystemConfiguration();
        assertEquals("number", 123, conf.getInt("test.number"));
    }

    public void testSetSystemProperties()
    {
        PropertiesConfiguration props = new PropertiesConfiguration();
        props.addProperty("test.name", "Apache");
        SystemConfiguration conf = new SystemConfiguration();
        conf.setSystemProperties(props);
        assertEquals("System Properties", "Apache", System.getProperty("test.name"));       
    }
}
