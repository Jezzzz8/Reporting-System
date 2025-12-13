package sys.main;

import backend.objects.Data;
import backend.objects.Data.User;
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
        String password = passwordField.getPassword();

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
            loginSuccessful = true;
            System.out.println("Login successful for: " + loggedInUser.getFullName());

            // Close the login window
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid username/email or password!", 
                "Login Failed", JOptionPane.ERROR_MESSAGE);

            // Reset form
            passwordField.setPassword("");
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
        CustomTextField refNumField = registration.getTransactionReferenceNumberText();
        CustomPasswordField passwordField = registration.getPasswordText();
        CustomPasswordField confirmPasswordField = registration.getConfirmPasswordText();
        CustomCheckBox termsCheckBox = registration.getTermsAndConditionCheckBox();
        
        // Validate required fields
        if (firstNameField.getText().trim().isEmpty() ||
            lastNameField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() ||
            refNumField.getText().trim().isEmpty() ||
            passwordField.getPassword().trim().isEmpty() ||
            confirmPasswordField.getPassword().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "Please fill in all required fields marked with *",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate email format
        String email = emailField.getText().trim();
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate password strength
        String password = passwordField.getPassword();
        String passwordError = validatePassword(password);
        if (passwordError != null) {
            JOptionPane.showMessageDialog(this,
                passwordError,
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            passwordField.clear();
            confirmPasswordField.clear();
            passwordField.requestFocus();
            return;
        }
        
        // Validate password match
        String confirmPassword = confirmPasswordField.getPassword();
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match. Please ensure both password fields are identical.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            passwordField.clear();
            confirmPasswordField.clear();
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
            if (isEmailAlreadyRegistered(email)) {
                JOptionPane.showMessageDialog(this,
                    "This email address is already registered. Please use a different email or sign in.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if transaction reference number exists
            String transactionId = refNumField.getText().trim();
            Data.IDStatus status = Data.IDStatus.getStatusByTransactionId(transactionId);
            if (status == null) {
                JOptionPane.showMessageDialog(this,
                    "Invalid transaction reference number. Please check and try again.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get citizen associated with this transaction
            Data.Citizen citizen = Data.Citizen.getCitizenById(status.getCitizenId());
            if (citizen == null) {
                JOptionPane.showMessageDialog(this,
                    "No application found for this transaction reference number.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if citizen already has a user account
            if (citizen.getUserId() != null) {
                JOptionPane.showMessageDialog(this,
                    "An account already exists for this application. Please sign in instead.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create user data object with separate name fields
            Data.User newUser = new Data.User();
            newUser.setFname(firstNameField.getText().trim());
            newUser.setMname(middleNameField.getText().trim());
            newUser.setLname(lastNameField.getText().trim());
            newUser.setUsername(email); // Using email as username
            newUser.setPassword(password);
            newUser.setRole("citizen");
            newUser.setPhone(citizen.getPhone()); // Use phone from citizen record
            newUser.setEmail(email);
            newUser.setCreatedDate(new java.sql.Date(System.currentTimeMillis()));
            
            // Save user to database
            boolean userSuccess = Data.User.addUser(newUser);
            
            if (userSuccess) {
                // Get the newly created user ID
                Data.User createdUser = Data.User.authenticate(email, password);
                if (createdUser != null) {
                    // Link citizen to user
                    citizen.setUserId(createdUser.getUserId());
                    boolean citizenSuccess = Data.Citizen.updateCitizen(citizen);
                    
                    if (citizenSuccess) {
                        // Log activity
                        Data.ActivityLog.logActivity(createdUser.getUserId(), "Registered account and linked to citizen ID: " + citizen.getCitizenId());
                        
                        JOptionPane.showMessageDialog(this,
                            "Registration successful!\n\n" +
                            "Account created for: " + createdUser.getFullName() + "\n" +
                            "Email: " + email + "\n" +
                            "Linked to your PhilSys application\n\n" +
                            "Please sign in with your credentials.",
                            "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
                        
                        // Clear form
                        clearRegistrationForm();
                        
                        // Switch back to login tab
                        BodyTabbedPane.setSelectedIndex(0);
                        
                        // Auto-fill login form with new credentials
                        login.getUsernameText().setText(email);
                        login.getPasswordText().setPassword("");
                        login.getUsernameText().requestFocus();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Registration completed but could not link to your application. Please contact support.",
                            "Partial Success", JOptionPane.WARNING_MESSAGE);
                    }
                }
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
    
    private boolean isEmailAlreadyRegistered(String email) {
        // Check if email exists in database
        java.util.List<Data.User> users = Data.User.getAllUsers();
        for (Data.User user : users) {
            if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
            if (user.getUsername().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }
    
    private void clearRegistrationForm() {
        registration.getFirstnameText().clear();
        registration.getMiddleNameTextOptional().clear();
        registration.getLastnameText().clear();
        registration.getEmailText().clear();
        registration.getTransactionReferenceNumberText().clear();
        registration.getPasswordText().clear();
        registration.getConfirmPasswordText().clear();
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
        LEFT2 = new javax.swing.JPanel();
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

        BodyTabbedPane.addTab("tab1", login);
        BodyTabbedPane.addTab("tab2", registration);

        javax.swing.GroupLayout RIGHTLayout = new javax.swing.GroupLayout(RIGHT);
        RIGHT.setLayout(RIGHTLayout);
        RIGHTLayout.setHorizontalGroup(
            RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(BodyTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
        );
        RIGHTLayout.setVerticalGroup(
            RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(BodyTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        LEFT2.setBackground(new java.awt.Color(142, 217, 255));
        LEFT2.setPreferredSize(new java.awt.Dimension(350, 500));

        LogoLabel2.setBackground(new java.awt.Color(142, 217, 255));
        LogoLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LogoLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/psa_logo_image.png"))); // NOI18N
        LogoLabel2.setMinimumSize(new java.awt.Dimension(2089, 2048));
        LogoLabel2.setPreferredSize(new java.awt.Dimension(300, 300));

        javax.swing.GroupLayout LEFT2Layout = new javax.swing.GroupLayout(LEFT2);
        LEFT2.setLayout(LEFT2Layout);
        LEFT2Layout.setHorizontalGroup(
            LEFT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LEFT2Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addComponent(LogoLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        LEFT2Layout.setVerticalGroup(
            LEFT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LEFT2Layout.createSequentialGroup()
                .addContainerGap(100, Short.MAX_VALUE)
                .addComponent(LogoLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(100, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout MainPanelLayout = new javax.swing.GroupLayout(MainPanel);
        MainPanel.setLayout(MainPanelLayout);
        MainPanelLayout.setHorizontalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addComponent(RIGHT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(LEFT2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        MainPanelLayout.setVerticalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(RIGHT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(LEFT2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
    private javax.swing.JPanel LEFT2;
    private javax.swing.JLabel LogoLabel2;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JPanel RIGHT;
    private sys.main.Login login;
    private sys.main.Registration registration;
    // End of variables declaration//GEN-END:variables
}
