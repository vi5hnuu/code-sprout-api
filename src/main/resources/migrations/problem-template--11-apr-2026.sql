CREATE TABLE IF NOT EXISTS problem_template (
    id            VARCHAR(32)  NOT NULL PRIMARY KEY,
    problem_id    VARCHAR(32)  NOT NULL,
    language      VARCHAR(20)  NOT NULL,
    template_code TEXT         NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_template_problem_lang UNIQUE (problem_id, language),
    CONSTRAINT fk_template_problem FOREIGN KEY (problem_id) REFERENCES problem_archive(id) ON DELETE CASCADE,
    INDEX idx_template_problem (problem_id)
);
