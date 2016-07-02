package com.wm.bfd.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.oo.api.exception.OneOpsClientAPIException;
import com.wm.bfd.oo.workflow.BuildZookeeper;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestBuildZookeeper extends BFDOOTest {
    BuildZookeeper zoo;

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	if (zoo == null)
	    zoo = new BuildZookeeper(oo, assemblyName, platformName, envName,
		    config);
    }

    @Test
    public void testAIsAssemblyExist() throws OneOpsClientAPIException {
	boolean isExist = zoo.isAssemblyExist();
	assertNotNull(isExist); // Ignore false or true
    }

    @Test
    public void testBCreateAssemblyIfNotExist() throws OneOpsClientAPIException {
	boolean isSuc = zoo.createAssemblyIfNotExist();
	assertEquals(isSuc, true);
    }

    @Test
    public void testCIsPlatformExist() throws OneOpsClientAPIException {
	boolean isExist = zoo.isPlatformExist();
	assertNotNull(isExist);
    }

    @Test
    public void testDCreatePlatform() throws OneOpsClientAPIException {
	boolean isSuc = zoo.createPlatform();
	assertEquals(isSuc, true);
    }

    @Test
    public void testEIsEnvExist() throws OneOpsClientAPIException {
	boolean isExist = zoo.isEnvExist();
	assertNotNull(isExist);
    }

    @Test
    public void testFCreateEnv() throws OneOpsClientAPIException {
	boolean isSuc = zoo.createEnv();
	assertEquals(isSuc, true);
    }

    @Test
    public void testGetDeploymentId() throws OneOpsClientAPIException {
	String id = zoo.getDeploymentId();
	assertNotNull(id);
    }

    @Test
    public void testGetStatus() throws OneOpsClientAPIException {
	String status = zoo.getStatus();
	assertNotNull(status);
    }

    @Test
    public void testGetIpsJson() throws OneOpsClientAPIException {
	String ips = zoo.getIpsJson();
	assertNotNull(ips);
    }

    @Test
    public void testGetIpsForYarn() throws OneOpsClientAPIException {
	String ips = zoo.getIpsForYarn();
	assertNotNull(ips);
    }

}
