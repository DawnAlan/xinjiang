
package com.cj.sys.api;

/**
 * 菜单API
 *
 * @author xuyuxiang
 * @date 2022/11/1 13:44
 **/
public interface SysMenuApi {

    /**
     * 代码生成菜单插入
     *
     * @author xuyuxiang
     * @date 2022/11/1 13:48
     **/
    String addForGenMenu(String parentId, String busName, String module, String title, String path);
}
