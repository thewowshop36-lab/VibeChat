import React from 'react'
import { Users, Volume2, Plus } from 'lucide-react'

export default function CommunitiesTab({ communities, onSelectGroup }) {
  return (
    <div className="flex-1 flex flex-col h-full bg-white dark:bg-wa-deep overflow-y-auto">
      {/* Header */}
      <div className="flex items-center justify-between px-4 py-3 bg-gray-100 dark:bg-wa-panel border-b border-gray-200 dark:border-gray-800">
        <h2 className="text-lg font-bold">Communities</h2>
      </div>

      <div className="divide-y divide-gray-200 dark:divide-gray-800">
        {/* New Community Row */}
        <div className="flex items-center gap-4 p-4 hover:bg-gray-100 dark:hover:bg-wa-panel cursor-pointer transition-colors">
          <div className="w-12 h-12 rounded-xl bg-gray-200 dark:bg-wa-panel flex items-center justify-center text-gray-500 relative">
            <Users size={24} />
            <div className="absolute -bottom-1 -right-1 w-5 h-5 rounded-full bg-wa-green text-white flex items-center justify-center">
              <Plus size={14} />
            </div>
          </div>
          <div>
            <h4 className="font-semibold text-sm">New community</h4>
          </div>
        </div>

        {/* Communities List */}
        {communities.map(comm => (
          <div key={comm.id} className="p-3 space-y-2">
            <div className="flex items-center gap-3 px-2">
              <div className="w-10 h-10 rounded-xl bg-wa-green/20 text-wa-green flex items-center justify-center font-bold">
                <Users size={22} />
              </div>
              <div>
                <h3 className="font-bold text-sm">{comm.name}</h3>
                <p className="text-xs text-gray-500">{comm.description}</p>
              </div>
            </div>

            <div className="space-y-1 pl-4">
              {comm.groups.map(group => (
                <div
                  key={group.id}
                  onClick={() => onSelectGroup(group)}
                  className="flex items-center gap-3 p-2 rounded-xl hover:bg-gray-100 dark:hover:bg-wa-panel cursor-pointer transition-colors"
                >
                  <div className="w-9 h-9 rounded-full bg-gray-100 dark:bg-wa-panel flex items-center justify-center text-wa-green">
                    {group.isAnnouncement ? <Volume2 size={18} /> : <Users size={18} />}
                  </div>
                  <div>
                    <h4 className="font-medium text-xs text-gray-800 dark:text-gray-200">{group.name}</h4>
                    <p className="text-[11px] text-gray-500">{group.members} members</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
