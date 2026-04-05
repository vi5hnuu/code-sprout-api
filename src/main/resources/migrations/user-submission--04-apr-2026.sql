CREATE TABLE IF NOT EXISTS user_submission (
    id          VARCHAR(32)  NOT NULL PRIMARY KEY,
    user_id     VARCHAR(32)  NOT NULL,
    problem_id  VARCHAR(32)  NOT NULL,
    language    VARCHAR(20)  NOT NULL,
    status      VARCHAR(30)  NOT NULL,
    is_official TINYINT(1)   NOT NULL DEFAULT 0,
    submitted_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_us_user    FOREIGN KEY (user_id)    REFERENCES users(id)            ON DELETE CASCADE,
    CONSTRAINT fk_us_problem FOREIGN KEY (problem_id) REFERENCES problem_archive(id)  ON DELETE CASCADE,
    INDEX idx_us_user_id  (user_id),
    INDEX idx_us_problem_id (problem_id),
    INDEX idx_us_user_problem (user_id, problem_id)
);