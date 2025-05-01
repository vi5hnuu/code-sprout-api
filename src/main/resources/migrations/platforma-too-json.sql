ALTER TABLE problem_archive
ADD COLUMN platforms_temp JSON;

UPDATE problem_archive
SET platforms_temp = CAST(CONVERT(platforms USING utf8mb4) AS JSON);

ALTER TABLE problem_archive
DROP COLUMN platforms;

ALTER TABLE problem_archive
CHANGE COLUMN platforms_temp platforms JSON;
