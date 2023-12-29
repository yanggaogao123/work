<template>
    <!-- 仿真图 -->
    <a-card :bordered="false">
        <div id="main">
            <header>
                <a-form layout="inline">
                    <a-form-item label="主线路">
                        <GCIRouteSelect
                            v-model="routeId"
                            :value="routeId"
                            placeholder="请选择线路"
                            style="width: 160px"
                        ></GCIRouteSelect>
                    </a-form-item>
                    <a-form-item label="关联线路">
                        <GCIRouteSelect
                            v-model="supportRouteId"
                            :value="supportRouteId"
                            placeholder="请选择线路"
                            style="width: 160px"
                        ></GCIRouteSelect>
                    </a-form-item>
                    <a-form-item label="日期">
                        <a-date-picker
                            placeholder="请选择日期"
                            moment="YYYY-MM-DD"
                            v-model="runDate"
                            @change="onDataChange"
                        />
                    </a-form-item>

                    <a-form-item label="排班">
                        <a-select
                            allowClear
                            default-value="2"
                            placeholder="请选择排班方案"
                            style="width: 160px"
                            v-model="plan"
                        >
                            <a-select-option :value="2">全部</a-select-option>
                            <a-select-option :value="0">上行</a-select-option>
                            <a-select-option :value="1">下行</a-select-option>
                        </a-select>
                    </a-form-item>

                    <a-form-item>
                        <a-button type="primary" @click="searchIt" icon="search">查询</a-button>
                    </a-form-item>

                    <a-form-item style="float:right;">
                        <a-radio-group v-model="pageValue" @change="onChange">
                            <a-radio-button value="a">监控调度</a-radio-button>
                            <a-radio-button value="b">拍板查询</a-radio-button>
                            <a-radio-button value="c">排班仿真</a-radio-button>
                        </a-radio-group>
                    </a-form-item>
                </a-form>
            </header>
            <!-- <car-type-four-modal></car-type-four-modal> -->
            <car-type-one-modal></car-type-one-modal>
        </div>
    </a-card>
</template>

<script>
import Vue from 'vue';
import { ACCESS_TOKEN, ENCRYPTED_STRING } from '@/store/mutation-types';
import { encryption, getEncryptedString } from '@/utils/encryption/aesEncrypt';
import '@/assets/less/base.css';
import { getAction, postAction, getActionNew, postActionNew } from '@/api/manage';
import store from '@/store/';
// import { mixinDevice } from '@/utils/mixin';
import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect';
import GCIRouteSelect from '@/components/gci/GCIRouteSelect';
import moment from 'moment';
import busData from './modules/busData.json';
import stationOne from './modules/stationOne.json';
import stationTwo from './modules/stationTwo.json';
import CarTypeFourModal from './modules/CarTypeFourModal.vue';
import CarTypeOneModal from './modules/CarTypeOneModal.vue';

export default {
    name: 'SchedulingSimulation',
    // mixins: [mixinDevice],
    components: {
        GCIOrgTreeSelect,
        GCIRouteSelect,
        CarTypeOneModal,
        CarTypeFourModal
    },
    data() {
        return {
            url: {
                adrealInfo: '/simulation/adrealInfo'
            },
            paramString: '',
            id: '',
            encryptedString: {
                key: '',
                iv: ''
            },
            routeId: '',
            supportRouteId: '',
            runDate: moment().add('1', 'days'),

            plan: '',
            pageValue: 'c',
            time: moment(),
            playBool: true
        };
    },
    created() {
        this.getParamString();
        // this.getAdrealInfo();
        console.log(busData);
    },
    // mounted() {
    //     this.getListByRouteId();
    //     this.getListByRouteId2();
    //     this.adrealInfo();
    //     this.adrealInfo2();
    // },
    methods: {
        moment,
        getEncrypte() {
            var encryptedString = Vue.ls.get(ENCRYPTED_STRING);
            if (encryptedString == null) {
                getEncryptedString().then(data => {
                    this.encryptedString = data;
                });
            } else {
                this.encryptedString = encryptedString;
            }
        },
        getParamString() {
            console.log('route', this.$route);
            let url = this.$route.meta.url;
            let id = this.$route.path;
            let name = this.$route.name;
            this.id = id;
            //url = "http://www.baidu.com"
            let token = Vue.ls.get(ACCESS_TOKEN);
            let menuId = this.$route.meta.id;
            let manageUrl = process.env.VUE_APP_DOMAIN_URL + '/schedule-driverless';
            let time = new Date().getTime();
            console.log('------url------' + url);
            let params = [manageUrl, token, menuId, time].join(';');
            this.getEncrypte();
            this.paramString = encryption(params, this.encryptedString.key, this.encryptedString.iv);
            console.log(this.paramString);
        },
        onChange(e) {
            console.log(`checked = ${e.target.value}`);
        },
        onDataChange(date, dateString) {
            console.log(date, dateString);
        },
        searchIt() {
            console.log(123);
            if (!this.routeId || !this.supportRouteId || !this.runDate) {
                this.$message.error('请选择相关选项再查询');
            }
        }
    }
};
</script>

