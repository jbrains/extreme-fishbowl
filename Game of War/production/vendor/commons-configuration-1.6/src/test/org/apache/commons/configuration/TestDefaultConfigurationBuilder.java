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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import junit.framework.TestCase;

import org.apache.commons.configuration.beanutils.BeanHelper;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.lang.text.StrLookup;

/**
 * Test class for DefaultConfigurationBuilder.
 *
 * @author Oliver Heger
 * @version $Id: TestDefaultConfigurationBuilder.java 727834 2008-12-18 22:16:32Z rgoers $
 */
public class TestDefaultConfigurationBuilder extends TestCase
{
    /** Test configuration definition file. */
    private static final File TEST_FILE = new File(
            "conf/testDigesterConfiguration.xml");

    private static final File ADDITIONAL_FILE = new File(
            "conf/testDigesterConfiguration2.xml");

    private static final File OPTIONAL_FILE = new File(
            "conf/testDigesterOptionalConfiguration.xml");

    private static final File OPTIONALEX_FILE = new File(
            "conf/testDigesterOptionalConfigurationEx.xml");

    private static final File MULTI_FILE = new File(
            "conf/testDigesterConfiguration3.xml");

    private static final File INIT_FILE = new File(
            "conf/testComplexInitialization.xml");

    private static final File CLASS_FILE = new File(
            "conf/testExtendedClass.xml");

    private static final File PROVIDER_FILE = new File(
            "conf/testConfigurationProvider.xml");

    private static final File EXTENDED_PROVIDER_FILE = new File(
            "conf/testExtendedXMLConfigurationProvider.xml");

    private static final File GLOBAL_LOOKUP_FILE = new File(
            "conf/testGlobalLookup.xml");

    private static final File SYSTEM_PROPS_FILE = new File(
            "conf/testSystemProperties.xml");

    /** Constant for the name of an optional configuration.*/
    private static final String OPTIONAL_NAME = "optionalConfig";

    /** Stores the object to be tested. */
    DefaultConfigurationBuilder factory;

    protected void setUp() throws Exception
    {
        super.setUp();
        System
                .setProperty("java.naming.factory.initial",
                        "org.apache.commons.configuration.MockInitialContextFactory");
        System.setProperty("test_file_xml", "test.xml");
        System.setProperty("test_file_combine", "testcombine1.xml");
        factory = new DefaultConfigurationBuilder();
        factory.clearErrorListeners();  // avoid exception messages
    }

    /**
     * Tests the isReservedNode() method of ConfigurationDeclaration.
     */
    public void testConfigurationDeclarationIsReserved()
    {
        DefaultConfigurationBuilder.ConfigurationDeclaration decl = new DefaultConfigurationBuilder.ConfigurationDeclaration(
                factory, factory);
        DefaultConfigurationNode parent = new DefaultConfigurationNode();
        DefaultConfigurationNode nd = new DefaultConfigurationNode("at");
        parent.addAttribute(nd);
        assertTrue("Attribute at not recognized", decl.isReservedNode(nd));
        nd = new DefaultConfigurationNode("optional");
        parent.addAttribute(nd);
        assertTrue("Attribute optional not recognized", decl.isReservedNode(nd));
        nd = new DefaultConfigurationNode("config-class");
        parent.addAttribute(nd);
        assertTrue("Inherited attribute not recognized", decl
                .isReservedNode(nd));
        nd = new DefaultConfigurationNode("different");
        parent.addAttribute(nd);
        assertFalse("Wrong reserved attribute", decl.isReservedNode(nd));
        nd = new DefaultConfigurationNode("at");
        parent.addChild(nd);
        assertFalse("Node type not evaluated", decl.isReservedNode(nd));
    }

    /**
     * Tests if the at attribute is correctly detected as reserved attribute.
     */
    public void testConfigurationDeclarationIsReservedAt()
    {
        checkOldReservedAttribute("at");
    }

    /**
     * Tests if the optional attribute is correctly detected as reserved
     * attribute.
     */
    public void testConfigurationDeclarationIsReservedOptional()
    {
        checkOldReservedAttribute("optional");
    }

