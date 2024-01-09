<template>
    <a-card :bordered="false">
        <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery">
                <a-row :gutter="24">
                    <a-col :xl="6" :lg="6" :md="8" :sm="24">
                        <a-form-item label="所属机构">
                            <GCIOrgTreeSelect
                                v-model="queryParam.orgCode"
                                :isGetOption="true"
                                placeholder="请选择所属机构"
                                @changeOptions="queryListRoute"
                            ></GCIOrgTreeSelect>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="6" :lg="6" :md="8" :sm="24">
                        <a-form-item label="所属线路">
                            <a-select
                                allowClear
                                placeholder="请选择线路"
                                v-model="oprSelect"
                                show-search
                                :filter-option="addFilterOption"
                                @change="handleChangeRoute"
                            >
                                <a-select-option v-for="(item, index) in routeData" :key="index" :value="item.id">
                                    {{ item.routeName }}
                                </a-select-option>
                            </a-select>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="6" :lg="6" :md="8" :sm="24">
                        <a-button type="primary" @click="searchQuery2" icon="search">查询</a-button>
                        <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 10px"
                            >重置</a-button
                        >
                    </a-col>
                </a-row>
                <a-row :gutter="24">
                    <a-col :xl="6" :lg="6" :md="8" :sm="24">
                        <a-form-item label="切换机构">
                            <GCIOrgTreeSelect
                                v-model="swapParam.swapOrgCode"
                                :isGetOption="true"
                                placeholder="请选择机构"
                                @changeOptions="querySwapListRoute"
                            ></GCIOrgTreeSelect>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="6" :lg="6" :md="8" :sm="24">
                        <a-form-item label="切换线路">
                            <a-select
                                labelInValue
                                allowClear
                                placeholder="请选择线路"
                                v-model="swapSelect"
                                show-search
                                :filter-option="addFilterOption"
                                @change="handleSwapRoute"
                            >
                                <a-select-option v-for="(item, index) in swapRouteData" :key="index" :value="item.id">
                                    {{ item.routeName }}
                                </a-select-option>
                            </a-select>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="6" :lg="6" :md="8" :sm="24">
                        <a-button type="primary" @click="swapRoute">切换线路</a-button>
                    </a-col>
                </a-row>
            </a-form>
        </div>
        <!-- table区域-begin -->
        <div>
            <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
                <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择
                <a style="font-weight: 600">{{ selectedRowKeys.length }}</a
                >项
                <a style="margin-left: 24px" @click="onClearSelected">清空</a>
            </div>
        </div>
        <a-table
            ref="table"
            size="middle"
            bordered
            rowKey="busId"
            :columns="columns"
            :dataSource="dataSource"
            :pagination="ipagination"
            :loading="loading"
            :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
            class="j-table-force-nowrap"
            @change="handleTableChange"
        >
            <template #publishStatus>
                <a-tooltip>
                    <template slot="title">
                        <div>问：车辆批量换所属线路后为什么生成的路单所属线路还是在原来线路</div>
                        <div>
                            答：车辆换所属线路后需要重新发布车辆才能生效
                        </div>
                    </template>
                    <a-icon type="question-circle" />
                    发布状态
                </a-tooltip>
            </template>
        </a-table>
    </a-card>
</template>

