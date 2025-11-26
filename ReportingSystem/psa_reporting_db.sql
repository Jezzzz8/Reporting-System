-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 26, 2025 at 02:09 PM
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
-- Database: `psa_reporting_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `activity_log_tb`
--

CREATE TABLE `activity_log_tb` (
  `log_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `action` varchar(100) NOT NULL,
  `action_datetime` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `activity_log_tb`
--

INSERT INTO `activity_log_tb` (`log_id`, `user_id`, `action`, `action_datetime`) VALUES
(1, 1, 'User logged in', '2025-11-26 19:39:47'),
(2, 2, 'Added new citizen record', '2025-11-26 19:39:47'),
(3, 3, 'Submitted verification request', '2025-11-26 19:39:47'),
(4, 1, 'Updated user permissions', '2025-11-26 19:39:47');

-- --------------------------------------------------------

--
-- Table structure for table `citizen_tb`
--

CREATE TABLE `citizen_tb` (
  `citizen_id` int(11) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `date_of_birth` date NOT NULL,
  `place_of_birth` varchar(100) NOT NULL,
  `sex_id` int(11) NOT NULL,
  `registry_number` varchar(20) NOT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `citizen_tb`
--

INSERT INTO `citizen_tb` (`citizen_id`, `first_name`, `last_name`, `date_of_birth`, `place_of_birth`, `sex_id`, `registry_number`, `created_at`) VALUES
(1, 'Juan', 'Dela Cruz', '1990-05-15', 'Manila', 1, 'REG001', '2025-11-26 19:39:46'),
(2, 'Maria', 'Santos', '1985-08-22', 'Quezon City', 2, 'REG002', '2025-11-26 19:39:46'),
(3, 'Pedro', 'Reyes', '1995-12-10', 'Makati', 1, 'REG003', '2025-11-26 19:39:46'),
(4, 'Ana', 'Lopez', '1988-03-30', 'Pasig', 2, 'REG004', '2025-11-26 19:39:46');

-- --------------------------------------------------------

--
-- Table structure for table `request_status_tb`
--

CREATE TABLE `request_status_tb` (
  `status_id` int(11) NOT NULL,
  `status_name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `request_status_tb`
--

INSERT INTO `request_status_tb` (`status_id`, `status_name`) VALUES
(1, 'Pending'),
(2, 'Approved'),
(3, 'Rejected'),
(4, 'Completed');

-- --------------------------------------------------------

--
-- Table structure for table `sex_tb`
--

CREATE TABLE `sex_tb` (
  `sex_id` int(11) NOT NULL,
  `sex_code` char(1) NOT NULL,
  `sex_name` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sex_tb`
--

INSERT INTO `sex_tb` (`sex_id`, `sex_code`, `sex_name`) VALUES
(1, 'M', 'Male'),
(2, 'F', 'Female');

-- --------------------------------------------------------

--
-- Table structure for table `user_role_tb`
--

CREATE TABLE `user_role_tb` (
  `role_id` int(11) NOT NULL,
  `role_name` varchar(50) NOT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_role_tb`
--

INSERT INTO `user_role_tb` (`role_id`, `role_name`, `description`) VALUES
(1, 'Admin', 'Full system access'),
(2, 'Staff', 'PSA staff with verification rights'),
(3, 'Agency_User', 'External agency users');

-- --------------------------------------------------------

--
-- Table structure for table `user_tb`
--

CREATE TABLE `user_tb` (
  `user_id` int(11) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role_id` int(11) NOT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_tb`
--

INSERT INTO `user_tb` (`user_id`, `first_name`, `last_name`, `username`, `password_hash`, `role_id`, `is_active`, `created_at`) VALUES
(1, 'Juan', 'Dela Cruz', 'admin', 'hashed_password_123', 1, 1, '2025-11-26 19:39:45'),
(2, 'Maria', 'Santos', 'maria.staff', 'hashed_password_456', 2, 1, '2025-11-26 19:39:45'),
(3, 'Hospital', 'ABC', 'hosp_abc', 'hashed_password_789', 3, 1, '2025-11-26 19:39:45');

-- --------------------------------------------------------

--
-- Table structure for table `verification_reason_tb`
--

CREATE TABLE `verification_reason_tb` (
  `reason_id` int(11) NOT NULL,
  `reason_name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `verification_reason_tb`
--

INSERT INTO `verification_reason_tb` (`reason_id`, `reason_name`) VALUES
(1, 'Lost Document'),
(2, 'Medical Emergency'),
(3, 'Legal Requirement'),
(4, 'Government Service');

-- --------------------------------------------------------

--
-- Table structure for table `verification_request_tb`
--

CREATE TABLE `verification_request_tb` (
  `request_id` int(11) NOT NULL,
  `citizen_id` int(11) NOT NULL,
  `requester_id` int(11) NOT NULL,
  `reason_id` int(11) NOT NULL,
  `status_id` int(11) DEFAULT 1,
  `request_datetime` datetime DEFAULT current_timestamp(),
  `response_details` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `verification_request_tb`
--

INSERT INTO `verification_request_tb` (`request_id`, `citizen_id`, `requester_id`, `reason_id`, `status_id`, `request_datetime`, `response_details`) VALUES
(1, 1, 3, 1, 4, '2025-11-26 19:39:47', 'Birth certificate verified and provided'),
(2, 2, 3, 2, 1, '2025-11-26 19:39:47', 'Pending hospital confirmation'),
(3, 3, 3, 3, 2, '2025-11-26 19:39:47', 'Approved for court proceedings');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `activity_log_tb`
--
ALTER TABLE `activity_log_tb`
  ADD PRIMARY KEY (`log_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `citizen_tb`
--
ALTER TABLE `citizen_tb`
  ADD PRIMARY KEY (`citizen_id`),
  ADD UNIQUE KEY `registry_number` (`registry_number`),
  ADD KEY `sex_id` (`sex_id`);

--
-- Indexes for table `request_status_tb`
--
ALTER TABLE `request_status_tb`
  ADD PRIMARY KEY (`status_id`);

--
-- Indexes for table `sex_tb`
--
ALTER TABLE `sex_tb`
  ADD PRIMARY KEY (`sex_id`),
  ADD UNIQUE KEY `sex_code` (`sex_code`);

--
-- Indexes for table `user_role_tb`
--
ALTER TABLE `user_role_tb`
  ADD PRIMARY KEY (`role_id`),
  ADD UNIQUE KEY `role_name` (`role_name`);

--
-- Indexes for table `user_tb`
--
ALTER TABLE `user_tb`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD KEY `role_id` (`role_id`);

--
-- Indexes for table `verification_reason_tb`
--
ALTER TABLE `verification_reason_tb`
  ADD PRIMARY KEY (`reason_id`);

--
-- Indexes for table `verification_request_tb`
--
ALTER TABLE `verification_request_tb`
  ADD PRIMARY KEY (`request_id`),
  ADD KEY `citizen_id` (`citizen_id`),
  ADD KEY `requester_id` (`requester_id`),
  ADD KEY `reason_id` (`reason_id`),
  ADD KEY `status_id` (`status_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `activity_log_tb`
--
ALTER TABLE `activity_log_tb`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `citizen_tb`
--
ALTER TABLE `citizen_tb`
  MODIFY `citizen_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `request_status_tb`
--
ALTER TABLE `request_status_tb`
  MODIFY `status_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `sex_tb`
--
ALTER TABLE `sex_tb`
  MODIFY `sex_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `user_role_tb`
--
ALTER TABLE `user_role_tb`
  MODIFY `role_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `user_tb`
--
ALTER TABLE `user_tb`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `verification_reason_tb`
--
ALTER TABLE `verification_reason_tb`
  MODIFY `reason_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `verification_request_tb`
--
ALTER TABLE `verification_request_tb`
  MODIFY `request_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `activity_log_tb`
--
ALTER TABLE `activity_log_tb`
  ADD CONSTRAINT `activity_log_tb_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user_tb` (`user_id`);

--
-- Constraints for table `citizen_tb`
--
ALTER TABLE `citizen_tb`
  ADD CONSTRAINT `citizen_tb_ibfk_1` FOREIGN KEY (`sex_id`) REFERENCES `sex_tb` (`sex_id`);

--
-- Constraints for table `user_tb`
--
ALTER TABLE `user_tb`
  ADD CONSTRAINT `user_tb_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `user_role_tb` (`role_id`);

--
-- Constraints for table `verification_request_tb`
--
ALTER TABLE `verification_request_tb`
  ADD CONSTRAINT `verification_request_tb_ibfk_1` FOREIGN KEY (`citizen_id`) REFERENCES `citizen_tb` (`citizen_id`),
  ADD CONSTRAINT `verification_request_tb_ibfk_2` FOREIGN KEY (`requester_id`) REFERENCES `user_tb` (`user_id`),
  ADD CONSTRAINT `verification_request_tb_ibfk_3` FOREIGN KEY (`reason_id`) REFERENCES `verification_reason_tb` (`reason_id`),
  ADD CONSTRAINT `verification_request_tb_ibfk_4` FOREIGN KEY (`status_id`) REFERENCES `request_status_tb` (`status_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
