package org.wso2.carbon.esb.http.inbound.transport.test;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.esb.integration.common.utils.CarbonLogReader;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.net.URL;
import java.util.HashMap;

/**
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

public class HttpInboundDispatchTestCase extends ESBIntegrationTest {

    private static final String requestPayload = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' >"
            + "<soapenv:Body xmlns:ser='http://services.samples' xmlns:xsd='http://services.samples/xsd'> "
            + "<ser:getQuote> <ser:request> <xsd:symbol>WSO2</xsd:symbol> </ser:request> </ser:getQuote> "
            + "</soapenv:Body></soapenv:Envelope> ";

    private CarbonLogReader carbonLogReader;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        carbonLogReader = new CarbonLogReader();
        carbonLogReader.start();

    }

    @Test(groups = "wso2.esb", description = "Inbound HTTP Super Tenant Sequence Dispatch")
    public void inboundHttpSuperSequenceTest() throws Exception {

        HttpRequestUtil.doPost(new URL("http://localhost:9090/"), requestPayload, new HashMap<String, String>());
        //this case matches with the regex but there is no api or proxy so dispatch to  super tenant main sequence
        Assert.assertTrue(carbonLogReader.assertIfLogExists("main sequence executed for call to non-existent = /"));
        carbonLogReader.clearLogs();
    }

    @Test(groups = "wso2.esb", description = "Inbound HTTP Super Tenant API Dispatch")
    public void inboundHttpSuperAPITest() throws Exception {
        axis2Client.sendSimpleStockQuoteRequest("http://localhost:9090/foo", null, "WSO2");
        Assert.assertTrue(carbonLogReader.assertIfLogExists("FOO"));
        axis2Client.sendSimpleStockQuoteRequest("http://localhost:9090/boo", null, "WSO2");
        Assert.assertTrue(carbonLogReader.assertIfLogExists("BOO"));

        /**
         * Test API dispatch to non existent API - this should trigger super tenant main sequence.
         * since this matches with inbound regex but no api or proxy found to be dispatched
         */
        carbonLogReader.clearLogs();
        HttpRequestUtil.doPost(new URL("http://localhost:9090/idontexist"), requestPayload, new HashMap<String, String>());
        Assert.assertTrue(carbonLogReader.assertIfLogExists("main sequence executed for call to non-existent = /idontexist"));
        carbonLogReader.clearLogs();
    }

    @Test(groups = "wso2.esb", description = "Inbound HTTP Super Tenant Default Main Sequence Dispatch")
    public void inboundHttpSuperDefaultMainTest() throws Exception {
        HttpRequestUtil.doPost(new URL("http://localhost:9091/"), requestPayload, new HashMap<String, String>());
        Assert.assertTrue(carbonLogReader.assertIfLogExists("main sequence executed for call to non-existent = /"));
        carbonLogReader.clearLogs();
    }

    @Test(groups = "wso2.esb", description = "Inbound HTTP Super Tenant Proxy Dispatch")
    public void inboundHttpSuperProxyDispatchTest() throws Exception {
        axis2Client.sendSimpleStockQuoteRequest("http://localhost:9090/services/HttpInboundDispatchTestProxy", null, "WSO2");
        Assert.assertTrue(carbonLogReader.assertIfLogExists("PROXY_HIT"));
        carbonLogReader.clearLogs();
    }

    @AfterTest(alwaysRun = true)
    public void destroy() throws Exception {
        carbonLogReader.stop();
    }
}
