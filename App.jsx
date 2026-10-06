import { useEffect, useState } from 'react'
import { supabase } from './lib/supabase'
import Auth from './components/Auth'
import Sidebar from './components/Sidebar'
import ChatWindow from './components/ChatWindow'

export default function App() {
  const [session, setSession] = useState(undefined) // undefined = still loading
  const [users, setUsers] = useState([])
  const [selected, setSelected] = useState(null)
  const [online, setOnline] = useState(new Set())
  const [dark, setDark] = useState(() => localStorage.getItem('theme') === 'dark')

  // Theme: toggle the "dark" class on <html>
  useEffect(() => {
    document.documentElement.classList.toggle('dark', dark)
    localStorage.setItem('theme', dark ? 'dark' : 'light')
  }, [dark])

  // 1) Auth state: restore session on load and react to login/logout
  useEffect(() => {
    supabase.auth.getSession().then(({ data }) => setSession(data.session))
    const { data: sub } = supabase.auth.onAuthStateChange((_e, s) => setSession(s))
    return () => sub.subscription.unsubscribe()
  }, [])

  const me = session?.user

  // 2) Load every other user's profile (RLS allows reading all profiles)
  useEffect(() => {
    if (!me) return
    supabase.from('profiles').select('id, username').neq('id', me.id).order('username')
      .then(({ data }) => setUsers(data ?? []))
  }, [me?.id])

  // 3) Presence: everyone joins one channel and tracks themselves -> online status
  useEffect(() => {
    if (!me) return
    const ch = supabase.channel('presence:online', { config: { presence: { key: me.id } } })
    ch.on('presence', { event: 'sync' }, () => setOnline(new Set(Object.keys(ch.presenceState()))))
      .subscribe((status) => { if (status === 'SUBSCRIBED') ch.track({ at: Date.now() }) })
    return () => { supabase.removeChannel(ch) }
  }, [me?.id])

  if (session === undefined) return null
  if (!session) return <Auth />

  return (
    <div className="h-full flex bg-white dark:bg-wa-chat text-gray-900 dark:text-gray-100">
      {/* On mobile show either the sidebar or the chat; on md+ show both */}
      <div className={`${selected ? 'hidden md:flex' : 'flex'} w-full md:w-96 shrink-0`}>
        <Sidebar
          me={me} users={users} online={online} selectedId={selected?.id}
          onSelect={setSelected} dark={dark} onToggleDark={() => setDark(!dark)}
          onSignOut={() => supabase.auth.signOut()}
        />
      </div>
      <div className={`${selected ? 'flex' : 'hidden md:flex'} flex-1 min-w-0`}>
        {selected
          ? <ChatWindow key={selected.id} me={me} other={selected} isOnline={online.has(selected.id)} onBack={() => setSelected(null)} />
          : <div className="flex-1 flex items-center justify-center bg-gray-50 dark:bg-wa-deep text-gray-500">Select a contact to start chatting</div>}
      </div>
    </div>
  )
}
