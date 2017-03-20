/*
 * Copyright 2017 Walmart, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oneops.boo;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;
import com.github.mustachejava.reflect.ReflectionObjectHandler;
import com.github.mustachejava.util.GuardException;
import com.github.mustachejava.util.Wrapper;
import com.google.common.io.ByteStreams;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class ClientConfigInterpolator {

  private static final String HOME = System.getProperty("user.home");
  private static final String WORK = System.getProperty("user.dir");
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
   * Take key/value pairs from a OneOps configuration profile and interpolate a Boo YAML template in InputStream
   * with them.
   * 
   * @see ClientConfigInterpolator#interpolate(File, File, String)
   * @param booYamlIn InputStream containing the Boo Yaml configuration.
   */
  public String interpolate(InputStream booYamlIn, File booConfigFile, String profile) throws IOException {
    String booYaml = new String(ByteStreams.toByteArray(booYamlIn));
    return interpolate(booYaml, booConfigFile, profile);
  }

  /** 
   * Take key/value pairs from a OneOps configuration profile and interpolate a Boo YAML template in string format
   * with them.
   * 
   * @see ClientConfigInterpolator#interpolate(File, File, String)
   * @param booYaml the template string
   */
  public String interpolate(String booYaml, File booConfigFile, String profile) throws IOException {
    if (booConfigFile.exists()) {
      // Extract the requested config profile
      Map<String, String> config = iniReader.read(booConfigFile, profile);
      // Interpolate the Boo YAML with the values from the profile
      return interpolate(booYaml, config);
    } else {
      return booYaml;
    }
  }

  /** 
   * Take key/value pairs of configuration and interpolate a Boo YAML template in straing format
   * with them.
   * 
   * @see ClientConfigInterpolator#interpolate(File, File, String)
   * @param booYaml the template string
   * @param config the key/value pairs
   */
  public String interpolate(String booYaml, Map<String, String> config) throws IOException {
    Writer writer = new StringWriter();
    NoEncodingMustacheFactory mustacheFactory = new NoEncodingMustacheFactory();
    mustacheFactory.setObjectHandler(new BooReflectionObjectHandler());
    Mustache mustache = mustacheFactory.compile(new StringReader(booYaml), "boo");
    mustache.execute(writer, config).flush();
    return writer.toString();
  }

  // Prevents doing standard Mustache XHTML encoding
  private static class NoEncodingMustacheFactory extends DefaultMustacheFactory {
    @Override
    public void encode(String value, Writer writer) {
      try {
        writer.write(value);
      } catch (IOException e) {
        throw new MustacheException(e);
      }
    }
  }

  // This whole mechanism should be replaced by creating bindings in Guice and injecting
  // a Map<String,BooFunction> but we only have one function right now so this is
  // sufficient or we can just use Sisu and it will be easier than creating
  // manual Guice bindings. JvZ

  // Perform special Boo lookups and then fall back to normal processing
  private class BooReflectionObjectHandler extends ReflectionObjectHandler {
    @Override
    public Wrapper find(final String name, List<Object> scopes) {
      if (name.startsWith("file(") && name.endsWith(")")) {
        return new Wrapper() {
          @Override
          public Object call(List<Object> scopes) throws GuardException {
            return file(defunction(name));
          }
        };
      }
      return super.find(name, scopes);
    }
  }

  private String defunction(String str) {
    return str.substring(str.indexOf('(') + 1, str.length() - 1);
  }

  private String file(String path) {
    if (path.startsWith("~")) {
      path = path.replace("~", HOME);
    } else if (path.startsWith("@")) {
      path = path.substring(1);
    } else if (path.startsWith("./")) {
      path = path.replace("./", String.format("%s%s", WORK, File.separator));
    }
    try {
      return FileUtils.readFileToString(new File(path));
    } catch (IOException e) {
      // Content that might be required for the compute to function may be ommitted so just fail fast.
      // If it's an ssh public key that is meant to be injected and it doesn't work it will result
      // in a compute you can't log in to.
      throw new RuntimeException(String.format("%s cannot be found or cannot be read.", path));
    }
  }
}
