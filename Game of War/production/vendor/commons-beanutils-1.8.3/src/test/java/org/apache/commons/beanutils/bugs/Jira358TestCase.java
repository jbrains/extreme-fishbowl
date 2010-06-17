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
package org.apache.commons.beanutils.bugs;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.TestBean;

/**
 * See https://issues.apache.org/jira/browse/BEANUTILS-358
 * <p />
 *
 * @version $Revision: 808710 $ $Date: 2009-08-28 02:01:28 +0100 (Fri, 28 Aug 2009) $
 */
public class Jira358TestCase extends TestCase {

    /**
     * Create a test case with the specified name.
     *
     * @param name The name of the test
     */
    public Jira358TestCase(String name) {
        super(name);
    }

    /**
     * Run the Test.
     *
     * @param args Arguments
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Create a test suite for this test.
     *
     * @return a test suite
     */
    public static Test suite() {
        return (new TestSuite(Jira358TestCase.class));
    }

    /**
     * Set up.
     *
     * @throws java.lang.Exception
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Tear Down.
     *
     * @throws java.lang.Exception
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test {@link PropertyUtils#getIndexedProperty(Object, String, int)}
     */
    public void testPropertyUtils_getIndexedProperty_Array() throws Exception {
        
        TestBean bean = new TestBean();
        try {
            PropertyUtils.getIndexedProperty(bean, "intArray", bean.getIntArray().length);
            fail("Expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
            // expected result
        }
    }

    /**
     * Test {@link PropertyUtils#getIndexedProperty(Object, String, int)}
     */
    public void testPropertyUtils_getIndexedProperty_List() throws Exception {
        
        TestBean bean = new TestBean();
        try {
            PropertyUtils.getIndexedProperty(bean, "listIndexed", bean.getListIndexed().size());
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // expected result
        }
    }
}
