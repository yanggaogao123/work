<template>
  <a-modal
    :title="currTitle"
    :width="450"
    :visible="visible"
    :closable="false"
    :maskClosable="closable">
    <template slot="footer">
      <a-button v-if="closable" @click="close">关闭</a-button>
      <a-button type="primary" @click="orgOk">确认</a-button>
    </template>

    <a-form>
      <a-form-item
        :labelCol="{span:4}"
        :wrapperCol="{span:20}"
        style="margin-bottom:10px"
        :validate-status="validate_status">
        <a-tooltip placement="topLeft" >
          <template slot="title">
            <span>请选择当前登录平台</span>
          </template>
          <a-avatar style="backgroundColor:#87d068" icon="gold" />
        </a-tooltip>
        <a-select v-model="platformSelected" :class="{'valid-error':validate_status=='error'}" placeholder="请选择登录平台" style="margin-left:10px;width: 80%">
          <a-icon slot="suffixIcon" type="gold" />
          <a-select-option
            v-for="d in platformList"
            :key="d.id"
            :value="d.platformCode">
            {{ d.platformName }}
          </a-select-option>
        </a-select>
      </a-form-item>
    </a-form>

  </a-modal>

</template>

<script>
  import { getAction,putAction } from '@/api/manage'
  import Vue from 'vue'
  import store from '@/store/'
  import { USER_INFO,USER_CUR_PLATFORMS} from "@/store/mutation-types"
  import {USER_PLATFORMS} from "../../store/mutation-types";
  export default {
    name: 'PlatformSelect',
    props:{
      title:{
        type:String,
        default:"平台选择",
        required:false
      },
      closable:{
        type:Boolean,
        default:false,
        required:false
      },
      username:{
        type:String,
        default:"",
        required:false
      }
    },
    watch:{
      username(val){
        if(val){
          this.loadPlatformList()
        }
      }
    },
    data(){
      return {
        currTitle:this.title,
        visible:false,
        platformList:[],
        orgSelected:"",
        platformSelected:"",
        validate_status:"",
        currOrgName:"",
        currPlatformName:"",
      }
    },
    created(){
      // this.loadPlatformList()
    },
    methods:{
      loadPlatformList(){
        return new Promise(resolve => {
          let url = "/sys/platform/getCurrentUserPlatform"
          this.currOrgName=''
          getAction(url).then(res=>{
            if(res.success){
              let orgs = res.result.list
              let platformCode = res.result.platformCode
              let vCurPlatforms = Vue.ls.get(USER_CUR_PLATFORMS)
              if (vCurPlatforms) {
                platformCode = vCurPlatforms
              }
              if(orgs && orgs.length>0){
                for(let i of orgs){
                  if(i.platformCode == platformCode){
                    this.currPlatformName = i.platformName
                    break
                  }
                }
              }
              this.platformSelected = platformCode
              this.platformList = orgs
              Vue.ls.set(USER_PLATFORMS, this.platformList, 7 * 24 * 60 * 60 * 1000);
              store.commit('SET_PLATFORM', USER_PLATFORMS);
              if(this.currPlatformName){
                this.currTitle ="平台切换（当前平台 : "+this.currPlatformName+"）"
              }
            }
            resolve()
          })
        })
        },
      close(){
        console.log('平台关闭')
        this.orgClear()
      },
      orgOk(){
        if(!this.platformSelected){
          this.validate_status='error'
          return false
        }
        let obj = {
          platformCode:this.platformSelected,
          username:this.username === "" ? this.platforms = this.$store.getters.username:this.username
        }
        Vue.ls.set(USER_CUR_PLATFORMS, this.platformSelected, 7 * 24 * 60 * 60 * 1000)
        putAction("/sys/selectPlatform",obj).then(res=>{
          if(res.success){
            const userInfo = res.result.userInfo;
            Vue.ls.set(USER_INFO, userInfo, 7 * 24 * 60 * 60 * 1000);
            store.commit('SET_INFO', userInfo);
            //console.log("---切换组织机构---userInfo-------",store.getters.userInfo.orgCode);
            //this.orgClear()
            //location.reload()
            //CAS平台跳转
            this.hrefPlatfrom()
            console.log('切换平台',userInfo)
          }
        })
      },
      show(){
        //如果组件传值username此处就不用loadPlatformList了
        this.loadPlatformList().then(()=>{
          this.visible=true
        })
      },
      hrefPlatfrom(){
        let hrefUrl = '/';
        for (let platform of this.platformList) {
           if (platform.platformCode == this.platformSelected) {
             hrefUrl = platform.platformUrl;
           }
        }
        window.location.href = hrefUrl;
      },
      orgClear(){
        this.platformList=[]
        this.orgSelected=""
        this.visible=false
        this.validate_status=''
        this.currPlatformName=""
      },
    }

  }
</script>
<style scoped>
  .valid-error .ant-select-selection__placeholder{
    color: #f5222d;
  }
</style>