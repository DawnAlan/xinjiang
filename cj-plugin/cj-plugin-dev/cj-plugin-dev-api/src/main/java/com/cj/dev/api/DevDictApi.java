
package com.cj.dev.api;

import cn.hutool.json.JSONObject;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * 字典API
 *
 * @author LB
 * @date 2023/9/19 15:58
 */
public interface DevDictApi {

    /**
     * 根据字典值查询其下所有子字典
     *
     * @param value 字典的值 DictValue
     * @return
     */
    List<JSONObject> getDictByValue(@RequestParam("value") String value);


    /**
     * 根据字典父节点ID其下所有子字典
     *
     * @author : lb
     * @date : 2023/10/30 16:20
    */
    List<JSONObject> getDictByParentId(@RequestParam("parentId") String parentId);
    /**
     * 根据字典名称查询其下所有子字典
     * @param name 字典的名称 DictLabel
     * @return
     */
    List<JSONObject> getDictByName(@RequestParam("name") String name);
}
