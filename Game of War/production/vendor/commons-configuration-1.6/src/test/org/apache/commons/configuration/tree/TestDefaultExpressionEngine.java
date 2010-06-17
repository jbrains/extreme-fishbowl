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
package org.apache.commons.configuration.tree;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * Test class for DefaultExpressionEngine.
 *
 * @author Oliver Heger
 */
public class TestDefaultExpressionEngine extends TestCase
{
    /** Stores the names of the test nodes representing tables. */
    private static String[] tables =
    { "users", "documents"};

    /** Stores the types of the test table nodes. */
    private static String[] tabTypes =
    { "system", "application"};

    /** Test data fields for the node hierarchy. */
    private static String[][] fields =
    {
    { "uid", "uname", "firstName", "lastName", "email"},
    { "docid", "name", "creationDate", "authorID", "version"}};

    /** The object to be tested. */
    DefaultExpressionEngine engine;

    /** The root of a hierarchy with configuration nodes. */
    ConfigurationNode root;

    protected void setUp() throws Exception
    {
        super.setUp();
        root = setUpNodes();
        engine = new DefaultExpressionEngine();
    }

    /**
     * Tests some simple queries.
     */
    public void testQueryKeys()
    {
        checkKey("tables.table.name", "name", 2);
        checkKey("tables.table.fields.field.name", "name", 10);
        checkKey("tables.table[@type]", "type", 2);
        checkKey("tables.table(0).fields.field.name", "name", 5);
        checkKey("tables.table(1).fields.field.name", "name", 5);
        checkKey("tables.table.fields.field(1).name", "name", 2);
    }

    /**
     * Performs some queries and evaluates the values of the result nodes.
     */
    public void testQueryNodes()
    {
        for (int i = 0; i < tables.length; i++)
        {
            checkKeyValue("tables.table(" + i + ").name", "name", tables[i]);
            checkKeyValue("tables.table(" + i + ")[@type]", "type", tabTypes[i]);

            for (int j = 0; j < fields[i].length; j++)
            {
                checkKeyValue("tables.table(" + i + ").fields.field(" + j
                        + ").name", "name", fields[i][j]);
            }
        }
    }

    /**
     * Tests querying keys that do not exist.
     */
    public void testQueryNonExistingKeys()
    {
        checkKey("tables.tablespace.name", null, 0);
        checkKey("tables.table(2).name", null, 0);
        checkKey("a complete unknown key", null, 0);
        checkKey("tables.table(0).fields.field(-1).name", null, 0);
        checkKey("tables.table(0).fields.field(28).name", null, 0);
        checkKey("tables.table(0).fields.field().name", null, 0);
        checkKey("connection.settings.usr.name", null, 0);
    }

    /**
     * Tests querying nodes whose names contain a delimiter.
     */
    public void testQueryEscapedKeys()
    {
        checkKeyValue("connection..settings.usr..name", "usr.name", "scott");
        checkKeyValue("connection..settings.usr..pwd", "usr.pwd", "tiger");
    }

    /**
     * Tests some queries when the same delimiter is used for properties and
     * attributes.
     */
    public void testQueryAttributeEmulation()
    {
        engine.setAttributeEnd(null);
        engine.setAttributeStart(engine.getPropertyDelimiter());
        checkKeyValue("tables.table(0).name", "name", tables[0]);
        checkKeyValue("tables.table(0).type", "type", tabTypes[0]);
        checkKey("tables.table.type", "type", 2);
    }

    /**
     * Tests accessing the root node.
     */
    public void testQueryRootNode()
    {
        List nodes = checkKey(null, null, 1);
        assertSame("Root node not found", root, nodes.get(0));
        nodes = checkKey("", null, 1);
        assertSame("Root node not found", root, nodes.get(0));
        checkKeyValue("[@test]", "test", "true");
    }

    /**
     * Tests a different query snytax. Sets other strings for the typical tokens
     * used by the expression engine.
     */
    public void testQueryAlternativeSyntax()
    {
        setUpAlternativeSyntax();
        checkKeyValue("tables/table[1]/name", "name", tables[1]);
        checkKeyValue("tables/table[0]@type", "type", tabTypes[0]);
        checkKeyValue("@test", "test", "true");
        checkKeyValue("connection.settings/usr.name", "usr.name", "scott");
    }

