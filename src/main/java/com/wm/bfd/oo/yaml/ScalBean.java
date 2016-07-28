package com.wm.bfd.oo.yaml;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ScalBean {

  @JsonProperty("current")
  private int current;
  private int max;
  private int min;
  private String platform;
  private int stepUp;
  private int stepDown;
  private int percentDeploy;

  @JsonIgnore
  private String component;

  public int getStepUp() {
    return stepUp;
  }

  public int getStepDown() {
    return stepDown;
  }

  public int getPercentDeploy() {
    return percentDeploy;
  }



  public ScalBean(ScalBeanBuilder builder) {
    this.component = builder.component;
    this.platform = builder.platform;
    this.current = builder.current;
    this.max = builder.max;
    this.min = builder.min;
    this.stepUp = builder.stepUp;
    this.stepDown = builder.stepDown;
    this.percentDeploy = builder.percentDeploy;
  }

  public int getCurrent() {
    return current;
  }

  public int getMax() {
    return max;
  }

  public int getMin() {
    return min;
  }

  public String getPlatform() {
    return platform;
  }

  @JsonIgnore
  public String getComponent() {
    return component;
  }

  public static class ScalBeanBuilder {
    private int current = 2;
    private int max = 10;
    private int min = 2;
    private String platform;
    private int stepUp = 1;
    private int stepDown = 1;
    private int percentDeploy = 100;
    private String component;

    public ScalBeanBuilder() {

    }

    public ScalBean build() {
      return new ScalBean(this);
    }

    public ScalBeanBuilder setComponent(String component) {
      this.component = component;
      return this;
    }
    
    public ScalBeanBuilder setStepUp(String stepUp) {
      if (stepUp != null)
        this.stepUp = Integer.parseInt(stepUp);
      return this;
    }

    public ScalBeanBuilder setPercentDeploy(String percentDeploy) {
      if (percentDeploy != null)
        this.percentDeploy = Integer.parseInt(percentDeploy);
      return this;
    }

    public ScalBeanBuilder setStepDown(String stepDown) {
      if (stepDown != null)
        this.stepDown = Integer.parseInt(stepDown);
      return this;
    }

    public ScalBeanBuilder setCurrent(String current) {
      if (current != null)
        this.current = Integer.parseInt(current);
      return this;
    }

    public ScalBeanBuilder setMax(String max) {
      if (max != null)
        this.max = Integer.parseInt(max);
      return this;
    }

    public ScalBeanBuilder setMin(String min) {
      if (min != null)
        this.min = Integer.parseInt(min);
      return this;
    }

    public ScalBeanBuilder setPlatform(String platform) {
      if (platform != null)
        this.platform = platform;
      return this;
    }

  }
}
