import api from './axiosClient'

// Dashboard
export const getDashboardStats = () => api.get('/dashboard')

// Departments
export const getDepartments = () => api.get('/departments')
export const getActiveDepartments = () => api.get('/departments/active')
export const getDepartment = (id) => api.get(`/departments/${id}`)
export const createDepartment = (data) => api.post('/departments', data)
export const updateDepartment = (id, data) => api.put(`/departments/${id}`, data)
export const deleteDepartment = (id) => api.delete(`/departments/${id}`)

// Positions
export const getPositions = (deptId) => api.get('/positions', { params: deptId ? { deptId } : {} })
export const getActivePositions = () => api.get('/positions/active')
export const getPosition = (id) => api.get(`/positions/${id}`)
export const createPosition = (data) => api.post('/positions', data)
export const updatePosition = (id, data) => api.put(`/positions/${id}`, data)
export const deletePosition = (id) => api.delete(`/positions/${id}`)

// Employees
export const getEmployees = (params) => api.get('/employees', { params })
export const getEmployee = (id) => api.get(`/employees/${id}`)
export const createEmployee = (data) => api.post('/employees', data)
export const updateEmployee = (id, data) => api.put(`/employees/${id}`, data)
export const deleteEmployee = (id) => api.delete(`/employees/${id}`)
export const getEmployeePhones = (id) => api.get(`/employees/${id}/phones`)
export const getEmployeeDependents = (id) => api.get(`/employees/${id}/dependents`)
export const getEmployeeSalaries = (id) => api.get(`/employees/${id}/salaries`)
export const getEmployeeHistory = (id) => api.get(`/employees/${id}/history`)

// Phones
export const createPhone = (data) => api.post('/phones', data)
export const updatePhone = (id, data) => api.put(`/phones/${id}`, data)
export const deletePhone = (id) => api.delete(`/phones/${id}`)

// Salaries
export const getSalaries = () => api.get('/salaries')
export const createSalary = (data) => api.post('/salaries', data)
export const updateSalary = (id, data) => api.put(`/salaries/${id}`, data)
export const deleteSalary = (id) => api.delete(`/salaries/${id}`)

// Dependents
export const createDependent = (data) => api.post('/dependents', data)
export const updateDependent = (id, data) => api.put(`/dependents/${id}`, data)
export const deleteDependent = (id) => api.delete(`/dependents/${id}`)

// History
export const createHistory = (data) => api.post('/history', data)

// Attendance
export const getAttendance = (empId) => api.get('/attendance', { params: empId ? { empId } : {} })
export const createAttendance = (data) => api.post('/attendance', data)
export const updateAttendance = (id, data) => api.put(`/attendance/${id}`, data)
export const deleteAttendance = (id) => api.delete(`/attendance/${id}`)

// Leave Types
export const getLeaveTypes = () => api.get('/leave-types')
export const getActiveLeaveTypes = () => api.get('/leave-types/active')
export const createLeaveType = (data) => api.post('/leave-types', data)
export const updateLeaveType = (id, data) => api.put(`/leave-types/${id}`, data)
export const deleteLeaveType = (id) => api.delete(`/leave-types/${id}`)

// Leave Requests
export const getLeaveRequests = (params) => api.get('/leave-requests', { params })
export const getPendingLeaveRequests = () => api.get('/leave-requests/pending')
export const createLeaveRequest = (data) => api.post('/leave-requests', data)
export const updateLeaveRequest = (id, data) => api.put(`/leave-requests/${id}`, data)
export const approveLeaveRequest = (id, data) => api.put(`/leave-requests/${id}/approve`, data)
export const rejectLeaveRequest = (id, data) => api.put(`/leave-requests/${id}/reject`, data)
export const deleteLeaveRequest = (id) => api.delete(`/leave-requests/${id}`)
