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
package com.cj.data.modular.artdata.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.data.api.artdata.entity.ArtdataColumnconfig;
import com.cj.data.api.artdata.param.ArtdataColumnconfigAddParam;
import com.cj.data.api.artdata.param.ArtdataColumnconfigEditParam;
import com.cj.data.api.artdata.param.ArtdataColumnconfigIdParam;
import com.cj.data.api.artdata.param.ArtdataColumnconfigPageParam;

import java.util.List;

/**
 * 格式配置表Service接口
 *
 * @author dd
 * @date  2024/01/12 17:23
 **/
public interface ArtdataColumnconfigService extends IService<ArtdataColumnconfig> {

    /**
     * 获取格式配置表分页
     *
     * @author dd
     * @date  2024/01/12 17:23
     */
    Page<ArtdataColumnconfig> page(ArtdataColumnconfigPageParam artdataColumnconfigPageParam);

    /**
     * 添加格式配置表
     *
     * @author dd
     * @date  2024/01/12 17:23
     */
    void add(ArtdataColumnconfigAddParam artdataColumnconfigAddParam);

    /**
     * 编辑格式配置表
     *
     * @author dd
     * @date  2024/01/12 17:23
     */
    void edit(ArtdataColumnconfigEditParam artdataColumnconfigEditParam);

    /**
     * 删除格式配置表
     *
     * @author dd
     * @date  2024/01/12 17:23
     */
    void delete(List<ArtdataColumnconfigIdParam> artdataColumnconfigIdParamList);

    /**
     * 获取格式配置表详情
     *
     * @author dd
     * @date  2024/01/12 17:23
     */
    ArtdataColumnconfig detail(ArtdataColumnconfigIdParam artdataColumnconfigIdParam);

    /**
     * 获取格式配置表详情
     *
     * @author dd
     * @date  2024/01/12 17:23
     **/
    ArtdataColumnconfig queryEntity(String id);
}
