package com.wm.bfd.oo.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.Uninterruptibles;
import com.jayway.restassured.path.json.JsonPath;
import com.oo.api.OOInstance;
import com.oo.api.exception.OneOpsClientAPIException;
import com.oo.api.resource.Assembly;
import com.oo.api.resource.Cloud;
import com.oo.api.resource.Design;
import com.oo.api.resource.Operation;
import com.oo.api.resource.Transition;
import com.wm.bfd.oo.ClientConfig;
import com.wm.bfd.oo.LogUtils;
import com.wm.bfd.oo.utils.ProgressBar;
import com.wm.bfd.oo.yaml.CloudBean;
import com.wm.bfd.oo.yaml.Constants;
import com.wm.bfd.oo.yaml.PlatformBean;
import com.wm.bfd.oo.yaml.helper.EnvironmentBeanHelper;

public abstract class AbstractWorkflow {
  private static Logger LOG = LoggerFactory.getLogger(AbstractWorkflow.class);
  private static Pattern pattern = Pattern.compile("^-[0-9a-zA-Z]{2,9}$");
  String assemblyName;
  String envName;

  Assembly assembly;
  Design design;
  Transition transition;
  Operation op;

  ClientConfig config;

  OOInstance instance;

  Cloud cloud;

  ProgressBar bar;

  public AbstractWorkflow(OOInstance instance, ClientConfig config)
      throws OneOpsClientAPIException {

    this.instance = instance;
    this.config = config;
    this.assemblyName = config.getYaml().getAssembly().getName();
    this.envName = config.getYaml().getBoo().getEnvName();
    this.cloud = new Cloud(instance);

    assembly = new Assembly(instance);
    design = new Design(instance, assemblyName);
    transition = new Transition(instance, assemblyName);
    op = new Operation(instance, assemblyName, envName);
    this.config = config;

    this.bar = new ProgressBar();
  }

  public abstract boolean process(boolean isUpdate, boolean isAssemblyOnly)
      throws OneOpsClientAPIException;

  public boolean cleanup() throws OneOpsClientAPIException {
    for (PlatformBean platform : this.config.getYaml().getPlatformsList()) {
      if (this.platformExist(platform.getName())) {
        LogUtils.info(Constants.DESTROY_PLATFORM, platform.getName());
        this.cleanupInt(platform.getName());
      }
    }
    this.deleteAssembly();
    return true;
  }

  public boolean removeAllEnvs() throws OneOpsClientAPIException {
    if (design == null)
      return true;
    for (String env : this.listEnvs()) {
      this.cancelDeployment(env);
      this.disableAllPlatforms(env);
      try {
        transition.deleteEnvironment(env);
      } catch (Exception e) {
        // Do nothing
      }
    }
    return true;
  }

  public boolean removeAllPlatforms() throws OneOpsClientAPIException {
    if (design == null)
      return true;
    boolean isSuc = true;
    for (String platformName : this.listPlatforms()) {
      try {
        design.deletePlatform(platformName);
      } catch (Exception e) {
        // Do nothing
        isSuc = false;
      }
    }
    if (isSuc)
      isSuc = this.deleteAssembly();
    return isSuc;
  }

  public ClientConfig getConfig() {
    return config;
  }

  private boolean cleanupInt(String platformName) throws OneOpsClientAPIException {
    return this.cleanupInt(this.envName, platformName);
  }

  private boolean cleanupInt(String envName, String platformName) throws OneOpsClientAPIException {
    if (design == null)
      return true;
    this.cancelDeployment();
    this.disableAllPlatforms();
    try {
      transition.deleteEnvironment(envName);
    } catch (Exception e) {
      // Do nothing
    }
    if (this.isPlatformsExist())
      design.deletePlatform(platformName);
    op = null;
    design = null;
    return true;
  }

  public List<String> listEnvs() throws OneOpsClientAPIException {
    JsonPath response = transition.listEnvironments();
    return response.getList(Constants.CINAME);
  }

  public List<String> listPlatforms() throws OneOpsClientAPIException {
    JsonPath response = design.listPlatforms();
    return response.getList(Constants.CINAME);
  }

  private boolean deleteAssembly() throws OneOpsClientAPIException {
    return this.deleteAssembly(this.assemblyName);
  }

  private boolean deleteAssembly(String assemblyName) throws OneOpsClientAPIException {
    // Don't add the following part to one try block as transition.
    assembly.deleteAssembly(assemblyName);
    LogUtils.info(Constants.DESTROY_ASSEMBLY, assemblyName);
    assembly = null;
    return true;
  }

  /**
   * 
   * @throws OneOpsClientAPIException
   */
  public List<String> getAssemblies() throws OneOpsClientAPIException {
    JsonPath response = assembly.listAssemblies();
    return response.getList(Constants.CINAME);
  }

