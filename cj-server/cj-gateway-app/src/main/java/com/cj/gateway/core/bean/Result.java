package com.cj.gateway.core.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {
    private Boolean success;
    private Integer code;
    private T data;
    private String message;
    private Throwable throwable;

    public static <T> Result<T> succeed() {
        return of(true,  null, 200, "成功", (Throwable) null);
    }

    public static <T> Result<T> succeed(String msg) {
        return of(true,  null, 200, msg, (Throwable) null);
    }

    public static <T> Result<T> succeed(T data, String msg) {
        return of(true, data, 200, msg, (Throwable) null);
    }

    public static <T> Result<T> succeed(T data) {
        return of(true, data, 200, (String) null, (Throwable) null);
    }

    public static <T> Result<T> of(Boolean success, T data, Integer code, String msg, Throwable t) {
        return new Result(success, code, data, msg, t);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static <T> Result<T> failed(String msg) {
        return of(false,  null, 500, msg, (Throwable) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static <T> Result<T> failed(Integer code, String msg) {
        return of(false,  null, code, msg, (Throwable) null);
    }

    public static <T> Result<T> failed(T data, String msg) {
        return of(false, data, 500, msg, (Throwable) null);
    }

    public static <T> Result<T> failed(Integer code, String msg, Throwable t) {
        return of(false, null, code, msg, t);
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public Integer getCode() {
        return this.code;
    }

    public T getData() {
        return this.data;
    }

    public String getMessage() {
        return this.message;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Result)) {
            return false;
        } else {
            Result<?> other = (Result) o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label71:
                {
                    Object this$success = this.getSuccess();
                    Object other$success = other.getSuccess();
                    if (this$success == null) {
                        if (other$success == null) {
                            break label71;
                        }
                    } else if (this$success.equals(other$success)) {
                        break label71;
                    }

                    return false;
                }

                Object this$code = this.getCode();
                Object other$code = other.getCode();
                if (this$code == null) {
                    if (other$code != null) {
                        return false;
                    }
                } else if (!this$code.equals(other$code)) {
                    return false;
                }

                label57:
                {
                    Object this$data = this.getData();
                    Object other$data = other.getData();
                    if (this$data == null) {
                        if (other$data == null) {
                            break label57;
                        }
                    } else if (this$data.equals(other$data)) {
                        break label57;
                    }

                    return false;
                }

                Object this$message = this.getMessage();
                Object other$message = other.getMessage();
                if (this$message == null) {
                    if (other$message != null) {
                        return false;
                    }
                } else if (!this$message.equals(other$message)) {
                    return false;
                }

                Object this$throwable = this.getThrowable();
                Object other$throwable = other.getThrowable();
                if (this$throwable == null) {
                    if (other$throwable == null) {
                        return true;
                    }
                } else if (this$throwable.equals(other$throwable)) {
                    return true;
                }

                return false;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Result;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $success = this.getSuccess();
        result = result * 59 + ($success == null ? 43 : $success.hashCode());
        Object $code = this.getCode();
        result = result * 59 + ($code == null ? 43 : $code.hashCode());
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        Object $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        Object $throwable = this.getThrowable();
        result = result * 59 + ($throwable == null ? 43 : $throwable.hashCode());
        return result;
    }

    public String toString() {
        return "Result(success=" + this.getSuccess() + ", code=" + this.getCode() + ", data=" + this.getData() + ", message=" + this.getMessage() + ", throwable=" + this.getThrowable() + ")";
    }

    public Result() {
    }

    public Result(Boolean success, Integer code, T data, String message, Throwable throwable) {
        this.success = success;
        this.code = code;
        this.data = data;
        this.message = message;
        this.throwable = throwable;
    }
}