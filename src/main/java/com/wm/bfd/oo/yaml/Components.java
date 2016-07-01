package com.wm.bfd.oo.yaml;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Components {
    @JsonProperty("compute/Compute")
    private Computes compute;

    @JsonProperty("user/User")
    private List<Users> users;

    public List<Users> getUsers() {
	return users;
    }

    public void setUsers(List<Users> users) {
	this.users = users;
    }

    public String getComputeSize() {
	return compute.getCompute().getSize();
    }

    public void setCompute(Computes compute) {
	this.compute = compute;
    }

}

class Computes {
    private Compute compute;

    public Compute getCompute() {
	return compute;
    }

    public void setCompute(Compute compute) {
	this.compute = compute;
    }
}

class Compute {

    private String size;

    public String getSize() {
	return size;
    }

    public void setSize(String size) {
	this.size = size;
    }

}
