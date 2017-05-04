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

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneops.api.exception.OneOpsClientAPIException;
import com.oneops.boo.yaml.PlatformBean;
import com.oneops.boo.yaml.Yaml;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestYaml extends BooTest {
  private static final Logger LOG = LoggerFactory.getLogger(TestYaml.class);

  @Test
  public void testGetAssembly() throws OneOpsClientAPIException {
    assertNotNull(config.getYaml().getAssembly());
  }
  
  @Test
  public void testGetAssemblyTags() throws OneOpsClientAPIException {
    assertNotNull(config.getYaml().getAssembly());
    assertNotNull(config.getYaml().getAssembly().getTags());
  }

  @Test
  public void testGetBoo() throws OneOpsClientAPIException {
    assertNotNull(config.getYaml().getBoo().getApikey());
  }

  @Test
  public void testGetEnvironmentBean() throws OneOpsClientAPIException {
    assertNotNull(config.getYaml().getEnvironmentBean().getOthers());
  }

  @Test
  public void testGetPlatforms() throws OneOpsClientAPIException {
    Yaml yaml = config.getYaml();
    assertNotNull(yaml.getPlatforms());
  }

  @Test
  public void testGetMultiplelines() throws OneOpsClientAPIException {
    Yaml yaml = config.getYaml();
    List<PlatformBean> list = yaml.getPlatformsList();
    for (PlatformBean platform : list) {
      Map<String, Object> components = platform.getComponents();
      // util.printMap(components, 5);
      assertNotNull(components);
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testGetPlatformComponents() throws OneOpsClientAPIException {
    List<PlatformBean> platforms = this.config.getYaml().getPlatformsList();
    for (PlatformBean platform : platforms) {
      for (Map.Entry<String, Object> entry : platform.getComponents().entrySet()) {
        Object value = entry.getValue();
        if (value instanceof Map) {
          Map<String, String> map = (Map<String, String>) value;
          for (Map.Entry<String, String> detail : map.entrySet()) {
            assertNotNull(detail.getValue());
          }
        } else {
          LOG.info("Unknow type {}.", value.getClass());
        }
      }
    }
  }


  @Test
  public void testGetEnvironments() throws OneOpsClientAPIException {
    assertNotNull(config.getYaml().getEnvironment());
    // this.printMap(config.getYaml().getEnvironment(), 0);
  }

  @Test
  public void testGetOthers() throws OneOpsClientAPIException {

    Yaml yaml = config.getYaml();
    assertNotNull(yaml.getOthers());
    // this.printMap(yaml.getOthers(), 0);
  }

}
