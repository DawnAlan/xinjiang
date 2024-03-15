package com.cj.approval.func.modular.approval.approvalManagement.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.StrUtil;
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
import sun.applet.Main;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
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
            if(byId.getApprovedById().equals(approval)){
                approvalManagement.setApprovalStatus(2);
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
        ApprovalManagement byId = this.getById(id);
        boolean update = this.lambdaUpdate().set(ApprovalManagement::getInstructionStatus, 4).eq(ApprovalManagement::getId, id).update();
        if(update){
            if(byId.getApprovalStatus()==1){
                String[] split = byId.getApprovedById().split(",");
                for(String s:split){
                    WebSocketServer.sendInfo(sdf1.format(byId.getCreateTime())+byId.getDispatchingObjectives()+"已撤销",s);
                }
            }
            if(byId.getApprovalStatus()==2){
                String[] split1 = byId.getApprovedById().split(",");
                for(String s:split1){
                    WebSocketServer.sendInfo(sdf1.format(byId.getCreateTime())+byId.getDispatchingObjectives()+"已撤销",s);
                }
                String[] split2 = byId.getRecipientId().split(",");
                for(String s:split2){
                    WebSocketServer.sendInfo(sdf1.format(byId.getCreateTime())+byId.getDispatchingObjectives()+"已撤销",s);
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
                WebSocketServer.sendInfo("您有一条待审批的指令",s);
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
                        try {
                            String approvedById = byId.getApprovedById();
                            String[] split = approvedById.split(",");
                            for(int a=0;a<split.length;a++) {
                                JSONObject userByIdWithoutException = sysUserApi.getUserByIdWithoutException(split[a]);
                                String digitalSignature = (String) userByIdWithoutException.get("digitalSignature");
                                String filePath = "http://192.168.31.154:9000/tth/"+digitalSignature;
                                String fileResultPath = System.getProperty("java.io.tmpdir")+"/"+UUIDUtils.getUUID()+".png";
                                downloadAndSaveFile(filePath,fileResultPath);
                                put("pic"+(a+1), Pictures.ofStream(new FileInputStream(fileResultPath), PictureType.PNG)
                                        .size(50, 25).create());
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
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

    @Override
    public void download(String path, HttpServletResponse response) {
        //从minio下载
        minioUtils.download("tth",path,response);
    }

    public String getIp(HttpServletRequest request) {
        /*String remoteAddr = request.getRemoteAddr();
        String forwarded = request.getHeader("X-Forwarded-For");
        String realIp = request.getHeader("X-Real-IP");
        String ip = null;
        if (realIp == null){
            if (forwarded == null) {
                ip = remoteAddr;
            } else {
                ip = remoteAddr + "/" + forwarded.split(",")[0];
            }
        } else{
            if (realIp.equals(forwarded)){
                ip = realIp;
            } else {
                if (forwarded != null) {
                    forwarded = forwarded.split(",")[0];
                }
                ip = realIp + "/" + forwarded;
            }
        }
         return ip;
        */
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (!networkInterface.isLoopback() && networkInterface.isUp()) {
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        if (address.isSiteLocalAddress()) {
                            return address.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            return null;
        }
       return null;
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
}

