package com.wm.bfd.oo;

import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ClientConfigIniReader {

  public Map<String, String> read(File booConfigFile, String profile) throws IOException {
    Wini ini = new Wini(booConfigFile);
    return ini.get(profile);
  }
}
