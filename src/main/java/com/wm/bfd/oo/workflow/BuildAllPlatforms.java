package com.wm.bfd.oo.workflow;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.restassured.path.json.JsonPath;
import com.oo.api.OOInstance;
import com.oo.api.exception.OneOpsClientAPIException;
import com.oo.api.exception.OneOpsComponentExistException;
import com.oo.api.resource.model.RedundancyConfig;
import com.wm.bfd.oo.ClientConfig;
import com.wm.bfd.oo.LogUtils;
import com.wm.bfd.oo.utils.BFDUtils;
import com.wm.bfd.oo.yaml.Constants;
import com.wm.bfd.oo.yaml.PlatformBean;
import com.wm.bfd.oo.yaml.ScalBean;

public class BuildAllPlatforms extends AbstractWorkflow {
  final private static Logger LOG = LoggerFactory.getLogger(BuildAllPlatforms.class);
  final private static String NAME = "ciName"; // Get component name.
  final private static String ACTIVE = "active";
  final private static String FAILED = "failed";
  final private static String NEWLINE = System.getProperty("line.separator");
  final private BFDUtils utils = new BFDUtils();

  public BuildAllPlatforms(OOInstance instance, ClientConfig config)
      throws OneOpsClientAPIException {
    super(instance, config);
  }

  public boolean process() throws OneOpsClientAPIException {
    this.bar.update(1, 100);
    this.createAssemblyIfNotExist();
    this.bar.update(5, 100);
    this.createPlatforms();
    this.bar.update(15, 100);
    this.updatePlatformVariables();
    this.bar.update(20, 100);
    this.createEnv();
    this.bar.update(30, 100);
    String status = this.getStatus();
    if (ACTIVE.equalsIgnoreCase(status)) {
      LogUtils.info(Constants.ACTIVE_DEPLOYMENT_EXISTING);
      return false;
    }

    if (FAILED.equalsIgnoreCase(status)) {
      LogUtils.info(Constants.FAIL_DEPLOYMENT_EXISTING);
      return false;
    }
    this.updateScaling();
    this.bar.update(50, 100);
    LogUtils.info(Constants.START_DEPLOYMENT);
    try {
      this.pullDesign();
    } catch (OneOpsClientAPIException e) {
      // Ignore
    }
    this.bar.update(70, 100);
    this.deploy();
    this.bar.update(100, 100);
    LogUtils.info(Constants.DEPLOYMENT_RUNNING);
    return true;
  }

  public boolean isPlatformExist(String platformName) throws OneOpsClientAPIException,
      OneOpsComponentExistException {
    JsonPath response = null;
    try {
      response = design.getPlatform(platformName);
    } catch (OneOpsClientAPIException e) {
      String msg = String.format("The platform %s is not exist!", platformName);
      throw new OneOpsComponentExistException(msg);
    }
    return response == null ? false : true;
  }

  @SuppressWarnings("unchecked")
  public boolean createPlatforms() throws OneOpsClientAPIException {
    List<PlatformBean> platforms = this.config.getYaml().getPlatformsList();
    Collections.sort(platforms);
    for (PlatformBean platform : platforms) {
      LogUtils.info(Constants.CREATING_PLATFORM, platform.getName());
      this.createPlatform(platform);
      if (platform.getComponents() == null)
        continue;
      for (Map.Entry<String, Object> entry : platform.getComponents().entrySet()) {
        String key = entry.getKey();
        Object value = entry.getValue();
        if (value instanceof Map) {
          this.updateComponentVariables(platform.getName(), key, (Map<String, String>) value);
        } else {
          LOG.info("Unknow type {}.", value.getClass());
        }
      }
    }
    return true;
  }

  private boolean createPlatform(PlatformBean platform) throws OneOpsClientAPIException {
    boolean isExist = false;
    try {
      isExist = this.isPlatformExist(platform.getName());
    } catch (OneOpsComponentExistException e) {
      // Ignore
    }
    if (!isExist) {
      JsonPath response =
          design.createPlatform(platform.getName(), platform.getPack(), platform.getPackVersion(),
              platform.getPackSource(), Constants.DESCRIPTION, Constants.DESCRIPTION);
      if (response != null)
        design.commitDesign();
      LogUtils.info(Constants.CREATING_PLATFORM_SUCCEED, platform.getName());
    } else {
      LogUtils.info(Constants.PLATFORM_EXISTING, platform.getName());
    }
    return true;

  }

