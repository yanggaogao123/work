<template>
  <div class="page-header-index-wide">
    <!--头部导航-->
    <a-row :gutter="24">
      <a-col>
        <a-card :style="{height:'200px'}">
          <a style="float:right" @click="loadMore('/extbpm/process/BpmAutoDesformApply')">更多</a>
          <!--展示头部导航信息-->
          <div style="margin-top: 40px" >
            <template v-for="(des,index) of commUseList">
              <a-col :span="4" :key="index">
                <span class="headicon">
                  <a-button type="primary" @click="handleOk(des)" shape="circle" :icon="des.desformIcon" size="large" :style="{background:colorList[index],border:0}"/><span style="margin-top: 15px"> {{des.desformName.length>4?des.desformName.substr(0,4):des.desformName}}</span>
                </span>
              </a-col>
            </template>
          </div>
        </a-card>
      </a-col>
    </a-row>
    <!--流程中心、流程提醒-->
    <a-row :gutter="24">
      <a-col :span="16" :style="{ paddingRight:'-12px' }">
        <a-card :loading="loading" :bordered="false" :style="{ marginTop: '12px',height:'450px' }">
          <span slot="title"> <a-icon type="tags" style="color:#3393FC;margin-right: 5px" theme="filled"/>流程中心</span>
          <a slot="extra" @click="loadMore('/bpm/task/MyTaskList')">更多</a>
          <template>
            <a-tabs  @change="callback">
              <a-tab-pane tab="待办" key="1">
                <my-running-task-list ref="runTaskList" @ok="runOk"></my-running-task-list>
              </a-tab-pane>
              <a-tab-pane tab="我的抄送" key="2">
                <my-cc-his-process-list ref="ccProcessList"></my-cc-his-process-list>
              </a-tab-pane>
            </a-tabs>
          </template>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card :loading="loading" :bordered="false" :style="{ marginTop: '12px',height:'450px' }">
          <span slot="title"> <a-icon type="tags" style="color:#3393FC;margin-right: 5px" theme="filled"/>流程提醒</span>
          <a slot="extra" @click="loadMore('/isps/userAnnouncement')">更多</a>
          <template v-if="processNotice.length>0">
            <a-list itemLayout="horizontal" :dataSource="processNotice">
              <a-list-item @click="showAnnouncement(item)" slot="renderItem" :key="index" slot-scope="item, index">
                <a-list-item-meta>
                  <a slot="title" href="#" :style="{color:item.readFlag=='0'?'':'#d2d2d2'}">{{item.titile}}</a>
                </a-list-item-meta>
                <div>{{item.sendTime}}</div>
              </a-list-item>
            </a-list>
          </template>
          <template v-else>
            <a-empty />
          </template>
        </a-card>
      </a-col>
    </a-row>
    <!--我的申请、系统公告-->
    <a-row :gutter="24">
      <a-col :span="16" :style="{ paddingRight:'-12px' }">
        <a-card :loading="loading" :bordered="false" :style="{ marginTop: '12px',height:'450px' }">
          <span slot="title"> <a-icon type="tags" style="color:#3393FC;margin-right: 5px" theme="filled"/>我的申请</span>
          <a slot="extra" @click="loadMore('/bpm/task/MyApplyProcessList')">更多</a>
          <template>
            <my-apply-process-list ref="applyProcessList" @ok="processOk"></my-apply-process-list>
          </template>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card :loading="loading" :bordered="false" :style="{ marginTop: '12px',height:'450px' }">
          <span slot="title"> <a-icon type="tags" style="color:#3393FC;margin-right: 5px" theme="filled"/>系统公告</span>
          <a slot="extra" @click="loadMore('/isps/userAnnouncement')">更多</a>
          <template v-if="userNotice.length>0">
            <a-list itemLayout="horizontal" :dataSource="userNotice">
              <a-list-item @click="showAnnouncement(item)" slot="renderItem" :key="index" slot-scope="item, index">
                <a-list-item-meta>
                  <a slot="title" href="#" :style="{color:item.readFlag=='0'?'':'#d2d2d2'}">{{item.titile}}</a>
                </a-list-item-meta>
                <div>{{item.sendTime}}</div>
              </a-list-item>
            </a-list>
          </template>
          <template v-else>
            <a-empty />
          </template>
        </a-card>
      </a-col>
    </a-row>
    <!--流程数据分析、流程类型统计-->
    <a-row :gutter="24">
      <a-col :span="16" :style="{ paddingRight:'-12px' }">
        <a-card :loading="loading" :bordered="false" :style="{ marginTop: '12px',height:'320px' }">
          <span slot="title"> <a-icon type="tags" style="color:#3393FC;margin-right: 5px" theme="filled"/>流程数据分析</span>
          <a slot="extra" @click="loadMore">更多</a>
          <stack-bar :dataSource="chartData"></stack-bar>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card :loading="loading" :bordered="false" :style="{ marginTop: '12px',height:'320px' }">
          <span slot="title"> <a-icon type="tags" style="color:#3393FC;margin-right: 5px" theme="filled"/>流程类型统计</span>
          <a slot="extra" @click="loadMore">更多</a>
          <pie class="statistic" title="流程统计" :dataSource="pieDate" :height="240"/>
        </a-card>
      </a-col>
    </a-row>
    <!--近期邮件、我的计划-->
    <a-row :gutter="24">
      <a-col :span="16" :style="{ paddingRight:'-12px' }">
        <a-card :loading="loading" :bordered="false" :style="{ marginTop: '12px',height:'530px' }">
          <span slot="title"> <a-icon type="tags" style="color:#3393FC;margin-right: 5px" theme="filled"/>近期邮件</span>
          <a slot="extra" @click="loadMore('/modules/eoa/email/EoaCmsEmailList')">更多</a>
          <template>
            <a-table
              ref="table"
              rowKey="id"
              :columns="columns"
              :dataSource="emailData"
              :pagination="false"
              :loading="emailLoading"
              :customRow="clickThenCheck">
              <span slot="emailTitle" slot-scope="text">
                <j-ellipsis :value="text" :length="12"/>
              </span>
            </a-table>
          </template>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card class="j-modules-plan-box" :loading="cardLoading" :bordered="false" :style="{ marginTop: '12px',height:'530px'}">
          <span slot="title"> <a-icon type="tags" style="color:#3393FC;margin-right: 5px" theme="filled"/>我的计划</span>
          <a slot="extra" @click="loadMore('/eoa/planList')">更多</a>
          <template>
              <FullCalendar
                ref="baseCalendar"
                selectable="true"
                weekNumberCalculation="ISO"
                :locale="locale"
                :firstDay="firstDay"
                :header="header"
                :plugins="calendarPlugins"
                :default-view="currentView"
                :buttonText=buttonText
                :events="calendarEventsNew"
                :fixedWeekCount="fixedWeekCount"
                :showNonCurrentDates="showNonCurrentDates"
                :eventLimit="1"
                :contentHeight="300"
                :allDayText="allDayText"
                @datesRender="datesRender"
                @eventClick="handleEventSelected"
                @select="select"
              ></FullCalendar>
          </template>
        </a-card>
      </a-col>
    </a-row>
    <!-- 弹窗区域 -->
    <eoa-plan-modal ref="baseModal" @ok="handleModalOk"/>
    <show-announcement ref="ShowAnnouncement" @ok="modalFormOk"></show-announcement>
    <eoaEmailIn-modal ref="eoaEmailInModal" @ok="handOk" @reply="handleReply"></eoaEmailIn-modal>
    <dynamic-notice ref="showDynamNotice" :path="openPath" :formData="formData"/>
    <auto-desform-data-modal ref="desformModal" :dialogOptions="dialogOptions"  @added="handleDesformDataAdded"></auto-desform-data-modal>
  </div>

