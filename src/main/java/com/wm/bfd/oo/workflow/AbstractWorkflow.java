package com.wm.bfd.oo.workflow;

import com.wm.bfd.oo.ClientConfig;
import com.wm.bfd.oo.LogUtils;
import com.wm.bfd.oo.utils.ProgressBar;
import com.wm.bfd.oo.yaml.CloudBean;
import com.wm.bfd.oo.yaml.Constants;
import com.wm.bfd.oo.yaml.PlatformBean;
import com.wm.bfd.oo.yaml.helper.EnvironmentBeanHelper;

import com.google.common.util.concurrent.Uninterruptibles;
import com.jayway.restassured.path.json.JsonPath;
import com.oo.api.OOInstance;
import com.oo.api.exception.OneOpsClientAPIException;
import com.oo.api.resource.Assembly;
import com.oo.api.resource.Cloud;
import com.oo.api.resource.Design;
import com.oo.api.resource.Operation;
import com.oo.api.resource.Transition;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Common methods in work flow.
 */
public abstract class AbstractWorkflow {

  /** The log. */
  private static Logger LOG = LoggerFactory.getLogger(AbstractWorkflow.class);

  /** The pattern. */
  private static Pattern pattern = Pattern.compile("^-[0-9a-zA-Z]{2,9}$");

  /** The assembly name. */
  String assemblyName;

  /** The env name. */
  String envName;

  /** The assembly. */
  Assembly assembly;

  /** The design. */
  Design design;

  /** The transition. */
  Transition transition;

  /** The op. */
  Operation op;

  /** The config. */
  ClientConfig config;

  /** The instance. */
  OOInstance instance;

  /** The cloud. */
  Cloud cloud;

  /** The bar. */
  ProgressBar bar;

  /** The comments. */
  String comments = null;

  /**
   * Instantiates a new abstract workflow.
   *
   * @param instance the instance
   * @param config the config
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public AbstractWorkflow(OOInstance instance, ClientConfig config, String comment)
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
    if (!StringUtils.isBlank(comment)) {
      this.comments = comment;
    }
  }

  /**
   * Process.
   *
   * @param isUpdate the is update
   * @param isAssemblyOnly the is assembly only
   * @return true, if successful
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public abstract boolean process(boolean isUpdate, boolean isAssemblyOnly)
      throws OneOpsClientAPIException;

  /**
   * Cleanup.
   *
   * @return true, if successful
   * @throws OneOpsClientAPIException the one ops client API exception
   */
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

  /**
   * Removes the all envs.
   *
   * @return true, if successful
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public boolean removeAllEnvs() throws OneOpsClientAPIException {
    if (design == null) {
      return true;
    }
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

  /**
   * Removes the all platforms.
   *
   * @return true, if successful
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public boolean removeAllPlatforms() throws OneOpsClientAPIException {
    if (design == null) {
      return true;
    }
    boolean isSuc = true;
    for (String platformName : this.listPlatforms()) {
      try {
        design.deletePlatform(platformName);
      } catch (Exception e) {
        // Do nothing
        isSuc = false;
      }
    }
    if (isSuc) {
      isSuc = this.deleteAssembly();
    }
    return isSuc;
  }

  /**
   * Gets the config.
   *
   * @return the config
   */
  public ClientConfig getConfig() {
    return config;
  }

