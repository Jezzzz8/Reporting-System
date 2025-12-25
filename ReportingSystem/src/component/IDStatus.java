package component;

import backend.objects.Data;
import component.Button.FlatButton;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import sys.main.Main;

public class IDStatus extends javax.swing.JPanel {
    
    private Data.User user;
    private Data.Citizen citizen;
    private Data.IDStatus idStatus;
    private Data.Address address;
    private javax.swing.Timer refreshTimer;
    
    public IDStatus(Data.User user) {
        this.user = user;
        initComponents();

        System.out.println("IDStatus created successfully");
        
        // Initialize components before using them
        initializeComponents();
        
        loadCitizenData();
        
        // Set up auto-refresh timer (every 30 seconds)
        setupRefreshTimer();
    }

    private void initializeComponents() {
        customStepProgressBar1.setTotalSteps(5); // Updated to 5 simplified steps
        String[] stepLabels = {
            "Application Submitted",
            "Processing & Validation", 
            "Printing & Packaging",
            "Ready for Pickup",
            "ID Claimed"
        };
        customStepProgressBar1.setStepLabels(stepLabels);
        
        // Initialize the table model
        DefaultTableModel model = (DefaultTableModel) FullStatusHistoryTable.getModel();
        model.setColumnIdentifiers(new String[]{"Date", "Status", "Description", "Notes / Updated By"});
    }

    private void setupRefreshTimer() {
        refreshTimer = new javax.swing.Timer(30000, e -> refreshData()); // Refresh every 30 seconds
        refreshTimer.setRepeats(true);
        refreshTimer.start();
        System.out.println("Auto-refresh timer started (every 30 seconds)");
    }

    private void loadCitizenData() {
        System.out.println("Loading citizen data for user ID: " + user.getUserId());

        citizen = Data.Citizen.getCitizenByUserId(user.getUserId());

        if (citizen != null) {
            System.out.println("Citizen found: " + citizen.getFullName() + " (ID: " + citizen.getCitizenId() + ")");

            // Load address information
            address = Data.Address.getAddressByCitizenId(citizen.getCitizenId());

            // IMPORTANT: Always get fresh status from database
            refreshIDStatus();
            
            // Update labels with actual data
            updateDisplayData();

        } else {
            // If no citizen data found
            System.out.println("No citizen data found for user ID: " + user.getUserId());
            jLabel4.setText("No application data found");
            customStepProgressBar1.setCurrentStep(0);
            loadDefaultTimeline();
        }
    }
    
    // NEW METHOD: Get fresh status from database
    private void refreshIDStatus() {
        if (citizen == null) {
            System.out.println("refreshIDStatus: citizen is null");
            return;
        }

        System.out.println("refreshIDStatus: Getting status for citizen ID: " + citizen.getCitizenId());

        // Get current status from database
        idStatus = Data.IDStatus.getStatusByCitizenId(citizen.getCitizenId());

        if (idStatus != null) {
            System.out.println("refreshIDStatus: Current status found:");
            System.out.println("  - Status ID: " + idStatus.getStatusId());
            System.out.println("  - Status: " + idStatus.getStatus());
            System.out.println("  - Status Name ID: " + idStatus.getStatusNameId());
            System.out.println("  - Transaction ID: " + idStatus.getTransactionId());
            System.out.println("  - Update Date: " + idStatus.getUpdateDate());
            System.out.println("  - Notes: " + (idStatus.getNotes() != null ? idStatus.getNotes().substring(0, Math.min(50, idStatus.getNotes().length())) + "..." : "null"));

            // Debug: Also check what status name this ID maps to
            Data.StatusName statusName = Data.StatusName.getStatusNameById(idStatus.getStatusNameId());
            if (statusName != null) {
                System.out.println("  - Mapped Status Name: " + statusName.getStatusName());
            } else {
                System.out.println("  - ERROR: No status name found for ID: " + idStatus.getStatusNameId());
            }
        } else {
            System.out.println("refreshIDStatus: No status found for citizen ID: " + citizen.getCitizenId());
        }
    }
    // NEW METHOD: Update display data
    private void updateDisplayData() {
        if (citizen.getApplicationDate() != null) {
            String appDateStr = citizen.getApplicationDate().toString();
            String transactionId = "No Transaction ID";

            // Get and format transaction ID
            if (idStatus != null) {
                String rawTransactionId = idStatus.getTransactionId();
                if (rawTransactionId != null && !rawTransactionId.trim().isEmpty()) {
                    transactionId = Data.IDStatus.formatTransactionId(rawTransactionId);
                    System.out.println("Raw Transaction ID: " + rawTransactionId);
                    System.out.println("Formatted Transaction ID: " + transactionId);
                }
            }

            // Show full information including application date and transaction ID
            jLabel4.setText("Application Date: " + appDateStr + " | Transaction ID: " + transactionId);
        }

        System.out.println("Progress bar initialized with 5 steps");

        checkIDStatus();
        loadLiveTimeline();
    }
    
