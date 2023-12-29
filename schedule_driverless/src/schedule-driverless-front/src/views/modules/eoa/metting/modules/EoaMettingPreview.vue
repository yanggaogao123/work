<template>
  <a-modal
    width="100%"
    height="110%"
    :visible="true"
    switchFullscreen
    :style="{background:background,top:'-48px'}"
    class="prewiewModal"
    :footer="null"
    :closable="false"
    :keyboard="false"
    :maskClosable="false">
    <div class="container" @click="test">
      <div id="qrcode" ref="qrCodeDiv" style="border: 1px solid #ffffff;"></div>
      <table :style="{fontSize:fontSize,color:fontColor}" :cellspacing="50">
        <thead>
        <tr align="center">
         <th>姓名</th>
         <th>部门</th>
         <th>签到时间</th>
        </tr>
        </thead>
        <tbody>
        <tr align="center">
          <td style="padding:0 20px">{{name}}</td>
          <td style="padding:0 20px">{{depart}}</td>
          <td style="padding:0 20px">{{signTime}}</td>
        </tr>
        </tbody>
      </table>
    </div>

  </a-modal>
</template>

<script>

  import QRCode from 'qrcodejs2'
  import { getAction,postAction } from '@/api/manage'
  import store from '@/store/'

  export default {
    name: "EoaMettingPreview",
    data () {
      return {
        text:"1244532246166118401",
        background: "#006699",
        fontSize:"24px",
        fontColor:"#ffffff",
        name:"",
        depart:"",
        signTime:"",
        websockSign: null,
        lockReconnect:false,
      }
    },
    created () {
        this.background=this.$route.query.bgColor;
      //TODO 会议的id
        let fontSize=this.$route.query.fontSize
        let fontColor=this.$route.query.fontColor
        if(fontSize){
          this.fontSize=this.$route.query.fontSize+"px";
        }
        if(fontColor){
          this.fontColor=this.$route.query.fontColor;
        }
    },
    mounted(){
      this.$nextTick (function () {
         this.qrcode();
         this.initWebSocket();
      })
    },
    methods: {
      test(){
        postAction("/metting/eoaMettingSign/mettingSign", {mettingId:"1244532246166118401",userId:"e9ca23d68d884d4ebb19d07889727dae"}).then((res) => {
          console.log("test===>res",res)
        })
      },
      loadUser(userId){
        var that=this;
        getAction("/metting/eoaMettingSign/mettingInfo", {id:this.text,userid:userId}).then((res) => {
          console.log("loadUser===>res",res)
          if (res.success){
            that.$nextTick(() => {
              that.name=res.result.username;
              that.depart=res.result.userDepart;
              if(res.result.signTime!=null){
                that.signTime=res.result.signTime.split(" ")[1];
              }
            });
          }
        })
      },
      qrcode () {
        document.getElementById("qrcode").innerHTML = "";
        let qrcode=new QRCode(this.$refs.qrCodeDiv, {
                text: this.text,
                width: 300,
                height: 300,
                colorDark: "#000000", //二维码颜色
                colorLight: "#ffffff", //二维码背景色
                correctLevel: QRCode.CorrectLevel.H//容错率，L/M/H
              })
          console.log(qrcode)
      },
      initWebSocket: function () {
        // WebSocket与普通的请求所用协议有所不同，ws等同于http，wss等同于https
        var userId = store.getters.userInfo.id;
        var url = window._CONFIG['domianURL'].replace("https://","wss://").replace("http://","ws://")+"/websocket/"+userId;
        console.log(url);
        this.websockSign = new WebSocket(url);
        this.websockSign.onopen = this.websocketOnopen;
        this.websockSign.onerror = this.websocketOnerror;
        this.websockSign.onmessage = this.websocketOnmessage;
        this.websockSign.onclose = this.websocketOnclose;
      },
      websocketOnopen: function () {
        console.log("WebSocket连接成功");
      },
      websocketOnerror: function (e) {
        console.log("WebSocket连接发生错误",e);
        this.reconnect();
      },
      websocketOnmessage: function (e) {
        var data = eval("(" + e.data + ")"); //解析对象
        if(data.cmd == "sign"){
          //签到通知
          this.loadUser(data.userId);
        }
      },
      websocketOnclose: function (e) {
        console.log("connection closed (" + e.code + ")");
        this.reconnect();
      },
      reconnect() {
        var that = this;
        if(that.lockReconnect) return;
        that.lockReconnect = true;
        //没连接上会一直重连，设置延迟避免请求过多
        setTimeout(function () {
          console.info("尝试重连...");
          that.initWebSocket();
          that.lockReconnect = false;
        }, 5000);
      },
    }
  }
</script>

<style lang="less" scoped>
  .container {
    height:100%;
    width:100%;
    color: #ffffff;
    position:fixed;
    display:flex;
    justify-content:space-around;
    align-items: center;
    flex-direction: row;
    overflow:hidden;
    li{
      float:left;
      list-style:none;
      margin:50px 0 0 50px;
    }

  }

</style>