<template>
    <a-card :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery">
                <a-row :gutter="24"> </a-row>
            </a-form>
        </div>
        <!-- 查询区域-END -->

        <!-- 操作按钮区域 -->
        <div class="table-operator">
            <a-button @click="handlePrep" type="primary" icon="plus">添加</a-button>
        </div>
        <div style="text-align: center;margin-bottom: 10px;font-size: large">
            预案明细
        </div>

        <!-- table区域-begin -->
        <div>
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

                <span slot="action" slot-scope="text, record, index">
                    <a-icon
                        type="arrow-up"
                        @click="changeIndex(record, index, 1)"
                        style="margin-right:5px;cursor: pointer;"
                    />
                    <a-icon type="arrow-down" @click="changeIndex(record, index, 2)" style="cursor: pointer;" />

                    <a-divider type="vertical" />

                    <a @click="handlePrep(record)">编辑</a>

                    <a-divider type="vertical" />

                    <a-popconfirm title="是否删除?" @confirm="() => handleDelete(record.preptripId)">
                        <a>删除</a>
                    </a-popconfirm>
                </span>
            </a-table>
        </div>

        <bsPreptrip-modal ref="modalForm" @ok="modalFormOk"></bsPreptrip-modal>
    </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less';
import { mixinDevice } from '@/utils/mixin';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import BsPreptripModal from './modules/BsPreptripModal';
import { randomUUID } from '@/utils/util';
import { postAction } from '../../api/manage';

export default {
    name: 'BsPreptripList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
        BsPreptripModal
    },
    data() {
        return {
            description: '预案明细管理页面',
            // 表头
            columns: [
                {
                    title: '类别',
                    align: 'center',
                    dataIndex: 'serviceName',
                    ellipsis: true,
                    sorter: true
                },
                {
                    title: '类别备注',
                    align: 'center',
                    dataIndex: 'serviceTypeRemark',
                    ellipsis: true,
                    sorter: true
                },
                {
                    title: '方向',
                    align: 'center',
                    dataIndex: 'direction',
                    sorter: true,
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
                    ellipsis: true,
                    sorter: true
                },
                {
                    title: '首站',
                    align: 'center',
                    dataIndex: 'firstStationName',
                    ellipsis: true,
                    sorter: true
                },
                {
                    title: '末站',
                    align: 'center',
                    dataIndex: 'lastStationName',
                    ellipsis: true,
                    sorter: true
                },

                {
                    title: '里程',
                    align: 'center',
                    dataIndex: 'mileage',
                    ellipsis: true,
                    sorter: true
                },
                {
                    title: '间隔',
                    align: 'center',
                    dataIndex: 'interval',
                    ellipsis: true,
                    sorter: true
                },
                // {
                //     title: '序号',
                //     align: 'center',
                //     dataIndex: 'orderNum',
                //     ellipsis: true,
                //     sorter: true
                // },
                {
                    title: '操作',
                    dataIndex: 'action',
                    align: 'center',
                    // fixed:"right",
                    width: 147,
                    scopedSlots: { customRender: 'action' }
                }
            ],
            url: {
                list: '/base/bsPreptrip/list',
                delete: '/base/bsPreptrip/delete',
                deleteBatch: '/base/bsPreptrip/deleteBatch',
                exportXlsUrl: '/base/bsPreptrip/exportXls',
                importExcelUrl: 'base/bsPreptrip/importExcel'
            },
            dictOptions: {},
            disableMixinCreated: true,
            routeId: null,
            deleteIds: [],
            orgCode: ''
        };
    },
    computed: {
        importExcelUrl: function() {
            return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
        }
    },
    methods: {
        initDictConfig() {},
        showList(tripdefineId, routeId) {
            console.log('ccc', tripdefineId);
            console.log(routeId);
            this.routeId = routeId;
            this.deleteIds = [];
            if (tripdefineId) {
                this.queryParam.tripdefineId = tripdefineId;
            } else {
                this.queryParam.tripdefineId = '-1';
            }
            this.searchQuery();
        },
        handlePrep: function(r) {
            console.log('r', r);
            this.$emit('checkRoute', r);
        },
        handleModal: function(r) {
            this.$refs.modalForm.routeId = this.routeId;
            this.$refs.modalForm.orgCode = this.orgCode;
            if (r) {
                // let temp = r.randomUUID;
                if (!r.randomUUID) {
                    r.randomUUID = randomUUID();
                }
                /* if (!temp){
                console.info("去你大爷的aaaaaaaaaaaaaaaaaaaa")
                this.handleAdd();
            }else {
                console.info("不好玩")
                this.handleEdit(r);
            } */
                console.log('rr====', r);
                this.handleEdit(r);
            } else {
                this.$refs.modalForm.tripdefineId = this.queryParam.tripdefineId;
                this.handleAdd();
            }
        },
        modalFormOk(item) {
            console.log('回传的数据', item);
            if (item.orderNum == NaN || !item.orderNum) {
                item.orderNum = this.dataSource.length + 1;
            }
            // if (!item.tripdefineid) {
            //     this.dataSource.push(item);
            // } else {
            // const target = this.dataSource.filter(t => t.randomUUID !== item.randomUUID);
            // this.dataSource = target;
            // this.dataSource.push(item);
            this.dataSource.forEach((ele, i) => {
                if (ele.preptripId == item.preptripId) {
                    this.$set(this.dataSource, i, item);
                }
            });
            let arr = this.dataSource.filter(t => t.preptripId == item.preptripId);
            console.log('arr', arr);
            if (arr.length < 1) {
                this.dataSource.push(item);
            }
            // }
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
            const params = this.getQueryParams(); // 查询条件
            this.loading = true;
            postAction(this.url.list, params).then(res => {
                if (res.success) {
                    // this.dataSource = res.result.records;
                    this.ipagination.total = res.result.total;
                    this.dataSource = res.result.records.map((item, i) => {
                        item.orderNum = i + 1;
                        return item;
                    });
                    // console.log('处理过的list', this.dataSource);
                }
                if (res.code === 510) {
                    this.$message.warning(res.message);
                }
                this.loading = false;
            });
        },
        handleDelete(id) {
            console.log('删除的id' + id);
            if (id) {
                this.deleteIds.push(id);
            }
            this.dataSource = this.dataSource.filter(t => t.preptripId !== id);
            this.dataSource = this.dataSource.map((item, i) => {
                item.orderNum = i + 1;
                return item;
            });
            console.log(this.deleteIds);
        },
        changeIndex(record, i, type) {
            // console.log('改变序号', record, i, type);
            if (type == 1) {
                if (i == 0) {
                    this.$message.warning('已经是最前排序');
                    return;
                }
                this.dataSource[i].orderNum -= 1;
                this.dataSource[i - 1].orderNum += 1;
                this.dataSource.sort((x, y) => {
                    return x.orderNum - y.orderNum;
                });
                console.log('按上改变顺序后的dataSource', this.dataSource);
            } else if (type == 2) {
                if (i == this.dataSource.length - 1) {
                    this.$message.warning('已经是最后排序');
                    return;
                }
                this.dataSource[i].orderNum += 1;
                this.dataSource[i + 1].orderNum -= 1;
                this.dataSource.sort((x, y) => {
                    return x.orderNum - y.orderNum;
                });
                // console.log('按下改变顺序后的dataSource', this.dataSource);
            }
        }
    }
};
</script>
<style scoped>
@import '../../assets/less/common.less';
</style>
