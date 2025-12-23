-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 23, 2025 at 02:17 PM
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

-- --------------------------------------------------------

--
-- Table structure for table `addresses`
--

CREATE TABLE `addresses` (
  `address_id` int(11) NOT NULL,
  `citizen_id` int(11) DEFAULT NULL,
  `street_address` varchar(200) DEFAULT NULL,
  `barangay` varchar(100) DEFAULT NULL,
  `address_line` varchar(200) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `state_province` varchar(100) DEFAULT NULL,
  `zip_postal_code` varchar(20) DEFAULT NULL,
  `country` varchar(100) DEFAULT 'Philippines'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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

-- --------------------------------------------------------

--
-- Table structure for table `documents`
--

CREATE TABLE `documents` (
  `document_id` int(11) NOT NULL,
  `citizen_id` int(11) DEFAULT NULL,
  `form_id` int(11) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `submitted` varchar(3) DEFAULT NULL,
  `required_by` varchar(50) DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `upload_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `doc_forms`
--

CREATE TABLE `doc_forms` (
  `form_id` int(11) NOT NULL,
  `form_name` varchar(100) NOT NULL,
  `form_code` varchar(20) NOT NULL,
  `description` text DEFAULT NULL,
  `is_required` tinyint(1) DEFAULT 1,
  `status` varchar(20) DEFAULT 'Active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `doc_forms`
--

INSERT INTO `doc_forms` (`form_id`, `form_name`, `form_code`, `description`, `is_required`, `status`) VALUES
(1, 'Birth Certificate', 'BC-001', 'PSA/NSO issued birth certificate', 1, 'Active'),
(2, 'Marriage Certificate', 'MC-001', 'PSA/NSO issued marriage certificate (if married)', 0, 'Active'),
(3, 'Valid ID', 'VID-001', 'Any government-issued valid ID (Passport, Driver\'s License, etc.)', 1, 'Active'),
(4, 'Barangay Clearance', 'BCL-001', 'Issued by barangay of residence', 1, 'Active'),
(5, 'Proof of Address', 'POA-001', 'Utility bill, lease agreement, or barangay certification', 1, 'Active'),
(6, 'Passport-sized Photo', 'PSP-001', '2x2 recent passport-sized photo with white background', 1, 'Active'),
(7, 'Certificate of Indigency', 'COI-001', 'For indigent applicants only', 0, 'Active'),
(8, 'Senior Citizen ID', 'SCID-001', 'For senior citizen applicants', 0, 'Active'),
(9, 'PWD ID', 'PWDID-001', 'For persons with disability applicants', 0, 'Active'),
(10, 'Proof of Income', 'POI-001', 'Latest payslip, BIR form, or certificate of employment', 0, 'Active');

-- --------------------------------------------------------

--
-- Table structure for table `id_status`
--

CREATE TABLE `id_status` (
  `status_id` int(11) NOT NULL,
  `transaction_id` varchar(50) DEFAULT NULL,
  `citizen_id` int(11) DEFAULT NULL,
  `status_name_id` int(11) DEFAULT NULL,
  `update_date` date DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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

-- --------------------------------------------------------

--
-- Table structure for table `roles`
--

CREATE TABLE `roles` (
  `role_id` int(11) NOT NULL,
  `role_name` varchar(50) NOT NULL,
  `role_code` varchar(20) NOT NULL,
  `description` text DEFAULT NULL,
  `created_date` date DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `roles`
--

INSERT INTO `roles` (`role_id`, `role_name`, `role_code`, `description`, `created_date`, `is_active`) VALUES
(1, 'Administrator', 'ADMIN', 'System administrator with full access', '2025-12-23', 1),
(2, 'Staff', 'STAFF', 'System staff with limited administrative access', '2025-12-23', 1),
(3, 'Citizen', 'CITIZEN', 'Regular citizen user', '2025-12-23', 1);

-- --------------------------------------------------------

--
-- Table structure for table `status_names`
--

CREATE TABLE `status_names` (
  `status_name_id` int(11) NOT NULL,
  `status_name` varchar(50) NOT NULL,
  `status_code` varchar(20) NOT NULL,
  `description` text DEFAULT NULL,
  `step_order` int(11) DEFAULT 0,
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `status_names`
--

INSERT INTO `status_names` (`status_name_id`, `status_name`, `status_code`, `description`, `step_order`, `is_active`) VALUES
(1, 'Submitted', 'STAT-001', 'Application has been submitted', 1, 1),
(2, 'Processing', 'STAT-002', 'Application is being processed', 2, 1),
(3, 'Document Verification', 'STAT-003', 'Documents are being verified', 3, 1),
(4, 'Biometrics Appointment', 'STAT-004', 'Waiting for biometrics appointment', 4, 1),
(5, 'Biometrics Completed', 'STAT-005', 'Biometrics data has been captured', 5, 1),
(6, 'Background Check', 'STAT-006', 'Background investigation in progress', 6, 1),
(7, 'Background Check Completed', 'STAT-007', 'Background check completed', 7, 1),
(8, 'ID Card Production', 'STAT-008', 'ID card is being produced', 8, 1),
(9, 'Ready for Pickup', 'STAT-009', 'ID card is ready for pickup', 9, 1),
(10, 'Completed', 'STAT-010', 'ID card has been received', 10, 1),
(11, 'Rejected', 'STAT-011', 'Application has been rejected', 0, 1);

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
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `created_date` date DEFAULT NULL,
  `last_login` datetime DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `fname`, `mname`, `lname`, `username`, `password`, `phone`, `email`, `created_date`, `last_login`, `is_active`) VALUES
(10, 'System', 'M', 'Administrator', 'admin', 'admin123', '+639123456789', 'admin@philsys.gov.ph', '2025-12-23', NULL, 1),
(11, 'System', 'A', 'Staff', 'staff', 'staff123', '+639123456780', 'staff@philsys.gov.ph', '2025-12-23', NULL, 1);

-- --------------------------------------------------------

--
-- Table structure for table `user_roles`
--

CREATE TABLE `user_roles` (
  `user_role_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  `assigned_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_roles`
--

INSERT INTO `user_roles` (`user_role_id`, `user_id`, `role_id`, `assigned_date`) VALUES
(1, 10, 1, '2025-12-23'),
(2, 11, 2, '2025-12-23');

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
  ADD KEY `citizen_id` (`citizen_id`),
  ADD KEY `form_id` (`form_id`);

--
-- Indexes for table `doc_forms`
--
ALTER TABLE `doc_forms`
  ADD PRIMARY KEY (`form_id`),
  ADD UNIQUE KEY `form_code` (`form_code`);

--
-- Indexes for table `id_status`
--
ALTER TABLE `id_status`
  ADD PRIMARY KEY (`status_id`),
  ADD KEY `citizen_id` (`citizen_id`),
  ADD KEY `transaction_id_idx` (`transaction_id`),
  ADD KEY `status_name_id` (`status_name_id`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`notification_id`),
  ADD KEY `citizen_id` (`citizen_id`);

--
-- Indexes for table `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`role_id`),
  ADD UNIQUE KEY `role_code` (`role_code`);

--
-- Indexes for table `status_names`
--
ALTER TABLE `status_names`
  ADD PRIMARY KEY (`status_name_id`),
  ADD UNIQUE KEY `status_code` (`status_code`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `user_roles`
--
ALTER TABLE `user_roles`
  ADD PRIMARY KEY (`user_role_id`),
  ADD UNIQUE KEY `unique_user_role` (`user_id`,`role_id`),
  ADD KEY `role_id` (`role_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `activity_log`
--
ALTER TABLE `activity_log`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=517;

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
  MODIFY `citizen_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `documents`
--
ALTER TABLE `documents`
  MODIFY `document_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT for table `doc_forms`
--
ALTER TABLE `doc_forms`
  MODIFY `form_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `id_status`
--
ALTER TABLE `id_status`
  MODIFY `status_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `notification_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `roles`
--
ALTER TABLE `roles`
  MODIFY `role_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `status_names`
--
ALTER TABLE `status_names`
  MODIFY `status_name_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `user_roles`
--
ALTER TABLE `user_roles`
  MODIFY `user_role_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

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
  ADD CONSTRAINT `documents_ibfk_1` FOREIGN KEY (`citizen_id`) REFERENCES `citizens` (`citizen_id`),
  ADD CONSTRAINT `documents_ibfk_2` FOREIGN KEY (`form_id`) REFERENCES `doc_forms` (`form_id`);

--
-- Constraints for table `id_status`
--
ALTER TABLE `id_status`
  ADD CONSTRAINT `id_status_ibfk_1` FOREIGN KEY (`citizen_id`) REFERENCES `citizens` (`citizen_id`),
  ADD CONSTRAINT `id_status_ibfk_2` FOREIGN KEY (`status_name_id`) REFERENCES `status_names` (`status_name_id`);

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`citizen_id`) REFERENCES `citizens` (`citizen_id`);

--
-- Constraints for table `user_roles`
--
ALTER TABLE `user_roles`
  ADD CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
