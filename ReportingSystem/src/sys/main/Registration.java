package sys.main;

import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Font;
import backend.objects.Data;
import java.sql.Date;
import java.util.regex.Pattern;

public class Registration extends javax.swing.JPanel {
    
    public Registration() {
        initComponents();
        applyStyles();
        setupValidationListeners();
    }
    
    private void applyStyles() {
        // Set placeholders
        FirstnameText.setPlaceholder("First Name *");
        MiddleNameTextOptional.setPlaceholder("Middle Name (Optional)");
        LastnameText.setPlaceholder("Last Name *");
        EmailText.setPlaceholder("Email Address *");
        TransactionReferenceNumberText.setPlaceholder("Transaction Reference Number *");
        PasswordText.setPlaceholder("Password *");
        ConfirmPasswordText.setPlaceholder("Confirm Password *");

        // Set consistent colors
        Color focusedBlue = new Color(0, 120, 215);
        Color grayBorder = new Color(200, 200, 200);
        Color placeholderGray = new Color(150, 150, 150);

        // Style first name field
        FirstnameText.setMaxLength(50);
        FirstnameText.setFocusedBorderColor(focusedBlue);
        FirstnameText.setUnfocusedBorderColor(grayBorder);
        FirstnameText.setPlaceholderColor(placeholderGray);
        FirstnameText.setNormalHeight(40);
        FirstnameText.setExpandedHeight(50);

        // Style middle name field
        MiddleNameTextOptional.setMaxLength(50);
        MiddleNameTextOptional.setFocusedBorderColor(focusedBlue);
        MiddleNameTextOptional.setUnfocusedBorderColor(grayBorder);
        MiddleNameTextOptional.setPlaceholderColor(placeholderGray);
        MiddleNameTextOptional.setNormalHeight(40);
        MiddleNameTextOptional.setExpandedHeight(50);

        // Style last name field
        LastnameText.setMaxLength(50);
        LastnameText.setFocusedBorderColor(focusedBlue);
        LastnameText.setUnfocusedBorderColor(grayBorder);
        LastnameText.setPlaceholderColor(placeholderGray);
        LastnameText.setNormalHeight(40);
        LastnameText.setExpandedHeight(50);

        // Style email field
        EmailText.setMaxLength(100);
        EmailText.setFocusedBorderColor(focusedBlue);
        EmailText.setUnfocusedBorderColor(grayBorder);
        EmailText.setPlaceholderColor(placeholderGray);
        EmailText.setNormalHeight(40);
        EmailText.setExpandedHeight(50);

        TransactionReferenceNumberText.setFocusedBorderColor(focusedBlue);
        TransactionReferenceNumberText.setUnfocusedBorderColor(grayBorder);
        TransactionReferenceNumberText.setPlaceholderColor(placeholderGray);
        TransactionReferenceNumberText.setNormalHeight(40);
        TransactionReferenceNumberText.setExpandedHeight(50);

        // ENABLE THE FORMAT GUIDE
        TransactionReferenceNumberText.enableFormatGuide("XXXX-XXXX-XXXX-XXXX-XXXX-XXXX-XX");
        TransactionReferenceNumberText.setPlaceholder("Transaction Reference Number *");

        // You can also customize the guide color
        TransactionReferenceNumberText.setFormatGuideColor(new Color(180, 180, 180, 100));

        // Style password field
        PasswordText.setFocusedBorderColor(focusedBlue);
        PasswordText.setUnfocusedBorderColor(grayBorder);
        PasswordText.setPlaceholderColor(placeholderGray);
        PasswordText.setNormalHeight(40);
        PasswordText.setExpandedHeight(50);
        PasswordText.disablePasswordVisibilityToggle();
        
        // Style confirm password field
        ConfirmPasswordText.setFocusedBorderColor(focusedBlue);
        ConfirmPasswordText.setUnfocusedBorderColor(grayBorder);
        ConfirmPasswordText.setPlaceholderColor(placeholderGray);
        ConfirmPasswordText.setNormalHeight(40);
        ConfirmPasswordText.setExpandedHeight(50);
        ConfirmPasswordText.disablePasswordVisibilityToggle();

        // Set fonts for consistency
        Font textFieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        FirstnameText.setFont(textFieldFont);
        MiddleNameTextOptional.setFont(textFieldFont);
        LastnameText.setFont(textFieldFont);
        EmailText.setFont(textFieldFont);
        TransactionReferenceNumberText.setFont(textFieldFont);
        PasswordText.setFont(textFieldFont);
        ConfirmPasswordText.setFont(textFieldFont);

        // Style create account button
        CreateAccountButton.setBackground(focusedBlue);
        CreateAccountButton.setForeground(Color.WHITE);
        CreateAccountButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        CreateAccountButton.setBorder(null);
        CreateAccountButton.setFocusPainted(false);

        // Style sign in button
        SigninButton.setForeground(focusedBlue);
        SigninButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Style terms and conditions checkbox
        TermsAndConditionCheckBox.setTermsText("I agree to the ");
        TermsAndConditionCheckBox.setLinkText("Terms and Conditions");
        TermsAndConditionCheckBox.setNormalColor(new Color(70, 70, 70));
        TermsAndConditionCheckBox.setLinkColor(focusedBlue);
        TermsAndConditionCheckBox.setHoverColor(new Color(0, 100, 180));
        TermsAndConditionCheckBox.setErrorColor(new Color(220, 53, 69)); // Red for errors
        TermsAndConditionCheckBox.setTextFont(new Font("Segoe UI", Font.PLAIN, 12));
        TermsAndConditionCheckBox.setLinkFont(new Font("Segoe UI", Font.BOLD, 12));

        // Add link click listener for terms and conditions
        TermsAndConditionCheckBox.addLinkClickListener(() -> {
            showTermsAndConditionsDialog();
        });
    }
    
