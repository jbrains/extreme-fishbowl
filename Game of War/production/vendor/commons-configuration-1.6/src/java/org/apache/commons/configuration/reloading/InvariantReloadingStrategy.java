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

package org.apache.commons.configuration.reloading;

import org.apache.commons.configuration.FileConfiguration;

/**
 * A strategy that never triggers a reloading.
 *
 * @author Emmanuel Bourg
 * @version $Revision: 439648 $, $Date: 2006-09-02 22:42:10 +0200 (Sa, 02 Sep 2006) $
 * @since 1.1
 */
public class InvariantReloadingStrategy implements ReloadingStrategy
{
    public void setConfiguration(FileConfiguration configuration)
    {
    }

    public void init()
    {
    }

    public boolean reloadingRequired()
    {
        return false;
    }

    public void reloadingPerformed()
    {
    }
}
