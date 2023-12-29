package com.cj.approval.func.modular.approval.approvalManagement.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.approval.func.modular.approval.approvalManagement.bean.req.SelectListReq;
import com.cj.approval.func.modular.approval.approvalManagement.entity.ApprovalManagement;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.common.model.RestResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 审批管理表(ApprovalManagement)表服务接口
 *
 * @author makejava
 * @since 2023-12-19 19:41:02
 */
public interface ApprovalManagementService extends IService<ApprovalManagement> {

    RestResponse add(ApprovalManagement approvalManagement);

    RestResponse delete(String id);

    RestResponse update(ApprovalManagement approvalManagement);

    RestResponse<IPage<ApprovalManagement>> selectList(SelectListReq req);
    RestResponse<IPage<ApprovalManagement>> selectFinishList(SelectListReq req);

    RestResponse<List<ApprovalManagement>> selectByIds(String ids);

    void thymeleafExport(HttpServletResponse response, String id);

    void testView(HttpServletResponse response);

    void downFile(HttpServletResponse response, String id);

    RestResponse getOrgList();

    RestResponse<SaBaseLoginUser> getUserInfo();
}

