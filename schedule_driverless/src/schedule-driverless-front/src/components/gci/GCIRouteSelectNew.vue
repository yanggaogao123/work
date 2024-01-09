<template>
    <a-row class="select-box" type="flex" :gutter="8">
        <a-col class="left">
            <a-select
                show-search
                v-model="dataSelect"
                :placeholder="placeholder"
                :mode="mode"
                style="width: 100%;"
                allowClear
                option-filter-prop="children"
                :filter-option="filterOption"
                :open="selectOpen"
                :disabled="disabled"
                @dropdownVisibleChange="handleDropdownVisibleChange"
            >
                <a-select-option
                    v-for="item in routeList"
                    :key="item['' + routeItem + '']"
                    :value="item['' + routeItem + '']"
                >
                    {{ item.routeName }}
                </a-select-option>
            </a-select>
        </a-col>
        <a-col class="right">
            <a-button type="primary" icon="search" :disabled="disabled" @click="openModal">{{
                selectButtonText
            }}</a-button>
        </a-col>
        <!-- <GCIRouteSelectNewModal
            ref="GCIRouteSelectNewModal"
            :visible="visible"
            name="线路"
            :value="ds"
            :valueData="routeList"
            @receive="receiveSth"
            @update="updateSth"
        ></GCIRouteSelectNewModal> -->
        <!-- GCIRouteSelectNewNewModal -->
        <GCIRouteSelectNewNewModal
            ref="GCIRouteSelectNewModal"
            :visible="visible"
            name="线路"
            :value="ds"
            :valueData="routeList"
            @receive="receiveSth"
            @update="updateSth"
        ></GCIRouteSelectNewNewModal>
    </a-row>
</template>
<script>
import { getAction, postAction } from '@/api/manage';
import { queryListRoute, queryPageListRoute } from '@/api/api';
import GCIRouteSelectNewModal from './GCIRouteSelectNewModal';
import GCIRouteSelectNewNewModal from './GCIRouteSelectNewNewModal';

export default {
    name: 'GCIRouteSelectNew',
    props: {
        //mode:默认单选，多选multiple 返回","分隔的字符串
        mode: {
            type: String,
            default: 'mode'
        },
        //item:指定返回线路字段，默认返回线路id字段
        item: {
            type: String,
            default: 'id'
        },
        value: {
            type: [Number, String, Array],
            default: () => []
        },
        placeholder: '',
        allowClear: true,
        width: {
            type: [Number, String],
            default: '100%'
        },
        disabled: {
            type: [Boolean],
            default: false
        },
        selectButtonText: {
            type: String,
            default: '选择'
        },
        send: {}
    },
    components: { GCIRouteSelectNewModal, GCIRouteSelectNewNewModal },
    data() {
        return {
            url: { queryPageListRoute: '/common/sys/queryPageListRoute' },
            routeItem: this.item,
            visible: false,
            dataSelect: undefined,
            ds: undefined,
            route: '',
            routeListAll: [],
            filterList: [], //前端搜索过滤用
            routeList: [],
            selectOpen: false,
            isLoading: false,
            page: 1,
            routeName: ''
        };
    },
    computed: {
        style: function() {
            return 'width:' + this.width + 'px';
        }
    },
    watch: {
        // value: {
        //     deep: true,
        //     immediate: true,
        //     handler(val) {
        //         console.log(val);
        //     }
        // },
        dataSelect: {
            deep: true,
            handler(val) {
                console.log('选择项第二步接收到的值', val);
                this.routeList = val.map(ktem => {
                    return this.routeListAll.filter(item => item[this.item] == ktem);
                });
                this.routeList = [].concat.apply([], this.routeList);

                this.$emit('input', val.toString());
                this.$emit('change', val.toString());
            }
        }
    },
    created() {
        this.dataInit();
    },
    methods: {
        dataInit() {
            let that = this;
            let sendData = {
                routeName: ''
            };
            this.isLoading = true;

            queryListRoute(sendData).then(res => {
                if (res.success) {
                    that.routeListAll = res.result;
                    console.log('routeListAll', that.routeListAll);
                    console.log('that.value', that.value);
                    if (that.value.constructor == Number) {
                        this.dataSelect = that.value;
                        that.routeList = that.routeListAll.filter(item => item[that.item] == that.value);
                    }
                }
            });
        },
        openModal() {
            this.visible = true;
            this.ds = this.dataSelect;
        },
        filterOption(input, option) {
            return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
        },
        handleDropdownVisibleChange() {
            // 解决antdv自己的bug —— open 设置为 false 了，点击后还是添加了 open 样式，导致点击事件失效
            this.selectOpen = true;
            this.$nextTick(() => {
                this.selectOpen = false;
            });
        },
        receiveSth(data) {
            console.log(data);

            let arr = [];
            arr = data.map(item => {
                if (item.routeId) {
                    return item.routeId;
                } else if (!item.routeId && item.id) {
                    return item.id;
                }
            });
            this.dataSelect = arr;
            console.log('选择项接收到的值第一步', this.dataSelect);

            // this.routeList = this.dataSelect.map(ktem => {
            //     return this.routeListAll.filter(item => item[this.item] == ktem);
            // });
            // this.routeList = [].concat.apply([], this.routeList);
            // console.log(this.routeList);

            // console.log('select数据', data);
            // this.$emit('select', data.toString());
            // // this.$emit('receive', arr);
            // this.$emit('input', arr.toString());
            // this.$emit('change', arr.toString());
        },
        updateSth(data) {
            this.visible = data;
        }
    }
};
</script>
<style lang="less" scoped>
.select-box {
    @width: 82px;

    .left {
        width: calc(100% - @width - 8px);
    }

    .right {
        width: @width;
    }

    .full {
        width: 100%;
    }

    /deep/ .ant-select-search__field {
        display: none !important;
    }
}
</style>
