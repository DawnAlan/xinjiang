package com.cj.dev.modular.dict.provider;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cj.dev.modular.dict.entity.DevDict;
import com.cj.dev.modular.dict.param.DevDictListParam;
import com.cj.dev.modular.dict.service.DevDictService;
import org.springframework.stereotype.Service;
import com.cj.dev.api.DevDictApi;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典API接口实现类
 *
 * @author xuyuxiang
 * @date 2022/9/2 16:05
 */
@Service
public class DevDictApiProvider implements DevDictApi {

    @Resource
    private DevDictService devDictService;

    @Override
    public List<JSONObject> getDictByValue(String value) {

        DevDict devDict = devDictService.queryEntityByValue(value);
        DevDictListParam devDictListParam = new DevDictListParam();
        devDictListParam.setCategory(devDict.getCategory());
        devDictListParam.setParentId(devDict.getId());

        return devDictService.list(devDictListParam).stream().map(JSONUtil::parseObj).collect(Collectors.toList());
    }

    @Override
    public List<JSONObject> getDictByName(String name) {
        return null;
    }
}
