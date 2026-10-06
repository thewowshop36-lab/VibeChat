-- ============ PROFILES ============
create table public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  username text unique not null,
  created_at timestamptz not null default now()
);
alter table public.profiles enable row level security;

-- Any signed-in user can see the user list; users edit only their own row
create policy "profiles readable by authenticated" on public.profiles
  for select to authenticated using (true);
create policy "users update own profile" on public.profiles
  for update to authenticated using (auth.uid() = id) with check (auth.uid() = id);

-- Auto-create a profile whenever someone signs up (username comes from sign-up metadata)
create or replace function public.handle_new_user() returns trigger
language plpgsql security definer set search_path = public as $$
begin
  insert into public.profiles (id, username)
  values (new.id, coalesce(new.raw_user_meta_data->>'username', split_part(new.email,'@',1)));
  return new;
end $$;
create trigger on_auth_user_created after insert on auth.users
  for each row execute function public.handle_new_user();

-- ============ MESSAGES ============
create table public.messages (
  id bigint generated always as identity primary key,
  sender_id uuid not null references public.profiles(id) on delete cascade,
  receiver_id uuid not null references public.profiles(id) on delete cascade,
  content text not null check (char_length(content) between 1 and 4000),
  created_at timestamptz not null default now()
);
create index messages_pair_idx on public.messages (sender_id, receiver_id, created_at);
create index messages_receiver_idx on public.messages (receiver_id, created_at);
alter table public.messages enable row level security;

-- You can only read messages you sent or received
create policy "read own messages" on public.messages
  for select to authenticated
  using (auth.uid() = sender_id or auth.uid() = receiver_id);
-- You can only send messages as yourself
create policy "send as self" on public.messages
  for insert to authenticated with check (auth.uid() = sender_id);

-- Enable Realtime (RLS is enforced on realtime events too)
alter publication supabase_realtime add table public.messages;
