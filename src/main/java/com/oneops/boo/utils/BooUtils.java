/*
 * Copyright 2017 Walmart, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oneops.boo.utils;

import com.google.common.util.concurrent.Uninterruptibles;
import com.oneops.boo.ClientConfig;
import com.oneops.boo.exception.BooException;
import com.oneops.boo.workflow.AbstractWorkflow;
import com.oneops.boo.workflow.BuildAllPlatforms;
import com.oneops.boo.yaml.Constants;
import com.oneops.boo.yaml.PlatformBean;
import com.oneops.boo.yaml.PlatformConfigBean;
import com.oneops.boo.yaml.Yaml;
import com.oneops.client.api.exception.OneOpsClientAPIException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * Common uses methods.
 */
public class BooUtils {


  /**
   * Verify template.
   *
   * @param config the config
   * @return true, if successful
   * @throws BooException the boo exception
   */
  public boolean verifyTemplate(ClientConfig config) throws BooException {
    if (config == null || config.getYaml() == null || config.getYaml().getAssembly() == null
        || config.getYaml().getPlatforms() == null) {
      throw new BooException(Constants.YAML_ERROR);
    }
    return false;
  }

  /**
   * Parse Ip list.
   * 
   * @param platformName the platform name
   * @param componentName the component name
   * @param workFlow the work flow
   * @return the ips
   * @throws OneOpsClientAPIException the one ops client API exception
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
        str.append(this.parseIps(split, format, ips));
      }
    }
    result = str.toString();
    return result;
  }

  /**
   * Wait certain time.
   *
   * @param seconds the seconds
   */
  public void waitTimeout(int seconds) {
    Uninterruptibles.sleepUninterruptibly(seconds, TimeUnit.SECONDS);
  }

  /**
   * Checks if is customized.
   *
   * @param val the val
   * @param config the config
   * @return the string
   */
  public String isCustomized(String val, ClientConfig config) {

    if (val.indexOf(Constants.DOLLAR) >= 0) {
      config.getYaml().getExtractBean().get(val.substring(1));
    }
    return val;
  }

  /**
   * Parses the ips.
   *
   * @param split the split
   * @param format the format
   * @param ips the ips
   * @return the string
   */
  public String parseIps(String split, String format, List<Map<String, String>> ips) {
    String result = null;
    StringBuilder str = new StringBuilder();
    int num = (null == format) ? Integer.MAX_VALUE : parseNumOfIp(format);
    String ipExtra = null == format ? "" : parseIpExtra(format);
    String ipSplit = null == split ? Constants.DEDAULT_IPSPLIT : split;
    String itemName = null == format ? "" : parseItemName(format);
    for (Map<String, String> ip : ips) {
      if (num-- <= 0) {
        break;
      }
      str.append(ipSplit);
      str.append(ip.get(itemName));
      str.append(ipExtra);
    }
    if (str.length() > 0) {
      str.delete(0, ipSplit.length());
    }
    result = str.toString();
    return result;
  }

  /**
   * Parses the num of ip.
   *
   * @param customFormat the custom format
   * @return the int
   */
  private int parseNumOfIp(String customFormat) {
    int left = customFormat.indexOf(Constants.NUMTERMINATOR1);
    int right = customFormat.indexOf(Constants.NUMTERMINATOR2);
    if (left == -1 || right == -1 || left >= right) {
      return 0;
    }
    return Integer.parseInt(customFormat.substring(left + 1, right));
  }

  /**
   * Parses the ip extra.
   *
   * @param customFormat the custom format
   * @return the string
   */
  private String parseIpExtra(String customFormat) {
    int left = customFormat.indexOf(Constants.IPTERMINATOR2);
    int right = customFormat.indexOf(Constants.NUMTERMINATOR1);
    if (left == -1 || right == -1 || left >= right) {
      return "";
    }
    return customFormat.substring(left + 1, right);
  }

  /**
   * Parses the item name.
   *
   * @param customFormat the custom format
   * @return the string
   */
  private String parseItemName(String customFormat) {
    int left = customFormat.indexOf(Constants.IPTERMINATOR1);
    int right = customFormat.indexOf(Constants.IPTERMINATOR2);
    if (left == -1 || right == -1 || left >= right) {
      return "";
    }
    return customFormat.substring(left + 1, right);
  }

  /**
   * Gets the absolute path.
   *
   * @param template the template
   * @return the absolute path
   */
  public String getAbsolutePath(String template) {
    if (template == null || template.length() == 0 || template.charAt(0) == Constants.SLASH) {
      return template;
    } else if (template.length() > 2 && template.substring(0, 2).equals(Constants.DOUBLE_PERIOD)) {
      return parseDoublePeriodPath(template);
    } else if (template.charAt(0) == Constants.DOT && template.length() > 1
        && template.charAt(1) == Constants.DOT) {
      return getAbsolutePath(template.substring(2));
    } else {
      StringBuilder sb = new StringBuilder();
      sb.append(System.getProperty("user.dir"));
      sb.append(Constants.SLASH);
      sb.append(template);
      return sb.toString();
    }
  }

  /**
   * Parses the double period path.
   * 
   * @param template the template
   * @return the absolute path
   */
  public String parseDoublePeriodPath(String template) {
    String userDir = System.getProperty("user.dir");
    while (template.length() > 2 && template.substring(0, 2).equals(Constants.DOUBLE_PERIOD)) {
      int lastSlash = userDir.lastIndexOf(Constants.SLASH);
      if (lastSlash != 0) {
        userDir = userDir.substring(0, lastSlash);
      }
      template = template.substring(3);
    }
    return userDir + Constants.SLASH + template;
  }

  /**
   * Creates the platforms.
   *
   * @param config the config
   * @param workflow the workflow
   * @return true, if successful
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public boolean createPlatforms(ClientConfig config, BuildAllPlatforms workflow)
      throws OneOpsClientAPIException {
    List<PlatformBean> platforms = config.getYaml().getPlatformsList();
    Collections.sort(platforms);
    Queue<Integer> queue = new LinkedList<Integer>();
    int prevOrderIdx = platforms.get(0).getDeployOrder();
    for (int i = 0; i < platforms.size(); i++) {
      if (prevOrderIdx == platforms.get(i).getDeployOrder()) {
        queue.add(i);
        workflow.createPlatform(platforms.get(i));
      } else {
        checkPlatformQ(workflow);
        queue.clear();
        prevOrderIdx++;
      }
    }
    return true;
  }

  /**
   * Check platform Q.
   *
   * @param workFlow the work flow
   */
  private void checkPlatformQ(AbstractWorkflow workFlow) {
    while (true) {
      if (!Constants.ACTIVE.equalsIgnoreCase(workFlow.getStatus())) {
        break;
      }
      waitTimeout(30);
    }
  }

  /**
   * Gets the component of compute.
   *
   * @param flow the flow
   * @return the component of compute
   */
  @SuppressWarnings("unchecked")
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


  /**
   * Prints the map.
   *
   * @param map the map
   * @param depth the depth
   */
  @SuppressWarnings("unchecked")
  public static void printMap(Map<String, Object> map, int depth) {
    String log = "Parent";
    if (depth > 0) {
      log = "Children";
    }
    StringBuilder str = new StringBuilder();
    int loop = depth;
    while (loop > 0) {
      str.append('\t');
      loop--;
    }
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      System.out.printf("%s %s: key: %s; value:%s: %n", str.toString(), log, key,
          value == null ? "" : value.getClass());
      if (value instanceof Map) {
        printMap((Map<String, Object>) value, ++depth);
      }

    }
  }
}
