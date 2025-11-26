package component;

import backend.database.DatabaseConnection;
import backend.objects.Data;
import sys.effect.RippleEffect;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Dashboard extends javax.swing.JPanel {

    public Dashboard() {
        initComponents();
        initComponents();
        testDatabaseConnection();
        enhanceDashboard();
        loadDashboardData();
        loadTableData();
    }
    
    private void testDatabaseConnection() {
        try {
            System.out.println("Testing database connection...");

            // Test getting citizens
            List<Data.Citizen> citizens = Data.Citizen.getAllCitizens();
            System.out.println("Total citizens in database: " + citizens.size());

            // Test getting verification requests
            List<Data.VerificationRequest> requests = Data.VerificationRequest.getAllRequests();
            System.out.println("Total verification requests in database: " + requests.size());

            // Test getting pending requests count
            int pendingCount = Data.VerificationRequest.getPendingRequestsCount();
            System.out.println("Pending requests count: " + pendingCount);

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
        if (approvedActionBtn != null) {
            RippleEffect approvedRipple = new RippleEffect(approvedActionBtn);
            approvedRipple.setRippleColor(new Color(255, 255, 255, 100));
        }
        
        if (readyActionBtn != null) {
            RippleEffect readyRipple = new RippleEffect(readyActionBtn);
            readyRipple.setRippleColor(new Color(255, 255, 255, 100));
        }
        
        if (pendingActionBtn != null) {
            RippleEffect pendingRipple = new RippleEffect(pendingActionBtn);
            pendingRipple.setRippleColor(new Color(255, 255, 255, 100));
        }
        
        if (urgentActionBtn != null) {
            RippleEffect urgentRipple = new RippleEffect(urgentActionBtn);
            urgentRipple.setRippleColor(new Color(255, 255, 255, 100));
        }
    }

    private void setupButtonActions() {
        if (pendingActionBtn != null) {
            pendingActionBtn.addActionListener((ActionEvent e) -> {
                showPendingRequests();
            });
        }
        
        if (approvedActionBtn != null) {
            approvedActionBtn.addActionListener((ActionEvent e) -> {
                showApprovedRequests();
            });
        }
        
        if (urgentActionBtn != null) {
            urgentActionBtn.addActionListener((ActionEvent e) -> {
                showUrgentRequests();
            });
        }
        
        if (readyActionBtn != null) {
            readyActionBtn.addActionListener((ActionEvent e) -> {
                showReadyRequests();
            });
        }
    }

    private void loadDashboardData() {
        try {
            // Load the correct data for each card
            int totalCitizens = Data.Citizen.getTotalCitizens();
            int pendingRequests = Data.VerificationRequest.getPendingRequestsCount();
            int totalUsers = Data.User.getAllUsers().size();

            // Get approved requests count (status_id = 2)
            int approvedRequests = getApprovedRequestsCount();

            // Get urgent requests (pending for more than 3 days)
            int urgentRequests = getUrgentRequestsCount();

            // Get ready for pickup count (approved requests)
            int readyRequests = approvedRequests; // Or you might have a separate status for "ready"

            // Update the BoxPanels with CORRECT data
            if (approvedValueLabel != null) {
                approvedValueLabel.setText(String.valueOf(approvedRequests));
                approvedTitleLabel.setText("Approved Requests");
            }

            if (pendingValueLabel != null) {
                pendingValueLabel.setText(String.valueOf(pendingRequests));
                pendingTitleLabel.setText("Pending Requests");
            }

            if (readyValueLabel != null) {
                readyValueLabel.setText(String.valueOf(readyRequests));
                readyTitleLabel.setText("Ready for Pickup");
            }

            if (urgentCountLabel != null) {
                urgentCountLabel.setText(String.valueOf(urgentRequests));
                urgentTitleLabel.setText("Urgent Cases");
            }

            System.out.println("Dashboard data loaded successfully!");
            System.out.println("Approved: " + approvedRequests + ", Pending: " + pendingRequests + 
                              ", Ready: " + readyRequests + ", Urgent: " + urgentRequests);

        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
            setDefaultValues();
        }
    }
    
    private int getApprovedRequestsCount() {
        String query = "SELECT COUNT(*) as approved_count FROM Verification_Request_tb WHERE status_id = 2";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("approved_count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting approved requests count: " + e.getMessage());
        }
        return 0;
    }
    
    private int getUrgentRequestsCount() {
        String query = "SELECT COUNT(*) as urgent_count FROM Verification_Request_tb " +
                      "WHERE status_id = 1 AND request_datetime < DATE_SUB(NOW(), INTERVAL 3 DAY)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("urgent_count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting urgent requests count: " + e.getMessage());
        }
        return 0;
    }
    
    private void loadTableData() {
        loadPendingTable();
    }

    private void loadPendingTable() {
        DefaultTableModel model = (DefaultTableModel) DetailsTable.getModel();
        model.setRowCount(0);

        try {
            System.out.println("Loading pending table data...");

            List<Data.VerificationRequest> allRequests = Data.VerificationRequest.getAllRequests();
            System.out.println("Total requests found: " + allRequests.size());

            int pendingCount = 0;
            for (Data.VerificationRequest request : allRequests) {
                if (request.getStatusId() == 1) { // Pending status
                    pendingCount++;
                    Data.Citizen citizen = Data.Citizen.getCitizenById(request.getCitizenId());
                    Data.User requester = Data.User.getUserById(request.getRequesterId());

                    String citizenName = (citizen != null) ? citizen.getFullName() : "Unknown";
                    String requesterName = (requester != null) ? requester.getFullName() : "Unknown";
                    String reason = getReasonName(request.getReasonId());
                    String status = getStatusName(request.getStatusId());

                    System.out.println("Adding row - Request ID: " + request.getRequestId() + 
                                     ", Citizen: " + citizenName + ", Status: " + status);

                    model.addRow(new Object[]{
                        request.getRequestId(),
                        citizenName,
                        requesterName,
                        reason,
                        status,
                        request.getRequestDatetime()
                    });
                }
            }

            System.out.println("Pending requests found: " + pendingCount);

            if (model.getRowCount() == 0) {
                System.out.println("No pending requests found, adding placeholder row");
                model.addRow(new Object[]{"No pending requests", "", "", "", "", ""});
            } else {
                System.out.println("Successfully loaded " + model.getRowCount() + " rows");
            }

        } catch (Exception e) {
            System.err.println("Error loading pending table: " + e.getMessage());
            e.printStackTrace();
            model.addRow(new Object[]{"Error loading data", "", "", "", "", ""});
        } finally {
        // Force GUI refresh
        model.fireTableDataChanged();
        DetailsTable.revalidate();
        DetailsTable.repaint();
        }
    }
    
    private void loadApprovedTable() {
        DefaultTableModel model = (DefaultTableModel) DetailsTable.getModel();
        model.setRowCount(0);
        
        try {
            List<Data.VerificationRequest> allRequests = Data.VerificationRequest.getAllRequests();
            
            for (Data.VerificationRequest request : allRequests) {
                if (request.getStatusId() == 2) { // Approved status
                    Data.Citizen citizen = Data.Citizen.getCitizenById(request.getCitizenId());
                    Data.User requester = Data.User.getUserById(request.getRequesterId());
                    
                    String citizenName = (citizen != null) ? citizen.getFullName() : "Unknown";
                    String requesterName = (requester != null) ? requester.getFullName() : "Unknown";
                    String reason = getReasonName(request.getReasonId());
                    String status = getStatusName(request.getStatusId());
                    
                    model.addRow(new Object[]{
                        request.getRequestId(),
                        citizenName,
                        requesterName,
                        reason,
                        status,
                        request.getRequestDatetime()
                    });
                }
            }
            
            if (model.getRowCount() == 0) {
                model.addRow(new Object[]{"No approved requests", "", "", "", "", ""});
            }
        } catch (Exception e) {
            System.err.println("Error loading approved table: " + e.getMessage());
            model.addRow(new Object[]{"Error loading data", "", "", "", "", ""});
        }
    }

    private void loadUrgentTable() {
        DefaultTableModel model = (DefaultTableModel) DetailsTable.getModel();
        model.setRowCount(0);

        try {
            // For urgent, show requests that are more than 3 days old and still pending
            List<Data.VerificationRequest> allRequests = Data.VerificationRequest.getAllRequests();

            for (Data.VerificationRequest request : allRequests) {
                if (request.getStatusId() == 1) { // Pending status
                    // Check if request is older than 3 days
                    long requestTime = request.getRequestDatetime().getTime();
                    long threeDaysAgo = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000);

                    if (requestTime < threeDaysAgo) {
                        Data.Citizen citizen = Data.Citizen.getCitizenById(request.getCitizenId());
                        Data.User requester = Data.User.getUserById(request.getRequesterId());

                        String citizenName = (citizen != null) ? citizen.getFullName() : "Unknown";
                        String requesterName = (requester != null) ? requester.getFullName() : "Unknown";
                        String reason = getReasonName(request.getReasonId());
                        String status = "URGENT - Overdue";

                        model.addRow(new Object[]{
                            request.getRequestId(),
                            citizenName,
                            requesterName,
                            reason,
                            status,
                            request.getRequestDatetime()
                        });
                    }
                }
            }

            if (model.getRowCount() == 0) {
                model.addRow(new Object[]{"No urgent requests", "", "", "", "", ""});
            }
        } catch (Exception e) {
            System.err.println("Error loading urgent table: " + e.getMessage());
            model.addRow(new Object[]{"Error loading data", "", "", "", "", ""});
        }
    }

    private void loadReadyTable() {
        DefaultTableModel model = (DefaultTableModel) DetailsTable.getModel();
        model.setRowCount(0);

        try {
            // For ready, show approved requests
            List<Data.VerificationRequest> allRequests = Data.VerificationRequest.getAllRequests();

            for (Data.VerificationRequest request : allRequests) {
                if (request.getStatusId() == 2) { // Approved status
                    Data.Citizen citizen = Data.Citizen.getCitizenById(request.getCitizenId());
                    Data.User requester = Data.User.getUserById(request.getRequesterId());

                    String citizenName = (citizen != null) ? citizen.getFullName() : "Unknown";
                    String requesterName = (requester != null) ? requester.getFullName() : "Unknown";
                    String reason = getReasonName(request.getReasonId());
                    String status = "READY FOR PICKUP";

                    model.addRow(new Object[]{
                        request.getRequestId(),
                        citizenName,
                        requesterName,
                        reason,
                        status,
                        request.getRequestDatetime()
                    });
                }
            }

            if (model.getRowCount() == 0) {
                model.addRow(new Object[]{"No ready requests", "", "", "", "", ""});
            }
        } catch (Exception e) {
            System.err.println("Error loading ready table: " + e.getMessage());
            model.addRow(new Object[]{"Error loading data", "", "", "", "", ""});
        }
    }

    private String getReasonName(int reasonId) {
        List<Data.VerificationReason> reasons = Data.VerificationReason.getAllReasons();
        for (Data.VerificationReason reason : reasons) {
            if (reason.getReasonId() == reasonId) {
                return reason.getReasonName();
            }
        }
        return "Unknown";
    }
    
    private String getStatusName(int statusId) {
        List<Data.RequestStatus> statuses = Data.RequestStatus.getAllStatus();
        for (Data.RequestStatus status : statuses) {
            if (status.getStatusId() == statusId) {
                return status.getStatusName();
            }
        }
        return "Unknown";
    }

    private void setDefaultValues() {
        if (approvedValueLabel != null) approvedValueLabel.setText("0");
        if (pendingValueLabel != null) pendingValueLabel.setText("0");
        if (readyValueLabel != null) readyValueLabel.setText("0");
        if (urgentCountLabel != null) urgentCountLabel.setText("0");
    }

    // Action methods for buttons
    private void showPendingRequests() {
        loadPendingTable();
        updateTableTitle("Pending Verification Requests");
    }

    private void showApprovedRequests() {
        loadApprovedTable();
        updateTableTitle("Approved Verification Requests");
    }

    private void showUrgentRequests() {
        loadUrgentTable();
        updateTableTitle("Urgent Verification Requests");
    }

    private void showReadyRequests() {
        loadReadyTable();
        updateTableTitle("Ready for Pickup Requests");
    }

    private void updateTableTitle(String title) {
        DetailsTable.getTableHeader().setToolTipText(title);
    }

    // Refresh method that can be called from outside
    public void refreshDashboard() {
        loadDashboardData();
        loadTableData();
    }

    private void filterCurrentTable(String searchTerm) {
        DefaultTableModel model = (DefaultTableModel) DetailsTable.getModel();

        if (model != null) {
            // Store which type of data we're currently showing
            String currentView = getCurrentTableView();

            // Reload the appropriate data first
            reloadCurrentTableView(currentView);

            // Then apply filtering
            model = (DefaultTableModel) DetailsTable.getModel();

            // Simple client-side filtering
            for (int i = 0; i < model.getRowCount(); i++) {
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
                    i--; // Adjust index after removal
                }
            }

            if (model.getRowCount() == 0) {
                model.addRow(new Object[]{"No matching records found", "", "", "", "", ""});
            }
        }
    }

    private String getCurrentTableView() {
        String tooltip = DetailsTable.getTableHeader().getToolTipText();
        if (tooltip != null) {
            if (tooltip.contains("Pending")) return "PENDING";
            if (tooltip.contains("Approved")) return "APPROVED";
            if (tooltip.contains("Urgent")) return "URGENT";
            if (tooltip.contains("Ready")) return "READY";
        }
        return "PENDING"; // default
    }
    
    private void reloadCurrentTableView(String viewType) {
        switch (viewType) {
            case "PENDING":
                loadPendingTable();
                break;
            case "APPROVED":
                loadApprovedTable();
                break;
            case "URGENT":
                loadUrgentTable();
                break;
            case "READY":
                loadReadyTable();
                break;
            default:
                loadPendingTable();
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel5 = new javax.swing.JPanel();
        approvedBoxPanel = new javax.swing.JPanel();
        approvedValueLabel = new javax.swing.JLabel();
        approvedTitleLabel = new javax.swing.JLabel();
        approvedActionBtn = new javax.swing.JButton();
        readyBoxPanel = new javax.swing.JPanel();
        readyValueLabel = new javax.swing.JLabel();
        readyActionBtn = new javax.swing.JButton();
        readyTitleLabel = new javax.swing.JLabel();
        pendingBoxPanel = new javax.swing.JPanel();
        pendingValueLabel = new javax.swing.JLabel();
        pendingTitleLabel = new javax.swing.JLabel();
        pendingActionBtn = new javax.swing.JButton();
        urgentBoxPanel = new javax.swing.JPanel();
        urgentCountLabel = new javax.swing.JLabel();
        urgentActionBtn = new javax.swing.JButton();
        urgentTitleLabel = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        searchLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        DetailsTable = new javax.swing.JTable();

        setBackground(new java.awt.Color(250, 250, 250));
        setPreferredSize(new java.awt.Dimension(850, 550));

        jPanel5.setBackground(new java.awt.Color(250, 250, 250));

        approvedBoxPanel.setBackground(new java.awt.Color(249, 254, 156));
        approvedBoxPanel.setPreferredSize(new java.awt.Dimension(150, 150));

        approvedValueLabel.setFont(new java.awt.Font("Times New Roman", 1, 36)); // NOI18N
        approvedValueLabel.setForeground(new java.awt.Color(25, 25, 25));
        approvedValueLabel.setText("0");
        approvedValueLabel.setToolTipText("");
        approvedValueLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        approvedTitleLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        approvedTitleLabel.setForeground(new java.awt.Color(25, 25, 25));
        approvedTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        approvedTitleLabel.setText("Approved This Week");
        approvedTitleLabel.setToolTipText("");
        approvedTitleLabel.setPreferredSize(new java.awt.Dimension(140, 43));

        approvedActionBtn.setBackground(new java.awt.Color(41, 128, 185));
        approvedActionBtn.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        approvedActionBtn.setForeground(new java.awt.Color(250, 250, 250));
        approvedActionBtn.setText("More Details");
        approvedActionBtn.setPreferredSize(new java.awt.Dimension(140, 22));
        approvedActionBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                approvedActionBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout approvedBoxPanelLayout = new javax.swing.GroupLayout(approvedBoxPanel);
        approvedBoxPanel.setLayout(approvedBoxPanelLayout);
        approvedBoxPanelLayout.setHorizontalGroup(
            approvedBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, approvedBoxPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(approvedValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
            .addGroup(approvedBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(approvedBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(approvedTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(approvedActionBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        approvedBoxPanelLayout.setVerticalGroup(
            approvedBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(approvedBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(approvedValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(approvedTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(approvedActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        readyBoxPanel.setBackground(new java.awt.Color(156, 200, 254));
        readyBoxPanel.setPreferredSize(new java.awt.Dimension(150, 150));

        readyValueLabel.setFont(new java.awt.Font("Times New Roman", 1, 36)); // NOI18N
        readyValueLabel.setForeground(new java.awt.Color(25, 25, 25));
        readyValueLabel.setText("0");
        readyValueLabel.setToolTipText("");
        readyValueLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        readyActionBtn.setBackground(new java.awt.Color(41, 128, 185));
        readyActionBtn.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        readyActionBtn.setForeground(new java.awt.Color(250, 250, 250));
        readyActionBtn.setText("More Details");
        readyActionBtn.setPreferredSize(new java.awt.Dimension(140, 22));
        readyActionBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readyActionBtnActionPerformed(evt);
            }
        });

        readyTitleLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        readyTitleLabel.setForeground(new java.awt.Color(25, 25, 25));
        readyTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        readyTitleLabel.setText("Ready for Pickup");
        readyTitleLabel.setToolTipText("");
        readyTitleLabel.setPreferredSize(new java.awt.Dimension(140, 43));

        javax.swing.GroupLayout readyBoxPanelLayout = new javax.swing.GroupLayout(readyBoxPanel);
        readyBoxPanel.setLayout(readyBoxPanelLayout);
        readyBoxPanelLayout.setHorizontalGroup(
            readyBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, readyBoxPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(readyValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
            .addGroup(readyBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(readyBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(readyActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(readyTitleLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        readyBoxPanelLayout.setVerticalGroup(
            readyBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(readyBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(readyValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(readyTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(readyActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pendingBoxPanel.setBackground(new java.awt.Color(254, 161, 156));
        pendingBoxPanel.setPreferredSize(new java.awt.Dimension(150, 150));

        pendingValueLabel.setFont(new java.awt.Font("Times New Roman", 1, 36)); // NOI18N
        pendingValueLabel.setForeground(new java.awt.Color(25, 25, 25));
        pendingValueLabel.setText("0");
        pendingValueLabel.setToolTipText("");
        pendingValueLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        pendingTitleLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        pendingTitleLabel.setForeground(new java.awt.Color(25, 25, 25));
        pendingTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pendingTitleLabel.setText("Pending Verifications");
        pendingTitleLabel.setToolTipText("");
        pendingTitleLabel.setPreferredSize(new java.awt.Dimension(140, 43));

        pendingActionBtn.setBackground(new java.awt.Color(41, 128, 185));
        pendingActionBtn.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        pendingActionBtn.setForeground(new java.awt.Color(250, 250, 250));
        pendingActionBtn.setText("More Details");
        pendingActionBtn.setPreferredSize(new java.awt.Dimension(140, 22));
        pendingActionBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pendingActionBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pendingBoxPanelLayout = new javax.swing.GroupLayout(pendingBoxPanel);
        pendingBoxPanel.setLayout(pendingBoxPanelLayout);
        pendingBoxPanelLayout.setHorizontalGroup(
            pendingBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pendingBoxPanelLayout.createSequentialGroup()
                .addGroup(pendingBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pendingBoxPanelLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(pendingValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pendingBoxPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pendingTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pendingBoxPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pendingActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pendingBoxPanelLayout.setVerticalGroup(
            pendingBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pendingBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pendingValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pendingTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(pendingActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        urgentBoxPanel.setBackground(new java.awt.Color(200, 254, 156));
        urgentBoxPanel.setPreferredSize(new java.awt.Dimension(150, 150));

        urgentCountLabel.setFont(new java.awt.Font("Times New Roman", 1, 36)); // NOI18N
        urgentCountLabel.setForeground(new java.awt.Color(25, 25, 25));
        urgentCountLabel.setText("0");
        urgentCountLabel.setToolTipText("");
        urgentCountLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        urgentActionBtn.setBackground(new java.awt.Color(41, 128, 185));
        urgentActionBtn.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        urgentActionBtn.setForeground(new java.awt.Color(250, 250, 250));
        urgentActionBtn.setText("More Details");
        urgentActionBtn.setPreferredSize(new java.awt.Dimension(140, 22));
        urgentActionBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                urgentActionBtnActionPerformed(evt);
            }
        });

        urgentTitleLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        urgentTitleLabel.setForeground(new java.awt.Color(25, 25, 25));
        urgentTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        urgentTitleLabel.setText("Urgent Cases");
        urgentTitleLabel.setToolTipText("");
        urgentTitleLabel.setPreferredSize(new java.awt.Dimension(140, 43));

        javax.swing.GroupLayout urgentBoxPanelLayout = new javax.swing.GroupLayout(urgentBoxPanel);
        urgentBoxPanel.setLayout(urgentBoxPanelLayout);
        urgentBoxPanelLayout.setHorizontalGroup(
            urgentBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(urgentBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(urgentBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(urgentBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, urgentBoxPanelLayout.createSequentialGroup()
                            .addComponent(urgentCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(25, 25, 25))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, urgentBoxPanelLayout.createSequentialGroup()
                            .addComponent(urgentActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap()))
                    .addComponent(urgentTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        urgentBoxPanelLayout.setVerticalGroup(
            urgentBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(urgentBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(urgentCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(urgentTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(urgentActionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addComponent(pendingBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(approvedBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(urgentBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(readyBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(readyBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(approvedBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pendingBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(urgentBoxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        DetailsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Request ID", "Citizen Name", "Requester", "Reason", "Status", "Request Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
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
        DetailsTable.getTableHeader().setResizingAllowed(false);
        DetailsTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(DetailsTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(50, 50, 50))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(searchLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pendingActionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pendingActionBtnActionPerformed
        showPendingRequests();
    }//GEN-LAST:event_pendingActionBtnActionPerformed

    private void approvedActionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_approvedActionBtnActionPerformed
        showApprovedRequests();
    }//GEN-LAST:event_approvedActionBtnActionPerformed

    private void urgentActionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_urgentActionBtnActionPerformed
        showUrgentRequests();
    }//GEN-LAST:event_urgentActionBtnActionPerformed

    private void readyActionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readyActionBtnActionPerformed
        showReadyRequests();
    }//GEN-LAST:event_readyActionBtnActionPerformed

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        String searchTerm = searchField.getText().trim();
        if (!searchTerm.isEmpty()) {
            filterCurrentTable(searchTerm);
        } else {
            // Reload current view without filter
            String currentView = getCurrentTableView();
            reloadCurrentTableView(currentView);
        }
    }//GEN-LAST:event_searchFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable DetailsTable;
    private javax.swing.JButton approvedActionBtn;
    private javax.swing.JPanel approvedBoxPanel;
    private javax.swing.JLabel approvedTitleLabel;
    private javax.swing.JLabel approvedValueLabel;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton pendingActionBtn;
    private javax.swing.JPanel pendingBoxPanel;
    private javax.swing.JLabel pendingTitleLabel;
    private javax.swing.JLabel pendingValueLabel;
    private javax.swing.JButton readyActionBtn;
    private javax.swing.JPanel readyBoxPanel;
    private javax.swing.JLabel readyTitleLabel;
    private javax.swing.JLabel readyValueLabel;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JButton urgentActionBtn;
    private javax.swing.JPanel urgentBoxPanel;
    private javax.swing.JLabel urgentCountLabel;
    private javax.swing.JLabel urgentTitleLabel;
    // End of variables declaration//GEN-END:variables
}
