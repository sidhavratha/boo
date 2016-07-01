package com.wm.bfd.oo.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.path.json.JsonPath;
import com.oo.api.OOInstance;
import com.oo.api.exception.OneOpsClientAPIException;
import com.wm.bfd.oo.ClientConfig;
import com.wm.bfd.oo.bean.Compute;

public class BuildZookeeper extends AbstractWorkflow {

    final static String PLATFORM_NAME = "zookeeper";

    public BuildZookeeper(OOInstance instance, String assemblyName,
	    String platformName, String envName, ClientConfig config)
	    throws OneOpsClientAPIException {
	super(instance, assemblyName, platformName, envName, config);
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
	    design.createPlatform(platformName, config.getConfig()
		    .getPlatforms().getZookeeper().getPack(),
		    config.getConfig().getPlatforms().getZookeeper()
			    .getPackVersion(), config.getConfig()
			    .getPlatforms().getZookeeper().getPackSource(), "",
		    "");
	    design.commitDesign();
	}

	return true;
    }

    

    public String getDeploymentId() {
	String id = null;
	try {
	    JsonPath latestDeployment = transition.getLatestDeployment(envName);
	    id = latestDeployment.getString("deploymentId");
	} catch (OneOpsClientAPIException e) {
	    // Ignore
	}
	// transition.getDeploymentStatus(envName, deploymentId);
	return id;
    }

    public String getStatus() {
	String status = null;
	try {
	    JsonPath response = transition.getDeploymentStatus(envName,
		    this.getDeploymentId());
	    status = response.getString("deploymentState");
	} catch (OneOpsClientAPIException e) {
	    // Ignore
	}
	return status;
    }

    public String getIpsJson() {
	String result = null;
	try {
	    List<Map<String, String>> ips = this.getIpsInternal();
	    Map<String, String> all = new HashMap<String, String>();

	    StringBuilder str = new StringBuilder();
	    for (Map<String, String> ip : ips) {
		str.append(",");
		str.append(ip.get("public_ip"));

	    }

	    if (str.length() > 0)
		str.deleteCharAt(0);
	    all.put("zookeeper", str.toString());
	    Compute compute = new Compute(all);

	    result = new ObjectMapper().writeValueAsString(compute);
	} catch (JsonProcessingException e) {
	    // Ignore
	} catch (OneOpsClientAPIException e) {
	    // Ignore
	}
	return result;
    }

    List<Map<String, String>> getIpsInternal() throws OneOpsClientAPIException {
	JsonPath response = op.listInstances(platformName, "compute");
	return response.getList("ciAttributes");
    }

    /**
     * Format IPs as ip1:2181,ip2:2181
     * 
     * @return
     */
    public String getIpsForYarn() {
	String result = null;
	try {
	    List<Map<String, String>> ips = this.getIpsInternal();

	    StringBuilder str = new StringBuilder();
	    for (Map<String, String> ip : ips) {
		str.append(",");
		str.append(ip.get("public_ip"));
		str.append(":");
		str.append("2181");

	    }
	    if (str.length() > 0)
		str.deleteCharAt(0);
	    result = str.toString();
	} catch (OneOpsClientAPIException e) {
	    // Ignore
	}
	return result;
    }

    @Override
    public boolean process() throws OneOpsClientAPIException {
	this.createAssemblyIfNotExist();
	this.bar.update(5, 100);
	this.createPlatform();
	this.bar.update(10, 100);
	this.createEnv();
	this.bar.update(15, 100);
	this.deploy();
	this.bar.update(18, 100);
	return true;
    }

    @Override
    public boolean cleanup() {
	return super.cleanup();
    }

}
