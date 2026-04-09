-- Add slug field to problem_archive for URL-based routing
-- Slug is derived from title (kebab-case) and must be unique per problem.
-- Back-fill existing rows with a slug generated from title before adding the constraint.

-- Step 1: Add nullable slug column
ALTER TABLE problem_archive
    ADD COLUMN slug VARCHAR(255) NULL;

-- Step 2: Back-fill existing rows: lowercase title, spaces→hyphens, strip non-alphanumeric
UPDATE problem_archive
SET slug = LOWER(
    REGEXP_REPLACE(
        REGEXP_REPLACE(TRIM(title), '[^a-zA-Z0-9\\s-]', '', 'g'),
        '\\s+', '-', 'g'
    )
)
WHERE slug IS NULL;

-- Step 3: Enforce NOT NULL + UNIQUE once back-filled
ALTER TABLE problem_archive
    ALTER COLUMN slug SET NOT NULL;

ALTER TABLE problem_archive
    ADD CONSTRAINT uq_problem_archive_slug UNIQUE (slug);

-- Index for fast slug lookups (single-column; covered by the unique constraint in most engines
-- but explicit for clarity and compatibility)
CREATE INDEX IF NOT EXISTS idx_problem_archive_slug ON problem_archive (slug);
