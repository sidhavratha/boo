package com.wm.bfd.oo;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.oo.api.OOInstance;

public class JaywayHttpModule extends AbstractModule {
  private static Logger LOG = LoggerFactory.getLogger(JaywayHttpModule.class);
  private static ClientConfig CLIENT = null;
  private String yaml;

  public JaywayHttpModule(String yaml) {
    this.yaml = yaml;
    if (LOG.isDebugEnabled())
      LOG.debug("Using config {}", yaml);
  }

  @Override
  protected void configure() {
    // bind(ClientConfig.class);
    // bind(OOInstance.class);
  }

  @Provides
  @Singleton
  ClientConfig getClientConfig() throws JsonParseException, JsonMappingException,
      FileNotFoundException, IOException {
    // test
    if (CLIENT == null)
      CLIENT = new ClientConfig(this.yaml);
    return CLIENT;
  }

  @Provides
  OOInstance getOOInstance() throws JsonParseException, JsonMappingException,
      FileNotFoundException, IOException {
    OOInstance instance = new OOInstance();
    ClientConfig client = this.getClientConfig();
    instance.setAuthtoken(client.getYaml().getBoo().getApikey());
    instance.setOrgname(client.getYaml().getBoo().getOrg());
    instance.setEndpoint(client.getYaml().getBoo().getHost());
    return instance;
  }

}
