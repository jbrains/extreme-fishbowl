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

package org.apache.commons.beanutils.converters;

import java.io.File;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.beanutils.Converter;


/**
 * Test Case for the FileConverter class.
 *
 * @author James Strachan
 * @version $Revision: 438363 $ $Date: 2006-08-30 05:48:00 +0100 (Wed, 30 Aug 2006) $
 */

public class FileConverterTestCase extends TestCase {

    private Converter converter = null;

    // ------------------------------------------------------------------------

    public FileConverterTestCase(String name) {
        super(name);
    }
    
    // ------------------------------------------------------------------------

    public void setUp() throws Exception {
        converter = makeConverter();
    }

    public static TestSuite suite() {
        return new TestSuite(FileConverterTestCase.class);        
    }

    public void tearDown() throws Exception {
        converter = null;
    }

    // ------------------------------------------------------------------------
    
    protected Converter makeConverter() {
        return new FileConverter();
    }
    
    protected Class getExpectedType() {
        return File.class;
    }

    // ------------------------------------------------------------------------

    public void testSimpleConversion() throws Exception {
        String[] message= { 
            "from String",
            "from String",
            "from String"
        };
        
        Object[] input = { 
            "/tmp",
            "/tmp/foo.txt",
            "/tmp/does/not/exist.foo"
        };
        
        File[] expected = { 
            new File("/tmp"),
            new File("/tmp/foo.txt"),
            new File("/tmp/does/not/exist.foo")
        };
        
        for(int i=0;i<expected.length;i++) {
            assertEquals(message[i] + " to File",expected[i],converter.convert(File.class,input[i]));
            assertEquals(message[i] + " to null type",expected[i],converter.convert(null,input[i]));
        }
    }
    
}

