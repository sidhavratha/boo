package com.wm.bfd.oo;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jayway.restassured.RestAssured;
import com.oo.api.OOInstance;
import com.oo.api.exception.OneOpsClientAPIException;
import com.wm.bfd.oo.exception.BFDOOException;
import com.wm.bfd.oo.utils.BFDUtils;
import com.wm.bfd.oo.workflow.BuildAllPlatforms;
import com.wm.bfd.oo.yaml.Constants;

public class BooCli {
  final private static Logger LOG = LoggerFactory.getLogger(BooCli.class);

  private BuildAllPlatforms flow;
  private String[] args = null;
  private Options options = new Options();

  public BooCli(String[] args) {
    this.args = args;
    Option help = new Option("h", "help", false, "show help.");
    Option create = Option.builder("create").desc("Create a new deployment in OneOps.").build();
    Option status = Option.builder("s").longOpt("status").desc("Check deployment status.").build();

    Option config =
        Option.builder("cf").longOpt("config_file").argName("yaml").hasArg()
            .desc("Use specified template.").build();

    Option cleanup = Option.builder("cleanup").desc("Remove all deployment in OneOps.").build();

    options.addOption(help);
    options.addOption(config);
    options.addOption(create);
    options.addOption(status);
    options.addOption(cleanup);
  }

  static {
    RestAssured.useRelaxedHTTPSValidation();
  }

  public void init(String template) throws BFDOOException, OneOpsClientAPIException {
    LOG.info("Loading {}", template);
    Injector injector = Guice.createInjector(new JaywayHttpModule(template));
    ClientConfig config = injector.getInstance(ClientConfig.class);
    new BFDUtils().verifyTemplate(config);
    OOInstance oo = injector.getInstance(OOInstance.class);
    flow = new BuildAllPlatforms(oo, config);
  }

  public void parse() throws ParseException, BFDOOException, OneOpsClientAPIException {
    CommandLineParser parser = new DefaultParser();
    // CommandLineParser parser = new GnuParser();
    CommandLine cmd = parser.parse(options, args);


    if (cmd.hasOption("h")) {
      this.help(null, Constants.BFD_TOOL);
      System.exit(0);
    }

    if (cmd.hasOption("cf")) {
      this.init(cmd.getOptionValue("cf"));
    } else {
      this.help(null, "No YAML file specified");
      System.exit(-1);
    }

    if (cmd.hasOption("s")) {
      System.out.println(this.getStatus());
    } else if (cmd.hasOption("create")) {
      this.createPacks();
    } else if (cmd.hasOption("cleanup")) {
      this.cleanup();
    }
  }

  private void help(String header, String footer) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("boo", header, options, footer, true);
  }

  private void helpOld() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("boo", options);
  }

  public void createPacks() throws BFDOOException, OneOpsClientAPIException {
    flow.process();
  }

  public void cleanup() throws BFDOOException, OneOpsClientAPIException {
    flow.cleanup();
  }

  public String getStatus() throws BFDOOException, OneOpsClientAPIException {
    return flow.getStatus();
  }

}
