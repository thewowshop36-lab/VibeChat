import React, { useState, useEffect } from 'react'
import { Plus, Camera, X, ArrowLeft, Send } from 'lucide-react'

export default function UpdatesTab({ me, stories, onAddStory, onReplyStory }) {
  const [activeStory, setActiveStory] = useState(null)
  const [progress, setProgress] = useState(0)
  const [replyText, setReplyText] = useState('')

  // Story Timer
  useEffect(() => {
    if (!activeStory) {
      setProgress(0)
      return
    }
    const interval = setInterval(() => {
      setProgress(p => {
        if (p >= 100) {
          clearInterval(interval)
          setActiveStory(null)
          return 0
        }
        return p + 2
      })
    }, 100)

    return () => clearInterval(interval)
  }, [activeStory])

  function handleReply(e) {
    e.preventDefault()
    if (!replyText.trim() || !activeStory) return
    onReplyStory(activeStory.userId, replyText.trim())
    setReplyText('')
    setActiveStory(null)
  }

  const myStory = stories.find(s => s.isMine)
  const otherStories = stories.filter(s => !s.isMine)

  return (
    <div className="flex-1 flex flex-col h-full bg-white dark:bg-wa-deep overflow-y-auto">
      {/* Header */}
      <div className="flex items-center justify-between px-4 py-3 bg-gray-100 dark:bg-wa-panel border-b border-gray-200 dark:border-gray-800">
        <h2 className="text-lg font-bold">Updates</h2>
        <div className="flex gap-2">
          <button
            onClick={() => onAddStory("Living life in high definition ✨")}
            className="flex items-center gap-1 text-xs px-2.5 py-1.5 rounded-lg bg-wa-green text-white hover:bg-wa-green-dark"
          >
            <Camera size={14} /> Add Status
          </button>
        </div>
      </div>

      <div className="p-4 space-y-4">
        {/* My Status */}
        <div
          onClick={() => myStory ? setActiveStory(myStory) : onAddStory("VibeChat is live on Vercel! 🚀")}
          className="flex items-center gap-3 p-2 rounded-xl hover:bg-gray-100 dark:hover:bg-wa-panel cursor-pointer transition-colors"
        >
          <div className="relative">
            <div className="w-12 h-12 rounded-full bg-wa-green flex items-center justify-center font-bold text-white text-lg">
              {me?.user_metadata?.username?.[0]?.toUpperCase() || 'M'}
            </div>
            <div className="absolute bottom-0 right-0 w-4 h-4 rounded-full bg-wa-green border-2 border-white dark:border-wa-deep flex items-center justify-center text-white">
              <Plus size={12} />
            </div>
          </div>
          <div>
            <h4 className="font-semibold text-sm">My status</h4>
            <p className="text-xs text-gray-500 dark:text-gray-400">
              {myStory ? 'Tap to view your update' : 'Tap to add status update'}
            </p>
          </div>
        </div>

        {/* Recent Updates */}
        <div>
          <h3 className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">Recent Updates</h3>
          <div className="space-y-1">
            {otherStories.map(story => (
              <div
                key={story.id}
                onClick={() => { setActiveStory(story); setProgress(0); }}
                className="flex items-center gap-3 p-2 rounded-xl hover:bg-gray-100 dark:hover:bg-wa-panel cursor-pointer transition-colors"
              >
                <div className={`p-0.5 rounded-full border-2 ${story.isViewed ? 'border-gray-400' : 'border-wa-green'}`}>
                  <div className="w-11 h-11 rounded-full bg-wa-teal flex items-center justify-center font-bold text-white">
                    {story.username[0].toUpperCase()}
                  </div>
                </div>
                <div>
                  <h4 className="font-semibold text-sm">{story.username}</h4>
                  <p className="text-xs text-gray-500 dark:text-gray-400">{story.time}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Full Screen Status Story Viewer Modal */}
      {activeStory && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/90 p-4 backdrop-blur-md animate-fadeIn">
          <div className="relative w-full max-w-sm h-[580px] rounded-3xl overflow-hidden flex flex-col justify-between p-6 shadow-2xl text-white" style={{ background: activeStory.bgGradient || 'linear-gradient(135deg, #005C4B 0%, #111B21 100%)' }}>
            {/* Top Bar */}
            <div>
              {/* Progress Line */}
              <div className="w-full bg-white/30 h-1 rounded-full overflow-hidden mb-4">
                <div className="bg-white h-full transition-all duration-100 ease-linear" style={{ width: `${progress}%` }} />
              </div>

              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-9 h-9 rounded-full bg-white/20 flex items-center justify-center font-bold text-sm">
                    {activeStory.username[0].toUpperCase()}
                  </div>
                  <div>
                    <h4 className="font-semibold text-sm">{activeStory.username}</h4>
                    <p className="text-xs text-white/70">{activeStory.time || 'Today'}</p>
                  </div>
                </div>
                <button onClick={() => setActiveStory(null)} className="p-1 rounded-full hover:bg-white/20">
                  <X size={20} />
                </button>
              </div>
            </div>

            {/* Story Text */}
            <div className="text-center px-4 py-8">
              <p className="text-2xl font-bold leading-relaxed">{activeStory.caption}</p>
            </div>

            {/* Bottom Reply Bar */}
            <form onSubmit={handleReply} className="flex gap-2">
              <input
                type="text"
                placeholder={`Reply to ${activeStory.username}...`}
                value={replyText}
                onChange={e => setReplyText(e.target.value)}
                className="flex-1 rounded-full bg-white/20 px-4 py-2 text-sm text-white placeholder-white/60 outline-none backdrop-blur-sm border border-white/20 focus:bg-white/30"
              />
              <button type="submit" className="w-10 h-10 rounded-full bg-wa-green flex items-center justify-center text-white shadow-lg">
                <Send size={16} />
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
