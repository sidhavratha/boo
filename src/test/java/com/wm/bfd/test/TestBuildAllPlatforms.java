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
import java.util.Map;

/**
*
* Packages always change in OneOps, this test mainly focus on if functions can run without exceptions so far.
*
*/
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestBuildAllPlatforms extends BfdOoTest {
  private static Logger LOG = LoggerFactory.getLogger(TestBuildAllPlatforms.class);
  BuildAllPlatforms build;

  @Before
  public void beforeMethod() throws Exception {
    assumeTrue(bfdOneOpsAvailable() && bfdDeveloper());
    if (build == null) {
      build = new BuildAllPlatforms(oo, config, null);
    }
  }

  private boolean bfdDeveloper() throws IOException {
    if (ONEOPS_CONFIG.exists()) {
      ClientConfigIniReader reader = new ClientConfigIniReader();
      Map<String, String> config = reader.read(ONEOPS_CONFIG, ONEOPS_DEFAULT_PROFILE);
      String organization = config.get("organization");
      if (organization != null && organization.equals("bfd")) {
        return true;
      }
    }
    return false;
  }

  private boolean bfdOneOpsAvailable() {
    try (Socket s = new Socket("web.bfd.dev.cloud.wal-mart.com", 443)) {
      return true;
    } catch (IOException ex) {
      // Ignore
    }
    return false;
  }

  @Test
  public void testACreateAssemblyIfNotExist() throws OneOpsClientAPIException {
    boolean isSuc = build.createAssemblyIfNotExist();
    assertEquals(isSuc, true);
  }

  @Test
  public void testGetAllAutoGenAssemblies() throws OneOpsClientAPIException {
    assertNotNull(config.getYaml().getAssembly().getName());
    build.getAllAutoGenAssemblies(config.getYaml().getAssembly().getName());
  }

  @Test
  public void testGetAssemblies() throws OneOpsClientAPIException {
    build.getAssemblies();
  }

  @Test
  public void testListActions() throws OneOpsClientAPIException {
    build.listActions("web", "apache");
  }

  @Test
  public void testListInstances() throws OneOpsClientAPIException {
    build.listInstances("web", "apache");
  }

  @Test
  public void testListInstancesMap() throws OneOpsClientAPIException {
    build.listInstancesMap("web", "apache");
  }

  @Test
  public void testExecuteAction() throws OneOpsClientAPIException {
    build.executeAction("web", "apache", "status", "", null, 100);
  }

  @Test
  public void testListAttachments() throws OneOpsClientAPIException {
  }

  @Test
  public void testAddAttachement() throws OneOpsClientAPIException {
    build.addAttachement("yarn", "hadoop-yarn-config", "testa2", null);
  }

  @Test
  public void testIsAttachementExist() throws OneOpsClientAPIException {
    build.isAttachmentExists("yarn", "hadoop-yarn-config", "test");
  }

  @Test
  public void testUpdateAttachement() throws OneOpsClientAPIException {
    build.updateAttachement("yarn", "hadoop-yarn-config", "testa2", null);
  }

  @Test
  public void testListEnvs() throws OneOpsClientAPIException {
    build.listEnvs();
  }

  @Test
  public void testListPlatforms() throws OneOpsClientAPIException {
    build.listPlatforms();
  }

  @Test
  public void testBCreatePlatform() throws OneOpsClientAPIException {
    boolean isSuc = build.createPlatforms(false);
    assertEquals(isSuc, true);
  }

  @Test
  public void testCCreateEnv() throws OneOpsClientAPIException {
    boolean isSuc = build.createEnv();
    assertEquals(isSuc, true);
  }

  @Test
  public void testDUpdateScaling() throws OneOpsClientAPIException {
    boolean isSuc = build.updateScaling();
    assertEquals(isSuc, true);
  }

  @Test
  public void testECleanup() throws OneOpsClientAPIException {
    boolean isSuc = build.cleanup();
    assertEquals(isSuc, true);
  }

  @Test
  public void testFGetStatus() throws OneOpsClientAPIException {
    String status = build.getStatus();
    LOG.info("The {} deploy {}", this.envName, status);
  }

  @Test
  public void testEGetCloud() throws OneOpsClientAPIException {
    String testCloud = "dev-cdc003";
    String id = build.getCloudId(testCloud);
    LOG.info("The {} id {}", testCloud, id);
  }

  @Test
  public void testUpdatePlatformCloudScale() throws OneOpsClientAPIException {
    boolean isSuc = build.updatePlatformCloudScale();
    assertEquals(isSuc, true);
  }

  @Test
  public void testUpdateUserComponents() throws OneOpsClientAPIException {
    boolean isSuc = build.updatePlatformComponents();
    assertEquals(isSuc, true);
  }

  @Test
  public void testGetIpsInternal() throws OneOpsClientAPIException {
    build.getIpsInternal("yarn", "compute");
  }
}
