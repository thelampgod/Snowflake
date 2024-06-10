CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY,
    pubkey TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS groups (
    id INTEGER PRIMARY KEY,
    name COLLATE NOCASE NOT NULL,
    owner_id INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS group_users (
    user_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,

    UNIQUE(user_id, group_id),

    -- Delete rows when the referenced group is deleted
    FOREIGN KEY(group_id) REFERENCES groups(id) ON DELETE CASCADE,

    -- Delete rows when the referenced user is deleted
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);