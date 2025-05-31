
INSERT INTO roles (created_at, name, description) VALUES (
CURRENT_TIMESTAMP,
'role_manager',
'Role for users that carry out administrative functions on the application'
);

INSERT INTO roles (created_at, name, description) VALUES (
CURRENT_TIMESTAMP,
'role_courier',
'Role for users that carry about delivery functions on the application'
);

INSERT INTO roles (created_at, name, description) VALUES (
CURRENT_TIMESTAMP,
'role_customer',
'Role for regular users of the application'
);