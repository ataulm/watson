CREATE TABLE IF NOT EXISTS 'trending_shows' (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    created INTEGER NOT NULL,
    json TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS 'popular_shows' (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    created INTEGER NOT NULL,
    json TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS 'tracked_shows' (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    created INTEGER NOT NULL,
    tmdb_show_id TEXT NOT NULL UNIQUE ON CONFLICT IGNORE
);

CREATE TABLE IF NOT EXISTS 'show_details' (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    created INTEGER NOT NULL,
    trakt_show_id TEXT NOT NULL UNIQUE ON CONFLICT REPLACE,
    json TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS 'show_people' (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    created INTEGER NOT NULL,
    trakt_show_id TEXT NOT NULL UNIQUE ON CONFLICT REPLACE,
    json TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS 'seasons' (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    created INTEGER NOT NULL,
    trakt_show_id TEXT NOT NULL UNIQUE ON CONFLICT REPLACE,
    json TEXT NOT NULL
);