    private void checkIDStatus() {
        // Always get fresh status
        refreshIDStatus();

        if (idStatus != null) {
            System.out.println("checkIDStatus: Processing status...");
            System.out.println("  - Raw status string: " + idStatus.getStatus());

            String status = idStatus.getStatus();
            int currentStep = getStepFromStatus(status);

            System.out.println("  - Current step calculated: " + currentStep);
            System.out.println("  - Display status: " + formatStatusForDisplay(status));

            customStepProgressBar1.setCurrentStep(currentStep);

            String displayStatus = formatStatusForDisplay(status);
            jLabel3.setText(displayStatus);

            setStatusLabelColor(status);

            // Check if status is ready for pickup or claimed
            if (currentStep == 4) { // Ready for Pickup
                System.out.println("  - Status is READY FOR PICKUP, showing panel");
                showPickupPanel();
            } else if (currentStep == 5) { // ID Claimed
                System.out.println("  - Status is ID CLAIMED, showing claimed message");
                showClaimedMessage();
            } else {
                System.out.println("  - Status is not ready for pickup, hiding panel");
                hidePickupPanel();
            }
        } else {
            // No status found - this means application hasn't started
            System.out.println("checkIDStatus: No ID status found");
            jLabel3.setText("APPLICATION SUBMITTED");
            jLabel3.setForeground(new java.awt.Color(0, 120, 215)); // Blue
            customStepProgressBar1.setCurrentStep(1);
            hidePickupPanel();
        }
    }

    private int getStepFromStatus(String status) {
        if (status == null) return 1; // Default to step 1 if no status

        String normalizedStatus = status.replace(" ", "_").toUpperCase();

        // Map your status_names to simplified 5-step workflow
        switch (normalizedStatus) {
            // Step 1: Application Submitted
            case "SUBMITTED":
            case "STAT-001":
                return 1;

            // Step 2: Processing & Validation (combines multiple statuses)
            case "PROCESSING":
            case "STAT-002":
            case "DOCUMENT_VERIFICATION":
            case "DOCUMENT VERIFICATION":
            case "STAT-003":
            case "BIOMETRICS_APPOINTMENT":
            case "BIOMETRICS APPOINTMENT":
            case "STAT-004":
            case "BIOMETRICS_COMPLETED":
            case "BIOMETRICS COMPLETED":
            case "STAT-005":
            case "BACKGROUND_CHECK":
            case "BACKGROUND CHECK":
            case "STAT-006":
            case "BACKGROUND_CHECK_COMPLETED":
            case "BACKGROUND CHECK COMPLETED":
            case "STAT-007":
                return 2;

            // Step 3: Printing & Packaging
            case "ID_CARD_PRODUCTION":
            case "ID CARD PRODUCTION":
            case "STAT-008":
                return 3;

            // Step 4: Ready for Pickup
            case "READY_FOR_PICKUP":
            case "READY FOR PICKUP":
            case "STAT-009":
                return 4;

            // Step 5: ID Claimed
            case "COMPLETED":
            case "STAT-010":
                return 5;

            // Error/Rejected state
            case "REJECTED":
            case "STAT-011":
                return 0;

            default:
                return 1; // Default to step 1
        }
    }
    
    private String formatStatusForDisplay(String status) {
        if (status == null) return "APPLICATION SUBMITTED";

        String normalizedStatus = status.replace(" ", "_").toUpperCase();

        // Convert detailed statuses to simplified display names
        switch (normalizedStatus) {
            // Step 1: Application Submitted
            case "SUBMITTED":
            case "STAT-001":
                return "APPLICATION SUBMITTED";

            // Step 2: Processing & Validation (all variations)
            case "PROCESSING":
            case "STAT-002":
            case "DOCUMENT_VERIFICATION":
            case "DOCUMENT VERIFICATION":
            case "STAT-003":
                return "PROCESSING & VALIDATION";

            case "BIOMETRICS_APPOINTMENT":
            case "BIOMETRICS APPOINTMENT":
            case "STAT-004":
                return "PROCESSING & VALIDATION";

            case "BIOMETRICS_COMPLETED":
            case "BIOMETRICS COMPLETED":
            case "STAT-005":
                return "PROCESSING & VALIDATION";

            case "BACKGROUND_CHECK":
            case "BACKGROUND CHECK":
            case "STAT-006":
                return "PROCESSING & VALIDATION";

            case "BACKGROUND_CHECK_COMPLETED":
            case "BACKGROUND CHECK COMPLETED":
            case "STAT-007":
                return "PROCESSING & VALIDATION";

            // Step 3: Printing & Packaging
            case "ID_CARD_PRODUCTION":
            case "ID CARD PRODUCTION":
            case "STAT-008":
                return "PRINTING & PACKAGING";

            // Step 4: Ready for Pickup
            case "READY_FOR_PICKUP":
            case "READY FOR PICKUP":
            case "STAT-009":
                return "READY FOR PICKUP";

            // Step 5: ID Claimed
            case "COMPLETED":
            case "STAT-010":
                return "ID CLAIMED";

            // Error/Rejected state
            case "REJECTED":
            case "STAT-011":
                return "APPLICATION REJECTED";

            default:
                return status.toUpperCase();
        }
    }
    
