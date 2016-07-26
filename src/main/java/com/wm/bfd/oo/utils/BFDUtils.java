package com.wm.bfd.oo.utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.Uninterruptibles;
import com.oo.api.exception.OneOpsClientAPIException;
import com.wm.bfd.oo.ClientConfig;
import com.wm.bfd.oo.exception.BFDOOException;
import com.wm.bfd.oo.workflow.AbstractWorkflow;
import com.wm.bfd.oo.yaml.Constants;
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
  public static void wait(String seconds) {
    Uninterruptibles.sleepUninterruptibly(20, TimeUnit.SECONDS);
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

}
