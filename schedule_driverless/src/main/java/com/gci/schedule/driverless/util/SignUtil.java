package com.gci.schedule.driverless.util;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2019-12-18 2:17 下午
 * @version: v1.0
 * @Modified by:
 **/
public class SignUtil {

    public static void main(String[] args) throws Exception {

        String appid="paiti_yiqi";
        String appkey="86CE693926B9241CAB202500FC94E3";

        /**
         * get 请求示例
         */
         //String url = "http://xxxxxx/dispatch-auto-api/paiti/stationStas";
         //HashMap<String, String> param = new HashMap<String, String>() {{
         //    put("routeCode", "05600");
         //}};
         //Map<String, Object> signParam = toSignParam(param, appid, appkey);
         //url = url + "?" +"appid="+signParam.get("appid")+"&"+"reqtime="+signParam.get("reqtime")
         //        +"&"+"reqpara="+ URLEncoder.encode(JSONObject.toJSONString(signParam.get("reqpara")), "UTF-8")
         //        +"&"+"data="+URLEncoder.encode(JSONObject.toJSONString(signParam.get("data")), "UTF-8")
         //        +"&"+"sign="+signParam.get("sign");
         //System.out.println("请求接口+参数："+url);
         //URL obj = new URL(url);
         //HttpURLConnection con = (HttpURLConnection) obj.openConnection();
         //con.setRequestMethod("GET");
         //BufferedReader in = new BufferedReader(
         //        new InputStreamReader(con.getInputStream()));
         //String inputLine;
         //StringBuffer response = new StringBuffer();
         //while ((inputLine = in.readLine()) != null) {
         //    response.append(inputLine);
         //}
         //in.close();

        /**
         * post 请求示例
         */
        String url = "http://113.108.166.149:8000/dispatch-auto-api/paiti/schedulePlans";
        String data = "{\"routeCode\":\"05600\",\"planDate\":\"2022-01-27\"}";
        long startTime = System.currentTimeMillis();
        Object parse = JSONObject.parseObject(data);
        Map<String, Object> signParam = toSignParam(parse, appid, appkey);
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        con.setDoOutput(true);
        String param = JSONObject.toJSONString(signParam);
        System.out.println("请求接口："+url);
        System.out.println("请求体："+param);
        con.getOutputStream().write(param.getBytes(StandardCharsets.UTF_8));
        con.getOutputStream().flush();
        con.getOutputStream().close();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        long endTime = System.currentTimeMillis();
        in.close();
        //打印结果
        System.out.println("返回值["+(endTime-startTime) / 1000.0+"]："+response.toString());
    }

    /**
     * 获取签名后参数
     *
     * @param data   请求参数JSON格式,无参数为null
     * @param appId  用户唯一凭证 必须
     * @param appKey 应用密匙 必须
     * @return 签名后参数Map
     */
    public static Map<String, Object> toSignParam(Object data, String appId, String appKey) {
        HashMap<String, Object> params = new HashMap<>();
        TreeMap<String, Object> treeMap = new TreeMap<>();
        String jsonData = "{}";
        if (data != null) {
            jsonData = JSONObject.toJSONString(data);
        }
        String timestamp = getTimestamp();
        treeMap.put("appid", appId);
        treeMap.put("reqtime", timestamp);
        treeMap.put("reqpara", JSONObject.parseObject("{}"));
        treeMap.put("data", jsonData);
        String sign = getSign(treeMap, appKey);
        params.put("appid", appId);
        params.put("reqtime", timestamp);
        params.put("reqpara", JSONObject.parseObject("{}"));
        params.put("data", JSONObject.parseObject(jsonData));
        params.put("sign", sign);
        params.put("reqId", timestamp);
        return params;
    }

    /**
     * 获取签名
     *
     * @param map    系统参数
     * @param appKey 应用密匙
     * @return 签名
     */
    private static String getSign(TreeMap<String, Object> map, String appKey) {
        StringBuffer sb = new StringBuffer();
        //应用密匙添加到请求参串的头部
        sb.append(appKey);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey()).append(entry.getValue());
        }
        sb.append(appKey);
        //应用密匙添加到请求参串的尾部
        String str = sb.toString();
        //SHA1签名运算
        return sha1(str);
    }

    private static String sha1(String str) {
        String reStr = null;
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            sha.update(str.getBytes());
            byte ss[] = sha.digest();
            reStr = bytes2String(ss);
        } catch (NoSuchAlgorithmException e) {

        }
        return reStr;
    }

    private static String getTimestamp() {
        return new Date().getTime() + "";
    }

    /**
     * 将字节数组转换为字符串
     *
     * @param aa
     * @return
     */
    private static String bytes2String(byte[] aa) {
        String hash = "";
        for (int i = 0; i < aa.length; i++) {
            int temp;
            if (aa[i] < 0) {
                temp = 256 + aa[i];
            } else {
                temp = aa[i];
            }
            if (temp < 16) {
                hash += "0";
            }
            hash += Integer.toString(temp, 16);
        }
        hash = hash.toUpperCase();
        return hash;
    }

}
