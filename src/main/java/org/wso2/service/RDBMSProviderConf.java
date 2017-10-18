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
