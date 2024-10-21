INSERT INTO users(id, name, username, email) VALUES (1, 'user', 'user', 'user@icloud.com');
INSERT INTO posts(id, title, body, user_id) VALUES (1, 'post 1', 'body 1', 1);
INSERT INTO posts(id, title, body, user_id) VALUES (2, 'post 2', 'body 2', 1);

-- ALTER SEQUENCE  hibernate_sequence RESTART WITH 3;