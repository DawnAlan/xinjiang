package com.cj.project.modular.treemodel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.project.api.treemodel.entity.TreePermissionsUser;
import com.cj.project.modular.treemodel.mapper.TreePermissionsUserMapper;
import com.cj.project.modular.treemodel.service.TreePermissionsUserService;
import org.springframework.stereotype.Service;

/**
 * 树权限组与用户关系表Service业务层处理
 * @author zsy
 * @date 2024-01-15
 */
@Service
public class TreePermissionsUserServiceImpl extends ServiceImpl<TreePermissionsUserMapper, TreePermissionsUser> implements TreePermissionsUserService {

}