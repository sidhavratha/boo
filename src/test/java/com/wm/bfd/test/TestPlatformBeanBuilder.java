package com.wm.bfd.test;

import static org.junit.Assert.assertEquals;

import com.wm.bfd.oo.yaml.PlatformBean;

import com.oo.api.exception.OneOpsClientAPIException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPlatformBeanBuilder extends BfdOoTest {

  @Test
  public void testGetAssembly() throws OneOpsClientAPIException {
    PlatformBean platform = new PlatformBean.PlatformBeanBuilder("oneops/hadoop-yarn-v1", "1").build();
    assertEquals(platform.getPackSource(), "oneops");
    assertEquals(platform.getPackVersion(), "1");
    assertEquals(platform.getPack(), "hadoop-yarn-v1");
  }

}
