package com.cj.project.modular.treemodel.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.exception.CommonException;
import com.cj.common.pojo.CommonEntity;
import com.cj.project.api.treemodel.dto.TreePermissionsDto;
import com.cj.project.api.treemodel.entity.TreePermissions;
import com.cj.project.modular.treemodel.enums.TreeModelEnum;
import com.cj.project.modular.treemodel.mapper.TreePermissionsMapper;
import com.cj.project.modular.treemodel.service.TreePermissionsService;
import org.springframework.stereotype.Service;

/**
 * 树目录权限组基本信息表Service业务层处理
 *
 * @author zsy
 * @date 2024-01-15
 */
@Service
public class TreePermissionsServiceImpl extends ServiceImpl<TreePermissionsMapper, TreePermissions> implements TreePermissionsService {

    @Override
    public boolean savePermissions(TreePermissionsDto treePermissionsDto) {
        boolean exists = baseMapper.exists(new LambdaQueryWrapper<TreePermissions>()
                .eq(TreePermissions::getName, treePermissionsDto.getName())
                .eq(CommonEntity::getDeleteFlag, TreeModelEnum.NOT_DELETE.getValue()));
        if (exists) {
            throw new CommonException(300, "该名称已存在，请勿重复添加");
        }
        TreePermissions treePermissions = BeanUtil.copyProperties(treePermissionsDto, TreePermissions.class);
        return this.saveOrUpdate(treePermissions);
    }

    @Override
    public boolean deletePermissions(String id) {
        return this.update(new LambdaUpdateWrapper<TreePermissions>()
                .eq(TreePermissions::getId, id)
                .set(CommonEntity::getDeleteFlag, TreeModelEnum.DELETE.getValue()));
    }
}