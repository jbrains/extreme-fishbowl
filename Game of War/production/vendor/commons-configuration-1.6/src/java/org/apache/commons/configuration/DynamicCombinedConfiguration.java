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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.configuration.event.ConfigurationErrorListener;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.ExpressionEngine;
import org.apache.commons.configuration.tree.NodeCombiner;

/**
 * DynamicCombinedConfiguration allows a set of CombinedConfigurations to be used. Each CombinedConfiguration
 * is referenced by a key that is dynamically constructed from a key pattern on each call. The key pattern
 * will be resolved using the configured ConfigurationInterpolator.
 * @since 1.6
 * @author <a
 * href="http://commons.apache.org/configuration/team-list.html">Commons
 * Configuration team</a>
 * @version $Id: DynamicCombinedConfiguration.java 727955 2008-12-19 07:06:16Z oheger $
 */
public class DynamicCombinedConfiguration extends CombinedConfiguration
{
    /**
     * Prevent recursion while resolving unprefixed properties.
     */
    private static ThreadLocal recursive = new ThreadLocal()
    {
        protected synchronized Object initialValue()
        {
            return Boolean.FALSE;
        }
    };

    /** The CombinedConfigurations */
    private Map configs = new HashMap();

    /** Stores a list with the contained configurations. */
    private List configurations = new ArrayList();

    /** Stores a map with the named configurations. */
    private Map namedConfigurations = new HashMap();

    /** The key pattern for the CombinedConfiguration map */
    private String keyPattern;

    /** Stores the combiner. */
    private NodeCombiner nodeCombiner;

    /**
     * Creates a new instance of <code>CombinedConfiguration</code> and
     * initializes the combiner to be used.
     *
     * @param comb the node combiner (can be <b>null</b>, then a union combiner
     * is used as default)
     */
    public DynamicCombinedConfiguration(NodeCombiner comb)
    {
        super();
        setNodeCombiner(comb);
    }

    /**
     * Creates a new instance of <code>CombinedConfiguration</code> that uses
     * a union combiner.
     *
     * @see org.apache.commons.configuration.tree.UnionCombiner
     */
    public DynamicCombinedConfiguration()
    {
        super();
    }

    public void setKeyPattern(String pattern)
    {
        this.keyPattern = pattern;
    }

    public String getKeyPattern()
    {
        return this.keyPattern;
    }

    /**
     * Returns the node combiner that is used for creating the combined node
     * structure.
     *
     * @return the node combiner
     */
    public NodeCombiner getNodeCombiner()
    {
        return nodeCombiner;
    }

    /**
     * Sets the node combiner. This object will be used when the combined node
     * structure is to be constructed. It must not be <b>null</b>, otherwise an
     * <code>IllegalArgumentException</code> exception is thrown. Changing the
     * node combiner causes an invalidation of this combined configuration, so
     * that the new combiner immediately takes effect.
     *
     * @param nodeCombiner the node combiner
     */
    public void setNodeCombiner(NodeCombiner nodeCombiner)
    {
        if (nodeCombiner == null)
        {
            throw new IllegalArgumentException(
                    "Node combiner must not be null!");
        }
        this.nodeCombiner = nodeCombiner;
        invalidateAll();
    }
    /**
     * Adds a new configuration to this combined configuration. It is possible
     * (but not mandatory) to give the new configuration a name. This name must
     * be unique, otherwise a <code>ConfigurationRuntimeException</code> will
     * be thrown. With the optional <code>at</code> argument you can specify
     * where in the resulting node structure the content of the added
     * configuration should appear. This is a string that uses dots as property
     * delimiters (independent on the current expression engine). For instance
     * if you pass in the string <code>&quot;database.tables&quot;</code>,
     * all properties of the added configuration will occur in this branch.
     *
     * @param config the configuration to add (must not be <b>null</b>)
     * @param name the name of this configuration (can be <b>null</b>)
     * @param at the position of this configuration in the combined tree (can be
     * <b>null</b>)
     */
    public void addConfiguration(AbstractConfiguration config, String name,
            String at)
    {
        ConfigData cd = new ConfigData(config, name, at);
        configurations.add(cd);
        if (name != null)
        {
            namedConfigurations.put(name, config);
        }
    }
       /**
     * Returns the number of configurations that are contained in this combined
     * configuration.
     *
     * @return the number of contained configurations
     */
    public int getNumberOfConfigurations()
    {
        return configurations.size();
    }

