<template>
    <div id="anIndex">
        <ul id="wrap">
            <li @click="ft">
                <img src="../../assets/disinfectionList/logo1.png" alt="">
                <div class="con">
                    <p>1.为疫情防控工作需要，请填写乘车使用的羊城通卡相关实名信息。羊城通记名卡、老人卡、学生卡、残疾人卡等有实名信息的卡无需重复登记。</p>
                    <p>2.使用现金支付的乘客，请使用微信扫描公交车上的防疫登记二维码进行登记。</p>
                </div>
            </li>
            <li @click="goMini">
                <img src="../../assets/disinfectionList/logo2.png" alt="">
                <div class="con">
                    <p>为疫情防控工作需要，请扫描出租车内的防疫登记二维码，填写相关实名信息。建议索取并保留乘车发票，记录车牌号码与乘车时间，以便保障您的权益。</p>
                </div>
            </li>
            <li @click="tt">
                <img src="../../assets/disinfectionList/logo3-free.png" alt="">
            </li>
        </ul>
    </div>
</template>

<script>
    export default {
        name: 'anIndex',
        data() {
            return {
                appid: 'gh_310086d36455',
                path: '/pages/taxi_scan/taxi_scan?source=jwxxt'
            }
        },
        created() {

        },
        mounted() {

        },
        methods: {
            ft() {
                window.location.href = 'https://r2.gzyct.com/epidemic/#/home?type=1';
            },
            tt() {
                // window.location.href = './anTaxiList.html';
                this.$router.push({name:'anTaxiList'});
            },
            goMini() {
                var u = navigator.userAgent,
                    app = navigator.appVersion;
                var isAndroid = u.indexOf('Android') > -1; //android终端或者uc浏览器
                var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
                console.log(this.appid)
                console.log(this.path)
                if (isAndroid) {
                    if (AndroidApp.startWXMiniProgram) {
                        AndroidApp.startWXMiniProgram(this.appid, this.path)
                    } else {
                        $.alert_ok("请使用新版的app访问");
                    }
                } else if (isiOS) {
                    if (typeof iOSApp != "undefined") {
                        if (window.GCI.startWXMiniPrograms) {
                            window.GCI.startWXMiniPrograms(this.appid, this.path)
                            console.log("调用成功");
                        } else {
                            $.confirm('请使用新版的app访问,点击确定跳转至app store下载', function(result) {
                                if (result) {
                                    var downLoadUrl = 'https://apps.apple.com/cn/app/%E5%B9%BF%E5%B7%9E%E4%BA%A4%E9%80%9A-%E8%A1%8C%E8%AE%AF%E9%80%9A/id931449163'
                                    window.GCI.sendHtml(downLoadUrl)
                                }
                            })
                        }
                    }
                }
            }
        }
    }
</script>
<style scoped lang='less'>
    html,
    body {
        width: 100%;
    }

    #wrap {
        width: 100%;
        color: #333333;
    }

    #wrap {
        width: 86.4%;
        margin: 0 auto;
    }

    #wrap li {
        margin-top: 12px;
        padding-bottom: 12px;
        border-bottom: 1px solid #dfdfdf;
    }

    #wrap li img {
        width: 100%;
    }

    #wrap li .con p {
        font-size: 1.4rem;
    }

    #wrap li:nth-child(3) {
        border: none;
    }
</style>