<!--车辆批量换所属线路-->
<script>
import { mixinDevice } from '@/utils/mixin';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect';
import { getAction, putAction } from '@/api/manage';
import debounce from 'lodash/debounce';
export default {
    name: 'EdBus',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
        GCIOrgTreeSelect
    },
    data() {
        return {
            oprSelect: undefined,
            swapSelect: undefined,
            routeData: [],
            swapRouteData: [],
            queryParam: {},
            swapParam: {},
            // 表头
            columns: [
                {
                    title: '序号',
                    dataIndex: '',
                    key: 'rowIndex',
                    width: 60,
                    align: 'center',
                    customRender: function(t, r, index) {
                        return parseInt(index) + 1;
                    }
                },
                {
                    title: '车辆名称',
                    align: 'center',
                    dataIndex: 'busName'
                },
                {
                    title: '车辆编码',
                    align: 'center',
                    dataIndex: 'busCode'
                },
                {
                    title: '车辆牌照',
                    align: 'center',
                    dataIndex: 'numberPlate'
                },
                {
                    title: '所属机构',
                    align: 'center',
                    dataIndex: 'organName'
                },
                {
                    title: '所属线路',
                    align: 'center',
                    dataIndex: 'routeName'
                },
                {
                    title: '终端ID',
                    align: 'center',
                    dataIndex: 'obuId'
                },
                {
                    title: 'ICCID',
                    align: 'center',
                    dataIndex: 'simCode'
                },
                {
                    title: '终端芯片编号',
                    align: 'center',
                    dataIndex: 'obuChipCode'
                },
                {
                    // title: '发布状态',
                    align: 'center',
                    dataIndex: 'publishStatus',
                    customRender: function(text) {
                        if (text == '0') {
                            return '起草';
                        } else if (text == '4') {
                            return '已发布';
                        }
                    },
                    slots: {
                        title: 'publishStatus'
                    }
                },
                {
                    title: '是否启用',
                    align: 'center',
                    dataIndex: 'isActive',
                    customRender: function(text) {
                        if (text == '0') {
                            return '是';
                        } else if (text == '1') {
                            return '否';
                        }
                    }
                },
                {
                    title: '核定载客量',
                    align: 'center',
                    dataIndex: 'carryCapacity'
                },
                {
                    title: '无障碍设施',
                    align: 'center',
                    dataIndex: 'isFacilities',
                    customRender: function(text) {
                        if (text == 1) {
                            return '有';
                        } else {
                            return '无';
                        }
                    }
                },
                {
                    title: '备注',
                    align: 'center',
                    dataIndex: 'remark'
                }
            ],
            url: {
                list: '/edBus/list',
                listRoute: '/common/sys/queryRouteByOrganId',
                swapRoute: '/edBus/swapRoute'
            }
        };
    },
    methods: {
        searchQuery2() {
            this.isorter = {};
            this.searchQuery();
        },
        searchReset() {
            this.oprSelect = undefined;
            this.swapSelect = undefined;
            this.routeData = [];
            this.swapRouteData = [];
            this.queryParam = {};
            this.swapParam = {};
            this.loadData(1);
        },
        queryListRoute(orgData) {
            if (orgData === undefined) {
                this.queryParam = {};
                this.routeData = [];
                this.oprSelect = undefined;
                return;
            }
            this.queryParam.isLeaf = orgData.isLeaf;
            this.queryParam.organId = orgData.id;
            let params = { organId: this.queryParam.organId, isLeaf: this.queryParam.isLeaf };
            getAction(this.url.listRoute, params).then(res => {
                if (res.success) {
                    this.routeData = res.result;
                }
            });
        },
        handleChangeRoute(value) {
            this.queryParam.routeId = value;
        },
        addFilterOption(input, option) {
            return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
        },
        handleSwapRoute(value) {
            this.swapParam.routeId = value.key;
            // 去掉空格和换行符
            this.swapParam.routeName = value.label.replace(/\ /g, '').replace(/\s/g, '');
        },
        querySwapListRoute(orgData) {
            if (orgData === undefined) {
                this.swapParam = {};
                this.swapRouteData = [];
                this.swapSelect = undefined;
                return;
            }
            this.swapParam.isLeaf = orgData.isLeaf;
            this.swapParam.organId = orgData.id;
            let params = { organId: this.swapParam.organId, isLeaf: this.swapParam.isLeaf };
            getAction(this.url.listRoute, params).then(res => {
                if (res.success) {
                    this.swapRouteData = res.result;
                }
            });
        },
        swapRoute() {
            if (this.selectedRowKeys.length <= 0) {
                this.$message.warning('请选择需要操作的数据');
                return;
            }
            if (!this.swapParam.routeId) {
                this.$message.warning('请选择切换线路');
                return;
            }
            this.$confirm({
                content: (
                    <div>
                        确定将选中车辆的所属线路修改为<span style="color:red">{this.swapParam.routeName}</span>吗?
                    </div>
                ),
                onOk: () => {
                    this.handleSwapRouteOk();
                }
            });
        },
        handleSwapRouteOk() {
            let params = { busIds: this.selectedRowKeys, routeId: this.swapParam.routeId };
            putAction(this.url.swapRoute, params).then(res => {
                if (res.success) {
                    this.$message.success('更新成功');
                    this.searchQuery2();
                    this.selectedRowKeys = [];
                } else {
                    this.$message.error(res.message);
                }
            });
        }
    }
};
</script>

<style lang="less" scoped>
.ant-input-number /deep/ .ant-input-number-handler-wrap {
    visibility: hidden;
}
</style>
