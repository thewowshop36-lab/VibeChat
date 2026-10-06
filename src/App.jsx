import React, { useEffect, useState } from 'react'
import { supabase, isSupabaseConfigured } from './lib/supabase'
import Auth from './components/Auth'
import Sidebar from './components/Sidebar'
import ChatWindow from './components/ChatWindow'
import ActiveCallModal from './components/ActiveCallModal'
import SupabaseConnectModal from './components/SupabaseConnectModal'
import { MessageSquare } from 'lucide-react'

const DEFAULT_USERS = [
  { id: 'user_alex', username: 'Alex Rivera', email: 'alex@vibechat.io', status_message: 'Coding the future 🚀' },
  { id: 'user_sarah', username: 'Sarah Jenkins', email: 'sarah@vibechat.io', status_message: 'Designing vibrant interfaces 🎨' },
  { id: 'user_jordan', username: 'Jordan Chen', email: 'jordan@vibechat.io', status_message: 'Coffee first, questions later ☕' },
  { id: 'user_emma', username: 'Emma Watson', email: 'emma@vibechat.io', status_message: 'Exploring new technologies 🌲' },
  { id: 'user_priya', username: 'Priya Patel', email: 'priya@vibechat.io', status_message: 'In a meeting, ping for urgent 📱' },
  { id: 'user_lucas', username: 'Lucas Miller', email: 'lucas@vibechat.io', status_message: 'Music is life 🎧' }
]

const DEFAULT_STORIES = [
  { id: 's1', userId: 'user_alex', username: 'Alex Rivera', caption: 'Excited for the new VibeChat updates! 🚀', time: '10:45 AM', isViewed: false, bgGradient: 'linear-gradient(135deg, #005C4B 0%, #111B21 100%)' },
  { id: 's2', userId: 'user_sarah', username: 'Sarah Jenkins', caption: 'Sunset views today 🌅✨', time: '8:20 AM', isViewed: false, bgGradient: 'linear-gradient(135deg, #E65100 0%, #3E2723 100%)' },
  { id: 's3', userId: 'user_lucas', username: 'Lucas Miller', caption: 'New playlist vibes out now 🎧🎶', time: 'Yesterday', isViewed: true, bgGradient: 'linear-gradient(135deg, #4A148C 0%, #004D40 100%)' }
]

const DEFAULT_COMMUNITIES = [
  {
    id: 'c1',
    name: 'Tech & Developers Hub',
    description: 'Discussions on React, Next.js, Vercel & Supabase.',
    groups: [
      { id: 'g1', name: 'Announcements', members: 1240, isAnnouncement: true },
      { id: 'g2', name: 'Web & Vercel Deployments', members: 840 },
      { id: 'g3', name: 'Supabase Database & Auth', members: 560 }
    ]
  },
  {
    id: 'c2',
    name: 'Vibe Creators Club',
    description: 'Designers, artists and content creators showcase.',
    groups: [
      { id: 'g4', name: 'Creators Announcements', members: 620, isAnnouncement: true },
      { id: 'g5', name: 'UI/UX Showcase', members: 430 }
    ]
  }
]

const DEFAULT_CALLS = [
  { id: 'call_1', name: 'Alex Rivera', time: 'Today, 11:30 AM', direction: 'incoming', type: 'video' },
  { id: 'call_2', name: 'Sarah Jenkins', time: 'Today, 9:15 AM', direction: 'missed', type: 'audio' },
  { id: 'call_3', name: 'Jordan Chen', time: 'Yesterday, 6:40 PM', direction: 'outgoing', type: 'audio' }
]

