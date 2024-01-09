/**
 * 我的计划
 */

import Vue from 'vue'
import { ACCESS_TOKEN } from "@/store/mutation-types"
import moment from 'moment';
import { defaultSettings, calendarViewType, CalendarMixins } from '@/utils/LunarFullCalendarUtil'
import { getAction} from '@/api/manage'

export const CmsMixin = {
  mixins: [CalendarMixins],
  data(){
    return {
      //token header
      tokenHeader: {'X-Access-Token': Vue.ls.get(ACCESS_TOKEN)},

      calendarEvent: [],
      eventDataSource: [],
      // k=id, v=record
      eventMaps: {},

    }
  },
  created() {
    this.loadData()
  },
  computed: {
    calendarProps() {
      return {
        config: {
          ...defaultSettings,
          contentHeight:360,
          eventLimit: 1,
         /* eventLimitText: "更多",*/
          fixedWeekCount:false,
          showNonCurrentDates:true,
          // 禁用编辑和拖动
          editable: false,
          selectable: true,
          // 是否显示农历
          lunarCalendar: false,
          select:this.select,
          ...this.getCalendarConfigEventHandler(),
        },
        events: this.calendarEvent,
      }
    }
  },
  methods:{
    moment,
    loadData() {
      getAction("/eoa/plan/queryMyCreation").then(res => {
        if (res.success) {
          this.eventDataSource = res.result

          let eventMaps = {}
          res.result.forEach(item => eventMaps[item.id] = item)
          this.eventMaps = eventMaps

          this.renderCalendar()
        } else {
          this.$message.warn(res.message)
        }
      }).finally(() => {
        this.cardLoading = false
      })
    },
    renderCalendar(currentView) {
      console.log("renderCalendar",currentView)
      const dateFormat = 'YYYY-MM-DD'
      const timeFormat = 'HH:mm:ss'
      const datetimeFormat = dateFormat + ' ' + timeFormat

      this.calendarEvent = this.eventDataSource.map(item => {
        let { id, title, startTime: start, endTime: end, allDay, urgentLevel: level, status,planColor } = item
        let event = {
          id, title,
          start: moment(start),
          end: moment(end),
          allDay: allDay === '1',
          backgroundColor: planColor, //增加颜色，从后台获取
          borderColor: planColor
        }
        // 解决月视图下跨天少显示一天的问题
        if (event.allDay) {
          event.end = moment(event.end.format(dateFormat) + ' 24:00:00')
        }
        /* 不同的视图做出不同的微调 */
        // 周视图下
        if (this.calenderCurrentViewType === calendarViewType.agendaWeek) {
          // ※ 如不属于全天活动，但跨天，则以全天方式展示，标注开始时间
          if (!event.allDay) {
            // 如果开始时间和结束时间不相等，说明跨天了
            if (event.start.format(dateFormat) !== event.end.format(dateFormat)) {
              event.allDay = true
              event.title = event.start.format(timeFormat.substr(0, 5)) + ' ' + event.title
            }
          }
        }
        // 日视图下（else if）
        else if (currentView && (this.calenderCurrentViewType === calendarViewType.agendaDay)) {
          // ※ 如果开始和结束时间是超过当天，即跨天且铺满当天的情况，则以全天方式展示
          if (!event.allDay) {
            let currentTime = currentView.start.format(dateFormat)

            // 如果结束时间是当天时间，则把当天实际加上 ' 23:59:59'
            if (event.end.format(dateFormat) === currentTime) {
              currentTime += ' 23:59:59'
            }
            currentTime = moment(currentTime)
            if (event.start <= currentTime && event.end >= currentTime) {
              event.allDay = true
            }
          }
        }
        return event
      })

      this.$nextTick(() => {
        this.calendarReloadEvents()
      })
    },

    /** 视图被更改事件（包括上一天下一天的情况） */
    handleViewRender(type, view) {
      console.log("handleViewRender",type);
      if (type === calendarViewType.agendaDay) {
        this.renderCalendar(view)
      }
    },

    /** 处理视图被切换事件 */
    handleViewChanged(type, view) {
      console.log("handleViewChanged",type);
      if (type === calendarViewType.agendaWeek) {
        this.renderCalendar()
      }
    },

    /** 事件渲染之前的事件 */
    handleEventRender(event, element, view) {

    },

    /* 点击空百日历处触发 */
    handleDayClick(date, jsEvent, view) {
      // isAllDay算法：如果格式化后的日期不带时间部分（HH:mm:ss），则长度 <= 10，即可认为是全天
      const isAllDay = (date.format().length <= 10)
      this.$refs.baseModal.addEvent(date, view, isAllDay)
    },

    /** 点击事件时触发 */
    handleEventSelected(event, jsEvent, view) {

      let { id } = event
      this.$refs.baseModal.edit(this.eventMaps[id])
    },
    handleModalOk() {
      this.loadData()

    },
    //update--begin--autor:wangshuai-----date:20191230------for：添加选择事件------
    select(start, end,jsEvent,view){

      const isAllDay = (start.format().length <= 10)
      this.$refs.baseModal.addEvent(start,end, view,isAllDay)
    },
    //update--end--autor:wangshuai-----date:20191230------for：添加选择事件------

  }

}