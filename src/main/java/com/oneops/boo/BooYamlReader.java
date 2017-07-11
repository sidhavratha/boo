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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.oneops.boo.yaml.Constants;
import com.oneops.boo.yaml.Yaml;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

public class BooYamlReader {

  private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

  public Yaml read(String yaml) throws IOException {
    return read(new StringReader(yaml));
  }
  
  public Yaml read(Reader yaml) throws IOException {
    Yaml booYaml = mapper.readValue(yaml, Yaml.class);    
    return booYaml;
  }  
}
