package com.wm.bfd.oo.yaml;

/**
 * 
 * @author rzhan33
 *
 */
public class PlatformConfigBean {

  private String platformName;
  private String customFormat;
  private String customSplit;
  private String customComponent;

  public PlatformConfigBean(String platformName, String customFormat, String customSplit,
      String customComponent) {
    this.platformName = platformName;
    this.customFormat = customFormat;
    this.customSplit = customSplit;
    this.customComponent = customComponent;
  }

  public String getCustomFormat() {
    return customFormat;
  }

  public void setCustomFormat(String customFormat) {
    this.customFormat = customFormat;
  }

  public String getCustomSplit() {
    return customSplit;
  }

  public void setCustomSplit(String customSplit) {
    this.customSplit = customSplit;
  }

  public String getCustomComponent() {
    return customComponent;
  }

  public void setCustomComponent(String customComponent) {
    this.customComponent = customComponent;
  }

  public String getPlatformName() {
    return platformName;
  }

  public void setPlatformName(String platformName) {
    this.platformName = platformName;
  }
}
