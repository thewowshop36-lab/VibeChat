import React, { useState } from 'react'
import { Database, Key, CheckCircle, ExternalLink, X, Zap } from 'lucide-react'
import { SUPABASE_URL, SUPABASE_ANON_KEY, isSupabaseConfigured, updateSupabaseConfig } from '../lib/supabase'

export default function SupabaseConnectModal({ isOpen, onClose }) {
  const [url, setUrl] = useState(SUPABASE_URL || '')
  const [key, setKey] = useState(SUPABASE_ANON_KEY || '')
  const [status, setStatus] = useState(isSupabaseConfigured ? 'connected' : 'idle')

  if (!isOpen) return null

  function handleSave(e) {
    e.preventDefault()
    if (!url || !key) return
    updateSupabaseConfig(url, key)
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm">
      <div className="w-full max-w-md rounded-2xl bg-white dark:bg-wa-panel p-6 shadow-2xl text-gray-900 dark:text-gray-100 border border-gray-200 dark:border-gray-700 animate-fadeIn">
        <div className="flex items-center justify-between pb-4 border-b border-gray-200 dark:border-gray-700">
          <div className="flex items-center gap-2">
            <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-wa-green/20 text-wa-green">
              <Database size={20} />
            </div>
            <div>
              <h3 className="font-semibold text-base">Supabase Connection</h3>
              <p className="text-xs text-gray-500 dark:text-gray-400">
                {isSupabaseConfigured ? '🟢 Connected & Active' : '🟡 Offline / Demo Mode Active'}
              </p>
            </div>
          </div>
          <button onClick={onClose} className="rounded-lg p-1 text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-800">
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSave} className="mt-4 space-y-4">
          <div>
            <label className="block text-xs font-medium text-gray-500 dark:text-gray-400 mb-1">
              Project URL
            </label>
            <div className="relative">
              <Database className="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
              <input
                type="url"
                placeholder="https://your-project.supabase.co"
                value={url}
                onChange={e => setUrl(e.target.value)}
                className="w-full rounded-xl border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-wa-deep pl-9 pr-3 py-2 text-sm outline-none focus:ring-2 focus:ring-wa-green"
                required
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-medium text-gray-500 dark:text-gray-400 mb-1">
              Anon Public API Key
            </label>
            <div className="relative">
              <Key className="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
              <input
                type="password"
                placeholder="eyJhbGciOiJIUzI1NiIsIn..."
                value={key}
                onChange={e => setKey(e.target.value)}
                className="w-full rounded-xl border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-wa-deep pl-9 pr-3 py-2 text-sm outline-none focus:ring-2 focus:ring-wa-green"
                required
              />
            </div>
          </div>

          <div className="rounded-xl bg-gray-50 dark:bg-wa-deep p-3 text-xs space-y-2 border border-gray-200 dark:border-gray-800">
            <div className="flex items-center gap-1.5 font-medium text-wa-green">
              <Zap size={14} /> Quick Vercel Setup:
            </div>
            <p className="text-gray-600 dark:text-gray-300">
              When deploying to Vercel, add these two Environment Variables in your Vercel Project Settings:
            </p>
            <code className="block bg-gray-200 dark:bg-black/40 p-1.5 rounded text-[11px] select-all">
              VITE_SUPABASE_URL<br />
              VITE_SUPABASE_ANON_KEY
            </code>
          </div>

          <div className="flex gap-2 pt-2">
            <button
              type="submit"
              className="flex-1 rounded-xl bg-wa-green py-2.5 font-medium text-white shadow-md hover:bg-wa-green-dark transition-colors text-sm"
            >
              Save & Connect
            </button>
            <button
              type="button"
              onClick={onClose}
              className="rounded-xl border border-gray-300 dark:border-gray-600 px-4 py-2.5 text-sm font-medium hover:bg-gray-100 dark:hover:bg-gray-800"
            >
              Close
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
