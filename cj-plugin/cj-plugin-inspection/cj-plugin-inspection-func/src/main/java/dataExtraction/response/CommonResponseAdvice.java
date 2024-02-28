package dataExtraction.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Description:</p>
 *
 * @author chenzhitao
 * @date 2022年11月28日
 */
@RestControllerAdvice(basePackages = "griddataanalyze.controller")
public class CommonResponseAdvice implements ResponseBodyAdvice {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body != null) {
            if (body instanceof Boolean) {
                if ((Boolean) body) {
                    return ResultObject.success();
                }
                return ResultObject.fail();
            }
            if (body instanceof String) {
                return ResultObject.success(body);
            }
            return ResultObject.success(body);
        }
        return ResultObject.blank();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultObject handlerGlobeException(HttpServletRequest request, Exception e) {
        logger.error("结果异常", e);
        return ResultObject.fail(e.getMessage(), null);
    }
}