    private void setupValidationListeners() {
        // Add focus listener for transaction reference validation
        TransactionReferenceNumberText.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                validateTransactionReferenceOnFocusLost();
            }
        });
    }
    
    private void showTermsAndConditionsDialog() {
        String terms = "TERMS AND CONDITIONS\n\n" +
                       "1. You agree to use this system for lawful purposes only.\n" +
                       "2. You are responsible for maintaining the confidentiality of your account.\n" +
                       "3. You must provide accurate and complete information.\n" +
                       "4. The system administrators reserve the right to suspend accounts.\n" +
                       "5. Your data will be handled according to privacy laws.\n\n" +
                       "By checking this box, you acknowledge that you have read and agree to these terms.";

        JOptionPane.showMessageDialog(this, terms, 
            "Terms and Conditions", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void validateTransactionReferenceNumber(String transactionId) {
        if (transactionId != null && !transactionId.isEmpty()) {
            // Format the transaction ID first
            String formattedId = Data.IDStatus.formatTransactionId(transactionId);
            
            Data.IDStatus status = Data.IDStatus.getStatusByTransactionId(formattedId);
            if (status != null) {
                Data.Citizen citizen = Data.Citizen.getCitizenById(status.getCitizenId());
                if (citizen != null) {
                    String enteredFirstName = FirstnameText.getText().trim();
                    String enteredLastName = LastnameText.getText().trim();
                    
                    if (!enteredFirstName.isEmpty() && !enteredLastName.isEmpty()) {
                        if (enteredFirstName.equalsIgnoreCase(citizen.getFname()) && 
                            enteredLastName.equalsIgnoreCase(citizen.getLname())) {
                            JOptionPane.showMessageDialog(this,
                                "Transaction reference verified!\n" +
                                "Application found for: " + citizen.getFullName(),
                                "Verification Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                "Name mismatch! Application is registered to: " + 
                                citizen.getFullName(),
                                "Verification Warning", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid transaction reference number. Please check and try again.",
                    "Verification Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void validateTransactionReferenceOnFocusLost() {
        String transactionId = TransactionReferenceNumberText.getText().trim();
        if (!transactionId.isEmpty()) {
            validateTransactionReferenceNumber(transactionId);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        RIGHT = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        CreateAccountButton = new javax.swing.JButton();
        SigninButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        FirstnameText = new sys.main.CustomTextField();
        MiddleNameTextOptional = new sys.main.CustomTextField();
        LastnameText = new sys.main.CustomTextField();
        TransactionReferenceNumberText = new sys.main.CustomTextField();
        ConfirmPasswordText = new sys.main.CustomPasswordField();
        EmailText = new sys.main.CustomTextField();
        PasswordText = new sys.main.CustomPasswordField();
        TermsAndConditionCheckBox = new sys.main.CustomCheckBox();

        RIGHT.setBackground(new java.awt.Color(255, 255, 255));
        RIGHT.setPreferredSize(new java.awt.Dimension(400, 500));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 120, 215));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("SIGN UP");

        CreateAccountButton.setBackground(new java.awt.Color(0, 120, 215));
        CreateAccountButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        CreateAccountButton.setForeground(new java.awt.Color(255, 255, 255));
        CreateAccountButton.setText("CREATE ACCOUNT");
        CreateAccountButton.setBorder(null);
        CreateAccountButton.setFocusable(false);
        CreateAccountButton.setPreferredSize(new java.awt.Dimension(200, 35));
        CreateAccountButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreateAccountButtonActionPerformed(evt);
            }
        });

        SigninButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SigninButton.setForeground(new java.awt.Color(0, 120, 215));
        SigninButton.setText("Sign in");
        SigninButton.setBorder(null);
        SigninButton.setBorderPainted(false);
        SigninButton.setContentAreaFilled(false);
        SigninButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        SigninButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        SigninButton.setPreferredSize(new java.awt.Dimension(100, 20));
        SigninButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SigninButtonActionPerformed(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("Already have an account?");
        jLabel3.setPreferredSize(new java.awt.Dimension(150, 20));

        javax.swing.GroupLayout RIGHTLayout = new javax.swing.GroupLayout(RIGHT);
        RIGHT.setLayout(RIGHTLayout);
        RIGHTLayout.setHorizontalGroup(
            RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(RIGHTLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(ConfirmPasswordText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(TransactionReferenceNumberText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(LastnameText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(MiddleNameTextOptional, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(FirstnameText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(EmailText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(PasswordText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(RIGHTLayout.createSequentialGroup()
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(SigninButton, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addComponent(TermsAndConditionCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RIGHTLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(CreateAccountButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        RIGHTLayout.setVerticalGroup(
            RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RIGHTLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(FirstnameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(MiddleNameTextOptional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LastnameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(EmailText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(TransactionReferenceNumberText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(PasswordText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ConfirmPasswordText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(TermsAndConditionCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CreateAccountButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SigninButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(RIGHT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(RIGHT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void CreateAccountButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreateAccountButtonActionPerformed
        createAccount();
    }//GEN-LAST:event_CreateAccountButtonActionPerformed

    private void SigninButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SigninButtonActionPerformed
        clearForm();
    }//GEN-LAST:event_SigninButtonActionPerformed
    
    private void createAccount() {
        // Step 1: Get all form values
        String firstName = FirstnameText.getText().trim();
        String middleName = MiddleNameTextOptional.getText().trim();
        String lastName = LastnameText.getText().trim();
        String email = EmailText.getText().trim();
        String transactionRef = TransactionReferenceNumberText.getText().trim();
        String password = new String(PasswordText.getPassword()).trim();
        String confirmPassword = new String(ConfirmPasswordText.getPassword()).trim();
        boolean agreedToTerms = TermsAndConditionCheckBox.isSelected();
        
        // Step 2: Validate required fields
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || 
            transactionRef.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all required fields marked with *",
                "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Step 3: Validate transaction reference format
        if (!TransactionReferenceNumberText.isFormatFilled()) {
            JOptionPane.showMessageDialog(this,
                "Please complete the transaction reference number format.\n" +
                "Expected format: XXXX-XXXX-XXXX-XXXX-XXXX-XXXX-XX",
                "Incomplete Format", JOptionPane.WARNING_MESSAGE);
            TransactionReferenceNumberText.requestFocus();
            return;
        }
        
        // Step 4: Validate email format
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address",
                "Invalid Email", JOptionPane.WARNING_MESSAGE);
            EmailText.requestFocus();
            return;
        }
        
        // Step 5: Validate password
        String passwordError = validatePassword(password);
        if (passwordError != null) {
            JOptionPane.showMessageDialog(this, passwordError,
                "Password Requirements", JOptionPane.WARNING_MESSAGE);
            PasswordText.requestFocus();
            return;
        }
        
        // Step 6: Check password match
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match. Please re-enter your password.",
                "Password Mismatch", JOptionPane.WARNING_MESSAGE);
            ConfirmPasswordText.requestFocus();
            return;
        }
        
        // Step 7: Validate transaction reference and match with citizen data
        String formattedTransactionId = Data.IDStatus.formatTransactionId(transactionRef);
        Data.IDStatus status = Data.IDStatus.getStatusByTransactionId(formattedTransactionId);
        
        if (status == null) {
            JOptionPane.showMessageDialog(this,
                "Invalid transaction reference number. Please check and try again.",
                "Verification Failed", JOptionPane.ERROR_MESSAGE);
            TransactionReferenceNumberText.requestFocus();
            return;
        }
        
        Data.Citizen citizen = Data.Citizen.getCitizenById(status.getCitizenId());
        if (citizen == null) {
            JOptionPane.showMessageDialog(this,
                "No application found for this transaction reference.",
                "Verification Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Step 8: Verify name match with citizen record
        if (!firstName.equalsIgnoreCase(citizen.getFname()) || 
            !lastName.equalsIgnoreCase(citizen.getLname())) {
            int response = JOptionPane.showConfirmDialog(this,
                "The name you entered doesn't match the application record.\n" +
                "Application is registered to: " + citizen.getFullName() + "\n\n" +
                "Do you want to continue with your entered name?",
                "Name Mismatch", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (response != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        // Step 9: Check if citizen already has a user account
        if (citizen.getUserId() != null) {
            JOptionPane.showMessageDialog(this,
                "An account already exists for this application.\n" +
                "Please use the sign-in option instead.",
                "Account Exists", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Step 10: Validate email uniqueness
        if (isEmailAlreadyUsed(email)) {
            JOptionPane.showMessageDialog(this,
                "This email address is already registered. Please use a different email.",
                "Email Already Exists", JOptionPane.WARNING_MESSAGE);
            EmailText.requestFocus();
            return;
        }
        
        // Step 11: Validate terms agreement
        if (!agreedToTerms) {
            TermsAndConditionCheckBox.setError(true);
            JOptionPane.showMessageDialog(this,
                "You must agree to the Terms and Conditions to create an account.",
                "Terms Agreement Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Step 12: Generate username (using first name + last name initial + random number)
        String username = generateUsername(firstName, lastName);
        
        // Step 13: Create user object
        Data.User newUser = new Data.User();
        newUser.setFname(firstName);
        newUser.setMname(middleName.isEmpty() ? null : middleName);
        newUser.setLname(lastName);
        newUser.setUsername(username);
        newUser.setPassword(password); // In production, this should be hashed
        newUser.setRole("CITIZEN");
        newUser.setPhone(citizen.getPhone());
        newUser.setEmail(email);
        newUser.setCreatedDate(new Date(System.currentTimeMillis()));
        
        // Step 14: Save user to database
        if (Data.User.addUser(newUser)) {
            // Get the newly created user ID
            Data.User savedUser = Data.User.authenticate(username, password);
            if (savedUser != null) {
                // Update citizen record with user ID
                citizen.setUserId(savedUser.getUserId());
                if (Data.Citizen.updateCitizen(citizen)) {
                    // Log activity
                    Data.ActivityLog.logActivity(savedUser.getUserId(), 
                        "New account created for citizen: " + citizen.getFullName());
                    
                    // Send notification
                    Data.Notification.addNotification(citizen.getCitizenId(),
                        "Welcome! Your account has been successfully created. " +
                        "You can now track your ID application status.",
                        "Account Created");
                    
                    JOptionPane.showMessageDialog(this,
                        "Account created successfully!\n\n" +
                        "Username: " + username + "\n" +
                        "Please use this username to sign in.\n\n" +
                        "You can now track your ID application status.",
                        "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
                    
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Error linking account to application. Please contact support.",
                        "Registration Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to create account. Please try again or contact support.",
                "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
    
    private String validatePassword(String password) {
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
    
    private boolean isEmailAlreadyUsed(String email) {
        // Check if email exists in users table
        java.util.List<Data.User> allUsers = Data.User.getAllUsers();
        for (Data.User user : allUsers) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }
    
    private String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName.toLowerCase() + 
                            (lastName.length() > 0 ? lastName.substring(0, 1).toLowerCase() : "");
        
        // Check if username exists
        String username = baseUsername;
        int counter = 1;
        
        while (usernameExists(username)) {
            username = baseUsername + counter;
            counter++;
        }
        
        return username;
    }
    
    private boolean usernameExists(String username) {
        java.util.List<Data.User> allUsers = Data.User.getAllUsers();
        for (Data.User user : allUsers) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }
    
    private void clearForm() {
        FirstnameText.clear();
        MiddleNameTextOptional.clear();
        LastnameText.clear();
        EmailText.clear();
        TransactionReferenceNumberText.clear();
        PasswordText.clear();
        ConfirmPasswordText.clear();
        TermsAndConditionCheckBox.setSelected(false);
        TermsAndConditionCheckBox.setError(false);
    }

    // Getters for components (existing code remains the same)
    public javax.swing.JButton getCreateAccountButton() {
        return CreateAccountButton;
    }

    public CustomTextField getFirstnameText() {
        return FirstnameText;
    }

    public CustomTextField getMiddleNameTextOptional() {
        return MiddleNameTextOptional;
    }

    public CustomTextField getLastnameText() {
        return LastnameText;
    }

    public CustomTextField getEmailText() {
        return EmailText;
    }

    public CustomTextField getTransactionReferenceNumberText() {
        return TransactionReferenceNumberText;
    }

    public CustomPasswordField getPasswordText() {
        return PasswordText;
    }

    public CustomPasswordField getConfirmPasswordText() {
        return ConfirmPasswordText;
    }
    
    public CustomCheckBox getTermsAndConditionCheckBox() {
        return TermsAndConditionCheckBox;
    }
    
    public javax.swing.JButton getSigninButton() {
        return SigninButton;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private sys.main.CustomPasswordField ConfirmPasswordText;
    private javax.swing.JButton CreateAccountButton;
    private sys.main.CustomTextField EmailText;
    private sys.main.CustomTextField FirstnameText;
    private sys.main.CustomTextField LastnameText;
    private sys.main.CustomTextField MiddleNameTextOptional;
    private sys.main.CustomPasswordField PasswordText;
    private javax.swing.JPanel RIGHT;
    private javax.swing.JButton SigninButton;
    private sys.main.CustomCheckBox TermsAndConditionCheckBox;
    private sys.main.CustomTextField TransactionReferenceNumberText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}
