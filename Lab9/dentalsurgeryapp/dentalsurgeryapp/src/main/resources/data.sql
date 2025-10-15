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


-- Insert user with password 'admin123'
INSERT IGNORE INTO users (firstName, lastName, username, password, email, enabled, accountNonExpired, accountNonLocked, credentialsNonExpired) VALUES 
('Test', 'User', 'testuser', '$2a$10$.XjD86rqkylsO4FQ5eG/pu204QrVdCpM1ixVkA0Rli61GPC07tFFq', 'test@dentalsurgery.com', 1, 1, 1, 1);

-- Insert user Jim B with password 'admin123'
INSERT IGNORE INTO users (firstName, lastName, username, password, email, enabled, accountNonExpired, accountNonLocked, credentialsNonExpired) VALUES 
('Jim', 'Brown', 'jim.b@gmail.com', '$2a$10$.XjD86rqkylsO4FQ5eG/pu204QrVdCpM1ixVkA0Rli61GPC07tFFq', 'jim.b@gmail.com', 1, 1, 1, 1);

-- Insert user Tony Smith with password 'admin123'
INSERT IGNORE INTO users (firstName, lastName, username, password, email, enabled, accountNonExpired, accountNonLocked, credentialsNonExpired) VALUES 
('Tony', 'Smith', 'tony.smith@southwest.dentists.org', '$2a$10$.XjD86rqkylsO4FQ5eG/pu204QrVdCpM1ixVkA0Rli61GPC07tFFq', 'tony.smith@southwest.dentists.org', 1, 1, 1, 1);

-- Assign SYSADMIN role to admin users
INSERT IGNORE INTO users_roles (user_id, role_id) VALUES (1, 1);
INSERT IGNORE INTO users_roles (user_id, role_id) VALUES (4, 1);


-- Insert Dentist records
INSERT IGNORE INTO dentists (dentist_id, firstName, lastName, email, contactNumber, specialization) VALUES 
(1, 'Test', 'User', 'test@dentalsurgery.com', '555-0100', 'General Dentistry');

INSERT IGNORE INTO dentists (dentist_id, firstName, lastName, email, contactNumber, specialization) VALUES 
(2, 'Tony', 'Smith', 'tony.smith@southwest.dentists.org', '555-0200', 'Orthodontics');

-- Insert Patient records  
INSERT IGNORE INTO patients (patient_id, firstName, lastName, email, contactNumber, dob) VALUES 
(1, 'Test', 'User', 'test@dentalsurgery.com', '555-0100', '1990-01-15');

INSERT IGNORE INTO patients (patient_id, firstName, lastName, email, contactNumber, dob) VALUES 
(2, 'Jim', 'Brown', 'jim.b@gmail.com', '555-0300', '1985-05-20');

-- Update users to link them with dentist/patient records
UPDATE users SET dentist_id = 1 WHERE user_id = 3; -- Test User is a dentist
UPDATE users SET dentist_id = 2 WHERE user_id = 6; -- Tony Smith is a dentist
UPDATE users SET patient_id = 1 WHERE user_id = 2; -- Link one of the PATIENT role users to Test User patient record
UPDATE users SET patient_id = 2 WHERE user_id = 5; -- Jim B is a patient

-- Assign PATIENT role to users
INSERT IGNORE INTO users_roles (user_id, role_id) VALUES (5, 3); -- Jim B as patient
INSERT IGNORE INTO users_roles (user_id, role_id) VALUES (2, 3); -- Second patient

--Assign DENTIST role to Test User
INSERT IGNORE INTO users_roles (user_id, role_id) VALUES (3, 2);

--Assign DENTIST role to Tony Smith
INSERT IGNORE INTO users_roles (user_id, role_id) VALUES (6, 2);