package com.wm.bfd.oo;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Maps;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class ClientConfigInterpolatorTest {

  private String basedir;

  @Before
  public void beforeTests() {
    basedir = System.getProperty("basedir", new File("").getAbsolutePath());
  }

  @Test
  public void validateInliningFiles() throws Exception {
    ClientConfigInterpolator interpolator = new ClientConfigInterpolator();
    File f0 = resource("f0.txt");
    assertEquals("f0", interpolator.interpolate(String.format("{{file(%s)}}", f0.getAbsolutePath()), Maps.newHashMap()));    
  }
  
  protected File resource(String name) {
    return new File(basedir, String.format("src/test/files/%s", name));
  }
}