    /**
     * Tests if special reserved attributes are recognized by the
     * isReservedNode() method. For compatibility reasons the attributes "at"
     * and "optional" are also treated as reserved attributes, but only if there
     * are no corresponding attributes with the "config-" prefix.
     *
     * @param name the attribute name
     */
    private void checkOldReservedAttribute(String name)
    {
        DefaultConfigurationBuilder.ConfigurationDeclaration decl = new DefaultConfigurationBuilder.ConfigurationDeclaration(
                factory, factory);
        DefaultConfigurationNode parent = new DefaultConfigurationNode();
        DefaultConfigurationNode nd = new DefaultConfigurationNode("config-"
                + name);
        parent.addAttribute(nd);
        assertTrue("config-" + name + " attribute not recognized", decl
                .isReservedNode(nd));
        DefaultConfigurationNode nd2 = new DefaultConfigurationNode(name);
        parent.addAttribute(nd2);
        assertFalse(name + " is reserved though config- exists", decl
                .isReservedNode(nd2));
        assertTrue("config- attribute not recognized when " + name + " exists",
                decl.isReservedNode(nd));
    }

    /**
     * Tests access to certain reserved attributes of a
     * ConfigurationDeclaration.
     */
    public void testConfigurationDeclarationGetAttributes()
    {
        factory.addProperty("xml.fileName", "test.xml");
        DefaultConfigurationBuilder.ConfigurationDeclaration decl = new DefaultConfigurationBuilder.ConfigurationDeclaration(
                factory, factory.configurationAt("xml"));
        assertNull("Found an at attribute", decl.getAt());
        assertFalse("Found an optional attribute", decl.isOptional());
        factory.addProperty("xml[@config-at]", "test1");
        assertEquals("Wrong value of at attribute", "test1", decl.getAt());
        factory.addProperty("xml[@at]", "test2");
        assertEquals("Wrong value of config-at attribute", "test1", decl.getAt());
        factory.clearProperty("xml[@config-at]");
        assertEquals("Old at attribute not detected", "test2", decl.getAt());
        factory.addProperty("xml[@config-optional]", "true");
        assertTrue("Wrong value of optional attribute", decl.isOptional());
        factory.addProperty("xml[@optional]", "false");
        assertTrue("Wrong value of config-optional attribute", decl.isOptional());
        factory.clearProperty("xml[@config-optional]");
        factory.setProperty("xml[@optional]", Boolean.TRUE);
        assertTrue("Old optional attribute not detected", decl.isOptional());
        factory.setProperty("xml[@optional]", "invalid value");
        try
        {
            decl.isOptional();
            fail("Invalid optional attribute was not detected!");
        }
        catch (ConfigurationRuntimeException crex)
        {
            // ok
        }
    }

    /**
     * Tests adding a new configuration provider.
     */
    public void testAddConfigurationProvider()
    {
        DefaultConfigurationBuilder.ConfigurationProvider provider = new DefaultConfigurationBuilder.ConfigurationProvider();
        assertNull("Provider already registered", factory
                .providerForTag("test"));
        factory.addConfigurationProvider("test", provider);
        assertSame("Provider not registered", provider, factory
                .providerForTag("test"));
    }

