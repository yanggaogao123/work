<template>
    <div class="user-wrapper" :class="theme">
        <!-- update_begin author:zhaoxin date:20191129 for: 做头部菜单栏导航 -->
        <!-- update-begin author:sunjianlei date:20191@20 for: 解决全局样式冲突的问题 -->
        <span class="action" @click="showClick">
            <a-icon type="search"></a-icon>
        </span>
        <!-- update-begin author:sunjianlei date:20200219 for: 菜单搜索改为动态组件，在手机端呈现出弹出框 -->
        <component
            :is="searchMenuComp"
            v-show="searchMenuVisible || isMobile()"
            class="borders"
            :visible="searchMenuVisible"
            title="搜索菜单"
            :footer="null"
            @cancel="searchMenuVisible = false"
        >
            <a-select
                class="search-input"
                showSearch
                :showArrow="false"
                placeholder="搜索菜单"
                optionFilterProp="children"
                :filterOption="filterOption"
                :open="isMobile() ? true : null"
                :getPopupContainer="node => node.parentNode"
                :style="isMobile() ? { width: '100%', marginBottom: '50px' } : {}"
                @blur="hiddenClick"
            >
                <a-select-option
                    @click.native="searchMethods(site.id)"
                    v-for="(site, index) in searchMenuOptions"
                    :key="index"
                    :value="site.id"
                    >{{ site.meta.title }}</a-select-option
                >
            </a-select>
        </component>
        <!-- update-end author:sunjianlei date:20200219 for: 菜单搜索改为动态组件，在手机端呈现出弹出框 -->
        <!-- update-end author:sunjianlei date:20191220 for: 解决全局样式冲突的问题 -->
        <!-- update_end  author:zhaoxin date:20191129 for: 做头部菜单栏导航 -->
        <span class="action">
            <a class="logout_title" target="_blank" href="http://doc.jeecg.com">
                <a-icon type="question-circle-o"></a-icon>
            </a>
        </span>
        <header-notice class="action" />
        <a-dropdown>
            <span class="action action-full ant-dropdown-link user-dropdown-menu">
                <!--                <a-avatar class="avatar" size="small" :src="getAvatar()" />-->
                <span v-if="isDesktop()">{{ currUserInfo.realname }}</span>
            </span>
            <a-menu slot="overlay" class="user-dropdown-menu-wrapper">
                <!--        <a-menu-item key="0">-->
                <!--          <router-link :to="{ name: 'account-center' }">-->
                <!--            <a-icon type="user" />-->
                <!--            <span>个人中心</span>-->
                <!--          </router-link>-->
                <!--        </a-menu-item>-->
                <!--        <a-menu-item key="1">-->
                <!--          <router-link :to="{ name: 'account-settings-base' }">-->
                <!--            <a-icon type="setting" />-->
                <!--            <span>账户设置</span>-->
                <!--          </router-link>-->
                <!--        </a-menu-item>-->
                <!-- <a-menu-item key="3" @click="systemSetting">
          <a-icon type="tool" />
          <span>系统设置</span>
        </a-menu-item> -->
                <a-menu-item key="4" @click="updatePassword">
                    <a-icon type="setting" />
                    <span>密码修改</span>
                </a-menu-item>
                <a-menu-item key="5" @click="updateCurrentDepart">
                    <a-icon type="cluster" />
                    <span>切换部门</span>
                </a-menu-item>
                <a-menu-item key="6" @click="updateCurrentPlatfrom">
                    <a-icon type="home" />
                    <span>切换平台</span>
                </a-menu-item>
                <!-- <a-menu-item key="2" disabled>
          <a-icon type="setting"/>
          <span>测试</span>
        </a-menu-item>
        <a-menu-divider/>
        <a-menu-item key="3">
          <a href="javascript:;" @click="handleLogout">
            <a-icon type="logout"/>
            <span>退出登录</span>
          </a>
        </a-menu-item>-->
            </a-menu>
        </a-dropdown>
        <span class="action">
            <a class="logout_title" href="javascript:;" @click="handleLogout">
                <a-icon type="logout" />
                <span v-if="isDesktop()">&nbsp;退出登录</span>
            </a>
        </span>
        <user-password ref="userPassword"></user-password>
        <depart-select ref="departSelect" :closable="true" title="部门切换"></depart-select>
        <platform-select ref="platformSelect" :closable="true" title="平台切换"></platform-select>
        <setting-drawer ref="settingDrawer" :closable="true" title="系统设置"></setting-drawer>
    </div>
</template>

<script>
import HeaderNotice from './HeaderNotice';
import UserPassword from './UserPassword';
import SettingDrawer from '@/components/setting/SettingDrawer';
import DepartSelect from './DepartSelect';
import PlatformSelect from './PlatformSelect';
import { mapActions, mapGetters, mapState } from 'vuex';
import { mixinDevice } from '@/utils/mixin.js';
import { getFileAccessHttpUrl } from '@/api/manage';
import Vue from 'vue';
import store from '@/store/';
import { ACCESS_TOKEN } from '@/store/mutation-types';
import { encryption, getEncryptedString } from '../../utils/encryption/aesEncrypt';
import { ENCRYPTED_STRING, USER_INFO } from '../../store/mutation-types';