    private void setStatusLabelColor(String status) {
        if (status == null) {
            jLabel3.setForeground(new java.awt.Color(0, 120, 215)); // Blue for Step 1
            return;
        }

        int currentStep = getStepFromStatus(status);

        // Color coding based on simplified steps
        switch (currentStep) {
            case 1: // Application Submitted
                jLabel3.setForeground(new java.awt.Color(255, 193, 7)); // Amber
                break;
                
            case 2: // Processing & Validation
                jLabel3.setForeground(new java.awt.Color(0, 120, 215)); // Blue
                break;
                
            case 3: // Printing & Packaging
                jLabel3.setForeground(new java.awt.Color(204, 85, 0)); // Orange
                break;
                
            case 4: // Ready for Pickup
                jLabel3.setForeground(new java.awt.Color(40, 167, 69)); // Green
                break;
                
            case 5: // ID Claimed
                jLabel3.setForeground(new java.awt.Color(111, 66, 193)); // Purple
                break;
                
            case 0: // Rejected
                jLabel3.setForeground(new java.awt.Color(220, 53, 69)); // Red
                break;
                
            default:
                jLabel3.setForeground(new java.awt.Color(100, 100, 100)); // Gray
        }
    }

    private void showPickupPanel() {
        System.out.println("showPickupPanel() called");
        jPanel1.setVisible(true);
        jLabel1.setText("Your National ID card is ready for pickup. Please schedule an appointment to claim your ID at a designated center.");
        SchedulePickup.setText("SCHEDULE PICKUP");
        SchedulePickup.setBackground(new java.awt.Color(0, 150, 0)); // Green

        jPanel1.revalidate();
        jPanel1.repaint();
        revalidate();
        repaint();

        System.out.println("Pickup panel should now be visible");
    }
    
