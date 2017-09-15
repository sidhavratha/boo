/*
 * Copyright 2017 Walmart, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.oneops.boo;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;
import com.github.mustachejava.reflect.ReflectionObjectHandler;
import com.github.mustachejava.util.GuardException;
import com.github.mustachejava.util.Wrapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.oneops.client.OneOpsConfigReader;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BooConfigInterpolator {

  private static final String HOME = System.getProperty("user.home");
  private static final String WORK = System.getProperty("user.dir");
  private final static Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
  private final OneOpsConfigReader iniReader;

  public BooConfigInterpolator() {
    iniReader = new OneOpsConfigReader();
  }

  // For testing
  public String interpolate(InputStream booYamlIn, File booConfigFile, String profile)
    throws IOException {
    String booYaml = new String(ByteStreams.toByteArray(booYamlIn));
    return interpolate(booYaml, booConfigFile, profile);
  }

  public String interpolate(InputStream booYamlIn, String profile)
    throws IOException {
    String booYaml = new String(ByteStreams.toByteArray(booYamlIn));
    return interpolate(booYaml, profile);
  }

  public String interpolate(String booYaml, String profile) throws IOException {
    File defaultConfig = iniReader.defaultConfig();
    return interpolate(booYaml, defaultConfig, profile);
  }

  public String interpolate(String booYaml, File booConfigFile, String profile) throws IOException {
    if (booConfigFile != null && booConfigFile.exists()) {
      Map<String, String> config = iniReader.read(booConfigFile, profile);
      return interpolate(booYaml, config);
    } else {
      return booYaml;
    }
  }

  public String interpolate(String booYaml, Map<String, String> config) throws IOException {
    Map<String, Object> mustacheMap = Maps.newHashMap();
    for (Map.Entry<String, String> e : config.entrySet()) {
      String key = e.getKey();
      String value = e.getValue();
      Object mustacheValue;
      if (value.startsWith("\"") && value.endsWith("\"")) {
        mustacheValue = deliteral(value);
      } else if (value.contains(",")) {
        mustacheValue = splitter.split(value);
      } else {
        mustacheValue = value;
      }
      mustacheMap.put(key, mustacheValue);
    }
    Writer writer = new StringWriter();
    NoEncodingMustacheFactory mustacheFactory = new NoEncodingMustacheFactory();
    mustacheFactory.setObjectHandler(new BooReflectionObjectHandler());
    Mustache mustache = mustacheFactory.compile(new StringReader(booYaml), "boo");
    mustache.execute(writer, mustacheMap).flush();
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
      else if (name.startsWith("multilineFile(") && name.endsWith(")")) {
        return new Wrapper() {
          @Override
          public Object call(List<Object> scopes) throws GuardException {
            // Keep the lines in a multiline file
            return file(dename(name), true, denumws(name));
          }
        };
      }
      return super.find(name, scopes);
    }
  }

  private String dename(String str) {
    int lastIndex = str.length() - 1;
    if (str.contains(",")) {
      lastIndex = str.indexOf(",");
    }
    return str.substring(str.indexOf('(') + 1, lastIndex);
  }

  private int denumws(String str) {
    int idx = str.indexOf(",");
    if (idx < 0) {
      return 0;
    } else {
      str = str.trim();
      return Integer.parseInt(str.substring(idx + 1, str.length() - 1));
    }
  }

  private String deliteral(String str) {
    return str.substring(1, str.length() - 1);
  }

  private String defunction(String str) {
    return str.substring(str.indexOf('(') + 1, str.length() - 1);
  }

  private String file(String path) {
    return file(path, false, 0);
  }

  private String file(String path, boolean keepNewlines, int numWhitespaceToBePrepend) {
    if (path.startsWith("~")) {
      path = path.replace("~", HOME);
    } else if (path.startsWith("@")) {
      path = path.substring(1);
    } else if (path.startsWith("./")) {
      path = path.replace("./", String.format("%s%s", WORK, File.separator));
    }
    try {
      final String contents =  keepNewlines ? readFileToString(new File(path)) : FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
      if (numWhitespaceToBePrepend > 0) {
        final String lines[] = contents.split("\\r?\\n");
        final StringBuilder sb = new StringBuilder();
        final String whitespaces = String.join("", Collections.nCopies(numWhitespaceToBePrepend, " "));
        boolean isFirstLineVisited = false;
        for (String line : lines) {
          if (isFirstLineVisited) {
            sb.append(whitespaces).append(line).append('\n');
          } else {
            sb.append(line).append('\n');
            isFirstLineVisited = true;
          }
        }
        return sb.toString();
      } else {
        return contents;
      }

    } catch (IOException e) {
      // Content that might be required for the compute to function may be ommitted so just fail
      // fast. If it's an ssh public key that is meant to be injected and it doesn't work it will result
      // in a compute you can't log in to.
      throw new RuntimeException(String.format("%s cannot be found or cannot be read.", path));
    }
  }

  // Read the file specified into a string.  If there are newlines
  // in the file, duplicate them to preserve the multi line
  // formatting in a YAML value.
  private String readFileToString(File inputFile) throws IOException {
    BufferedReader bufrdr = new BufferedReader(new FileReader(inputFile));
    StringWriter strWr = new StringWriter();
    BufferedWriter wr = new BufferedWriter(strWr);

    try {
      char[] copyBuffer = new char[1024];
      int readChars;

      do {
        readChars = bufrdr.read(copyBuffer, 0, 1024);
        if (readChars != -1) {
          boolean addNewLine = false;

          for (int charIndex = 0; charIndex < readChars; charIndex++) {
            char thisChar = copyBuffer[charIndex];
            if (thisChar == '\n') {
              // Append the newline to the output. Add
              // another newline before the next character.
              wr.append('\n');
              addNewLine = true;
            } else {
              if (addNewLine) {
                wr.append('\n');
                addNewLine = false;
              }
              wr.append(thisChar);
            }
          }
        }
      } while (readChars != -1);
    } finally {
      bufrdr.close();
      wr.close();
    }

    return strWr.toString();
  }
}
