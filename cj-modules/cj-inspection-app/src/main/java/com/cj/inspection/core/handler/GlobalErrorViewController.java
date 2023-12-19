package com.cj.inspection.core.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cj.common.exception.CommonException;
import com.cj.common.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常页面处理器，覆盖默认的Whitelabel Error Page
 *
 * @author xuyuxiang
 * @date 2022/2/11 15:41
 **/
@Slf4j
@RestController
public class GlobalErrorViewController {

    /**
     * Error页面视图，直接响应JSON
     *
     * @author xuyuxiang
     * @date 2022/2/11 16:11
     **/
    @RequestMapping("/errorView")
    public CommonResult<String> globalError(HttpServletRequest request) {
        CommonResult<String> commonResult = new CommonResult<>(404, "路径不存在", null);
        Object model = request.getAttribute("model");
        if(ObjectUtil.isNotEmpty(model)) {
            if(model instanceof Exception){
                if(model instanceof CommonException) {
                    JSONObject errorObj = JSONUtil.parseObj(model);
                    Integer code = errorObj.getInt("code");
                    String msg = errorObj.getStr("msg");
                    if(ObjectUtil.isAllNotEmpty(code, msg)) {
                        commonResult.setCode(code).setMsg(msg);
                    } else if(ObjectUtil.isNotEmpty(msg)) {
                        commonResult = CommonResult.error(msg);
                    } else {
                        commonResult = CommonResult.error();
                    }
                } else {
                    commonResult = CommonResult.error();
                    log.error(">>> 服务器未知异常，具体信息：", (Exception) model);
                }
            }
        }
        return commonResult;
    }
}
