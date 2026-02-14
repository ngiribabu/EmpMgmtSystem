import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { Save, ArrowLeft } from 'lucide-react'
import FormField, { Input, Select } from '../common/FormField'
import { getEmployee, createEmployee, updateEmployee, getActiveDepartments, getPositions } from '../../api/endpoints'

const statusOptions = [
  { value: 'ACTIVE', label: 'Active' },
  { value: 'INACTIVE', label: 'Inactive' },
  { value: 'ONLEAVE', label: 'On Leave' },
  { value: 'SUSPENDED', label: 'Suspended' },
  { value: 'TERMINATE', label: 'Terminated' },
]

const genderOptions = [
  { value: '', label: 'Select...' },
  { value: 'M', label: 'Male' },
  { value: 'F', label: 'Female' },
  { value: 'O', label: 'Other' },
]

export default function EmployeeForm() {
  const { id } = useParams()
  const navigate = useNavigate()
  const isEdit = Boolean(id)

  const [form, setForm] = useState({
    firstName: '', lastName: '', middleName: '', email: '',
    hireDate: '', termDate: '', deptId: '', posId: '', managerId: '',
    empStatus: 'ACTIVE', addr1: '', addr2: '', city: '', state: '',
    zipCode: '', country: 'USA', dob: '', gender: '',
  })
  const [departments, setDepartments] = useState([])
  const [positions, setPositions] = useState([])
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    getActiveDepartments().then((r) => setDepartments(r.data)).catch(() => {})
    getPositions().then((r) => setPositions(r.data)).catch(() => {})
    if (isEdit) {
      getEmployee(id).then((r) => {
        const e = r.data
        setForm({
          firstName: e.firstName || '', lastName: e.lastName || '', middleName: e.middleName || '',
          email: e.email || '', hireDate: e.hireDate || '', termDate: e.termDate || '',
          deptId: e.deptId ?? '', posId: e.posId ?? '', managerId: e.managerId ?? '',
          empStatus: e.empStatus || 'ACTIVE', addr1: e.addr1 || '', addr2: e.addr2 || '',
          city: e.city || '', state: e.state || '', zipCode: e.zipCode || '',
          country: e.country || 'USA', dob: e.dob || '', gender: e.gender || '',
        })
      })
    }
  }, [id, isEdit])

  const handleChange = (e) => {
    const { name, value } = e.target
    setForm((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    const payload = {
      ...form,
      deptId: form.deptId ? Number(form.deptId) : null,
      posId: form.posId ? Number(form.posId) : null,
      managerId: form.managerId ? Number(form.managerId) : null,
    }
    try {
      if (isEdit) {
        await updateEmployee(id, payload)
        navigate(`/employees/${id}`)
      } else {
        const res = await createEmployee(payload)
        navigate(`/employees/${res.data.empId}`)
      }
    } catch (err) {
      alert('Error saving employee: ' + (err.response?.data?.message || err.message))
    } finally {
      setSaving(false)
    }
  }

  return (
    <div>
      <button onClick={() => navigate(-1)} className="flex items-center gap-1 text-sm text-gray-500 hover:text-gray-700 mb-4">
        <ArrowLeft size={16} /> Back
      </button>
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-8 max-w-4xl">
        <h2 className="text-xl font-bold text-gray-900 mb-6">{isEdit ? 'Edit Employee' : 'Add New Employee'}</h2>
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <FormField label="First Name" required>
              <Input name="firstName" value={form.firstName} onChange={handleChange} required />
            </FormField>
            <FormField label="Middle Name">
              <Input name="middleName" value={form.middleName} onChange={handleChange} />
            </FormField>
            <FormField label="Last Name" required>
              <Input name="lastName" value={form.lastName} onChange={handleChange} required />
            </FormField>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <FormField label="Email">
              <Input type="email" name="email" value={form.email} onChange={handleChange} />
            </FormField>
            <FormField label="Date of Birth">
              <Input type="date" name="dob" value={form.dob} onChange={handleChange} />
            </FormField>
            <FormField label="Gender">
              <Select name="gender" value={form.gender} onChange={handleChange} options={genderOptions} />
            </FormField>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <FormField label="Department">
              <Select name="deptId" value={form.deptId} onChange={handleChange}
                placeholder="Select Department"
                options={departments.map((d) => ({ value: d.deptId, label: d.deptName }))} />
            </FormField>
            <FormField label="Position">
              <Select name="posId" value={form.posId} onChange={handleChange}
                placeholder="Select Position"
                options={positions.map((p) => ({ value: p.posId, label: p.posTitle }))} />
            </FormField>
            <FormField label="Status" required>
              <Select name="empStatus" value={form.empStatus} onChange={handleChange} options={statusOptions} />
            </FormField>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <FormField label="Hire Date" required>
              <Input type="date" name="hireDate" value={form.hireDate} onChange={handleChange} required />
            </FormField>
            <FormField label="Termination Date">
              <Input type="date" name="termDate" value={form.termDate} onChange={handleChange} />
            </FormField>
          </div>

          <h3 className="text-md font-semibold text-gray-700 mt-6 mb-4 border-t pt-4">Address</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <FormField label="Address Line 1">
              <Input name="addr1" value={form.addr1} onChange={handleChange} />
            </FormField>
            <FormField label="Address Line 2">
              <Input name="addr2" value={form.addr2} onChange={handleChange} />
            </FormField>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <FormField label="City">
              <Input name="city" value={form.city} onChange={handleChange} />
            </FormField>
            <FormField label="State">
              <Input name="state" value={form.state} onChange={handleChange} />
            </FormField>
            <FormField label="Zip Code">
              <Input name="zipCode" value={form.zipCode} onChange={handleChange} />
            </FormField>
            <FormField label="Country">
              <Input name="country" value={form.country} onChange={handleChange} />
            </FormField>
          </div>

          <div className="flex justify-end gap-3 mt-6 pt-4 border-t">
            <button type="button" onClick={() => navigate(-1)}
              className="px-4 py-2 border border-gray-300 rounded-lg text-sm hover:bg-gray-50">Cancel</button>
            <button type="submit" disabled={saving}
              className="flex items-center gap-2 px-6 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700 disabled:opacity-50">
              <Save size={16} /> {saving ? 'Saving...' : 'Save'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
