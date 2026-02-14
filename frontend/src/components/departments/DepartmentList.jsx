import { useState, useEffect } from 'react'
import { Plus, Pencil, Trash2 } from 'lucide-react'
import DataTable from '../common/DataTable'
import StatusBadge from '../common/StatusBadge'
import Modal from '../common/Modal'
import ConfirmDialog from '../common/ConfirmDialog'
import FormField, { Input, TextArea } from '../common/FormField'
import { getDepartments, createDepartment, updateDepartment, deleteDepartment } from '../../api/endpoints'

const empty = { deptName: '', deptDesc: '', location: '', isActive: 'Y' }

export default function DepartmentList() {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState({ ...empty })
  const [editId, setEditId] = useState(null)
  const [deleteId, setDeleteId] = useState(null)

  const load = () => {
    setLoading(true)
    getDepartments().then((r) => setItems(r.data)).catch(() => {}).finally(() => setLoading(false))
  }
  useEffect(load, [])

  const openNew = () => { setForm({ ...empty }); setEditId(null); setModalOpen(true) }
  const openEdit = (row) => {
    setForm({ deptName: row.deptName, deptDesc: row.deptDesc || '', location: row.location || '', isActive: row.isActive })
    setEditId(row.deptId); setModalOpen(true)
  }

  const handleSave = async () => {
    try {
      if (editId) await updateDepartment(editId, form)
      else await createDepartment(form)
      setModalOpen(false); load()
    } catch (err) { alert('Error: ' + (err.response?.data?.message || err.message)) }
  }

  const handleDelete = async () => {
    try { await deleteDepartment(deleteId); load() } catch (err) { alert('Error deleting') }
  }

  const columns = [
    { key: 'deptId', label: 'ID' },
    { key: 'deptName', label: 'Department Name' },
    { key: 'location', label: 'Location', accessor: (r) => r.location || '-' },
    { key: 'isActive', label: 'Active', render: (r) => <StatusBadge status={r.isActive} /> },
    { key: 'actions', label: 'Actions', sortable: false, render: (r) => (
      <div className="flex gap-2">
        <button onClick={(e) => { e.stopPropagation(); openEdit(r) }} className="p-1 hover:bg-blue-50 rounded"><Pencil size={15} className="text-blue-600" /></button>
        <button onClick={(e) => { e.stopPropagation(); setDeleteId(r.deptId) }} className="p-1 hover:bg-red-50 rounded"><Trash2 size={15} className="text-red-500" /></button>
      </div>
    )},
  ]

  if (loading) return <div className="flex items-center justify-center h-64"><div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div></div>

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div><h1 className="text-2xl font-bold text-gray-900">Departments</h1><p className="text-gray-500 text-sm mt-1">Manage departments</p></div>
        <button onClick={openNew} className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 text-sm font-medium"><Plus size={16} /> Add Department</button>
      </div>
      <DataTable columns={columns} data={items} />

      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title={editId ? 'Edit Department' : 'New Department'}>
        <FormField label="Name" required><Input value={form.deptName} onChange={(e) => setForm({ ...form, deptName: e.target.value })} /></FormField>
        <FormField label="Description"><TextArea value={form.deptDesc} onChange={(e) => setForm({ ...form, deptDesc: e.target.value })} /></FormField>
        <FormField label="Location"><Input value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} /></FormField>
        <div className="flex justify-end gap-3 mt-4">
          <button onClick={() => setModalOpen(false)} className="px-4 py-2 border border-gray-300 rounded-lg text-sm">Cancel</button>
          <button onClick={handleSave} className="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700">Save</button>
        </div>
      </Modal>

      <ConfirmDialog isOpen={!!deleteId} onClose={() => setDeleteId(null)} onConfirm={handleDelete}
        title="Delete Department" message="Are you sure you want to delete this department?" />
    </div>
  )
}
