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

import static com.oneops.client.OneOpsClientUtils.developerConfigurationPresent;
import static com.oneops.client.OneOpsClientUtils.oneOpsAvailable;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.oneops.api.exception.OneOpsClientAPIException;
import com.oneops.boo.workflow.BuildAllPlatforms;

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
    assumeTrue(oneOpsAvailable(oo.getEndpoint(), 443) && developerConfigurationPresent());
    build = new BuildAllPlatforms(oo, config, null);
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
    assertNotNull(build.process(false, false));
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
    assertTrue(build.executeAction("tomcat", "compute", "status", "", null, 100) > 0);

    // Attachments
    System.out.println("Attachments");
    assertTrue(build.addAttachment("tomcat", "tomcat", "test", null));
    assertTrue(build.listAttachments("tomcat", "tomcat").size() > 0);
    assertTrue(build.isAttachmentExists("tomcat", "tomcat", "test"));
    Map<String, String> attr = new HashMap<String, String>();
    attr.put("content", "echo 'TEST PASSED'");
	assertTrue(build.updateAttachment("tomcat", "tomcat", "test", attr));

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
    for (int i = 0; i < 10; i++) {
      BuildAllPlatforms cleanBuild = new BuildAllPlatforms(oo, config, null);
      try {
        System.out.println("attempt # " + i);
        cleanBuild.cleanup();
      } catch (Exception ex) {
        if (ex.getMessage() != null && ex.getMessage().matches(".*assembly with name.*404 Not Found")) {
          break; //Assembly already deleted
        } else if (ex.getMessage() != null && ex.getMessage().matches(".*assembly with name .* HTTP/1.1 422 Unprocessable Entity")) {
            //ignore the error
          } else 
        ex.printStackTrace();
      }
      while (cleanBuild.getStatus() != null && cleanBuild.getStatus().equalsIgnoreCase("active")) {
        System.out.println("Env deployment still in progress");
        TimeUnit.SECONDS.sleep(10);
      }
      System.out.println("deployment status: " + cleanBuild.getStatus());
      TimeUnit.SECONDS.sleep(10);
    }
    System.out.println("Assembly removal finished.");
  }
}
