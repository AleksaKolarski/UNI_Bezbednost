
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
INSERT INTO roles (name) VALUES ('ROLE_REGULAR');

INSERT INTO users (email, password, certificate, active) VALUES ('admin@g.com', '$2a$10$9YE6vhq8pq6lBYHSSmoc3eL/160EwDac0WZWlhf.OKeiRQ2d6q0hW', 'cert1.c', true);

INSERT INTO users_roles (user_id, role_id) VALUES (1, 1);