    /**
     * Tries to register a null configuration provider. This should cause an
     * exception.
     */
    public void testAddConfigurationProviderNull()
    {
        try
        {
            factory.addConfigurationProvider("test", null);
            fail("Could register null provider");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tries to register a configuration provider for a null tag. This should
     * cause an exception to be thrown.
     */
    public void testAddConfigurationProviderNullTag()
    {
        try
        {
            factory.addConfigurationProvider(null,
                    new DefaultConfigurationBuilder.ConfigurationProvider());
            fail("Could register provider for null tag!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests removing configuration providers.
     */
    public void testRemoveConfigurationProvider()
    {
        assertNull("Removing unknown provider", factory
                .removeConfigurationProvider("test"));
        assertNull("Removing provider for null tag", factory
                .removeConfigurationProvider(null));
        DefaultConfigurationBuilder.ConfigurationProvider provider = new DefaultConfigurationBuilder.ConfigurationProvider();
        factory.addConfigurationProvider("test", provider);
        assertSame("Failed to remove provider", provider, factory
                .removeConfigurationProvider("test"));
        assertNull("Provider still registered", factory.providerForTag("test"));
    }

    /**
     * Tests creating a configuration object from a configuration declaration.
     */
    public void testConfigurationBeanFactoryCreateBean()
    {
        factory.addConfigurationProvider("test",
                new DefaultConfigurationBuilder.ConfigurationProvider(
                        PropertiesConfiguration.class));
        factory.addProperty("test[@throwExceptionOnMissing]", "true");
        DefaultConfigurationBuilder.ConfigurationDeclaration decl = new DefaultConfigurationBuilder.ConfigurationDeclaration(
                factory, factory.configurationAt("test"));
        PropertiesConfiguration conf = (PropertiesConfiguration) BeanHelper
                .createBean(decl);
        assertTrue("Property was not initialized", conf
                .isThrowExceptionOnMissing());
    }

    /**
     * Tests creating a configuration object from an unknown tag. This should
     * cause an exception.
     */
    public void testConfigurationBeanFactoryCreateUnknownTag()
    {
        factory.addProperty("test[@throwExceptionOnMissing]", "true");
        DefaultConfigurationBuilder.ConfigurationDeclaration decl = new DefaultConfigurationBuilder.ConfigurationDeclaration(
                factory, factory.configurationAt("test"));
        try
        {
            BeanHelper.createBean(decl);
            fail("Could create configuration from unknown tag!");
        }
        catch (ConfigurationRuntimeException crex)
        {
            // ok
        }
    }

    /**
     * Tests loading a simple configuration definition file.
     */
    public void testLoadConfiguration() throws ConfigurationException
    {
        factory.setFile(TEST_FILE);
        checkConfiguration();
    }

    /**
     * Tests the file constructor.
     */
    public void testLoadConfigurationFromFile() throws ConfigurationException
    {
        factory = new DefaultConfigurationBuilder(TEST_FILE);
        checkConfiguration();
    }

    /**
     * Tests the file name constructor.
     */
    public void testLoadConfigurationFromFileName()
            throws ConfigurationException
    {
        factory = new DefaultConfigurationBuilder(TEST_FILE.getAbsolutePath());
        checkConfiguration();
    }

    /**
     * Tests the URL constructor.
     */
    public void testLoadConfigurationFromURL() throws Exception
    {
        factory = new DefaultConfigurationBuilder(TEST_FILE.toURL());
        checkConfiguration();
    }

    /**
     * Tests if the configuration was correctly created by the factory.
     */
    private void checkConfiguration() throws ConfigurationException
    {
        CombinedConfiguration compositeConfiguration = (CombinedConfiguration) factory
                .getConfiguration();

        assertEquals("Number of configurations", 3, compositeConfiguration
                .getNumberOfConfigurations());
        assertEquals(PropertiesConfiguration.class, compositeConfiguration
                .getConfiguration(0).getClass());
        assertEquals(XMLPropertiesConfiguration.class, compositeConfiguration
                .getConfiguration(1).getClass());
        assertEquals(XMLConfiguration.class, compositeConfiguration
                .getConfiguration(2).getClass());

        // check the first configuration
        PropertiesConfiguration pc = (PropertiesConfiguration) compositeConfiguration
                .getConfiguration(0);
        assertNotNull("Make sure we have a fileName: " + pc.getFileName(), pc
                .getFileName());

        // check some properties
        checkProperties(compositeConfiguration);
    }

    /**
     * Checks if the passed in configuration contains the expected properties.
     *
     * @param compositeConfiguration the configuration to check
     */
    private void checkProperties(Configuration compositeConfiguration)
    {
        assertTrue("Make sure we have loaded our key", compositeConfiguration
                .getBoolean("test.boolean"));
        assertEquals("I'm complex!", compositeConfiguration
                .getProperty("element2.subelement.subsubelement"));
        assertEquals("property in the XMLPropertiesConfiguration", "value1",
                compositeConfiguration.getProperty("key1"));
    }

    /**
     * Tests loading a configuration definition file with an additional section.
     */
    public void testLoadAdditional() throws ConfigurationException
    {
        factory.setFile(ADDITIONAL_FILE);
        CombinedConfiguration compositeConfiguration = (CombinedConfiguration) factory
                .getConfiguration();
        assertEquals("Verify how many configs", 2, compositeConfiguration
                .getNumberOfConfigurations());

        // Test if union was constructed correctly
        Object prop = compositeConfiguration.getProperty("tables.table.name");
        assertTrue(prop instanceof Collection);
        assertEquals(3, ((Collection) prop).size());
        assertEquals("users", compositeConfiguration
                .getProperty("tables.table(0).name"));
        assertEquals("documents", compositeConfiguration
                .getProperty("tables.table(1).name"));
        assertEquals("tasks", compositeConfiguration
                .getProperty("tables.table(2).name"));

        prop = compositeConfiguration
                .getProperty("tables.table.fields.field.name");
        assertTrue(prop instanceof Collection);
        assertEquals(17, ((Collection) prop).size());

        assertEquals("smtp.mydomain.org", compositeConfiguration
                .getString("mail.host.smtp"));
        assertEquals("pop3.mydomain.org", compositeConfiguration
                .getString("mail.host.pop"));

        // This was overriden
        assertEquals("masterOfPost", compositeConfiguration
                .getString("mail.account.user"));
        assertEquals("topsecret", compositeConfiguration
                .getString("mail.account.psswd"));

        // This was overriden, too, but not in additional section
        assertEquals("enhanced factory", compositeConfiguration
                .getString("test.configuration"));
    }

    /**
     * Tests whether a default log error listener is registered at the builder
     * instance.
     */
    public void testLogErrorListener()
    {
        assertEquals("No default error listener registered", 1,
                new DefaultConfigurationBuilder().getErrorListeners().size());
    }

    /**
     * Tests loading a definition file that contains optional configurations.
     */
    public void testLoadOptional() throws Exception
    {
        factory.setURL(OPTIONAL_FILE.toURL());
        Configuration config = factory.getConfiguration();
        assertTrue(config.getBoolean("test.boolean"));
        assertEquals("value", config.getProperty("element"));
    }

    /**
     * Tests whether loading a failing optional configuration causes an error
     * event.
     */
    public void testLoadOptionalErrorEvent() throws Exception
    {
        factory.clearErrorListeners();
        ConfigurationErrorListenerImpl listener = new ConfigurationErrorListenerImpl();
        factory.addErrorListener(listener);
        prepareOptionalTest("configuration", false);
        listener.verify(DefaultConfigurationBuilder.EVENT_ERR_LOAD_OPTIONAL,
                OPTIONAL_NAME, null);
    }

    /**
     * Tests loading a definition file with optional and non optional
     * configuration sources. One non optional does not exist, so this should
     * cause an exception.
     */
    public void testLoadOptionalWithException()
    {
        factory.setFile(OPTIONALEX_FILE);
        try
        {
            factory.getConfiguration();
            fail("Non existing source did not cause an exception!");
        }
        catch (ConfigurationException cex)
        {
            // ok
        }
    }

    /**
     * Tries to load a configuration file with an optional, non file-based
     * configuration. The optional attribute should work for other configuration
     * classes, too.
     */
    public void testLoadOptionalNonFileBased() throws ConfigurationException
    {
        CombinedConfiguration config = prepareOptionalTest("configuration", false);
        assertTrue("Configuration not empty", config.isEmpty());
        assertEquals("Wrong number of configurations", 0, config
                .getNumberOfConfigurations());
    }

    /**
     * Tests an optional, non existing configuration with the forceCreate
     * attribute. This configuration should be added to the resulting
     * configuration.
     */
    public void testLoadOptionalForceCreate() throws ConfigurationException
    {
        factory.setBasePath(TEST_FILE.getParent());
        CombinedConfiguration config = prepareOptionalTest("xml", true);
        assertEquals("Wrong number of configurations", 1, config
                .getNumberOfConfigurations());
        FileConfiguration fc = (FileConfiguration) config
                .getConfiguration(OPTIONAL_NAME);
        assertNotNull("Optional config not found", fc);
        assertEquals("File name was not set", "nonExisting.xml", fc
                .getFileName());
        assertNotNull("Base path was not set", fc.getBasePath());
    }

    /**
     * Tests loading an embedded optional configuration builder with the force
     * create attribute.
     */
    public void testLoadOptionalBuilderForceCreate()
            throws ConfigurationException
    {
        CombinedConfiguration config = prepareOptionalTest("configuration",
                true);
        assertEquals("Wrong number of configurations", 1, config
                .getNumberOfConfigurations());
        assertTrue(
                "Wrong optional configuration type",
                config.getConfiguration(OPTIONAL_NAME) instanceof CombinedConfiguration);
    }

    /**
     * Tests loading an optional configuration with the force create attribute
     * set. The provider will always throw an exception. In this case the
     * configuration will not be added to the resulting combined configuration.
     */
    public void testLoadOptionalForceCreateWithException()
            throws ConfigurationException
    {
        factory.addConfigurationProvider("test",
                new DefaultConfigurationBuilder.ConfigurationBuilderProvider()
                {
                    // Throw an exception here, too
                    public AbstractConfiguration getEmptyConfiguration(
                            DefaultConfigurationBuilder.ConfigurationDeclaration decl) throws Exception
                    {
                        throw new Exception("Unable to create configuration!");
                    }
                });
        CombinedConfiguration config = prepareOptionalTest("test", true);
        assertEquals("Optional configuration could be created", 0, config
                .getNumberOfConfigurations());
    }

    /**
     * Prepares a test for loading a configuration definition file with an
     * optional configuration declaration.
     *
     * @param tag the tag name with the optional configuration
     * @param force the forceCreate attribute
     * @return the combined configuration obtained from the builder
     * @throws ConfigurationException if an error occurs
     */
    private CombinedConfiguration prepareOptionalTest(String tag, boolean force)
            throws ConfigurationException
    {
        String prefix = "override." + tag;
        factory.addProperty(prefix + "[@fileName]", "nonExisting.xml");
        factory.addProperty(prefix + "[@config-optional]", Boolean.TRUE);
        factory.addProperty(prefix + "[@config-name]", OPTIONAL_NAME);
        if (force)
        {
            factory.addProperty(prefix + "[@config-forceCreate]", Boolean.TRUE);
        }
        return factory.getConfiguration(false);
    }

    /**
     * Tests loading a definition file with multiple different sources.
     */
    public void testLoadDifferentSources() throws ConfigurationException
    {
        factory.setFile(MULTI_FILE);
        Configuration config = factory.getConfiguration();
        assertFalse(config.isEmpty());
        assertTrue(config instanceof CombinedConfiguration);
        CombinedConfiguration cc = (CombinedConfiguration) config;
        assertEquals("Wrong number of configurations", 1, cc
                .getNumberOfConfigurations());

        assertNotNull(config
                .getProperty("tables.table(0).fields.field(2).name"));
        assertNotNull(config.getProperty("element2.subelement.subsubelement"));
        assertEquals("value", config.getProperty("element3"));
        assertEquals("foo", config.getProperty("element3[@name]"));
        assertNotNull(config.getProperty("mail.account.user"));

        // test JNDIConfiguration
        assertNotNull(config.getProperty("test.onlyinjndi"));
        assertTrue(config.getBoolean("test.onlyinjndi"));

        Configuration subset = config.subset("test");
        assertNotNull(subset.getProperty("onlyinjndi"));
        assertTrue(subset.getBoolean("onlyinjndi"));

        // test SystemConfiguration
        assertNotNull(config.getProperty("java.version"));
        assertEquals(System.getProperty("java.version"), config
                .getString("java.version"));
    }

    /**
     * Tests if the base path is correctly evaluated.
     */
    public void testSetConfigurationBasePath() throws ConfigurationException
    {
        factory.addProperty("properties[@fileName]", "test.properties");
        File deepDir = new File("conf/config/deep");
        factory.setConfigurationBasePath(deepDir.getAbsolutePath());

        Configuration config = factory.getConfiguration(false);
        assertEquals("Wrong property value", "somevalue", config
                .getString("somekey"));
    }

    /**
     * Tests reading a configuration definition file that contains complex
     * initialization of properties of the declared configuration sources.
     */
    public void testComplexInitialization() throws ConfigurationException
    {
        factory.setFile(INIT_FILE);
        CombinedConfiguration cc = (CombinedConfiguration) factory
                .getConfiguration();

        assertEquals("System property not found", "test.xml",
                cc.getString("test_file_xml"));
        PropertiesConfiguration c1 = (PropertiesConfiguration) cc
                .getConfiguration(1);
        assertTrue(
                "Reloading strategy was not set",
                c1.getReloadingStrategy() instanceof FileChangedReloadingStrategy);
        assertEquals("Refresh delay was not set", 10000,
                ((FileChangedReloadingStrategy) c1.getReloadingStrategy())
                        .getRefreshDelay());

        Configuration xmlConf = cc.getConfiguration("xml");
        assertEquals("Property not found", "I'm complex!", xmlConf
                .getString("element2/subelement/subsubelement"));
        assertEquals("List index not found", "two", xmlConf
                .getString("list[0]/item[1]"));
        assertEquals("Property in combiner file not found", "yellow", cc
                .getString("/gui/selcolor"));

        assertTrue("Delimiter flag was not set", cc
                .isDelimiterParsingDisabled());
        assertTrue("Expression engine was not set",
                cc.getExpressionEngine() instanceof XPathExpressionEngine);
    }

    /**
     * Tests if the returned combined configuration has the expected structure.
     */
    public void testCombinedConfiguration() throws ConfigurationException
    {
        factory.setFile(INIT_FILE);
        CombinedConfiguration cc = (CombinedConfiguration) factory
                .getConfiguration();
        assertNotNull("Properties configuration not found", cc
                .getConfiguration("properties"));
        assertNotNull("XML configuration not found", cc.getConfiguration("xml"));
        assertEquals("Wrong number of contained configs", 4, cc
                .getNumberOfConfigurations());

        CombinedConfiguration cc2 = (CombinedConfiguration) cc
                .getConfiguration(DefaultConfigurationBuilder.ADDITIONAL_NAME);
        assertNotNull("No additional configuration found", cc2);
        Set names = cc2.getConfigurationNames();
        assertEquals("Wrong number of contained additional configs", 2, names
                .size());
        assertTrue("Config 1 not contained", names.contains("combiner1"));
        assertTrue("Config 2 not contained", names.contains("combiner2"));
    }

    /**
     * Tests the structure of the returned combined configuration if there is no
     * additional section.
     */
    public void testCombinedConfigurationNoAdditional()
            throws ConfigurationException
    {
        factory.setFile(TEST_FILE);
        CombinedConfiguration cc = factory.getConfiguration(true);
        assertNull("Additional configuration was found", cc
                .getConfiguration(DefaultConfigurationBuilder.ADDITIONAL_NAME));
    }

    /**
     * Tests whether the list node definition was correctly processed.
     */
    public void testCombinedConfigurationListNodes()
            throws ConfigurationException
    {
        factory.setFile(INIT_FILE);
        CombinedConfiguration cc = factory.getConfiguration(true);
        Set listNodes = cc.getNodeCombiner().getListNodes();
        assertEquals("Wrong number of list nodes", 2, listNodes.size());
        assertTrue("table node not a list node", listNodes.contains("table"));
        assertTrue("list node not a list node", listNodes.contains("list"));

        CombinedConfiguration cca = (CombinedConfiguration) cc
                .getConfiguration(DefaultConfigurationBuilder.ADDITIONAL_NAME);
        listNodes = cca.getNodeCombiner().getListNodes();
        assertTrue("Found list nodes for additional combiner", listNodes
                .isEmpty());
    }

    /**
     * Tests whether a configuration builder can itself be declared in a
     * configuration definition file.
     */
    public void testConfigurationBuilderProvider()
            throws ConfigurationException
    {
        factory.addProperty("override.configuration[@fileName]", TEST_FILE
                .getAbsolutePath());
        CombinedConfiguration cc = factory.getConfiguration(false);
        assertEquals("Wrong number of configurations", 1, cc
                .getNumberOfConfigurations());
        checkProperties(cc);
    }

    /**
     * Tests whether XML settings can be inherited.
     */
    public void testLoadXMLWithSettings() throws ConfigurationException,
            IOException
    {
        File confDir = new File("conf");
        File targetDir = new File("target");
        File testXMLSource = new File(confDir, "testDtd.xml");
        File testXMLValidationSource = new File(confDir,
                "testValidateInvalid.xml");
        File testSavedXML = new File(targetDir, "testSave.xml");
        File testSavedFactory = new File(targetDir, "testSaveFactory.xml");
        File dtdFile = new File(confDir, "properties.dtd");
        final String publicId = "http://commons.apache.org/test.dtd";

        XMLConfiguration config = new XMLConfiguration(testXMLSource);
        config.setPublicID(publicId);
        config.save(testSavedXML);
        factory.addProperty("xml[@fileName]", testSavedXML.getAbsolutePath());
        factory.addProperty("xml(0)[@validating]", "true");
        factory.addProperty("xml(-1)[@fileName]", testXMLValidationSource
                .getAbsolutePath());
        factory.addProperty("xml(1)[@config-optional]", "true");
        factory.addProperty("xml(1)[@validating]", "true");
        factory.save(testSavedFactory);

        factory = new DefaultConfigurationBuilder();
        factory.setFile(testSavedFactory);
        factory.registerEntityId(publicId, dtdFile.toURL());
        factory.clearErrorListeners();
        Configuration c = factory.getConfiguration();
        assertEquals("Wrong property value", "value1", c.getString("entry(0)"));
        assertFalse("Invalid XML source was loaded", c
                .containsKey("table.name"));

        testSavedXML.delete();
        testSavedFactory.delete();
    }

    /**
     * Tests loading a configuration definition file that defines a custom
     * result class.
     */
    public void testExtendedClass() throws ConfigurationException
    {
        factory.setFile(CLASS_FILE);
        CombinedConfiguration cc = factory.getConfiguration(true);
        assertEquals("Extended", cc.getProperty("test"));
        assertTrue("Wrong result class: " + cc.getClass(),
                cc instanceof ExtendedCombinedConfiguration);
    }

    /**
     * Tests loading a configuration definition file that defines new providers.
     */
    public void testConfigurationProvider() throws ConfigurationException
    {
        factory.setFile(PROVIDER_FILE);
        factory.getConfiguration(true);
        DefaultConfigurationBuilder.ConfigurationProvider provider = factory
                .providerForTag("test");
        assertNotNull("Provider 'test' not registered", provider);
    }

        /**
     * Tests loading a configuration definition file that defines new providers.
     */
    public void testExtendedXMLConfigurationProvider() throws ConfigurationException
    {
        factory.setFile(EXTENDED_PROVIDER_FILE);
        CombinedConfiguration cc = factory.getConfiguration(true);
        DefaultConfigurationBuilder.ConfigurationProvider provider = factory
                .providerForTag("test");
        assertNotNull("Provider 'test' not registered", provider);
        Configuration config = cc.getConfiguration("xml");
        assertNotNull("Test configuration not present", config);
        assertTrue("Configuration is not ExtendedXMLConfiguration, is " +
                config.getClass().getName(), config instanceof ExtendedXMLConfiguration);
    }

    public void testGlobalLookup() throws Exception
    {
        factory.setFile(GLOBAL_LOOKUP_FILE);
        CombinedConfiguration cc = factory.getConfiguration(true);
        String value = cc.getInterpolator().lookup("test:test_key");
        assertNotNull("The test key was not located", value);
        assertEquals("Incorrect value retrieved","test.value",value);       
    }
         
    public void testSystemProperties() throws Exception
    {
        factory.setFile(SYSTEM_PROPS_FILE);
        CombinedConfiguration cc = factory.getConfiguration(true);
        String value = System.getProperty("key1");
        assertNotNull("The test key was not located", value);
        assertEquals("Incorrect value retrieved","value1",value);
    }


    /**
     * A specialized combined configuration implementation used for testing
     * custom result classes.
     */
    public static class ExtendedCombinedConfiguration extends
            CombinedConfiguration
    {
        /**
         * The serial version UID.
         */
        private static final long serialVersionUID = 4678031745085083392L;

        public Object getProperty(String key)
        {
            if (key.equals("test"))
            {
                return "Extended";
            }
            return super.getProperty(key);
        }
    }

    public static class ExtendedXMLConfiguration extends XMLConfiguration
    {
        public ExtendedXMLConfiguration()
        {
        }

    }

    public static class TestLookup extends StrLookup
    {
        Map map = new HashMap();

        public TestLookup()
        {
            map.put("test_file_xml", "test.xml");
            map.put("test_file_combine", "testcombine1.xml");
            map.put("test_key", "test.value");
        }

        public String lookup(String key)
        {
            if (key == null)
            {
                return null;
            }
            return (String)map.get(key);

        }
    }
}

