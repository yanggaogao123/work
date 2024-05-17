package com.gci.schedule.driverless.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.OgranTree;
import com.gci.schedule.driverless.bean.paiti.Bus;
import com.gci.schedule.driverless.bean.paiti.Route;
import com.gci.schedule.driverless.common.MyException;
import com.gci.schedule.driverless.service.schedule.PaitiService;
import com.gci.schedule.driverless.service.schedule.QueryJsonService;
import com.gci.schedule.driverless.util.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 启动时缓存机构树及最下层节点机构线路，提供获取线路列表的API
 *
 * @author pdl
 */
@Component
public class RouteListCacheComponent implements CommandLineRunner {

    private static Logger log = LoggerFactory.getLogger(RouteListCacheComponent.class);

    @Value("${head.url}")
    private String headUrl;
    @Value("${getRouteList.url}")
    private String routeList;

    @Autowired
    private QueryJsonService queryJsonService;

    @Autowired
    private PaitiService paitiService;

    //完整机构树
    private static OgranTree ORGAN_TREE;

    //最下层机构及其线路列表
    private static Map<String, List<Map>> ORGAN_ROUTE = new HashMap<>();

    //顶层机构ID
    private static final String TOP_ORGAN = "0";

    private Map<String, List<Bus>> dicheOrganIdAndBusMap;
    private Map<String, List<Route>> dicheRouteCodeAndOrganIdMap;

    @Override
    public void run(String... args) {
        /*try {
            //缓存电车线路与机构关系数据
            dicheRouteCodeAndOrganIdMap = paitiService.queryDicheRouteCodeAndOrganIdMap();
            dicheOrganIdAndBusMap = paitiService.queryDicheOrganIdAndBusMap();
        } catch (Exception e) {
            log.error("缓存电车线路与机构关系数据失败, msg={}", e.getMessage(),e);
        }


        String tree;
        try {
            tree = queryJsonService.organTree(TOP_ORGAN);
        } catch (Exception e) {
            log.error("初始化机构树失败, msg={}", e.getMessage());
            return;
        }
        JSONArray jsonArray = JSONArray.parseArray(tree);
        if(jsonArray!=null) {
        	OgranTree ogranTree = jsonArray.toJavaList(OgranTree.class).get(0);
            ORGAN_TREE = ogranTree;

            List<String> lowestLevelNodes = getLowestLevelNode(TOP_ORGAN);
            lowestLevelNodes.forEach(organId -> {
                String url = headUrl + routeList + "?organId=" + organId;
                JSONObject routeListObject = HttpClientUtils.httpGet(url);
                JSONArray retData = routeListObject.getJSONArray("retData");
                List<Map> maps = retData.toJavaList(Map.class);
                ORGAN_ROUTE.put(organId, maps);
            });
        }*/
    }

    public void setDicheOrganIdAndBusMap(Map<String, List<Bus>> dicheOrganIdAndBusMap) {
        this.dicheOrganIdAndBusMap = dicheOrganIdAndBusMap;
    }

    public void setDicheRouteCodeAndOrganIdMap(Map<String, List<Route>> dicheRouteCodeAndOrganIdMap) {
        this.dicheRouteCodeAndOrganIdMap = dicheRouteCodeAndOrganIdMap;
    }

    public Map<String, List<Route>> getDicheRouteCodeAndOrganIdMap() {
        return dicheRouteCodeAndOrganIdMap;
    }

    public Map<String, List<Bus>> getDicheOrganIdAndBusMap() {
        return dicheOrganIdAndBusMap;
    }

    public List<Map> getFinalRouteList(String organId) {

        log.info("当前查询线路的机构ID为 {}", organId);

        if (Objects.isNull(ORGAN_TREE)) {
            run();
        }

        //先根据机构查询线路
        String url = headUrl + routeList + "?organId=" + organId;

        log.info("查询线路的url {}", url);

        JSONObject routeListObject = HttpClientUtils.httpGet(url);

        JSONArray retData = routeListObject.getJSONArray("retData");
        List<Map> currentOrganRoutes = retData.toJavaList(Map.class);

        List<Map> result = new ArrayList<>(currentOrganRoutes);
        //从缓存中拿该机构最下层节点的机构
        List<String> lowestLevelNodes = getLowestLevelNode(organId);
        //合并
        lowestLevelNodes.forEach(item -> result.addAll(ORGAN_ROUTE.getOrDefault(item, Collections.emptyList())));
        //去重
        ArrayList<Map> list = result.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(m -> m.get("routeId").toString()))), ArrayList::new));
        //排序
        list.sort(Comparator.comparing(a -> Integer.parseInt(a.get("sort") == null? "9999":a.get("sort").toString())));
        return list;
    }


    /**
     * 根据机构ID得到所有最下级节点机构ID
     */
    private List<String> getLowestLevelNode(String organId) {
        log.info("根据机构ID得到所有最下级节点机构ID organId: {}", organId);
        log.info("ORGAN_TREE {}", JSON.toJSONString(ORGAN_TREE));
        OgranTree tree = getOrganNode(organId, ORGAN_TREE);
        if (Objects.isNull(tree)) {
            throw new MyException("500", "您所在公司不在集团机构树中，请联系相关人员处理");
        }
        log.info("该机构所在的节点 {}", JSON.toJSONString(tree));
        List<String> result = new ArrayList<>();
        getNodes(tree, result);
        return result;
    }

    /**
     * 从机构树获取当前节点
     */
    private OgranTree getOrganNode(String organId, OgranTree organTree) {
        OgranTree result = null;
        if (organTree.getId().equals(Long.parseLong(organId))) {
            return organTree;
        } else {
            for (OgranTree item : organTree.getChildren()) {
                OgranTree organNode = getOrganNode(organId, item);
                if (Objects.nonNull(organNode)) {
                    result = organNode;
                }
            }
        }
        return result;
    }

    /**
     * 递归将最下级节点ID添加至list
     */
    private void getNodes(OgranTree tree, List<String> list) {
        if (tree.getChildren() == null || tree.getChildren().size() < 1) {
            list.add(String.valueOf(tree.getId()));
        } else {
            tree.getChildren().forEach(item -> getNodes(item, list));
        }
    }

}
