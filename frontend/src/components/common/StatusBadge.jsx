const colors = {
  ACTIVE: 'bg-green-100 text-green-700',
  INACTIVE: 'bg-gray-100 text-gray-600',
  TERMINATE: 'bg-red-100 text-red-700',
  ONLEAVE: 'bg-yellow-100 text-yellow-700',
  SUSPENDED: 'bg-orange-100 text-orange-700',
  PENDING: 'bg-yellow-100 text-yellow-700',
  APPROVED: 'bg-green-100 text-green-700',
  REJECTED: 'bg-red-100 text-red-700',
  CANCELLED: 'bg-gray-100 text-gray-600',
  PRESENT: 'bg-green-100 text-green-700',
  ABSENT: 'bg-red-100 text-red-700',
  WFH: 'bg-blue-100 text-blue-700',
  HALFDAY: 'bg-yellow-100 text-yellow-700',
  HOLIDAY: 'bg-purple-100 text-purple-700',
  Y: 'bg-green-100 text-green-700',
  N: 'bg-gray-100 text-gray-600',
}

export default function StatusBadge({ status }) {
  if (!status) return null
  const cls = colors[status] || 'bg-gray-100 text-gray-600'
  return (
    <span className={`inline-block px-2.5 py-0.5 rounded-full text-xs font-medium ${cls}`}>
      {status}
    </span>
  )
}
