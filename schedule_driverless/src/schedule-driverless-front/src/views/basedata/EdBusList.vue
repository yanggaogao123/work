<template>
    <!-- 车辆管理页面 -->
    <a-card :bordered="false">
        <!-- 查询区域 2-->
        <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery">
                <a-row :gutter="24">
                    <a-col :xl="6" :lg="7" :md="8" :sm="24">
                        <a-form-item label="所属机构">
                            <GCIOrgTreeSelect
                                v-model="queryParam.organId"
                                placeholder="请选择所属机构"
                                :selectType="selectType"
                                :isGetOption="isGetOption"
                                @change="changeOrg"
                            ></GCIOrgTreeSelect>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="6" :lg="7" :md="8" :sm="24">
                        <a-form-item label="所属线路">
                            <a-select
                                allowClear
                                placeholder="请选择所属线路"
                                @change="handleChangeRoute"
                                v-model="queryParam.routeId"
                                show-search
                                :filter-option="addFilterOption"
                                @search="handleRouteSearch"
                                :loading="routeLoading"
                            >
                                <a-select-option v-for="(item, index) in routeList" :key="index" :value="item.routeId">
                                    {{ item.routeName }}
                                </a-select-option>
                            </a-select>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="6" :lg="7" :md="8" :sm="24">
                        <a-form-item label="终端ID">
                            <a-input
                                placeholder="请输入数字格式的终端ID"
                                v-model="queryParam.obuId"
                                allowClear
                            ></a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="6" :lg="7" :md="8" :sm="24">
                        <a-form-item label="ICCID">
                            <a-input
                                placeholder="请输入数字格式的ICCID"
                                v-model="queryParam.simCode"
                                allowClear
                            ></a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="6" :lg="7" :md="8" :sm="24">
                        <a-form-item label="终端芯片编号">
                            <a-input
                                placeholder="请输入数字格式的终端芯片编号"
                                v-model="queryParam.obuChipCode"
                                allowClear
                            ></a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="6" :lg="7" :md="8" :sm="24">
                        <a-form-item label="车辆名称/车辆编码/车辆牌照">
                            <a-input
                                placeholder="请输入车辆名称/车辆编码/车辆牌照"
                                v-model="queryParam.flagStr"
                                allowClear
                            ></a-input>
                        </a-form-item>
                    </a-col>
                    <template v-if="toggleSearchStatus">
                        <a-col :xl="6" :lg="7" :md="8" :sm="24">
                            <a-form-item label="车辆状态">
                                <j-dict-select-tag
                                    v-model="queryParam.busStatus"
                                    placeholder="请选择车辆状态"
                                    dictCode="bus_status"
                                />
                            </a-form-item>
                        </a-col>
                        <a-col :xl="6" :lg="7" :md="8" :sm="24">
                            <a-form-item label="发布状态">
                                <j-dict-select-tag
                                    v-model="queryParam.publishStatus"
                                    placeholder="请选择发布状态"
                                    dictCode="publish_status"
                                />
                            </a-form-item>
                        </a-col>
                        <a-col :xl="6" :lg="7" :md="8" :sm="24">
                            <a-form-item label="是否启用">
                                <j-dict-select-tag
                                    v-model="queryParam.isActive"
                                    placeholder="请选择是否启用"
                                    dictCode="is_active"
                                />
                            </a-form-item>
                        </a-col>
                    </template>
                    <a-col :xl="6" :lg="7" :md="8" :sm="24">
                        <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
                            <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                            <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px"
                                >重置</a-button
                            >
                            <a @click="handleToggleSearch" style="margin-left: 8px">
                                {{ toggleSearchStatus ? '收起' : '展开' }}
                                <a-icon :type="toggleSearchStatus ? 'up' : 'down'" />
                            </a>
                        </span>
                    </a-col>
                </a-row>
            </a-form>
        </div>
        <!-- 查询区域-END -->

        <!-- 操作按钮区域 -->
        <div class="table-operator">
            <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
            <a-button @click="editRow" type="primary" icon="edit">编辑</a-button>
            <a-button @click="publishBus" type="primary" icon="plus">发布</a-button>
            <a-button type="primary" icon="download" @click="handleExportXls('车辆管理')">导出</a-button>
            <a-button type="primary" icon="import" @click="handleImportXlsMy('import')">导入</a-button>
            <a-dropdown v-if="selectedRowKeys.length > 0">
                <a-menu slot="overlay">
                    <a-menu-item key="2" @click="batchDealBusStatus(0)">
                        <a-icon type="check-circle" />启用
                    </a-menu-item>
                    <a-menu-item key="3" @click="batchDealBusStatus(1)"> <a-icon type="stop" />停用 </a-menu-item>
                </a-menu>
                <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down"/></a-button>
            </a-dropdown>
        </div>

        <!-- table区域-begin -->
        <div>
            <div class="ant-alert ant-alert-info" style="margin-bottom: 16px">
                <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择
                <a style="font-weight: 600">{{ selectedRowKeys.length }}</a
                >项
                <a style="margin-left: 24px" @click="onClearSelected">清空</a>
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
                :customRow="customRow"
                @change="handleTableChange"
                :scroll="{ x: 1600 }"
            >
            </a-table>
        </div>

        <edBus-modal ref="modalForm" @ok="modalFormOk"></edBus-modal>
        <!-- 导入 弹窗 -->
        <a-modal
            title="车辆导入"
            :width="width"
            :visible="importVisible"
            :confirmLoading="confirmLoading"
            switchFullscreen
            cancelText="关闭"
            @cancel="handleCancel"
            @ok="handleOkItem"
        >
            <a-spin :spinning="confirmLoading">
                <a-form-item label="">
                    <a class="table-page-search-wrapper">所属机构：</a
                    ><GCIOrgTreeSelect
                        v-model="uploadOrgCode"
                        placeholder="请选择所属机构"
                        :selectType="selectType"
                        :isGetOption="isGetOption"
                        style="width: 70%"
                    ></GCIOrgTreeSelect>
                </a-form-item>
                <a-upload
                    name="file"
                    :multiple="false"
                    :fileList="fileList"
                    :remove="handleRemove"
                    :beforeUpload="beforeUpload"
                >
                    <a-button>
                        <a-icon
                            type="
          upload"
                        />
                        上传文件
                    </a-button>
                </a-upload>
                <!-- <span
          class="upload"
          style="color: rgb(61, 126, 234);float: right;
    display: block; margin-top: -71px;z-index: 999; height: 32px;line-height: 32px;padding: 24px;text-align: center;"
          @click="downloadTemplate()"
        >
          <a-icon type="file-excel" /> 模板下载
        </span> -->
                <span
                    class="upload"
                    style="color: rgb(61, 126, 234);float: right;
    display: block; margin-top: -71px;z-index: 999; height: 32px;line-height: 32px;padding: 24px;text-align: center;"
                >
                    <a-icon type="file-excel" />
                    <a :href="path + 'fileDown/base/busModel.xls'" download="busModel.xls">模板下载</a>
                </span>
            </a-spin>
        </a-modal>
        <!-- 导入 弹窗end -->
        <!-- 完成确认 弹窗 -->
        <a-modal
            title="操作结果"
            :width="width"
            :visible="handleResultVisible"
            :confirmLoading="confirmLoading"
            switchFullscreen
            cancelText="关闭"
            okText="继续添加"
            @cancel="handleCancel"
            @ok="handleResultOkItem"
        >
            <span>{{ handleResult }}</span>
        </a-modal>
    </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less';
