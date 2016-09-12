package com.wm.bfd.oo;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Main {

  static {
    Logger.getRootLogger().setLevel(Level.OFF);
  }

  public static void main(String[] args) {
    BooCli cli = new BooCli(args);
    try {
      cli.parse();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    }
  }
}
