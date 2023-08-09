
package com.cj.gen.modular.basic.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 预览代码生成结果
 *
 * @author xuyuxiang
 * @date 2022/10/28 17:03
 **/
@Getter
@Setter
public class GenBasicPreviewResult {

    /** SQL代码结果集 */
    @ApiModelProperty(value = "SQL代码结果集", position = 1)
    private List<GenBasicCodeResult> genBasicCodeSqlResultList;

    /** 前端代码结果集 */
    @ApiModelProperty(value = "前端代码结果集", position = 2)
    private List<GenBasicCodeResult> genBasicCodeFrontendResultList;

    /** 后端代码结果集 */
    @ApiModelProperty(value = "后端代码结果集", position = 3)
    private List<GenBasicCodeResult> genBasicCodeBackendResultList;

    /** 前端代码结果集 */
    @ApiModelProperty(value = "移动端代码结果集", position = 4)
    private List<GenBasicCodeResult> genBasicCodeMobileResultList;

    @Getter
    @Setter
    public static class GenBasicCodeResult {

        /** 代码文件名称 */
        @ApiModelProperty(value = "代码文件名称", position = 1)
        private String codeFileName;

        /** 代码文件带路径名称 */
        @ApiModelProperty(value = "代码文件带路径名称", position = 2)
        private String codeFileWithPathName;

        /** 代码文件内容 */
        @ApiModelProperty(value = "代码文件内容", position = 2)
        private String codeFileContent;
    }
}
