package com.wm.bfd.oo.workflow;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

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

/**
 * Install yarn pack in one run. Include all dependencies.
 * 
 * @author rzhan33
 *
 */
public class YarnWorkflow extends AbstractWorkflow {

    final private static int WAIT_TIME = 10; // seconds
    String zkHost;

    public YarnWorkflow(OOInstance instance, String assemblyName,
	    String platformName, String envName, ClientConfig config)
	    throws OneOpsClientAPIException {
	super(instance, assemblyName, platformName, envName, config);
	zkHost = config.getConfig().getPlatforms().getYarn().getVariables()
		.get(ClientConfig.ZK_HOST);
    }

    String waitForActiveDeployment(OOInstance instance, String assembly,
	    String env, String deploymentId) throws OneOpsClientAPIException {
	Transition transition = new Transition(instance, assembly);
	JsonPath deploymentStatus = transition.getDeploymentStatus(env,
		deploymentId);
	String deploymentState = deploymentStatus.getString("deploymentState");
	this.bar.update(20, 100);
	while ("active".equals(deploymentState)) {
	    Uninterruptibles.sleepUninterruptibly(WAIT_TIME, TimeUnit.SECONDS);
	    deploymentStatus = transition
		    .getDeploymentStatus(env, deploymentId);
	    deploymentState = deploymentStatus.getString("deploymentState");
	    System.out.println(deploymentState);
	    this.bar.update(20, 100);
	}
	return deploymentState;

    }

    public boolean process() throws OneOpsClientAPIException {
	BuildZookeeper zookeeper = new BuildZookeeper(instance, assemblyName,
		platformName, envName, config);
	this.bar.update(0, 100);
	boolean isBuildYarn = false;
	if (StringUtils.isEmpty(zkHost)) {
	    System.out.println("Start to build zookeeper cluster...");
	    // Build zookeeper
	    boolean status = zookeeper.process();
	    if (status) {
		String state = this.waitForActiveDeployment(instance,
			assemblyName, envName, zookeeper.getDeploymentId());
		if (state.equalsIgnoreCase("success")) {
		    // Build yarn
		    isBuildYarn = true;
		}
	    }
	} else {
	    System.out.println("Using zookeeper " + zookeeper);
	    isBuildYarn = true;
	}
	this.bar.update(30, 100);

	if (isBuildYarn) {
	    BuildYarn yarn = new BuildYarn(instance, assemblyName,
		    platformName, envName, config, zookeeper.getIpsForYarn());
	    System.out.println("Start to build the yarn cluster, zk_host = "
		    + zookeeper.getIpsForYarn());
	    yarn.process();
	}

	return true;
    }
}
