package com.cj.project.modular.treemodel.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.project.api.treemodel.dto.TreePermissionsDto;
import com.cj.project.api.treemodel.entity.TreePermissions;

/**
 * 树目录权限组基本信息表Service接口
 *
 * @author zsy
 * @date 2024-01-15
 */
public interface TreePermissionsService extends IService<TreePermissions> {


    boolean savePermissions(TreePermissionsDto treePermissionsDto);

    boolean deletePermissions(String id);
}