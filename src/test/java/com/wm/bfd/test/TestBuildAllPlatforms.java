package com.wm.bfd.test;

import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oo.api.exception.OneOpsClientAPIException;
import com.wm.bfd.oo.workflow.BuildAllPlatforms;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestBuildAllPlatforms extends BFDOOTest {
  private static Logger LOG = LoggerFactory.getLogger(TestBuildAllPlatforms.class);
  BuildAllPlatforms build;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    if (build == null)
      build = new BuildAllPlatforms(oo, config);
  }

  @Test
  public void testACreateAssemblyIfNotExist() throws OneOpsClientAPIException {
    boolean isSuc = build.createAssemblyIfNotExist();
    assertEquals(isSuc, true);
  }

  @Test
  public void testGetAllAutoGenAssemblies() throws OneOpsClientAPIException {
    assertNotNull(config.getYaml().getAssembly().getName());
    List<String> assemblies =
        build.getAllAutoGenAssemblies(config.getYaml().getAssembly().getName());
  }

  @Test
  public void testGetAssemblies() throws OneOpsClientAPIException {
    build.getAssemblies();
  }

  @Test
  public void testListActions() throws OneOpsClientAPIException {
    List<String> actions = build.listActions("web", "apache");
  }

  @Test
  public void testListInstances() throws OneOpsClientAPIException {
    List<String> actions = build.listInstances("web", "apache");
  }

  @Test
  public void testListInstancesMap() throws OneOpsClientAPIException {
    Map<String, Integer> actions = build.listInstancesMap("web", "apache");
  }

  @Test
  public void testExecuteAction() throws OneOpsClientAPIException {
    build.executeAction("web", "apache", "status", "", null, 100);
  }

  @Test
  public void testListAttachements() throws OneOpsClientAPIException {
    List<String> attachements = build.listAttachements("yarn", "hadoop-yarn-config");
  }

  @Test
  public void testAddAttachement() throws OneOpsClientAPIException {
    boolean isSuc = build.addAttachement("yarn", "hadoop-yarn-config", "testa2", null);
  }

  @Test
  public void testIsAttachementExist() throws OneOpsClientAPIException {
    boolean isSuc = build.isAttachmentExists("yarn", "hadoop-yarn-config", "test");
  }

  @Test
  public void testUpdateAttachement() throws OneOpsClientAPIException {
    // Map<String, String> attributes = new HashMap<String, String>();
    // attributes.put("path", "/opt/datameer/etc/das-env.sh");
    // attributes.put("priority", "2");
    // attributes.put("exec_cmd", "chmod +x /opt/datameer/etc/das-env.sh");
    // attributes.put("run_on", "{ \"after-add\":true }");
    // attributes.put("content", "content");
    boolean isSuc = build.updateAttachement("yarn", "hadoop-yarn-config", "testa2", null);
  }

  @Test
  public void testListEnvs() throws OneOpsClientAPIException {
    List<String> envs = build.listEnvs();
    // System.out.println(envs);
  }

  @Test
  public void testListPlatformss() throws OneOpsClientAPIException {
    List<String> platforms = build.listPlatforms();
    // System.out.println(platforms);
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
  public void testGetIpsInternal() throws OneOpsClientAPIException {
    System.out.println(build.getIpsInternal("yarn", "compute"));
  }
}
