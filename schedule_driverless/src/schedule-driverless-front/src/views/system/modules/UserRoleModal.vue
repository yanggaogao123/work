<template>
    <a-drawer
        :title="title"
        :maskClosable="true"
        width="650"
        placement="right"
        :closable="true"
        @close="close"
        :visible="visible"
        style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;"
    >
        <a-form>
            <a-form-item label="平台">
                <a-select
                    labelInValue
                    :value="{ key: this.platformSelected.platformCode }"
                    @change="platformChange"
                    disabled
                >
                    <a-select-option v-for="(item, key) in userPlatforms" :key="key" :value="item.platformCode"
                        >{{ item.platformName }}
                    </a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="所拥有的权限">
                <a-tree
                    :disabled="disableAction"
                    checkable
                    @check="onCheck"
                    :checkedKeys="checkedKeys"
                    :treeData="treeData"
                    @expand="onExpand"
                    @select="onTreeNodeSelect"
                    :selectedKeys="selectedKeys"
                    :expandedKeys="expandedKeysss"
                    :checkStrictly="checkStrictly"
                >
                    <span slot="hasDatarule" slot-scope="{ slotTitle, ruleFlag }">
                        {{ slotTitle}}<a-icon v-if="ruleFlag" type="align-left" style="margin-left:5px;color: red;"></a-icon>
                    </span>
                </a-tree>
            </a-form-item>
        </a-form>

        <div class="drawer-bootom-button">
            <a-dropdown style="float: left" :trigger="['click']" placement="topCenter" :disabled="disableAction">
                <a-menu slot="overlay">
                    <!--          <a-menu-item key="1" @click="switchCheckStrictly(1)">父子关联</a-menu-item>-->
                    <!--          <a-menu-item key="2" @click="switchCheckStrictly(2)">取消关联</a-menu-item>-->
                    <a-menu-item key="3" @click="checkALL">全部勾选</a-menu-item>
                    <a-menu-item key="4" @click="cancelCheckALL">取消全选</a-menu-item>
                    <a-menu-item key="5" @click="expandAll">展开所有</a-menu-item>
                    <a-menu-item key="6" @click="closeAll">合并所有</a-menu-item>
                </a-menu>
                <a-button> 树操作 <a-icon type="up" /> </a-button>
            </a-dropdown>
            <a-popconfirm title="确定放弃编辑？" @confirm="close" okText="确定" cancelText="取消">
                <a-button style="margin-right: .8rem">取消</a-button>
            </a-popconfirm>
            <a-button
                :disabled="disableAction"
                @click="handleSubmit(false)"
                type="primary"
                :loading="loading"
                ghost
                style="margin-right: 0.8rem;color: #fff;background-color: #1890ff !important;border-color: #1890ff"
                >仅保存</a-button
            >
            <a-button :disabled="disableAction" @click="handleSubmit(true)" type="primary" :loading="loading"
                >保存并关闭</a-button
            >
        </div>

        <role-datarule-modal ref="datarule"></role-datarule-modal>
        <EmpowerRoleModal ref="empowerRoleModal" @ok="onOkEmpower" />
    </a-drawer>
</template>
<script>
import { queryTreeListForRole, queryRolePermission, saveRolePermission } from '@/api/api';
import RoleDataruleModal from './RoleDataruleModal.vue';
import { postAction } from '../../../api/manage';
import EmpowerRoleModal from './EmpowerRoleModal';

