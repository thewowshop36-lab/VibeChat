import { createClient } from '@supabase/supabase-js'

// Vite exposes only variables prefixed with VITE_. The anon key is safe in the browser
// because Row Level Security (see schema.sql) protects the data.
export const supabase = createClient(
  import.meta.env.VITE_SUPABASE_URL,
  import.meta.env.VITE_SUPABASE_ANON_KEY
)
