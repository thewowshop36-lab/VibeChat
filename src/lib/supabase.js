import { createClient } from '@supabase/supabase-js'

const envUrl = import.meta.env?.VITE_SUPABASE_URL || ''
const envKey = import.meta.env?.VITE_SUPABASE_ANON_KEY || ''

const localUrl = typeof window !== 'undefined' ? localStorage.getItem('supabase_url') || '' : ''
const localKey = typeof window !== 'undefined' ? localStorage.getItem('supabase_anon_key') || '' : ''

export const SUPABASE_URL = envUrl || localUrl
export const SUPABASE_ANON_KEY = envKey || localKey

export const isSupabaseConfigured = Boolean(
  SUPABASE_URL && 
  SUPABASE_ANON_KEY && 
  SUPABASE_URL.startsWith('http')
)

export const supabase = isSupabaseConfigured
  ? createClient(SUPABASE_URL, SUPABASE_ANON_KEY)
  : null

export function updateSupabaseConfig(url, key) {
  if (typeof window !== 'undefined') {
    localStorage.setItem('supabase_url', url.trim())
    localStorage.setItem('supabase_anon_key', key.trim())
    window.location.reload()
  }
}
