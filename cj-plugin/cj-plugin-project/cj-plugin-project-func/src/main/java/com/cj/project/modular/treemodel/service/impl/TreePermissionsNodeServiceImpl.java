package com.cj.project.modular.treemodel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.project.api.treemodel.entity.TreePermissionsNode;
import com.cj.project.modular.treemodel.mapper.TreePermissionsNodeMapper;
import com.cj.project.modular.treemodel.service.TreePermissionsNodeService;
import org.springframework.stereotype.Service;

/**
 * 权限组与树节点绑定关系表Service业务层处理
 * @author zsy
 * @date 2024-01-15
 */
@Service
public class TreePermissionsNodeServiceImpl extends ServiceImpl<TreePermissionsNodeMapper, TreePermissionsNode> implements TreePermissionsNodeService {

}