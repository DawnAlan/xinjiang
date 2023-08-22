/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package com.cj.textua.textua.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.textua.textua.entity.FiducialBase;
import com.cj.textua.textua.param.FiducialBaseAddParam;
import com.cj.textua.textua.param.FiducialBaseEditParam;
import com.cj.textua.textua.param.FiducialBaseIdParam;
import com.cj.textua.textua.param.FiducialBasePageParam;

import java.util.List;

/**
 * 测点信息基本表Service接口
 *
 * @author yancheng
 * @date  2023/08/21 20:32
 **/
public interface FiducialBaseService extends IService<FiducialBase> {

    /**
     * 获取测点信息基本表分页
     *
     * @author yancheng
     * @date  2023/08/21 20:32
     */
    Page<FiducialBase> page(FiducialBasePageParam fiducialBasePageParam);

    /**
     * 添加测点信息基本表
     *
     * @author yancheng
     * @date  2023/08/21 20:32
     */
    void add(FiducialBaseAddParam fiducialBaseAddParam);

    /**
     * 编辑测点信息基本表
     *
     * @author yancheng
     * @date  2023/08/21 20:32
     */
    void edit(FiducialBaseEditParam fiducialBaseEditParam);

    /**
     * 删除测点信息基本表
     *
     * @author yancheng
     * @date  2023/08/21 20:32
     */
    void delete(List<FiducialBaseIdParam> fiducialBaseIdParamList);

    /**
     * 获取测点信息基本表详情
     *
     * @author yancheng
     * @date  2023/08/21 20:32
     */
    FiducialBase detail(FiducialBaseIdParam fiducialBaseIdParam);

    /**
     * 获取测点信息基本表详情
     *
     * @author yancheng
     * @date  2023/08/21 20:32
     **/
    FiducialBase queryEntity(String id);
}
