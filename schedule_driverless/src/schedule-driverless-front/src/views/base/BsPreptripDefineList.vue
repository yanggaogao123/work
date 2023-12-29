<template>
    <a-card :bordered="false">
        <!-- 预案管理页面 -->
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery">
                <a-row :gutter="24">
                    <a-col :xl="4" :lg="7" :md="8" :sm="24">
                        <a-form-item label="预案类型">
                            <!--              <a-input placeholder="请输入预案类型" v-model="queryParam.taskTypeName"></a-input>-->
                            <a-select
                                @change="handleChange"
                                v-model="queryParam.taskTypeId"
                                :filterOption="filterOption"
                                allowClear
                                show-search
                            >
                                <a-select-option v-for="item in dictOptions" :key="item.id" :value="item.value">
                                    {{ item.type }}
                                </a-select-option>
                            </a-select>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="4" :lg="7" :md="8" :sm="24">
                        <a-form-item label="所属线路">
                            <GCIRouteSelect
                                placeholder="请选择线路"
                                v-model="queryParam.routeId"
                                @change="changeRoute"
                                mode="mode"
                            ></GCIRouteSelect>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="4" :lg="7" :md="8" :sm="24">
                        <a-form-item label="预案名称">
                            <a-input placeholder="请输入预案名称" v-model="queryParam.keytitle"></a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="4" :lg="7" :md="8" :sm="24">
                        <a-form-item label="状态">
                            <j-dict-select-tag
                                v-model="queryParam.status"
                                placeholder="请输入状态"
                                dictCode="preptrip_status"
                            />
                        </a-form-item>
                    </a-col>
                </a-row>
            </a-form>
        </div>
        <!-- 查询区域-END -->

        <!-- 操作按钮区域 -->
        <div class="table-operator">
            <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
            <a-button type="primary" @click="searchReset" icon="reload">重置</a-button>
            <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
            <a-button @click="handleStatus(1)" type="primary" icon="">启用</a-button>
            <a-button @click="handleStatus(2)" type="primary" icon="">停用</a-button>
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
                :rowKey="
                    (record, index) => {
                        return record.tripdefineId;
                    }
                "
                :columns="columns"
                :dataSource="dataSource"
                :pagination="ipagination"
                :loading="loading"
                @expand="open"
                expandRowByClick
                :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
                class="j-table-force-nowrap"
                @change="handleTableChange"
            >
                <template #status>
                    <a-tooltip>
                        <template slot="title">
                            <div>问：为什么预案的状态有时会自动变成停用状态</div>
                            <div>
                                答：修改过子线路或线路服务途径站点会导致预案明细的首末站发生变化，系统是会自动把预案状态更改为停用的，需要重新修改首末站后手动启用预案即可
                            </div>
                        </template>
                        <a-icon type="question-circle" />
                        状态
                    </a-tooltip>
                </template>
                <a-table
                    slot="expandedRowRender"
                    slot-scope="record"
                    :columns="innerColumns"
                    :data-source="innerData[record.tripdefineId]"
                    :pagination="false"
                >
                </a-table>

                <span slot="action" slot-scope="text, record" @click.stop="">
                    <a @click="handleEdit(record)">编辑</a>

                    <a-divider type="vertical" />
                    <a-dropdown>
                        <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
                        <a-menu slot="overlay">
                            <a-menu-item>
                                <a v-if="record.status == '0'" @click="updateStatus(record)">启用</a>
                                <a v-if="record.status == '1'" @click="updateStatus(record)">停用</a>
                            </a-menu-item>
                        </a-menu>
                    </a-dropdown>
                </span>
            </a-table>
        </div>

        <bsPreptripDefine-modal ref="modalForm" @ok="modalFormOk"></bsPreptripDefine-modal>
    </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less';
import { mixinDevice } from '@/utils/mixin';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import BsPreptripDefineModal from './modules/BsPreptripDefineModal';
import { httpAction } from '@/api/manage';
import { getAction, postAction } from '../../api/manage';
import GCIRouteSelect from '@/components/gci/GCIRouteSelect';