</template>

<script>

import ACol from 'ant-design-vue/es/grid/Col'
import { getFileAccessHttpUrl, getAction, putAction, httpAction } from '@/api/manage'
import infiniteScroll from 'vue-infinite-scroll'
import { getPlanInfo, getUserNoticeInfo, getChartData, getPieData } from '@/api/api'
import EoaPlanModal from '@/views/modules/eoa/plan/modules/EoaPlanModal'
import ShowAnnouncement from '@/components/tools/ShowAnnouncement'
import DynamicNotice from '@/components/tools/DynamicNotice'
import JEllipsis from '@/components/jeecg/JEllipsis'
import Pie from '@/components/chart/Pie'
import StackBar from '@/components/chart/StackBar'
import BarMultid from '@/components/chart/BarMultid'
import MyRunningTaskList from './modules/MyRunningTaskList'
import MyCcHisProcessList from './modules/MyCcHisProcessList'
import MyApplyProcessList from './modules/MyApplyProcessList'
import EoaEmailInModal from '../email/modals/EoaEmailInModal'
import store from '@/store'
import { formatDate } from '@/utils/util'
import moment from 'moment'
import AutoDesformDataModal from '../../extbpm/process/modules/AutoDesformDataModal'
import { CalendarMixin } from '@/views/modules/eoa/plan/utils/CalendarMixin'
import FullCalendar from '@fullcalendar/vue'

