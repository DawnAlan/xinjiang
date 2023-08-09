
package com.cj.gen.modular.basic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import com.cj.common.pojo.CommonEntity;

/**
 * 代码生成基础
 *
 * @author yubaoshan
 * @date 2022/10/25 22:33
 **/
@Getter
@Setter
@TableName("GEN_BASIC")
public class GenBasic extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    /** 主表名称 */
    @ApiModelProperty(value = "主表名称", position = 2)
    private String dbTable;

    /** 主表主键 */
    @ApiModelProperty(value = "主表主键", position = 3)
    private String dbTableKey;

    /** 插件名 */
    @ApiModelProperty(value = "插件名", position = 4)
    private String pluginName;

    /** 模块名 */
    @ApiModelProperty(value = "模块名", position = 5)
    private String moduleName;

    /** 表前缀移除 */
    @ApiModelProperty(value = "表前缀移除", position = 6)
    private String tablePrefix;

    /** 生成方式 */
    @ApiModelProperty(value = "生成方式", position = 7)
    private String generateType;

    /** 所属模块 */
    @ApiModelProperty(value = "所属模块", position = 8)
    private String module;

    /** 移动端所属模块 */
    @ApiModelProperty(value = "移动端所属模块", position = 9)
    private String mobileModule;

    /** 上级目录 */
    @ApiModelProperty(value = "上级目录", position = 10)
    private String menuPid;

    /** 功能名 */
    @ApiModelProperty(value = "功能名", position = 11)
    private String functionName;

    /** 业务名 */
    @ApiModelProperty(value = "业务名", position = 12)
    private String busName;

    /** 类名 */
    @ApiModelProperty(value = "类名", position = 13)
    private String className;

    /** 表单布局 */
    @ApiModelProperty(value = "表单布局", position = 14)
    private String formLayout;

    /** 使用栅格 */
    @ApiModelProperty(value = "使用栅格", position = 15)
    private String gridWhether;

    /** 排序 */
    @ApiModelProperty(value = "排序", position = 16)
    private Integer sortCode;

    /** 包名 */
    @ApiModelProperty(value = "包名", position = 17)
    private String packageName;

    /** 作者 */
    @ApiModelProperty(value = "作者", position = 18)
    private String authorName;
}
