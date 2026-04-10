CREATE TABLE IF NOT EXISTS bookmark (
    id             VARCHAR(32)  NOT NULL PRIMARY KEY,
    user_id        VARCHAR(32)  NOT NULL,
    type  VARCHAR(20)  NOT NULL,
    target_id      VARCHAR(32)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_bookmark_type_target UNIQUE (user_id, type, target_id),
    CONSTRAINT fk_ub_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_ub_user_type (user_id, type)
);
