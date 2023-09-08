package com.gci.schedule.driverless.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class HttpUtils {

    public static String Post(String urlstr,String bodystr) throws Exception{
        URL url = new URL(urlstr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
//        connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");//设置参数类型是json格式
//        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----footfoodapplicationrequestnetwork");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.connect();

        String body = bodystr;
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
        writer.write(body);
//        writer.write("id=2");
        writer.close();

        int responseCode = connection.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK){
            InputStream inputStream = connection.getInputStream();
            String result = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining(System.lineSeparator()));//将流转换为字符串。
            return result;
        }
        return null;
    }
}

