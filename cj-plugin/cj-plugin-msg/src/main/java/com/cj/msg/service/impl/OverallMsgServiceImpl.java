package com.cj.msg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.msg.entity.OverallMsgInsertReq;
import com.cj.msg.entity.OverallMsgQueryReq;
import com.cj.msg.mapper.OverallMsgMapper;
import com.cj.msg.entity.OverallMsg;
import com.cj.msg.service.OverallMsgService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局消息管理(OverallMsg)表服务实现类
 *
 * @author makejava
 * @since 2024-04-19 16:30:39
 */
@Service("overallMsgService")
public class OverallMsgServiceImpl extends ServiceImpl<OverallMsgMapper, OverallMsg> implements OverallMsgService {

    @Override
    public RestResponse selectCount() {
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        Long count = this.lambdaQuery().eq(OverallMsg::getReceiveUser, saBaseLoginUser.getId()).eq(OverallMsg::getIsRead,0).count();
        if(null==count){
            return RestResponse.ok(0);
        }else{
            return RestResponse.ok(count);
        }
    }

    @Override
    public RestResponse selectInfoSubjectList() {
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        List<OverallMsg> list = this.lambdaQuery().eq(OverallMsg::getReceiveUser, saBaseLoginUser.getId()).eq(OverallMsg::getIsRead,0).list();
        if(null!=list && list.size()>0){
            List<String> strings = list.stream().map(OverallMsg::getSubject).collect(Collectors.toList());
            return RestResponse.ok(strings);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    public RestResponse selectDetailsList(String date) {
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        List<OverallMsg> list = this.lambdaQuery().eq(OverallMsg::getReceiveUser, saBaseLoginUser.getId()).apply("TO_CHAR(CREATE_TIME,'YYYY-MM-DD') = '"+date+"'")
        .orderByDesc(OverallMsg::getCreateTime).list();
        if(null!=list && list.size()>0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    public RestResponse editReadStatus(String id) {
        boolean update = this.lambdaUpdate().set(OverallMsg::getIsRead, 1).set(OverallMsg::getReadTime, new Date()).eq(OverallMsg::getId, id).update();
        if(update){
            return RestResponse.ok();
        }else {
            return RestResponse.no("失败");
        }
    }

    @Override
    public RestResponse insert(OverallMsgInsertReq req) {
        OverallMsg msg = new OverallMsg();
        msg.setId(UUIDUtils.getUUID());
        msg.setCategory("预警");
        msg.setContent(req.getContent());
        msg.setCreateTime(new Date());
        msg.setIsRead(0);
        return RestResponse.ok(this.save(msg));
    }

    @Override
    public RestResponse query(OverallMsgQueryReq req) {
        IPage<OverallMsg> page = new Page<>(req.getPageNo(), req.getPageSize());
        return RestResponse.ok(this.lambdaQuery()
                .eq(OverallMsg::getCategory, "告警")
                .ge(req.getStarTime() != null , OverallMsg::getCreateTime, req.getStarTime())
                .le(req.getEndTime() != null , OverallMsg::getCreateTime, req.getEndTime())
                .page(page));
    }
}

