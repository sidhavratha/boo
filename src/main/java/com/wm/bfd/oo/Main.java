package com.wm.bfd.oo;

import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oo.api.exception.OneOpsClientAPIException;
import com.wm.bfd.oo.exception.BFDOOException;

public class Main {

  final private static Logger LOG = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws BFDOOException, OneOpsClientAPIException,
      ParseException {
    BooCli cli = new BooCli(args);
    cli.parse();
  }

}
