import React, { useState } from 'react'
import { supabase, isSupabaseConfigured } from '../lib/supabase'
import { MessageSquare, Database, ArrowRight, ShieldCheck } from 'lucide-react'

export default function Auth({ onDemoLogin, onOpenSupabaseModal }) {
  const [mode, setMode] = useState('signin')
  const [email, setEmail] = useState('user@vibechat.io')
  const [password, setPassword] = useState('password123')
  const [username, setUsername] = useState('VibeExplorer')
  const [error, setError] = useState('')
  const [info, setInfo] = useState('')
  const [busy, setBusy] = useState(false)

  async function submit(e) {
    e.preventDefault()
    setError(''); setInfo(''); setBusy(true)

    if (isSupabaseConfigured && supabase) {
      try {
        const { data, error: authError } = mode === 'signup'
          ? await supabase.auth.signUp({
              email,
              password,
              options: { data: { username: username.trim() } }
            })
          : await supabase.auth.signInWithPassword({ email, password })

        setBusy(false)
        if (authError) return setError(authError.message)
        if (mode === 'signup' && !data.session) {
          setInfo('Check your email to confirm your account, then sign in.')
        }
      } catch (err) {
        setBusy(false)
        setError(err.message || 'Authentication error')
      }
    } else {
      // Offline / Demo mode
      setTimeout(() => {
        setBusy(false)
        onDemoLogin({
          id: 'user_me',
          email: email.trim(),
          user_metadata: { username: mode === 'signup' ? username.trim() : email.split('@')[0] }
        })
      }, 400)
    }
  }

  const inputStyle = "w-full rounded-xl border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-wa-deep px-4 py-2.5 outline-none focus:ring-2 focus:ring-wa-green text-sm transition-all"

  return (
    <div className="h-full flex items-center justify-center bg-gray-100 dark:bg-wa-deep p-4 select-none">
      <div className="w-full max-w-md space-y-5 rounded-3xl bg-white dark:bg-wa-panel p-8 shadow-2xl text-gray-900 dark:text-gray-100 border border-gray-200 dark:border-gray-800">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-wa-green text-white shadow-lg shadow-wa-green/30">
              <MessageSquare size={26} />
            </div>
            <div>
              <h1 className="text-2xl font-bold tracking-tight text-wa-green">VibeChat</h1>
              <p className="text-xs text-gray-500 dark:text-gray-400">WhatsApp Web Experience</p>
            </div>
          </div>

          <button
            onClick={onOpenSupabaseModal}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-full border border-wa-green/30 bg-wa-green/10 text-xs font-medium text-wa-green hover:bg-wa-green/20 transition-all"
            title="Configure Supabase"
          >
            <Database size={13} />
            <span>{isSupabaseConfigured ? 'Supabase' : 'Connect'}</span>
          </button>
        </div>

        <form onSubmit={submit} className="space-y-4">
          <div className="text-left">
            <p className="text-sm font-medium text-gray-700 dark:text-gray-300">
              {mode === 'signin' ? 'Sign in to start messaging' : 'Create your new account'}
            </p>
          </div>

          {mode === 'signup' && (
            <input
              className={inputStyle}
              placeholder="Username"
              value={username}
              onChange={e => setUsername(e.target.value)}
              required
              minLength={2}
            />
          )}

          <input
            className={inputStyle}
            type="email"
            placeholder="Email address"
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
          />

          <input
            className={inputStyle}
            type="password"
            placeholder="Password (min 6 chars)"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
            minLength={6}
          />

          {error && <p className="text-xs text-red-500 bg-red-50 dark:bg-red-950/40 p-2 rounded-lg">{error}</p>}
          {info && <p className="text-xs text-wa-green bg-wa-green/10 p-2 rounded-lg">{info}</p>}

          <button
            disabled={busy}
            className="w-full rounded-xl bg-wa-green py-3 font-semibold text-white shadow-lg shadow-wa-green/30 hover:bg-wa-green-dark transition-all disabled:opacity-50 flex items-center justify-center gap-2"
          >
            {busy ? 'Connecting…' : mode === 'signin' ? 'Sign In' : 'Sign Up'}
            <ArrowRight size={18} />
          </button>

          <button
            type="button"
            onClick={() => { setMode(mode === 'signin' ? 'signup' : 'signin'); setError('') }}
            className="w-full text-center text-xs text-gray-500 dark:text-gray-400 hover:text-wa-green transition-colors"
          >
            {mode === 'signin' ? "Don't have an account? Sign up" : 'Already have an account? Sign in'}
          </button>
        </form>

        {/* Demo Fast Login Pills */}
        <div className="pt-2 border-t border-gray-100 dark:border-gray-800">
          <p className="text-[11px] font-medium text-gray-400 mb-2 uppercase tracking-wider">Quick Demo Accounts:</p>
          <div className="flex flex-wrap gap-2">
            {[
              { name: 'VibeExplorer', email: 'user@vibechat.io' },
              { name: 'Alex Rivera', email: 'alex@vibechat.io' },
              { name: 'Sarah Jenkins', email: 'sarah@vibechat.io' },
              { name: 'Jordan Chen', email: 'jordan@vibechat.io' },
            ].map(demo => (
              <button
                key={demo.email}
                type="button"
                onClick={() => {
                  setEmail(demo.email)
                  setUsername(demo.name)
                  onDemoLogin({
                    id: 'user_' + demo.name.toLowerCase().replace(' ', '_'),
                    email: demo.email,
                    user_metadata: { username: demo.name }
                  })
                }}
                className="rounded-full bg-gray-100 dark:bg-wa-deep px-3 py-1 text-xs text-gray-700 dark:text-gray-300 hover:bg-wa-green/20 hover:text-wa-green transition-all border border-gray-200 dark:border-gray-700"
              >
                {demo.name}
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
