
package com.cj.dev.modular.monitor.service;

import com.cj.dev.modular.monitor.result.DevMonitorServerResult;

/**
 * 监控Service接口
 *
 * @author xuyuxiang
 * @date 2022/9/1 15:59
 */
public interface DevMonitorService {

    /**
     * 获取服务器监控信息
     *
     * @author xuyuxiang
     * @date 2022/9/1 16:02
     */
    DevMonitorServerResult serverInfo();
}
