package com.wm.bfd.oo;

import com.wm.bfd.oo.exception.BfdOoException;
import com.wm.bfd.oo.utils.BfdUtils;
import com.wm.bfd.oo.workflow.BuildAllPlatforms;
import com.wm.bfd.oo.yaml.Constants;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jayway.restassured.RestAssured;
import com.oo.api.OOInstance;
import com.oo.api.exception.OneOpsClientAPIException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

/**
 * The Class BooCli.
 */
public class BooCli {

  /** The Constant LOG. */
  private static final Logger LOG = LoggerFactory.getLogger(BooCli.class);

  /** The is quiet. */
  private static boolean isQuiet = false;

  /** The is forced. */
  private static boolean isForced = false;

  /** The is no deploy. */
  private static boolean isNoDeploy = false;

  /** The Constant YES_NO. */
  private static final String YES_NO =
      "WARNING! There are %s instances using the %s configuration. Do you want to destroy all of them? (y/n)";


  /** The config file. */
  private String configFile;

  /** The flow. */
  private BuildAllPlatforms flow;

  /** The options. */
  private Options options = new Options();

  /** The config. */
  private ClientConfig config;

  /** The injector. */
  private Injector injector;

  /** The bfd utils. */
  private BfdUtils bfdUtils = new BfdUtils();

  private String comment = null;

  /**
   * Instantiates a new boo cli.
   */
  public BooCli() {
    Option help = new Option("h", "help", false, "show help.");
    Option create = Option.builder("c").longOpt("create")
        .desc(
            "Create a new Assembly specified by -d or -f. If Assembly automatic naming is enabled, each invocation will create a new Assembly.")
        .build();
    Option update = Option.builder("u").longOpt("update")
        .desc("Update configurations specified by -d or -f.").build();
    Option status = Option.builder("s").longOpt("status")
        .desc("Get status of deployments specified by -d or -f").build();

    Option config = Option.builder("f").longOpt("config-file").argName("FILE").hasArg()
        .desc("Use specified configuration file, required if -d not used").build();

    Option cleanup = Option.builder("r").longOpt("remove")
        .desc("Remove all deployed configurations specified by -d or -f").build();
    Option list = Option.builder("l").longOpt("list").numberOfArgs(1).optionalArg(Boolean.TRUE)
        .desc("Return a list of instances applicable to the identifier provided..").build();

    Option force = Option.builder().longOpt("force").desc("Do not prompt for --remove").build();

    Option nodeploy =
        Option.builder().longOpt("no-deploy").desc("Create assembly without deployments").build();

    Option getIps = Option.builder().longOpt("get-ips").argName("environment> <compute-class")
        .desc("Get IPs of deployed nodes specified by -d or -f; Args are optional.").build();
    getIps.setOptionalArg(true);
    getIps.setArgs(Option.UNLIMITED_VALUES);

    Option retry = Option.builder().longOpt("retry")
        .desc("Retry deployments of configurations specified by -d or -f").build();
    Option quiet = Option.builder().longOpt("quiet").desc("Silence the textual output.").build();
    Option assembly = Option.builder("a").longOpt("assembly").hasArg()
        .desc("Override the assembly name.").build();
    Option action = Option.builder().longOpt("procedure").numberOfArgs(3).optionalArg(Boolean.TRUE)
        .argName("platform> <component> <action")
        .desc("Execute actions. Use 'list' as an action to show available actions.").build();
    Option procedureArguments =
        Option.builder().longOpt("procedure-arguments").argName("arglist").hasArg()
            .desc(
                "Arguments to pass to the procedure call. Example: '{\"backup_type\":\"incremental\"}'")
            .build();
    Option instanceList =
        Option.builder().longOpt("procedure-instances").argName("instanceList").hasArg()
            .desc(
                "Comma-separated list of component instance names. 'list' to show all available component instances.")
            .build();

    Option stepSize = Option.builder().longOpt("procedure-step-size").argName("size").hasArg()
        .desc("Percent of nodes to perform procedure on, default is 100.").build();
    Option comment = Option.builder("m").longOpt("message").argName("description").hasArg()
        .desc("Customize the comment for deployments").build();
    options.addOption(help);
    options.addOption(config);
    options.addOption(create);
    options.addOption(update);
    options.addOption(status);
    options.addOption(list);
    options.addOption(cleanup);
    options.addOption(getIps);
    options.addOption(retry);
    options.addOption(quiet);
    options.addOption(force);
    options.addOption(nodeploy);
    options.addOption(assembly);
    options.addOption(action);
    options.addOption(procedureArguments);
    options.addOption(instanceList);
    options.addOption(stepSize);
    options.addOption(comment);
  }

