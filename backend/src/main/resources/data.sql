-- Passwords are all 'password' encrypted via BCrypt
INSERT INTO users (name, email, password, role) VALUES 
('System Admin', 'admin@hospital.com', '$2a$10$wO4q4X4n3y1V4B1B8A4R/e3vjJ9T5N9vVlqVx2z5A5sN/C9xG7L/O', 'ADMIN'),
('Dr. Alice Smith', 'alice.smith@hospital.com', '$2a$10$wO4q4X4n3y1V4B1B8A4R/e3vjJ9T5N9vVlqVx2z5A5sN/C9xG7L/O', 'DOCTOR'),
('Dr. Bob Jones', 'bob.jones@hospital.com', '$2a$10$wO4q4X4n3y1V4B1B8A4R/e3vjJ9T5N9vVlqVx2z5A5sN/C9xG7L/O', 'DOCTOR'),
('John Doe', 'john.doe@email.com', '$2a$10$wO4q4X4n3y1V4B1B8A4R/e3vjJ9T5N9vVlqVx2z5A5sN/C9xG7L/O', 'PATIENT');

INSERT INTO departments (name, description) VALUES 
('Cardiology', 'Heart and cardiovascular diseases'),
('Neurology', 'Brain and nervous system'),
('Pediatrics', 'Children healthcare');

INSERT INTO doctors (user_id, specialization, experience_years, consultation_fee, department_id) VALUES 
(2, 'Cardiologist', 12, 150.00, 1),
(3, 'Pediatrician', 8, 100.00, 3);

INSERT INTO patients (user_id, dob, blood_group, phone, address) VALUES 
(4, '1990-05-15', 'O+', '555-0198', '123 Main St, Springfield');

INSERT INTO appointments (patient_id, doctor_id, slot_datetime, status, notes) VALUES 
(1, 1, DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), 'CONFIRMED', 'Annual checkup'),
(1, 2, DATE_ADD(CURRENT_DATE, INTERVAL 2 DAY), 'PENDING', 'Fever symptoms');
