CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY,
    pubkey TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS recipients (
    recipient_user_id INTEGER,
    pubkey TEXT NOT NULL,--for recipient
    user_id INTEGER, --of user that is sharing
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);