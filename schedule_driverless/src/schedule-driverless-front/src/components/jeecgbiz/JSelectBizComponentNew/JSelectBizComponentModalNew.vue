<template>
    <a-modal
        centered
        :title="name + '选择'"
        :width="width"
        :visible="visible"
        @ok="handleOk"
        @cancel="close"
        cancelText="关闭"
    >
        <a-row :gutter="18">
            <a-col :span="16">
                <!-- 查询区域 -->
                <!-- <div class="table-page-search-wrapper">
                    <a-form layout="inline">
                        <a-row :gutter="24">
                            <a-col :span="14">
                                <a-form-item :label="queryParamText || name">
                                    <a-input
                                        v-model="queryParam[queryParamCode || valueKey]"
                                        :placeholder="'请输入' + (queryParamText || name)"
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
import Ellipsis from '@/components/Ellipsis';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import { cloneObject, pushIfNotExist } from '@/utils/util';
import difference from 'lodash/difference';

export default {
    name: 'JSelectBizComponentModalNew',
    mixins: [JeecgListMixin],
    components: { Ellipsis },
    props: {
        value: {
            type: Array,
            default: () => []
        },
        visible: {
            type: Boolean,
            default: false
        },
        valueKey: {
            type: String,
            required: true
        },
        multiple: {
            type: Boolean,
            default: true
        },
        width: {
            type: Number,
            default: 900
        },

        name: {
            type: String,
            default: ''
        },
        listUrl: {
            type: String,
            required: true,
            default: ''
        },
        // 根据 value 获取显示文本的地址，例如存的是 username，可以通过该地址获取到 realname
        valueUrl: {
            type: String,
            default: ''
        },
        displayKey: {
            type: String,
            default: null
        },
        columns: {
            type: Array,
            required: true,
            default: () => []
        },
        // 查询条件Code
        queryParamCode: {
            type: String,
            default: null
        },
        // 查询条件文字
        queryParamText: {
            type: String,
            default: null
        },
        rowKey: {
            type: String,
            default: 'id'
        },
        // 过长裁剪长度，设置为 -1 代表不裁剪
        ellipsisLength: {
            type: Number,
            default: 12
        }
    },
    data() {
        return {
            innerValue: [],
            // 已选择列表
            selectedTable: {
                pagination: false,
                scroll: { y: 240 },
                columns: [
                    {
                        ...this.columns[0],
                        width: this.columns[0].widthRight || this.columns[0].width
                    },
                    {
                        title: '操作',
                        dataIndex: 'action',
                        align: 'center',
                        width: 60,
                        scopedSlots: { customRender: 'action' }
                    }
                ],
                dataSource: []
            },
            renderEllipsis: value => <ellipsis length={this.ellipsisLength}>{value}</ellipsis>,
            url: { list: this.listUrl },
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
            dataList: '',
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
                    title: '姓名',
                    align: 'center',
                    dataIndex: 'realname'
                },
                {
                    title: '帐号',
                    align: 'center',
                    dataIndex: 'username'
                },
                {
                    title: '电话',
                    align: 'center',
                    dataIndex: 'phone'
                },
                {
                    title: '出生日期',
                    align: 'center',
                    dataIndex: 'birthday'
                }
            ],
            rightColumns: [
                {
                    title: '姓名',
                    align: 'center',
                    dataIndex: 'realname'
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
                this.targetKeys = val;
                // this.innerValue = cloneObject(val);
                // this.selectedRowKeys = [];
                // this.valueWatchHandler(val);
                // this.queryOptionsByValue(val);
            }
        }
    },
    created() {
        this.loadData();
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
            getAction(this.url.list, params)
                .then(res => {
                    console.log('用户数据：', res);
                    if (res.success) {
                        let data = res.result.records.map(item => ({
                            ...item,
                            key: item.username,
                            title: item.username
                        }));
                        console.log('处理后数据：', data);
                        this.dataList = data;
                    } else {
                        this.$message.warning(res.message);
                    }
                    this.loading = false;
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        /** 关闭弹窗 */
        close() {
            this.$emit('update:visible', false);
        },

        valueWatchHandler(val) {
            val.forEach(item => {
                this.dataSource.concat(this.selectedTable.dataSource).forEach(data => {
                    if (data[this.valueKey] === item) {
                        pushIfNotExist(this.selectedRowKeys, data[this.rowKey]);
                    }
                });
            });
        },

        queryOptionsByValue(value) {
            if (!value || value.length === 0) {
                return;
            }
            // 判断options是否存在value，如果已存在数据就不再请求后台了
            let notExist = false;
            for (let val of value) {
                let find = false;
                for (let option of this.options) {
                    if (val === option.value) {
                        find = true;
                        break;
                    }
                }
                if (!find) {
                    notExist = true;
                    break;
                }
            }
            if (!notExist) return;
            getAction(this.valueUrl || this.listUrl, {
                // 这里最后加一个 , 的原因是因为无论如何都要使用 in 查询，防止后台进行了模糊匹配，导致查询结果不准确
                [this.valueKey]: value.join(',') + ',',
                pageNo: 1,
                pageSize: value.length
            }).then(res => {
                if (res.success) {
                    let dataSource = res.result;
                    if (!(dataSource instanceof Array)) {
                        dataSource = res.result.records;
                    }
                    this.emitOptions(dataSource, data => {
                        pushIfNotExist(this.innerValue, data[this.valueKey]);
                        pushIfNotExist(this.selectedRowKeys, data[this.rowKey]);
                        pushIfNotExist(this.selectedTable.dataSource, data, this.rowKey);
                    });
                }
            });
        },

        emitOptions(dataSource, callback) {
            dataSource.forEach(data => {
                let key = data[this.valueKey];
                this.dataSourceMap[key] = data;
                pushIfNotExist(this.options, { label: data[this.displayKey || this.valueKey], value: key }, 'value');
                typeof callback === 'function' ? callback(data) : '';
            });
            this.$emit('options', this.options, this.dataSourceMap);
        },

        /** 完成选择 */
        handleOk() {
            // let value = this.selectedTable.dataSource.map(data => data[this.valueKey]);
            let value = this.targetKeys;
            this.$emit('input', value);
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
