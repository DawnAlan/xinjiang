package com.cj.fourPredictions.func.modular.flood.weather.service;

import com.cj.fourPredictions.func.modular.flood.weather.bean.vo.ForecastVO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class ForecastService {

    public List<ForecastVO> getForecast() throws IOException, ParseException {
        return ForecastRainfallGet();
    }
    private List<ForecastVO> ForecastRainfallGet() throws IOException, ParseException {
        List<ForecastVO> forecastVOList = new ArrayList<ForecastVO>();
        String url = "https://weathernew.pae.baidu.com/weathernew/pc?query=%E6%96%B0%E7%96%86%E6%98%8C%E5%90%89%E5%A4%A9%E6%B0%94&srcid=4982";
        HttpClient client = HttpClients.createDefault();
        // 要调用的接口方法
        //String url = "https://weathernew.pae.baidu.com/weathernew/pc?query=%E6%99%AE%E5%85%B0%E5%8E%BF%E5%A4%A9%E6%B0%94&srcid=4982";
        HttpGet httpGet = new HttpGet(url);
        String jsonObject = null;
        HttpResponse res = client.execute(httpGet);
        jsonObject = EntityUtils.toString(res.getEntity());
        String temperaturePointArr = jsonObject.split("\r\n|\r|\n")[0].split("\"temperaturePointArr\":")[1].split(",\"middleTemperature\"")[0];
        //"temperaturePointArr":
        String str = generateCode(jsonObject);
        String[] lines = str.split("\r\n|\r|\n");
        String i32 = lines[32];
        int i = lines.length;
        //时间
        String timeNodeArr = "";
        //预报降雨量
        String precipitationPointArr = "";
        for (String line : lines) {
            if (line.startsWith("data[\"timeNodeArr\"]=")) {
                timeNodeArr = line.replace("data[\"timeNodeArr\"]=", "").replace(";", "");
            }
            if (line.startsWith("data[\"precipitationPointArr\"]=")) {
                precipitationPointArr = line.replace("data[\"precipitationPointArr\"]=", "").replace(";", "");
            }
        }

        List<String> timeNodeList = new Gson().fromJson(timeNodeArr, new TypeToken<List<String>>() {
        }.getType());
        List<List<String>> precipitationPointList = new Gson().fromJson(precipitationPointArr, new TypeToken<List<List<String>>>() {
        }.getType());

        List<List<String>> temperaturePointArrList = new Gson().fromJson(temperaturePointArr, new TypeToken<List<List<String>>>() {
        }.getType());


        String[][] rian = new String[24][4];
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");

        Date date = new Date();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:00");
        date = sdf1.parse(sdf1.format(date));

        for (int r = 0; r < (long) timeNodeList.size(); r++) {
            ForecastVO forecastVO = new ForecastVO();
            forecastVO.setTime(date);
            forecastVO.setTemperature(new BigDecimal(temperaturePointArrList.get(r).get(2)));
            forecastVO.setRainfall(new BigDecimal(precipitationPointList.get(r).get(2)));
            forecastVO.setWeather(precipitationPointList.get(r).get(3));
            forecastVOList.add(forecastVO);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.HOUR_OF_DAY, 1);// 24小时制
            date = cal.getTime();
        }
        ForecastVO forecastVO = new ForecastVO();
        forecastVO.setTime(date);
        forecastVO.setTemperature(new BigDecimal(temperaturePointArrList.get(timeNodeList.size() - 1).get(2)));
        forecastVO.setRainfall(new BigDecimal(precipitationPointList.get(timeNodeList.size() - 1).get(2)));
        forecastVO.setWeather(precipitationPointList.get(timeNodeList.size() - 1).get(3));
        forecastVOList.add(forecastVO);

        return forecastVOList;
    }

    private static String generateCode(String html) {
        ScriptEngineManager manager = new ScriptEngineManager();

        ScriptEngine engine = manager.getEngineByName("javascript");
//        String html = ""; // html串
        Document doc = Jsoup.parse(html);
        Element tds = doc.getElementsByTag("script").get(5); // 标识获取html中第一个<script>标签
//        String data = tds.toString().replaceAll("\\&[a-zA-Z]{0,9};", "").replaceAll("<[^>]*>", "\n\t"); // 去除html中的标签
//        String o = null;
//        try {
//            engine.eval(data);
//            Invocable invokeEngine = (Invocable) engine;
//            o = (String) invokeEngine.invokeFunction("reverse", "--code--", "==type=="); //执行的js，以及参数
//        } catch (ScriptException | NoSuchMethodException e) {
//            e.printStackTrace();
//        }

        return tds.toString();
    }


}
