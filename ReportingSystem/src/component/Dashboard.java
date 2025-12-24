package component;

import backend.objects.Data;
import backend.objects.Data.User;
import backend.objects.Data.Citizen;
import backend.objects.Data.IDStatus;
import backend.objects.Data.Appointment;
import backend.objects.Data.Address;
import backend.objects.Data.CitizenInfo;
import static backend.objects.Data.IDStatus.formatTransactionId;
import sys.effect.RippleEffect;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;

public class Dashboard extends javax.swing.JPanel {
    
    private User currentUser;
    private Citizen currentCitizen;
    private Address currentAddress; // ADD THIS FIELD
    private int currentView = 1;
    private Animator glowAnimator;
    private float glowAlpha = 0.0f;
    private boolean isGlowing = false;
    private Color[] boxColors = {
        new Color(254, 161, 156),  // Application Status
        new Color(249, 254, 156),  // Days Since
        new Color(200, 254, 156),  // Appointment
        new Color(156, 200, 254)   // Notifications
    };
    
    public Dashboard(User user) {
        this.currentUser = user;
        initComponents();
        
        // Initialize tables before other setup
        initializeTables();
        
        showApplicationTimelineView();
        initGlowAnimation();
        applyButtonEffects();
        applyBoxEffects();
        testDatabaseConnection();
        enhanceDashboard();
        loadCitizenData();
        loadDashboardData();
        loadApplicationTimelineTable();
        setupSearchFunctionality();
        setupButtonColumn();
        setupButtonBorders();
    }
    
    private void initializeTables() {
        // Initialize all tables with custom configurations
        initApplicationTimelineTable();
        initRequiredDocumentsTable();
        initAppointmentDetailsTable();
        initNotificationsTable();
    }
    
    private void initApplicationTimelineTable() {
        // Configure Application Timeline Table
        ApplicationTimelineTable.setHeaderColor(new Color(180, 220, 255));
        ApplicationTimelineTable.setOddRowColor(new Color(250, 250, 250));
        ApplicationTimelineTable.setEvenRowColor(new Color(240, 248, 255));
        ApplicationTimelineTable.setHoverColor(new Color(230, 240, 255));
        ApplicationTimelineTable.setSelectedRowColor(new Color(41, 128, 185));
        ApplicationTimelineTable.setSelectedRowTextColor(Color.WHITE);
        ApplicationTimelineTable.setNormalTextColor(Color.BLACK);
        ApplicationTimelineTable.setCellFont(new Font("Times New Roman", Font.PLAIN, 12));
        ApplicationTimelineTable.setHeaderFont(new Font("Times New Roman", Font.BOLD, 14));
        ApplicationTimelineTable.setCustomRowHeight(35);
        ApplicationTimelineTable.setShowGrid(true);
        ApplicationTimelineTable.setAutoResize(true, true);
        
        // Ensure scroll pane is created and configured
        ApplicationTimelineTable.createAndConfigureScrollPane();
    }
    
    private void initRequiredDocumentsTable() {
        // Configure Required Documents Table
        RequiredDocumentsTable.setHeaderColor(new Color(180, 220, 255));
        RequiredDocumentsTable.setOddRowColor(new Color(250, 250, 250));
        RequiredDocumentsTable.setEvenRowColor(new Color(240, 248, 255));
        RequiredDocumentsTable.setHoverColor(new Color(230, 240, 255));
        RequiredDocumentsTable.setSelectedRowColor(new Color(41, 128, 185));
        RequiredDocumentsTable.setSelectedRowTextColor(Color.WHITE);
        RequiredDocumentsTable.setNormalTextColor(Color.BLACK);
        RequiredDocumentsTable.setCellFont(new Font("Times New Roman", Font.PLAIN, 12));
        RequiredDocumentsTable.setHeaderFont(new Font("Times New Roman", Font.BOLD, 14));
        RequiredDocumentsTable.setCustomRowHeight(35);
        RequiredDocumentsTable.setShowGrid(true);
        RequiredDocumentsTable.setAutoResize(true, true);
        
        // Ensure scroll pane is created and configured
        RequiredDocumentsTable.createAndConfigureScrollPane();
    }
    
    private void initAppointmentDetailsTable() {
        // Configure Appointment Details Table
        MyAppointmentDetailsTable.setHeaderColor(new Color(180, 220, 255));
        MyAppointmentDetailsTable.setOddRowColor(new Color(250, 250, 250));
        MyAppointmentDetailsTable.setEvenRowColor(new Color(240, 248, 255));
        MyAppointmentDetailsTable.setHoverColor(new Color(230, 240, 255));
        MyAppointmentDetailsTable.setSelectedRowColor(new Color(41, 128, 185));
        MyAppointmentDetailsTable.setSelectedRowTextColor(Color.WHITE);
        MyAppointmentDetailsTable.setNormalTextColor(Color.BLACK);
        MyAppointmentDetailsTable.setCellFont(new Font("Times New Roman", Font.PLAIN, 12));
        MyAppointmentDetailsTable.setHeaderFont(new Font("Times New Roman", Font.BOLD, 14));
        MyAppointmentDetailsTable.setCustomRowHeight(35);
        MyAppointmentDetailsTable.setShowGrid(true);
        MyAppointmentDetailsTable.setAutoResize(true, true);
        
        // Configure the Action column as a button column
        setupButtonColumn();
        
        // Ensure scroll pane is created and configured
        MyAppointmentDetailsTable.createAndConfigureScrollPane();
    }
    
    private void initNotificationsTable() {
        // Configure Notifications Table
        MyNotificationsTable.setHeaderColor(new Color(180, 220, 255));
        MyNotificationsTable.setOddRowColor(new Color(250, 250, 250));
        MyNotificationsTable.setEvenRowColor(new Color(240, 248, 255));
        MyNotificationsTable.setHoverColor(new Color(230, 240, 255));
        MyNotificationsTable.setSelectedRowColor(new Color(41, 128, 185));
        MyNotificationsTable.setSelectedRowTextColor(Color.WHITE);
        MyNotificationsTable.setNormalTextColor(Color.BLACK);
        MyNotificationsTable.setCellFont(new Font("Times New Roman", Font.PLAIN, 12));
        MyNotificationsTable.setHeaderFont(new Font("Times New Roman", Font.BOLD, 14));
        MyNotificationsTable.setCustomRowHeight(35);
        MyNotificationsTable.setShowGrid(true);
        MyNotificationsTable.setAutoResize(true, true);
        
        // Ensure scroll pane is created and configured
        MyNotificationsTable.createAndConfigureScrollPane();
    }
    
    
    private void setupButtonBorders() {
        // Set line borders with same color as background and no rounded corners
        setupLineBorder(MyApplicationStatusActionBtn, new Color(254, 100, 100));
        setupLineBorder(MyAppointmentActionBtn, new Color(249, 200, 100));
        setupLineBorder(DaySinceApplicationActionBtn, new Color(100, 254, 100));
        setupLineBorder(NotificationsActionBtn, new Color(100, 100, 254));
    }

    private void setupLineBorder(JButton button, Color borderColor) {
        button.setBorder(BorderFactory.createLineBorder(borderColor, 0));
        button.setBorderPainted(true);
        button.setFocusPainted(false);
    }
    
