package dataExtraction.service;

import com.alibaba.fastjson.JSONArray;
import dataExtraction.ghd.dao.WpdRrInfoDao;
import dataExtraction.ghd.entity.WpdRrInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BasicService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WpdRrInfoDao wpdRrInfoDao;

    public WpdRrInfo queryNode(String ndcdId) {
        WpdRrInfo wpdRrInfo = wpdRrInfoDao.findId(ndcdId);
        return wpdRrInfo;
    }

    public List<WpdRrInfo> queryRRs(String id) {
        //水库数据
        JSONArray jsonArray = new JSONArray();
        List<WpdRrInfo> wpdRrInfos = wpdRrInfoDao.findAllId(id);
        return wpdRrInfos;
    }

    public WpdRrInfo modify(WpdRrInfo wpdRrInfo) {
        wpdRrInfoDao.save(wpdRrInfo);
        return wpdRrInfo;
    }

    public List<WpdRrInfo> queryLast(String typeName) {
        String type = "雨量站";
        String typeOf = "0";
        List<WpdRrInfo> wpdRrInfos = wpdRrInfoDao.findLast(typeName,typeOf,type);
        return wpdRrInfos;
    }

    public List<WpdRrInfo> searchTreeMenu() {
        List<WpdRrInfo> menus = wpdRrInfoDao.findAll();
        List<WpdRrInfo> TreeMenu = menus.stream().filter(m -> m.getpId().equals("0")).map(
                (m) -> {
                    m.setChildren(getChildrens(m,menus));
                    return m;
                }
        ).collect(Collectors.toList());
        return TreeMenu;
    }


    private List<WpdRrInfo> getChildrens(WpdRrInfo root, List<WpdRrInfo> all) {

        List<WpdRrInfo> children = all.stream().filter(m -> {
            return Objects.equals(m.getpId(), root.getId());
        }).map(
                (m) -> {
                    m.setChildren(getChildrens(m, all));
                    return m;
                }
        ).collect(Collectors.toList());
        return children;
    }

    public List<WpdRrInfo> queryRR() {
        List<WpdRrInfo> wpdRrInfos = searchTreeMenu();
        return wpdRrInfos;
    }

    public void deleteRR(String id) {
        wpdRrInfoDao.deleteById(id);
    }

    public List<WpdRrInfo> queryAllData() {
        List<WpdRrInfo> wpdRrInfos = wpdRrInfoDao.findAll();
        return wpdRrInfos;
    }

    public List<WpdRrInfo> queryRain(String typeName) {
        String infoName = "雨量站";
        List<WpdRrInfo> wpdRrInfos = wpdRrInfoDao.findAllData(typeName,infoName);
        return wpdRrInfos;
    }

    public JSONArray queryRRRain() {
        JSONArray jsonArray = new JSONArray();
        String id = "0";
        List<WpdRrInfo> wpdRrInfos = wpdRrInfoDao.findAllId(id);
        for (WpdRrInfo wpdRR:wpdRrInfos) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("typeName",wpdRR.getTypeName());
            map.put("name",wpdRR.getName());
            map.put("id",wpdRR.getId());
            jsonArray.add(map);
            String typeName = wpdRR.getTypeName();
            String infoName = "雨量站";
            List<WpdRrInfo> wpd = wpdRrInfoDao.findAllData(typeName,infoName);
            for (WpdRrInfo wpdInfo:wpd) {
                Map<String,Object> mps = new LinkedHashMap<>();
                //map.put("typeName",wpdInfo.getTypeName());
                mps.put("name",wpdInfo.getName());
                mps.put("id",wpdInfo.getId());
                mps.put("pid",wpdRR.getId());
                jsonArray.add(mps);
            }
        }
        return jsonArray;
    }
}
