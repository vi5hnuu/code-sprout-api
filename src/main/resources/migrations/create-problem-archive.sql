CREATE TABLE problem_archive (
    id VARCHAR(32) PRIMARY KEY,  -- Assuming `id` is a VARCHAR, adjust length if necessary.
    title VARCHAR(255) NOT NULL UNIQUE,  -- Title cannot be NULL and must be unique.
    description TEXT,  -- Description can be any length, TEXT is appropriate.
    language ENUM('CPP','SQL','JAVASCRIPT') NOT NULL,  -- Language can only be 'cpp'.
    category ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL,  -- Category can be one of 'easy', 'medium', or 'hard'.
    file_path VARCHAR(255) NOT NULL  -- File path cannot be NULL.
);