package component;

import backend.objects.Data;
import backend.objects.Data.User;
import backend.objects.Data.Citizen;
import backend.objects.Data.IDStatus;
import backend.objects.Data.Appointment;
import backend.objects.Data.Document;
import backend.objects.Data.Notification;
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

    private void setupButtonBorders() {
        // Set line borders with same color as background and no rounded corners
        setupLineBorder(MyApplicationStatusActionBtn, new Color(254, 100, 100));
        setupLineBorder(DaySinceApplicationActionBtn, new Color(249, 200, 100));
        setupLineBorder(MyAppointmentActionBtn, new Color(100, 254, 100));
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
        applyEnhancedRippleEffect(DaySinceApplicationActionBtn, new Color(41, 128, 185, 150));
        applyEnhancedRippleEffect(MyAppointmentActionBtn, new Color(41, 128, 185, 150));
        applyEnhancedRippleEffect(NotificationsActionBtn, new Color(41, 128, 185, 150));
        
        // Apply hover effects
        applyHoverEffect(MyApplicationStatusActionBtn);
        applyHoverEffect(DaySinceApplicationActionBtn);
        applyHoverEffect(MyAppointmentActionBtn);
        applyHoverEffect(NotificationsActionBtn);
        
        // Apply shadow effects
        applyShadowEffect(MyApplicationStatusActionBtn);
        applyShadowEffect(DaySinceApplicationActionBtn);
        applyShadowEffect(MyAppointmentActionBtn);
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
        if (currentCitizen == null) {
            System.out.println("No citizen record found for user ID: " + currentUser.getUserId());
            // If no citizen record exists, create a default one
            createDefaultCitizenRecord();
        } else {
            System.out.println("Loaded citizen data for: " + currentCitizen.getFullName());
        }
    }
    
    private void createDefaultCitizenRecord() {
        try {
            // Check if user already has a citizen record
            currentCitizen = Data.Citizen.getCitizenByUserId(currentUser.getUserId());
            if (currentCitizen != null) return;
            
            // Create a new citizen record for the user
            Citizen newCitizen = new Citizen();
            newCitizen.setUserId(currentUser.getUserId());
            newCitizen.setFullName(currentUser.getFullName());
            newCitizen.setPhone(currentUser.getPhone());
            newCitizen.setApplicationDate(new java.sql.Date(System.currentTimeMillis()));
            
            // Try to add citizen
            boolean success = Data.Citizen.addCitizen(newCitizen);
            if (success) {
                // Reload citizen data
                currentCitizen = Data.Citizen.getCitizenByUserId(currentUser.getUserId());
                System.out.println("Created default citizen record for user: " + currentUser.getUsername());
                
                // Create default status
                IDStatus defaultStatus = new IDStatus();
                defaultStatus.setCitizenId(currentCitizen.getCitizenId());
                defaultStatus.setStatus("Application Submitted");
                defaultStatus.setUpdateDate(new java.sql.Date(System.currentTimeMillis()));
                defaultStatus.setNotes("Application has been submitted for processing");
                Data.IDStatus.addStatus(defaultStatus);
            }
        } catch (Exception e) {
            System.err.println("Error creating default citizen record: " + e.getMessage());
        }
    }
    
    private void testDatabaseConnection() {
        try {
            System.out.println("Testing database connection for user: " + currentUser.getUsername());
            
            // Test getting citizens
            List<Citizen> citizens = Data.Citizen.getAllCitizens();
            System.out.println("Total citizens in database: " + citizens.size());

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
        if (DaySinceApplicationActionBtn != null) {
            RippleEffect approvedRipple = new RippleEffect(DaySinceApplicationActionBtn);
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
        
        if (MyAppointmentActionBtn != null) {
            RippleEffect urgentRipple = new RippleEffect(MyAppointmentActionBtn);
            urgentRipple.setRippleColor(new Color(255, 255, 255, 100));
        }
    }

    private void setupButtonActions() {
        if (MyApplicationStatusActionBtn != null) {
            MyApplicationStatusActionBtn.addActionListener((ActionEvent e) -> {
                showApplicationTimelineView(); // Shows timeline
            });
        }

        if (DaySinceApplicationActionBtn != null) {
            DaySinceApplicationActionBtn.addActionListener((ActionEvent e) -> {
                showAppointmentView(); // Shows appointments
            });
        }

        if (MyAppointmentActionBtn != null) {
            MyAppointmentActionBtn.addActionListener((ActionEvent e) -> {
                showDocumentsView(); // Shows documents
            });
        }

        if (NotificationsActionBtn != null) {
            NotificationsActionBtn.addActionListener((ActionEvent e) -> {
                showNotificationsView();
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

        // Add search icon effect - with null check
        try {
            java.net.URL imageUrl = getClass().getResource("/images/search.png");
            if (imageUrl != null) {
                searchLabel.setIcon(new javax.swing.ImageIcon(imageUrl));
            } else {
                // Use text if image not found
                searchLabel.setText("🔍 Search:");
                System.out.println("Search icon not found at /images/search.png");
            }
        } catch (Exception e) {
            // Fallback to text
            searchLabel.setText("Search:");
        }

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

            // Box 1: My Application Status
            IDStatus status = Data.IDStatus.getStatusByCitizenId(currentCitizen.getCitizenId());
            String statusText = (status != null) ? status.getStatus() : "Application Submitted";
            MyApplicationStatusValueLabel.setText(statusText);
            MyApplicationStatusTitleLabel.setText("My Application Status");

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
            Appointment appointment = Data.Appointment.getAppointmentByCitizenId(currentCitizen.getCitizenId());
            if (appointment != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
                MyAppointmentCountLabel.setText(sdf.format(appointment.getAppDate()));
                MyAppointmentTitleLabel.setText("My Appointment");
            } else {
                MyAppointmentCountLabel.setText("No");
                MyAppointmentTitleLabel.setText("Appointment");
            }

            // Box 4: Notifications - Use the new Notification class
            int notificationCount = Data.Notification.getUnreadCount(currentCitizen.getCitizenId());
            NotificationsValueLabel.setText(String.valueOf(notificationCount));
            NotificationsTitleLabel.setText("Notifications");
        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
            setDefaultValues();
        }
    }
    
    private void loadApplicationTimelineTable() {
        DefaultTableModel model = (DefaultTableModel) ApplicationTimelineTable.getModel(); // Changed from RequiredDocumentsTable
        model.setRowCount(0);
        currentView = 1;

        try {
            if (currentCitizen == null) {
                model.addRow(new Object[]{"No citizen data found", "", "", "", ""});
                return;
            }

            // Get all activity logs for this citizen
            List<Data.ActivityLog> activityLogs = Data.ActivityLog.getActivityLogsByCitizenId(currentCitizen.getCitizenId());

            // Get all status updates
            List<Data.IDStatus> statuses = Data.IDStatus.getAllStatus();

            // Create a list to store all timeline entries
            List<Object[]> timelineEntries = new ArrayList<>();

            // Add application submission
            if (currentCitizen.getApplicationDate() != null) {
                timelineEntries.add(new Object[]{
                    formatDate(currentCitizen.getApplicationDate()),
                    "Application Submitted",
                    "Your National ID application has been submitted",
                    "System",
                    "Awaiting processing"
                });
            }

            // Add status updates for this citizen
            for (Data.IDStatus status : statuses) {
                if (status.getCitizenId() == currentCitizen.getCitizenId()) {
                    timelineEntries.add(new Object[]{
                        formatDate(status.getUpdateDate()),
                        status.getStatus(),
                        "Status updated",
                        "PSA Staff",
                        status.getNotes()
                    });
                }
            }

            // Add appointment information
            Data.Appointment appointment = Data.Appointment.getAppointmentByCitizenId(currentCitizen.getCitizenId());
            if (appointment != null) {
                timelineEntries.add(new Object[]{
                    formatDate(appointment.getCreatedDate()),
                    "Appointment " + appointment.getStatus(),
                    "Appointment scheduled for ID pickup",
                    appointment.getStatus().equals("Scheduled") ? "You" : "System",
                    "Time: " + appointment.getAppTime() + " | Date: " + formatDate(appointment.getAppDate())
                });
            }

            // Add activity logs
            for (Data.ActivityLog log : activityLogs) {
                timelineEntries.add(new Object[]{
                    formatDate(log.getActionDate()),
                    "Activity",
                    log.getAction(),
                    "System",
                    "Time: " + log.getActionTime()
                });
            }

            // Add document submission events
            List<Data.Document> documents = Data.Document.getDocumentsByCitizenId(currentCitizen.getCitizenId());
            for (Data.Document doc : documents) {
                if ("Yes".equals(doc.getSubmitted()) || "Verified".equals(doc.getStatus())) {
                    timelineEntries.add(new Object[]{
                        formatDate(doc.getUploadDate()),
                        "Document Submitted",
                        doc.getDocumentName() + " uploaded",
                        "You",
                        "Status: " + doc.getStatus()
                    });
                }
            }

            // Sort timeline by date (most recent first)
            timelineEntries.sort((a, b) -> {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date dateA = sdf.parse(a[0].toString());
                    Date dateB = sdf.parse(b[0].toString());
                    return dateB.compareTo(dateA); // Descending order
                } catch (Exception e) {
                    return 0;
                }
            });

            // Add to table model
            for (Object[] entry : timelineEntries) {
                model.addRow(entry);
            }

            // If no data, add a message
            if (model.getRowCount() == 0) {
                model.addRow(new Object[]{"No timeline data available", "", "Submit your application to see updates", "", ""});
            }

        } catch (Exception e) {
            System.err.println("Error loading application timeline: " + e.getMessage());
            e.printStackTrace();
            model.addRow(new Object[]{"Error loading data", "Check database connection", e.getMessage(), "", ""});
        } finally {
            model.fireTableDataChanged();
            ApplicationTimelineTable.revalidate();
            ApplicationTimelineTable.repaint();
        }
    }
    
    private void loadRequiredDocumentsTable() {
        DefaultTableModel model = (DefaultTableModel) RequiredDocumentsTable.getModel();
        model.setRowCount(0);
        currentView = 3;

        try {
            if (currentCitizen == null) {
                model.addRow(new Object[]{"No citizen data found", "", "", "", ""});
                return;
            }

            // Use the new Document class
            List<Document> documents = Data.Document.getDocumentsByCitizenId(currentCitizen.getCitizenId());
            
            if (documents.isEmpty()) {
                // Create default documents if none exist
                createDefaultDocuments();
                documents = Data.Document.getDocumentsByCitizenId(currentCitizen.getCitizenId());
            }

            for (Document doc : documents) {
                String actionText = "View";
                if ("Pending".equals(doc.getStatus()) && "No".equals(doc.getSubmitted())) {
                    actionText = "Upload";
                } else if ("Not Required".equals(doc.getStatus())) {
                    actionText = "N/A";
                }
                
                model.addRow(new Object[]{
                    doc.getDocumentName(),
                    doc.getStatus(),
                    doc.getSubmitted(),
                    doc.getRequiredBy(),
                    actionText
                });
            }

        } catch (Exception e) {
            System.err.println("Error loading required documents: " + e.getMessage());
            model.addRow(new Object[]{"Error loading data", "", "", "", ""});
        } finally {
            model.fireTableDataChanged();
            RequiredDocumentsTable.revalidate();
            RequiredDocumentsTable.repaint();
        }
    }
    
    private void createDefaultDocuments() {
        try {
            // Create default documents for the citizen
            String[][] defaultDocs = {
                {"Birth Certificate", "Verified", "Yes", "Required"},
                {"Proof of Address", "Pending", "No", "Required"},
                {"Government ID", "Not Required", "N/A", "Optional"},
                {"Application Form", "Submitted", "Yes", "Required"}
            };
            
            for (String[] docData : defaultDocs) {
                Document doc = new Document();
                doc.setCitizenId(currentCitizen.getCitizenId());
                doc.setDocumentName(docData[0]);
                doc.setStatus(docData[1]);
                doc.setSubmitted(docData[2]);
                doc.setRequiredBy(docData[3]);
                doc.setUploadDate(new java.sql.Date(System.currentTimeMillis()));
                
                Data.Document.addDocument(doc);
            }
        } catch (Exception e) {
            System.err.println("Error creating default documents: " + e.getMessage());
        }
    }

    
    private void loadNotificationsTable() {
        DefaultTableModel model = (DefaultTableModel) MyNotificationsTable.getModel();
        model.setRowCount(0);
        currentView = 4;

        try {
            if (currentCitizen == null) {
                model.addRow(new Object[]{"No citizen data found", "", "", "", ""});
                return;
            }

            // Use the new Notification class
            List<Notification> notifications = Data.Notification.getNotificationsByCitizenId(currentCitizen.getCitizenId());
            
            if (notifications.isEmpty()) {
                // Create default notifications if none exist
                createDefaultNotifications();
                notifications = Data.Notification.getNotificationsByCitizenId(currentCitizen.getCitizenId());
            }

            for (Notification notification : notifications) {
                model.addRow(new Object[]{
                    formatDate(notification.getNotificationDate()),
                    notification.getNotificationTime(),
                    notification.getMessage(),
                    notification.getType(),
                    notification.getReadStatus()
                });
            }

            if (model.getRowCount() == 0) {
                model.addRow(new Object[]{"No notifications", "", "", "", ""});
            }

        } catch (Exception e) {
            System.err.println("Error loading notifications: " + e.getMessage());
            model.addRow(new Object[]{"Error loading data", "", "", "", ""});
        } finally {
            model.fireTableDataChanged();
            MyNotificationsTable.revalidate();
            MyNotificationsTable.repaint();
        }
    }

    private void createDefaultNotifications() {
        try {
            IDStatus status = Data.IDStatus.getStatusByCitizenId(currentCitizen.getCitizenId());
            
            // Add application submission notification
            Data.Notification.addNotification(
                currentCitizen.getCitizenId(),
                "Your National ID application has been received.",
                "Application"
            );
            
            if (status != null) {
                String statusText = status.getStatus();
                
                if ("Ready".equals(statusText)) {
                    Data.Notification.addNotification(
                        currentCitizen.getCitizenId(),
                        "Your National ID is ready for pickup! Please schedule an appointment.",
                        "Status Update"
                    );
                }
                
                if ("Processing".equals(statusText)) {
                    Data.Notification.addNotification(
                        currentCitizen.getCitizenId(),
                        "Your ID application is now being processed.",
                        "Status Update"
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
    }

    // Action methods for buttons
    private void showApplicationTimelineView() {
        switchTableVisibility(1);
        loadApplicationTimelineTable();
    }

    private void showAppointmentView() {
        switchTableVisibility(2);
        loadAppointmentDetailsTable();
    }

    private void showDocumentsView() {
        switchTableVisibility(3);
        loadRequiredDocumentsTable();
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
                model = (DefaultTableModel) RequiredDocumentsTable.getModel();
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
                    model = (DefaultTableModel) RequiredDocumentsTable.getModel();
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
        currentView = 2;

        try {
            if (currentCitizen == null) {
                model.addRow(new Object[]{"No citizen data found", "", "", "", "", ""});
                return;
            }

            Appointment appointment = Data.Appointment.getAppointmentByCitizenId(currentCitizen.getCitizenId());
            if (appointment != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

                // Get action text
                String actionText = getAppointmentActions(appointment);

                model.addRow(new Object[]{
                    dateFormat.format(appointment.getAppDate()),
                    appointment.getAppTime(),
                    appointment.getStatus(),
                    "ID Pickup",
                    "PSA Office - Main Branch",
                    actionText  // Use String instead of JButton
                });

                // Get additional appointment history
                List<Appointment> allAppointments = Data.Appointment.getAllAppointments();
                for (Appointment app : allAppointments) {
                    if (app.getCitizenId() == currentCitizen.getCitizenId() && 
                        app.getAppointmentId() != appointment.getAppointmentId()) {
                        model.addRow(new Object[]{
                            dateFormat.format(app.getAppDate()),
                            app.getAppTime(),
                            app.getStatus(),
                            "ID Pickup",
                            "PSA Office",
                            "Completed"  // Use String instead of JButton
                        });
                    }
                }
            } else {
                model.addRow(new Object[]{
                    "No appointment scheduled",
                    "",
                    "",
                    "ID Pickup",
                    "PSA Office",
                    "Schedule"  // Use String instead of JButton
                });
            }

        } catch (Exception e) {
            System.err.println("Error loading appointment details: " + e.getMessage());
            model.addRow(new Object[]{"Error loading data", "", "", "", "", ""});
        } finally {
            model.fireTableDataChanged();

            // Set up the button renderer and editor for the Actions column (column 5)
            setupButtonColumn();

            MyAppointmentDetailsTable.revalidate();
            MyAppointmentDetailsTable.repaint();
        }
    }


    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 1));
            setBorderPainted(true);
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
            setFont(new Font("Times New Roman", Font.PLAIN, 12));
            setBackground(new Color(41, 128, 185));
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 1));

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
        if ("Scheduled".equals(appointment.getStatus())) {
            return "Reschedule/Cancel";
        } else if ("Completed".equals(appointment.getStatus())) {
            return "Completed";
        } else {
            return appointment.getStatus();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BoxPanel = new javax.swing.JPanel();
        MyApplicationStatusBoxPanel = new javax.swing.JPanel();
        MyApplicationStatusValueLabel = new javax.swing.JLabel();
        MyApplicationStatusTitleLabel = new javax.swing.JLabel();
        MyApplicationStatusActionBtn = new javax.swing.JButton();
        DaySinceApplicationBoxPanel = new javax.swing.JPanel();
        DaySinceApplicationValueLabel = new javax.swing.JLabel();
        DaySinceApplicationTitleLabel = new javax.swing.JLabel();
        DaySinceApplicationActionBtn = new javax.swing.JButton();
        MyAppointmentBoxPanel = new javax.swing.JPanel();
        MyAppointmentCountLabel = new javax.swing.JLabel();
        MyAppointmentActionBtn = new javax.swing.JButton();
        MyAppointmentTitleLabel = new javax.swing.JLabel();
        NotificationsBoxPanel = new javax.swing.JPanel();
        NotificationsValueLabel = new javax.swing.JLabel();
        NotificationsActionBtn = new javax.swing.JButton();
        NotificationsTitleLabel = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        searchLabel = new javax.swing.JLabel();
        DashboardCitizenTable = new javax.swing.JLayeredPane();
        ApplicationTimelineTablePanel = new javax.swing.JPanel();
        ApplicationTimelineTableScrollPane = new javax.swing.JScrollPane();
        ApplicationTimelineTable = new component.Table.CustomTable();
        RequiredDocumentsTablePanel = new javax.swing.JPanel();
        RequiredDocumentsTableScrollPane = new javax.swing.JScrollPane();
        RequiredDocumentsTable = new component.Table.CustomTable();
        MyAppointmentDetailsTablePanel = new javax.swing.JPanel();
        MyAppointmentDetailsTableScrollPane = new javax.swing.JScrollPane();
        MyAppointmentDetailsTable = new component.Table.CustomTable();
        MyNotificationsTablePanel = new javax.swing.JPanel();
        MyNotificationsTableScrollPane = new javax.swing.JScrollPane();
        MyNotificationsTable = new component.Table.CustomTable();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(850, 550));

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
        MyApplicationStatusActionBtn.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        MyApplicationStatusActionBtn.setForeground(new java.awt.Color(25, 25, 25));
        MyApplicationStatusActionBtn.setText("More Details");
        MyApplicationStatusActionBtn.setBorder(null);
        MyApplicationStatusActionBtn.setBorderPainted(false);
        MyApplicationStatusActionBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        MyApplicationStatusActionBtn.setFocusPainted(false);
        MyApplicationStatusActionBtn.setHideActionText(true);
        MyApplicationStatusActionBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        MyApplicationStatusActionBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        MyApplicationStatusActionBtn.setPreferredSize(new java.awt.Dimension(140, 28));
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
            .addComponent(MyApplicationStatusActionBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        DaySinceApplicationActionBtn.setBackground(new java.awt.Color(249, 200, 100));
        DaySinceApplicationActionBtn.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        DaySinceApplicationActionBtn.setForeground(new java.awt.Color(25, 25, 25));
        DaySinceApplicationActionBtn.setText("More Details");
        DaySinceApplicationActionBtn.setBorder(null);
        DaySinceApplicationActionBtn.setBorderPainted(false);
        DaySinceApplicationActionBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        DaySinceApplicationActionBtn.setFocusPainted(false);
        DaySinceApplicationActionBtn.setHideActionText(true);
        DaySinceApplicationActionBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        DaySinceApplicationActionBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        DaySinceApplicationActionBtn.setPreferredSize(new java.awt.Dimension(140, 28));
        DaySinceApplicationActionBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DaySinceApplicationActionBtnActionPerformed(evt);
            }
        });

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
            .addComponent(DaySinceApplicationActionBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        DaySinceApplicationBoxPanelLayout.setVerticalGroup(
            DaySinceApplicationBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DaySinceApplicationBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DaySinceApplicationValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(DaySinceApplicationTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(DaySinceApplicationActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        MyAppointmentBoxPanel.setBackground(new java.awt.Color(200, 254, 156));
        MyAppointmentBoxPanel.setPreferredSize(new java.awt.Dimension(200, 150));

        MyAppointmentCountLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        MyAppointmentCountLabel.setForeground(new java.awt.Color(25, 25, 25));
        MyAppointmentCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        MyAppointmentCountLabel.setText("0");
        MyAppointmentCountLabel.setToolTipText("");
        MyAppointmentCountLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        MyAppointmentActionBtn.setBackground(new java.awt.Color(100, 254, 100));
        MyAppointmentActionBtn.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        MyAppointmentActionBtn.setForeground(new java.awt.Color(25, 25, 25));
        MyAppointmentActionBtn.setText("More Details");
        MyAppointmentActionBtn.setBorder(null);
        MyAppointmentActionBtn.setBorderPainted(false);
        MyAppointmentActionBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        MyAppointmentActionBtn.setFocusPainted(false);
        MyAppointmentActionBtn.setHideActionText(true);
        MyAppointmentActionBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        MyAppointmentActionBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        MyAppointmentActionBtn.setPreferredSize(new java.awt.Dimension(140, 28));
        MyAppointmentActionBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MyAppointmentActionBtnActionPerformed(evt);
            }
        });

        MyAppointmentTitleLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        MyAppointmentTitleLabel.setForeground(new java.awt.Color(25, 25, 25));
        MyAppointmentTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        MyAppointmentTitleLabel.setText("My Appointment");
        MyAppointmentTitleLabel.setToolTipText("");
        MyAppointmentTitleLabel.setPreferredSize(new java.awt.Dimension(140, 43));

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
            .addComponent(MyAppointmentActionBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        MyAppointmentBoxPanelLayout.setVerticalGroup(
            MyAppointmentBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MyAppointmentBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MyAppointmentCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(MyAppointmentTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(MyAppointmentActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        NotificationsBoxPanel.setBackground(new java.awt.Color(156, 200, 254));
        NotificationsBoxPanel.setPreferredSize(new java.awt.Dimension(200, 150));

        NotificationsValueLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        NotificationsValueLabel.setForeground(new java.awt.Color(25, 25, 25));
        NotificationsValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        NotificationsValueLabel.setText("0");
        NotificationsValueLabel.setToolTipText("");
        NotificationsValueLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        NotificationsActionBtn.setBackground(new java.awt.Color(80, 80, 254));
        NotificationsActionBtn.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        NotificationsActionBtn.setForeground(new java.awt.Color(25, 25, 25));
        NotificationsActionBtn.setText("More Details");
        NotificationsActionBtn.setBorder(null);
        NotificationsActionBtn.setBorderPainted(false);
        NotificationsActionBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        NotificationsActionBtn.setFocusPainted(false);
        NotificationsActionBtn.setHideActionText(true);
        NotificationsActionBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        NotificationsActionBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        NotificationsActionBtn.setPreferredSize(new java.awt.Dimension(140, 28));
        NotificationsActionBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NotificationsActionBtnActionPerformed(evt);
            }
        });

        NotificationsTitleLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        NotificationsTitleLabel.setForeground(new java.awt.Color(25, 25, 25));
        NotificationsTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        NotificationsTitleLabel.setText("Notifications");
        NotificationsTitleLabel.setToolTipText("");
        NotificationsTitleLabel.setPreferredSize(new java.awt.Dimension(140, 43));

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(NotificationsActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout BoxPanelLayout = new javax.swing.GroupLayout(BoxPanel);
        BoxPanel.setLayout(BoxPanelLayout);
        BoxPanelLayout.setHorizontalGroup(
            BoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BoxPanelLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(MyApplicationStatusBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(DaySinceApplicationBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(MyAppointmentBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(NotificationsBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
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
        searchLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        searchLabel.setToolTipText("");
        searchLabel.setPreferredSize(new java.awt.Dimension(30, 30));

        DashboardCitizenTable.setLayout(new javax.swing.OverlayLayout(DashboardCitizenTable));

        ApplicationTimelineTablePanel.setPreferredSize(new java.awt.Dimension(812, 242));

        ApplicationTimelineTableScrollPane.setPreferredSize(new java.awt.Dimension(402, 150));

        ApplicationTimelineTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Date", "Status", "Description", "Updated By", "Notes"
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
            .addComponent(ApplicationTimelineTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 965, Short.MAX_VALUE)
        );
        ApplicationTimelineTablePanelLayout.setVerticalGroup(
            ApplicationTimelineTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ApplicationTimelineTableScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        DashboardCitizenTable.add(ApplicationTimelineTablePanel);

        RequiredDocumentsTablePanel.setPreferredSize(new java.awt.Dimension(812, 242));

        RequiredDocumentsTableScrollPane.setPreferredSize(new java.awt.Dimension(912, 150));

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
        RequiredDocumentsTableScrollPane.setViewportView(RequiredDocumentsTable);
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
            .addComponent(RequiredDocumentsTableScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 965, Short.MAX_VALUE)
        );
        RequiredDocumentsTablePanelLayout.setVerticalGroup(
            RequiredDocumentsTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RequiredDocumentsTablePanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(RequiredDocumentsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        DashboardCitizenTable.add(RequiredDocumentsTablePanel);

        MyAppointmentDetailsTablePanel.setPreferredSize(new java.awt.Dimension(812, 242));

        MyAppointmentDetailsTableScrollPane.setPreferredSize(new java.awt.Dimension(402, 150));

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

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
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
            MyAppointmentDetailsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        }

        javax.swing.GroupLayout MyAppointmentDetailsTablePanelLayout = new javax.swing.GroupLayout(MyAppointmentDetailsTablePanel);
        MyAppointmentDetailsTablePanel.setLayout(MyAppointmentDetailsTablePanelLayout);
        MyAppointmentDetailsTablePanelLayout.setHorizontalGroup(
            MyAppointmentDetailsTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MyAppointmentDetailsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 965, Short.MAX_VALUE)
        );
        MyAppointmentDetailsTablePanelLayout.setVerticalGroup(
            MyAppointmentDetailsTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MyAppointmentDetailsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
        );

        DashboardCitizenTable.add(MyAppointmentDetailsTablePanel);

        MyNotificationsTablePanel.setPreferredSize(new java.awt.Dimension(812, 242));

        MyNotificationsTableScrollPane.setPreferredSize(new java.awt.Dimension(918, 150));

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
        MyNotificationsTableScrollPane.setViewportView(MyNotificationsTable);
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
            .addComponent(MyNotificationsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 965, Short.MAX_VALUE)
        );
        MyNotificationsTablePanelLayout.setVerticalGroup(
            MyNotificationsTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MyNotificationsTablePanelLayout.createSequentialGroup()
                .addComponent(MyNotificationsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        DashboardCitizenTable.add(MyNotificationsTablePanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(BoxPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(50, 50, 50))
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(searchLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(727, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DashboardCitizenTable)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(BoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(146, 146, 146)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(searchLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(searchField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DashboardCitizenTable, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void MyApplicationStatusActionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MyApplicationStatusActionBtnActionPerformed
        showApplicationTimelineView();
    }//GEN-LAST:event_MyApplicationStatusActionBtnActionPerformed

    private void DaySinceApplicationActionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DaySinceApplicationActionBtnActionPerformed
        showDocumentsView();
    }//GEN-LAST:event_DaySinceApplicationActionBtnActionPerformed

    private void MyAppointmentActionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MyAppointmentActionBtnActionPerformed
        showAppointmentView();
    }//GEN-LAST:event_MyAppointmentActionBtnActionPerformed

    private void NotificationsActionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NotificationsActionBtnActionPerformed
        showNotificationsView();
    }//GEN-LAST:event_NotificationsActionBtnActionPerformed

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        String searchTerm = searchField.getText().trim();
        if (!searchTerm.isEmpty()) {
            filterCurrentTable(searchTerm);
        } else {
            reloadCurrentView();
        }
    }//GEN-LAST:event_searchFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.Table.CustomTable ApplicationTimelineTable;
    private javax.swing.JPanel ApplicationTimelineTablePanel;
    private javax.swing.JScrollPane ApplicationTimelineTableScrollPane;
    private javax.swing.JPanel BoxPanel;
    private javax.swing.JLayeredPane DashboardCitizenTable;
    private javax.swing.JButton DaySinceApplicationActionBtn;
    private javax.swing.JPanel DaySinceApplicationBoxPanel;
    private javax.swing.JLabel DaySinceApplicationTitleLabel;
    private javax.swing.JLabel DaySinceApplicationValueLabel;
    private javax.swing.JButton MyApplicationStatusActionBtn;
    private javax.swing.JPanel MyApplicationStatusBoxPanel;
    private javax.swing.JLabel MyApplicationStatusTitleLabel;
    private javax.swing.JLabel MyApplicationStatusValueLabel;
    private javax.swing.JButton MyAppointmentActionBtn;
    private javax.swing.JPanel MyAppointmentBoxPanel;
    private javax.swing.JLabel MyAppointmentCountLabel;
    private component.Table.CustomTable MyAppointmentDetailsTable;
    private javax.swing.JPanel MyAppointmentDetailsTablePanel;
    private javax.swing.JScrollPane MyAppointmentDetailsTableScrollPane;
    private javax.swing.JLabel MyAppointmentTitleLabel;
    private component.Table.CustomTable MyNotificationsTable;
    private javax.swing.JPanel MyNotificationsTablePanel;
    private javax.swing.JScrollPane MyNotificationsTableScrollPane;
    private javax.swing.JButton NotificationsActionBtn;
    private javax.swing.JPanel NotificationsBoxPanel;
    private javax.swing.JLabel NotificationsTitleLabel;
    private javax.swing.JLabel NotificationsValueLabel;
    private component.Table.CustomTable RequiredDocumentsTable;
    private javax.swing.JPanel RequiredDocumentsTablePanel;
    private javax.swing.JScrollPane RequiredDocumentsTableScrollPane;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel searchLabel;
    // End of variables declaration//GEN-END:variables
}
