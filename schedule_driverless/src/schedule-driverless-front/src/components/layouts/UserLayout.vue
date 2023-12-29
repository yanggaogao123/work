<template>
    <!-- <div id="userLayout" :class="['user-layout-wrapper', device]"> -->
    <div id="userLayout" class="user-layout-wrapper">
        <div class="container">
            <!-- <div class="top"> -->
            <!-- <div class="header"> -->
            <!-- <a href="/"> -->
            <!-- <img src="~@/assets/logo.svg" class="logo" alt="logo"> -->
            <!-- <span class="title">智能公交调度系统</span> -->
            <!-- </a> -->
            <!-- </div> -->
            <!-- </div> -->

            <!-- <img class="bg-top" src="@assets/login/bg_top1.png" alt="" /> -->

            <route-view></route-view>

            <!-- <div class="footer">
        <div class="copyright">
          Copyright &copy; 2021 <a href="javascript:void(0);" target="">广州交信投科技股份有限公司</a> 出品
        </div>
        <div class="links">
          <a href="javascript:void(0);" target="">联系我们</a>
          <a href="javascript:void(0);" target="">帮助</a>
          <a href="javascript:void(0);" target="">网站地图</a>
        </div>
      </div> -->

            <img class="bg-bottom" src="@assets/login/bg_btm.png" alt="" />
        </div>
    </div>
</template>

<script>
import RouteView from '@/components/layouts/RouteView';
import { mixinDevice } from '@/utils/mixin.js';

export default {
    name: 'UserLayout',
    components: { RouteView },
    mixins: [mixinDevice],
    data() {
        return {};
    },
    mounted() {
        document.body.classList.add('userLayout');
    },
    beforeDestroy() {
        document.body.classList.remove('userLayout');
    }
};
</script>

<style lang="less" scoped>
/deep/#app {
    overflow: auto !important;
}
#userLayout.user-layout-wrapper {
    min-width: 1269px;
    height: 100%;
    // overflow-x: scroll;

    &.mobile {
        .container {
            .main {
                max-width: 368px;
                width: 98%;
            }
        }
    }

    .container {
        width: 100%;
        min-height: 100%;
        // background: #f0f2f5 url(~@/assets/background.svg) no-repeat 50%;
        // background-size: 100%;
        // padding: 110px 0 144px;
        position: relative;
        background: #dbefff;

        a {
            text-decoration: none;
        }

        // .top {
        //   text-align: center;

        //   .header {
        //     height: 44px;
        //     line-height: 44px;

        //     .badge {
        //       position: absolute;
        //       display: inline-block;
        //       line-height: 1;
        //       vertical-align: middle;
        //       margin-left: -12px;
        //       margin-top: -10px;
        //       opacity: 0.8;
        //     }

        //     .logo {
        //       height: 44px;
        //       vertical-align: top;
        //       margin-right: 16px;
        //       border-style: none;
        //     }

        //     .title {
        //       font-size: 33px;
        //       color: rgba(0, 0, 0, 0.85);
        //       font-family: 'Chinese Quote', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB',
        //         'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji',
        //         'Segoe UI Symbol';
        //       font-weight: 600;
        //       position: relative;
        //       top: 2px;
        //     }
        //   }
        //   .desc {
        //     font-size: 14px;
        //     color: rgba(0, 0, 0, 0.45);
        //     margin-top: 12px;
        //     margin-bottom: 40px;
        //   }
        // }
        .bg-top {
            width: 40%;
            position: absolute;
            top: 0;
            left: 0;
            z-index: 4;
        }
        .main {
            position: absolute;
            width: 1269px;
            height: 714px;
            left: 0;
            right: 0;
            top: 0;
            bottom: 0;
            margin: auto;
            // margin: 21% auto 0 auto;
            z-index: 3;
            // border-radius: 24px;
            //   min-width: 260px;
            //   width: 368px;
            //   margin: 0 auto;
        }
        .bg-bottom {
            position: absolute;
            width: 100%;
            bottom: 0;
            left: 0;
            z-index: 2;
        }

        .footer {
            position: absolute;
            width: 100%;
            bottom: 0;
            padding: 0 16px;
            margin: 48px 0 24px;
            text-align: center;

            .links {
                margin-bottom: 8px;
                font-size: 14px;
                a {
                    color: rgba(0, 0, 0, 0.45);
                    transition: all 0.3s;
                    &:not(:last-child) {
                        margin-right: 40px;
                    }
                }
            }
            .copyright {
                color: rgba(0, 0, 0, 0.45);
                font-size: 14px;
            }
        }
    }
}
</style>
