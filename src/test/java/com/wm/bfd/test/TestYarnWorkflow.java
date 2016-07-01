package com.wm.bfd.test;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.oo.api.exception.OneOpsClientAPIException;
import com.wm.bfd.oo.JaywayHttpModule;
import com.wm.bfd.oo.Main;
import com.wm.bfd.oo.workflow.YarnWorkflow;

public class TestYarnWorkflow extends BFDOOTest {

    YarnWorkflow flow;
    Injector injector = Guice.createInjector(new JaywayHttpModule(Main.TEMPLATE));

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	if (flow == null)
	    flow = new YarnWorkflow(oo, assemblyName, platformName, envName,
		    config);
    }

    @Test
    public void testCreateYarn() throws OneOpsClientAPIException {
	boolean isSuc = flow.process();
	assertEquals(isSuc, true);
    }
    
    @Test
    public void testCleanup() throws OneOpsClientAPIException {
	boolean isSuc = flow.cleanup();
	assertEquals(isSuc, true);
    }

}
