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
package org.apache.commons.vfs.provider.https;

import org.apache.commons.vfs.provider.http.HttpFileProvider;

/**
 * An HTTPS provider that uses commons-httpclient.
 *
 * @author <a href="mailto:adammurdoch@apache.org">Adam Murdoch</a>
 * @version $Revision: 485638 $ $Date: 2006-12-11 13:20:55 +0100 (Mo, 11 Dez 2006) $
 */
public class HttpsFileProvider
    extends HttpFileProvider
{
	public HttpsFileProvider()
    {
        super();
        setFileNameParser(HttpsFileNameParser.getInstance());
    }
}
