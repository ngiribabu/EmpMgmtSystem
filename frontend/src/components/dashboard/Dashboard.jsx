import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Users, Building2, Briefcase, FileText, UserPlus, Clock } from 'lucide-react'
import { getDashboardStats } from '../../api/endpoints'

export default function Dashboard() {
  const [stats, setStats] = useState({})
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()

  useEffect(() => {
    getDashboardStats()
      .then((res) => setStats(res.data))
      .catch(() => setStats({}))
      .finally(() => setLoading(false))
  }, [])

  const cards = [
    { label: 'Total Employees', value: stats.totalEmployees, icon: Users, color: 'bg-blue-500', link: '/employees' },
    { label: 'Active Employees', value: stats.activeEmployees, icon: UserPlus, color: 'bg-green-500', link: '/employees' },
    { label: 'Departments', value: stats.totalDepartments, icon: Building2, color: 'bg-purple-500', link: '/departments' },
    { label: 'Positions', value: stats.totalPositions, icon: Briefcase, color: 'bg-indigo-500', link: '/positions' },
    { label: 'Pending Leaves', value: stats.pendingLeaveRequests, icon: FileText, color: 'bg-amber-500', link: '/leave-requests' },
  ]

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900">Welcome to EMS</h1>
        <p className="text-gray-500 mt-1">Employee Management System Overview</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-6 mb-8">
        {cards.map((card) => (
          <div
            key={card.label}
            onClick={() => navigate(card.link)}
            className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 cursor-pointer hover:shadow-md transition-shadow"
          >
            <div className="flex items-center justify-between mb-4">
              <div className={`p-3 rounded-lg ${card.color}`}>
                <card.icon size={20} className="text-white" />
              </div>
            </div>
            <p className="text-2xl font-bold text-gray-900">{card.value ?? '-'}</p>
            <p className="text-sm text-gray-500 mt-1">{card.label}</p>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <h3 className="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
            <Clock size={18} /> Quick Actions
          </h3>
          <div className="space-y-3">
            <button onClick={() => navigate('/employees/new')} className="w-full text-left px-4 py-3 bg-blue-50 text-blue-700 rounded-lg hover:bg-blue-100 transition-colors text-sm font-medium">
              + Add New Employee
            </button>
            <button onClick={() => navigate('/leave-requests')} className="w-full text-left px-4 py-3 bg-amber-50 text-amber-700 rounded-lg hover:bg-amber-100 transition-colors text-sm font-medium">
              View Pending Leave Requests ({stats.pendingLeaveRequests ?? 0})
            </button>
            <button onClick={() => navigate('/attendance')} className="w-full text-left px-4 py-3 bg-green-50 text-green-700 rounded-lg hover:bg-green-100 transition-colors text-sm font-medium">
              Log Attendance
            </button>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <h3 className="text-lg font-semibold text-gray-800 mb-4">System Info</h3>
          <div className="space-y-3 text-sm">
            <div className="flex justify-between py-2 border-b border-gray-50">
              <span className="text-gray-500">Database</span>
              <span className="font-medium text-gray-700">DB2 for IBM i</span>
            </div>
            <div className="flex justify-between py-2 border-b border-gray-50">
              <span className="text-gray-500">Host</span>
              <span className="font-medium text-gray-700">PUB400.COM</span>
            </div>
            <div className="flex justify-between py-2 border-b border-gray-50">
              <span className="text-gray-500">Library</span>
              <span className="font-medium text-gray-700">NGIRI4001</span>
            </div>
            <div className="flex justify-between py-2">
              <span className="text-gray-500">Tables</span>
              <span className="font-medium text-gray-700">10</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
