DELETE FROM TODO;
ALTER TABLE TODO
    ADD COLUMN IF NOT EXISTS user_id BIGINT NOT NULL;
ALTER TABLE TODO
    DROP CONSTRAINT IF EXISTS fk_user;
ALTER TABLE TODO
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE ON UPDATE CASCADE;