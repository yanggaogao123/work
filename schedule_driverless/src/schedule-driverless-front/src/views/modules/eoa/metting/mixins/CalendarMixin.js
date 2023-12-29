/**
 * 会议日历
 */

import moment from 'moment';
import { getAction} from '@/api/manage'
import resourceTimelinePlugin from '@fullcalendar/resource-timeline';
import interactionPlugin from '@fullcalendar/interaction'

export const CalendarMixin = {
  data(){
    return {
      calendarPlugins: [
        resourceTimelinePlugin,interactionPlugin
      ],
      currentView:"resourceTimelineMonth",
      eventDataSource: [],
      eventMaps: {},
      item: {},
      buttonText: {
        today: '今天',
        resourceTimelineMonth: '月',
        resourceTimelineWeek: '周',
        resourceTimelineDay: '日'
      },
      //点击今天日列表图
      eventLimitClick: 'resourceTimelineDay',
      // 隐藏超出的事件
      eventLimit: true,
      firstDay: 0,
      header:{
        left:'prev,next, today',
        center:'title',
        right: 'resourceTimelineDay,resourceTimelineWeek,resourceTimelineMonth'
      },
      licenseKey: 'GPL-My-Project-Is-Open-Source',
      aspectRatio:2.4,
      handlerSelectAllow: info => {
        const currentDate = new Date()
        const start = info.start
        const end = info.end
        const viewAllow=this.currentView=='resourceTimelineDay'?true : false
        return (start <= end && start >= currentDate&&viewAllow&&this.adleEdit)
      },
      resources:  [],
      calendarEventsNew:  []
    }
  },
  created() {

  },
  watch: {
    resources(val) {
      this.$emit("change",val)
    }
  },
  methods:{
    moment,
    loadRoom(departId) {
      getAction("/metting/eoaMettingRoom/queryMettingRoom",{departId:departId}).then(res => {
        console.log("loadRoom==>res.result",res.result)
        if (res.success) {
          let resources =[]
          for(var item  of res.result) {
            //执行相关操作
            console.log("loadRoom==>item",item)
            let obj={};
            obj.id=item.id;
            obj.title=item.name;
            resources.push(obj)
          }
          this.resources=resources;
          if(this.resources==null||this.resources.length ==0){
            let calendarApi = this.$refs.baseCalendar.getApi() //通过getApi()方法获取
            if(this.currentView=='resourceTimelineDay'){
              calendarApi.changeView("resourceTimelineMonth")
            }else{
              calendarApi.changeView("resourceTimelineDay")
            }

          }
          console.log("loadRoom==>this.resources",this.resources)
          this.loadMetting()
        }
      })


    },
    loadMetting() {
      getAction("/metting/eoaMetting/queryMetting").then(res => {
        if (res.success) {
          let eventMaps = {}
          this.eventDataSource=res.result
          res.result.forEach(item => eventMaps[item.id] = item)
          this.eventMaps = eventMaps
          console.log("this.eventMaps==>",this.eventMaps)
          this.renderCalendar()
        }
      })
    },
    renderCalendar(currentView) {
      //TODO 判断修改重复的event
      this.calendarEventsNew = this.eventDataSource.map(item => {
        //console.log("item===>",item);
        let { id, name:title, beginTime: start, endTime: end,location } = item
        let event = {
          id,
          title,
          resourceId:location,
          start: start,
          end:end,
          backgroundColor: "#6f66f6", //增加颜色，从后台获取
        }
        //console.log("成功event===>",event)
        return event
      })
    },
    datesRender(info) {
      console.log('当呈现一组新的日期时触发。',info)
      this.currentView=info.view.type;
    },
    resourceRender(info) {
      console.log('resourceRender触发。',info)
    },
    handleEventClick(info) {
      if(this.adleEdit){
        this.$refs.mettingModal.eventEdit(info)
      }
    },
    select: function(info) {
      console.log("select===>",info)
      if(this.adleEdit) {
        this.$refs.mettingModal.eventAdd(info)
      }
    },
    eventEnter(info){
      console.log("eventMouseEnter==info",info)
      this.tranLeft = (info.jsEvent.pageX-490) + 'px';
      this.tranTop = (info.jsEvent.pageY-155) + 'px';
      this.$nextTick(() => {
        this.item=info.event
      })
      // 点击的时候显示模态框
      this.showBox = true;
    },
    eventLeave(info){
      console.log("eventMouseLeave==info",info)
      this.showBox =false
    }
  }

}