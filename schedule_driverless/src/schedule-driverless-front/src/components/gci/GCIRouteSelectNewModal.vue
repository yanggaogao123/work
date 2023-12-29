<template>
    <a-modal
        centered
        :title="name + '选择'"
        :width="width"
        :visible="visible"
        @ok="handleOk"
        @cancel="close"
        cancelText="关闭"
        :destroyOnClose="true"
        :maskClosable="false"
    >
        <a-row :gutter="18">
            <a-col :span="16">
                <!-- 查询区域 -->
                <div class="table-page-search-wrapper">
                    <a-form layout="inline">
                        <a-row :gutter="24">
                            <a-col :span="14">
                                <a-form-item label="线路">
                                    <a-input
                                        v-model="queryParam.routeName"
                                        placeholder="请输入线路"
                                        @pressEnter="searchQuery"
                                    />
                                </a-form-item>
                            </a-col>
                            <a-col :span="8">
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

                <a-table
                    ref="table"
                    size="small"
                    bordered
                    :rowKey="
                        (record, index) => {
                            return record.routeId;
                        }
                    "
                    :columns="columns"
                    :dataSource="dataSource"
                    :pagination="ipagination"
                    :loading="loading"
                    :scroll="{ y: 240 }"
                    :rowSelection="{ selectedRowKeys, onChange: onSelectChange, type: multiple ? 'checkbox' : 'radio' }"
                    @change="handleTableChange"
                >
                </a-table>
            </a-col>
            <a-col :span="8">
                <a-card
                    :title="'已选' + name"
                    :bordered="false"
                    :head-style="{ padding: 0 }"
                    :body-style="{ padding: 0 }"
                >
                    <a-table
                        size="small"
                        bordered
                        :rowKey="
                            (record, index) => {
                                return index;
                            }
                        "
                        :pagination="false"
                        :scroll="{ y: 240 }"
                        :columns="selectColumns"
                        :dataSource="selectDataSource"
                    >
                        <span slot="action" slot-scope="text, record, index">
                            <a @click="handleDeleteSelected(record, index)">删除</a>
                        </span>
                    </a-table>
                </a-card>
            </a-col>
        </a-row>
    </a-modal>
</template>

<script>
import { getAction } from '@/api/manage';
// import Ellipsis from '@/components/Ellipsis'
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
// import { cloneObject, pushIfNotExist } from '@/utils/util'
// import { queryListRoute, queryPageListRoute } from '@/api/api'

