<template>

    <iframe  :id="id" :src="url" frameborder="0" width="100%" height="800px" scrolling="auto"></iframe>

</template>

<script>
  import Vue from 'vue'
  import { ACCESS_TOKEN,ENCRYPTED_STRING } from "@/store/mutation-types"
  import { encryption , getEncryptedString } from '@/utils/encryption/aesEncrypt'
  import PageLayout from '../page/PageLayout'
  import RouteView from './RouteView'

  export default {
    name: "IframePageContent",
    inject:['closeCurrent'],
    data () {
      return {
        url: "",
        id:"",
        encryptedString:{
          key:"",
          iv:"",
        }
      }
    },
    created () {
      this.goUrl()
    },
    updated () {
    },
    watch: {
      $route(to, from) {
        this.goUrl();
      }
    },
    methods: {
      goUrl () {
        let url = this.$route.meta.url
        let id = this.$route.path
        let name = this.$route.name
        this.id = id
        //url = "http://www.baidu.com"
        let token = Vue.ls.get(ACCESS_TOKEN);
        let menuId =  this.$route.meta.id
        let manageUrl = process.env.VUE_APP_DOMAIN_URL + process.env.VUE_APP_BASE_API
        let time = new Date ().getTime ()
        console.log("------url------"+url)
        let params = [manageUrl, token, menuId, time].join(";")
        this.getEncrypte()
        let paramString = encryption(params,this.encryptedString.key, this.encryptedString.iv)

        if (url.lastIndexOf("?") == -1) {
          url += "?paramString=" + paramString
        } else {
          url += "&paramString=" + paramString
        }

        if (url !== null && url !== undefined) {
          url += "&language=zh_CN";
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
      },
      //获取密码加密规则
      getEncrypte(){
        var encryptedString = Vue.ls.get(ENCRYPTED_STRING);
        if(encryptedString == null){
          getEncryptedString().then((data) => {
            this.encryptedString = data
          });
        }else{
          this.encryptedString = encryptedString;
        }
      },
    }
  }
</script>

<style>
</style>