package com.wm.bfd.oo.yaml;

/**
 * Platform variables in OneOps.
 */
public class PlatformConfigBean {

  /** The platform name. */
  private String platformName;

  /** The custom format. */
  private String customFormat;

  /** The custom split. */
  private String customSplit;

  /** The custom component. */
  private String customComponent;

  /**
   * Instantiates a new platform config bean.
   *
   * @param platformName the platform name
   * @param customFormat the custom format
   * @param customSplit the custom split
   * @param customComponent the custom component
   */
  public PlatformConfigBean(String platformName, String customFormat, String customSplit, String customComponent) {
    this.platformName = platformName;
    this.customFormat = customFormat;
    this.customSplit = customSplit;
    this.customComponent = customComponent;
  }

  /**
   * Gets the custom format.
   *
   * @return the custom format
   */
  public String getCustomFormat() {
    return customFormat;
  }

  /**
   * Sets the custom format.
   *
   * @param customFormat the new custom format
   */
  public void setCustomFormat(String customFormat) {
    this.customFormat = customFormat;
  }

  /**
   * Gets the custom split.
   *
   * @return the custom split
   */
  public String getCustomSplit() {
    return customSplit;
  }

  /**
   * Sets the custom split.
   *
   * @param customSplit the new custom split
   */
  public void setCustomSplit(String customSplit) {
    this.customSplit = customSplit;
  }

  /**
   * Gets the custom component.
   *
   * @return the custom component
   */
  public String getCustomComponent() {
    return customComponent;
  }

  /**
   * Sets the custom component.
   *
   * @param customComponent the new custom component
   */
  public void setCustomComponent(String customComponent) {
    this.customComponent = customComponent;
  }

  /**
   * Gets the platform name.
   *
   * @return the platform name
   */
  public String getPlatformName() {
    return platformName;
  }

  /**
   * Sets the platform name.
   *
   * @param platformName the new platform name
   */
  public void setPlatformName(String platformName) {
    this.platformName = platformName;
  }
}
