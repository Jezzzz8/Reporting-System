package sys.main;

import javax.swing.JOptionPane;
import java.awt.Color;
import backend.objects.*;
import backend.services.AccountService;
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
        setupTransactionReferenceNumberFormat(); // Add this line

        // Debug: Print component info
        System.out.println("GenderDropdownButton initialized: " + (GenderDropdownButton != null));
        System.out.println("GenderDropdownButton class: " + GenderDropdownButton.getClass().getName());
    }
    
    private void setupTransactionReferenceNumberFormat() {
        // Remove the enableFormatGuide call and use a different approach

        // Add a key listener to auto-format as user types
        TransactionReferenceNumberText.getTextField().addKeyListener(new java.awt.event.KeyAdapter() {
            private String previousText = "";

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String currentText = TransactionReferenceNumberText.getText();

                // Only process if text changed
                if (currentText.equals(previousText)) {
                    return;
                }

                // Remove non-alphanumeric characters
                String clean = currentText.replaceAll("[^A-Za-z0-9]", "");

                // Format according to pattern
                StringBuilder formatted = new StringBuilder();
                int[] segments = {4, 4, 4, 4, 4, 4, 2}; // 26 characters total

                int charIndex = 0;
                for (int i = 0; i < segments.length && charIndex < clean.length(); i++) {
                    if (i > 0) {
                        formatted.append("-");
                    }

                    int segmentSize = segments[i];
                    int endIndex = Math.min(charIndex + segmentSize, clean.length());
                    formatted.append(clean.substring(charIndex, endIndex));
                    charIndex = endIndex;
                }

                // Append any remaining characters
                if (charIndex < clean.length()) {
                    if (formatted.length() > 0 && formatted.charAt(formatted.length() - 1) != '-') {
                        formatted.append("-");
                    }
                    formatted.append(clean.substring(charIndex));
                }

                String newText = formatted.toString();

                // Update if changed
                if (!newText.equals(currentText)) {
                    TransactionReferenceNumberText.setText(newText);

                    // Try to maintain cursor position
                    int cursorPos = TransactionReferenceNumberText.getTextField().getCaretPosition();
                    if (cursorPos < newText.length()) {
                        TransactionReferenceNumberText.getTextField().setCaretPosition(cursorPos);
                    }
                }

                previousText = newText;
            }
        });

        // Also format on focus loss using Data.IDStatus.formatTransactionId
        TransactionReferenceNumberText.getTextField().addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                String currentText = TransactionReferenceNumberText.getText();
                if (currentText != null && !currentText.trim().isEmpty()) {
                    String formatted = Data.IDStatus.formatTransactionId(currentText.trim());
                    if (!formatted.equals(currentText)) {
                        TransactionReferenceNumberText.setText(formatted);
                    }
                }
            }
        });

        // Set a tooltip to show expected format
        TransactionReferenceNumberText.setToolTipText("Format: XXXX-XXXX-XXXX-XXXX-XXXX-XXXX-XX (26 characters)");
    }
    
    private void showTransactionReferenceNumberFormatError() {
        String message = 
            "Invalid Transaction Reference Number format.\n\n" +
            "Expected format: XXXX-XXXX-XXXX-XXXX-XXXX-XXXX-XX\n" +
            "Total characters: 26 (including letters and numbers)\n" +
            "Format example: ABCD-1234-EFGH-5678-IJKL-9012-MN\n\n" +
            "Please enter a valid Transaction Reference Number\n" +
            "or leave the field empty if you don't have one yet.";

        JOptionPane.showMessageDialog(this, message,
            "Invalid Format", JOptionPane.WARNING_MESSAGE);
    }

    private boolean validateTransactionReferenceNumberFormat(String trn) {
        if (trn == null || trn.trim().isEmpty()) {
            return true; // Optional field
        }

        // Remove hyphens for validation
        String clean = trn.replace("-", "");

        // Should be 26 alphanumeric characters
        if (clean.length() != 26) {
            System.out.println("TRN validation failed: Expected 26 characters, got " + clean.length());
            return false;
        }

        // Should contain only letters and numbers
        boolean isValid = clean.matches("[A-Za-z0-9]{26}");
        if (!isValid) {
            System.out.println("TRN validation failed: Contains invalid characters");
            System.out.println("TRN: " + clean);
        }
        return isValid;
    }

    private void formatTransactionReferenceNumber() {
        String currentText = TransactionReferenceNumberText.getText();
        if (currentText != null && !currentText.trim().isEmpty()) {
            // Use Data.IDStatus.formatTransactionId to format
            String formatted = Data.IDStatus.formatTransactionId(currentText.trim());
            TransactionReferenceNumberText.setText(formatted);
        }
    }
    
    private void applyStyles() {
        // No need for transaction ID format guide anymore
        
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
        // No transaction ID validation needed anymore
        
        // Add password strength checker
        PasswordText.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String password = String.valueOf(PasswordText.getPassword());
                checkPasswordStrength(password);
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
    
    private boolean validateGenderSelection() {
        String selectedGender = GenderDropdownButton.getText();
        System.out.println("Validating gender: " + selectedGender);
        
        if (selectedGender == null || selectedGender.isEmpty() || selectedGender.equals("Select Gender")) {
            // Duplicate dialog removed - only show in createAccount()
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
        UsernameText = new sys.main.CustomTextField();
        jLabel10 = new javax.swing.JLabel();
        GenderDropdownButton = new component.DropdownButton.CustomDropdownButton();
        CityText = new sys.main.CustomTextField();
        ZipPostalCodeTextField = new sys.main.CustomTextField();
        BarangayAddressText = new sys.main.CustomTextField();
        TransactionReferenceNumberText = new sys.main.CustomTextField();
        CreateAccountButton = new component.Button.FlatButton();

        setPreferredSize(new java.awt.Dimension(450, 500));

        customScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        customScrollPane1.setPreferredSize(new java.awt.Dimension(450, 500));

        RIGHT.setBackground(new java.awt.Color(255, 255, 255));
        RIGHT.setPreferredSize(new java.awt.Dimension(450, 1150));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 120, 215));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("REGISTRATION FORM");

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

        FirstnameText.setPlaceholder("First Name*");

        MiddleNameOptionalTextOptional.setPlaceholder("Middle Name (Optional)");

        LastnameText.setPlaceholder("Last Name*");

        ConfirmPasswordText.setPlaceholder("Confirm Password*");

        EmailText.setPlaceholder("Email Address*");

        PasswordText.setPlaceholder("Password*");

        DateOfBirthcustomDatePicker.setDate(null);
        DateOfBirthcustomDatePicker.setPlaceholder("Date of Birth*");

        StreetAddressText.setPlaceholder("Street*");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 120, 215));
        jLabel7.setText("Contact Information");
        jLabel7.setPreferredSize(new java.awt.Dimension(400, 40));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 120, 215));
        jLabel9.setText("Account Creation");
        jLabel9.setPreferredSize(new java.awt.Dimension(400, 40));

        StateProvinceText.setPlaceholder("State/Province*");

        CountryText.setPlaceholder("Country*");

        PhoneNumberText.setPlaceholder("Phone Number*");

        UsernameText.setPlaceholder("Username*");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 120, 215));
        jLabel10.setText("Personal Information");
        jLabel10.setPreferredSize(new java.awt.Dimension(400, 40));

        GenderDropdownButton.setPlaceholder("Gender*");

        CityText.setPlaceholder("City*");

        ZipPostalCodeTextField.setPlaceholder("Zip/Postal Code*");

        BarangayAddressText.setPlaceholder("Barangay*");

        TransactionReferenceNumberText.setPlaceholder("Transaction Reference Number (Optional)");

        CreateAccountButton.setBackground(new java.awt.Color(0, 120, 215));
        CreateAccountButton.setText("CREATE ACCOUNT");
        CreateAccountButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        CreateAccountButton.setNormalColor(new java.awt.Color(0, 120, 215));
        CreateAccountButton.setPreferredSize(new java.awt.Dimension(200, 35));
        CreateAccountButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreateAccountButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout RIGHTLayout = new javax.swing.GroupLayout(RIGHT);
        RIGHT.setLayout(RIGHTLayout);
        RIGHTLayout.setHorizontalGroup(
            RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
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
                                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                                .addComponent(StreetAddressText, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                                .addComponent(PhoneNumberText, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                                .addComponent(CityText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(BarangayAddressText, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(TermsAndConditionCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ConfirmPasswordText, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                                .addComponent(PasswordText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(UsernameText, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                                .addComponent(TransactionReferenceNumberText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(RIGHTLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(RIGHTLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SigninButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(CreateAccountButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        RIGHTLayout.setVerticalGroup(
            RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RIGHTLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(FirstnameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(MiddleNameOptionalTextOptional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(LastnameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(DateOfBirthcustomDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(GenderDropdownButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 20, Short.MAX_VALUE)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(EmailText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(PhoneNumberText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(StreetAddressText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(BarangayAddressText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(CityText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(StateProvinceText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(ZipPostalCodeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(CountryText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 20, Short.MAX_VALUE)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(UsernameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(PasswordText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(ConfirmPasswordText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(TransactionReferenceNumberText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(TermsAndConditionCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CreateAccountButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SigninButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
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
            .addComponent(customScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        // TODO add your handling code here:
    }//GEN-LAST:event_CreateAccountButtonActionPerformed
    
    public void createAccount() {
        // Step 1: Get all form values
        String firstName = FirstnameText.getText().trim();
        String middleName = MiddleNameOptionalTextOptional.getText().trim();
        String lastName = LastnameText.getText().trim();
        String email = EmailText.getText().trim();
        String password = String.valueOf(PasswordText.getPassword()).trim();
        String confirmPassword = String.valueOf(ConfirmPasswordText.getPassword()).trim();
        String selectedGender = GenderDropdownButton.getText();
        boolean agreedToTerms = TermsAndConditionCheckBox.isSelected();
        String username = UsernameText.getText().trim();

        // Get TRN directly from the text field
        String transactionRefNumber = TransactionReferenceNumberText.getText().trim();

        // Format the TRN if provided
        if (!transactionRefNumber.isEmpty()) {
            transactionRefNumber = Data.IDStatus.formatTransactionId(transactionRefNumber);
            TransactionReferenceNumberText.setText(transactionRefNumber); // Update UI
        }

        // Debug output
        System.out.println("=== Creating Account ===");
        System.out.println("First Name: " + firstName);
        System.out.println("Last Name: " + lastName);
        System.out.println("Gender: " + selectedGender);
        System.out.println("Email: " + email);
        System.out.println("Username: " + username);
        System.out.println("Password length: " + password.length());
        System.out.println("Confirm Password length: " + confirmPassword.length());
        System.out.println("Transaction Ref Number: " + transactionRefNumber);
        System.out.println("Terms agreed: " + agreedToTerms);

        // Step 2: Validate required fields - ADD USERNAME TO VALIDATION
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || 
            password.isEmpty() || confirmPassword.isEmpty() || username.isEmpty()) {
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

        // Step 4: Validate email format
        if (!AccountService.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address",
                "Invalid Email", JOptionPane.WARNING_MESSAGE);
            EmailText.requestFocus();
            return;
        }

        // Step 5: Validate password
        String passwordError = AccountService.validatePassword(password);
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

        // Step 7: Validate email uniqueness
        if (AccountService.isEmailAlreadyUsed(email)) {
            JOptionPane.showMessageDialog(this,
                "This email address is already registered. Please use a different email.",
                "Email Already Exists", JOptionPane.WARNING_MESSAGE);
            EmailText.requestFocus();
            return;
        }

        // Step 8: Validate username - Check if username already exists
        if (AccountService.usernameExists(username)) {
            JOptionPane.showMessageDialog(this,
                "Username already exists. Please choose a different one.",
                "Username Taken", JOptionPane.WARNING_MESSAGE);
            UsernameText.requestFocus();
            return;
        }
        
        // NEW STEP: Validate Transaction Reference Number format
            if (!transactionRefNumber.isEmpty()) {
            String validationResult = AccountService.validateTransactionReferenceNumber(transactionRefNumber);
            if (validationResult != null) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid Transaction Reference Number:\n" + validationResult +
                    "\n\nLeave this field empty if you don't have a Transaction Reference Number yet.",
                    "Invalid Format", JOptionPane.WARNING_MESSAGE);
                TransactionReferenceNumberText.requestFocus();
                return;
            }

            // Additional check for citizens who might already have an account
            Data.IDStatus existingStatus = Data.IDStatus.getStatusByTransactionId(transactionRefNumber);
            if (existingStatus != null) {
                int response = JOptionPane.showConfirmDialog(this,
                    "This Transaction Reference Number is already linked to an existing account.\n\n" +
                    "Do you want to:\n" +
                    "1. Leave this field empty to create a new application\n" +
                    "2. Sign in to your existing account instead",
                    "TRN Already Exists", JOptionPane.YES_NO_OPTION);

                if (response == JOptionPane.YES_OPTION) {
                    // Clear the TRN field and continue with new account
                    TransactionReferenceNumberText.setText("");
                } else {
                    // Switch to login tab
                    javax.swing.JTabbedPane parentTab = (javax.swing.JTabbedPane) this.getParent().getParent();
                    parentTab.setSelectedIndex(0);
                    return;
                }
            }
        }

        // Step 9: Validate terms agreement
        if (!agreedToTerms) {
            TermsAndConditionCheckBox.setError(true);
            JOptionPane.showMessageDialog(this,
                "You must agree to the Terms and Conditions to create an account.",
                "Terms Agreement Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get other form values
        String phone = PhoneNumberText.getText().trim();
        String streetAddress = StreetAddressText.getText().trim();
        String barangay = BarangayAddressText.getText().trim();
        String city = CityText.getText().trim();
        String stateProvince = StateProvinceText.getText().trim();
        String zipCode = ZipPostalCodeTextField.getText().trim();
        String country = CountryText.getText().trim();

        // Get date of birth
        java.util.Date dob = DateOfBirthcustomDatePicker.getDate();

        // Step 10: Use AccountService to create the account
        System.out.println("Calling AccountService.createAccount() with TRN: " + 
            (transactionRefNumber.isEmpty() ? "No TRN provided" : transactionRefNumber));

        boolean success = AccountService.createAccount(
            firstName, middleName, lastName, email, password, username,
            selectedGender, phone, streetAddress, barangay, city, 
            stateProvince, zipCode, country, dob,
            transactionRefNumber.isEmpty() ? null : transactionRefNumber
        );

        if (success) {
            String successMessage = "✅ Account created successfully!\n\n" +
                "Username: " + username + "\n" +
                "Email: " + email + "\n" +
                "Gender: " + selectedGender + "\n" +
                "Address: " + barangay + ", " + city + "\n\n";

            if (!transactionRefNumber.isEmpty()) {
                successMessage += "Your existing Transaction Reference Number has been linked: " + 
                    transactionRefNumber + "\n" +
                    "You can now track your existing ID application status online.";
            } else {
                successMessage += "A new Transaction Reference Number has been generated for you.\n" +
                    "You can start your ID application process.";
            }

            JOptionPane.showMessageDialog(this,
                successMessage,
                "Registration Successful", JOptionPane.INFORMATION_MESSAGE);

            clearForm();

            // Switch to login tab
            javax.swing.JTabbedPane parentTab = (javax.swing.JTabbedPane) this.getParent().getParent();
            parentTab.setSelectedIndex(0);

        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to create account. Please try again or contact support.",
                "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean checkForExistingCitizenRecord(String firstName, String lastName, String dob) {
        // This method would check if there's already a citizen record
        // based on name and date of birth

        try {
            // Check citizens table for matching records
            java.util.List<Data.Citizen> citizens = Data.Citizen.searchCitizens(firstName + " " + lastName);

            for (Data.Citizen citizen : citizens) {
                if (citizen.getFname().equalsIgnoreCase(firstName) && 
                    citizen.getLname().equalsIgnoreCase(lastName)) {

                    // Check date of birth if provided
                    if (dob != null && citizen.getBirthDate() != null) {
                        // Simple date comparison
                        String citizenDob = citizen.getBirthDate().toString();
                        if (citizenDob.equals(dob)) {
                            return true;
                        }
                    } else {
                        return true; // Name match found
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking existing citizen: " + e.getMessage());
        }

        return false;
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
            if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
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
        PhoneNumberText.clear();
        StreetAddressText.clear();
        BarangayAddressText.clear(); // NEW: Clear barangay field
        CityText.clear();
        StateProvinceText.clear();
        ZipPostalCodeTextField.clear();
        CountryText.clear();
        UsernameText.clear();
        PasswordText.setText(""); // Changed from clear()
        ConfirmPasswordText.setText(""); // Changed from clear()
        DateOfBirthcustomDatePicker.setDate(null);
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
    
    public CustomTextField getUsernameText() {
        return UsernameText;
    }
    
    public CustomPasswordField getPasswordText() {
        return PasswordText;
    }

    public CustomPasswordField getConfirmPasswordText() {
        return ConfirmPasswordText;
    }
    
    public String getRawTransactionReferenceNumber() {
        return TransactionReferenceNumberText.getText().trim();
    }
    
    public CustomTextField getTransactionReferenceNumberText() {
        return TransactionReferenceNumberText;
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
    
    public CustomTextField getPhoneNumberText() {
        return PhoneNumberText;
    }
    
    public CustomTextField getStreetAddressText() {
        return StreetAddressText;
    }
    
    public CustomTextField getBarangayAddressText() {
        return BarangayAddressText;
    }
    
    public CustomTextField getCityText() {
        return CityText;
    }
    
    public CustomTextField getStateProvinceText() {
        return StateProvinceText;
    }
    
    public CustomTextField getZipPostalCodeTextField() {
        return ZipPostalCodeTextField;
    }
    
    public CustomTextField getCountryText() {
        return CountryText;
    }
    
    public component.CustomDatePicker.CustomDatePicker getDateOfBirthcustomDatePicker() {
        return DateOfBirthcustomDatePicker;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private sys.main.CustomTextField BarangayAddressText;
    private sys.main.CustomTextField CityText;
    private sys.main.CustomPasswordField ConfirmPasswordText;
    private sys.main.CustomTextField CountryText;
    private component.Button.FlatButton CreateAccountButton;
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
    private sys.main.CustomTextField TransactionReferenceNumberText;
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