  public List<String> listAttachements(String platformName, String componentName)
      throws OneOpsClientAPIException {
    JsonPath response = design.listPlatformComponentAttachments(platformName, componentName);
    return response.getList(Constants.CINAME);
  }

  public boolean addAttachement(String platformName, String componentName, String uniqueName,
      Map<String, String> attributes) throws OneOpsClientAPIException {
    design.addPlatformComponentAttachment(platformName, componentName, uniqueName, attributes);
    return true;
  }

  public boolean updateAttachement(String platformName, String componentName, String uniqueName,
      Map<String, String> attributes) throws OneOpsClientAPIException {
    design.updatePlatformComponentAttachment(platformName, componentName, uniqueName, attributes);
    return true;
  }

  public boolean isAttachmentExists(String platformName, String componentName,
      String attachmentName) {
    boolean isExist = true;
    JsonPath response = null;
    try {
      response = design.getPlatformComponentAttachment(platformName, componentName, attachmentName);
    } catch (Exception e) {
      isExist = false;
    }
    return (response == null || !isExist) ? false : true;
  }

  boolean cancelDeployment() {
    return this.cancelDeployment(this.envName);
  }

  boolean cancelDeployment(String envName) {
    boolean isSuc = false;
    try {
      JsonPath response = transition.getLatestDeployment(envName);
      String deploymentId = response.getString("deploymentId");
      response = transition.getLatestRelease(envName);
      String releaseId = response.getString("releaseId");
      if (LOG.isDebugEnabled())
        LOG.debug("deploymentId:" + deploymentId + "; releaseId: " + releaseId);
      response = transition.getDeploymentStatus(envName, deploymentId);
      Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
      response = transition.cancelDeployment(envName, deploymentId, releaseId);
      if (LOG.isDebugEnabled())
        LOG.debug("Cancel deployment: " + (response == null ? "" : response.prettyPrint()));
      isSuc = true;
    } catch (Exception e) {
      // Ignore
    }

    return isSuc;

  }

  /**
   * Sometimes we have to retry a few times to make the deployment done.
   * 
   * @return
   */
  public boolean retryDeployment() {
    boolean isSuc = false;
    try {
      JsonPath response = transition.getLatestDeployment(envName);
      String deploymentId = response.getString("deploymentId");
      response = transition.getLatestRelease(envName);
      String releaseId = response.getString("releaseId");
      if (LOG.isDebugEnabled())
        LOG.debug("deploymentId:" + deploymentId + "; releaseId: " + releaseId);
      response = transition.getDeploymentStatus(envName, deploymentId);
      Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
      response = transition.retryDeployment(envName, deploymentId, releaseId);
      if (LOG.isDebugEnabled())
        LOG.debug("Retry deployment: " + (response == null ? "" : response.prettyPrint()));
      isSuc = true;
    } catch (Exception e) {
      // Ignore
    }

    return isSuc;

  }

  void disableAllPlatforms() {
    this.disableAllPlatforms(this.envName);
  }

