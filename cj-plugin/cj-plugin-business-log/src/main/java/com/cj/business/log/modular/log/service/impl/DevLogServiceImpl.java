
package com.cj.business.log.modular.log.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cj.business.log.modular.log.enums.DevLogCategoryEnum;
import com.cj.business.log.modular.log.mapper.DevLogMapper;
import com.cj.business.log.modular.log.service.DevLogService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import com.cj.common.enums.CommonSortOrderEnum;
import com.cj.common.page.CommonPageRequest;
import com.cj.business.log.modular.log.entity.DevLog;
import com.cj.business.log.modular.log.param.DevLogDeleteParam;
import com.cj.business.log.modular.log.param.DevLogPageParam;
import com.cj.business.log.modular.log.result.DevLogOpBarChartDataResult;
import com.cj.business.log.modular.log.result.DevLogOpPieChartDataResult;
import com.cj.business.log.modular.log.result.DevLogVisLineChartDataResult;
import com.cj.business.log.modular.log.result.DevLogVisPieChartDataResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 * 日志Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/9/2 15:05
 */
@Service
public class DevLogServiceImpl extends ServiceImpl<DevLogMapper, DevLog> implements DevLogService {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Page<DevLog> page(DevLogPageParam devLogPageParam) {
        QueryWrapper<DevLog> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(devLogPageParam.getCategory())) {
            queryWrapper.lambda().eq(DevLog::getCategory, devLogPageParam.getCategory());
        }
        if(ObjectUtil.isNotEmpty(devLogPageParam.getSearchKey())) {
            queryWrapper.lambda().like(DevLog::getName, devLogPageParam.getSearchKey());
        }
        if(ObjectUtil.isAllNotEmpty(devLogPageParam.getSortField(), devLogPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(devLogPageParam.getSortOrder());
            queryWrapper.orderBy(true, devLogPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(devLogPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByDesc(DevLog::getCreateTime);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public void delete(DevLogDeleteParam devLogDeleteParam) {
        this.remove(new LambdaQueryWrapper<DevLog>().eq(DevLog::getCategory, devLogDeleteParam.getCategory()));
    }

    @Override
    public List<DevLogVisLineChartDataResult> visLogLineChartData() {
        Date now = new Date();
        Date lastWeek = calculateTime(now,-24);
        List<DevLog> list = this.lambdaQuery().between(DevLog::getOpTime, sdf.format(lastWeek), sdf.format(now)).list();
        Map<String, List<JSONObject>> listMap =list.stream().map(devLog -> JSONUtil.parseObj(devLog).set("date", DateUtil.formatDate(devLog.getOpTime())))
                .collect(Collectors.groupingBy(jsonObject -> jsonObject.getStr("date")));
        long between = DateUtil.between(lastWeek, now, DateUnit.DAY);
        List<DevLogVisLineChartDataResult> resultList = CollectionUtil.newArrayList();
        for(int i = 1; i<= between; i++) {
            DevLogVisLineChartDataResult devLogVisLineChartDataResult = new DevLogVisLineChartDataResult();
            String date = DateUtil.formatDate(DateUtil.offsetDay(lastWeek, i));
            devLogVisLineChartDataResult.setDate(date);
            List<JSONObject> jsonObjectList = listMap.get(date);
            if(ObjectUtil.isNotEmpty(jsonObjectList)) {
                devLogVisLineChartDataResult.setLoginCount(jsonObjectList.stream().filter(jsonObject -> jsonObject.getStr("category")
                        .equals(DevLogCategoryEnum.LOGIN.getValue())).count());
                devLogVisLineChartDataResult.setLogoutCount(jsonObjectList.stream().filter(jsonObject -> jsonObject.getStr("category")
                        .equals(DevLogCategoryEnum.LOGOUT.getValue())).count());
            } else {
                devLogVisLineChartDataResult.setLoginCount(0L);
                devLogVisLineChartDataResult.setLogoutCount(0L);
            }
            resultList.add(devLogVisLineChartDataResult);
        }
        return resultList;
    }

    @Override
    public List<DevLogVisPieChartDataResult> visLogPieChartData() {
        List<DevLogVisPieChartDataResult> resultList = CollectionUtil.newArrayList();
        DevLogVisPieChartDataResult devLogLoginPieChartDataResult = new DevLogVisPieChartDataResult();
        devLogLoginPieChartDataResult.setType("登录");
        devLogLoginPieChartDataResult.setValue(this.count(new LambdaQueryWrapper<DevLog>()
                .eq(DevLog::getCategory, DevLogCategoryEnum.LOGIN.getValue())));
        resultList.add(devLogLoginPieChartDataResult);

        DevLogVisPieChartDataResult devLogLogoutPieChartDataResult = new DevLogVisPieChartDataResult();
        devLogLogoutPieChartDataResult.setType("登出");
        devLogLogoutPieChartDataResult.setValue(this.count(new LambdaQueryWrapper<DevLog>()
                .eq(DevLog::getCategory, DevLogCategoryEnum.LOGOUT.getValue())));
        resultList.add(devLogLogoutPieChartDataResult);
        return resultList;
    }

    @Override
    public List<DevLogOpBarChartDataResult> opLogBarChartData() {
        Date now = new Date();
        Date lastWeek = getWeekByDate(now);
        List<DevLog> list = this.lambdaQuery().between(DevLog::getOpTime, sdf.format(lastWeek), sdf.format(now)).select(DevLog::getOpTime,DevLog::getCategory).list();
        Map<String, List<JSONObject>> listMap =list.stream().map(devLog -> JSONUtil.parseObj(devLog).set("date", DateUtil.formatDate(devLog.getOpTime())))
                .collect(Collectors.groupingBy(jsonObject -> jsonObject.getStr("date")));
        long between = DateUtil.between(lastWeek, now, DateUnit.DAY);
        List<DevLogOpBarChartDataResult> resultList = CollectionUtil.newArrayList();
        for(int i = 1; i<= between; i++) {
            String date = DateUtil.formatDate(DateUtil.offsetDay(lastWeek, i));
            DevLogOpBarChartDataResult devLogOperateBarChartDataResult = new DevLogOpBarChartDataResult();
            devLogOperateBarChartDataResult.setDate(date);
            devLogOperateBarChartDataResult.setName("操作日志");
            DevLogOpBarChartDataResult devLogExceptionBarChartDataResult = new DevLogOpBarChartDataResult();
            devLogExceptionBarChartDataResult.setDate(date);
            devLogExceptionBarChartDataResult.setName("异常日志");
            List<JSONObject> jsonObjectList = listMap.get(date);
            if(ObjectUtil.isNotEmpty(jsonObjectList)) {
                devLogOperateBarChartDataResult.setCount(jsonObjectList.stream().filter(jsonObject -> jsonObject.getStr("category")
                        .equals(DevLogCategoryEnum.OPERATE.getValue())).count());
                devLogExceptionBarChartDataResult.setCount(jsonObjectList.stream().filter(jsonObject -> jsonObject.getStr("category")
                        .equals(DevLogCategoryEnum.EXCEPTION.getValue())).count());
            } else {
                devLogOperateBarChartDataResult.setCount(0L);
                devLogExceptionBarChartDataResult.setCount(0L);
            }
            resultList.add(devLogOperateBarChartDataResult);
            resultList.add(devLogExceptionBarChartDataResult);
        }
        return resultList;
    }

    @Override
    public List<DevLogOpPieChartDataResult> opLogPieChartData() {
        List<DevLogOpPieChartDataResult> resultList = CollectionUtil.newArrayList();
        DevLogOpPieChartDataResult devLogOperatePieChartDataResult = new DevLogOpPieChartDataResult();
        devLogOperatePieChartDataResult.setType("操作日志");
        devLogOperatePieChartDataResult.setValue(this.count(new LambdaQueryWrapper<DevLog>()
                .eq(DevLog::getCategory, DevLogCategoryEnum.OPERATE.getValue())));
        resultList.add(devLogOperatePieChartDataResult);

        DevLogOpPieChartDataResult devLogExceptionPieChartDataResult = new DevLogOpPieChartDataResult();
        devLogExceptionPieChartDataResult.setType("异常日志");
        devLogExceptionPieChartDataResult.setValue(this.count(new LambdaQueryWrapper<DevLog>()
                .eq(DevLog::getCategory, DevLogCategoryEnum.EXCEPTION.getValue())));
        resultList.add(devLogExceptionPieChartDataResult);
        return resultList;
    }

    @SneakyThrows
    private Date getWeekByDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if(1== dayWeek){
            cal.add(Calendar.DAY_OF_MONTH,-1);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DATE,cal.getFirstDayOfWeek()-day);
        Date time = cal.getTime();
        return sdf.parse(sdf.format(time));
    }

    private Date calculateTime(Date time,int hour){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.HOUR,hour);
        Date date = calendar.getTime();
        return date;
    }
}
