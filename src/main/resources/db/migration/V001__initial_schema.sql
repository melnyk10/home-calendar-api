create table if not exists provider
(
    id          bigserial primary key,
    name        text unique not null, -- e.g. 'hltv', 'sonarr'
    description text
);

insert into provider (name, description)
values ('HLTV', 'hltv.org: cs2 tournaments & matches'),
       ('SONARR', 'sonarr: tv series & episodes')
on conflict (name) do nothing;

-- ==============
-- subjects (things users subscribe to: tournament, tv_show, movie, team, etc.)
-- ==============
create table if not exists subject
(
    id          bigserial primary key,
    source_id   varchar(64) not null,                     -- provider-scoped id/slug
    provider_id bigint      not null references provider (id) on delete cascade,
    type        text        not null,                     -- 'tournament', 'tv_show', 'team', ...
    name        varchar(64) not null,
    data        jsonb       not null default '{}'::jsonb, -- arbitrary metadata/external_refs
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now(),

    constraint subject_unique_external
        unique (provider_id, type, source_id)
            deferrable initially immediate
-- That makes PostgreSQL check the constraint only when you commit, not right after each insert/update.
);

create table if not exists event
(
    id          uuid primary key     default gen_random_uuid(),
    source_id   varchar(64) not null,
    provider_id bigint      not null references provider (id) on delete cascade,
    subject_id  bigint      not null references subject (id) on delete cascade,
    type        text        not null,                     -- 'match.scheduled', 'match.resulted', 'episode.released', ...
    title       text,
    description text,
    start_at    timestamptz not null,
    endAt       timestamptz not null,
    status      text        not null check (status in ('scheduled', 'completed', 'canceled', 'updated')),
    payload     jsonb       not null default '{}'::jsonb, -- event-type-specific data
    tags        text[]      not null default '{}',        -- free-form labels
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now(),

    -- precomputed change detector (hash of stable presentation fields)
    hash        text generated always as
        (md5(
                coalesce(title, '') || '|' ||
                coalesce(description, '') || '|' ||
                coalesce(status::text, '') || '|' ||
                coalesce(subject_id, '') || '|' ||
                coalesce(type, '') || '|' ||
                coalesce(provider_id, '') || '|' ||
                coalesce(start_at::text, '') || '|' ||
                coalesce((payload #>> '{}'), '')          -- flatten json to text
         )) stored
);

-- indexes for common queries
create index if not exists idx_event_subject_time on event (subject_id, start_at);
create index if not exists idx_event_type_time on event (type, start_at);
create index if not exists idx_event_provider_time on event (provider_id, start_at);
create index if not exists idx_event_tags_gin on event using gin (tags);
create index if not exists idx_event_payload_gin on event using gin (payload jsonb_path_ops);

create table if not exists user
(
    id         bigserial primary key,
    first_name varchar(32),
    last_name  varchar(32),
    email      text unique,
    created_at timestamptz not null default now()
);

create table if not exists subscription
(
    id         bigserial primary key,
    user_id    bigint      not null references user (id) on delete cascade,
    is_active  bool        not null default true,
    subject_id bigint      not null references subject (id) on delete cascade,
    created_at timestamptz not null default now(),

    constraint uq_subscription unique (user_id, subject_id)
);

create index if not exists idx_subscription_user on subscription (user_id);
create index if not exists idx_subscription_subject on subscription (subject_id);

create table user_subscription
(
    id         bigserial primary key,
    user_id    bigint      not null references user (id) on delete cascade,
    provider   text, -- optional filter by provider
    subject_id text, -- 'sonarr:series:the-boys-2019'
    created_at timestamptz not null default now(),

    unique (user_id, provider, subject_id)
);

create table if not exists calendar
(
    id                 serial primary key,
    source_calendar_id text,
    name               text        not null,
    provider           text        not null, -- e.g. 'google','outlook','ics'
    account_email      text,
    user_id            bigint      not null references user (id) on delete cascade,
    created_at         timestamptz not null default now(),
    updated_at         timestamptz not null default now(),
    unique (user_id, name)
);

create table calendar_connection
(
    id                 bigserial primary key,
    user_id            bigint      not null references user (id) on delete cascade,
    calendar_id        bigint      not null references calendar (id) on delete cascade,
    access_token       text,
    refresh_token      text,
    token_expires_at   timestamptz,
    scopes             text[],
    source_calendar_id text,
    created_at         timestamptz not null default now(),
    updated_at         timestamptz not null default now(),
    unique (user_id, calendar_id)
);

create table user_calendar_event
(
    id               bigserial primary key,
    user_calendar_id bigint      not null references calendar_connection (id),
    event_id         bigint      not null references event (id) on delete cascade,
    source_event_id  text, -- id in Google/Outlook/etc.
    created_at       timestamptz not null default now(),
    updated_at       timestamptz not null default now(),

    unique (user_calendar_id, event_id)
);
create index idx_uce_event on user_calendar_event (event_id);
