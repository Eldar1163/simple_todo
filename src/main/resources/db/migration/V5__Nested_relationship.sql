ALTER TABLE TODO
    ADD COLUMN IF NOT EXISTS parent_id BIGINT;
ALTER TABLE TODO
    ADD CONSTRAINT IF NOT EXISTS fk_parent FOREIGN KEY (parent_id) REFERENCES TODO(id) ON DELETE CASCADE ON UPDATE CASCADE;