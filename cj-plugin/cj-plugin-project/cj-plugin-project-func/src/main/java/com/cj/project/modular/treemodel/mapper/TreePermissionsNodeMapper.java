package com.cj.project.modular.treemodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.project.api.treemodel.entity.TreePermissionsNode;
import org.apache.ibatis.annotations.Mapper;


/**
 * 权限组与树节点绑定关系表Mapper接口
 * @author zsy
 * @date 2024-01-15
 */
@Mapper
public interface TreePermissionsNodeMapper extends BaseMapper<TreePermissionsNode> {

}