    /**
     * Returns the configuration at the specified index. The contained
     * configurations are numbered in the order they were added to this combined
     * configuration. The index of the first configuration is 0.
     *
     * @param index the index
     * @return the configuration at this index
     */
    public Configuration getConfiguration(int index)
    {
        ConfigData cd = (ConfigData) configurations.get(index);
        return cd.getConfiguration();
    }

    /**
     * Returns the configuration with the given name. This can be <b>null</b>
     * if no such configuration exists.
     *
     * @param name the name of the configuration
     * @return the configuration with this name
     */
    public Configuration getConfiguration(String name)
    {
        return (Configuration) namedConfigurations.get(name);
    }

    /**
     * Returns a set with the names of all configurations contained in this
     * combined configuration. Of course here are only these configurations
     * listed, for which a name was specified when they were added.
     *
     * @return a set with the names of the contained configurations (never
     * <b>null</b>)
     */
    public Set getConfigurationNames()
    {
        return namedConfigurations.keySet();
    }

    /**
     * Removes the configuration with the specified name.
     *
     * @param name the name of the configuration to be removed
     * @return the removed configuration (<b>null</b> if this configuration
     * was not found)
     */
    public Configuration removeConfiguration(String name)
    {
        Configuration conf = getConfiguration(name);
        if (conf != null)
        {
            removeConfiguration(conf);
        }
        return conf;
    }

    /**
     * Removes the specified configuration from this combined configuration.
     *
     * @param config the configuration to be removed
     * @return a flag whether this configuration was found and could be removed
     */
    public boolean removeConfiguration(Configuration config)
    {
        for (int index = 0; index < getNumberOfConfigurations(); index++)
        {
            if (((ConfigData) configurations.get(index)).getConfiguration() == config)
            {
                removeConfigurationAt(index);

            }
        }

        return super.removeConfiguration(config);
    }

    /**
     * Removes the configuration at the specified index.
     *
     * @param index the index
     * @return the removed configuration
     */
    public Configuration removeConfigurationAt(int index)
    {
        ConfigData cd = (ConfigData) configurations.remove(index);
        if (cd.getName() != null)
        {
            namedConfigurations.remove(cd.getName());
        }
        return super.removeConfigurationAt(index);
    }
    /**
     * Returns the configuration root node of this combined configuration. This
     * method will construct a combined node structure using the current node
     * combiner if necessary.
     *
     * @return the combined root node
     */
    public ConfigurationNode getRootNode()
    {
        return getCurrentConfig().getRootNode();
    }

    public void setRootNode(ConfigurationNode rootNode)
    {
        if (configs != null)
        {
            this.getCurrentConfig().setRootNode(rootNode);
        }
        else
        {
            super.setRootNode(rootNode);
        }
    }

    public void addProperty(String key, Object value)
    {
        this.getCurrentConfig().addProperty(key, value);
    }

    public void clear()
    {
        if (configs != null)
        {
            this.getCurrentConfig().clear();
        }
    }

    public void clearProperty(String key)
    {
        this.getCurrentConfig().clearProperty(key);
    }

