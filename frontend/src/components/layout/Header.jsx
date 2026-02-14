import { useLocation } from 'react-router-dom'

const titles = {
  '/dashboard': 'Dashboard',
  '/employees': 'Employees',
  '/departments': 'Departments',
  '/positions': 'Positions',
  '/attendance': 'Attendance',
  '/leave-types': 'Leave Types',
  '/leave-requests': 'Leave Requests',
}

export default function Header() {
  const location = useLocation()
  const basePath = '/' + location.pathname.split('/')[1]
  const title = titles[basePath] || 'Employee Management System'

  return (
    <header className="h-16 bg-white border-b border-gray-200 flex items-center px-8 shadow-sm">
      <h2 className="text-lg font-semibold text-gray-800">{title}</h2>
    </header>
  )
}
