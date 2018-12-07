package uk.gov.dft.bluebadge.service.printservice.generated.controller;

@SuppressWarnings("squid:S1068")
public class ApiException extends Exception {
  private int code;

  public ApiException(int code, String msg) {
    super(msg);
    this.code = code;
  }
}