    private void showClaimedMessage() {
        jPanel1.setVisible(true);
        jLabel1.setText("Your National ID has been successfully claimed. Thank you for using our services!");
        SchedulePickup.setText("VIEW ID DETAILS");
        SchedulePickup.setBackground(new java.awt.Color(0, 120, 215)); // Blue
        
        // Remove existing action listeners
        for (java.awt.event.ActionListener al : SchedulePickup.getActionListeners()) {
            SchedulePickup.removeActionListener(al);
        }
        
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
        System.out.println("Loading live timeline for citizen ID: " + (citizen != null ? citizen.getCitizenId() : "null"));

        if (citizen == null) {
            System.out.println("Citizen is null, loading default timeline");
            loadDefaultTimeline();
            return;
        }

        try {
            DefaultTableModel model = (DefaultTableModel) FullStatusHistoryTable.getModel();
            model.setRowCount(0); // Clear existing data

            // IMPORTANT: Get fresh status history from database
            System.out.println("Getting status history for citizen ID: " + citizen.getCitizenId());
            List<Data.IDStatus> statusHistory = Data.IDStatus.getStatusHistoryByCitizenId(citizen.getCitizenId());
            System.out.println("Found " + (statusHistory != null ? statusHistory.size() : 0) + " status history entries");

            if (statusHistory != null && !statusHistory.isEmpty()) {
                System.out.println("Status history entries:");
                for (Data.IDStatus status : statusHistory) {
                    System.out.println("  - Status ID: " + status.getStatusId() + 
                                     ", Status: " + status.getStatus() + 
                                     ", Date: " + status.getUpdateDate());
                }
            }

            // Add application date as first entry
            if (citizen.getApplicationDate() != null) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                model.addRow(new Object[]{
                    sdf.format(citizen.getApplicationDate()),
                    "Application Submitted",
                    "National ID application submitted successfully",
                    "System"
                });
                System.out.println("Added application date: " + citizen.getApplicationDate());
            }

            // Add all status history entries, but consolidate them into simplified steps
            if (statusHistory != null) {
                for (Data.IDStatus status : statusHistory) {
                    String simplifiedStatus = formatStatusForDisplay(status.getStatus());
                    String description = getSimplifiedStatusDescription(status.getStatus());

                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    String dateStr = status.getUpdateDate() != null ? 
                        sdf.format(status.getUpdateDate()) : "";

                    String notes = status.getNotes();
                    if (notes != null && notes.length() > 100) {
                        notes = notes.substring(0, 100) + "...";
                    }

                    model.addRow(new Object[]{
                        dateStr,
                        simplifiedStatus,
                        description,
                        notes != null && !notes.isEmpty() ? notes : "System Update"
                    });
                    System.out.println("Added status to timeline: " + simplifiedStatus + " on " + dateStr + 
                                     " (Status ID: " + status.getStatusId() + ")");
                }
            }

            // Add document submissions (as part of Processing & Validation)
            List<Data.Document> documents = Data.Document.getDocumentsByCitizenId(citizen.getCitizenId());
            System.out.println("Found " + (documents != null ? documents.size() : 0) + " documents for this citizen");

            if (documents != null) {
                for (Data.Document doc : documents) {
                    if ("Yes".equalsIgnoreCase(doc.getSubmitted()) || 
                        "Verified".equalsIgnoreCase(doc.getStatus()) ||
                        "Submitted".equalsIgnoreCase(doc.getSubmitted())) {

                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                        String dateStr = doc.getUploadDate() != null ? 
                            sdf.format(doc.getUploadDate()) : "";

                        model.addRow(new Object[]{
                            dateStr,
                            "Document Verified",
                            doc.getDocumentName() + " - Verified",
                            "Document Validation System"
                        });
                        System.out.println("Added document: " + doc.getDocumentName());
                    }
                }
            }

            // Add appointment information
            Data.Appointment appointment = Data.Appointment.getAppointmentByCitizenId(citizen.getCitizenId());
            if (appointment != null) {
                String appStatus = appointment.getStatus();
                String statusDesc = "";
                String description = "";

                if ("SCHEDULED".equalsIgnoreCase(appStatus)) {
                    statusDesc = "Pickup Appointment Scheduled";
                    description = "ID pickup appointment scheduled";
                } else if ("COMPLETED".equalsIgnoreCase(appStatus)) {
                    statusDesc = "ID Claimed";
                    description = "ID collected at center";
                } else if ("CANCELLED".equalsIgnoreCase(appStatus)) {
                    statusDesc = "Appointment Cancelled";
                    description = "Pickup appointment cancelled";
                }

                if (!statusDesc.isEmpty()) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    String dateStr = appointment.getAppDate() != null ? 
                        sdf.format(appointment.getAppDate()) : "";

                    model.addRow(new Object[]{
                        dateStr,
                        statusDesc,
                        description,
                        "Time: " + (appointment.getAppTime() != null ? appointment.getAppTime() : "N/A")
                    });
                    System.out.println("Added appointment: " + statusDesc);
                }
            }

            // If no timeline data, show default based on current status
            if (model.getRowCount() == 0) {
                System.out.println("No timeline data found, showing default based on status");
                String[][] defaultData = getDefaultTimelineBasedOnStatus();
                for (String[] row : defaultData) {
                    model.addRow(row);
                }
            }

            System.out.println("Total rows in timeline table: " + model.getRowCount());

            // Sort the table by date (newest first)
            sortTableByDateDesc(model);

