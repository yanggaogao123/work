package com.gci.schedule.driverless.dynamic.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: yaozy
 * @Description:
 * @Date: Created in 12:02 2017/9/28.
 */
public class JsonUtil {
    public static Object getStringValeByKey(String json, String... keys){
        Object returnStr = null;
        try{
            JSONObject jsonObject = JSON.parseObject(json);
            for(int i=0;i<keys.length-1;i++) {
            	jsonObject=jsonObject.getJSONObject(keys[i]);
            }
            returnStr = jsonObject.get(keys[keys.length-1]);
        }catch (Exception e){}
        return returnStr;
    }
    
    public static <T> List<T> getListByKey(String json,Class<T> c,String...keys){
    	JSONObject jsonObject = JSON.parseObject(json);
        for(int i=0;i<keys.length-1;i++) {
        	jsonObject=jsonObject.getJSONObject(keys[i]);
        }
        JSONArray jsonArray = jsonObject.getJSONArray(keys[keys.length-1]);
        List<T> list=new ArrayList<T>();
        for(int i=0;i<jsonArray.size();i++) {
        	JSONObject jo=jsonArray.getJSONObject(i);
        	T t=jo.toJavaObject(c);
        	list.add(t);
        }
    	return list;
    }
    
    public static <T> T getObjectByKey(String json,Class<T> c,String...keys){
    	JSONObject jsonObject = JSON.parseObject(json);
        for(int i=0;i<keys.length;i++) {
        	jsonObject=jsonObject.getJSONObject(keys[i]);
        }
    	T t=jsonObject.toJavaObject(c);
    	return t;
    }
}
