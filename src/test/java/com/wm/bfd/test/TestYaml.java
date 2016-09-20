package com.wm.bfd.test;

import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oo.api.exception.OneOpsClientAPIException;
import com.wm.bfd.oo.yaml.PlatformBean;
import com.wm.bfd.oo.yaml.Yaml;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestYaml extends BFDOOTest {
  final private static Logger LOG = LoggerFactory.getLogger(TestYaml.class);

  @Test
  public void testGetAssembly() throws OneOpsClientAPIException {
    assertNotNull(config.getYaml().getAssembly());
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
  public void testGetPlatformComponents() throws OneOpsClientAPIException {
    List<PlatformBean> platforms = this.config.getYaml().getPlatformsList();
    for (PlatformBean platform : platforms) {
      for (Map.Entry<String, Object> entry : platform.getComponents().entrySet()) {
        Object value = entry.getValue();
        if (value instanceof Map) {
          Map<String, String> map = (Map<String, String>) value;
          for (Map.Entry<String, String> detail : map.entrySet()) {
            // System.out.printf("Key: %s; value: %s \n", detail.getKey(), detail.getValue());
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
