package component;

import backend.objects.Data;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class IDStatus extends javax.swing.JPanel {
    
    private Data.User user;
    private Data.Citizen citizen;
    private Data.IDStatus idStatus;
    
    public IDStatus(Data.User user) {
        this.user = user;
        initComponents();
        
        // Initialize the table model with proper columns
        initializeTableModel();
        
        loadCitizenData();
    }
    
    private void initializeTableModel() {
        // Create a proper table model with column names
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{}, // Empty data initially
            new String[] {"Date", "Status", "Description", "Notes / Updated By"}
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
        
        customTable.setModel(model);
        
        // Set column widths
        customTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        customTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        customTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        customTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        
        // Ensure the table is visible
        customTable.setVisible(true);
        jScrollPane1.setVisible(true);
    }
    
    private void loadCitizenData() {
        System.out.println("Loading citizen data for user ID: " + user.getUserId());
        
        // Get citizen associated with this user
        citizen = Data.Citizen.getCitizenByUserId(user.getUserId());
        
        if (citizen != null) {
            System.out.println("Citizen found: " + citizen.getFullName() + " (ID: " + citizen.getCitizenId() + ")");
            
            // Get the ID status for this citizen
            idStatus = Data.IDStatus.getStatusByCitizenId(citizen.getCitizenId());
            
            // Update labels with actual data
            if (citizen.getApplicationDate() != null) {
                String appDateStr = citizen.getApplicationDate().toString();
                String nationalId = citizen.getNationalId() != null ? citizen.getNationalId() : "N/A";
                jLabel4.setText("Application Date: " + appDateStr + " | National ID: " + nationalId);
            }
            
            // Set step labels for 5-step progress
            String[] stepLabels = {
                "Application Submitted",
                "Processing & Validation", 
                "Printing & Packaging",
                "Ready for Pickup",
                "ID Claimed"
            };
            
            // Ensure progress bar is initialized with 5 steps
            customStepProgressBar1.setTotalSteps(5);
            customStepProgressBar1.setStepLabels(stepLabels);
            
            // Check if ID is ready for pickup
            checkIDStatus();
            
            // Load live timeline data
            loadLiveTimeline();
            
        } else {
            // If no citizen data found
            System.out.println("No citizen data found for user ID: " + user.getUserId());
            jLabel4.setText("No application data found");
            customStepProgressBar1.setCurrentStep(0);
            loadDefaultTimeline();
        }
    }
    
    private void checkIDStatus() {
        if (idStatus != null) {
            System.out.println("ID Status found: " + idStatus.getStatus());
            String status = idStatus.getStatus();
            int currentStep = getStepFromStatus(status);
            
            // Update progress bar
            customStepProgressBar1.setCurrentStep(currentStep);
            
            // Update status label
            String displayStatus = formatStatusForDisplay(status);
            jLabel3.setText(displayStatus);
            
            // Set color based on status
            setStatusLabelColor(status);
            
            // Show/hide pickup panel based on status
            if ("READY_FOR_PICKUP".equalsIgnoreCase(status) || "READY".equalsIgnoreCase(status)) {
                showPickupPanel();
            } else if ("ID_CLAIMED".equalsIgnoreCase(status) || "CLAIMED".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)) {
                showClaimedMessage();
            } else {
                hidePickupPanel();
            }
            
        } else {
            // No status found
            System.out.println("No ID status found for citizen ID: " + citizen.getCitizenId());
            jLabel3.setText("APPLICATION SUBMITTED");
            jLabel3.setForeground(new java.awt.Color(0, 120, 215));
            customStepProgressBar1.setCurrentStep(1);
            hidePickupPanel();
        }
    }
    
    private int getStepFromStatus(String status) {
        if (status == null) return 1;
        
        switch (status.toUpperCase()) {
            case "SUBMITTED":
            case "PENDING":
                return 1;
                
            case "PROCESSING":
            case "VALIDATION":
            case "UNDER_REVIEW":
                return 2;
                
            case "PRINTING":
            case "PACKAGING":
            case "PRODUCTION":
                return 3;
                
            case "READY_FOR_PICKUP":
            case "READY":
                return 4;
                
            case "CLAIMED":
            case "ID_CLAIMED":
            case "COMPLETED":
                return 5;
                
            case "REJECTED":
            case "FAILED":
            case "CANCELLED":
                return 0; // Error state
                
            default:
                return 1;
        }
    }
    
    private String formatStatusForDisplay(String status) {
        if (status == null) return "APPLICATION SUBMITTED";
        
        switch (status.toUpperCase()) {
            case "SUBMITTED":
                return "APPLICATION SUBMITTED";
            case "PENDING":
                return "PENDING REVIEW";
            case "PROCESSING":
                return "PROCESSING & VALIDATION";
            case "VALIDATION":
                return "UNDER VALIDATION";
            case "UNDER_REVIEW":
                return "UNDER REVIEW";
            case "PRINTING":
                return "PRINTING & PACKAGING";
            case "PACKAGING":
                return "PACKAGING IN PROGRESS";
            case "PRODUCTION":
                return "IN PRODUCTION";
            case "READY_FOR_PICKUP":
                return "READY FOR PICKUP";
            case "READY":
                return "READY FOR PICKUP";
            case "CLAIMED":
            case "ID_CLAIMED":
                return "ID CLAIMED";
            case "COMPLETED":
                return "PROCESS COMPLETED";
            case "REJECTED":
                return "APPLICATION REJECTED";
            case "FAILED":
                return "PROCESSING FAILED";
            case "CANCELLED":
                return "APPLICATION CANCELLED";
            default:
                return status.replace("_", " ").toUpperCase();
        }
    }
    
    private void setStatusLabelColor(String status) {
        if (status == null) {
            jLabel3.setForeground(new java.awt.Color(0, 120, 215)); // Blue
            return;
        }
        
        switch (status.toUpperCase()) {
            case "SUBMITTED":
            case "PENDING":
                jLabel3.setForeground(new java.awt.Color(0, 120, 215)); // Blue
                break;
                
            case "PROCESSING":
            case "VALIDATION":
            case "UNDER_REVIEW":
            case "PRINTING":
            case "PACKAGING":
            case "PRODUCTION":
                jLabel3.setForeground(new java.awt.Color(255, 165, 0)); // Orange
                break;
                
            case "READY_FOR_PICKUP":
            case "READY":
                jLabel3.setForeground(new java.awt.Color(0, 150, 0)); // Green
                break;
                
            case "CLAIMED":
            case "ID_CLAIMED":
            case "COMPLETED":
                jLabel3.setForeground(new java.awt.Color(0, 100, 0)); // Dark Green
                break;
                
            case "REJECTED":
            case "FAILED":
            case "CANCELLED":
                jLabel3.setForeground(new java.awt.Color(220, 0, 0)); // Red
                break;
                
            default:
                jLabel3.setForeground(new java.awt.Color(100, 100, 100)); // Gray
        }
    }
    
    private void showPickupPanel() {
        jPanel1.setVisible(true);
        jLabel1.setText("Congratulations! Your physical National ID card is ready. Please schedule an appointment to claim your ID at a center near you.");
        SchedulePickup.setText("SCHEDULE MY PICKUP");
        SchedulePickup.setBackground(new java.awt.Color(0, 150, 0)); // Green
        revalidate();
        repaint();
    }
    
    private void showClaimedMessage() {
        jPanel1.setVisible(true);
        jLabel1.setText("Your National ID has been successfully claimed. Thank you for using our services!");
        SchedulePickup.setText("VIEW ID DETAILS");
        SchedulePickup.setBackground(new java.awt.Color(0, 120, 215)); // Blue
        
        // Update action for claimed ID
        SchedulePickup.removeActionListener(SchedulePickup.getActionListeners()[0]);
        SchedulePickup.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewIDDetailsActionPerformed(evt);
            }
        });
        revalidate();
        repaint();
    }
    
    private void hidePickupPanel() {
        jPanel1.setVisible(false);
        revalidate();
        repaint();
    }
    
    private void loadLiveTimeline() {
        System.out.println("Loading live timeline for citizen ID: " + citizen.getCitizenId());
        
        if (citizen == null) {
            System.out.println("Citizen is null, loading default timeline");
            loadDefaultTimeline();
            return;
        }

        try {
            // Get the table model
            DefaultTableModel model = (DefaultTableModel) customTable.getModel();
            model.setRowCount(0); // Clear existing data

            // Add application date
            if (citizen.getApplicationDate() != null) {
                model.addRow(new Object[]{
                    citizen.getApplicationDate().toString(),
                    "Application Submitted",
                    "National ID application submitted successfully",
                    "System"
                });
                System.out.println("Added application date: " + citizen.getApplicationDate());
            }

            // Get all status updates for this citizen
            List<Data.IDStatus> allStatuses = Data.IDStatus.getAllStatus();
            System.out.println("Total statuses in database: " + allStatuses.size());

            // Add status updates for this citizen
            int statusCount = 0;
            for (Data.IDStatus status : allStatuses) {
                if (status.getCitizenId() == citizen.getCitizenId()) {
                    statusCount++;
                    String statusDesc = formatStatusForDisplay(status.getStatus());
                    model.addRow(new Object[]{
                        status.getUpdateDate() != null ? status.getUpdateDate().toString() : "",
                        statusDesc,
                        getStatusDescription(status.getStatus()),
                        status.getNotes() != null && !status.getNotes().isEmpty() ? 
                            status.getNotes() : "System Update"
                    });
                    System.out.println("Added status: " + status.getStatus() + " for citizen ID: " + citizen.getCitizenId());
                }
            }
            System.out.println("Found " + statusCount + " status entries for this citizen");

            // Add appointment information
            Data.Appointment appointment = Data.Appointment.getAppointmentByCitizenId(citizen.getCitizenId());
            if (appointment != null) {
                model.addRow(new Object[]{
                    appointment.getAppDate() != null ? appointment.getAppDate().toString() : "",
                    "Appointment " + appointment.getStatus(),
                    "ID pickup appointment",
                    "Scheduled by: " + (appointment.getStatus().equals("Scheduled") ? "You" : "System")
                });
                System.out.println("Added appointment: " + appointment.getAppDate());
            }

            // Add document submissions
            List<Data.Document> documents = Data.Document.getDocumentsByCitizenId(citizen.getCitizenId());
            System.out.println("Found " + documents.size() + " documents for this citizen");
            
            for (Data.Document doc : documents) {
                if ("Yes".equals(doc.getSubmitted()) || "Verified".equals(doc.getStatus())) {
                    model.addRow(new Object[]{
                        doc.getUploadDate() != null ? doc.getUploadDate().toString() : "",
                        "Document Submitted",
                        doc.getDocumentName() + " uploaded",
                        "Status: " + doc.getStatus()
                    });
                    System.out.println("Added document: " + doc.getDocumentName());
                }
            }

            // If no timeline data, show default based on current status
            if (model.getRowCount() == 0) {
                System.out.println("No timeline data found, showing default");
                String[][] defaultData = getDefaultTimelineBasedOnStatus();
                for (String[] row : defaultData) {
                    model.addRow(row);
                }
            }

            System.out.println("Total rows in table: " + model.getRowCount());
            
            // Refresh the table
            model.fireTableDataChanged();
            customTable.revalidate();
            customTable.repaint();
            jScrollPane1.revalidate();
            jScrollPane1.repaint();

        } catch (Exception e) {
            System.err.println("Error loading live timeline: " + e.getMessage());
            e.printStackTrace();
            
            // Show error in table
            DefaultTableModel model = (DefaultTableModel) customTable.getModel();
            model.setRowCount(0);
            model.addRow(new Object[]{"Error", "Database Error", e.getMessage(), "System"});
            model.fireTableDataChanged();
        }
    }
    
    private String[][] getDefaultTimelineBasedOnStatus() {
        String currentStatus = idStatus != null ? idStatus.getStatus() : null;
        int currentStep = getStepFromStatus(currentStatus);
        System.out.println("Current status: " + currentStatus + ", Step: " + currentStep);
        
        switch (currentStep) {
            case 5: // ID Claimed - Complete timeline
                return new String[][] {
                    {"2024-01-15", "Submitted", "Application submitted online", "System"},
                    {"2024-01-18", "Processing", "Documents verified and validated", "Officer Smith"},
                    {"2024-01-25", "Printing", "ID card sent for printing", "System"},
                    {"2024-02-01", "Ready", "Card ready for pickup", "Officer Johnson"},
                    {"2024-02-05", "Claimed", "ID collected at center", "Center Staff"}
                };
                
            case 4: // Ready for Pickup
                return new String[][] {
                    {"2024-01-15", "Submitted", "Application submitted online", "System"},
                    {"2024-01-18", "Processing", "Documents verified and validated", "Officer Smith"},
                    {"2024-01-25", "Printing", "ID card sent for printing", "System"},
                    {"2024-02-01", "Ready", "Card ready for pickup", "Officer Johnson"},
                    {"", "Next Step", "Schedule pickup appointment", ""}
                };
                
            case 3: // Printing & Packaging
                return new String[][] {
                    {"2024-01-15", "Submitted", "Application submitted online", "System"},
                    {"2024-01-18", "Processing", "Documents verified and validated", "Officer Smith"},
                    {"2024-01-25", "Printing", "ID card printing in progress", "System"},
                    {"", "Next Step", "Awaiting packaging completion", ""},
                    {"", "Future Step", "Ready for pickup notification", ""}
                };
                
            case 2: // Processing & Validation
                return new String[][] {
                    {"2024-01-15", "Submitted", "Application submitted online", "System"},
                    {"2024-01-18", "Processing", "Documents under review", "Officer Smith"},
                    {"", "Next Step", "Background verification", ""},
                    {"", "Future Step", "Approval for printing", ""},
                    {"", "Future Step", "Printing and packaging", ""}
                };
                
            case 1: // Application Submitted
            default:
                return new String[][] {
                    {"2024-01-15", "Submitted", "Application submitted online", "System"},
                    {"", "Next Step", "Document verification", ""},
                    {"", "Future Step", "Background check", ""},
                    {"", "Future Step", "Approval process", ""},
                    {"", "Future Step", "Card production", ""}
                };
        }
    }
    
    private String getStatusDescription(String status) {
        if (status == null) return "No status available";
        
        switch (status.toUpperCase()) {
            case "SUBMITTED":
                return "Application received and registered in the system";
            case "PROCESSING":
                return "Documents are being reviewed and validated";
            case "PRINTING":
                return "Physical ID card is being printed";
            case "READY_FOR_PICKUP":
                return "ID card is ready for pickup at the center";
            case "ID_CLAIMED":
                return "ID card has been collected by the applicant";
            case "REJECTED":
                return "Application did not meet requirements";
            default:
                return "Status update: " + status;
        }
    }
    
    private void loadDefaultTimeline() {
        System.out.println("Loading default timeline");
        
        DefaultTableModel model = (DefaultTableModel) customTable.getModel();
        model.setRowCount(0);
        
        String[][] timelineData = {
            {"", "No application data", "Please submit an application first", ""},
            {"", "Step 1", "Submit application online", ""},
            {"", "Step 2", "Document verification", ""},
            {"", "Step 3", "Background check", ""},
            {"", "Step 4", "Card production", ""}
        };
        
        for (String[] row : timelineData) {
            model.addRow(row);
        }
        
        model.fireTableDataChanged();
        customTable.revalidate();
        customTable.repaint();
        jScrollPane1.revalidate();
        jScrollPane1.repaint();
        
        System.out.println("Default timeline loaded with " + timelineData.length + " rows");
    }
    
    private void viewIDDetailsActionPerformed(java.awt.event.ActionEvent evt) {
        // Show ID details
        Data.Appointment appointment = Data.Appointment.getAppointmentByCitizenId(citizen.getCitizenId());
        
        StringBuilder details = new StringBuilder();
        details.append("ID Details:\n");
        details.append("----------------\n");
        details.append("Name: ").append(citizen.getFullName()).append("\n");
        details.append("National ID: ").append(citizen.getNationalId()).append("\n");
        details.append("Date of Birth: ").append(citizen.getBirthDate()).append("\n");
        details.append("Address: ").append(citizen.getAddress()).append("\n");
        
        if (appointment != null) {
            details.append("\nClaim Details:\n");
            details.append("----------------\n");
            details.append("Claim Date: ").append(appointment.getAppDate()).append("\n");
            details.append("Claim Time: ").append(appointment.getAppTime()).append("\n");
            details.append("Status: ").append(appointment.getStatus()).append("\n");
        }
        
        javax.swing.JOptionPane.showMessageDialog(this,
            details.toString(),
            "National ID Details",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Add method to refresh status (can be called from outside)
    public void refreshData() {
        System.out.println("Refreshing ID Status data...");
        loadCitizenData();
    }
    
    // Also add a public getter for debugging
    public void debugInfo() {
        System.out.println("=== IDStatus Debug Info ===");
        System.out.println("User: " + (user != null ? user.getUsername() : "null"));
        System.out.println("Citizen: " + (citizen != null ? citizen.getFullName() : "null"));
        System.out.println("ID Status: " + (idStatus != null ? idStatus.getStatus() : "null"));
        System.out.println("Table visible: " + customTable.isVisible());
        System.out.println("ScrollPane visible: " + jScrollPane1.isVisible());
        System.out.println("Table model rows: " + customTable.getModel().getRowCount());
        System.out.println("===========================");
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        SchedulePickup = new javax.swing.JButton();
        customStepProgressBar1 = new component.Progress.CustomStepProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        customTable = new component.Table.CustomTable();
        jLabel5 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(850, 550));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 120, 215));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("NATIONAL ID APPLICATION STATUS");
        jLabel2.setPreferredSize(new java.awt.Dimension(850, 25));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 150, 0));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("READY FOR PICKUP");
        jLabel3.setPreferredSize(new java.awt.Dimension(850, 50));

        jLabel4.setForeground(new java.awt.Color(100, 100, 100));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Application Date: 2024-1-15 | TRN: 1234-5678-9123-4567-8912-4567-8912-34");
        jLabel4.setPreferredSize(new java.awt.Dimension(850, 25));

        jSeparator1.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator1.setForeground(new java.awt.Color(150, 150, 150));
        jSeparator1.setMinimumSize(new java.awt.Dimension(800, 10));
        jSeparator1.setPreferredSize(new java.awt.Dimension(800, 10));

        jPanel1.setBackground(new java.awt.Color(150, 220, 240));
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 125));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 120, 215));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Congratulations! Your physical National ID card is ready Please schedule an appointment to claim your ID at a center near you.");

        SchedulePickup.setBackground(new java.awt.Color(0, 150, 0));
        SchedulePickup.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        SchedulePickup.setForeground(new java.awt.Color(255, 255, 255));
        SchedulePickup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar.png"))); // NOI18N
        SchedulePickup.setText("SCHEDULE MY PICKUP");
        SchedulePickup.setPreferredSize(new java.awt.Dimension(200, 35));
        SchedulePickup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SchedulePickupActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(SchedulePickup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel1)
                .addGap(25, 25, 25)
                .addComponent(SchedulePickup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));

        customTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Date", "Status", "Description", "Notes / Updated By"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(customTable);
        if (customTable.getColumnModel().getColumnCount() > 0) {
            customTable.getColumnModel().getColumn(0).setResizable(false);
            customTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            customTable.getColumnModel().getColumn(1).setResizable(false);
            customTable.getColumnModel().getColumn(1).setPreferredWidth(80);
            customTable.getColumnModel().getColumn(2).setResizable(false);
            customTable.getColumnModel().getColumn(2).setPreferredWidth(200);
            customTable.getColumnModel().getColumn(3).setResizable(false);
            customTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        }

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 120, 215));
        jLabel5.setText("FULL STATUS HISTORY");
        jLabel5.setPreferredSize(new java.awt.Dimension(850, 25));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(25, 25, 25))
            .addComponent(customStepProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(customStepProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void SchedulePickupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SchedulePickupActionPerformed
        // Check if there's already an appointment
        Data.Appointment existingAppointment = Data.Appointment.getAppointmentByCitizenId(citizen.getCitizenId());
        
        if (existingAppointment != null && "SCHEDULED".equalsIgnoreCase(existingAppointment.getStatus())) {
            // Show existing appointment details
            javax.swing.JOptionPane.showMessageDialog(this,
                "You already have a scheduled appointment:\n\n" +
                "Date: " + existingAppointment.getAppDate() + "\n" +
                "Time: " + existingAppointment.getAppTime() + "\n\n" +
                "Would you like to reschedule?",
                "Existing Appointment Found",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
            // TODO: Navigate to rescheduling page
        } else {
            // Navigate to scheduling page
            javax.swing.JOptionPane.showMessageDialog(this,
                "Redirecting to scheduling page...\n\n" +
                "Citizen: " + citizen.getFullName() + "\n" +
                "National ID: " + citizen.getNationalId(),
                "Schedule Pickup",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
            // In your Main class, you would call:
            // main.showForm(new Scheduling(user));
        }
    }//GEN-LAST:event_SchedulePickupActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton SchedulePickup;
    private component.Progress.CustomStepProgressBar customStepProgressBar1;
    private component.Table.CustomTable customTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}