<style lang="less" scoped>
.ant-card {
    display: flex;
    min-height: 100%;

    /deep/.ant-card-body {
        // min-height: 100% !important;
        // height: 100% !important;
        flex: 1;
        padding: 0;
    }
}
#main {
    min-width: 1366px;
    height: 100%;
    overflow-x: auto;
}
header {
    height: 66px;
    background: #bfd9f3;
    .ant-form {
        padding: 12px;
    }
}
section {
    .con-head {
        // margin: 12px 0 0 0;
        padding: 12px;
        div {
            display: inline-block;
            font-size: 14px;
            margin-right: 12px;
        }
        div:nth-child(1) {
            span {
                display: inline-block;
                font-size: 16px;
                font-weight: 500;
            }
        }
        div:nth-child(2) {
            display: inline-block;
            font-size: 16px;
            font-weight: 500;
        }
        .time-choice {
            img {
                display: inline-block;
                width: 24px;
                margin-right: 5px;
                cursor: pointer;
            }
        }
        .progress-bar {
            display: inline-block;
            position: relative;
            height: 34px;
            span {
                display: inline-block;
                font-size: 14px;
                line-height: 34px;
                margin-right: 5px;
            }
            .bar {
                display: inline-block;
                width: 180px;
                height: 2px;
                background: #8ba0bf;
                vertical-align: top;
                margin: 12px 0 0 0;
                position: relative;
                .round {
                    position: absolute;
                    top: -8px;
                    width: 100%;
                    li {
                        display: inline-block;
                        width: 18px;
                        height: 18px;
                        border-radius: 18px;
                        background: #bfd9f3;
                        position: absolute;
                        cursor: pointer;
                        img {
                            position: absolute;
                            width: 23px;
                            top: -1px;
                            left: -2px;
                        }
                    }
                    li:nth-child(1) {
                        top: 0;
                        left: 0;
                    }
                    li:nth-child(2) {
                        top: 0;
                        left: 80px;
                    }
                    li:nth-child(3) {
                        top: 0;
                        right: 0;
                    }
                }
                .num {
                    position: absolute;
                    top: 8px;
                    width: 100%;
                    font-size: 12px;
                    li {
                        display: inline-block;
                    }
                    li:nth-child(2) {
                        margin: 0 52px;
                    }
                }
            }
        }
    }
    .car-content {
        display: flex;
        flex-wrap: nowrap;
        .left-car {
            width: 50%;
            height: 560px;

            .content {
                // overflow: scroll;
                width: 100%;
                height: 560px;
                position: relative;
                display: flex;
                align-items: center;
                left: 0;
                right: 0;
                margin: 0 auto;

                .car-box {
                    flex: 1;
                    height: 400px;
                    margin: 0 0 0 60px;
                    position: relative;

                    .top-car {
                        position: relative;
                        width: 100%;
                        height: 50%;

                        .top-bg {
                            position: absolute;
                            width: 100%;
                            height: 100%;
                            border-top-left-radius: 50px;
                            border-top-right-radius: 50px;
                            overflow: hidden;
                            border: 6px solid #2796fd;
                            border-bottom: none;
                        }
                    }

                    .bottom-car {
                        position: relative;
                        display: grid;
                        width: 100%;
                        height: 50%;

                        .bottom-bg {
                            position: absolute;
                            width: 100%;
                            height: 100%;
                            border-bottom-left-radius: 50px;
                            border-bottom-right-radius: 50px;
                            overflow: hidden;
                            border: 6px solid #2796fd;
                            border-top: none;
                        }
                    }

                    .line-car {
                        height: 168px;
                        width: 85%;
                        margin: 0 auto;
                        display: flex;
                        position: relative;

                        .line {
                            flex: 1;
                            position: relative;

                            .model {
                                width: 20px;
                                height: 162px;
                                line-height: 21px;
                                font-size: 14px;
                                position: relative;

                                > span {
                                    width: 100%;
                                    // text-align: center;
                                    display: -webkit-box;
                                    overflow: hidden;
                                    text-overflow: ellipsis;
                                    display: -webkit-box;
                                    -webkit-line-clamp: 7;
                                    line-clamp: 7;
                                    -webkit-box-orient: vertical;
                                    writing-mode: vertical-rl;
                                    // text-orientation: upright; /* 保持文字正立，可选 */
                                }
                            }
                        }
                    }

                    .line-car-top {
                        margin-top: 0;

                        .line {
                            .model {
                                float: right;

                                > span {
                                    margin-top: 15px;
                                }
                            }

                            .model:after {
                                content: ' ';
                                position: absolute;
                                left: 0;
                                top: -2px;
                                right: 0;
                                width: 10px;
                                height: 10px;
                                border-radius: 5px;
                                border: 2px solid #2680eb;
                                background: white;
                                margin: 0 auto;
                            }

                            .bus {
                                // display: none;
                                position: absolute;
                                top: -26px;
                                right: calc(0% - 14px);

                                .bus-container {
                                    position: relative;
                                    width: 50px;
                                    height: 26px;
                                    background: #ffffff;
                                    border-radius: 15px; /* 设置长方形四个角的弧度 */
                                    overflow: hidden;
                                }

                                .bus-bar {
                                    position: relative;
                                    width: 100%;
                                    height: 100%;
                                    background: conic-gradient(
                                        #ff0000 0% 0%,
                                        #ff0000 0% 50%,
                                        #b6b6b6 50% 100%,
                                        #b6b6b6 100% 100%
                                    );
                                    border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
                                    clip-path: inset(0 0 0 0 round 20px); /* 设置进度条四个角的弧度，与容器相同 */
                                }

                                .bus-text {
                                    position: absolute;
                                    width: 40px;
                                    height: 16px;
                                    border-radius: 10px;
                                    font-size: 13px;
                                    line-height: 16px;
                                    top: 50%;
                                    left: 50%;
                                    transform: translate(-50%, -50%);
                                    text-align: center;
                                    color: #262626; /* 进度条颜色 */
                                    background: #ffffff;
                                }
                            }

                            .bus-middle {
                                // display: none;
                                position: absolute;
                                top: -26px;
                                right: 40%;

                                .bus-info {
                                    position: absolute;
                                    width: 40px;
                                    height: 20px;

                                    img {
                                        display: block;
                                        width: 100%;
                                        height: 100%;
                                    }

                                    p {
                                        position: absolute;
                                        font-size: 1.2rem;
                                        line-height: 1.4rem;
                                        text-align: center;
                                        color: #fff;
                                        top: 1px;
                                        left: 0;
                                        right: 0;
                                        margin: 0 auto;
                                    }
                                }

                                .bus-congestion {
                                    position: absolute;
                                    width: 40px;
                                    top: -32px;
                                    text-align: center;
                                    .congestion-info {
                                        position: relative;
                                        display: inline-block;
                                        width: 40px;
                                        height: 5px;
                                        border-radius: 5px;
                                        background: #d6d6d6;
                                        overflow: hidden;

                                        div {
                                            position: absolute;
                                            //  width: 80%;
                                            height: 5px;
                                            border-radius: 5px;
                                            top: 0;
                                            left: 0;
                                            background: red;
                                        }
                                    }

                                    span {
                                        display: inline-block;
                                        font-size: 1.2rem;
                                    }
                                }
                            }
                            .logo {
                                position: absolute;
                                top: 0;
                                left: 0;
                                width: 6px;
                            }
                        }

                        > div:first-child {
                            flex: none;
                        }
                    }

                    .line-car-bottom {
                        margin-top: 40px;

                        .line {
                            .model {
                                float: left;
                                position: relative;
                                > span {
                                    position: absolute;
                                    margin-bottom: 15px;
                                    left: 0;
                                    bottom: 0;
                                }
                            }

                            .model:after {
                                content: ' ';
                                position: absolute;
                                left: 0;
                                bottom: 0px;
                                right: 0;
                                width: 10px;
                                height: 10px;
                                border-radius: 5px;
                                border: 2px solid #2680eb;
                                background: white;
                                margin: 0 auto;
                            }

                            .bus {
                                // display: none;
                                position: absolute;
                                bottom: -20px;
                                left: calc(0% - 16px);

                                .bus-container {
                                    position: relative;
                                    width: 50px;
                                    height: 26px;
                                    background: #ffffff;
                                    border-radius: 15px; /* 设置长方形四个角的弧度 */
                                    overflow: hidden;
                                }

                                .bus-bar {
                                    position: relative;
                                    width: 100%;
                                    height: 100%;
                                    background: conic-gradient(
                                        #ff0000 0% 0%,
                                        #ff0000 0% 50%,
                                        #b6b6b6 50% 100%,
                                        #b6b6b6 100% 100%
                                    );
                                    border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
                                    clip-path: inset(0 0 0 0 round 20px); /* 设置进度条四个角的弧度，与容器相同 */
                                }

                                .bus-text {
                                    position: absolute;
                                    width: 40px;
                                    height: 16px;
                                    border-radius: 10px;
                                    font-size: 13px;
                                    line-height: 16px;
                                    top: 50%;
                                    left: 50%;
                                    transform: translate(-50%, -50%);
                                    text-align: center;
                                    color: #262626; /* 进度条颜色 */
                                    background: #ffffff;
                                }
                            }

                            .bus-middle {
                                // display: none;
                                position: absolute;
                                bottom: -10px;
                                left: -75%;

                                .bus-info {
                                    position: absolute;
                                    width: 40px;
                                    height: 20px;

                                    img {
                                        display: block;
                                        width: 100%;
                                        height: 100%;
                                    }

                                    p {
                                        position: absolute;
                                        font-size: 1.2rem;
                                        line-height: 1.4rem;
                                        text-align: center;
                                        color: #fff;
                                        top: 1px;
                                        left: 0;
                                        right: 0;
                                        margin: 0 auto;
                                    }
                                }

                                .bus-congestion {
                                    position: absolute;
                                    width: 40px;
                                    top: 20px;
                                    text-align: center;
                                    .congestion-info {
                                        position: relative;
                                        display: inline-block;
                                        width: 40px;
                                        height: 5px;
                                        border-radius: 5px;
                                        background: #d6d6d6;
                                        overflow: hidden;

                                        div {
                                            position: absolute;
                                            // width: 80%;
                                            height: 5px;
                                            border-radius: 5px;
                                            top: 0;
                                            left: 0;
                                            background: red;
                                        }
                                    }

                                    span {
                                        display: inline-block;
                                        font-size: 1.2rem;
                                    }
                                }
                            }

                            .logo {
                                position: absolute;
                                bottom: 8px;
                                right: 0;
                                width: 6px;
                            }
                        }

                        > div:last-child {
                            flex: none;
                        }
                    }

                    .center-car {
                        z-index: 99;
                        background: #b8deff;
                        height: 100px;
                        width: 76%;
                        position: absolute;
                        // border: 1px #3789fe dashed;
                        border-radius: 15px;
                        box-sizing: border-box;
                        padding: 5px 0;
                        overflow: hidden;
                        left: 0;
                        right: 0;
                        top: 0;
                        bottom: 0;
                        margin: auto;

                        > p {
                            display: flex;
                            width: 100%;
                            line-height: 30px;

                            > span {
                                font-size: 15px;
                                line-height: 30px;
                                text-align: center;
                                color: #444444;
                                flex: 1;
                            }
                        }
                    }

                    .top-st {
                        width: 18px;
                        top: 50%;
                        left: 12px;
                        position: absolute;
                        font-weight: 600;
                        transform: translate(0, -50%);
                        -webkit-box-orient: vertical;
                        writing-mode: vertical-rl;
                    }
                    .top-st:after {
                        content: '';
                        width: 18px;
                        height: 18px;
                        background: #fff;
                        border: 2px solid #2680eb;
                        border-radius: 10px;
                        position: absolute;
                        left: -18px;
                        top: 50%;
                        transform: translate(0, -50%);
                    }

                    .bottom-st {
                        width: 18px;
                        top: 50%;
                        right: 12px;
                        position: absolute;
                        font-weight: 600;
                        transform: translate(0, -50%);
                    }
                    .bottom-st:after {
                        content: '';
                        width: 18px;
                        height: 18px;
                        background: #fff;
                        border: 2px solid #2680eb;
                        border-radius: 10px;
                        position: absolute;
                        right: -18px;
                        top: 50%;
                        transform: translate(0, -50%);
                    }
                }

                .left-z-car {
                    position: absolute;
                    left: -6px;
                    top: 50%;
                    transform: translate(0, -50%);
                    width: 50px;
                    // text-align: center;

                    .bus {
                        display: block;
                        position: relative;
                        height: 20px;
                        margin: 0 0 10px 0;

                        .bus-container {
                            position: relative;
                            width: 50px;
                            height: 26px;
                            background: #ffffff;
                            border-radius: 15px; /* 设置长方形四个角的弧度 */
                            overflow: hidden;
                        }

                        .bus-bar {
                            position: relative;
                            width: 100%;
                            height: 100%;
                            background: conic-gradient(
                                #ff0000 0% 0%,
                                #ff0000 0% 50%,
                                #b6b6b6 50% 100%,
                                #b6b6b6 100% 100%
                            );
                            border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
                            clip-path: inset(0 0 0 0 round 20px); /* 设置进度条四个角的弧度，与容器相同 */
                        }

                        .bus-text {
                            position: absolute;
                            width: 40px;
                            height: 16px;
                            border-radius: 10px;
                            font-size: 13px;
                            line-height: 16px;
                            top: 50%;
                            left: 50%;
                            transform: translate(-50%, -50%);
                            text-align: center;
                            color: #262626; /* 进度条颜色 */
                            background: #ffffff;
                        }
                    }
                }

                .right-z-car {
                    position: absolute;
                    right: 14px;
                    top: 50%;
                    transform: translate(0, -50%);
                    .bus {
                        display: block;
                        position: relative;
                        height: 20px;
                        margin: 0 0 10px 0;

                        .bus-container {
                            position: relative;
                            width: 50px;
                            height: 26px;
                            background: #ffffff;
                            border-radius: 15px; /* 设置长方形四个角的弧度 */
                            overflow: hidden;
                        }

                        .bus-bar {
                            position: relative;
                            width: 100%;
                            height: 100%;
                            background: conic-gradient(
                                #ff0000 0% 0%,
                                #ff0000 0% 50%,
                                #b6b6b6 50% 100%,
                                #b6b6b6 100% 100%
                            );
                            border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
                            clip-path: inset(0 0 0 0 round 20px); /* 设置进度条四个角的弧度，与容器相同 */
                        }

                        .bus-text {
                            position: absolute;
                            width: 40px;
                            height: 16px;
                            border-radius: 10px;
                            font-size: 13px;
                            line-height: 16px;
                            top: 50%;
                            left: 50%;
                            transform: translate(-50%, -50%);
                            text-align: center;
                            color: #262626; /* 进度条颜色 */
                            background: #ffffff;
                        }
                    }
                }
            }
        }
        .right-car {
            width: 50%;
            height: 560px;

            .content {
                // overflow: scroll;
                width: 100%;
                height: 560px;
                position: relative;
                display: flex;
                align-items: center;
                left: 0;
                right: 0;
                margin: 0 auto;

                .car-box {
                    flex: 1;
                    height: 400px;
                    margin: 0px 60px 0 0;
                    position: relative;

                    .top-car {
                        position: relative;
                        width: 100%;
                        height: 50%;

                        .top-bg {
                            position: absolute;
                            width: 100%;
                            height: 100%;
                            border-top-left-radius: 50px;
                            border-top-right-radius: 50px;
                            overflow: hidden;
                            border: 6px solid #2fcba9;
                            border-bottom: none;
                        }
                    }

                    .bottom-car {
                        position: relative;
                        display: grid;
                        width: 100%;
                        height: 50%;

                        .bottom-bg {
                            position: absolute;
                            width: 100%;
                            height: 100%;
                            border-bottom-left-radius: 50px;
                            border-bottom-right-radius: 50px;
                            overflow: hidden;
                            border: 6px solid #2fcba9;
                            border-top: none;
                        }
                    }

                    .line-car {
                        height: 168px;
                        width: 85%;
                        margin: 0 auto;
                        display: flex;
                        position: relative;

                        .line {
                            flex: 1;
                            position: relative;

                            .model {
                                width: 20px;
                                height: 162px;
                                line-height: 21px;
                                font-size: 14px;
                                position: relative;

                                > span {
                                    width: 100%;
                                    // text-align: center;
                                    display: -webkit-box;
                                    overflow: hidden;
                                    text-overflow: ellipsis;
                                    display: -webkit-box;
                                    -webkit-line-clamp: 7;
                                    line-clamp: 7;
                                    -webkit-box-orient: vertical;
                                    writing-mode: vertical-rl;
                                    // text-orientation: upright; /* 保持文字正立，可选 */
                                }
                            }
                        }
                    }

                    .line-car-top {
                        margin-top: 0;

                        .line {
                            .model {
                                float: right;

                                > span {
                                    margin-top: 15px;
                                }
                            }

                            .model:after {
                                content: ' ';
                                position: absolute;
                                left: 0;
                                top: -2px;
                                right: 0;
                                width: 10px;
                                height: 10px;
                                border-radius: 5px;
                                border: 2px solid #2fcba9;
                                background: white;
                                margin: 0 auto;
                            }

                            .bus {
                                // display: none;
                                position: absolute;
                                top: -26px;
                                right: calc(0% - 14px);

                                .bus-container {
                                    position: relative;
                                    width: 50px;
                                    height: 26px;
                                    background: #ffffff;
                                    border-radius: 15px; /* 设置长方形四个角的弧度 */
                                    overflow: hidden;
                                }

                                .bus-bar {
                                    position: relative;
                                    width: 100%;
                                    height: 100%;
                                    background: conic-gradient(
                                        #ff0000 0% 0%,
                                        #ff0000 0% 50%,
                                        #b6b6b6 50% 100%,
                                        #b6b6b6 100% 100%
                                    );
                                    border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
                                    clip-path: inset(0 0 0 0 round 20px); /* 设置进度条四个角的弧度，与容器相同 */
                                }

                                .bus-text {
                                    position: absolute;
                                    width: 40px;
                                    height: 16px;
                                    border-radius: 10px;
                                    font-size: 13px;
                                    line-height: 16px;
                                    top: 50%;
                                    left: 50%;
                                    transform: translate(-50%, -50%);
                                    text-align: center;
                                    color: #262626; /* 进度条颜色 */
                                    background: #ffffff;
                                }
                            }

                            .bus-middle {
                                // display: none;
                                position: absolute;
                                top: -26px;
                                right: 40%;

                                .bus-info {
                                    position: absolute;
                                    width: 40px;
                                    height: 20px;

                                    img {
                                        display: block;
                                        width: 100%;
                                        height: 100%;
                                    }

                                    p {
                                        position: absolute;
                                        font-size: 1.2rem;
                                        line-height: 1.4rem;
                                        text-align: center;
                                        color: #fff;
                                        top: 1px;
                                        left: 0;
                                        right: 0;
                                        margin: 0 auto;
                                    }
                                }

                                .bus-congestion {
                                    position: absolute;
                                    width: 40px;
                                    top: -32px;
                                    text-align: center;
                                    .congestion-info {
                                        position: relative;
                                        display: inline-block;
                                        width: 40px;
                                        height: 5px;
                                        border-radius: 5px;
                                        background: #d6d6d6;
                                        overflow: hidden;

                                        div {
                                            position: absolute;
                                            //  width: 80%;
                                            height: 5px;
                                            border-radius: 5px;
                                            top: 0;
                                            left: 0;
                                            background: red;
                                        }
                                    }

                                    span {
                                        display: inline-block;
                                        font-size: 1.2rem;
                                    }
                                }
                            }
                            .logo {
                                position: absolute;
                                top: 0;
                                left: 0;
                                width: 6px;
                            }
                        }

                        > div:first-child {
                            flex: none;
                        }
                    }

                    .line-car-bottom {
                        margin-top: 40px;

                        .line {
                            .model {
                                float: left;
                                position: relative;
                                > span {
                                    position: absolute;
                                    margin-bottom: 15px;
                                    left: 0;
                                    bottom: 0;
                                }
                            }

                            .model:after {
                                content: ' ';
                                position: absolute;
                                left: 0;
                                bottom: 0px;
                                right: 0;
                                width: 10px;
                                height: 10px;
                                border-radius: 5px;
                                border: 2px solid #2fcba9;
                                background: white;
                                margin: 0 auto;
                            }

                            .bus {
                                // display: none;
                                position: absolute;
                                bottom: -20px;
                                left: calc(0% - 16px);

                                .bus-container {
                                    position: relative;
                                    width: 50px;
                                    height: 26px;
                                    background: #ffffff;
                                    border-radius: 15px; /* 设置长方形四个角的弧度 */
                                    overflow: hidden;
                                }

                                .bus-bar {
                                    position: relative;
                                    width: 100%;
                                    height: 100%;
                                    background: conic-gradient(
                                        #ff0000 0% 0%,
                                        #ff0000 0% 50%,
                                        #b6b6b6 50% 100%,
                                        #b6b6b6 100% 100%
                                    );
                                    border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
                                    clip-path: inset(0 0 0 0 round 20px); /* 设置进度条四个角的弧度，与容器相同 */
                                }

                                .bus-text {
                                    position: absolute;
                                    width: 40px;
                                    height: 16px;
                                    border-radius: 10px;
                                    font-size: 13px;
                                    line-height: 16px;
                                    top: 50%;
                                    left: 50%;
                                    transform: translate(-50%, -50%);
                                    text-align: center;
                                    color: #262626; /* 进度条颜色 */
                                    background: #ffffff;
                                }
                            }

                            .bus-middle {
                                // display: none;
                                position: absolute;
                                bottom: -10px;
                                left: -75%;

                                .bus-info {
                                    position: absolute;
                                    width: 40px;
                                    height: 20px;

                                    img {
                                        display: block;
                                        width: 100%;
                                        height: 100%;
                                    }

                                    p {
                                        position: absolute;
                                        font-size: 1.2rem;
                                        line-height: 1.4rem;
                                        text-align: center;
                                        color: #fff;
                                        top: 1px;
                                        left: 0;
                                        right: 0;
                                        margin: 0 auto;
                                    }
                                }

                                .bus-congestion {
                                    position: absolute;
                                    width: 40px;
                                    top: 20px;
                                    text-align: center;
                                    .congestion-info {
                                        position: relative;
                                        display: inline-block;
                                        width: 40px;
                                        height: 5px;
                                        border-radius: 5px;
                                        background: #d6d6d6;
                                        overflow: hidden;

                                        div {
                                            position: absolute;
                                            // width: 80%;
                                            height: 5px;
                                            border-radius: 5px;
                                            top: 0;
                                            left: 0;
                                            background: red;
                                        }
                                    }

                                    span {
                                        display: inline-block;
                                        font-size: 1.2rem;
                                    }
                                }
                            }

                            .logo {
                                position: absolute;
                                bottom: 8px;
                                right: 0;
                                width: 6px;
                            }
                        }

                        > div:last-child {
                            flex: none;
                        }
                    }

                    .center-car {
                        z-index: 99;
                        background: #bce9f0;
                        height: 100px;
                        width: 76%;
                        position: absolute;
                        // border: 1px #3789fe dashed;
                        border-radius: 15px;
                        box-sizing: border-box;
                        padding: 5px 0;
                        overflow: hidden;
                        left: 0;
                        right: 0;
                        top: 0;
                        bottom: 0;
                        margin: auto;

                        > p {
                            display: flex;
                            width: 100%;
                            line-height: 30px;

                            > span {
                                font-size: 15px;
                                line-height: 30px;
                                text-align: center;
                                color: #444444;
                                flex: 1;
                            }
                        }
                    }

                    .top-st {
                        width: 18px;
                        top: 50%;
                        left: 12px;
                        position: absolute;
                        font-weight: 600;
                        transform: translate(0, -50%);
                        -webkit-box-orient: vertical;
                        writing-mode: vertical-rl;
                    }
                    .top-st:after {
                        content: '';
                        width: 18px;
                        height: 18px;
                        background: #fff;
                        border: 2px solid #2fcba9;
                        border-radius: 10px;
                        position: absolute;
                        left: -18px;
                        top: 50%;
                        transform: translate(0, -50%);
                    }

                    .bottom-st {
                        width: 18px;
                        top: 50%;
                        right: 12px;
                        position: absolute;
                        font-weight: 600;
                        transform: translate(0, -50%);
                    }
                    .bottom-st:after {
                        content: '';
                        width: 18px;
                        height: 18px;
                        background: #fff;
                        border: 2px solid #2fcba9;
                        border-radius: 10px;
                        position: absolute;
                        right: -18px;
                        top: 50%;
                        transform: translate(0, -50%);
                    }
                }

                .left-z-car {
                    position: absolute;
                    left: 16px;
                    top: 50%;
                    transform: translate(0, -50%);
                    width: 50px;
                    // text-align: center;

                    .bus {
                        display: block;
                        position: relative;
                        height: 20px;
                        margin: 0 0 10px 0;

                        .bus-container {
                            position: relative;
                            width: 50px;
                            height: 26px;
                            background: #ffffff;
                            border-radius: 15px; /* 设置长方形四个角的弧度 */
                            overflow: hidden;
                        }

                        .bus-bar {
                            position: relative;
                            width: 100%;
                            height: 100%;
                            background: conic-gradient(
                                #ff0000 0% 0%,
                                #ff0000 0% 50%,
                                #b6b6b6 50% 100%,
                                #b6b6b6 100% 100%
                            );
                            border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
                            clip-path: inset(0 0 0 0 round 20px); /* 设置进度条四个角的弧度，与容器相同 */
                        }

                        .bus-text {
                            position: absolute;
                            width: 40px;
                            height: 16px;
                            border-radius: 10px;
                            font-size: 13px;
                            line-height: 16px;
                            top: 50%;
                            left: 50%;
                            transform: translate(-50%, -50%);
                            text-align: center;
                            color: #262626; /* 进度条颜色 */
                            background: #ffffff;
                        }
                    }
                }

                .right-z-car {
                    position: absolute;
                    right: 3px;
                    top: 50%;
                    transform: translate(0, -50%);
                    .bus {
                        display: block;
                        position: relative;
                        height: 20px;
                        margin: 0 0 10px 0;

                        .bus-container {
                            position: relative;
                            width: 50px;
                            height: 26px;
                            background: #ffffff;
                            border-radius: 15px; /* 设置长方形四个角的弧度 */
                            overflow: hidden;
                        }

                        .bus-bar {
                            position: relative;
                            width: 100%;
                            height: 100%;
                            background: conic-gradient(
                                #ff0000 0% 0%,
                                #ff0000 0% 50%,
                                #b6b6b6 50% 100%,
                                #b6b6b6 100% 100%
                            );
                            border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
                            clip-path: inset(0 0 0 0 round 20px); /* 设置进度条四个角的弧度，与容器相同 */
                        }

                        .bus-text {
                            position: absolute;
                            width: 40px;
                            height: 16px;
                            border-radius: 10px;
                            font-size: 13px;
                            line-height: 16px;
                            top: 50%;
                            left: 50%;
                            transform: translate(-50%, -50%);
                            text-align: center;
                            color: #262626; /* 进度条颜色 */
                            background: #ffffff;
                        }
                    }
                }
            }
        }

        .content::-webkit-scrollbar {
            display: none;
        }

        .middleRun {
            animation: stationToMiddle 1s;
        }
        .stationRun {
            animation: middleToStation 1s;
        }

        @keyframes stationToMiddle {
            from {
                right: 31px;
            }
            to {
                right: 40%;
            }
        }

        @keyframes middleToStation {
            from {
                right: calc(100% - 14px);
            }

            to {
                right: calc(0% - 14px);
            }
        }

        .stm {
            animation: downStationToMiddle 1s;
        }
        .mts {
            animation: downMiddleToStation 1s;
        }

        @keyframes downStationToMiddle {
            from {
                left: -10%;
            }

            to {
                left: -75%;
            }
        }

        @keyframes downMiddleToStation {
            from {
                left: calc(100% - 16px);
            }

            to {
                left: calc(0% - 16px);
            }
        }
    }
}
</style>
