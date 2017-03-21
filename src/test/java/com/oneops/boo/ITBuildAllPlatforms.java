/*
 * Copyright 2017 Walmart, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.oneops.boo;

import static com.oneops.boo.ClientConfig.ONEOPS_CONFIG;
import static com.oneops.boo.ClientConfig.ONEOPS_DEFAULT_PROFILE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.oneops.boo.ClientConfigIniReader;
import com.oneops.client.api.exception.OneOpsClientAPIException;
import com.oneops.boo.workflow.BuildAllPlatforms;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Packages always change in OneOps, this test mainly focus on if functions can run without
 * exceptions so far.
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ITBuildAllPlatforms extends BooTest {
  private BuildAllPlatforms build;

  @Before
  public void beforeMethod() throws Exception {
    assumeTrue(oneOpsAvailable() && developerEnvironment());
    build = new BuildAllPlatforms(oo, config, null);
  }

  private boolean developerEnvironment() throws IOException {
    if (ONEOPS_CONFIG.exists()) {
      ClientConfigIniReader reader = new ClientConfigIniReader();
      Map<String, String> config = reader.read(ONEOPS_CONFIG, ONEOPS_DEFAULT_PROFILE);
      String organization = config.get("organization");
      if (organization != null) {
        return true;
      }
    }
    return false;
  }

  private boolean oneOpsAvailable() {
    try (Socket s = new Socket(new java.net.URI(oo.getEndpoint()).getHost(), 443)) {
      return true;
    } catch (IOException ex) {
      System.out.format("Unable to reach %s. Skipping tests%n", oo.getEndpoint());
      return false;
    } catch (java.net.URISyntaxException ex) {
      System.out.format("%s is not a valid URI. Skipping tests%n", oo.getEndpoint());
      return false;
    }
  }

  @Test
  public void testGetAllAutoGenAssemblies() throws OneOpsClientAPIException {
    assertNotNull(config.getYaml().getAssembly().getName());
    build.getAllAutoGenAssemblies(config.getYaml().getAssembly().getName());
  }

  @Test
  public void testOneOpsLifeCycle() throws OneOpsClientAPIException, InterruptedException {

    // Make sure our test assembly is not present
    removeAssembly();

    System.out.println("Deploy");
    assertTrue(build.process(false, false));
    while (build.getStatus().equalsIgnoreCase("active")) {
      TimeUnit.SECONDS.sleep(10);
    }
    System.out.println("Deploy Done");

    // Platforms
    assertTrue(build.listPlatforms().size() > 0);

    // Environments
    assertTrue(build.listEnvs().size() > 0);

    // Actions
    System.out.println("Actions");
    assertTrue(build.listActions("tomcat", "compute").size() > 2);
    assertTrue(build.executeAction("tomcat", "compute", "status", "", null, 100).length() > 5);

    // Attachments
    System.out.println("Attachments");
    assertTrue(build.addAttachment("tomcat", "tomcat", "test", null));
    assertTrue(build.listAttachments("tomcat", "tomcat").size() > 0);
    assertTrue(build.isAttachmentExists("tomcat", "tomcat", "test"));
    assertTrue(build.updateAttachment("tomcat", "tomcat", "test", null));

    // Instances
    System.out.println("Instances");
    assertTrue(build.listInstances("tomcat", "compute").size() > 0);
    assertTrue(build.listInstancesMap("tomcat", "compute").size() > 0);

    System.out.println("Platform cloud scale update");
    assertTrue(build.updatePlatformCloudScale());

    System.out.println("Platform component update");
    assertTrue(build.updatePlatformComponents());

    System.out.println("Platform variable update");
    assertTrue(build.updatePlatformVariables(true));

    System.out.println("Get Ips");
    assertTrue(build.getIpsInternal("tomcat", "compute").size() > 0);

    removeAssembly();

    System.out.println("Done Clean up");
  }

  // This logic should be in Boo itself it reliably remove all traces of an assembly in
  // one pass from the users perspective
  private void removeAssembly() throws OneOpsClientAPIException, InterruptedException {
    System.out.println("Assembly removal started...");
    BuildAllPlatforms cleanBuild = new BuildAllPlatforms(oo, config, null);
    for (int i = 0; i < 10; i++) {
      try {
        cleanBuild.cleanup();
      } catch (Exception ex) {
        // ignore
      }
      while (cleanBuild.getStatus() != null && cleanBuild.getStatus().equalsIgnoreCase("active")) {
        TimeUnit.SECONDS.sleep(10);
      }
      TimeUnit.SECONDS.sleep(10);
    }
    System.out.println("Assembly removal finished.");
  }
}
