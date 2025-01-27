CREATE TABLE hashtag (
    id uuid primary key,
    name varchar(64) NOT NULL
);

CREATE TABLE post_hashtag (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    post_id bigint not null,
    hashtag_id uuid not null,

    CONSTRAINT fk_post_hashtag_id FOREIGN KEY (post_id) REFERENCES post (id),
    CONSTRAINT fk_hashtag_post_id FOREIGN KEY (hashtag_id) REFERENCES hashtag (id)
);