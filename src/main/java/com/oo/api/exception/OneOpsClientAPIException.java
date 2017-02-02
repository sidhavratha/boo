package com.oo.api.exception;

public class OneOpsClientAPIException extends Exception {

  private static final long serialVersionUID = 1L;

  public OneOpsClientAPIException() {
    super();
  }

  public OneOpsClientAPIException(String message) {
    super(message);
  }

  public OneOpsClientAPIException(Throwable cause) {
    super(cause);
  }

  public OneOpsClientAPIException(String message, Throwable cause) {
    super(message, cause);
  }

}
