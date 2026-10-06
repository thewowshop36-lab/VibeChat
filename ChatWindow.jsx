import { useEffect, useRef, useState } from 'react'
import { supabase } from '../lib/supabase'

const fmt = (iso) => new Date(iso).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })

export default function ChatWindow({ me, other, isOnline, onBack }) {
  const [messages, setMessages] = useState([])
  const [text, setText] = useState('')
  const bottomRef = useRef(null)

  // Add a message only once (guards against duplicates from insert response + Realtime)
  const addMessage = (m) => setMessages(prev => prev.some(x => x.id === m.id) ? prev : [...prev, m])

  useEffect(() => {
    // 1) Load history for this conversation (both directions)
    supabase.from('messages').select('*')
      .or(`and(sender_id.eq.${me.id},receiver_id.eq.${other.id}),and(sender_id.eq.${other.id},receiver_id.eq.${me.id})`)
      .order('created_at', { ascending: true })
      .then(({ data }) => setMessages(data ?? []))

    // 2) Realtime: listen for new rows addressed to me; keep only those from this contact.
    //    (Messages I send are added locally from the insert response.)
    const channel = supabase.channel(`chat:${me.id}:${other.id}`)
      .on('postgres_changes',
        { event: 'INSERT', schema: 'public', table: 'messages', filter: `receiver_id=eq.${me.id}` },
        (payload) => { if (payload.new.sender_id === other.id) addMessage(payload.new) })
      .subscribe()
    return () => { supabase.removeChannel(channel) }
  }, [me.id, other.id])

  // Auto-scroll to newest message
  useEffect(() => { bottomRef.current?.scrollIntoView({ behavior: 'smooth' }) }, [messages])

  async function send(e) {
    e.preventDefault()
    const content = text.trim()
    if (!content) return
    setText('')
    const { data, error } = await supabase.from('messages')
      .insert({ sender_id: me.id, receiver_id: other.id, content }).select().single()
    if (error) { setText(content); alert(error.message); return }
    addMessage(data)
  }

  return (
    <section className="flex flex-1 flex-col min-w-0">
      <header className="flex items-center gap-3 bg-gray-100 dark:bg-wa-panel px-4 py-3">
        <button onClick={onBack} className="md:hidden text-xl">←</button>
        <div className="flex h-10 w-10 items-center justify-center rounded-full bg-wa-green font-semibold text-white">{other.username[0].toUpperCase()}</div>
        <div>
          <p className="font-medium leading-tight">{other.username}</p>
          <p className="text-xs text-gray-500">{isOnline ? 'online' : 'offline'}</p>
        </div>
      </header>

      <div className="flex-1 overflow-y-auto bg-wa-bg dark:bg-wa-chat px-4 py-4 space-y-1">
        {messages.map(m => {
          const mine = m.sender_id === me.id
          return (
            <div key={m.id} className={`flex ${mine ? 'justify-end' : 'justify-start'}`}>
              <div className={`max-w-[75%] rounded-lg px-3 py-1.5 shadow-sm ${mine ? 'bg-wa-out dark:bg-wa-outDark' : 'bg-white dark:bg-wa-panel'}`}>
                <p className="whitespace-pre-wrap break-words">{m.content}</p>
                <p className="text-right text-[10px] text-gray-500 dark:text-gray-400">{fmt(m.created_at)}</p>
              </div>
            </div>
          )
        })}
        <div ref={bottomRef} />
      </div>

      <form onSubmit={send} className="flex items-center gap-2 bg-gray-100 dark:bg-wa-panel px-3 py-3">
        <input value={text} onChange={e => setText(e.target.value)} placeholder="Type a message"
          className="flex-1 rounded-full bg-white dark:bg-wa-deep px-4 py-2 outline-none" />
        <button className="rounded-full bg-wa-green px-5 py-2 font-medium text-white hover:opacity-90">Send</button>
      </form>
    </section>
  )
}
