ALTER TABLE problem_archive
    CHANGE category difficulty ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL;


CREATE TABLE tag (
    id VARCHAR(32) PRIMARY KEY,  -- Assuming `id` is a VARCHAR, adjust length if necessary.
    title VARCHAR(255) NOT NULL UNIQUE,  -- Title cannot be NULL and must be unique.
    image_url VARCHAR(255) NOT NULL UNIQUE,
    description TEXT  -- Description can be any length, TEXT is appropriate.
);

CREATE TABLE problem_tag_association (
    id VARCHAR(32) PRIMARY KEY,
    tag_id VARCHAR(32) NOT NULL,
    problem_id VARCHAR(32) NOT NULL,
    CONSTRAINT uq_tag_problem UNIQUE (tag_id, problem_id), -- Unique constraint name
    CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE, -- Foreign key name for tag_id
    CONSTRAINT fk_problem FOREIGN KEY (problem_id) REFERENCES problem_archive(id) ON DELETE CASCADE -- Foreign key name for problem_id
);