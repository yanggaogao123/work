<template>
    <!-- 考勤间隔管理 -->
    <a-card :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery">
                <a-row :gutter="24">
                    <a-col :xl="5" :lg="7" :md="8" :sm="24">
                        <a-form-item label="企业">
                            <GCIOrgTreeSelect
                                v-model="queryParam.organId"
                                placeholder="请选择企业"
                                :isGetOption="isGetOption"
                                :selectType="selectType"
                            ></GCIOrgTreeSelect>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="4" :lg="7" :md="8" :sm="24">
                        <a-form-item label="考勤间隔启用状态">
                            <a-select placeholder="" v-model="queryParam.status" allow-clear>
                                <!-- <a-select-option value="-1">全部</a-select-option> -->
                                <a-select-option value="0">启用</a-select-option>
                                <a-select-option value="1">停用</a-select-option>
                            </a-select>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="6" :lg="7" :md="8" :sm="24">
                        <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                            <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                            <a-button type="primary" @click="add" icon="add" style="margin-left: 8px">添加</a-button>
                            <a-button type="primary" @click="exportData" icon="download" style="margin-left: 8px"
                                >导出</a-button
                            >
                        </span>
                    </a-col>
                </a-row>
            </a-form>
        </div>
        <!-- 操作按钮区域 -->
        <a-table
            ref="table"
            size="middle"
            bordered
            rowKey="employeeId"
            :columns="columns"
            :dataSource="dataSource"
            :pagination="ipagination"
            :loading="loading"
            :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
            class="j-table-force-nowrap"
            @change="handleTableChange"
        >
            <span slot="action" slot-scope="text, record">
                <a-button type="primary" @click="edit(record)" style="margin-left: 8px">编辑</a-button>
                <a-popconfirm title="确定删除？" @confirm="deleteById(record)">
                    <a-button type="primary" style="margin-left: 8px">删除</a-button>
                </a-popconfirm>
                <a-popconfirm title="确定启用？" @confirm="updateStatus(record, '0')" v-if="record.status === '1'">
                    <a-button type="primary" style="margin-left: 8px">启用</a-button>
                </a-popconfirm>
                <a-popconfirm title="确定停止？" @confirm="updateStatus(record, '1')" v-if="record.status === '0'">
                    <a-button type="primary" style="margin-left: 8px">停止</a-button>
                </a-popconfirm>
            </span>
        </a-table>

        <BsAtndInvlOrganModal ref="bsAtndInvlOrganModal" @ok="modalFormOk" v-show="showModal"></BsAtndInvlOrganModal>
    </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less';
import { mixinDevice } from '@/utils/mixin';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import JSearchSelectTag from '@/components/dict/JSearchSelectTag';
import JMultiSelectTag from '@/components/dict/JMultiSelectTag';
import GCIRouteSelect from '@/components/gci/GCIRouteSelect';
import BsAtndInvlOrganModal from './modules/BsAtndInvlOrganModal';
import { postAction } from '@/api/manage';
import GCIOrgTreeSelect from '../../components/gci/GCIOrgTreeSelect';

