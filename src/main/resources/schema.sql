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
