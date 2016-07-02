package com.wm.bfd.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    final private static Logger LOG = LoggerFactory.getLogger(BFDOOTest.class);
    final private static Injector injector = Guice
	    .createInjector(new JaywayHttpModule(Main.TEMPLATE));
    ClientConfig config;
    OOInstance oo;
    String assemblyName;
    String platformName;
    String envName;
    JaywayHttpModule module;
    BFDUtils bfdUtils = new BFDUtils();

    public BFDOOTest() {
	try {
	    this.init();
	} catch (BFDOOException e) {
	    LOG.error("BFDOOException: Fatal error {}, quit!",
		    e.getMessage());
	    System.exit(-1);
	} catch (ProvisionException e) {
	    LOG.error("ProvisionException: Fatal error {}, quit!",
		    e.getMessage());
	    System.exit(-2);
	} catch (Exception e) {
	    LOG.error("Exception: Fatal error {}, quit!",
		    e.getMessage());
	    System.exit(-3);
	}
	RestAssured.useRelaxedHTTPSValidation(); // Disable SSL check.

    }

    void init() throws BFDOOException, ProvisionException {
	if (oo == null) {
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
