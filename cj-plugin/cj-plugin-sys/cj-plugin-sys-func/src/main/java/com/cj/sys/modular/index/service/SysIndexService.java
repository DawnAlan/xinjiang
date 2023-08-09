
package com.cj.sys.modular.index.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.cj.common.pojo.CommonValidList;
import com.cj.sys.modular.index.param.*;
import com.cj.sys.modular.index.result.*;

import java.util.List;

/**
 * 系统首页Service接口
 *
 * @author xuyuxiang
 * @date 2022/9/2 10:45
 */
public interface SysIndexService {

    /**
     * 添加当前用户日程
     *
     * @author xuyuxiang
     * @date 2022/9/2 11:13
     */
    void addSchedule(SysIndexScheduleAddParam sysIndexScheduleAddParam);

    /**
     * 删除日程
     *
     * @author xuyuxiang
     * @date 2022/9/2 11:32
     */
    void deleteSchedule(CommonValidList<SysIndexScheduleIdParam> sysIndexScheduleIdParamList);

    /**
     * 获取当前用户日程列表
     *
     * @author xuyuxiang
     * @date 2022/9/2 11:23
     */
    List<SysIndexScheduleListResult> scheduleList(SysIndexScheduleListParam sysIndexScheduleListParam);

    /**
     * 获取当前用户站内信列表
     *
     * @author xuyuxiang
     * @date 2022/9/2 11:36
     */
    List<SysIndexMessageListResult> messageList(SysIndexMessageListParam sysIndexMessageListParam);

    /**
     * 获取站内信详情
     *
     * @author xuyuxiang
     * @date 2022/9/2 11:44
     */
    SysIndexMessageDetailResult messageDetail(SysIndexMessageIdParam sysIndexMessageIdParam);

    /**
     * 站内信全部标记已读
     *
     * @author diantu
     * @date 2023/7/10
     */
    void allMessageMarkRead();

    /**
     * 获取当前用户访问日志列表
     *
     * @author xuyuxiang
     * @date 2022/9/4 15:11
     */
    List<SysIndexVisLogListResult> visLogList();

    /**
     * 获取当前用户操作日志列表
     *
     * @author xuyuxiang
     * @date 2022/9/4 15:11
     */
    List<SysIndexOpLogListResult> opLogList();

    /**
     * 创建连接
     *
     * @author diantu
     * @date 2023/7/10
     **/
    public SseEmitter createSseConnect(String clientId);
}
