-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 17, 2025 at 06:22 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `philsys_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `activity_log`
--

CREATE TABLE `activity_log` (
  `log_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `action` varchar(100) DEFAULT NULL,
  `action_date` date DEFAULT NULL,
  `action_time` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `activity_log`
--

INSERT INTO `activity_log` (`log_id`, `user_id`, `action`, `action_date`, `action_time`) VALUES
(1, 1, 'Login', '2024-01-25', '10:30 AM'),
(2, 3, 'Update Status', '2024-01-25', '11:00 AM'),
(3, 2, 'Book Appointment', '2024-01-25', '11:15 AM'),
(4, 4, 'Verify Document', '2024-01-26', '09:45 AM'),
(5, 1, 'View Status', '2024-01-26', '02:30 PM'),
(6, 3, 'Send Notification', '2024-01-26', '03:15 PM'),
(7, 2, 'Update Profile', '2024-01-27', '10:00 AM'),
(8, 5, 'Register Account', '2024-01-27', '11:30 AM'),
(9, 6, 'Register', '2024-01-19', '09:00 AM'),
(10, 7, 'Login', '2024-01-20', '10:30 AM'),
(11, 3, 'Update Status', '2024-01-21', '11:15 AM'),
(12, 4, 'Verify Document', '2024-01-22', '02:45 PM'),
(459, 1, 'Logged in successfully from Landing page', '2025-12-17', '11:27 PM'),
(460, 1, 'Logged in successfully from Landing page', '2025-12-17', '11:38 PM'),
(461, 1, 'Logged in successfully from Landing page', '2025-12-17', '11:39 PM'),
(462, 1, 'Logged in successfully from Landing page', '2025-12-17', '11:52 PM'),
(463, 1, 'Logged in successfully from Landing page', '2025-12-18', '12:09 AM'),
(464, 1, 'Updated personal details', '2025-12-18', '12:11 AM'),
(465, 2, 'Logged in successfully from Landing page', '2025-12-18', '12:11 AM'),
(466, 1, 'Logged in successfully from Landing page', '2025-12-18', '12:13 AM'),
(467, 2, 'Logged in successfully from Landing page', '2025-12-18', '12:14 AM'),
(468, 1, 'Logged in successfully from Landing page', '2025-12-18', '12:18 AM'),
(469, 1, 'Logged in successfully from Landing page', '2025-12-18', '12:19 AM'),
(470, 1, 'Logged in successfully from Landing page', '2025-12-18', '12:25 AM'),
(471, 1, 'Logged in successfully from Landing page', '2025-12-18', '12:55 AM'),
(472, 1, 'Logged in from Login panel', '2025-12-18', '12:55 AM'),
(473, 1, 'Logged in successfully from Landing page', '2025-12-18', '12:56 AM'),
(474, 1, 'Logged in from Login panel', '2025-12-18', '12:56 AM'),
(475, 1, 'Logged in successfully from Landing page', '2025-12-18', '01:09 AM'),
(476, 1, 'Logged in from Login panel', '2025-12-18', '01:09 AM'),
(477, 1, 'Logged in successfully from Landing page', '2025-12-18', '01:19 AM'),
(478, 1, 'Logged in from Login panel', '2025-12-18', '01:19 AM');

-- --------------------------------------------------------

--
-- Table structure for table `addresses`
--

