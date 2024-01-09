<template>
  <j-modal
    :width="1000"
    :visible="visible"
    @cancel="handleCancel"
    :footer="null"
    cancelText="关闭">
    <a-card :bordered="false">
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
          @eventMouseEnter="eventMouseEnter"
          @eventMouseLeave="eventMouseLeave"
        ></FullCalendar>
        <a-empty v-if="resources.length==0" style="margin-top: 10px"/>
      </div>
      <a-table
        ref="table"
        size="middle"
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        />
      <!--回显气泡-->
      <div v-if='showBox'>
        <div :style="{position:'relative',left: tranLeft, top: tranTop}">
          <a-tooltip v-model="popvisible" placement="left">
            <template slot="title">
              {{item.title}}<br>
              {{item.start | dayjs()}}<br>
              {{item.end | dayjs()}}
            </template>
          </a-tooltip>
        </div>
      </div>
    </a-card>
  </j-modal>
</template>

<script>
  import moment from 'moment';
  import { getAction } from '@/api/manage'
  import FullCalendar from '@fullcalendar/vue';
  import '@fullcalendar/core/main.css';
  import '@fullcalendar/timeline/main.css'
  import '@fullcalendar/resource-timeline/main.css'
  import { CalendarMixin} from '@/views/modules/eoa/metting/mixins/CalendarMixin'

  export default {
    name: "EoaMettingRoomUseModal",
    mixins: [CalendarMixin],
    components: {
      FullCalendar
    },
    data () {
      return {
        columns: [
          {
            title: '会议名称',
            align:"center",
            dataIndex: 'name'
          }, {
            title: '召集人',
            align:"center",
            dataIndex: 'originator_dictText'
          },{
            title: '位置',
            align:"center",
            dataIndex: 'location_dictText'
          }, {
            title: '开始时间',
            align:"center",
            dataIndex: 'beginTime'
          }, {
            title: '结束时间',
            align:"center",
            dataIndex: 'endTime'
          }, {
            title: '状态',
            align:"center",
            dataIndex: 'status_dictText'
          }
        ],
        loading:false,
        dataSource: [],
        /* 分页参数 */
        ipagination:{
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + "-" + range[1] + " 共" + total + "条"
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        /* table选中keys*/
        selectedRowKeys: [],
        /* table选中records*/
        selectionRows: [],
        visible: false,
        resourceIsNull: true,
        currentView:"resourceTimelineDay",
				tranLeft: 0,
        tranTop: 0,
        marginTop: 0,
        marginLeft: 0,
        popvisible: true,
        showBox: false,
        calendarFlag: true,
        eventMouseEnter: info => this.eventEnter(info),
        eventMouseLeave: info => this.eventLeave(info),
    }
  },
    created() {

    },
    methods: {
      moment,
      show(){
        this.loadRoom();
        this.loadAllMetting();
        this.visible=true;
      },
      loadAllMetting(){
        var params ={};//查询条件
        params.pageNo = this.ipagination.current;
        params.pageSize = this.ipagination.pageSize;
        this.loading = true;
        getAction("/metting/eoaMetting/list", params).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records;
            this.ipagination.total = res.result.total;
          }
          if(res.code===510){
            this.$message.warning(res.message)
          }
          this.loading = false;
        })
      },
      eventEnter(info){
        this.tranLeft = (info.jsEvent.pageX-390) + 'px';
        if(this.currentView=='resourceTimelineWeek'){
          this.tranTop = (info.jsEvent.pageY-650) + 'px';
        }else{
          this.tranTop = (info.jsEvent.pageY-605) + 'px';
        }
        this.$nextTick(() => {
          this.item=info.event
        })
        // 点击的时候显示模态框
        this.showBox = true;
      },
      eventLeave(info){
        console.log("当前页面：：eventMouseLeave==info",info)
        this.showBox =false
      },
      handleCancel () {
        this.close()
      },
      close () {
        this.visible = false;
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedRowKeys = selectedRowKeys;
        this.selectionRows = selectionRows;
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
  @import '~@/assets/less/TableExpand.less';
  /deep/  .fc-button-primary {
    background-color: #1890FF !important;
    border-color: #1890FF !important;
  }
</style>