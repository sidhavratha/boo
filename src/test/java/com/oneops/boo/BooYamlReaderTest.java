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
import com.oneops.boo.yaml.CloudBean;
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
    // Links 
    PlatformBean p1 = platforms.get(1);
    assertNotNull(p1.getLinks());
    
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
    
    // component- dependsOn
    Map<String,Object> klohia = (Map<String,Object>)users.get("user-klohia"); 
    assertNotNull(klohia.get("dependson"));  
    
    // Environment
    List<EnvironmentBean> environmentList = yaml.getEnvironmentList();
    assertNotNull(environmentList);
    assertTrue(environmentList.size() > 0);
    environmentList.forEach(env -> {
      assertNotNull(env.getEnvName());
      assertNotNull(env.getOthers());
      
      //env clouds
      List<CloudBean> clouds = env.getClouds();
      assertNotNull(clouds);
      clouds.forEach(cl -> {
    	  assertEquals("dev-cloud0", cl.getCloudName());
    	  assertEquals("1", cl.getPriority());
    	  assertEquals("1", cl.getDpmtOrder());
    	  assertEquals("100", cl.getPctScale());
      });
      
      //relays //TODO not implemented in current boo
      
      //global variables //TODO not implemented in current boo
      
      //env platforms
      List<PlatformBean> plist = env.getPlatformsList();
      plist.forEach(pb -> {
    	  //env platform variables
    	  assertNotNull(pb.getVariables());
    	  assertEquals("pack-platform-0-variable-1-value-new", pb.getVariables().get("pack-platform-variable-1"));
    	  
    	  assertNotNull(pb.getComponents());
    	  Map<String,String> comp0 = pb.getComponentAsStringMap("component-0");
    	  assertEquals("config-0-new-value", comp0.get("config-0"));
    	  
    	  //env platform auto healing
    	  assertNotNull(pb.getAutoHealing());
    	  assertTrue(pb.getAutoHealing().containsKey("autorepair"));
    	  assertTrue(pb.getAutoHealing().containsKey("autoreplace"));
    	  assertTrue(pb.getAutoHealing().containsKey("autoscale"));
    	  assertTrue(pb.getAutoHealing().containsKey("replace_after_minutes"));
    	  assertTrue(pb.getAutoHealing().containsKey("replace_after_repairs"));
    	  
    	  //env platform scaling
    	  if("redundant".equals(env.getOthers().get("availability"))) {
    		  ScaleBean scale = pb.getScale();
    		  assertEquals(1, scale.getCurrent());
    		  assertEquals(1, scale.getMin());
    		  assertEquals(3, scale.getMax());
    		  assertEquals("component-0", scale.getComponent());
    	  }
      });
    });
    
  }
  
  protected File yaml(String name) {
    return new File(basedir, String.format("src/test/yaml/boo/%s", name));
  }
}
