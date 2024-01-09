/**
 * 日历
 */

import moment from 'moment'
import { getAction } from '@/api/manage'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import '@fullcalendar/core/main.css'
import '@fullcalendar/timegrid/main.css'

/* 定义默认视图 */
export const CalendarMixin = {
  data () {
    return {
      calendarPlugins: [
        dayGridPlugin,
        interactionPlugin,
        timeGridPlugin
      ],
      locale: 'zh-cn',
      // 按钮文字
      buttonText: {
        today: '今天',
        day: '日',
        month: '月',
        week: '周'
      },
      // 头部排列方式
      header: {
        left: 'prev,next,today',
        center: 'title',
        right: 'dayGridMonth,timeGridWeek,timeGridDay'
      },
      currentView: 'dayGridMonth',
      // 设置每周开始日期为周日
      firstDay: 1,
      // agenda视图下是否显示all-day
      allDaySlot: true,
      // agenda视图下all-day的显示文本
      allDayText: '全天',
      // 时区默认本地的
      timezone: 'local',
      // 周视图和日视同的左侧时间显示
      slotLabelFormat: 'HH:mm',
      // 设置第二天阈值
      nextDayThreshold: '00:00:00',
      fixedWeekCount: false,
      showNonCurrentDates: true,
      eventLimit: 2,
      eventLimitClick: 'day',
      eventLimitText: 'More',
      calendarEventsNew: [],
      eventDataSource: [],
      // k=id, v=record
      eventMaps: {}
    }
  },
  created () {
    this.loadData()
  },
  methods: {
    moment,
    loadData () {
      // 根据点击的人来查看
      getAction('/eoa/plan/queryMyCreation').then(res => {
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
    queryPlanByUser (username) {
      // 根据点击的人来查看
      getAction('/eoa/plan/queryPlanByUser', { username: username }).then(res => {
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
    renderCalendar (currentView) {
      const dateFormat = 'YYYY-MM-DD'
      const timeFormat = 'HH:mm:ss'
      const datetimeFormat = dateFormat + ' ' + timeFormat

      this.calendarEventsNew = this.eventDataSource.map(item => {
        let { id, title, startTime: start, endTime: end, allDay, urgentLevel: level, status,planColor } = item
        let event = {
          id, title,
          start: new Date(start).getTime(),
          end: new Date(end).getTime(),
          allDay: allDay === '1',
          backgroundColor: planColor,
          borderColor: planColor
        }

        // 解决月视图下跨天少显示一天的问题
        if (event.allDay) {
          event.end = moment(event.end).format(dateFormat) + ' 24:00:00'
        }
        // 周视图下
        if (this.currentView === 'timeGridWeek') {
          // ※ 如不属于全天活动，但跨天，则以全天方式展示，标注开始时间
          if (!event.allDay) {
            // 如果开始时间和结束时间不相等，说明跨天了
            if (moment(event.start).format(dateFormat) !== moment(event.end).format(dateFormat)) {
              event.allDay = true
              event.title = moment(event.start).format(timeFormat).substr(0, 5) + ' ' + event.title
            }
          }
        }
        // 日视图下（else if）
        else if (this.currentView === 'timeGridDay') {
          // ※ 如果开始和结束时间是超过当天，即跨天且铺满当天的情况，则以全天方式展示
          if (!event.allDay) {
            let currentTime = moment(currentView.currentStart).format(dateFormat)

            // 如果结束时间是当天时间，则把当天实际加上 ' 23:59:59'
            if (moment(event.end).format(dateFormat) === currentTime) {
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
    },
    datesRender (info) {
      this.currentView = info.view.type
      this.renderCalendar(info.view)
    },
    handleModalOk () {
      this.loadData()
    },
    /** 点击事件时触发 */
    handleEventSelected (info) {
      let { id } = info.event
      this.$refs.baseModal.edit(this.eventMaps[id])
    },
    select (info) {
      this.$refs.baseModal.addEvent(moment(info.start.getTime()), moment(info.end.getTime()), info.view, info.allDay)
    }
  }

}
