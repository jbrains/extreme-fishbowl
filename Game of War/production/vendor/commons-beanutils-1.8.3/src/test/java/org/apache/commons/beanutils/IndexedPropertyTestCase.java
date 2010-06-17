/*
 * $Id: IndexedPropertyTestCase.java 546480 2007-06-12 13:35:32Z niallp $
 *
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

package org.apache.commons.beanutils;

import java.util.List;
import java.util.ArrayList;
import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;




/**
 * <p>Test Case for the Indexed Properties.</p>
 *
 * @author Niall Pemberton
 * @version $Revision: 546480 $ $Date: 2007-06-12 14:35:32 +0100 (Tue, 12 Jun 2007) $
 */

public class IndexedPropertyTestCase extends TestCase {

    private static final Log log = LogFactory.getLog(IndexedPropertyTestCase.class);

    // ---------------------------------------------------- Instance Variables

    /**
     * The test bean for each test.
     */
    private IndexedTestBean bean = null;
    private BeanUtilsBean beanUtilsBean; 
    private PropertyUtilsBean propertyUtilsBean;
    private String[] testArray;
    private String[] newArray;
    private List testList;
    private List newList;
    private ArrayList arrayList;

    // ---------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public IndexedPropertyTestCase(String name) {
        super(name);
    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() {

        // BeanUtils
        beanUtilsBean = new BeanUtilsBean();
        propertyUtilsBean = beanUtilsBean.getPropertyUtils();

        // initialize Arrays and Lists
        testArray= new String[] {"array-0", "array-1", "array-2"};
        newArray = new String[]  {"newArray-0", "newArray-1", "newArray-2"};

        testList = new ArrayList();
        testList.add("list-0");
        testList.add("list-1");
        testList.add("list-2");

        newList = new ArrayList();
        newList.add("newList-0");
        newList.add("newList-1");
        newList.add("newList-2");

        arrayList = new ArrayList();
        arrayList.add("arrayList-0");
        arrayList.add("arrayList-1");
        arrayList.add("arrayList-2");

        // initialize Test Bean  properties
        bean = new IndexedTestBean();
        bean.setStringArray(testArray);
        bean.setStringList(testList);
        bean.setArrayList(arrayList);
    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(IndexedPropertyTestCase.class));
    }

    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {
        bean = null;
    }


    // ------------------------------------------------ Individual Test Methods

    /**
     * Test IndexedPropertyDescriptor for an Array
     */
    public void testArrayIndexedPropertyDescriptor() {

        try {
            PropertyDescriptor descriptor = propertyUtilsBean.getPropertyDescriptor(bean, "stringArray");
            assertNotNull("No Array Descriptor", descriptor);
            assertEquals("Not IndexedPropertyDescriptor", 
                         IndexedPropertyDescriptor.class,
                         descriptor.getClass());
            assertEquals("PropertDescriptor Type invalid", 
                         testArray.getClass(),
                         descriptor.getPropertyType());
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test IndexedPropertyDescriptor for a List
     */
    public void testListIndexedPropertyDescriptor() {

        try {
            PropertyDescriptor descriptor = propertyUtilsBean.getPropertyDescriptor(bean, "stringList");
            assertNotNull("No List Descriptor", descriptor);
            assertEquals("Not IndexedPropertyDescriptor", 
                         IndexedPropertyDescriptor.class,
                         descriptor.getClass());
            assertEquals("PropertDescriptor Type invalid", 
                         List.class,
                         descriptor.getPropertyType());
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test IndexedPropertyDescriptor for an ArrayList
     */
    public void testArrayListIndexedPropertyDescriptor() {

        try {
            PropertyDescriptor descriptor = propertyUtilsBean.getPropertyDescriptor(bean, "arrayList");
            assertNotNull("No ArrayList Descriptor", descriptor);
            assertEquals("Not IndexedPropertyDescriptor", 
                         IndexedPropertyDescriptor.class,
                         descriptor.getClass());
            assertEquals("PropertDescriptor Type invalid", 
                         ArrayList.class,
                         descriptor.getPropertyType());
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test Read Method for an Array
     */
    public void testArrayReadMethod() {

        try {
            IndexedPropertyDescriptor descriptor = 
                 (IndexedPropertyDescriptor)propertyUtilsBean.getPropertyDescriptor(bean, "stringArray");
            assertNotNull("No Array Read Method", descriptor.getReadMethod());
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test Write Method for an Array
     */
    public void testArrayWriteMethod() {

        try {
            IndexedPropertyDescriptor descriptor = 
                 (IndexedPropertyDescriptor)propertyUtilsBean.getPropertyDescriptor(bean, "stringArray");
            assertNotNull("No Array Write Method", descriptor.getWriteMethod());
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test Indexed Read Method for an Array
     */
    public void testArrayIndexedReadMethod() {

        try {
            IndexedPropertyDescriptor descriptor = 
                 (IndexedPropertyDescriptor)propertyUtilsBean.getPropertyDescriptor(bean, "stringArray");
            assertNotNull("No Array Indexed Read Method", descriptor.getIndexedReadMethod());
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test Indexed Write Method for an Array
     */
    public void testArrayIndexedWriteMethod() {

        try {
            IndexedPropertyDescriptor descriptor = 
                 (IndexedPropertyDescriptor)propertyUtilsBean.getPropertyDescriptor(bean, "stringArray");
            assertNotNull("No Array Indexed Write Method", descriptor.getIndexedWriteMethod());
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test Read Method for a List
     *
     * JDK 1.3.1_04: Test Passes
     * JDK 1.4.2_05: Test Fails - getter which returns java.util.List not returned
     *                            by IndexedPropertyDescriptor.getReadMethod();
     */
    public void testListReadMethod() {

        try {
            IndexedPropertyDescriptor descriptor = 
                 (IndexedPropertyDescriptor)propertyUtilsBean.getPropertyDescriptor(bean, "stringList");
            assertNotNull("No List Read Method", descriptor.getReadMethod());
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test Write Method for a List
     *
     * JDK 1.3.1_04: Test Passes
     * JDK 1.4.2_05: Test Fails - setter whith java.util.List argument not returned
     *                            by IndexedPropertyDescriptor.getWriteMethod();
     */
    public void testListWriteMethod() {

        try {
            IndexedPropertyDescriptor descriptor = 
                 (IndexedPropertyDescriptor)propertyUtilsBean.getPropertyDescriptor(bean, "stringList");
            assertNotNull("No List Write Method", descriptor.getWriteMethod());
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test Indexed Read Method for a List
     */
    public void testListIndexedReadMethod() {

        try {
            IndexedPropertyDescriptor descriptor = 
                 (IndexedPropertyDescriptor)propertyUtilsBean.getPropertyDescriptor(bean, "stringList");
            assertNotNull("No List Indexed Read Method", descriptor.getIndexedReadMethod());
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test Indexed Write Method for a List
     */
    public void testListIndexedWriteMethod() {

        try {
            IndexedPropertyDescriptor descriptor = 
                 (IndexedPropertyDescriptor)propertyUtilsBean.getPropertyDescriptor(bean, "stringList");
            assertNotNull("No List Indexed Write Method", descriptor.getIndexedWriteMethod());
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test Read Method for an ArrayList
     */
    public void testArrayListReadMethod() {

        try {
            IndexedPropertyDescriptor descriptor = 
                 (IndexedPropertyDescriptor)propertyUtilsBean.getPropertyDescriptor(bean, "arrayList");
            assertNotNull("No ArrayList Read Method", descriptor.getReadMethod());
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test Write Method for an ArrayList
     */
    public void testArrayListWriteMethod() {

        try {
            IndexedPropertyDescriptor descriptor = 
                 (IndexedPropertyDescriptor)propertyUtilsBean.getPropertyDescriptor(bean, "arrayList");
            assertNotNull("No ArrayList Write Method", descriptor.getWriteMethod());
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test getting an array property
     */
    public void testGetArray() {
        try {
            assertEquals(testArray, 
                         propertyUtilsBean.getProperty(bean, "stringArray"));
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test getting an array property as a String
     *
     * NOTE: Why does retrieving array just return the first element in the array, whereas
     *       retrieveing a List returns a comma separated list of all the elements?
     */
    public void testGetArrayAsString() {
        try {
            assertEquals("array-0", 
                         beanUtilsBean.getProperty(bean, "stringArray"));
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test getting an indexed item of an Array using getProperty("name[x]")
     */
    public void testGetArrayItemA() {

        try {
            assertEquals("array-1",
                         beanUtilsBean.getProperty(bean, "stringArray[1]"));
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test getting an indexed item of an Array using getIndexedProperty("name")
     */
    public void testGetArrayItemB() {

        try {
            assertEquals("array-1",
                         beanUtilsBean.getIndexedProperty(bean, "stringArray", 1));
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test getting a List
     *
     * JDK 1.3.1_04: Test Passes
     * JDK 1.4.2_05: Test Fails - fails NoSuchMethodException, i.e. reason as testListReadMethod()
     *                            failed.   
     */
    public void testGetList() {

        try {
            assertEquals(testList, 
                         propertyUtilsBean.getProperty(bean, "stringList"));
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test getting a List property as a String
     *
     * JDK 1.3.1_04: Test Passes
     * JDK 1.4.2_05: Test Fails - fails NoSuchMethodException, i.e. reason as testListReadMethod()
     *                            failed.   
     */
    public void testGetListAsString() {

        try {
            assertEquals("list-0", 
                         beanUtilsBean.getProperty(bean, "stringList"));
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test getting an indexed item of a List using getProperty("name[x]")
     */
    public void testGetListItemA() {

        try {
            assertEquals("list-1",
                         beanUtilsBean.getProperty(bean, "stringList[1]"));
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test getting an indexed item of a List using getIndexedProperty("name")
     */
    public void testGetListItemB() {

        try {
            assertEquals("list-1",
                         beanUtilsBean.getIndexedProperty(bean, "stringList", 1));
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test setting an Array property
     *
     * JDK 1.3.1_04 and 1.4.2_05: Test Fails - IllegalArgumentException can't invoke setter, argument type mismatch
     *
     * Fails because of a bug in BeanUtilsBean.setProperty() method. Value is always converted to the array's component
     * type which in this case is a String. Then it calls the setStringArray(String[]) passing a String rather than
     * String[] causing this exception. If there isn't an "index" value then the PropertyType (rather than
     * IndexedPropertyType) should be used.
     *
     */
    public void testSetArray() {
        try {
            beanUtilsBean.setProperty(bean, "stringArray", newArray);
            Object value = bean.getStringArray();
            assertEquals("Type is different", newArray.getClass(), value.getClass());
            String[] array = (String[])value;
            assertEquals("Array Length is different", newArray.length, array.length);
            for (int i = 0; i < array.length; i++) {
                assertEquals("Element " + i + " is different", newArray[i], array[i]);
            }
        } catch(Exception e) {
            log.error("testSetArray()", e);
            fail("Threw exception " + e);
        }
    }


    /**
     * Test setting an indexed item of an Array using setProperty("name[x]", value)
     */
    public void testSetArrayItemA() {

        try {
            beanUtilsBean.setProperty(bean, "stringArray[1]", "modified-1");
            assertEquals("modified-1", bean.getStringArray(1));
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test setting an indexed item of an Array using setIndexedProperty("name", value)
     */
    public void testSetArrayItemB() {

        try {
            propertyUtilsBean.setIndexedProperty(bean, "stringArray", 1, "modified-1");
            assertEquals("modified-1", bean.getStringArray(1));
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test setting a List property
     *
     * JDK 1.3.1_04: Test Passes
     * JDK 1.4.2_05: Test Fails - setter which returns java.util.List not returned
     *                            by IndexedPropertyDescriptor.getWriteMethod() - therefore
     *                            setProperty does nothing and values remain unchanged.
     */
    public void testSetList() {
        try {
            beanUtilsBean.setProperty(bean, "stringList", newList);
            Object value = bean.getStringList();
            assertEquals("Type is different", newList.getClass(), value.getClass());
            List list  = (List)value;
            assertEquals("List size is different", newList.size(), list.size());
            for (int i = 0; i < list.size(); i++) {
                assertEquals("Element " + i + " is different", newList.get(i), list.get(i));
            }
        } catch(Exception e) {
            log.error("testSetList()", e);
            fail("Threw exception " + e);
        }
    }


    /**
     * Test setting an indexed item of a List using setProperty("name[x]", value)
     */
    public void testSetListItemA() {

        try {
            beanUtilsBean.setProperty(bean, "stringList[1]", "modified-1");
            assertEquals("modified-1", bean.getStringList(1));
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test setting an indexed item of a List using setIndexedProperty("name", value)
     */
    public void testSetListItemB() {

        try {
            propertyUtilsBean.setIndexedProperty(bean, "stringList", 1, "modified-1");
            assertEquals("modified-1", bean.getStringList(1));
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }


    /**
     * Test getting an ArrayList
     */
    public void testGetArrayList() {

        try {
            assertEquals(arrayList, 
                         propertyUtilsBean.getProperty(bean, "arrayList"));
        } catch(Exception e) {
            fail("Threw exception " + e);
        }
    }

    /**
     * Test setting an ArrayList property
     */
    public void testSetArrayList() {
        try {
            beanUtilsBean.setProperty(bean, "arrayList", newList);
            Object value = bean.getArrayList();
            assertEquals("Type is different", newList.getClass(), value.getClass());
            List list  = (List)value;
            assertEquals("List size is different", newList.size(), list.size());
            for (int i = 0; i < list.size(); i++) {
                assertEquals("Element " + i + " is different", newList.get(i), list.get(i));
            }
        } catch(Exception e) {
            log.error("testSetList()", e);
            fail("Threw exception " + e);
        }
    }

}
