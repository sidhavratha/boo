package com.wm.bfd.oo;

import org.apache.commons.cli.ParseException;

import com.oo.api.exception.OneOpsClientAPIException;
import com.wm.bfd.oo.exception.BFDOOException;

public class Main {

  public static void main(String[] args) throws BFDOOException, OneOpsClientAPIException,
      ParseException {
    BooCli cli = new BooCli(args);
    cli.parse();
  }
}