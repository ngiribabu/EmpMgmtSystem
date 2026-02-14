import { useState, useEffect } from 'react'
import { Plus, Pencil, Trash2 } from 'lucide-react'
import DataTable from '../common/DataTable'
import StatusBadge from '../common/StatusBadge'
import Modal from '../common/Modal'
import ConfirmDialog from '../common/ConfirmDialog'
import FormField, { Input, Select, TextArea } from '../common/FormField'
import { getLeaveTypes, createLeaveType, updateLeaveType, deleteLeaveType } from '../../api/endpoints'

const empty = { lvTypeName: '', lvTypeDesc: '', maxDays: 0, isPaid: 'Y', isActive: 'Y' }

export default function LeaveTypeList() {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState({ ...empty })
  const [editId, setEditId] = useState(null)
  const [deleteId, setDeleteId] = useState(null)

  const load = () => { setLoading(true); getLeaveTypes().then((r) => setItems(r.data)).catch(() => {}).finally(() => setLoading(false)) }
  useEffect(load, [])

  const openNew = () => { setForm({ ...empty }); setEditId(null); setModalOpen(true) }
  const openEdit = (row) => {
    setForm({ lvTypeName: row.lvTypeName, lvTypeDesc: row.lvTypeDesc || '', maxDays: row.maxDays, isPaid: row.isPaid, isActive: row.isActive })
    setEditId(row.lvTypeId); setModalOpen(true)
  }

  const handleSave = async () => {
    try {
      const payload = { ...form, maxDays: Number(form.maxDays) }
      if (editId) await updateLeaveType(editId, payload)
      else await createLeaveType(payload)
      setModalOpen(false); load()
    } catch (err) { alert('Error: ' + (err.response?.data?.message || err.message)) }
  }

  const handleDelete = async () => { try { await deleteLeaveType(deleteId); load() } catch {} }

  const columns = [
    { key: 'lvTypeId', label: 'ID' },
    { key: 'lvTypeName', label: 'Leave Type' },
    { key: 'maxDays', label: 'Max Days/Year' },
    { key: 'isPaid', label: 'Paid', render: (r) => <StatusBadge status={r.isPaid} /> },
    { key: 'isActive', label: 'Active', render: (r) => <StatusBadge status={r.isActive} /> },
    { key: 'actions', label: 'Actions', sortable: false, render: (r) => (
      <div className="flex gap-2">
        <button onClick={(e) => { e.stopPropagation(); openEdit(r) }} className="p-1 hover:bg-blue-50 rounded"><Pencil size={15} className="text-blue-600" /></button>
        <button onClick={(e) => { e.stopPropagation(); setDeleteId(r.lvTypeId) }} className="p-1 hover:bg-red-50 rounded"><Trash2 size={15} className="text-red-500" /></button>
      </div>
    )},
  ]

  if (loading) return <div className="flex items-center justify-center h-64"><div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div></div>

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div><h1 className="text-2xl font-bold text-gray-900">Leave Types</h1><p className="text-gray-500 text-sm mt-1">Configure leave categories</p></div>
        <button onClick={openNew} className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 text-sm font-medium"><Plus size={16} /> Add Leave Type</button>
      </div>
      <DataTable columns={columns} data={items} />

      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title={editId ? 'Edit Leave Type' : 'New Leave Type'}>
        <FormField label="Name" required><Input value={form.lvTypeName} onChange={(e) => setForm({ ...form, lvTypeName: e.target.value })} /></FormField>
        <FormField label="Description"><TextArea value={form.lvTypeDesc} onChange={(e) => setForm({ ...form, lvTypeDesc: e.target.value })} /></FormField>
        <div className="grid grid-cols-2 gap-4">
          <FormField label="Max Days/Year"><Input type="number" value={form.maxDays} onChange={(e) => setForm({ ...form, maxDays: e.target.value })} /></FormField>
          <FormField label="Paid Leave"><Select value={form.isPaid} onChange={(e) => setForm({ ...form, isPaid: e.target.value })} options={[{value:'Y',label:'Yes'},{value:'N',label:'No'}]} /></FormField>
        </div>
        <div className="flex justify-end gap-3 mt-4">
          <button onClick={() => setModalOpen(false)} className="px-4 py-2 border border-gray-300 rounded-lg text-sm">Cancel</button>
          <button onClick={handleSave} className="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700">Save</button>
        </div>
      </Modal>
      <ConfirmDialog isOpen={!!deleteId} onClose={() => setDeleteId(null)} onConfirm={handleDelete} title="Delete Leave Type" message="Delete this leave type?" />
    </div>
  )
}