const colorList = ['#F14A2D', '#0079DE', '#217346', '#F14A2D', '#6D3CF7', '#0079DE']
export default {
  name: 'EoaCmsProcessPortal',
  directives: { infiniteScroll },
  mixins: [CalendarMixin],
  components: {
    AutoDesformDataModal,
    MyApplyProcessList,
    MyCcHisProcessList,
    ACol,
    EoaPlanModal,
    JEllipsis,
    ShowAnnouncement,
    DynamicNotice,
    Pie,
    StackBar,
    BarMultid,
    MyRunningTaskList,
    EoaEmailInModal,
    FullCalendar
  },
  data () {
    return {
      loading: true,
      cardLoading: true,
      processUpdate: false,
      colorList,
      chartData: [],
      pieDate: [],
      userNotice: [],
      processNotice: [],
      planData: [],
      selectDay: {},
      emailData: [],
      emailLoading: false,
      formData: {},
      openPath: '',
      plainOptions: [],
      tags: [],
      commUseList: [],
      planList: [],
      current: '',
      dialogOptions: { top: 60, width: 1000, padding: { top: 25, right: 25, bottom: 30, left: 25 } },
      flowCodePre: 'desform_',
      header: {
        left: 'prev,next',
        center: 'title',
        right: 'dayGridMonth,timeGridDay'
      },
      columns:[
        {
          title: '标题',
          dataIndex: 'title',
          align:'center',
          scopedSlots: { customRender: 'emailTitle' }
        },
        {
          title: '状态',
          align:'center',
          dataIndex: 'readFlagText'
        },
        {
          title: '发件人',
          align:'center',
          dataIndex: 'senderId_dictText',
        },  {
          title: '日期',
          align:'center',
          dataIndex: 'sendTime'
        }
      ]
     }
  },
  filters: {
    getTime(time) {
      if(time){
        var d=new Date(time)
        var h= d.getHours();
        var m= d.getMinutes();
        if(h<10){
          h="0"+h;
        }
        if(m<10){
          m="0"+m;
        }
        return h+":"+m;
      }
    }
  },
  created() {
    //通告和日程计划
    this.initNoticePlanEmailInfo();
    //图表数据
    this.loadChartData()
    //常用
    this.loadCommUseList()
    setTimeout(() => {
      this.loading = !this.loading
    }, 1000)
  },
  methods: {
    moment,
    loadCommUseList() {
      getAction("/designform/designFormCommuse/getCommuseByUserId",{}).then((res) =>{
        console.log("loadCommUseList==>",res)
        if (res.success) {
          let data=res.result
          if(data.length >6){
            this.commUseList = data.slice(0,6)
          }else{
            this.commUseList =data
          }
        }
      });
    },
    loadChartData(){
      getChartData().then((res)=>{
        if(res.success){
          console.log("getChartData::",res.result)
          this.chartData=res.result;
        }
      })

      getPieData().then((res)=>{
        if(res.success){
           console.log("getPieData::",res.result)
           this.pieDate=res.result;
        }
      })
    },
    async initNoticePlanEmailInfo () {
       var _this=this;
       getPlanInfo().then((res)=>{
        if(res.success){
          let planList=this.getPlanTime(res.result)
          _this.planList=planList;
        }
      })
    /*   getPlanInfo({date:new Date().getTime().toString()}).then((res)=>{
        if(res.success){
          _this.planData=res.result;
        }
      })*/

    getUserNoticeInfo({pageNo:1,pageSize:5,bizSource:'isNoBpm'}).then((res)=>{
       if(res.success){
         _this.userNotice=res.result.records;
       }
     })

    getUserNoticeInfo({pageNo:1,pageSize:5,busType:'bpm'}).then((res)=>{
      console.log("getUserNoticeInfo===busType",res);
      if(res.success){
        _this.processNotice=res.result.records;
      }
    })
      this.getEmailList()
    },
    getEmailList(){
      this.emailLoading=true
      this.emailData=[];
      var param=[];
      param.pageNo =1;
      param.pageSize = 5;
      param.userId=store.getters.userInfo.id;
      param.status="0";
      getAction("/email/eoaMailboxInfo/sendList",param).then((res) =>{
        if(res.success){
          var emailData=res.result.records
          if(emailData){
            for(let i=0;i<emailData.length;i++){
              var readFlag=emailData[i].readFlag;
              var readFlagText="";
              if(readFlag==="0"){
                readFlagText="未读"
              }else{
                readFlagText="已读";
              }
              emailData[i].readFlagText=readFlagText;
              emailData[i].sendTime=emailData[i].sendTime.substring(0,10);
            }
            this.emailData=emailData;
            this.emailLoading=false
          }
        }
      })
    },
    loadMore(path){
      let query={}
      if(path=='/modules/eoa/email/EoaCmsEmailList'){
        query.type="inbox"
      }
      this.$router.push({ path: path,query:query})
    },
    clickThenCheck(record) {
      return {
        on: {
          click: () => {
            this.handClick(record)
          }
        }
      }
    },
    handleInfiniteOnLoad() {
      this.$message.warning('已加载全部');
    },
    onPanelChange(value, mode) {
      console.log("onPanelChange",value, mode);
    },
 /*   onSelect(value) {
      console.log("onSelect",value);
      this.selectDay=value._d;
      this.planData=[];
      getPlanInfo({date:value._d.getTime().toString()}).then((res)=>{
        if(res.success){
          console.log("this.getPlanInfo::",res.result)
          this.planData=res.result;
        }
      })
    },*/
    addPlan(){
      console.log("addPlan", this.selectDay);
      this.$refs.baseModal.edit({startTime:this.selectDay,endTime:this.selectDay,allDay:"1"},"新增日程计划")
    },
    showAnnouncement(record){
      console.log("showAnnouncement",record);
      putAction('/sys/sysAnnouncementSend/editByAnntIdAndUserId',{anntId:record.anntId}).then((res)=> {
        if (res.success) {
          this.initNoticePlanEmailInfo();
        }
      });
      console.log(record.openType);
      if(record.openType==='component'){
        this.openPath = record.openPage;
        this.formData = {id:record.busId};
        this.$refs.showDynamNotice.detail(record.openPage);
      }else{
        this.$refs.ShowAnnouncement.detail(record);
      }
    },
    handClick(data){
      this.$refs.eoaEmailInModal.view(data);

    },
    handleReply(record){
      this.$router.push({ path: '/modules/eoa/email/EoaCmsEmailList',query:{type:'process',rec:record}})
    },
    handOk(){
      console.log("===handOk===");
      this.initNoticePlanEmailInfo();
    },
    modalFormOk(){
      console.log("===modalFormOk===");
      this.initNoticePlanEmailInfo();
    },
    getAvatarView(url){
      return getFileAccessHttpUrl(url)
    },
     processOk(){
       this.$refs.runTaskList.loadData();
       this.processUpdate=true;
    },
     runOk(){
       this.$refs.applyProcessList.loadData();
       this.processUpdate=true;
    },

    /** bmp 选择 ok */
    handleOk(value) {
      if (value) this.current = value
      this.handleOkBpmSelect(this.current);
      this.close()
    },
    close() {

    },
    handleOkBpmSelect(desform) {
      var title = "表单【"+desform.desformName+"】发起申请";
      this.openDesformModal('add', desform, title)
    },
    openDesformModal(mode, record, title) {
      let desform = record, dataId = null
      if (mode === 'edit' || mode === 'detail') {
        let { desformId: id, desformCode, desformDataId } = record
        dataId = desformDataId
        desform = { id, desformCode }
      }
      getAction( '/desform/queryByCode', {
        desformCode: desform.desformCode
      }).then(res => {
        if (res.success) {
          let designJson = res.result.desformDesignJson;
          let json = JSON.parse(designJson)
          // 保存 dialogConfig
          let dialogOptions = json.config.dialogOptions
          if (dialogOptions) {
            this.dialogOptions = dialogOptions
          }
          this.$refs.desformModal.open(mode, desform, dataId, title)
        }

      })

    },
    handleDesformDataAdded(event) {
      // 将流程保存至后台
      let { desform, dataId } = event
      this.loading = true
      httpAction(this.url.add, {
        desformId: desform.id,
        desformCode: desform.desformCode,
        desformDataId: dataId,
        desformName: desform.desformName,
        processName: desform.procName,
        flowCode: this.flowCodePre + desform.desformCode,
        titleExp: desform.titleExp
      }, 'POST').then(res => {
        if (!res.success) {
          this.$message.error(res.message)
          return
        }
        this.$router.push({ path: '/extbpm/process/ExtActDesignFlowDataList'})
      }).finally(() => this.loading = false)
    },
    /** bmp 选择 end */
    getPlanTime(data){
      var res=data.map(function(value,index,data){
           //字符串转Date
        if(value.startTime){
          var arr1 = value.startTime.split(" ");
          var sdate = arr1[0].split('-');
          var date = new Date(sdate[0], sdate[1]-1, sdate[2]);
          return  formatDate(date.getTime(),'yyyy年M月d日');
        }
      })
      return Array.from(new Set(res))
    },
    callback(key) {
      if(key=='2'&&this.processUpdate){
        console.log("callback",key);
        this.$refs.ccProcessList.loadData()
        this.processUpdate=false;
      }
    }
  }
}
</script>