export default {
    name: 'UserMenu',
    mixins: [mixinDevice],
    data() {
        return {
            currUserInfo: {},
            // update-begin author:sunjianlei date:20200219 for: 头部菜单搜索规范命名 --------------
            searchMenuOptions: [],
            searchMenuComp: 'span',
            searchMenuVisible: false
            // update-begin author:sunjianlei date:20200219 for: 头部菜单搜索规范命名 --------------
        };
    },
    components: {
        HeaderNotice,
        UserPassword,
        DepartSelect,
        PlatformSelect,
        SettingDrawer
    },
    props: {
        theme: {
            type: String,
            required: false,
            default: 'dark'
        }
    },
    /* update_begin author:zhaoxin date:20191129 for: 做头部菜单栏导航*/
    created() {
        this.currUserInfo = Vue.ls.get(USER_INFO);
        let lists = [];
        this.searchMenus(lists, this.permissionMenuList);
        this.searchMenuOptions = [...lists];
    },
    mounted() {
        let depart = this.currUserInfo.orgCode;
        if (!depart) {
            this.updateCurrentDepart();
        }
        this.initWebSocket();
        this.heartCheckFun();
    },
    destroyed: function() {
        // 离开页面生命周期函数
        this.websocketclose();
    },
    computed: {
        ...mapState({
            // 后台菜单
            permissionMenuList: state => state.user.permissionList
        })
    },
    /* update_end author:zhaoxin date:20191129 for: 做头部菜单栏导航*/
    watch: {
        // update-begin author:sunjianlei date:20200219 for: 菜单搜索改为动态组件，在手机端呈现出弹出框
        device: {
            immediate: true,
            handler() {
                this.searchMenuVisible = false;
                this.searchMenuComp = this.isMobile() ? 'a-modal' : 'span';
            }
        }
        // update-end author:sunjianlei date:20200219 for: 菜单搜索改为动态组件，在手机端呈现出弹出框
    },
    methods: {
        /* update_begin author:zhaoxin date:20191129 for: 做头部菜单栏导航*/
        showClick() {
            this.searchMenuVisible = true;
        },
        hiddenClick() {
            this.shows = false;
        },
        /* update_end author:zhaoxin date:20191129 for: 做头部菜单栏导航*/
        ...mapActions(['Logout']),
        ...mapGetters(['nickname', 'avatar', 'userInfo']),
        getAvatar() {
            return getFileAccessHttpUrl(this.avatar());
        },
        handleLogout() {
            const that = this;

            this.$confirm({
                title: '提示',
                content: '真的要注销登录吗 ?',
                onOk() {
                    return that
                        .Logout({})
                        .then(() => {
                            window.location.href = window.location.href =
                                process.env.VUE_APP_PUBLIC_PATH + 'user/login';
                            //window.location.reload()
                            // window.localStorage.clear();
                        })
                        .catch(err => {
                            that.$message.error({
                                title: '错误',
                                description: err.message
                            });
                        });
                },
                onCancel() {}
            });
        },
        updatePassword() {
            let username = this.userInfo().username;
            this.$refs.userPassword.show(username);
        },
        updateCurrentDepart() {
            this.$refs.departSelect.show();
        },
        updateCurrentPlatfrom() {
            this.$refs.platformSelect.show();
        },
        systemSetting() {
            this.$refs.settingDrawer.showDrawer();
        },
        /* update_begin author:zhaoxin date:20191129 for: 做头部菜单栏导航*/
        searchMenus(arr, menus, parentPath) {
            if (!parentPath) {
                parentPath = '/';
            }
            for (let i of menus) {
                i.parentPath = parentPath;
                if (!i.hidden && 'layouts/RouteView' !== i.component) {
                    arr.push(i);
                }
                if (i.children && i.children.length > 0) {
                    this.searchMenus(arr, i.children, i.path);
                }
            }
        },
        filterOption(input, option) {
            return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
        },
        // update_begin author:sunjianlei date:20191230 for: 解决外部链接打开失败的问题
        searchMethods(value) {
            let route = this.searchMenuOptions.filter(item => item.id === value)[0];
            let path = route.path;
            if ('layouts/FineReportPageView' === route.component) {
                if (path.indexOf('/') === 0) {
                    path = route.parentPath + path;
                } else {
                    path = route.parentPath + '/' + path;
                }
            }
            if (route.meta.internalOrExternal === true && route.component.includes('layouts/IframePageView')) {
                //有外部打开标识并且是包含IframePageView组件才允许外部打开
                this.openIframePageView(route);
            } else {
                this.$router.push({ path: path });
            }
            this.searchMenuVisible = false;
        },
        openIframePageView(route) {
            let url = route.meta.url;
            let id = route.path;
            let name = route.name;
            this.id = id;
            //url = "http://www.baidu.com"
            let token = Vue.ls.get(ACCESS_TOKEN);
            let menuId = route.meta.id;
            let manageUrl = process.env.VUE_APP_DOMAIN_URL + process.env.VUE_APP_BASE_API;
            let time = new Date().getTime();
            console.log('------url------' + url);
            let params = [manageUrl, token, menuId, time].join(';');
            this.getEncrypte();
            let paramString = encryption(params, this.encryptedString.key, this.encryptedString.iv);

            if (url.lastIndexOf('?') == -1) {
                url += '?paramString=' + paramString;
            } else {
                url += '&paramString=' + paramString;
            }

            if (url !== null && url !== undefined) {
                this.url = url;
                /*update_begin author:wuxianquan date:20190908 for:判断打开方式，新窗口打开时this.$route.meta.internalOrExternal==true */
                if (route.meta.internalOrExternal != undefined && route.meta.internalOrExternal == true) {
                    window.open(this.url);
                }
                /*update_end author:wuxianquan date:20190908 for:判断打开方式，新窗口打开时this.$route.meta.internalOrExternal==true */
            }
        },
        //获取密码加密规则
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
        // update_end author:sunjianlei date:20191230 for: 解决外部链接打开失败的问题
        /*update_end author:zhaoxin date:20191129 for: 做头部菜单栏导航*/
        //webSocket监测登录情况
        initWebSocket: function() {
            // WebSocket与普通的请求所用协议有所不同，ws等同于http，wss等同于https
            var userId = store.getters.userInfo.id;
            console.log('ACCESS_TOKEN', Vue.ls.get(ACCESS_TOKEN));
            var url =
                window._CONFIG['domianURL'].replace('https://', 'wss://').replace('http://', 'ws://') +
                '/sessionWebSocket/' +
                Vue.ls.get(ACCESS_TOKEN);
            console.log(url);
            this.websock = new WebSocket(url);
            this.websock.onopen = this.websocketOnopen;
            this.websock.onerror = this.websocketOnerror;
            this.websock.onmessage = this.websocketOnmessage;
            this.websock.onclose = this.websocketOnclose;
        },
        websocketOnopen: function() {
            console.log('WebSocket连接成功');
            //心跳检测重置
            this.heartCheck.reset().start();
        },
        websocketOnerror: function(e) {
            console.log('WebSocket连接发生错误', e);
            this.reconnect();
        },
        websocketOnclose: function(e) {
            console.log('websocket 断开 code:' + e.code + ' reason:' + e.reason + ' wasClean:' + e.wasClean);
            console.log('websocket 断开', e);
            this.reconnect();
        },
        websocketOnmessage: function(e) {
            // console.log('-----接收消息-------', e.data);
            let message = JSON.parse(e.data);
            if (message.msgTxt === '心跳响应成功' && message.isRefreshToken === false) {
                console.log('关闭');
                this.websocketclose();
                this.heartCheck.reset();
                this.lockReconnect = true;
            }
        },
        reconnect() {
            var that = this;
            if (that.lockReconnect) return;
            that.lockReconnect = true;
            //没连接上会一直重连，设置延迟避免请求过多
            setTimeout(function() {
                console.info('尝试重连...');
                that.initWebSocket();
                that.lockReconnect = false;
            }, 5000);
        },
        websocketclose() {
            console.log('websocketclose');
            this.websock.close();
        },
        websocketSend(text) {
            // 数据发送
            try {
                this.websock.send(text);
            } catch (err) {
                console.log('send failed (' + err.code + ')');
            }
        },
        heartCheckFun() {
            let that = this;
            //心跳检测,每10s心跳一次
            that.heartCheck = {
                timeout: 10000,
                timeoutObj: null,
                serverTimeoutObj: null,
                reset: function() {
                    clearInterval(this.timeoutObj);
                    //clearTimeout(this.serverTimeoutObj);
                    return this;
                },
                start: function() {
                    let self = this;
                    this.timeoutObj = setInterval(function() {
                        //这里发送一个心跳，后端收到后，返回一个心跳消息，
                        //onmessage拿到返回的心跳就说明连接正常
                        that.websocketSend('HeartBeat');
                        // console.info('客户端发送心跳');
                        //self.serverTimeoutObj = setTimeout(function(){//如果超过一定时间还没重置，说明后端主动断开了
                        //  that.websock.close();//如果onclose会执行reconnect，我们执行ws.close()就行了.如果直接执行reconnect 会触发onclose导致重连两次
                        //}, self.timeout)
                    }, this.timeout);
                }
            };
        }
    }
};
</script>

<style lang="less" scoped>
/* update_begin author:zhaoxin date:20191129 for: 让搜索框颜色能随主题颜色变换*/
/* update-begin author:sunjianlei date:20191220 for: 解决全局样式冲突问题 */
.user-wrapper .search-input {
    width: 180px;
    color: inherit;

    /deep/ .ant-select-selection {
        background-color: inherit;
        border: 0;
        border-bottom: 1px solid white;
        &__placeholder,
        &__field__placeholder {
            color: inherit;
        }
    }
}
/* update-end author:sunjianlei date:20191220 for: 解决全局样式冲突问题 */
/* update_end author:zhaoxin date:20191129 for: 让搜索框颜色能随主题颜色变换*/
</style>

<style scoped>
.logout_title {
    color: inherit;
    text-decoration: none;
}
</style>
