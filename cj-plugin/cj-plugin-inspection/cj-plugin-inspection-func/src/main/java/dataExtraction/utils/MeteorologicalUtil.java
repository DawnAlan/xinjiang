package dataExtraction.utils;

import com.alibaba.fastjson.JSONObject;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Description:</p>
 *
 * @author chenzhitao
 * @date 2023年03月17日
 */
public class MeteorologicalUtil {


    public static String getSHA256StrJava(String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    private static String byte2Hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String temp = null;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                // 1得到一位的进行补0操作
                sb.append("0");
            }
            sb.append(temp);
        }
        return sb.toString();
    }


    /**
     * 获取请求头参数
     *
     * @return
     */
    public static Map<String, String> getHeaderPar() {
        HashMap<String, String> map = new HashMap<>();
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long epoch = 0;
            Date d = new Date();
            String t = df.format(d);
            // unix时间戳
            epoch = df.parse(t).getTime() / 1000;
            // 计算签名
            String api_signature = getSHA256StrJava(epoch + "jMLymaDOeeKb7Lvopg8pZK2tKRiGKMZN" +
                    "2fc62edd-03ee-4c32-8db8-e7e9c77ed05a" + epoch);
            map.put("paasid", "gdsslszlspt");
            map.put("signature", api_signature.toUpperCase());
            map.put("timestamp", String.valueOf(epoch));
            map.put("nonce", "2fc62edd-03ee-4c32-8db8-e7e9c77ed05a");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    /**
     * 通过url获取对应的数据信息
     *
     * @return
     */
    public static String getFromShuZiLuanSheng(String url, JSONObject object) {
        JSONObject inputJson = new JSONObject();
        inputJson.put("query", object);
        Map<String, String> headerPar = getHeaderPar();
        String resultString = OkHttpUtils.builder().url(url)
                .addHeader("accept", "application/json")
                .addHeader("Charset", "UTF-8")
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("x-tif-paasid", headerPar.get("paasid"))
                .addHeader("x-tif-signature", headerPar.get("signature"))
                .addHeader("x-tif-timestamp", headerPar.get("timestamp"))
                .addHeader("x-tif-nonce", headerPar.get("nonce"))
                .post(true, inputJson).async();
        return resultString;
    }

}
