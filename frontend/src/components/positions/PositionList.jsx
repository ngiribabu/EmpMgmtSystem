import { useState, useEffect } from 'react'
import { Plus, Pencil, Trash2 } from 'lucide-react'
import DataTable from '../common/DataTable'
import StatusBadge from '../common/StatusBadge'
import Modal from '../common/Modal'
import ConfirmDialog from '../common/ConfirmDialog'
import FormField, { Input, Select, TextArea } from '../common/FormField'
import { getPositions, createPosition, updatePosition, deletePosition, getActiveDepartments } from '../../api/endpoints'

const empty = { posTitle: '', posDesc: '', deptId: '', minSalary: 0, maxSalary: 0, isActive: 'Y' }

export default function PositionList() {
  const [items, setItems] = useState([])
  const [departments, setDepartments] = useState([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState({ ...empty })
  const [editId, setEditId] = useState(null)
  const [deleteId, setDeleteId] = useState(null)

  const load = () => {
    setLoading(true)
    Promise.all([getPositions(), getActiveDepartments()])
      .then(([p, d]) => { setItems(p.data); setDepartments(d.data) })
      .catch(() => {}).finally(() => setLoading(false))
  }
  useEffect(load, [])

  const openNew = () => { setForm({ ...empty }); setEditId(null); setModalOpen(true) }
  const openEdit = (row) => {
    setForm({ posTitle: row.posTitle, posDesc: row.posDesc || '', deptId: row.deptId, minSalary: row.minSalary, maxSalary: row.maxSalary, isActive: row.isActive })
    setEditId(row.posId); setModalOpen(true)
  }

  const handleSave = async () => {
    try {
      const payload = { ...form, deptId: Number(form.deptId), minSalary: Number(form.minSalary), maxSalary: Number(form.maxSalary) }
      if (editId) await updatePosition(editId, payload)
      else await createPosition(payload)
      setModalOpen(false); load()
    } catch (err) { alert('Error: ' + (err.response?.data?.message || err.message)) }
  }

  const handleDelete = async () => {
    try { await deletePosition(deleteId); load() } catch (err) { alert('Error deleting') }
  }

  const fmt = (n) => n != null ? Number(n).toLocaleString('en-US', { style: 'currency', currency: 'USD', maximumFractionDigits: 0 }) : '-'

  const columns = [
    { key: 'posId', label: 'ID' },
    { key: 'posTitle', label: 'Title' },
    { key: 'department', label: 'Department', accessor: (r) => r.department?.deptName || '-' },
    { key: 'minSalary', label: 'Min Salary', render: (r) => fmt(r.minSalary) },
    { key: 'maxSalary', label: 'Max Salary', render: (r) => fmt(r.maxSalary) },
    { key: 'isActive', label: 'Active', render: (r) => <StatusBadge status={r.isActive} /> },
    { key: 'actions', label: 'Actions', sortable: false, render: (r) => (
      <div className="flex gap-2">
        <button onClick={(e) => { e.stopPropagation(); openEdit(r) }} className="p-1 hover:bg-blue-50 rounded"><Pencil size={15} className="text-blue-600" /></button>
        <button onClick={(e) => { e.stopPropagation(); setDeleteId(r.posId) }} className="p-1 hover:bg-red-50 rounded"><Trash2 size={15} className="text-red-500" /></button>
      </div>
    )},
  ]

  if (loading) return <div className="flex items-center justify-center h-64"><div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div></div>

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div><h1 className="text-2xl font-bold text-gray-900">Positions</h1><p className="text-gray-500 text-sm mt-1">Manage job positions</p></div>
        <button onClick={openNew} className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 text-sm font-medium"><Plus size={16} /> Add Position</button>
      </div>
      <DataTable columns={columns} data={items} />

      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title={editId ? 'Edit Position' : 'New Position'}>
        <FormField label="Title" required><Input value={form.posTitle} onChange={(e) => setForm({ ...form, posTitle: e.target.value })} /></FormField>
        <FormField label="Description"><TextArea value={form.posDesc} onChange={(e) => setForm({ ...form, posDesc: e.target.value })} /></FormField>
        <FormField label="Department" required>
          <Select value={form.deptId} onChange={(e) => setForm({ ...form, deptId: e.target.value })} placeholder="Select Department"
            options={departments.map((d) => ({ value: d.deptId, label: d.deptName }))} />
        </FormField>
        <div className="grid grid-cols-2 gap-4">
          <FormField label="Min Salary"><Input type="number" value={form.minSalary} onChange={(e) => setForm({ ...form, minSalary: e.target.value })} /></FormField>
          <FormField label="Max Salary"><Input type="number" value={form.maxSalary} onChange={(e) => setForm({ ...form, maxSalary: e.target.value })} /></FormField>
        </div>
        <div className="flex justify-end gap-3 mt-4">
          <button onClick={() => setModalOpen(false)} className="px-4 py-2 border border-gray-300 rounded-lg text-sm">Cancel</button>
          <button onClick={handleSave} className="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700">Save</button>
        </div>
      </Modal>

      <ConfirmDialog isOpen={!!deleteId} onClose={() => setDeleteId(null)} onConfirm={handleDelete}
        title="Delete Position" message="Are you sure you want to delete this position?" />
    </div>
  )
}
