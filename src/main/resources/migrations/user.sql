CREATE TABLE users (
    id VARCHAR(32) PRIMARY KEY,
    profile_url TEXT,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    username VARCHAR(255),
    email VARCHAR(255),
    password TEXT,
    roles JSON DEFAULT (JSON_ARRAY('ROLE_USER')),         -- Assumes JSON array of roles
    is_locked BOOLEAN DEFAULT FALSE,
    is_enabled BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    password_updated_at TIMESTAMP NULL
);

--no provider means manual
CREATE TABLE user_auth_provider (
    id VARCHAR(32) PRIMARY KEY,
    user_id VARCHAR(32) NOT NULL,                      -- FK to users.id
    provider VARCHAR(50) NOT NULL,                -- e.g., 'google', 'github'
    provider_user_id VARCHAR(255) NOT NULL,       -- e.g., Google's `sub`, GitHub's user ID
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT uq_provider_user UNIQUE (provider, provider_user_id)
);
