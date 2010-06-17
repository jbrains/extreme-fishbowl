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


package org.apache.commons.beanutils.priv;


/**
 * Bean that exposes methods defined by an interface that is implemented
 * in the superclass.
 *
 * @author Jan Sorensen
 * @version $Revision: 438363 $ $Date: 2006-08-30 05:48:00 +0100 (Wed, 30 Aug 2006) $
 */

class PrivateBeanSubclass extends PrivateBean {


    // ----------------------------------------------------------- Constructors


    /**
     * Create a new PrivateBeanSubclass instance.
     */
    PrivateBeanSubclass() {

        super();

    }


    // ------------------------------------------------------------- Properties


    /**
     * A property accessible via the superclass.
     */
    public String getBar() {

        return (super.getBar());

    }


}
