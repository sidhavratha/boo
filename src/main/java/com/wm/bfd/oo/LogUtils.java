package com.wm.bfd.oo;


public class LogUtils {
  public static void info(String msg, String... arg) {
    System.out.printf(msg, arg);
    System.out.println();
  }

}
