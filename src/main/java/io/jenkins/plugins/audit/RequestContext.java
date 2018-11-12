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
import java.util.function.Supplier;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.audit.annotation.Chained;
import org.apache.logging.log4j.audit.annotation.ClientServer;
import org.apache.logging.log4j.audit.annotation.HeaderPrefix;
import org.apache.logging.log4j.audit.annotation.Local;
import org.apache.logging.log4j.core.util.NetUtils;
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
    @ClientServer
    public static final String SESSION_ID = "sessionId";
    @ClientServer
    public static final String LOGIN_ID = "loginId";
    @Local
    public static final String CALLING_HOST = "callingHost";

    public static final String HOST_NAME = "hostName";

    private static final String LOCAL_HOST_NAME = NetUtils.getLocalHostname();

    /**
     * The Supplier is used to populate the hostName key after the hostName value from the caller has been
     * placed into the callingHost map entry.
     */
    @Chained(fieldName = HOST_NAME, chainedFieldName = CALLING_HOST)
    public static final Supplier<String> LOCAL_HOST_SUPPLIER = () -> LOCAL_HOST_NAME;

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
        UUID uuid;
        if (uuidStr == null) {
            uuid = UuidUtil.getTimeBasedUuid();
            ThreadContext.put(REQUEST_ID, uuid.toString());
        }
        return uuidStr;
    }

    public static String getSessionId() {
        return ThreadContext.get(SESSION_ID);
    }

    public static void setSessionId(UUID sessionId) {
        if (sessionId != null) {
            ThreadContext.put(SESSION_ID, sessionId.toString());
        }
    }

    public static void setSessionId(String sessionId) {
        if (sessionId != null) {
            ThreadContext.put(SESSION_ID, sessionId);
        }
    }

    public static void setIpAddress(String address) {
        ThreadContext.put(IP_ADDRESS, address);
    }

    public static String getIpAddress() {
        return ThreadContext.get(IP_ADDRESS);
    }

    public static void setUserId(String userId) {
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

    public static void setNodeName(String nodeName) {
        ThreadContext.put(NODE_NAME, nodeName);
    }

    public static String getNodeName() {
        return ThreadContext.get(NODE_NAME);
    }

    public static void setTimeStamp(String timestamp) {
        ThreadContext.put(TIMESTAMP, timestamp);
    }

    public static String getTimeStamp() {
        return ThreadContext.get(TIMESTAMP);
    }

    public static void setLoginId(String loginId) {
        ThreadContext.put(LOGIN_ID, loginId);
    }

    public static String getLoginId() {
        return ThreadContext.get(LOGIN_ID);
    }

    public static String getHostName() {
        return ThreadContext.get(HOST_NAME);
    }

    public static void setHostName(String hostName) {
        ThreadContext.put(HOST_NAME, hostName);
    }

    public static String getCallingHost() {
        return ThreadContext.get(CALLING_HOST);
    }

    public static void setCallingHost(String hostName) {
        ThreadContext.put(CALLING_HOST, hostName);
    }


}
