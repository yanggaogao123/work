<template>
    <a-locale-provider :locale="locale">
        <div id="app">
            <router-view />
        </div>
    </a-locale-provider>
</template>
<script>
import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN';
import enquireScreen from '@/utils/device';
//Fontclass模式
import '@/components/iconfont/common.less';
// 使用symbol模式 支持多色
import '@/components/iconfont/iconfont.js';
export default {
    data() {
        return {
            locale: zhCN
        };
    },
    created() {
        let that = this;
        enquireScreen(deviceType => {
            // tablet
            if (deviceType === 0) {
                that.$store.commit('TOGGLE_DEVICE', 'mobile');
                that.$store.dispatch('setSidebar', false);
            }
            // mobile
            else if (deviceType === 1) {
                that.$store.commit('TOGGLE_DEVICE', 'mobile');
                that.$store.dispatch('setSidebar', false);
            } else {
                that.$store.commit('TOGGLE_DEVICE', 'desktop');
                that.$store.dispatch('setSidebar', true);
            }
        });
    }
};
</script>
<style lang="less">
@border: #99bbe8;
@bg: #dbefff;
@antbg: #e8f3fd;
@choicebg: #bae7ff;
#app {
    height: 100%;
}
input {
    background: @antbg !important;
    border: 1px solid @border;
}
.ant-input,
.ant-input-number-input,
.ant-select-selection {
    background: @antbg !important;
    border: 1px solid @border !important;
}

.ant-btn {
    background: @antbg !important;
    border-color: @border !important;
}
.ant-btn-primary,
.ant-btn-background-ghost {
    color: #fff !important;
    background-color: #1890ff !important;
    border-color: #1890ff !important;
}
.ant-pagination-options-quick-jumper input,
.ant-pagination-item-active {
    background: @bg !important;
    border: 1px solid @border !important;
}
.ant-form label {
    // font-weight: 600;
}
.ant-tabs-nav-container {
    // font-weight: bolder !important;
}
.ant-tabs.ant-tabs-card .ant-tabs-card-bar .ant-tabs-tab {
    // font-weight: bolder !important;
}
.ant-modal-header {
    background: @bg !important;
    border-bottom: 1px solid @border !important;
}
.ant-modal-content {
    background: @bg !important;
}
.ant-modal-footer {
    border-top: 1px solid @border !important;
}
.ant-select-dropdown-menu,
.ant-select-dropdown {
    background: @antbg !important;
    scrollbar-face-color: #99bbe8;
    /* 定义滑块 */
    &::-webkit-scrollbar-thumb {
        width: 2px;
        background-color: #99bbe8;
        box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.1);

        &:hover {
            background-color: #dddddd;
        }

        &:active {
            background-color: #bbbbbb;
        }
    }
}
.ant-select-dropdown-menu-item-active {
    background: #bae7ff !important;
}
.ant-select-dropdown,
.ant-select-tree-dropdown .ant-select-dropdown-search {
    background: @antbg !important;
}
.ant-card-head {
    background: @bg !important;
}
.ant-table-small {
    border: 1px solid @border !important;
}
.ant-table-tbody .ant-table-row:nth-child(n) {
    background: #fff !important;
}
.ant-table-tbody .ant-table-row:nth-child(2n) {
    background: #eff6fc !important;
}
.ant-table-thead > tr > th {
    color: #15428b !important;
    font-weight: 600 !important;
    background: #eff6fc !important;
    border-bottom: 1px solid #99bbe8 !important;
    border-right: 1px solid #99bbe8 !important;
}
.ant-table-tbody > tr > td {
    border-bottom: 1px solid #99bbe8 !important;
    border-right: 1px solid #99bbe8 !important;
}
.ant-table-body {
    border-bottom: 1px solid #99bbe8 !important;
}
.ant-table-tbody > tr.ant-table-row-selected td {
    background: @antbg !important;
}
.ant-table-body {
    scrollbar-face-color: #99bbe8;
    /* 定义滑块 */
    &::-webkit-scrollbar-thumb {
        width: 4px;
        background-color: #99bbe8;
        box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.1);

        &:hover {
            background-color: #dddddd;
        }

        &:active {
            background-color: #bbbbbb;
        }
    }
}
.ant-table-content {
    scrollbar-face-color: #99bbe8;
    /* 定义滑块 */
    &::-webkit-scrollbar-thumb {
        background-color: #99bbe8;
        box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.1);

        &:hover {
            background-color: #dddddd;
        }

        &:active {
            background-color: #bbbbbb;
        }
    }
}
.ant-table-bordered .ant-table-body > table {
    border: 1px solid #99bbe8 !important;
    border-right: 0 !important;
    border-bottom: 0 !important;
}
.ant-table-placeholder {
    background: #eff6fc !important;
}
.ant-table-bordered .ant-table-fixed-right table {
    border: 1px solid @border !important;
    border-right: 0;
    border-bottom: 0;
}
.user-wrapper .search-input {
    color: black !important;
}
.ant-select-selection--multiple .ant-select-selection__choice {
    background: #e6f7ff !important;
    border: 1px solid @border !important;
}
.ant-calendar,
.ant-calendar-input {
    background: @antbg !important;
}
.ant-calendar-range .ant-calendar-in-range-cell::before {
    background: @choicebg !important;
}
.ant-dropdown-menu {
    background-color: @antbg !important;
}
.ant-dropdown-menu-item-active {
    background: @choicebg !important;
}
.ant-select-tree li .ant-select-tree-node-content-wrapper:hover {
    background: @choicebg !important;
}
.ant-message-notice-content {
    background: @antbg !important;
}
.ant-popover-inner {
    background: @antbg !important;
}
.ant-calendar-range.ant-calendar-time .ant-calendar-time-picker-combobox {
    background: @antbg !important;
}
li.ant-calendar-time-picker-select-option-selected {
    background: @choicebg !important;
}
.ant-calendar-time-picker-select li:hover {
    background: @choicebg !important;
}
.ant-drawer-header,
.ant-drawer-body,
.ant-drawer-body > div,
.ant-input-group-addon {
    background: @antbg !important;
}
.ant-drawer-header {
    border-bottom: 1px solid @border !important;
}
.ant-drawer-body > div {
    border: 1px solid @border !important;
}
</style>
