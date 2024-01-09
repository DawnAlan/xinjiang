package com.cj.approval.func.modular.approval.approvalManagement.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.approval.func.core.utils.MinioUtils;
import com.cj.approval.func.core.utils.WebSocketServer;
import com.cj.approval.func.modular.approval.approvalManagement.bean.req.SelectListReq;
import com.cj.approval.func.modular.approval.approvalManagement.mapper.ApprovalManagementMapper;
import com.cj.approval.func.modular.approval.approvalManagement.entity.ApprovalManagement;
import com.cj.approval.func.modular.approval.approvalManagement.service.ApprovalManagementService;
import com.cj.approval.func.modular.approval.instructionViewing.entity.InstructionViewing;
import com.cj.approval.func.modular.approval.instructionViewing.service.InstructionViewingService;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.sys.api.SysOrgApi;
import com.cj.sys.api.SysUserApi;
import com.deepoove.poi.XWPFTemplate;
import io.minio.ObjectWriteResponse;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import sun.applet.Main;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
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

    @Autowired
    private SysUserApi sysUserApi;

    @Autowired
    private InstructionViewingService instructionViewingService;

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${minio.bucket}")
    private String defaultBucket;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(ApprovalManagement approvalManagement) {
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        approvalManagement.setId(UUIDUtils.getUUID());
        approvalManagement.setDel(0);
        approvalManagement.setCreateTime(new Date());
        if(approvalManagement.getApprovalStatus()==null){
            approvalManagement.setApprovalStatus(1);
        }
        approvalManagement.setInstructionStatus(1);
        approvalManagement.setCreateBy(saBaseLoginUser.getName());
        approvalManagement.setLssuedBy(saBaseLoginUser.getName());
        approvalManagement.setLssuedById(saBaseLoginUser.getId());
        boolean save = this.save(approvalManagement);
        if(save){
            List<InstructionViewing> instructionViewingList = new ArrayList<>();
            String[] split = approvalManagement.getDispatchingUnitId().split(",");
            for(String s:split){
                InstructionViewing instructionViewing = new InstructionViewing();
                instructionViewing.setId(UUIDUtils.getUUID());
                instructionViewing.setInstructionId(approvalManagement.getId());
                instructionViewing.setInstructionStatus(1);
                instructionViewing.setViewStatus(2);
                instructionViewing.setUnitId(s);
                instructionViewing.setUnit(sysOrgApi.getNameById(s));
                instructionViewingList.add(instructionViewing);
            }
            boolean b = instructionViewingService.saveBatch(instructionViewingList);
            if(b){
                try {
                    String[] approvedById = approvalManagement.getApprovedById().split(",");
                    for (String s:approvedById){
                        WebSocketServer.sendInfo("您有一条待审批的指令",s);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return RestResponse.no("send msg fail");
                }

                return RestResponse.ok();
            }else {
                return RestResponse.no("error");
            }
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
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        String approvalTemp= (String) redisUtil.get("approvalManagement_"+approvalManagement.getId());
        if(StringUtils.isNotEmpty(approvalTemp)&&approvalTemp.contains(saBaseLoginUser.getId())){
            return RestResponse.no("当前用户已审批，请勿再审批");
        }
        if(StringUtils.isEmpty(approvalTemp)){
            redisUtil.set("approvalManagement_"+approvalManagement.getId(),saBaseLoginUser.getId());
        }else {
            redisUtil.set("approvalManagement_"+approvalManagement.getId(),approvalTemp+","+saBaseLoginUser.getId());
        }
        String approval= (String) redisUtil.get("approvalManagement_"+approvalManagement.getId());
        ApprovalManagement byId = this.getById(approvalManagement.getId());
        if(byId.getApprovedById().equals(approval)){
            approvalManagement.setApprovalStatus(2);
        }else {
            approvalManagement.setApprovalStatus(1);
        }
        boolean b = this.updateById(approvalManagement);
        if(b){
            try {
                ApprovalManagement byId1 = this.getById(approvalManagement.getId());
                if(byId1.getApprovalStatus()==2){
                    if(!byId1.getInstructionType().equals("水库调水")){
                        String[] lssuedById = byId1.getLssuedById().split(",");
                        for(String s:lssuedById){
                            WebSocketServer.sendInfo("您创建的指令已审批",s);
                        }
                        String[] split = byId1.getRecipientId().split(",");
                        for(String s:split){
                            JSONObject userByIdWithoutException = sysUserApi.getUserByIdWithoutException(s);
                            String orgId = (String) userByIdWithoutException.get("orgId");
                            InstructionViewing one = instructionViewingService.lambdaQuery().eq(InstructionViewing::getInstructionId, approvalManagement.getId()).
                                    eq(InstructionViewing::getUnitId, orgId).one();
                            WebSocketServer.sendInfo("您有一条待执行的指令,"+one.getId(),s);
                        }
                    }else {
                        String[] lssuedById = byId1.getLssuedById().split(",");
                        for(String s:lssuedById){
                            WebSocketServer.sendInfo("您创建的指令已审批",s);
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return RestResponse.no("send msg fail");
            }
            return RestResponse.ok();
        }else{
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse<IPage<ApprovalManagement>> selectList(SelectListReq req) {
        IPage<ApprovalManagement> p = new Page<>(req.getPageNum(),req.getPageSize());
        IPage<ApprovalManagement> page = this.lambdaQuery().
                between(req.getStartTime() !=null && req.getEndTime()!=null,ApprovalManagement::getCreateTime, req.getStartTime(),req.getEndTime()).
                eq(StringUtils.isNotEmpty(req.getInstructionType()),ApprovalManagement::getInstructionType, req.getInstructionType()).
                eq(ApprovalManagement::getDel, 0).orderByDesc(ApprovalManagement::getCreateTime).page(p);
        if(page.getTotal()>0){
            return RestResponse.ok(page);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    public RestResponse<IPage<ApprovalManagement>> selectFinishList(SelectListReq req) {
        IPage<ApprovalManagement> p = new Page<>(req.getPageNum(),req.getPageSize());
        IPage<ApprovalManagement> page = this.lambdaQuery().
                between(req.getStartTime() !=null && req.getEndTime()!=null,ApprovalManagement::getCreateTime, req.getStartTime(),req.getEndTime()).
                eq(StringUtils.isNotEmpty(req.getInstructionType()),ApprovalManagement::getInstructionType, req.getInstructionType()).eq(ApprovalManagement::getApprovalStatus,2).
                eq(ApprovalManagement::getDel, 0).orderByDesc(ApprovalManagement::getCreateTime).page(p);
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
    @Transactional(rollbackFor = Exception.class)
    public void thymeleafExport(HttpServletResponse response, String id) {
        ApprovalManagement byId = this.getById(id);
        if(StringUtils.isNotEmpty(byId.getFileAddress())){
            try {
                String fileAddress = byId.getFileAddress();
                minioUtils.download(defaultBucket, fileAddress,response);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            String format = sdf.format(new Date());
            String year = format.split("-")[0];
            String wordNum = (String) redisUtil.get(year + ":wordNum");
            if (StringUtils.isEmpty(wordNum)) {
                redisUtil.set(year + ":wordNum", "001");
                wordNum = "001";
            } else {
                Integer i = Integer.parseInt(wordNum) + 1;
                String string = i.toString();
                if (string.length() == 1) {
                    wordNum = "00" + i;
                }
                if (string.length() == 2) {
                    wordNum = "0" + i;
                }
                if (string.length() > 2) {
                    wordNum = string;
                }
                redisUtil.set(year + ":wordNum", wordNum);
            }
            String fileName = "调度指令";
            String filepath = "D:\\tth_system\\end\\file\\approvalManagement.docx";
            // 通过 XWPFTemplate 编译文件并渲染数据到模板中
            XWPFTemplate template = XWPFTemplate.compile(filepath).render(
                    new HashMap<String, Object>() {{
                        put("wordNum", (String) redisUtil.get(year + ":wordNum"));
                        put("dispatchingUnit", byId.getDispatchingUnit());
                        put("dispatchingObjectives", byId.getDispatchingObjectives());
                        put("dispatchingParams", byId.getDispatchingParams());
                        put("dispatchingTime", sdf1.format(byId.getDispatchingTime()));
                        put("lssuedBy", byId.getLssuedBy());
                        put("approvedBy", byId.getApprovedBy());
                        put("createTime", sdf.format(byId.getCreateTime()));
                    }});
            try {
                // 将完成数据渲染的文档写出
                String property = System.getProperty("java.io.tmpdir");
                String filePath = property + fileName;
                template.writeAndClose(new FileOutputStream(filePath + ".docx"));
                File tempFile = new File(filePath + ".docx");
                FileInputStream inputStream = new FileInputStream(tempFile);
                MockMultipartFile file = new MockMultipartFile("file", tempFile.getName(), "text/plain", inputStream);
                Date date = new Date();
                String yyyyMMdd = DateUtil.format(date, "yyyyMMdd");
                String hh = DateUtil.format(date, "HH");
                String mm = DateUtil.format(date, "mm");
                String ss = DateUtil.format(date, "ss");
                String namePath = yyyyMMdd + "/" + hh + "/" + mm + "/" + ss + "/" + UUID.fastUUID().toString(true) + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                ObjectWriteResponse objectWriteResponse = minioUtils.putObject(defaultBucket, namePath, file.getInputStream(), file.getContentType());
                String object = objectWriteResponse.object();
                boolean update = this.lambdaUpdate().set(ApprovalManagement::getFileAddress, object).eq(ApprovalManagement::getId, id).update();
                if (update) {
                    Path path = Paths.get(filePath + ".docx");
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-Disposition", "attachment;filename=" + path.getFileName());
                    Files.copy(path, response.getOutputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void testView(HttpServletResponse response) {
        try {
            Path path = Paths.get("D:\\tth_system\\end\\file\\123.docx");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + path.getFileName());
            Files.copy(path, response.getOutputStream());
        }catch (Exception e){

        }
    }


    @Override
    public void downFile(HttpServletResponse response, String id) {
        ApprovalManagement byId = this.getById(id);
        minioUtils.download(defaultBucket,byId.getFileAddress(),response);
    }

    @Override
    public RestResponse getOrgList() {
        List<Tree<String>> trees = sysOrgApi.orgTreeSelector();
        return RestResponse.ok(trees);
    }

    @Override
    public RestResponse<SaBaseLoginUser> getUserInfo() {
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        return RestResponse.ok(saBaseLoginUser);
    }

    public static void main(String[] args) {
        String s= "123,456";
        System.out.println(s.contains("123"));
    }
}

