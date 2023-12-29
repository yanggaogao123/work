<template>
    <div>
        <a-modal title="车辆选择" v-model="visible" @ok="handleOk" @cancel="handleCancel" centered :width="1200">
            <!--表单搜索区域-->
            <div class="table-page-search-wrapper">
                <a-form layout="inline" @keyup.enter.native="searchQuery">
                    <a-row :gutter="24">
                        <a-col :xl="5" :lg="6" :md="6" :sm="24">
                            <a-form-item label="所属线路">
                                <!--                <GCIRouteSelect placeholder="请选择线路" v-model="queryParam.routeId" :value="queryParam.routeId" mode="mode" @change="fieldChange"></GCIRouteSelect>-->
                                <GCIRouteSelect
                                    v-model="queryParam.routeId"
                                    :value="queryParam.routeId"
                                    placeholder="请选择线路"
                                    @change="fieldChange"
                                ></GCIRouteSelect>
                            </a-form-item>
                        </a-col>
                        <a-col :xl="5" :lg="5" :md="5" :sm="24">
                            <a-form-item label="车辆编码">
                                <a-input
                                    placeholder="请输入编码"
                                    v-model="queryParam.busCode"
                                    @change="fieldChange"
                                ></a-input>
                            </a-form-item>
                        </a-col>
                        <a-col :xl="5" :lg="5" :md="5" :sm="24">
                            <a-form-item label="车辆牌照">
                                <a-input
                                    placeholder="请输入车牌"
                                    v-model="queryParam.numberPlate"
                                    @change="fieldChange"
                                ></a-input>
                            </a-form-item>
                        </a-col>
                        <a-col :xl="5" :lg="5" :md="5" :sm="24">
                            <a-form-item label="终端ID">
                                <a-input
                                    placeholder="请输入终端ID"
                                    v-model="queryParam.obuId"
                                    @change="fieldChange"
                                ></a-input>
                            </a-form-item>
                        </a-col>
                        <a-col :xl="2" :lg="2" :md="2" :sm="24">
                            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                                <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                                <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px"
                                    >重置</a-button
                                >
                            </span>
                        </a-col>
                    </a-row>
                </a-form>
            </div>

            <!--车辆简要信息列表-->
            <a-table
                ref="table"
                size="middle"
                bordered
                rowKey="id"
                :columns="columns"
                :dataSource="dataSource"
                :pagination="ipagination"
                :loading="loading"
                :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange, type: 'radio' }"
                class="j-table-force-nowrap"
                @change="handleTableChange"
                :customRow="click"
            >
                <template slot="htmlSlot" slot-scope="text">
                    <div v-html="text"></div>
                </template>
            </a-table>
        </a-modal>
    </div>
</template>
<script>
import { filterObj } from '@/utils/util';
import GCIRouteSelect from '@/components/gci/GCIRouteSelect';
import { queryBusList } from '@/api/api';

export default {
    name: 'GCIBusSelectWin',
    props: {
        value: String,
        routeId: [String, Number],
        organId: [String, Number]
    },
    components: { GCIRouteSelect },
    data() {
        return {
            isChange: false,
            visible: false,
            loading: false,
            selectedRowKeys: [],
            selectedRows: [],
            selectedBus: {},
            dataSource: [],
            // 查询条件
            queryParam: {},
            ipagination: {
                current: 1,
                pageSize: 10,
                pageSizeOptions: ['10', '20', '30'],
                showTotal: (total, range) => {
                    return range[0] + '-' + range[1] + ' 共' + total + '条';
                },
                showQuickJumper: true,
                showSizeChanger: true,
                total: 0
            },
            isorter: {
                column: 'createTime',
                order: 'desc'
            },
            columns: [
                {
                    title: '#',
                    dataIndex: '',
                    key: 'rowIndex',
                    width: 60,
                    align: 'center',
                    customRender: function(t, r, index) {
                        return parseInt(index) + 1;
                    }
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
                    title: '车辆名称',
                    align: 'center',
                    dataIndex: 'busName'
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
                    title: '是否启用',
                    align: 'center',
                    dataIndex: 'isActive',
                    customRender: function(text) {
                        return text === '0' ? '是' : '否';
                    }
                }
            ],
            urls: {
                routeList: ''
            }
        };
    },
    created() {
        this.loadData();
    },
    methods: {
        click(record, index) {
            return {
                on: {
                    click: e => {
                        console.log(record, index);
                        this.selectedRowKeys = [index];
                        this.selectedRows = [record];
                        this.selectedBus = record;
                        this.handleOk();
                    }
                }
            };
        },
        fieldChange() {
            this.isChange = true;
        },
        open() {
            this.visible = true;
            this.loadData();
        },
        searchQuery() {
            this.selectedRowKeys = [];
            this.selectionRows = [];
            this.selectedBus = {};
            if (this.isChange) {
                this.ipagination.current = 1;
            }
            this.loadData();
        },
        searchReset() {
            this.selectedRowKeys = [];
            this.selectionRows = [];
            this.selectedBus = {};
            this.routeId = undefined;
            this.organId = undefined;
            this.queryParam = {};
            this.loadData();
        },
        loadData(arg) {
            if (this.routeId && !this.queryParam.routeId) {
                this.queryParam.routeId = this.routeId;
            }
            if (this.organId && !this.queryParam.organId) {
                this.queryParam.organId = this.organId;
            }
            // 加载数据 若传入参数1则加载第一页的内容
            if (arg === 1) {
                this.ipagination.current = 1;
            }
            var params = this.getQueryParams(); // 查询条件
            queryBusList(params).then(res => {
                if (res.success) {
                    this.dataSource = res.result.records;
                    this.ipagination.total = res.result.total;
                }
            });
        },
        getQueryParams() {
            var param = Object.assign({}, this.queryParam, this.isorter);
            param.field = this.getQueryField();
            param.pageNo = this.ipagination.current;
            param.pageSize = this.ipagination.pageSize;
            return filterObj(param);
        },
        getQueryField() {
            // TODO 字段权限控制
        },
        onSelect(record, selected) {
            if (selected === true) {
                this.dataSource2.push(record);
            } else {
                var index = this.dataSource2.indexOf(record);
                // console.log();
                if (index >= 0) {
                    this.dataSource2.splice(this.dataSource2.indexOf(record), 1);
                }
            }
        },
        onSelectChange(selectedRowKeys, selectedRows) {
            console.log(selectedRowKeys, selectedRows);
            this.selectedRowKeys = selectedRowKeys;
            this.selectionRows = selectedRows;
            this.selectedBus = this.selectionRows[0];
            this.handleOk();
            // this.value = this.selectionRows[0].id
        },
        handleTableChange(pagination, filters, sorter) {
            // 分页、排序、筛选变化时触发
            console.log(sorter);
            // TODO 筛选
            if (Object.keys(sorter).length > 0) {
                this.isorter.column = sorter.field;
                this.isorter.order = sorter.order === 'ascend' ? 'asc' : 'desc';
            }
            this.ipagination = pagination;
            this.loadData();
        },
        handleOk() {
            this.queryParam.routeId = undefined;
            this.queryParam.organId = undefined;
            this.visible = false;
            console.log(this.selectedBus);
            this.$emit('input', this.selectedBus.numberPlate);
            this.$emit('ok', this.selectedBus);
        },
        handleCancel() {
            this.queryParam.routeId = undefined;
            this.queryParam.organId = undefined;
            this.visible = false;
        }
    }
};
</script>
<style lang="scss" scoped></style>
