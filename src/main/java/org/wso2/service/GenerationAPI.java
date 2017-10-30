/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package org.wso2.service;


import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;




@Path("/chart-gen-service")
public class GenerationAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerationAPI.class);

    @GET
    @Path("/providers")
    public String getProviders() {
        String content = "";


        try {
            content = new Scanner(new File(getClass().getClassLoader()
                    .getResource("provider-configs.json").getFile()),"utf-8").useDelimiter("\\Z").next();
        } catch (FileNotFoundException | NullPointerException e) {
            LOGGER.error(e.getMessage(),e);
        }

        return content;
    }

    @GET
    @Path("/chart-configs")
    public String getChartConfigs(){
        String content = "";

        try {
            content = new Scanner(new File(getClass().getClassLoader()
                    .getResource("chart-configs.json").getFile()),"utf-8").useDelimiter("\\Z").next();
        } catch (FileNotFoundException | NullPointerException e) {
            LOGGER.error(e.getMessage(),e);
        }
        return content;
    }


    @POST
    @Path("/poll-db")
    public String pollDb(String message){
        int lastRow=Integer.parseInt(message.split(";")[1]);
        DataSetMetadata metadata = null;
        Object[][] data = new Object[0][];
        RDBMSProviderConf conf=new Gson().fromJson(message.split(";")[0],RDBMSProviderConf.class);
        try {
            Connection conn=RDBMSProvider.getConnection(conf.getUrl(),conf.getUsername(),conf.getPassword());
            Statement statement=conn.createStatement();
            ResultSet resultSet=statement.executeQuery(conf.getQuery());
            ResultSetMetaData rsmd=resultSet.getMetaData();
            metadata= new DataSetMetadata(rsmd.getColumnCount());
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                metadata.put(i - 1, rsmd.getColumnName(i),
                        RDBMSProvider.getMetadataTypes(conf.getUrl().split(":")[1], rsmd.getColumnTypeName(i)));
            }
            resultSet.last();
            data = new Object[resultSet.getRow() - lastRow][metadata.getNames().length];
            resultSet.absolute(lastRow);
            while (resultSet.next()){
                for (int i = 0; i < metadata.getNames().length; i++) {
                    if(metadata.getTypes()[i].equalsIgnoreCase("linear")){
                        data[resultSet.getRow()-lastRow -1][i]=resultSet.getDouble(i+1);
                    }else if(metadata.getTypes()[i].equalsIgnoreCase("ordinal")){
                        data[resultSet.getRow()-lastRow -1][i]=resultSet.getString(i+1);
                    }else {
                        data[resultSet.getRow()-lastRow -1][i]=resultSet.getDate(i+1);
                    }
                }
            }

        } catch (SQLException e) {
            LOGGER.error("SQL Error Occurred",e);
        }


        return "metadata;"+new Gson().toJson(metadata)+"|data;"+new Gson().toJson(data);
    }



    @GET
    @Path("/test")
    public void testAsync(){

    }

//    @GET
//    @Path("/rest-db-poll")
//    public String

}
