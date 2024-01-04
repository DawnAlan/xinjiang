package com.cj.common.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSONObject;




/**
 *
 * @ClassName：HttpRequestUtil
 * @Description： Http请求
 */
public class HttpRequestUtil {
    private String defaultContentEncoding;

    public HttpRequestUtil() {
        this.defaultContentEncoding = Charset.defaultCharset().name();
    }

    /**
     * 默认的响应字符集
     */
    public String getDefaultContentEncoding() {
        return this.defaultContentEncoding;
    }

    /**
     * 设置默认的响应字符集
     */
    public void setDefaultContentEncoding(String defaultContentEncoding) {
        this.defaultContentEncoding = defaultContentEncoding;
    }

    public static String post(JSONObject json, String url) throws Exception{
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        CloseableHttpResponse  response = null;
        InputStream in = null;
        BufferedReader br = null;
        String result = "";
        try {
            StringEntity s = new StringEntity(json.toString(),"utf-8");
            s.setContentEncoding("UTF-8");
            /*发送json数据需要设置contentType*/
            s.setContentType("application/json");
            post.setEntity(s);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            response = httpclient.execute(post);
            in = response.getEntity().getContent();
            br = new BufferedReader(new InputStreamReader(in, "utf-8"));
            StringBuilder strber= new StringBuilder();
            String line = null;
            while((line = br.readLine())!=null){
                strber.append(line+'\n');
            }
            result = strber.toString();
            if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
                if(StringUtils.isBlank(result)) result = "服务器异常";
                throw new Exception(result);
            }
            // System.out.println("返回数据="+result);
        } catch (Exception e) {
            //System.err.println("调用接口出错：：：：：：：：：：：："+e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if(null != br) br.close();
            if(null != br) in.close();
            if(null != response) response.close();
            if(null != httpclient) httpclient.close();
        }
        return result;
    }

    public static String post(JSONObject json, String url, Map<String, String> headerMap) throws Exception{
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        CloseableHttpResponse  response = null;
        InputStream in = null;
        BufferedReader br = null;
        String result = "";
        try {
            StringEntity s = new StringEntity(json.toString(),"utf-8");
            s.setContentEncoding("UTF-8");
            /*发送json数据需要设置contentType*/
            s.setContentType("application/json");
            post.setEntity(s);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            Set<Entry<String, String>> headerEntries = headerMap.entrySet();
            for (Entry<String, String> headerEntry:headerEntries){
                post.setHeader(headerEntry.getKey(), headerEntry.getValue());
            }
            response = httpclient.execute(post);
            in = response.getEntity().getContent();
            br = new BufferedReader(new InputStreamReader(in, "utf-8"));
            StringBuilder strber= new StringBuilder();
            String line = null;
            while((line = br.readLine())!=null){
                strber.append(line+'\n');
            }
            result = strber.toString();
            if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
                if(StringUtils.isBlank(result)) result = "服务器异常";
                throw new Exception(result);
            }
            //System.out.println("返回数据="+result);
        } catch (Exception e) {
            //System.err.println("调用接口出错：：：：：：：：：：：："+e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            br.close();
            in.close();
            response.close();
            httpclient.close();
        }
        return result;
    }

    /**
     * ContentType.URLENCODED.getHeader()
     * @param map
     * @param url
     * @param headerMap
     * @param contentType
     * @return
     * @throws Exception
     */
    public static String post(Map<String, String> map, String url, Map<String, String> headerMap, String contentType) throws Exception{
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        CloseableHttpResponse  response = null;
        InputStream in = null;
        BufferedReader br = null;
        String result = "";
        try {
            List<NameValuePair> nameValuePairs = getNameValuePairList(map);
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            /*发送json数据需要设置contentType*/
            urlEncodedFormEntity.setContentType(contentType);
            post.setEntity(urlEncodedFormEntity);
            post.setHeader("Content-Type", contentType);
            Set<Entry<String, String>> headerEntries = headerMap.entrySet();
            for (Entry<String, String> headerEntry:headerEntries){
                post.setHeader(headerEntry.getKey(), headerEntry.getValue());
            }
            response = httpclient.execute(post);
            in = response.getEntity().getContent();
            br = new BufferedReader(new InputStreamReader(in, "utf-8"));
            StringBuilder strber= new StringBuilder();
            String line = null;
            while((line = br.readLine())!=null){
                strber.append(line+'\n');
            }
            result = strber.toString();
            if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
                if(StringUtils.isBlank(result)) result = "服务器异常";
                throw new Exception(result);
            }
            //System.out.println("返回数据="+result);
        } catch (Exception e) {
            //System.err.println("调用接口出错：：：：：：：：：：：："+e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            br.close();
            in.close();
            response.close();
            httpclient.close();
        }
        return result;
    }

    private static List<NameValuePair> getNameValuePairList(Map<String, String> map) {
        List<NameValuePair> list = new ArrayList<>();
        for(String key : map.keySet()) {
            list.add(new BasicNameValuePair(key,map.get(key)));
        }

        return list;
    }

    public static String post(String params, String url, Map<String, String> headerMap) throws Exception{
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        CloseableHttpResponse  response = null;
        InputStream in = null;
        BufferedReader br = null;
        String result = "";
        try {
            StringEntity s = new StringEntity(params.toString(),"utf-8");
            s.setContentEncoding("UTF-8");
            /*发送json数据需要设置contentType*/
            s.setContentType("application/json");
            post.setEntity(s);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            Set<Entry<String, String>> headerEntries = headerMap.entrySet();
            for (Entry<String, String> headerEntry:headerEntries){
                post.setHeader(headerEntry.getKey(), headerEntry.getValue());
            }
            response = httpclient.execute(post);
            in = response.getEntity().getContent();
            br = new BufferedReader(new InputStreamReader(in, "utf-8"));
            StringBuilder strber= new StringBuilder();
            String line = null;
            while((line = br.readLine())!=null){
                strber.append(line+'\n');
            }
            result = strber.toString();
            if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
                if(StringUtils.isBlank(result)) result = "服务器异常";
                throw new Exception(result);
            }
            //System.out.println("返回数据="+result);
        } catch (Exception e) {
            //System.err.println("调用接口出错：：：：：：：：：：：："+e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            br.close();
            in.close();
            response.close();
            httpclient.close();
        }
        return result;
    }

    public static String put(JSONObject json, String url, Map<String, String> headerMap) throws Exception {
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpPut post = new HttpPut(url);
        CloseableHttpResponse  response = null;
        InputStream in = null;
        BufferedReader br = null;
        String result = "";
        try {
            StringEntity s = new StringEntity(json.toString(),"utf-8");
            s.setContentEncoding("UTF-8");
            /*发送json数据需要设置contentType*/
            s.setContentType("application/json");
            post.setEntity(s);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            Set<Entry<String, String>> headerEntries = headerMap.entrySet();
            for (Entry<String, String> headerEntry:headerEntries){
                post.setHeader(headerEntry.getKey(), headerEntry.getValue());
            }
            response = httpclient.execute(post);
            in = response.getEntity().getContent();
            br = new BufferedReader(new InputStreamReader(in, "utf-8"));
            StringBuilder strber= new StringBuilder();
            String line = null;
            while((line = br.readLine())!=null){
                strber.append(line+'\n');
            }
            result = strber.toString();
            if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
                if(StringUtils.isBlank(result)) result = "服务器异常";
                throw new Exception(result);
            }
            //System.out.println("返回数据="+result);
        } catch (Exception e) {
            //System.err.println("调用接口出错：：：：：：：：：：：："+e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            br.close();
            in.close();
            response.close();
            httpclient.close();
        }
        return result;
    }

    public static String delete(String url, Map<String, String> headerMap) throws Exception {
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpDelete post = new HttpDelete(url);
        CloseableHttpResponse  response = null;
        InputStream in = null;
        BufferedReader br = null;
        String result = "";
        try {
            post.setHeader("Content-Type","application/json;charset=utf-8");
            Set<Entry<String, String>> headerEntries = headerMap.entrySet();
            for (Entry<String, String> headerEntry:headerEntries){
                post.setHeader(headerEntry.getKey(), headerEntry.getValue());
            }
            response = httpclient.execute(post);
            in = response.getEntity().getContent();
            br = new BufferedReader(new InputStreamReader(in, "utf-8"));
            StringBuilder strber= new StringBuilder();
            String line = null;
            while((line = br.readLine())!=null){
                strber.append(line+'\n');
            }
            result = strber.toString();
            if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
                if(StringUtils.isBlank(result)) result = "服务器异常";
                throw new Exception(result);
            }
            //System.out.println("返回数据="+result);
        } catch (Exception e) {
            //System.err.println("调用接口出错：：：：：：：：：：：："+e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            br.close();
            in.close();
            response.close();
            httpclient.close();
        }
        return result;
    }

    public static String get(JSONObject paramsObj, String url, Map<String, String> headerMap) throws Exception {
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        CloseableHttpResponse  response = null;
        InputStream in = null;
        BufferedReader br = null;
        String result = "";
        try {
            StringBuffer param = new StringBuffer();
            int i = 0;

            Set<Entry<String, Object>> entries = paramsObj.entrySet();
            for (Entry<String, Object> entry:entries){
                if (i == 0)
                    param.append("?");
                else
                    param.append("&");
                param.append(entry.getKey()).append("=").append(entry.getValue());
                i++;
            }

            url += param;
            HttpGet post = new HttpGet(url);
//            post.setHeader("Content-Type","application/json;charset=utf-8");

            Set<Entry<String, String>> headerEntries = headerMap.entrySet();
            for (Entry<String, String> headerEntry:headerEntries){
                post.setHeader(headerEntry.getKey(), headerEntry.getValue());
            }

            response = httpclient.execute(post);
            in = response.getEntity().getContent();
            br = new BufferedReader(new InputStreamReader(in, "utf-8"));
            StringBuilder strber= new StringBuilder();
            String line = null;
            while((line = br.readLine())!=null){
                strber.append(line+'\n');
            }
            result = strber.toString();
            if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
                if(StringUtils.isBlank(result)) result = "服务器异常";
                throw new Exception(result);
            }
            //System.out.println("返回数据="+result);
        } catch (Exception e) {
            // System.err.println("调用接口出错：：：：：：：：：：：："+e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            br.close();
            in.close();
            response.close();
            httpclient.close();
        }
        return result;
    }
}

