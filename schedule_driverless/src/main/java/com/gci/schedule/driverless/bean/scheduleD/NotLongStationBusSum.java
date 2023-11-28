package com.gci.schedule.driverless.bean.scheduleD;

//非全程在某个站点开始的车辆和
public class NotLongStationBusSum implements Cloneable {
    private  Integer  notLongBusNum ;//该半个小时内非全程发班的总班次
    private  Integer  runTimeNum ;//时间段，6点是12，6点半是13
    private  Integer  direction; //方向
    private  Integer  firstRouteStationId;//开始线路站点ID
    private  Integer  lastRouteStationId;//结束线路站点ID

   public Integer getNotLongBusNum() {
      return notLongBusNum;
   }

   public void setNotLongBusNum(Integer notLongBusNum) {
      this.notLongBusNum = notLongBusNum;
   }

   public Integer getRunTimeNum() {
      return runTimeNum;
   }

   public void setRunTimeNum(Integer runTimeNum) {
      this.runTimeNum = runTimeNum;
   }

   public Integer getDirection() {
      return direction;
   }

   public void setDirection(Integer direction) {
      this.direction = direction;
   }

   public Integer getFirstRouteStationId() {
      return firstRouteStationId;
   }

   public void setFirstRouteStationId(Integer firstRouteStationId) {
      this.firstRouteStationId = firstRouteStationId;
   }

   public Integer getLastRouteStationId() {
      return lastRouteStationId;
   }

   public void setLastRouteStationId(Integer lastRouteStationId) {
      this.lastRouteStationId = lastRouteStationId;
   }
}