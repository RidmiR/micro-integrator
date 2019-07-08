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

package org.wso2.esb.integration.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Micro-Integrator registry manager for integration tests
 */
public class MicroRegistryManager {

    private HashMap<String, byte[]> backupResources;
    private static final String CONF_PATH = "conf:";
    private static final String GOV_PATH = "gov:";

    private static final Log log = LogFactory.getLog(MicroRegistryManager.class);

    public MicroRegistryManager() {
        backupResources = new LinkedHashMap<>();
    }

    /**
     * Function to add resource to the Micro Registry
     *
     * @param resourcePath resource path in WSO2 registry path style
     * @param sourcePath source file
     * @throws MicroRegistryManagerException if error occurred while updating resource
     */
    public void addResource(String resourcePath, String sourcePath) throws MicroRegistryManagerException {
        updateResource(resourcePath, sourcePath, false);
    }

    /**
     * Function to update existing resource from the Micro Registry
     *
     * @param resourcePath resource path in WSO2 registry path style
     * @param sourcePath source file
     * @param backupOriginal bool to indicate whether to backup the original resource if exists
     * @throws MicroRegistryManagerException if error occurred while updating resource
     */
    public void updateResource(String resourcePath, String sourcePath, boolean backupOriginal) throws MicroRegistryManagerException {

        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(System.getProperty("carbon.home")).append(File.separator).append("registry").append(File.separator);

        if (resourcePath.startsWith(CONF_PATH)) {
            String relativePath = resourcePath.substring(5).replace('/', File.separatorChar);
            pathBuilder.append("config").append(relativePath);
        } else if (resourcePath.startsWith(GOV_PATH)) {
            String relativePath = resourcePath.substring(4).replace('/', File.separatorChar);
            pathBuilder.append("governance").append(relativePath);
        } else {
            throw new MicroRegistryManagerException("Unknown resource path: " + resourcePath);
        }

        String targetPath = pathBuilder.toString();
        String message = " source:" + sourcePath + " to: " + targetPath;

        if (backupOriginal) {
            //backup original file if exists
            try {
                backupFile(targetPath);
            } catch (IOException e) {
                throw new MicroRegistryManagerException("Error occurred while taking backup of file: " + targetPath, e);
            }
        }

        try {
            Files.copy(Paths.get(sourcePath), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
            log.info("Successfully copied" + message);
        } catch (IOException e) {
            throw new MicroRegistryManagerException("Error occurred while copying" + message, e);
        }
    }

    /**
     * Function is to check if the registry resources are exist in filepath
     *
     * @param resourcePath path to resource file path from registry resource
     * @param filename registry filename
     */

    public boolean checkResourceExist(String resourcePath, String filename) throws Exception {

        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(System.getProperty("carbon.home")).append(File.separator).append("registry").append(File.separator);

        if (resourcePath.startsWith(CONF_PATH)) {
            String relativePath = resourcePath.substring(5).replace('/', File.separatorChar);
            pathBuilder.append("config").append(relativePath);
        } else if (resourcePath.startsWith(GOV_PATH)) {
            String relativePath = resourcePath.substring(4).replace('/', File.separatorChar);
            pathBuilder.append("governance").append(relativePath);
        } else {
            throw new MicroRegistryManagerException("Exception occurred while checking the registry resource file: " + resourcePath);
        }

        return new File(pathBuilder.toString() + filename).isFile();
    }

    /**
     * Function to restore backed up resources by this utility
     *
     * @throws MicroRegistryManagerException
     */
    public void restoreOriginalResources() throws MicroRegistryManagerException {
        for (Map.Entry<String, byte[]> backupEntry : backupResources.entrySet()) {
            restoreFile(backupEntry.getKey(), backupEntry.getValue());
            backupResources.remove(backupEntry.getKey());
        }
    }

    private void backupFile(String pathToFile) throws IOException {

        File sourceFile = new File(pathToFile);
        if (sourceFile.exists() && sourceFile.isFile()) {
            backupResources.put(pathToFile, Files.readAllBytes(sourceFile.toPath()));
        }
    }

    private void restoreFile(String path, byte[] data) throws MicroRegistryManagerException {

        try {
            Files.write(Paths.get(path), data, StandardOpenOption.WRITE);
            log.info("Successfully restored file: " + path);
        } catch (IOException e) {
            throw new MicroRegistryManagerException("Error occurred while restoring file : " + path, e);
        }
    }


    /**
     * Custom exception for exceptions related for MicroRegistryManager
     */
    public class MicroRegistryManagerException extends Exception {

        public MicroRegistryManagerException() {

        }

        public MicroRegistryManagerException(String message) {

            super(message);
        }

        public MicroRegistryManagerException(String message, Throwable cause) {

            super(message, cause);
        }

        public MicroRegistryManagerException(Throwable cause) {

            super(cause);
        }

    }
 }
