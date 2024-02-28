package dataExtraction.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Description:</p>
 *
 * @author chenzhitao
 * @date 2022年11月28日
 * 响应结果类
 */
@Schema(title = "接口返回通用状态")
@Data
@AllArgsConstructor
public class ResultObject {
    @Schema(title = "状态码", description = "返回状态码，0为无异常，大于0则为自定义异常编号，具体定义参见ResultState")
    private int state;

    @Schema(title = "返回消息", description = "一般返回异常消息")
    private String message;

    @Schema(title = "返回结果数据")
    private Object data;

    public static ResultObject success() {
        ResultObject resultObject = new ResultObject();
        resultObject.setState(ResultState.SUCCESS);
        return resultObject;
    }

    public static ResultObject success(Object date) {
        ResultObject resultObject = new ResultObject();
        resultObject.setState(ResultState.SUCCESS);
        resultObject.setData(date);
        return resultObject;
    }

    public static ResultObject fail() {
        ResultObject resultObject = new ResultObject();
        resultObject.setState(ResultState.FAIL);
        return resultObject;
    }

    public static ResultObject fail(Object date) {
        ResultObject resultObject = new ResultObject();
        resultObject.setState(ResultState.FAIL);
        resultObject.setData(date);
        return resultObject;
    }

    public static ResultObject fail(String message, Object date) {
        ResultObject resultObject = new ResultObject();
        resultObject.setState(ResultState.FAIL);
        resultObject.setMessage(message);
        resultObject.setData(date);
        return resultObject;
    }

    public static ResultObject blank() {
        ResultObject resultObject = new ResultObject();
        resultObject.setState(ResultState.BLANK);
        return resultObject;
    }
    public ResultObject(int state) {
        this.state = state;
    }

    public ResultObject() {
        this.state = ResultState.SUCCESS;
    }

    public ResultObject(Builder builder) {
        this.state = builder.state;
        this.message = builder.message;
        this.data = builder.data;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static class Builder {
        private int state;
        private String message;
        private Object data;

        public Builder() {
            this.state = ResultState.SUCCESS;
        }

        public Builder state(int state) {
            this.state = state;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public ResultObject build() {
            return new ResultObject(this);
        }
    }
}
