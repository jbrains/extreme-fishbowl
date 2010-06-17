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

import java.util.Iterator;

/**
 * A specialized configuration implementation that does not support cloning.
 * This class is only used in some test cases for testing implementations of
 * clone() methods. It does not make much sense otherwise; all methods are just
 * dummies.
 *
 * @version $Id: NonCloneableConfiguration.java 439648 2006-09-02 20:42:10Z oheger $
 */
public class NonCloneableConfiguration extends AbstractConfiguration
{
    /**
     * Dummy implementation of this method.
     */
    protected void addPropertyDirect(String key, Object value)
    {
    }

    /**
     * Dummy implementation of this method.
     */
    public boolean isEmpty()
    {
        return true;
    }

    /**
     * Dummy implementation of this method.
     */
    public boolean containsKey(String key)
    {
        return false;
    }

    /**
     * Dummy implementation of this method.
     */
    public Iterator getKeys()
    {
        return null;
    }

    /**
     * Dummy implementation of this method.
     */
    public Object getProperty(String key)
    {
        return null;
    }
}
