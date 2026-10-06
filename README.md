# VibeChat - WhatsApp Web Experience

A real-time WhatsApp Web clone built with **React**, **Vite**, **Tailwind CSS**, and **Supabase**, ready for 1-click deployment on **Vercel**.

---

## 🚀 1. Deploy on Vercel

1. Push this repository to your **GitHub**.
2. Go to **[vercel.com](https://vercel.com)** -> **Add New Project** -> **Import Git Repository**.
3. Framework Preset: **Vite**
4. Build Command: `npm run build`
5. Output Directory: `dist`
6. Under **Environment Variables**, add:
   - `VITE_SUPABASE_URL` = `https://your-project.supabase.co`
   - `VITE_SUPABASE_ANON_KEY` = `your-anon-key`
7. Click **Deploy**! 

*(Note: `vite` is included directly in `dependencies` in `package.json` and configured in `vercel.json` so Vercel builds cleanly without `vite: command not found` error!)*

---

## 🗄️ 2. Supabase Setup

1. Create a project on **[supabase.com](https://supabase.com)**.
2. Go to **SQL Editor** -> copy and paste the contents of `schema.sql` -> click **Run**.
3. Go to **Authentication** -> **Providers** -> **Email**:
   - (Optional for testing) Turn off "Confirm email" so test users log in immediately.
4. Go to **Project Settings** -> **API** -> copy:
   - **Project URL**
   - **anon public key**
5. Enter these keys in Vercel or directly in the app's **Supabase Connect** modal!

---

## ✨ Features Included
- **Chats**: 1-on-1 direct messaging, online presence indicators, message checkmarks, timestamps, typing indicator.
- **Updates (Status)**: WhatsApp status stories with automatic 5s timer progress bar, My Status creation, and story reply bar.
- **Communities**: WhatsApp Communities with Announcement channels, subgroups, and member counters.
- **Calls**: Call history logs, "Create call link", and live simulated Audio/Video calling screen.
- **Dark & Light Mode**: WhatsApp-inspired emerald theme with persistent theme toggling.
- **Supabase & Offline Demo Mode**: Works instantly with demo accounts even before Supabase is connected!
