package com.wm.bfd.oo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.Singleton;
import com.wm.bfd.oo.yaml.YamlBean;

@Singleton
public class ClientConfig {
    final private static ObjectMapper MAPPER = new ObjectMapper(
	    new YAMLFactory());
    private YamlBean config;
    
    // For add user component in design
    final public static String SSH_KEY = "authorized_keys";
    final public static String USER_NAME = "username";
    final public static String USER = "user";
    
    // Platform yarn variables
    final public static String ZK_HOST = "zk_hosts";
    
    
    // Compute size
    final public static String SIZE = "size";
    
    final public static String COMPUTE = "compute";

    public ClientConfig(String file) throws JsonParseException,
	    JsonMappingException, FileNotFoundException, IOException {
	System.out.println("ClientConfig initializing...");
	this.config = MAPPER.readValue(new FileInputStream(new File(file)),
		YamlBean.class);
	MAPPER.readValue(new FileInputStream(new File(file)),
		YamlBean.class);
    }

    public YamlBean getConfig() {
	return config;
    }

}
