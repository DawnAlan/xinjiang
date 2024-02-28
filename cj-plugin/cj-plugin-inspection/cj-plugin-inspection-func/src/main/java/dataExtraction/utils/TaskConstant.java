package dataExtraction.utils;

/**
 * <p>Description:</p>
 *
 * @author chenzhitao
 * @date 2023年03月09日
 * 网格数据解析常量类
 */
public class TaskConstant {
    /**
     * 运行状态:0
     */
    public static Integer STATUS_RUNNING = 0;
    /**
     * 暂停状态:1
     */
    public static Integer STATUS_STOP = 1;

    /**
     * 自动重启:0（默认方式）
     */
    public static Integer AUTO_MODE = 0;
    /**
     * 手动重启:1
     */
    public static Integer MANUAL_MODE = 1;

    /**
     * 唐主任给与的解析预报数据逻辑
     */
    public static Integer TASK_TYPE_TANG = 0;
    /**
     * 史博士团对的预报数据
     */
    public static Integer TASK_TYPE_SHI = 1;
}
