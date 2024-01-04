package com.cj.biz.core.context.dev;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cj.dev.api.DevDictApi;
import com.cj.dev.feign.DevConfigFeign;
import com.cj.dev.feign.DevDictFeign;
import com.cj.dev.feign.DevFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 配置APi上下文Bean
 *
 * @author dongxiayu
 * @date 2022/11/22 14:12
 */
@Slf4j
@RequiredArgsConstructor
@Component public class DevDictApiContenxtBean implements DevDictApi {

    private final DevDictFeign devDictFeign;


    @Override
    public List<JSONObject> getDictByValue(String value) {
        String dictList = devDictFeign.getDictByValue(value);
        JSONArray jsonArray = new JSONArray(dictList);
        List<JSONObject> resp = jsonArray.toList(JSONObject.class);
        return resp;
    }

    @Override
    public List<JSONObject> getDictByParentId(String parentId) {
        String dictList = devDictFeign.getDictByParentId(parentId);
        JSONArray jsonArray = new JSONArray(dictList);
        List<JSONObject> resp = jsonArray.toList(JSONObject.class);
        return resp;
    }

    @Override
    public List<JSONObject> getDictByName(String name)
    {
        String dictList = devDictFeign.getDictByName(name);
        JSONArray jsonArray = new JSONArray(dictList);
        List<JSONObject> resp = jsonArray.toList(JSONObject.class);
        return resp;
    }
}
