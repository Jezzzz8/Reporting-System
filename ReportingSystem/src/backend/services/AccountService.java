package backend.services;

import javax.swing.JOptionPane;
import backend.objects.Data;
import java.sql.Date;

public class AccountService {
    
    /**
     * Creates a new user account with citizen record and address
     * 
     * @param firstName First name
     * @param middleName Middle name (optional)
     * @param lastName Last name
     * @param email Email address
     * @param password Password
     * @param username Username
     * @param gender Gender
     * @param phone Phone number
     * @param streetAddress Street address
     * @param barangay Barangay
     * @param city City
     * @param stateProvince State/Province
     * @param zipCode Zip/Postal code
     * @param country Country
     * @param dob Date of birth
     * @param transactionRefNumber Transaction Reference Number (optional)
     * @return true if account creation was successful, false otherwise
     */
    public static boolean createAccount(String firstName, String middleName, String lastName,
                                      String email, String password, String username,
                                      String gender, String phone, String streetAddress,
                                      String barangay, String city, String stateProvince,
                                      String zipCode, String country, java.util.Date dob,
                                      String transactionRefNumber) {
        
        try {
            System.out.println("=== AccountService: Creating Account ===");
            System.out.println("First Name: " + firstName);
            System.out.println("Last Name: " + lastName);
            System.out.println("Gender: " + gender);
            System.out.println("Email: " + email);
            System.out.println("Username: " + username);
            System.out.println("Password length: " + password.length());
            System.out.println("Phone: " + phone);
            System.out.println("Address: " + barangay + ", " + city);
            
            // Format the TRN if provided
            String formattedTrn = null;
            if (transactionRefNumber != null && !transactionRefNumber.trim().isEmpty()) {
                formattedTrn = transactionRefNumber.trim();
                // Format the TRN using Data.IDStatus method
                formattedTrn = Data.IDStatus.formatTransactionId(formattedTrn);
                System.out.println("Formatted TRN: " + formattedTrn);
                
                // Validate the TRN format
                String validationResult = validateTransactionReferenceNumber(formattedTrn);
                if (validationResult != null) {
                    System.err.println("Invalid Transaction Reference Number: " + validationResult);
                    JOptionPane.showMessageDialog(null, 
                        "Invalid Transaction Reference Number format:\n" + validationResult,
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
                
                // Check if TRN already exists in database
                if (isTransactionReferenceNumberAlreadyUsed(formattedTrn)) {
                    System.err.println("TRN already exists in database: " + formattedTrn);
                    JOptionPane.showMessageDialog(null, 
                        "This Transaction Reference Number is already linked to another account.",
                        "TRN Already Used", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }

            // Step 1: Validate email uniqueness
            if (isEmailAlreadyUsed(email)) {
                System.err.println("Email already exists: " + email);
                JOptionPane.showMessageDialog(null, 
                    "This email address is already registered. Please use a different email.",
                    "Email Already Exists", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // Step 2: Validate username uniqueness
            if (usernameExists(username)) {
                System.err.println("Username already exists: " + username);
                JOptionPane.showMessageDialog(null,
                    "Username already exists. Please choose a different one.",
                    "Username Taken", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // Step 3: Create user object
            Data.User newUser = new Data.User();
            newUser.setFname(firstName);
            newUser.setMname(middleName.isEmpty() ? null : middleName);
            newUser.setLname(lastName);
            newUser.setUsername(username);
            newUser.setPassword(password); // In production, this should be hashed
            newUser.setPhone(phone);
            newUser.setEmail(email);
            newUser.setCreatedDate(new Date(System.currentTimeMillis()));
            newUser.setActive(true);

            // Step 4: Save user to database
            System.out.println("Saving user to database...");
            if (!Data.User.addUser(newUser)) {
                System.err.println("Failed to save user to database!");
                JOptionPane.showMessageDialog(null,
                    "Failed to save user to database. Please try again.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            System.out.println("User saved successfully!");

            // Step 5: Get the newly created user ID
            Data.User savedUser = Data.User.authenticate(username, password);
            if (savedUser == null) {
                System.err.println("Failed to authenticate new user!");
                JOptionPane.showMessageDialog(null,
                    "Failed to authenticate new user. Please try again.",
                    "Authentication Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            System.out.println("User authenticated, ID: " + savedUser.getUserId());

            // Step 6: Assign CITIZEN role
            Data.Role citizenRole = Data.Role.getRoleByCode("CITIZEN");
            if (citizenRole != null) {
                Data.UserRole.assignRoleToUser(savedUser.getUserId(), citizenRole.getRoleId());
                System.out.println("CITIZEN role assigned to user");
            }

            // Step 7: Create a citizen record
            Data.Citizen newCitizen = new Data.Citizen();

            // Parse date of birth if provided
            if (dob != null) {
                newCitizen.setBirthDate(new java.sql.Date(dob.getTime()));
            }

            newCitizen.setFname(firstName);
            newCitizen.setMname(middleName.isEmpty() ? null : middleName);
            newCitizen.setLname(lastName);
            newCitizen.setGender(gender);
            newCitizen.setEmail(email);
            newCitizen.setPhone(phone);
            newCitizen.setApplicationDate(new java.sql.Date(System.currentTimeMillis()));

            // Link the citizen to the user
            newCitizen.setUserId(savedUser.getUserId());

            System.out.println("Creating citizen record...");
            int citizenId = Data.Citizen.addCitizenAndGetId(newCitizen);
            if (citizenId <= 0) {
                System.err.println("Failed to create citizen record!");
                JOptionPane.showMessageDialog(null,
                    "Failed to create citizen record. Please try again.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            System.out.println("Citizen record created! Citizen ID: " + citizenId);

            // Step 8: Create address record
            Data.Address newAddress = new Data.Address();
            newAddress.setCitizenId(citizenId);
            newAddress.setStreetAddress(streetAddress);
            newAddress.setBarangay(barangay);
            newAddress.setAddressLine(streetAddress + ", " + barangay);
            newAddress.setCity(city);
            newAddress.setStateProvince(stateProvince);
            newAddress.setZipPostalCode(zipCode);
            newAddress.setCountry(country);

            System.out.println("Creating address record...");
            if (!Data.Address.addAddress(newAddress)) {
                System.err.println("Failed to create address record!");
                JOptionPane.showMessageDialog(null,
                    "Failed to create address record. Please try again.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            System.out.println("Address record created!");

            // Step 9: Create ID Status record (with or without TRN)
            System.out.println("Creating ID Status record...");
            
            // Get the initial status from status_names table (ID 1 = "Submitted")
            Data.StatusName initialStatus = Data.StatusName.getStatusNameById(1);
            if (initialStatus == null) {
                // Fallback to get by code
                initialStatus = Data.StatusName.getStatusNameByCode("STAT-001");
            }
            
            // Create ID Status object
            Data.IDStatus newStatus = new Data.IDStatus();
            
            // Set transaction ID (can be null if user didn't provide TRN)
            if (formattedTrn != null && !formattedTrn.trim().isEmpty()) {
                newStatus.setTransactionId(formattedTrn);
            } else {
                // Generate a transaction ID for users without TRN
                newStatus.setTransactionId(Data.IDStatus.generateTransactionId(citizenId));
            }
            
            newStatus.setCitizenId(citizenId);
            
            // Set status_name_id based on your database schema
            if (initialStatus != null) {
                newStatus.setStatusNameId(initialStatus.getStatusNameId());
            } else {
                newStatus.setStatusNameId(1); // Default to "Submitted" status
            }
            
            newStatus.setUpdateDate(new java.sql.Date(System.currentTimeMillis()));
            newStatus.setNotes("Account created through online registration");
            
            // Save to database using the correct method from Data.IDStatus
            boolean statusCreated = Data.IDStatus.addStatus(newStatus);
            
            if (!statusCreated) {
                System.err.println("Warning: Failed to create ID Status record!");
                // Don't fail the entire registration, but log this issue
                JOptionPane.showMessageDialog(null,
                    "Account created successfully, but there was an issue creating the application status record.\n" +
                    "Please contact support to update your application status.",
                    "Partial Success", JOptionPane.WARNING_MESSAGE);
            } else {
                System.out.println("ID Status record created successfully!");
            }

            // Step 10: Send notification
            String notificationMessage = "Welcome! Your account has been successfully created.\n";
            if (formattedTrn != null && !formattedTrn.trim().isEmpty()) {
                notificationMessage += "Your Transaction Reference Number has been linked: " + formattedTrn + "\n";
            }
            notificationMessage += "Your application status is: " + (initialStatus != null ? initialStatus.getStatusName() : "Submitted");
            
            Data.Notification.addNotification(citizenId,
                notificationMessage,
                "Account Created");

            // Step 11: Log activity
            String logMessage = "Registered new account and created citizen record (ID: " + citizenId + ")";
            if (formattedTrn != null && !formattedTrn.trim().isEmpty()) {
                logMessage += " with Transaction Reference Number: " + formattedTrn;
            }
            
            Data.ActivityLog.logActivity(savedUser.getUserId(), logMessage);

            System.out.println("Account creation completed successfully!");
            return true;

        } catch (Exception e) {
            System.err.println("Error in AccountService.createAccount: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error creating account: " + e.getMessage(),
                "System Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Checks if Transaction Reference Number already exists in the database
     * 
     * @param trn Transaction Reference Number to check
     * @return true if TRN already exists, false otherwise
     */
    public static boolean isTransactionReferenceNumberAlreadyUsed(String trn) {
        try {
            // Check if TRN exists in id_status table
            Data.IDStatus existingStatus = Data.IDStatus.getStatusByTransactionId(trn);
            return existingStatus != null;
        } catch (Exception e) {
            System.err.println("Error checking TRN: " + e.getMessage());
            return false;
        }
    }
    
    /**
    * Validates Transaction Reference Number format (strict 26-character format)
    * 
    * @param trn Transaction Reference Number to validate
    * @return null if valid, error message otherwise
    */
    public static String validateTransactionReferenceNumber(String trn) {
        if (trn == null || trn.trim().isEmpty()) {
            return null; // Optional field, so empty is valid
        }

        String trimmedTrn = trn.trim();

        // Remove hyphens to check the actual alphanumeric characters
        String cleanTrn = trimmedTrn.replace("-", "");

        // Should be exactly 26 alphanumeric characters
        if (cleanTrn.length() != 26) {
            return "Transaction Reference Number must be exactly 26 characters (letters and numbers)\n" +
                   "Current length: " + cleanTrn.length() + " characters\n" +
                   "Expected format: XXXX-XXXX-XXXX-XXXX-XXXX-XXXX-XX";
        }

        // Should contain only letters and numbers
        if (!cleanTrn.matches("^[A-Za-z0-9]{26}$")) {
            return "Transaction Reference Number can only contain letters (A-Z) and numbers (0-9)\n" +
                   "Invalid characters detected in: " + trimmedTrn;
        }

        return null; // TRN is valid
    }
    
    /**
     * Validates email format
     * 
     * @param email Email address to validate
     * @return true if email format is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
    
    /**
     * Validates password strength
     * 
     * @param password Password to validate
     * @return null if password is valid, error message otherwise
     */
    public static String validatePassword(String password) {
        if (password.length() < 6) {
            return "Password must be at least 6 characters long";
        }

        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one number";
        }

        if (!password.matches(".*[a-zA-Z].*")) {
            return "Password must contain at least one letter";
        }

        return null; // Password is valid
    }
    
    /**
     * Checks if email is already used by another user
     * 
     * @param email Email address to check
     * @return true if email already exists, false otherwise
     */
    public static boolean isEmailAlreadyUsed(String email) {
        return Data.User.checkEmailExists(email, null);
    }
    
    /**
     * Checks if username already exists
     * 
     * @param username Username to check
     * @return true if username already exists, false otherwise
     */
    public static boolean usernameExists(String username) {
        return Data.User.checkUsernameExists(username, null);
    }
    
    /**
     * Generates a unique username based on first and last name
     * 
     * @param firstName First name
     * @param lastName Last name
     * @return Generated username
     */
    public static String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName.toLowerCase() + 
                            (lastName.length() > 0 ? lastName.substring(0, 1).toLowerCase() : "");
        return baseUsername;
    }
    
    /**
     * Generates a unique username with incrementing number if needed
     * 
     * @param firstName First name
     * @param lastName Last name
     * @return Unique username
     */
    public static String generateUniqueUsername(String firstName, String lastName) {
        String baseUsername = generateUsername(firstName, lastName);
        String username = baseUsername;
        int counter = 1;
        
        while (usernameExists(username)) {
            username = baseUsername + counter;
            counter++;
        }
        
        return username;
    }
}