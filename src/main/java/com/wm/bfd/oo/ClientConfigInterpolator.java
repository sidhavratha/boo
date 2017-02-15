package com.wm.bfd.oo;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Map;

public class ClientConfigInterpolator {

  private final ClientConfigIniReader iniReader;

  public ClientConfigInterpolator() {
    iniReader = new ClientConfigIniReader();
  }

  /**
   * Take key/value pairs from a OneOps configuration profile and interpolate a Boo YAML template
   * with them.
   * 
   * @param booYamlFile template to use
   * @param booConfigFile to use for key/value pairs
   * @param profile in configuration file to use for key/value pairs
   * @return Interpolate Boo YAML template
   * @throws IOException throw if there are errors interpolating the template
   */
  public String interpolate(File booYamlFile, File booConfigFile, String profile) throws IOException {
    String booYaml = new String(Files.readAllBytes(booYamlFile.toPath()));
    return interpolate(booYaml, booConfigFile, profile);
  }
  
  /**
   * @see ClientConfigInterpolator#interpolate(File, File, String)
   * @param booYamlIn  InputStream containing the Boo Yaml configuration.
   * @param booConfigFile
   * @param profile
   * @return
   * @throws IOException
   */
  public String interpolate(InputStream booYamlIn, File booConfigFile, String profile) throws IOException {
    String booYaml = new String(ByteStreams.toByteArray(booYamlIn));
    return interpolate(booYaml, booConfigFile, profile);
  }
  
  /**
   * @see ClientConfigInterpolator#interpolate(File, File, String)
   * @param booYaml
   * @param booConfigFile
   * @param profile
   * @return
   * @throws IOException
   */
  public String interpolate(String booYaml, File booConfigFile, String profile) throws IOException {
    if (booConfigFile.exists()) {
      // Extract the requested config profile
      Map<String, String> config = iniReader.read(booConfigFile, profile);
      // Interpolate the Boo YAML with the values from the profile
      Writer writer = new StringWriter();
      MustacheFactory mf = new DefaultMustacheFactory();
      Mustache mustache = mf.compile(new StringReader(booYaml), "boo");
      mustache.execute(writer, config).flush();
      return writer.toString();
    } else {
      return booYaml;
    }  
  }
  
}
