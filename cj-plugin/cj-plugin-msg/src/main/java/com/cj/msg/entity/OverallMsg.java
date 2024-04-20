package com.cj.msg.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 全局消息管理(OverallMsg)表实体类
 *
 * @author makejava
 * @since 2024-04-19 16:30:38
 */
@Data
public class OverallMsg extends Model<OverallMsg> {
    //主键ID  
    private String id;
    //分类
    private String category;
    //主题
    private String subject;
    //正文
    private String content;
    //扩展信息
    private String extJson;
    //创建时间
    private Date createTime;
    //创建用户
    private String createUser;
    //接收用户
    private String receiveUser;
    //已读状态（0-未读 1-已读）
    private Integer isRead;
    //已读时间
    private Date readTime;
}