    public boolean containsKey(String key)
    {
        return this.getCurrentConfig().containsKey(key);
    }

    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue)
    {
        return this.getCurrentConfig().getBigDecimal(key, defaultValue);
    }

    public BigDecimal getBigDecimal(String key)
    {
        return this.getCurrentConfig().getBigDecimal(key);
    }

    public BigInteger getBigInteger(String key, BigInteger defaultValue)
    {
        return this.getCurrentConfig().getBigInteger(key, defaultValue);
    }

    public BigInteger getBigInteger(String key)
    {
        return this.getCurrentConfig().getBigInteger(key);
    }

    public boolean getBoolean(String key, boolean defaultValue)
    {
        return this.getCurrentConfig().getBoolean(key, defaultValue);
    }

    public Boolean getBoolean(String key, Boolean defaultValue)
    {
        return this.getCurrentConfig().getBoolean(key, defaultValue);
    }

    public boolean getBoolean(String key)
    {
        return this.getCurrentConfig().getBoolean(key);
    }

    public byte getByte(String key, byte defaultValue)
    {
        return this.getCurrentConfig().getByte(key, defaultValue);
    }

    public Byte getByte(String key, Byte defaultValue)
    {
        return this.getCurrentConfig().getByte(key, defaultValue);
    }

    public byte getByte(String key)
    {
        return this.getCurrentConfig().getByte(key);
    }

    public double getDouble(String key, double defaultValue)
    {
        return this.getCurrentConfig().getDouble(key, defaultValue);
    }

    public Double getDouble(String key, Double defaultValue)
    {
        return this.getCurrentConfig().getDouble(key, defaultValue);
    }

    public double getDouble(String key)
    {
        return this.getCurrentConfig().getDouble(key);
    }

    public float getFloat(String key, float defaultValue)
    {
        return this.getCurrentConfig().getFloat(key, defaultValue);
    }

    public Float getFloat(String key, Float defaultValue)
    {
        return this.getCurrentConfig().getFloat(key, defaultValue);
    }

    public float getFloat(String key)
    {
        return this.getCurrentConfig().getFloat(key);
    }

    public int getInt(String key, int defaultValue)
    {
        return this.getCurrentConfig().getInt(key, defaultValue);
    }

    public int getInt(String key)
    {
        return this.getCurrentConfig().getInt(key);
    }

    public Integer getInteger(String key, Integer defaultValue)
    {
        return this.getCurrentConfig().getInteger(key, defaultValue);
    }

    public Iterator getKeys()
    {
        return this.getCurrentConfig().getKeys();
    }

    public Iterator getKeys(String prefix)
    {
        return this.getCurrentConfig().getKeys(prefix);
    }

    public List getList(String key, List defaultValue)
    {
        return this.getCurrentConfig().getList(key, defaultValue);
    }

    public List getList(String key)
    {
        return this.getCurrentConfig().getList(key);
    }

    public long getLong(String key, long defaultValue)
    {
        return this.getCurrentConfig().getLong(key, defaultValue);
    }

    public Long getLong(String key, Long defaultValue)
    {
        return this.getCurrentConfig().getLong(key, defaultValue);
    }

    public long getLong(String key)
    {
        return this.getCurrentConfig().getLong(key);
    }

    public Properties getProperties(String key)
    {
        return this.getCurrentConfig().getProperties(key);
    }

    public Object getProperty(String key)
    {
        return this.getCurrentConfig().getProperty(key);
    }

    public short getShort(String key, short defaultValue)
    {
        return this.getCurrentConfig().getShort(key, defaultValue);
    }

    public Short getShort(String key, Short defaultValue)
    {
        return this.getCurrentConfig().getShort(key, defaultValue);
    }

    public short getShort(String key)
    {
        return this.getCurrentConfig().getShort(key);
    }

    public String getString(String key, String defaultValue)
    {
        return this.getCurrentConfig().getString(key, defaultValue);
    }

    public String getString(String key)
    {
        return this.getCurrentConfig().getString(key);
    }

    public String[] getStringArray(String key)
    {
        return this.getCurrentConfig().getStringArray(key);
    }

    public boolean isEmpty()
    {
        return this.getCurrentConfig().isEmpty();
    }

    public void setProperty(String key, Object value)
    {
        if (configs != null)
        {
            this.getCurrentConfig().setProperty(key, value);
        }
    }

    public Configuration subset(String prefix)
    {
        return this.getCurrentConfig().subset(prefix);
    }

    public Node getRoot()
    {
        return this.getCurrentConfig().getRoot();
    }

    public void setRoot(Node node)
    {
        if (configs != null)
        {
            this.getCurrentConfig().setRoot(node);
        }
        else
        {
            super.setRoot(node);
        }
    }

    public ExpressionEngine getExpressionEngine()
    {
        return super.getExpressionEngine();
    }

    public void setExpressionEngine(ExpressionEngine expressionEngine)
    {
        super.setExpressionEngine(expressionEngine);
    }

    public void addNodes(String key, Collection nodes)
    {
        this.getCurrentConfig().addNodes(key, nodes);
    }

    public SubnodeConfiguration configurationAt(String key, boolean supportUpdates)
    {
        return this.getCurrentConfig().configurationAt(key, supportUpdates);
    }

    public SubnodeConfiguration configurationAt(String key)
    {
        return this.getCurrentConfig().configurationAt(key);
    }

    public List configurationsAt(String key)
    {
        return this.getCurrentConfig().configurationsAt(key);
    }

    public void clearTree(String key)
    {
        this.getCurrentConfig().clearTree(key);
    }

    public int getMaxIndex(String key)
    {
        return this.getCurrentConfig().getMaxIndex(key);
    }

    public Configuration interpolatedConfiguration()
    {
        return this.getCurrentConfig().interpolatedConfiguration();
    }


    /**
     * Returns the configuration source, in which the specified key is defined.
     * This method will determine the configuration node that is identified by
     * the given key. The following constellations are possible:
     * <ul>
     * <li>If no node object is found for this key, <b>null</b> is returned.</li>
     * <li>If the key maps to multiple nodes belonging to different
     * configuration sources, a <code>IllegalArgumentException</code> is
     * thrown (in this case no unique source can be determined).</li>
     * <li>If exactly one node is found for the key, the (child) configuration
     * object, to which the node belongs is determined and returned.</li>
     * <li>For keys that have been added directly to this combined
     * configuration and that do not belong to the namespaces defined by
     * existing child configurations this configuration will be returned.</li>
     * </ul>
     *
     * @param key the key of a configuration property
     * @return the configuration, to which this property belongs or <b>null</b>
     * if the key cannot be resolved
     * @throws IllegalArgumentException if the key maps to multiple properties
     * and the source cannot be determined, or if the key is <b>null</b>
     */
    public Configuration getSource(String key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("Key must not be null!");
        }
        return getCurrentConfig().getSource(key);
    }

    public void addConfigurationListener(ConfigurationListener l)
    {
        super.addConfigurationListener(l);

        Iterator iter = configs.values().iterator();
        while (iter.hasNext())
        {
            CombinedConfiguration config = (CombinedConfiguration) iter.next();
            config.addConfigurationListener(l);
        }
    }

    public boolean removeConfigurationListener(ConfigurationListener l)
    {
        Iterator iter = configs.values().iterator();
        while (iter.hasNext())
        {
            CombinedConfiguration config = (CombinedConfiguration) iter.next();
            config.removeConfigurationListener(l);
        }
        return super.removeConfigurationListener(l);
    }

    public Collection getConfigurationListeners()
    {
        return super.getConfigurationListeners();
    }

    public void clearConfigurationListeners()
    {
        Iterator iter = configs.values().iterator();
        while (iter.hasNext())
        {
            CombinedConfiguration config = (CombinedConfiguration) iter.next();
            config.clearConfigurationListeners();
        }
        super.clearConfigurationListeners();
    }

    public void addErrorListener(ConfigurationErrorListener l)
    {
        Iterator iter = configs.values().iterator();
        while (iter.hasNext())
        {
            CombinedConfiguration config = (CombinedConfiguration) iter.next();
            config.addErrorListener(l);
        }
        super.addErrorListener(l);
    }

    public boolean removeErrorListener(ConfigurationErrorListener l)
    {
        Iterator iter = configs.values().iterator();
        while (iter.hasNext())
        {
            CombinedConfiguration config = (CombinedConfiguration) iter.next();
            config.removeErrorListener(l);
        }
        return super.removeErrorListener(l);
    }

    public void clearErrorListeners()
    {
        Iterator iter = configs.values().iterator();
        while (iter.hasNext())
        {
            CombinedConfiguration config = (CombinedConfiguration) iter.next();
            config.clearErrorListeners();
        }
        super.clearErrorListeners();
    }

    public Collection getErrorListeners()
    {
        return super.getErrorListeners();
    }



    /**
     * Returns a copy of this object. This implementation performs a deep clone,
     * i.e. all contained configurations will be cloned, too. For this to work,
     * all contained configurations must be cloneable. Registered event
     * listeners won't be cloned. The clone will use the same node combiner than
     * the original.
     *
     * @return the copied object
     */
    public Object clone()
    {
        return super.clone();
    }



    /**
     * Invalidates the current combined configuration. This means that the next time a
     * property is accessed the combined node structure must be re-constructed.
     * Invalidation of a combined configuration also means that an event of type
     * <code>EVENT_COMBINED_INVALIDATE</code> is fired. Note that while other
     * events most times appear twice (once before and once after an update),
     * this event is only fired once (after update).
     */
    public void invalidate()
    {
        getCurrentConfig().invalidate();
    }

    public void invalidateAll()
    {
        if (configs == null)
        {
            return;
        }
        Iterator iter = configs.values().iterator();
        while (iter.hasNext())
        {
           CombinedConfiguration config = (CombinedConfiguration) iter.next();
           config.invalidate();
        }
    }

    /*
     * Don't allow resolveContainerStore to be called recursively.
     * @param key The key to resolve.
     * @return The value of the key.
     */
    protected Object resolveContainerStore(String key)
    {
        if (((Boolean) recursive.get()).booleanValue())
        {
            return null;
        }
        recursive.set(Boolean.TRUE);
        try
        {
            return super.resolveContainerStore(key);
        }
        finally
        {
            recursive.set(Boolean.FALSE);
        }
    }

    private CombinedConfiguration getCurrentConfig()
    {
        String key = getSubstitutor().replace(keyPattern);
        CombinedConfiguration config;
        synchronized (getNodeCombiner())
        {
            config = (CombinedConfiguration) configs.get(key);
            if (config == null)
            {
                config = new CombinedConfiguration(getNodeCombiner());
                config.setExpressionEngine(this.getExpressionEngine());
                Iterator iter = config.getErrorListeners().iterator();
                while (iter.hasNext())
                {
                    ConfigurationErrorListener listener = (ConfigurationErrorListener) iter.next();
                    config.addErrorListener(listener);
                }
                iter = config.getConfigurationListeners().iterator();
                while (iter.hasNext())
                {
                    ConfigurationListener listener = (ConfigurationListener) iter.next();
                    config.addConfigurationListener(listener);
                }
                config.setForceReloadCheck(isForceReloadCheck());
                iter = configurations.iterator();
                while (iter.hasNext())
                {
                    ConfigData data = (ConfigData) iter.next();
                    config.addConfiguration(data.getConfiguration(), data.getName(),
                            data.getAt());
                }
                configs.put(key, config);
            }
        }
        return config;
    }

    /**
     * Internal class that identifies each Configuration.
     */
    static class ConfigData
    {
        /** Stores a reference to the configuration. */
        private AbstractConfiguration configuration;

        /** Stores the name under which the configuration is stored. */
        private String name;

        /** Stores the at string.*/
        private String at;

                /**
         * Creates a new instance of <code>ConfigData</code> and initializes
         * it.
         *
         * @param config the configuration
         * @param n the name
         * @param at the at position
         */
        public ConfigData(AbstractConfiguration config, String n, String at)
        {
            configuration = config;
            name = n;
            this.at = at;
        }

                /**
         * Returns the stored configuration.
         *
         * @return the configuration
         */
        public AbstractConfiguration getConfiguration()
        {
            return configuration;
        }

        /**
         * Returns the configuration's name.
         *
         * @return the name
         */
        public String getName()
        {
            return name;
        }

        /**
         * Returns the at position of this configuration.
         *
         * @return the at position
         */
        public String getAt()
        {
            return at;
        }

    }
}
