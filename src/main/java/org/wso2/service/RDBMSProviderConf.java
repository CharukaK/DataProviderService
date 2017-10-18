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

/**
 * template class for the provider configuration sent from the client
 */
public class RDBMSProviderConf {
    private String url;
    private String table;
    private String username;
    private String password;
    private String query;


    public RDBMSProviderConf(String url, String table, String username, String password, String query) {
        this.url = url;
        this.table = table;
        this.username = username;
        this.password = password;
        this.query = query;
    }


    public String getUrl() {
        return url;
    }

    public String getTable() {
        return table;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getQuery() {
        return query;
    }
}
