create table if not exists provider
(
    id          serial primary key,
    type        varchar(32) unique not null unique,
    description text
);

insert into provider (type, description)
values ('HLTV', 'hltv.org: CS2 tournaments & matches'),
       ('SONARR', 'sonarr: tv series & episodes')
on conflict (type) do nothing;

create or replace function event_hash(
    p_name text,
    p_description text,
    p_status text,
    p_type text,
    p_provider_id bigint,
    p_start_at timestamptz,
    p_payload jsonb
) returns text
    language sql
    immutable
as
$$
select md5(
               coalesce(p_name, '') || '|' ||
               coalesce(p_description, '') || '|' ||
               coalesce(p_status, '') || '|' ||
               coalesce(p_type, '') || '|' ||
               p_provider_id::text || '|' ||
               (extract(epoch from p_start_at))::bigint::text || '|' ||
               p_payload::text
       );
$$;

create table if not exists event
(
    id          bigserial primary key,
    source_id   varchar(64) not null,
    provider_id bigint      not null references provider (id) on delete cascade,
    type        text        not null,                     -- 'match.scheduled', 'match.resulted', 'episode.released', ...
    name        text,
    details     text,
    is_all_day  bool        not null default false,
    start_at    timestamptz not null,
    end_at      timestamptz not null,
    status      text        not null check (status in ('scheduled', 'completed', 'canceled', 'updated')),
    payload     jsonb       not null default '{}'::jsonb, -- event-type-specific data
    tags        text[]      not null default '{}',        -- free-form labels
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now(),

    -- precomputed change detector (hash of stable presentation fields)

    hash        text generated always as (
        event_hash(name, details, status, type, provider_id, start_at, payload)
        ) stored
);
create index if not exists idx_event_type_time on event (type, start_at);
create index if not exists idx_event_provider_time on event (provider_id, start_at);
create index if not exists idx_event_tags_gin on event using gin (tags);
create index if not exists idx_event_payload_gin on event using gin (payload jsonb_path_ops);

create table target
(
    id          bigserial primary key,
    name        varchar(64) not null,
    source_id   varchar(64) not null,
    type        text        not null,                    -- 'team','season','episode',...
    provider_id bigint      not null references provider (id) on delete cascade,
    data        jsonb       not null default '{}'::jsonb -- arbitrary metadata/external_refs
);

create table event_target
(
    event_id  bigint not null references event (id) on delete cascade,
    target_id bigint not null references target (id) on delete cascade,
    primary key (event_id, target_id)
);

create table if not exists "user"
(
    id         bigserial primary key,
    first_name varchar(32),
    last_name  varchar(32),
    email      text unique,
    created_at timestamptz not null default now()
);

create table user_subscription
(
    id         bigserial primary key,
    user_id    bigint      not null references "user" (id) on delete cascade,
    is_active  bool        not null default true,
    target_id  bigint references target (id) on delete cascade,
    created_at timestamptz not null default now(),

    unique (user_id, target_id)
);

create table if not exists calendar
(
    id                 serial primary key,
    source_calendar_id text,
    name               text        not null,
    provider           text        not null, -- e.g. 'google','outlook','ics'
    account_email      text,
    user_id            bigint      not null references "user" (id) on delete cascade,
    created_at         timestamptz not null default now(),
    updated_at         timestamptz not null default now(),
    unique (user_id, name)
);

create table calendar_connection
(
    id                 bigserial primary key,
    user_id            bigint      not null references "user" (id) on delete cascade,
    calendar_id        int         not null references calendar (id) on delete cascade,
    access_token       text,
    refresh_token      text,
    expires_at         timestamptz,
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
    source_event_id  varchar(64), -- id in Google/Outlook/etc.
    created_at       timestamptz not null default now(),
    updated_at       timestamptz not null default now(),

    unique (user_calendar_id, event_id)
);
create index idx_uce_event on user_calendar_event (event_id);
