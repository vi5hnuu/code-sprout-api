CREATE TABLE IF NOT EXISTS problem_of_day (
    id             VARCHAR(32) NOT NULL PRIMARY KEY,
    problem_id     VARCHAR(32) NOT NULL,
    scheduled_date DATE        NOT NULL,
    created_at     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_pod_date     UNIQUE (scheduled_date),
    CONSTRAINT fk_pod_problem  FOREIGN KEY (problem_id) REFERENCES problem_archive(id) ON DELETE CASCADE,
    INDEX idx_pod_date (scheduled_date)
);
