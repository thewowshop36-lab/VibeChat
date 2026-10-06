import React from 'react'
import { Link2, Phone, Video, PhoneIncoming, PhoneOutgoing, PhoneMissed } from 'lucide-react'

export default function CallsTab({ callLogs, onStartCall }) {
  return (
    <div className="flex-1 flex flex-col h-full bg-white dark:bg-wa-deep overflow-y-auto">
      {/* Header */}
      <div className="flex items-center justify-between px-4 py-3 bg-gray-100 dark:bg-wa-panel border-b border-gray-200 dark:border-gray-800">
        <h2 className="text-lg font-bold">Calls</h2>
      </div>

      <div className="p-3 space-y-3">
        {/* Create Call Link */}
        <div className="flex items-center gap-3 p-3 rounded-2xl hover:bg-gray-100 dark:hover:bg-wa-panel cursor-pointer transition-colors">
          <div className="w-12 h-12 rounded-full bg-wa-green flex items-center justify-center text-white">
            <Link2 size={22} />
          </div>
          <div>
            <h4 className="font-semibold text-sm">Create call link</h4>
            <p className="text-xs text-gray-500 dark:text-gray-400">Share a link for your WhatsApp call</p>
          </div>
        </div>

        <h3 className="text-xs font-semibold text-gray-500 uppercase tracking-wider px-2 pt-2">Recent</h3>

        {/* Calls History */}
        <div className="divide-y divide-gray-100 dark:divide-gray-800/60">
          {callLogs.map(log => (
            <div key={log.id} className="flex items-center justify-between py-2.5 px-2 hover:bg-gray-100 dark:hover:bg-wa-panel rounded-xl cursor-pointer">
              <div className="flex items-center gap-3">
                <div className="w-11 h-11 rounded-full bg-wa-teal flex items-center justify-center font-bold text-white">
                  {log.name[0].toUpperCase()}
                </div>
                <div>
                  <h4 className={`font-semibold text-sm ${log.direction === 'missed' ? 'text-red-500' : ''}`}>
                    {log.name}
                  </h4>
                  <div className="flex items-center gap-1.5 text-xs text-gray-500">
                    {log.direction === 'incoming' && <PhoneIncoming size={13} className="text-wa-green" />}
                    {log.direction === 'outgoing' && <PhoneOutgoing size={13} className="text-wa-green" />}
                    {log.direction === 'missed' && <PhoneMissed size={13} className="text-red-500" />}
                    <span>{log.time}</span>
                  </div>
                </div>
              </div>

              <div className="flex gap-1">
                <button
                  onClick={() => onStartCall(log.name, 'video')}
                  className="p-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 text-wa-green"
                >
                  <Video size={18} />
                </button>
                <button
                  onClick={() => onStartCall(log.name, 'audio')}
                  className="p-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 text-wa-green"
                >
                  <Phone size={18} />
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
