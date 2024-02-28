package dataExtraction.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Description:</p>
 *
 * @author chenzhitao
 * @date 2022年11月29日
 * json类型的工具类
 */
@Component
public class JsonUtil {
    /**
     * 任务存放位置
     */
    /*public static String TASKS_PATH="F:\\test\\beijiang_dataTasks.json";*/
    public static String TASKS_PATH="/data/tasks/beijiang_dataTasks.json";


    /**
     * 验证是否存在该文件
     *
     * @return
     */
    public static Boolean isExistFile() {
        return new File(TASKS_PATH).exists();
    }

    /**
     * 获取json文件
     *
     * @return
     * @throws IOException
     */
    public static File getJsonFile() throws IOException {
        File file = new File(TASKS_PATH);
        if (!file.exists()) {
            file.createNewFile();
            OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()));
            writer.write(JSONObject.toJSONString(new HashMap<>()));
            writer.flush();
            writer.close();
        }
        return file;
    }


    /**
     * 将对象转存到json文件中
     *
     * @throws IOException
     */
    public static void updateJsonFile(Map map) throws IOException {
        String jsonString = JSONObject.toJSONString(map);
        File file = getJsonFile();
        OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8);
        writer.write(jsonString);
        writer.flush();
        writer.close();
    }


    /**
     * json文件转JsonObject
     *
     * @return
     * @throws IOException
     */
    public static JSONObject jsonToJsonObject() throws IOException {
        //使用BufferedReader，能够解决中文乱码问题
        InputStreamReader inputStreamReader = new InputStreamReader(Files.newInputStream(getJsonFile().toPath()), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        int len;
        String line = "";
        StringBuilder stringBuffer = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            // 添加字符串到缓冲区
            stringBuffer.append(line);
        }
        // 关闭资源
        reader.close();
        inputStreamReader.close();
        return JSON.parseObject(stringBuffer.toString());
    }

    /**
     * 加载站点信息
     *
     * @return
     * @throws IOException
     */
    public static JSONObject stationToMap(String path) throws IOException {
        BufferedInputStream stream = new BufferedInputStream(new ClassPathResource(path).getInputStream());
        int len;
        byte[] bytes = new byte[1024];
        StringBuilder stringBuffer = new StringBuilder();
        while ((len = stream.read(bytes)) != -1) {
            // 添加字符串到缓冲区
            stringBuffer.append(new String(bytes, 0, len));
        }
        // 关闭资源
        stream.close();
        return JSON.parseObject(stringBuffer.toString());
    }
}
