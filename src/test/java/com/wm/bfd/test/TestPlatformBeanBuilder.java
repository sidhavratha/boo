package com.wm.bfd.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oo.api.exception.OneOpsClientAPIException;
import com.wm.bfd.oo.yaml.PlatformBean;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPlatformBeanBuilder extends BFDOOTest {
  final private static Logger LOG = LoggerFactory.getLogger(TestPlatformBeanBuilder.class);

  @Test
  public void testGetAssembly() throws OneOpsClientAPIException {
    PlatformBean platform =
        new PlatformBean.PlatformBeanBuilder("oneops/hadoop_yarn_vrc3", "1").build();
    assertEquals(platform.getPackSource(), "oneops");
    assertEquals(platform.getPackVersion(), "1");
    assertEquals(platform.getPack(), "hadoop_yarn_vrc3");
  }

}
