package com.cj.common.model;

import lombok.Setter;
import org.springframework.validation.Errors;


public class RestResponse<T> {

  private Status status;

  @Setter
  private String msg;
  @Setter
  private Object obj;

  private T data;


  public RestResponse<T> setMsg(String msg) {
    this.msg = msg;
    return this;
  }
  public RestResponse<T> setObj(Object obj) {
    this.obj = obj;
    return this;
  }

  public T getData() {
    return data;
  }

  public RestResponse<T> setData(T data) {
    this.data = data;
    return this;
  }

  public int getCode() {
    return status.getCode();
  }

  public String getMsg() {
    if (msg == null) {
      return status.getDescript();
    }
    return msg;
  }

  protected RestResponse(Status status) {
    this.status = status;
  }

  protected RestResponse() {
  }

  // ----------------- 静态方法 -----------------------

  public static <T> RestResponse<T> ok() {
    return ok(null);
  }


  public static <T> RestResponse<T> ok(T data) {
    final RestResponse<T> ok = new RestResponse<>(Status.SUCCESS);
    ok.setData(data);
    return ok;
  }

  public static <T> RestResponse<T> no(Status status) {
    if (status.equals(Status.SUCCESS)) {
      throw new IllegalArgumentException("只能传递异常的Status");
    }
    return new RestResponse<>(status);
  }

  public static <T> RestResponse<T> no(Status status, String message) {
    final RestResponse<T> no = RestResponse.no(status);
    no.setMsg(message);
    return no;
  }

  public static <T> RestResponse<T> no(Status status, T data,String msg) {
    final RestResponse<T> no = RestResponse.no(status);
    no.setMsg(msg);
    no.setData(data);
    return no;
  }

  public static <T> RestResponse<T> no(String message) {
    // 使用什么status实例不影响,除了code值不一样
    return RestResponse.no(Status.ERROR, message);
  }

  public static <T> RestResponse<T> no(Errors errors) {
    if (!errors.hasErrors()) {
      throw new IllegalArgumentException("errors.hasErrors() must be true");
    }
    return RestResponse.no(Status.PARAM_ERROR,
        errors.getFieldError() == null ? null :
            errors.getFieldError().getDefaultMessage());
  }
}

