ALTER TABLE post
    ADD COLUMN IF NOT EXISTS verified boolean;

ALTER TABLE post
    ADD COLUMN IF NOT EXISTS verifiedDate timestamptz;