import React, { useState } from 'react'
import { MessageSquare, CircleDashed, Users, Phone, Search, Sun, Moon, LogOut, Database, Plus } from 'lucide-react'
import UpdatesTab from './UpdatesTab'
import CommunitiesTab from './CommunitiesTab'
import CallsTab from './CallsTab'

export default function Sidebar({
  me,
  users,
  online,
  selectedId,
  onSelect,
  dark,
  onToggleDark,
  onSignOut,
  onOpenSupabaseModal,
  stories,
  onAddStory,
  onReplyStory,
  communities,
  callLogs,
  onStartCall
}) {
  const [tab, setTab] = useState('chats') // chats, updates, communities, calls
  const [query, setQuery] = useState('')

  const filteredUsers = users.filter(u =>
    u.username.toLowerCase().includes(query.toLowerCase())
  )

  return (
    <aside className="flex flex-col w-full h-full border-r border-gray-200 dark:border-gray-800 bg-white dark:bg-wa-deep">
      {/* Top App Header */}
      <header className="flex items-center justify-between bg-gray-100 dark:bg-wa-panel px-4 py-3 border-b border-gray-200 dark:border-gray-800">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-full bg-wa-green flex items-center justify-center font-bold text-white text-sm">
            {me?.user_metadata?.username?.[0]?.toUpperCase() || me?.email?.[0]?.toUpperCase() || 'U'}
          </div>
          <div>
            <span className="truncate text-xs font-semibold block leading-tight">
              {me?.user_metadata?.username || me?.email}
            </span>
            <span className="text-[10px] text-wa-green font-medium">Online</span>
          </div>
        </div>

        <div className="flex items-center gap-2 text-gray-500 dark:text-gray-400">
          <button
            onClick={onOpenSupabaseModal}
            className="p-1.5 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors"
            title="Supabase Settings"
          >
            <Database size={17} />
          </button>
          <button
            onClick={onToggleDark}
            className="p-1.5 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors"
            title="Toggle theme"
          >
            {dark ? <Sun size={17} /> : <Moon size={17} />}
          </button>
          <button
            onClick={onSignOut}
            className="p-1.5 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-700 text-red-500 transition-colors"
            title="Logout"
          >
            <LogOut size={17} />
          </button>
        </div>
      </header>

      {/* Main Tab Viewport */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {tab === 'chats' && (
          <div className="flex-1 flex flex-col overflow-hidden">
            {/* Search Input */}
            <div className="p-2 border-b border-gray-100 dark:border-gray-800">
              <div className="flex items-center gap-2 bg-gray-100 dark:bg-wa-panel px-3 py-1.5 rounded-xl">
                <Search size={16} className="text-gray-400" />
                <input
                  value={query}
                  onChange={e => setQuery(e.target.value)}
                  placeholder="Search or start new chat"
                  className="w-full bg-transparent text-sm outline-none text-gray-900 dark:text-gray-100 placeholder-gray-400"
                />
              </div>
            </div>

            {/* Contacts List */}
            <ul className="flex-1 overflow-y-auto divide-y divide-gray-100 dark:divide-gray-800/60">
              {filteredUsers.map(u => {
                const isSelected = selectedId === u.id
                const isOnline = online.has(u.id)
                return (
                  <li key={u.id}>
                    <button
                      onClick={() => onSelect(u)}
                      className={`flex w-full items-center gap-3 px-4 py-3 text-left transition-colors ${isSelected ? 'bg-gray-100 dark:bg-wa-panel' : 'hover:bg-gray-50 dark:hover:bg-wa-panel/50'}`}
                    >
                      <div className="relative">
                        <div className="flex h-12 w-12 items-center justify-center rounded-full bg-wa-teal font-semibold text-white text-base">
                          {u.username[0].toUpperCase()}
                        </div>
                        {isOnline && (
                          <span className="absolute bottom-0 right-0 h-3.5 w-3.5 rounded-full border-2 border-white dark:border-wa-deep bg-wa-green" />
                        )}
                      </div>
                      <div className="min-w-0 flex-1">
                        <div className="flex items-center justify-between">
                          <p className="truncate font-semibold text-sm">{u.username}</p>
                          <span className="text-[10px] text-gray-400">12:30</span>
                        </div>
                        <p className="text-xs text-gray-500 dark:text-gray-400 truncate">
                          {u.status_message || (isOnline ? 'Active now' : 'offline')}
                        </p>
                      </div>
                    </button>
                  </li>
                )
              })}
              {filteredUsers.length === 0 && (
                <div className="p-8 text-center text-sm text-gray-400">
                  No contacts found
                </div>
              )}
            </ul>
          </div>
        )}

        {tab === 'updates' && (
          <UpdatesTab
            me={me}
            stories={stories}
            onAddStory={onAddStory}
            onReplyStory={onReplyStory}
          />
        )}

        {tab === 'communities' && (
          <CommunitiesTab
            communities={communities}
            onSelectGroup={(grp) => {
              if (users.length > 0) onSelect(users[0])
            }}
          />
        )}

        {tab === 'calls' && (
          <CallsTab
            callLogs={callLogs}
            onStartCall={onStartCall}
          />
        )}
      </div>

      {/* WhatsApp Bottom Navigation Bar */}
      <nav className="flex items-center justify-around bg-gray-100 dark:bg-wa-panel py-2 border-t border-gray-200 dark:border-gray-800">
        <button
          onClick={() => setTab('chats')}
          className={`flex flex-col items-center gap-1 text-[11px] font-medium transition-colors ${tab === 'chats' ? 'text-wa-green font-bold' : 'text-gray-500 dark:text-gray-400 hover:text-gray-800'}`}
        >
          <div className={`p-1 rounded-full ${tab === 'chats' ? 'bg-wa-green/15' : ''}`}>
            <MessageSquare size={20} />
          </div>
          <span>Chats</span>
        </button>

        <button
          onClick={() => setTab('updates')}
          className={`flex flex-col items-center gap-1 text-[11px] font-medium transition-colors ${tab === 'updates' ? 'text-wa-green font-bold' : 'text-gray-500 dark:text-gray-400 hover:text-gray-800'}`}
        >
          <div className={`p-1 rounded-full relative ${tab === 'updates' ? 'bg-wa-green/15' : ''}`}>
            <CircleDashed size={20} />
            <span className="absolute top-1 right-1 w-2 h-2 rounded-full bg-wa-green" />
          </div>
          <span>Updates</span>
        </button>

        <button
          onClick={() => setTab('communities')}
          className={`flex flex-col items-center gap-1 text-[11px] font-medium transition-colors ${tab === 'communities' ? 'text-wa-green font-bold' : 'text-gray-500 dark:text-gray-400 hover:text-gray-800'}`}
        >
          <div className={`p-1 rounded-full ${tab === 'communities' ? 'bg-wa-green/15' : ''}`}>
            <Users size={20} />
          </div>
          <span>Communities</span>
        </button>

        <button
          onClick={() => setTab('calls')}
          className={`flex flex-col items-center gap-1 text-[11px] font-medium transition-colors ${tab === 'calls' ? 'text-wa-green font-bold' : 'text-gray-500 dark:text-gray-400 hover:text-gray-800'}`}
        >
          <div className={`p-1 rounded-full ${tab === 'calls' ? 'bg-wa-green/15' : ''}`}>
            <Phone size={20} />
          </div>
          <span>Calls</span>
        </button>
      </nav>
    </aside>
  )
}
