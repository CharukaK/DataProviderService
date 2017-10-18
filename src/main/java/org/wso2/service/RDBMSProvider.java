/*
 * Copyright (c) 2017 WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.wso2.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

/**
 * Provider instance for a RDBMS session
 */
public class RDBMSProvider implements DataProvider{
    private static final Logger LOGGER = LoggerFactory.getLogger(RDBMSProvider.class);
    private RDBMSProviderConf providerConf;
    private Connection connection;







    /**
     * get connection object for the instance
     * @return java.sql.Connection object for the dataProvider Configuration
     */
    private Connection getConnection(){


        return null;
    }

    @Override
    public DataProvider init(RDBMSProviderConf providerConf) {
        this.providerConf=providerConf;
        return this;
    }

    @Override
    public void shutDown() {

    }

    @Override
    public DataProvider start() {
        return this;
    }
}
