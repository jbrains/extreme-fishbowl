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

import java.util.Iterator;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;

import org.apache.commons.collections.iterators.EnumerationIterator;

/**
 * A configuration wrapper around a {@link ServletConfig}. This configuration
 * is read only, adding or removing a property will throw an
 * UnsupportedOperationException.
 *
 * @author <a href="mailto:ebourg@apache.org">Emmanuel Bourg</a>
 * @version $Revision: 515306 $, $Date: 2007-03-06 22:15:00 +0100 (Di, 06 Mrz 2007) $
 * @since 1.1
 */
public class ServletConfiguration extends BaseWebConfiguration
{
    /** Stores a reference to the wrapped <code>ServletConfig</code>.*/
    protected ServletConfig config;

    /**
     * Create a ServletConfiguration using the initialization parameter of
     * the specified servlet.
     *
     * @param servlet the servlet
     */
    public ServletConfiguration(Servlet servlet)
    {
        this(servlet.getServletConfig());
    }

    /**
     * Create a ServletConfiguration using the servlet initialization parameters.
     *
     * @param config the servlet configuration
     */
    public ServletConfiguration(ServletConfig config)
    {
        this.config = config;
    }

    public Object getProperty(String key)
    {
        return handleDelimiters(config.getInitParameter(key));
    }

    public Iterator getKeys()
    {
        return new EnumerationIterator(config.getInitParameterNames());
    }
}
