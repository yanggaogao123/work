<template>
    <j-modal
        :title="title"
        :width="1200"
        :visible="visible"
        :confirmLoading="confirmLoading"
        :destroyOnClose="true"
        switchFullscreen
        @cancel="handleCancel"
        @ok="handleCancel"
        cancelText="关闭"
    >
        <a-spin :spinning="confirmLoading">
            <a-card :bordered="false">
                <!-- table区域-begin -->
                <div>
                    <a-table
                        size="middle"
                        bordered
                        :columns="columns"
                        :loading="loading"
                        :scroll="{ x: true }"
                        :data-source="data"
                        :pagination="ipagination"
                        @change="handleTableChange"
                    >
                    </a-table>
                </div>
            </a-card>
        </a-spin>
    </j-modal>
</template>

<script>
import moment from 'moment';
import { getAction, postAction } from '@/api/manage';
import { mixinDevice } from '@/utils/mixin';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';

export default {
    mixins: [JeecgListMixin, mixinDevice],
    components: {},
    props: {},
    data() {
        return {
            moment,
            title: '进出站信息',
            width: 1680,
            visible: false,
            confirmLoading: false,
            disableMixinCreated: true,
            loading: false,
            queryParam: {},
            ipagination: {
                current: 1,
                pageSize: 10,
                pageSizeOptions: ['10', '20', '30', '50'],
                showTotal: (total, range) => {
                    return range[0] + '-' + range[1] + ' 共' + total + '条';
                },
                showQuickJumper: true,
                showSizeChanger: true,
                total: 0
            },
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
                    title: '所属机构',
                    width: 100,
                    // dataIndex: 'sysOrgCode_dictText',
                    dataIndex: 'organName',
                    align: 'center',
                    ellipsis: true
                },
                {
                    title: '运营支线路',
                    width: 100,
                    dataIndex: 'routeSubName',
                    align: 'center',
                    ellipsis: true
                },
                {
                    title: '站点编码',
                    width: 80,
                    dataIndex: 'busStopCode',
                    align: 'center'
                },
                {
                    title: '站点名称',
                    width: 100,
                    dataIndex: 'stationName',
                    align: 'center',
                    ellipsis: true
                },
                {
                    title: '站台序号',
                    width: 80,
                    dataIndex: 'orderNumber',
                    align: 'center'
                },
                {
                    title: '进出站标志',
                    width: 100,
                    // dataIndex: 'adFlag_dictText',
                    dataIndex: 'adFlag',
                    align: 'center'
                },
                {
                    title: '进出站时间',
                    width: 100,
                    align: 'center',
                    dataIndex: 'adTime',
                    ellipsis: true
                },
                {
                    title: '入库时间',
                    width: 100,
                    align: 'center',
                    dataIndex: 'inTime',
                    ellipsis: true
                },
                {
                    title: '终端编号',
                    width: 80,
                    dataIndex: 'obuid',
                    align: 'center'
                },
                {
                    title: '报站类型',
                    width: 80,
                    dataIndex: 'reportType_dictText',
                    //dataIndex: 'reportType_dictText',
                    align: 'center',
                    ellipsis: true
                },
                {
                    title: '服务编码',
                    width: 80,
                    dataIndex: 'serviceNumber',
                    align: 'center'
                },
                {
                    title: '趟次号',
                    width: 80,
                    dataIndex: 'runningboard',
                    align: 'center'
                },
                {
                    title: '车辆编码',
                    width: 80,
                    dataIndex: 'busCode',
                    align: 'center',
                    ellipsis: true
                },
                {
                    title: '车辆牌照',
                    width: 80,
                    dataIndex: 'numberPlate',
                    align: 'center',
                    ellipsis: true
                },
                {
                    title: '运营线路编码',
                    width: 80,
                    dataIndex: 'routeCode',
                    align: 'center'
                },
                {
                    title: '运营线路',
                    width: 100,
                    dataIndex: 'routeName',
                    align: 'center',
                    ellipsis: true
                }
            ],
            data: [],
            url: {
                list: '/monitor/dyAdreal/list',
                exportXlsUrl: ''
            }
        };
    },
    watch: {},
    computed: {},
    created() {},
    mounted() {},
    methods: {
        initData(tripData) {
            let sendData = {
                routeId: tripData.routeIdRun,
                obuid: tripData.obuid,
                beginTime: tripData.triplogBeginTime,
                endTime: tripData.triplogEndTime,
                column: 'adTime',
                field:
                    'id,,,sysOrganCode,routeSubName,busStopCode,stationName,orderNumber,adFlag_dictText,adTime,inTime,obuid,reportType_dictText,serviceNumber,tripId,busCode,numberPlate,routeCode,routeName',
                pageNo: this.ipagination.current,
                pageSize: this.ipagination.pageSize,
                order: 'asc'
            };

            this.queryParam = sendData;

            // let sendData = {
            //   routeId: null,
            //   obuid: null,
            //   beginTime: '2021-09-13 00:00:00',
            //   endTime: '2021-09-14 00:00:00',
            //   column: 'adTime',
            //   field: 'id,,,sysOrganCode,routeSubName,busStopCode,stationName,orderNumber,adFlag_dictText,adTime,inTime,obuid,reportType_dictText,serviceNumber,tripId,busCode,numberPlate,routeCode,routeName',
            //   pageNo: 1,
            //   pageSize: 10,
            //   order: 'desc'
            // }

            this.getList(sendData);
        },
        getList(sendData) {
            this.loading = true;
            postAction(this.url.list, sendData).then(res => {
                console.log('222222', res);
                if (res.success) {
                    this.data = res.result.records;
                    this.ipagination.total = res.result.total;
                } else {
                    this.$message.warning(res.message);
                }
                this.loading = false;
            });
        },
        loadData() {
            this.queryParam.pageNo = this.ipagination.current;
            this.queryParam.pageSize = this.ipagination.pageSize;
            this.getList(this.queryParam);
        },
        close() {
            this.visible = false;
        },
        handleCancel() {
            this.close();
        }
    }
};
</script>

<style lang="less" scoped>
.ant-table td {
    white-space: nowrap;
}
</style>
