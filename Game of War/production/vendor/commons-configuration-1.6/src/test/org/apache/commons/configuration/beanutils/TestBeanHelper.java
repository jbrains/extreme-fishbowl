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
package org.apache.commons.configuration.beanutils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationRuntimeException;

import junit.framework.TestCase;

/**
 * Test class for BeanHelper.
 *
 * @since 1.3
 * @author Oliver Heger
 * @version $Id: TestBeanHelper.java 570462 2007-08-28 15:56:49Z oheger $
 */
public class TestBeanHelper extends TestCase
{
    /** Constant for the name of the test bean factory. */
    private static final String TEST_FACTORY = "testFactory";

    /**
     * Stores the default bean factory. Because this is a static field in
     * BeanHelper it is temporarily stored and reset after the tests.
     */
    private BeanFactory tempDefaultBeanFactory;

    protected void setUp() throws Exception
    {
        super.setUp();
        tempDefaultBeanFactory = BeanHelper.getDefaultBeanFactory();
        deregisterFactories();
    }

    protected void tearDown() throws Exception
    {
        deregisterFactories();

        // Reset old default bean factory
        BeanHelper.setDefaultBeanFactory(tempDefaultBeanFactory);

        super.tearDown();
    }

    /**
     * Removes all bean factories that might have been registered during a test.
     */
    private void deregisterFactories()
    {
        for (Iterator it = BeanHelper.registeredFactoryNames().iterator(); it
                .hasNext();)
        {
            BeanHelper.deregisterBeanFactory((String) it.next());
        }
        assertTrue("Remaining registered bean factories", BeanHelper
                .registeredFactoryNames().isEmpty());
    }

    /**
     * Tests registering a new bean factory.
     */
    public void testRegisterBeanFactory()
    {
        assertTrue("List of registered factories is not empty", BeanHelper
                .registeredFactoryNames().isEmpty());
        BeanHelper.registerBeanFactory(TEST_FACTORY, new TestBeanFactory());
        assertEquals("Wrong number of registered factories", 1, BeanHelper
                .registeredFactoryNames().size());
        assertTrue("Test factory is not contained", BeanHelper
                .registeredFactoryNames().contains(TEST_FACTORY));
    }

