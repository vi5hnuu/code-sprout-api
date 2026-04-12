CREATE TABLE IF NOT EXISTS problem_hint (
    id          VARCHAR(32) NOT NULL PRIMARY KEY,
    problem_id  VARCHAR(32) NOT NULL,
    hint_order  INT         NOT NULL DEFAULT 0,
    content     TEXT        NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_hint_problem FOREIGN KEY (problem_id) REFERENCES problem_archive(id) ON DELETE CASCADE,
    INDEX idx_hint_problem (problem_id)
);
