package com.wm.bfd.oo;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;

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
    assertEquals("f0", interpolator.interpolate(String.format("{{file(%s)}}", f0.getAbsolutePath()), new HashMap<String, String>()));    
  }
  
  protected File resource(String name) {
    return new File(basedir, String.format("src/test/files/%s", name));
  }
}