  /**
   * Cleanup int.
   *
   * @param platformName the platform name
   * @return true, if successful
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  private boolean cleanupInt(String platformName) throws OneOpsClientAPIException {
    return this.cleanupInt(this.envName, platformName);
  }

  /**
   * Cleanup int.
   *
   * @param envName the env name
   * @param platformName the platform name
   * @return true, if successful
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  private boolean cleanupInt(String envName, String platformName) throws OneOpsClientAPIException {
    if (design == null) {
      return true;
    }
    this.cancelDeployment();
    this.disableAllPlatforms();
    try {
      transition.deleteEnvironment(envName);
    } catch (Exception e) {
      // Do nothing
    }
    if (this.isPlatformsExist()) {
      design.deletePlatform(platformName);
    }
    op = null;
    design = null;
    return true;
  }

  /**
   * List envs.
   *
   * @return the list
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public List<String> listEnvs() throws OneOpsClientAPIException {
    JsonPath response = transition.listEnvironments();
    return response.getList(Constants.CINAME);
  }

  /**
   * List platforms.
   *
   * @return the list
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public List<String> listPlatforms() throws OneOpsClientAPIException {
    JsonPath response = design.listPlatforms();
    return response.getList(Constants.CINAME);
  }

  /**
   * Delete assembly.
   *
   * @return true, if successful
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  private boolean deleteAssembly() throws OneOpsClientAPIException {
    return this.deleteAssembly(this.assemblyName);
  }

  /**
   * Delete assembly.
   *
   * @param assemblyName the assembly name
   * @return true, if successful
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  private boolean deleteAssembly(String assemblyName) throws OneOpsClientAPIException {
    // Don't add the following part to one try block as transition.
    assembly.deleteAssembly(assemblyName);
    LogUtils.info(Constants.DESTROY_ASSEMBLY, assemblyName);
    assembly = null;
    return true;
  }

  /**
   * Gets the assemblies.
   *
   * @return the assemblies
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public List<String> getAssemblies() throws OneOpsClientAPIException {
    JsonPath response = assembly.listAssemblies();
    return response.getList(Constants.CINAME);
  }

  /**
   * List attachements.
   *
   * @param platformName the platform name
   * @param componentName the component name
   * @return the list
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public List<String> listAttachements(String platformName, String componentName)
      throws OneOpsClientAPIException {
    JsonPath response = design.listPlatformComponentAttachments(platformName, componentName);
    return response.getList(Constants.CINAME);
  }

  /**
   * Adds the attachement.
   *
   * @param platformName the platform name
   * @param componentName the component name
   * @param uniqueName the unique name
   * @param attributes the attributes
   * @return true, if successful
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public boolean addAttachement(String platformName, String componentName, String uniqueName,
      Map<String, String> attributes) throws OneOpsClientAPIException {
    design.addPlatformComponentAttachment(platformName, componentName, uniqueName, attributes);
    return true;
  }

  /**
   * Update attachement.
   *
   * @param platformName the platform name
   * @param componentName the component name
   * @param uniqueName the unique name
   * @param attributes the attributes
   * @return true, if successful
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public boolean updateAttachement(String platformName, String componentName, String uniqueName,
      Map<String, String> attributes) throws OneOpsClientAPIException {
    design.updatePlatformComponentAttachment(platformName, componentName, uniqueName, attributes);
    return true;
  }

  /**
   * Checks if is attachment exists.
   *
   * @param platformName the platform name
   * @param componentName the component name
   * @param attachmentName the attachment name
   * @return true, if is attachment exists
   */
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

  /**
   * Checks whether the component is user related.
   *
   */
  public boolean isUserCustomizedComponent(String platformName, String componentName)
      throws OneOpsClientAPIException {
    JsonPath componentDetails = design.getPlatformComponent(platformName, componentName);
    Map<String, Object> ciAttrProps = componentDetails.getMap(Constants.CIATTRPROPS);
    if (ciAttrProps == null || ciAttrProps.get(Constants.OWNER) == null) {
      return false;
    }
    Map<String, String> ciAttrPropsMap = (Map<String, String>) ciAttrProps.get(Constants.OWNER);
    return !ciAttrPropsMap.isEmpty();
  }


  /**
   * Cancel deployment.
   *
   * @return true, if successful
   */
  boolean cancelDeployment() {
    return this.cancelDeployment(this.envName);
  }

  /**
   * Cancel deployment.
   *
   * @param envName the env name
   * @return true, if successful
   */
  boolean cancelDeployment(String envName) {
    boolean isSuc = false;
    try {
      JsonPath response = transition.getLatestDeployment(envName);
      String deploymentId = response.getString("deploymentId");
      response = transition.getLatestRelease(envName);
      String releaseId = response.getString("releaseId");
      if (LOG.isDebugEnabled()) {
        LOG.debug("deploymentId:" + deploymentId + "; releaseId: " + releaseId);
      }
      response = transition.getDeploymentStatus(envName, deploymentId);
      Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
      response = transition.cancelDeployment(envName, deploymentId, releaseId);
      if (LOG.isDebugEnabled()) {
        LOG.debug("Cancel deployment: " + (response == null ? "" : response.prettyPrint()));
      }
      isSuc = true;
    } catch (Exception e) {
      // Ignore
    }

    return isSuc;

  }

