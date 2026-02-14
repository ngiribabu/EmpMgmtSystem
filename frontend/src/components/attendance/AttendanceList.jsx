import { useState, useEffect } from 'react'
import { Plus, Pencil, Trash2 } from 'lucide-react'
import DataTable from '../common/DataTable'
import StatusBadge from '../common/StatusBadge'
import Modal from '../common/Modal'
import ConfirmDialog from '../common/ConfirmDialog'
import FormField, { Input, Select } from '../common/FormField'
import { getAttendance, createAttendance, updateAttendance, deleteAttendance, getEmployees } from '../../api/endpoints'

const statusOpts = [
  { value: 'PRESENT', label: 'Present' }, { value: 'ABSENT', label: 'Absent' },
  { value: 'HALFDAY', label: 'Half Day' }, { value: 'WFH', label: 'WFH' },
  { value: 'HOLIDAY', label: 'Holiday' }, { value: 'OTHER', label: 'Other' },
]

const empty = { empId: '', workDate: '', clockIn: '', clockOut: '', hrsWorked: '', otHrs: '0', status: 'PRESENT', notes: '' }

export default function AttendanceList() {
  const [items, setItems] = useState([])
  const [employees, setEmployees] = useState([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState({ ...empty })
  const [editId, setEditId] = useState(null)
  const [deleteId, setDeleteId] = useState(null)

  const load = () => {
    setLoading(true)
    Promise.all([getAttendance(), getEmployees()])
      .then(([a, e]) => { setItems(a.data); setEmployees(e.data) })
      .catch(() => {}).finally(() => setLoading(false))
  }
  useEffect(load, [])

  const openNew = () => { setForm({ ...empty }); setEditId(null); setModalOpen(true) }
  const openEdit = (row) => {
    setForm({ empId: row.empId, workDate: row.workDate || '', clockIn: row.clockIn || '', clockOut: row.clockOut || '',
      hrsWorked: row.hrsWorked ?? '', otHrs: row.otHrs ?? '0', status: row.status, notes: row.notes || '' })
    setEditId(row.attendId); setModalOpen(true)
  }

  const handleSave = async () => {
    try {
      const payload = { ...form, empId: Number(form.empId), hrsWorked: form.hrsWorked ? Number(form.hrsWorked) : null, otHrs: Number(form.otHrs || 0),
        clockIn: form.clockIn || null, clockOut: form.clockOut || null }
      if (editId) await updateAttendance(editId, payload)
      else await createAttendance(payload)
      setModalOpen(false); load()
    } catch (err) { alert('Error: ' + (err.response?.data?.message || err.message)) }
  }

  const handleDelete = async () => { try { await deleteAttendance(deleteId); load() } catch {} }

  const empName = (empId) => { const e = employees.find((x) => x.empId === empId); return e ? `${e.firstName} ${e.lastName}` : empId }

  const columns = [
    { key: 'attendId', label: 'ID' },
    { key: 'empId', label: 'Employee', render: (r) => empName(r.empId) },
    { key: 'workDate', label: 'Date' },
    { key: 'clockIn', label: 'Clock In' },
    { key: 'clockOut', label: 'Clock Out' },
    { key: 'hrsWorked', label: 'Hours', accessor: (r) => r.hrsWorked ?? '-' },
    { key: 'status', label: 'Status', render: (r) => <StatusBadge status={r.status} /> },
    { key: 'actions', label: 'Actions', sortable: false, render: (r) => (
      <div className="flex gap-2">
        <button onClick={(e) => { e.stopPropagation(); openEdit(r) }} className="p-1 hover:bg-blue-50 rounded"><Pencil size={15} className="text-blue-600" /></button>
        <button onClick={(e) => { e.stopPropagation(); setDeleteId(r.attendId) }} className="p-1 hover:bg-red-50 rounded"><Trash2 size={15} className="text-red-500" /></button>
      </div>
    )},
  ]

  if (loading) return <div className="flex items-center justify-center h-64"><div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div></div>

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div><h1 className="text-2xl font-bold text-gray-900">Attendance</h1><p className="text-gray-500 text-sm mt-1">Track daily attendance</p></div>
        <button onClick={openNew} className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 text-sm font-medium"><Plus size={16} /> Log Attendance</button>
      </div>
      <DataTable columns={columns} data={items} />

      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title={editId ? 'Edit Attendance' : 'Log Attendance'}>
        <FormField label="Employee" required>
          <Select value={form.empId} onChange={(e) => setForm({ ...form, empId: e.target.value })} placeholder="Select Employee"
            options={employees.map((e) => ({ value: e.empId, label: `${e.firstName} ${e.lastName}` }))} />
        </FormField>
        <FormField label="Work Date" required><Input type="date" value={form.workDate} onChange={(e) => setForm({ ...form, workDate: e.target.value })} /></FormField>
        <div className="grid grid-cols-2 gap-4">
          <FormField label="Clock In"><Input type="time" value={form.clockIn} onChange={(e) => setForm({ ...form, clockIn: e.target.value })} /></FormField>
          <FormField label="Clock Out"><Input type="time" value={form.clockOut} onChange={(e) => setForm({ ...form, clockOut: e.target.value })} /></FormField>
        </div>
        <div className="grid grid-cols-2 gap-4">
          <FormField label="Hours Worked"><Input type="number" step="0.5" value={form.hrsWorked} onChange={(e) => setForm({ ...form, hrsWorked: e.target.value })} /></FormField>
          <FormField label="Overtime Hours"><Input type="number" step="0.5" value={form.otHrs} onChange={(e) => setForm({ ...form, otHrs: e.target.value })} /></FormField>
        </div>
        <FormField label="Status" required><Select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })} options={statusOpts} /></FormField>
        <div className="flex justify-end gap-3 mt-4">
          <button onClick={() => setModalOpen(false)} className="px-4 py-2 border border-gray-300 rounded-lg text-sm">Cancel</button>
          <button onClick={handleSave} className="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700">Save</button>
        </div>
      </Modal>

      <ConfirmDialog isOpen={!!deleteId} onClose={() => setDeleteId(null)} onConfirm={handleDelete}
        title="Delete Record" message="Delete this attendance record?" />
    </div>
  )
}
