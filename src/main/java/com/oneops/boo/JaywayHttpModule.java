/*
 * Copyright 2017 Walmart, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.oneops.boo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.oneops.api.OOInstance;

/**
 * The Class JaywayHttpModule.
 */
public class JaywayHttpModule extends AbstractModule {

  /** The log. */
  private static Logger LOG = LoggerFactory.getLogger(JaywayHttpModule.class);

  /** The client. */
  private static BooConfig CLIENT = null;

  /** The yaml. */
  private File yaml;

  /** The boo template subsitution variables values **/
  private Map<String, String> variables;

  /** ~/.boo/config profile */
  private String profile;

  /**
   * Instantiates a new jayway http module.
   *
   * @param yaml the yaml
   */
  public JaywayHttpModule(File yaml, String profile) {
    this.yaml = yaml;
    this.profile = profile;
    if (LOG.isDebugEnabled()) {
      LOG.debug("Using config {}", yaml);
    }

  }

  /**
   *
   * @param yaml
   * @param booTemplateVariables
   */
  public JaywayHttpModule(File yaml, Map<String, String> booTemplateVariables) {
    this.yaml = yaml;
    this.variables = booTemplateVariables;
    if (LOG.isDebugEnabled()) {
      LOG.debug("Using config {}", yaml);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.inject.AbstractModule#configure()
   */
  @Override
  protected void configure() {
    // bind(ClientConfig.class);
    // bind(OOInstance.class);
  }

  /**
   * Gets the client config.
   *
   * @return the client config
   * @throws JsonParseException the json parse exception
   * @throws JsonMappingException the json mapping exception
   * @throws FileNotFoundException the file not found exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Provides
  @Singleton
  BooConfig getClientConfig()
      throws JsonParseException, JsonMappingException, FileNotFoundException, IOException {
    // test
    if (CLIENT == null) {
      if (variables != null) {
        CLIENT = new BooConfig(this.yaml, variables);
      } else {
        CLIENT = new BooConfig(this.yaml, this.profile);
      }
    }

    return CLIENT;
  }

  /**
   * Gets the OO instance.
   *
   * @return the OO instance
   * @throws JsonParseException the json parse exception
   * @throws JsonMappingException the json mapping exception
   * @throws FileNotFoundException the file not found exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Provides
  OOInstance getOoInstance()
      throws JsonParseException, JsonMappingException, FileNotFoundException, IOException {
    OOInstance instance = new OOInstance();
    BooConfig client = this.getClientConfig();
    instance.setAuthtoken(client.getYaml().getBoo().getApikey());
    instance.setOrgname(client.getYaml().getBoo().getOrg());
    instance.setEndpoint(client.getYaml().getBoo().getHost());
    instance.setGzipEnabled(client.getYaml().getBoo().isGzipEnabled());
    return instance;
  }

}