    private void initGlowAnimation() {
        glowAnimator = new Animator(1000, new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                glowAlpha = (float) (0.3 + 0.3 * Math.sin(fraction * Math.PI * 2));
                repaint();
            }
        });
        glowAnimator.setResolution(10);
        glowAnimator.setRepeatCount(Animator.INFINITE);
        glowAnimator.start();
    }
    
    private void applyButtonEffects() {
        // Apply enhanced ripple effects to all buttons
        applyEnhancedRippleEffect(MyApplicationStatusActionBtn, new Color(41, 128, 185, 150));
        applyEnhancedRippleEffect(MyAppointmentActionBtn, new Color(41, 128, 185, 150));
        applyEnhancedRippleEffect(DaySinceApplicationActionBtn, new Color(41, 128, 185, 150));
        applyEnhancedRippleEffect(NotificationsActionBtn, new Color(41, 128, 185, 150));
        
        // Apply hover effects
        applyHoverEffect(MyApplicationStatusActionBtn);
        applyHoverEffect(MyAppointmentActionBtn);
        applyHoverEffect(DaySinceApplicationActionBtn);
        applyHoverEffect(NotificationsActionBtn);
        
        // Apply shadow effects
        applyShadowEffect(MyApplicationStatusActionBtn);
        applyShadowEffect(MyAppointmentActionBtn);
        applyShadowEffect(DaySinceApplicationActionBtn);
        applyShadowEffect(NotificationsActionBtn);
    }
    
    private void applyEnhancedRippleEffect(JButton button, Color rippleColor) {
        RippleEffect ripple = new RippleEffect(button);
        ripple.setRippleColor(rippleColor);

        // Set initial foreground color to black (25,25,25)
        button.setForeground(new Color(25, 25, 25));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Change to white when mouse enters
                button.setForeground(new Color(250, 250, 250));
                button.setBorder(null);
                button.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Change back to black when mouse exits
                button.setForeground(new Color(25, 25, 25));
                button.setBorder(null);
                button.repaint();
            }
        });
    }
    
    private void applyHoverEffect(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Keep the animation for background if needed
                Animator animator = new Animator(200, new TimingTargetAdapter() {
                    @Override
                    public void timingEvent(float fraction) {
                        // Optional: you can also animate the background
                        button.repaint();
                    }
                });
                animator.start();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                Animator animator = new Animator(200, new TimingTargetAdapter() {
                    @Override
                    public void timingEvent(float fraction) {
                        button.repaint();
                    }
                });
                animator.start();
            }
        });
    }

    private void applyShadowEffect(JButton button) {
        // This method is now used for line borders
        // The border is already set in setupButtonBorders()
    }
    
    private void applyBoxEffects() {
        // Custom paint for box panels with gradients and effects
        MyApplicationStatusBoxPanel = createGlowingPanel(MyApplicationStatusBoxPanel, 0);
        DaySinceApplicationBoxPanel = createGlowingPanel(DaySinceApplicationBoxPanel, 1);
        MyAppointmentBoxPanel = createGlowingPanel(MyAppointmentBoxPanel, 2);
        NotificationsBoxPanel = createGlowingPanel(NotificationsBoxPanel, 3);
    }
    
    private JPanel createGlowingPanel(JPanel panel, int colorIndex) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                // Enable anti-aliasing
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create gradient background
                Color baseColor = boxColors[colorIndex];
                Color lighterColor = baseColor.brighter().brighter();

                // Main gradient - REMOVE rounded corners (change 20,20 to 0,0)
                GradientPaint gradient = new GradientPaint(
                    0, 0, lighterColor,
                    getWidth(), getHeight(), baseColor
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight()); // Changed from fillRoundRect to fillRect

                // Add subtle inner glow
                if (isGlowing) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, glowAlpha));
                    g2.setColor(Color.WHITE);
                    g2.fillRect(2, 2, getWidth()-4, getHeight()-4); // Changed from fillRoundRect to fillRect
                }

                // Add border with shadow effect - REMOVE rounded corners
                g2.setComposite(AlphaComposite.SrcOver);
                g2.setColor(baseColor.darker());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRect(0, 0, getWidth()-1, getHeight()-1); // Changed from drawRoundRect to drawRect

                // Add inner highlight - REMOVE rounded corners
                g2.setColor(new Color(255, 255, 255, 100));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRect(1, 1, getWidth()-3, getHeight()-3); // Changed from drawRoundRect to drawRect

                g2.dispose();
                super.paintComponent(g);
            }
        };
    }    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Add subtle gradient background
        Color color1 = new Color(25, 25, 25);
        Color color2 = new Color(240, 240, 240);
        GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Add subtle pattern
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
        g2.setColor(Color.GRAY);
        for (int i = 0; i < getWidth(); i += 20) {
            for (int j = 0; j < getHeight(); j += 20) {
                g2.fillOval(i, j, 2, 2);
            }
        }
        
        g2.dispose();
        super.paintComponent(g);
    }
    
    private void loadCitizenData() {
        // Get citizen data for current user
        currentCitizen = Data.Citizen.getCitizenByUserId(currentUser.getUserId());
        
        // Get address data for current citizen
        if (currentCitizen != null) {
            currentAddress = Data.Address.getAddressByCitizenId(currentCitizen.getCitizenId());
        }
        
        if (currentCitizen == null) {
            System.out.println("No citizen record found for user ID: " + currentUser.getUserId());
            // If no citizen record exists, create a default one
            createDefaultCitizenRecord();
        } else {
            System.out.println("Loaded citizen data for: " + currentCitizen.getFullName());
            if (currentAddress != null) {
                System.out.println("Loaded address data: " + currentAddress.getFullAddress());
            }
        }
    }
    
    private void createDefaultCitizenRecord() {
        try {
            // Check if user already has a citizen record
            currentCitizen = Data.Citizen.getCitizenByUserId(currentUser.getUserId());
            if (currentCitizen != null) return;

            // Create a new citizen record for the user
            Data.Citizen newCitizen = new Data.Citizen();
            newCitizen.setUserId(currentUser.getUserId());

            // Set separate name fields instead of fullName
            newCitizen.setFname(currentUser.getFname() != null ? currentUser.getFname() : "");
            newCitizen.setMname(currentUser.getMname());
            newCitizen.setLname(currentUser.getLname() != null ? currentUser.getLname() : "");

            newCitizen.setPhone(currentUser.getPhone());
            newCitizen.setEmail(currentUser.getEmail());
            newCitizen.setApplicationDate(new java.sql.Date(System.currentTimeMillis()));

            // Set default gender if not provided
            newCitizen.setGender("Not Specified");

            // Try to add citizen
            int citizenId = Data.Citizen.addCitizenAndGetId(newCitizen);
            if (citizenId > 0) {
                // Reload citizen data
                currentCitizen = Data.Citizen.getCitizenByUserId(currentUser.getUserId());
                System.out.println("Created default citizen record for user: " + currentUser.getUsername());

                // Create default status with generated transaction ID
                Data.IDStatus defaultStatus = new Data.IDStatus();
                defaultStatus.setCitizenId(citizenId);
                defaultStatus.setTransactionId(Data.IDStatus.generateTransactionId(citizenId));
                // Set status_name_id to 1 for "Submitted" status
                defaultStatus.setStatusNameId(1);
                defaultStatus.setUpdateDate(new java.sql.Date(System.currentTimeMillis()));
                defaultStatus.setNotes("Initial application submitted");
                Data.IDStatus.addStatus(defaultStatus);

                // Create default address
                createDefaultAddress();
            } else {
                System.err.println("Failed to create citizen record");
            }
        } catch (Exception e) {
            System.err.println("Error creating default citizen record: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createDefaultAddress() {
        try {
            if (currentCitizen == null) return;
            
            Data.Address address = new Data.Address();
            address.setCitizenId(currentCitizen.getCitizenId());
            address.setStreetAddress("Not Provided");
            address.setCity("Not Provided");
            address.setStateProvince("Not Provided");
            address.setZipPostalCode("0000");
            address.setCountry("Philippines");
            
            boolean success = Data.Address.addAddress(address);
            if (success) {
                currentAddress = Data.Address.getAddressByCitizenId(currentCitizen.getCitizenId());
                System.out.println("Created default address for citizen ID: " + currentCitizen.getCitizenId());
            }
        } catch (Exception e) {
            System.err.println("Error creating default address: " + e.getMessage());
        }
    }
    
    private void testDatabaseConnection() {
        try {
            System.out.println("Testing database connection for user: " + currentUser.getUsername());

            // Test getting citizens
            List<Citizen> citizens = Data.Citizen.getAllCitizens();
            System.out.println("Total citizens in database: " + (citizens != null ? citizens.size() : 0));

            // Test getting addresses
            int addressCount = 0;
            if (citizens != null) {
                for (Citizen citizen : citizens) {
                    Address addr = Data.Address.getAddressByCitizenId(citizen.getCitizenId());
                    if (addr != null) {
                        addressCount++;
                    }
                }
            }
            System.out.println("Total addresses in database: " + addressCount);

            // Test CitizenInfo class
            if (currentCitizen != null) {
                CitizenInfo info = Data.CitizenInfo.getCitizenInfoByCitizenId(currentCitizen.getCitizenId());
                if (info != null) {
                    System.out.println("CitizenInfo loaded successfully:");
                    System.out.println("  Citizen: " + info.getCitizen().getFullName());
                    System.out.println("  Gender: " + info.getCitizen().getGender());
                    System.out.println("  Address: " + (info.getAddress() != null ? info.getAddress().getFullAddress() : "No address"));
                    System.out.println("  Status: " + (info.getStatus() != null ? info.getStatus().getStatus() : "No status"));
                    System.out.println("  Transaction ID: " + (info.getStatus() != null ? info.getStatus().getTransactionId() : "No transaction ID"));
                } else {
                    System.out.println("Could not load CitizenInfo for citizen ID: " + currentCitizen.getCitizenId());
                }
            }

        } catch (Exception e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }   
    
    private void enhanceDashboard() {
        // Apply ripple effects to buttons
        applyRippleEffects();
        
        // Add action listeners for buttons
        setupButtonActions();
    }

    private void applyRippleEffects() {
        if (MyAppointmentActionBtn != null) {
            RippleEffect approvedRipple = new RippleEffect(MyAppointmentActionBtn);
            approvedRipple.setRippleColor(new Color(255, 255, 255, 100));
        }
        
        if (NotificationsActionBtn != null) {
            RippleEffect readyRipple = new RippleEffect(NotificationsActionBtn);
            readyRipple.setRippleColor(new Color(255, 255, 255, 100));
        }
        
        if (MyApplicationStatusActionBtn != null) {
            RippleEffect pendingRipple = new RippleEffect(MyApplicationStatusActionBtn);
            pendingRipple.setRippleColor(new Color(255, 255, 255, 100));
        }
        
        if (DaySinceApplicationActionBtn != null) {
            RippleEffect urgentRipple = new RippleEffect(DaySinceApplicationActionBtn);
            urgentRipple.setRippleColor(new Color(255, 255, 255, 100));
        }
    }

    private void setupButtonActions() {
        if (MyApplicationStatusActionBtn != null) {
            MyApplicationStatusActionBtn.addActionListener((ActionEvent e) -> {
                showApplicationTimelineView(); // Shows timeline
            });
        }

        if (MyAppointmentActionBtn != null) {
            MyAppointmentActionBtn.addActionListener((ActionEvent e) -> {
                showAppointmentView(); // Shows appointments
            });
        }

        if (DaySinceApplicationActionBtn != null) {
            DaySinceApplicationActionBtn.addActionListener((ActionEvent e) -> {
                showDocumentsView(); // Shows documents
            });
        }

        if (NotificationsActionBtn != null) {
            NotificationsActionBtn.addActionListener((ActionEvent e) -> {
                showNotificationsView(); // Shows notifications
            });
        }
    }
    
    private void setupSearchFunctionality() {
        searchField.setText("Search...");
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        // Add animation to search
        searchField.addActionListener((ActionEvent e) -> {
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty() && !searchTerm.equals("Search...")) {
                // Add search animation
                Animator animator = new Animator(200, new TimingTargetAdapter() {
                    @Override
                    public void timingEvent(float fraction) {
                        searchField.setBackground(new Color(255, 255, 200));
                        searchField.repaint();
                    }

                    @Override
                    public void end() {
                        searchField.setBackground(Color.WHITE);
                    }
                });
                animator.start();

                filterCurrentTable(searchTerm);
            } else {
                reloadCurrentView();
            }
        });
    }

    private void loadDashboardData() {
        try {
            if (currentCitizen == null) {
                setDefaultValues();
                return;
            }

            // Get ID status with proper SQL query
            Data.IDStatus status = Data.IDStatus.getStatusByCitizenId(currentCitizen.getCitizenId());
            String transactionId = "TXN-Not-Assigned";
            String statusText = "Application Submitted";

            if (status != null) {
                // Get raw transaction ID
                String rawTransactionId = status.getTransactionId();
                if (rawTransactionId != null && !rawTransactionId.trim().isEmpty()) {
                    transactionId = Data.IDStatus.formatTransactionId(rawTransactionId);
                }

                // Get status text
                String rawStatus = status.getStatus();
                if (rawStatus != null && !rawStatus.trim().isEmpty()) {
                    statusText = rawStatus;
                }
            }

            // Box 1: My Application Status - Show transaction ID in tooltip or additional label
            MyApplicationStatusValueLabel.setText(statusText);
            MyApplicationStatusTitleLabel.setText("My Application Status");

            // Set tooltip with more details
            StringBuilder tooltip = new StringBuilder();
            tooltip.append("Transaction ID: ").append(transactionId).append("\n");
            tooltip.append("Gender: ").append(currentCitizen.getGender() != null ? currentCitizen.getGender() : "Not specified").append("\n");

            // Add address information if available
            if (currentAddress != null) {
                tooltip.append("Address: ").append(currentAddress.getConciseAddress()).append("\n");
            }

            MyApplicationStatusBoxPanel.setToolTipText(tooltip.toString());

            // Box 2: Days Since Application
            if (currentCitizen.getApplicationDate() != null) {
                long diff = new Date().getTime() - currentCitizen.getApplicationDate().getTime();
                int days = (int) (diff / (1000 * 60 * 60 * 24));
                DaySinceApplicationValueLabel.setText(String.valueOf(days));
                DaySinceApplicationTitleLabel.setText("Days Since Application");
            } else {
                DaySinceApplicationValueLabel.setText("N/A");
            }

            // Box 3: My Appointment
            Data.Appointment appointment = Data.Appointment.getAppointmentByCitizenId(currentCitizen.getCitizenId());
            if (appointment != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
                MyAppointmentCountLabel.setText(sdf.format(appointment.getAppDate()));
                MyAppointmentTitleLabel.setText("My Appointment");
            } else {
                MyAppointmentCountLabel.setText("No");
                MyAppointmentTitleLabel.setText("Appointment");
            }

            // Box 4: Notifications - Use the new Notification class
            int notificationCount = 0;
            try {
                notificationCount = Data.Notification.getUnreadCount(currentCitizen.getCitizenId());
            } catch (Exception e) {
                System.err.println("Error getting notification count: " + e.getMessage());
            }
            NotificationsValueLabel.setText(String.valueOf(notificationCount));
            NotificationsTitleLabel.setText("Notifications");

            // Update search label to include name details
            String shortTransactionId = transactionId;
            if (transactionId.length() > 15) {
                // Show first 4 segments: 1234-5678-9012-3456...
                shortTransactionId = transactionId.substring(0, 19) + "...";
            }

            // Get first and last name safely
            String firstName = (currentCitizen.getFname() != null) ? currentCitizen.getFname() : "";
            String lastName = (currentCitizen.getLname() != null) ? currentCitizen.getLname() : "";
            String gender = (currentCitizen.getGender() != null && !currentCitizen.getGender().equals("Not Specified")) 
                ? "(" + currentCitizen.getGender() + ")" : "";
            String fullNameDisplay = firstName + " " + lastName + " " + gender;

            // Add address info to search label if available
            String addressInfo = "";
            if (currentAddress != null && currentAddress.getConciseAddress() != null 
                && !currentAddress.getConciseAddress().equals("Not Provided")) {
                addressInfo = " | " + currentAddress.getConciseAddress();
            }

            searchLabel.setText(fullNameDisplay.trim() + " | TRN: " + shortTransactionId + addressInfo);

        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
            setDefaultValues();
        }
    }
    
    public static String maskTransactionId(String transactionId) {
        if (transactionId == null || transactionId.isEmpty()) {
            return "TXN-Not-Assigned";
        }

        String formatted = formatTransactionId(transactionId);

        // Mask all but last 4 digits of the last segment: 1234-5678-9012-3456-7890-1234-XX56
        if (formatted.length() >= 8) {
            String[] segments = formatted.split("-");
            if (segments.length >= 7) {
                String lastSegment = segments[6];
                if (lastSegment.length() == 2) {
                    // Show as XX-56 (last 2 digits)
                    return segments[0] + "-" + segments[1] + "-XXXX-XXXX-XXXX-XXXX-XX" + lastSegment;
                }
            }
        }

        return formatted;
    }
    
    private CitizenInfo getCompleteCitizenInfo() {
        if (currentCitizen == null) return null;
        
        // Use the CitizenInfo class from Data.java
        return Data.CitizenInfo.getCitizenInfoByCitizenId(currentCitizen.getCitizenId());
    }
    
    private void loadApplicationTimelineTable() {
        DefaultTableModel model = (DefaultTableModel) ApplicationTimelineTable.getModel();
        model.setRowCount(0);
        currentView = 1;

        try {
            if (currentCitizen == null) {
                model.addRow(new Object[]{"No citizen data available", "", "", "", ""});
                return;
            }

            // Get status history for the current citizen
            List<Data.IDStatus> statusHistory = Data.IDStatus.getStatusHistoryByCitizenId(currentCitizen.getCitizenId());

            if (statusHistory == null || statusHistory.isEmpty()) {
                // Get current status if no history
                Data.IDStatus currentStatus = Data.IDStatus.getStatusByCitizenId(currentCitizen.getCitizenId());
                if (currentStatus != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String date = currentStatus.getUpdateDate() != null ? 
                        dateFormat.format(currentStatus.getUpdateDate()) : "N/A";
                    String status = currentStatus.getStatus() != null ? 
                        currentStatus.getStatus() : "No Status";
                    String transactionId = currentStatus.getTransactionId() != null ? 
                        currentStatus.getTransactionId() : "N/A";

                    model.addRow(new Object[]{
                        date,
                        "Application Status",
                        status,
                        "System",
                        "Transaction: " + transactionId
                    });
                } else {
                    model.addRow(new Object[]{
                        formatDate(new Date()),
                        "Application Submitted",
                        "Pending Review",
                        "System",
                        "Initial application submission"
                    });
                }
            } else {
                // Add all status history entries
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                for (Data.IDStatus status : statusHistory) {
                    String date = status.getUpdateDate() != null ? 
                        dateFormat.format(status.getUpdateDate()) : "N/A";
                    String statusText = status.getStatus() != null ? 
                        status.getStatus() : "No Status";
                    String transactionId = status.getTransactionId() != null ? 
                        status.getTransactionId() : "N/A";
                    String notes = status.getNotes() != null && !status.getNotes().isEmpty() ? 
                        status.getNotes() : "Transaction: " + transactionId;

                    model.addRow(new Object[]{
                        date,
                        "Status Update",
                        statusText,
                        "System",
                        notes
                    });
                }
            }

            // Add application date if available
            if (currentCitizen.getApplicationDate() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                model.addRow(new Object[]{
                    dateFormat.format(currentCitizen.getApplicationDate()),
                    "Application Submitted",
                    "Application received",
                    currentCitizen.getFullName(),
                    "National ID Application"
                });
            }

        } catch (Exception e) {
            System.err.println("Error loading application timeline: " + e.getMessage());
            e.printStackTrace();
            model.addRow(new Object[]{"Error loading data", "Check database connection", e.getMessage(), "", ""});
        } finally {
            model.fireTableDataChanged();

            // Use the CustomTable's built-in methods
            ApplicationTimelineTable.revalidate();
            ApplicationTimelineTable.repaint();

            // Update the scroll pane if needed
            if (ApplicationTimelineTable.isScrollPaneCreated()) {
                ApplicationTimelineTable.getScrollPane().revalidate();
                ApplicationTimelineTable.getScrollPane().repaint();
            }
        }
    }
    
    private List<Data.IDStatus> getStatusHistoryForCitizen(int citizenId) {
        try {
            // Call the getAllStatus() method which should return all status records
            List<Data.IDStatus> allStatuses = Data.IDStatus.getAllStatus();
            List<Data.IDStatus> citizenStatuses = new ArrayList<>();

            if (allStatuses != null) {
                for (Data.IDStatus status : allStatuses) {
                    if (status != null && status.getCitizenId() == citizenId) {
                        citizenStatuses.add(status);
                    }
                }

                // Sort by date (oldest to newest for timeline)
                citizenStatuses.sort((s1, s2) -> {
                    if (s1.getUpdateDate() == null && s2.getUpdateDate() == null) return 0;
                    if (s1.getUpdateDate() == null) return -1;
                    if (s2.getUpdateDate() == null) return 1;
                    return s1.getUpdateDate().compareTo(s2.getUpdateDate());
                });
            }

            return citizenStatuses;
        } catch (Exception e) {
            System.err.println("Error getting status history: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    private void loadRequiredDocumentsTable() {
        DefaultTableModel model = (DefaultTableModel) RequiredDocumentsTable.getModel();
        model.setRowCount(0);
        currentView = 2;

        try {
            if (currentCitizen == null) {
                model.addRow(new Object[]{"No citizen data available", "", "", "", ""});
                return;
            }

            // Get all documents for the citizen
            List<Data.Document> documents = Data.Document.getDocumentsByCitizenId(currentCitizen.getCitizenId());

            if (documents == null || documents.isEmpty()) {
                // Create default documents if none exist
                createDefaultDocuments();
                // Reload documents
                documents = Data.Document.getDocumentsByCitizenId(currentCitizen.getCitizenId());
            }

            if (documents != null && !documents.isEmpty()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                for (Data.Document doc : documents) {
                    String documentName = doc.getDocumentName() != null ? 
                        doc.getDocumentName() : "Document #" + doc.getDocumentId();
                    String status = doc.getStatus() != null ? doc.getStatus() : "Pending";
                    String submitted = doc.getSubmitted() != null ? doc.getSubmitted() : "No";
                    String requiredBy = doc.getRequiredBy() != null ? doc.getRequiredBy() : "PSA";
                    String uploadDate = doc.getUploadDate() != null ? 
                        dateFormat.format(doc.getUploadDate()) : "Not uploaded";

                    model.addRow(new Object[]{
                        documentName,
                        status,
                        submitted,
                        requiredBy,
                        uploadDate
                    });
                }
            } else {
                model.addRow(new Object[]{"No documents found", "", "", "", ""});
            }

        } catch (Exception e) {
            System.err.println("Error loading required documents: " + e.getMessage());
            e.printStackTrace();
            model.addRow(new Object[]{"Error loading data", e.getMessage(), "", "", ""});
        } finally {
            model.fireTableDataChanged();

            // Use the CustomTable's built-in methods
            RequiredDocumentsTable.revalidate();
            RequiredDocumentsTable.repaint();
        }
    }
    
    private void createDefaultDocuments() {
        try {
            if (currentCitizen == null) {
                System.err.println("No citizen to create documents for");
                return;
            }

            // Get all available doc forms
            List<Data.DocForm> docForms = Data.DocForm.getAllDocForms();
            if (docForms == null || docForms.isEmpty()) {
                System.err.println("No document forms found in database");
                return;
            }

            System.out.println("Found " + docForms.size() + " document forms");

            // Create documents for required forms
            for (Data.DocForm docForm : docForms) {
                Data.Document doc = new Data.Document();
                doc.setCitizenId(currentCitizen.getCitizenId());
                doc.setFormId(docForm.getFormId()); // Use form_id instead of document_name

                // Set initial status based on whether it's required
                if (docForm.isRequired()) {
                    doc.setStatus("Pending");
                    doc.setSubmitted("No");
                } else {
                    doc.setStatus("Not Required");
                    doc.setSubmitted("N/A");
                }

                doc.setRequiredBy("PSA Requirement");
                doc.setUploadDate(new java.sql.Date(System.currentTimeMillis()));

                // Use the new addDocument method that takes form_id
                boolean success = addDocumentWithFormId(doc);
                if (success) {
                    System.out.println("Created document for form: " + docForm.getFormName() + 
                                      " (Form ID: " + docForm.getFormId() + ")");
                } else {
                    System.err.println("Failed to create document for form: " + docForm.getFormName());
                }
            }

            System.out.println("Created default documents for citizen ID: " + currentCitizen.getCitizenId());
        } catch (Exception e) {
            System.err.println("Error creating default documents: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean addDocumentWithFormId(Data.Document doc) {
        String query = "INSERT INTO documents (citizen_id, form_id, status, submitted, required_by, upload_date) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection conn = backend.database.DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, doc.getCitizenId());
            stmt.setInt(2, doc.getFormId());
            stmt.setString(3, doc.getStatus());
            stmt.setString(4, doc.getSubmitted());
            stmt.setString(5, doc.getRequiredBy());
            stmt.setDate(6, doc.getUploadDate());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error adding document with form_id: " + e.getMessage());
            return false;
        }
    }

    private void loadNotificationsTable() {
        DefaultTableModel model = (DefaultTableModel) MyNotificationsTable.getModel();
        model.setRowCount(0);
        currentView = 4;

        try {
            if (currentCitizen == null) {
                model.addRow(new Object[]{"No citizen data available", "", "", "", ""});
                return;
            }

            // Get notifications for the citizen
            List<Data.Notification> notifications = Data.Notification.getNotificationsByCitizenId(currentCitizen.getCitizenId());

            if (notifications == null || notifications.isEmpty()) {
                // Create default notifications if none exist
                createDefaultNotifications();
                // Reload notifications
                notifications = Data.Notification.getNotificationsByCitizenId(currentCitizen.getCitizenId());
            }

            if (notifications != null && !notifications.isEmpty()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                for (Data.Notification notif : notifications) {
                    String notifDate = notif.getNotificationDate() != null ? 
                        dateFormat.format(notif.getNotificationDate()) : "N/A";
                    String time = notif.getNotificationTime() != null ? 
                        notif.getNotificationTime() : "N/A";
                    String message = notif.getMessage() != null ? 
                        notif.getMessage() : "Notification";
                    String type = notif.getType() != null ? 
                        notif.getType() : "General";
                    String readStatus = notif.getReadStatus() != null ? 
                        notif.getReadStatus() : "Unread";

                    model.addRow(new Object[]{
                        notifDate,
                        time,
                        message,
                        type,
                        readStatus
                    });
                }
            } else {
                model.addRow(new Object[]{"No notifications", "", "", "", ""});
            }

        } catch (Exception e) {
            System.err.println("Error loading notifications: " + e.getMessage());
            model.addRow(new Object[]{"Error loading data", "", "", "", ""});
        } finally {
            model.fireTableDataChanged();

            // Use the CustomTable's built-in methods
            MyNotificationsTable.revalidate();
            MyNotificationsTable.repaint();
        }
    }

    private void createDefaultNotifications() {
        try {
            if (currentCitizen == null) return;

            // Check if notifications already exist
            List<Data.Notification> existingNotifications = Data.Notification.getNotificationsByCitizenId(currentCitizen.getCitizenId());
            if (existingNotifications != null && !existingNotifications.isEmpty()) {
                return; // Notifications already exist
            }

            // Add application submission notification
            Data.Notification.addNotification(
                currentCitizen.getCitizenId(),
                "Your National ID application has been successfully submitted.",
                "Application"
            );

            Data.IDStatus status = Data.IDStatus.getStatusByCitizenId(currentCitizen.getCitizenId());

            if (status != null) {
                String statusText = status.getStatus();
                String transactionId = status.getTransactionId();

                // Add transaction ID notification
                if (transactionId != null && !transactionId.isEmpty()) {
                    Data.Notification.addNotification(
                        currentCitizen.getCitizenId(),
                        "Your Transaction ID: " + transactionId + " - Save this for reference.",
                        "Transaction"
                    );
                }

                // Add status-specific notifications
                if ("Submitted".equalsIgnoreCase(statusText) || "Pending".equalsIgnoreCase(statusText)) {
                    Data.Notification.addNotification(
                        currentCitizen.getCitizenId(),
                        "Your application is currently under review. Please check back for updates.",
                        "Status Update"
                    );
                } else if ("Processing".equalsIgnoreCase(statusText)) {
                    Data.Notification.addNotification(
                        currentCitizen.getCitizenId(),
                        "Your ID application is now being processed. This may take 7-10 business days.",
                        "Status Update"
                    );
                } else if ("Ready".equalsIgnoreCase(statusText)) {
                    Data.Notification.addNotification(
                        currentCitizen.getCitizenId(),
                        "Your National ID is ready for pickup! Please schedule an appointment.",
                        "Status Update"
                    );
                } else if ("Completed".equalsIgnoreCase(statusText)) {
                    Data.Notification.addNotification(
                        currentCitizen.getCitizenId(),
                        "Your National ID application process has been completed.",
                        "Status Update"
                    );
                }
            }

            // Add document submission reminder if needed
            List<Data.Document> documents = Data.Document.getDocumentsByCitizenId(currentCitizen.getCitizenId());
            if (documents != null) {
                long pendingDocuments = documents.stream()
                    .filter(doc -> "Pending".equals(doc.getStatus()) || "Required".equals(doc.getStatus()))
                    .count();

                if (pendingDocuments > 0) {
                    Data.Notification.addNotification(
                        currentCitizen.getCitizenId(),
                        "You have " + pendingDocuments + " pending documents to submit for your application.",
                        "Reminder"
                    );
                }
            }

        } catch (Exception e) {
            System.err.println("Error creating default notifications: " + e.getMessage());
        }
    }
    
    private String formatDate(Date date) {
        if (date == null) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    private void setDefaultValues() {
        if (MyApplicationStatusValueLabel != null) {
            MyApplicationStatusValueLabel.setText("No Data");
            MyApplicationStatusTitleLabel.setText("My Application Status");
        }
        if (DaySinceApplicationValueLabel != null) {
            DaySinceApplicationValueLabel.setText("0");
            DaySinceApplicationTitleLabel.setText("Days Since Application");
        }
        if (MyAppointmentCountLabel != null) {
            MyAppointmentCountLabel.setText("No");
            MyAppointmentTitleLabel.setText("Appointment");
        }
        if (NotificationsValueLabel != null) {
            NotificationsValueLabel.setText("0");
            NotificationsTitleLabel.setText("Notifications");
        }
        if (searchLabel != null) {
            searchLabel.setText("User: Not Available | TXN: Not Available");
        }
    }

    // Action methods for buttons
    private void showApplicationTimelineView() {
        switchTableVisibility(1);
        loadApplicationTimelineTable();
    }
    
    private void showDocumentsView() {
        switchTableVisibility(2);
        loadRequiredDocumentsTable();
    }
    
    private void showAppointmentView() {
        switchTableVisibility(3);
        loadAppointmentDetailsTable();
    }
    
    private void showNotificationsView() {
        switchTableVisibility(4);
        loadNotificationsTable();
    }
    
    private void switchTableVisibility(int viewNumber) {
        // Hide all tables first
        ApplicationTimelineTablePanel.setVisible(false);
        MyAppointmentDetailsTablePanel.setVisible(false);
        RequiredDocumentsTablePanel.setVisible(false);
        MyNotificationsTablePanel.setVisible(false);

        // Show the selected table
        switch (viewNumber) {
            case 1:
                ApplicationTimelineTablePanel.setVisible(true);
                loadApplicationTimelineTable(); // Load data when showing
                break;
            case 2:
                MyAppointmentDetailsTablePanel.setVisible(true);
                loadAppointmentDetailsTable(); // Load data when showing
                break;
            case 3:
                RequiredDocumentsTablePanel.setVisible(true);
                loadRequiredDocumentsTable(); // Load data when showing
                break;
            case 4:
                MyNotificationsTablePanel.setVisible(true);
                loadNotificationsTable(); // Load data when showing
                break;
        }
    }

    // Refresh method that can be called from outside
    public void refreshDashboard() {
        loadCitizenData();
        loadDashboardData();
        reloadCurrentView();
    }
    
    private void reloadCurrentView() {
        switch (currentView) {
            case 1:
                loadApplicationTimelineTable();
                break;
            case 2:
                loadAppointmentDetailsTable();
                break;
            case 3:
                loadRequiredDocumentsTable();
                break;
            case 4:
                loadNotificationsTable();
                break;
        }
    }

    private void filterCurrentTable(String searchTerm) {
        DefaultTableModel model = null;
        
        switch (currentView) {
            case 1:
                model = (DefaultTableModel) ApplicationTimelineTable.getModel();
                break;
            case 2:
                model = (DefaultTableModel) MyAppointmentDetailsTable.getModel();
                break;
            case 3:
                model = (DefaultTableModel) RequiredDocumentsTable.getModel();
                break;
            case 4:
                model = (DefaultTableModel) MyNotificationsTable.getModel();
                break;
        }

        if (model != null) {
            // Store current data count
            int originalRows = model.getRowCount();
            
            // Reload the data first
            reloadCurrentView();
            
            // Get fresh model
            switch (currentView) {
                case 1:
                    ApplicationTimelineTable.revalidate();
                    ApplicationTimelineTable.repaint();
                    break;
                case 2:
                    MyAppointmentDetailsTable.revalidate();
                    MyAppointmentDetailsTable.repaint();
                    break;
                case 3:
                    RequiredDocumentsTable.revalidate();
                    RequiredDocumentsTable.repaint();
                    break;
                case 4:
                    MyNotificationsTable.revalidate();
                    MyNotificationsTable.repaint();
                    break;
            }

            // Apply filtering
            for (int i = model.getRowCount() - 1; i >= 0; i--) {
                boolean match = false;
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    if (value != null && value.toString().toLowerCase().contains(searchTerm.toLowerCase())) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    model.removeRow(i);
                }
            }

            if (model.getRowCount() == 0) {
                model.addRow(new Object[]{"No matching records found", "", "", "", ""});
            }
        }
    }
    
    private void loadAppointmentDetailsTable() {
        DefaultTableModel model = (DefaultTableModel) MyAppointmentDetailsTable.getModel();
        model.setRowCount(0);
        currentView = 3;

        try {
            if (currentCitizen == null) {
                model.addRow(new Object[]{"No citizen data available", "", "", "", "", "Schedule"});
                return;
            }

            // Get appointment for the citizen
            Data.Appointment appointment = Data.Appointment.getAppointmentByCitizenId(currentCitizen.getCitizenId());

            if (appointment != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = appointment.getAppDate() != null ? 
                    dateFormat.format(appointment.getAppDate()) : "Not scheduled";
                String time = appointment.getAppTime() != null ? 
                    appointment.getAppTime() : "N/A";
                String status = appointment.getStatus() != null ? 
                    appointment.getStatus() : "Scheduled";
                String purpose = "ID Application/Pickup";

                // Get address for location
                String location = "PSA Office";
                if (currentAddress != null && currentAddress.getCity() != null) {
                    location = "PSA " + currentAddress.getCity() + " Office";
                }

                String action = getAppointmentActions(appointment);

                model.addRow(new Object[]{
                    date,
                    time,
                    status,
                    purpose,
                    location,
                    action
                });
            } else {
                // Show that no appointment is scheduled
                model.addRow(new Object[]{
                    "Not scheduled",
                    "N/A",
                    "No appointment",
                    "ID Application",
                    "PSA Office",
                    "Schedule"
                });

                // Optionally, show past appointments if any
                List<Data.Appointment> allAppointments = Data.Appointment.getAllAppointments();
                if (allAppointments != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    for (Data.Appointment appt : allAppointments) {
                        if (appt.getCitizenId() == currentCitizen.getCitizenId()) {
                            model.addRow(new Object[]{
                                dateFormat.format(appt.getAppDate()),
                                appt.getAppTime(),
                                appt.getStatus(),
                                "ID Application",
                                "PSA Office",
                                "Completed"
                            });
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading appointment details: " + e.getMessage());
            model.addRow(new Object[]{"Error loading data", "", "", "", "", ""});
        } finally {
            model.fireTableDataChanged();

            // Set up the button renderer and editor for the Actions column (column 5)
            setupButtonColumn();

            // Use the CustomTable's built-in methods
            MyAppointmentDetailsTable.revalidate();
            MyAppointmentDetailsTable.repaint();
        }
    }
    
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 1));
            setBorderPainted(true);
            setFont(new Font("Times New Roman", Font.PLAIN, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof JButton) {
                return (JButton) value;
            } else if (value instanceof String) {
                setText((String) value);
            }

            // Style the button
            setBackground(new Color(41, 128, 185));
            setForeground(Color.WHITE);
            
            // Apply hover effect if needed
            if (!isSelected && MyAppointmentDetailsTable.getHoveredRow() == row) {
                setBackground(new Color(52, 152, 219)); // Lighter blue on hover
            }

            return this;
        }
    }

    class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;
        private Dashboard dashboard;

        public ButtonEditor(Dashboard dashboard) {
            this.dashboard = dashboard;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(this);
            button.setFont(new Font("Times New Roman", Font.PLAIN, 12));
            button.setBackground(new Color(41, 128, 185));
            button.setForeground(Color.WHITE);
            // Set line border with same color as background
            button.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 1));
            button.setBorderPainted(true);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (value instanceof String) {
                label = (String) value;
            } else {
                label = "";
            }

            button.setText(label);
            currentRow = row;
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fireEditingStopped();

            // Handle button click
            handleButtonClick(currentRow);
        }

        private void handleButtonClick(int row) {
            // Get the appointment data from the table
            DefaultTableModel model = (DefaultTableModel) dashboard.MyAppointmentDetailsTable.getModel();

            // Get data from the clicked row
            String date = (String) model.getValueAt(row, 0);
            String time = (String) model.getValueAt(row, 1);
            String status = (String) model.getValueAt(row, 2);
            String purpose = (String) model.getValueAt(row, 3);
            String location = (String) model.getValueAt(row, 4);

            // Handle different actions based on status
            if ("Reschedule/Cancel".equals(label)) {
                showAppointmentActionDialog(row, date, time, status, purpose, location);
            }
        }

        private void showAppointmentActionDialog(int row, String date, String time, 
                                                String status, String purpose, String location) {
            // Create a dialog for reschedule/cancel
            JDialog dialog = new JDialog();
            dialog.setTitle("Appointment Actions");
            dialog.setLayout(new BorderLayout());
            dialog.setSize(300, 150);
            dialog.setLocationRelativeTo(dashboard);

            JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
            panel.setBorder(null);

            // Appointment info
            JLabel infoLabel = new JLabel("Appointment: " + date + " at " + time);
            infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Buttons panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

            JButton rescheduleButton = new JButton("Reschedule");

            rescheduleButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(dialog, "Reschedule functionality would go here");
                dialog.dispose();
            });

            JButton cancelButton = new JButton("Cancel");

            cancelButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(dialog, 
                    "Are you sure you want to cancel this appointment?", 
                    "Confirm Cancellation", 
                    JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Update the table to show cancelled
                    DefaultTableModel model = (DefaultTableModel) dashboard.MyAppointmentDetailsTable.getModel();
                    model.setValueAt("Cancelled", row, 2);
                    model.setValueAt("Cancelled", row, 5);
                    JOptionPane.showMessageDialog(dialog, "Appointment cancelled successfully!");
                    dialog.dispose();
                }
            });

            JButton closeButton = new JButton("Close");
            
            closeButton.addActionListener(e -> dialog.dispose());

            buttonPanel.add(rescheduleButton);
            buttonPanel.add(cancelButton);
            buttonPanel.add(closeButton);

            panel.add(infoLabel);
            panel.add(buttonPanel);
            dialog.add(panel, BorderLayout.CENTER);
            dialog.setVisible(true);
        }
    }
    // Helper method to create styled buttons
    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        // Set line border with same color as background
        button.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 1));
        button.setBorderPainted(true);
        button.setFocusPainted(false);

        // Add action listener
        button.addActionListener(e -> {
            handleAppointmentAction(button.getText());
        });

        return button;
    }

    // Method to set up the button column
    private void setupButtonColumn() {
        int actionsColumn = 5; // The Actions column index

        // Set the renderer and editor for the Actions column
        MyAppointmentDetailsTable.getColumnModel().getColumn(actionsColumn)
            .setCellRenderer(new ButtonRenderer());
        MyAppointmentDetailsTable.getColumnModel().getColumn(actionsColumn)
            .setCellEditor(new ButtonEditor(this));
    }

    // Method to handle button clicks
    private void handleAppointmentAction(String action) {
        int row = MyAppointmentDetailsTable.getSelectedRow();
        if (row == -1) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) MyAppointmentDetailsTable.getModel();
        String date = (String) model.getValueAt(row, 0);
        String time = (String) model.getValueAt(row, 1);
        String status = (String) model.getValueAt(row, 2);

        if ("Reschedule/Cancel".equals(action)) {
            showAppointmentOptions(row, date, time, status);
        } else if ("Schedule".equals(action)) {
            // Show schedule appointment dialog with actual scheduling functionality
            showScheduleAppointmentDialog(row);
        }
    }
    
    private void showScheduleAppointmentDialog(int row) {
        JDialog scheduleDialog = new JDialog();
        scheduleDialog.setTitle("Schedule New Appointment");
        scheduleDialog.setLayout(new BorderLayout());
        scheduleDialog.setSize(400, 200);
        scheduleDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Date field
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        JTextField dateField = new JTextField();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateField.setText(dateFormat.format(new Date()));

        // Time field
        JLabel timeLabel = new JLabel("Time (HH:MM):");
        JTextField timeField = new JTextField();
        timeField.setText("09:00");

        // Purpose field
        JLabel purposeLabel = new JLabel("Purpose:");
        JTextField purposeField = new JTextField();
        purposeField.setText("ID Pickup");

        panel.add(dateLabel);
        panel.add(dateField);
        panel.add(timeLabel);
        panel.add(timeField);
        panel.add(purposeLabel);
        panel.add(purposeField);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton scheduleButton = new JButton("Schedule");
        scheduleButton.addActionListener(e -> {
            String selectedDate = dateField.getText().trim();
            String selectedTime = timeField.getText().trim();
            String purpose = purposeField.getText().trim();

            if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
                JOptionPane.showMessageDialog(scheduleDialog, 
                    "Please enter both date and time", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update the table
            DefaultTableModel model = (DefaultTableModel) MyAppointmentDetailsTable.getModel();
            model.setValueAt(selectedDate, row, 0);
            model.setValueAt(selectedTime, row, 1);
            model.setValueAt("Scheduled", row, 2);
            model.setValueAt("Reschedule/Cancel", row, 5); // Update action button

            // Here you would also save to database:
            // Data.Appointment.scheduleAppointment(currentCitizen.getCitizenId(), selectedDate, selectedTime, purpose);

            JOptionPane.showMessageDialog(scheduleDialog, 
                "Appointment scheduled successfully!\n" +
                "Date: " + selectedDate + "\n" +
                "Time: " + selectedTime + "\n" +
                "Purpose: " + purpose,
                "Appointment Scheduled", 
                JOptionPane.INFORMATION_MESSAGE);

            scheduleDialog.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> scheduleDialog.dispose());

        buttonPanel.add(scheduleButton);
        buttonPanel.add(cancelButton);

        scheduleDialog.add(panel, BorderLayout.CENTER);
        scheduleDialog.add(buttonPanel, BorderLayout.SOUTH);
        scheduleDialog.setVisible(true);
    }

    // Method to show appointment options
    private void showAppointmentOptions(int row, String date, String time, String status) {
        Object[] options = {"Reschedule", "Cancel", "Close"};

        int choice = JOptionPane.showOptionDialog(this,
            "Appointment: " + date + " at " + time + "\nStatus: " + status + "\n\nWhat would you like to do?",
            "Appointment Actions",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);

        switch (choice) {
            case 0: // Reschedule
                showRescheduleDialog(row, date, time);
                break;

            case 1: // Cancel
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to cancel this appointment?",
                    "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Update the table
                    DefaultTableModel model = (DefaultTableModel) MyAppointmentDetailsTable.getModel();
                    model.setValueAt("Cancelled", row, 2);
                    model.setValueAt("Cancelled", row, 5);

                    // Update the appointment in database if needed
                    // Data.Appointment.cancelAppointment(appointmentId);

                    JOptionPane.showMessageDialog(this, 
                        "Appointment cancelled successfully!",
                        "Cancellation Complete",
                        JOptionPane.INFORMATION_MESSAGE);
                }
                break;
        }
    }
    
    private void showRescheduleDialog(int row, String currentDate, String currentTime) {
        JDialog rescheduleDialog = new JDialog();
        rescheduleDialog.setTitle("Reschedule Appointment");
        rescheduleDialog.setLayout(new BorderLayout());
        rescheduleDialog.setSize(400, 200);
        rescheduleDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel dateLabel = new JLabel("New Date (YYYY-MM-DD):");
        JTextField dateField = new JTextField();
        dateField.setText(currentDate);

        JLabel timeLabel = new JLabel("New Time (HH:MM):");
        JTextField timeField = new JTextField();
        timeField.setText(currentTime);

        panel.add(dateLabel);
        panel.add(dateField);
        panel.add(timeLabel);
        panel.add(timeField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton rescheduleButton = new JButton("Reschedule");
        rescheduleButton.addActionListener(e -> {
            String newDate = dateField.getText().trim();
            String newTime = timeField.getText().trim();

            if (newDate.isEmpty() || newTime.isEmpty()) {
                JOptionPane.showMessageDialog(rescheduleDialog, 
                    "Please enter both date and time", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update the table
            DefaultTableModel model = (DefaultTableModel) MyAppointmentDetailsTable.getModel();
            model.setValueAt(newDate, row, 0);
            model.setValueAt(newTime, row, 1);
            model.setValueAt("Rescheduled", row, 2);

            JOptionPane.showMessageDialog(rescheduleDialog, 
                "Appointment rescheduled successfully!\n" +
                "New Date: " + newDate + "\n" +
                "New Time: " + newTime,
                "Appointment Rescheduled", 
                JOptionPane.INFORMATION_MESSAGE);

            rescheduleDialog.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> rescheduleDialog.dispose());

        buttonPanel.add(rescheduleButton);
        buttonPanel.add(cancelButton);

        rescheduleDialog.add(panel, BorderLayout.CENTER);
        rescheduleDialog.add(buttonPanel, BorderLayout.SOUTH);
        rescheduleDialog.setVisible(true);
    }
    
    private String getAppointmentActions(Appointment appointment) {
        if (appointment == null) return "Schedule";

        String status = appointment.getStatus();
        if (status == null) return "Schedule";

        if ("SCHEDULED".equalsIgnoreCase(status) || "Scheduled".equals(status)) {
            return "Reschedule/Cancel";
        } else if ("COMPLETED".equalsIgnoreCase(status)) {
            return "Completed";
        } else if ("CANCELLED".equalsIgnoreCase(status)) {
            return "Cancelled";
        } else if ("RESCHEDULED".equalsIgnoreCase(status)) {
            return "Rescheduled";
        } else {
            return status;
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BoxPanel = new javax.swing.JPanel();
        MyApplicationStatusBoxPanel = new javax.swing.JPanel();
        MyApplicationStatusValueLabel = new javax.swing.JLabel();
        MyApplicationStatusTitleLabel = new javax.swing.JLabel();
        MyApplicationStatusActionBtn = new component.Button.FlatButton();
        DaySinceApplicationBoxPanel = new javax.swing.JPanel();
        DaySinceApplicationValueLabel = new javax.swing.JLabel();
        DaySinceApplicationTitleLabel = new javax.swing.JLabel();
        MyAppointmentActionBtn = new component.Button.FlatButton();
        MyAppointmentBoxPanel = new javax.swing.JPanel();
        MyAppointmentCountLabel = new javax.swing.JLabel();
        MyAppointmentTitleLabel = new javax.swing.JLabel();
        DaySinceApplicationActionBtn = new component.Button.FlatButton();
        NotificationsBoxPanel = new javax.swing.JPanel();
        NotificationsValueLabel = new javax.swing.JLabel();
        NotificationsTitleLabel = new javax.swing.JLabel();
        NotificationsActionBtn = new component.Button.FlatButton();
        searchField = new javax.swing.JTextField();
        searchLabel = new javax.swing.JLabel();
        DashboardCitizenTable = new javax.swing.JLayeredPane();
        ApplicationTimelineTablePanel = new javax.swing.JPanel();
        ApplicationTimelineTableScrollPane = new component.Scroll.CustomScrollPane();
        ApplicationTimelineTable = new component.Table.CustomTable();
        RequiredDocumentsTablePanel = new javax.swing.JPanel();
        RequiredDocumentsTableScrollPanel = new component.Scroll.CustomScrollPane();
        RequiredDocumentsTable = new component.Table.CustomTable();
        MyAppointmentDetailsTablePanel = new javax.swing.JPanel();
        MyAppointmentDetailsTableScrollPane = new component.Scroll.CustomScrollPane();
        MyAppointmentDetailsTable = new component.Table.CustomTable();
        MyNotificationsTablePanel = new javax.swing.JPanel();
        customScrollPane = new component.Scroll.CustomScrollPane();
        MyNotificationsTable = new component.Table.CustomTable();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(930, 550));

        BoxPanel.setBackground(new java.awt.Color(255, 255, 255));

        MyApplicationStatusBoxPanel.setBackground(new java.awt.Color(254, 161, 156));
        MyApplicationStatusBoxPanel.setPreferredSize(new java.awt.Dimension(200, 150));

        MyApplicationStatusValueLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        MyApplicationStatusValueLabel.setForeground(new java.awt.Color(25, 25, 25));
        MyApplicationStatusValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        MyApplicationStatusValueLabel.setText("0");
        MyApplicationStatusValueLabel.setToolTipText("");
        MyApplicationStatusValueLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        MyApplicationStatusTitleLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        MyApplicationStatusTitleLabel.setForeground(new java.awt.Color(25, 25, 25));
        MyApplicationStatusTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        MyApplicationStatusTitleLabel.setText("My Application Status");
        MyApplicationStatusTitleLabel.setToolTipText("");
        MyApplicationStatusTitleLabel.setPreferredSize(new java.awt.Dimension(140, 43));

        MyApplicationStatusActionBtn.setBackground(new java.awt.Color(254, 100, 100));
        MyApplicationStatusActionBtn.setForeground(new java.awt.Color(0, 0, 0));
        MyApplicationStatusActionBtn.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        MyApplicationStatusActionBtn.setInheritsPopupMenu(true);
        MyApplicationStatusActionBtn.setLabel("More Details");
        MyApplicationStatusActionBtn.setNormalColor(new java.awt.Color(254, 100, 100));
        MyApplicationStatusActionBtn.setPreferredSize(new java.awt.Dimension(140, 28));
        MyApplicationStatusActionBtn.setTextColor(new java.awt.Color(0, 0, 0));
        MyApplicationStatusActionBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MyApplicationStatusActionBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MyApplicationStatusBoxPanelLayout = new javax.swing.GroupLayout(MyApplicationStatusBoxPanel);
        MyApplicationStatusBoxPanel.setLayout(MyApplicationStatusBoxPanelLayout);
        MyApplicationStatusBoxPanelLayout.setHorizontalGroup(
            MyApplicationStatusBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MyApplicationStatusBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MyApplicationStatusBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(MyApplicationStatusTitleLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                    .addComponent(MyApplicationStatusValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(MyApplicationStatusActionBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        MyApplicationStatusBoxPanelLayout.setVerticalGroup(
            MyApplicationStatusBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MyApplicationStatusBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MyApplicationStatusValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(MyApplicationStatusTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(MyApplicationStatusActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        DaySinceApplicationBoxPanel.setBackground(new java.awt.Color(249, 254, 156));
        DaySinceApplicationBoxPanel.setPreferredSize(new java.awt.Dimension(200, 150));

        DaySinceApplicationValueLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        DaySinceApplicationValueLabel.setForeground(new java.awt.Color(25, 25, 25));
        DaySinceApplicationValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        DaySinceApplicationValueLabel.setText("0");
        DaySinceApplicationValueLabel.setToolTipText("");
        DaySinceApplicationValueLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        DaySinceApplicationTitleLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        DaySinceApplicationTitleLabel.setForeground(new java.awt.Color(25, 25, 25));
        DaySinceApplicationTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        DaySinceApplicationTitleLabel.setText("Days Since Application");
        DaySinceApplicationTitleLabel.setToolTipText("");
        DaySinceApplicationTitleLabel.setPreferredSize(new java.awt.Dimension(140, 43));

        MyAppointmentActionBtn.setBackground(new java.awt.Color(249, 200, 100));
        MyAppointmentActionBtn.setForeground(new java.awt.Color(0, 0, 0));
        MyAppointmentActionBtn.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        MyAppointmentActionBtn.setInheritsPopupMenu(true);
        MyAppointmentActionBtn.setLabel("More Details");
        MyAppointmentActionBtn.setNormalColor(new java.awt.Color(249, 200, 100));
        MyAppointmentActionBtn.setPreferredSize(new java.awt.Dimension(140, 28));
        MyAppointmentActionBtn.setTextColor(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout DaySinceApplicationBoxPanelLayout = new javax.swing.GroupLayout(DaySinceApplicationBoxPanel);
        DaySinceApplicationBoxPanel.setLayout(DaySinceApplicationBoxPanelLayout);
        DaySinceApplicationBoxPanelLayout.setHorizontalGroup(
            DaySinceApplicationBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DaySinceApplicationBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DaySinceApplicationBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DaySinceApplicationTitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                    .addComponent(DaySinceApplicationValueLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(MyAppointmentActionBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        DaySinceApplicationBoxPanelLayout.setVerticalGroup(
            DaySinceApplicationBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DaySinceApplicationBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DaySinceApplicationValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(DaySinceApplicationTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(MyAppointmentActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        MyAppointmentBoxPanel.setBackground(new java.awt.Color(200, 254, 156));
        MyAppointmentBoxPanel.setPreferredSize(new java.awt.Dimension(200, 150));

        MyAppointmentCountLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        MyAppointmentCountLabel.setForeground(new java.awt.Color(25, 25, 25));
        MyAppointmentCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        MyAppointmentCountLabel.setText("0");
        MyAppointmentCountLabel.setToolTipText("");
        MyAppointmentCountLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        MyAppointmentTitleLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        MyAppointmentTitleLabel.setForeground(new java.awt.Color(25, 25, 25));
        MyAppointmentTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        MyAppointmentTitleLabel.setText("My Appointment");
        MyAppointmentTitleLabel.setToolTipText("");
        MyAppointmentTitleLabel.setPreferredSize(new java.awt.Dimension(140, 43));

        DaySinceApplicationActionBtn.setBackground(new java.awt.Color(100, 254, 100));
        DaySinceApplicationActionBtn.setForeground(new java.awt.Color(0, 0, 0));
        DaySinceApplicationActionBtn.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        DaySinceApplicationActionBtn.setInheritsPopupMenu(true);
        DaySinceApplicationActionBtn.setLabel("More Details");
        DaySinceApplicationActionBtn.setNormalColor(new java.awt.Color(100, 254, 100));
        DaySinceApplicationActionBtn.setPreferredSize(new java.awt.Dimension(140, 28));
        DaySinceApplicationActionBtn.setTextColor(new java.awt.Color(0, 0, 0));
        DaySinceApplicationActionBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DaySinceApplicationActionBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MyAppointmentBoxPanelLayout = new javax.swing.GroupLayout(MyAppointmentBoxPanel);
        MyAppointmentBoxPanel.setLayout(MyAppointmentBoxPanelLayout);
        MyAppointmentBoxPanelLayout.setHorizontalGroup(
            MyAppointmentBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MyAppointmentBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MyAppointmentBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(MyAppointmentCountLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(MyAppointmentTitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(DaySinceApplicationActionBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        MyAppointmentBoxPanelLayout.setVerticalGroup(
            MyAppointmentBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MyAppointmentBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MyAppointmentCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(MyAppointmentTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(DaySinceApplicationActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        NotificationsBoxPanel.setBackground(new java.awt.Color(156, 200, 254));
        NotificationsBoxPanel.setPreferredSize(new java.awt.Dimension(200, 150));

        NotificationsValueLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        NotificationsValueLabel.setForeground(new java.awt.Color(25, 25, 25));
        NotificationsValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        NotificationsValueLabel.setText("0");
        NotificationsValueLabel.setToolTipText("");
        NotificationsValueLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        NotificationsTitleLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        NotificationsTitleLabel.setForeground(new java.awt.Color(25, 25, 25));
        NotificationsTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        NotificationsTitleLabel.setText("Notifications");
        NotificationsTitleLabel.setToolTipText("");
        NotificationsTitleLabel.setPreferredSize(new java.awt.Dimension(140, 43));

        NotificationsActionBtn.setBackground(new java.awt.Color(80, 80, 254));
        NotificationsActionBtn.setForeground(new java.awt.Color(0, 0, 0));
        NotificationsActionBtn.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        NotificationsActionBtn.setInheritsPopupMenu(true);
        NotificationsActionBtn.setLabel("More Details");
        NotificationsActionBtn.setNormalColor(new java.awt.Color(80, 80, 254));
        NotificationsActionBtn.setPreferredSize(new java.awt.Dimension(140, 28));
        NotificationsActionBtn.setTextColor(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout NotificationsBoxPanelLayout = new javax.swing.GroupLayout(NotificationsBoxPanel);
        NotificationsBoxPanel.setLayout(NotificationsBoxPanelLayout);
        NotificationsBoxPanelLayout.setHorizontalGroup(
            NotificationsBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NotificationsBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(NotificationsBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(NotificationsValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(NotificationsTitleLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(NotificationsActionBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        NotificationsBoxPanelLayout.setVerticalGroup(
            NotificationsBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NotificationsBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(NotificationsValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(NotificationsTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(NotificationsActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout BoxPanelLayout = new javax.swing.GroupLayout(BoxPanel);
        BoxPanel.setLayout(BoxPanelLayout);
        BoxPanelLayout.setHorizontalGroup(
            BoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BoxPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(MyApplicationStatusBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(DaySinceApplicationBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(MyAppointmentBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(NotificationsBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        BoxPanelLayout.setVerticalGroup(
            BoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(BoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(NotificationsBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DaySinceApplicationBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MyApplicationStatusBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MyAppointmentBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        searchField.setToolTipText("Search");
        searchField.setPreferredSize(new java.awt.Dimension(200, 30));
        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });

        searchLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        searchLabel.setForeground(new java.awt.Color(25, 25, 25));
        searchLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search.png"))); // NOI18N
        searchLabel.setToolTipText("");
        searchLabel.setPreferredSize(new java.awt.Dimension(30, 30));

        DashboardCitizenTable.setLayout(new javax.swing.OverlayLayout(DashboardCitizenTable));

        ApplicationTimelineTablePanel.setPreferredSize(new java.awt.Dimension(812, 150));

        ApplicationTimelineTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Date", "Content", "Description", "Updated By", "Notes"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ApplicationTimelineTableScrollPane.setViewportView(ApplicationTimelineTable);
        if (ApplicationTimelineTable.getColumnModel().getColumnCount() > 0) {
            ApplicationTimelineTable.getColumnModel().getColumn(0).setResizable(false);
            ApplicationTimelineTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            ApplicationTimelineTable.getColumnModel().getColumn(1).setResizable(false);
            ApplicationTimelineTable.getColumnModel().getColumn(1).setPreferredWidth(80);
            ApplicationTimelineTable.getColumnModel().getColumn(2).setResizable(false);
            ApplicationTimelineTable.getColumnModel().getColumn(2).setPreferredWidth(200);
            ApplicationTimelineTable.getColumnModel().getColumn(3).setResizable(false);
            ApplicationTimelineTable.getColumnModel().getColumn(3).setPreferredWidth(100);
            ApplicationTimelineTable.getColumnModel().getColumn(4).setResizable(false);
            ApplicationTimelineTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        }

        javax.swing.GroupLayout ApplicationTimelineTablePanelLayout = new javax.swing.GroupLayout(ApplicationTimelineTablePanel);
        ApplicationTimelineTablePanel.setLayout(ApplicationTimelineTablePanelLayout);
        ApplicationTimelineTablePanelLayout.setHorizontalGroup(
            ApplicationTimelineTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ApplicationTimelineTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        ApplicationTimelineTablePanelLayout.setVerticalGroup(
            ApplicationTimelineTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ApplicationTimelineTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        DashboardCitizenTable.add(ApplicationTimelineTablePanel);

        RequiredDocumentsTablePanel.setPreferredSize(new java.awt.Dimension(812, 150));

        RequiredDocumentsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Document", "Status", "Submitted", "Required By", "Upload"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        RequiredDocumentsTableScrollPanel.setViewportView(RequiredDocumentsTable);
        if (RequiredDocumentsTable.getColumnModel().getColumnCount() > 0) {
            RequiredDocumentsTable.getColumnModel().getColumn(0).setResizable(false);
            RequiredDocumentsTable.getColumnModel().getColumn(0).setPreferredWidth(150);
            RequiredDocumentsTable.getColumnModel().getColumn(1).setResizable(false);
            RequiredDocumentsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
            RequiredDocumentsTable.getColumnModel().getColumn(2).setResizable(false);
            RequiredDocumentsTable.getColumnModel().getColumn(2).setPreferredWidth(80);
            RequiredDocumentsTable.getColumnModel().getColumn(3).setResizable(false);
            RequiredDocumentsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
            RequiredDocumentsTable.getColumnModel().getColumn(4).setResizable(false);
            RequiredDocumentsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        }

        javax.swing.GroupLayout RequiredDocumentsTablePanelLayout = new javax.swing.GroupLayout(RequiredDocumentsTablePanel);
        RequiredDocumentsTablePanel.setLayout(RequiredDocumentsTablePanelLayout);
        RequiredDocumentsTablePanelLayout.setHorizontalGroup(
            RequiredDocumentsTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(RequiredDocumentsTableScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        RequiredDocumentsTablePanelLayout.setVerticalGroup(
            RequiredDocumentsTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(RequiredDocumentsTableScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        DashboardCitizenTable.add(RequiredDocumentsTablePanel);

        MyAppointmentDetailsTablePanel.setPreferredSize(new java.awt.Dimension(812, 150));

        MyAppointmentDetailsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Date", "Time", "Status", "Purpose", "Location", "Action"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        MyAppointmentDetailsTableScrollPane.setViewportView(MyAppointmentDetailsTable);
        if (MyAppointmentDetailsTable.getColumnModel().getColumnCount() > 0) {
            MyAppointmentDetailsTable.getColumnModel().getColumn(0).setResizable(false);
            MyAppointmentDetailsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            MyAppointmentDetailsTable.getColumnModel().getColumn(1).setResizable(false);
            MyAppointmentDetailsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
            MyAppointmentDetailsTable.getColumnModel().getColumn(2).setResizable(false);
            MyAppointmentDetailsTable.getColumnModel().getColumn(2).setPreferredWidth(80);
            MyAppointmentDetailsTable.getColumnModel().getColumn(3).setResizable(false);
            MyAppointmentDetailsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
            MyAppointmentDetailsTable.getColumnModel().getColumn(4).setResizable(false);
            MyAppointmentDetailsTable.getColumnModel().getColumn(4).setPreferredWidth(150);
            MyAppointmentDetailsTable.getColumnModel().getColumn(5).setResizable(false);
            MyAppointmentDetailsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        }

        javax.swing.GroupLayout MyAppointmentDetailsTablePanelLayout = new javax.swing.GroupLayout(MyAppointmentDetailsTablePanel);
        MyAppointmentDetailsTablePanel.setLayout(MyAppointmentDetailsTablePanelLayout);
        MyAppointmentDetailsTablePanelLayout.setHorizontalGroup(
            MyAppointmentDetailsTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MyAppointmentDetailsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        MyAppointmentDetailsTablePanelLayout.setVerticalGroup(
            MyAppointmentDetailsTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MyAppointmentDetailsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        DashboardCitizenTable.add(MyAppointmentDetailsTablePanel);

        MyNotificationsTablePanel.setPreferredSize(new java.awt.Dimension(812, 150));

        customScrollPane.setPreferredSize(new java.awt.Dimension(812, 150));

        MyNotificationsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Date", "Time", "Message", "Type", "Read Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        customScrollPane.setViewportView(MyNotificationsTable);
        if (MyNotificationsTable.getColumnModel().getColumnCount() > 0) {
            MyNotificationsTable.getColumnModel().getColumn(0).setResizable(false);
            MyNotificationsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            MyNotificationsTable.getColumnModel().getColumn(1).setResizable(false);
            MyNotificationsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
            MyNotificationsTable.getColumnModel().getColumn(2).setResizable(false);
            MyNotificationsTable.getColumnModel().getColumn(2).setPreferredWidth(250);
            MyNotificationsTable.getColumnModel().getColumn(3).setResizable(false);
            MyNotificationsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
            MyNotificationsTable.getColumnModel().getColumn(4).setResizable(false);
            MyNotificationsTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        }

        javax.swing.GroupLayout MyNotificationsTablePanelLayout = new javax.swing.GroupLayout(MyNotificationsTablePanel);
        MyNotificationsTablePanel.setLayout(MyNotificationsTablePanelLayout);
        MyNotificationsTablePanelLayout.setHorizontalGroup(
            MyNotificationsTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(customScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        MyNotificationsTablePanelLayout.setVerticalGroup(
            MyNotificationsTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(customScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        DashboardCitizenTable.add(MyNotificationsTablePanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(BoxPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(50, 50, 50))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DashboardCitizenTable)
                    .addComponent(searchLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(BoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(104, 104, 104)
                .addComponent(searchLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DashboardCitizenTable, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        String searchTerm = searchField.getText().trim();
        if (!searchTerm.isEmpty()) {
            filterCurrentTable(searchTerm);
        } else {
            reloadCurrentView();
        }
    }//GEN-LAST:event_searchFieldActionPerformed

    private void MyApplicationStatusActionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MyApplicationStatusActionBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MyApplicationStatusActionBtnActionPerformed

    private void DaySinceApplicationActionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DaySinceApplicationActionBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DaySinceApplicationActionBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.Table.CustomTable ApplicationTimelineTable;
    private javax.swing.JPanel ApplicationTimelineTablePanel;
    private component.Scroll.CustomScrollPane ApplicationTimelineTableScrollPane;
    private javax.swing.JPanel BoxPanel;
    private javax.swing.JLayeredPane DashboardCitizenTable;
    private component.Button.FlatButton DaySinceApplicationActionBtn;
    private javax.swing.JPanel DaySinceApplicationBoxPanel;
    private javax.swing.JLabel DaySinceApplicationTitleLabel;
    private javax.swing.JLabel DaySinceApplicationValueLabel;
    private component.Button.FlatButton MyApplicationStatusActionBtn;
    private javax.swing.JPanel MyApplicationStatusBoxPanel;
    private javax.swing.JLabel MyApplicationStatusTitleLabel;
    private javax.swing.JLabel MyApplicationStatusValueLabel;
    private component.Button.FlatButton MyAppointmentActionBtn;
    private javax.swing.JPanel MyAppointmentBoxPanel;
    private javax.swing.JLabel MyAppointmentCountLabel;
    private component.Table.CustomTable MyAppointmentDetailsTable;
    private javax.swing.JPanel MyAppointmentDetailsTablePanel;
    private component.Scroll.CustomScrollPane MyAppointmentDetailsTableScrollPane;
    private javax.swing.JLabel MyAppointmentTitleLabel;
    private component.Table.CustomTable MyNotificationsTable;
    private javax.swing.JPanel MyNotificationsTablePanel;
    private component.Button.FlatButton NotificationsActionBtn;
    private javax.swing.JPanel NotificationsBoxPanel;
    private javax.swing.JLabel NotificationsTitleLabel;
    private javax.swing.JLabel NotificationsValueLabel;
    private component.Table.CustomTable RequiredDocumentsTable;
    private javax.swing.JPanel RequiredDocumentsTablePanel;
    private javax.swing.JScrollPane RequiredDocumentsTableScrollPane;
    private component.Scroll.CustomScrollPane RequiredDocumentsTableScrollPanel;
    private component.Scroll.CustomScrollPane customScrollPane;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel searchLabel;
    // End of variables declaration//GEN-END:variables
}
