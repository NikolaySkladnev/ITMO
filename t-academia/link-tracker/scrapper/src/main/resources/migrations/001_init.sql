create table if not exists chats
(
    chat_id    bigint primary key,
    created_at timestamptz not null default now()
);

create table if not exists links
(
    id              bigserial primary key,
    url             varchar(2048) not null unique,
    last_updated_at timestamptz,
    created_at      timestamptz   not null default now()
);

create table if not exists subscriptions
(
    id         bigserial primary key,
    chat_id    bigint      not null references chats (chat_id) on delete cascade,
    link_id    bigint      not null references links (id) on delete cascade,
    created_at timestamptz not null default now(),
    constraint uk_subscriptions_chat_link unique (chat_id, link_id)
);

create table if not exists tags
(
    id         bigserial primary key,
    name       varchar(255) not null unique,
    created_at timestamptz  not null default now()
);

create table if not exists subscription_tags
(
    subscription_id bigint      not null references subscriptions (id) on delete cascade,
    tag_id          bigint      not null references tags (id) on delete cascade,
    created_at      timestamptz not null default now(),
    primary key (subscription_id, tag_id)
);

create index if not exists idx_subscriptions_chat_id on subscriptions (chat_id);
create index if not exists idx_subscriptions_link_id on subscriptions (link_id);
create index if not exists idx_links_last_updated_at on links (last_updated_at);
create index if not exists idx_subscription_tags_subscription_id on subscription_tags (subscription_id);
create index if not exists idx_subscription_tags_tag_id on subscription_tags (tag_id);