export default function App() {
  const [session, setSession] = useState(undefined)
  const [users, setUsers] = useState(DEFAULT_USERS)
  const [selected, setSelected] = useState(null)
  const [online, setOnline] = useState(new Set(['user_alex', 'user_sarah', 'user_lucas']))
  const [dark, setDark] = useState(() => localStorage.getItem('theme') !== 'light')
  const [isSupabaseModalOpen, setIsSupabaseModalOpen] = useState(false)
  const [activeCall, setActiveCall] = useState(null) // { contactName, type }

  const [stories, setStories] = useState(DEFAULT_STORIES)
  const [communities] = useState(DEFAULT_COMMUNITIES)
  const [callLogs, setCallLogs] = useState(DEFAULT_CALLS)

  // Dark mode class on html
  useEffect(() => {
    document.documentElement.classList.toggle('dark', dark)
    localStorage.setItem('theme', dark ? 'dark' : 'light')
  }, [dark])

  // Auth restore & subscription
  useEffect(() => {
    if (isSupabaseConfigured && supabase) {
      supabase.auth.getSession().then(({ data }) => setSession(data.session))
      const { data: sub } = supabase.auth.onAuthStateChange((_e, s) => setSession(s))
      return () => sub.subscription.unsubscribe()
    } else {
      const savedUser = localStorage.getItem('demo_user')
      if (savedUser) {
        setSession({ user: JSON.parse(savedUser) })
      } else {
        setSession(null)
      }
    }
  }, [])

  const me = session?.user

  // Load profiles from Supabase if configured
  useEffect(() => {
    if (!me || !isSupabaseConfigured || !supabase) return
    supabase.from('profiles').select('*').neq('id', me.id).order('username')
      .then(({ data }) => {
        if (data && data.length > 0) setUsers(data)
      })
  }, [me?.id])

  // Presence channel
  useEffect(() => {
    if (!me || !isSupabaseConfigured || !supabase) return
    const ch = supabase.channel('presence:online', { config: { presence: { key: me.id } } })
    ch.on('presence', { event: 'sync' }, () => setOnline(new Set(Object.keys(ch.presenceState()))))
      .subscribe((status) => { if (status === 'SUBSCRIBED') ch.track({ at: Date.now() }) })
    return () => { supabase.removeChannel(ch) }
  }, [me?.id])

  function handleDemoLogin(userObj) {
    localStorage.setItem('demo_user', JSON.stringify(userObj))
    setSession({ user: userObj })
  }

  function handleSignOut() {
    if (isSupabaseConfigured && supabase) {
      supabase.auth.signOut()
    }
    localStorage.removeItem('demo_user')
    setSession(null)
    setSelected(null)
  }

  function handleAddStory(caption = 'Live on Vercel! 🚀') {
    const newStory = {
      id: 's_my_' + Date.now(),
      userId: me?.id || 'user_me',
      username: 'My Status',
      caption,
      time: 'Just now',
      isMine: true,
      isViewed: false,
      bgGradient: 'linear-gradient(135deg, #008069 0%, #111B21 100%)'
    }
    setStories([newStory, ...stories.filter(s => !s.isMine)])
  }

  function handleStartCall(name, type) {
    setActiveCall({ contactName: name, type })
    const newLog = {
      id: 'call_' + Date.now(),
      name,
      time: 'Just now',
      direction: 'outgoing',
      type
    }
    setCallLogs([newLog, ...callLogs])
  }

  if (session === undefined) return null

  if (!session) {
    return (
      <>
        <Auth
          onDemoLogin={handleDemoLogin}
          onOpenSupabaseModal={() => setIsSupabaseModalOpen(true)}
        />
        <SupabaseConnectModal
          isOpen={isSupabaseModalOpen}
          onClose={() => setIsSupabaseModalOpen(false)}
        />
      </>
    )
  }

  return (
    <div className="h-full flex bg-white dark:bg-wa-chat text-gray-900 dark:text-gray-100 overflow-hidden font-sans">
      {/* Sidebar Panel: Mobile switches, Tablet/Desktop side-by-side */}
      <div className={`${selected ? 'hidden md:flex' : 'flex'} w-full md:w-[420px] shrink-0 h-full`}>
        <Sidebar
          me={me}
          users={users}
          online={online}
          selectedId={selected?.id}
          onSelect={setSelected}
          dark={dark}
          onToggleDark={() => setDark(!dark)}
          onSignOut={handleSignOut}
          onOpenSupabaseModal={() => setIsSupabaseModalOpen(true)}
          stories={stories}
          onAddStory={handleAddStory}
          onReplyStory={(uid, msg) => {
            const contact = users.find(u => u.id === uid)
            if (contact) setSelected(contact)
          }}
          communities={communities}
          callLogs={callLogs}
          onStartCall={handleStartCall}
        />
      </div>

      {/* Main Chat Conversation Viewport */}
      <div className={`${selected ? 'flex' : 'hidden md:flex'} flex-1 min-w-0 h-full`}>
        {selected ? (
          <ChatWindow
            key={selected.id}
            me={me}
            other={selected}
            isOnline={online.has(selected.id)}
            onBack={() => setSelected(null)}
            onStartCall={handleStartCall}
          />
        ) : (
          <div className="flex-1 flex flex-col items-center justify-center bg-gray-50 dark:bg-wa-deep text-gray-400 p-8 text-center select-none">
            <div className="w-16 h-16 rounded-full bg-wa-green/10 flex items-center justify-center text-wa-green mb-4">
              <MessageSquare size={32} />
            </div>
            <h3 className="text-xl font-bold text-gray-800 dark:text-gray-200">VibeChat for Web</h3>
            <p className="text-sm max-w-sm mt-1 text-gray-500">
              Select a contact to start messaging, view status updates, or start high-definition audio/video calls.
            </p>
          </div>
        )}
      </div>

      {/* Active Call Modal */}
      {activeCall && (
        <ActiveCallModal
          contactName={activeCall.contactName}
          callType={activeCall.type}
          onEndCall={() => setActiveCall(null)}
        />
      )}

      {/* Supabase Connect Modal */}
      <SupabaseConnectModal
        isOpen={isSupabaseModalOpen}
        onClose={() => setIsSupabaseModalOpen(false)}
      />
    </div>
  )
}
