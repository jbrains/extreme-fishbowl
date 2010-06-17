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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;

import junit.framework.TestSuite;

/**
 * Test Case for the {@link SqlTimestampConverter} class.
 *
 * @version $Revision: 471689 $ $Date: 2006-11-06 10:52:49 +0000 (Mon, 06 Nov 2006) $
 */

public class SqlTimestampConverterTestCase extends DateConverterTestBase {

    /**
     * Construct a new Date test case.
     * @param name Test Name
     */
    public SqlTimestampConverterTestCase(String name) {
        super(name);
    }

    // ------------------------------------------------------------------------

    /**
     * Create Test Suite
     * @return test suite
     */
    public static TestSuite suite() {
        return new TestSuite(SqlTimestampConverterTestCase.class);
    }

    // ------------------------------------------------------------------------

    /**
     * Test Date Converter with no default value
     */
    public void testLocale() {

        // Re-set the default Locale to Locale.US
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        String pattern = "M/d/yy h:mm a"; // SHORT style Date & Time format for US Locale

        // Create & Configure the Converter
        DateTimeConverter converter = makeConverter();
        converter.setUseLocaleFormat(true);

        // Valid String --> Type Conversion
        String testString = "3/21/06 3:06 pm";
        Object expected = toType(testString, pattern, null);
        validConversion(converter, expected, testString);

        // Invalid Conversions
        invalidConversion(converter, null);
        invalidConversion(converter, "");
        invalidConversion(converter, "13:05 pm");
        invalidConversion(converter, "11:05 p");
        invalidConversion(converter, "11.05 pm");
        invalidConversion(converter, new Integer(2));

        // Restore the default Locale
        Locale.setDefault(defaultLocale);

    }

    /**
     * Test default String to java.sql.Timestamp conversion
     */
    public void testDefaultStringToTypeConvert() {

        // Create & Configure the Converter
        DateTimeConverter converter = makeConverter();
        converter.setUseLocaleFormat(false);

        // Valid String --> java.sql.Timestamp Conversion
        String testString = "2006-10-23 15:36:01.0";
        Object expected = toType(testString, "yyyy-MM-dd HH:mm:ss.S", null);
        validConversion(converter, expected, testString);

        // Invalid String --> java.sql.Timestamp Conversion
        invalidConversion(converter, "2006/09/21 15:36:01.0");
        invalidConversion(converter, "2006-10-22");
        invalidConversion(converter, "15:36:01");

    }

    /**
     * Create the Converter with no default value.
     * @return A new Converter
     */
    protected DateTimeConverter makeConverter() {
        return new SqlTimestampConverter();
    }
    
    /**
     * Create the Converter with a default value.
     * @param defaultValue The default value
     * @return A new Converter
     */
    protected DateTimeConverter makeConverter(Object defaultValue) {
        return new SqlTimestampConverter(defaultValue);
    }

    /**
     * Return the expected type
     * @return The expected type
     */
    protected Class getExpectedType() {
        return Timestamp.class;
    }

    /**
     * Convert from a Calendar to the appropriate Date type
     * 
     * @param value The Calendar value to convert
     * @return The converted value
     */
    protected Object toType(Calendar value) {
        return new Timestamp(getTimeInMillis(value));
    }

}