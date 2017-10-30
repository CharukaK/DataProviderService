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

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * DataProviderEndpoint Websocket all the gadgets connect to to this
 */
@ServerEndpoint(value = "/data-provider")
public class DataProviderEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataProviderEndpoint.class);
    private Map<String, DataProvider> providerMap = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("Connected with user : " + session.getId());
    }

    @OnMessage
    public void onMessage(String text, Session session) {
//        System.out.println(text);
        switch (text.split(";")[0]) {
            case "rdbms": {
//TODO : DAta provider factory , don't use split
                RDBMSProviderConf conf = new Gson().fromJson(text.split(";")[1], RDBMSProviderConf.class);
                DataProvider rdbmsProvider =
                        new RDBMSProvider(conf, session)
                                .setLastRow(Integer.parseInt(text.split(";")[2]))
                                .start();
                //initialize and
                // start the
                // data provider
                providerMap.put(session.getId(), rdbmsProvider); //save the data provider instance in the Map
                break;
            }
            case "ping": {//to communicate with client to check the connection is open
                new Thread(() -> {//TODO: use a separate method
                    try {
                        session.getBasicRemote().sendText("pong");
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }).start();
                break;
            }

            default://TODO: send bacllk error message
                LOGGER.error("Unknown Provider");
        }
    }

    @OnClose
    public void onClose(Session session) {
        if (providerMap.containsKey(session.getId())) {
            providerMap.get(session.getId()).shutDown();
            providerMap.remove(session.getId());
        }
    }

}
