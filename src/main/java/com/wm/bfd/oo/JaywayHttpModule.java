package com.wm.bfd.oo;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.oo.api.OOInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * The Class JaywayHttpModule.
 */
public class JaywayHttpModule extends AbstractModule {

  /** The log. */
  private static Logger LOG = LoggerFactory.getLogger(JaywayHttpModule.class);

  /** The client. */
  private static ClientConfig CLIENT = null;

  /** The yaml. */
  private String yaml;

  /**
   * Instantiates a new jayway http module.
   *
   * @param yaml the yaml
   */
  public JaywayHttpModule(String yaml) {
    this.yaml = yaml;
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
  ClientConfig getClientConfig()
      throws JsonParseException, JsonMappingException, FileNotFoundException, IOException {
    // test
    if (CLIENT == null) {
      CLIENT = new ClientConfig(this.yaml);
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
    ClientConfig client = this.getClientConfig();
    instance.setAuthtoken(client.getYaml().getBoo().getApikey());
    instance.setOrgname(client.getYaml().getBoo().getOrg());
    instance.setEndpoint(client.getYaml().getBoo().getHost());
    return instance;
  }

}
