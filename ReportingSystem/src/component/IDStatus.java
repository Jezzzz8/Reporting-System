package component;

import backend.objects.Data;
import backend.objects.Data.Address; // NEW: Import Address class
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class IDStatus extends javax.swing.JPanel {
    
    private Data.User user;
    private Data.Citizen citizen;
    private Data.IDStatus idStatus;
    private Data.Address address; // NEW: Store address separately
    
    public IDStatus(Data.User user) {
        this.user = user;
        initComponents();

        // component listener to debug
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                System.out.println("IDStatus component shown - refreshing data");
                loadCitizenData();
            }
        });

        customStepProgressBar1.setTotalSteps(5);
        String[] stepLabels = {
            "Application Submitted",
            "Processing & Validation", 
            "Printing & Packaging",
            "Ready for Pickup",
            "ID Claimed"
        };
        customStepProgressBar1.setStepLabels(stepLabels);

        loadCitizenData();
    }


    private void loadCitizenData() {
        System.out.println("Loading citizen data for user ID: " + user.getUserId());

        citizen = Data.Citizen.getCitizenByUserId(user.getUserId());

        if (citizen != null) {
            System.out.println("Citizen found: " + citizen.getFullName() + " (ID: " + citizen.getCitizenId() + ")");

            // Load address information - NEW
            address = Data.Address.getAddressByCitizenId(citizen.getCitizenId());

            idStatus = Data.IDStatus.getStatusByCitizenId(citizen.getCitizenId());

            // Update labels with actual data
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

            // Set step labels for 5-step progress
            String[] stepLabels = {
                "Application Submitted",
                "Processing & Validation", 
                "Printing & Packaging",
                "Ready for Pickup",
                "ID Claimed"
            };

            customStepProgressBar1.setTotalSteps(5);
            customStepProgressBar1.setStepLabels(stepLabels);

            System.out.println("Progress bar initialized with 5 steps");

            checkIDStatus();

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

            System.out.println("Current step calculated: " + currentStep + " for status: " + status);

            customStepProgressBar1.setCurrentStep(currentStep);

            String displayStatus = formatStatusForDisplay(status);
            jLabel3.setText(displayStatus);

            setStatusLabelColor(status);

            String normalizedStatus = status.replace(" ", "_").toUpperCase();
            if ("READY_FOR_PICKUP".equals(normalizedStatus) || "READY".equals(normalizedStatus)) {
                System.out.println("Showing pickup panel for status: " + normalizedStatus);
                showPickupPanel();
            } else if ("ID_CLAIMED".equals(normalizedStatus) || "CLAIMED".equals(normalizedStatus) || "COMPLETED".equals(normalizedStatus)) {
                System.out.println("Showing claimed message for status: " + normalizedStatus);
                showClaimedMessage();
            } else {
                System.out.println("Hiding pickup panel for status: " + normalizedStatus);
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

        String normalizedStatus = status.replace(" ", "_").toUpperCase();

        switch (normalizedStatus) {
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

        String normalizedStatus = status.replace(" ", "_").toUpperCase();

        switch (normalizedStatus) {
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
                return status.toUpperCase();
        }
    }
    
    private void setStatusLabelColor(String status) {
        if (status == null) {
            jLabel3.setForeground(new java.awt.Color(0, 120, 215)); // Blue
            return;
        }

        String normalizedStatus = status.replace(" ", "_").toUpperCase();

        switch (normalizedStatus) {
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
        System.out.println("showPickupPanel() called");
        jPanel1.setVisible(true);
        jLabel1.setText("Congratulations! Your physical National ID card is ready. Please schedule an appointment to claim your ID at a center near you.");
        SchedulePickup.setText("SCHEDULE MY PICKUP");
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
        System.out.println("Loading live timeline for citizen ID: " + (citizen != null ? citizen.getCitizenId() : "null"));

        if (citizen == null) {
            System.out.println("Citizen is null, loading default timeline");
            loadDefaultTimeline();
            return;
        }

        try {
            DefaultTableModel model = (DefaultTableModel) FullStatusHistoryTable.getModel();
            model.setRowCount(0); // Clear existing data

            List<Data.IDStatus> statusHistory = getStatusHistoryForCitizen(citizen.getCitizenId());
            System.out.println("Found " + statusHistory.size() + " status history entries for citizen ID: " + citizen.getCitizenId());

            // Add application date as first entry
            if (citizen.getApplicationDate() != null) {
                model.addRow(new Object[]{
                    citizen.getApplicationDate().toString(),
                    "Application Submitted",
                    "National ID application submitted successfully",
                    "System"
                });
                System.out.println("Added application date: " + citizen.getApplicationDate());
            }

            // Add all status history entries in chronological order
            for (Data.IDStatus status : statusHistory) {
                String statusDesc = formatStatusForDisplay(status.getStatus());
                String description = getStatusDescription(status.getStatus());

                model.addRow(new Object[]{
                    status.getUpdateDate() != null ? status.getUpdateDate().toString() : "",
                    statusDesc,
                    description,
                    status.getNotes() != null && !status.getNotes().isEmpty() ? 
                        status.getNotes() : "System Update"
                });
                System.out.println("Added status: " + status.getStatus() + " on " + status.getUpdateDate());
            }

            // Add document submissions
            List<Data.Document> documents = Data.Document.getDocumentsByCitizenId(citizen.getCitizenId());
            System.out.println("Found " + documents.size() + " documents for this citizen");

            for (Data.Document doc : documents) {
                if ("Yes".equalsIgnoreCase(doc.getSubmitted()) || 
                    "Verified".equalsIgnoreCase(doc.getStatus()) ||
                    "Submitted".equalsIgnoreCase(doc.getSubmitted())) {

                    String docStatus = "Submitted";
                    if ("Verified".equalsIgnoreCase(doc.getStatus())) {
                        docStatus = "Verified";
                    }

                    model.addRow(new Object[]{
                        doc.getUploadDate() != null ? doc.getUploadDate().toString() : "",
                        "Document " + docStatus,
                        doc.getDocumentName() + " document",
                        "Status: " + doc.getStatus()
                    });
                    System.out.println("Added document: " + doc.getDocumentName());
                }
            }

            // Add appointment information
            Data.Appointment appointment = Data.Appointment.getAppointmentByCitizenId(citizen.getCitizenId());
            if (appointment != null) {
                String appStatus = appointment.getStatus();
                String statusDesc = "";
                String description = "";

                if ("SCHEDULED".equalsIgnoreCase(appStatus)) {
                    statusDesc = "Appointment Scheduled";
                    description = "ID pickup appointment scheduled";
                } else if ("COMPLETED".equalsIgnoreCase(appStatus)) {
                    statusDesc = "Appointment Completed";
                    description = "ID collected at center";
                } else if ("CANCELLED".equalsIgnoreCase(appStatus)) {
                    statusDesc = "Appointment Cancelled";
                    description = "Pickup appointment cancelled";
                } else if ("RESCHEDULED".equalsIgnoreCase(appStatus)) {
                    statusDesc = "Appointment Rescheduled";
                    description = "Pickup appointment rescheduled";
                }

                if (!statusDesc.isEmpty()) {
                    model.addRow(new Object[]{
                        appointment.getAppDate() != null ? appointment.getAppDate().toString() : "",
                        statusDesc,
                        description,
                        "Time: " + (appointment.getAppTime() != null ? appointment.getAppTime() : "N/A")
                    });
                    System.out.println("Added appointment: " + statusDesc);
                }
            }

            // Add address updates if available - NEW
            if (address != null && address.getAddressId() > 0) {
                // We could add address verification as a step in the timeline
                // For now, just note that address information is on file
                model.addRow(new Object[]{
                    citizen.getApplicationDate() != null ? citizen.getApplicationDate().toString() : "",
                    "Address Verified",
                    "Residential address verified and stored",
                    "Address System"
                });
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
            jScrollPane1.revalidate();
            jScrollPane1.repaint();

        } catch (Exception e) {
            System.err.println("Error loading live timeline: " + e.getMessage());
            e.printStackTrace();

            DefaultTableModel model = (DefaultTableModel) FullStatusHistoryTable.getModel();
            model.setRowCount(0);
            model.addRow(new Object[]{"Error", "Database Error", e.getMessage(), "System"});
            model.fireTableDataChanged();
        }
    }
    
    private List<Data.IDStatus> getStatusHistoryForCitizen(int citizenId) {
        try {
            // Use the new method
            return Data.IDStatus.getStatusHistoryByCitizenId(citizenId);
        } catch (Exception e) {
            System.err.println("Error getting status history: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
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
        String currentStatus = idStatus != null ? idStatus.getStatus() : null;
        int currentStep = getStepFromStatus(currentStatus);
        System.out.println("Current status: " + currentStatus + ", Step: " + currentStep);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new java.util.Date());

        String[] dates = new String[5];
        dates[0] = getDateOffset(-30); // Submitted 30 days ago
        dates[1] = getDateOffset(-25); // Processing 25 days ago
        dates[2] = getDateOffset(-20); // Printing 20 days ago
        dates[3] = getDateOffset(-5);  // Ready 5 days ago
        dates[4] = getDateOffset(0);   // Today/Claimed

        switch (currentStep) {
            case 5: // ID Claimed - Complete timeline
                return new String[][] {
                    {dates[0], "Application Submitted", "Application submitted online via portal", "System"},
                    {dates[1], "Processing & Validation", "Documents verified and background check completed", "Officer Smith"},
                    {dates[2], "Printing & Packaging", "ID card printed and prepared for distribution", "Production Dept"},
                    {dates[3], "Ready for Pickup", "Card ready for pickup at designated center", "Officer Johnson"},
                    {dates[4], "ID Claimed", "ID collected successfully at the center", "Center Staff"}
                };

            case 4: // Ready for Pickup
                return new String[][] {
                    {dates[0], "Application Submitted", "Application submitted online via portal", "System"},
                    {dates[1], "Processing & Validation", "Documents verified and validated", "Officer Smith"},
                    {dates[2], "Printing & Packaging", "ID card sent for printing and packaging", "Production Dept"},
                    {dates[3], "Ready for Pickup", "Card ready for collection at center", "Officer Johnson"},
                    {"", "Next Step", "Schedule pickup appointment at your convenience", ""}
                };

            case 3: // Printing & Packaging
                return new String[][] {
                    {dates[0], "Application Submitted", "Application submitted online via portal", "System"},
                    {dates[1], "Processing & Validation", "Documents verified and approved for printing", "Officer Smith"},
                    {dates[2], "Printing & Packaging", "ID card printing in progress at facility", "Production Dept"},
                    {"", "Next Step", "Awaiting packaging and quality control", ""},
                    {"", "Future Step", "Notification when ready for pickup", ""}
                };

            case 2: // Processing & Validation
                return new String[][] {
                    {dates[0], "Application Submitted", "Application submitted online via portal", "System"},
                    {dates[1], "Processing & Validation", "Documents under review and verification", "Officer Smith"},
                    {"", "Next Step", "Background check and validation process", ""},
                    {"", "Future Step", "Approval for printing", ""},
                    {"", "Future Step", "Printing and packaging phase", ""}
                };

            case 1: // Application Submitted
            default:
                return new String[][] {
                    {dates[0], "Application Submitted", "Application submitted successfully", "System"},
                    {"", "Next Step", "Document verification and processing", ""},
                    {"", "Future Step", "Background check and validation", ""},
                    {"", "Future Step", "Approval and production", ""},
                    {"", "Future Step", "Pickup scheduling and collection", ""}
                };
        }
    }
    
    private String getDateOffset(int daysOffset) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DATE, daysOffset);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }

    private String getStatusDescription(String status) {
        if (status == null) return "No status available";

        String normalizedStatus = status.replace(" ", "_").toUpperCase();

        switch (normalizedStatus) {
            case "SUBMITTED":
            case "PENDING":
                return "Application received and registered in the system";

            case "PROCESSING":
            case "UNDER_REVIEW":
            case "VALIDATION":
                return "Documents are being reviewed and validated by officials";

            case "APPROVED":
                return "Application has been approved for processing";

            case "PRINTING":
            case "PRODUCTION":
                return "Physical ID card is being printed and manufactured";

            case "PACKAGING":
                return "ID card is being packaged and prepared for distribution";

            case "READY_FOR_PICKUP":
            case "READY":
                return "ID card is ready for pickup at the designated center";

            case "ID_CLAIMED":
            case "CLAIMED":
                return "ID card has been successfully collected by the applicant";

            case "COMPLETED":
                return "ID application process has been completed";

            case "REJECTED":
                return "Application did not meet the required criteria";

            case "FAILED":
                return "Processing failed due to technical issues";

            case "CANCELLED":
                return "Application was cancelled by the applicant";

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
            {"", "Step 1", "Submit application online", ""},
            {"", "Step 2", "Document verification", ""},
            {"", "Step 3", "Background check", ""},
            {"", "Step 4", "Card production", ""}
        };
        
        for (String[] row : timelineData) {
            model.addRow(row);
        }
        
        model.fireTableDataChanged();
        FullStatusHistoryTable.revalidate();
        FullStatusHistoryTable.repaint();
        jScrollPane1.revalidate();
        jScrollPane1.repaint();
        
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

        Data.Appointment appointment = Data.Appointment.getAppointmentByCitizenId(citizen.getCitizenId());

        StringBuilder details = new StringBuilder();
        details.append("ID Details:\n");
        details.append("----------------\n");
        details.append("Name: ").append(citizen.getFullName()).append("\n");
        details.append("First Name: ").append(citizen.getFname()).append("\n");
        if (citizen.getMname() != null && !citizen.getMname().isEmpty()) {
            details.append("Middle Name: ").append(citizen.getMname()).append("\n");
        }
        details.append("Last Name: ").append(citizen.getLname()).append("\n");
        details.append("Gender: ").append(citizen.getGender() != null ? citizen.getGender() : "Not specified").append("\n");

        // Show formatted Transaction ID
        String transactionId = "Not Assigned";
        if (idStatus != null) {
            String rawTransactionId = idStatus.getTransactionId();
            if (rawTransactionId != null && !rawTransactionId.trim().isEmpty()) {
                transactionId = Data.IDStatus.formatTransactionId(rawTransactionId);
            }
        }
        details.append("Transaction ID: ").append(transactionId).append("\n");

        details.append("Date of Birth: ").append(citizen.getBirthDate()).append("\n");

        // Load address if not already loaded
        if (address == null && citizen != null) {
            address = Data.Address.getAddressByCitizenId(citizen.getCitizenId());
        }

        // Show address from Address table
        if (address != null) {
            details.append("\nAddress Details:\n");
            details.append("----------------\n");
            if (address.getStreetAddress() != null && !address.getStreetAddress().isEmpty()) {
                details.append("Street Address: ").append(address.getStreetAddress()).append("\n");
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
                details.append("State/Province: ").append(address.getStateProvince()).append("\n");
            }
            if (address.getZipPostalCode() != null && !address.getZipPostalCode().isEmpty()) {
                details.append("ZIP/Postal Code: ").append(address.getZipPostalCode()).append("\n");
            }
            if (address.getCountry() != null && !address.getCountry().isEmpty()) {
                details.append("Country: ").append(address.getCountry()).append("\n");
            }

            // Also show the full formatted address
            details.append("Full Address: ").append(address.getFullAddress()).append("\n");
        } else {
            details.append("\nAddress: Not available\n");
        }

        if (appointment != null) {
            details.append("\nAppointment Details:\n");
            details.append("----------------\n");
            details.append("Appointment Date: ").append(appointment.getAppDate()).append("\n");
            details.append("Appointment Time: ").append(appointment.getAppTime()).append("\n");
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
        FullStatusHistoryTable = new component.Table.CustomTable();
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
        jLabel4.setText("Application Date: yyyy-mm-dd | TRN: 1234-5678-9123-4567-8912-4567-8912-34");
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

        FullStatusHistoryTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(FullStatusHistoryTable);
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
        if (citizen == null) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Please submit an application first.",
                "No Application",
                javax.swing.JOptionPane.WARNING_MESSAGE);
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

        if (existingAppointment != null && "SCHEDULED".equalsIgnoreCase(existingAppointment.getStatus())) {
            // Show existing appointment details
            javax.swing.JOptionPane.showMessageDialog(this,
                "You already have a scheduled appointment:\n\n" +
                "Transaction ID: " + transactionId + "\n" +
                "Date: " + existingAppointment.getAppDate() + "\n" +
                "Time: " + existingAppointment.getAppTime() + "\n" +
                "Name: " + citizen.getFname() + " " + citizen.getLname() + "\n\n" +
                "Would you like to reschedule?",
                "Existing Appointment Found",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);

            // TODO: Navigate to rescheduling page
        } else {
            // Navigate to scheduling page
            javax.swing.JOptionPane.showMessageDialog(this,
                "Redirecting to scheduling page...\n\n" +
                "Transaction ID: " + transactionId + "\n" +
                "Citizen: " + citizen.getFullName() + "\n" +
                "First Name: " + citizen.getFname() + "\n" +
                "Last Name: " + citizen.getLname(),
                "Schedule Pickup",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);

                // In Main class, it would call:
                // main.showForm(new Scheduling(user));
        }
    }//GEN-LAST:event_SchedulePickupActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.Table.CustomTable FullStatusHistoryTable;
    private javax.swing.JButton SchedulePickup;
    private component.Progress.CustomStepProgressBar customStepProgressBar1;
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
