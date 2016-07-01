package com.wm.bfd.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jayway.restassured.RestAssured;
import com.oo.api.OOInstance;
import com.wm.bfd.oo.ClientConfig;
import com.wm.bfd.oo.JaywayHttpModule;

import junit.framework.TestCase;

public abstract class BFDOOTest extends TestCase {
    Injector injector = Guice.createInjector(new JaywayHttpModule());
    ClientConfig config;
    OOInstance oo;
    String assemblyName;
    String platformName;
    String envName;

    public BFDOOTest() {
	this.init();
	RestAssured.useRelaxedHTTPSValidation(); // Disable SSL check.
    }

    void init() {
	if (oo == null) {
	    System.out.println("set up ...");
	    config = injector.getInstance(ClientConfig.class);
	    oo = injector.getInstance(OOInstance.class);
	    //assemblyName = "wes";
	    assemblyName = config.getConfig().getAssembly().getName();
	    platformName = config.getConfig().getPlatforms().getYarn().getName();
	    envName = "test";
	}
    }
}
