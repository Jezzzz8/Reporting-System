package sys.main;

import javax.swing.JOptionPane;
import java.awt.Color;
import backend.objects.*;
import java.sql.Date;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class Registration extends javax.swing.JPanel {
    
    private Data.Citizen citizenInfo;
    
    
    public Registration() {
        initComponents();

        // Set gender options immediately after initComponents
        setupGenderDropdown();

        applyStyles();
        setupValidationListeners();

        // Debug: Print component info
        System.out.println("GenderDropdownButton initialized: " + (GenderDropdownButton != null));
        System.out.println("GenderDropdownButton class: " + GenderDropdownButton.getClass().getName());
    }
    
    private void applyStyles() {
        
        // ENABLE THE FORMAT GUIDE
        TransactionIDText.enableFormatGuide("XXXX-XXXX-XXXX-XXXX-XXXX-XXXX-XX");
        TransactionIDText.setPlaceholder("Transaction Reference Number *");

        PasswordText.disablePasswordVisibilityToggle();
        ConfirmPasswordText.disablePasswordVisibilityToggle();
        
        // Style terms and conditions checkbox
        TermsAndConditionCheckBox.setTermsText("I agree to the ");
        TermsAndConditionCheckBox.setLinkText("Terms and Conditions");

        // Add link click listener for terms and conditions
        TermsAndConditionCheckBox.addLinkClickListener(() -> {
            showTermsAndConditionsDialog();
        });
    }
    
    private void setupGenderDropdown() {
        // Set gender options
        String[] genderOptions = {"Male", "Female", "Other"};

        // Create a custom dropdown button with options
        JPopupMenu genderMenu = new JPopupMenu();

        // Add options to the menu
        for (String gender : genderOptions) {
            JMenuItem menuItem = new JMenuItem(gender);
            menuItem.addActionListener(e -> {
                GenderDropdownButton.setText(gender);
            });
            genderMenu.add(menuItem);
        }

        // Add action listener to the dropdown button
        GenderDropdownButton.getButton().addActionListener(e -> {
            genderMenu.show(GenderDropdownButton, 0, GenderDropdownButton.getHeight());
        });

        // Set initial gender if available
        if (citizenInfo != null && citizenInfo.getGender() != null && !citizenInfo.getGender().isEmpty()) {
            String savedGender = citizenInfo.getGender();
            for (String option : genderOptions) {
                if (option.equalsIgnoreCase(savedGender)) {
                    GenderDropdownButton.setText(option);
                    break;
                }
            }
        } else {
            GenderDropdownButton.setText(null);
        }
    }
    
    private void setupFallbackGenderDropdown() {
        // This is a fallback method in case CustomDropdownButton doesn't work
        System.out.println("Setting up fallback gender dropdown...");
        
        // Remove the existing dropdown button
        this.remove(GenderDropdownButton);
        
        // Create a simple JComboBox as fallback
        JComboBox<String> genderComboBox = new JComboBox<>();
        genderComboBox.addItem("Select Gender");
        genderComboBox.addItem("Male");
        genderComboBox.addItem("Female");
        genderComboBox.addItem("Other");
        genderComboBox.setPreferredSize(new java.awt.Dimension(350, 40));
        
        // Add it to the layout (you might need to adjust the layout)
        // This is a simplified approach - you may need to adjust based on your actual layout
        
        System.out.println("Fallback dropdown created");
    }

    private void setupValidationListeners() {
        // Add focus listener for transaction reference validation
        TransactionIDText.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                validateTransactionReferenceOnFocusLost();
            }
        });
        
        // Add password strength checker
        PasswordText.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String password = String.valueOf(PasswordText.getPassword());
                checkPasswordStrength(password);
            }
        });
        
        // Add a test button listener for debugging
        CreateAccountButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Test gender dropdown before account creation
                testGenderDropdown();
                // Then proceed with account creation
                createAccount();
            }
        });
    }
    
    private void testGenderDropdown() {
        System.out.println("=== Testing Gender Dropdown ===");
        System.out.println("GenderDropdownButton is null: " + (GenderDropdownButton == null));
        System.out.println("Current text: " + GenderDropdownButton.getText());
        System.out.println("Placeholder: " + GenderDropdownButton.getPlaceholder());
        System.out.println("Is enabled: " + GenderDropdownButton.isEnabled());
        System.out.println("Is visible: " + GenderDropdownButton.isVisible());
        System.out.println("=== End Test ===");
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
    
    private void showPasswordRequirements() {
        String requirements = "PASSWORD REQUIREMENTS:\n\n" +
                             "1. Minimum 6 characters\n" +
                             "2. At least one letter (a-z, A-Z)\n" +
                             "3. At least one number (0-9)\n\n" +
                             "Example: Pass123 or user456";

        JOptionPane.showMessageDialog(this, requirements,
            "Password Requirements", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void checkPasswordStrength(String password) {
        if (password.length() >= 8 && 
            password.matches(".*[A-Z].*") && 
            password.matches(".*[a-z].*") && 
            password.matches(".*\\d.*") && 
            password.matches(".*[!@#$%^&*()].*")) {
            // Strong password
            PasswordText.setBorderColor(new Color(0, 150, 0));
        } else if (password.length() >= 6 && 
                   password.matches(".*[a-zA-Z].*") && 
                   password.matches(".*\\d.*")) {
            // Medium password
            PasswordText.setBorderColor(new Color(255, 165, 0));
        } else if (password.length() > 0) {
            // Weak password
            PasswordText.setBorderColor(new Color(255, 0, 0));
        } else {
            // Empty
            PasswordText.setBorderColor(new Color(200, 200, 200));
        }
    }
    
    private void validateTransactionReferenceOnFocusLost() {
        String transactionId = TransactionIDText.getText().trim();
        if (!transactionId.isEmpty()) {
            validateTransactionReferenceNumber(transactionId);
        }
    }
    
    private boolean validateGenderSelection() {
        String selectedGender = GenderDropdownButton.getText();
        System.out.println("Validating gender: " + selectedGender);
        
        if (selectedGender == null || selectedGender.isEmpty() || selectedGender.equals("Select Gender")) {
            JOptionPane.showMessageDialog(this, "Please select a gender.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            GenderDropdownButton.requestFocus();
            return false;
        }
        return true;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        customScrollPane1 = new component.Scroll.CustomScrollPane();
        RIGHT = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        CreateAccountButton = new javax.swing.JButton();
        SigninButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        FirstnameText = new sys.main.CustomTextField();
        MiddleNameOptionalTextOptional = new sys.main.CustomTextField();
        LastnameText = new sys.main.CustomTextField();
        ConfirmPasswordText = new sys.main.CustomPasswordField();
        EmailText = new sys.main.CustomTextField();
        PasswordText = new sys.main.CustomPasswordField();
        TermsAndConditionCheckBox = new sys.main.CustomCheckBox();
        DateOfBirthcustomDatePicker = new component.CustomDatePicker.CustomDatePicker();
        StreetAddressText = new sys.main.CustomTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        StateProvinceText = new sys.main.CustomTextField();
        CountryText = new sys.main.CustomTextField();
        PhoneNumberText = new sys.main.CustomTextField();
        TransactionIDText = new sys.main.CustomTextField();
        UsernameText = new sys.main.CustomTextField();
        jLabel10 = new javax.swing.JLabel();
        GenderDropdownButton = new component.DropdownButton.CustomDropdownButton();
        CityText = new sys.main.CustomTextField();
        ZipPostalCodeTextField = new sys.main.CustomTextField();

        setPreferredSize(new java.awt.Dimension(450, 500));

        customScrollPane1.setPreferredSize(new java.awt.Dimension(450, 500));

        RIGHT.setBackground(new java.awt.Color(255, 255, 255));
        RIGHT.setPreferredSize(new java.awt.Dimension(450, 1060));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 120, 215));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("REGISTRATION FORM");

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

        FirstnameText.setPlaceholder("First Name");

        MiddleNameOptionalTextOptional.setPlaceholder("Middle Name Optional");

        LastnameText.setPlaceholder("Last Name");

        ConfirmPasswordText.setPlaceholder("Confirm Password");

        EmailText.setPlaceholder("Email Address");

        PasswordText.setPlaceholder("Password");

        DateOfBirthcustomDatePicker.setDate(null);
        DateOfBirthcustomDatePicker.setPlaceholder("Date of Birth");

        StreetAddressText.setPlaceholder("Street Address");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 120, 215));
        jLabel7.setText("Contact Information");
        jLabel7.setPreferredSize(new java.awt.Dimension(400, 40));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 120, 215));
        jLabel9.setText("Account Creation");
        jLabel9.setPreferredSize(new java.awt.Dimension(400, 40));

        StateProvinceText.setPlaceholder("State/Province Address");

        CountryText.setPlaceholder("Country Address");

        PhoneNumberText.setPlaceholder("Phone Number");

        TransactionIDText.setPlaceholder("Transaction Reference Number");

        UsernameText.setPlaceholder("Username");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 120, 215));
        jLabel10.setText("Personal Information");
        jLabel10.setPreferredSize(new java.awt.Dimension(400, 40));

        GenderDropdownButton.setPlaceholder("Gender");

        CityText.setPlaceholder("City Address");

        ZipPostalCodeTextField.setPlaceholder("Zip/Postal Code address");

        javax.swing.GroupLayout RIGHTLayout = new javax.swing.GroupLayout(RIGHT);
        RIGHT.setLayout(RIGHTLayout);
        RIGHTLayout.setHorizontalGroup(
            RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
            .addGroup(RIGHTLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SigninButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(RIGHTLayout.createSequentialGroup()
                .addGroup(RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(RIGHTLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(DateOfBirthcustomDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LastnameText, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(MiddleNameOptionalTextOptional, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(EmailText, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(StateProvinceText, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CountryText, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(FirstnameText, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(RIGHTLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(ZipPostalCodeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(GenderDropdownButton, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(TermsAndConditionCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                                .addComponent(ConfirmPasswordText, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                                .addComponent(PasswordText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(UsernameText, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                                .addComponent(TransactionIDText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(StreetAddressText, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                                .addComponent(PhoneNumberText, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                                .addComponent(CityText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(RIGHTLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(CreateAccountButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        RIGHTLayout.setVerticalGroup(
            RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RIGHTLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(FirstnameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(MiddleNameOptionalTextOptional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LastnameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(DateOfBirthcustomDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(GenderDropdownButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(EmailText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(PhoneNumberText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(StreetAddressText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CityText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(StateProvinceText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ZipPostalCodeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CountryText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(TransactionIDText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(UsernameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PasswordText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ConfirmPasswordText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(TermsAndConditionCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CreateAccountButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SigninButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        PasswordText.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String password = String.valueOf(PasswordText.getPassword());
                checkPasswordStrength(password);
            }
        });

        customScrollPane1.setViewportView(RIGHT);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(customScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(customScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void SigninButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SigninButtonActionPerformed
        clearForm();
    }//GEN-LAST:event_SigninButtonActionPerformed

    private void CreateAccountButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreateAccountButtonActionPerformed
        createAccount();
    }//GEN-LAST:event_CreateAccountButtonActionPerformed
    
    private void createAccount() {
        // Step 1: Get all form values
        String firstName = FirstnameText.getText().trim();
        String middleName = MiddleNameOptionalTextOptional.getText().trim();
        String lastName = LastnameText.getText().trim();
        String email = EmailText.getText().trim();
        String transactionRef = TransactionIDText.getText().trim();
        String password = String.valueOf(PasswordText.getPassword()).trim();
        String confirmPassword = String.valueOf(ConfirmPasswordText.getPassword()).trim();
        String selectedGender = GenderDropdownButton.getText();
        boolean agreedToTerms = TermsAndConditionCheckBox.isSelected();
        
        // Debug output
        System.out.println("=== Creating Account ===");
        System.out.println("First Name: " + firstName);
        System.out.println("Last Name: " + lastName);
        System.out.println("Gender: " + selectedGender);
        System.out.println("Email: " + email);
        System.out.println("Transaction Ref: " + transactionRef);
        System.out.println("Password length: " + password.length());
        System.out.println("Confirm Password length: " + confirmPassword.length());
        System.out.println("Terms agreed: " + agreedToTerms);
        
        // Step 2: Validate required fields
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || 
            transactionRef.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all required fields marked with *",
                "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Step 3: Validate gender selection
        if (selectedGender == null || selectedGender.isEmpty() || selectedGender.equals("Select Gender")) {
            JOptionPane.showMessageDialog(this, "Please select a gender.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            GenderDropdownButton.requestFocus();
            return;
        }
        
        // Step 4: Validate transaction reference format
        if (!TransactionIDText.isFormatFilled()) {
            JOptionPane.showMessageDialog(this,
                "Please complete the transaction reference number format.\n" +
                "Expected format: ****-****-****-****-****-****-**",
                "Incomplete Format", JOptionPane.WARNING_MESSAGE);
            TransactionIDText.requestFocus();
            return;
        }
        
        // Step 5: Validate email format
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address",
                "Invalid Email", JOptionPane.WARNING_MESSAGE);
            EmailText.requestFocus();
            return;
        }
        
        // Step 6: Validate password
        String passwordError = validatePassword(password);
        if (passwordError != null) {
            JOptionPane.showMessageDialog(this, passwordError,
                "Password Requirements", JOptionPane.WARNING_MESSAGE);
            PasswordText.requestFocus();
            return;
        }
        
        // Step 7: Check password match
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match. Please re-enter your password.",
                "Password Mismatch", JOptionPane.WARNING_MESSAGE);
            ConfirmPasswordText.requestFocus();
            return;
        }
        
        // Step 8: Validate transaction reference and match with citizen data
        String formattedTransactionId = Data.IDStatus.formatTransactionId(transactionRef);
        Data.IDStatus status = Data.IDStatus.getStatusByTransactionId(formattedTransactionId);
        
        if (status == null) {
            JOptionPane.showMessageDialog(this,
                "Invalid transaction reference number. Please check and try again.",
                "Verification Failed", JOptionPane.ERROR_MESSAGE);
            TransactionIDText.requestFocus();
            return;
        }
        
        Data.Citizen citizen = Data.Citizen.getCitizenById(status.getCitizenId());
        if (citizen == null) {
            JOptionPane.showMessageDialog(this,
                "No application found for this transaction reference.",
                "Verification Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Step 9: Verify name match with citizen record
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
        
        // Step 10: Check if citizen already has a user account
        if (citizen.getUserId() != null) {
            JOptionPane.showMessageDialog(this,
                "An account already exists for this application.\n" +
                "Please use the sign-in option instead.",
                "Account Exists", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Step 11: Validate email uniqueness
        if (isEmailAlreadyUsed(email)) {
            JOptionPane.showMessageDialog(this,
                "This email address is already registered. Please use a different email.",
                "Email Already Exists", JOptionPane.WARNING_MESSAGE);
            EmailText.requestFocus();
            return;
        }
        
        // Step 12: Validate terms agreement
        if (!agreedToTerms) {
            TermsAndConditionCheckBox.setError(true);
            JOptionPane.showMessageDialog(this,
                "You must agree to the Terms and Conditions to create an account.",
                "Terms Agreement Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Step 13: Generate username (using first name + last name initial + random number)
        String username = generateUsername(firstName, lastName);
        
        // Step 14: Create user object
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
        
        // Step 15: Save user to database
        if (Data.User.addUser(newUser)) {
            // Get the newly created user ID
            Data.User savedUser = Data.User.authenticate(username, password);
            if (savedUser != null) {
                // Update citizen record with user ID
                citizen.setUserId(savedUser.getUserId());
                citizen.setGender(selectedGender); // Save the selected gender
                
                if (Data.Citizen.updateCitizen(citizen)) {
                    // Log activity
                    Data.ActivityLog.logActivity(savedUser.getUserId(), 
                        "New account created for citizen: " + citizen.getFullName() + " (Gender: " + selectedGender + ")");
                    
                    // Send notification
                    Data.Notification.addNotification(citizen.getCitizenId(),
                        "Welcome! Your account has been successfully created. " +
                        "You can now track your ID application status.",
                        "Account Created");
                    
                    JOptionPane.showMessageDialog(this,
                        "Account created successfully!\n\n" +
                        "Username: " + username + "\n" +
                        "Gender: " + selectedGender + "\n" +
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
        MiddleNameOptionalTextOptional.clear();
        LastnameText.clear();
        EmailText.clear();
        CityText.clear();
        PasswordText.setText(""); // Changed from clear()
        ConfirmPasswordText.setText(""); // Changed from clear()
        GenderDropdownButton.setText("Select Gender");
        TermsAndConditionCheckBox.setSelected(false);
        TermsAndConditionCheckBox.setError(false);
    }

    // Getters for components
    public javax.swing.JButton getCreateAccountButton() {
        return CreateAccountButton;
    }

    public CustomTextField getFirstnameText() {
        return FirstnameText;
    }

    public CustomTextField getMiddleNameTextOptional() {
        return MiddleNameOptionalTextOptional;
    }

    public CustomTextField getLastnameText() {
        return LastnameText;
    }

    public CustomTextField getEmailText() {
        return EmailText;
    }

    public CustomTextField getTransactionReferenceNumberText() {
        return TransactionIDText;
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
    
    public component.DropdownButton.CustomDropdownButton getGenderDropdownButton() {
        return GenderDropdownButton;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private sys.main.CustomTextField CityText;
    private sys.main.CustomPasswordField ConfirmPasswordText;
    private sys.main.CustomTextField CountryText;
    private javax.swing.JButton CreateAccountButton;
    private component.CustomDatePicker.CustomDatePicker DateOfBirthcustomDatePicker;
    private sys.main.CustomTextField EmailText;
    private sys.main.CustomTextField FirstnameText;
    private component.DropdownButton.CustomDropdownButton GenderDropdownButton;
    private sys.main.CustomTextField LastnameText;
    private sys.main.CustomTextField MiddleNameOptionalTextOptional;
    private sys.main.CustomPasswordField PasswordText;
    private sys.main.CustomTextField PhoneNumberText;
    private javax.swing.JPanel RIGHT;
    private javax.swing.JButton SigninButton;
    private sys.main.CustomTextField StateProvinceText;
    private sys.main.CustomTextField StreetAddressText;
    private sys.main.CustomCheckBox TermsAndConditionCheckBox;
    private sys.main.CustomTextField TransactionIDText;
    private sys.main.CustomTextField UsernameText;
    private sys.main.CustomTextField ZipPostalCodeTextField;
    private component.Scroll.CustomScrollPane customScrollPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    // End of variables declaration//GEN-END:variables
}