  static {
    RestAssured.useRelaxedHTTPSValidation();
  }

  /**
   * Inits the YAML template.
   *
   * @param template the template
   * @param assembly the assembly
   * @throws BfdOoException the BFDOO exception
   */
  public void init(String template, String assembly) throws BfdOoException {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Loading {}", template);
    }

    injector = Guice.createInjector(new JaywayHttpModule(this.configFile));
    config = injector.getInstance(ClientConfig.class);
    bfdUtils.verifyTemplate(config);
    if (assembly != null) {
      config.getYaml().getAssembly().setName(assembly);
    }
  }

  /**
   * Inits the OO.
   *
   * @param config the config
   * @param assembly the assembly
   */
  public void initOo(ClientConfig config, String assembly, String comment) {
    OOInstance oo = injector.getInstance(OOInstance.class);
    try {
      if (assembly != null) {
        config.getYaml().getAssembly().setName(assembly);
      }
      flow = new BuildAllPlatforms(oo, config, comment);
    } catch (OneOpsClientAPIException e) {
      System.err.println("Init failed! Quit!");
    }
  }

  /**
   * Parse user's input.
   *
   * @param arg the arg
   * @throws ParseException the parse exception
   * @throws BfdOoException the BFDOO exception
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public int parse(String[] arg) throws ParseException, BfdOoException, OneOpsClientAPIException {
    CommandLineParser parser = new DefaultParser();
    int exit = 0;
    // CommandLineParser parser = new GnuParser();
    try {

      String assembly = null;
      CommandLine cmd = parser.parse(options, arg);
      /**
       * Handle command without configuration file dependency first.
       */
      if (cmd.hasOption("h")) {
        this.help(null, Constants.BFD_TOOL);
        return exit;
      }

      if (cmd.hasOption("quiet")) {
        BooCli.setQuiet(Boolean.TRUE);
      }

      if (cmd.hasOption("force")) {
        BooCli.setForced(Boolean.TRUE);
      }
      if (cmd.hasOption("no-deploy")) {
        BooCli.setNoDeploy(Boolean.TRUE);
      }

      if (cmd.hasOption("a")) {
        assembly = cmd.getOptionValue("a");
      }
      /**
       * Get configuration dir or file.
       */
      if (cmd.hasOption("f")) {
        this.configFile = cmd.getOptionValue("f");
        this.configFile = bfdUtils.getAbsolutePath(configFile);
        System.out.printf(Constants.CONFIG_FILE, this.configFile);
        System.out.println();
      }


      if (this.configFile == null) {
        this.help(null, "No YAML file found.");
        return Constants.EXIT_TWO;
      }

      if (cmd.hasOption("m")) {
        this.comment = cmd.getOptionValue("m");
      }

      this.init(this.configFile, assembly);
      this.initOo(config, null, comment);

      if (cmd.hasOption("l")) {
        String prefix = cmd.getOptionValue("l");
        if (prefix == null) {
          this.listFiles(config.getYaml().getAssembly().getName());
        } else {
          this.listFiles(prefix.trim());
        }
        return Constants.EXIT_ZERO;
      }
      /**
       * Handle other commands.
       */
      if (cmd.hasOption("s")) {
        if (!flow.isAssemblyExist()) {
          System.err.printf(Constants.NOTFOUND_ERROR, config.getYaml().getAssembly().getName());
        } else {
          System.out.println(this.getStatus());
        }
      } else if (cmd.hasOption("c")) {
        if (config.getYaml().getAssembly().getAutoGen()) {
          this.initOo(this.config,
              this.autoGenAssemblyName(config.getYaml().getAssembly().getAutoGen(),
                  config.getYaml().getAssembly().getName()),
              comment);
          LogUtils.info(Constants.CREATING_ASSEMBLY, config.getYaml().getAssembly().getName());
        }
        this.createPacks(Boolean.FALSE, isNoDeploy);
      } else if (cmd.hasOption("u")) {
        if (!config.getYaml().getAssembly().getAutoGen()) {
          if (flow.isAssemblyExist()) {
            this.createPacks(Boolean.TRUE, isNoDeploy);
          } else {
            System.err.printf(Constants.NOTFOUND_ERROR, config.getYaml().getAssembly().getName());
          }
        } else {
          List<String> assemblies = this.listFiles(this.config.getYaml().getAssembly().getName());
          for (String asm : assemblies) {
            this.initOo(config, asm, comment);
            this.createPacks(Boolean.TRUE, isNoDeploy);
          }
        }
      } else if (cmd.hasOption("r")) {
        List<String> assemblies;
        if (config.getYaml().getAssembly().getAutoGen()) {
          assemblies = this.listFiles(this.config.getYaml().getAssembly().getName());
        } else {
          assemblies = new ArrayList<String>();
          String asb = this.config.getYaml().getAssembly().getName();
          if (this.flow.isAssemblyExist(asb)) {
            assemblies.add(asb);
          }
        }
        this.cleanup(assemblies);
      } else if (cmd.hasOption("get-ips")) {
        if (!flow.isAssemblyExist()) {
          System.err.printf(Constants.NOTFOUND_ERROR, config.getYaml().getAssembly().getName());
        } else if (cmd.getOptionValues("get-ips") == null) {
          // if there is no args for get-ips
          getIps0();
        } else if (cmd.getOptionValues("get-ips").length == 1) {
          // if there is one arg for get-ips
          getIps1(cmd.getOptionValues("get-ips")[0]);
        } else if (cmd.getOptionValues("get-ips").length == 2) {
          // if there are two args for get-ips
          getIps2(cmd.getOptionValues("get-ips")[0], cmd.getOptionValues("get-ips")[1]);
        }
      } else if (cmd.hasOption("retry")) {
        this.retryDeployment();
      } else if (cmd.hasOption("procedure")) {
        if (cmd.getOptionValues("procedure").length != 3) {
          System.err
              .println("Wrong prameters! --prodedure <platformName> <componentName> <actionName>");
          return Constants.EXIT_ONE;
        } else {
          String[] args = cmd.getOptionValues("procedure");
          String arglist = "";
          int rollAt = 100;
          if (cmd.hasOption("procedure-arguments")) {
            arglist = cmd.getOptionValue("procedure-arguments");
          }
          if (cmd.hasOption("procedure-step-size")) {
            rollAt = Integer.parseInt(cmd.getOptionValue("procedure-step-size"));
          }
          List<String> instances = null;
          if (cmd.hasOption("procedure-instances")) {
            String ins = cmd.getOptionValue("procedure-instances");
            if (ins != null && ins.trim().length() > 0) {
              if (ins.equalsIgnoreCase("list")) {
                List<String> list = flow.listInstances(args[0], args[1]);
                if (list != null) {
                  for (String instance : list) {
                    System.out.println(instance);
                  }
                }
                return Constants.EXIT_ZERO;
              }
              instances = Arrays.asList(ins.split(","));
            }
          }
          if ("list".equalsIgnoreCase(args[2])) {
            List<String> list = flow.listActions(args[0], args[1]);
            if (list != null) {
              for (String instance : list) {
                System.out.println(instance);
              }
            }
          } else {
            exit = this.executeAction(args[0], args[1], args[2], arglist, instances, rollAt);
          }

        }
      }
    } catch (ParseException e) {
      exit = Constants.EXIT_ONE;
      System.err.println(e.getMessage());
      this.help(null, Constants.BFD_TOOL);
    } catch (Exception e) {
      exit = Constants.EXIT_ONE;
      System.err.println(e.getMessage());
    }
    return exit;
  }

  /**
   * Execute action.
   *
   * @param platformName the platform name
   * @param componentName the component name
   * @param actionName the action name
   * @param arglist the arglist
   * @param instanceList the instance list
   * @param rollAt the roll at
   */
  private int executeAction(String platformName, String componentName, String actionName,
      String arglist, List<String> instanceList, int rollAt) {
    int returnCode = 0;
    String procedureId = null;
    try {
      System.out.println(Constants.PROCEDURE_RUNNING);
      procedureId = flow.executeAction(platformName, componentName, actionName, arglist,
          instanceList, rollAt);

    } catch (OneOpsClientAPIException e) {
      System.err.println(e.getMessage());
      returnCode = Constants.EXIT_ONE;
    }
    if (procedureId != null) {
      String procStatus = "active";
      try {
        while (procStatus != null
            && (procStatus.equalsIgnoreCase("active") || procStatus.equalsIgnoreCase("pending"))) {
          procStatus = flow.getProcedureStatusForAction(procedureId);
          try {
            Thread.sleep(3000);
          } catch (InterruptedException e) {
            // Ignore
          }
        }
      } catch (OneOpsClientAPIException e) {
        // Ignore
      }
      if (procStatus.equalsIgnoreCase("complete")) {
        System.out.println(Constants.SUCCEED);
      } else {
        System.err.println(Constants.PROCEDURE_NOT_COMPLETE);
        returnCode = Constants.EXIT_ONE;
      }
    }
    return returnCode;
  }

  /**
   * User input.
   *
   * @param msg the msg
   * @return the string
   */
  @SuppressWarnings("resource")
  private String userInput(String msg) {
    System.out.println(msg);
    Scanner inputReader = new Scanner(System.in);
    String input = inputReader.nextLine();
    return input;
  }

  /**
   * Gets the ips 0.
   *
   * @return the ips 0
   */
  private void getIps0() {
    Map<String, Object> platforms = flow.getConfig().getYaml().getPlatforms();
    List<String> computes = bfdUtils.getComponentOfCompute(this.flow);
    System.out.println("Environment name: " + flow.getConfig().getYaml().getBoo().getEnvName());
    for (String pname : platforms.keySet()) {
      System.out.println("Platform name: " + pname);
      for (String cname : computes) {
        System.out.println("Compute name: " + cname);
        System.out.printf(getIps(pname, cname));
      }
    }
  }

  /**
   * Gets the ips 1.
   *
   * @param inputEnv the input env
   * @return the ips 1
   */
  private void getIps1(String inputEnv) {
    String yamlEnv = flow.getConfig().getYaml().getBoo().getEnvName();
    if (yamlEnv.equals(inputEnv)) {
      getIps0();
    } else {
      System.out.println(Constants.NO_ENVIRONMENT);
    }
  }

  /**
   * Gets the ips 2.
   *
   * @param inputEnv the input env
   * @param componentName the component name
   * @return the ips 2
   */
  private void getIps2(String inputEnv, String componentName) {
    String yamlEnv = flow.getConfig().getYaml().getBoo().getEnvName();
    if (inputEnv.equals("*") || yamlEnv.equals(inputEnv)) {
      Map<String, Object> platforms = flow.getConfig().getYaml().getPlatforms();
      List<String> computes = bfdUtils.getComponentOfCompute(this.flow);
      for (String s : computes) {
        if (s.equals(componentName)) {
          System.out
              .println("Environment name: " + flow.getConfig().getYaml().getBoo().getEnvName());
          for (String pname : platforms.keySet()) {
            System.out.println("Platform name: " + pname);
            System.out.println("Compute name: " + componentName);
            System.out.printf(getIps(pname, componentName));
          }
          return;
        }
      }
      System.out.println("No such component: " + componentName);
    } else {
      System.out.println("No such environment: " + inputEnv);
    }
  }

  /**
   * Gets the ips.
   *
   * @param platformName the platform name
   * @param componentName the component name
   * @return the ips
   */
  private String getIps(String platformName, String componentName) {
    try {
      return flow.printIps(platformName, componentName);
    } catch (OneOpsClientAPIException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Retry deployment.
   *
   * @return true, if successful
   */
  private boolean retryDeployment() {
    return flow.retryDeployment();
  }

  /**
   * Help.
   *
   * @param header the header
   * @param footer the footer
   */
  private void help(String header, String footer) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(120, "boo", header, options, footer, true);
  }

  /**
   * List files.
   *
   * @param prefix the prefix
   * @return the list
   */
  private List<String> listFiles(String prefix) {
    if (prefix == null || prefix.trim().length() == 0) {
      System.err.println(Constants.ASSEMBLY_PREFIX_ERROR);
      System.exit(1);
    }
    List<String> assemblies = flow.getAllAutoGenAssemblies(prefix);
    for (String assembly : assemblies) {
      if (assembly != null) {
        System.out.println(assembly);
      }

    }
    return assemblies;
  }

  /**
   * Creates the packs.
   *
   * @param isUpdate the is update
   * @param isAssemblyOnly the is assembly only
   * @throws BfdOoException the BFDOO exception
   * @throws OneOpsClientAPIException the one ops client API exception
   */
  public void createPacks(boolean isUpdate, boolean isAssemblyOnly)
      throws BfdOoException, OneOpsClientAPIException {
    flow.process(isUpdate, isAssemblyOnly);
  }

  /**
   * Limit to 32 characters long.
   *
   * @param isAutoGen the is auto gen
   * @param assemblyName the assembly name
   * @return the string
   */
  private String autoGenAssemblyName(boolean isAutoGen, String assemblyName) {
    if (isAutoGen) {
      assemblyName = (assemblyName == null ? this.randomString("")
          : (assemblyName + Constants.DASH + this.randomString(assemblyName)));
    }
    return assemblyName;
  }

  /**
   * Random string.
   *
   * @param assemblyName the assembly name
   * @return the string
   */
  private String randomString(String assemblyName) {
    StringBuilder name = new StringBuilder();
    int rand = 32 - assemblyName.length() - 1;
    rand = rand > 8 ? 8 : rand;
    name.append(UUID.randomUUID().toString().substring(0, rand));
    return name.toString();
  }

  /**
   * Cleanup.
   *
   * @param assemblies the assemblies
   */
  public void cleanup(List<String> assemblies) {
    if (assemblies.size() == 0) {
      System.out.println("There is no instance to remove");
      return;
    }
    if (isForced == false) {
      String str =
          String.format(YES_NO, assemblies.size(), this.config.getYaml().getAssembly().getName());
      str = this.userInput(str);
      if (!"y".equalsIgnoreCase(str.trim())) {
        return;
      }

    }
    boolean isSuc = true;
    for (String assembly : assemblies) {
      LogUtils.info("Destroying OneOps assembly %s \n", assembly);
      this.initOo(config, assembly, comment);
      if (flow.isAssemblyExist(assembly)) {
        boolean isDone;
        try {
          isDone = flow.removeAllEnvs();
          isDone = flow.removeAllPlatforms();
          if (!isDone && isSuc) {
            isSuc = false;
          }
        } catch (OneOpsClientAPIException e) {
          isSuc = false;
        }
      }
    }
    if (!isSuc) {
      LogUtils.error(Constants.NEED_ANOTHER_CLEANUP);
    }
  }

  /**
   * Gets the status.
   *
   * @return the status
   * @throws BfdOoException the BFDOO exception
   */
  public String getStatus() throws BfdOoException {
    return flow.getStatus();
  }

  /**
   * Checks if is quiet.
   *
   * @return true, if is quiet
   */
  public static boolean isQuiet() {
    return isQuiet;
  }

  /**
   * Sets the quiet.
   *
   * @param isQuiet the new quiet
   */
  public static void setQuiet(boolean isQuiet) {
    BooCli.isQuiet = isQuiet;
  }

  /**
   * Sets the forced.
   *
   * @param isForced the new forced
   */
  public static void setForced(boolean isForced) {
    BooCli.isForced = isForced;
  }

  /**
   * Sets the no deploy.
   *
   * @param isNoDeploy the new no deploy
   */
  public static void setNoDeploy(boolean isNoDeploy) {
    BooCli.isNoDeploy = isNoDeploy;
  }

  /**
   * Checks if is no deploy.
   *
   * @return true, if is no deploy
   */
  public static boolean isNoDeploy() {
    return isNoDeploy;
  }
}
