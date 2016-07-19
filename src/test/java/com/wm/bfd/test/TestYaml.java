package com.wm.bfd.test;

import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oo.api.exception.OneOpsClientAPIException;
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
  public void testGetEnvironments() throws OneOpsClientAPIException {
    assertNotNull(config.getYaml().getEnvironment());
    this.printMap(config.getYaml().getEnvironment(), 0);
  }

  @Test
  public void testGetOthers() throws OneOpsClientAPIException {

    Yaml yaml = config.getYaml();
    assertNotNull(yaml.getOthers());
    this.printMap(yaml.getOthers(), 0);
  }

  @SuppressWarnings("unchecked")
  void printMap(Map<String, Object> map, int depth) {
    String log = "Parent";
    if (depth > 0)
      log = "Children";
    StringBuilder str = new StringBuilder();
    int loop = depth;
    while (loop > 0) {
      str.append('\t');
      loop--;
    }
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      LOG.debug("{} {}: key: {}; value:{}: \n", str.toString(), log, key, value == null ? ""
          : value.getClass());
      if (value instanceof Map) {
        this.printMap((Map<String, Object>) value, ++depth);
      }

    }
  }

}
