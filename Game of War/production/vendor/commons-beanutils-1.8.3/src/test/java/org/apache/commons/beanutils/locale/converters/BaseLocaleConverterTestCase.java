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

package org.apache.commons.beanutils.locale.converters;

import junit.framework.TestCase;
import java.util.Locale;
import org.apache.commons.beanutils.locale.BaseLocaleConverter;

/**
 * Base Test Case for the DecimalLocaleConverter classes. This class doesn't
 * define any real tests; it just provides useful methods for the real
 * test case classes to inherit.
 *
 * @author Niall Pemberton
 * @version $Revision: 469728 $ $Date: 2006-11-01 01:08:34 +0000 (Wed, 01 Nov 2006) $
 */

public class BaseLocaleConverterTestCase extends TestCase {


    // Original Default Locale
    protected Locale origLocale;

    // Converter
    protected BaseLocaleConverter converter;
    protected Object result;
    protected Object defaultValue;
    protected Object expectedValue;


    // Localized values
    protected Locale localizedLocale;
    protected String localizedDecimalPattern; 
    protected String localizedIntegerPattern; 
    protected String localizedDecimalValue;
    protected String localizedIntegerValue;

    // Locale values
    protected Locale defaultLocale;
    protected String defaultDecimalPattern; 
    protected String defaultIntegerPattern; 
    protected String defaultDecimalValue;
    protected String defaultIntegerValue;


    // Expected values
    protected String expectedDecimalValue;
    protected String expectedIntegerValue;

    // ---------------------------------------------------------- Constructors

    public BaseLocaleConverterTestCase(String name) {
        super(name);
    }
    
    // -------------------------------------------------- Overall Test Methods

    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() throws Exception {

        // Default Locale (Use US)
        defaultLocale           = Locale.US;
        defaultDecimalPattern   = "#,###.00";
        defaultIntegerPattern   = "#,###";
        defaultDecimalValue     = "1,234.56";
        defaultIntegerValue     = "1,234";

        // Use German Locale (uses different separators to US)
        localizedLocale         = Locale.GERMAN;
        localizedDecimalPattern = "#.###,00";
        localizedIntegerPattern = "#.###";
        localizedDecimalValue   = "1.234,56";
        localizedIntegerValue   = "1.234";

        // Expected Values
        expectedDecimalValue    = "1234.56";
        expectedIntegerValue    = "1234";

        // Reset default to the one specified
        origLocale = Locale.getDefault();

        // Initialize
        converter = null;
        result = null;
        defaultValue = null;
        expectedValue= null;

        if (defaultLocale.equals(origLocale)) {
            origLocale = null;
        } else {
            // System.out.println("Changing default locale from " + origLocale + " to " + defaultLocale);
            Locale.setDefault(defaultLocale);
        }


    }

    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {

        converter = null;
        result = null;
        defaultValue = null;
        expectedValue= null;

        // Set the Default Locale back to the original value 
        if (origLocale != null) {
            // System.out.println("Restoring default locale to " + origLocale);
            Locale.setDefault(origLocale);
        }

    }


    // -------------------------------------------------- Generic Test Methods

    /**
     * Test Converting Value WITH a pattern
     */
    protected void convertValueWithPattern(BaseLocaleConverter converter, Object value, String pattern, Object expectedValue) {
        convertValueWithPattern(converter, "", value, pattern, expectedValue);
    }

    /**
     * Test Converting Value WITH a pattern
     */
    protected void convertValueWithPattern(BaseLocaleConverter converter, String msgId, Object value, String pattern, Object expectedValue) {

        // Convert value with no pattern
        try {
            result = converter.convert(value, pattern);
        } catch (Exception e) {
            fail("Pattern conversion threw " + msgId + " threw " + e);
        }
        assertEquals("Check conversion value with pattern " + msgId, expectedValue, result);

    }

    /**
     * Test Converting Value WITHOUT a pattern
     */
    protected void convertValueNoPattern(BaseLocaleConverter converter, Object value, Object expectedValue) {
        convertValueNoPattern(converter, "", value, expectedValue);
    }

    /**
     * Test Converting Value WITHOUT a pattern
     */
    protected void convertValueNoPattern(BaseLocaleConverter converter, String msgId, Object value, Object expectedValue) {

        // Convert value with no pattern
        try {
            result = converter.convert(value);
        } catch (Exception e) {
            fail("No Pattern conversion threw " + msgId + " threw " + e);
        }
        assertEquals("Check conversion value without pattern " + msgId, expectedValue, result);


    }

    /**
     * Test Converting Value To a spcified Type
     */
    protected void convertValueToType(BaseLocaleConverter converter, Class clazz, Object value, String pattern, Object expectedValue) {
        convertValueToType(converter, "", clazz, value, pattern, expectedValue);
    }

    /**
     * Test Converting Value To a spcified Type
     */
    protected void convertValueToType(BaseLocaleConverter converter, String msgId, Class clazz, Object value, String pattern, Object expectedValue) {

        // Convert value with no pattern
        try {
            result = converter.convert(clazz, value, pattern);
        } catch (Exception e) {
            fail("Type  conversion threw " + msgId + " threw " + e);
        }
        assertEquals("Check conversion value to type " + msgId, expectedValue, result);

    }

    /**
     * Test Converting Null value.
     */
    protected void convertNull(BaseLocaleConverter converter, Object expectedValue) {
        convertNull(converter, "", expectedValue);
    }

    /**
     * Test Converting Null value.
     */
    protected void convertNull(BaseLocaleConverter converter, String msgId, Object expectedValue) {

        // Convert value with no pattern
        try {
            result = converter.convert(null);
        } catch (Exception e) {
            fail("Null conversion " + msgId + " threw " + e);
        }

        if (expectedValue == null) {
            assertNull("Check null conversion is null " + msgId + " result="+result, result);
        } else {
            assertEquals("Check null conversion is default " + msgId, expectedValue, result);
        }

    }

    /**
     * Test Converting an invalid value.
     */
    protected void convertInvalid(BaseLocaleConverter converter, Object expectedValue) {
        convertInvalid(converter, "", expectedValue);
    }

    /**
     * Test Converting an invalid value.
     */
    protected void convertInvalid(BaseLocaleConverter converter, String msgId, Object expectedValue) {

        // Convert value with no pattern
        try {
            result = converter.convert("xyz");
            if (expectedValue == null) {
                fail("Expected ConversionException if no default value " + msgId);
            }
        } catch (Exception e) {
            if (expectedValue != null) {
                fail("Expected default value " + msgId + " threw " + e);
            }
        }

        if (expectedValue != null) {
            assertEquals("Check invalid conversion is default " + msgId, expectedValue, result);
        }

    }

    /**
     * This class isn't intended to perform any real tests; it just provides
     * methods for the real test cases to inherit. However junit complains
     * if a class named ..TestCase contains no test methods, so here we
     * define a dummy one to keep it happy.
     */
    public void testNothing() {
    }
}

