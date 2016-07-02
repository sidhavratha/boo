package com.wm.bfd.test;

import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oo.api.exception.OneOpsClientAPIException;
import com.wm.bfd.oo.yaml.Users;
import com.wm.bfd.oo.yaml.YamlBean;
import com.wm.bfd.oo.yaml.ZookeeperBean;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestYaml extends BFDOOTest {
    final private static Logger LOG = LoggerFactory.getLogger(TestYaml.class);
    @Test
    public void testConfig() throws OneOpsClientAPIException {
	YamlBean yaml = config.getConfig();
	assertNotNull(yaml.getBoo().getApikey());
	assertNotNull(yaml.getBoo().getHost());
	assertNotNull(yaml.getBoo().getCloudId());
	assertNotNull(yaml.getBoo().getOrg());
    }

    @Test
    public void testGetSshKey() throws OneOpsClientAPIException {
	YamlBean yaml = config.getConfig();
	List<Users> users = yaml.getPlatforms().getYarn().getComponents()
		.getUsers();
	for (Users user : users) {
	    Map<String, Map<String, String>> map = user.getUsers();
	    for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {

		Map<String, String> keys = entry.getValue();
		for (Map.Entry<String, String> ssh : keys.entrySet()) {
		    LOG.debug("{}: key : {} and value: {} \n",
			    entry.getKey(), ssh.getKey(), ssh.getValue());
		    assertNotNull(ssh.getValue());
		}
	    }
	}

    }
    
    @Test
    public void testGetAllvariables() throws OneOpsClientAPIException {
	YamlBean yaml = config.getConfig();
	assertNotNull(yaml.getPlatforms().getYarn().getVariables());
	assertNotNull(yaml.getPlatforms().getYarn().getSecureVariables());
	assertNotNull(yaml.getPlatforms().getYarn().getComponents().getComputeSize());
    }

    @Test
    public void testGetZookeeper() throws OneOpsClientAPIException {
	YamlBean yaml = config.getConfig();
	ZookeeperBean zoo = yaml.getPlatforms().getZookeeper();
	assertNotNull(zoo.getPack());
	assertNotNull(zoo.getPackVersion());
	assertNotNull(zoo.getPackSource());
    }
}
