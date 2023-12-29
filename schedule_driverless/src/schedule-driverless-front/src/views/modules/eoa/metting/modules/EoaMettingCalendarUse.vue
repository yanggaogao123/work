<template>
  <div>
    <!-- 使用情况日历  -->
    <div style=" margin: 0 auto;" >
    <FullCalendar
        ref="baseCalendar"
        resourceLabelText="会议室"
        resourceAreaWidth="20%"
        height="auto"
        :contentHeight="50"
        locale="zh-cn"
        selectable="true"
        selectHelper="true"
        :plugins="calendarPlugins"
        :firstDay="firstDay"
        weekNumberCalculation="ISO"
        :select-allow="handlerSelectAllow"
        :header=header
        :default-view="currentView"
        :buttonText=buttonText
        :schedulerLicenseKey="licenseKey"
        :resources="resources"
        :events="calendarEventsNew"
        @datesRender="datesRender"
        @eventClick="handleEventClick"
        @select="select"
        @eventMouseEnter="eventMouseEnter"
        @eventMouseLeave="eventMouseLeave"
      ></FullCalendar>
      <a-empty v-if="resources.length==0" style="margin-top: 10px"/>
    </div>
    <!--回显气泡-->
    <div v-if='showBox'>
      <div class='show-parent' :style="{left: tranLeft, top: tranTop}">
        <a-tooltip v-model="visible" placement="left">
          <template slot="title">
           {{item.title}}<br>
           {{item.start | dayjs('YYYY-MM-DD HH:mm:ss')}}<br>
           {{item.end | dayjs('YYYY-MM-DD HH:mm:ss')}}
          </template>
        </a-tooltip>
      </div>
    </div>
    <eoa-metting-modal ref="mettingModal"></eoa-metting-modal>
  </div>
</template>

<script>
  import FullCalendar from '@fullcalendar/vue';
  import '@fullcalendar/core/main.css';
  import '@fullcalendar/timeline/main.css'
  import '@fullcalendar/resource-timeline/main.css'
  import moment from 'moment';
  import { CalendarMixin} from '@/views/modules/eoa/metting/mixins/CalendarMixin'
  import EoaMettingModal from '@/views/modules/eoa/metting/modules/EoaMettingModal'
  export default {
    name: "EoaMettingCalendarUser",
    mixins: [CalendarMixin],
    components: {
      EoaMettingModal,
      FullCalendar
    },
    props:{
      adleEdit: {
        type: Boolean,
        default: true,
        required: false
      }
    },
    data () {
      return {
        visible: true,
        showBox: false,
        calendarFlag: true,
				tranLeft: 0,
        tranTop: 0,
        eventMouseEnter: info => this.eventEnter(info),
        eventMouseLeave: info => this.eventLeave(info),
      }
    },
    created() {
    },
    methods: {

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
  @import '~@/assets/less/TableExpand.less';
  .show-parent {
    position: absolute;
    width: 300px;
    background-color: #ffffff;
    box-shadow: 0 0 2px #8c939d;
    z-index: 1;
  }
  /deep/  .fc-button-primary {
    background-color: #1890FF !important;
    border-color: #1890FF !important;
  }
</style>