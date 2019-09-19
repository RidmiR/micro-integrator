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
package util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.wso2.carbon.esb.cli.CliAPITestCase;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    private static final Log log = LogFactory.getLog(TestUtils.class);
    static File miBuildFilePath;
    static String miPath;


//     get pom version from the pom file
    public static String getPomVerion() throws IOException, XmlPullParserException {

        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader("../pom.xml"));
        String pomVersion = model.getParent().getVersion();
        return pomVersion;
    }

//    get the mi build path to run mi commands
    public static  String getMIBuildPath() {
        try {
            TestUtils testUtils = new TestUtils();
            miBuildFilePath = new File(".." + File.separator + ".." + File.separator + ".." + File.separator
                    + "cmd" + File.separator + "build" + File.separator + "wso2mi-cli-" + testUtils.getPomVerion()
                    + File.separator + "bin" + File.separator + "mi");
            miPath = miBuildFilePath.getCanonicalPath();

        } catch (IOException e) {
            log.info("Exception = " + e.getMessage());
        } catch (XmlPullParserException e) {
            log.info("Exception = " + e.getMessage());
        }
        return miPath;
    }

   public static List<String> runCLICommand(String artifact , String command ){

        List<String> lines = new ArrayList();
        String  line;

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(TestUtils.runMiCommand(
                TestUtils.getMIBuildPath(),artifact ,command ).getInputStream()))) {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            log.info("Exception occurred while running the command :  " + command + " for artifact : " + artifact  + " . Exception : "  + e.getMessage());
        }
        return lines;
    }

    public static List<String> runCLICommandWithArtifactName(String artifact , String command, String name ){

        List<String> lines = new ArrayList();
        String  line;

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(TestUtils.runMiCommandWithArtifact(
                TestUtils.getMIBuildPath(),artifact ,command ,name ).getInputStream()))) {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            log.info("Exception occurred while running the command :  " + command + " for artifact : " + artifact  + " . Exception : "  + e.getMessage());
        }
        return lines;
    }

    /**
     * run the mi commands
     * ex: mi sequence show
     */
    public static Process runMiCommand(String path, String artifact, String command) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(path, artifact, command);
        Process process = builder.start();
        return process;
    }

    /**
     * run the mi commands with the artifact name
     * ex: mi sequence show sampleSequence
     */
    public static  Process runMiCommandWithArtifact(String path, String artifact, String command, String name) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(path, artifact, command, name);
        Process process = builder.start();
        return process;
    }


}