  private boolean isComponentExist(String platformName, String componentName)
      throws OneOpsClientAPIException, OneOpsComponentExistException {
    JsonPath j = null;
    try {
      j = design.getPlatformComponent(platformName, componentName);
    } catch (OneOpsClientAPIException e) {
      // e.printStackTrace();
      throw new OneOpsComponentExistException(e.getMessage());
    }
    return (j == null ? false : true);
  }


  private boolean updatePlatformVariables() throws OneOpsClientAPIException {
    List<PlatformBean> platforms = this.config.getYaml().getPlatformsList();
    for (PlatformBean platform : platforms) {
      Map<String, String> secureVariables = platform.getSecureVariables();
      if (secureVariables != null && secureVariables.size() > 0)
        this.updateOrAddPlatformVariables(platform.getName(), secureVariables, true);
      Map<String, String> variables = platform.getVariables();
      if (variables != null && variables.size() > 0)
        this.updateOrAddPlatformVariables(platform.getName(), variables, false);
    }
    if (platforms.size() > 0)
      design.commitDesign();
    return true;
  }

  /**
   * Have to add isExist method later
   * 
   * @param platformName
   * @param variables
   * @param isSecure
   * @throws OneOpsClientAPIException
   */
  private void updateOrAddPlatformVariables(String platformName, Map<String, String> variables,
      boolean isSecure) throws OneOpsClientAPIException {
    try {
      design.updatePlatformVariable(platformName, variables, isSecure);
    } catch (OneOpsClientAPIException e) {
      design.addPlatformVariable(platformName, variables, isSecure);
    }
  }

  private boolean updateComponentVariables(String platformName, String componentName,
      Map<String, String> attributes) throws OneOpsClientAPIException {
    String uniqueName = componentName;
    if (componentName.endsWith(Constants.USER)) {
      attributes.put(Constants.DESCRIPTIONS, Constants.DESCRIPTION);
      uniqueName = attributes.get(NAME);
      attributes.remove(NAME);
    }
    LogUtils.info(Constants.UPDATE_COMPONENTS, componentName, platformName);
    boolean isExist = false;
    try {
      isExist = this.isComponentExist(platformName, componentName);
    } catch (OneOpsComponentExistException e1) {
      // Ignore
    }
    if (isExist) {
      design.updatePlatformComponent(platformName, componentName, attributes);
    } else {
      try {
        design.addPlatformComponent(platformName, componentName, uniqueName, attributes);
      } catch (Exception e) {
        // Ignore
        if (LOG.isDebugEnabled())
          LOG.debug("Update component variables failed! {}", e.getMessage());
        // System.err.println(e.getMessage());
      }
    }
    design.commitDesign();
    return true;
  }

  public String getCustomIps(String platformName, String componentName)
      throws OneOpsClientAPIException {
    return utils.getIps(platformName, componentName, this);
  }

  public String printIps(String platformName, String componentName) throws OneOpsClientAPIException {
    List<Map<String, String>> ips = this.getIpsInternal(platformName, componentName);
    StringBuilder str = new StringBuilder();
    for (Map<String, String> ip : ips) {
      str.append(ip.get(Constants.PRIVATE_IP));
      str.append(NEWLINE);
    }
    return str.toString();
  }

  public boolean updateScaling() throws OneOpsClientAPIException {
    List<ScalBean> scales = this.config.getYaml().getScales();
    for (ScalBean scale : scales) {
      RedundancyConfig config = new RedundancyConfig();
      config.setCurrent(scale.getCurrent());
      config.setMin(scale.getMin());
      config.setMax(scale.getMax());
      LogUtils.info(Constants.COMPUTE_SIZE, envName, scale.getPlatform());
      transition.updatePlatformRedundancyConfig(envName, scale.getPlatform(), config);
    }
    transition.commitEnvironment(envName, null, Constants.DESCRIPTION);
    return true;
  }

}
