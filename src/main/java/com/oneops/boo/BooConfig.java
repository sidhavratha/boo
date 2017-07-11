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

import com.google.inject.Singleton;
import com.oneops.boo.yaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

@Singleton
public class BooConfig {

  private Yaml yaml;

  // For add user component in design
  public static final String SSH_KEY = "authorized_keys";
  public static final String USER_NAME = "username";
  public static final String USER = "user";

  // Compute size
  public static final String SIZE = "size";

  /** The Constant COMPUTE. */
  public static final String COMPUTE = "compute";

  /**
   * Instantiates a new client config.
   *
   * @param booYamlFile the file
   * @param profile the profile
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public BooConfig(File booYamlFile, String profile) throws IOException {
    BooYamlReader reader = new BooYamlReader();
    BooConfigInterpolator interpolator = new BooConfigInterpolator();
    try (InputStream is = new FileInputStream(booYamlFile)) {
      this.yaml = reader.read(interpolator.interpolate(is, profile));
    }
  }

  /**
   *
   * @param booYamlFile the file
   * @param config the boo template config file which contains variables
   * @throws IOException
   */
  public BooConfig(File booYamlFile, Map<String, String> config) throws IOException {
    BooYamlReader reader = new BooYamlReader();
    BooConfigInterpolator interpolator = new BooConfigInterpolator();
    this.yaml = reader.read(interpolator.interpolate(new String(Files.readAllBytes(booYamlFile.toPath())), config));
  }

  /**
   * Create a ClientConfig by parsing an InputStream.
   * 
   * @param input the inputstream
   * @param profile the profile
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public BooConfig(InputStream input, String profile) throws IOException {
    BooYamlReader reader = new BooYamlReader();
    BooConfigInterpolator interpolator = new BooConfigInterpolator();
    this.yaml = reader.read(interpolator.interpolate(input, profile));
  }

  public Yaml getYaml() {
    return yaml;
  }
}
