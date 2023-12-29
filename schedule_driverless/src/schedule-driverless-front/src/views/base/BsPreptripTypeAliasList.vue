<template>
    <a-card :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery">
                <a-row :gutter="24">
                    <a-col :xl="6" :lg="7" :md="8" :sm="24">
                        <a-form-item label="预案类型">
                            <a-select @change="handleChange" :filterOption="filterOption" allowClear show-search>
                                <a-select-option v-for="item in dictOptions" :key="item.id" :value="item.value">
                                    {{ item.type }}
                                </a-select-option>
                            </a-select>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="6" :lg="7" :md="8" :sm="24">
                        <a-form-item label="预案别名">
                            <a-input placeholder="请输入预案别名" v-model="queryParam.taskTypeAlias"></a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="6" :lg="7" :md="8" :sm="24">
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
        <!-- 查询区域-END -->

        <!-- 操作按钮区域 -->
        <div class="table-operator">
            <a-button @click="handleAdd" type="primary" icon="plus" v-show="isTopCompany">新增</a-button>
            <a-dropdown v-if="selectedRowKeys.length > 0" v-show="isTopCompany">
                <a-menu slot="overlay">
                    <a-menu-item key="1" @click="batchDel"><a-icon type="delete" />删除</a-menu-item>
                </a-menu>
                <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down"/></a-button>
            </a-dropdown>
        </div>

        <!-- table区域-begin -->
        <div>
            <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
                <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择
                <a style="font-weight: 600">{{ selectedRowKeys.length }}</a
                >项
                <a style="margin-left: 24px" @click="onClearSelected">清空</a>
            </div>

            <a-table
                ref="table"
                size="middle"
                bordered
                rowKey="id"
                :columns="columns"
                :dataSource="dataSource"
                :pagination="ipagination"
                :loading="loading"
                :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
                class="j-table-force-nowrap"
                @change="handleTableChange"
            >
                <span slot="action" slot-scope="text, record" v-show="isTopCompany">
                    <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                        <a>删除</a>
                    </a-popconfirm>
                </span>
            </a-table>
        </div>

        <bsPreptripTypeAlias-modal ref="modalForm" @ok="modalFormOk"></bsPreptripTypeAlias-modal>
    </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less';
import { mixinDevice } from '@/utils/mixin';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import BsPreptripTypeAliasModal from './modules/BsPreptripTypeAliasModal';
import { getAction, postAction } from '../../api/manage';

export default {
    name: 'BsPreptripTypeAliasList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
        BsPreptripTypeAliasModal
    },
    data() {
        return {
            isTopCompany: false,
            description: '预案类别管理管理页面',
            /* 排序参数 */
            isorter: {
                column: 'dateCreated',
                order: 'desc'
            },
            // 表头
            columns: [
                {
                    title: '所属机构',
                    align: 'center',
                    dataIndex: 'organId',
                    sorter: true,
                    customRender: function(text, record) {
                        return record.organName;
                    }
                },
                {
                    title: '预案类型',
                    align: 'center',
                    dataIndex: 'taskTypeName',
                    sorter: true
                },
                {
                    title: '预案别名',
                    align: 'center',
                    dataIndex: 'taskTypeAlias',
                    sorter: true
                },
                {
                    title: '起草人',
                    align: 'center',
                    dataIndex: 'createOperator',
                    sorter: true
                },
                {
                    title: '记录创建时间',
                    align: 'center',
                    dataIndex: 'dateCreated',
                    sorter: true
                },
                {
                    title: '操作',
                    dataIndex: 'action',
                    align: 'center',

                    scopedSlots: { customRender: 'action' }
                }
            ],
            url: {
                list: '/base/bsPreptripTypeAlias/listPreptripTypeAlias',
                delete: '/base/bsPreptripTypeAlias/delete',
                deleteBatch: '/base/bsPreptripTypeAlias/deleteBatch',
                exportXlsUrl: '/base/bsPreptripTypeAlias/exportXls',
                importExcelUrl: 'base/bsPreptripTypeAlias/importExcel',
                getDictItem: '/base/bsPreptripTypeAlias/getTaskTypeList'
            },
            dictOptions: {}
        };
    },
    created() {
        const currOrgCategory = this.$store.getters.userInfo.currOrgCategory;
        console.log('currOrgCategory', currOrgCategory);
        if (currOrgCategory && currOrgCategory === '0') {
            //当前机构属于总公司的机构
            this.isTopCompany = true;
        }
        this.initDictData();
    },
    computed: {
        importExcelUrl: function() {
            return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
        }
    },
    methods: {
        initDictConfig() {},
        searchQuery() {
            this.loadData(1);
        },
        loadData(arg) {
            if (!this.url.list) {
                this.$message.error('请设置url.list属性!');
                return;
            }
            // 加载数据 若传入参数1则加载第一页的内容
            if (arg === 1) {
                this.ipagination.current = 1;
            }
            var params = this.getQueryParams(); // 查询条件
            this.loading = true;
            postAction(this.url.list, params).then(res => {
                if (res.success) {
                    this.dataSource = res.result.records;
                    this.ipagination.total = res.result.total;
                } else {
                    this.$message.warning(res.message);
                }
                this.loading = false;
            });
        },
        handleChange(e) {
            this.queryParam.taskTypeId = e;
        },
        ok() {
            this.searchQuery();
        },
        filterOption(value, option) {
            return option.componentOptions.children[0].text.indexOf(value) >= 0;
        },
        initDictData() {
            this.dictOptions = [];
            getAction(this.url.getDictItem).then(res => {
                if (res.success) {
                    this.dictOptions = res.result;
                }
            });
        }
    }
};
</script>
<style scoped>
@import '../../assets/less/common.less';
</style>
