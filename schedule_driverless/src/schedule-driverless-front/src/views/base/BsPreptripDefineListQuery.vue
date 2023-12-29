<template>
    <a-card :bordered="false">
        <!-- 预案查询页面 -->
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery">
                <a-row :gutter="24">
                    <a-col :xl="4" :lg="7" :md="8" :sm="24">
                        <a-form-item label="所属机构">
                            <GCIOrgTreeSelect
                                v-model="queryOrgId"
                                placeholder="请选择所属机构"
                                selectType="id"
                                isGetOption="true"
                            ></GCIOrgTreeSelect>
                        </a-form-item>
                    </a-col>
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
                    <!--          <a-col :xl="4" :lg="7" :md="8" :sm="24">-->
                    <!--            <a-form-item label="预案名称">-->
                    <!--              <a-input placeholder="请输入预案名称" v-model="queryParam.keytitle"></a-input>-->
                    <!--            </a-form-item>-->
                    <!--          </a-col>-->
                    <!--          <a-col :xl="4" :lg="7" :md="8" :sm="24">-->
                    <!--            <a-form-item label="状态">-->
                    <!--              <j-dict-select-tag v-model="queryParam.status" placeholder="请输入状态" dictCode="use_status"/>-->
                    <!--            </a-form-item>-->
                    <!--          </a-col>-->
                </a-row>
            </a-form>
        </div>
        <!-- 查询区域-END -->

        <!-- 操作按钮区域 -->
        <div class="table-operator">
            <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
            <a-button type="primary" @click="searchReset" icon="reload">重置</a-button>
            <a-button @click="handleExportXls1('预案查询')" type="primary" icon="download" :loading="downLoading"
                >导出</a-button
            >
            <a-button
                @click="handleExportXls1('预案明细', 'queryDetails')"
                type="primary"
                icon="download"
                :loading="downLoading2"
                >导出(详细)</a-button
            >
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
                <a-table
                    slot="expandedRowRender"
                    slot-scope="record"
                    :columns="innerColumns"
                    :data-source="innerData[record.tripdefineId]"
                    :pagination="false"
                >
                </a-table>
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
import GCIOrgTreeSelect from '../../components/gci/GCIOrgTreeSelect';
import { httpAction } from '@/api/manage';
import { downFile, getAction, postAction } from '../../api/manage';
import GCIRouteSelect from '@/components/gci/GCIRouteSelect';

export default {
    name: 'BsPreptripDefineListQuery',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
        BsPreptripDefineModal,
        GCIRouteSelect,
        GCIOrgTreeSelect
    },
    data() {
        return {
            queryOrgId: undefined,
            description: '预案查询页面',
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
                    title: '所属线路编码',
                    align: 'center',
                    dataIndex: 'routeCode',
                    ellipsis: true,
                    sorter: true
                },
                {
                    title: '所属线路',
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
                    title: '状态',
                    align: 'center',
                    dataIndex: 'status',
                    sorter: true,
                    customRender: function(text) {
                        return text === '1' ? '有效' : '无效';
                    }
                },
                {
                    title: '下发调度内容',
                    align: 'center',
                    dataIndex: 'ordercontent',
                    ellipsis: true,
                    sorter: true
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
                exportXlsUrl: '/base/bsPreptripDefine/exportExcel',
                getBsPreptripList: '/base/bsPreptrip/list',
                getDictItem: '/base/bsPreptripTypeAlias/getTaskTypeList',
                checkParentOrgan: '/base/bsPreptripDefine/checkParentOrgan'
            },
            dictOptions: {},
            downLoading: false,
            downLoading2: false
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
            let params = this.getQueryParams(); // 查询条件
            params.isQuery = true;
            params.queryOrgId = this.queryOrgId;
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

        handleExportXls1(fileName, type) {
            let param = this.getQueryParams(); // 查询条件
            param.isQuery = true;
            param.queryOrgId = this.queryOrgId;
            if (!this.queryOrgId) {
                this.$message.warning('请选择机构');
                return;
            }
            postAction(this.url.checkParentOrgan, param).then(res => {
                if (res.success) {
                    this.handleExportXls(fileName, type);
                } else {
                    this.$message.warning(res.message);
                }
            });
        },

        handleExportXls(fileName, type) {
            let that = this;
            if (!fileName || typeof fileName !== 'string') {
                fileName = '导出文件';
            }

            let param = this.getQueryParams(); // 查询条件
            param.isQuery = true;
            param.queryOrgId = this.queryOrgId;
            if (type === 'queryDetails') {
                param.queryDetails = true;
                that.downLoading2 = true;
            } else {
                that.downLoading = true;
            }
            if (this.selectedRowKeys && this.selectedRowKeys.length > 0) {
                param['selections'] = this.selectedRowKeys.join(',');
            }
            console.log('导出参数', param);
            downFile(this.url.exportXlsUrl, param)
                .then(data => {
                    if (!data) {
                        this.$message.warning('文件下载失败');
                        return;
                    }
                    if (typeof window.navigator.msSaveBlob !== 'undefined') {
                        window.navigator.msSaveBlob(
                            new Blob([data], { type: 'application/vnd.ms-excel' }),
                            fileName + '.xls'
                        );
                    } else {
                        let url = window.URL.createObjectURL(new Blob([data], { type: 'application/vnd.ms-excel' }));
                        let link = document.createElement('a');
                        link.style.display = 'none';
                        link.href = url;
                        link.setAttribute('download', fileName + '.xls');
                        document.body.appendChild(link);
                        link.click();
                        document.body.removeChild(link); // 下载完成移除元素
                        window.URL.revokeObjectURL(url); // 释放掉blob对象
                    }
                })
                .finally(() => {
                    if (type === 'queryDetails') {
                        that.downLoading2 = false;
                    } else {
                        that.downLoading = false;
                    }
                });
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
            if (expand) {
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
                delete this.innerData[record.tripdefineId];
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
        }
    }
};
</script>
<style scoped>
@import '../../assets/less/common.less';
</style>