export default {
    name: 'BsAtndInvlOrgan',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
        GCIOrgTreeSelect,
        JSearchSelectTag,
        JMultiSelectTag,
        GCIRouteSelect,
        BsAtndInvlOrganModal
    },
    data() {
        return {
            isGetOption: true,
            selectType: 'id',
            showModal: false,
            selectValue: '',
            description: '考勤间隔管理',
            selectionRows: [],
            handleResult: '',
            handleResultVisible: false,
            uploadOrgCode: '',
            /* 排序参数 */
            isorter: {
                column: 'updateTime',
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
                    title: '操作',
                    dataIndex: 'action',
                    align: 'center',
                    // fixed:"right",
                    width: 147,
                    scopedSlots: { customRender: 'action' }
                },
                {
                    title: '企业',
                    align: 'center',
                    dataIndex: 'organId',
                    customRender: function(text, record) {
                        return record.organId_dictText;
                    }
                },
                {
                    title: '线路',
                    align: 'center',
                    dataIndex: 'routeId',
                    customRender: function(text, record) {
                        return record.routeName;
                    }
                },
                {
                    title: '考勤时间间隔(小时)',
                    align: 'center',
                    dataIndex: 'atndInvlDaily'
                },
                {
                    title: '可连续上班天数(天)',
                    align: 'center',
                    dataIndex: 'serialWorkDays'
                },
                {
                    title: '允许误差(分钟)',
                    align: 'center',
                    dataIndex: 'allowRange'
                },
                {
                    title: '启用状态',
                    align: 'center',
                    dataIndex: 'status',
                    customRender: function(text, record) {
                        return text === '0' ? '启用' : '停止';
                    }
                },
                {
                    title: '添加日期',
                    align: 'center',
                    dataIndex: 'createTime'
                },
                {
                    title: '启用日期',
                    align: 'center',
                    dataIndex: 'updateTime'
                },
                {
                    title: '添加人',
                    align: 'center',
                    dataIndex: 'createBy'
                }
            ],
            url: {
                list: '/base/bsAtndInvlOrgan/list',
                updateStatus: '/base/bsAtndInvlOrgan/updateStatus',
                deleteById: '/base/bsAtndInvlOrgan/deleteById',
                exportXlsUrl: '/base/bsAtndInvlOrgan/export'
            },
            width: 600,
            confirmLoading: false,
            importVisible: false,
            format: false,
            fileList: [],
            dictOptions: {}
        };
    },
    computed: {
        importExcelUrl: function() {
            return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
        }
    },
    methods: {
        loadData(arg) {
            if (!this.url.list) {
                this.$message.error('请设置url.list属性!');
                return;
            }
            // 加载数据 若传入参数1则加载第一页的内容
            if (arg === 1) {
                this.ipagination.current = 1;
            }
            const params = this.getQueryParams(); // 查询条件
            this.loading = true;
            postAction(this.url.list, params).then(res => {
                if (res.success) {
                    this.dataSource = res.result.records;
                    this.ipagination.total = res.result.total;
                }
                if (res.code === 510) {
                    this.$message.warning(res.message);
                }
                this.loading = false;
            });
        },
        // modalFormOk() {
        //   this.loadData()
        // },
        add() {
            console.log('add');
            this.showModal = true;
            this.$refs.bsAtndInvlOrganModal.add();
        },
        edit(record) {
            this.showModal = true;
            console.log('record', record);
            this.$refs.bsAtndInvlOrganModal.edit(record);
        },
        deleteById(record) {
            postAction(this.url.deleteById, { id: record.id }).then(res => {
                if (res.success) {
                    this.$message.success(res.message);
                    this.loadData();
                } else if (res.code !== 200) {
                    this.$message.warning(res.message);
                }
                this.loading = false;
            });
        },
        handleRemove(file) {
            const index = this.fileList.indexOf(file);
            const newFileList = this.fileList.slice();
            newFileList.splice(index, 1);
            this.fileList = newFileList;
        },
        handleCancel() {
            this.importVisible = false;
            this.handleResultVisible = false;
        },
        updateStatus(record, status) {
            postAction(this.url.updateStatus, { id: record.id, status: status }).then(res => {
                if (res.success) {
                    this.$message.success(res.message);
                    this.loadData();
                } else if (res.code !== 200) {
                    this.$message.warning(res.message);
                }
                this.loading = false;
            });
        },
        // 导入
        importData(title) {
            this.importVisible = true;
            this.fileList = [];
        },
        exportData() {
            this.queryParam = this.getQueryParams(); // 查询条件
            this.handleExportXls('考勤间隔');
        },
        handleResultOkItem() {
            this.handleResultVisible = false;
            this.importData();
        },
        initDictConfig() {},
        beforeUpload(file) {
            console.log(file);
            const excel = file.type.indexOf('excel') > -1;
            const sheet = file.type.indexOf('sheet') > -1;
            if (!excel && !sheet) {
                this.$message.warning('请选择excel!');
                this.format = false;
                return false;
            }
            this.format = true;
            // 只允许上传一个文件
            this.fileList = [];
            this.fileList = [...this.fileList, file];

            return true;
        },
        handleOkItem() {
            if (!this.format) {
                this.$message.warning('请选择文件!');
                return;
            }
            const { fileList } = this;
            const formData = new FormData();
            fileList.forEach(file => {
                formData.append('file', file);
            });
            postAction(this.url.importExcelUrl, formData)
                .then(res => {
                    if (res.success) {
                        this.format = false;
                        this.fileList = [];
                        this.handleResult = res.message;
                        this.importVisible = false;
                        this.handleResultVisible = true;
                    } else {
                        this.format = false;
                        this.handleResult = res.message;
                        this.importVisible = false;
                        this.handleResultVisible = true;
                    }
                })
                .finally(() => {
                    this.format = false;
                });
        }
    }
};
</script>
<style scoped>
@import '../../assets/less/common.less';
</style>
