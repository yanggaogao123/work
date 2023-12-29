<template>
    <!-- 车辆上下线记录 -->
    <a-card :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery">
                <a-row :gutter="24">
                    <a-col :xxl="5" :xl="6" :lg="7" :md="8" :sm="24">
                        <a-form-item label="车辆">
                            <GCIBusSelect v-model="queryParam.obuid" />
                        </a-form-item>
                    </a-col>
                    <a-col :xl="8" :lg="8" :md="8" :sm="24">
                        <a-form-item label="上线时间">
                            <j-date
                                placeholder="请选择开始日期"
                                class="query-group-cust"
                                v-model="queryParam.start"
                            ></j-date>
                            <span class="query-group-split-cust"></span>
                            <j-date
                                placeholder="请选择结束日期"
                                class="query-group-cust"
                                v-model="queryParam.end"
                            ></j-date>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="6" :lg="7" :md="8" :sm="24">
                        <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                            <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                            <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px"
                                >重置</a-button
                            >
                            <!--              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>-->
                        </span>
                    </a-col>
                </a-row>
            </a-form>
        </div>
        <!-- 查询区域-END -->

        <!-- 操作按钮区域 -->
        <div class="table-operator">
            <a-button type="primary" icon="download" @click="handleExportXls('车辆上下线记录')">导出</a-button>
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
                class="j-table-force-nowrap"
                @change="handleTableChange"
            >
                <template slot="htmlSlot" slot-scope="text">
                    <div v-html="text"></div>
                </template>
                <template slot="imgSlot" slot-scope="text">
                    <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
                    <img
                        v-else
                        :src="getImgView(text)"
                        height="25px"
                        alt=""
                        style="max-width:80px;font-size: 12px;font-style: italic;"
                    />
                </template>
                <template slot="fileSlot" slot-scope="text">
                    <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
                    <a-button
                        v-else
                        :ghost="true"
                        type="primary"
                        icon="download"
                        size="small"
                        @click="uploadFile(text)"
                    >
                        下载
                    </a-button>
                </template>

                <span slot="action" slot-scope="text, record">
                    <a @click="handleEdit(record)">编辑</a>

                    <a-divider type="vertical" />
                    <a-dropdown>
                        <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
                        <a-menu slot="overlay">
                            <a-menu-item>
                                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                                    <a>删除</a>
                                </a-popconfirm>
                            </a-menu-item>
                        </a-menu>
                    </a-dropdown>
                </span>
            </a-table>
        </div>
    </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less';
import { mixinDevice } from '@/utils/mixin';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';

import JDate from '@/components/jeecg/JDate.vue';
import { httpAction } from '@/api/manage';
import { getAction, postAction } from '../../api/manage';
import GCIBusSelectWin from '@/components/gci/GCIBusSelectWin';
import GCIBusSelect from '../../components/gci/GCIBusSelect';

export default {
    name: 'VBusOnlineHistList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
        GCIBusSelect,
        JDate,
        GCIBusSelectWin
    },
    data() {
        return {
            description: 'V_BUS_ONLINE_HIST管理页面',
            queryParam: {
                obuid: null,
                start: null,
                end: null
            },
            // 表头
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
                    title: '机构名称',
                    align: 'center',
                    dataIndex: 'organName'
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
                    title: '终端编号',
                    align: 'center',
                    dataIndex: 'obuid'
                },
                {
                    title: '上线站台',
                    align: 'center',
                    dataIndex: 'upStationName'
                },
                {
                    title: '下线站台',
                    align: 'center',
                    dataIndex: 'downStationName'
                },
                {
                    title: '上线时间',
                    align: 'center',
                    dataIndex: 'upTime'
                },
                {
                    title: '下线时间',
                    align: 'center',
                    dataIndex: 'downTime'
                },
                {
                    title: '最近更新时间',
                    align: 'center',
                    dataIndex: 'lastUpdated'
                },
                {
                    title: 'IP',
                    align: 'center',
                    dataIndex: 'ip'
                }
                /*          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            // fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }*/
            ],
            url: {
                list: '/base/vBusOnlineHist/list',
                exportXlsUrl: '/base/vBusOnlineHist/exportXls'
            },
            dictOptions: {}
        };
    },
    computed: {
        importExcelUrl: function() {
            return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
        }
    },
    methods: {
        initDictConfig() {},
        getDate() {
            if (!this.queryParam.start) {
                let date = new Date();
                let year = date.getFullYear();
                let month = date.getMonth() + 1;
                let day = date.getDate();
                month = month < 10 ? '0' + month : month; //月小于10，加0
                day = day < 10 ? '0' + day : day; //day小于10，加0
                let yDay = day - 1;
                this.queryParam.start = year + '-' + month + '-' + yDay;
                this.queryParam.end = year + '-' + month + '-' + day;
            }
        },
        searchReset() {
            this.queryParam = {};
            this.getDate();
            this.loadData(1);
        },
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
            this.getDate();
            console.log(this.queryParam.obuid + 'obuid');
            if (!this.queryParam.obuid) {
                this.$message.warning('请选择车辆!');
                return;
            }
            var params = this.getQueryParams(); // 查询条件
            console.log(params);
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
        }
    }
};
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>
