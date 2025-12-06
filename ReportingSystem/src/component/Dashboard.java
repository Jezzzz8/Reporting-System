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
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JTable;

public class Dashboard extends javax.swing.JPanel {
    
    private User currentUser;
    private Citizen currentCitizen;
    private int currentView = 1; // 1=Application Timeline, 2=Appointment, 3=Documents, 4=Notifications

    public Dashboard(User user) {
        this.currentUser = user;
        initComponents();
        customizeTableHeaders(); // Add this line to customize table headers
        testDatabaseConnection();
        enhanceDashboard();
        loadCitizenData();
        loadDashboardData();
        loadApplicationTimelineTable(); // Default view
        setupSearchFunctionality(); // Add search functionality
    }
    
    private void customizeTableHeaders() {
        // Create a custom header renderer with the desired color
        TableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(new Color(142, 217, 255)); // Set header background color
                setForeground(Color.BLACK); // Set header text color
                setFont(new Font("Times New Roman", Font.BOLD, 12)); // Set header font
                setHorizontalAlignment(CENTER); // Center align header text
                return this;
            }
        };
        
        // Apply the custom header renderer to all tables
        if (ApplicationTimelineTable != null) {
            JTableHeader header = ApplicationTimelineTable.getTableHeader();
            header.setDefaultRenderer(headerRenderer);
            header.setReorderingAllowed(false);
        }
        
        if (MyAppointmentDetailsTable != null) {
            JTableHeader header = MyAppointmentDetailsTable.getTableHeader();
            header.setDefaultRenderer(headerRenderer);
            header.setReorderingAllowed(false);
        }
        
        if (RequiredDocumentsTable != null) {
            JTableHeader header = RequiredDocumentsTable.getTableHeader();
            header.setDefaultRenderer(headerRenderer);
            header.setReorderingAllowed(false);
        }
        
        if (MyNotificationsTable != null) {
            JTableHeader header = MyNotificationsTable.getTableHeader();
            header.setDefaultRenderer(headerRenderer);
            header.setReorderingAllowed(false);
        }
    }
    
    // Alternative method with more customization options
    private void customizeTableHeadersEnhanced() {
        // Custom renderer with the specified color
        TableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Set the specific color (142, 217, 255)
                Color headerColor = new Color(142, 217, 255);
                setBackground(headerColor);
                
                // Customize other header properties
                setForeground(Color.BLACK); // Black text
                setFont(new Font("Times New Roman", Font.BOLD, 12));
                setHorizontalAlignment(CENTER);
                
                // Add a border if desired
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 1, Color.WHITE),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                
                return this;
            }
        };
        
        // Apply to all tables
        applyHeaderRenderer(ApplicationTimelineTable, headerRenderer);
        applyHeaderRenderer(MyAppointmentDetailsTable, headerRenderer);
        applyHeaderRenderer(RequiredDocumentsTable, headerRenderer);
        applyHeaderRenderer(MyNotificationsTable, headerRenderer);
    }
    
    private void applyHeaderRenderer(javax.swing.JTable table, TableCellRenderer renderer) {
        if (table != null) {
            JTableHeader header = table.getTableHeader();
            header.setDefaultRenderer(renderer);
            
            // Additional header settings
            header.setReorderingAllowed(false);
            header.setResizingAllowed(true);
            
            // Set header height
            header.setPreferredSize(new Dimension(header.getWidth(), 35));
        }
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
                showApplicationTimeline();
            });
        }
        
        if (DaySinceApplicationActionBtn != null) {
            DaySinceApplicationActionBtn.addActionListener((ActionEvent e) -> {
                showDaysSinceApplication();
            });
        }
        
        if (MyAppointmentActionBtn != null) {
            MyAppointmentActionBtn.addActionListener((ActionEvent e) -> {
                showAppointmentDetails();
            });
        }
        
        if (NotificationsActionBtn != null) {
            NotificationsActionBtn.addActionListener((ActionEvent e) -> {
                showNotifications();
            });
        }
        
        // Add document view button if exists
        if (RequiredDocumentsTablePanel != null) {
            // You can add a separate button for documents or use existing ones
        }
    }
    
    private void setupSearchFunctionality() {
        searchField.addActionListener((ActionEvent e) -> {
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
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
        DefaultTableModel model = (DefaultTableModel) ApplicationTimelineTable.getModel();
        model.setRowCount(0);
        currentView = 1;

        try {
            if (currentCitizen == null) {
                model.addRow(new Object[]{"No citizen data found", "", "", "", ""});
                return;
            }
            
            // Row 1: Application Submitted
            model.addRow(new Object[]{
                formatDate(currentCitizen.getApplicationDate()),
                "Application Submitted",
                "Your National ID application has been submitted",
                "System",
                "Awaiting processing"
            });

            // Get status history
            IDStatus status = Data.IDStatus.getStatusByCitizenId(currentCitizen.getCitizenId());
            if (status != null) {
                model.addRow(new Object[]{
                    formatDate(status.getUpdateDate()),
                    status.getStatus(),
                    "Application status updated",
                    "PSA Staff",
                    status.getNotes()
                });
            }

            // Get appointment if exists
            Appointment appointment = Data.Appointment.getAppointmentByCitizenId(currentCitizen.getCitizenId());
            if (appointment != null) {
                model.addRow(new Object[]{
                    formatDate(appointment.getCreatedDate()),
                    "Appointment Scheduled",
                    "Appointment scheduled for ID pickup",
                    "You",
                    "Time: " + appointment.getAppTime()
                });
            }

            // Get additional status history if available
            List<IDStatus> allStatus = Data.IDStatus.getAllStatus();
            for (IDStatus stat : allStatus) {
                if (stat.getCitizenId() == currentCitizen.getCitizenId()) {
                    // Skip if already added
                    if (status != null && stat.getStatusId() == status.getStatusId()) {
                        continue;
                    }
                    model.addRow(new Object[]{
                        formatDate(stat.getUpdateDate()),
                        stat.getStatus(),
                        "Previous status update",
                        "PSA Staff",
                        stat.getNotes()
                    });
                }
            }

            if (model.getRowCount() == 0) {
                model.addRow(new Object[]{"No timeline data available", "", "", "", ""});
            }

        } catch (Exception e) {
            System.err.println("Error loading application timeline: " + e.getMessage());
            model.addRow(new Object[]{"Error loading data", "", "", "", ""});
        } finally {
            model.fireTableDataChanged();
            ApplicationTimelineTable.revalidate();
            ApplicationTimelineTable.repaint();
            
            // Ensure header styling is applied
            customizeTableHeaders();
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
                
                model.addRow(new Object[]{
                    dateFormat.format(appointment.getAppDate()),
                    appointment.getAppTime(),
                    appointment.getStatus(),
                    "ID Pickup",
                    "PSA Office - Main Branch",
                    getAppointmentActions(appointment)
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
                            "Completed"
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
                    "Schedule"
                });
            }

        } catch (Exception e) {
            System.err.println("Error loading appointment details: " + e.getMessage());
            model.addRow(new Object[]{"Error loading data", "", "", "", "", ""});
        } finally {
            model.fireTableDataChanged();
            MyAppointmentDetailsTable.revalidate();
            MyAppointmentDetailsTable.repaint();
            
            // Ensure header styling is applied
            customizeTableHeaders();
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
            
            // Ensure header styling is applied
            customizeTableHeaders();
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
            
            // Ensure header styling is applied
            customizeTableHeaders();
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
    
    private String getAppointmentActions(Appointment appointment) {
        if ("Scheduled".equals(appointment.getStatus())) {
            return "Reschedule/Cancel";
        } else if ("Completed".equals(appointment.getStatus())) {
            return "Completed";
        } else {
            return appointment.getStatus();
        }
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
    private void showApplicationTimeline() {
        switchTableVisibility(1);
        loadApplicationTimelineTable();
    }

    private void showDaysSinceApplication() {
        switchTableVisibility(1);
        loadApplicationTimelineTable();
    }

    private void showAppointmentDetails() {
        switchTableVisibility(2);
        loadAppointmentDetailsTable();
    }
    
    private void showDocuments() {
        switchTableVisibility(3);
        loadRequiredDocumentsTable();
    }

    private void showNotifications() {
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
                break;
            case 2:
                MyAppointmentDetailsTablePanel.setVisible(true);
                break;
            case 3:
                RequiredDocumentsTablePanel.setVisible(true);
                break;
            case 4:
                MyNotificationsTablePanel.setVisible(true);
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel5 = new javax.swing.JPanel();
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
        ApplicationTimelineTable = new javax.swing.JTable();
        MyAppointmentDetailsTablePanel = new javax.swing.JPanel();
        MyAppointmentDetailsTableScrollPane = new javax.swing.JScrollPane();
        MyAppointmentDetailsTable = new javax.swing.JTable();
        RequiredDocumentsTablePanel = new javax.swing.JPanel();
        RequiredDocumentsTableScrollPane = new javax.swing.JScrollPane();
        RequiredDocumentsTable = new javax.swing.JTable();
        MyNotificationsTablePanel = new javax.swing.JPanel();
        MyNotificationsTableScrollPane = new javax.swing.JScrollPane();
        MyNotificationsTable = new javax.swing.JTable();

        setBackground(new java.awt.Color(250, 250, 250));
        setPreferredSize(new java.awt.Dimension(850, 550));

        jPanel5.setBackground(new java.awt.Color(250, 250, 250));

        MyApplicationStatusBoxPanel.setBackground(new java.awt.Color(254, 161, 156));
        MyApplicationStatusBoxPanel.setPreferredSize(new java.awt.Dimension(150, 150));

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

        MyApplicationStatusActionBtn.setBackground(new java.awt.Color(41, 128, 185));
        MyApplicationStatusActionBtn.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        MyApplicationStatusActionBtn.setForeground(new java.awt.Color(250, 250, 250));
        MyApplicationStatusActionBtn.setText("More Details");
        MyApplicationStatusActionBtn.setPreferredSize(new java.awt.Dimension(140, 22));
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
                .addGroup(MyApplicationStatusBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MyApplicationStatusBoxPanelLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(MyApplicationStatusValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(MyApplicationStatusBoxPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(MyApplicationStatusTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(MyApplicationStatusBoxPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(MyApplicationStatusActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        MyApplicationStatusBoxPanelLayout.setVerticalGroup(
            MyApplicationStatusBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MyApplicationStatusBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MyApplicationStatusValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(MyApplicationStatusTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(MyApplicationStatusActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        DaySinceApplicationBoxPanel.setBackground(new java.awt.Color(249, 254, 156));
        DaySinceApplicationBoxPanel.setPreferredSize(new java.awt.Dimension(150, 150));

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

        DaySinceApplicationActionBtn.setBackground(new java.awt.Color(41, 128, 185));
        DaySinceApplicationActionBtn.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        DaySinceApplicationActionBtn.setForeground(new java.awt.Color(250, 250, 250));
        DaySinceApplicationActionBtn.setText("More Details");
        DaySinceApplicationActionBtn.setPreferredSize(new java.awt.Dimension(140, 22));
        DaySinceApplicationActionBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DaySinceApplicationActionBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout DaySinceApplicationBoxPanelLayout = new javax.swing.GroupLayout(DaySinceApplicationBoxPanel);
        DaySinceApplicationBoxPanel.setLayout(DaySinceApplicationBoxPanelLayout);
        DaySinceApplicationBoxPanelLayout.setHorizontalGroup(
            DaySinceApplicationBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DaySinceApplicationBoxPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(DaySinceApplicationValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
            .addGroup(DaySinceApplicationBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DaySinceApplicationBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DaySinceApplicationTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DaySinceApplicationActionBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        DaySinceApplicationBoxPanelLayout.setVerticalGroup(
            DaySinceApplicationBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DaySinceApplicationBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DaySinceApplicationValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(DaySinceApplicationTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(DaySinceApplicationActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        MyAppointmentBoxPanel.setBackground(new java.awt.Color(200, 254, 156));
        MyAppointmentBoxPanel.setPreferredSize(new java.awt.Dimension(150, 150));

        MyAppointmentCountLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        MyAppointmentCountLabel.setForeground(new java.awt.Color(25, 25, 25));
        MyAppointmentCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        MyAppointmentCountLabel.setText("0");
        MyAppointmentCountLabel.setToolTipText("");
        MyAppointmentCountLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        MyAppointmentActionBtn.setBackground(new java.awt.Color(41, 128, 185));
        MyAppointmentActionBtn.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        MyAppointmentActionBtn.setForeground(new java.awt.Color(250, 250, 250));
        MyAppointmentActionBtn.setText("More Details");
        MyAppointmentActionBtn.setPreferredSize(new java.awt.Dimension(140, 22));
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
                .addGroup(MyAppointmentBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MyAppointmentBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MyAppointmentBoxPanelLayout.createSequentialGroup()
                            .addComponent(MyAppointmentCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(25, 25, 25))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MyAppointmentBoxPanelLayout.createSequentialGroup()
                            .addComponent(MyAppointmentActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap()))
                    .addComponent(MyAppointmentTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        MyAppointmentBoxPanelLayout.setVerticalGroup(
            MyAppointmentBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MyAppointmentBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MyAppointmentCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(MyAppointmentTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(MyAppointmentActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        NotificationsBoxPanel.setBackground(new java.awt.Color(156, 200, 254));
        NotificationsBoxPanel.setPreferredSize(new java.awt.Dimension(150, 150));

        NotificationsValueLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        NotificationsValueLabel.setForeground(new java.awt.Color(25, 25, 25));
        NotificationsValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        NotificationsValueLabel.setText("0");
        NotificationsValueLabel.setToolTipText("");
        NotificationsValueLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        NotificationsActionBtn.setBackground(new java.awt.Color(41, 128, 185));
        NotificationsActionBtn.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        NotificationsActionBtn.setForeground(new java.awt.Color(250, 250, 250));
        NotificationsActionBtn.setText("More Details");
        NotificationsActionBtn.setPreferredSize(new java.awt.Dimension(140, 22));
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, NotificationsBoxPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(NotificationsValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
            .addGroup(NotificationsBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(NotificationsBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(NotificationsActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NotificationsTitleLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        NotificationsBoxPanelLayout.setVerticalGroup(
            NotificationsBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NotificationsBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(NotificationsValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(NotificationsTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(NotificationsActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
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
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(NotificationsBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DaySinceApplicationBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MyApplicationStatusBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MyAppointmentBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        searchField.setToolTipText("Search");
        searchField.setPreferredSize(new java.awt.Dimension(150, 22));
        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });

        searchLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        searchLabel.setForeground(new java.awt.Color(25, 25, 25));
        searchLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        searchLabel.setText("Search:");
        searchLabel.setToolTipText("");
        searchLabel.setPreferredSize(new java.awt.Dimension(140, 43));

        DashboardCitizenTable.setLayout(new javax.swing.OverlayLayout(DashboardCitizenTable));

        ApplicationTimelineTablePanel.setPreferredSize(new java.awt.Dimension(812, 242));

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
        ApplicationTimelineTable.setRowHeight(30);
        ApplicationTimelineTable.setRowMargin(1);
        ApplicationTimelineTable.setShowGrid(true);
        ApplicationTimelineTable.getTableHeader().setResizingAllowed(false);
        ApplicationTimelineTable.getTableHeader().setReorderingAllowed(false);
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
            .addGroup(ApplicationTimelineTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ApplicationTimelineTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 826, Short.MAX_VALUE)
                .addContainerGap())
        );
        ApplicationTimelineTablePanelLayout.setVerticalGroup(
            ApplicationTimelineTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ApplicationTimelineTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ApplicationTimelineTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addContainerGap())
        );

        DashboardCitizenTable.add(ApplicationTimelineTablePanel);

        MyAppointmentDetailsTablePanel.setPreferredSize(new java.awt.Dimension(812, 242));

        MyAppointmentDetailsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Date", "Time", "Status", "Purpose", "Location", "Actions"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        MyAppointmentDetailsTable.setRowHeight(30);
        MyAppointmentDetailsTable.setRowMargin(1);
        MyAppointmentDetailsTable.setShowGrid(true);
        MyAppointmentDetailsTable.getTableHeader().setResizingAllowed(false);
        MyAppointmentDetailsTable.getTableHeader().setReorderingAllowed(false);
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
            .addGroup(MyAppointmentDetailsTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MyAppointmentDetailsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 826, Short.MAX_VALUE)
                .addContainerGap())
        );
        MyAppointmentDetailsTablePanelLayout.setVerticalGroup(
            MyAppointmentDetailsTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MyAppointmentDetailsTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MyAppointmentDetailsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addContainerGap())
        );

        DashboardCitizenTable.add(MyAppointmentDetailsTablePanel);

        RequiredDocumentsTablePanel.setPreferredSize(new java.awt.Dimension(812, 242));

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
        RequiredDocumentsTable.setRowHeight(30);
        RequiredDocumentsTable.setRowMargin(1);
        RequiredDocumentsTable.setShowGrid(true);
        RequiredDocumentsTable.getTableHeader().setResizingAllowed(false);
        RequiredDocumentsTable.getTableHeader().setReorderingAllowed(false);
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
            .addGroup(RequiredDocumentsTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(RequiredDocumentsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 826, Short.MAX_VALUE)
                .addContainerGap())
        );
        RequiredDocumentsTablePanelLayout.setVerticalGroup(
            RequiredDocumentsTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RequiredDocumentsTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(RequiredDocumentsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addContainerGap())
        );

        DashboardCitizenTable.add(RequiredDocumentsTablePanel);

        MyNotificationsTablePanel.setPreferredSize(new java.awt.Dimension(812, 242));

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
        MyNotificationsTable.setRowHeight(30);
        MyNotificationsTable.setRowMargin(1);
        MyNotificationsTable.setShowGrid(true);
        MyNotificationsTable.getTableHeader().setResizingAllowed(false);
        MyNotificationsTable.getTableHeader().setReorderingAllowed(false);
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
            MyNotificationsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        }

        javax.swing.GroupLayout MyNotificationsTablePanelLayout = new javax.swing.GroupLayout(MyNotificationsTablePanel);
        MyNotificationsTablePanel.setLayout(MyNotificationsTablePanelLayout);
        MyNotificationsTablePanelLayout.setHorizontalGroup(
            MyNotificationsTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MyNotificationsTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MyNotificationsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 826, Short.MAX_VALUE)
                .addContainerGap())
        );
        MyNotificationsTablePanelLayout.setVerticalGroup(
            MyNotificationsTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MyNotificationsTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MyNotificationsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addContainerGap())
        );

        DashboardCitizenTable.add(MyNotificationsTablePanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(50, 50, 50))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DashboardCitizenTable, javax.swing.GroupLayout.DEFAULT_SIZE, 838, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(searchLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 154, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DashboardCitizenTable, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void MyApplicationStatusActionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MyApplicationStatusActionBtnActionPerformed
        showApplicationTimeline();
    }//GEN-LAST:event_MyApplicationStatusActionBtnActionPerformed

    private void DaySinceApplicationActionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DaySinceApplicationActionBtnActionPerformed
        showDaysSinceApplication();
    }//GEN-LAST:event_DaySinceApplicationActionBtnActionPerformed

    private void MyAppointmentActionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MyAppointmentActionBtnActionPerformed
        showAppointmentDetails();
    }//GEN-LAST:event_MyAppointmentActionBtnActionPerformed

    private void NotificationsActionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NotificationsActionBtnActionPerformed
        showNotifications();
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
    private javax.swing.JTable ApplicationTimelineTable;
    private javax.swing.JPanel ApplicationTimelineTablePanel;
    private javax.swing.JScrollPane ApplicationTimelineTableScrollPane;
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
    private javax.swing.JTable MyAppointmentDetailsTable;
    private javax.swing.JPanel MyAppointmentDetailsTablePanel;
    private javax.swing.JScrollPane MyAppointmentDetailsTableScrollPane;
    private javax.swing.JLabel MyAppointmentTitleLabel;
    private javax.swing.JTable MyNotificationsTable;
    private javax.swing.JPanel MyNotificationsTablePanel;
    private javax.swing.JScrollPane MyNotificationsTableScrollPane;
    private javax.swing.JButton NotificationsActionBtn;
    private javax.swing.JPanel NotificationsBoxPanel;
    private javax.swing.JLabel NotificationsTitleLabel;
    private javax.swing.JLabel NotificationsValueLabel;
    private javax.swing.JTable RequiredDocumentsTable;
    private javax.swing.JPanel RequiredDocumentsTablePanel;
    private javax.swing.JScrollPane RequiredDocumentsTableScrollPane;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel searchLabel;
    // End of variables declaration//GEN-END:variables
}
