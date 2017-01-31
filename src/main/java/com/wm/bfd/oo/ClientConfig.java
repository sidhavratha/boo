package com.wm.bfd.oo;

import com.wm.bfd.oo.yaml.Yaml;

import com.google.inject.Singleton;

import java.io.File;
import java.io.IOException;

@Singleton
public class ClientConfig {
  public static final File ONEOPS_CONFIG = new File(new File(System.getProperty("user.home"), ".oneops"), "config");
  public static final String ONEOPS_DEFAULT_PROFILE = "default";
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
   * @param booYamlFile the file
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public ClientConfig(File booYamlFile) throws IOException {
    ClientConfigReader reader = new ClientConfigReader();
    ClientConfigInterpolator interpolator = new ClientConfigInterpolator();
    this.yaml = reader.read(interpolator.interpolate(booYamlFile, ONEOPS_CONFIG, ONEOPS_DEFAULT_PROFILE));
  }

  public Yaml getYaml() {
    return yaml;
  }
}
