package dataExtraction.service;

import dataExtraction.ghd.dao.TthDeviceAbnormalDao;
import dataExtraction.ghd.dao.WpdJlDataDao;
import dataExtraction.ghd.dao.WpdRrInfoDao;
import dataExtraction.ghd.dao.WpdStPptnRDao;
import dataExtraction.ghd.entity.*;
import dataExtraction.water.dao.*;
import dataExtraction.water.entity.IRRIGATED_PLATFORM_TREE;
import dataExtraction.water.entity.LZZ_GAUGING_STATION;
import dataExtraction.water.entity.LZZ_PLATFORM_TREE;
import dataExtraction.water.entity.LZZ_RAINFALL_STATION;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@EnableScheduling
@Component
public class ScheduledTasksService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    WpdRrInfoDao wpdRrInfoDao;
    @Autowired
    LZZPLATFORMTREEDao lzzplatformtreeDao;
    @Autowired
    IRRIGATED_PLATFORM_TREEDao irrigated_platform_treeDao;
    @Autowired
    LZZ_RAINFALL_STATIONDao lzz_rainfall_stationDao;
    @Autowired
    WpdStPptnRDao wpdStPptnRDao;
    @Autowired
    IRRIGATED_PLATFORM_DATA_INFODao irrigated_platform_data_infoDao;
    @Autowired
    LZZ_GAUGING_STATIONDao lzz_gauging_stationDao;
    @Autowired
    WpdJlDataDao wpdJlDataDao;
    @Autowired
    TthDeviceAbnormalDao tthDeviceAbnormalDao;

    @Async
    //@Scheduled(cron ="0 0 0 * * ?")//每日凌晨执行
    //@Scheduled(cron ="0 */1 * * * ?")
    //@Scheduled(cron = "0 30 */2 * * ?")
    public void wpsRR(){
        //楼庄子水库树结构数据数据
        List<LZZ_PLATFORM_TREE> lzzplatformtrees = lzzplatformtreeDao.findAll();
        for (LZZ_PLATFORM_TREE lzz:lzzplatformtrees) {
            WpdRrInfo wpdRrInfo = new WpdRrInfo();
            wpdRrInfo.setId(lzz.getId());
            wpdRrInfo.setName(lzz.getName());
            wpdRrInfo.setpId(lzz.getpId());
            wpdRrInfoDao.saveAndFlush(wpdRrInfo);
        }
        //头屯河水库树结构数据
        List<IRRIGATED_PLATFORM_TREE> irrigated_platform_tree = irrigated_platform_treeDao.findAll();
        for (IRRIGATED_PLATFORM_TREE irr:irrigated_platform_tree) {
            WpdRrInfo wpdRrInfo = new WpdRrInfo();
            wpdRrInfo.setId(irr.getId());
            wpdRrInfo.setName(irr.getName());
            wpdRrInfo.setpId(irr.getParentId());
            wpdRrInfoDao.saveAndFlush(wpdRrInfo);
        }
    }

    @Async
    //@Scheduled(cron ="0 0 0 * * ?")//每日凌晨执行
    @Scheduled(cron ="0 3 * * * ?")//同步雨量点试试数据
    //@Scheduled(cron = "0 3 */1 * * ?")
    public void wpsPptn(){
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
        Date dts = new Date();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(dts);
        cal1.add(Calendar.HOUR, -24);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dts);
        cal.add(Calendar.HOUR, +0);
        String beginTime = sdfTime.format(cal1.getTime());
        String endTime = sdfTime.format(cal.getTime());

        //楼庄子雨量站数据
        String id = "92102";
        String name = "雨量站";
        List<WpdRrInfo> wpdRrInfo = wpdRrInfoDao.findYlId(id,name);
        for (WpdRrInfo wpdInfo:wpdRrInfo) {
            List<LZZ_RAINFALL_STATION> LzzSt = lzz_rainfall_stationDao.findAllData(wpdInfo.getId(),beginTime,endTime);
            for (LZZ_RAINFALL_STATION lzz:LzzSt) {
                try {
                    WpdStPptnR wpdStPptnR = new WpdStPptnR();
                    WpdStPptnRId wpdStPptnRId = new WpdStPptnRId();
                    wpdStPptnRId.setStcd(lzz.getTreeId());
                    wpdStPptnRId.setTm(lzz.getTime());
                    wpdStPptnR.setId(wpdStPptnRId);
                    wpdStPptnR.setDrp(lzz.getRainfall());
                    wpdStPptnRDao.save(wpdStPptnR);
                }catch (Exception e){
                    continue;
                }
            }
        }
        //头屯河水库树结构数据
         DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //List<WpdRrInfo> wpdfo = wpdRrInfoDao.findAllId(id,name);
        List<WpdRrInfo> wpdInio = wpdRrInfoDao.findAllTree(id,name);
        /*List<Tree> tr = new LinkedList<>();
        for (WpdRrInfo info:wpdInio) {
            Tree tree = new Tree();
            tree.setId(info.getId());
            tree.setName(info.getName());
            tree.setParentId(info.getpId());
            tr.add(tree);
        }
        List<Tree> li = SpanTreeUtils.buildTree(tr);*/
        for (WpdRrInfo wpdInfo:wpdInio) {
            List<Map<String,Object>> list = irrigated_platform_data_infoDao.findAllPid(wpdInfo.getId(),beginTime,endTime);
            for (int i = 0; i < list.size(); i++) {
                Map<String,Object> map = list.get(i);
                WpdStPptnR wpdStPptnR = new WpdStPptnR();
                WpdStPptnRId wpdStPptnRId = new WpdStPptnRId();
                wpdStPptnRId.setStcd(map.get("MONITOR_ID").toString());
                String dateStr = map.get("MONITOR_TIME")+":00";
                LocalDateTime date2 = LocalDateTime.parse(dateStr, fmt);
                wpdStPptnRId.setTm(date2);
                wpdStPptnR.setId(wpdStPptnRId);
                wpdStPptnR.setDrp(Double.valueOf(map.get("YQ_RAIN_FALL_ONE").toString()));
                wpdStPptnRDao.save(wpdStPptnR);
            }
        }
    }

    @Async
    //@Scheduled(cron ="0 0 0 * * ?")//每日凌晨执行
    //@Scheduled(cron ="0 */1 * * * ?")//同步水位站数据
    @Scheduled(cron = "0 */2 * * * ?")
    public void wpsZZ(){
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
        Date dts = new Date();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(dts);
        cal1.add(Calendar.HOUR, -24);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dts);
        cal.add(Calendar.HOUR, +0);
        String beginTime = sdfTime.format(cal1.getTime());
        String endTime = sdfTime.format(cal.getTime());

        //楼庄子水位站数据
        String id = "92102";
        String name = "水位站";
        List<WpdRrInfo> wpdRrInfo = wpdRrInfoDao.findYlId(id,name);
        for (WpdRrInfo wpdInfo:wpdRrInfo) {
            List<LZZ_GAUGING_STATION> lzz_gauging_stations = lzz_gauging_stationDao.findAllId(wpdInfo.getId(),beginTime,endTime);
            for (LZZ_GAUGING_STATION lzz:lzz_gauging_stations) {
                try {
                    WpdJlData wpdJlData = new WpdJlData();
                    WpdJlDataId wpdJlDataId = new WpdJlDataId();
                    wpdJlDataId.setStcd(lzz.getTreeId());
                    wpdJlDataId.setTm(lzz.getGatherTime());
                    wpdJlData.setId(wpdJlDataId);
                    if (lzz.getRelativeWaterLevel()!=null){
                        wpdJlData.setRz(Double.valueOf(lzz.getRelativeWaterLevel()));//水位
                    }
                    if (lzz.getFlow()!=null){
                        wpdJlData.setInq(Double.valueOf(lzz.getFlow()));
                    }
                    wpdJlDataDao.saveAndFlush(wpdJlData);
                }catch (Exception e){
                    continue;
                }
            }
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Map<String,Object>> list = irrigated_platform_data_infoDao.findAllData111(beginTime,endTime);
        for (int i = 0; i < list.size(); i++) {
            Map<String,Object> map = list.get(i);
            try {
                WpdJlData wpdJlData = new WpdJlData();
                WpdJlDataId wpdJlDataId = new WpdJlDataId();
                wpdJlDataId.setStcd(map.get("MONITOR_ID").toString());
                String dateStr = map.get("MONITOR_TIME")+":00";
                LocalDateTime date2 = LocalDateTime.parse(dateStr, fmt);
                wpdJlDataId.setTm(date2);
                wpdJlData.setId(wpdJlDataId);
                if (map.get("SQ_WATER_LEVEL")!=null){
                    wpdJlData.setRz(Double.valueOf(map.get("SQ_WATER_LEVEL").toString()));//水位
                }
                if (map.get("SQ_MONITOR_FLOW_RATE")!=null){
                    wpdJlData.setInq(Double.valueOf(map.get("SQ_MONITOR_FLOW_RATE").toString()));
                }

                if (map.get("SQ_MONITOR_FLOW_RATE")!=null){
                    wpdJlData.setSqMonitorFlowRate(Double.valueOf(map.get("SQ_MONITOR_FLOW_RATE").toString()));
                }
                if (map.get("SQ_TOTAL_FLOW")!=null){
                    wpdJlData.setSqTotalFlow(Double.valueOf(map.get("SQ_TOTAL_FLOW").toString()));
                }
                if (map.get("AVG_FLOW")!=null){
                    wpdJlData.setAvgFlow(Double.valueOf(map.get("AVG_FLOW").toString()));
                }
                if (map.get("AVG_WATER_DEEP")!=null){
                    wpdJlData.setAvgWaterDeep(Double.valueOf(map.get("AVG_WATER_DEEP").toString()));
                }
                if (map.get("YESTERDAY_AVG_FLOW")!=null){
                    wpdJlData.setYesterdayAvgFlow(Double.valueOf(map.get("YESTERDAY_AVG_FLOW").toString()));
                }
                if (map.get("WATER_DAILY")!=null){
                    wpdJlData.setWaterDaily(Double.valueOf(map.get("WATER_DAILY").toString()));
                }
                if (map.get("YESTERDAY_WATER_DAILY")!=null){
                    wpdJlData.setYesterdayWaterDaily(Double.valueOf(map.get("YESTERDAY_WATER_DAILY").toString()));
                }
                if (map.get("YEAR_WATER_DAILY")!=null){
                    wpdJlData.setYearWaterDaily(Double.valueOf(map.get("YEAR_WATER_DAILY").toString()));
                }
                if (map.get("VOLTAGE")!=null){
                    wpdJlData.setVoltage(Double.valueOf(map.get("VOLTAGE").toString()));
                }
                if (map.get("SQ_CAPACITY")!=null){
                    wpdJlData.setSqCapacity(Double.valueOf(map.get("SQ_CAPACITY").toString()));
                }
                wpdJlDataDao.save(wpdJlData);
            }catch (Exception e){
                continue;
            }
        }
    }

    @Async
    @Scheduled(cron = "0 5 * * * ?")
    public void wpsDevice(){
        String typeOf = "0";
        String name = "雨量站";
        List<WpdRrInfo> wpdRrInfos = wpdRrInfoDao.findListAll(typeOf,name);
        LocalDateTime currentTime = LocalDateTime.now();
        for (WpdRrInfo wpdInfo:wpdRrInfos) {
            WpdJlData wpdJlData = wpdJlDataDao.findData(wpdInfo.getId());
            if (wpdJlData==null){
                continue;
            }
            Duration duration = Duration.between(wpdJlData.getId().getTm(), currentTime);
            // 输出时间差（以分钟为单位）
            long minutes = duration.toMinutes();
            //查询异常里是否有此数据
            TthDeviceAbnormal tthab = tthDeviceAbnormalDao.findNull(wpdInfo.getId());
            if (tthab!=null){
                if (minutes>30){
                    tthab.setTime(wpdJlData.getId().getTm());
                    tthDeviceAbnormalDao.save(tthab);
                }else {
                    tthDeviceAbnormalDao.deleteById(tthab.getId());
                }
            }else {
                if (minutes>30){
                    TthDeviceAbnormal tthDeviceAbnormal = new TthDeviceAbnormal();
                    tthDeviceAbnormal.setId(UUID.randomUUID().toString().replace("-",""));
                    tthDeviceAbnormal.setName(wpdInfo.getName());
                    tthDeviceAbnormal.setTime(wpdJlData.getId().getTm());
                    tthDeviceAbnormal.setDeviceId(wpdInfo.getId());
                    tthDeviceAbnormalDao.save(tthDeviceAbnormal);
                }
            }

        }
    }

}