    /**
     * Tries to register a null factory. This should cause an exception.
     */
    public void testRegisterBeanFactoryNull()
    {
        try
        {
            BeanHelper.registerBeanFactory(TEST_FACTORY, null);
            fail("Could register null factory!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tries to register a bean factory with a null name. This should cause an
     * exception.
     */
    public void testRegisterBeanFactoryNullName()
    {
        try
        {
            BeanHelper.registerBeanFactory(null, new TestBeanFactory());
            fail("Could register factory with null name!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests to deregister a bean factory.
     */
    public void testDeregisterBeanFactory()
    {
        assertNull("deregistering non existing factory", BeanHelper
                .deregisterBeanFactory(TEST_FACTORY));
        assertNull("deregistering null factory", BeanHelper
                .deregisterBeanFactory(null));
        BeanFactory factory = new TestBeanFactory();
        BeanHelper.registerBeanFactory(TEST_FACTORY, factory);
        assertSame("Could not deregister factory", factory, BeanHelper
                .deregisterBeanFactory(TEST_FACTORY));
        assertTrue("List of factories is not empty", BeanHelper
                .registeredFactoryNames().isEmpty());
    }

    /**
     * Tests whether the default bean factory is correctly initialized.
     */
    public void testGetDefaultBeanFactory()
    {
        assertSame("Incorrect default bean factory",
                DefaultBeanFactory.INSTANCE, tempDefaultBeanFactory);
    }

    /**
     * Tests setting the default bean factory to null. This should caus an
     * exception.
     */
    public void testSetDefaultBeanFactoryNull()
    {
        try
        {
            BeanHelper.setDefaultBeanFactory(null);
            fail("Could set default bean factory to null!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests initializing a bean.
     */
    public void testInitBean()
    {
        BeanHelper.setDefaultBeanFactory(new TestBeanFactory());
        TestBeanDeclaration data = setUpBeanDeclaration();
        TestBean bean = new TestBean();
        BeanHelper.initBean(bean, data);
        checkBean(bean);
    }

    /**
     * Tests initializing a bean when the bean declaration does not contain any
     * data.
     */
    public void testInitBeanWithNoData()
    {
        TestBeanDeclaration data = new TestBeanDeclaration();
        TestBean bean = new TestBean();
        BeanHelper.initBean(bean, data);
        assertNull("Wrong string property", bean.getStringValue());
        assertEquals("Wrong int property", 0, bean.getIntValue());
        assertNull("Buddy was set", bean.getBuddy());
    }

    /**
     * Tries to initialize a bean with a bean declaration that contains an
     * invalid property value. This should cause an exception.
     */
    public void testInitBeanWithInvalidProperty()
    {
        TestBeanDeclaration data = setUpBeanDeclaration();
        data.getBeanProperties().put("nonExistingProperty", Boolean.TRUE);
        try
        {
            BeanHelper.initBean(new TestBean(), data);
            fail("Could initialize non existing property!");
        }
        catch (ConfigurationRuntimeException cex)
        {
            // ok
        }
    }

    /**
     * Tests creating a bean. All necessary information is stored in the bean
     * declaration.
     */
    public void testCreateBean()
    {
        TestBeanFactory factory = new TestBeanFactory();
        BeanHelper.registerBeanFactory(TEST_FACTORY, factory);
        TestBeanDeclaration data = setUpBeanDeclaration();
        data.setBeanFactoryName(TEST_FACTORY);
        data.setBeanClassName(TestBean.class.getName());
        checkBean((TestBean) BeanHelper.createBean(data, null));
        assertNull("A parameter was passed", factory.parameter);
    }

    /**
     * Tests creating a bean when no bean declaration is provided. This should
     * cause an exception.
     */
    public void testCreateBeanWithNullDeclaration()
    {
        try
        {
            BeanHelper.createBean(null);
            fail("Could create bean with null declaration!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests creating a bean. The bean's class is specified as the default class
     * argument.
     */
    public void testCreateBeanWithDefaultClass()
    {
        BeanHelper.registerBeanFactory(TEST_FACTORY, new TestBeanFactory());
        TestBeanDeclaration data = setUpBeanDeclaration();
        data.setBeanFactoryName(TEST_FACTORY);
        checkBean((TestBean) BeanHelper.createBean(data, TestBean.class));
    }

    /**
     * Tests creating a bean when the bean's class is specified as the default
     * class of the bean factory.
     */
    public void testCreateBeanWithFactoryDefaultClass()
    {
        TestBeanFactory factory = new TestBeanFactory();
        factory.supportsDefaultClass = true;
        BeanHelper.registerBeanFactory(TEST_FACTORY, factory);
        TestBeanDeclaration data = setUpBeanDeclaration();
        data.setBeanFactoryName(TEST_FACTORY);
        checkBean((TestBean) BeanHelper.createBean(data, null));
    }

    /**
     * Tries to create a bean when no class is provided. This should cause an
     * exception.
     */
    public void testCreateBeanWithNoClass()
    {
        BeanHelper.registerBeanFactory(TEST_FACTORY, new TestBeanFactory());
        TestBeanDeclaration data = setUpBeanDeclaration();
        data.setBeanFactoryName(TEST_FACTORY);
        try
        {
            BeanHelper.createBean(data, null);
            fail("Could create bean without class!");
        }
        catch (ConfigurationRuntimeException cex)
        {
            // ok
        }
    }

    /**
     * Tries to create a bean with a non existing class. This should cause an
     * exception.
     */
    public void testCreateBeanWithInvalidClass()
    {
        BeanHelper.registerBeanFactory(TEST_FACTORY, new TestBeanFactory());
        TestBeanDeclaration data = setUpBeanDeclaration();
        data.setBeanFactoryName(TEST_FACTORY);
        data.setBeanClassName("non.existing.ClassName");
        try
        {
            BeanHelper.createBean(data, null);
            fail("Could create bean of an unexisting class!");
        }
        catch (ConfigurationRuntimeException cex)
        {
            // ok
        }
    }

    /**
     * Tests creating a bean using the default bean factory.
     */
    public void testCreateBeanWithDefaultFactory()
    {
        BeanHelper.setDefaultBeanFactory(new TestBeanFactory());
        TestBeanDeclaration data = setUpBeanDeclaration();
        data.setBeanClassName(TestBean.class.getName());
        checkBean((TestBean) BeanHelper.createBean(data, null));
    }

    /**
     * Tests creating a bean using a non registered factory.
     */
    public void testCreateBeanWithUnknownFactory()
    {
        TestBeanDeclaration data = setUpBeanDeclaration();
        data.setBeanFactoryName(TEST_FACTORY);
        data.setBeanClassName(TestBean.class.getName());
        try
        {
            BeanHelper.createBean(data, null);
            fail("Could create bean with non registered factory!");
        }
        catch (ConfigurationRuntimeException cex)
        {
            // ok
        }
    }

    /**
     * Tests creating a bean when the factory throws an exception.
     */
    public void testCreateBeanWithException()
    {
        BeanHelper.registerBeanFactory(TEST_FACTORY, new TestBeanFactory());
        TestBeanDeclaration data = setUpBeanDeclaration();
        data.setBeanFactoryName(TEST_FACTORY);
        data.setBeanClassName(getClass().getName());
        try
        {
            BeanHelper.createBean(data, null);
            fail("Could create bean of wrong class!");
        }
        catch (ConfigurationRuntimeException cex)
        {
            // ok
        }
    }

    /**
     * Tests if a parameter is correctly passed to the bean factory.
     */
    public void testCreateBeanWithParameter()
    {
        Object param = new Integer(42);
        TestBeanFactory factory = new TestBeanFactory();
        BeanHelper.registerBeanFactory(TEST_FACTORY, factory);
        TestBeanDeclaration data = setUpBeanDeclaration();
        data.setBeanFactoryName(TEST_FACTORY);
        data.setBeanClassName(TestBean.class.getName());
        checkBean((TestBean) BeanHelper.createBean(data, null, param));
        assertSame("Wrong parameter", param, factory.parameter);
    }

    /**
     * Returns an initialized bean declaration.
     *
     * @return the bean declaration
     */
    private TestBeanDeclaration setUpBeanDeclaration()
    {
        TestBeanDeclaration data = new TestBeanDeclaration();
        Map properties = new HashMap();
        properties.put("stringValue", "testString");
        properties.put("intValue", "42");
        data.setBeanProperties(properties);
        TestBeanDeclaration buddyData = new TestBeanDeclaration();
        Map properties2 = new HashMap();
        properties2.put("stringValue", "Another test string");
        properties2.put("intValue", new Integer(100));
        buddyData.setBeanProperties(properties2);
        buddyData.setBeanClassName(TestBean.class.getName());
        if (BeanHelper.getDefaultBeanFactory() == null)
        {
            buddyData.setBeanFactoryName(TEST_FACTORY);
        }
        Map nested = new HashMap();
        nested.put("buddy", buddyData);
        data.setNestedBeanDeclarations(nested);
        return data;
    }

    /**
     * Tests if the bean was correctly initialized from the data of the test
     * bean declaration.
     *
     * @param bean the bean to be checked
     */
    private void checkBean(TestBean bean)
    {
        assertEquals("Wrong string property", "testString", bean
                .getStringValue());
        assertEquals("Wrong int property", 42, bean.getIntValue());
        TestBean buddy = bean.getBuddy();
        assertNotNull("Buddy was not set", buddy);
        assertEquals("Wrong string property in buddy", "Another test string",
                buddy.getStringValue());
        assertEquals("Wrong int property in buddy", 100, buddy.getIntValue());
    }

    /**
     * A simple bean class used for testing creation operations.
     */
    public static class TestBean
    {
        private String stringValue;

        private int intValue;

        private TestBean buddy;

        public TestBean getBuddy()
        {
            return buddy;
        }

        public void setBuddy(TestBean buddy)
        {
            this.buddy = buddy;
        }

        public int getIntValue()
        {
            return intValue;
        }

        public void setIntValue(int intValue)
        {
            this.intValue = intValue;
        }

        public String getStringValue()
        {
            return stringValue;
        }

        public void setStringValue(String stringValue)
        {
            this.stringValue = stringValue;
        }
    }

    /**
     * An implementation of the BeanFactory interface used for testing. This
     * implementation is really simple: If the TestBean class is provided, a new
     * instance will be created. Otherwise an exception is thrown.
     */
    static class TestBeanFactory implements BeanFactory
    {
        Object parameter;

        boolean supportsDefaultClass;

        public Object createBean(Class beanClass, BeanDeclaration data, Object param)
                throws Exception
        {
            parameter = param;
            if (TestBean.class.equals(beanClass))
            {
                TestBean bean = new TestBean();
                BeanHelper.initBean(bean, data);
                return bean;
            }
            else
            {
                throw new IllegalArgumentException("Unsupported class: "
                        + beanClass);
            }
        }

        /**
         * Returns the default class, but only if the supportsDefaultClass flag
         * is set.
         */
        public Class getDefaultBeanClass()
        {
            return supportsDefaultClass ? TestBean.class : null;
        }
    }

    /**
     * A test implementation of the BeanDeclaration interface. This
     * implementation allows to set the values directly, which should be
     * returned by the methods required by the BeanDeclaration interface.
     */
    static class TestBeanDeclaration implements BeanDeclaration
    {
        private String beanClassName;

        private String beanFactoryName;

        private Object beanFactoryParameter;

        private Map beanProperties;

        private Map nestedBeanDeclarations;

        public String getBeanClassName()
        {
            return beanClassName;
        }

        public void setBeanClassName(String beanClassName)
        {
            this.beanClassName = beanClassName;
        }

        public String getBeanFactoryName()
        {
            return beanFactoryName;
        }

        public void setBeanFactoryName(String beanFactoryName)
        {
            this.beanFactoryName = beanFactoryName;
        }

        public Object getBeanFactoryParameter()
        {
            return beanFactoryParameter;
        }

        public void setBeanFactoryParameter(Object beanFactoryParameter)
        {
            this.beanFactoryParameter = beanFactoryParameter;
        }

        public Map getBeanProperties()
        {
            return beanProperties;
        }

        public void setBeanProperties(Map beanProperties)
        {
            this.beanProperties = beanProperties;
        }

        public Map getNestedBeanDeclarations()
        {
            return nestedBeanDeclarations;
        }

        public void setNestedBeanDeclarations(Map nestedBeanDeclarations)
        {
            this.nestedBeanDeclarations = nestedBeanDeclarations;
        }
    }
}
