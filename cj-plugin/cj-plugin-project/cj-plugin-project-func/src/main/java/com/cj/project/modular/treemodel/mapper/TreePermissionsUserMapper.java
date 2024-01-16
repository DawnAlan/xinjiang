package com.cj.project.modular.treemodel.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.project.api.treemodel.entity.TreePermissionsUser;
import org.apache.ibatis.annotations.Mapper;


/**
 * 树权限组与用户关系表Mapper接口
 * @author zsy
 * @date 2024-01-15
 */
@Mapper
public interface TreePermissionsUserMapper extends BaseMapper<TreePermissionsUser> {

}