
package com.cj.sys.modular.resource.provider;

import org.springframework.stereotype.Service;
import com.cj.sys.api.SysButtonApi;
import com.cj.sys.modular.resource.service.SysButtonService;

import javax.annotation.Resource;

/**
 * 按钮API接口实现类
 *
 * @author xuyuxiang
 * @date 2022/11/1 13:50
 **/
@Service
public class SysButtonApiProvider implements SysButtonApi {

    @Resource
    private SysButtonService sysButtonService;

    @Override
    public void addForGenButton(String menuId, String className, String functionName) {
        sysButtonService.addForGenButton(menuId, className, functionName);
    }
}