export default {
    name: 'RoleModal',
    components: {
        RoleDataruleModal,
        EmpowerRoleModal
    },
    data() {
        return {
            disableAction: false,
            roleId: '',
            userType: '',
            treeData: [],
            defaultCheckedKeys: [],
            checkedKeys: [],
            expandedKeysss: [],
            allTreeKeys: [],
            autoExpandParent: true,
            checkStrictly: true,
            title: '角色权限配置',
            visible: false,
            loading: false,
            selectedKeys: [],
            platformSelected: {
                platformCode: '',
                platformName: ''
            },
            url: {
                checkSaveRolePermission: '/sys/permission/checkSaveRolePermission' // 保存授权前需要：校验保存角色的菜单涉及的角色，在保存角色的权限时展示给用户，用户看到后确认修改权限关系就修改授权关系
            },
            userPlatforms: []
        };
    },
    created() {
        this.userType = this.$store.getters.userInfo.type; // 9999: 超级管理员；1: 普通管理员；0: 普通用户；
    },
    mounted() {
        this.userPlatforms = this.$store.getters.userPlatforms;
    },
    methods: {
        onTreeNodeSelect(id) {
            if (id && id.length > 0) {
                this.selectedKeys = id;
            }
            this.$refs.datarule.show(this.selectedKeys[0], this.roleId);
        },
        onCheck(selectedKeys, evt) {
            if (this.checkStrictly) {
                this.onCheck2(selectedKeys, evt, false);
            } else {
                this.checkedKeys = o;
            }
        },
        onCheck2(selectedKeys, evt) {
            let keys = {};
            selectedKeys.checked.forEach(item => {
                keys[item] = 1;
            });
            let setParent = function(node) {
                node = node.$parent;
                while (node) {
                    if (node.eventKey) {
                        keys[node.eventKey] = 1;
                    }
                    node = node.$parent;
                }
            };
            function setChildren(node) {
                if (!node.$children) {
                    return;
                }
                for (let i = 0; i < node.$children.length; i++) {
                    let child = node.$children[i];
                    if (child.eventKey) {
                        keys[child.eventKey] = evt.checked ? 1 : 0;
                    }
                    //取消父节点，子孙节点都取消
                    if (!evt.checked) {
                        setChildren(child);
                    }
                }
            }
            if (evt.checked) {
                //更新父节点选中
                setParent(evt.node);
            }
            //更新子孙节点
            setChildren(evt.node);
            let list = [];
            for (const i in keys) {
                if (keys[i] == 1) {
                    list.push(i);
                }
            }

            this.checkedKeys = list;
        },
        show(role) {
            // if(role.roleType === 2 ) { // 管理员
            //   console.log('管理员禁止操作用户级角色')
            //   this.disableAction = true
            // }else {
            //   this.disableAction = false
            // }
            for (var i = 0; i < this.userPlatforms.length; i++) {
                if (this.userPlatforms[i].platformCode === role.platformCode) {
                    //只赋值
                    this.platformSelected = Object.assign({}, this.userPlatforms[i]);
                    break;
                }
            }
            this.roleId = role.id;
            this.visible = true;
        },
        close() {
            this.reset();
            this.$emit('close');
            this.visible = false;
        },
        onExpand(expandedKeys) {
            this.expandedKeysss = expandedKeys;
            this.autoExpandParent = false;
        },
        reset() {
            this.expandedKeysss = [];
            this.checkedKeys = [];
            this.defaultCheckedKeys = [];
            this.loading = false;
        },
        expandAll() {
            this.expandedKeysss = this.allTreeKeys;
        },
        closeAll() {
            this.expandedKeysss = [];
        },
        checkALL() {
            this.checkedKeys = this.allTreeKeys;
        },
        cancelCheckALL() {
            //this.checkedKeys = this.defaultCheckedKeys
            this.checkedKeys = [];
        },
        switchCheckStrictly(v) {
            if (v == 1) {
                this.checkStrictly = false;
            } else if (v == 2) {
                this.checkStrictly = true;
            }
        },
        handleCancel() {
            this.close();
        },
        handleSubmit(flag) {
            let that = this;
            let params = {
                roleId: that.roleId,
                permissionIds: that.checkedKeys.join(','),
                lastpermissionIds: that.defaultCheckedKeys.join(',')
            };
            that.loading = true;
            console.log('请求参数：', params);
            postAction(this.url.checkSaveRolePermission, params)
                .then(res => {
                    if (res.success) {
                        console.log(res.result);
                        if (res.result.permissions) {
                            this.$refs.empowerRoleModal.show(res.result);
                        } else {
                            saveRolePermission(params).then(res => {
                                if (res.success) {
                                    that.$message.success(res.message);
                                    if (flag) {
                                        that.close();
                                    }
                                } else {
                                    that.$message.error(res.message);
                                    if (flag) {
                                        that.close();
                                    }
                                }
                            });
                        }
                    } else {
                        this.$message.warning(res.message);
                    }
                })
                .catch(err => {
                    this.$message.error(err.message);
                })
                .finally(() => {
                    that.loading = false;
                });
        },
        onOkEmpower() {
            let params = {
                roleId: this.roleId,
                permissionIds: this.checkedKeys.join(','),
                lastpermissionIds: this.defaultCheckedKeys.join(',')
            };
            this.$refs.empowerRoleModal.confirmLoading = true;
            saveRolePermission(params)
                .then(res => {
                    if (res.success) {
                        this.$message.success(res.message);
                        this.$refs.empowerRoleModal.close();
                        this.close();
                    } else {
                        this.$message.error(res.message);
                    }
                })
                .finally(() => {
                    this.$refs.empowerRoleModal.confirmLoading = false;
                });
        },
        loadData() {
            queryTreeListForRole().then(res => {
                console.log('查询结果', res);
                if (!res.success) {
                    this.$message.warning(res.message);
                    return;
                }
                this.treeData = res.result.treeList;
                this.allTreeKeys = res.result.ids;
                queryRolePermission({ roleId: this.roleId }).then(res => {
                    this.checkedKeys = [...res.result];
                    this.defaultCheckedKeys = [...res.result];
                    this.expandedKeysss = this.allTreeKeys;
                    console.log(this.defaultCheckedKeys);
                });
            });
        },
        platformChange(selVal) {
            this.platformSelected.platformCode = selVal.key;
            this.platformSelected.platformName = selVal.label;
            this.loadPermissions();
        },
        loadPermissions() {
            let params = {
                platformCode: this.platformSelected.platformCode,
                roleId: this.roleId
            };
            if (this.visible) {
                queryTreeListForRole(params).then(res => {
                    console.log('查询结果', res);
                    if (!res.success) {
                        this.$message.warning(res.message);
                        return;
                    }
                    this.treeData = res.result.treeList;
                    this.allTreeKeys = res.result.ids;
                    queryRolePermission({
                        roleId: this.roleId
                    }).then(res => {
                        this.checkedKeys = [...res.result];
                        this.defaultCheckedKeys = [...res.result];
                        this.expandedKeysss = this.allTreeKeys;
                        //console.log(this.defaultCheckedKeys)
                    });
                });
            }
        }
    },
    watch: {
        visible() {
            if (this.visible) {
                this.loadPermissions();
            }
        }
    }
};
</script>
<style lang="less" scoped>
.drawer-bootom-button {
    position: absolute;
    bottom: 0;
    width: 100%;
    border-top: 1px solid #e8e8e8;
    padding: 10px 16px;
    text-align: right;
    left: 0;
    background: #fff;
    border-radius: 0 0 2px 2px;
}
</style>
