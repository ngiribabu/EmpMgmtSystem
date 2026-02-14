import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Plus } from 'lucide-react'
import DataTable from '../common/DataTable'
import StatusBadge from '../common/StatusBadge'
import { getEmployees } from '../../api/endpoints'

export default function EmployeeList() {
  const [employees, setEmployees] = useState([])
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()

  useEffect(() => {
    getEmployees()
      .then((res) => setEmployees(res.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const columns = [
    { key: 'empId', label: 'ID' },
    { key: 'name', label: 'Name', accessor: (r) => `${r.firstName} ${r.lastName}` },
    { key: 'email', label: 'Email' },
    { key: 'department', label: 'Department', accessor: (r) => r.department?.deptName || '-' },
    { key: 'position', label: 'Position', accessor: (r) => r.position?.posTitle || '-' },
    { key: 'hireDate', label: 'Hire Date' },
    { key: 'empStatus', label: 'Status', render: (r) => <StatusBadge status={r.empStatus} /> },
  ]

  if (loading) {
    return <div className="flex items-center justify-center h-64"><div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div></div>
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Employees</h1>
          <p className="text-gray-500 text-sm mt-1">Manage employee records</p>
        </div>
        <button
          onClick={() => navigate('/employees/new')}
          className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium"
        >
          <Plus size={16} /> Add Employee
        </button>
      </div>
      <DataTable
        columns={columns}
        data={employees}
        onRowClick={(row) => navigate(`/employees/${row.empId}`)}
        searchPlaceholder="Search by name or email..."
      />
    </div>
  )
}