export default {
    name: 'BsPreptripDefineList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
        BsPreptripDefineModal,
        GCIRouteSelect
    },
    data() {
        return {
            description: '预案管理管理页面',
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
                    ellipsis: true,
                    sorter: true,
                    customRender: function(text, record) {
                        return record.organName;
                    }
                },
                {
                    title: '线路',
                    align: 'center',
                    dataIndex: 'routeId',
                    ellipsis: true,
                    sorter: true,
                    customRender: function(text, record) {
                        return record.routeName;
                    }
                },
                {
                    title: '预案名称',
                    align: 'center',
                    dataIndex: 'keytitle',
                    ellipsis: true,
                    sorter: true
                },
                {
                    title: '预案类型',
                    align: 'center',
                    dataIndex: 'taskTypeName',
                    ellipsis: true,
                    sorter: true
                },
                {
                    // title: '状态',
                    align: 'center',
                    dataIndex: 'status',
                    sorter: true,
                    customRender: function(text) {
                        return text === '1' ? '启用' : '停用';
                    },
                    slots: {
                        title: 'status'
                    }
                },
                {
                    title: '下发调度内容',
                    align: 'center',
                    dataIndex: 'ordercontent',
                    ellipsis: true,
                    sorter: true
                },
                {
                    title: '修改人',
                    align: 'center',
                    dataIndex: 'updateBy',
                    ellipsis: true,
                    sorter: true
                },
                {
                    title: '修改时间',
                    align: 'center',
                    dataIndex: 'updateTime',
                    ellipsis: true,
                    sorter: true
                },
                {
                    title: '操作',
                    dataIndex: 'action',
                    align: 'center',
                    // fixed:"right",
                    width: 147,
                    scopedSlots: { customRender: 'action' }
                }
            ],
            innerColumns: [
                {
                    title: '类别',
                    align: 'center',
                    dataIndex: 'serviceName',
                    ellipsis: true
                },
                {
                    title: '方向',
                    align: 'center',
                    dataIndex: 'direction',
                    customRender: function(text) {
                        let str = '无方向';
                        if (!text) {
                            return str;
                        }
                        if (text === '0') {
                            str = '上行';
                        } else if (text === '1') {
                            str = '下行';
                        }
                        return str;
                    }
                },
                {
                    title: '触发号',
                    align: 'center',
                    dataIndex: 'serviceNumber',
                    ellipsis: true
                },
                {
                    title: '首站',
                    align: 'center',
                    dataIndex: 'firstStationName',
                    ellipsis: true
                },
                {
                    title: '末站',
                    align: 'center',
                    dataIndex: 'lastStationName',
                    ellipsis: true
                },

                {
                    title: '里程',
                    align: 'center',
                    dataIndex: 'mileage',
                    ellipsis: true
                },
                {
                    title: '间隔',
                    align: 'center',
                    dataIndex: 'interval',
                    ellipsis: true
                }
            ],
            innerData: [],
            url: {
                list: '/base/bsPreptripDefine/list',
                updateStatus: '/base/bsPreptripDefine/updateStatus',
                updateStatuses: '/base/bsPreptripDefine/updateStatuses',
                getBsPreptripList: '/base/bsPreptrip/list',
                getDictItem: '/base/bsPreptripTypeAlias/getTaskTypeList'
            },
            dictOptions: {},
            openRecord: {},
            openExpand: false
        };
    },
    computed: {
        importExcelUrl: function() {
            return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
        }
    },
    created() {
        this.dictOptions = [];
        getAction(this.url.getDictItem).then(res => {
            if (res.success) {
                this.dictOptions = res.result;
            }
        });
    },
    methods: {
        initDictConfig() {},
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
            params.isQuery = true;
            this.loading = true;
            postAction(this.url.list, params)
                .then(res => {
                    if (res.success) {
                        this.dataSource = res.result.records;
                        this.ipagination.total = res.result.total;
                    } else {
                        this.$message.warning(res.message);
                    }
                    this.loading = false;
                })
                .finally(() => {
                    this.loading = false;
                });
            this.onClearSelected();
        },
        handleChange(e) {
            this.queryParam.taskTypeId = e;
        },
        updateStatus(r) {
            if (r.status === '0') {
                r.status = '1';
            } else {
                r.status = '0';
            }
            httpAction(this.url.updateStatus, r, 'post')
                .then(res => {
                    if (res.success) {
                        this.$message.success(res.message);
                    } else {
                        this.$message.warning(res.message);
                    }
                })
                .finally(() => {
                    this.searchQuery();
                });
        },
        // 表格行的交互
        rowClick(record, index) {
            // console.log(record,index)
            return {
                props: {
                    scroll: {
                        x: '100%'
                    } // 属性
                },
                on: {
                    click: () => {
                        // 点击
                        console.log(record);
                        // this.open(true, record.tripdefineId)
                    },
                    dblclick: () => {
                        // 双击
                        // console.log(record, index)
                    },
                    mouseleave: event => {
                        // let changeCol = that.data.filter(item => item.editable)
                        // if(changeCol.length){
                        //   changeCol[0].editable = false
                        // }
                    }
                }
            };
        },
        open(expand, record) {
            // console.log(expand, record);
            if (expand) {
                this.openExpand = expand;
                this.openRecord = record;
                const param = { tripdefineId: record.tripdefineId, pageNo: 1, pageSize: 100 };
                postAction(this.url.getBsPreptripList, param).then(res => {
                    if (res.success) {
                        this.innerData = {
                            ...this.innerData,
                            [record.tripdefineId]: res.result.records
                        };
                    }
                    console.log(this.innerData);
                    this.loading = false;
                });
            } else {
                this.openExpand = expand;
                this.openRecord = '';
                delete this.innerData[record.tripdefineId];
            }
        },
        modalFormOk() {
            // 新增/修改 成功时，重载列表
            this.loadData();
            if (this.openExpand) {
                const param = { tripdefineId: this.openRecord.tripdefineId, pageNo: 1, pageSize: 100 };
                postAction(this.url.getBsPreptripList, param).then(res => {
                    if (res.success) {
                        this.innerData = {
                            ...this.innerData,
                            [this.openRecord.tripdefineId]: res.result.records
                        };
                    }
                    console.log(this.innerData);
                    this.loading = false;
                });
            }
        },
        filterOption(input, option) {
            return option.componentOptions.children[0].text.indexOf(input) >= 0;
        },
        changeRoute(value) {
            console.log(value);
            this.queryParam.routeId = value;
        },
        searchReset() {
            this.queryParam = {};
            this.loadData(1);
        },
        handleStatus(status) {
            if (this.selectedRowKeys.length <= 0) {
                this.$message.warning('请选择一条记录！');
                return;
            }
            let params = {};
            params.tripdefineIds = this.selectedRowKeys;
            params.status = status;

            const that = this;
            this.$confirm({
                title: status === 1 ? '确认启用' : '确认停用',
                content: status === 1 ? '是否启用选中预案?' : '是否停用选中预案?',
                onOk: function() {
                    postAction(that.url.updateStatuses, params).then(res => {
                        if (res.success) {
                            that.$message.success(res.message);
                            that.loadData();
                        } else {
                            that.$message.warning(res.message);
                        }
                    });
                }
            });
        }
    }
};
</script>
<style scoped>
@import '../../assets/less/common.less';
</style>
