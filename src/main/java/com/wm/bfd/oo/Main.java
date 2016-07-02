package com.wm.bfd.oo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.oo.api.OOInstance;
import com.oo.api.exception.OneOpsClientAPIException;
import com.wm.bfd.oo.exception.BFDOOException;
import com.wm.bfd.oo.utils.BFDUtils;
import com.wm.bfd.oo.workflow.YarnWorkflow;

public class Main {
    final public static String TEMPLATE = "/etc/oneops-tool-bfd/yarn.yaml";
    final public static String IP_OUTPUT = "json";
    private YarnWorkflow flow;
    static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws BFDOOException,
	    OneOpsClientAPIException {
	if (args.length < 2) {
	    System.out
		    .println("Usage: \nboo create yarn -c <template_fullpath> \nboo create yarn "
			    + "\nboo cleanup yarn "
			    + "\nboo getip zookeeper"
			    + "\nboo getip yarn"
			    + "\n-c template_fullpath: If not set, will use the default template: /etc/oneops-tool-bfd/yarn.yaml");
	    System.exit(0);
	}

	String template = TEMPLATE;
	if (args.length > 2) {
	    template = args[3];
	    LOG.debug("Using template file %s", args[3]);
	}
	// Will move over to option handler in future.
	Main main = new Main(template);
	if (args[0].equals("cleanup")) {
	    if (args[1].equals("yarn")) {
		main.cleanupYarn();
	    }
	} else if (args[0].equals("create")) {
	    if (args[1].equals("yarn")) {
		main.createYarn();
	    }
	} else if (args[0].equals("getip")) {
	    if (args[1].equals("zookeeper")) {
		System.out.println(main.getIpZookeeper());
	    } else if (args[1].equals("yarn")) {
		System.out.println(main.getIpYarn());
	    }
	}

    }

    public Main(String template) throws BFDOOException,
	    OneOpsClientAPIException {
	Injector injector = Guice
		.createInjector(new JaywayHttpModule(template));
	ClientConfig config = injector.getInstance(ClientConfig.class);
	new BFDUtils().verifyTemplate(config);
	OOInstance oo = injector.getInstance(OOInstance.class);
	String assemblyName = config.getConfig().getAssembly().getName();
	String platformName = config.getConfig().getPlatforms().getYarn()
		.getName();
	String envName = config.getConfig().getBoo().getEnvName();
	flow = new YarnWorkflow(oo, assemblyName, platformName, envName, config);
    }

    public void createYarn() throws BFDOOException, OneOpsClientAPIException {
	flow.process();
    }

    public void cleanupYarn() throws BFDOOException, OneOpsClientAPIException {
	flow.cleanup();
    }

    public String getIpZookeeper() {
	return flow.getZookeeperIp();
    }
    
    public String getIpYarn() {
	return flow.getYarnIp();
    }

}