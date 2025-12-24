package sys.main;

import backend.objects.Data;
import backend.objects.Data.User;
import backend.services.AccountService;
import javax.swing.JOptionPane;

public class Landing extends javax.swing.JFrame {
    
    private boolean isLoggingIn = false;
    private User loggedInUser = null;
    private boolean loginSuccessful = false;

    public Landing() {
        initComponents();
        setupLoginAction();
        setupRegistrationAction();
    }
    
    private void setupLoginAction() {
        // Get the LoginButton from the Login panel
        javax.swing.JButton loginButton = login.getLoginButton();
        CustomTextField usernameField = login.getUsernameText();
        sys.main.CustomPasswordField passwordField = login.getPasswordText();
        
        // Set up login button action
        loginButton.addActionListener(e -> performLogin());
        
        // Set up Enter key action for password field
        passwordField.addActionListener(e -> performLogin());
        
        // Set up Enter key action for username field
        usernameField.addActionListener(e -> passwordField.requestFocus());
    }
    
    private void setupRegistrationAction() {
        // Get the RegisterButton from the Login panel
        javax.swing.JButton registerButton = login.getRegisterButton();
        
        // Set up register button action - switch to registration tab
        registerButton.addActionListener(e -> {
            BodyTabbedPane.setSelectedIndex(1); // Switch to registration tab (index 1)
        });
        
        // Get the SigninButton from the Registration panel
        javax.swing.JButton signinButton = registration.getSigninButton();
        
        // Set up signin button action - switch back to login tab
        signinButton.addActionListener(e -> {
            BodyTabbedPane.setSelectedIndex(0); // Switch to login tab (index 0)
        });
        
        // Get the CreateAccountButton from the Registration panel
        javax.swing.JButton createAccountButton = registration.getCreateAccountButton();
        
        // Set up create account button action
        createAccountButton.addActionListener(e -> performRegistration());
    }
    
    private void performLogin() {
        if (isLoggingIn) {
            return; // Prevent multiple login attempts
        }

        CustomTextField usernameField = login.getUsernameText();
        sys.main.CustomPasswordField passwordField = login.getPasswordText();
        javax.swing.JButton loginButton = login.getLoginButton();

        String username = usernameField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username/email and password.", 
                "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        isLoggingIn = true;
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        // Try to authenticate - allow both username or email
        loggedInUser = Data.User.authenticate(username, password);

        if (loggedInUser != null) {
            // Check if user is active
            if (!loggedInUser.isActive()) {
                JOptionPane.showMessageDialog(this,
                    "Your account has been deactivated. Please contact support.",
                    "Account Disabled", JOptionPane.ERROR_MESSAGE);
                
                // Reset form
                passwordField.setText("");
                loginButton.setEnabled(true);
                loginButton.setText("LOGIN");
                isLoggingIn = false;
                return;
            }
            
            // Check if user has at least one role assigned
            if (loggedInUser.getRoles() == null || loggedInUser.getRoles().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Your account has no roles assigned. Please contact administrator.",
                    "Role Error", JOptionPane.ERROR_MESSAGE);
                
                // Reset form
                passwordField.setText("");
                loginButton.setEnabled(true);
                loginButton.setText("LOGIN");
                isLoggingIn = false;
                return;
            }
            
            // Update last login time
            Data.User.updateLastLogin(loggedInUser.getUserId());
            
            loginSuccessful = true;
            System.out.println("Login successful for: " + loggedInUser.getFullName());
            System.out.println("Roles: " + loggedInUser.getRoles());
            System.out.println("Primary Role: " + loggedInUser.getRole());

            // Close the login window
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid username/email or password!", 
                "Login Failed", JOptionPane.ERROR_MESSAGE);

