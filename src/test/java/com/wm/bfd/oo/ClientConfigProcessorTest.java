package com.wm.bfd.oo;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;

import com.wm.bfd.oo.yaml.Yaml;

public class ClientConfigProcessorTest {

  private String basedir;

  @Before
  public void beforeTests() {
    basedir = System.getProperty("basedir", new File("").getAbsolutePath());
  }

  @Test
  public void validateBooYamlProcessing() throws Exception {
    ClientConfigReader reader = new ClientConfigReader();
    ClientConfigInterpolator interpolator = new ClientConfigInterpolator();
    Yaml yaml = reader.read(interpolator.interpolate(resource("boo.yaml"), resource("config"), ClientConfig.ONEOPS_DEFAULT_PROFILE));
    doAssert(yaml);
  }

  @Test
  public void validateBooYamlProcessingInputStream() throws Exception {
    ClientConfigReader reader = new ClientConfigReader();
    ClientConfigInterpolator interpolator = new ClientConfigInterpolator();
    Yaml yaml = reader.read(interpolator.interpolate(new FileInputStream(resource("boo.yaml")), resource("config"), ClientConfig.ONEOPS_DEFAULT_PROFILE));
    doAssert(yaml);
  }
  
  private void doAssert(Yaml yaml) {
    // We current append a trailing '/' to the URI
    assertEquals("https://web.bfd.dev.cloud.wal-mart.com/", yaml.getBoo().getHost());
    assertEquals("bfd", yaml.getBoo().getOrg());
    assertEquals("BOO!!", yaml.getBoo().getApikey());
    assertEquals("boo@walmart.com", yaml.getBoo().getEmail());
  }

  protected File resource(String name) {
    return new File(basedir, String.format("src/test/yaml/%s", name));
  }
}
