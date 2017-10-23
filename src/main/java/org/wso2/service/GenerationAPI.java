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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
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

        System.out.println(message);
        return "hello";
    }



    @GET
    @Path("/test")
    public void testAsync(){

    }

//    @GET
//    @Path("/rest-db-poll")
//    public String

}
