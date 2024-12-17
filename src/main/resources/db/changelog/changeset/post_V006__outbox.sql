CREATE TABLE outbox_event (
    id BIGSERIAL PRIMARY KEY,
    aggregate_id BIGINT NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    processed BOOLEAN DEFAULT FALSE NOT NULL
);