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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletRequest;

import org.apache.commons.collections.iterators.EnumerationIterator;

/**
 * A configuration wrapper to read the parameters of a servlet request. This
 * configuration is read only, adding or removing a property will throw an
 * UnsupportedOperationException.
 *
 * @author <a href="mailto:ebourg@apache.org">Emmanuel Bourg</a>
 * @version $Revision: 515306 $, $Date: 2007-03-06 22:15:00 +0100 (Di, 06 Mrz 2007) $
 * @since 1.1
 */
public class ServletRequestConfiguration extends BaseWebConfiguration
{
    /** Stores the wrapped request.*/
    protected ServletRequest request;

    /**
     * Create a ServletRequestConfiguration using the request parameters.
     *
     * @param request the servlet request
     */
    public ServletRequestConfiguration(ServletRequest request)
    {
        this.request = request;
    }

    public Object getProperty(String key)
    {
        String[] values = request.getParameterValues(key);

        if (values == null || values.length == 0)
        {
            return null;
        }
        else if (values.length == 1)
        {
            return handleDelimiters(values[0]);
        }
        else
        {
            // ensure that escape characters in all list elements are removed
            List result = new ArrayList(values.length);
            for (int i = 0; i < values.length; i++)
            {
                Object val = handleDelimiters(values[i]);
                if (val instanceof Collection)
                {
                    result.addAll((Collection) val);
                }
                else
                {
                    result.add(val);
                }
            }
            return result;
        }
    }

    public Iterator getKeys()
    {
        return new EnumerationIterator(request.getParameterNames());
    }
}
