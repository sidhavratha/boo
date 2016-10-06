package com.wm.bfd.test;

import com.wm.bfd.oo.ClientConfig;
import com.wm.bfd.oo.JaywayHttpModule;
import com.wm.bfd.oo.exception.BfdOoException;
import com.wm.bfd.oo.utils.BfdUtils;
import com.wm.bfd.oo.yaml.Constants;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import com.jayway.restassured.RestAssured;
import com.oo.api.OOInstance;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * Parent class of all unit testing.
 */
public abstract class BfdOoTest extends TestCase {
  private static final Logger LOG = LoggerFactory.getLogger(BfdOoTest.class);
  private static final Injector injector = Guice.createInjector(new JaywayHttpModule(getConfig()));
  
  /** The config. */
  ClientConfig config;
  OOInstance oo;
  String assemblyName;
  String envName;
  JaywayHttpModule module;
  
  /** The bfd utils. */
  BfdUtils bfdUtils = new BfdUtils();

  /**
   * Instantiates a new bfd oo test.
   */
  public BfdOoTest() {
    try {
      this.init();
    } catch (BfdOoException e) {
      e.printStackTrace();
      LOG.error("BfdOoException: Fatal error {}, quit!", e.getMessage());
      System.exit(-1);
    } catch (ProvisionException e) {
      LOG.error("ProvisionException: Fatal error {}, quit!", e.getMessage());
      System.exit(-2);
    } catch (Exception e) {
      LOG.error("Exception: Fatal error {}, quit!", e.getMessage());
      System.exit(-3);
    }
    RestAssured.useRelaxedHTTPSValidation(); // Disable SSL check.

  }

  static String getConfig() {
    URL url = BfdOoTest.class.getResource(Constants.TEMPLATE);
    LOG.info("Using config file {}", url);
    if (url == null) {
      System.err.println("The test.yaml not found!");
      System.exit(-1);
    }
    String config = url.getFile();
    return config;
  }

  void init() throws BfdOoException, ProvisionException {
    if (oo == null) {
      oo = injector.getInstance(OOInstance.class);
      config = injector.getInstance(ClientConfig.class);

      assemblyName = config.getYaml().getAssembly().getName();
      envName = config.getYaml().getBoo().getEnvName();
    }
  }

  void initOld() throws BfdOoException, ProvisionException {
    if (oo == null) {
      config = injector.getInstance(ClientConfig.class);
      bfdUtils.verifyTemplate(config);
      oo = injector.getInstance(OOInstance.class);
      assemblyName = config.getYaml().getAssembly().getName();
      envName = config.getYaml().getBoo().getEnvName();
    }
  }
}
