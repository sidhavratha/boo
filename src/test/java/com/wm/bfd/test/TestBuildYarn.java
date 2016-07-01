package com.wm.bfd.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.oo.api.exception.OneOpsClientAPIException;
import com.wm.bfd.oo.ClientConfig;
import com.wm.bfd.oo.workflow.BuildYarn;
import com.wm.bfd.oo.yaml.ScalBean;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestBuildYarn extends BFDOOTest {
    BuildYarn yarn;

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	if (yarn == null)
	    yarn = new BuildYarn(oo, assemblyName, platformName, envName,
		    config,
		    config.getConfig().getPlatforms().getYarn().getVariables().get(ClientConfig.ZK_HOST));
    }

    @Test
    public void testCreatePlatform() throws OneOpsClientAPIException {
	boolean isSuc = yarn.createPlatform();
	assertEquals(isSuc, true);
    }

    @Test
    public void testUpdatePlatformVariables() throws OneOpsClientAPIException {
	boolean isSuc = yarn.updatePlatformVariables();
	assertEquals(isSuc, true);
    }

    @Test
    public void testUpdateScaling() throws OneOpsClientAPIException {
	ScalBean scale = config.getConfig().getPlatforms().getYarn().getScale();
	yarn.updateScaling(scale.getCurrent(), scale.getMin(), scale.getMax());
    }
}
