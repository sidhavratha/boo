/*
 * Copyright 2017 Walmart, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.oneops.boo.yaml;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Scale options in OneOps.
 */
public class ScaleBean {

  /** The current. */
  @JsonProperty("current")
  private int current;

  /** The max. */
  private int max;

  /** The min. */
  private int min;

  /** The platform. */
  private String platform;

  /** The step up. */
  private int stepUp;

  /** The step down. */
  private int stepDown;

  /** The percent deploy. */
  private int percentDeploy;

  /** The component. */
  @JsonIgnore
  private String component;

  /**
   * Gets the step up.
   *
   * @return the step up
   */
  public int getStepUp() {
    return stepUp;
  }

  /**
   * Gets the step down.
   *
   * @return the step down
   */
  public int getStepDown() {
    return stepDown;
  }

  /**
   * Gets the percent deploy.
   *
   * @return the percent deploy
   */
  public int getPercentDeploy() {
    return percentDeploy;
  }



  /**
   * Instantiates a new scal bean.
   *
   * @param builder the builder
   */
  public ScaleBean(ScalBeanBuilder builder) {
    this.component = builder.component;
    this.platform = builder.platform;
    this.current = builder.current;
    this.max = builder.max;
    this.min = builder.min;
    this.stepUp = builder.stepUp;
    this.stepDown = builder.stepDown;
    this.percentDeploy = builder.percentDeploy;
  }

  /**
   * Gets the current.
   *
   * @return the current
   */
  public int getCurrent() {
    return current;
  }

  /**
   * Gets the max.
   *
   * @return the max
   */
  public int getMax() {
    return max;
  }

  /**
   * Gets the min.
   *
   * @return the min
   */
  public int getMin() {
    return min;
  }

  /**
   * Gets the platform.
   *
   * @return the platform
   */
  public String getPlatform() {
    return platform;
  }

  /**
   * Gets the component.
   *
   * @return the component
   */
  @JsonIgnore
  public String getComponent() {
    return component;
  }

  /**
   * The Class ScalBeanBuilder.
   */
  public static class ScalBeanBuilder {

    /** The current. */
    private int current = 2;

    /** The max. */
    private int max = 10;

    /** The min. */
    private int min = 2;

    /** The platform. */
    private String platform;

    /** The step up. */
    private int stepUp = 1;

    /** The step down. */
    private int stepDown = 1;

    /** The percent deploy. */
    private int percentDeploy = 100;

    /** The component. */
    private String component;

    /**
     * Instantiates a new scal bean builder.
     */
    public ScalBeanBuilder() {

    }

    /**
     * Builds the.
     *
     * @return the scal bean
     */
    public ScaleBean build() {
      return new ScaleBean(this);
    }

    /**
     * Sets the component.
     *
     * @param component the component
     * @return the scal bean builder
     */
    public ScalBeanBuilder setComponent(String component) {
      this.component = component;
      return this;
    }

    /**
     * Sets the step up.
     *
     * @param stepUp the step up
     * @return the scal bean builder
     */
    public ScalBeanBuilder setStepUp(String stepUp) {
      if (stepUp != null) {
        this.stepUp = Integer.parseInt(stepUp);
      }
      return this;
    }

    /**
     * Sets the percent deploy.
     *
     * @param percentDeploy the percent deploy
     * @return the scal bean builder
     */
    public ScalBeanBuilder setPercentDeploy(String percentDeploy) {
      if (percentDeploy != null) {
        this.percentDeploy = Integer.parseInt(percentDeploy);
      }
      return this;
    }

    /**
     * Sets the step down.
     *
     * @param stepDown the step down
     * @return the scal bean builder
     */
    public ScalBeanBuilder setStepDown(String stepDown) {
      if (stepDown != null) {
        this.stepDown = Integer.parseInt(stepDown);
      }
      return this;
    }

    /**
     * Sets the current.
     *
     * @param current the current
     * @return the scal bean builder
     */
    public ScalBeanBuilder setCurrent(String current) {
      if (current != null) {
        this.current = Integer.parseInt(current);
      }
      return this;
    }

    /**
     * Sets the max.
     *
     * @param max the max
     * @return the scal bean builder
     */
    public ScalBeanBuilder setMax(String max) {
      if (max != null) {
        this.max = Integer.parseInt(max);
      }
      return this;
    }

    /**
     * Sets the min.
     *
     * @param min the min
     * @return the scal bean builder
     */
    public ScalBeanBuilder setMin(String min) {
      if (min != null) {
        this.min = Integer.parseInt(min);
      }
      return this;
    }

    /**
     * Sets the platform.
     *
     * @param platform the platform
     * @return the scal bean builder
     */
    public ScalBeanBuilder setPlatform(String platform) {
      if (platform != null) {
        this.platform = platform;
      }
      return this;
    }

  }
}
