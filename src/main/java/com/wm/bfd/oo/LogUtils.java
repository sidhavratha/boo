package com.wm.bfd.oo;


public class LogUtils {
  public static void info(String msg, Object... arg) {
    if (!BooCli.isQuiet()) {
      System.out.printf(msg, arg);
      System.out.println();
    }
  }

  public static void error(String msg, Object... arg) {
    if (!BooCli.isQuiet()) {
      System.err.printf(msg, arg);
      System.err.println();
    }
  }

}
