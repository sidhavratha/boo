package com.wm.bfd.oo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
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
  final private static String YAML = "yaml";
  final private static String FILE_NAME_SPLIT = "-";
  final private static String TEMPLATE_FILE = ".yaml" + FILE_NAME_SPLIT;
  final private static String YES_NO =
      "WARNING! There are %s instances using the %s configuration. Do you want to destroy all of them? (y/n) ";
  private String configDir;
  private String configFile;
  private static boolean isQuiet = false;
  private static boolean isForced = false;
  private BuildAllPlatforms flow;
  private String[] args = null;
  private Options options = new Options();
  private static int BUFFER = 1024;
  private String assembly;

  public BooCli(String[] args) {
    this.args = args;
    Option help = new Option("h", "help", false, "show help.");
    Option create =
        Option
            .builder("c")
            .longOpt("create")
            .desc(
                "Create a new Assembly specified by -d or -f. If Assembly automatic naming is enabled, each invocation will create a new Assembly.")
            .build();
    Option update =
        Option.builder("u").longOpt("update").desc("Update configurations specified by -d or -f.")
            .build();
    Option status =
        Option.builder("s").longOpt("status")
            .desc("Get status of deployments specified by -d or -f").build();

    Option config_dir =
        Option.builder("d").longOpt("config-dir").argName("DIR").hasArg()
            .desc("Use all configuration files in given directory, required if -f not used")
            .build();

    Option config =
        Option.builder("f").longOpt("config-file").argName("FILE").hasArg()
            .desc("Use specified configuration file, required if -d not used").build();

    Option cleanup =
        Option.builder("r").longOpt("remove")
            .desc("Remove all deployed configurations specified by -d or -f").build();
    Option list = new Option("l", "list", false, "List all YAML files specified by -d or -f");

    Option force = Option.builder().longOpt("force").desc("Do not prompt for --remove").build();

    Option getIps =
        Option.builder().longOpt("get-ips").argName("environment> <compute-class")
            .desc("Get IPs of deployed nodes specified by -d or -f; Args are optional.").build();
    getIps.setOptionalArg(true);
    getIps.setArgs(Option.UNLIMITED_VALUES);

    Option retry =
        Option.builder().longOpt("retry")
            .desc("Retry deployments of configurations specified by -d or -f").build();
    Option quiet = Option.builder().longOpt("quiet").desc("Silence the textual output.").build();
    Option assembly =
        Option.builder("a").longOpt("assembly").desc("Override the assembly name.").build();
    options.addOption(help);
    options.addOption(config);
    options.addOption(config_dir);
    options.addOption(create);
    options.addOption(update);
    options.addOption(status);
    options.addOption(list);
    options.addOption(cleanup);
    options.addOption(getIps);
    options.addOption(retry);
    options.addOption(quiet);
    options.addOption(force);
    options.addOption(assembly);
  }

  static {
    RestAssured.useRelaxedHTTPSValidation();
  }

  public void init(String template, String assembly) throws BFDOOException {
    if (LOG.isDebugEnabled())
      LOG.debug("Loading {}", template);
    this.configFile = new BFDUtils().getAbsolutePath(template);
    Injector injector = Guice.createInjector(new JaywayHttpModule(this.configFile));
    ClientConfig config = injector.getInstance(ClientConfig.class);
    new BFDUtils().verifyTemplate(config);
    OOInstance oo = injector.getInstance(OOInstance.class);
    try {
      flow = new BuildAllPlatforms(oo, config);
      if (assembly != null) {
        config.getYaml().getAssembly().setName(assembly);
      }
      this.assembly = config.getYaml().getAssembly().getName();
    } catch (OneOpsClientAPIException e) {
      System.err.println("Init failed! Quit!");
    }
  }

  /**
   * Parse user's input
   * 
   * @throws ParseException
   * @throws BFDOOException
   * @throws OneOpsClientAPIException
   */
  public void parse() throws ParseException, BFDOOException, OneOpsClientAPIException {
    CommandLineParser parser = new DefaultParser();
    // CommandLineParser parser = new GnuParser();
    try {
      CommandLine cmd = parser.parse(options, args);
      /**
       * Handle command without configuration file dependency first.
       */
      if (cmd.hasOption("h")) {
        this.help(null, Constants.BFD_TOOL);
        System.exit(0);
      }

      if (cmd.hasOption("quiet")) {
        BooCli.setQuiet(Boolean.TRUE);
      }

      if (cmd.hasOption("force")) {
        BooCli.setForced(Boolean.TRUE);
      }

      if (cmd.hasOption("a")) {
        this.assembly = cmd.getOptionValue("a");
      }
      /**
       * Get configuration dir or file.
       */
      if (cmd.hasOption("f")) {
        this.configFile = cmd.getOptionValue("f");
        System.out.printf(Constants.CONFIG_FILE, new BFDUtils().getAbsolutePath(this.configFile));
        System.out.println();
        this.init(this.configFile, this.assembly);
      }

      if (cmd.hasOption("d")) {
        this.configDir = cmd.getOptionValue("d");
        System.out.printf(Constants.CONFIG_DIR, new BFDUtils().getAbsolutePath(this.configDir));
        System.out.println();
        if (cmd.hasOption("l")) {
          this.listFiles(this.configDir);
        }
      }



      if (this.configDir == null && this.configFile != null) {
        this.configDir = this.configFile.substring(0, this.configFile.lastIndexOf('/'));
      } else if (this.configDir == null && this.configFile == null) {
        this.help(null, "No YAML file found.");
        System.exit(-1);
      }

      /**
       * Handle other commands.
       */
      if (cmd.hasOption("s")) {
        System.out.println(this.getStatus());
      } else if (cmd.hasOption("c")) {
        this.createPacks(Boolean.FALSE);
      } else if (cmd.hasOption("u")) {
        if (flow.isAssemblyExist()) {
          this.createPacks(Boolean.TRUE);
        } else {
          System.err.printf(Constants.UPDATE_ERROR, assembly);
        }
      } else if (cmd.hasOption("r")) {
        this.cleanup();
      } else if (cmd.hasOption("get-ips")) {
        if (cmd.getOptionValues("get-ips") == null) {
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
      }
    } catch (ParseException e) {
      System.err.println(e.getMessage());
      this.help(null, Constants.BFD_TOOL);
    }
  }

  @SuppressWarnings("resource")
  private String userInput(String msg) {
    System.out.println(msg);
    Scanner inputReader = new Scanner(System.in);
    String input = inputReader.nextLine();
    return input;
  }

  private void getIps0() {
    Map<String, Object> platforms = flow.getConfig().getYaml().getPlatforms();
    List<String> computes = new BFDUtils().getComponentOfCompute(this.flow);
    System.out.println("Environment name: " + flow.getConfig().getYaml().getBoo().getEnvName());
    for (String pname : platforms.keySet()) {
      System.out.println("Platform name: " + pname);
      for (String cname : computes) {
        System.out.println("Compute name: " + cname);
        System.out.printf(getIps(pname, cname));
      }
    }
  }

  private void getIps1(String inputEnv) {
    String yamlEnv = flow.getConfig().getYaml().getBoo().getEnvName();
    if (yamlEnv.equals(inputEnv)) {
      getIps0();
    } else {
      System.out.println("No such environment");
    }
  }

  private void getIps2(String inputEnv, String componentName) {
    String yamlEnv = flow.getConfig().getYaml().getBoo().getEnvName();
    if (inputEnv.equals("*") || yamlEnv.equals(inputEnv)) {
      Map<String, Object> platforms = flow.getConfig().getYaml().getPlatforms();
      List<String> computes = new BFDUtils().getComponentOfCompute(this.flow);
      for (String s : computes) {
        if (s.equals(componentName)) {
          System.out.println("Environment name: "
              + flow.getConfig().getYaml().getBoo().getEnvName());
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

  private String getIps(String platformName, String componentName) {
    try {
      return flow.printIps(platformName, componentName);
    } catch (OneOpsClientAPIException e) {
      e.printStackTrace();
    }
    return null;
  }

  private boolean retryDeployment() {
    return flow.retryDeployment();
  }

  private void help(String header, String footer) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("boo", header, options, footer, true);
  }

  private void listFiles(String dir) {
    File dirs = new File(dir);
    File[] files = dirs.listFiles();
    for (File file : files) {
      if (StringUtils.containsIgnoreCase(file.getName(), YAML))
        System.out.println(file.getName());
    }
  }

  private List<String> listConfigFiles(String dir, String file) {
    List<String> list = new ArrayList<String>();
    File dirs = new File(dir);
    File ori = new File(file);
    File[] files = dirs.listFiles();
    if (file.indexOf(TEMPLATE_FILE) > 0) {
      list.add(ori.getName());
    } else {
      String startWith = ori.getName() + FILE_NAME_SPLIT;
      for (File f : files) {
        if (StringUtils.startsWithIgnoreCase(f.getName(), startWith))
          list.add(f.getName());
      }
    }
    return list;
  }

  private String randomName() {
    return UUID.randomUUID().toString();
  }

  private String copyFile(String src) {
    String des = null;
    InputStream inStream = null;
    OutputStream outStream = null;
    try {

      File source = new File(src);
      File destination = new File(src + FILE_NAME_SPLIT + this.randomName());
      System.out.printf(Constants.WORKING_FILE, destination.getPath());
      System.out.println();
      des = destination.getPath();

      inStream = new FileInputStream(source);
      outStream = new FileOutputStream(destination);

      byte[] buffer = new byte[BUFFER];

      int length;
      while ((length = inStream.read(buffer)) > 0) {
        outStream.write(buffer, 0, length);
      }

      if (inStream != null)
        inStream.close();
      if (outStream != null)
        outStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return des;
  }

  public void createPacks(boolean isUpdate) throws BFDOOException, OneOpsClientAPIException {
    this.copyFile(this.configFile);
    flow.process(isUpdate);
  }

  private void deleteFile(String dir, String file) {
    if (StringUtils.isEmpty(file))
      return;
    if (LOG.isWarnEnabled())
      LOG.warn("Deleting yaml file {}", file);
    File f = new File(dir + "/" + file);
    if (f.exists()) {
      f.deleteOnExit();
    }
  }

  private String trimFileName(String file) {
    String name = new File(file).getName();
    return (name == null || name.lastIndexOf('.') < 0) ? "" : name.substring(0,
        name.lastIndexOf('.'));
  }

  public void cleanup() {
    List<String> files = this.listConfigFiles(this.configDir, this.configFile);
    if (files.size() == 0) {
      System.out.println("There is no instance to remove");
      return;
    }
    if (isForced == false) {
      String str = String.format(YES_NO, files.size(), trimFileName(this.configFile));
      str = this.userInput(str);
      if (!"y".equalsIgnoreCase(str.trim()))
        return;
    }
    boolean isSuc = true;
    for (String file : files) {
      LogUtils.info("Destroying OneOps instance %s \n", file);
      try {
        this.init(file, null);
        if (flow.isAssemblyExist()) {
          boolean isDone = flow.cleanup();
          if (!isDone && isSuc) {
            isSuc = false;
          }
        }
        this.deleteFile(this.configDir, file);
      } catch (BFDOOException e) {
        // Ignore
        isSuc = false;
      } catch (OneOpsClientAPIException e) {
        // Ignore
        isSuc = false;
      }
    }
    if (!isSuc) {
      LogUtils.error(Constants.NEED_ANOTHER_CLEANUP);
    }

  }

  public String getStatus() throws BFDOOException {
    return flow.getStatus();
  }

  public static boolean isQuiet() {
    return isQuiet;
  }

  public static void setQuiet(boolean isQuiet) {
    BooCli.isQuiet = isQuiet;
  }

  public static void setForced(boolean isForced) {
    BooCli.isForced = isForced;
  }

}
