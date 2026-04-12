CREATE TABLE IF NOT EXISTS user_note (
    id          VARCHAR(32)  NOT NULL PRIMARY KEY,
    user_id     VARCHAR(32)  NOT NULL,
    problem_id  VARCHAR(32)  NOT NULL,
    content     TEXT,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_note_user_problem UNIQUE (user_id, problem_id),
    CONSTRAINT fk_note_user    FOREIGN KEY (user_id)    REFERENCES users(id)            ON DELETE CASCADE,
    CONSTRAINT fk_note_problem FOREIGN KEY (problem_id) REFERENCES problem_archive(id)  ON DELETE CASCADE
);
