package com.wm.bfd.oo.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.Uninterruptibles;
import com.oo.api.exception.OneOpsClientAPIException;
import com.wm.bfd.oo.ClientConfig;
import com.wm.bfd.oo.LogUtils;
import com.wm.bfd.oo.exception.BFDOOException;
import com.wm.bfd.oo.workflow.AbstractWorkflow;
import com.wm.bfd.oo.workflow.BuildAllPlatforms;
import com.wm.bfd.oo.yaml.Constants;
import com.wm.bfd.oo.yaml.PlatformBean;
import com.wm.bfd.oo.yaml.PlatformConfigBean;
import com.wm.bfd.oo.yaml.Yaml;

public class BFDUtils {

  public boolean verifyTemplate(ClientConfig config) throws BFDOOException {
    if (config == null || config.getYaml() == null || config.getYaml().getAssembly() == null
        || config.getYaml().getPlatforms() == null) {
      throw new BFDOOException(Constants.YAML_ERROR);
    }
    return false;
  }

  /**
   * Parse Ip list
   * 
   * 
   */

  public String getIps(String platformName, String componentName, AbstractWorkflow workFlow)
      throws OneOpsClientAPIException {
    String result = null;
    StringBuilder str = new StringBuilder();
    Yaml yaml = workFlow.getConfig().getYaml();
    Map<String, PlatformConfigBean> platformConfigs = yaml.getExtractBean();
    List<Map<String, String>> ips = workFlow.getIpsInternal(platformName, componentName);
    // for (PlatformConfigBean pfConfig : platformConfigs) {
    for (Map.Entry<String, PlatformConfigBean> pfConfig : platformConfigs.entrySet()) {
      PlatformConfigBean config = pfConfig.getValue();
      if (pfConfig.getKey().equals(platformName)) {
        String split = yaml.getBoo().getCustom().get(config.getCustomSplit().substring(1));
        String format = yaml.getBoo().getCustom().get(config.getCustomFormat().substring(1));
        str.append(new BFDUtils().parseIps(split, format, ips));
      }
    }
    result = str.toString();
    return result;
  }

  /**
   * Wait certain time.
   * @param seconds
   */
  public void waitTimeout(int seconds) {
    Uninterruptibles.sleepUninterruptibly(seconds, TimeUnit.SECONDS);
  }

  public String isCustomized(String val, ClientConfig config) {

    if (val.indexOf(Constants.DOLLAR) >= 0) {
      PlatformConfigBean pfConfig = config.getYaml().getExtractBean().get(val.substring(1));
    }
    return val;
  }

  public String parseIps(String split, String format, List<Map<String, String>> ips) {
    String result = null;
    StringBuilder str = new StringBuilder();
    int num = (null == format) ? Integer.MAX_VALUE : parseNumOfIp(format);
    String ipExtra = null == format ? "" : parseIPExtra(format);
    String ipSplit = null == split ? Constants.DEDAULT_IPSPLIT : split;
    String itemName = null == format ? "" : parseItemName(format);
    for (Map<String, String> ip : ips) {
      if (num-- <= 0)
        break;
      str.append(ipSplit);
      str.append(ip.get(itemName));
      str.append(ipExtra);
    }
    if (str.length() > 0)
      str.delete(0, ipSplit.length());
    result = str.toString();
    return result;
  }

  private int parseNumOfIp(String customFormat) {
    int l = customFormat.indexOf(Constants.NUMTERMINATOR1);
    int r = customFormat.indexOf(Constants.NUMTERMINATOR2);
    if (l == -1 || r == -1 || l >= r)
      return 0;
    return Integer.parseInt(customFormat.substring(l + 1, r));
  }

  private String parseIPExtra(String customFormat) {
    int l = customFormat.indexOf(Constants.IPTERMINATOR2);
    int r = customFormat.indexOf(Constants.NUMTERMINATOR1);
    if (l == -1 || r == -1 || l >= r)
      return "";
    return customFormat.substring(l + 1, r);
  }

  private String parseItemName(String customFormat) {
    int l = customFormat.indexOf(Constants.IPTERMINATOR1);
    int r = customFormat.indexOf(Constants.IPTERMINATOR2);
    if (l == -1 || r == -1 || l >= r)
      return "";
    return customFormat.substring(l + 1, r);
  }
  
  public String getAbsolutePath(String template) {
    if (template == null || template.length() == 0 || template.charAt(0) == Constants.SLASH) {
      return template;
    } else if (template.charAt(0) == Constants.DOT && template.length() > 1 && template.charAt(1) == Constants.DOT) {
      return getAbsolutePath(template.substring(2));
    } else {
      StringBuilder sb = new StringBuilder();
      sb.append(System.getProperty("user.dir"));
      sb.append(Constants.SLASH);
      sb.append(template);
      return sb.toString();
    }
  }
  
  @SuppressWarnings("unchecked")
  public boolean createPlatforms(ClientConfig config, BuildAllPlatforms workflow) throws OneOpsClientAPIException {
    List<PlatformBean> platforms = config.getYaml().getPlatformsList();
    Collections.sort(platforms);
    Queue<Integer> q = new LinkedList<Integer>();
    int prevOrderIdx = platforms.get(0).getDeployOrder();
    for (int i = 0; i < platforms.size(); i++) {
      if (prevOrderIdx == platforms.get(i).getDeployOrder()) {
        q.add(i);
        workflow.createPlatform(platforms.get(i));
      } else {
        checkPlatformQ(workflow);
        q.clear();
        prevOrderIdx++;
      }
    }
    return true;
  }
  
  private void checkPlatformQ(AbstractWorkflow workFlow) {
    while (true) {
      if (!Constants.ACTIVE.equalsIgnoreCase(workFlow.getStatus())) {
        break;
      }
      waitTimeout(30);
    }
  }

  public List<String> getComponentOfCompute(BuildAllPlatforms flow) {
    List<String> comp = new ArrayList<String>();
    for (PlatformBean platform : flow.getConfig().getYaml().getPlatformsList()) {
      for (Map.Entry<String, Object> entry : platform.getComponents().entrySet()) {
        Object value = entry.getValue();
        if (value instanceof Map) {
          Map<String, Object> target = (Map<String, Object>) value;
          if (target.containsKey(Constants.SIZE)) {
            comp.add(entry.getKey());
          }
        }
      }
     }
    return comp;
  }
}