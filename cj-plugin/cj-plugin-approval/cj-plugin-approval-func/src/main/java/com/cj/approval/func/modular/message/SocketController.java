package com.cj.approval.func.modular.message;

import com.cj.approval.func.core.utils.WebSocketServer;
import com.cj.common.model.RestResponse;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "socket")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@RestController
@Validated
@RequestMapping("socket")
public class SocketController {

    @ApiOperationSupport(order = 2)
    @ApiOperation("发送消息")
    @GetMapping("/sendMessage")
    public void sendMessage(@RequestParam("id") String id) {
        try {
            WebSocketServer.sendInfo("hello world",id);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
