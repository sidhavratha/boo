package com.wm.bfd.test;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.junit.Test;

import com.google.common.util.concurrent.Uninterruptibles;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.oo.api.OOInstance;
import com.oo.api.exception.OneOpsClientAPIException;
import com.oo.api.resource.Assembly;
import com.oo.api.resource.Design;
import com.oo.api.resource.Operation;
import com.oo.api.resource.Transition;
import com.oo.api.resource.model.RedundancyConfig;


public class CreateYarnPackTest extends TestCase {
	final private String EMAIL = "lzhang@walmartlabs.com";
	static OOInstance  instance = new OOInstance();
	static Assembly assembly;
	static Design design;
	static Transition transition;
	static Operation op;

	private static String assemblyName;
	private static String platformName;
	private static String envName;
	
	static void init() throws OneOpsClientAPIException {
		if (design == null) {
			System.out.println("Set up...");
			//instance.setAuthtoken("XY7U4RsBn2S2KUgxbjWP"); // bfd
			//
			instance.setAuthtoken("oxsHNFWgzNmkJs5j-hLF"); // web.dev
			instance.setOrgname("bfd");
			//instance.setEndpoint("https://web.bfd.dev.cloud.wal-mart.com/");
			instance.setEndpoint("https://web.dev.oneops.walmart.com/");
			
			RestAssured.useRelaxedHTTPSValidation(); // Disable SSL check.

			assemblyName = "ray-yarn-test";
			platformName = "yarn";
			envName = "yarn-test";
			assembly = new Assembly(instance);
			design = new Design(instance, assemblyName);
			transition = new Transition(instance, assemblyName);
			op = new Operation(instance, assemblyName, envName);
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		init();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		//this.cleanUp();
	}

	/**
	 * Cancel an existing deployment.
	 * 
	 * @throws OneOpsClientAPIException
	 */
	void cancelDeployment() {
		try {
			JsonPath response = transition.getLatestDeployment(envName);
			String deploymentId = response.getString("deploymentId");
			response = transition.getLatestRelease(envName);
			String releaseId = response.getString("releaseId");
			System.out.println("deploymentId:" + deploymentId + "; releaseId: "
					+ releaseId);
			response = transition.getDeploymentStatus(envName, deploymentId);
			Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
			response = transition.cancelDeployment(envName, deploymentId,
					releaseId);

			System.out.println("cancelDeployment: "
					+ (response == null ? "" : response.prettyPrint()));
		} catch (Exception e) {
			// Ignore
		}

	}

	void disableAllPlatforms() {
		try {
			transition.disableAllPlatforms(envName);
			transition.commitEnvironment(envName, null, "Clean up " + envName);
			transition.deploy(envName, "test deploy!");
		} catch (Exception e) {
			// Ignore
		}
	}

	void deleteDesign() {
		JsonPath response = null;
		try {
			System.out.println("deleteEnvironment log:"
					+ (response == null ? "" : response.prettyPrint()));
			response = design.commitDesign();
			System.out.println("commitDesign log:"
					+ (response == null ? "" : response.prettyPrint()));
			design.deletePlatform(platformName);
		} catch (Exception e) {
			// Ignore
			e.printStackTrace();
		}
	}

	void cleanUp() {
		if (design == null) return;
		System.out.println("Clean up...");
		this.cancelDeployment();
		this.disableAllPlatforms();

		try {
			transition.deleteEnvironment(envName);
		} catch (Exception e) {
			// Ignore
			e.printStackTrace();
		}
		this.deleteDesign();
		// Don't add the following part to one try block as transition.
		try {

			assembly.deleteAssembly(assemblyName);
		} catch (Exception e) {
			// Ignore
		}
		op = null;
		design = null;
		assembly = null;
	}

	@Test
	public void testACreateAssembly() throws OneOpsClientAPIException {
		JsonPath response = assembly
				.createAssembly(assemblyName, EMAIL, "", "");
		System.out.println("createAssembly:" + response.prettyPrint());
		System.out.println(response.getString("ciId"));
	}

	@Test
	public void testBCreatePlatform() throws OneOpsClientAPIException {
		design = new Design(instance, assemblyName);
		JsonPath response = design.createPlatform(platformName, "hadoop_yarn_vrc3", "1",
				"oneops", "", "");
		System.out.println("createPlatform:" + response.prettyPrint());
		response = design.listPlatforms();
		System.out.println("listPlatforms:" + response.prettyPrint());
		response = design.commitDesign();
	}

	@Test
	public void testCAddVariables() throws OneOpsClientAPIException {
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("swift_password", "swbfd97109");
		variables.put("hive_db_password", "xYPmCPqbjb8c3Y");
		
		design.updatePlatformVariable(platformName, variables, true);
		
		variables.clear();
		variables.put("additional_libraries", "http://cdc-chef00.bfd.walmart.com:8080/chef_artifacts/hadoop/latest/hadoop-openstack-3.0.0-SNAPSHOT.jar");
		variables.put("yarn_tarball", "http://cdc-chef00.bfd.walmart.com:8080/chef_artifacts/hadoop/latest/hadoop-2.7.2.tar.gz");
		variables.put("swift_username", "sw_bfd");
		variables.put("swift_url", "http://cdc-prd001-api-endpoint.ost.cloud.wal-mart.com:5000/v2.0/tokens");
		variables.put("swift_tenant", "sw_bfd");
		variables.put("s3_endpoint", "cdc-rgw.ost.cloud.wal-mart.com");
		variables.put("hive_connect_url", "jdbc:mysql://db.sandbox.metastore.bfd.prod.cloud.wal-mart.com:3306/bfd_exp_hive?createDatabaseIfNotExist=false");
		variables.put("hive_tarball_url", "http://cdc-chef00.bfd.walmart.com:8080/chef_artifacts/hive/apache-hive-1.2.1-bin.tar.gz");
		//variables.put("zk_hosts", "10.227.67.157:2181,10.227.67.158:2181,10.227.67.180:2181");
		variables.put("zk_host", "10.226.114.132:2181,10.226.114.133:2181,10.226.114.131:2181");

		design.updatePlatformVariable(platformName, variables, false);
		
		Map<String, String> attributes1 = new HashMap<String, String>();
		attributes1.put("size", "XXL");
		design.updatePlatformComponent(platformName, "compute", attributes1);
		
		
		//Add user
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("system_account", "true");
		attributes.put("sudoer", "true");
		attributes.put("login_shell", "/bin/bash");
		attributes.put("ulimit", "16384");
		attributes.put("home_directory_mode", "755");
		attributes.put("username", "rzhan33");
		attributes.put("authorized_keys", "[\"ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQDR01CQ1iLSMI+vHUFUfqEesflosSJE9JOQLXA0vaY1Y7ozvGrJPL342BMOvkQWT7rMHT0XVXm0c9K3kCfaLPrLZbJ4pxkSVZpbQN7KtGdMWlPF83k9+u+K7SpJTKOiaTTKJW2YfvGiRFMgFoZJBql1ZESShzYluGgympzQGT2AFMiMg8NY5D7DtK2invrXS4X0F3DpnfCgg5BDP1hUVO1sKWppnULwfzfjDwiqv2hvT/1ZBq+5ZkBD+N9KOq7jEZW4QM9SPnEeT0KQRgymgqplMBZGUJJpHTu7L/fDMVH/O4FMmLItnhA+4+KVK/EfL3/EkVwvY6Pg3Qs/dVXz+ttB8iYoszfES5RPvb+Fwg/ZZHsQOz5xc/bq+S7cPcWBLI+6svrZOY7WyLMxJfCPb6FThj5nJpA5eRPKdEXWndyHWX4Sq+G8IQtOXc/RDWuBNNwqZUISS5M8EsbegXB1ywLmjK6eETmgfEzfIAdZKMGlvtzlLU1KT5n+FHvPX0ddr5m2c4UT3p5HgaptWL4qgUd8bx6EdSm5pAt2NEPfynmiQKiYLmWyH3cHNXv02LodxeGfWb/JSHDBQItu3kpaXPLOdOKhQltdRPhmZlS8si0z3ZCxaEEyAObDVgv7Lmp1cqHfd9N//3oOTInW5IqmqCqaQhmXc6NnOdrpmDRwqAmtVw== lzhang@walmartlabs.com\"]");
		//design.addPlatformComponent(platformName, "user", "rzhan33", attributes);
		//design.updatePlatformComponent(platformName, "user", attributes);
		
		//design.updatePlatformComponent(platformName, "", attributes)
		//design.addGlobalVariable(variables, true);
		
		design.commitDesign();
	}

	@Test
	public void testDCreateEnv() throws OneOpsClientAPIException {
		
		Map<String, String> cloudMap = new HashMap<String, String>();
		//cloudMap.put("318450", "1"); // cdc-dev1
		cloudMap.put("22915862", "1"); //qa-cdc1a
		JsonPath response = transition.createEnvironment(envName, "DEV",
				"redundant", null, cloudMap, false, true, "");
		response = transition.getEnvironment(envName);
		System.out.println(response.getString("ciName"));
		
		
		
		this.getPlatforms();
		System.out.println("Before commit...");
		List<Integer> excludePlatforms = new ArrayList<Integer>();
		excludePlatforms.add(1554494);
		Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
		transition.commitEnvironment(envName, null, "bfdoo tool test commit!");
		System.out.println("After commit...");
		Uninterruptibles.sleepUninterruptibly(20, TimeUnit.SECONDS);
		response = transition.deploy(envName, "bfdoo tool test deploy!");
	}
	
	@Test
	public void testGUpdateVariablesInTransition() throws OneOpsClientAPIException {
	 // Test update scaling size
	    RedundancyConfig config = new RedundancyConfig();
            config.setMax(5);
            config.setMin(5);
            transition.updatePlatformRedundancyConfig(envName, platformName, config );
	}
	
	void getPlatforms() {
		JsonPath response = null;
		try {
			response = transition.listPlatforms(envName);
			System.out.println( "[getPlatforms]>>" + response.prettyPrint() + response.getString("ciId").getClass());
			System.out.println( "[getPlatforms]>>" + response.getString("ciId"));
		} catch (OneOpsClientAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Test
	public void testFGetInstance() throws OneOpsClientAPIException {
		///assemblies/wes/operations/environments/test/platforms/zookeeper/components/compute/instances
		JsonPath response = op.listInstances(platformName, "compute");
		List<Map> ips = response.getList("ciAttributes");
		System.out.println("testGetInstance:" + response.prettyPrint());
		for (Map ip : ips) {
			System.out.println("ip:"
					+ (response == null ? "" : ip.get("public_ip")));
	}

    }

}
