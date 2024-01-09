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
                <!-- <div class="table-page-search-wrapper">
                    <a-form layout="inline">
                        <a-row :gutter="24">
                            <a-col :span="14">
                                <a-form-item label="线路">
                                    <a-input v-model="routeName" placeholder="请输入线路" @pressEnter="searchQuery" />
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
                </div> -->
            </a-col>
        </a-row>
        <a-row>
            <a-transfer
                :data-source="dataList"
                show-search
                :target-keys="targetKeys"
                :filter-option="(inputValue, item) => item.title.indexOf(inputValue) !== -1"
                :show-select-all="false"
                @change="onChange"
            >
                <template
                    slot="children"
                    slot-scope="{
                        props: { direction, filteredItems, selectedKeys, disabled: listDisabled },
                        on: { itemSelectAll, itemSelect }
                    }"
                >
                    <a-table
                        :row-selection="
                            getRowSelection({ disabled: listDisabled, selectedKeys, itemSelectAll, itemSelect })
                        "
                        :columns="direction === 'left' ? leftColumns : rightColumns"
                        :data-source="filteredItems"
                        size="small"
                        :style="{ pointerEvents: listDisabled ? 'none' : null }"
                        :custom-row="
                            ({ key, disabled: itemDisabled }) => ({
                                on: {
                                    click: () => {
                                        if (itemDisabled || listDisabled) return;
                                        itemSelect(key, !selectedKeys.includes(key));
                                    }
                                }
                            })
                        "
                    />
                </template>
            </a-transfer>
        </a-row>
    </a-modal>
</template>

<script>
import { getAction } from '@/api/manage';
// import Ellipsis from '@/components/Ellipsis'
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import difference from 'lodash/difference';
// import { cloneObject, pushIfNotExist } from '@/utils/util'
import { queryListRoute, queryPageListRoute } from '@/api/api';

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
            routeName: '',
            dataList: null,
            routeListAll: null,
            targetKeys: [],
            selectedRowKeys: [],
            leftColumns: [
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
            rightColumns: [
                {
                    title: '线路名',
                    align: 'center',
                    dataIndex: 'routeName'
                }
            ]
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
                // if (val.length < 1) {
                //     this.selectedRowKeys = [];
                //     this.oldSelect = [];
                //     this.selectDataSource = [];
                //     // this.selectionRows = [];
                // }
                this.targetKeys = val;
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
        }
    },
    created() {
        this.loadData(1);
    },
    methods: {
        loadData() {
            if (!this.url.list) {
                this.$message.error('请设置url.list属性!');
                return;
            }
            let params = {
                routeName: this.routeName,
                pageSize: 10000,
                pageNo: 1
            }; // 查询条件
            // 加载数据 若传入参数1则加载第一页的内容
            // if (arg === 1) {
            //     this.ipagination.current = 1;
            // }

            this.loading = true;
            // queryListRoute(params)
            //     .then(res => {
            //         if (res.success) {
            //             let data = res.result.map(item => ({
            //                 ...item,
            //                 key: item.id
            //             }));
            //             console.log('组件数据：', data);
            //             this.dataSource = data;
            //             // this.ipagination.total = res.data.total
            //         } else {
            //             this.$message.warning(res.message);
            //         }
            //         this.loading = false;
            //     })
            //     .finally(() => {
            //         this.loading = false;
            //     });
            getAction(this.url.list, params)
                .then(res => {
                    if (res.success) {
                        let data = res.result.records.map(item => ({
                            ...item,
                            key: item.routeId,
                            title: item.routeName
                        }));
                        this.dataList = data;
                        this.routeListAll = data;
                        // this.ipagination.total = res.data.total
                    } else {
                        this.$message.warning(res.message);
                    }
                    this.loading = false;
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        searchQuery() {
            this.loadData();
        },
        searchReset() {
            this.routeName = '';
            this.loadData();
        },
        /** 关闭弹窗 */
        close() {
            this.$emit('update', false);
        },
        handleOk() {
            let that = this;
            console.log('确定按钮的数据', this.targetKeys);
            let arr = that.routeListAll.filter(item => {
                return that.targetKeys.some(ktem => item.routeId == ktem);
            });
            console.log(arr);
            this.$emit('receive', arr);
            this.close();
        },
        onChange(nextTargetKeys) {
            console.log(nextTargetKeys);
            this.targetKeys = nextTargetKeys;
        },

        getRowSelection({ disabled, selectedKeys, itemSelectAll, itemSelect }) {
            // console.log(selectedKeys);
            return {
                getCheckboxProps: item => ({ props: { disabled: disabled || item.disabled } }),
                onSelectAll(selected, selectedRows) {
                    const treeSelectedKeys = selectedRows.filter(item => !item.disabled).map(({ key }) => key);
                    const diffKeys = selected
                        ? difference(treeSelectedKeys, selectedKeys)
                        : difference(selectedKeys, treeSelectedKeys);
                    itemSelectAll(diffKeys, selected);
                },
                onSelect({ key }, selected) {
                    itemSelect(key, selected);
                },
                selectedRowKeys: selectedKeys
            };
        }
    }
};
</script>
<style lang="less" scoped></style>
