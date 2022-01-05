UPDATE TODO
SET
    title = ''
WHERE title IS NULL;
UPDATE TODO
SET
    done = false
WHERE done IS NULL;