            // Reset form
            passwordField.setText("");
            loginButton.setEnabled(true);
            loginButton.setText("LOGIN");
            isLoggingIn = false;
        }
    }
    
    private void performRegistration() {
        // Get form data from registration panel
        CustomTextField firstNameField = registration.getFirstnameText();
        CustomTextField middleNameField = registration.getMiddleNameTextOptional();
        CustomTextField lastNameField = registration.getLastnameText();
        CustomTextField emailField = registration.getEmailText();
        CustomTextField phoneField = registration.getPhoneNumberText();
        CustomTextField streetField = registration.getStreetAddressText();
        CustomTextField barangayField = registration.getBarangayAddressText();
        CustomTextField cityField = registration.getCityText();
        CustomTextField stateField = registration.getStateProvinceText();
        CustomTextField zipField = registration.getZipPostalCodeTextField();
        CustomTextField countryField = registration.getCountryText();
        CustomTextField usernameField = registration.getUsernameText();
        CustomTextField trnField = registration.getTransactionReferenceNumberText(); // TRN field
        CustomPasswordField passwordField = registration.getPasswordText();
        CustomPasswordField confirmPasswordField = registration.getConfirmPasswordText();
        CustomCheckBox termsCheckBox = registration.getTermsAndConditionCheckBox();
        component.CustomDatePicker.CustomDatePicker dobPicker = registration.getDateOfBirthcustomDatePicker();
        component.DropdownButton.CustomDropdownButton genderDropdown = registration.getGenderDropdownButton();

        // Get values
        String firstName = firstNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String streetAddress = streetField.getText().trim();
        String barangay = barangayField.getText().trim();
        String city = cityField.getText().trim();
        String stateProvince = stateField.getText().trim();
        String zipCode = zipField.getText().trim();
        String country = countryField.getText().trim();
        String username = usernameField.getText().trim();

        // GET THE TRN VALUE FROM THE TEXT FIELD
        String transactionRefNumber = trnField.getText().trim();

        // Format the TRN before validation
        if (!transactionRefNumber.isEmpty()) {
            transactionRefNumber = Data.IDStatus.formatTransactionId(transactionRefNumber);
            trnField.setText(transactionRefNumber); // Update UI with formatted version
        }

        String password = String.valueOf(passwordField.getPassword());
        String confirmPassword = String.valueOf(confirmPasswordField.getPassword());
        String selectedGender = genderDropdown.getText();
        java.util.Date dob = dobPicker.getDate();

        // Validate required fields
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || 
            username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                "Please fill in all required fields marked with *",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate gender selection
        if (selectedGender == null || selectedGender.isEmpty() || selectedGender.equals("Select Gender")) {
            JOptionPane.showMessageDialog(this,
                "Please select a gender",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            genderDropdown.requestFocus();
            return;
        }

        // Validate email format
        if (!AccountService.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            emailField.requestFocus();
            return;
        }

        // Validate username uniqueness
        if (AccountService.usernameExists(username)) {
            JOptionPane.showMessageDialog(this,
                "Username already exists. Please choose a different one.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return;
        }

        // Validate password strength
        String passwordError = AccountService.validatePassword(password);
        if (passwordError != null) {
            JOptionPane.showMessageDialog(this,
                passwordError,
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            passwordField.setText("");
            confirmPasswordField.setText("");
            passwordField.requestFocus();
            return;
        }

        // Validate password match
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match. Please ensure both password fields are identical.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            passwordField.setText("");
            confirmPasswordField.setText("");
            passwordField.requestFocus();
            return;
        }

        // Validate terms and conditions
        if (!termsCheckBox.isSelected()) {
            JOptionPane.showMessageDialog(this,
                "You must agree to the Terms and Conditions to create an account.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            termsCheckBox.setError(true);
            return;
        } else {
            termsCheckBox.setError(false);
        }

        try {
            // Check if email already exists
            if (AccountService.isEmailAlreadyUsed(email)) {
                JOptionPane.showMessageDialog(this,
                    "This email address is already registered. Please use a different email or sign in.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
                emailField.requestFocus();
                return;
            }

            // Validate TRN format if provided
            if (!transactionRefNumber.isEmpty()) {
                String trnValidation = AccountService.validateTransactionReferenceNumber(transactionRefNumber);
                if (trnValidation != null) {
                    JOptionPane.showMessageDialog(this,
                        "Invalid Transaction Reference Number:\n" + trnValidation,
                        "Invalid Format", JOptionPane.WARNING_MESSAGE);
                    trnField.requestFocus();
                    return;
                }

                // Check if TRN already exists in database
                if (AccountService.isTransactionReferenceNumberAlreadyUsed(transactionRefNumber)) {
                    JOptionPane.showMessageDialog(this,
                        "This Transaction Reference Number is already linked to another account.",
                        "TRN Already Used", JOptionPane.WARNING_MESSAGE);
                    trnField.requestFocus();
                    return;
                }
            }

            // Use AccountService to create the account with user-provided username and TRN
            System.out.println("Calling AccountService.createAccount() with username: " + username + " and TRN: " + transactionRefNumber);
            boolean success = AccountService.createAccount(
                firstName, middleName, lastName, email, password, username,
                selectedGender, phone, streetAddress, barangay, city, 
                stateProvince, zipCode, country, dob,
                transactionRefNumber
            );

            if (success) {
                // Log activity
                Data.User createdUser = Data.User.authenticate(username, password);
                if (createdUser != null) {
                    Data.ActivityLog.logActivity(createdUser.getUserId(), 
                        "Registered new account and created citizen record" + 
                        (transactionRefNumber.isEmpty() ? "" : " with Transaction Reference Number: " + transactionRefNumber));
                }

                JOptionPane.showMessageDialog(this,
                    "✅ Registration successful!\n\n" +
                    "Account created for: " + firstName + " " + lastName + "\n" +
                    "Username: " + username + "\n" +
                    "Email: " + email + "\n" +
                    "Gender: " + selectedGender + "\n" +
                    "Role: Citizen\n\n" +
                    (transactionRefNumber.isEmpty() ? 
                     "You can now sign in and add your transaction reference number\nin your profile to track your ID application status." :
                     "Your Transaction Reference Number (" + transactionRefNumber + ") has been linked to your account."),
                    "Registration Successful", JOptionPane.INFORMATION_MESSAGE);

                // Clear form
                clearRegistrationForm();

                // Switch back to login tab
                BodyTabbedPane.setSelectedIndex(0);

                // Auto-fill login form with new credentials
                login.getUsernameText().setText(username);
                login.getPasswordText().setText("");
                login.getUsernameText().requestFocus();

            } else {
                JOptionPane.showMessageDialog(this,
                    "Registration failed. Please try again or contact support.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error during registration: " + e.getMessage(),
                "Registration Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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
        
        // Check for at least one digit
        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one number";
        }
        
        // Check for at least one letter
        if (!password.matches(".*[a-zA-Z].*")) {
            return "Password must contain at least one letter";
        }
        
        return null; // Password is valid
    }
    
    private String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName.toLowerCase() + 
                            (lastName.length() > 0 ? lastName.substring(0, 1).toLowerCase() : "");
        return baseUsername;
    }
    
    private String generateUniqueUsername(String firstName, String lastName) {
        String baseUsername = generateUsername(firstName, lastName);
        String username = baseUsername;
        int counter = 1;
        
        while (Data.User.checkUsernameExists(username, null)) {
            username = baseUsername + counter;
            counter++;
        }
        
        return username;
    }
    
    private void clearRegistrationForm() {
        registration.getFirstnameText().clear();
        registration.getMiddleNameTextOptional().clear();
        registration.getLastnameText().clear();
        registration.getEmailText().clear();
        registration.getPhoneNumberText().clear();
        registration.getStreetAddressText().clear();
        registration.getCityText().clear();
        registration.getStateProvinceText().clear();
        registration.getZipPostalCodeTextField().clear();
        registration.getCountryText().clear();
        registration.getUsernameText().clear();
        registration.getPasswordText().setText("");
        registration.getConfirmPasswordText().setText("");
        registration.getDateOfBirthcustomDatePicker().setDate(null);
        registration.getGenderDropdownButton().setText("Select Gender");
        registration.getTermsAndConditionCheckBox().setSelected(false);
        registration.getTermsAndConditionCheckBox().setError(false);
    }
    
    // Add getter methods for Main to check login status
    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }
    
    public User getLoggedInUser() {
        return loggedInUser;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MainPanel = new javax.swing.JPanel();
        RIGHT = new javax.swing.JPanel();
        BodyTabbedPane = new component.NoTabJTabbedPane();
        login = new sys.main.Login();
        registration = new sys.main.Registration();
        LEFT = new javax.swing.JPanel();
        LogoLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Login");
        setBackground(new java.awt.Color(201, 177, 158));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setSize(new java.awt.Dimension(800, 500));

        MainPanel.setBackground(new java.awt.Color(255, 255, 255));
        MainPanel.setPreferredSize(new java.awt.Dimension(800, 500));

        RIGHT.setBackground(new java.awt.Color(255, 255, 255));
        RIGHT.setPreferredSize(new java.awt.Dimension(450, 500));

        BodyTabbedPane.addTab("tab2", login);
        BodyTabbedPane.addTab("tab2", registration);

        javax.swing.GroupLayout RIGHTLayout = new javax.swing.GroupLayout(RIGHT);
        RIGHT.setLayout(RIGHTLayout);
        RIGHTLayout.setHorizontalGroup(
            RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(BodyTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        RIGHTLayout.setVerticalGroup(
            RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(BodyTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        LEFT.setBackground(new java.awt.Color(142, 217, 255));
        LEFT.setPreferredSize(new java.awt.Dimension(350, 500));

        LogoLabel2.setBackground(new java.awt.Color(142, 217, 255));
        LogoLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LogoLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/psa_logo_image.png"))); // NOI18N
        LogoLabel2.setMinimumSize(new java.awt.Dimension(2089, 2048));
        LogoLabel2.setPreferredSize(new java.awt.Dimension(300, 300));

        javax.swing.GroupLayout LEFTLayout = new javax.swing.GroupLayout(LEFT);
        LEFT.setLayout(LEFTLayout);
        LEFTLayout.setHorizontalGroup(
            LEFTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LEFTLayout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addComponent(LogoLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );
        LEFTLayout.setVerticalGroup(
            LEFTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LEFTLayout.createSequentialGroup()
                .addContainerGap(100, Short.MAX_VALUE)
                .addComponent(LogoLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(100, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout MainPanelLayout = new javax.swing.GroupLayout(MainPanel);
        MainPanel.setLayout(MainPanelLayout);
        MainPanelLayout.setHorizontalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addComponent(RIGHT, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(LEFT, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE))
        );
        MainPanelLayout.setVerticalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(RIGHT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(LEFT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.NoTabJTabbedPane BodyTabbedPane;
    private javax.swing.JPanel LEFT;
    private javax.swing.JLabel LogoLabel2;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JPanel RIGHT;
    private sys.main.Login login;
    private sys.main.Registration registration;
    // End of variables declaration//GEN-END:variables
}