CREATE TABLE `addresses` (
  `address_id` int(11) NOT NULL,
  `citizen_id` int(11) DEFAULT NULL,
  `street_address` varchar(200) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `state_province` varchar(100) DEFAULT NULL,
  `zip_postal_code` varchar(20) DEFAULT NULL,
  `country` varchar(100) DEFAULT 'Philippines'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `addresses`
--

INSERT INTO `addresses` (`address_id`, `citizen_id`, `street_address`, `city`, `state_province`, `zip_postal_code`, `country`) VALUES
(1, 1, '123 Rizal Avenue', 'Manila', 'Metro Manila', '1000', 'Philippines'),
(2, 2, '456 Commonwealth Avenue', 'Quezon City', 'Metro Manila', '1100', 'Philippines'),
(3, 3, '789 Ayala Avenue', 'Makati', 'Metro Manila', '1200', 'Philippines'),
(4, 4, '321 Osmeña Boulevard', 'Cebu City', 'Cebu', '6000', 'Philippines'),
(5, 5, '654 Roxas Avenue', 'Davao City', 'Davao del Sur', '8000', 'Philippines'),
(6, 6, '987 Ledesma Street', 'Iloilo City', 'Iloilo', '5000', 'Philippines'),
(7, 7, '159 Session Road', 'Baguio City', 'Benguet', '2600', 'Philippines');

-- --------------------------------------------------------

--
-- Table structure for table `appointments`
--

CREATE TABLE `appointments` (
  `appointment_id` int(11) NOT NULL,
  `citizen_id` int(11) DEFAULT NULL,
  `app_date` date DEFAULT NULL,
  `app_time` varchar(10) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `created_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `appointments`
--

INSERT INTO `appointments` (`appointment_id`, `citizen_id`, `app_date`, `app_time`, `status`, `created_date`) VALUES
(1, 2, '2024-01-30', '09:00 AM', 'Scheduled', '2024-01-25'),
(2, 3, '2024-02-01', '10:30 AM', 'Scheduled', '2024-01-26'),
(3, 1, '2024-01-28', '02:00 PM', 'Completed', '2024-01-20'),
(4, 4, '2024-02-05', '11:00 AM', 'Pending', '2024-01-27'),
(5, 5, '2024-02-10', '09:30 AM', 'Scheduled', '2024-01-28'),
(6, 6, '2024-02-12', '11:00 AM', 'Pending', '2024-01-29'),
(7, 7, '2024-02-08', '10:00 AM', 'Completed', '2024-01-27');

-- --------------------------------------------------------

--
-- Table structure for table `citizens`
--

CREATE TABLE `citizens` (
  `citizen_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `fname` varchar(50) DEFAULT NULL,
  `mname` varchar(50) DEFAULT NULL,
  `lname` varchar(50) DEFAULT NULL,
  `national_id` varchar(20) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `application_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `citizens`
--

INSERT INTO `citizens` (`citizen_id`, `user_id`, `fname`, `mname`, `lname`, `national_id`, `birth_date`, `gender`, `phone`, `email`, `application_date`) VALUES
(1, 1, 'Juan', 'Santos', 'Dela Cruz', 'NID001', '2004-08-24', 'Male', '09123456789', 'juan.delacruz@gmail.com', '2024-01-15'),
(2, 2, 'Maria', 'Gonzales', 'Santos', 'NID002', '1985-08-22', 'Female', '09234567890', 'maria.santos@gmail.com', '2024-01-16'),
(3, 5, 'Pedro', 'Manuel', 'Reyes', NULL, '1978-11-30', 'Male', '09345678901', 'pedro.reyes@gmail.com', '2024-01-17'),
(4, NULL, 'Ana', 'Marie', 'Lopez', NULL, '1995-02-14', 'Female', '09456789012', 'ana.lopez@gmail.com', '2024-01-18'),
(5, 6, 'John', 'Paul', 'Smith', NULL, '1988-07-12', 'Male', '09567890123', 'john.smith@gmail.com', '2024-01-19'),
(6, 7, 'Sarah', 'Jane', 'Tan', NULL, '1992-11-05', 'Female', '09678901234', 'sarah.tan@gmail.com', '2024-01-20'),
(7, NULL, 'Michael', 'James', 'Lim', 'NID005', '1980-03-25', 'Male', '09789012345', 'michael.lim@gmail.com', '2024-01-21');

-- --------------------------------------------------------

--
-- Table structure for table `documents`
--

CREATE TABLE `documents` (
  `document_id` int(11) NOT NULL,
  `citizen_id` int(11) DEFAULT NULL,
  `document_name` varchar(100) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `submitted` varchar(3) DEFAULT NULL,
  `required_by` varchar(50) DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `upload_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `documents`
--

INSERT INTO `documents` (`document_id`, `citizen_id`, `document_name`, `status`, `submitted`, `required_by`, `file_path`, `upload_date`) VALUES
(1, 1, 'Birth Certificate', 'Verified', 'Yes', 'Philippine Statistics Authority', '/uploads/birth_cert_1.pdf', '2024-01-15'),
(2, 1, 'Proof of Address', 'Pending', 'Yes', 'Local Government Unit', '/uploads/address_proof_1.pdf', '2024-01-16'),
(3, 2, 'Birth Certificate', 'Verified', 'Yes', 'Philippine Statistics Authority', '/uploads/birth_cert_2.pdf', '2024-01-16'),
(4, 2, 'Proof of Address', 'Verified', 'Yes', 'Local Government Unit', '/uploads/address_proof_2.pdf', '2024-01-17'),
(5, 3, 'Birth Certificate', 'Missing', 'No', 'Philippine Statistics Authority', NULL, NULL),
(6, 3, 'Proof of Address', 'Submitted', 'Yes', 'Local Government Unit', '/uploads/address_proof_3.pdf', '2024-01-17'),
(7, 4, 'Birth Certificate', 'Submitted', 'Yes', 'Philippine Statistics Authority', '/uploads/birth_cert_4.pdf', '2024-01-18'),
(8, 4, 'Proof of Address', 'Submitted', 'Yes', 'Local Government Unit', '/uploads/address_proof_4.pdf', '2024-01-18'),
(9, 1, 'Marriage Certificate', 'Not Required', 'No', 'Philippine Statistics Authority', NULL, NULL),
(10, 2, 'Valid ID', 'Verified', 'Yes', 'Government Office', '/uploads/valid_id_2.pdf', '2024-01-17'),
(11, 3, 'Valid ID', 'Submitted', 'Yes', 'Government Office', '/uploads/valid_id_3.pdf', '2024-01-18'),
(12, 4, 'Valid ID', 'Missing', 'No', 'Government Office', NULL, NULL),
(13, 5, 'Birth Certificate', 'Submitted', 'Yes', 'Philippine Statistics Authority', '/uploads/birth_cert_5.pdf', '2024-01-19'),
(14, 5, 'Proof of Address', 'Verified', 'Yes', 'Local Government Unit', '/uploads/address_proof_5.pdf', '2024-01-20'),
(15, 6, 'Birth Certificate', 'Submitted', 'Yes', 'Philippine Statistics Authority', '/uploads/birth_cert_6.pdf', '2024-01-20'),
(16, 6, 'Proof of Address', 'Pending', 'Yes', 'Local Government Unit', '/uploads/address_proof_6.pdf', '2024-01-21'),
(17, 7, 'Birth Certificate', 'Verified', 'Yes', 'Philippine Statistics Authority', '/uploads/birth_cert_7.pdf', '2024-01-21'),
(18, 7, 'Proof of Address', 'Verified', 'Yes', 'Local Government Unit', '/uploads/address_proof_7.pdf', '2024-01-22');

-- --------------------------------------------------------

--
-- Table structure for table `id_status`
--

CREATE TABLE `id_status` (
  `status_id` int(11) NOT NULL,
  `transaction_id` varchar(50) DEFAULT NULL,
  `citizen_id` int(11) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `update_date` date DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `id_status`
--

INSERT INTO `id_status` (`status_id`, `transaction_id`, `citizen_id`, `status`, `update_date`, `notes`) VALUES
(1, '1234-5678-9012-3456-7890-1234-01', 1, 'Processing', '2024-01-20', 'Being processed at Manila office'),
(2, '1234-5678-9012-3456-7890-1234-02', 2, 'Ready for Pickup', '2024-01-25', 'Ready at Quezon City office'),
(3, '1234-5678-9012-3456-7890-1234-03', 3, 'Pending Documents', '2024-01-18', 'Waiting for birth certificate'),
(4, '1234-5678-9012-3456-7890-1234-04', 4, 'Submitted', '2024-01-19', 'Application submitted online'),
(5, '1234-5678-9012-3456-7890-1234-05', 5, 'Processing', '2024-01-20', 'New application received'),
(6, '1234-5678-9012-3456-7890-1234-06', 6, 'Verification', '2024-01-21', 'Documents under verification'),
(7, '1234-5678-9012-3456-7890-1234-07', 7, 'Ready', '2024-01-22', 'ID printed and ready');

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `notification_id` int(11) NOT NULL,
  `citizen_id` int(11) DEFAULT NULL,
  `notification_date` date DEFAULT NULL,
  `notification_time` varchar(10) DEFAULT NULL,
  `message` text DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  `read_status` varchar(20) DEFAULT 'Unread'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `notifications`
--

INSERT INTO `notifications` (`notification_id`, `citizen_id`, `notification_date`, `notification_time`, `message`, `type`, `read_status`) VALUES
(1, 1, '2024-01-20', '10:30 AM', 'Your application is now being processed.', 'Status Update', 'Read'),
(2, 2, '2024-01-25', '09:15 AM', 'Your PhilSys ID is ready for pickup at Quezon City Office.', 'ID Ready', 'Unread'),
(3, 2, '2024-01-25', '02:00 PM', 'Your appointment is confirmed for January 30, 2024 at 9:00 AM.', 'Appointment', 'Read'),
(4, 3, '2024-01-18', '11:00 AM', 'Please submit your birth certificate to complete your application.', 'Document Request', 'Unread'),
(5, 4, '2024-01-19', '03:45 PM', 'Your application has been submitted successfully.', 'Confirmation', 'Read'),
(6, 1, '2024-01-22', '01:20 PM', 'Your proof of address document is pending verification.', 'Document Status', 'Unread'),
(7, 3, '2024-01-26', '10:00 AM', 'Your appointment is scheduled for February 1, 2024 at 10:30 AM.', 'Appointment', 'Read'),
(8, 4, '2024-01-27', '04:30 PM', 'Please bring valid ID for your appointment on February 5.', 'Reminder', 'Unread'),
(9, 5, '2024-01-28', '02:30 PM', 'Your appointment is scheduled for February 10, 2024.', 'Appointment', 'Unread'),
(10, 6, '2024-01-29', '10:45 AM', 'Please verify your proof of address document.', 'Document Request', 'Unread'),
(11, 7, '2024-01-27', '03:00 PM', 'Your PhilSys ID has been successfully printed.', 'Status Update', 'Read');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `fname` varchar(50) DEFAULT NULL,
  `mname` varchar(50) DEFAULT NULL,
  `lname` varchar(50) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `role` varchar(20) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `created_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `fname`, `mname`, `lname`, `username`, `password`, `role`, `phone`, `email`, `created_date`) VALUES
(1, 'Juan', 'Santos', 'Dela Cruz', 'juan', 'password123', 'citizen', '09123456789', 'juan.delacruz@philsys.gov.ph', '2024-01-15'),
(2, 'Maria', 'Gonzales', 'Santos', 'maria', 'password456', 'citizen', '09234567890', 'maria.santos@philsys.gov.ph', '2024-01-16'),
(3, 'Admin', 'User', 'System', 'admin', 'admin123', 'admin', '0287654321', 'admin@philsys.gov.ph', '2024-01-10'),
(4, 'Staff', 'Member', 'One', 'staff1', 'staff123', 'staff', '0287654322', 'staff1@philsys.gov.ph', '2024-01-10'),
(5, 'Pedro', 'Manuel', 'Reyes', 'pedro', 'password789', 'citizen', '09345678901', 'pedro.reyes@philsys.gov.ph', '2024-01-17'),
(6, 'John', 'Paul', 'Smith', 'johnsmith', 'password101', 'citizen', '09567890123', 'john.smith@philsys.gov.ph', '2024-01-19'),
(7, 'Sarah', 'Jane', 'Tan', 'sarahtan', 'password202', 'citizen', '09678901234', 'sarah.tan@philsys.gov.ph', '2024-01-20');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `activity_log`
--
ALTER TABLE `activity_log`
  ADD PRIMARY KEY (`log_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `addresses`
--
ALTER TABLE `addresses`
  ADD PRIMARY KEY (`address_id`),
  ADD KEY `citizen_id` (`citizen_id`);

--
-- Indexes for table `appointments`
--
ALTER TABLE `appointments`
  ADD PRIMARY KEY (`appointment_id`),
  ADD KEY `citizen_id` (`citizen_id`);

--
-- Indexes for table `citizens`
--
ALTER TABLE `citizens`
  ADD PRIMARY KEY (`citizen_id`),
  ADD UNIQUE KEY `national_id` (`national_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `documents`
--
ALTER TABLE `documents`
  ADD PRIMARY KEY (`document_id`),
  ADD KEY `citizen_id` (`citizen_id`);

--
-- Indexes for table `id_status`
--
ALTER TABLE `id_status`
  ADD PRIMARY KEY (`status_id`),
  ADD KEY `citizen_id` (`citizen_id`),
  ADD KEY `transaction_id_idx` (`transaction_id`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`notification_id`),
  ADD KEY `citizen_id` (`citizen_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `activity_log`
--
ALTER TABLE `activity_log`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=479;

--
-- AUTO_INCREMENT for table `addresses`
--
ALTER TABLE `addresses`
  MODIFY `address_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `appointments`
--
ALTER TABLE `appointments`
  MODIFY `appointment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `citizens`
--
ALTER TABLE `citizens`
  MODIFY `citizen_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `documents`
--
ALTER TABLE `documents`
  MODIFY `document_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `id_status`
--
ALTER TABLE `id_status`
  MODIFY `status_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `notification_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `activity_log`
--
ALTER TABLE `activity_log`
  ADD CONSTRAINT `activity_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `addresses`
--
ALTER TABLE `addresses`
  ADD CONSTRAINT `addresses_ibfk_1` FOREIGN KEY (`citizen_id`) REFERENCES `citizens` (`citizen_id`);

--
-- Constraints for table `appointments`
--
ALTER TABLE `appointments`
  ADD CONSTRAINT `appointments_ibfk_1` FOREIGN KEY (`citizen_id`) REFERENCES `citizens` (`citizen_id`);

--
-- Constraints for table `citizens`
--
ALTER TABLE `citizens`
  ADD CONSTRAINT `citizens_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `documents`
--
ALTER TABLE `documents`
  ADD CONSTRAINT `documents_ibfk_1` FOREIGN KEY (`citizen_id`) REFERENCES `citizens` (`citizen_id`);

--
-- Constraints for table `id_status`
--
ALTER TABLE `id_status`
  ADD CONSTRAINT `id_status_ibfk_1` FOREIGN KEY (`citizen_id`) REFERENCES `citizens` (`citizen_id`);

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`citizen_id`) REFERENCES `citizens` (`citizen_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
