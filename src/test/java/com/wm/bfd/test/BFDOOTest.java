package com.wm.bfd.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import com.jayway.restassured.RestAssured;
import com.oo.api.OOInstance;
import com.wm.bfd.oo.ClientConfig;
import com.wm.bfd.oo.JaywayHttpModule;
import com.wm.bfd.oo.Main;
import com.wm.bfd.oo.exception.BFDOOException;
import com.wm.bfd.oo.utils.BFDUtils;

import junit.framework.TestCase;

public abstract class BFDOOTest extends TestCase {
    Injector injector;
    ClientConfig config;
    OOInstance oo;
    String assemblyName;
    String platformName;
    String envName;
    JaywayHttpModule module;
    BFDUtils bfdUtils;

    public BFDOOTest() {

	this.module = new JaywayHttpModule(Main.TEMPLATE);
	injector = Guice.createInjector(this.module);
	try {
	    this.init();
	} catch (BFDOOException e) {
	    System.out.printf("Fatal error %s, quit!", e.getMessage());
	} catch (ProvisionException e) {
	    System.out.printf("Fatal error %s, quit!", e.getMessage());
	}
	RestAssured.useRelaxedHTTPSValidation(); // Disable SSL check.
	bfdUtils = new BFDUtils();
    }

    void init() throws BFDOOException, ProvisionException {
	if (oo == null) {
	    System.out.println("set up ...");
	    config = injector.getInstance(ClientConfig.class);
	    bfdUtils.verifyTemplate(config);
	    oo = injector.getInstance(OOInstance.class);
	    assemblyName = config.getConfig().getAssembly().getName();
	    platformName = config.getConfig().getPlatforms().getYarn()
		    .getName();
	    envName = config.getConfig().getBoo().getEnvName();
	}
    }
}
