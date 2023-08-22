package com.cj.textua.textua.param;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @创建人 yancheng
 * @创建时间 2023-08-22 14:41
 * @描述
 */
@Getter
@Setter
public class TextuaBaseEditParam {

    /** 主键UID */
    @ApiModelProperty(value = "主键UID", required = true, position = 1)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 测点名称 */
    @ApiModelProperty(value = "测点名称", position = 2)
    private String pointname;

    /** 仪器类型 */
    @ApiModelProperty(value = "仪器类型", position = 3)
    private String instrumentname;

    /** 监测类型 */
    @ApiModelProperty(value = "监测类型", position = 4)
    private String monitorname;

    /** 测点别名 */
    @ApiModelProperty(value = "测点别名", position = 5)
    private String pointalias;

    /** 测点状态 */
    @ApiModelProperty(value = "测点状态", position = 6)
    private String pointtype;

    /** 人工/自动化 */
    @ApiModelProperty(value = "人工/自动化", position = 7)
    private String recordmethod;

    @TableField(exist = false)
    @ApiModelProperty(value = "考证扩展属性", position = 8)
    private List<TextuaExtraParam> extraParam;
}
