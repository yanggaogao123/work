<template>
    <div id="weibo">
        <div class="xxt_header"><img class="top_img" src="../../assets/weiboTop_new.png"></div>
        <div class="xxt_body">
            <div class="tip">
                <!-- <div class="tip_top">温馨提醒</div> -->
                <div class="tip_bottom">点击下方按钮进入“广州交通”微博，可以公开评论或私信吐槽。如您首次使用微博参与交通建言或评论，需要完成微博的注册流程。
                </div>
            </div>
        </div>
        <div class="xxt_footer">
            <div class="weibo_btn">
                <div class="weibo_link" @click="jumpWeibo">交通建言</div>
            </div>
        </div>

        <div class="xxt_feedback" @click="jumpFeedback">
            对App提出建议
        </div>
    </div>
</template>

<script>
    export default {
        name: 'weibo',
        data() {
            return {

            }
        },
        methods:{
            jumpWeibo(){
                window.location.href = 'https://weibo.com/u/2606568840';
            },
            jumpFeedback(){
                this.$router.push({'name':'feedback'});
            }
        }
    }
</script>

<style scoped lang="less">
    body{
    width: 100%;
    height: 100%;
    background: rgba(255,255,255,1);
}
.xxt_header{
    margin: 50px 0;
    text-align: center;
}
.top_img{
    width: 65%;
    height: 40%;
}
.tip_top {
    width: 56px;
    height:60px;
    font-size:14px;
    font-family:PingFangSC-Medium,PingFang SC;
    margin: 28px 100px 0px;
    bold : 500;
    font-weight: 700;
}

.tip {
    margin: 0 auto;
    /* text-align: center; */
    width: 90%;
}

.tip_bottom {
    width: 100%;
    /* height:40px; */
    font-size: 15px;
    font-family:PingFangSC-Regular,PingFang SC;
    font-weight:400;
    color:rgba(51,51,51,1);
    line-height: 28px;
    margin: -20px 7px;
    text-indent: 2em;
}

.weibo_btn {
    width: 180px;
    height:44px;
    box-sizing: border-box;
    background:linear-gradient(53deg,rgba(36,161,238,1) 0%,rgba(36,188,238,1) 100%);
    box-shadow:0px 1px 8px 0px rgba(36,161,238,0.5);
    border-radius:22px;
    margin: 35px auto;
}
.weibo_link{
    width: 60px;
    height:20px;
    font-size:14px;
    font-family:PingFangSC-Regular,PingFang SC;
    font-weight:400;
    color:rgba(255,255,255,1);
    line-height:20px;
    margin: 0 auto;
    padding-top: 13px;
}

.xxt_footer {
    width: 365px;
    margin: 40px auto;
}

/* .xxt_feedback {
    width: 100%;
    height: 21px;
    font-size: 15px;
    font-family: PingFangSC-Medium,PingFang SC;
    font-weight: 500;
    color: rgba(36,161,238,1);
    line-height: 21px;
    text-align: center;
    position: absolute;
    bottom: 7%;
} */

.xxt_feedback{
    width: 180px;
    height: 40px;
    box-sizing: border-box;
    font-size: 14px;
    text-align: center;
    border-radius: 20px;
    font-family: PingFangSC-Medium,PingFang SC;
    /* font-weight: 500; */
    color: #23ABEF;
    line-height: 40px;
    background: #fff;
    border: 1px solid #23ABEF;
    margin: 0 auto;
}
</style>