            // Refresh the table
            model.fireTableDataChanged();
            FullStatusHistoryTable.revalidate();
            FullStatusHistoryTable.repaint();
            customScrollPane1.revalidate();
            customScrollPane1.repaint();

        } catch (Exception e) {
            System.err.println("Error loading live timeline: " + e.getMessage());
            e.printStackTrace();

            DefaultTableModel model = (DefaultTableModel) FullStatusHistoryTable.getModel();
            model.setRowCount(0);
            model.addRow(new Object[]{"Error", "Database Error", e.getMessage(), "System"});
            model.fireTableDataChanged();
        }
    }
    
    // Add this method to your IDStatus class
    private void addForceRefreshButton() {
        // Create a small hidden button for force refresh
        FlatButton forceRefreshBtn = new FlatButton("🔄 Force Refresh");
        forceRefreshBtn.setFont(new java.awt.Font("Segoe UI", Font.PLAIN, 10));
        forceRefreshBtn.setNormalColor(new java.awt.Color(200, 200, 200));
        forceRefreshBtn.setPreferredSize(new java.awt.Dimension(120, 25));
        forceRefreshBtn.addActionListener(e -> {
            System.out.println("Force refresh triggered manually");
            refreshData();
            javax.swing.JOptionPane.showMessageDialog(this,
                "Data refreshed manually at " + new java.util.Date(),
                "Force Refresh",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
        });

        // Add it to your layout (you'll need to adjust your layout)
        // Or make it a right-click context menu option
    }

    // Add this to your right-click menu
    private void setupContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem refreshItem = new JMenuItem("Refresh Status");
        refreshItem.addActionListener(e -> {
            System.out.println("Context menu refresh triggered");
            refreshData();
        });
        contextMenu.add(refreshItem);

        this.setComponentPopupMenu(contextMenu);
    }
    
    private void sortTableByDateDesc(DefaultTableModel model) {
        List<Object[]> rows = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            Object[] row = new Object[model.getColumnCount()];
            for (int j = 0; j < model.getColumnCount(); j++) {
                row[j] = model.getValueAt(i, j);
            }
            rows.add(row);
        }

        // Sort by date (newest first)
        rows.sort((r1, r2) -> {
            String date1 = (String) r1[0];
            String date2 = (String) r2[0];

            // Handle empty dates (put them at the end)
            if (date1 == null || date1.isEmpty()) return 1;
            if (date2 == null || date2.isEmpty()) return -1;

            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                java.util.Date d1 = sdf.parse(date1);
                java.util.Date d2 = sdf.parse(date2);
                return d2.compareTo(d1); // Descending order (newest first)
            } catch (Exception e) {
                return date2.compareTo(date1); // Fallback to string comparison
            }
        });

        // Clear and repopulate model
        model.setRowCount(0);
        for (Object[] row : rows) {
            model.addRow(row);
        }
    }
    
    private String[][] getDefaultTimelineBasedOnStatus() {
        // Get fresh status
        refreshIDStatus();
        
        String currentStatus = idStatus != null ? idStatus.getStatus() : null;
        int currentStep = getStepFromStatus(currentStatus);
        System.out.println("Current status: " + currentStatus + ", Step: " + currentStep);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new java.util.Date());

        // Generate dates for the last 30 days
        String[] dates = new String[5];
        for (int i = 0; i < 5; i++) {
            dates[i] = getDateOffset(-30 + (i * 7)); // Spread over 30 days, one week apart
        }

        // Return timeline based on current step
        List<String[]> timeline = new ArrayList<>();
        
        if (currentStep >= 1) {
            timeline.add(new String[]{dates[0], "Application Submitted", "Application submitted online via portal", "System"});
        }
        if (currentStep >= 2) {
            timeline.add(new String[]{dates[1], "Processing & Validation", "Documents verified and background check completed", "Validation Team"});
        }
        if (currentStep >= 3) {
            timeline.add(new String[]{dates[2], "Printing & Packaging", "ID card is being printed and packaged", "Production Dept"});
        }
        if (currentStep >= 4) {
            timeline.add(new String[]{dates[3], "Ready for Pickup", "ID card is ready for pickup", "Distribution Center"});
        }
        if (currentStep >= 5) {
            timeline.add(new String[]{dates[4], "ID Claimed", "ID card has been collected", "Center Staff"});
        }

        return timeline.toArray(new String[0][]);
    }
    
    private String getDateOffset(int daysOffset) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DATE, daysOffset);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }

    private String getSimplifiedStatusDescription(String status) {
        if (status == null) return "No status available";

        String normalizedStatus = status.replace(" ", "_").toUpperCase();

        // Group detailed statuses into simplified descriptions
        switch (normalizedStatus) {
            // Step 1: Application Submitted
            case "SUBMITTED":
            case "STAT-001":
                return "Application has been submitted and is awaiting processing";

            // Step 2: Processing & Validation (all variations)
            case "PROCESSING":
            case "STAT-002":
                return "Initial processing started";

            case "DOCUMENT_VERIFICATION":
            case "DOCUMENT VERIFICATION":
            case "STAT-003":
                return "Documents are being verified for authenticity";

            case "BIOMETRICS_APPOINTMENT":
            case "BIOMETRICS APPOINTMENT":
            case "STAT-004":
                return "Biometrics data collection scheduled";

            case "BIOMETRICS_COMPLETED":
            case "BIOMETRICS COMPLETED":
            case "STAT-005":
                return "Biometrics data (fingerprints, photo) successfully captured";

            case "BACKGROUND_CHECK":
            case "BACKGROUND CHECK":
            case "STAT-006":
                return "Background investigation and verification in progress";

            case "BACKGROUND_CHECK_COMPLETED":
            case "BACKGROUND CHECK COMPLETED":
            case "STAT-007":
                return "Background check completed successfully";

            // Step 3: Printing & Packaging
            case "ID_CARD_PRODUCTION":
            case "ID CARD PRODUCTION":
            case "STAT-008":
                return "Physical ID card is being manufactured and packaged";

            // Step 4: Ready for Pickup
            case "READY_FOR_PICKUP":
            case "READY FOR PICKUP":
            case "STAT-009":
                return "ID card is ready for pickup at designated center";

            // Step 5: ID Claimed
            case "COMPLETED":
            case "STAT-010":
                return "ID card has been successfully received by applicant";

            // Error/Rejected state
            case "REJECTED":
            case "STAT-011":
                return "Application did not meet required criteria";

            default:
                return "Status update: " + status;
        }
    }
    
    private void loadDefaultTimeline() {
        System.out.println("Loading default timeline");
        
        DefaultTableModel model = (DefaultTableModel) FullStatusHistoryTable.getModel();
        model.setRowCount(0);
        
        String[][] timelineData = {
            {"", "No application data", "Please submit an application first", ""},
            {"", "Step 1: Submit Application", "Submit application online with required documents", ""},
            {"", "Step 2: Processing & Validation", "Document verification and background check", ""},
            {"", "Step 3: Printing & Packaging", "Physical ID card manufacturing", ""},
            {"", "Step 4: Ready for Pickup", "Card ready at designated center", ""},
            {"", "Step 5: ID Claimed", "Collect your ID at the center", ""}
        };
        
        for (String[] row : timelineData) {
            model.addRow(row);
        }
        
        model.fireTableDataChanged();
        FullStatusHistoryTable.revalidate();
        FullStatusHistoryTable.repaint();
        customScrollPane1.revalidate();
        customScrollPane1.repaint();
        
        System.out.println("Default timeline loaded with " + timelineData.length + " rows");
    }
    
    private void viewIDDetailsActionPerformed(java.awt.event.ActionEvent evt) {
        if (citizen == null) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "No citizen data available. Please submit an application first.",
                "No Data",
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get fresh appointment data
        Data.Appointment appointment = Data.Appointment.getAppointmentByCitizenId(citizen.getCitizenId());

        StringBuilder details = new StringBuilder();
        details.append("NATIONAL ID DETAILS\n");
        details.append("===================\n\n");
        details.append("PERSONAL INFORMATION:\n");
        details.append("--------------------\n");
        details.append("Full Name: ").append(citizen.getFullName()).append("\n");
        details.append("First Name: ").append(citizen.getFname()).append("\n");
        if (citizen.getMname() != null && !citizen.getMname().isEmpty()) {
            details.append("Middle Name: ").append(citizen.getMname()).append("\n");
        }
        details.append("Last Name: ").append(citizen.getLname()).append("\n");
        details.append("Gender: ").append(citizen.getGender() != null ? citizen.getGender() : "Not specified").append("\n");

        // Show formatted Transaction ID - get fresh status
        refreshIDStatus();
        String transactionId = "Not Assigned";
        if (idStatus != null) {
            String rawTransactionId = idStatus.getTransactionId();
            if (rawTransactionId != null && !rawTransactionId.trim().isEmpty()) {
                transactionId = Data.IDStatus.formatTransactionId(rawTransactionId);
            }
        }
        details.append("Transaction ID: ").append(transactionId).append("\n");

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        details.append("Date of Birth: ").append(citizen.getBirthDate() != null ? sdf.format(citizen.getBirthDate()) : "Not provided").append("\n");
        details.append("Application Date: ").append(citizen.getApplicationDate() != null ? sdf.format(citizen.getApplicationDate()) : "Not provided").append("\n\n");

        // Application Status
        details.append("APPLICATION STATUS:\n");
        details.append("-------------------\n");
        if (idStatus != null) {
            String simplifiedStatus = formatStatusForDisplay(idStatus.getStatus());
            details.append("Current Status: ").append(simplifiedStatus).append("\n");
            if (idStatus.getUpdateDate() != null) {
                details.append("Last Updated: ").append(sdf.format(idStatus.getUpdateDate())).append("\n");
            }
            if (idStatus.getNotes() != null && !idStatus.getNotes().isEmpty()) {
                details.append("Notes: ").append(idStatus.getNotes()).append("\n");
            }
            details.append("Status ID: ").append(idStatus.getStatusId()).append("\n");
        } else {
            details.append("Status: Application submitted\n");
        }
        details.append("\n");

        // Load address if not already loaded
        if (address == null && citizen != null) {
            address = Data.Address.getAddressByCitizenId(citizen.getCitizenId());
        }

        // Show address from Address table
        if (address != null) {
            details.append("ADDRESS DETAILS:\n");
            details.append("----------------\n");
            if (address.getStreetAddress() != null && !address.getStreetAddress().isEmpty()) {
                details.append("Street: ").append(address.getStreetAddress()).append("\n");
            }
            if (address.getAddressLine() != null && !address.getAddressLine().isEmpty()) {
                details.append("Address Line 2: ").append(address.getAddressLine()).append("\n");
            }
            if (address.getBarangay() != null && !address.getBarangay().isEmpty()) {
                details.append("Barangay: ").append(address.getBarangay()).append("\n");
            }
            if (address.getCity() != null && !address.getCity().isEmpty()) {
                details.append("City: ").append(address.getCity()).append("\n");
            }
            if (address.getStateProvince() != null && !address.getStateProvince().isEmpty()) {
                details.append("Province: ").append(address.getStateProvince()).append("\n");
            }
            if (address.getZipPostalCode() != null && !address.getZipPostalCode().isEmpty()) {
                details.append("ZIP Code: ").append(address.getZipPostalCode()).append("\n");
            }
            if (address.getCountry() != null && !address.getCountry().isEmpty()) {
                details.append("Country: ").append(address.getCountry()).append("\n");
            }
            details.append("\n");
            details.append("Formatted Address: ").append(address.getFullAddress()).append("\n\n");
        } else {
            details.append("ADDRESS: Not available\n\n");
        }

        // Document Status Summary
        List<Data.Document> documents = Data.Document.getDocumentsByCitizenId(citizen.getCitizenId());
        if (documents != null && !documents.isEmpty()) {
            details.append("DOCUMENT STATUS:\n");
            details.append("----------------\n");
            int verifiedCount = 0;
            int totalCount = 0;
            
            for (Data.Document doc : documents) {
                totalCount++;
                if ("Verified".equalsIgnoreCase(doc.getStatus()) || "Yes".equalsIgnoreCase(doc.getSubmitted())) {
                    verifiedCount++;
                }
                details.append("- ").append(doc.getDocumentName()).append(": ").append(doc.getStatus()).append("\n");
            }
            details.append("\n");
            details.append("Documents Verified: ").append(verifiedCount).append("/").append(totalCount).append("\n\n");
        }

        if (appointment != null) {
            details.append("APPOINTMENT DETAILS:\n");
            details.append("--------------------\n");
            details.append("Date: ").append(appointment.getAppDate()).append("\n");
            details.append("Time: ").append(appointment.getAppTime()).append("\n");
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
        if (citizen != null) {
            System.out.println("Refreshing for citizen ID: " + citizen.getCitizenId());
        }
        
        // Always get fresh data from database
        if (citizen != null) {
            refreshIDStatus();
        }
        
        loadCitizenData();
        System.out.println("Refresh completed at " + new java.util.Date());
    }
    
    // Override dispose to clean up timer
    @Override
    public void removeNotify() {
        super.removeNotify();
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
            System.out.println("Refresh timer stopped");
        }
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
        SchedulePickup = new component.Button.FlatButton();
        customStepProgressBar1 = new component.Progress.CustomStepProgressBar();
        jLabel5 = new javax.swing.JLabel();
        customScrollPane1 = new component.Scroll.CustomScrollPane();
        FullStatusHistoryTable = new component.Table.CustomTable();

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
        jLabel4.setText("Application Date: yyyy-mm-dd | TRN: 1234-5678-9123-4567-8912-4567-8912-34");
        jLabel4.setPreferredSize(new java.awt.Dimension(850, 25));

        jSeparator1.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator1.setForeground(new java.awt.Color(0, 120, 215));
        jSeparator1.setMinimumSize(new java.awt.Dimension(800, 10));
        jSeparator1.setPreferredSize(new java.awt.Dimension(800, 10));

        jPanel1.setBackground(new java.awt.Color(150, 220, 240));
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 125));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 120, 215));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Congratulations! Your physical National ID card is ready Please schedule an appointment to claim your ID at a center near you.");

        SchedulePickup.setBackground(new java.awt.Color(0, 150, 0));
        SchedulePickup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar.png"))); // NOI18N
        SchedulePickup.setText("SCHEDULE MY PICKUP");
        SchedulePickup.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        SchedulePickup.setNormalColor(new java.awt.Color(0, 150, 0));
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
                .addGap(18, 18, 18)
                .addComponent(SchedulePickup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        customStepProgressBar1.setCurrentStep(4);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 120, 215));
        jLabel5.setText("FULL STATUS HISTORY");
        jLabel5.setPreferredSize(new java.awt.Dimension(850, 25));

        FullStatusHistoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Status", "Description", "Notes / Updated By"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        customScrollPane1.setViewportView(FullStatusHistoryTable);
        if (FullStatusHistoryTable.getColumnModel().getColumnCount() > 0) {
            FullStatusHistoryTable.getColumnModel().getColumn(0).setResizable(false);
            FullStatusHistoryTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            FullStatusHistoryTable.getColumnModel().getColumn(1).setResizable(false);
            FullStatusHistoryTable.getColumnModel().getColumn(1).setPreferredWidth(80);
            FullStatusHistoryTable.getColumnModel().getColumn(2).setResizable(false);
            FullStatusHistoryTable.getColumnModel().getColumn(2).setPreferredWidth(200);
            FullStatusHistoryTable.getColumnModel().getColumn(3).setResizable(false);
            FullStatusHistoryTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        }

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(customScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(customScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void SchedulePickupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SchedulePickupActionPerformed
        if (citizen == null) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Please submit an application first.",
                "No Application",
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check ID status eligibility first
        if (!isIDReadyForPickup()) {
            String currentStatus = getCurrentIDStatus();
            javax.swing.JOptionPane.showMessageDialog(this,
                "Your ID is not ready for pickup yet.\n\n" +
                "Current Status: " + currentStatus + "\n" +
                "You can only schedule a pickup appointment when your ID status is 'Ready for Pickup'.\n\n" +
                "Please check back later or contact support for more information.",
                "ID Not Ready",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Check if there's already an appointment
        Data.Appointment existingAppointment = Data.Appointment.getAppointmentByCitizenId(citizen.getCitizenId());

        String transactionId = "Not Assigned";
        if (idStatus != null) {
            String rawTransactionId = idStatus.getTransactionId();
            if (rawTransactionId != null && !rawTransactionId.trim().isEmpty()) {
                transactionId = Data.IDStatus.formatTransactionId(rawTransactionId);
            }
        }

        // Get a reference to the Main window
        java.awt.Window mainWindow = javax.swing.SwingUtilities.getWindowAncestor(this);

        if (existingAppointment != null && "SCHEDULED".equalsIgnoreCase(existingAppointment.getStatus())) {
            // Show existing appointment details
            int option = javax.swing.JOptionPane.showConfirmDialog(this,
                "You already have a scheduled appointment:\n\n" +
                "Transaction ID: " + transactionId + "\n" +
                "Date: " + existingAppointment.getAppDate() + "\n" +
                "Time: " + existingAppointment.getAppTime() + "\n" +
                "Name: " + citizen.getFname() + " " + citizen.getLname() + "\n\n" +
                "Would you like to reschedule?",
                "Existing Appointment Found",
                javax.swing.JOptionPane.YES_NO_OPTION);

            if (option == javax.swing.JOptionPane.YES_OPTION && mainWindow instanceof Main) {
                // Navigate to rescheduling page
                Scheduling scheduling = new Scheduling(user, citizen, existingAppointment);
                ((Main) mainWindow).showForm(scheduling);
            }
        } else {
            // Navigate to scheduling page for new appointment
            if (mainWindow instanceof Main) {
                Scheduling scheduling = new Scheduling(user, citizen, null);
                ((Main) mainWindow).showForm(scheduling);
            } else {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Unable to navigate to scheduling page.",
                    "Navigation Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_SchedulePickupActionPerformed

    private boolean isIDReadyForPickup() {
        if (idStatus == null || citizen == null) {
            return false;
        }

        String currentStatus = getCurrentIDStatus();
        if (currentStatus == null) {
            return false;
        }

        String normalizedStatus = currentStatus.toUpperCase().trim();
        return normalizedStatus.contains("READY") || 
               normalizedStatus.contains("READY FOR PICKUP") ||
               normalizedStatus.equals("READY_FOR_PICKUP") ||
               normalizedStatus.equals("STAT-009");
    }

    private String getCurrentIDStatus() {
        if (idStatus != null && idStatus.getStatus() != null) {
            return idStatus.getStatus();
        }
        return "No status found";
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.Table.CustomTable FullStatusHistoryTable;
    private component.Button.FlatButton SchedulePickup;
    private component.Scroll.CustomScrollPane customScrollPane1;
    private component.Progress.CustomStepProgressBar customStepProgressBar1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}
