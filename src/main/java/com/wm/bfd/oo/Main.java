package com.wm.bfd.oo;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.oo.api.OOInstance;
import com.oo.api.exception.OneOpsClientAPIException;
import com.wm.bfd.oo.exception.BFDOOException;
import com.wm.bfd.oo.utils.BFDUtils;
import com.wm.bfd.oo.workflow.YarnWorkflow;

public class Main {
    final public static String TEMPLATE = "/etc/oneops-tool-bfd/yarn.yaml";

    public static void main(String[] args) throws BFDOOException,
	    OneOpsClientAPIException {
	if (args.length < 2) {
	    System.out.println("Usage: boo create yarn -c <template_fullpath> \nor boo create yarn \nDefault template: /etc/oneops-tool-bfd/yarn.yaml");
	    System.exit(0);
	}

	Main main = new Main();
	String template = TEMPLATE;
	if (args.length > 2) {
	    template = args[3];
	}
	if (args[0].equals("create")) {
	    if (args[1].equals("yarn")) {
		main.createYarn(template);
	    }
	}

    }

    public void createYarn(String template) throws BFDOOException,
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
	YarnWorkflow flow = new YarnWorkflow(oo, assemblyName, platformName,
		envName, config);
	flow.process();
    }

}