import { mixinDevice } from '@/utils/mixin';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import { deleteAction, postAction, getAction } from '@/api/manage';
import EdBusModal from './modules/EdBusModal';
// import {deleteAction} from "../../../../smart-bus-cloud-tenant-manage/src/api/manage";
import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect';
import GCIRouteSelect from '@/components/gci/GCIRouteSelect';

export default {
    name: 'EdBusList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
        EdBusModal,
        GCIOrgTreeSelect,
        GCIRouteSelect
    },
    data() {
        return {
            routeSelect: '',
            uploadOrgCode: '',
            selectType: 'id',
            isGetOption: true,
            description: 'ed_bus管理页面',
            handleResult: '',
            handleResultVisible: false,
            queryParam: {
                routeId: ''
            },
            /* 排序参数 */
            isorter: {
                column: 'busId',
                order: 'desc'
            },
            // 表头
            columns: [
                {
                    title: '序号',
                    dataIndex: '',
                    key: 'rowIndex',
                    align: 'center',
                    width: 60,
                    customRender: function(t, r, index) {
                        return parseInt(index) + 1;
                    }
                },
                {
                    title: '车辆名称',
                    align: 'center',
                    dataIndex: 'busName',
                    ellipsis: true,
                    sorter: true
                },
                {
                    title: '车辆编码',
                    align: 'center',
                    dataIndex: 'busCode',
                    sorter: true
                },
                {
                    title: '车辆牌照',
                    align: 'center',
                    dataIndex: 'numberPlate',
                    sorter: true
                },
                {
                    title: '所属机构',
                    align: 'center',
                    dataIndex: 'organId',
                    sorter: true,
                    ellipsis: true,
                    customRender: function(text, record) {
                        return record.organId_dictText;
                    }
                },
                {
                    title: '所属线路',
                    align: 'center',
                    dataIndex: 'routeId',
                    sorter: true,
                    ellipsis: true,
                    customRender: function(text, record) {
                        return record.routeName;
                    }
                },
                {
                    title: '终端ID',
                    align: 'center',
                    dataIndex: 'obuId',
                    ellipsis: true,
                    sorter: true
                },
                {
                    title: 'ICCID',
                    align: 'center',
                    dataIndex: 'simCode',
                    width: 150,
                    sorter: true
                },
                {
                    title: '终端芯片编号',
                    align: 'center',
                    dataIndex: 'obuChipCode',
                    width: 120,
                    sorter: true
                },
                {
                    title: '发布状态',
                    align: 'center',
                    dataIndex: 'publishStatus',
                    sorter: true,
                    customRender: function(text, record) {
                        return record.publishStatus_dictText;
                    }
                },
                {
                    title: '是否启用',
                    align: 'center',
                    dataIndex: 'isActive',
                    sorter: true,
                    customRender: function(text, record) {
                        return record.isActive_dictText;
                    }
                },
                {
                    title: '车辆状态',
                    align: 'center',
                    dataIndex: 'busStatus',
                    sorter: true,
                    customRender: function(text, record) {
                        return record.busStatus_dictText;
                    }
                },
                {
                    title: '无障碍设施情况',
                    align: 'center',
                    dataIndex: 'isFacilities',
                    width: 130,
                    sorter: true,
                    customRender: function(text, record) {
                        return text === 1 ? '有' : text === 0 ? '无' : '';
                    }
                },
                {
                    title: '核定载客量',
                    align: 'center',
                    dataIndex: 'carryCapacity',
                    sorter: true
                },
                {
                    title: '备注',
                    align: 'center',
                    dataIndex: 'remark',
                    ellipsis: true,
                    sorter: true
                }
            ],
            url: {
                list: '/edBus/list',
                delete: '/edBus/deleteBatch',
                exportXlsUrl: '/edBus/export',
                importExcelUrl: '/edBus/importExcel',
                publishBus: '/edBus/publishBus',
                updateStatus: '/edBus/updateStatus',
                batchPublish: '/base/bsBusManger/updateBusPublishStatus',
                getRouteUrl: '/baseservice/bsRoute/getRouteList'
            },
            confirmLoading: false,
            width: 600,
            importVisible: false, // 导入
            format: false,
            fileList: [],
            dictOptions: {},
            treeData: [],
            routeList: [],
            value: null,
            routeLoading: false,
            selectOrgId: '',
            routeSelectPage: 1,
            routeNullFlag: true,
            routeNameSearch: '',
            routeTimer: null,
            path: ''
        };
    },
    created() {
        this.path = process.env.VUE_APP_PUBLIC_PATH;
    },
    watch: {
        value(value) {
            console.log(value);
        }
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
            postAction(this.url.list, params)
                .then(res => {
                    if (res.success) {
                        this.dataSource = res.result.records;
                        this.ipagination.total = res.result.total;
                    }
                    if (res.code === 510) {
                        this.$message.warning(res.message);
                    }
                    this.loading = false;
                })
                .finally(() => {
                    this.loading = false;
                });
            this.onClearSelected();
        },
        changeOrg(value) {
            // this.queryParam.routeSelect = '';
            this.queryParam.routeId = '';
            if (value) {
                this.changeRoute(value);
            }
            this.selectOrgId = value;
        },
        changeRoute(organId, routePage, routeName) {
            if (!routePage) {
                routePage = 1;
            }
            if (!routeName) {
                routeName = this.routeNameSearch;
            }

            if (!organId) {
                this.$message.warning('请先选择机构');
                return;
            }

            if (this.routeLoading) {
                return;
            }

            // 查询日班线路
            let param = {
                organRunId: organId,
                routeName: routeName,
                pageNo: routePage,
                pageSize: 10
            };
            this.routeLoading = true;
            let that = this;
            getAction(this.url.getRouteUrl, param)
                .then(res => {
                    if (res.success) {
                        if (!res.result.records || res.result.records.length == 0) {
                            // that.routeNullFlag = false
                            // window.clearInterval(that.routeTimer)
                        }
                        if (routePage == 1) {
                            that.routeList = res.result.records;
                        } else {
                            for (let i in res.result.records) {
                                that.routeList.push(res.result.records[i]);
                            }
                        }
                    }
                })
                .finally(res => {
                    this.routeLoading = false;
                });
        },
        addFilterOption(input, option) {
            let flag = option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
            return flag;
        },
        handleChangeRoute(value) {
            console.log('value', value);
            this.queryParam.routeId = value;
        },
        handleCancel() {
            this.importVisible = false;
            this.handleResultVisible = false;
        },
        handleResultOkItem() {
            this.handleResultVisible = false;
            this.importData();
        },
        importData(title) {
            this.importVisible = true;
            this.fileList = [];
        },
        handleRemove(file) {
            const index = this.fileList.indexOf(file);
            const newFileList = this.fileList.slice();
            newFileList.splice(index, 1);
            this.fileList = newFileList;
        },
        // 导入
        handleImportXlsMy(title) {
            if (title === 'import') {
                this.importVisible = true;
                this.fileList = [];
            }
        },
        downloadTemplate() {
            this.costomDownFile('/common/sys/getExcelModel?modelName=busModel.xls', 'busModel');
        },
        handleOkItem() {
            if (!this.format) {
                this.$message.warning('请选择文件!');
                return;
            }
            if (!this.uploadOrgCode) {
                this.$message.warning('请选择所属机构!');
                return;
            }
            const { fileList } = this;
            const formData = new FormData();
            fileList.forEach(file => {
                formData.append('file', file);
            });
            formData.append('organId', this.uploadOrgCode);
            postAction(this.url.importExcelUrl, formData).then(res => {
                this.uploading = false;
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
            });
        },
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

            return false;
        },
        routeChange(e) {
            for (let t of this.routeList) {
                if (e === t.id) {
                    this.model.orgCode = t.orgCode;
                    break;
                }
            }
        },
        publishBus() {
            if (this.selectedRowKeys.length <= 0) {
                this.$message.warning('请选择一条记录！');
            } else {
                let params = {};
                params.busIds = this.selectedRowKeys;

                const that = this;
                this.$confirm({
                    title: '发布',
                    content: '是否确认发布所选车辆?',
                    onOk: function() {
                        postAction(that.url.publishBus, params).then(res => {
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
        },
        batchDealBusStatus: function(isActive) {
            if (!this.url.updateStatus) {
                this.$message.error('请设置url.updateStatus属性!');
                return;
            }
            if (this.selectedRowKeys.length <= 0) {
                this.$message.warning('请选择一条记录！');
            } else {
                // var ids = '';
                // for (var a = 0; a < this.selectedRowKeys.length; a++) {
                //   ids += this.selectedRowKeys[a] + ',';
                // }
                let params = {};
                params.busIds = this.selectedRowKeys;
                params.isActive = isActive;

                const that = this;
                this.$confirm({
                    title: isActive === 0 ? '确认启用' : '确认停用',
                    content: isActive === 0 ? '是否启用选中车辆?' : '是否停用选中车辆?',
                    onOk: function() {
                        postAction(that.url.updateStatus, params).then(res => {
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
        },
        initDictConfig() {},
        initOrganInfo() {
            console.log(11);
            getAction(this.url.routeList)
                .then(res => {
                    if (res.success) {
                        this.routeList = res.result;
                    } else {
                        this.$message.warning(res.message);
                    }
                })
                .catch(e => {
                    console.error(e);
                })
                .then(() => {
                    this.confirmLoading = false;
                });
        },
        batchPublish(status) {
            console.log(this.selectedRowKeys);
            if (!this.url.batchPublish) {
                this.$message.error('请设置url.batchPublish属性!');
                return;
            }
            if (this.selectedRowKeys.length <= 0) {
                this.$message.warning('请选择一条记录！');
            } else {
                var ids = '';
                for (var a = 0; a < this.selectedRowKeys.length; a++) {
                    ids += this.selectedRowKeys[a] + ',';
                }
                let parmas = {};
                parmas.ids = ids;
                parmas.status = status;

                var that = this;
                this.$confirm({
                    title: '确认发布',
                    content: '是否确认发布?',
                    onOk: function() {
                        postAction(that.url.batchPublish, parmas).then(res => {
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
        },
        handleChangeTree() {
            let param = { orgCode: this.queryParam.orgCode };
            getAction(this.url.routeList, param)
                .then(res => {
                    if (res.success) {
                        this.routeList = res.result;
                    } else {
                        this.$message.warning(res.message);
                    }
                })
                .catch(e => {
                    console.error(e);
                })
                .then(() => {
                    this.confirmLoading = false;
                });
        },
        batchStop() {},
        batchUse() {},
        editRow() {
            if (this.selectionRows.length <= 0) {
                this.$message.warning('请选择需要编辑的数据');
                return;
            }
            if (this.selectionRows.length !== 1) {
                this.$message.warning('编辑只能选择一条数据');
                return;
            }
            console.log('this.selectionRows[0]', this.selectionRows[0]);
            this.handleEdit(this.selectionRows[0]);
            this.selectionRows = [];
            this.selectedRowKeys = [];
        },
        handleChange(value) {
            this.queryParam.routeId = value;
        },
        /* 导入 */
        handleImportExcel(info) {
            // debugger

            if (info.file.status !== 'uploading') {
                console.log(info.file, info.fileList);
            }
            if (info.file.status === 'done') {
                var index = -1;
                index = info.file.name.lastIndexOf('.');
                if (index > -1) {
                    var s = info.file.name.substr(index + 1);
                    if (s !== 'xls') {
                        this.$message.error(`导入失败,请选择后缀为xls的文件操作! `);
                        return;
                    }
                } else {
                    this.$message.error(`导入失败,请选择后缀为xls的文件操作! `);
                }
                if (info.file.response.success) {
                    // this.$message.success(`${info.file.name} 文件上传成功`);
                    if (info.file.response.code === 201) {
                        let {
                            message,
                            result: { msg, fileUrl, fileName }
                        } = info.file.response;
                        let href = window._CONFIG['domianURL'] + fileUrl;
                        this.$warning({
                            title: message,
                            content: (
                                <div>
                                    <span>{msg}</span>
                                    <br />
                                    <span>
                                        具体详情请{' '}
                                        <a href={href} target="_blank" download={fileName}>
                                            点击下载
                                        </a>{' '}
                                    </span>
                                </div>
                            )
                        });
                    } else {
                        this.$message.success(info.file.response.message || `${info.file.name} 文件上传成功`);
                    }
                    this.loadData();
                } else {
                    this.$message.error(`${info.file.name} ${info.file.response.message}.`);
                }
            } else if (info.file.status === 'error') {
                this.$message.error(`文件上传失败: ${info.file.msg} `);
            }
        },
        delete() {
            if (this.selectedRowKeys.length <= 0) {
                this.$message.warning('请选择一条记录！');
            } else {
                let ids = this.selectionRows.map(row => row.id).join(',');
                this.$confirm({
                    title: '确认删除吗',
                    content: '是否删除选中数据?',
                    onOk: () => {
                        deleteAction(this.url.delete, {
                            ids: ids
                        }).then(res => {
                            if (res.success) {
                                this.$message.success(res.message);
                                this.loadData();
                            } else {
                                this.$message.warning(res.message);
                            }
                        });
                    }
                });
            }
        },
        handleRoutePopupScroll(e) {
            // console.log('滑动......',e)
            // e.persist()
            const { target } = e;
            // scrollHeight：代表包括当前不可见部分的元素的高度
            // scrollTop：代表当有滚动条时滚动条向下滚动的距离，也就是元素顶部被遮住的高度
            // clientHeight：包括padding但不包括border、水平滚动条、margin的元素的高度
            const rmHeight = target.scrollHeight - target.scrollTop;
            const clHeight = target.clientHeight;
            // 当下拉框失焦的时候，也就是不下拉的时候
            if (rmHeight === 0 && clHeight === 0) {
                this.routeSelectPage = 1;
            } else {
                // 当下拉框下拉并且滚动条到达底部的时候
                // 可以看成是分页，当滚动到底部的时候就翻到下一页
                if (rmHeight < clHeight + 5) {
                    const scrollPage = this.routeSelectPage;
                    this.routeSelectPage = scrollPage + 1;
                    //调用处理数据的函数增加下一页的数据
                    this.changeRoute(this.selectOrgId, scrollPage + 1);
                }
            }
        },
        handleRouteSearch(value) {
            this.routeNameSearch = value;
            this.changeRoute(this.selectOrgId, 1, value);
        },
        customRow() {
            return {
                style: {
                    'font-size': '10px'
                }
            };
        },
        searchReset() {
            this.queryParam = {
                organId: '',
                routeId: '',
                obuId: '',
                simCode: '',
                obuChipCode: '',
                flagStr: '',
                busStatus: '',
                publishStatus: '',
                isActive: ''
            };
        }
    }
};
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>
