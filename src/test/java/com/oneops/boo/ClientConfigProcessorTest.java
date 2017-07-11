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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;

import com.oneops.boo.yaml.Yaml;
import com.oneops.client.OneOpsConfigReader;

public class ClientConfigProcessorTest {

  private String basedir;

  @Before
  public void beforeTests() {
    basedir = System.getProperty("basedir", new File("").getAbsolutePath());
  }

  @Test
  public void validateBooYamlProcessing() throws Exception {
    BooYamlReader reader = new BooYamlReader();
    BooConfigInterpolator interpolator = new BooConfigInterpolator();
    Yaml yaml = reader.read(interpolator.interpolate(new FileInputStream(resource("boo.yaml")), resource("config"),
        OneOpsConfigReader.ONEOPS_DEFAULT_PROFILE));
    doAssert(yaml);
  }

  @Test
  public void validateBooYamlProcessingInputStream() throws Exception {
    BooYamlReader reader = new BooYamlReader();
    BooConfigInterpolator interpolator = new BooConfigInterpolator();
    Yaml yaml = reader.read(interpolator.interpolate(new FileInputStream(resource("boo.yaml")),
        resource("config"), OneOpsConfigReader.ONEOPS_DEFAULT_PROFILE));
    doAssert(yaml);
  }

  private void doAssert(Yaml yaml) {
    // We current append a trailing '/' to the URI
    assertEquals("https://localhost:9090/", yaml.getBoo().getHost());
    assertEquals("bfd", yaml.getBoo().getOrg());
    assertEquals("BOO!!", yaml.getBoo().getApikey());
    assertEquals("boo@walmart.com", yaml.getBoo().getEmail());
  }

  protected File resource(String name) {
    return new File(basedir, String.format("src/test/yaml/%s", name));
  }
}
