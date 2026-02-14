import { Routes, Route, Navigate } from 'react-router-dom'
import Layout from './components/layout/Layout'
import Dashboard from './components/dashboard/Dashboard'
import EmployeeList from './components/employees/EmployeeList'
import EmployeeDetail from './components/employees/EmployeeDetail'
import EmployeeForm from './components/employees/EmployeeForm'
import DepartmentList from './components/departments/DepartmentList'
import PositionList from './components/positions/PositionList'
import AttendanceList from './components/attendance/AttendanceList'
import LeaveTypeList from './components/leave/LeaveTypeList'
import LeaveReqList from './components/leave/LeaveReqList'

export default function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/employees" element={<EmployeeList />} />
        <Route path="/employees/new" element={<EmployeeForm />} />
        <Route path="/employees/:id" element={<EmployeeDetail />} />
        <Route path="/employees/:id/edit" element={<EmployeeForm />} />
        <Route path="/departments" element={<DepartmentList />} />
        <Route path="/positions" element={<PositionList />} />
        <Route path="/attendance" element={<AttendanceList />} />
        <Route path="/leave-types" element={<LeaveTypeList />} />
        <Route path="/leave-requests" element={<LeaveReqList />} />
      </Routes>
    </Layout>
  )
}