    /**
     * Tests obtaining keys for nodes.
     */
    public void testNodeKey()
    {
        ConfigurationNode node = root.getChild(0);
        assertEquals("Invalid name for descendant of root", "tables", engine
                .nodeKey(node, ""));
        assertEquals("Parent key not respected", "test.tables", engine.nodeKey(
                node, "test"));
        assertEquals("Full parent key not taken into account",
                "a.full.parent.key.tables", engine.nodeKey(node,
                        "a.full.parent.key"));
    }

    /**
     * Tests obtaining keys when the root node is involved.
     */
    public void testNodeKeyWithRoot()
    {
        assertEquals("Wrong name for root noot", "", engine.nodeKey(root, null));
        assertEquals("Null name not detected", "test", engine.nodeKey(root,
                "test"));
    }

    /**
     * Tests obtaining keys for attribute nodes.
     */
    public void testNodeKeyWithAttribute()
    {
        ConfigurationNode node = root.getChild(0).getChild(0).getAttribute(0);
        assertEquals("Wrong attribute node", "type", node.getName());
        assertEquals("Wrong attribute key", "tables.table[@type]", engine
                .nodeKey(node, "tables.table"));
        assertEquals("Wrong key for root attribute", "[@test]", engine.nodeKey(
                root.getAttribute(0), ""));
    }

    /**
     * Tests obtaining keys for nodes that contain the delimiter character.
     */
    public void testNodeKeyWithEscapedDelimiters()
    {
        ConfigurationNode node = root.getChild(1);
        assertEquals("Wrong escaped key", "connection..settings", engine
                .nodeKey(node, ""));
        assertEquals("Wrong complex escaped key",
                "connection..settings.usr..name", engine.nodeKey(node
                        .getChild(0), engine.nodeKey(node, "")));
    }

    /**
     * Tests obtaining node keys when a different syntax is set.
     */
    public void testNodeKeyWithAlternativeSyntax()
    {
        setUpAlternativeSyntax();
        assertEquals("Wrong child key", "tables/table", engine.nodeKey(root
                .getChild(0).getChild(0), "tables"));
        assertEquals("Wrong attribute key", "@test", engine.nodeKey(root
                .getAttribute(0), ""));

        engine.setAttributeStart(engine.getPropertyDelimiter());
        assertEquals("Wrong attribute key", "/test", engine.nodeKey(root
                .getAttribute(0), ""));
    }

    /**
     * Tests adding direct child nodes to the existing hierarchy.
     */
    public void testPrepareAddDirectly()
    {
        NodeAddData data = engine.prepareAdd(root, "newNode");
        assertSame("Wrong parent node", root, data.getParent());
        assertTrue("Path nodes available", data.getPathNodes().isEmpty());
        assertEquals("Wrong name of new node", "newNode", data.getNewNodeName());
        assertFalse("New node is an attribute", data.isAttribute());

        data = engine.prepareAdd(root, "tables.table.fields.field.name");
        assertEquals("Wrong name of new node", "name", data.getNewNodeName());
        assertTrue("Path nodes available", data.getPathNodes().isEmpty());
        assertEquals("Wrong parent node", "field", data.getParent().getName());
        ConfigurationNode nd = data.getParent().getChild(0);
        assertEquals("Field has no name node", "name", nd.getName());
        assertEquals("Incorrect name", "version", nd.getValue());
    }

    /**
     * Tests adding when indices are involved.
     */
    public void testPrepareAddWithIndex()
    {
        NodeAddData data = engine
                .prepareAdd(root, "tables.table(0).tableSpace");
        assertEquals("Wrong name of new node", "tableSpace", data
                .getNewNodeName());
        assertTrue("Path nodes available", data.getPathNodes().isEmpty());
        assertEquals("Wrong type of parent node", "table", data.getParent()
                .getName());
        ConfigurationNode node = data.getParent().getChild(0);
        assertEquals("Wrong table", tables[0], node.getValue());

        data = engine.prepareAdd(root, "tables.table(1).fields.field(2).alias");
        assertEquals("Wrong name of new node", "alias", data.getNewNodeName());
        assertEquals("Wrong type of parent node", "field", data.getParent()
                .getName());
        assertEquals("Wrong field node", "creationDate", data.getParent()
                .getChild(0).getValue());
    }

