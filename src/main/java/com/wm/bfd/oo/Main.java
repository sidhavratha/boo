package com.wm.bfd.oo;

import com.wm.bfd.oo.yaml.Constants;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Main {

  static {
    Logger.getRootLogger().setLevel(Level.OFF);
  }

  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(String[] args) {
    BooCli cli = new BooCli();
    try {
      int exit = cli.parse(args);
      System.exit(exit);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      System.exit(Constants.EXIT_ONE);
    }
  }
}
