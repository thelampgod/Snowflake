CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY,
    pubkey TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS groups (
    id INTEGER PRIMARY KEY,
    name COLLATE NOCASE NOT NULL,
    owner_id INTEGER NOT NULL,

    -- Delete deleted group reference in users table on group delete
    FOREIGN KEY(id) REFERENCES group_users(group_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS group_users (
    user_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,

    UNIQUE(user_id, group_id)
);