  /**
   * Sometimes we have to retry a few times to make the deployment done.
   *
   * @return true, if successful
   */
  public boolean retryDeployment() {
    boolean isSuc = false;
    try {
      JsonPath response = transition.getLatestDeployment(envName);
      String deploymentId = response.getString("deploymentId");
      response = transition.getLatestRelease(envName);
      String releaseId = response.getString("releaseId");
      if (LOG.isDebugEnabled()) {
        LOG.debug("deploymentId:" + deploymentId + "; releaseId: " + releaseId);
      }
      response = transition.getDeploymentStatus(envName, deploymentId);
      Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
      response = transition.retryDeployment(envName, deploymentId, releaseId);
      if (LOG.isDebugEnabled()) {
        LOG.debug("Retry deployment: " + (response == null ? "" : response.prettyPrint()));
      }
      isSuc = true;
    } catch (Exception e) {
      // Ignore
    }

    return isSuc;

  }

  /**
   * Disable all platforms.
   */
  void disableAllPlatforms() {
    this.disableAllPlatforms(this.envName);
  }

  /**
   * Disable all platforms.
   *
   * @param envName the env name
   */
  void disableAllPlatforms(String envName) {
    try {
      transition.disableAllPlatforms(envName);
      transition.commitEnvironment(envName, null, "Clean up " + envName);
      if (StringUtils.isBlank(this.comments)) {
        transition.deploy(envName, Constants.CLEANUP_DESCRIPTION);
      } else {
        transition.deploy(envName, comments);
      }
    } catch (Exception e) {
      // Ignore
    }
  }

  /**
   * Delete design.
   *
   * @param platformName the platform name
   */
  void deleteDesign(String platformName) {
    JsonPath response = null;
    try {
      if (LOG.isDebugEnabled()) {
        LOG.debug("deleteEnvironment log:" + (response == null ? "" : response.prettyPrint()));
      }
      response = design.commitDesign();
      if (LOG.isDebugEnabled()) {
        LOG.debug("commitDesign log:" + (response == null ? "" : response.prettyPrint()));
      }
      design.deletePlatform(platformName);
    } catch (Exception e) {
      // Ignore
      e.printStackTrace();
    }
  }

  /**
   * Checks if is assembly exist.
   *
   * @return true, if is assembly exist
   */
  public boolean isAssemblyExist() {
    return this.isAssemblyExist(this.assemblyName);
  }

  /**
   * Checks if is assembly exist.
   *
   * @param assemblyName the assembly name
   * @return true, if is assembly exist
   */
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

