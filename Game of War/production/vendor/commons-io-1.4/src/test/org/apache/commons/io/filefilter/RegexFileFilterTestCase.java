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
package org.apache.commons.io.filefilter;

import java.io.File;
import java.util.regex.Pattern;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.testtools.FileBasedTestCase;

/**
 * Used to test RegexFileFilterUtils.
 */
public class RegexFileFilterTestCase extends FileBasedTestCase {

    public RegexFileFilterTestCase(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static TestSuite suite() {
        return new TestSuite(RegexFileFilterTestCase.class);
    }

    public void setUp() {
        getTestDirectory().mkdirs();
    }

    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(getTestDirectory());
    }

    public void assertFiltering(IOFileFilter filter, File file, boolean expected) throws Exception {
        // Note. This only tests the (File, String) version if the parent of
        //       the File passed in is not null
        assertTrue(
            "Filter(File) " + filter.getClass().getName() + " not " + expected + " for " + file,
            (filter.accept(file) == expected));

        if (file != null && file.getParentFile() != null) {
            assertTrue(
                "Filter(File, String) " + filter.getClass().getName() + " not " + expected + " for " + file,
                (filter.accept(file.getParentFile(), file.getName()) == expected));
        } else if (file == null) {
            assertTrue(
                "Filter(File, String) " + filter.getClass().getName() + " not " + expected + " for null",
                filter.accept(file) == expected);
        }
    }

    public void testRegex() throws Exception {
        IOFileFilter filter = new RegexFileFilter("^.*[tT]est(-\\d+)?\\.java$");
        assertFiltering(filter, new File("Test.java"), true);
        assertFiltering(filter, new File("test-10.java"), true);
        assertFiltering(filter, new File("test-.java"), false);

        filter = new RegexFileFilter("^[Tt]est.java$");
        assertFiltering(filter, new File("Test.java"), true);
        assertFiltering(filter, new File("test.java"), true);
        assertFiltering(filter, new File("tEST.java"), false);

        filter = new RegexFileFilter(Pattern.compile("^test.java$", Pattern.CASE_INSENSITIVE));
        assertFiltering(filter, new File("Test.java"), true);
        assertFiltering(filter, new File("test.java"), true);
        assertFiltering(filter, new File("tEST.java"), true);

        filter = new RegexFileFilter("^test.java$", Pattern.CASE_INSENSITIVE);
        assertFiltering(filter, new File("Test.java"), true);
        assertFiltering(filter, new File("test.java"), true);
        assertFiltering(filter, new File("tEST.java"), true);

        filter = new RegexFileFilter("^test.java$", IOCase.INSENSITIVE);
        assertFiltering(filter, new File("Test.java"), true);
        assertFiltering(filter, new File("test.java"), true);
        assertFiltering(filter, new File("tEST.java"), true);

        try {
            new RegexFileFilter((String)null);
            fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            new RegexFileFilter((String)null, Pattern.CASE_INSENSITIVE);
            fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            new RegexFileFilter((String)null, IOCase.INSENSITIVE);
            fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            new RegexFileFilter((java.util.regex.Pattern)null);
            fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
         
}
