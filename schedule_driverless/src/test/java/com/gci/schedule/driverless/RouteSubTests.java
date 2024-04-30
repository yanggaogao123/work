package com.gci.schedule.driverless;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.scheduleD.StationPassenger;
import com.gci.schedule.driverless.service.schedule.BigDataService;
import com.gci.schedule.driverless.service.schedule.RouteSubService;
import com.gci.schedule.driverless.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SpringBootTest(classes = ScheduleDriverlessApplication.class)
@RunWith(SpringRunner.class)
public class RouteSubTests {
    @Autowired
    private RouteSubService routeSubService;
    @Autowired
    private BigDataService bigDataService;

    private final int N = 4;

    @Test
    public void testRouteSub(){
        System.out.println(JSONObject.toJSONString(routeSubService.getListByRouteId(194l)));
    }

    @Test
    public void testSchedule(){
        for(int i=1;i<=30;i++){
            Date runDate = DateUtil.getDateAddDay(new Date(),-i);
            List<StationPassenger> stationPassengerList = bigDataService.getStationPassengerList(DateUtil.date2Str(runDate,DateUtil.date_sdf),"7091");
            if(!CollectionUtils.isEmpty(stationPassengerList)){
                Integer maxPassengerNum = stationPassengerList.stream().sorted(Comparator.comparing(StationPassenger::getCurpeople).reversed())
                        .collect(Collectors.toList()).get(0).getCurpeople();
                System.out.println(DateUtil.date2Str(runDate,DateUtil.date_sdf)+" : "+maxPassengerNum);
            }

        }

    }

    public String convert(String str) {
        // ipv4 -> int
        if (str.contains(".")) {
            String[] fields = str.split("\\.");
            long result = 0;
            for (int i = 0; i < N; i++) {
                result = result * 256 + Integer.parseInt(fields[i]);
            }
            return "" + result;
        }
        // int -> ipv4
        else {
            long ipv4 = Long.parseLong(str);
            String result = "";
            for (int i = 0; i < N; i++) {
                result = ipv4 % 256 + "." + result;
                ipv4 /= 256;
            }
            return result.substring(0, result.length() - 1);
        }


    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // 注意 hasNext 和 hasNextLine 的区别

        while (in.hasNextLine()) { // 注意 while 处理多个 case
            int n = Integer.valueOf(in.nextLine());
            String[] strs = in.nextLine().split(" ");
            int limit = Integer.valueOf(in.nextLine());
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) {
                nums[i] = Integer.valueOf(strs[i]);
            }
            Map<Integer, Integer> map = new HashMap<>();
            for (int j = 0; j < n; j++) {
                if (map.containsKey(nums[j])) {
                    map.put(nums[j], map.get(nums[j]) + 1);
                } else {
                    map.put(nums[j], 1);
                }
            }
            Map<Integer, String> map2 = new HashMap<>();
            for (Integer key : map.keySet()) {
                if(map.get(key)<limit){
                    continue;
                }
                if (map2.containsKey(map.get(key))) {
                    String map2Val = map2.get(map.get(key));
                    if (map2Val.contains("_")) {
                        map2Val = map2Val + "_" + key;
                        String[] map2Str = map2Val.split("_");
                        Arrays.sort(map2Str);
                        map2.put(map.get(key), String.join("_", map2Str));
                    } else {
                        map2Val = Integer.valueOf(map2Val) > key ? key + "_" + map2Val : map2Val + "_" + key;
                        map2.put(map.get(key), map2Val);
                    }

                } else {
                    map2.put(map.get(key), key.toString());
                }

            }
            List<Integer> list = map2.keySet().stream().collect(Collectors.toList());
            Collections.reverse(list);
            for(Integer li : list){
                if(map2.get(li).contains("_")){
                    String[] liStr = map2.get(li).split("_");
                    for(int k=0; k< liStr.length;k++){
                        System.out.println(liStr[k]);
                    }

                }else{
                    System.out.println(map.get(li));
                }

            }
            
        }
    }

    /*public String convert(String s, int numRows) {
        if(numRows == 1)
            return s;

        int len = Math.min(s.length(), numRows);
        String []rows = new String[len];
        for(int i = 0; i< len; i++) rows[i] = "";
        int loc = 0;
        boolean down = false;

        for(int i=0;i<s.length();i++) {
            rows[loc] += s.substring(i,i+1);
            if(loc == 0 || loc == numRows - 1)
                down = !down;
            loc += down ? 1 : -1;
        }

        String ans = "";
        for(String row : rows) {
            ans += row;
        }
        return ans;
    }

    public static List<List<Integer>> permute(int[] nums){
        List<List<Integer>> res = new ArrayList<>();
        Integer[] newNum = new Integer[nums.length];
        IntStream stream = Arrays.stream(nums);
        Stream<Integer> integerStream = stream.boxed();
        change(integerStream.toArray(Integer[]::new),res,newNum,0);
        return res;
    }
    public static void change(Integer[] nums, List<List<Integer>> res, Integer[] newNum, int p){
        if(p==nums.length){
            res.add(Arrays.asList(newNum.clone()));
        }
        for(int i=0;i<nums.length;i++){
            if(nums[i]!=null){
                newNum[p]=nums[i];
                nums[i] = null;
                change(nums,res,newNum,p+1);
                nums[i] = newNum[p];
            }
        }
    };


    public static int maxSubArray(int[] nums) {
        List<Integer> list = new ArrayList<>();
        getMaxValue(nums,nums[0],0,0,list);
        return list.get(list.size()-1);
    }
    public static void getMaxValue(int[] nums,Integer res,int p,int temp,List<Integer> list){
        if(p==nums.length){
            list.add(res);
            return;
        }
        for(int i=p;i<nums.length;i++){
            temp +=nums[i];
            if(temp>res){
                res =temp;
            }
        }
        getMaxValue(nums,res,p+1,0,list);
    }*/




}
