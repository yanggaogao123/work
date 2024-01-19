<template>
    <div id="bikeSharing">
        <div class="wrap">
            <p>温馨提醒:各单车平台由其公司独立运营，如有订单或费用等疑问，请点击拨打 <span @click="show = true"><img src="../assets/bikeSharing/phone.png" alt="">客服热线</span></p>
            <img @click="goHaluo" src="../assets/bikeSharing/bikeSharing_haluo.png" alt="">
            <img @click="goMeituan" src="../assets/bikeSharing/bikeSharing_meituan.png" alt="">
            <img @click="goDidi" src="../assets/bikeSharing/bikeSharing_didi.png" alt="">
        </div>
        <van-overlay :show="show" @click="show = false">
            <div class="wrapper" >
                <div class="block">
                    <p>哈啰单车客服热线：400-091-0857；</p>
                    <p>美团单车客服热线：400-811-7799；</p>
                    <p>青桔单车客服热线：400-000-0999；</p>
                </div>
            </div>
        </van-overlay>

    </div>
</template>

<script>
import { ref } from 'vue';
export default {
    name:'bikeSharing',
    data(){
        return{
            show: false,
        }
    },
    created(){

    },
    mounted(){

    },
    methods:{
        goHaluo(){
            let url = `https://render.alipay.com/p/s/i/?scheme=alipays://platformapi/startapp?appId=2017050407110255&query=/evbike/private/oho/home/index`;
            var u = navigator.userAgent, app = navigator.appVersion;
            var isAndroid = u.indexOf('Android') > -1; //android终端或者uc浏览器
            var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
            if(isAndroid) {
                AndroidApp.sendHtml(url);
            }else if(isiOS){
                window.GCI.sendHtml(url)
            }
        },
        goMeituan(){
            var u = navigator.userAgent, app = navigator.appVersion;
            var isAndroid = u.indexOf('Android') > -1; //android终端或者uc浏览器
            var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
            var appid = 'gh_870576f3c6f9';
            var url = 'mobike/pages/index/index';
            if (isAndroid) {
                    if (AndroidApp.startWXMiniProgram) {
                        AndroidApp.startWXMiniProgram(appid, url)
                    } else {
                        $.alert_ok("请使用新版的app访问");
                    }
                    console.log("进入安卓跳转小程序方法成功")
                } else if (isiOS) {
                    if (typeof iOSApp != "undefined") {
                        if (window.GCI.startWXMiniPrograms) {
                            window.GCI.startWXMiniPrograms(appid, url)
                            console.log("调用成功");
                        } else {
                            $.confirm('请使用新版的app访问,点击确定跳转至app store下载', function (result) {
                                if (result) {
                                    var downLoadUrl = 'https://apps.apple.com/cn/app/%E5%B9%BF%E5%B7%9E%E4%BA%A4%E9%80%9A-%E8%A1%8C%E8%AE%AF%E9%80%9A/id931449163'
                                    window.GCI.sendHtml(downLoadUrl)
                                }
                            })
                        }
                    }
                    window.GCI.startWXMiniPrograms(appid, url)
                }
        },
        goDidi(){
            var u = navigator.userAgent, app = navigator.appVersion;
            var isAndroid = u.indexOf('Android') > -1; //android终端或者uc浏览器
            var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
            var appid = 'gh_0f3405792a89';
            var url = 'pages/init/init';
            if (isAndroid) {
                    if (AndroidApp.startWXMiniProgram) {
                        AndroidApp.startWXMiniProgram(appid, url)
                    } else {
                        $.alert_ok("请使用新版的app访问");
                    }
                    console.log("进入安卓跳转小程序方法成功")
                } else if (isiOS) {
                    if (typeof iOSApp != "undefined") {
                        if (window.GCI.startWXMiniPrograms) {
                            window.GCI.startWXMiniPrograms(appid, url)
                            console.log("调用成功");
                        } else {
                            $.confirm('请使用新版的app访问,点击确定跳转至app store下载', function (result) {
                                if (result) {
                                    var downLoadUrl = 'https://apps.apple.com/cn/app/%E5%B9%BF%E5%B7%9E%E4%BA%A4%E9%80%9A-%E8%A1%8C%E8%AE%AF%E9%80%9A/id931449163'
                                    window.GCI.sendHtml(downLoadUrl)
                                }
                            })
                        }
                    }
                    window.GCI.startWXMiniPrograms(appid, url)
                }
        }
    }
}
</script>

<style lang='less' scoped>
    #bikeSharing{
        width: 100%;
        height: 100%;
        min-height: 100%;
        .wrap{
            width: 100%;
            height: 100%;
            background: url('../assets/bikeSharing/bikeSharing_bg.png') no-repeat;
            background-size: 100% 100%;
            p{
                width: 86%;
                font-size: 1.4rem;
                line-height: 20px;
                padding-top: 24px;
                margin: 0 auto 22px auto;
                span{
                    display: inline-block;
                    font-size: 1.4rem;
                    color: #fff;
                    background: #15C87A;
                    padding: 2px 8px;
                    border-radius: 4px;
                    margin-left: 10px;
                    font-weight: 600;
                    img{
                        display: inline-block;
                        width: 16px;
                        vertical-align: top;
                        margin: 2px 4px 0 0;
                    }
                }

            }
            img{
                display: block;
                width: 88%;
                margin: 0 auto;
            }
        }
        .wrapper{
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100%;
            .block{
                width: 340px;
                // height: 240px;
                background-color: #fff;
                border-radius: 10px;
                box-sizing: border-box;
                padding: 10px;
                p{
                    font-size: 1.6rem;
                    line-height: 20px;
                }
            }
        }
    }
</style>