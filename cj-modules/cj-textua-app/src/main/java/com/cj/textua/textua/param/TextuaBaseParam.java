package com.cj.textua.textua.param;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @创建人 yancheng
 * @创建时间 2023-08-22 09:41
 * @描述
 * @Null(message = "XXXX不能为空") 被注释的元素必须为 null, message尽量要写不然前端不知道是哪个字段
 *
 * @NotNull(message = "XXXX不能为空") 被注释的元素必须不为 null, message尽量要写不然前端不知道是哪个字段
 *
 * @Length 被注释的字符串的大小必须在指定的范围内，注意只能用在String上 否则会报错, message尽量要写不然前端不知道是哪个字段
 *
 * @NotEmpty 被注释的字符串的必须非空，注意只能用在String上 否则会报错, message尽量要写不然前端不知道是哪个字段
 *
 * @AssertTrue(message = "XXXX") 被注释的元素必须为 true, message尽量要写不然前端不知道是哪个字段
 *
 * @AssertFalse 被注释的元素必须为 false
 *
 * @Min(value=L,message="XXXX") 被注释的元素必须是一个数字，其值必须大于等于指定的最小值, message尽量要写不然前端不知道是哪个字段
 *
 * @Max(value=L,message="XXXX") 被注释的元素必须是一个数字，其值必须小于等于指定的最小值, message尽量要写不然前端不知道是哪个字段
 *
 * @DecimalMin(value=L,message="XXXX") 被注释的元素必须是一个数字，其值必须大于等于指定的最小值, message尽量要写不然前端不知道是哪个字段
 *
 * @DecimalMax(value=L,message="XXXX") 被注释的元素必须是一个数字，其值必须小于等于指定的最大值, message尽量要写不然前端不知道是哪个字段
 *
 * @Size(max, min)  被注释的元素的大小必须在指定的范围内, message尽量要写不然前端不知道是哪个字段
 *
 * @Digits (integer, fraction) 被注释的元素必须是一个数字，其值必须在可接受的范围内, message尽量要写不然前端不知道是哪个字段
 *
 * @Past 被注释的元素必须是一个过去的日期, message尽量要写不然前端不知道是哪个字段
 *
 * @Future 被注释的元素必须是一个将来的日期, message尽量要写不然前端不知道是哪个字段
 *
 * @Pattern(value) 被注释的元素必须符合指定的正则表达式, message尽量要写不然前端不知道是哪个字段
 *
 * @Email 被注释的元素必须是电子邮箱地址, message尽量要写不然前端不知道是哪个字段
 *
 * @Range 被注释的元素必须在合适的范围内, message尽量要写不然前端不知道是哪个字段
 *
 * @NotBlank 验证字符串非null，且长度必须大于0，注意只能用在String上 否则会报错
 */
@Getter
@Setter
public class TextuaBaseParam {

    /** 测点名称 */
    @NotBlank(message = "pointname不能为空")
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
