package com.oneops.boo;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.oneops.boo.BooYamlReader;
import com.oneops.boo.yaml.AssemblyBean;
import com.oneops.boo.yaml.BooBean;
import com.oneops.boo.yaml.EnvironmentBean;
import com.oneops.boo.yaml.PlatformBean;
import com.oneops.boo.yaml.ScaleBean;
import com.oneops.boo.yaml.Yaml;

public class BooYamlReaderTest {

  private String basedir;

  @Before
  public void beforeTests() {
    basedir = System.getProperty("basedir", new File("").getAbsolutePath());
  }
  
  @Test
  public void validateReadingAssembly(  ) throws Exception {
    BooYamlReader reader = new BooYamlReader();
    Yaml yaml = reader.read(new FileReader(yaml("assembly.yaml")));
    // Boo
    BooBean boo = yaml.getBoo();
    assertEquals("api_key", boo.getApikey());
    assertEquals("organization", boo.getOrg());
    assertEquals("email", boo.getEmail());
    assertEquals("environment_name", boo.getEnvName());
    assertEquals("json", boo.getIpOutput());    
    // Assembly
    AssemblyBean assembly = yaml.getAssembly();
    assertNotNull(assembly);
    assertEquals("assembly-name", assembly.getName());
    // Tags
    Map<String,String> tags = assembly.getTags();
    assertNotNull(tags);
    assertEquals("tag-value-0", tags.get("tag-0"));
    assertEquals("tag-value-1", tags.get("tag-1"));    
    // Variables
    Map<String,String> variables = yaml.getGlobalVariables();
    assertEquals("variable-value-0", variables.get("variable-0"));
    assertEquals("variable-value-1", variables.get("variable-1"));    
    // Platform 0
    List<PlatformBean> platforms = yaml.getPlatformsList();
    assertNotNull(platforms);
    PlatformBean p0 = platforms.get(0);
    assertEquals(1, p0.getDeployOrder());
    assertEquals("platform-0", p0.getName());
    assertEquals("source/pack-platform-0", p0.getPackId());
    assertEquals("1", p0.getPackVersion());
    // Links (not supported in Boo)
    // Variables
    Map<String,String> platformVariables = p0.getVariables();
    assertEquals("pack-platform-0-variable-0-value", platformVariables.get("pack-platform-variable-0"));
    assertEquals("pack-platform-0-variable-1-value", platformVariables.get("pack-platform-variable-1")); 
    // Components
    Map<String,Object> components = p0.getComponents();
    assertNotNull(components);
    // component -> component-0
    Map<String,String> component0 = p0.getComponentAsStringMap("component-0");
    assertEquals("platform-0-config-0-value", component0.get("config-0"));
    assertEquals("platform-0-config-1-value", component0.get("config-1"));
    // component -> compute
    Map<String,String> compute = p0.getComponentAsStringMap("compute");
    assertEquals("XXL", compute.get("size"));
    assertEquals(Boolean.TRUE, compute.get("require_public_ip"));  
    // component -> user
    Map<String,Object> users = p0.getComponent("user");
    Map<String,String> jvanzyl = (Map<String,String>)users.get("user-jvanzyl");    
    assertEquals(Boolean.TRUE, jvanzyl.get("system_account"));  
    assertEquals(Boolean.TRUE, jvanzyl.get("sudoer"));  
    assertEquals("jvanzyl", jvanzyl.get("username"));  
    // Scale 
    List<ScaleBean> scales = yaml.getScales();
    ScaleBean s0 = scales.get(0);
    assertEquals(s0.getPlatform(), "platform-0");
    assertEquals(s0.getCurrent(), 2);
    assertEquals(s0.getMin(), 2);
    assertEquals(s0.getMax(), 2);  
    // Environment
    List<EnvironmentBean> environmentList = yaml.getEnvironmentList();
    assertNotNull(environmentList);
    assertTrue(environmentList.size() > 0);
    assertNotNull(environmentList.get(0).getOthers());
    assertEquals("DEV", environmentList.get(0).getOthers().get("profile"));
    assertEquals("true", environmentList.get(0).getOthers().get("global_dns"));
    assertEquals("subdomain", environmentList.get(0).getOthers().get("subdomain"));
    assertEquals("redundant", environmentList.get(0).getOthers().get("availability"));
  }
  
  protected File yaml(String name) {
    return new File(basedir, String.format("src/test/yaml/boo/%s", name));
  }
}