  void disableAllPlatforms(String envName) {
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
      if (LOG.isDebugEnabled())
        LOG.debug("deleteEnvironment log:" + (response == null ? "" : response.prettyPrint()));
      response = design.commitDesign();
      if (LOG.isDebugEnabled())
        LOG.debug("commitDesign log:" + (response == null ? "" : response.prettyPrint()));
      design.deletePlatform(platformName);
    } catch (Exception e) {
      // Ignore
      e.printStackTrace();
    }
  }

  public boolean isAssemblyExist() {
    return this.isAssemblyExist(this.assemblyName);
  }

  public boolean isAssemblyExist(String assemblyName) {
    JsonPath response = null;
    try {
      response = assembly.getAssembly(assemblyName);
    } catch (OneOpsClientAPIException e) {
      // String msg = String.format("The assembly %s is not exist!", assemblyName);
      // System.err.println(msg);
      // Ignore
    }
    return response == null ? false : true;
  }

  public boolean isPlatformsExist() {
    JsonPath response = null;
    try {
      response = design.listPlatforms();
    } catch (OneOpsClientAPIException e) {
      // String msg = String.format("The assembly %s is not exist!", assemblyName);
      // System.err.println(msg);
      // Ignore
    }
    return response == null ? false : true;
  }

  public boolean platformExist(String platformName) {
    JsonPath response = null;
    try {
      response = design.getPlatform(platformName);
    } catch (OneOpsClientAPIException e) {
      // Ignore
    }
    return response == null ? false : true;
  }

  public boolean createAssemblyIfNotExist() throws OneOpsClientAPIException {
    boolean isExist = this.isAssemblyExist();
    if (!isExist) {
      this.checkAssemblyName();
      assembly.createAssembly(assemblyName, config.getYaml().getBoo().getEmail(), "", "");
    }
    return true;
  }

  void checkAssemblyName() {
    if (this.assemblyName.length() > 32) {
      System.err.println();
      System.err.println(Constants.ASSEMBLY_NAME_TOO_LONG);
      System.exit(3);
    }
  }

  private boolean isMatch(String str, String prefix) {
    if (str.startsWith(prefix)) {
      Matcher matcher = pattern.matcher(str.substring(prefix.length()));
      if (matcher.matches()) {
        return true;
      }
    }
    return false;
  }

  public List<String> getAllAutoGenAssemblies(String prefix) {

    if (config.getYaml().getAssembly().getAutoGen()) {
      try {
        List<String> assemblies = this.getAssemblies();
        if (assemblies != null && assemblies.size() > 0) {
          List<String> matches = new ArrayList<String>();
          for (String assembly : assemblies) {
            // Only match auto generated string.
            if (this.isMatch(assembly, prefix)) {
              matches.add(assembly);
            }
          }
          return matches;
        }
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
    } else {
      List<String> matches = new ArrayList<String>();
      if (this.isAssemblyExist(prefix))
        matches.add(prefix);
      return matches;
    }
    return null;
  }

  public boolean isEnvExist(String platformName) {
    JsonPath response = null;
    try {
      response = transition.getEnvironment(envName);
    } catch (OneOpsClientAPIException e) {
      if (LOG.isDebugEnabled())
        LOG.debug(Constants.ENV_NOT_EXISTING, platformName, e.getMessage());
    }
    return (response == null ? false : true);
  }

  public boolean createEnv() throws OneOpsClientAPIException {
    boolean isExist = this.isEnvExist(envName);
    JsonPath response = null;
    if (!isExist) {

      LogUtils.info(Constants.CREATE_ENV, envName);

      Map<String, Map<String, String>> cloudMaps = new HashMap<String, Map<String, String>>();

      List<CloudBean> clouds = config.getYaml().getEnvironmentBean().getClouds();
      for (CloudBean cloud : clouds) {
        Map<String, String> cloudMap = new HashMap<String, String>();
        cloudMap.put(EnvironmentBeanHelper.PRIORITY, cloud.getPriority());
        cloudMap.put(EnvironmentBeanHelper.DPMT_ORDER, cloud.getDpmtOrder());
        cloudMap.put(EnvironmentBeanHelper.PCT_SCALE, cloud.getPctScale());
        cloudMaps.put(this.getCloudId(cloud.getCloudName()), cloudMap);
      }


      // config.getYaml().getBoo().getCloud();

      // boolean isGlobalDns = Constants.TRUE.equalsIgnoreCase(cloudMap.get(Constants.GLOBAL_DNS));
      // String availability = cloudMap.get(Constants.AVAILABILITY);
      // if (StringUtils.isEmpty(availability))
      // throw new OneOpsClientAPIException(Constants.NO_AVAILABILITY);
      response = transition.createEnvironment(envName,
          config.getYaml().getEnvironmentBean().getOthers().get(Constants.AVAILABILITY),
          config.getYaml().getEnvironmentBean().getOthers(), null, cloudMaps,
          Constants.DESCRIPTION);
      response = transition.getEnvironment(envName);

      transition.commitEnvironment(envName, null, Constants.DESCRIPTION);
    } else {
      LogUtils.info(Constants.ENV_EXISTING, envName);
    }
    return response == null ? false : true;
  }


  @SuppressWarnings("unchecked")
  public boolean updateEnv() throws OneOpsClientAPIException {
    List<PlatformBean> platforms = config.getYaml().getEnvironmentBean().getPlatformsList();
    if (platforms == null)
      return false;
    for (PlatformBean platform : platforms) {
      Map<String, Object> map = platform.getComponents();
      if (map != null) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
          transition.updatePlatformComponent(envName, platform.getName(), entry.getKey(),
              (Map<String, String>) entry.getValue());
        }
      }
    }
    return true;
  }

  public void pullDesign() throws OneOpsClientAPIException {
    transition.pullDesin(envName);
  }

  public boolean commitEnv() throws OneOpsClientAPIException {
    JsonPath response = transition.commitEnvironment(envName, null, Constants.DESCRIPTION);
    return response == null ? false : true;
  }

  public boolean deploy() throws OneOpsClientAPIException {

    JsonPath response = transition.deploy(envName, Constants.DESCRIPTION);
    return response == null ? false : true;
  }


  /**
   * Get ip address from oneops operate.
   * 
   * @param platformName
   * @param componentName
   * @return
   * @throws OneOpsClientAPIException
   */
  public List<Map<String, String>> getIpsInternal(String platformName, String componentName)
      throws OneOpsClientAPIException {
    JsonPath response = op.listInstances(platformName, componentName);
    return response.getList("ciAttributes");
  }

  public String getCloudId(String cloudName) throws OneOpsClientAPIException {
    JsonPath response = cloud.getCloud(cloudName);
    return response.getString("ciId");
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