export default {
    name: 'GCIRouteSelectNewModal',
    mixins: [JeecgListMixin],
    // components: { Ellipsis },
    props: {
        value: {
            type: [Array, Number, String],
            default: () => []
        },
        visible: {
            type: Boolean,
            default: false
        },
        valueData: {
            // type: [Array, String, Number]
            // required: true
        },
        multiple: {
            type: Boolean,
            default: true
        },
        width: {
            type: Number,
            default: 1000
        },

        name: {
            type: String,
            default: ''
        }
    },
    data() {
        return {
            loading: false,
            url: { list: '/common/sys/queryPageListRoute' },
            /* 分页参数 */
            ipagination: {
                current: 1,
                pageSize: 5,
                pageSizeOptions: ['5', '10', '20', '30'],
                showTotal: (total, range) => {
                    return range[0] + '-' + range[1] + ' 共' + total + '条';
                },
                showQuickJumper: true,
                showSizeChanger: true,
                total: 0
            },
            options: [],
            dataSourceMap: {},
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
                    title: '线路名',
                    align: 'center',
                    dataIndex: 'routeName'
                },
                {
                    title: '线路ID',
                    align: 'center',
                    dataIndex: 'routeId'
                }
            ],
            queryParam: {},
            selectedRowKeys: [],
            oldSelect: [],
            selectionRows: [],
            selectColumns: [
                {
                    title: '线路名',
                    align: 'center',
                    dataIndex: 'routeName'
                },
                {
                    title: '操作',
                    dataIndex: 'action',
                    align: 'center',
                    width: 60,
                    scopedSlots: { customRender: 'action' }
                }
            ],
            selectDataSource: []
        };
    },
    computed: {
        // 表头
        innerColumns() {
            let columns = cloneObject(this.columns);
            columns.forEach(column => {
                // 给所有的列加上过长裁剪
                if (this.ellipsisLength !== -1) {
                    column.customRender = text => this.renderEllipsis(text);
                }
            });
            return columns;
        }
    },
    watch: {
        value: {
            deep: true,
            immediate: true,
            handler(val) {
                console.log('接收的routeId', val);
                if (val.length < 1) {
                    this.selectedRowKeys = [];
                    this.oldSelect = [];
                    this.selectDataSource = [];
                    // this.selectionRows = [];
                }
            }
        },
        valueData: {
            deep: true,
            handler(val) {
                console.log('接收的列表数据', val);
                this.selectDataSource = val;
                this.selectedRowKeys = val.map(item => {
                    if (item.routeId) {
                        return item.routeId;
                    } else if (!item.routeId && item.id) {
                        return item.id;
                    }
                });
                this.oldSelect = JSON.parse(JSON.stringify(this.selectedRowKeys));
                console.log(this.oldSelect);
            }
        },
        selectedRowKeys() {
            // console.log('selectedRowKeys', this.selectedRowKeys);
            // let arr = [];
            // this.selectDataSource.forEach((item, i) => {
            //   this.selectedRowKeys.forEach((ktem, k) => {
            //     if (item.routeId) {
            //       if (item.routeId == ktem) {
            //         arr.push(item)
            //       }
            //     } else if (!item.routeId && item.id) {
            //       if (item.id == ktem) {
            //         arr.push(item)
            //       }
            //     }
            //   })
            // })
            // this.selectDataSource = arr
            // if (this.selectedRowKeys.length < 1) {
            //   if (this.selectDataSource.length < 1) {
            //     return
            //   }
            //   this.selectedRowKeys = this.selectDataSource.map(item => {
            //     if (item.routeId) {
            //       return item.routeId
            //     } else if (!item.routeId && item.id) {
            //       return item.id
            //     }
            //   })
            // }
        },
        selectionRows() {
            // console.log('selectedRows', this.selectionRows);
            // this.selectDataSource = this.selectionRows
        }
    },

    methods: {
        /** 关闭弹窗 */
        close() {
            this.$emit('update', false);
        },
        handleOk() {
            console.log('确定按钮的数据', this.selectDataSource);
            this.$emit('receive', this.selectDataSource);
            this.close();
        },
        onSelectChange(selectedRowKeys, selectedRows) {
            console.log('onSelectChange', selectedRowKeys, selectedRows);
            console.log('this.oldSelect', this.oldSelect);
            // if (selectedRowKeys >= this.oldSelect) {
            this.selectedRowKeys = selectedRowKeys;
            this.selectionRows = selectedRows;
            this.selectDataSource = Array.from(new Set(this.selectDataSource.concat(selectedRows)));
            this.selectDataSource = [...new Map(this.selectDataSource.map(item => [item.routeName, item])).values()];
            // } else {
            // }
            this.selectDataSource = this.selectDataSource.filter(item => {
                for (let i = 0; i < this.selectedRowKeys.length; i++) {
                    if (item.routeId == this.selectedRowKeys[i] || item.id == this.selectedRowKeys[i]) {
                        return item;
                    }
                }
            });
            console.log('选择的列表数据', this.selectDataSource);
        },
        async handleTableChange(pagination, filters, sorter) {
            console.log('触发了');
            // 分页、排序、筛选变化时触发
            // TODO 筛选
            let that = this;
            if (Object.keys(sorter).length > 0) {
                this.isorter.column = sorter.field;
                this.isorter.order = sorter.order == 'ascend' ? 'asc' : 'desc';
            }
            this.ipagination = pagination;

            await this.loadData();
            function countKeys() {
                that.selectedRowKeys = that.selectDataSource.map(item => {
                    if (item.routeId) {
                        return item.routeId;
                    } else if (!item.routeId && item.id) {
                        return item.id;
                    }
                });
                that.oldSelect = JSON.parse(JSON.stringify(that.selectedRowKeys));
            }
            await countKeys();
            // console.log('翻页selectedRowKeys', this.selectedRowKeys);
        },
        handleDeleteSelected(record, index) {
            // console.log(record)
            this.selectDataSource.splice(index, 1);
            this.selectedRowKeys = this.selectDataSource.map(item => {
                if (item.routeId) {
                    return item.routeId;
                } else if (!item.routeId && item.id) {
                    return item.id;
                }
            });
            // this.selectionRows = this.selectDataSource
            // console.log(this.selectDataSource)
        }
    }
};
</script>
<style lang="less" scoped></style>
