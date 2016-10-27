package com.wm.bfd.oo;

import com.wm.bfd.oo.yaml.Yaml;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.Singleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Singleton
public class ClientConfig {
  private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());
  private Yaml yaml;

  // For add user component in design
  public static final String SSH_KEY = "authorized_keys";
  public static final String USER_NAME = "username";
  public static final String USER = "user";

  // Platform yarn variables
  public static final String ZK_HOST = "zk_hosts";

  // Compute size
  public static final String SIZE = "size";

  /** The Constant COMPUTE. */
  public static final String COMPUTE = "compute";

  /**
   * Instantiates a new client config.
   *
   * @param file the file
   * @throws JsonParseException the json parse exception
   * @throws JsonMappingException the json mapping exception
   * @throws FileNotFoundException the file not found exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public ClientConfig(String file)
      throws JsonParseException, JsonMappingException, FileNotFoundException, IOException {
    FileInputStream input = null;
    try {
      input = new FileInputStream(new File(file));
      this.yaml = MAPPER.readValue(input, Yaml.class);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (input != null) {
        input.close();
      }
    }
  }

  public Yaml getYaml() {
    return yaml;
  }

}
