    -- Initial data for dental surgery app

-- Insert roles if they don't exist
INSERT IGNORE INTO roles (role_id, name) VALUES 
(1, 'SYSADMIN'),
(2, 'DENTIST'),
(3, 'PATIENT');

-- Insert default admin user
-- Password is 'admin123' encoded with BCrypt (using camelCase column names to match PhysicalNamingStrategyStandardImpl)
INSERT IGNORE INTO users ( firstName, lastName, username, password, email, enabled, accountNonExpired, accountNonLocked, credentialsNonExpired) VALUES 
( 'System', 'Administrator', 'admin', '$2a$10$.XjD86rqkylsO4FQ5eG/pu204QrVdCpM1ixVkA0Rli61GPC07tFFq', 'admin@dentalsurgery.com', 1, 1, 1, 1);

-- Insert second admin user
INSERT IGNORE INTO users ( firstName, lastName, username, password, email, enabled, accountNonExpired, accountNonLocked, credentialsNonExpired) VALUES 
('System', 'Administrator', 'admin2', '$2a$10$.XjD86rqkylsO4FQ5eG/pu204QrVdCpM1ixVkA0Rli61GPC07tFFq', 'admin2@dentalsurgery.com', 1, 1, 1, 1);


-- Insert user with password 'ts123'
INSERT IGNORE INTO users (firstName, lastName, username, password, email, enabled, accountNonExpired, accountNonLocked, credentialsNonExpired) VALUES 
('Test', 'User', 'testuser', '$2a$10$KCb7gH9L4qN6Eo8xP5xU4eGHfKN5QvYzR8pB2mC3dD4fE5fG6hH7i', 'test@dentalsurgery.com', 1, 1, 1, 1);

-- Insert user Jim B with password 'jb123'
INSERT IGNORE INTO users (firstName, lastName, username, password, email, enabled, accountNonExpired, accountNonLocked, credentialsNonExpired) VALUES 
('Jim', 'Brown', 'jim.b@gmail.com', '$2a$10$dNTtBsHzUqMBYZNq6c.NveKqF5O5JJ1oHdJb8Cjp.jZN4O9xP5Uy6', 'jim.b@gmail.com', 1, 1, 1, 1);

-- Assign SYSADMIN role to admin users
INSERT IGNORE INTO users_roles (user_id, role_id) VALUES (1, 1);
INSERT IGNORE INTO users_roles (user_id, role_id) VALUES (4, 1);
INSERT IGNORE INTO users_roles (user_id, role_id) VALUES (2, 3);
INSERT IGNORE INTO users_roles (user_id, role_id) VALUES (3, 2);
-- Assign PATIENT role to Jim B
INSERT IGNORE INTO users_roles (user_id, role_id) VALUES (5, 3);