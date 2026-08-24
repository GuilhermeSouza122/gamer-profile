CREATE TABLE IF NOT EXISTS games (
    id BIGSERIAL PRIMARY KEY,
    platform VARCHAR(40) NOT NULL,
    external_id VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    playtime_minutes INTEGER,
    last_played_epoch_seconds BIGINT,
    image_url VARCHAR(500),
    CONSTRAINT uk_games_platform_external_id UNIQUE (platform, external_id)
);

CREATE TABLE IF NOT EXISTS achievements (
    id BIGSERIAL PRIMARY KEY,
    game_id BIGINT NOT NULL REFERENCES games(id),
    external_id VARCHAR(150) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    achieved BOOLEAN NOT NULL,
    unlock_time BIGINT,
    icon_url VARCHAR(500),
    icon_gray_url VARCHAR(500),
    CONSTRAINT uk_achievements_game_external_id UNIQUE (game_id, external_id)
);

CREATE TABLE IF NOT EXISTS app_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(120) NOT NULL UNIQUE,
    display_name VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

ALTER TABLE app_users ADD COLUMN IF NOT EXISTS avatar_key VARCHAR(40) NOT NULL DEFAULT 'cyberpunk';

CREATE TABLE IF NOT EXISTS platform_connections (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_users(id),
    platform VARCHAR(40) NOT NULL,
    external_account_id VARCHAR(150) NOT NULL,
    access_token VARCHAR(2000),
    refresh_token VARCHAR(2000),
    connected_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_platform_connections_user_platform UNIQUE (user_id, platform)
);
