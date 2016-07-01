package com.wm.bfd.oo.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScalBean {

    @JsonProperty("current")
    private int current;
    private int max;
    private int min;

    public int getCurrent() {
	return current;
    }

    public void setCurrent(int current) {
	this.current = current;
    }

    public int getMax() {
	return max;
    }

    public void setMax(int max) {
	this.max = max;
    }

    public int getMin() {
	return min;
    }

    public void setMin(int min) {
	this.min = min;
    }

}
