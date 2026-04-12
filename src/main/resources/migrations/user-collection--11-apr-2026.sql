CREATE TABLE IF NOT EXISTS user_collection (
    id          VARCHAR(32)   NOT NULL PRIMARY KEY,
    user_id     VARCHAR(32)   NOT NULL,
    name        VARCHAR(100)  NOT NULL,
    description VARCHAR(500),
    is_public   TINYINT(1)    NOT NULL DEFAULT 0,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_col_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_col_user (user_id)
);

CREATE TABLE IF NOT EXISTS user_collection_item (
    id            VARCHAR(32) NOT NULL PRIMARY KEY,
    collection_id VARCHAR(32) NOT NULL,
    problem_id    VARCHAR(32) NOT NULL,
    added_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_col_item         UNIQUE (collection_id, problem_id),
    CONSTRAINT fk_ci_collection    FOREIGN KEY (collection_id) REFERENCES user_collection(id) ON DELETE CASCADE,
    CONSTRAINT fk_ci_problem       FOREIGN KEY (problem_id)    REFERENCES problem_archive(id) ON DELETE CASCADE,
    INDEX idx_ci_collection (collection_id)
);
