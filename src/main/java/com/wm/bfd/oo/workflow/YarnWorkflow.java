package com.wm.bfd.oo.workflow;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.Uninterruptibles;
import com.jayway.restassured.path.json.JsonPath;
import com.oo.api.OOInstance;
import com.oo.api.exception.OneOpsClientAPIException;
import com.oo.api.resource.Transition;
import com.wm.bfd.oo.ClientConfig;
import com.wm.bfd.oo.Main;

/**
 * Install yarn pack in one run. Include all dependencies.
 * 
 * @author rzhan33
 *
 */
public class YarnWorkflow extends AbstractWorkflow {
    private static Logger LOG = LoggerFactory.getLogger(AbstractWorkflow.class);
    final private static int WAIT_TIME = 10; // seconds
    String zkHost;
    BuildZookeeper zookeeper;
    BuildYarn yarn;

    public YarnWorkflow(OOInstance instance, String assemblyName,
	    String platformName, String envName, ClientConfig config)
	    throws OneOpsClientAPIException {
	super(instance, assemblyName, platformName, envName, config);
	zkHost = config.getConfig().getPlatforms().getYarn().getVariables()
		.get(ClientConfig.ZK_HOST);
	zookeeper = new BuildZookeeper(instance, assemblyName, platformName,
		envName, config);

	yarn = new BuildYarn(instance, assemblyName, platformName, envName,
		config, zkHost);
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
	    LOG.debug(deploymentState);
	    this.bar.update(20, 100);
	}
	return deploymentState;

    }

    public String getZookeeperIp() {
	if (config.getConfig().getBoo().getIpOutput()
		.equalsIgnoreCase(Main.IP_OUTPUT)) {
	    return zookeeper.getIpsJson();
	}
	return "";
    }

    public String getYarnIp() {
	if (config.getConfig().getBoo().getIpOutput()
		.equalsIgnoreCase(Main.IP_OUTPUT)) {
	    return yarn.getIpsJson();
	}
	return "";
    }

    public boolean process() throws OneOpsClientAPIException {

	this.bar.update(0, 100);
	boolean isBuildYarn = false;
	if (StringUtils.isEmpty(zkHost)) {
	    LOG.debug("Start to build zookeeper cluster...");
	    // Build zookeeper
	    boolean status = zookeeper.process();
	    if (status) {
		String state = this.waitForActiveDeployment(instance,
			assemblyName, envName, zookeeper.getDeploymentId());
		 LOG.debug(state);
		if (state.equalsIgnoreCase("complete")
			|| state.equalsIgnoreCase("success")) {
		    // Build yarn
		    isBuildYarn = true;
		}
	    }
	    zkHost = zookeeper.getIpsForYarn();
	} else {
	    isBuildYarn = true;
	}
	this.bar.update(30, 100);

	if (isBuildYarn) {
	    LOG.warn("Starting to build the yarn cluster with zookeeper {} ",
			    zkHost);
	    yarn.process();
	}

	return true;
    }
}
