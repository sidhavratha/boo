package com.wm.bfd.test;

import com.wm.bfd.oo.yaml.PlatformBean;

import com.oo.api.exception.OneOpsClientAPIException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPlatformBeanBuilder extends BfdOoTest {
  private static final Logger LOG = LoggerFactory.getLogger(TestPlatformBeanBuilder.class);

  @Test
  public void testGetAssembly() throws OneOpsClientAPIException {
    PlatformBean platform =
        new PlatformBean.PlatformBeanBuilder("oneops/hadoop_yarn_vrc3", "1").build();
    assertEquals(platform.getPackSource(), "oneops");
    assertEquals(platform.getPackVersion(), "1");
    assertEquals(platform.getPack(), "hadoop_yarn_vrc3");
  }

}
