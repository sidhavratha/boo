package com.wm.bfd.oo.yaml;

public class PlatformsBean {
    private YarnBean yarn;
    private ZookeeperBean zookeeper;

    public YarnBean getYarn() {
	return yarn;
    }

    public void setYarn(YarnBean yarn) {
	this.yarn = yarn;
    }

    public ZookeeperBean getZookeeper() {
	return zookeeper;
    }

    public void setZookeeper(ZookeeperBean zookeeper) {
	this.zookeeper = zookeeper;
    }
}
