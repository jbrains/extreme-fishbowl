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

/**
 * <p>A specialized SAX2 XML parser that processes configuration objects.</p>
 *
 * <p>This class mimics to be a SAX compliant XML parser. It is able to iterate
 * over the keys in a configuration object and to generate corresponding SAX
 * events. By registering a <code>ContentHandler</code> at an instance
 * it is possible to perform XML processing on a configuration object.</p>
 *
 * @author <a href="mailto:oliver.heger@t-online.de">Oliver Heger</a>
 * @version $Id: BaseConfigurationXMLReader.java 439648 2006-09-02 20:42:10Z oheger $
 */
public class BaseConfigurationXMLReader extends ConfigurationXMLReader
{
    /** Stores the actual configuration.*/
    private Configuration config;

    /**
     * Creates a new instance of <code>BaseConfigurationXMLReader</code>.
     */
    public BaseConfigurationXMLReader()
    {
        super();
    }

    /**
     * Creates a new instance of <code>BaseConfigurationXMLReader</code> and
     * sets the configuration object to be parsed.
     *
     * @param conf the configuration to be parsed
     */
    public BaseConfigurationXMLReader(Configuration conf)
    {
        this();
        setConfiguration(conf);
    }

    /**
     * Returns the actual configuration to be processed.
     *
     * @return the actual configuration
     */
    public Configuration getConfiguration()
    {
        return config;
    }

    /**
     * Sets the configuration to be processed.
     *
     * @param conf the configuration
     */
    public void setConfiguration(Configuration conf)
    {
        config = conf;
    }

    /**
     * Returns the configuration to be processed.
     *
     * @return the actual configuration
     */
    public Configuration getParsedConfiguration()
    {
        return getConfiguration();
    }

    /**
     * The main SAX event generation method. This element uses an internal
     * <code>HierarchicalConfigurationConverter</code> object to iterate over
     * all keys in the actual configuration and to generate corresponding SAX
     * events.
     */
    protected void processKeys()
    {
        fireElementStart(getRootName(), null);
        new SAXConverter().process(getConfiguration());
        fireElementEnd(getRootName());
    }

    /**
     * An internally used helper class to iterate over all configuration keys
     * ant to generate corresponding SAX events.
     *
     */
    class SAXConverter extends HierarchicalConfigurationConverter
    {
        /**
         * Callback for the start of an element.
         *
         * @param name the element name
         * @param value the element value
         */
        protected void elementStart(String name, Object value)
        {
            fireElementStart(name, null);
            if (value != null)
            {
                fireCharacters(value.toString());
            }
        }

        /**
         * Callback for the end of an element.
         *
         * @param name the element name
         */
        protected void elementEnd(String name)
        {
            fireElementEnd(name);
        }
    }
}
