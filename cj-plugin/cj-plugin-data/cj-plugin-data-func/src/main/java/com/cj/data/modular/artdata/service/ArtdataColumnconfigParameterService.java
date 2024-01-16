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
import com.cj.data.api.artdata.entity.ArtdataColumnconfigParameter;
import com.cj.data.api.artdata.param.ArtdataColumnconfigParameterAddParam;
import com.cj.data.api.artdata.param.ArtdataColumnconfigParameterEditParam;
import com.cj.data.api.artdata.param.ArtdataColumnconfigParameterIdParam;
import com.cj.data.api.artdata.param.ArtdataColumnconfigParameterPageParam;

import java.util.List;

/**
 * 模板列参数表Service接口
 *
 * @author dd
 * @date  2024/01/12 17:25
 **/
public interface ArtdataColumnconfigParameterService extends IService<ArtdataColumnconfigParameter> {

    /**
     * 获取模板列参数表分页
     *
     * @author dd
     * @date  2024/01/12 17:25
     */
    Page<ArtdataColumnconfigParameter> page(ArtdataColumnconfigParameterPageParam artdataColumnconfigParameterPageParam);

    /**
     * 添加模板列参数表
     *
     * @author dd
     * @date  2024/01/12 17:25
     */
    void add(ArtdataColumnconfigParameterAddParam artdataColumnconfigParameterAddParam);

    /**
     * 编辑模板列参数表
     *
     * @author dd
     * @date  2024/01/12 17:25
     */
    void edit(ArtdataColumnconfigParameterEditParam artdataColumnconfigParameterEditParam);

    /**
     * 删除模板列参数表
     *
     * @author dd
     * @date  2024/01/12 17:25
     */
    void delete(List<ArtdataColumnconfigParameterIdParam> artdataColumnconfigParameterIdParamList);

    /**
     * 获取模板列参数表详情
     *
     * @author dd
     * @date  2024/01/12 17:25
     */
    ArtdataColumnconfigParameter detail(ArtdataColumnconfigParameterIdParam artdataColumnconfigParameterIdParam);

    /**
     * 获取模板列参数表详情
     *
     * @author dd
     * @date  2024/01/12 17:25
     **/
    ArtdataColumnconfigParameter queryEntity(String id);
}
