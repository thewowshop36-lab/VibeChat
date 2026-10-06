import React, { useEffect, useRef, useState } from 'react'
import { ArrowLeft, Video, Phone, MoreVertical, Smile, Paperclip, Send, CheckCheck } from 'lucide-react'
import { supabase, isSupabaseConfigured } from '../lib/supabase'

const fmt = (iso) => new Date(iso).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })

export default function ChatWindow({ me, other, isOnline, onBack, onStartCall }) {
  const [messages, setMessages] = useState([])
  const [text, setText] = useState('')
  const [isTyping, setIsTyping] = useState(false)
  const bottomRef = useRef(null)

  const addMessage = (m) => setMessages(prev => prev.some(x => x.id === m.id) ? prev : [...prev, m])

  useEffect(() => {
    if (isSupabaseConfigured && supabase) {
      // 1) Load history for this conversation from Supabase
      supabase.from('messages').select('*')
        .or(`and(sender_id.eq.${me.id},receiver_id.eq.${other.id}),and(sender_id.eq.${other.id},receiver_id.eq.${me.id})`)
        .order('created_at', { ascending: true })
        .then(({ data }) => {
          if (data && data.length > 0) setMessages(data)
          else setInitialDemoMessages()
        })

      // 2) Realtime: listen for incoming messages
      const channel = supabase.channel(`chat:${me.id}:${other.id}`)
        .on('postgres_changes',
          { event: 'INSERT', schema: 'public', table: 'messages', filter: `receiver_id=eq.${me.id}` },
          (payload) => {
            if (payload.new.sender_id === other.id) addMessage(payload.new)
          })
        .subscribe()

      return () => { supabase.removeChannel(channel) }
    } else {
      setInitialDemoMessages()
    }
  }, [me.id, other.id])

  function setInitialDemoMessages() {
    setMessages([
      {
        id: 'msg_1',
        sender_id: other.id,
        receiver_id: me.id,
        content: `Hey! Welcome to VibeChat! How do you like the WhatsApp Web experience? ✨`,
        created_at: new Date(Date.now() - 3600000).toISOString()
      },
      {
        id: 'msg_2',
        sender_id: me.id,
        receiver_id: other.id,
        content: `It looks super crisp! Love the emerald WhatsApp colors and the bottom navigation tabs.`,
        created_at: new Date(Date.now() - 1800000).toISOString()
      }
    ])
  }

  // Auto-scroll to newest message
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages, isTyping])

  async function send(e) {
    e.preventDefault()
    const content = text.trim()
    if (!content) return
    setText('')

    const newMsg = {
      id: 'local_' + Date.now(),
      sender_id: me.id,
      receiver_id: other.id,
      content,
      created_at: new Date().toISOString()
    }
    addMessage(newMsg)

    if (isSupabaseConfigured && supabase) {
      const { data, error } = await supabase.from('messages')
        .insert({ sender_id: me.id, receiver_id: other.id, content }).select().single()
      if (error) {
        console.error('Supabase send error:', error.message)
      }
    }

    // Trigger simulated reply from contact
    setTimeout(() => {
      setIsTyping(true)
      setTimeout(() => {
        setIsTyping(false)
        const replies = [
          "Got it! Thanks for keeping me updated 👍",
          "That sounds wonderful! VibeChat is looking awesome 🚀",
          "Totally agree with you on that! ✨",
          "Awesome! Everything is running smoothly in real-time."
        ]
        const replyMsg = {
          id: 'reply_' + Date.now(),
          sender_id: other.id,
          receiver_id: me.id,
          content: replies[Math.floor(Math.random() * replies.length)],
          created_at: new Date().toISOString()
        }
        addMessage(replyMsg)
      }, 1500)
    }, 1000)
  }

  return (
    <section className="flex flex-1 flex-col h-full min-w-0 bg-wa-bg dark:bg-wa-chat">
      {/* Chat Header */}
      <header className="flex items-center justify-between bg-gray-100 dark:bg-wa-panel px-4 py-2.5 border-b border-gray-200 dark:border-gray-800">
        <div className="flex items-center gap-3 min-w-0">
          <button onClick={onBack} className="md:hidden text-gray-600 dark:text-gray-300 p-1 hover:bg-gray-200 dark:hover:bg-gray-700 rounded-lg">
            <ArrowLeft size={20} />
          </button>
          <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-wa-teal font-semibold text-white">
            {other.username[0].toUpperCase()}
          </div>
          <div className="min-w-0">
            <p className="font-semibold text-sm truncate">{other.username}</p>
            <p className="text-[11px] text-gray-500 dark:text-gray-400 leading-tight">
              {isTyping ? <span className="text-wa-green font-medium">typing…</span> : (isOnline ? 'online' : 'offline')}
            </p>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex items-center gap-1 text-gray-600 dark:text-gray-300">
          <button
            onClick={() => onStartCall(other.username, 'video')}
            className="p-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors"
            title="Video Call"
          >
            <Video size={19} />
          </button>
          <button
            onClick={() => onStartCall(other.username, 'audio')}
            className="p-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors"
            title="Audio Call"
          >
            <Phone size={19} />
          </button>
          <button className="p-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors">
            <MoreVertical size={19} />
          </button>
        </div>
      </header>

      {/* Messages Stream */}
      <div className="flex-1 overflow-y-auto p-4 space-y-2">
        {messages.map(m => {
          const mine = m.sender_id === me.id
          return (
            <div key={m.id} className={`flex ${mine ? 'justify-end' : 'justify-start'}`}>
              <div className={`max-w-[78%] md:max-w-[65%] rounded-2xl px-3.5 py-2 shadow-sm relative ${mine ? 'bg-wa-out dark:bg-wa-outDark text-gray-900 dark:text-gray-100 rounded-tr-none' : 'bg-white dark:bg-wa-panel text-gray-900 dark:text-gray-100 rounded-tl-none'}`}>
                <p className="whitespace-pre-wrap break-words text-sm leading-relaxed">{m.content}</p>
                <div className="flex items-center justify-end gap-1 mt-1">
                  <span className="text-[10px] text-gray-500 dark:text-gray-400">{fmt(m.created_at)}</span>
                  {mine && <CheckCheck size={14} className="text-blue-500 inline" />}
                </div>
              </div>
            </div>
          )
        })}

        {isTyping && (
          <div className="flex justify-start">
            <div className="rounded-2xl rounded-tl-none bg-white dark:bg-wa-panel px-4 py-2 shadow-sm">
              <span className="text-xs text-wa-green font-medium animate-pulse">typing…</span>
            </div>
          </div>
        )}

        <div ref={bottomRef} />
      </div>

      {/* Input Box Bar */}
      <form onSubmit={send} className="flex items-center gap-2 bg-gray-100 dark:bg-wa-panel px-3 py-2.5 border-t border-gray-200 dark:border-gray-800">
        <button
          type="button"
          onClick={() => setText(t => t + " 😊")}
          className="text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 p-1.5"
        >
          <Smile size={22} />
        </button>
        <button
          type="button"
          className="text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 p-1.5"
        >
          <Paperclip size={20} />
        </button>

        <input
          value={text}
          onChange={e => setText(e.target.value)}
          placeholder="Type a message"
          className="flex-1 rounded-2xl bg-white dark:bg-wa-deep px-4 py-2.5 text-sm outline-none text-gray-900 dark:text-gray-100 placeholder-gray-400 border border-transparent focus:border-wa-green"
        />

        <button
          type="submit"
          className="flex h-10 w-10 items-center justify-center rounded-full bg-wa-green font-medium text-white hover:bg-wa-green-dark shadow-md transition-all"
        >
          <Send size={18} />
        </button>
      </form>
    </section>
  )
}
