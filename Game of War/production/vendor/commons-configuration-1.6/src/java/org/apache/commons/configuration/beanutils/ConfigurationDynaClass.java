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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The <tt>ConfigurationDynaClass</tt> dynamically determines properties for
 * a <code>ConfigurationDynaBean</code> from a wrapped configuration-collection
 * {@link org.apache.commons.configuration.Configuration} instance.
 *
 * @author <a href="mailto:ricardo.gladwell@btinternet.com">Ricardo Gladwell</a>
 * @version $Revision: 727664 $, $Date: 2008-12-18 08:16:09 +0100 (Do, 18 Dez 2008) $
 * @since 1.0-rc1
 */
public class ConfigurationDynaClass implements DynaClass
{
    /** The logger.*/
    private static Log log = LogFactory.getLog(ConfigurationDynaClass.class);

    /** Stores the associated configuration.*/
    private Configuration configuration;

    /**
     * Construct an instance of a <code>ConfigurationDynaClass</code>
     * wrapping the specified <code>Configuration</code> instance.
     * @param configuration <code>Configuration</code> instance.
     */
    public ConfigurationDynaClass(Configuration configuration)
    {
        super();
        if (log.isTraceEnabled())
        {
            log.trace("ConfigurationDynaClass(" + configuration + ")");
        }
        this.configuration = configuration;
    }

    public DynaProperty getDynaProperty(String name)
    {
        if (log.isTraceEnabled())
        {
            log.trace("getDynaProperty(" + name + ")");
        }

        if (name == null)
        {
            throw new IllegalArgumentException("Property name must not be null!");
        }

        Object value = configuration.getProperty(name);
        if (value == null)
        {
            return null;
        }
        else
        {
            Class type = value.getClass();

            if (type == Byte.class)
            {
                type = Byte.TYPE;
            }
            if (type == Character.class)
            {
                type = Character.TYPE;
            }
            else if (type == Boolean.class)
            {
                type = Boolean.TYPE;
            }
            else if (type == Double.class)
            {
                type = Double.TYPE;
            }
            else if (type == Float.class)
            {
                type = Float.TYPE;
            }
            else if (type == Integer.class)
            {
                type = Integer.TYPE;
            }
            else if (type == Long.class)
            {
                type = Long.TYPE;
            }
            else if (type == Short.class)
            {
                type = Short.TYPE;
            }

            return new DynaProperty(name, type);
        }
    }

    public DynaProperty[] getDynaProperties()
    {
        if (log.isTraceEnabled())
        {
            log.trace("getDynaProperties()");
        }

        Iterator keys = configuration.getKeys();
        List properties = new ArrayList();
        while (keys.hasNext())
        {
            String key = (String) keys.next();
            DynaProperty property = getDynaProperty(key);
            properties.add(property);
        }

        DynaProperty[] propertyArray = new DynaProperty[properties.size()];
        properties.toArray(propertyArray);
        if (log.isDebugEnabled())
        {
            log.debug("Found " + properties.size() + " properties.");
        }

        return propertyArray;
    }

    public String getName()
    {
        return ConfigurationDynaBean.class.getName();
    }

    public DynaBean newInstance() throws IllegalAccessException, InstantiationException
    {
        return new ConfigurationDynaBean(configuration);
    }
}