    /**
     * Tests adding new attributes.
     */
    public void testPrepareAddAttribute()
    {
        NodeAddData data = engine.prepareAdd(root,
                "tables.table(0)[@tableSpace]");
        assertEquals("Wrong table node", tables[0], data.getParent()
                .getChild(0).getValue());
        assertEquals("Wrong name of new node", "tableSpace", data
                .getNewNodeName());
        assertTrue("Attribute not detected", data.isAttribute());
        assertTrue("Path nodes available", data.getPathNodes().isEmpty());

        data = engine.prepareAdd(root, "[@newAttr]");
        assertSame("Root node is not parent", root, data.getParent());
        assertEquals("Wrong name of new node", "newAttr", data.getNewNodeName());
        assertTrue("Attribute not detected", data.isAttribute());
    }

    /**
     * Tests add operations where complete pathes are added.
     */
    public void testPrepareAddWithPath()
    {
        NodeAddData data = engine.prepareAdd(root,
                "tables.table(1).fields.field(-1).name");
        assertEquals("Wrong name of new node", "name", data.getNewNodeName());
        checkNodePath(data, new String[]
        { "field"});
        assertEquals("Wrong type of parent node", "fields", data.getParent()
                .getName());

        data = engine.prepareAdd(root, "tables.table(-1).name");
        assertEquals("Wrong name of new node", "name", data.getNewNodeName());
        checkNodePath(data, new String[]
        { "table"});
        assertEquals("Wrong type of parent node", "tables", data.getParent()
                .getName());

        data = engine.prepareAdd(root, "a.complete.new.path");
        assertEquals("Wrong name of new node", "path", data.getNewNodeName());
        checkNodePath(data, new String[]
        { "a", "complete", "new"});
        assertSame("Root is not parent", root, data.getParent());
    }

    /**
     * Tests add operations when property and attribute delimiters are equal.
     * Then it is not possible to add new attribute nodes.
     */
    public void testPrepareAddWithSameAttributeDelimiter()
    {
        engine.setAttributeEnd(null);
        engine.setAttributeStart(engine.getPropertyDelimiter());

        NodeAddData data = engine.prepareAdd(root, "tables.table(0).test");
        assertEquals("Wrong name of new node", "test", data.getNewNodeName());
        assertFalse("New node is an attribute", data.isAttribute());
        assertEquals("Wrong type of parent node", "table", data.getParent()
                .getName());

        data = engine.prepareAdd(root, "a.complete.new.path");
        assertFalse("New node is an attribute", data.isAttribute());
        checkNodePath(data, new String[]
        { "a", "complete", "new"});
    }

    /**
     * Tests add operations when an alternative syntax is set.
     */
    public void testPrepareAddWithAlternativeSyntax()
    {
        setUpAlternativeSyntax();
        NodeAddData data = engine.prepareAdd(root, "tables/table[0]/test");
        assertEquals("Wrong name of new node", "test", data.getNewNodeName());
        assertFalse("New node is attribute", data.isAttribute());
        assertEquals("Wrong parent node", tables[0], data.getParent().getChild(
                0).getValue());

        data = engine.prepareAdd(root, "a/complete/new/path@attr");
        assertEquals("Wrong name of new attribute", "attr", data
                .getNewNodeName());
        checkNodePath(data, new String[]
        { "a", "complete", "new", "path"});
        assertSame("Root is not parent", root, data.getParent());
    }

