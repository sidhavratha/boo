package com.wm.bfd.oo.workflow;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.wm.bfd.oo.yaml.Constants;
import com.wm.bfd.oo.yaml.PlatformBean;

public abstract class AbstractWorkflow {
  private static Logger LOG = LoggerFactory.getLogger(AbstractWorkflow.class);
  String assemblyName;
  String envName;

  Assembly assembly;
  Design design;
  Transition transition;
  Operation op;

  ClientConfig config;

  OOInstance instance;

  ProgressBar bar;

  public AbstractWorkflow(OOInstance instance, ClientConfig config) throws OneOpsClientAPIException {

    this.instance = instance;
    this.config = config;
    this.assemblyName = config.getYaml().getAssembly().getName();
    this.envName = config.getYaml().getBoo().getEnvName();

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
    for (PlatformBean platform : this.config.getYaml().getPlatformsList()) {
      this.cleanupInt(platform.getName());
    }
    return true;
  }

  private boolean cleanupInt(String platformName) {
    if (design == null)
      return true;
    this.bar.update(5, 100);
    this.cancelDeployment();
    this.bar.update(20, 100);
    this.disableAllPlatforms();
    this.bar.update(40, 100);
    try {
      transition.deleteEnvironment(envName);
    } catch (Exception e) {
      // Ignore
      e.printStackTrace();
    }
    this.bar.update(60, 100);
    this.deleteDesign(platformName);
    // Don't add the following part to one try block as transition.
    try {
      assembly.deleteAssembly(assemblyName);
    } catch (Exception e) {
      // Ignore
    }
    this.bar.update(90, 100);
    op = null;
    design = null;
    assembly = null;
    this.bar.update(100, 100);
    return true;
  }

  void cancelDeployment() {
    try {
      JsonPath response = transition.getLatestDeployment(envName);
      String deploymentId = response.getString("deploymentId");
      response = transition.getLatestRelease(envName);
      String releaseId = response.getString("releaseId");
      LOG.debug("deploymentId:" + deploymentId + "; releaseId: " + releaseId);
      response = transition.getDeploymentStatus(envName, deploymentId);
      Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
      response = transition.cancelDeployment(envName, deploymentId, releaseId);

      LOG.debug("cancelDeployment: " + (response == null ? "" : response.prettyPrint()));
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

  void deleteDesign(String platformName) {
    JsonPath response = null;
    try {
      LOG.debug("deleteEnvironment log:" + (response == null ? "" : response.prettyPrint()));
      response = design.commitDesign();
      LOG.debug("commitDesign log:" + (response == null ? "" : response.prettyPrint()));
      design.deletePlatform(platformName);
    } catch (Exception e) {
      // Ignore
      e.printStackTrace();
    }
  }

  public boolean isAssemblyExist() {
    JsonPath response = null;
    try {
      response = assembly.getAssembly(assemblyName);
    } catch (OneOpsClientAPIException e) {
      String msg = String.format("The assembly %s is not exist!", assemblyName);
      System.err.println(msg);
    }
    return response == null ? false : true;
  }

  public boolean createAssemblyIfNotExist() throws OneOpsClientAPIException {
    boolean isExist = this.isAssemblyExist();
    if (!isExist) {
      assembly.createAssembly(assemblyName, config.getYaml().getBoo().getEmail(), "", "");
    }
    this.bar.update(10, 100);
    return true;
  }

  public boolean isEnvExist(String platformName) {
    JsonPath response = null;
    try {
      response = transition.getEnvironment(envName);
    } catch (OneOpsClientAPIException e) {
      String msg =
          String.format("The environment %s is not exist! %s", platformName, e.getMessage());
      System.err.println(msg);
    }
    return (response == null ? false : true);
  }

  public boolean createEnv() throws OneOpsClientAPIException {
    boolean isExist = this.isEnvExist(envName);
    JsonPath response = null;
    if (!isExist) {
      Map<String, String> cloudMap = new HashMap<String, String>();
      cloudMap.put(config.getYaml().getBoo().getCloudId(), "1");
      response =
          transition.createEnvironment(envName, "DEV", "redundant", null, cloudMap, false, true,
              Constants.DESCRIPTION);
      response = transition.getEnvironment(envName);

      transition.commitEnvironment(envName, null, Constants.DESCRIPTION);
    }
    this.bar.update(50, 100);
    return response == null ? false : true;
  }

  public boolean deploy() throws OneOpsClientAPIException {

    JsonPath response = transition.deploy(envName, Constants.DESCRIPTION);
    this.bar.update(60, 100);
    return response == null ? false : true;
  }

  String getDeploymentId() {
    String id = null;
    try {
      JsonPath latestDeployment = transition.getLatestDeployment(envName);
      id = latestDeployment.getString(Constants.DEPLOYMENT_ID);
    } catch (OneOpsClientAPIException e) {
      // Ignore
    }
    return id;
  }

  public String getStatus() {
    String status = null;
    try {
      JsonPath response = transition.getDeploymentStatus(envName, this.getDeploymentId());
      status = response.getString(Constants.DEPLOYMENT_STATE);
    } catch (OneOpsClientAPIException e) {
      // Ignore
    }
    return status;
  }
}
