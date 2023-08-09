
package com.cj.gen.modular.basic.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.gen.modular.basic.entity.GenBasic;
import com.cj.gen.modular.basic.result.GenBasicMobileModuleSelectorResult;
import com.cj.gen.modular.basic.result.GenBasicPreviewResult;
import com.cj.gen.modular.basic.param.*;
import com.cj.gen.modular.basic.result.GenBasicTableColumnResult;
import com.cj.gen.modular.basic.result.GenBasicTableResult;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 代码生成基础Service接口
 *
 * @author yubaoshan
 * @date 2022/10/25 22:33
 **/
public interface GenBasicService extends IService<GenBasic> {

    /**
     * 查询代码生成基础分页
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     */
    Page<GenBasic> page(GenBasicPageParam genBasicPageParam);

    /**
     * 添加代码生成基础
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     */
    GenBasic add(GenBasicAddParam genBasicAddParam);

    /**
     * 编辑代码生成基础
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     */
    GenBasic edit(GenBasicEditParam genBasicEditParam);

    /**
     * 删除代码生成基础
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     */
    void delete(List<GenBasicIdParam> genBasicIdParamList);

    /**
     * 获取代码生成基础详情
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     */
    GenBasic detail(GenBasicIdParam genBasicIdParam);

    /**
     * 获取代码生成基础详情
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     **/
    GenBasic queryEntity(String id);

    /**
     * 获取所有表信息
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     **/
    List<GenBasicTableResult> tables();

    /**
     * 获取表内所有字段信息
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     **/
    List<GenBasicTableColumnResult> tableColumns(GenBasicTableColumnParam genBasicTableColumnParam);

    /**
     * 执行代码生成
     *
     * @author xuyuxiang yubaoshan
     * @date 2022/10/28 9:37
     **/
    void execGenZip(GenBasicIdParam genBasicIdParam, HttpServletResponse response) throws IOException;

    /**
     * 执行代码生成
     *
     * @author xuyuxiang
     * @date 2022/10/28 9:37
     **/
    void execGenPro(GenBasicIdParam genBasicIdParam, HttpServletResponse response) throws IOException;

    /**
     * 预览代码生成
     *
     * @author xuyuxiang
     * @date 2022/10/28 17:08
     **/
    GenBasicPreviewResult previewGen(GenBasicIdParam genBasicIdParam);

}