    /**
     * Tests using invalid keys, e.g. if something should be added to
     * attributes.
     */
    public void testPrepareAddInvalidKeys()
    {
        try
        {
            engine.prepareAdd(root, "tables.table(0)[@type].new");
            fail("Could add node to existing attribute!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }

        try
        {
            engine
                    .prepareAdd(root,
                            "a.complete.new.path.with.an[@attribute].at.a.non.allowed[@position]");
            fail("Could add invalid path!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }

        try
        {
            engine.prepareAdd(root, null);
            fail("Could add null key!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }

        try
        {
            engine.prepareAdd(root, "");
            fail("Could add undefined key!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Creates a node hierarchy for testing that consists of tables, their
     * fields, and some additional data:
     *
     * <pre>
     *  tables
     *       table
     *          name
     *          fields
     *              field
     *                  name
     *              field
     *                  name
     * </pre>
     *
     * @return the root of the test node hierarchy
     */
    protected ConfigurationNode setUpNodes()
    {
        DefaultConfigurationNode rootNode = new DefaultConfigurationNode();

        DefaultConfigurationNode nodeTables = new DefaultConfigurationNode(
                "tables");
        rootNode.addChild(nodeTables);
        for (int i = 0; i < tables.length; i++)
        {
            DefaultConfigurationNode nodeTable = new DefaultConfigurationNode(
                    "table");
            nodeTables.addChild(nodeTable);
            nodeTable.addChild(new DefaultConfigurationNode("name", tables[i]));
            nodeTable.addAttribute(new DefaultConfigurationNode("type",
                    tabTypes[i]));
            DefaultConfigurationNode nodeFields = new DefaultConfigurationNode(
                    "fields");
            nodeTable.addChild(nodeFields);

            for (int j = 0; j < fields[i].length; j++)
            {
                nodeFields.addChild(createFieldNode(fields[i][j]));
            }
        }

        DefaultConfigurationNode nodeConn = new DefaultConfigurationNode(
                "connection.settings");
        rootNode.addChild(nodeConn);
        nodeConn.addChild(new DefaultConfigurationNode("usr.name", "scott"));
        nodeConn.addChild(new DefaultConfigurationNode("usr.pwd", "tiger"));
        rootNode.addAttribute(new DefaultConfigurationNode("test", "true"));

        return rootNode;
    }

    /**
     * Configures the expression engine to use a different syntax.
     */
    private void setUpAlternativeSyntax()
    {
        engine.setAttributeEnd(null);
        engine.setAttributeStart("@");
        engine.setPropertyDelimiter("/");
        engine.setEscapedDelimiter(null);
        engine.setIndexStart("[");
        engine.setIndexEnd("]");
    }

    /**
     * Helper method for checking the evaluation of a key. Queries the
     * expression engine and tests if the expected results are returned.
     *
     * @param key the key
     * @param name the name of the nodes to be returned
     * @param count the number of expected result nodes
     * @return the list with the results of the query
     */
    private List checkKey(String key, String name, int count)
    {
        List nodes = engine.query(root, key);
        assertEquals("Wrong number of result nodes for key " + key, count,
                nodes.size());
        for (Iterator it = nodes.iterator(); it.hasNext();)
        {
            assertEquals("Wrong result node for key " + key, name,
                    ((ConfigurationNode) it.next()).getName());
        }
        return nodes;
    }

    /**
     * Helper method for checking the value of a node specified by the given
     * key. This method evaluates the key and checks whether the resulting node
     * has the expected value.
     *
     * @param key the key
     * @param name the expected name of the result node
     * @param value the expected value of the result node
     */
    private void checkKeyValue(String key, String name, String value)
    {
        List nodes = checkKey(key, name, 1);
        assertEquals("Wrong value for key " + key, value,
                ((ConfigurationNode) nodes.get(0)).getValue());
    }

    /**
     * Helper method for checking the path of an add operation.
     *
     * @param data the add data object
     * @param expected the expected path nodes
     */
    private void checkNodePath(NodeAddData data, String[] expected)
    {
        assertEquals("Wrong number of path nodes", expected.length, data
                .getPathNodes().size());
        Iterator it = data.getPathNodes().iterator();
        for (int i = 0; i < expected.length; i++)
        {
            assertEquals("Wrong path node " + i, expected[i], it.next());
        }
    }

    /**
     * Helper method for creating a field node with its children for the test
     * node hierarchy.
     *
     * @param name the name of the field
     * @return the field node
     */
    private static ConfigurationNode createFieldNode(String name)
    {
        DefaultConfigurationNode nodeField = new DefaultConfigurationNode(
                "field");
        nodeField.addChild(new DefaultConfigurationNode("name", name));
        return nodeField;
    }
}
