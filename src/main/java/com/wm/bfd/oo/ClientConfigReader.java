package com.wm.bfd.oo;

import com.wm.bfd.oo.yaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;

public class ClientConfigReader {

  private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

  public Yaml read(String yaml) throws IOException {
    return mapper.readValue(yaml, Yaml.class);
  }
}
