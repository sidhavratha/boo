package com.wm.bfd.oo.workflow;

import java.util.HashMap;
import java.util.Map;

import com.jayway.restassured.path.json.JsonPath;
import com.oo.api.OOInstance;
import com.oo.api.exception.OneOpsClientAPIException;
import com.oo.api.resource.model.RedundancyConfig;
import com.wm.bfd.oo.ClientConfig;
import com.wm.bfd.oo.yaml.ScalBean;
import com.wm.bfd.oo.yaml.Users;
import com.wm.bfd.oo.yaml.YarnBean;

public class BuildYarn extends AbstractWorkflow {

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
	    System.out.println("Platform exist, skip create platform "
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
		System.out.println(entry.getKey() + "::"
			+ keys.get(ClientConfig.SSH_KEY));
		// JsonPath response = design.getPlatformComponent(platformName,
		// ClientConfig.USER);
		// System.out.println( "getPlatformComponent>>" +
		// response.prettyPrint());
		design.addPlatformComponent(platformName, ClientConfig.USER,
			entry.getKey(), attributes);

	    }

	}

	design.commitDesign();
	return true;
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
	boolean status = this.createPlatform();
	this.bar.update(40, 100);
	if (status) {
	    status = this.updatePlatformVariables();
	    this.bar.update(50, 100);
	    ScalBean scale = config.getConfig().getPlatforms().getYarn()
		    .getScale();
	    status = this.updateScaling(scale.getCurrent(), scale.getMin(),
		    scale.getMax());

	    this.bar.update(60, 100);
	}
	return status;
    }
}
