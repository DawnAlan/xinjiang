package com.cj.msg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.msg.entity.OverallMsg;
import com.cj.msg.entity.OverallMsgInsertReq;
import com.cj.msg.entity.OverallMsgQueryReq;

/**
 * 全局消息管理(OverallMsg)表服务接口
 *
 * @author makejava
 * @since 2024-04-19 16:30:38
 */
public interface OverallMsgService extends IService<OverallMsg> {

    RestResponse selectCount();

    RestResponse selectInfoSubjectList();

    RestResponse selectDetailsList(String date);

    RestResponse editReadStatus(String id);


    RestResponse insert(OverallMsgInsertReq req);

    RestResponse query(OverallMsgQueryReq req);
}

