package com.cj.web.core.provider.dev.dict;

import cn.hutool.json.JSONUtil;
import com.cj.dev.feign.DevDictFeign;
import com.cj.dev.modular.dict.provider.DevDictApiProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequiredArgsConstructor
@RestController
public class DevDictFeignProvider implements DevDictFeign {


    private final DevDictApiProvider devDictApiProvider;
    @Override
    @RequestMapping("/feign/dev/dict/getDictByValue")
    public String getDictByValue(String value) {
        return JSONUtil.toJsonStr(devDictApiProvider.getDictByValue(value));
    }

    @Override
    @RequestMapping("/feign/dev/dict/getDictByParentId")
    public String getDictByParentId(String parentId) {
        return JSONUtil.toJsonStr(devDictApiProvider.getDictByParentId(parentId));
    }

    @Override
    @RequestMapping("/feign/dev/dict/getDictByName")
    public String getDictByName(String name) {
        return JSONUtil.toJsonStr(devDictApiProvider.getDictByName(name));
    }
}
