<template>
	<a-modal :title="title" :width="width" :visible="visible" :confirmLoading="confirmLoading" @ok="handleOk"
		@cancel="handleCancel" destroyOnClose cancelText="关闭">
		<div>
			<div style="font-size: 14px; font-weight: bold; padding: 5px 0;">菜单：</div>
			<span v-for="(item, index) in recordInfo.permissions" :key="index" style="margin-left: 10px;">
				{{ item }} 
				<span v-if="recordInfo.permissions.length > 1 && index !== recordInfo.permissions.length - 1">、</span>
			</span>
		</div>
		<div style="margin-top:10px;">
			<div style="font-size: 14px; font-weight: bold; padding: 5px 0;">角色：</div>
			<a-table ref="table" size="middle" bordered rowKey="id" :scroll="{y: 500}" :columns="columns" :dataSource="recordInfo.deleteRoles"
				:pagination="false">
			</a-table>
		</div>
	</a-modal>
</template>
  
<script>
export default {
	name: "EmpowerRoleModal",
	data() {
		return {
			title: "校验保存角色的菜单涉及的角色",
			width: 600,
			visible: false,
			confirmLoading: false,
			recordInfo: {}, //受影响的对象
			columns: [
				{
					title: '机构',
					align: "center",
					dataIndex: 'orgName'
				},
				{
					title: '角色',
					align: "center",
					dataIndex: 'value'
				},
			]
		}
	},
	created() {
	},
	methods: {
		show(record) {
			this.recordInfo = record
			this.visible = true;
		},
		close() {
			this.$emit('close');
			this.visible = false;
		},
		handleOk() {
			this.$emit('ok');
		},
		handleCancel() {
			this.close()
		},
	}
}
</script>
  
<style scoped></style>