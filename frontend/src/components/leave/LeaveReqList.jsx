import { useState, useEffect } from 'react'
import { Plus, Check, X, Trash2 } from 'lucide-react'
import DataTable from '../common/DataTable'
import StatusBadge from '../common/StatusBadge'
import Modal from '../common/Modal'
import ConfirmDialog from '../common/ConfirmDialog'
import FormField, { Input, Select, TextArea } from '../common/FormField'
import { getLeaveRequests, createLeaveRequest, deleteLeaveRequest, approveLeaveRequest, rejectLeaveRequest, getEmployees, getActiveLeaveTypes } from '../../api/endpoints'

const empty = { empId: '', lvTypeId: '', startDate: '', endDate: '', totalDays: 1, reason: '' }

export default function LeaveReqList() {
  const [items, setItems] = useState([])
  const [employees, setEmployees] = useState([])
  const [leaveTypes, setLeaveTypes] = useState([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState({ ...empty })
  const [deleteId, setDeleteId] = useState(null)

  const load = () => {
    setLoading(true)
    Promise.all([getLeaveRequests(), getEmployees(), getActiveLeaveTypes()])
      .then(([r, e, lt]) => { setItems(r.data); setEmployees(e.data); setLeaveTypes(lt.data) })
      .catch(() => {}).finally(() => setLoading(false))
  }
  useEffect(load, [])

  const empName = (empId) => { const e = employees.find((x) => x.empId === empId); return e ? `${e.firstName} ${e.lastName}` : empId }

  const handleCreate = async () => {
    try {
      await createLeaveRequest({ ...form, empId: Number(form.empId), lvTypeId: Number(form.lvTypeId), totalDays: Number(form.totalDays) })
      setModalOpen(false); load()
    } catch (err) { alert('Error: ' + (err.response?.data?.message || err.message)) }
  }

  const handleApprove = async (id) => {
    try { await approveLeaveRequest(id, { approverId: 1000, comments: 'Approved' }); load() } catch {}
  }
  const handleReject = async (id) => {
    try { await rejectLeaveRequest(id, { approverId: 1000, comments: 'Rejected' }); load() } catch {}
  }
  const handleDelete = async () => { try { await deleteLeaveRequest(deleteId); load() } catch {} }

  const columns = [
    { key: 'lvReqId', label: 'ID' },
    { key: 'empId', label: 'Employee', render: (r) => empName(r.empId) },
    { key: 'leaveType', label: 'Type', accessor: (r) => r.leaveType?.lvTypeName || '-' },
    { key: 'startDate', label: 'Start' },
    { key: 'endDate', label: 'End' },
    { key: 'totalDays', label: 'Days' },
    { key: 'status', label: 'Status', render: (r) => <StatusBadge status={r.status} /> },
    { key: 'actions', label: 'Actions', sortable: false, render: (r) => (
      <div className="flex gap-1">
        {r.status === 'PENDING' && (
          <>
            <button onClick={(e) => { e.stopPropagation(); handleApprove(r.lvReqId) }} className="p-1 hover:bg-green-50 rounded" title="Approve"><Check size={15} className="text-green-600" /></button>
            <button onClick={(e) => { e.stopPropagation(); handleReject(r.lvReqId) }} className="p-1 hover:bg-red-50 rounded" title="Reject"><X size={15} className="text-red-500" /></button>
          </>
        )}
        <button onClick={(e) => { e.stopPropagation(); setDeleteId(r.lvReqId) }} className="p-1 hover:bg-red-50 rounded"><Trash2 size={15} className="text-red-500" /></button>
      </div>
    )},
  ]

  if (loading) return <div className="flex items-center justify-center h-64"><div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div></div>

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div><h1 className="text-2xl font-bold text-gray-900">Leave Requests</h1><p className="text-gray-500 text-sm mt-1">Manage leave requests</p></div>
        <button onClick={() => { setForm({ ...empty }); setModalOpen(true) }} className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 text-sm font-medium"><Plus size={16} /> New Request</button>
      </div>
      <DataTable columns={columns} data={items} />

      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title="New Leave Request">
        <FormField label="Employee" required>
          <Select value={form.empId} onChange={(e) => setForm({ ...form, empId: e.target.value })} placeholder="Select Employee"
            options={employees.map((e) => ({ value: e.empId, label: `${e.firstName} ${e.lastName}` }))} />
        </FormField>
        <FormField label="Leave Type" required>
          <Select value={form.lvTypeId} onChange={(e) => setForm({ ...form, lvTypeId: e.target.value })} placeholder="Select Type"
            options={leaveTypes.map((lt) => ({ value: lt.lvTypeId, label: lt.lvTypeName }))} />
        </FormField>
        <div className="grid grid-cols-2 gap-4">
          <FormField label="Start Date" required><Input type="date" value={form.startDate} onChange={(e) => setForm({ ...form, startDate: e.target.value })} /></FormField>
          <FormField label="End Date" required><Input type="date" value={form.endDate} onChange={(e) => setForm({ ...form, endDate: e.target.value })} /></FormField>
        </div>
        <FormField label="Total Days" required><Input type="number" step="0.5" value={form.totalDays} onChange={(e) => setForm({ ...form, totalDays: e.target.value })} /></FormField>
        <FormField label="Reason"><TextArea value={form.reason} onChange={(e) => setForm({ ...form, reason: e.target.value })} /></FormField>
        <div className="flex justify-end gap-3 mt-4">
          <button onClick={() => setModalOpen(false)} className="px-4 py-2 border border-gray-300 rounded-lg text-sm">Cancel</button>
          <button onClick={handleCreate} className="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700">Submit</button>
        </div>
      </Modal>
      <ConfirmDialog isOpen={!!deleteId} onClose={() => setDeleteId(null)} onConfirm={handleDelete} title="Delete Request" message="Delete this leave request?" />
    </div>
  )
}
