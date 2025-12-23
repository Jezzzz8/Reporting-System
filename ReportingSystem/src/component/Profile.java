package component;

import backend.objects.Data.User;
import backend.objects.Data.Citizen;
import backend.objects.Data.Appointment;
import backend.objects.Data.ActivityLog;
import backend.objects.Data.Address; // NEW: Import Address class
import java.awt.Color;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Profile extends javax.swing.JPanel {
    private User currentUser;
    private Citizen citizenInfo;
    private backend.objects.Data.IDStatus idStatus;
    private Appointment appointment;
    private Address addressInfo; // NEW: Store address separately
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    private SimpleDateFormat fullDateFormat = new SimpleDateFormat("MMMM dd, yyyy, hh:mm a");
    
    public Profile(User user) {
        this.currentUser = user;
        initComponents();
        loadUserData();  // Load data FIRST
        setupDateDisplay();
        setReadOnlyFields();
        setupCustomFields();
        setupGenderDropdown();  // Now citizenInfo is loaded
    }
    
    private void setupDateDisplay() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        
        String day = dayFormat.format(calendar.getTime());
        String date = dateFormat.format(calendar.getTime());
        TodayDate.setText(day + " " + date);
        
        WelcomeLabel.setText("Welcome, " + currentUser.getFullName());
    }
    
    private void setupCustomFields() {
        // Set max lengths for text fields
        FirstnameTextField.setMaxLength(50);
        MiddlenameTextField.setMaxLength(50);
        LastnameTextField.setMaxLength(50);
        NationalIDTextField.setMaxLength(20);
        EmailAddressTextField.setMaxLength(100);
        PhoneNumberTextField.setMaxLength(15);
        CityTextField.setMaxLength(50);
        StateProvinceTextField.setMaxLength(50);
        ZIPPostalCodeTextField.setMaxLength(10);
        CountryTextField.setMaxLength(50);
        UsernameTextField.setMaxLength(50);
        StreetAddressTextField.setMaxLength(200); // NEW: Street address field
        
        PasswordField.disablePasswordVisibilityToggle();
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
    
    private void loadUserData() {
        // Load citizen data
        citizenInfo = Citizen.getCitizenByUserId(currentUser.getUserId());

        if (citizenInfo != null) {
            // Load ID status
            idStatus = backend.objects.Data.IDStatus.getStatusByCitizenId(citizenInfo.getCitizenId());

            // Load appointment
            appointment = Appointment.getAppointmentByCitizenId(citizenInfo.getCitizenId());

            // Load address information
            addressInfo = Address.getAddressByCitizenId(citizenInfo.getCitizenId());

            // Populate personal details
            FirstnameTextField.setText(citizenInfo.getFname());
            MiddlenameTextField.setText(citizenInfo.getMname() != null ? citizenInfo.getMname() : "");
            LastnameTextField.setText(citizenInfo.getLname());
            NationalIDTextField.setText(citizenInfo.getNationalId() != null ? citizenInfo.getNationalId() : "");

            // Set date of birth
            if (citizenInfo.getBirthDate() != null) {
                DateofBirthPicker.setDate(citizenInfo.getBirthDate());
            }

            // Set gender
            if (citizenInfo.getGender() != null) {
                GenderDropdownButton.setText(citizenInfo.getGender());
            } else {
                GenderDropdownButton.setText("Select Gender");
            }

            // Populate contact details
            EmailAddressTextField.setText(citizenInfo.getEmail() != null ? citizenInfo.getEmail() : "");
            PhoneNumberTextField.setText(citizenInfo.getPhone() != null ? citizenInfo.getPhone() : "");

            // Populate address details
            if (addressInfo != null) {
                StreetAddressTextField.setText(addressInfo.getStreetAddress() != null ? addressInfo.getStreetAddress() : "");
                BarangayAddressTextField.setText(addressInfo.getBarangay() != null ? addressInfo.getBarangay() : "");
                CityTextField.setText(addressInfo.getCity() != null ? addressInfo.getCity() : "");
                StateProvinceTextField.setText(addressInfo.getStateProvince() != null ? addressInfo.getStateProvince() : "");
                ZIPPostalCodeTextField.setText(addressInfo.getZipPostalCode() != null ? addressInfo.getZipPostalCode() : "");
                CountryTextField.setText(addressInfo.getCountry() != null ? addressInfo.getCountry() : "");

                // Build address line from components
                StringBuilder addressLine = new StringBuilder();
                if (addressInfo.getStreetAddress() != null && !addressInfo.getStreetAddress().isEmpty()) {
                    addressLine.append(addressInfo.getStreetAddress());
                }
                if (addressInfo.getBarangay() != null && !addressInfo.getBarangay().isEmpty()) {
                    if (addressLine.length() > 0) addressLine.append(", ");
                    addressLine.append(addressInfo.getBarangay());
                }
                if (addressInfo.getCity() != null && !addressInfo.getCity().isEmpty()) {
                    if (addressLine.length() > 0) addressLine.append(", ");
                    addressLine.append(addressInfo.getCity());
                }
                if (addressInfo.getStateProvince() != null && !addressInfo.getStateProvince().isEmpty()) {
                    if (addressLine.length() > 0) addressLine.append(", ");
                    addressLine.append(addressInfo.getStateProvince());
                }
                if (addressInfo.getZipPostalCode() != null && !addressInfo.getZipPostalCode().isEmpty()) {
                    if (addressLine.length() > 0) addressLine.append(" ");
                    addressLine.append(addressInfo.getZipPostalCode());
                }
                if (addressInfo.getCountry() != null && !addressInfo.getCountry().isEmpty()) {
                    if (addressLine.length() > 0) addressLine.append(", ");
                    addressLine.append(addressInfo.getCountry());
                }
                AddressLineTextArea.setText(addressLine.toString());
            }

            // Populate ID application status
            if (idStatus != null) {
                // Get transaction ID directly from the IDStatus object
                String transactionId = idStatus.getTransactionId();

                if (transactionId != null && !transactionId.trim().isEmpty()) {
                    // Format the transaction ID for display
                    String formattedTransactionId = backend.objects.Data.IDStatus.formatTransactionId(transactionId);
                    TransactionIDTextField.setText(formattedTransactionId);
                } else {
                    TransactionIDTextField.setText("No Transaction ID Assigned");
                }

                // Get status from the linked status_names table
                if (idStatus.getStatus() != null) {
                    StatusTextField.setText(idStatus.getStatus());
                } else {
                    StatusTextField.setText("No Status Available");
                }

                // Set applied date from citizen record
                if (citizenInfo.getApplicationDate() != null) {
                    AppliedDateTextField.setText(dateFormat.format(citizenInfo.getApplicationDate()));
                } else {
                    AppliedDateTextField.setText("Not Available");
                }

                // Calculate estimated completion date (45 days after application)
                if (citizenInfo.getApplicationDate() != null) {
                    Calendar estimatedCal = Calendar.getInstance();
                    estimatedCal.setTime(citizenInfo.getApplicationDate());
                    estimatedCal.add(Calendar.DAY_OF_MONTH, 45);
                    EstimatedDateTextField.setText(dateFormat.format(estimatedCal.getTime()));
                } else {
                    EstimatedDateTextField.setText("Not Available");
                }

                // Get next step based on current status
                String nextStep = getNextStep(idStatus.getStatus());
                NextStepTextField.setText(nextStep);
            } else {
                // If no status record exists, show default values
                TransactionIDTextField.setText("No Application Submitted");
                StatusTextField.setText("No Application");
                AppliedDateTextField.setText("Not Available");
                EstimatedDateTextField.setText("Not Available");
                NextStepTextField.setText("Submit Application");
            }

            // Populate appointment details
            if (appointment != null) {
                String appointmentText = dateFormat.format(appointment.getAppDate()) + ", " + appointment.getAppTime();
                AppointmentDateTextField.setText(appointmentText);
            } else {
                AppointmentDateTextField.setText("No Appointment Scheduled");
            }
        } else {
            // If no citizen info exists, show default values
            TransactionIDTextField.setText("No Citizen Record");
            StatusTextField.setText("Not Available");
            AppliedDateTextField.setText("Not Available");
            EstimatedDateTextField.setText("Not Available");
            NextStepTextField.setText("Complete Profile");
            AppointmentDateTextField.setText("Not Available");
        }

        // Set account details
        UsernameTextField.setText(currentUser.getUsername());
        PasswordField.setText(currentUser.getPassword());

        // Set read-only password field appearance
        PasswordField.setEnabled(false);
        PasswordField.setPlaceholder("********");
    }
    
    private String getNextStep(String status) {
        if (status == null) return "Application Submitted";
        
        switch(status.toLowerCase()) {
            case "submitted":
                return "Document Verification";
            case "processing":
                return "Biometrics Appointment";
            case "biometrics completed":
                return "Background Check";
            case "background check completed":
                return "ID Card Production";
            case "production":
                return "Ready for Pickup/Delivery";
            case "ready for pickup":
                return "Pick Up ID Card";
            case "completed":
                return "Application Complete";
            default:
                return "Application Submitted";
        }
    }
    
    private void setReadOnlyFields() {
        // Make these fields read-only
        StatusTextField.setEnabled(false);
        AppliedDateTextField.setEnabled(false);
        EstimatedDateTextField.setEnabled(false);
        NextStepTextField.setEnabled(false);
        AppointmentDateTextField.setEnabled(false);
        UsernameTextField.setEnabled(false);
        PasswordField.setEnabled(false);
        
        // Style read-only fields
        Color disabledBgColor = new Color(255, 255, 255);
        StatusTextField.setBackground(disabledBgColor);
        AppliedDateTextField.setBackground(disabledBgColor);
        EstimatedDateTextField.setBackground(disabledBgColor);
        NextStepTextField.setBackground(disabledBgColor);
        AppointmentDateTextField.setBackground(disabledBgColor);
        UsernameTextField.setBackground(disabledBgColor);
        PasswordField.setBackground(disabledBgColor);
    }
    
    private boolean validatePersonalDetails() {
        if (FirstnameTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "First name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            FirstnameTextField.requestFocus();
            return false;
        }
        
        if (LastnameTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Last name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            LastnameTextField.requestFocus();
            return false;
        }
        
        if (DateofBirthPicker.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Date of birth is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Check if gender is selected
        String selectedGender = GenderDropdownButton.getText();
        if (selectedGender == null || selectedGender.isEmpty() || selectedGender.equals("Select Gender")) {
            JOptionPane.showMessageDialog(this, "Please select a gender.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private boolean validateContactDetails() {
        String email = EmailAddressTextField.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email address is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            EmailAddressTextField.requestFocus();
            return false;
        }

        // Simple email validation
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            EmailAddressTextField.requestFocus();
            return false;
        }

        String phone = PhoneNumberTextField.getText().trim();
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone number is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            PhoneNumberTextField.requestFocus();
            return false;
        }

        // Basic phone number validation
        if (!phone.matches("^[0-9\\s\\-()+]*$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid phone number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            PhoneNumberTextField.requestFocus();
            return false;
        }

        // Validate address fields - UPDATED
        String streetAddress = StreetAddressTextField.getText().trim();
        if (streetAddress.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Street address is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            StreetAddressTextField.requestFocus();
            return false;
        }

        String barangay = BarangayAddressTextField.getText().trim(); // NEW
        if (barangay.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Barangay is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            BarangayAddressTextField.requestFocus();
            return false;
        }

        String city = CityTextField.getText().trim();
        if (city.isEmpty()) {
            JOptionPane.showMessageDialog(this, "City is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            CityTextField.requestFocus();
            return false;
        }

        String stateProvince = StateProvinceTextField.getText().trim();
        if (stateProvince.isEmpty()) {
            JOptionPane.showMessageDialog(this, "State/Province is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            StateProvinceTextField.requestFocus();
            return false;
        }

        String zipCode = ZIPPostalCodeTextField.getText().trim();
        if (zipCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ZIP/Postal Code is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            ZIPPostalCodeTextField.requestFocus();
            return false;
        }

        String country = CountryTextField.getText().trim();
        if (country.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Country is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            CountryTextField.requestFocus();
            return false;
        }

        return true;
    }
    
    private boolean validatePasswordChange() {
        String currentPassword = new String(CurrentPasswordField.getPassword());
        String newPassword = new String(NewPasswordField.getPassword());
        String confirmPassword = new String(ConfirmPasswordField.getPassword());
        
        if (currentPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Current password is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            CurrentPasswordField.requestFocus();
            return false;
        }
        
        // Verify current password
        if (!currentUser.getPassword().equals(currentPassword)) {
            JOptionPane.showMessageDialog(this, "Current password is incorrect.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            CurrentPasswordField.requestFocus();
            CurrentPasswordField.setText("");
            return false;
        }
        
        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "New password is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            NewPasswordField.requestFocus();
            return false;
        }
        
        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "New password must be at least 6 characters long.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            NewPasswordField.requestFocus();
            return false;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "New password and confirmation do not match.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            ConfirmPasswordField.requestFocus();
            ConfirmPasswordField.setText("");
            return false;
        }
        
        return true;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ProfileHeader = new javax.swing.JPanel();
        WelcomeLabel = new javax.swing.JLabel();
        TodayDate = new javax.swing.JLabel();
        MainProfile = new javax.swing.JPanel();
        MainProfileContentTabbedPane = new component.NoTabJTabbedPane();
        customScrollPane1 = new component.Scroll.CustomScrollPane();
        PersonalInformationPanel = new javax.swing.JPanel();
        PersonalInformationCard = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        GenderDropdownButton = new component.DropdownButton.CustomDropdownButton();
        DateofBirthPicker = new component.CustomDatePicker.CustomDatePicker();
        jLabel5 = new javax.swing.JLabel();
        MiddlenameTextField = new sys.main.CustomTextField();
        jLabel3 = new javax.swing.JLabel();
        FirstnameTextField = new sys.main.CustomTextField();
        jLabel2 = new javax.swing.JLabel();
        LastnameTextField = new sys.main.CustomTextField();
        jLabel6 = new javax.swing.JLabel();
        NationalIDTextField = new sys.main.CustomTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        UpdatePersonalDetailsButton = new javax.swing.JButton();
        ContactInformationCard = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        PhoneNumberTextField = new sys.main.CustomTextField();
        StateProvinceTextField = new sys.main.CustomTextField();
        jLabel10 = new javax.swing.JLabel();
        EmailAddressTextField = new sys.main.CustomTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        CityTextField = new sys.main.CustomTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        ZIPPostalCodeTextField = new sys.main.CustomTextField();
        CountryTextField = new sys.main.CustomTextField();
        jLabel17 = new javax.swing.JLabel();
        UpdateContactDetailsButton = new javax.swing.JButton();
        AddressLineTextArea = new component.CustomTextArea.CustomTextArea();
        jLabel16 = new javax.swing.JLabel();
        StreetAddressTextField = new sys.main.CustomTextField();
        BarangayAddressTextField = new sys.main.CustomTextField();
        jLabel20 = new javax.swing.JLabel();
        AccountSecurityCard = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        UpdatePasswordButton = new javax.swing.JButton();
        CurrentPasswordField = new sys.main.CustomPasswordField();
        ConfirmPasswordField = new sys.main.CustomPasswordField();
        jLabel35 = new javax.swing.JLabel();
        UsernameTextField = new sys.main.CustomTextField();
        PasswordField = new sys.main.CustomPasswordField();
        jLabel37 = new javax.swing.JLabel();
        NewPasswordField = new sys.main.CustomPasswordField();
        IDApplicationStatusCard = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        RescheduleAppointmentButton = new javax.swing.JButton();
        TransactionIDTextField = new sys.main.CustomTextField();
        StatusTextField = new sys.main.CustomTextField();
        AppliedDateTextField = new sys.main.CustomTextField();
        EstimatedDateTextField = new sys.main.CustomTextField();
        NextStepTextField = new sys.main.CustomTextField();
        AppointmentDateTextField = new sys.main.CustomTextField();

        setBackground(new java.awt.Color(250, 250, 250));
        setPreferredSize(new java.awt.Dimension(850, 550));

        ProfileHeader.setBackground(new java.awt.Color(204, 204, 204));
        ProfileHeader.setPreferredSize(new java.awt.Dimension(250, 60));

        WelcomeLabel.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        WelcomeLabel.setForeground(new java.awt.Color(0, 120, 215));
        WelcomeLabel.setText("Welcome, User");

        TodayDate.setText("Mon 15 December 2025");

        javax.swing.GroupLayout ProfileHeaderLayout = new javax.swing.GroupLayout(ProfileHeader);
        ProfileHeader.setLayout(ProfileHeaderLayout);
        ProfileHeaderLayout.setHorizontalGroup(
            ProfileHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProfileHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ProfileHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(WelcomeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TodayDate))
                .addContainerGap(344, Short.MAX_VALUE))
        );
        ProfileHeaderLayout.setVerticalGroup(
            ProfileHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProfileHeaderLayout.createSequentialGroup()
                .addComponent(WelcomeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TodayDate)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        MainProfile.setBackground(new java.awt.Color(142, 217, 255));

        MainProfileContentTabbedPane.setBackground(new java.awt.Color(255, 255, 255));
        MainProfileContentTabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        MainProfileContentTabbedPane.setPreferredSize(new java.awt.Dimension(830, 440));

        PersonalInformationCard.setBackground(new java.awt.Color(255, 255, 255));
        PersonalInformationCard.setPreferredSize(new java.awt.Dimension(400, 500));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Date of Birth:");
        jLabel4.setPreferredSize(new java.awt.Dimension(120, 40));

        GenderDropdownButton.setPlaceholder("Select Gender");

        DateofBirthPicker.setDate(null);
        DateofBirthPicker.setPlaceholder("Date of Birth");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Gender:");
        jLabel5.setPreferredSize(new java.awt.Dimension(120, 40));

        MiddlenameTextField.setPlaceholder("Middle Name (Optional)");
        MiddlenameTextField.setPreferredSize(new java.awt.Dimension(250, 40));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Last Name:");
        jLabel3.setPreferredSize(new java.awt.Dimension(120, 40));

        FirstnameTextField.setPlaceholder("First Name");
        FirstnameTextField.setPreferredSize(new java.awt.Dimension(250, 40));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Middle Name:");
        jLabel2.setPreferredSize(new java.awt.Dimension(120, 40));

        LastnameTextField.setPlaceholder("Last Name");
        LastnameTextField.setPreferredSize(new java.awt.Dimension(250, 40));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("National ID:");
        jLabel6.setPreferredSize(new java.awt.Dimension(120, 40));

        NationalIDTextField.setPlaceholder("National ID (Optional)");
        NationalIDTextField.setPreferredSize(new java.awt.Dimension(250, 40));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("First Name:");
        jLabel1.setPreferredSize(new java.awt.Dimension(120, 40));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 120, 215));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Personal Details");
        jLabel7.setPreferredSize(new java.awt.Dimension(400, 40));

        UpdatePersonalDetailsButton.setBackground(new java.awt.Color(0, 120, 215));
        UpdatePersonalDetailsButton.setForeground(new java.awt.Color(255, 255, 255));
        UpdatePersonalDetailsButton.setText("Update Details");
        UpdatePersonalDetailsButton.setBorder(null);
        UpdatePersonalDetailsButton.setBorderPainted(false);
        UpdatePersonalDetailsButton.setPreferredSize(new java.awt.Dimension(150, 45));
        UpdatePersonalDetailsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpdatePersonalDetailsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PersonalInformationCardLayout = new javax.swing.GroupLayout(PersonalInformationCard);
        PersonalInformationCard.setLayout(PersonalInformationCardLayout);
        PersonalInformationCardLayout.setHorizontalGroup(
            PersonalInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PersonalInformationCardLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(PersonalInformationCardLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(PersonalInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PersonalInformationCardLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(FirstnameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE))
                    .addGroup(PersonalInformationCardLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(MiddlenameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(PersonalInformationCardLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(LastnameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(PersonalInformationCardLayout.createSequentialGroup()
                        .addGroup(PersonalInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(PersonalInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(DateofBirthPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(GenderDropdownButton, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(PersonalInformationCardLayout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(NationalIDTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(PersonalInformationCardLayout.createSequentialGroup()
                        .addComponent(UpdatePersonalDetailsButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        PersonalInformationCardLayout.setVerticalGroup(
            PersonalInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PersonalInformationCardLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PersonalInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FirstnameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PersonalInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MiddlenameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PersonalInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LastnameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PersonalInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateofBirthPicker, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PersonalInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(GenderDropdownButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PersonalInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NationalIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(UpdatePersonalDetailsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        ContactInformationCard.setBackground(new java.awt.Color(255, 255, 255));
        ContactInformationCard.setPreferredSize(new java.awt.Dimension(400, 520));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("City:");
        jLabel8.setPreferredSize(new java.awt.Dimension(120, 40));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("State/Province:");
        jLabel9.setPreferredSize(new java.awt.Dimension(120, 40));

        PhoneNumberTextField.setPlaceholder("Phone Number");
        PhoneNumberTextField.setPreferredSize(new java.awt.Dimension(250, 40));

        StateProvinceTextField.setPlaceholder("State/Province");
        StateProvinceTextField.setPreferredSize(new java.awt.Dimension(250, 40));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Address Line:");
        jLabel10.setPreferredSize(new java.awt.Dimension(120, 40));

        EmailAddressTextField.setPlaceholder("Email Address");
        EmailAddressTextField.setPreferredSize(new java.awt.Dimension(250, 40));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setText("Phone Number:");
        jLabel11.setPreferredSize(new java.awt.Dimension(120, 40));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setText("Email Address:");
        jLabel13.setPreferredSize(new java.awt.Dimension(120, 40));

        CityTextField.setPlaceholder("City");
        CityTextField.setPreferredSize(new java.awt.Dimension(250, 40));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 120, 215));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Contact Details");
        jLabel14.setPreferredSize(new java.awt.Dimension(400, 40));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel15.setText("ZIP/Postal Code:");
        jLabel15.setPreferredSize(new java.awt.Dimension(120, 40));

        ZIPPostalCodeTextField.setPlaceholder("Zip/PostalCode");
        ZIPPostalCodeTextField.setPreferredSize(new java.awt.Dimension(250, 40));

        CountryTextField.setPlaceholder("Country");
        CountryTextField.setPreferredSize(new java.awt.Dimension(250, 40));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel17.setText("Country:");
        jLabel17.setPreferredSize(new java.awt.Dimension(120, 40));

        UpdateContactDetailsButton.setBackground(new java.awt.Color(0, 120, 215));
        UpdateContactDetailsButton.setForeground(new java.awt.Color(255, 255, 255));
        UpdateContactDetailsButton.setText("Update Details");
        UpdateContactDetailsButton.setBorder(null);
        UpdateContactDetailsButton.setBorderPainted(false);
        UpdateContactDetailsButton.setPreferredSize(new java.awt.Dimension(150, 45));
        UpdateContactDetailsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpdateContactDetailsButtonActionPerformed(evt);
            }
        });

        AddressLineTextArea.setPlaceholder("Street, City, State/Province, ZIP/Postal Code, Country");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel16.setText("Street:");
        jLabel16.setPreferredSize(new java.awt.Dimension(120, 40));

        StreetAddressTextField.setPlaceholder("Street");
        StreetAddressTextField.setPreferredSize(new java.awt.Dimension(250, 40));

        BarangayAddressTextField.setPlaceholder("Barangay");
        BarangayAddressTextField.setPreferredSize(new java.awt.Dimension(250, 40));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel20.setText("Street:");
        jLabel20.setPreferredSize(new java.awt.Dimension(120, 40));

        javax.swing.GroupLayout ContactInformationCardLayout = new javax.swing.GroupLayout(ContactInformationCard);
        ContactInformationCard.setLayout(ContactInformationCardLayout);
        ContactInformationCardLayout.setHorizontalGroup(
            ContactInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContactInformationCardLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(ContactInformationCardLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(ContactInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ContactInformationCardLayout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(CityTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE))
                    .addGroup(ContactInformationCardLayout.createSequentialGroup()
                        .addGroup(ContactInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, 0)
                        .addGroup(ContactInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(StateProvinceTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ZIPPostalCodeTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(CountryTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(ContactInformationCardLayout.createSequentialGroup()
                        .addComponent(UpdateContactDetailsButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(ContactInformationCardLayout.createSequentialGroup()
                        .addGroup(ContactInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(ContactInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PhoneNumberTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(EmailAddressTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(AddressLineTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(StreetAddressTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(BarangayAddressTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        ContactInformationCardLayout.setVerticalGroup(
            ContactInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContactInformationCardLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ContactInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EmailAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(ContactInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PhoneNumberTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(ContactInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AddressLineTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(ContactInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ContactInformationCardLayout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ContactInformationCardLayout.createSequentialGroup()
                        .addComponent(StreetAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BarangayAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(ContactInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(ContactInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(StateProvinceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(ContactInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ZIPPostalCodeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(ContactInformationCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CountryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(UpdateContactDetailsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        AccountSecurityCard.setBackground(new java.awt.Color(255, 255, 255));
        AccountSecurityCard.setPreferredSize(new java.awt.Dimension(400, 520));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setText("Confirm Password:");
        jLabel12.setPreferredSize(new java.awt.Dimension(120, 40));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel18.setText("New Password:");
        jLabel18.setPreferredSize(new java.awt.Dimension(120, 40));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel19.setText("Current Password:");
        jLabel19.setPreferredSize(new java.awt.Dimension(120, 40));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel21.setText("Password:");
        jLabel21.setPreferredSize(new java.awt.Dimension(120, 40));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 120, 215));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("Account & Security");
        jLabel22.setPreferredSize(new java.awt.Dimension(400, 40));

        UpdatePasswordButton.setBackground(new java.awt.Color(0, 120, 215));
        UpdatePasswordButton.setForeground(new java.awt.Color(255, 255, 255));
        UpdatePasswordButton.setText("Update Password");
        UpdatePasswordButton.setBorder(null);
        UpdatePasswordButton.setBorderPainted(false);
        UpdatePasswordButton.setPreferredSize(new java.awt.Dimension(150, 45));
        UpdatePasswordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpdatePasswordButtonActionPerformed(evt);
            }
        });

        CurrentPasswordField.setPlaceholder("Current Password");

        ConfirmPasswordField.setPlaceholder("Confirm Password");

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel35.setText("Username:");
        jLabel35.setPreferredSize(new java.awt.Dimension(120, 40));

        UsernameTextField.setPlaceholder("Username");
        UsernameTextField.setPreferredSize(new java.awt.Dimension(250, 40));

        PasswordField.setPlaceholder("Password");

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel37.setText("Change Password:");
        jLabel37.setPreferredSize(new java.awt.Dimension(120, 40));

        NewPasswordField.setPlaceholder("New Password");

        javax.swing.GroupLayout AccountSecurityCardLayout = new javax.swing.GroupLayout(AccountSecurityCard);
        AccountSecurityCard.setLayout(AccountSecurityCardLayout);
        AccountSecurityCardLayout.setHorizontalGroup(
            AccountSecurityCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AccountSecurityCardLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(AccountSecurityCardLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(AccountSecurityCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AccountSecurityCardLayout.createSequentialGroup()
                        .addComponent(UpdatePasswordButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(AccountSecurityCardLayout.createSequentialGroup()
                        .addGroup(AccountSecurityCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(AccountSecurityCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CurrentPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(PasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(AccountSecurityCardLayout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(ConfirmPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(AccountSecurityCardLayout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(NewPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(AccountSecurityCardLayout.createSequentialGroup()
                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(UsernameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)))
                .addContainerGap())
        );
        AccountSecurityCardLayout.setVerticalGroup(
            AccountSecurityCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AccountSecurityCardLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AccountSecurityCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UsernameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addGroup(AccountSecurityCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AccountSecurityCardLayout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE))
                    .addGroup(AccountSecurityCardLayout.createSequentialGroup()
                        .addComponent(PasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(76, 76, 76)))
                .addGroup(AccountSecurityCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(CurrentPasswordField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addGroup(AccountSecurityCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NewPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(AccountSecurityCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ConfirmPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(UpdatePasswordButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        IDApplicationStatusCard.setBackground(new java.awt.Color(255, 255, 255));
        IDApplicationStatusCard.setPreferredSize(new java.awt.Dimension(400, 520));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel23.setText("Estimated Date:");
        jLabel23.setPreferredSize(new java.awt.Dimension(120, 40));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel24.setText("Next Step:");
        jLabel24.setPreferredSize(new java.awt.Dimension(120, 40));

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel25.setText(" Applied Date:");
        jLabel25.setPreferredSize(new java.awt.Dimension(120, 40));

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel26.setText("Status:");
        jLabel26.setPreferredSize(new java.awt.Dimension(120, 40));

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel27.setText("Transaction ID:");
        jLabel27.setPreferredSize(new java.awt.Dimension(120, 40));

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(0, 120, 215));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("ID Application Status");
        jLabel28.setPreferredSize(new java.awt.Dimension(400, 40));

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel29.setText("Appointment Date");
        jLabel29.setPreferredSize(new java.awt.Dimension(120, 40));

        RescheduleAppointmentButton.setBackground(new java.awt.Color(0, 120, 215));
        RescheduleAppointmentButton.setForeground(new java.awt.Color(255, 255, 255));
        RescheduleAppointmentButton.setText("Reschedule Appointment");
        RescheduleAppointmentButton.setBorder(null);
        RescheduleAppointmentButton.setBorderPainted(false);
        RescheduleAppointmentButton.setPreferredSize(new java.awt.Dimension(150, 45));
        RescheduleAppointmentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RescheduleAppointmentButtonActionPerformed(evt);
            }
        });

        TransactionIDTextField.setFocusable(false);
        TransactionIDTextField.setPlaceholder("Transaction ID");
        TransactionIDTextField.setPreferredSize(new java.awt.Dimension(250, 40));

        StatusTextField.setPlaceholder("Status");
        StatusTextField.setPreferredSize(new java.awt.Dimension(250, 40));

        AppliedDateTextField.setPlaceholder("Applied Date");
        AppliedDateTextField.setPreferredSize(new java.awt.Dimension(250, 40));

        EstimatedDateTextField.setPlaceholder("Estimated Date");
        EstimatedDateTextField.setPreferredSize(new java.awt.Dimension(250, 40));

        NextStepTextField.setPlaceholder("Next Step");
        NextStepTextField.setPreferredSize(new java.awt.Dimension(250, 40));

        AppointmentDateTextField.setPlaceholder("Appointment Date");
        AppointmentDateTextField.setPreferredSize(new java.awt.Dimension(250, 40));

        javax.swing.GroupLayout IDApplicationStatusCardLayout = new javax.swing.GroupLayout(IDApplicationStatusCard);
        IDApplicationStatusCard.setLayout(IDApplicationStatusCardLayout);
        IDApplicationStatusCardLayout.setHorizontalGroup(
            IDApplicationStatusCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, IDApplicationStatusCardLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(IDApplicationStatusCardLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(IDApplicationStatusCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(IDApplicationStatusCardLayout.createSequentialGroup()
                        .addGroup(IDApplicationStatusCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(IDApplicationStatusCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TransactionIDTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(StatusTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(AppliedDateTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(EstimatedDateTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(NextStepTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(IDApplicationStatusCardLayout.createSequentialGroup()
                        .addComponent(RescheduleAppointmentButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(IDApplicationStatusCardLayout.createSequentialGroup()
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(AppointmentDateTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        IDApplicationStatusCardLayout.setVerticalGroup(
            IDApplicationStatusCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(IDApplicationStatusCardLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(IDApplicationStatusCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TransactionIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(IDApplicationStatusCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(StatusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(IDApplicationStatusCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AppliedDateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(IDApplicationStatusCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EstimatedDateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(IDApplicationStatusCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NextStepTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(IDApplicationStatusCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AppointmentDateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(RescheduleAppointmentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout PersonalInformationPanelLayout = new javax.swing.GroupLayout(PersonalInformationPanel);
        PersonalInformationPanel.setLayout(PersonalInformationPanelLayout);
        PersonalInformationPanelLayout.setHorizontalGroup(
            PersonalInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PersonalInformationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PersonalInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(AccountSecurityCard, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                    .addComponent(PersonalInformationCard, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PersonalInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ContactInformationCard, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                    .addComponent(IDApplicationStatusCard, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE))
                .addContainerGap())
        );
        PersonalInformationPanelLayout.setVerticalGroup(
            PersonalInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PersonalInformationPanelLayout.createSequentialGroup()
                .addGroup(PersonalInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PersonalInformationPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(ContactInformationCard, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE))
                    .addComponent(PersonalInformationCard, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PersonalInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(IDApplicationStatusCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AccountSecurityCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        customScrollPane1.setViewportView(PersonalInformationPanel);

        MainProfileContentTabbedPane.addTab("tab2", customScrollPane1);

        javax.swing.GroupLayout MainProfileLayout = new javax.swing.GroupLayout(MainProfile);
        MainProfile.setLayout(MainProfileLayout);
        MainProfileLayout.setHorizontalGroup(
            MainProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainProfileLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MainProfileContentTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        MainProfileLayout.setVerticalGroup(
            MainProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainProfileLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MainProfileContentTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ProfileHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE)
            .addComponent(MainProfile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(ProfileHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(MainProfile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void RescheduleAppointmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RescheduleAppointmentButtonActionPerformed
        if (citizenInfo == null) {
            JOptionPane.showMessageDialog(this, "No citizen record found. Please update your personal details first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog rescheduleDialog = new JDialog((java.awt.Frame)SwingUtilities.getWindowAncestor(this), "Reschedule Appointment", true);
        rescheduleDialog.setLayout(new java.awt.BorderLayout());
        rescheduleDialog.setSize(400, 300);
        rescheduleDialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(10, 10, 10, 10);
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("Select New Date:"), gbc);
        
        gbc.gridx = 1;
        component.CustomDatePicker.CustomDatePicker newDatePicker = new component.CustomDatePicker.CustomDatePicker("Select date");
        newDatePicker.setPreferredSize(new java.awt.Dimension(200, 40));
        contentPanel.add(newDatePicker, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("Select Time:"), gbc);
        
        gbc.gridx = 1;
        String[] timeSlots = {"09:00 AM", "10:00 AM", "11:00 AM", "02:00 PM", "03:00 PM", "04:00 PM"};
        JComboBox<String> timeComboBox = new JComboBox<>(timeSlots);
        timeComboBox.setPreferredSize(new java.awt.Dimension(200, 40));
        contentPanel.add(timeComboBox, gbc);
        
        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton scheduleButton = new JButton("Schedule");
        
        cancelButton.addActionListener(e -> rescheduleDialog.dispose());
        scheduleButton.addActionListener(e -> {
            if (newDatePicker.getDate() == null) {
                JOptionPane.showMessageDialog(rescheduleDialog, "Please select a date.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String selectedTime = (String) timeComboBox.getSelectedItem();
            
            try {
                Appointment newAppointment = new Appointment();
                newAppointment.setCitizenId(citizenInfo.getCitizenId());
                newAppointment.setAppDate(new java.sql.Date(newDatePicker.getDate().getTime()));
                newAppointment.setAppTime(selectedTime);
                newAppointment.setStatus("Scheduled");
                newAppointment.setCreatedDate(new java.sql.Date(System.currentTimeMillis()));
                
                boolean success;
                if (appointment != null) {
                    newAppointment.setAppointmentId(appointment.getAppointmentId());
                    success = Appointment.updateAppointment(newAppointment);
                } else {
                    success = Appointment.addAppointment(newAppointment);
                }
                
                if (success) {
                    appointment = newAppointment;
                    String appointmentText = dateFormat.format(appointment.getAppDate()) + ", " + appointment.getAppTime();
                    AppointmentDateTextField.setText(appointmentText);
                    
                    ActivityLog.logActivity(currentUser.getUserId(), "Rescheduled appointment to " + appointmentText);
                    
                    JOptionPane.showMessageDialog(rescheduleDialog, "Appointment rescheduled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    rescheduleDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(rescheduleDialog, "Failed to reschedule appointment. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(rescheduleDialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(scheduleButton);
        
        rescheduleDialog.add(contentPanel, java.awt.BorderLayout.CENTER);
        rescheduleDialog.add(buttonPanel, java.awt.BorderLayout.SOUTH);
        rescheduleDialog.setVisible(true);
    }//GEN-LAST:event_RescheduleAppointmentButtonActionPerformed

    private void UpdatePasswordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdatePasswordButtonActionPerformed
        if (!validatePasswordChange()) {
            return;
        }
        
        String newPassword = new String(NewPasswordField.getPassword());
        
        currentUser.setPassword(newPassword);
        boolean success = User.updateUser(currentUser);
        
        if (success) {
            CurrentPasswordField.setText("");
            NewPasswordField.setText("");
            ConfirmPasswordField.setText("");
            
            PasswordField.setText("********");
            
            ActivityLog.logActivity(currentUser.getUserId(), "Changed password");
            
            JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_UpdatePasswordButtonActionPerformed

    private void UpdatePersonalDetailsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdatePersonalDetailsButtonActionPerformed
        if (!validatePersonalDetails()) {
            return;
        }

        try {
            Citizen citizen = new Citizen();

            if (citizenInfo != null) {
                citizen.setCitizenId(citizenInfo.getCitizenId());
                citizen.setUserId(citizenInfo.getUserId());
            } else {
                citizen.setUserId(currentUser.getUserId());
            }

            citizen.setFname(FirstnameTextField.getText().trim());
            citizen.setMname(MiddlenameTextField.getText().trim());
            citizen.setLname(LastnameTextField.getText().trim());
            citizen.setNationalId(NationalIDTextField.getText().trim());
            citizen.setBirthDate(new java.sql.Date(DateofBirthPicker.getDate().getTime()));
            citizen.setGender(GenderDropdownButton.getText());

            if (citizenInfo != null && citizenInfo.getApplicationDate() != null) {
                citizen.setApplicationDate(citizenInfo.getApplicationDate());
            } else {
                citizen.setApplicationDate(new java.sql.Date(System.currentTimeMillis()));
            }

            // Keep existing contact info
            if (citizenInfo != null) {
                citizen.setPhone(citizenInfo.getPhone());
                citizen.setEmail(citizenInfo.getEmail());
            }

            boolean success;
            if (citizenInfo != null) {
                success = Citizen.updateCitizen(citizen);
            } else {
                success = Citizen.addCitizen(citizen);
            }

            if (success) {
                citizenInfo = citizen;

                // Create ID status if it doesn't exist
                if (idStatus == null && citizenInfo != null) {
                    backend.objects.Data.IDStatus newStatus = new backend.objects.Data.IDStatus();
                    newStatus.setCitizenId(citizenInfo.getCitizenId());

                    // Generate and set transaction ID - FIXED
                    String transactionId = backend.objects.Data.IDStatus.generateTransactionId(citizenInfo.getCitizenId());
                    newStatus.setTransactionId(transactionId);

                    // Set status name ID - assuming "Submitted" status has ID 1
                    // You might need to adjust this based on your status_names table
                    newStatus.setStatusNameId(1);
                    newStatus.setUpdateDate(new java.sql.Date(System.currentTimeMillis()));
                    newStatus.setNotes("Initial application submitted");

                    if (backend.objects.Data.IDStatus.addStatus(newStatus)) {
                        idStatus = newStatus;

                        // Format and display transaction ID
                        String formattedTransactionId = backend.objects.Data.IDStatus.formatTransactionId(idStatus.getTransactionId());
                        TransactionIDTextField.setText(formattedTransactionId);

                        StatusTextField.setText("Submitted");

                        if (citizenInfo.getApplicationDate() != null) {
                            AppliedDateTextField.setText(dateFormat.format(citizenInfo.getApplicationDate()));
                        }

                        Calendar estimatedCal = Calendar.getInstance();
                        estimatedCal.setTime(citizenInfo.getApplicationDate());
                        estimatedCal.add(Calendar.DAY_OF_MONTH, 45);
                        EstimatedDateTextField.setText(dateFormat.format(estimatedCal.getTime()));

                        String nextStep = getNextStep("Submitted");
                        NextStepTextField.setText(nextStep);
                    }
                }

                WelcomeLabel.setText("Welcome, " + citizenInfo.getFullName());

                ActivityLog.logActivity(currentUser.getUserId(), "Updated personal details");

                JOptionPane.showMessageDialog(this, "Personal details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update personal details. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_UpdatePersonalDetailsButtonActionPerformed

    private void UpdateContactDetailsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateContactDetailsButtonActionPerformed
        if (!validateContactDetails()) {
            return;
        }

        if (citizenInfo == null) {
            JOptionPane.showMessageDialog(this, "Please update your personal details first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Update citizen contact information
            Citizen citizen = new Citizen();
            citizen.setCitizenId(citizenInfo.getCitizenId());
            citizen.setUserId(citizenInfo.getUserId());

            // Copy existing personal details
            citizen.setFname(citizenInfo.getFname());
            citizen.setMname(citizenInfo.getMname());
            citizen.setLname(citizenInfo.getLname());
            citizen.setNationalId(citizenInfo.getNationalId());
            citizen.setBirthDate(citizenInfo.getBirthDate());
            citizen.setGender(citizenInfo.getGender());
            citizen.setApplicationDate(citizenInfo.getApplicationDate());

            // Update contact details
            citizen.setEmail(EmailAddressTextField.getText().trim());
            citizen.setPhone(PhoneNumberTextField.getText().trim());

            boolean citizenSuccess = Citizen.updateCitizen(citizen);

            // Update address information with new fields
            Address address = new Address();

            if (addressInfo != null) {
                address.setAddressId(addressInfo.getAddressId());
            }

            address.setCitizenId(citizenInfo.getCitizenId());
            address.setStreetAddress(StreetAddressTextField.getText().trim());
            address.setBarangay(BarangayAddressTextField.getText().trim()); // NEW
            address.setAddressLine(AddressLineTextArea.getText().trim()); // Use from text area
            address.setCity(CityTextField.getText().trim());
            address.setStateProvince(StateProvinceTextField.getText().trim());
            address.setZipPostalCode(ZIPPostalCodeTextField.getText().trim());
            address.setCountry(CountryTextField.getText().trim());

            boolean addressSuccess;
            if (addressInfo != null) {
                addressSuccess = Address.updateAddress(address);
            } else {
                addressSuccess = Address.addAddress(address);
            }

            if (citizenSuccess && addressSuccess) {
                citizenInfo = citizen;
                addressInfo = address;

                ActivityLog.logActivity(currentUser.getUserId(), "Updated contact details");

                JOptionPane.showMessageDialog(this, "Contact details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update contact details. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_UpdateContactDetailsButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AccountSecurityCard;
    private component.CustomTextArea.CustomTextArea AddressLineTextArea;
    private sys.main.CustomTextField AppliedDateTextField;
    private sys.main.CustomTextField AppointmentDateTextField;
    private sys.main.CustomTextField BarangayAddressTextField;
    private sys.main.CustomTextField CityTextField;
    private sys.main.CustomPasswordField ConfirmPasswordField;
    private javax.swing.JPanel ContactInformationCard;
    private sys.main.CustomTextField CountryTextField;
    private sys.main.CustomPasswordField CurrentPasswordField;
    private component.CustomDatePicker.CustomDatePicker DateofBirthPicker;
    private sys.main.CustomTextField EmailAddressTextField;
    private sys.main.CustomTextField EstimatedDateTextField;
    private sys.main.CustomTextField FirstnameTextField;
    private component.DropdownButton.CustomDropdownButton GenderDropdownButton;
    private javax.swing.JPanel IDApplicationStatusCard;
    private sys.main.CustomTextField LastnameTextField;
    private javax.swing.JPanel MainProfile;
    private component.NoTabJTabbedPane MainProfileContentTabbedPane;
    private sys.main.CustomTextField MiddlenameTextField;
    private sys.main.CustomTextField NationalIDTextField;
    private sys.main.CustomPasswordField NewPasswordField;
    private sys.main.CustomTextField NextStepTextField;
    private sys.main.CustomPasswordField PasswordField;
    private javax.swing.JPanel PersonalInformationCard;
    private javax.swing.JPanel PersonalInformationPanel;
    private sys.main.CustomTextField PhoneNumberTextField;
    private javax.swing.JPanel ProfileHeader;
    private javax.swing.JButton RescheduleAppointmentButton;
    private sys.main.CustomTextField StateProvinceTextField;
    private sys.main.CustomTextField StatusTextField;
    private sys.main.CustomTextField StreetAddressTextField;
    private javax.swing.JLabel TodayDate;
    private sys.main.CustomTextField TransactionIDTextField;
    private javax.swing.JButton UpdateContactDetailsButton;
    private javax.swing.JButton UpdatePasswordButton;
    private javax.swing.JButton UpdatePersonalDetailsButton;
    private sys.main.CustomTextField UsernameTextField;
    private javax.swing.JLabel WelcomeLabel;
    private sys.main.CustomTextField ZIPPostalCodeTextField;
    private component.Scroll.CustomScrollPane customScrollPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    // End of variables declaration//GEN-END:variables
}
