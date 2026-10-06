import React, { useState, useEffect } from 'react'
import { PhoneOff, Mic, MicOff, Video, VideoOff, Volume2 } from 'lucide-react'

export default function ActiveCallModal({ contactName, callType, onEndCall }) {
  const [seconds, setSeconds] = useState(0)
  const [isMuted, setIsMuted] = useState(false)
  const [isVideoOff, setIsVideoOff] = useState(callType === 'audio')
  const [isSpeaker, setIsSpeaker] = useState(false)

  useEffect(() => {
    const timer = setInterval(() => setSeconds(s => s + 1), 1000)
    return () => clearInterval(timer)
  }, [])

  const fmt = (sec) => {
    const m = Math.floor(sec / 60)
    const s = sec % 60
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-md p-4 animate-fadeIn">
      <div className="w-full max-w-sm h-[520px] rounded-3xl bg-gradient-to-b from-gray-900 to-wa-deep p-8 flex flex-col justify-between items-center text-white shadow-2xl border border-gray-800">
        <div className="text-center space-y-3 pt-6">
          <div className="w-24 h-24 rounded-full bg-wa-teal mx-auto flex items-center justify-center text-3xl font-bold shadow-xl">
            {contactName[0].toUpperCase()}
          </div>
          <h2 className="text-2xl font-bold">{contactName}</h2>
          <p className="text-sm text-wa-green font-medium">
            {seconds < 2 ? 'Ringing…' : fmt(seconds)}
          </p>
          <p className="text-xs text-gray-400">
            {callType === 'video' ? 'WhatsApp Video Call' : 'WhatsApp Voice Call'}
          </p>
        </div>

        {/* Controls */}
        <div className="flex items-center gap-4 bg-white/10 px-6 py-4 rounded-full backdrop-blur-md">
          <button
            onClick={() => setIsSpeaker(!isSpeaker)}
            className={`w-12 h-12 rounded-full flex items-center justify-center transition-colors ${isSpeaker ? 'bg-white text-gray-900' : 'bg-white/20 text-white'}`}
          >
            <Volume2 size={20} />
          </button>

          {callType === 'video' && (
            <button
              onClick={() => setIsVideoOff(!isVideoOff)}
              className={`w-12 h-12 rounded-full flex items-center justify-center transition-colors ${isVideoOff ? 'bg-white text-gray-900' : 'bg-white/20 text-white'}`}
            >
              {isVideoOff ? <VideoOff size={20} /> : <Video size={20} />}
            </button>
          )}

          <button
            onClick={() => setIsMuted(!isMuted)}
            className={`w-12 h-12 rounded-full flex items-center justify-center transition-colors ${isMuted ? 'bg-white text-gray-900' : 'bg-white/20 text-white'}`}
          >
            {isMuted ? <MicOff size={20} /> : <Mic size={20} />}
          </button>

          <button
            onClick={onEndCall}
            className="w-12 h-12 rounded-full bg-red-600 hover:bg-red-700 flex items-center justify-center text-white shadow-lg shadow-red-600/40"
          >
            <PhoneOff size={20} />
          </button>
        </div>
      </div>
    </div>
  )
}
