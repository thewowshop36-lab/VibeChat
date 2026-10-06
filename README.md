# WA Chat (React + Vite + Tailwind + Supabase)

## 1. Supabase setup
1. Create a project at https://supabase.com.
2. SQL Editor -> paste and run `supabase/schema.sql`.
3. (Dev convenience) Authentication -> Providers -> Email -> turn off "Confirm email" so sign-up logs in immediately.
4. Project Settings -> API -> copy the Project URL and the `anon` public key.

## 2. Run locally
    cp .env.example .env     # fill in the two values
    npm install
    npm run dev              # open two browsers/incognito windows with two accounts

## 3. Deploy (Vercel / Netlify)
- Push to GitHub, import the repo. Build command: `npm run build`, output dir: `dist`.
- Add env vars `VITE_SUPABASE_URL` and `VITE_SUPABASE_ANON_KEY` in the host's dashboard, then redeploy.
- Supabase -> Authentication -> URL Configuration: set Site URL to your deployed URL.
- Never put the `service_role` key in the frontend.
