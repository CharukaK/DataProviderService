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


import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.Properties;

/**
 * Provider instance for a RDBMS session
 */
public class RDBMSProvider implements DataProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(RDBMSProvider.class);
    private RDBMSProviderConf providerConf;
    private Connection connection;
    private Thread pollThread;
    private Boolean stopPolling;
    private Boolean sentMetaData;
    private Session session;
    private DataSetMetadata metadata;
    private int prevLastRow;

    public RDBMSProvider(RDBMSProviderConf providerConf, Session session) {
        prevLastRow = 0;
        stopPolling = false;
        sentMetaData = false;
        this.providerConf = providerConf;
        this.session = session;
//        LOGGER.info("RDBMS provider initiated");
    }

    /**
     * get connection object for the instance
     *
     * @return java.sql.Connection object for the dataProvider Configuration
     */
    public static Connection getConnection(String url,String username, String password) throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", username);
        properties.setProperty("password", password);
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");
        return DriverManager.getConnection(url, properties);
    }


    @Override
    public void shutDown() {
        try {
            stopPolling = true;
            pollThread.join();
            connection.close();
        } catch (InterruptedException | SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public DataProvider start() {
//        LOGGER.info("Polling started");
        try {
            connection = RDBMSProvider.getConnection(providerConf.getUrl(),providerConf.getUsername(),
                                                                                   providerConf.getPassword());
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        pollThread = new Thread(() -> {

            while (!stopPolling) {


                try {
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(providerConf.getQuery());

                    if (!sentMetaData) {
                        ResultSetMetaData rsmd = resultSet.getMetaData();
                        metadata = new DataSetMetadata(rsmd.getColumnCount());

                        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                            metadata.put(i - 1, rsmd.getColumnName(i),
                                    RDBMSProvider.getMetadataTypes(providerConf.getUrl().split(":")[1], rsmd
                                            .getColumnTypeName
                                            (i)));
                        }

                        sendMessage(session, "metadata;" + new Gson().toJson(metadata));
                        sentMetaData = true;


                    }

                    resultSet.last();
                    int currentLast = resultSet.getRow();
                    resultSet.beforeFirst();
                    Object[][] data = new Object[currentLast - prevLastRow][metadata.getNames().length];
                    if (prevLastRow < currentLast) {

                        while (resultSet.next()) {
                            if (resultSet.getRow() <= prevLastRow) {
                                continue;
                            }

                            for (int i = 1; i <= metadata.getNames().length; i++) {
                                if (metadata.getTypes()[i - 1].equalsIgnoreCase("linear")) {
                                    data[resultSet.getRow() - prevLastRow - 1][i - 1] = resultSet.getDouble(i);
                                } else if (metadata.getTypes()[i - 1].equalsIgnoreCase("ordinal")) {
                                    data[resultSet.getRow() - prevLastRow - 1][i - 1] = resultSet.getString(i);
                                } else {
                                    data[resultSet.getRow() - prevLastRow - 1][i - 1] = resultSet.getDate(i);
                                }
                            }


                        }

                        sendMessage(session, "data;" + new Gson().toJson(data)+";"+currentLast);
                        prevLastRow = currentLast;
                    }


                } catch (SQLException | NullPointerException e) {
                    LOGGER.error(e.getMessage(), e);
                    stopPolling=true;
                }

                try {
                    Thread.sleep(providerConf.getPollingInterval());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }

        });
        pollThread.start();
        return this;
    }


    /**
     * send messages to the frontend client
     *
     * @param session Session object between client and the socket endpoint
     * @param message String message to be sent
     */
    private void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Get metadata type(linear,ordinal,time) for the given data type of the data base
     * @param dbType String name of the database that the datatype belongs
     * @param dataType String data type name provided by the result set metadata
     * @return String metadata type
     */
    public static String getMetadataTypes(String dbType, String dataType) {
        String[] linearTypes = new String[0];
        String[] ordinalTypes = new String[0];
//        String[] timeTypes;
        //ToDo: data type implementation for supported databases
        switch (dbType) {
            case "mysql":
                linearTypes = new String[]{"INTEGER", "INT", "SMALLINT", "TINYINT", "MEDIUMINT", "BIGINT", "DECIMAL", "NUMERIC", "FLOAT", "DOUBLE"};
                ordinalTypes = new String[]{"CHAR", "VARCHAR", "BINARY", "VARBINARY", "BLOB", "TEXT", "ENUM", "SET"};
                break;
            case "postgresql":
                break;
            case "derby":
                break;
            case "oracle":
                linearTypes = new String[]{"NUMBER","BINARY_FLOAT","BINARY_DOUBLE"};
                ordinalTypes = new String[]{"CHAR", "VARCHAR","VARCHAR2", "NCHAR","NVARCHAR2",};
                break;
            default:
                LOGGER.warn("Unknown database");
                break;
        }

        if (Arrays.asList(linearTypes).contains(dataType)) {
            return "linear";
        } else if (Arrays.asList(ordinalTypes).contains(dataType)) {
            return "ordinal";
        } else {
            return "time";
        }

    }

    public RDBMSProvider setlastRow(int prevLastRow) {
        this.prevLastRow = prevLastRow;
        return this;
    }
}
