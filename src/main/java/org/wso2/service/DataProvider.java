package org.wso2.service;

public interface DataProvider {
    /**
     * this method initializes the provider instance for the session
     * @param RDBMSProviderConf RDBMS provider configuration
     */
    DataProvider init(RDBMSProviderConf RDBMSProviderConf);


    /**
     * terminates the provider session and stop the polling thread
     */
    void shutDown();

    /**
     * start polling data and pushing to the client
     */
    DataProvider start();
}
