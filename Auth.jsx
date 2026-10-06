import { useState } from 'react'
import { supabase } from '../lib/supabase'

// Combined Sign In / Sign Up form
export default function Auth() {
  const [mode, setMode] = useState('signin')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [username, setUsername] = useState('')
  const [error, setError] = useState('')
  const [info, setInfo] = useState('')
  const [busy, setBusy] = useState(false)

  async function submit(e) {
    e.preventDefault()
    setError(''); setInfo(''); setBusy(true)
    const { data, error } = mode === 'signup'
      // username travels as metadata; the DB trigger copies it into "profiles"
      ? await supabase.auth.signUp({ email, password, options: { data: { username: username.trim() } } })
      : await supabase.auth.signInWithPassword({ email, password })
    setBusy(false)
    if (error) return setError(error.message)
    // If email confirmation is enabled in Supabase, there is no session yet
    if (mode === 'signup' && !data.session) setInfo('Check your email to confirm your account, then sign in.')
  }

  const input = 'w-full rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-wa-panel px-3 py-2 outline-none focus:ring-2 focus:ring-wa-green'

  return (
    <div className="h-full flex items-center justify-center bg-wa-bg dark:bg-wa-deep p-4">
      <form onSubmit={submit} className="w-full max-w-sm space-y-4 rounded-2xl bg-white dark:bg-wa-chat p-8 shadow-xl text-gray-900 dark:text-gray-100">
        <h1 className="text-2xl font-semibold text-wa-green">WA Chat</h1>
        <p className="text-sm text-gray-500">{mode === 'signin' ? 'Sign in to continue' : 'Create your account'}</p>
        {mode === 'signup' && (
          <input className={input} placeholder="Username" value={username} onChange={e => setUsername(e.target.value)} required minLength={2} />
        )}
        <input className={input} type="email" placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} required />
        <input className={input} type="password" placeholder="Password (min 6 chars)" value={password} onChange={e => setPassword(e.target.value)} required minLength={6} />
        {error && <p className="text-sm text-red-500">{error}</p>}
        {info && <p className="text-sm text-wa-green">{info}</p>}
        <button disabled={busy} className="w-full rounded-lg bg-wa-green py-2 font-medium text-white hover:opacity-90 disabled:opacity-50">
          {busy ? 'Please wait…' : mode === 'signin' ? 'Sign In' : 'Sign Up'}
        </button>
        <button type="button" onClick={() => { setMode(mode === 'signin' ? 'signup' : 'signin'); setError('') }} className="w-full text-sm text-gray-500 hover:underline">
          {mode === 'signin' ? "No account? Sign up" : 'Have an account? Sign in'}
        </button>
      </form>
    </div>
  )
}
