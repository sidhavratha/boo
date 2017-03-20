/*
 * Copyright 2017 Walmart, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oneops.boo;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import com.jayway.restassured.RestAssured;
import com.oneops.boo.ClientConfig;
import com.oneops.boo.JaywayHttpModule;
import com.oneops.boo.exception.BooException;
import com.oneops.boo.utils.BooUtils;
import com.oneops.boo.yaml.Constants;
import com.oneops.client.api.OOInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

/**
 * Parent class of all unit testing.
 */
public abstract class BooTest {
  private static final Logger LOG = LoggerFactory.getLogger(BooTest.class);
  private static final Injector injector = Guice.createInjector(new JaywayHttpModule(getConfig(), ClientConfig.ONEOPS_DEFAULT_PROFILE));

  /** The config. */
  ClientConfig config;
  OOInstance oo;
  String assemblyName;
  String envName;
  JaywayHttpModule module;

  /** The boo utils. */
  BooUtils booUtils = new BooUtils();

  /**
   * Instantiates a new boo test.
   */
  public BooTest() {
    try {
      this.init();
    } catch (BooException e) {
      e.printStackTrace();
      LOG.error("BooException: Fatal error {}, quit!", e.getMessage());
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

  static File getConfig() {
    URL url = BooTest.class.getResource(Constants.TEMPLATE);
    LOG.info("Using config file {}", url);
    if (url == null) {
      System.err.println("The test.yaml not found!");
      System.exit(-1);
    }
    return new File(url.getFile());
  }

  void init() throws BooException, ProvisionException {
    if (oo == null) {
      oo = injector.getInstance(OOInstance.class);
      config = injector.getInstance(ClientConfig.class);

      assemblyName = config.getYaml().getAssembly().getName();
      envName = config.getYaml().getBoo().getEnvName();
    }
  }

  void initOld() throws BooException, ProvisionException {
    if (oo == null) {
      config = injector.getInstance(ClientConfig.class);
      booUtils.verifyTemplate(config);
      oo = injector.getInstance(OOInstance.class);
      assemblyName = config.getYaml().getAssembly().getName();
      envName = config.getYaml().getBoo().getEnvName();
    }
  }
}
