<template>
  <div class="page-header-index-wide">
    <!--轮播图-->
    <template>
      <a-carousel autoplay>
        <template v-for="(item, index) in carouselImg" v-if="carouselImg.length>0">
          <img :src="getAvatarView(item)" :key="index" style="height: 240px;width:100%"/>
        </template>
        <template v-else>
          <img src="@assets/nodata.png" :key="index" style="height: 240px;width:100%"/>
        </template>
      </a-carousel>
    </template>
    <!--新闻动态、系统公告、我的计划-->
    <a-row :gutter="24">
      <a-col :span="16" :style="{ paddingRight:'-12px' }">
        <a-row :gutter="24">
          <a-col :span="24">
            <a-card :loading="loading" :bordered="false" :style="{ marginTop: '12px',height:'670px' }">
              <span slot="title"> <a-icon type="tags" style="color:#3393FC;margin-right: 5px" theme="filled"/>新闻动态</span>
              <a slot="extra" @click="loadMore('A01')">更多</a>
              <!--展示新闻动态信息-->
              <template v-if="newsInfo.length>0">
                <a-list itemLayout="horizontal" :dataSource="newsInfo" class="tableList">
                  <a-list-item @click="showDetail(item)" slot="renderItem" :key="index" slot-scope="item, index" style="margin:10px 0px" >
                    <a-list-item-meta :description="item.content | contentFilter">
                      <a slot="title" href="#">{{item.title}}</a>
                      <img slot="avatar" :src="getAvatarView(item.imageHref)" style="width:220px;height:110px;"/>
                    </a-list-item-meta>
                    <template slot="actions">
                      <span @click.stop="eyeClick(item)">
                        <a-icon type="eye"  style="margin-right: 5px" />{{item.readTimes==0?'':item.readTimes}}
                      </span>
                    </template>
                  </a-list-item>
                </a-list>
              </template>
              <template v-else>
                <a-empty />
              </template>
            </a-card>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12" :style="{ paddingRight:'-12px' }">
            <a-tabs defaultActiveKey="1" :style="{ marginTop: '12px',height:'380px',background:'#fff' }" @change="ruleTabChange">
              <a slot="tabBarExtraContent" @click="loadMore(key)" style="margin-right:10px">更多</a>
              <a-tab-pane key="1">
                <span slot="tab"><a-icon type="tags" style="margin-right:5px" theme="filled"/>规章制度</span>
                <!--展示规章制度信息-->
                <template v-if="ruleInfo.length>0">
                  <a-list itemLayout="horizontal" :dataSource="ruleInfo">
                    <a-list-item @click="showDetail(item)" slot="renderItem" :key="index" slot-scope="item, index" style="margin:0 10px">
                      <div @click.stop="eyeClick(item)"><a-icon type="eye"  style="margin-right: 5px" />{{item.readTimes==0?'':item.readTimes}}</div>
                      <a href="#" style="margin-left: 5px;color:#000000">
                        <j-ellipsis :value="item.title" :length="15"/>
                      </a>
                      <div class="ant-list-item-content">{{item.createTime}}</div>
                    </a-list-item>
                  </a-list>
                </template>
                <template v-else>
                  <a-empty />
                </template>
              </a-tab-pane>
              <a-tab-pane key="2">
                <span slot="tab"><a-icon type="download" style="margin-right:5px"></a-icon> 常用下载</span>
                <a-list itemLayout="horizontal" :dataSource="downloadInfo" >
                  <a-list-item slot="renderItem" :key="index" slot-scope="item, index" style="margin:0 20px" @click="recordDownload(item)">
                    <div @click.stop="eyeClick(item)"><a-icon type="eye"  style="margin-right: 5px" />{{item.readTimes==0?'':item.readTimes}}</div>
                    <a style="margin-left: 5px;color:#000000" :href="getFileDownloadUrl(item.fileUrl)" target="_blank">
                      <j-ellipsis :value="item.title" :length="15"/>
                    </a>
                    <div class="ant-list-item-content">{{item.createTime}}</div>
                  </a-list-item>
                </a-list>
              </a-tab-pane>
            </a-tabs>
          </a-col>
          <a-col :span="12">
            <a-tabs defaultActiveKey="1" :style="{ marginTop: '12px',height:'380px',background:'#fff' }">
              <a slot="tabBarExtraContent" @click="loadMore('A03')" style="margin-right:10px">更多</a>
              <a-tab-pane key="1">
                <span slot="tab"><a-icon type="tags" style="margin-right:5px" theme="filled"/>签约新闻</span>
                <!--展示规章制度信息-->
                <template v-if="signNews.length>0">
                  <a-list itemLayout="horizontal" :dataSource="signNews">
                    <a-list-item @click="showDetail(item)" slot="renderItem" :key="index" slot-scope="item, index" style="margin:0 10px">
                      <div @click.stop="eyeClick(item)"><a-icon type="eye"  style="margin-right: 5px" />{{item.readTimes==0?'':item.readTimes}}</div>
                      <a href="#" style="margin-left: 5px;color:#000000">
                        <j-ellipsis :value="item.title" :length="15"/>
                      </a>
                      <div class="ant-list-item-content">{{item.createTime}}</div>
                    </a-list-item>
                  </a-list>
                </template>
                <template v-else>
                  <a-empty />
                </template>
              </a-tab-pane>
            </a-tabs>
          </a-col>
        </a-row>
      </a-col>
      <a-col :span="8">
        <a-row>
          <a-card :loading="loading" :bordered="false"  :style="{ marginTop: '12px',height:'335px' }">
            <span slot="title"> <a-icon type="tags" style="color:#3393FC;margin-right:5px" theme="filled"/>系统公告</span>
            <a slot="extra" @click="loadMore('notice')">更多</a>
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
        </a-row>
        <a-row>
          <a-card :loading="loading" :bordered="false" :style="{ marginTop: '12px',height:'513px'}">
            <span slot="title"> <a-icon type="tags" style="color:#3393FC;margin-right:5px" theme="filled"/>我的计划</span>
            <a slot="extra" @click="loadMore('plan')">更多</a>
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
        </a-row>
        <a-row >
          <a-col>
          <a-card :loading="loading" :bordered="false" :style="{ marginTop: '12px',height:'190px' }">
              <span slot="title"> <a-icon type="tags" style="color:#3393FC;margin-right:5px" theme="filled"/>友情链接</span>
              <a-row type="flex" justify="space-around" align="middle">
                <a-col :span="4"> <a-button size="large" @click="toWWW('http://www.jeecg.com')">官网</a-button></a-col>
                <a-col :span="4"> <a-button size="large" @click="toWWW('http://bbs.jeecg.com')">BBS</a-button></a-col>
                <a-col :span="4"> <a-button size="large" @click="toWWW('http://doc.jeecg.com')">文档</a-button></a-col>
              </a-row>
              <a-row type="flex" justify="space-around" align="middle" :style="{ marginTop: '10px'}">
                <a-col :span="4"> <a-button size="large">系统1</a-button></a-col>
                <a-col :span="4"> <a-button size="large">系统2</a-button></a-col>
                <a-col :span="4"> <a-button size="large">系统3</a-button></a-col>
              </a-row>
            </a-card>
          </a-col>
        </a-row>
      </a-col>
    </a-row>
    <!-- 弹窗区域 -->
    <eoa-plan-modal ref="baseModal" @ok="handleModalOk"/>
    <show-announcement ref="ShowAnnouncement" @ok="modalFormOk"></show-announcement>
    <dynamic-notice ref="showDynamNotice" :path="openPath" :formData="formData"/>
    <eoa-cms-article-reader-modal ref="readerModal"></eoa-cms-article-reader-modal>
  </div>

