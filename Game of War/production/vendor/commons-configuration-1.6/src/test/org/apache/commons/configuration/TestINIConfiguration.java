/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.commons.configuration;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

/**
 * Test class for INIConfiguration.
 *
 * @author Trevor Miller
 * @version $Id: TestINIConfiguration.java 620146 2008-02-09 16:33:45Z oheger $
 */
public class TestINIConfiguration extends TestCase
{
    private static String LINE_SEPARATOR = System.getProperty("line.separator");

    /** Constant for the content of an ini file. */
	private static final String INI_DATA =
            "[section1]" + LINE_SEPARATOR
            + "var1 = foo" + LINE_SEPARATOR
            + "var2 = 451" + LINE_SEPARATOR
            + LINE_SEPARATOR
            + "[section2]" + LINE_SEPARATOR
            + "var1 = 123.45" + LINE_SEPARATOR
            + "var2 = bar" + LINE_SEPARATOR
            + LINE_SEPARATOR
            + "[section3]" + LINE_SEPARATOR
            + "var1 = true" + LINE_SEPARATOR
            + "interpolated = ${section3.var1}" + LINE_SEPARATOR
            + "multi = foo" + LINE_SEPARATOR
            + "multi = bar" + LINE_SEPARATOR
            + LINE_SEPARATOR;

	private static final String INI_DATA2 =
            "[section4]" + LINE_SEPARATOR
            + "var1 = \"quoted value\"" + LINE_SEPARATOR
            + "var2 = \"quoted value\\nwith \\\"quotes\\\"\"" + LINE_SEPARATOR
            + "var3 = 123 ; comment" + LINE_SEPARATOR
            + "var4 = \"1;2;3\" ; comment" + LINE_SEPARATOR
            + "var5 = '\\'quoted\\' \"value\"' ; comment";

    /**
     * Test of save method, of class {@link INIConfiguration}.
     */
	public void testSave() throws Exception
	{
		Writer writer = new StringWriter();
		INIConfiguration instance = new INIConfiguration();
		instance.addProperty("section1.var1", "foo");
		instance.addProperty("section1.var2", "451");
		instance.addProperty("section2.var1", "123.45");
		instance.addProperty("section2.var2", "bar");
		instance.addProperty("section3.var1", "true");
		instance.addProperty("section3.interpolated", "${section3.var1}");
		instance.addProperty("section3.multi", "foo");
		instance.addProperty("section3.multi", "bar");
		instance.save(writer);

        assertEquals("Wrong content of ini file", INI_DATA, writer.toString());
	}

	/**
     * Test of load method, of class {@link INIConfiguration}.
     */
	public void testLoad() throws Exception
	{
		checkLoad(INI_DATA);
	}

	/**
     * Tests the load() method when the alternative value separator is used (a
     * ':' for '=').
     */
	public void testLoadAlternativeSeparator() throws Exception
	{
		checkLoad(INI_DATA.replace('=', ':'));
	}

	/**
     * Helper method for testing the load operation. Loads the specified content
     * into a configuration and then checks some properties.
     *
     * @param data the data to load
     */
	private void checkLoad(String data) throws ConfigurationException, IOException
	{
		Reader reader = new StringReader(data);
		INIConfiguration instance = new INIConfiguration();
		instance.load(reader);
		reader.close();
		assertTrue(instance.getString("section1.var1").equals("foo"));
		assertTrue(instance.getInt("section1.var2") == 451);
		assertTrue(instance.getDouble("section2.var1") == 123.45);
		assertTrue(instance.getString("section2.var2").equals("bar"));
		assertTrue(instance.getBoolean("section3.var1"));
		assertTrue(instance.getSections().size() == 3);
	}

	/**
     * Test of isCommentLine method, of class {@link INIConfiguration}.
     */
	public void testIsCommentLine()
	{
		INIConfiguration instance = new INIConfiguration();
		assertTrue(instance.isCommentLine("#comment1"));
		assertTrue(instance.isCommentLine(";comment1"));
		assertFalse(instance.isCommentLine("nocomment=true"));
		assertFalse(instance.isCommentLine(null));
	}

	/**
     * Test of isSectionLine method, of class {@link INIConfiguration}.
     */
	public void testIsSectionLine()
	{
		INIConfiguration instance = new INIConfiguration();
		assertTrue(instance.isSectionLine("[section]"));
		assertFalse(instance.isSectionLine("nosection=true"));
		assertFalse(instance.isSectionLine(null));
	}

	/**
     * Test of getSections method, of class {@link INIConfiguration}.
     */
	public void testGetSections()
	{
		INIConfiguration instance = new INIConfiguration();
		instance.addProperty("test1.foo", "bar");
		instance.addProperty("test2.foo", "abc");
		Set expResult = new HashSet();
		expResult.add("test1");
		expResult.add("test2");
		Set result = instance.getSections();
		assertEquals(expResult, result);
	}

    public void testQuotedValue() throws Exception
    {
        INIConfiguration config = new INIConfiguration();
        config.load(new StringReader(INI_DATA2));

        assertEquals("value", "quoted value", config.getString("section4.var1"));
    }

    public void testQuotedValueWithQuotes() throws Exception
    {
        INIConfiguration config = new INIConfiguration();
        config.load(new StringReader(INI_DATA2));

        assertEquals("value", "quoted value\\nwith \"quotes\"", config.getString("section4.var2"));
    }

    public void testValueWithComment() throws Exception
    {
        INIConfiguration config = new INIConfiguration();
        config.load(new StringReader(INI_DATA2));

        assertEquals("value", "123", config.getString("section4.var3"));
    }

    public void testQuotedValueWithComment() throws Exception
    {
        INIConfiguration config = new INIConfiguration();
        config.load(new StringReader(INI_DATA2));

        assertEquals("value", "1;2;3", config.getString("section4.var4"));
    }

    public void testQuotedValueWithSingleQuotes() throws Exception
    {
        INIConfiguration config = new INIConfiguration();
        config.load(new StringReader(INI_DATA2));

        assertEquals("value", "'quoted' \"value\"", config.getString("section4.var5"));
    }

    public void testWriteValueWithCommentChar() throws Exception
    {
        INIConfiguration config = new INIConfiguration();
        config.setProperty("section.key1", "1;2;3");

        StringWriter writer = new StringWriter();
        config.save(writer);

        INIConfiguration config2 = new INIConfiguration();
        config2.load(new StringReader(writer.toString()));

        assertEquals("value", "1;2;3", config2.getString("section.key1"));
    }

    /**
     * Tests whether whitespace is left unchanged for quoted values.
     */
    public void testQuotedValueWithWhitespace() throws Exception
    {
        final String content = "CmdPrompt = \" [test@cmd ~]$ \"";
        INIConfiguration config = new INIConfiguration();
        config.load(new StringReader(content));
        assertEquals("Wrong propert value", " [test@cmd ~]$ ", config
                .getString("CmdPrompt"));
    }

    /**
     * Tests a quoted value with space and a comment.
     */
    public void testQuotedValueWithWhitespaceAndComment() throws Exception
    {
        final String content = "CmdPrompt = \" [test@cmd ~]$ \" ; a comment";
        INIConfiguration config = new INIConfiguration();
        config.load(new StringReader(content));
        assertEquals("Wrong propert value", " [test@cmd ~]$ ", config
                .getString("CmdPrompt"));
    }
}
