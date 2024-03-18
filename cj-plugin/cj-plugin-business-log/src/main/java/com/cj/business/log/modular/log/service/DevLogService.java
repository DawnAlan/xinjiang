
package com.cj.business.log.modular.log.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.business.log.modular.log.entity.DevLog;
import com.cj.business.log.modular.log.param.DevLogDeleteParam;
import com.cj.business.log.modular.log.param.DevLogPageParam;
import com.cj.business.log.modular.log.result.DevLogOpBarChartDataResult;
import com.cj.business.log.modular.log.result.DevLogOpPieChartDataResult;
import com.cj.business.log.modular.log.result.DevLogVisLineChartDataResult;
import com.cj.business.log.modular.log.result.DevLogVisPieChartDataResult;


import java.util.List;

/**
 * 日志Service接口
 *
 * @author xuyuxiang
 * @date 2022/9/2 15:04
 */
public interface DevLogService extends IService<DevLog> {

    /**
     * 获取日志分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<DevLog> page(DevLogPageParam devLogPageParam);

    /**
     * 清空日志
     *
     * @author xuyuxiang
     * @date 2022/9/6 13:17
     */
    void delete(DevLogDeleteParam devLogDeleteParam);

    /**
     * 获取访问日志折线图数据
     *
     * @author xuyuxiang
     * @date 2022/9/4 21:18
     */
    List<DevLogVisLineChartDataResult> visLogLineChartData();

    /**
     * 获取访问日志饼状图数据
     *
     * @author xuyuxiang
     * @date 2022/9/4 21:18
     */
    List<DevLogVisPieChartDataResult> visLogPieChartData();

    /**
     * 获取操作日志柱状图数据
     *
     * @author xuyuxiang
     * @date 2022/9/4 21:18
     */
    List<DevLogOpBarChartDataResult> opLogBarChartData();

    /**
     * 获取操作日志饼状图数据
     *
     * @author xuyuxiang
     * @date 2022/9/4 21:18
     */
    List<DevLogOpPieChartDataResult> opLogPieChartData();
}
