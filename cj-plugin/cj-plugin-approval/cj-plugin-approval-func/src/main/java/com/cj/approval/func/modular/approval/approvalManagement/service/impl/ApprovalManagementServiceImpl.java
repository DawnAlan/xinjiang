package com.cj.approval.func.modular.approval.approvalManagement.service.impl;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.approval.func.modular.approval.approvalManagement.bean.req.SelectListReq;
import com.cj.approval.func.modular.approval.approvalManagement.mapper.ApprovalManagementMapper;
import com.cj.approval.func.modular.approval.approvalManagement.entity.ApprovalManagement;
import com.cj.approval.func.modular.approval.approvalManagement.service.ApprovalManagementService;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.sys.api.SysOrgApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 审批管理表(ApprovalManagement)表服务实现类
 *
 * @author makejava
 * @since 2023-12-19 19:41:02
 */
@Service("approvalManagementService")
public class ApprovalManagementServiceImpl extends ServiceImpl<ApprovalManagementMapper, ApprovalManagement> implements ApprovalManagementService {

    @Autowired
    private SysOrgApi sysOrgApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(ApprovalManagement approvalManagement) {
        approvalManagement.setId(UUIDUtils.getUUID());
        approvalManagement.setDel(0);
        approvalManagement.setCreateTime(new Date());
        boolean save = this.save(approvalManagement);
        if(save){
            return RestResponse.ok();
        }else{
            return RestResponse.no("error");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse delete(String id) {
        boolean update = this.lambdaUpdate().set(ApprovalManagement::getDel, 1).eq(ApprovalManagement::getId, id).update();
        if(update){
            return RestResponse.ok();
        }else{
            return RestResponse.no("error");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse update(ApprovalManagement approvalManagement) {
        boolean b = this.updateById(approvalManagement);
        if(b){
            return RestResponse.ok();
        }else{
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse<IPage<ApprovalManagement>> selectList(SelectListReq req) {
        IPage<ApprovalManagement> p = new Page<>(req.getPageNum(),req.getPageSize());
        IPage<ApprovalManagement> page = this.lambdaQuery().
                eq(ApprovalManagement::getCreateTime, req.getCreateTime()).
                eq(ApprovalManagement::getInstructionType, req.getInstructionType()).
                eq(ApprovalManagement::getDel, 0).page(p);
        if(page.getTotal()>0){
            return RestResponse.ok(page);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    public RestResponse<List<ApprovalManagement>> selectByIds(String ids) {
        List<ApprovalManagement> approvalManagements = this.listByIds(Arrays.stream(ids.split(",")).collect(Collectors.toList()));
        if(null!= approvalManagements && approvalManagements.size()>0){
            return RestResponse.ok(approvalManagements);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    public RestResponse getOrgList() {
        List<Tree<String>> trees = sysOrgApi.orgTreeSelector();

        return RestResponse.ok(trees);
    }
}

