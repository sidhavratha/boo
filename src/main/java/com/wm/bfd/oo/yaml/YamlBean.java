package com.wm.bfd.oo.yaml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"variables"})
public class YamlBean {
    private AssemblyBean assembly;
    private PlatformsBean platforms;
    private BooBean boo;

    public AssemblyBean getAssembly() {
	return assembly;
    }

    public void setAssembly(AssemblyBean assembly) {
	this.assembly = assembly;
    }

    public PlatformsBean getPlatforms() {
	return platforms;
    }

    public void setPlatforms(PlatformsBean platforms) {
	this.platforms = platforms;
    }

    public BooBean getBoo() {
	return boo;
    }

    public void setBoo(BooBean boo) {
	this.boo = boo;
    }
}