</template>

<script>

import ACol from 'ant-design-vue/es/grid/Col'
import store from '@/store/'
import { getFileAccessHttpUrl, putAction, getAction } from '@/api/manage'
import infiniteScroll from 'vue-infinite-scroll'
import { getSiteInfo, getUserNoticeInfo, getPlanInfo } from '@/api/api'
import EoaPlanModal from '@/views/modules/eoa/plan/modules/EoaPlanModal'
import JEllipsis from '@/components/jeecg/JEllipsis'
import ShowAnnouncement from '@/components/tools/ShowAnnouncement'
import DynamicNotice from '@/components/tools/DynamicNotice'
import EoaCmsArticleReaderModal from './modules/EoaCmsArticleReaderModal'
import { CalendarMixin } from '@/views/modules/eoa/plan/utils/CalendarMixin'
import FullCalendar from '@fullcalendar/vue'
export default {
  name: 'EoaCmsEnterprisePortal',
  directives: { infiniteScroll },
  mixins: [CalendarMixin],
  components: {
    EoaCmsArticleReaderModal,
    ACol,
    EoaPlanModal,
    JEllipsis,
    ShowAnnouncement,
    DynamicNotice,
    FullCalendar
  },
  data () {
    return {
      loading: true,
      hovered: false,
      key: 'A02',
      siteInfo: {},
      userNotice: [],
      carouselImg: [],
      planData: [],
      signNews: [],
      newsInfo: [],
      ruleInfo: [],
      downloadInfo: [],
      selectDay: {},
      formData: {},
      openPath: '',
      header: {
        left: 'prev,next',
        center: 'title',
        right: 'dayGridMonth,timeGridDay'
      },
    }
  },
  filters: {
    getTime (time) {
      if (time) {
        var d = new Date(time)
        var h = d.getHours()
        var m = d.getMinutes()
        if (h < 10) {
          h = '0' + h
        }
        if (m < 10) {
          m = '0' + m
        }
        return h + ':' + m
      }
    },
    contentFilter (content ){
      // let reg1=/<().*?>/g;
      let reg2 =/<\/?.+?\/?>/g;
      content = content.replace(reg2, '').replace(new RegExp("&nbsp;","gm"), '').replace(new RegExp("&darr;","gm"), '')
      if(content.length >100){
        content= content.slice(0,100)+"......"
      }
      return content;
    }
  },
  created(){
    this.initSiteInfo();
    setTimeout(() => {
      this.loading = !this.loading
    }, 1000)
  },
  methods: {
     initSiteInfo () {
       var _this=this;
       getSiteInfo().then((res)=>{
        if(res.success){
          _this.siteInfo=res.result;
          _this.signNews=res.result.SIGN_NEWS;
          _this.newsInfo=res.result.NEWS_INFO;
          _this.ruleInfo=res.result.Rule;
          _this.downloadInfo=res.result.DOWNLOAD;
          console.log("siteInfo",_this.siteInfo)
          console.log("downloadInfo",_this.downloadInfo)
          if(res.result.site[0].carouselImg){
            _this.carouselImg=res.result.site[0].carouselImg.split(",");
          }
          console.log("carouselImg",_this.carouselImg)
        }
      })
       getUserNoticeInfo({pageNo:1,pageSize:5}).then((res)=>{
        if(res.success){
          _this.userNotice=res.result.records;
          console.log("this.userNotice::",this.userNotice)
        }
      })
    /*   getPlanInfo({date:new Date().getTime().toString()}).then((res)=>{
        if(res.success){
          console.log("this.getPlanInfo::",res.result)
          _this.planData=res.result;
        }
      })*/
    },
    loadMore(type){
       if(type=='notice'){
         this.$router.push({ path: '/isps/userAnnouncement'})
       }else if(type=='plan'){
         this.$router.push({ path: '/eoa/planList'})
       }else{
         this.$router.push({ path: '/modules/eoa/cms/eoaCmsArticlePortal',query:{type:type}})
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
    handleModalOk() {
      this.initSiteInfo()
    },
    showDetail(item){
      console.log("showDetail==>item", item);
      this.$router.push({ path: '/modules/eoa/cms/modules/eoaCmsArticleDetailModal',query : { item:item }})
    },
    getAvatarView(url){
      return getFileAccessHttpUrl(url)
    },
    getFileDownloadUrl: function (path) {
      return getFileAccessHttpUrl(path)
    },
    recordDownload(item){
      if(item.id && store.getters.userInfo.username){
        getAction("/cms/eoaCmsArticleRead/recordReader",{articleId:item.id,username:store.getters.userInfo.username}).then((res) =>{
          if(res.success){
            console.log(res);
          }
        });
      }else{
        if(!this.item.id){
          this.$message.warning("文章信息异常");
        }else{
          this.$message.warning("登录信息异常");
        }
      }
    },
    modalFormOk(){
       this.initSiteInfo();
    },
    toWWW(url){
      window.open(url,'top');
    },
    eyeClick(item){
         this.$refs.readerModal.loadReader(item);
    },
    ruleTabChange(key){
       if(key=='1'){
         this.key='A02';
       }else{
         this.key='A04';
       }

    },
    showAnnouncement(record){
       console.log("showAnnouncement",record);
      putAction('/sys/sysAnnouncementSend/editByAnntIdAndUserId',{anntId:record.anntId}).then((res)=> {
          if (res.success) {
            getUserNoticeInfo({ pageNo: 1, pageSize: 5 }).then((res) => {
              if (res.success) {
                this.userNotice = res.result.records;
                console.log("this.userNotice::", this.userNotice)
              }
            })
          }
      });
      if(record.openType==='component'){
        this.openPath = record.openPage;
        this.formData = {id:record.busId};
        this.$refs.showDynamNotice.detail(record.openPage);
      }else{
        this.$refs.ShowAnnouncement.detail(record);
      }
    },
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

  .tableList {
     /deep/ .ant-list-item-action {
        margin-top: auto !important;
      }
  }

  /deep/ .fc-center{
    font-size: x-small;
  }
  /deep/  .fc-button-primary {
    background-color: #1890FF !important;
    border-color: #1890FF !important;
  }
</style>