<style lang="less" scoped>
  .ant-list-item-meta-title {
   font-weight: 600;
  }
  .demo-infinite-container {
    border: 1px solid #e8e8e8;
    overflow: auto;
    /*padding: 8px 24px;*/
    overflow: auto;
    height: 95px;
  }
  .demo-loading-container {
    position: absolute;
    bottom: 40px;
    width: 100%;
    text-align: center;
  }
  .timespan{
    background: #F5F5F5;
    width: 100px;
    height: 30px;
    line-height: 30px;
    text-align: center;
  }
  .demo-infinite-container .ant-list-item {
    padding: 0px 0;
  }
  .headicon{
    display:flex;
    flex-direction: column;
    align-items: center;
    border:0
}
  .header1{
    width: 100%;
    text-align: center;
    display: flex;
    background-color: white;
    border-bottom: 1px solid #e7eaec;
    height: 42px;
    border-top: 1px solid #e7eaec;
    margin-top: 10px;
  }
 /* a{
    color: black;
    text-align: center;
  }*/
  .full-calendar{
    margin: 0 30px 0 30px !important;
  }
  /deep/ .fc-center{
    font-size: x-small;
  }
  /deep/  .fc-button-primary {
    background-color: #1890FF !important;
    border-color: #1890FF !important;
  }
</style>