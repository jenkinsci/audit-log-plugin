/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package io.jenkins.plugins.audit;

import java.util.UUID;
import javax.annotation.Nonnull;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.audit.annotation.ClientServer;
import org.apache.logging.log4j.audit.annotation.HeaderPrefix;
import org.apache.logging.log4j.core.util.UuidUtil;

/**
 * Defines all the variables that an application needs to be available in the ThreadContext for audit logging and
 * general application usage.
 */
@HeaderPrefix("jenkins")
public final class RequestContext {
    @ClientServer
    public static final String REQUEST_ID = "requestId";
    @ClientServer
    public static final String REQUEST_URI = "requestUri";
    @ClientServer
    public static final String NODE_NAME = "nodeName";
    @ClientServer
    public static final String TIMESTAMP = "timestamp";
    @ClientServer
    public static final String USER_ID = "userId";
    @ClientServer
    public static final String IP_ADDRESS = "ipAddress";

    /**
     * The methods in this class are not required by framework components that use the RequestContext properties.
     * They are provided as a convenience for applications. If they are not provided the properties can be accessed
     * directly through the Log4j ThreadContext Map using the keys above.
     */
    public static void clear() {
        ThreadContext.clearMap();
    }

    public static String getRequestId() {
        String uuidStr = ThreadContext.get(REQUEST_ID);
        if (uuidStr == null) {
            ThreadContext.put(REQUEST_ID, UuidUtil.getTimeBasedUuid().toString());
            uuidStr = ThreadContext.get(REQUEST_ID);
        }
        return uuidStr;
    }

    public static void setIpAddress(String address) {
        ThreadContext.put(IP_ADDRESS, address);
    }

    public static String getIpAddress() {
        return ThreadContext.get(IP_ADDRESS);
    }

    public static void setUserId(@Nonnull String userId) {
        ThreadContext.put(USER_ID, userId);
    }

    public static String getUserId() {
        return ThreadContext.get(USER_ID);
    }

    public static void setRequestUri(String requestUri) {
        ThreadContext.put(REQUEST_URI, requestUri);
    }

    public static String getRequestUri() {
        return ThreadContext.get(REQUEST_URI);
    }

    public static void setNodeName(@Nonnull String nodeName) {
        ThreadContext.put(NODE_NAME, nodeName);
    }

    public static String getNodeName() {
        String nodeName = ThreadContext.get(NODE_NAME);
        if (nodeName == null) {
            ThreadContext.put(NODE_NAME, "master");
            nodeName = ThreadContext.get(NODE_NAME);
        }
        return nodeName;
    }

    public static void setTimeStamp(@Nonnull String timestamp) {
        ThreadContext.put(TIMESTAMP, timestamp);
    }

    public static String getTimeStamp() {
        return ThreadContext.get(TIMESTAMP);
    }


}
