<template>
    <a-card :bordered="false">
        <!-- 预案查询页面 -->
        <!-- 查询区域 -->
        <div>
            <a-form layout="inline" @keyup.enter.native="searchQuery">
                <a-form-item label="机构名称">
                    <GCIOrgTreeSelect
                        v-model="queryOrgId"
                        placeholder="请选择所属机构"
                        selectType="id"
                        isGetOption="true"
                        style="width:160px;"
                    ></GCIOrgTreeSelect>
                </a-form-item>
                <a-form-item label="线路">
                    <GCIRouteSelect
                        placeholder="请选择线路"
                        v-model="queryParam.routeId"
                        @change="changeRoute"
                        mode="mode"
                        style="width:160px;"
                    ></GCIRouteSelect>
                </a-form-item>
                <a-form-item label="任务名称">
                    <!--              <a-input placeholder="请输入预案类型" v-model="queryParam.taskTypeName"></a-input>-->
                    <a-select
                        @change="handleChange"
                        v-model="queryParam.taskTypeId"
                        :filterOption="filterOption"
                        allowClear
                        show-search
                        style="width:160px;"
                    >
                        <a-select-option v-for="item in dictOptions" :key="item.id" :value="item.value">
                            {{ item.type }}
                        </a-select-option>
                    </a-select>
                </a-form-item>
                <a-form-item label="发送状态">
                    <a-select
                        @change="handleChange"
                        v-model="queryParam.taskTypeId"
                        :filterOption="filterOption"
                        allowClear
                        show-search
                        style="width:160px;"
                    >
                        <a-select-option v-for="item in dictOptions" :key="item.id" :value="item.value">
                            {{ item.type }}
                        </a-select-option>
                    </a-select>
                </a-form-item>
                <a-form-item label="时间范围">
                    <a-range-picker format="YYYY-MM-DD HH:mm:ss" @change="onChange" />
                </a-form-item>

                <a-form-item>
                    <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                    <a-button type="primary" @click="searchReset" icon="reload" style="margin-left:8px;">重置</a-button>
                </a-form-item>
            </a-form>
        </div>
        <!-- 查询区域-END -->

        <!-- 操作按钮区域 -->
        <div class="table-operator">
            <a-button type="primary" @click="showAddModal" icon="plus">新增任务</a-button>
        </div>

        <!-- table区域-begin -->
        <div>
            <!-- <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
                <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择
                <a style="font-weight: 600">{{ selectedRowKeys.length }}</a
                >项
                <a style="margin-left: 24px" @click="onClearSelected">清空</a>
            </div> -->

            <a-table
                ref="table"
                size="middle"
                bordered
                :rowKey="id"
                :columns="columns"
                :dataSource="dataSource"
                :pagination="ipagination"
                :loading="loading"
                @expand="open"
                class="j-table-force-nowrap"
                :scroll="{ x: 1748 }"
                @change="handleTableChange"
            >
                <span slot="status" slot-scope="text, record">
                    <span v-if="record.status == '1'" style="color:#1890FF;">正在发布(5/10)</span>
                    <span v-if="record.status == '2'">正在准备 <a-icon type="file"/></span>
                    <span v-if="record.status == '3'">文件起草 </span>
                    <div v-if="record.status == '4'">
                        <span style="color:#1890FF;">文件已生成 </span>
                        <a-icon type="file" />
                    </div>
                    <span v-if="record.status == '5'" style="color:#1890FF;">已发布(5/10)</span>
                </span>
                <span slot="action" slot-scope="text, record">
                    <div v-if="record.status == '1'">
                        <a-popconfirm title="确定发送？" @confirm="deleteById(record)">
                            <a-button style="margin-left: 8px">文件发送</a-button>
                        </a-popconfirm>
                        <a-button>编辑</a-button>
                        <a-popconfirm title="确定解除？" @confirm="deleteById(record)">
                            <a-button type="danger" style="margin-left: 8px">
                                删除
                            </a-button>
                        </a-popconfirm>
                    </div>
                    <div v-if="record.status == '2'">
                        <a-popconfirm title="确认解除？" @confirm="deleteById(record)">
                            <a-button style="margin-left: 8px">解除发送</a-button>
                        </a-popconfirm>
                        <a-button>编辑</a-button>
                        <a-popconfirm title="确定解除？" @confirm="deleteById(record)">
                            <a-button type="danger" style="margin-left: 8px">
                                删除
                            </a-button>
                        </a-popconfirm>
                    </div>
                </span>
            </a-table>
        </div>

        <Sm-plus-modal ref="modalForm" @ok="modalFormOk"></Sm-plus-modal>
    </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less';
import { mixinDevice } from '@/utils/mixin';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
// import BsPreptripDefineModal from './modules/BsPreptripDefineModal';
import GCIOrgTreeSelect from '../../components/gci/GCIOrgTreeSelect';
import { httpAction } from '@/api/manage';
import { downFile, getAction, postAction } from '../../api/manage';
import GCIRouteSelect from '@/components/gci/GCIRouteSelect';
import SmPlusModal from './modules/SmPlusModal';

export default {
    name: 'SendManagement',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
        GCIRouteSelect,
        GCIOrgTreeSelect,
        SmPlusModal
    },
    data() {
        return {
            queryOrgId: undefined,
            description: '终端文件发送管理',
            /* 排序参数 */
            isorter: {
                column: 'dateCreated',
                order: 'desc'
            },
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
                    title: '任务名称',
                    align: 'center',
                    dataIndex: 'organId',
                    ellipsis: true,
                    // sorter: true,
                    customRender: function(text, record) {
                        return record.organName;
                    }
                },
                {
                    title: '文件所在机构',
                    align: 'center',
                    dataIndex: 'routeCode',
                    ellipsis: true
                    // sorter: true
                },
                {
                    title: '下发任务开始时间',
                    align: 'center',
                    dataIndex: 'routeId',
                    ellipsis: true,
                    // sorter: true,
                    customRender: function(text, record) {
                        return record.routeName;
                    }
                },
                {
                    title: '下发任务截止时间',
                    align: 'center',
                    dataIndex: 'keytitle',
                    ellipsis: true
                    // sorter: true
                },
                {
                    title: '前置条件',
                    align: 'center',
                    dataIndex: 'taskTypeName',
                    ellipsis: true
                    // sorter: true
                },
                {
                    title: '发布文件数',
                    align: 'center',
                    dataIndex: 'ordercontent',
                    ellipsis: true
                    // sorter: true
                },
                {
                    title: '状态/进度',
                    align: 'center',
                    dataIndex: 'status',
                    // sorter: true,
                    scopedSlots: { customRender: 'status' }
                    // customRender: function(text) {
                    //     return text === '1' ? '有效' : '无效';
                    // }
                },

                {
                    title: '操作',
                    dataIndex: 'action',
                    align: 'center',
                    scopedSlots: { customRender: 'action' }
                }
            ],

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

        showAddModal() {
            this.$refs.modalForm.add();
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
.table-operator {
    margin-top: 10px;
}
</style>