  /**
   * List actions.
   *
   * @param platformName the platform name
   * @param componentName the component name
   * @return the list
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public List<String> listActions(String platformName, String componentName)
      throws OneOpsClientAPIException {
    JsonPath response = op.listActions(platformName, componentName);
    return response.getList("actionName");
  }

  /**
   * List instances map.
   *
   * @param platformName the platform name
   * @param componentName the component name
   * @return the map
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public Map<String, Integer> listInstancesMap(String platformName, String componentName)
      throws OneOpsClientAPIException {
    JsonPath response = op.listInstances(platformName, componentName);
    List<String> names = response.getList("ciName");
    List<Integer> ids = response.getList("ciId");
    Map<String, Integer> map = new HashMap<String, Integer>();
    int count = 0;
    for (String name : names) {
      map.put(name, ids.get(count++));
    }
    return map;
  }

  /**
   * List instances.
   *
   * @param platformName the platform name
   * @param componentName the component name
   * @return the list
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public List<String> listInstances(String platformName, String componentName)
      throws OneOpsClientAPIException {
    JsonPath response = op.listInstances(platformName, componentName);
    return response.getList("ciName");
  }

  /**
   * Gets the procedure status.
   *
   * @param procedureId the procedure id
   * @return the procedure status
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public List<String> getProcedureStatus(String procedureId) throws OneOpsClientAPIException {
    JsonPath response = op.getProcedureStatus(procedureId);
    return response.getList("ciName");
  }

  /**
   * List instance ids.
   *
   * @param platformName the platform name
   * @param componentName the component name
   * @return the list
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  private List<String> listInstanceIds(String platformName, String componentName)
      throws OneOpsClientAPIException {
    JsonPath response = op.listInstances(platformName, componentName);
    return response.getList("ciId");
  }

  /**
   * Execute action.
   *
   * @param platformName the platform name
   * @param componentName the component name
   * @param actionName the action name
   * @param arglist the arglist
   * @param instanceList the instance list
   * @param rollAt the roll at
   * @return the string
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public String executeAction(String platformName, String componentName, String actionName,
      String arglist, List<String> instanceList, int rollAt) throws OneOpsClientAPIException {
    List<String> list = new ArrayList<String>();
    if (instanceList == null || instanceList.size() == 0) {

      list = this.listInstanceIds(platformName, componentName);
    } else {
      Map<String, Integer> map = this.listInstancesMap(platformName, componentName);
      for (String name : instanceList) {
        list.add(String.valueOf(map.get(name)));
      }
    }
    JsonPath response =
        op.executeAction(platformName, componentName, actionName, list, arglist, rollAt);
    return response == null ? null : response.getString("procedureId");
  }

  /**
   * Gets the procedure status for action.
   *
   * @param procedureId the procedure id
   * @return the procedure status for action
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public String getProcedureStatusForAction(String procedureId) throws OneOpsClientAPIException {
    return op.getProcedureStatus(procedureId).getString("procedureState");
  }

  /**
   * Checks if is platforms exist.
   *
   * @return true, if is platforms exist
   */
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

  /**
   * Platform exist.
   *
   * @param platformName the platform name
   * @return true, if successful
   */
  public boolean platformExist(String platformName) {
    JsonPath response = null;
    try {
      response = design.getPlatform(platformName);
    } catch (OneOpsClientAPIException e) {
      // Ignore
    }
    return response == null ? false : true;
  }

  /**
   * Creates the assembly if not exist.
   *
   * @return true, if successful
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public boolean createAssemblyIfNotExist() throws OneOpsClientAPIException {
    boolean isExist = this.isAssemblyExist();
    if (!isExist) {
      this.checkAssemblyName();
      assembly.createAssembly(assemblyName, config.getYaml().getBoo().getEmail(), "", "");
    }
    return true;
  }

  /**
   * Check assembly name.
   */
  void checkAssemblyName() {
    if (this.assemblyName.length() > 32) {
      System.err.println();
      System.err.println(Constants.ASSEMBLY_NAME_TOO_LONG);
      System.exit(3);
    }
  }

  /**
   * Checks if is match.
   *
   * @param str the str
   * @param prefix the prefix
   * @return true, if is match
   */
  private boolean isMatch(String str, String prefix) {
    if (str.startsWith(prefix)) {
      Matcher matcher = pattern.matcher(str.substring(prefix.length()));
      if (matcher.matches()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gets the all auto gen assemblies.
   *
   * @param prefix the prefix
   * @return the all auto gen assemblies
   */
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
      if (this.isAssemblyExist(prefix)) {
        matches.add(prefix);
      }
      return matches;
    }
    return null;
  }

  /**
   * Checks if is env exist.
   *
   * @param platformName the platform name
   * @return true, if is env exist
   */
  public boolean isEnvExist(String platformName) {
    JsonPath response = null;
    try {
      response = transition.getEnvironment(envName);
    } catch (OneOpsClientAPIException e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug(Constants.ENV_NOT_EXISTING, platformName, e.getMessage());
      }
    }
    return (response == null ? false : true);
  }

