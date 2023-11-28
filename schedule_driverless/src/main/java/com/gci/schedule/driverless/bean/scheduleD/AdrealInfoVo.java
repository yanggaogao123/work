package com.gci.schedule.driverless.bean.scheduleD;

import com.gci.schedule.driverless.bean.AdrealInfo;
import lombok.Data;

import java.util.List;

@Data
public class AdrealInfoVo {
   private List<AdrealInfo> mainList;
   private List<AdrealInfo> subList;
   //0:共首站，1:共末站，2:共首末站，3:首站为支援线路末站，4:末站为支援线路首站，5:首末站相邻
   private Integer simulationType;
}
