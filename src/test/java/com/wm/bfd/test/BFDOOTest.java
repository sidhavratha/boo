package com.wm.bfd.test;

import java.net.URL;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import com.jayway.restassured.RestAssured;
import com.oo.api.OOInstance;
import com.wm.bfd.oo.ClientConfig;
import com.wm.bfd.oo.JaywayHttpModule;
import com.wm.bfd.oo.exception.BFDOOException;
import com.wm.bfd.oo.utils.BFDUtils;
import com.wm.bfd.oo.yaml.Constants;

public abstract class BFDOOTest extends TestCase {
  final private static Logger LOG = LoggerFactory.getLogger(BFDOOTest.class);
  final private static Injector injector = Guice.createInjector(new JaywayHttpModule(getConfig()));
  ClientConfig config;
  OOInstance oo;
  String assemblyName;
  String envName;
  JaywayHttpModule module;
  BFDUtils bfdUtils = new BFDUtils();

  public BFDOOTest() {
    try {
      this.init();
    } catch (BFDOOException e) {
      e.printStackTrace();
      LOG.error("BFDOOException: Fatal error {}, quit!", e.getMessage());
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
    URL url = BFDOOTest.class.getResource(Constants.TEMPLATE);
    LOG.info("Using config file {}", url);
    if (url == null) {
      System.err.println("The test.yaml not found!");
      System.exit(-1);
    }
    String config = url.getFile();
    return config;
  }

  void init() throws BFDOOException, ProvisionException {
    if (oo == null) {
      oo = injector.getInstance(OOInstance.class);
      config = injector.getInstance(ClientConfig.class);

      assemblyName = config.getYaml().getAssembly().getName();
      envName = config.getYaml().getBoo().getEnvName();
    }
  }

  void initOld() throws BFDOOException, ProvisionException {
    if (oo == null) {
      config = injector.getInstance(ClientConfig.class);
      bfdUtils.verifyTemplate(config);
      oo = injector.getInstance(OOInstance.class);
      assemblyName = config.getYaml().getAssembly().getName();
      envName = config.getYaml().getBoo().getEnvName();
    }
  }
}
