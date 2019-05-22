
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
INSERT INTO roles (name) VALUES ('ROLE_REGULAR');


INSERT INTO users (email, password, certificate, active) VALUES ('admin@g.com', '$2a$10$mjuVz7gxtaEZ4.hTgGHI..CZhUGBXkBOjNxbWEOq8w/giWlqYrjnu', 'admin@g.com.jks', true)
INSERT INTO users (email, password, certificate, active) VALUES ('user1@g.com', '$2a$10$4DGLAJOY8DAjJL9IbZ4tGetRONqvD3NJZHOLYhG.AlSDhc5UREUcG', 'user1@g.com.jks', true);
INSERT INTO users (email, password, certificate, active) VALUES ('user2@g.com', '$2a$10$.i0xddtUiFUK01jdeQmFBehONdYlxuZrO7O9oyQFO10AjV9.jBPLy', 'user2@g.com.jks', true);
INSERT INTO users (email, password, certificate, active) VALUES ('user3@g.com', '$2a$10$wYULtyeq9qVhmqKsGIBPT.eBIoo2/GeGtxBEp2G8x9MNP8GFVt4Ju', 'user3@g.com.jks', false);


INSERT INTO users_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO users_roles (user_id, role_id) VALUES (1, 2);
INSERT INTO users_roles (user_id, role_id) VALUES (2, 1);
INSERT INTO users_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO users_roles (user_id, role_id) VALUES (3, 2);
INSERT INTO users_roles (user_id, role_id) VALUES (4, 2);
