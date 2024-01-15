package com.cj.project.modular.treemodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.project.api.treemodel.entity.TreePermissions;
import org.apache.ibatis.annotations.Mapper;


/**
 * 树目录权限组基本信息表Mapper接口
 * @author zsy
 * @date 2024-01-15
 */
@Mapper
public interface TreePermissionsMapper extends BaseMapper<TreePermissions> {

}