package com.wm.bfd.test;

import static com.wm.bfd.oo.ClientConfig.ONEOPS_CONFIG;
import static com.wm.bfd.oo.ClientConfig.ONEOPS_DEFAULT_PROFILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

import com.wm.bfd.oo.ClientConfigIniReader;
import com.wm.bfd.oo.workflow.BuildAllPlatforms;

import com.oo.api.exception.OneOpsClientAPIException;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * Packages always change in OneOps, this test mainly focus on if functions can run without exceptions so far.
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestBuildAllPlatforms extends BfdOoTest {
  private static Logger LOG = LoggerFactory.getLogger(TestBuildAllPlatforms.class);
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
      s.close();
      return true;
    }
    catch (IOException ex) {
      System.out.format("Unable to reach %s. Skipping tests%n", oo.getEndpoint());
      return false;
    }
    catch (java.net.URISyntaxException ex) {
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

    cleanUp();

    System.out.println("Deploy");
    boolean isSuc = build.process(false,false);
    assertEquals(true,isSuc);
    while (build.getStatus().equalsIgnoreCase("active"))
    {
      TimeUnit.SECONDS.sleep(10);
    }

    System.out.println("Deploy Done");

    build.listPlatforms();
    build.listEnvs();

    System.out.println("Actions");
    List<String> actions = build.listActions("tomcat", "compute");
    assertEquals(true, actions.size() > 2);

    String status = build.executeAction("tomcat", "compute",
            "status", "", null, 100);
    assertEquals(true, status.length() > 5);

    System.out.println("Attachments");

    assertEquals(true,isSuc);
    isSuc = build.addAttachment("tomcat", "tomcat",
            "testa2", null);
    assertEquals(true,isSuc);

    build.listAttachments("tomcat", "tomcat");

    build.isAttachmentExists("tomcat", "tomcat",
            "test");

    build.updateAttachment("tomcat", "tomcat",
            "testa2", null);

    System.out.println("Instances");
    build.listInstances("tomcat", "compute");

    build.listInstancesMap("tomcat", "compute");

    System.out.println("Platform cloud scale update");
    isSuc = build.updatePlatformCloudScale();
    assertEquals(true,isSuc);

    System.out.println("Platform component update");
    isSuc = build.updatePlatformComponents();
    assertEquals(true,isSuc);

    System.out.println("Get Ips");

    build.getIpsInternal("tomcat", "compute");

    cleanUp();

    assertEquals(true, isSuc);
    System.out.println("Done Clean up");
  }

  private void cleanUp() throws OneOpsClientAPIException, InterruptedException {
    System.out.println("Cleanup Started");
    BuildAllPlatforms cleanBuild = new BuildAllPlatforms(oo, config, null);
    for (int i = 0; i < 10; i++) {
      try {
        cleanBuild.cleanup();
      }
      catch (Exception ex) {
        // ignore
      }

      while (cleanBuild.getStatus() != null &&
              cleanBuild.getStatus().equalsIgnoreCase("active"))
      {
        TimeUnit.SECONDS.sleep(10);
      }
      TimeUnit.SECONDS.sleep(10);
    }
    System.out.println("Cleanup Finished");
  }
}
