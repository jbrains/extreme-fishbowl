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

import org.apache.commons.lang.exception.NestableRuntimeException;

/**
 * A configuration related runtime exception.
 *
 * @since 1.0
 *
 * @author Emmanuel Bourg
 * @version $Revision: 439648 $, $Date: 2006-09-02 22:42:10 +0200 (Sa, 02 Sep 2006) $
 */
public class ConfigurationRuntimeException extends NestableRuntimeException
{
    /**
     * The serial version ID.
     */
    private static final long serialVersionUID = -7838702245512140996L;

    /**
     * Constructs a new <code>ConfigurationRuntimeException</code> without
     * specified detail message.
     */
    public ConfigurationRuntimeException()
    {
        super();
    }

    /**
     * Constructs a new <code>ConfigurationRuntimeException</code> with
     * specified detail message.
     *
     * @param message  the error message
     */
    public ConfigurationRuntimeException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>ConfigurationRuntimeException</code> with
     * specified nested <code>Throwable</code>.
     *
     * @param cause  the exception or error that caused this exception to be thrown
     */
    public ConfigurationRuntimeException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs a new <code>ConfigurationRuntimeException</code> with
     * specified detail message and nested <code>Throwable</code>.
     *
     * @param message  the error message
     * @param cause    the exception or error that caused this exception to be thrown
     */
    public ConfigurationRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
