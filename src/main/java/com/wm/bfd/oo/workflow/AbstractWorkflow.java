package com.wm.bfd.oo.workflow;

import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.Uninterruptibles;
import com.jayway.restassured.path.json.JsonPath;
import com.oo.api.OOInstance;
import com.oo.api.exception.OneOpsClientAPIException;
import com.oo.api.resource.Assembly;
import com.oo.api.resource.Design;
import com.oo.api.resource.Operation;
import com.oo.api.resource.Transition;
import com.wm.bfd.oo.ClientConfig;
import com.wm.bfd.oo.utils.ProgressBar;

public abstract class AbstractWorkflow {
    String assemblyName;
    String platformName;
    String envName;

    Assembly assembly;
    Design design;
    Transition transition;
    Operation op;

    ClientConfig config;

    OOInstance instance;

    ProgressBar bar;

    public AbstractWorkflow(OOInstance instance, String assemblyName,
	    String platformName, String envName, ClientConfig config)
	    throws OneOpsClientAPIException {
	this.assemblyName = assemblyName;
	this.platformName = platformName;
	this.envName = envName;
	this.instance = instance;

	assembly = new Assembly(instance);
	design = new Design(instance, assemblyName);
	transition = new Transition(instance, assemblyName);
	op = new Operation(instance, assemblyName, envName);
	this.config = config;

	this.bar = new ProgressBar();
    }

    public boolean process() throws OneOpsClientAPIException {
	return false;
    }

    public boolean cleanup() {
	if (design == null)
	    return true;
	System.out.println("Clean up...");
	this.cancelDeployment();
	this.disableAllPlatforms();

	try {
	    transition.deleteEnvironment(envName);
	} catch (Exception e) {
	    // Ignore
	    e.printStackTrace();
	}
	this.deleteDesign();
	// Don't add the following part to one try block as transition.
	try {

	    assembly.deleteAssembly(assemblyName);
	} catch (Exception e) {
	    // Ignore
	}
	op = null;
	design = null;
	assembly = null;
	return true;
    }

    void cancelDeployment() {
	try {
	    JsonPath response = transition.getLatestDeployment(envName);
	    String deploymentId = response.getString("deploymentId");
	    response = transition.getLatestRelease(envName);
	    String releaseId = response.getString("releaseId");
	    System.out.println("deploymentId:" + deploymentId + "; releaseId: "
		    + releaseId);
	    response = transition.getDeploymentStatus(envName, deploymentId);
	    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
	    response = transition.cancelDeployment(envName, deploymentId,
		    releaseId);

	    System.out.println("cancelDeployment: "
		    + (response == null ? "" : response.prettyPrint()));
	} catch (Exception e) {
	    // Ignore
	}

    }

    void disableAllPlatforms() {
	try {
	    transition.disableAllPlatforms(envName);
	    transition.commitEnvironment(envName, null, "Clean up " + envName);
	    transition.deploy(envName, "test deploy!");
	} catch (Exception e) {
	    // Ignore
	}
    }

    void deleteDesign() {
	JsonPath response = null;
	try {
	    System.out.println("deleteEnvironment log:"
		    + (response == null ? "" : response.prettyPrint()));
	    response = design.commitDesign();
	    System.out.println("commitDesign log:"
		    + (response == null ? "" : response.prettyPrint()));
	    design.deletePlatform(platformName);
	} catch (Exception e) {
	    // Ignore
	    e.printStackTrace();
	}
    }

    void cleanUp() {
	if (design == null)
	    return;
	System.out.println("Clean up...");
	this.cancelDeployment();
	this.disableAllPlatforms();

	try {
	    transition.deleteEnvironment(envName);
	} catch (Exception e) {
	    // Ignore
	    e.printStackTrace();
	}
	this.deleteDesign();
	// Don't add the following part to one try block as transition.
	try {

	    assembly.deleteAssembly(assemblyName);
	} catch (Exception e) {
	    // Ignore
	}
	op = null;
	design = null;
	assembly = null;
    }
}
