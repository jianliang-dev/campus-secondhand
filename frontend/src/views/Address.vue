<template>
  <div class="page">
    <el-card>
      <template #header>
        <div class="title">收货地址</div>
      </template>
      <el-button type="primary" @click="openAdd">新增地址</el-button>
      <el-table :data="addresses" style="margin-top: 12px">
        <el-table-column prop="receiverName" label="收货人" width="120" />
        <el-table-column prop="receiverPhone" label="电话" width="140" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.addressType === 1 ? 'success' : 'info'" size="small">
              {{ row.addressType === 1 ? '校内' : '校外' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="地址">
          <template #default="{ row }">
            {{ formatAddress(row) }}
          </template>
        </el-table-column>
        <el-table-column label="默认" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isDefault === 1" type="success">默认</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
            <el-button size="small" type="warning" @click="handleDefault(row.id)">设默认</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editForm.id ? '编辑地址' : '新增地址'" width="520px">
      <el-form :model="editForm" label-width="90px">
        <el-form-item label="收货人">
          <el-input v-model="editForm.receiverName" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="editForm.receiverPhone" />
        </el-form-item>
        <el-form-item label="地址类型">
          <el-radio-group v-model="editForm.addressType">
            <el-radio :label="1">校内</el-radio>
            <el-radio :label="2">校外</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="editForm.addressType === 1">
          <el-form-item label="学校">
            <el-input v-model="editForm.school" />
          </el-form-item>
          <el-form-item label="校区">
            <el-input v-model="editForm.campus" />
          </el-form-item>
          <el-form-item label="宿舍楼/驿站">
            <el-input v-model="editForm.building" />
          </el-form-item>
          <el-form-item label="寝室号/取件点">
            <el-input v-model="editForm.room" />
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="省">
            <el-input v-model="editForm.province" />
          </el-form-item>
          <el-form-item label="市">
            <el-input v-model="editForm.city" />
          </el-form-item>
          <el-form-item label="详细地址">
            <el-input v-model="editForm.detailAddress" />
          </el-form-item>
        </template>
        <el-form-item label="取件备注">
          <el-input v-model="editForm.pickupRemark" placeholder="如菜鸟驿站/校门口" />
        </el-form-item>
        <el-form-item label="默认">
          <el-switch v-model="editForm.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  addAddress,
  deleteAddress,
  getAddresses,
  setDefaultAddress,
  updateAddress,
} from '../api/address'

const addresses = ref([])
const dialogVisible = ref(false)
const editForm = reactive({
  id: null,
  receiverName: '',
  receiverPhone: '',
  addressType: 1,
  school: '',
  campus: '',
  building: '',
  room: '',
  province: '',
  city: '',
  detailAddress: '',
  pickupRemark: '',
  isDefault: 0,
})

const unwrapList = (res) => {
  if (res && typeof res === 'object' && 'code' in res) {
    return res.data || []
  }
  return res || []
}

const load = async () => {
  const res = await getAddresses()
  addresses.value = unwrapList(res)
}

const openAdd = () => {
  Object.assign(editForm, {
    id: null,
    receiverName: '',
    receiverPhone: '',
    addressType: 1,
    school: '',
    campus: '',
    building: '',
    room: '',
    province: '',
    city: '',
    detailAddress: '',
    pickupRemark: '',
    isDefault: 0,
  })
  dialogVisible.value = true
}

const openEdit = (row) => {
  Object.assign(editForm, row)
  dialogVisible.value = true
}

const handleSave = async () => {
  try {
    if (!editForm.receiverName.trim()) {
      ElMessage.error('请输入收货人')
      return
    }
    if (!editForm.receiverPhone.trim()) {
      ElMessage.error('请输入电话')
      return
    }
    if (editForm.addressType === 1) {
      if (!editForm.school.trim()) {
        ElMessage.error('请输入学校')
        return
      }
      if (!editForm.campus.trim()) {
        ElMessage.error('请输入校区')
        return
      }
      if (!editForm.building.trim()) {
        ElMessage.error('请输入宿舍楼或驿站')
        return
      }
    } else {
      if (!editForm.province.trim()) {
        ElMessage.error('请输入省')
        return
      }
      if (!editForm.city.trim()) {
        ElMessage.error('请输入市')
        return
      }
      if (!editForm.detailAddress.trim()) {
        ElMessage.error('请输入详细地址')
        return
      }
    }
    const res = editForm.id ? await updateAddress(editForm) : await addAddress(editForm)
    if (res.code !== 200) {
      ElMessage.error(res.message || '保存失败')
      return
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    await load()
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const handleDelete = async (id) => {
  const res = await deleteAddress(id)
  if (res.code !== 200) {
    ElMessage.error(res.message || '删除失败')
    return
  }
  ElMessage.success('删除成功')
  await load()
}

const handleDefault = async (id) => {
  const res = await setDefaultAddress(id)
  if (res.code !== 200) {
    ElMessage.error(res.message || '设置失败')
    return
  }
  ElMessage.success('设置成功')
  await load()
}

const formatAddress = (row) => {
  if (row.addressType === 1) {
    const parts = [row.school, row.campus, row.building, row.room].filter(Boolean)
    const remark = row.pickupRemark ? `（取件备注：${row.pickupRemark}）` : ''
    return parts.join(' ') + remark
  }
  const base = [row.province, row.city, row.detailAddress].filter(Boolean).join('')
  const remark = row.pickupRemark ? `（取件备注：${row.pickupRemark}）` : ''
  return base + remark
}

onMounted(load)
</script>

<style scoped>
.page {
  padding: 12px;
}
.title {
  font-weight: 600;
}
</style>
