CREATE TABLE IF NOT EXISTS movie (
    id            BIGINT PRIMARY KEY,
    title         TEXT NOT NULL,
    vote_average  DOUBLE PRECISION,
    vote_count    INTEGER,
    release_date  DATE,
    revenue       BIGINT,
    runtime       INTEGER,
    backdrop_path TEXT,
    budget        BIGINT,
    homepage      TEXT,
    overview      TEXT,
    popularity    DOUBLE PRECISION,
    poster_path   TEXT,
    director      TEXT,
    genres        TEXT[],
    themes        TEXT[]
);

CREATE INDEX IF NOT EXISTS idx_movie_vote_count   ON movie (vote_count DESC);
CREATE INDEX IF NOT EXISTS idx_movie_release_date ON movie (release_date DESC);
