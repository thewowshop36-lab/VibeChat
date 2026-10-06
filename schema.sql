-- ============================================
-- VibeChat - Complete Supabase Database Schema
-- Paste and run this script in Supabase SQL Editor
-- ============================================

-- 1. Profiles Table
create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  username text unique not null,
  email text,
  avatar_color text default '#00A884',
  status_message text default 'Hey there! I am using VibeChat.',
  is_online boolean default false,
  last_seen timestamptz default now(),
  created_at timestamptz not null default now()
);

alter table public.profiles enable row level security;

-- Profiles RLS Policies
create policy "profiles readable by authenticated" on public.profiles
  for select to authenticated using (true);

create policy "profiles readable by anon for demo" on public.profiles
  for select to anon using (true);

create policy "users update own profile" on public.profiles
  for update to authenticated using (auth.uid() = id) with check (auth.uid() = id);

create policy "users insert own profile" on public.profiles
  for insert to authenticated with check (auth.uid() = id);

-- Auto-create profile trigger on auth signup
create or replace function public.handle_new_user() returns trigger
language plpgsql security definer set search_path = public as $$
begin
  insert into public.profiles (id, username, email)
  values (
    new.id,
    coalesce(new.raw_user_meta_data->>'username', split_part(new.email, '@', 1)),
    new.email
  ) on conflict (id) do nothing;
  return new;
end $$;

drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created after insert on auth.users
  for each row execute function public.handle_new_user();

-- 2. Messages Table
create table if not exists public.messages (
  id bigint generated always as identity primary key,
  sender_id uuid not null references public.profiles(id) on delete cascade,
  receiver_id uuid not null references public.profiles(id) on delete cascade,
  content text not null check (char_length(content) between 1 and 4000),
  created_at timestamptz not null default now()
);

create index if not exists messages_pair_idx on public.messages (sender_id, receiver_id, created_at);
create index if not exists messages_receiver_idx on public.messages (receiver_id, created_at);
alter table public.messages enable row level security;

-- Messages RLS Policies
create policy "read own messages" on public.messages
  for select to authenticated
  using (auth.uid() = sender_id or auth.uid() = receiver_id);

create policy "send as self" on public.messages
  for insert to authenticated with check (auth.uid() = sender_id);

-- 3. Enable Realtime Publications
alter publication supabase_realtime add table public.messages;
alter publication supabase_realtime add table public.profiles;
