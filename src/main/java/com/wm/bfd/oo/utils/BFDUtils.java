package com.wm.bfd.oo.utils;

import com.wm.bfd.oo.ClientConfig;
import com.wm.bfd.oo.exception.BFDOOException;

public class BFDUtils {

    public boolean verifyTemplate(ClientConfig config) throws BFDOOException {
	if (config == null || config.getConfig() == null
		|| config.getConfig().getAssembly() == null) {
	    throw new BFDOOException(
		    "The template file not found or has wrong format!");
	}
	return false;
    }
}
