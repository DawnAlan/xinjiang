<template>
	<a-card
		:bordered="false"
		:active-tab-key="activeKey"
		:tab-list="tabListNoTitle"
		@tabChange="(key) => onTabChange(key, 'frmIndex')"
	>
		<p v-if="activeKey === 'frmIndex'">
			<frm-index />
		</p>
		<p v-if="activeKey === 'bizIndex'">
			<biz-index />
		</p>
		<p v-if="activeKey === 'fiducialIndex'">
			<fiducial-Index />
        </p>
	</a-card>
</template>

<script setup name="devDict">
	import frmIndex from './category/frmIndex.vue'
	import bizIndex from './category/bizIndex.vue'
	import fiducialIndex from './category/fiducialIndex.vue'
	const activeKey = ref('frmIndex')
	const tabListNoTitle = ref([
		{ key: 'frmIndex', tab: '系统字典' },
		{ key: 'bizIndex', tab: '业务字典' },
		{ key: 'fiducialIndex', tab: '考证字典' }
	])
	const onTabChange = (value, type) => {
		if (type === 'key') {
			key.value = value
		} else if (type === 'frmIndex') {
			activeKey.value = value
		}
	}
</script>
