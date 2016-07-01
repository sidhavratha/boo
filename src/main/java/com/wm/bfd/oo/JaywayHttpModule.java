package com.wm.bfd.oo;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.oo.api.OOInstance;

public class JaywayHttpModule extends AbstractModule {

    final private static String file = "/Users/rzhan33/Documents/Ray/Projects/Swifthadoop/oneops-tool-bfd/resource/yarn.yaml";
    private static ClientConfig CLIENT = null;
    
    @Override
    protected void configure() {
	// bind(ClientConfig.class);
	// bind(OOInstance.class);
    }

    // void useBfdDev(ClientConfig client) {
    // client.setApikey("XY7U4RsBn2S2KUgxbjWP");// bfd.dev
    // client.setHost("https://web.bfd.dev.cloud.wal-mart.com/");
    // client.setCloudId("1441188");// bfd.dev
    // }
    //
    // void useWebDev(ClientConfig client) {
    // client.setCloudId("22915862");
    // client.setApikey("oxsHNFWgzNmkJs5j-hLF"); // web.dev
    // client.setHost("https://web.dev.oneops.walmart.com/");
    // }

    @Provides
    @Singleton
    ClientConfig getClientConfig() throws JsonParseException,
	    JsonMappingException, FileNotFoundException, IOException {
	// test
	if (CLIENT == null)
	    CLIENT = new ClientConfig(file);
	return CLIENT;
    }

    @Provides
    OOInstance getOOInstance() throws JsonParseException, JsonMappingException,
	    FileNotFoundException, IOException {
	OOInstance instance = new OOInstance();
	ClientConfig client = this.getClientConfig();
	instance.setAuthtoken(client.getConfig().getBoo().getApikey());
	instance.setOrgname(client.getConfig().getBoo().getOrg());
	instance.setEndpoint(client.getConfig().getBoo().getHost());
	return instance;
    }

}
