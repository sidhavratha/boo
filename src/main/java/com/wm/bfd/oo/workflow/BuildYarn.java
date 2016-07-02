package com.wm.bfd.oo.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.path.json.JsonPath;
import com.oo.api.OOInstance;
import com.oo.api.exception.OneOpsClientAPIException;
import com.oo.api.resource.model.RedundancyConfig;
import com.wm.bfd.oo.ClientConfig;
import com.wm.bfd.oo.bean.Compute;
import com.wm.bfd.oo.yaml.ScalBean;
import com.wm.bfd.oo.yaml.Users;
import com.wm.bfd.oo.yaml.YarnBean;

public class BuildYarn extends AbstractWorkflow {
    final private static Logger LOG = LoggerFactory.getLogger(BuildYarn.class);
    final private static String COMPUTE = "compute";
    final private static String CLIENT_COMPUTE = "client-compute";
    final private static String PRM_COMPUTE = "prm-compute";
    String zookeepers;
    YarnBean yarn;

    public BuildYarn(OOInstance instance, String assemblyName,
	    String platformName, String envName, ClientConfig config,
	    String zookeepers) throws OneOpsClientAPIException {
	super(instance, assemblyName, platformName, envName, config);
	this.zookeepers = zookeepers;
	this.yarn = config.getConfig().getPlatforms().getYarn();
    }

    public boolean isPlatformExist() {
	JsonPath response = null;
	try {
	    response = design.getPlatform(platformName);
	} catch (OneOpsClientAPIException e) {
	    String msg = String.format("The platform %s is not exist! %s",
		    platformName, e.getMessage());
	    System.err.println(msg);
	}
	return response == null ? false : true;
    }

    public boolean createPlatform() throws OneOpsClientAPIException {
	boolean isExist = this.isPlatformExist();
	if (!isExist) {
	    JsonPath response = design.createPlatform(platformName,
		    yarn.getPack(), yarn.getPackVersion(),
		    yarn.getPackSource(), "", "");
	    if (response != null)
		design.commitDesign();
	} else {
	    LOG.warn("Platform exist, skip create platform "
		    + platformName);
	}
	return true;

    }

    public boolean updatePlatformVariables() throws OneOpsClientAPIException {

	Map<String, String> variables = config.getConfig().getPlatforms()
		.getYarn().getSecureVariables();

	for (Map.Entry<String, String> entry : variables.entrySet()) {
	    variables.put(entry.getKey(), entry.getValue());
	}

	design.updatePlatformVariable(platformName, variables, true);

	variables.clear();
	variables = config.getConfig().getPlatforms().getYarn().getVariables();
	for (Map.Entry<String, String> entry : variables.entrySet()) {
	    variables.put(entry.getKey(), entry.getValue());
	}

	design.updatePlatformVariable(platformName, variables, false);

	Map<String, String> attributes = new HashMap<String, String>();
	attributes.put(ClientConfig.SIZE, config.getConfig().getPlatforms()
		.getYarn().getComponents().getComputeSize());
	design.updatePlatformComponent(platformName, ClientConfig.COMPUTE,
		attributes);

	attributes.clear();

	for (Users user : config.getConfig().getPlatforms().getYarn()
		.getComponents().getUsers()) {
	    Map<String, Map<String, String>> map = user.getUsers();
	    for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
		attributes.clear();
		Map<String, String> keys = entry.getValue();
		attributes.put(ClientConfig.USER_NAME, entry.getKey());
		// Json in a array here.
		attributes.put(ClientConfig.SSH_KEY,
			"[\"" + keys.get(ClientConfig.SSH_KEY) + "\"]");

		this.addPlatformComponent(platformName, ClientConfig.USER,
			entry.getKey(), attributes);

	    }

	}

	design.commitDesign();
	return true;
    }

    private void addPlatformComponent(String platformName,
	    String componentName, String uniqueName,
	    Map<String, String> attributes) {
	try {
	    design.addPlatformComponent(platformName, componentName,
		    uniqueName, attributes);
	} catch (OneOpsClientAPIException e) {
	    System.err.printf("User %s exists. Skip this step.", uniqueName);
	}
    }

    public boolean updateScaling(int current, int min, int max)
	    throws OneOpsClientAPIException {
	RedundancyConfig config = new RedundancyConfig();
	config.setCurrent(current);
	config.setMin(min);
	config.setMax(max);
	transition
		.updatePlatformRedundancyConfig(envName, platformName, config);
	return true;
    }

    @Override
    public boolean process() throws OneOpsClientAPIException {
	this.createAssemblyIfNotExist();
	this.bar.update(35, 100);
	boolean status = this.createPlatform();
	this.bar.update(40, 100);

	this.createEnv();
	this.bar.update(45, 100);
	if (status) {
	    status = this.updatePlatformVariables();
	    this.bar.update(50, 100);
	    ScalBean scale = config.getConfig().getPlatforms().getYarn()
		    .getScale();
	    status = this.updateScaling(scale.getCurrent(), scale.getMin(),
		    scale.getMax());
	}
	this.deploy();
	this.bar.update(60, 100);
	return status;
    }

    public String getIpsJson() {
	String result = null;
	try {
	    Map<String, String> all = new HashMap<String, String>();
	    all.put(CLIENT_COMPUTE, this.getJsonInternal(CLIENT_COMPUTE));
	    all.put(COMPUTE, this.getJsonInternal(COMPUTE));
	    all.put(PRM_COMPUTE, this.getJsonInternal(PRM_COMPUTE));
	    Compute compute = new Compute(all);

	    result = new ObjectMapper().writeValueAsString(compute);
	} catch (JsonProcessingException e) {
	    e.printStackTrace();
	    // Ignore
	} catch (OneOpsClientAPIException e) {
	    e.printStackTrace();
	    // Ignore
	}
	return result;
    }

    private String getJsonInternal(String componentName)
	    throws OneOpsClientAPIException {
	List<Map<String, String>> ips = this.getIpsInternal(componentName);
	StringBuilder str = new StringBuilder();
	LOG.debug("Get ips for component {} in {}", componentName, ips);
	for (Map<String, String> ip : ips) {
	    str.append(",");
	    // Cannot get public_ip sometimes, so temporary using private_ip
	    // instead.
	    str.append(ip.get("private_ip"));
	}

	if (str.length() > 0)
	    str.deleteCharAt(0);
	return str.toString();
    }

    private List<Map<String, String>> getIpsInternal(String componentName)
	    throws OneOpsClientAPIException {
	JsonPath response = op.listInstances(config.getConfig().getPlatforms().getYarn()
		.getName(), componentName);
	LOG.debug("Trying to get ip from {}", platformName);
	return response.getList("ciAttributes");
    }
}
