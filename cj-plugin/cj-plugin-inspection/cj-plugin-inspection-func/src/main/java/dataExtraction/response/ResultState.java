package dataExtraction.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>Description:</p>
 *
 * @author chenzhitao
 * @date 2022年11月28日
 */
@Schema(title = "接口返回通用状态")
public class ResultState {
    @Schema(title = "成功")
    public static int SUCCESS = 0;

    @Schema(title = "失败")
    public static int FAIL = 1;

    @Schema(title = "没有数据")
    public static int BLANK = 2;
}
