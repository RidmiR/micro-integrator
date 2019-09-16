/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.micro.integrator.transport.handlers.requestprocessors.swagger.format;

import org.apache.axiom.ext.io.StreamCopyException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.util.blob.BlobOutputStream;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.protocol.HTTP;
import org.apache.synapse.config.SynapseConfigUtils;
import org.apache.synapse.config.SynapseConfiguration;
import org.apache.synapse.registry.Registry;
import org.apache.synapse.rest.API;
import org.apache.synapse.rest.version.DefaultStrategy;
import org.wso2.carbon.mediation.commons.rest.api.swagger.SwaggerConstants;
import org.wso2.micro.core.Constants;
import org.wso2.micro.core.transports.CarbonHttpRequest;
import org.wso2.micro.core.transports.CarbonHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

/**
 * This is the base class used by swagger formatter classes(JSON and YAML) and contains generic functions.
 */
public class SwaggerGenerator {
    private static final Log log = LogFactory.getLog(SwaggerGenerator.class);
    /**
     * Registry path prefixes
     */
    static final String CONFIG_REG_PREFIX = "conf:";

    /**
     * Update the response with provided response string.
     *
     * @param response       CarbonHttpResponse which will be updated
     * @param responseString String response to be updated in response
     * @param contentType    Content type of the response to be updated in response headaers
     * @throws Exception Any exception occured during the update
     */
    protected void updateResponse(CarbonHttpResponse response, String responseString, String contentType) throws
            AxisFault {
        try {
            ((BlobOutputStream) response.getOutputStream()).getBlob().readFrom(new ByteArrayInputStream
                    (responseString.getBytes(SwaggerConstants.DEFAULT_ENCODING)), responseString.length());
        } catch (StreamCopyException streamCopyException) {
            handleException("Error in generating Swagger definition : failed to copy data to response ",
                    streamCopyException);
        } catch (UnsupportedEncodingException encodingException) {
            handleException("Error in generating Swagger definition : exception in encoding ", encodingException);
        }
        response.setStatus(SwaggerConstants.HTTP_OK);
        response.getHeaders().put(HTTP.CONTENT_TYPE, contentType);
    }

    /**
     * Returns API instance related to the URI in provided request.
     *
     * @param request CarbonHttpRequest which contains the request URI info
     * @return API instance with respect to the request
     */
    protected API getAPIFromSynapseConfig(CarbonHttpRequest request) {
        String apiName = getApiNameFromRequestUri(request.getRequestURI());
        return SynapseConfigUtils.getSynapseConfiguration(Constants.SUPER_TENANT_DOMAIN_NAME).getAPI(apiName);
    }

    /**
     * Extract API Name from the given URI.
     *
     * @param requestUri URI String of the request
     * @return API Name extracted from the URI provided
     */
    protected String getApiNameFromRequestUri(String requestUri) {
        return requestUri.substring(1);
    }

    /**
     * Function to extract swagger definition from the registry
     *
     * @param api API object
     * @param request CarbonHttpRequest which contains the request URI info
     * @return null if registry content unavailable or empty, otherwise relevant content
     */
    protected String retrieveFromRegistry(API api, CarbonHttpRequest request) {

        String defString = null;
        String resourcePath = api.getSwaggerResourcePath();

        if (resourcePath == null) {
            //Create resource path in registry
            StringBuilder resourcePathBuilder = new StringBuilder();
            resourcePathBuilder.append(CONFIG_REG_PREFIX)
                    .append(SwaggerConstants.DEFAULT_SWAGGER_REGISTRY_PATH).append(api.getAPIName());
            if (!(api.getVersionStrategy() instanceof DefaultStrategy)) {
                resourcePathBuilder.append(":v").append(api.getVersion());
            }
            resourcePathBuilder.append("/swagger.json");
            resourcePath = resourcePathBuilder.toString();

        }

        // Retrieve from registry
        SynapseConfiguration synapseConfig =
                SynapseConfigUtils.getSynapseConfiguration(Constants.SUPER_TENANT_DOMAIN_NAME);
        Registry registry = synapseConfig.getRegistry();
        OMNode regContent = registry.lookup(resourcePath);

        if (regContent instanceof OMText) {
            defString = ((OMText) regContent).getText();
        }

        return defString;
    }

    /**
     * Logs exceptions occured in formatters and throws.
     *
     * @param errorMsg  String message which contains error information
     * @param exception Actual Thowable instance with exception details
     * @throws AxisFault throws with error information
     */
    private void handleException(String errorMsg, Exception exception) throws AxisFault {
        log.error(errorMsg);
        throw new AxisFault(errorMsg, exception);
    }

    /**
     * Logs the error message and throws New AxisFault.
     *
     * @param errorMsg String message which contains error information
     * @throws AxisFault throws with error information
     */
    protected void handleException(String errorMsg) throws AxisFault {
        log.error(errorMsg);
        throw new AxisFault(errorMsg);
    }
}
