package com.cj.approval.func.modular.approval.approvalManagement.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.approval.func.core.utils.MinioUtils;
import com.cj.approval.func.core.utils.WebSocketServer;
import com.cj.approval.func.modular.approval.approvalManagement.bean.req.SelectListReq;
import com.cj.approval.func.modular.approval.approvalManagement.bean.res.SendMsgRes;
import com.cj.approval.func.modular.approval.approvalManagement.mapper.ApprovalManagementMapper;
import com.cj.approval.func.modular.approval.approvalManagement.entity.ApprovalManagement;
import com.cj.approval.func.modular.approval.approvalManagement.service.ApprovalManagementService;
import com.cj.approval.func.modular.approval.dutyRecords.entity.DutyRecords;
import com.cj.approval.func.modular.approval.dutyRecords.service.DutyRecordsService;
import com.cj.approval.func.modular.approval.instructionViewing.entity.InstructionViewing;
import com.cj.approval.func.modular.approval.instructionViewing.service.InstructionViewingService;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.msg.entity.OverallMsg;
import com.cj.msg.enums.MessageCategoryEnum;
import com.cj.msg.service.OverallMsgService;
import com.cj.sys.api.SysOrgApi;
import com.cj.sys.api.SysUserApi;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.PictureType;
import com.deepoove.poi.data.Pictures;
import io.minio.ObjectWriteResponse;
import lombok.SneakyThrows;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
//import sun.applet.Main;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.springframework.core.io.buffer.DataBufferUtils.readInputStream;

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

    @Value("${minio.url}")
    private String url;

    @Value("${approvalFilepath}")
    private String approvalFilepath;

    @Autowired
    private OverallMsgService overallMsgService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Object lock = new Object();

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
                    int i = 1;
                    for (String s:approvedById){
                        String msgContext = "您有一条待审批的指令";
                        saveMsg(saBaseLoginUser,msgContext,s,"");
                        WebSocketServer.sendInfo(msgContext,s);
                        log.warn("已发送指令："+i+",用户id："+s);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return RestResponse.no("send msg fail");
                }
                ExecutorService pool = Executors.newSingleThreadExecutor();
                pool.submit(new Runnable() {
                    private DutyRecordsService dutyRecordsService = SpringUtil.getBean(DutyRecordsService.class);
                    @Override
                    public void run() {
                        DutyRecords dutyRecords = dutyRecordsService.lambdaQuery().eq(DutyRecords::getStation, "供水科").eq(DutyRecords::getType,1).
                                apply("RECORD_TIME = {0}", DateUtil.format(new Date(), "yyyy-MM-dd")).last("limit 1").one();
                        if(dutyRecords!=null){
                            String splicingMsg = dutyRecords.getContextInfo()+"\\n"+splicingMsg(approvalManagement);
                            boolean update = dutyRecordsService.lambdaUpdate().set(DutyRecords::getContextInfo, splicingMsg).eq(DutyRecords::getId, dutyRecords.getId()).update();
                            System.out.println("-------------=======================------------------"+update);
                        }else {
                            DutyRecords dutyRecordsTemp = new DutyRecords();
                            dutyRecordsTemp.setDel(0);
                            dutyRecordsTemp.setType(1);
                            dutyRecordsTemp.setId(UUIDUtils.getUUID());
                            dutyRecordsTemp.setCreateTime(new Date());
                            try {
                                dutyRecordsTemp.setRecordTime(sdf.parse(DateUtil.format(new Date(), "yyyy-MM-dd")));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            dutyRecordsTemp.setCreateBy(saBaseLoginUser.getName());
                            dutyRecordsTemp.setStation("供水科");
                            dutyRecordsTemp.setContextInfo(splicingMsg(approvalManagement));
                            dutyRecordsService.save(dutyRecordsTemp);
                        }
                    }
                });
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
        synchronized (lock){
            SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
            ApprovalManagement byId1 = this.getById(approvalManagement.getId());
            if(!Arrays.asList(byId1.getApprovedById().split(",")).contains(saBaseLoginUser.getId())){
                return RestResponse.no("当前用户没有审批权限");
            }
            String approvalTemp= (String) redisUtil.get("approvalManagement_"+approvalManagement.getId());
            if(StringUtils.isNotEmpty(approvalTemp)&&Arrays.asList(approvalTemp.split(",")).contains(saBaseLoginUser.getId())){
                return RestResponse.no("当前用户已审批，请勿再审批");
            }
            if(StringUtils.isEmpty(approvalTemp)){
                redisUtil.set("approvalManagement_"+approvalManagement.getId(),saBaseLoginUser.getId());
            }else {
                redisUtil.set("approvalManagement_"+approvalManagement.getId(),approvalTemp+","+saBaseLoginUser.getId());
            }
            String approval= (String) redisUtil.get("approvalManagement_"+approvalManagement.getId());
            ApprovalManagement byId = this.getById(approvalManagement.getId());
            String[] split1 = approval.split(",");
            String[] split2 = byId.getApprovedById().split(",");
            List<String> bList = turnToList(split1);
            List<String> aList = turnToList(split2);
            if(toS(aList,bList)){
                approvalManagement.setApprovalStatus(2);
                redisUtil.del("approvalManagement_"+approvalManagement.getId());
            }else {
                approvalManagement.setApprovalStatus(1);
            }
            boolean b = this.updateById(approvalManagement);
            if(b){
                try {
                    if(approvalManagement.getApprovalStatus()==2){
                        if(!byId1.getInstructionType().equals("指令签批")){
                            String[] lssuedById = byId1.getLssuedById().split(",");
                            for(String s:lssuedById){
                                String msgContext = "您创建的指令已审批";
                                saveMsg(saBaseLoginUser,msgContext,s,"");
                                WebSocketServer.sendInfo(msgContext,s);
                            }
                            String[] split = byId1.getRecipientId().split(",");
                            for(String s:split){
                                JSONObject userByIdWithoutException = sysUserApi.getUserByIdWithoutException(s);
                                String orgId = (String) userByIdWithoutException.get("orgId");
                                InstructionViewing one = instructionViewingService.lambdaQuery().eq(InstructionViewing::getInstructionId, approvalManagement.getId()).
                                        eq(InstructionViewing::getUnitId, orgId).one();
                                String msgContext = "您有一条待执行的指令";
                                saveMsg(saBaseLoginUser,msgContext,s,one.getId());
                                WebSocketServer.sendInfo(msgContext+"id:"+one.getId(),s);
                            }
                        }else {
                            String[] lssuedById = byId1.getLssuedById().split(",");
                            for(String s:lssuedById){
                                String msgContext = "您创建的指令已审批";
                                saveMsg(saBaseLoginUser,msgContext,s,"");
                                WebSocketServer.sendInfo(msgContext,s);
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    redisUtil.set("approvalManagement_"+approvalManagement.getId(),approvalTemp);
                    return RestResponse.no("send msg fail");
                }
                return RestResponse.ok();
            }else{
                return RestResponse.no("error");
            }
        }
    }

    @SneakyThrows
    @Override
    public RestResponse revoke(String id) {
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        ApprovalManagement byId = this.getById(id);
        boolean update = this.lambdaUpdate().set(ApprovalManagement::getInstructionStatus, 4).eq(ApprovalManagement::getId, id).update();
        if(update){
            if(byId.getApprovalStatus()==1){
                String[] split = byId.getApprovedById().split(",");
                for(String s:split){
                    String msgContext = sdf1.format(byId.getCreateTime())+byId.getDispatchingObjectives()+"已撤销";
                    saveMsg(saBaseLoginUser,msgContext,s,"");
                    WebSocketServer.sendInfo(msgContext,s);
                }
            }
            if(byId.getApprovalStatus()==2){
                String[] split1 = byId.getApprovedById().split(",");
                for(String s:split1){
                    String msgContext = sdf1.format(byId.getCreateTime())+byId.getDispatchingObjectives()+"已撤销";
                    saveMsg(saBaseLoginUser,msgContext,s,"");
                    WebSocketServer.sendInfo(msgContext,s);
                }
                String[] split2 = byId.getRecipientId().split(",");
                for(String s:split2){
                    String msgContext = sdf1.format(byId.getCreateTime())+byId.getDispatchingObjectives()+"已撤销";
                    saveMsg(saBaseLoginUser,msgContext,s,"");
                    WebSocketServer.sendInfo(msgContext,s);
                }
            }
            return RestResponse.ok("撤销成功");
        }else {
            return RestResponse.no("撤销失败");
        }
    }

    @SneakyThrows
    @Override
    public RestResponse replacePerson(ApprovalManagement approvalManagement) {
        ApprovalManagement byId = this.getById(approvalManagement.getId());
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        if(!saBaseLoginUser.getId().equals(byId.getLssuedById())){
            return RestResponse.no("您无权限修改审批人");
        }
        if(byId.getApprovalStatus()==2){
            return RestResponse.no("该指令已审批，请勿修改审批人");
        }
        boolean update = this.lambdaUpdate().set(ApprovalManagement::getApprovedById, approvalManagement.getApprovedById()).set(ApprovalManagement::getApprovedBy, approvalManagement.getApprovedBy()).
                eq(ApprovalManagement::getId, approvalManagement.getId()).update();
        if(update) {
            String[] approvedById = approvalManagement.getApprovedById().split(",");
            for (String s:approvedById){
                String msgContext = "您有一条待审批的指令";
                saveMsg(saBaseLoginUser,msgContext,s,"");
                WebSocketServer.sendInfo(msgContext,s);
            }
            return RestResponse.ok();
        }else {
            return RestResponse.no("修改失败");
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
                ne(ApprovalManagement::getInstructionType, "指令签批").
                eq(StringUtils.isNotEmpty(req.getInstructionType()),ApprovalManagement::getInstructionType, req.getInstructionType()).eq(ApprovalManagement::getApprovalStatus,2).
                eq(ApprovalManagement::getDel, 0).orderByDesc(ApprovalManagement::getCreateTime).
                like(StringUtils.isNotEmpty(req.getUnit()),ApprovalManagement::getDispatchingUnit,req.getUnit()).
                page(p);
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
    public void thymeleafExport(HttpServletResponse response, String id,HttpServletRequest request) {
        ApprovalManagement byId = this.getById(id);
        if(StringUtils.isNotEmpty(byId.getFileAddress())){
            try {
                String fileAddress = byId.getFileAddress();
                minioUtils.download(defaultBucket, fileAddress,response);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            String fileName = "approvalManagement";
            String netWorkFilepath = url+approvalFilepath+"approvalManagement.docx";
            String savePath = System.getProperty("java.io.tmpdir")+File.separator+"approvalManagement"+ UUIDUtils.getUUID() +".xlsx";
            downloadFile(netWorkFilepath,savePath);
            // 通过 XWPFTemplate 编译文件并渲染数据到模板中
            XWPFTemplate template = XWPFTemplate.compile(savePath).render(
                    new HashMap<String, Object>() {{
                        put("wordNum", byId.getInstructionSheetNum());
                        put("dispatchingUnit", byId.getDispatchingUnit());
                        put("dispatchingObjectives", byId.getDispatchingObjectives());
                        put("dispatchingParams", byId.getDispatchingParams());
                        put("dispatchingTime", sdf1.format(byId.getDispatchingTime()));
                        put("lssuedBy", byId.getLssuedBy());
                        put("approvedBy", byId.getApprovedBy());
                        put("createTime", sdf.format(byId.getCreateTime()));
                        try {
                            String approvedById = byId.getApprovedById();
                            String[] split = approvedById.split(",");
                            for(int a=0;a<split.length;a++) {
                                JSONObject userByIdWithoutException = sysUserApi.getUserByIdWithoutException(split[a]);
                                String digitalSignature = (String) userByIdWithoutException.get("digitalSignature");
                                if(StringUtils.isEmpty(digitalSignature)){
                                    throw new RuntimeException("审批用户暂无电子签名，请到用户管理上传电子签名");
                                }
                                String filePath = url+"/tth/"+digitalSignature;
                                String fileResultPath = System.getProperty("java.io.tmpdir")+File.separator+UUIDUtils.getUUID()+".png";
                                downloadAndSaveFile(filePath,fileResultPath);
                                put("pic"+(a+1), Pictures.ofStream(new FileInputStream(fileResultPath), PictureType.PNG)
                                        .size(50, 25).create());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }});
            try {
                // 将完成数据渲染的文档写出
                String property = System.getProperty("java.io.tmpdir");
                String filePath = property + File.separator+fileName;
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
                    minioUtils.download(defaultBucket, namePath,response);
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

    @Override
    public void download(String path, HttpServletResponse response) {
        //从minio下载
        minioUtils.download("tth",path,response);
    }

    @SneakyThrows
    public static void downloadAndSaveFile(String fileUrl, String destinationFilePath) {
        try (InputStream in = new URL(fileUrl).openStream();
             FileOutputStream out = new FileOutputStream(destinationFilePath)) {

            byte[] buffer = new byte[4096];
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> turnToList(String[] strings){
        List<String> result = new ArrayList<>();
        for(String s:strings){
            result.add(s);
        }
        return result;
    }

    private Boolean toS(List<String> a ,List<String> b){
        int size = a.size();
        int i = 0;
        for(String s:b){
            if(a.contains(s)){
                i++;
            }
        }
        if(size==i){
            return true;
        }else {
            return false;
        }
    }

    private void saveMsg(SaBaseLoginUser saBaseLoginUser,String msgContext,String receiveUser,String extJson){
        OverallMsg msg = new OverallMsg();
        msg.setId(UUIDUtils.getUUID());
        msg.setSubject(msgContext);
        msg.setCreateTime(new Date());
        msg.setIsRead(0);
        msg.setCreateUser(saBaseLoginUser.getId());
        msg.setReceiveUser(receiveUser);
        msg.setCategory(MessageCategoryEnum.APPROVAL.getValue());
        msg.setExtJson(extJson);
        SendMsgRes sendMsgRes = new SendMsgRes();
        sendMsgRes.setSendBy(saBaseLoginUser.getName());
        sendMsgRes.setSendUnit(saBaseLoginUser.getOrgName());
        sendMsgRes.setSendTime(new Date());
        sendMsgRes.setSendContent(msgContext);
        msg.setContent(com.alibaba.fastjson2.JSONObject.toJSONString(sendMsgRes));
        overallMsgService.save(msg);
    }

    @SneakyThrows
    public static String downloadFile(String fileUrl, String savePath) {
        URL url = new URL(fileUrl);
        InputStream inputStream = url.openStream();
        Paths.get(savePath,getFileName(fileUrl));
        Files.copy(inputStream, Paths.get(savePath));
        return savePath;
    }
    public static String getFileName(String fileUrl) {
        String[] parts = fileUrl.split("/");
        return parts[parts.length - 1];
    }

    private String splicingMsg(ApprovalManagement approvalManagement){
        String msg = "";
        if(approvalManagement.getInstructionType().equals("指令签批")){
            msg += sdf1.format(approvalManagement.getCreateTime())+",";
            msg += approvalManagement.getCreateBy()+"制作"+approvalManagement.getInstructionSheetNum()+"号签批指令,";
            msg += sdf1.format(approvalManagement.getDispatchingTime())+approvalManagement.getDispatchingUnit()+"向";
            msg += approvalManagement.getDispatchingObjectives()+"调水，";
            msg += "调度参数为:"+approvalManagement.getDispatchingParams()+",";
            msg += "审批人为："+approvalManagement.getApprovedBy()+"。";
        }
        if(approvalManagement.getInstructionType().equals("指令下达")){
            msg += sdf1.format(approvalManagement.getCreateTime())+",";
            msg += approvalManagement.getCreateBy()+"向";
            msg += approvalManagement.getDispatchingUnit()+"的"+approvalManagement.getRecipient()+"下达指令，";
            msg += "调度时间为:"+sdf1.format(approvalManagement.getDispatchingTime());
            msg += "，审批人为："+approvalManagement.getApprovedBy()+"。其中，";
            msg += "楼庄子通知内容为:"+approvalManagement.getDispatchingParamsLzz()+";";
            msg += "头屯河通知内容为:"+approvalManagement.getDispatchingParamsTth()+";";
            msg += "渠首通知内容为:"+approvalManagement.getDispatchingParamsQs()+";";
            msg += "河东通知内容为:"+approvalManagement.getDispatchingParamsHd()+";";
            msg += "河西通知内容为:"+approvalManagement.getDispatchingParamsHx()+"。";
        }
        return msg;
    }
}

