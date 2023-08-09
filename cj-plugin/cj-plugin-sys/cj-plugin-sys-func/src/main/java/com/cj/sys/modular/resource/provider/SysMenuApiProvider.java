
package com.cj.sys.modular.resource.provider;

import org.springframework.stereotype.Service;
import com.cj.sys.api.SysMenuApi;
import com.cj.sys.modular.resource.service.SysMenuService;

import javax.annotation.Resource;

/**
 * 菜单API接口实现类
 *
 * @author xuyuxiang
 * @date 2022/11/1 13:50
 **/
@Service
public class SysMenuApiProvider implements SysMenuApi {

    @Resource
    private SysMenuService sysMenuService;

    @Override
    public String addForGenMenu(String parentId, String busName, String module, String title, String path) {
        return sysMenuService.addForGenMenu(parentId, busName, title, module, path);
    }
}
