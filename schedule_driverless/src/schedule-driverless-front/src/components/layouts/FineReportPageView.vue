<template>
<div>
  <iframe :id="id" :src="id" frameborder="0" width="100%" height="800px" scrolling="auto" ></iframe>
</div>
</template>

<script>
  import Vue from 'vue'
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import PageLayout from '../page/PageLayout'
  import {getAction,postAction} from '@/api/manage'

  export default {
    name: "FinReportPageContent",
    components: { },
    inject:['closeCurrent'],
    data () {
      return {
        url: "",
        id:"",
        fineReportUrl:"fineReport/redirect"
      }
    },
    created () {
      this.goUrl()
    },
    updated () {
      // this.goUrl()
    },
    watch: {
      $route(to, from) {
        this.goUrl();
      }
    },
    computed: {
      keepAlive () {
        return this.$route.meta.keepAlive
      }
    },
    methods: {
      goUrl () {
        let url = this.$route.meta.url
        let id = this.$route.meta.sourcePath
        let name = this.$route.name
        console.log('id',id)
        this.id = id
        //url = "http://www.baidu.com"
        console.log("------url------"+url)
        if (url !== null && url !== undefined) {
          this.url = url;
          /*update_begin author:wuxianquan date:20190908 for:判断打开方式，新窗口打开时this.$route.meta.internalOrExternal==true */
          if(this.$route.meta.internalOrExternal != undefined && this.$route.meta.internalOrExternal==true){
            this.closeCurrent();
            //外部url加入token
            let tokenStr = "${token}";
            if(url.indexOf(tokenStr)!=-1){
              let token = Vue.ls.get(ACCESS_TOKEN);
               this.url = url.replace(tokenStr,token);
            }
            window.open(this.url);
          }
          /*update_end author:wuxianquan date:20190908 for:判断打开方式，新窗口打开时this.$route.meta.internalOrExternal==true */

        }
        let reportUrl = ""
        let params = {}
        if (this.id.indexOf("&") != -1){
          reportUrl = this.id.substring(0,this.id.indexOf("&"))
          let ps = this.id.substring(this.id.indexOf("&")+1,this.id.length).split("&")
          let temp = []
          ps.forEach(item =>{
            let t = item.split("=")
            temp.push(t)
          })
          params = Object.fromEntries(temp)
          //params = this.id.substring(this.id.indexOf("&"))
        } else {
          reportUrl = this.id;
        }

        let postBody = {url:reportUrl,params:params}
        postAction(this.fineReportUrl, postBody).then((res) => {
          if (res.success) {
            this.id = res.message
          } else {
          }
          if (this.id !== null && this.id !== undefined) {
            /*update_begin author:wuxianquan date:20190908 for:判断打开方式，新窗口打开时this.$route.meta.internalOrExternal==true */
            if(this.$route.meta.internalOrExternal != undefined && this.$route.meta.internalOrExternal==true){
              this.closeCurrent();
              window.open(this.id);
            }
            /*update_end author:wuxianquan date:20190908 for:判断打开方式，新窗口打开时this.$route.meta.internalOrExternal==true */
          }
        }).finally(() => {
        });
      }
    }
  }
</script>

<style>
</style>