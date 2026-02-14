import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft, Pencil, Trash2, Plus, Phone, Users as UsersIcon, DollarSign, History } from 'lucide-react'
import StatusBadge from '../common/StatusBadge'
import Modal from '../common/Modal'
import ConfirmDialog from '../common/ConfirmDialog'
import FormField, { Input, Select } from '../common/FormField'
import {
  getEmployee, deleteEmployee,
  getEmployeePhones, createPhone, updatePhone, deletePhone,
  getEmployeeDependents, createDependent, updateDependent, deleteDependent,
  getEmployeeSalaries, createSalary,
  getEmployeeHistory
} from '../../api/endpoints'

const tabs = [
  { id: 'info', label: 'Info' },
  { id: 'phones', label: 'Phones', icon: Phone },
  { id: 'dependents', label: 'Dependents', icon: UsersIcon },
  { id: 'salaries', label: 'Salary History', icon: DollarSign },
  { id: 'history', label: 'Job History', icon: History },
]

export default function EmployeeDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [emp, setEmp] = useState(null)
  const [activeTab, setActiveTab] = useState('info')
  const [phones, setPhones] = useState([])
  const [dependents, setDependents] = useState([])
  const [salaries, setSalaries] = useState([])
  const [history, setHistory] = useState([])
  const [loading, setLoading] = useState(true)
  const [deleteConfirm, setDeleteConfirm] = useState(false)

  // Phone modal
  const [phoneModal, setPhoneModal] = useState(false)
  const [phoneForm, setPhoneForm] = useState({ phoneType: 'MOBILE', phoneNum: '', isPrimary: 'N' })
  const [phoneEditId, setPhoneEditId] = useState(null)

  // Dependent modal
  const [depModal, setDepModal] = useState(false)
  const [depForm, setDepForm] = useState({ depName: '', relation: 'SPOUSE', dob: '', gender: '' })
  const [depEditId, setDepEditId] = useState(null)

  // Salary modal
  const [salModal, setSalModal] = useState(false)
  const [salForm, setSalForm] = useState({ baseSalary: '', bonus: '0', payFreq: 'ANNUAL', effDate: '', reason: 'HIRE' })

  useEffect(() => {
    setLoading(true)
    Promise.all([
      getEmployee(id),
      getEmployeePhones(id),
      getEmployeeDependents(id),
      getEmployeeSalaries(id),
      getEmployeeHistory(id),
    ]).then(([e, p, d, s, h]) => {
      setEmp(e.data); setPhones(p.data); setDependents(d.data); setSalaries(s.data); setHistory(h.data)
    }).catch(() => {}).finally(() => setLoading(false))
  }, [id])

  const handleDeleteEmp = async () => {
    await deleteEmployee(id)
    navigate('/employees')
  }

  // Phone handlers
  const openPhoneNew = () => { setPhoneForm({ phoneType: 'MOBILE', phoneNum: '', isPrimary: 'N' }); setPhoneEditId(null); setPhoneModal(true) }
  const openPhoneEdit = (p) => { setPhoneForm({ phoneType: p.phoneType, phoneNum: p.phoneNum, isPrimary: p.isPrimary }); setPhoneEditId(p.phoneId); setPhoneModal(true) }
  const savePhone = async () => {
    const payload = { ...phoneForm, empId: Number(id) }
    if (phoneEditId) await updatePhone(phoneEditId, payload)
    else await createPhone(payload)
    setPhoneModal(false)
    getEmployeePhones(id).then((r) => setPhones(r.data))
  }
  const removePhone = async (pid) => { await deletePhone(pid); getEmployeePhones(id).then((r) => setPhones(r.data)) }

  // Dependent handlers
  const openDepNew = () => { setDepForm({ depName: '', relation: 'SPOUSE', dob: '', gender: '' }); setDepEditId(null); setDepModal(true) }
  const openDepEdit = (d) => { setDepForm({ depName: d.depName, relation: d.relation, dob: d.dob || '', gender: d.gender || '' }); setDepEditId(d.depId); setDepModal(true) }
  const saveDep = async () => {
    const payload = { ...depForm, empId: Number(id), gender: depForm.gender || null }
    if (depEditId) await updateDependent(depEditId, payload)
    else await createDependent(payload)
    setDepModal(false)
    getEmployeeDependents(id).then((r) => setDependents(r.data))
  }
  const removeDep = async (did) => { await deleteDependent(did); getEmployeeDependents(id).then((r) => setDependents(r.data)) }

  // Salary handler
  const saveSalary = async () => {
    await createSalary({ ...salForm, empId: Number(id), baseSalary: Number(salForm.baseSalary), bonus: Number(salForm.bonus || 0) })
    setSalModal(false)
    getEmployeeSalaries(id).then((r) => setSalaries(r.data))
  }

  if (loading) return <div className="flex items-center justify-center h-64"><div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div></div>
  if (!emp) return <div className="text-center text-gray-500 py-20">Employee not found</div>

  const fmt = (n) => n != null ? Number(n).toLocaleString('en-US', { style: 'currency', currency: 'USD' }) : '-'

  return (
    <div>
      <button onClick={() => navigate('/employees')} className="flex items-center gap-1 text-sm text-gray-500 hover:text-gray-700 mb-4">
        <ArrowLeft size={16} /> Back to Employees
      </button>

      {/* Header card */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 mb-6">
        <div className="flex items-start justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">{emp.firstName} {emp.middleName ? emp.middleName + ' ' : ''}{emp.lastName}</h1>
            <p className="text-gray-500 mt-1">{emp.position?.posTitle || 'No Position'} &middot; {emp.department?.deptName || 'No Department'}</p>
            <div className="flex gap-3 mt-3">
              <StatusBadge status={emp.empStatus} />
              {emp.email && <span className="text-sm text-gray-500">{emp.email}</span>}
            </div>
          </div>
          <div className="flex gap-2">
            <button onClick={() => navigate(`/employees/${id}/edit`)} className="flex items-center gap-1 px-3 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700">
              <Pencil size={14} /> Edit
            </button>
            <button onClick={() => setDeleteConfirm(true)} className="flex items-center gap-1 px-3 py-2 bg-red-50 text-red-600 rounded-lg text-sm hover:bg-red-100">
              <Trash2 size={14} /> Delete
            </button>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-1 mb-6 bg-white rounded-lg p-1 shadow-sm border border-gray-100">
        {tabs.map((tab) => (
          <button key={tab.id} onClick={() => setActiveTab(tab.id)}
            className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${
              activeTab === tab.id ? 'bg-blue-600 text-white' : 'text-gray-600 hover:bg-gray-100'
            }`}>
            {tab.label}
          </button>
        ))}
      </div>

      {/* Tab content */}
      {activeTab === 'info' && (
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div><span className="text-xs text-gray-400 uppercase">Employee ID</span><p className="font-medium text-gray-800">{emp.empId}</p></div>
            <div><span className="text-xs text-gray-400 uppercase">Hire Date</span><p className="font-medium text-gray-800">{emp.hireDate || '-'}</p></div>
            <div><span className="text-xs text-gray-400 uppercase">Gender</span><p className="font-medium text-gray-800">{emp.gender === 'M' ? 'Male' : emp.gender === 'F' ? 'Female' : emp.gender || '-'}</p></div>
            <div><span className="text-xs text-gray-400 uppercase">Date of Birth</span><p className="font-medium text-gray-800">{emp.dob || '-'}</p></div>
            <div><span className="text-xs text-gray-400 uppercase">Country</span><p className="font-medium text-gray-800">{emp.country || '-'}</p></div>
            <div><span className="text-xs text-gray-400 uppercase">City/State</span><p className="font-medium text-gray-800">{[emp.city, emp.state].filter(Boolean).join(', ') || '-'}</p></div>
            <div className="md:col-span-2"><span className="text-xs text-gray-400 uppercase">Address</span><p className="font-medium text-gray-800">{[emp.addr1, emp.addr2].filter(Boolean).join(', ') || '-'}</p></div>
            <div><span className="text-xs text-gray-400 uppercase">Zip Code</span><p className="font-medium text-gray-800">{emp.zipCode || '-'}</p></div>
          </div>
        </div>
      )}

      {activeTab === 'phones' && (
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <div className="flex justify-between items-center mb-4">
            <h3 className="font-semibold text-gray-800">Phone Numbers</h3>
            <button onClick={openPhoneNew} className="flex items-center gap-1 px-3 py-1.5 bg-blue-600 text-white rounded-lg text-xs hover:bg-blue-700"><Plus size={14} /> Add</button>
          </div>
          {phones.length === 0 ? <p className="text-gray-400 text-sm">No phone numbers recorded</p> : (
            <table className="w-full text-sm">
              <thead><tr className="border-b"><th className="text-left py-2 text-gray-500">Type</th><th className="text-left py-2 text-gray-500">Number</th><th className="text-left py-2 text-gray-500">Primary</th><th className="py-2"></th></tr></thead>
              <tbody>{phones.map((p) => (
                <tr key={p.phoneId} className="border-b border-gray-50">
                  <td className="py-2">{p.phoneType}</td><td className="py-2">{p.phoneNum}</td>
                  <td className="py-2"><StatusBadge status={p.isPrimary} /></td>
                  <td className="py-2 text-right">
                    <button onClick={() => openPhoneEdit(p)} className="p-1 hover:bg-blue-50 rounded"><Pencil size={14} className="text-blue-600" /></button>
                    <button onClick={() => removePhone(p.phoneId)} className="p-1 hover:bg-red-50 rounded"><Trash2 size={14} className="text-red-500" /></button>
                  </td>
                </tr>
              ))}</tbody>
            </table>
          )}
        </div>
      )}

      {activeTab === 'dependents' && (
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <div className="flex justify-between items-center mb-4">
            <h3 className="font-semibold text-gray-800">Dependents</h3>
            <button onClick={openDepNew} className="flex items-center gap-1 px-3 py-1.5 bg-blue-600 text-white rounded-lg text-xs hover:bg-blue-700"><Plus size={14} /> Add</button>
          </div>
          {dependents.length === 0 ? <p className="text-gray-400 text-sm">No dependents recorded</p> : (
            <table className="w-full text-sm">
              <thead><tr className="border-b"><th className="text-left py-2 text-gray-500">Name</th><th className="text-left py-2 text-gray-500">Relationship</th><th className="text-left py-2 text-gray-500">DOB</th><th className="py-2"></th></tr></thead>
              <tbody>{dependents.map((d) => (
                <tr key={d.depId} className="border-b border-gray-50">
                  <td className="py-2">{d.depName}</td><td className="py-2">{d.relation}</td><td className="py-2">{d.dob || '-'}</td>
                  <td className="py-2 text-right">
                    <button onClick={() => openDepEdit(d)} className="p-1 hover:bg-blue-50 rounded"><Pencil size={14} className="text-blue-600" /></button>
                    <button onClick={() => removeDep(d.depId)} className="p-1 hover:bg-red-50 rounded"><Trash2 size={14} className="text-red-500" /></button>
                  </td>
                </tr>
              ))}</tbody>
            </table>
          )}
        </div>
      )}

      {activeTab === 'salaries' && (
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <div className="flex justify-between items-center mb-4">
            <h3 className="font-semibold text-gray-800">Salary History</h3>
            <button onClick={() => { setSalForm({ baseSalary: '', bonus: '0', payFreq: 'ANNUAL', effDate: '', reason: 'HIRE' }); setSalModal(true) }}
              className="flex items-center gap-1 px-3 py-1.5 bg-blue-600 text-white rounded-lg text-xs hover:bg-blue-700"><Plus size={14} /> Add Salary</button>
          </div>
          {salaries.length === 0 ? <p className="text-gray-400 text-sm">No salary records</p> : (
            <table className="w-full text-sm">
              <thead><tr className="border-b"><th className="text-left py-2 text-gray-500">Effective</th><th className="text-left py-2 text-gray-500">Base Salary</th><th className="text-left py-2 text-gray-500">Bonus</th><th className="text-left py-2 text-gray-500">Frequency</th><th className="text-left py-2 text-gray-500">Reason</th><th className="text-left py-2 text-gray-500">Current</th></tr></thead>
              <tbody>{salaries.map((s) => (
                <tr key={s.salaryId} className={`border-b border-gray-50 ${s.isCurrent === 'Y' ? 'bg-green-50' : ''}`}>
                  <td className="py-2">{s.effDate}</td><td className="py-2 font-medium">{fmt(s.baseSalary)}</td><td className="py-2">{fmt(s.bonus)}</td>
                  <td className="py-2">{s.payFreq}</td><td className="py-2">{s.reason || '-'}</td><td className="py-2"><StatusBadge status={s.isCurrent} /></td>
                </tr>
              ))}</tbody>
            </table>
          )}
        </div>
      )}

      {activeTab === 'history' && (
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <h3 className="font-semibold text-gray-800 mb-4">Employment History</h3>
          {history.length === 0 ? <p className="text-gray-400 text-sm">No job history records</p> : (
            <div className="space-y-4">
              {history.map((h) => (
                <div key={h.histId} className="flex gap-4 p-4 bg-gray-50 rounded-lg">
                  <div className="w-2 bg-blue-500 rounded-full flex-shrink-0"></div>
                  <div>
                    <div className="flex items-center gap-2 mb-1">
                      <span className="font-medium text-gray-800">{h.changeType}</span>
                      <span className="text-xs text-gray-400">{h.effDate}</span>
                    </div>
                    {h.notes && <p className="text-sm text-gray-600">{h.notes}</p>}
                    {(h.oldSalary || h.newSalary) && <p className="text-xs text-gray-500 mt-1">Salary: {fmt(h.oldSalary)} &rarr; {fmt(h.newSalary)}</p>}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Phone Modal */}
      <Modal isOpen={phoneModal} onClose={() => setPhoneModal(false)} title={phoneEditId ? 'Edit Phone' : 'Add Phone'} size="sm">
        <FormField label="Type"><Select value={phoneForm.phoneType} onChange={(e) => setPhoneForm({ ...phoneForm, phoneType: e.target.value })}
          options={[{value:'HOME',label:'Home'},{value:'MOBILE',label:'Mobile'},{value:'WORK',label:'Work'},{value:'FAX',label:'Fax'},{value:'OTHER',label:'Other'}]} /></FormField>
        <FormField label="Number" required><Input value={phoneForm.phoneNum} onChange={(e) => setPhoneForm({ ...phoneForm, phoneNum: e.target.value })} /></FormField>
        <FormField label="Primary"><Select value={phoneForm.isPrimary} onChange={(e) => setPhoneForm({ ...phoneForm, isPrimary: e.target.value })} options={[{value:'Y',label:'Yes'},{value:'N',label:'No'}]} /></FormField>
        <div className="flex justify-end gap-3 mt-4">
          <button onClick={() => setPhoneModal(false)} className="px-4 py-2 border border-gray-300 rounded-lg text-sm">Cancel</button>
          <button onClick={savePhone} className="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700">Save</button>
        </div>
      </Modal>

      {/* Dependent Modal */}
      <Modal isOpen={depModal} onClose={() => setDepModal(false)} title={depEditId ? 'Edit Dependent' : 'Add Dependent'} size="sm">
        <FormField label="Name" required><Input value={depForm.depName} onChange={(e) => setDepForm({ ...depForm, depName: e.target.value })} /></FormField>
        <FormField label="Relationship"><Select value={depForm.relation} onChange={(e) => setDepForm({ ...depForm, relation: e.target.value })}
          options={[{value:'SPOUSE',label:'Spouse'},{value:'CHILD',label:'Child'},{value:'PARENT',label:'Parent'},{value:'SIBLING',label:'Sibling'},{value:'OTHER',label:'Other'}]} /></FormField>
        <FormField label="Date of Birth"><Input type="date" value={depForm.dob} onChange={(e) => setDepForm({ ...depForm, dob: e.target.value })} /></FormField>
        <div className="flex justify-end gap-3 mt-4">
          <button onClick={() => setDepModal(false)} className="px-4 py-2 border border-gray-300 rounded-lg text-sm">Cancel</button>
          <button onClick={saveDep} className="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700">Save</button>
        </div>
      </Modal>

      {/* Salary Modal */}
      <Modal isOpen={salModal} onClose={() => setSalModal(false)} title="Add Salary Record" size="sm">
        <FormField label="Base Salary" required><Input type="number" step="0.01" value={salForm.baseSalary} onChange={(e) => setSalForm({ ...salForm, baseSalary: e.target.value })} /></FormField>
        <FormField label="Bonus"><Input type="number" step="0.01" value={salForm.bonus} onChange={(e) => setSalForm({ ...salForm, bonus: e.target.value })} /></FormField>
        <FormField label="Effective Date" required><Input type="date" value={salForm.effDate} onChange={(e) => setSalForm({ ...salForm, effDate: e.target.value })} /></FormField>
        <FormField label="Reason"><Select value={salForm.reason} onChange={(e) => setSalForm({ ...salForm, reason: e.target.value })}
          options={[{value:'HIRE',label:'Hire'},{value:'PROMOTION',label:'Promotion'},{value:'MERIT',label:'Merit'},{value:'ADJUST',label:'Adjustment'},{value:'TRANSFER',label:'Transfer'},{value:'OTHER',label:'Other'}]} /></FormField>
        <div className="flex justify-end gap-3 mt-4">
          <button onClick={() => setSalModal(false)} className="px-4 py-2 border border-gray-300 rounded-lg text-sm">Cancel</button>
          <button onClick={saveSalary} className="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700">Save</button>
        </div>
      </Modal>

      <ConfirmDialog isOpen={deleteConfirm} onClose={() => setDeleteConfirm(false)} onConfirm={handleDeleteEmp}
        title="Delete Employee" message={`Are you sure you want to delete ${emp.firstName} ${emp.lastName}? This action cannot be undone.`} />
    </div>
  )
}
