-- QuickURL Database Schema
-- This script is idempotent and safe to run multiple times

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id char(36) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    hashed_password VARCHAR(255) NOT NULL,
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- URLs table
CREATE TABLE IF NOT EXISTS urls (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    short_key VARCHAR(255) UNIQUE,
    original_url TEXT NOT NULL,
    created_by VARCHAR(255) NOT NULL DEFAULT 'anonymous',
    last_used_at DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT uk_ourl_user UNIQUE (original_url(500), created_by),
    INDEX idx_short_key (short_key),
    INDEX idx_created_by (created_by),
    INDEX idx_last_used_at (last_used_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- URL Click Logs table
CREATE TABLE IF NOT EXISTS url_click_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    short_key VARCHAR(20) NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    referer VARCHAR(500),
    clicked_at DATETIME(6) NOT NULL,
    INDEX idx_short_key (short_key),
    INDEX idx_clicked_at (clicked_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Refresh Tokens table
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id char(36) NOT NULL,
    issue_count BIGINT NOT NULL DEFAULT 0,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL,
    INDEX idx_token (token),
    INDEX idx_user_id (user_id),
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Daily Click Count table
CREATE TABLE IF NOT EXISTS daily_click_count (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    daily_clicks BIGINT NOT NULL DEFAULT 0,
    date DATE NOT NULL UNIQUE,
    INDEX idx_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Cumulative Click Count table (MUST have exactly 1 row)
CREATE TABLE IF NOT EXISTS cumulative_click_count (
    id BIGINT PRIMARY KEY DEFAULT 1,
    cumulative_clicks BIGINT NOT NULL DEFAULT 0,
    CHECK (id = 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Initialize cumulative_click_count with single row if not exists
INSERT IGNORE INTO cumulative_click_count (id, cumulative_clicks) VALUES (1, 0);
