import { useState } from 'react'

// Left panel: own header, search box, and the contact list
export default function Sidebar({ me, users, online, selectedId, onSelect, dark, onToggleDark, onSignOut }) {
  const [q, setQ] = useState('')
  const filtered = users.filter(u => u.username.toLowerCase().includes(q.toLowerCase()))

  return (
    <aside className="flex flex-col w-full border-r border-gray-200 dark:border-gray-800 bg-white dark:bg-wa-deep">
      <header className="flex items-center justify-between bg-gray-100 dark:bg-wa-panel px-4 py-3">
        <span className="truncate text-sm font-medium">{me.user_metadata?.username ?? me.email}</span>
        <div className="flex gap-3 text-sm">
          <button onClick={onToggleDark} title="Toggle theme">{dark ? '☀️' : '🌙'}</button>
          <button onClick={onSignOut} className="text-red-500 hover:underline">Logout</button>
        </div>
      </header>

      <div className="p-2">
        <input value={q} onChange={e => setQ(e.target.value)} placeholder="Search users"
          className="w-full rounded-lg bg-gray-100 dark:bg-wa-panel px-3 py-2 text-sm outline-none" />
      </div>

      <ul className="flex-1 overflow-y-auto">
        {filtered.map(u => (
          <li key={u.id}>
            <button onClick={() => onSelect(u)}
              className={`flex w-full items-center gap-3 px-4 py-3 text-left hover:bg-gray-100 dark:hover:bg-wa-panel ${selectedId === u.id ? 'bg-gray-100 dark:bg-wa-panel' : ''}`}>
              <div className="relative">
                <div className="flex h-11 w-11 items-center justify-center rounded-full bg-wa-green font-semibold text-white">
                  {u.username[0].toUpperCase()}
                </div>
                {online.has(u.id) && <span className="absolute bottom-0 right-0 h-3 w-3 rounded-full border-2 border-white dark:border-wa-deep bg-green-500" />}
              </div>
              <div className="min-w-0">
                <p className="truncate font-medium">{u.username}</p>
                <p className="text-xs text-gray-500">{online.has(u.id) ? 'online' : 'offline'}</p>
              </div>
            </button>
          </li>
        ))}
        {filtered.length === 0 && <p className="p-4 text-center text-sm text-gray-500">No users found</p>}
      </ul>
    </aside>
  )
}
