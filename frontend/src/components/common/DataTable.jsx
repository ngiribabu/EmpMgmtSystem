import { useState } from 'react'
import { Search, ChevronUp, ChevronDown } from 'lucide-react'

export default function DataTable({ columns, data, onRowClick, searchable = true, searchPlaceholder = 'Search...' }) {
  const [search, setSearch] = useState('')
  const [sortCol, setSortCol] = useState(null)
  const [sortDir, setSortDir] = useState('asc')

  const filtered = searchable && search
    ? data.filter((row) =>
        columns.some((col) => {
          const val = col.accessor ? col.accessor(row) : row[col.key]
          return val && String(val).toLowerCase().includes(search.toLowerCase())
        })
      )
    : data

  const sorted = sortCol
    ? [...filtered].sort((a, b) => {
        const col = columns.find((c) => c.key === sortCol)
        const aVal = col?.accessor ? col.accessor(a) : a[sortCol]
        const bVal = col?.accessor ? col.accessor(b) : b[sortCol]
        if (aVal == null) return 1
        if (bVal == null) return -1
        const cmp = String(aVal).localeCompare(String(bVal), undefined, { numeric: true })
        return sortDir === 'asc' ? cmp : -cmp
      })
    : filtered

  const toggleSort = (key) => {
    if (sortCol === key) {
      setSortDir(sortDir === 'asc' ? 'desc' : 'asc')
    } else {
      setSortCol(key)
      setSortDir('asc')
    }
  }

  return (
    <div>
      {searchable && (
        <div className="mb-4 relative">
          <Search className="absolute left-3 top-2.5 text-gray-400" size={18} />
          <input
            type="text"
            placeholder={searchPlaceholder}
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none text-sm"
          />
        </div>
      )}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-50 border-b border-gray-200">
              {columns.map((col) => (
                <th
                  key={col.key}
                  onClick={() => col.sortable !== false && toggleSort(col.key)}
                  className={`px-4 py-3 text-left font-semibold text-gray-600 ${
                    col.sortable !== false ? 'cursor-pointer select-none hover:bg-gray-100' : ''
                  }`}
                >
                  <div className="flex items-center gap-1">
                    {col.label}
                    {sortCol === col.key && (
                      sortDir === 'asc' ? <ChevronUp size={14} /> : <ChevronDown size={14} />
                    )}
                  </div>
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {sorted.length === 0 ? (
              <tr>
                <td colSpan={columns.length} className="px-4 py-8 text-center text-gray-400">
                  No data found
                </td>
              </tr>
            ) : (
              sorted.map((row, idx) => (
                <tr
                  key={idx}
                  onClick={() => onRowClick?.(row)}
                  className={`border-b border-gray-100 ${
                    onRowClick ? 'cursor-pointer hover:bg-blue-50' : ''
                  } transition-colors`}
                >
                  {columns.map((col) => (
                    <td key={col.key} className="px-4 py-3 text-gray-700">
                      {col.render ? col.render(row) : (col.accessor ? col.accessor(row) : row[col.key])}
                    </td>
                  ))}
                </tr>
              ))
            )}
          </tbody>
        </table>
        <div className="px-4 py-2 bg-gray-50 text-xs text-gray-500 border-t">
          {sorted.length} record{sorted.length !== 1 ? 's' : ''}
        </div>
      </div>
    </div>
  )
}
