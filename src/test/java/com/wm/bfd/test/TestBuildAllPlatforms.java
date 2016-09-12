package com.wm.bfd.test;

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
  public void testGetIpsInternal() throws OneOpsClientAPIException {
    System.out.println(build.getIpsInternal("yarn", "compute"));
  }
}