  /**
   * Creates the env.
   *
   * @return true, if successful
   * @throws OneOpsClientAPIException the one ops client API exception
   */
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
      if (StringUtils.isBlank(this.comments)) {
        transition.commitEnvironment(envName, null, Constants.DESCRIPTION);
      } else {
        transition.commitEnvironment(envName, null, comments);
      }

    } else {
      LogUtils.info(Constants.ENV_EXISTING, envName);
    }
    return response == null ? false : true;
  }


  /**
   * Update env.
   *
   * @return true, if successful
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  @SuppressWarnings("unchecked")
  public boolean updateEnv() throws OneOpsClientAPIException {
    List<PlatformBean> platforms = config.getYaml().getEnvironmentBean().getPlatformsList();
    if (platforms == null) {
      return false;
    }
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

  /**
   * Update platform cloud scale.
   *
   * @return true, if successful
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public boolean updatePlatformCloudScale() throws OneOpsClientAPIException {
    for (PlatformBean platform : this.config.getYaml().getPlatformsList()) {
      if (this.platformExist(platform.getName())) {
        Map<String, Object> sysClouds = transition.getEnvironment(envName).getMap(Constants.CLOUDS);
        List<CloudBean> clouds = config.getYaml().getEnvironmentBean().getClouds();
        for (CloudBean cloud : clouds) {
          if (sysClouds.containsKey(this.getCloudId(cloud.getCloudName()))) {
            Map<String, String> cloudMap = new HashMap<String, String>();
            cloudMap.put(EnvironmentBeanHelper.ADMINSTATUS, Constants.ACTIVE);
            cloudMap.put(EnvironmentBeanHelper.PRIORITY, cloud.getPriority());
            cloudMap.put(EnvironmentBeanHelper.DPMT_ORDER, cloud.getDpmtOrder());
            cloudMap.put(EnvironmentBeanHelper.PCT_SCALE, cloud.getPctScale());
            transition.updatePlatformCloudScale(envName, platform.getName(),
                this.getCloudId(cloud.getCloudName()), cloudMap);
            // If cloud exists in yaml, remove the cloud name from the system clouds map after
            // updating cloud.
            sysClouds.remove(this.getCloudId(cloud.getCloudName()));
          }
        }
        // For rest clouds not in yaml, set them as shutdown.
        for (String cloud : sysClouds.keySet()) {
          Map<String, String> cloudMap = new HashMap<String, String>();
          cloudMap.put(EnvironmentBeanHelper.ADMINSTATUS, Constants.OFFLINE);
          transition.updatePlatformCloudScale(envName, platform.getName(), cloud, cloudMap);
        }
      }
    }
    return true;
  }

  /**
   * Pull design.
   *
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public void pullDesign() throws OneOpsClientAPIException {
    transition.pullDesin(envName);
  }

  /**
   * Commit env.
   *
   * @return true, if successful
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public boolean commitEnv() throws OneOpsClientAPIException {
    JsonPath response;
    if (StringUtils.isBlank(this.comments)) {
      response = transition.commitEnvironment(envName, null, Constants.DESCRIPTION);
    } else {
      response = transition.commitEnvironment(envName, null, comments);
    }
    return response == null ? false : true;
  }

  /**
   * Deploy.
   *
   * @return true, if successful
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public boolean deploy(boolean isUpdate) throws OneOpsClientAPIException {
    JsonPath response;
    if (StringUtils.isBlank(this.comments)) {
      if (isUpdate) {
        response = transition.deploy(envName, Constants.UPDATE_DESCRIPTION);
      } else {
        response = transition.deploy(envName, Constants.CREATE_DESCRIPTION);
      }
    } else {
      response = transition.deploy(envName, comments);
    }
    return response == null ? false : true;
  }


  /**
   * Get ip address from oneops operate.
   *
   * @param platformName the platform name
   * @param componentName the component name
   * @return the ips internal
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public List<Map<String, String>> getIpsInternal(String platformName, String componentName)
      throws OneOpsClientAPIException {
    JsonPath response = op.listInstances(platformName, componentName);
    return response.getList("ciAttributes");
  }

  /**
   * Gets the cloud id.
   *
   * @param cloudName the cloud name
   * @return the cloud id
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public String getCloudId(String cloudName) throws OneOpsClientAPIException {
    JsonPath response = cloud.getCloud(cloudName);
    return response.getString("ciId");
  }

  /**
   * Gets the deployment id.
   *
   * @return the deployment id
   */
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

  /**
   * Gets the status.
   *
   * @return the status
   */
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
