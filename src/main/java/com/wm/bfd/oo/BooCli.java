package com.wm.bfd.oo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
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
  private String configDir;
  private String configFile;
  private BuildAllPlatforms flow;
  private String[] args = null;
  private Options options = new Options();
  private static int BUFFER = 1024;

  public BooCli(String[] args) {
    this.args = args;
    Option help = new Option("h", "help", false, "show help.");
    Option create = Option.builder("create").desc("Create a new deployment in OneOps.").build();
    Option status = Option.builder("s").longOpt("status").desc("Check deployment status.").build();

    Option config =
        Option.builder("cf").longOpt("config_file").argName("yaml").hasArg()
            .desc("Use specified template.").build();

    Option config_dir =
        Option.builder("cd").longOpt("config_dir").argName("yaml").hasArg()
            .desc("Use the config dir.").build();

    Option cleanup = Option.builder("cleanup").desc("Remove all deployment in OneOps.").build();
    Option list = new Option("l", "list", false, "List all YAML files.");

    options.addOption(help);
    options.addOption(config);
    options.addOption(config_dir);
    options.addOption(create);
    options.addOption(status);
    options.addOption(list);
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

    /**
     * Handle command without configuration file dependency first.
     */
    if (cmd.hasOption("h")) {
      this.help(null, Constants.BFD_TOOL);
      System.exit(0);
    }

    /**
     * Get configuration dir or file.
     */
    if (cmd.hasOption("cf")) {
      this.configFile = cmd.getOptionValue("cf");
      this.init(this.configFile);
    }
    if (cmd.hasOption("cd")) {
      this.configDir = cmd.getOptionValue("cd");
      if (cmd.hasOption("l")) {
        this.listFiles(this.configDir);
      }
    } else {
      this.help(null, "No YAML file specified");
      System.exit(-1);
    }

    /**
     * Handle other commands.
     */
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

  private void listFiles(String dir) {
    File dirs = new File(dir);
    File[] files = dirs.listFiles();
    for (File file : files) {
      if (StringUtils.containsIgnoreCase(file.getName(), YAML))
        System.out.println(file.getName());
    }
  }

  private List<String> listFilesStartWith(String dir, String file) {
    List<String> list = new ArrayList<String>();
    File dirs = new File(dir);
    File ori = new File(file);
    File[] files = dirs.listFiles();
    list.add(ori.getName());
    String startWith = ori.getName() + FILE_NAME_SPLIT;
    for (File f : files) {
      if (StringUtils.startsWithIgnoreCase(f.getName(), startWith))
        list.add(f.getName());
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

  private void helpOld() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("boo", options);
  }

  public void createPacks() throws BFDOOException, OneOpsClientAPIException {
    this.copyFile(this.configFile);
    flow.process();
  }

  public void cleanup() throws BFDOOException, OneOpsClientAPIException {
    List<String> files = this.listFilesStartWith(this.configDir, this.configFile);
    for (String file : files) {
      LOG.warn("Destroying OneOps instance {}", file);
    }
    // flow.cleanup();
  }

  public String getStatus() throws BFDOOException, OneOpsClientAPIException {
    return flow.getStatus();
  }

}
