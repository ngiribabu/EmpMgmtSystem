import { NavLink } from 'react-router-dom'
import { LayoutDashboard, Users, Building2, Briefcase, Clock, CalendarDays, FileText } from 'lucide-react'

const navItems = [
  { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/employees', label: 'Employees', icon: Users },
  { to: '/departments', label: 'Departments', icon: Building2 },
  { to: '/positions', label: 'Positions', icon: Briefcase },
  { to: '/attendance', label: 'Attendance', icon: Clock },
  { to: '/leave-types', label: 'Leave Types', icon: CalendarDays },
  { to: '/leave-requests', label: 'Leave Requests', icon: FileText },
]

export default function Sidebar() {
  return (
    <aside className="fixed left-0 top-0 h-screen w-64 bg-slate-900 text-white flex flex-col z-30">
      <div className="p-6 border-b border-slate-700">
        <h1 className="text-xl font-bold tracking-tight">EMS</h1>
        <p className="text-xs text-slate-400 mt-1">Employee Management</p>
      </div>
      <nav className="flex-1 py-4 overflow-y-auto">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              `flex items-center gap-3 px-6 py-3 text-sm transition-colors ${
                isActive
                  ? 'bg-blue-600 text-white border-r-4 border-blue-400'
                  : 'text-slate-300 hover:bg-slate-800 hover:text-white'
              }`
            }
          >
            <item.icon size={18} />
            {item.label}
          </NavLink>
        ))}
      </nav>
      <div className="p-4 border-t border-slate-700 text-xs text-slate-500">
        DB2 for IBM i &middot; NGIRI4001
      </div>
    </aside>
  )
}
