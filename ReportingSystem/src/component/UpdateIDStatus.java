package component;

import backend.objects.Data.*;
import backend.objects.Data.IDStatus;
import backend.objects.Data.Citizen;
import backend.objects.Data.Appointment;
import backend.objects.Data.User;
import backend.objects.Data.StatusName;
import backend.objects.Data.Document;
import backend.objects.Data.Notification;

import backend.database.DatabaseConnection;
import backend.objects.Data;
import component.filter.CustomFilterBar;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;
import javax.swing.table.TableRowSorter;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;
import javax.swing.border.EmptyBorder;

public class UpdateIDStatus extends javax.swing.JPanel {
    
    private User user;
    private List<Citizen> allCitizens;
    private Map<String, Integer> statusCounts;
    
    // For edit icon
    private ImageIcon editIcon;
    
    public UpdateIDStatus(User user) {
        this.user = user;
        
        // Load edit icon
        editIcon = createDefaultEditIcon();
        
        initComponents();
        initializeComponents();
        loadAllData();
    }
    
    // CREATE DEFAULT EDIT ICON METHOD
    private ImageIcon createDefaultEditIcon() {
        // Create a simple edit icon
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw pencil/edit icon
        g2d.setColor(new Color(0, 120, 215)); // Blue color
        g2d.setStroke(new BasicStroke(1.5f));
        
        // Draw pencil body
        g2d.drawLine(4, 12, 12, 4);
        g2d.drawLine(4, 11, 11, 4);
        
        // Draw pencil tip
        int[] xPoints = {12, 13, 14, 13, 12};
        int[] yPoints = {4, 3, 4, 5, 4};
        g2d.drawPolyline(xPoints, yPoints, 5);
        
        // Draw eraser
        g2d.setColor(new Color(200, 50, 50));
        g2d.fillRect(2, 12, 3, 3);
        
        g2d.dispose();
        return new ImageIcon(image);
    }
    
    private void initializeComponents() {
        // Configure table
        configureTable();
        
        // Configure filter bar
        configureFilterBar();
        
        // Configure search field
        configureSearchField();
        
        // Configure refresh button
        configureRefreshButton();
        
        // Setup auto-resizing
        setupAutoResizing();
    }
    
    private void configureTable() {
        // Set table properties
        customTable1.setHeaderColor(new Color(0, 120, 215));
        customTable1.setOddRowColor(new Color(250, 250, 250));
        customTable1.setEvenRowColor(new Color(240, 248, 255));
        customTable1.setHoverColor(new Color(230, 240, 255));
        customTable1.setSelectedRowColor(new Color(41, 128, 185));
        customTable1.setSelectedRowTextColor(Color.WHITE);
        customTable1.setNormalTextColor(Color.BLACK);
        customTable1.setCellFont(new Font("Segoe UI", Font.PLAIN, 12));
        customTable1.setHeaderFont(new Font("Segoe UI", Font.BOLD, 12));
        customTable1.setCustomRowHeight(40);
        customTable1.setShowGrid(true);
        customTable1.setAutoResize(true, false); // Allow horizontal auto-resize
        
        // Configure action column as button
        setupActionColumn();
        
        // Configure scroll pane
        configureScrollPane();
    }
    
    private void setupAutoResizing() {
        // Add component listener to handle resizing
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                resizeComponents();
            }
        });
    }
    
    private void resizeComponents() {
        // Resize the scroll pane and table
        Dimension parentSize = getSize();
        int tableHeight = Math.max(300, parentSize.height - 250); // Adjust based on available space
        customScrollPane1.setPreferredSize(new Dimension(parentSize.width - 20, tableHeight));
        
        // Revalidate and repaint
        customScrollPane1.revalidate();
        customScrollPane1.repaint();
        customTable1.revalidate();
        customTable1.repaint();
    }
    
    private void configureScrollPane() {
        // Set scroll pane properties
        customScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        customScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        customScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        
        // Make the viewport follow the table's size
        customTable1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Set preferred size for scroll pane
        customScrollPane1.setPreferredSize(new Dimension(800, 350));
    }
    
    private void setupActionColumn() {
        int actionColumn = 8; // Actions column index
        
        // Configure the button column to show icon
        customTable1.getColumnModel().getColumn(actionColumn).setCellRenderer(new ButtonRenderer());
        customTable1.getColumnModel().getColumn(actionColumn).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        // Set preferred width for action column
        customTable1.getColumnModel().getColumn(actionColumn).setPreferredWidth(60);
    }
    
    private void configureFilterBar() {
        customFilterBar1.setFilterListener(new CustomFilterBar.FilterListener() {
            @Override
            public void onFilterSelected(String filterName) {
                filterTableByStatus(filterName);
            }
            
            @Override
            public void onFilterHover(String filterName) {
                // Optional hover feedback
            }
            
            @Override
            public void onFilterContextMenu(String filterName, String action) {
                handleFilterContextAction(filterName, action);
            }
        });
    }
    
    private void configureSearchField() {
        searchField.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty() && !searchTerm.equals("Search")) {
                searchCitizens(searchTerm);
            }
        });
        
        // Add key listener for real-time search
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    searchField.setText("");
                    searchCitizens("");
                } else if (!evt.isActionKey()) {
                    String searchTerm = searchField.getText().trim();
                    searchCitizens(searchTerm);
                }
            }
        });
    }
    
    private void configureRefreshButton() {
        RefreshButton.addActionListener(e -> {
            loadAllData();
        });
        
        // Set F5 shortcut
        RefreshButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh");
        RefreshButton.getActionMap().put("refresh", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                loadAllData();
            }
        });
    }
    
    private void loadAllData() {
        loadStatusCounts();
        loadCitizensTable();
        updateFilterBarCounts();
    }
    
    private void loadStatusCounts() {
        statusCounts = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Total applications
            String totalQuery = "SELECT COUNT(*) FROM citizens";
            try (PreparedStatement stmt = conn.prepareStatement(totalQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    statusCounts.put("ALL", rs.getInt(1));
                    MyApplicationStatusValueLabel.setText(String.valueOf(rs.getInt(1)));
                }
            }

            // Get status names from database for proper querying
            List<StatusName> allStatuses = Data.StatusName.getAllStatusNames();

            // **FIXED: Pending count - Count DISTINCT citizens with Submitted status**
            for (StatusName status : allStatuses) {
                if ("Submitted".equalsIgnoreCase(status.getStatusName())) {
                    // Get the LATEST status for each citizen and count those with Submitted
                    String pendingQuery = 
                        "SELECT COUNT(DISTINCT ist.citizen_id) " +
                        "FROM id_status ist " +
                        "INNER JOIN ( " +
                        "    SELECT citizen_id, MAX(update_date) as latest_date " +
                        "    FROM id_status " +
                        "    GROUP BY citizen_id " +
                        ") latest ON ist.citizen_id = latest.citizen_id AND ist.update_date = latest.latest_date " +
                        "WHERE ist.status_name_id = ?";

                    try (PreparedStatement stmt = conn.prepareStatement(pendingQuery)) {
                        stmt.setInt(1, status.getStatusNameId());
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                statusCounts.put("PENDING", rs.getInt(1));
                                DaySinceApplicationValueLabel.setText(String.valueOf(rs.getInt(1)));
                            }
                        }
                    }
                    break;
                }
            }

            // **FIXED: Processing count - Count DISTINCT citizens with Processing status**
            for (StatusName status : allStatuses) {
                if ("Processing".equalsIgnoreCase(status.getStatusName())) {
                    String processingQuery = 
                        "SELECT COUNT(DISTINCT ist.citizen_id) " +
                        "FROM id_status ist " +
                        "INNER JOIN ( " +
                        "    SELECT citizen_id, MAX(update_date) as latest_date " +
                        "    FROM id_status " +
                        "    GROUP BY citizen_id " +
                        ") latest ON ist.citizen_id = latest.citizen_id AND ist.update_date = latest.latest_date " +
                        "WHERE ist.status_name_id = ?";

                    try (PreparedStatement stmt = conn.prepareStatement(processingQuery)) {
                        stmt.setInt(1, status.getStatusNameId());
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                statusCounts.put("PROCESSING", rs.getInt(1));
                                MyAppointmentCountLabel.setText(String.valueOf(rs.getInt(1)));
                            }
                        }
                    }
                    break;
                }
            }

            // **FIXED: Ready count - check for "Ready for Pickup"**
            for (StatusName status : allStatuses) {
                if ("Ready for Pickup".equalsIgnoreCase(status.getStatusName())) {
                    String readyQuery = 
                        "SELECT COUNT(DISTINCT ist.citizen_id) " +
                        "FROM id_status ist " +
                        "INNER JOIN ( " +
                        "    SELECT citizen_id, MAX(update_date) as latest_date " +
                        "    FROM id_status " +
                        "    GROUP BY citizen_id " +
                        ") latest ON ist.citizen_id = latest.citizen_id AND ist.update_date = latest.latest_date " +
                        "WHERE ist.status_name_id = ?";

                    try (PreparedStatement stmt = conn.prepareStatement(readyQuery)) {
                        stmt.setInt(1, status.getStatusNameId());
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                statusCounts.put("READY", rs.getInt(1));
                            }
                        }
                    }
                    break;
                }
            }

            // **FIXED: Completed count**
            for (StatusName status : allStatuses) {
                if ("Completed".equalsIgnoreCase(status.getStatusName())) {
                    String completedQuery = 
                        "SELECT COUNT(DISTINCT ist.citizen_id) " +
                        "FROM id_status ist " +
                        "INNER JOIN ( " +
                        "    SELECT citizen_id, MAX(update_date) as latest_date " +
                        "    FROM id_status " +
                        "    GROUP BY citizen_id " +
                        ") latest ON ist.citizen_id = latest.citizen_id AND ist.update_date = latest.latest_date " +
                        "WHERE ist.status_name_id = ?";

                    try (PreparedStatement stmt = conn.prepareStatement(completedQuery)) {
                        stmt.setInt(1, status.getStatusNameId());
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                statusCounts.put("COMPLETED", rs.getInt(1));
                            }
                        }
                    }
                    break;
                }
            }

            // **FIXED: Rejected count**
            for (StatusName status : allStatuses) {
                if ("Rejected".equalsIgnoreCase(status.getStatusName())) {
                    String rejectedQuery = 
                        "SELECT COUNT(DISTINCT ist.citizen_id) " +
                        "FROM id_status ist " +
                        "INNER JOIN ( " +
                        "    SELECT citizen_id, MAX(update_date) as latest_date " +
                        "    FROM id_status " +
                        "    GROUP BY citizen_id " +
                        ") latest ON ist.citizen_id = latest.citizen_id AND ist.update_date = latest.latest_date " +
                        "WHERE ist.status_name_id = ?";

                    try (PreparedStatement stmt = conn.prepareStatement(rejectedQuery)) {
                        stmt.setInt(1, status.getStatusNameId());
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                statusCounts.put("REJECTED", rs.getInt(1));
                            }
                        }
                    }
                    break;
                }
            }

            // **FIXED: Today's Updates - Count DISTINCT citizens with status updates today**
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = dateFormat.format(new java.util.Date());
            String todayQuery = 
                "SELECT COUNT(DISTINCT citizen_id) FROM id_status WHERE DATE(update_date) = ?";

            try (PreparedStatement stmt = conn.prepareStatement(todayQuery)) {
                stmt.setString(1, today);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        statusCounts.put("TODAY_UPDATES", rs.getInt(1));
                        NotificationsValueLabel.setText(String.valueOf(rs.getInt(1)));
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading status counts: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateFilterBarCounts() {
        if (statusCounts != null && customFilterBar1 != null) {
            Map<String, Integer> filterCounts = new HashMap<>();
            filterCounts.put("ALL", statusCounts.getOrDefault("ALL", 0));
            filterCounts.put("PENDING", statusCounts.getOrDefault("PENDING", 0));
            filterCounts.put("PROCESSING", statusCounts.getOrDefault("PROCESSING", 0));
            filterCounts.put("READY", statusCounts.getOrDefault("READY", 0));
            filterCounts.put("COMPLETED", statusCounts.getOrDefault("COMPLETED", 0));
            filterCounts.put("REJECTED", statusCounts.getOrDefault("REJECTED", 0));
            
            customFilterBar1.setFilterCounts(filterCounts);
        }
    }
    
    private void loadCitizensTable() {
        DefaultTableModel model = (DefaultTableModel) customTable1.getModel();
        model.setRowCount(0);
        
        try {
            allCitizens = Data.Citizen.getAllCitizens();
            
            if (allCitizens == null || allCitizens.isEmpty()) {
                model.addRow(new Object[]{"No citizens found", "", "", "", "", "", "", "", ""});
                return;
            }
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            for (Citizen citizen : allCitizens) {
                int citizenId = citizen.getCitizenId();
                String fullName = citizen.getFullName();
                String nationalId = citizen.getNationalId() != null ? citizen.getNationalId() : "PH-" + citizenId;
                String gender = citizen.getGender() != null ? citizen.getGender() : "Not specified";
                String appDate = citizen.getApplicationDate() != null ? 
                    dateFormat.format(citizen.getApplicationDate()) : "N/A";
                
                // Calculate days since application
                int daysSince = 0;
                if (citizen.getApplicationDate() != null) {
                    long diff = new java.util.Date().getTime() - citizen.getApplicationDate().getTime();
                    daysSince = (int) (diff / (1000 * 60 * 60 * 24));
                }
                
                // Get current status using the updated method
                IDStatus currentStatus = Data.IDStatus.getStatusByCitizenId(citizenId);
                String statusText = "Submitted"; // Default status
                String statusIcon = getStatusIcon("Submitted");
                
                if (currentStatus != null) {
                    statusText = currentStatus.getStatus(); // This will get the status name from joined table
                    statusIcon = getStatusIcon(statusText);
                }
                
                String transactionId = "N/A";
                if (currentStatus != null && currentStatus.getTransactionId() != null) {
                    transactionId = formatTransactionIdShort(currentStatus.getTransactionId());
                }
                
                // Get documents status
                List<Document> documents = Data.Document.getDocumentsByCitizenId(citizenId);
                String docsStatus = getDocumentsStatus(documents);
                
                // Get appointment
                Appointment appointment = Data.Appointment.getAppointmentByCitizenId(citizenId);
                String appointmentInfo = "None";
                if (appointment != null) {
                    if (appointment.getAppDate() != null) {
                        appointmentInfo = dateFormat.format(appointment.getAppDate());
                    }
                    if ("Claimed".equals(appointment.getStatus())) {
                        appointmentInfo = "Claimed";
                    } else if ("Scheduled".equals(appointment.getStatus())) {
                        appointmentInfo = "Scheduled";
                    }
                }
                
                // Add row to table with status icon
                model.addRow(new Object[]{
                    String.valueOf(citizenId),
                    fullName,
                    transactionId,
                    gender,
                    appDate,
                    String.valueOf(daysSince),
                    statusIcon + " " + statusText,
                    appointmentInfo,
                    "" // Empty string for button (icon will be shown)
                });
            }
            
            // Adjust column widths after loading data
            adjustColumnWidths();
            
        } catch (Exception e) {
            System.err.println("Error loading citizens table: " + e.getMessage());
            e.printStackTrace();
            model.addRow(new Object[]{"Error loading data", e.getMessage(), "", "", "", "", "", "", ""});
        }
    }
    
    private void adjustColumnWidths() {
        // Adjust column widths based on content
        customTable1.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        customTable1.getColumnModel().getColumn(1).setPreferredWidth(180); // Full Name
        customTable1.getColumnModel().getColumn(2).setPreferredWidth(200); // Transaction ID
        customTable1.getColumnModel().getColumn(3).setPreferredWidth(80);  // Gender
        customTable1.getColumnModel().getColumn(4).setPreferredWidth(100); // App Date
        customTable1.getColumnModel().getColumn(5).setPreferredWidth(60);  // Days
        customTable1.getColumnModel().getColumn(6).setPreferredWidth(120); // Status
        customTable1.getColumnModel().getColumn(7).setPreferredWidth(100); // Appointment
        customTable1.getColumnModel().getColumn(8).setPreferredWidth(80);  // Actions
        
        // Auto resize all columns
        customTable1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }
    
    private String getDocumentsStatus(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return "0/0 (0✓)";
        }
        
        int total = documents.size();
        int submitted = 0;
        int approved = 0;
        
        for (Document doc : documents) {
            if ("Submitted".equalsIgnoreCase(doc.getSubmitted())) {
                submitted++;
            }
            if ("Approved".equalsIgnoreCase(doc.getStatus())) {
                approved++;
            }
        }
        
        return submitted + "/" + total + " (" + approved + "✓)";
    }
    
    private String formatTransactionIdShort(String transactionId) {
        if (transactionId == null || transactionId.isEmpty()) {
            return "N/A";
        }
        
        String formatted = Data.IDStatus.formatTransactionId(transactionId);
        
        // Show shortened version: TXN-...-56
        if (formatted.length() > 15) {
            return "TXN-" + formatted.substring(0, 4) + "..." + formatted.substring(formatted.length() - 3);
        }
        
        return formatted;
    }
    
    private void filterTableByStatus(String status) {
        DefaultTableModel model = (DefaultTableModel) customTable1.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        customTable1.setRowSorter(sorter);
        
        if ("ALL".equalsIgnoreCase(status)) {
            sorter.setRowFilter(null);
        } else {
            // Filter by status - look for status text in column 6
            String filterText = "";
            switch (status) {
                case "PENDING":
                    filterText = "Submitted|Pending";
                    break;
                case "PROCESSING":
                    filterText = "Processing|Under Review";
                    break;
                case "READY":
                    filterText = "Ready|Ready for Pickup";
                    break;
                case "COMPLETED":
                    filterText = "Completed|Claimed";
                    break;
                case "REJECTED":
                    filterText = "Rejected|Cancelled";
                    break;
                default:
                    filterText = status;
            }
            
            RowFilter<DefaultTableModel, Object> rowFilter = RowFilter.regexFilter("(?i)" + filterText, 6);
            sorter.setRowFilter(rowFilter);
        }
    }
    
    private void searchCitizens(String searchTerm) {
        DefaultTableModel model = (DefaultTableModel) customTable1.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        customTable1.setRowSorter(sorter);
        
        if (searchTerm.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            // Search in multiple columns
            RowFilter<DefaultTableModel, Object> rowFilter = RowFilter.orFilter(Arrays.asList(
                RowFilter.regexFilter("(?i)" + searchTerm, 0), // ID
                RowFilter.regexFilter("(?i)" + searchTerm, 1), // Name
                RowFilter.regexFilter("(?i)" + searchTerm, 2), // Transaction ID
                RowFilter.regexFilter("(?i)" + searchTerm, 6)  // Status
            ));
            sorter.setRowFilter(rowFilter);
        }
    }
    
    private void handleFilterContextAction(String filterName, String action) {
        switch (action) {
            case "VIEW_DETAILS":
                showFilterDetails(filterName);
                break;
            case "EXCLUDE":
                excludeFilter(filterName);
                break;
        }
    }
    
    private void showFilterDetails(String filterName) {
        int count = statusCounts.getOrDefault(filterName, 0);
        int total = statusCounts.getOrDefault("ALL", 1);
        JOptionPane.showMessageDialog(this,
            filterName + " Applications: " + count + "\n" +
            "Percentage: " + calculatePercentage(count, total) + "%",
            "Filter Details",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void excludeFilter(String filterName) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Exclude '" + filterName + "' from results?",
            "Exclude Filter",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Reapply filter excluding this status
            reapplyFiltersExcluding(filterName);
        }
    }
    
    private void reapplyFiltersExcluding(String excludedFilter) {
        // Get current active filter
        String activeFilter = customFilterBar1.getActiveFilter();
        
        // If excluded filter is active, switch to ALL
        if (excludedFilter.equals(activeFilter)) {
            customFilterBar1.setActiveFilter("ALL");
        }
        
        // Apply complex filter that excludes the specified status
        filterTableByStatus(activeFilter);
    }
    
    private double calculatePercentage(int part, int total) {
        if (total == 0) return 0;
        return Math.round((part * 100.0 / total) * 100.0) / 100.0;
    }
    
    // Button Renderer with Icon
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setText("");
            setIcon(editIcon);
            setToolTipText("Edit Status");
            setFont(new Font("Segoe UI", Font.PLAIN, 11));
            setBackground(new Color(0, 120, 215));
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(0, 100, 180), 1));
            setBorderPainted(true);
            setFocusPainted(false);
            setHorizontalAlignment(SwingConstants.CENTER);
            setContentAreaFilled(true);
            setMargin(new Insets(2, 5, 2, 5));
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            // Style based on selection
            if (isSelected) {
                setBackground(new Color(0, 100, 180));
                setBorder(BorderFactory.createLineBorder(new Color(0, 80, 160), 1));
            } else {
                setBackground(new Color(0, 120, 215));
                setBorder(BorderFactory.createLineBorder(new Color(0, 100, 180), 1));
            }
            
            return this;
        }
    }
    
    // Button Editor with Icon
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private boolean isPushed;
        private int currentRow;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setIcon(editIcon);
            button.setText("");
            button.setToolTipText("Edit Status");
            button.setOpaque(true);
            button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            button.setBackground(new Color(0, 120, 215));
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 180), 1));
            button.setHorizontalAlignment(SwingConstants.CENTER);
            button.setMargin(new Insets(2, 5, 2, 5));
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            isPushed = true;
            
            if (isSelected) {
                button.setBackground(new Color(0, 100, 180));
                button.setBorder(BorderFactory.createLineBorder(new Color(0, 80, 160), 2));
            } else {
                button.setBackground(new Color(0, 120, 215));
                button.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 180), 1));
            }
            
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Handle edit button click
                editCitizenStatus(currentRow);
            }
            isPushed = false;
            return ""; // Return empty string since we're using icon
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
    
    private void editCitizenStatus(int row) {
        // Get data from the selected row
        int modelRow = customTable1.convertRowIndexToModel(row);
        String citizenIdStr = (String) customTable1.getModel().getValueAt(modelRow, 0);
        
        try {
            int citizenId = Integer.parseInt(citizenIdStr);
            Citizen citizen = Data.Citizen.getCitizenById(citizenId);
            
            if (citizen != null) {
                // Open edit dialog
                EditStatusDialog dialog = new EditStatusDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    citizen,
                    user
                );
                dialog.setVisible(true);
                
                // Refresh data if updated
                if (dialog.isUpdated()) {
                    loadAllData();
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid citizen ID",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getStatusIcon(String status) {
        if (status == null) return "⚫";
        
        String statusUpper = status.toUpperCase();
        if (statusUpper.contains("SUBMITTED") || statusUpper.contains("PENDING")) {
            return "⚫";
        } else if (statusUpper.contains("PROCESSING") || statusUpper.contains("UNDER REVIEW")) {
            return "⚪";
        } else if (statusUpper.contains("READY") || statusUpper.contains("READY FOR PICKUP")) {
            return "✅";
        } else if (statusUpper.contains("COMPLETED") || statusUpper.contains("CLAIMED")) {
            return "🟢";
        } else if (statusUpper.contains("REJECTED") || statusUpper.contains("CANCELLED")) {
            return "🔴";
        } else {
            return "⚫";
        }
    }
    
    // Edit Status Dialog (inner class)
    class EditStatusDialog extends JDialog {
        private Citizen citizen;
        private User staffUser;
        private boolean updated = false;
        
        private JComboBox<String> statusComboBox;
        private JTextArea notesTextArea;
        private JLabel citizenInfoLabel;
        private JLabel currentStatusLabel;
        
        public EditStatusDialog(JFrame parent, Citizen citizen, User staffUser) {
            super(parent, "Update Citizen Status", true);
            this.citizen = citizen;
            this.staffUser = staffUser;
            
            initComponents();
            loadCurrentStatus();
            loadStatusOptions();
            pack();
            setLocationRelativeTo(parent);
            setSize(500, 400);
        }
        
        private void initComponents() {
            setLayout(new BorderLayout());
            
            // Header panel
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            headerPanel.setBackground(Color.WHITE);
            
            citizenInfoLabel = new JLabel("Citizen: " + citizen.getFullName() + " (ID: " + citizen.getCitizenId() + ")");
            citizenInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            citizenInfoLabel.setForeground(new Color(70, 70, 70));
            
            currentStatusLabel = new JLabel("Current Status: ");
            currentStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            currentStatusLabel.setForeground(new Color(100, 100, 100));
            
            headerPanel.add(citizenInfoLabel, BorderLayout.NORTH);
            headerPanel.add(currentStatusLabel, BorderLayout.SOUTH);
            
            // Main form panel
            JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            formPanel.setBackground(Color.WHITE);
            
            // Status selection
            JPanel statusPanel = new JPanel(new BorderLayout(5, 5));
            statusPanel.setBackground(Color.WHITE);
            JLabel statusLabel = new JLabel("New Status:");
            statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            statusComboBox = new JComboBox<>();
            statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            statusPanel.add(statusLabel, BorderLayout.WEST);
            statusPanel.add(statusComboBox, BorderLayout.CENTER);
            
            // Notes field
            JPanel notesPanel = new JPanel(new BorderLayout(5, 5));
            notesPanel.setBackground(Color.WHITE);
            JLabel notesLabel = new JLabel("Internal Notes:");
            notesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            notesTextArea = new JTextArea(5, 30);
            notesTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            notesTextArea.setLineWrap(true);
            notesTextArea.setWrapStyleWord(true);
            JScrollPane notesScrollPane = new JScrollPane(notesTextArea);
            
            notesPanel.add(notesLabel, BorderLayout.NORTH);
            notesPanel.add(notesScrollPane, BorderLayout.CENTER);
            
            formPanel.add(statusPanel);
            formPanel.add(notesPanel);
            
            // Citizen notification (optional)
            JPanel notificationPanel = new JPanel(new BorderLayout(5, 5));
            notificationPanel.setBackground(Color.WHITE);
            JLabel notifyLabel = new JLabel("Notification to Citizen:");
            notifyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            JTextArea notificationTextArea = new JTextArea(3, 30);
            notificationTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            notificationTextArea.setLineWrap(true);
            notificationTextArea.setText("Your National ID application status has been updated.");
            JScrollPane notifyScrollPane = new JScrollPane(notificationTextArea);
            
            notificationPanel.add(notifyLabel, BorderLayout.NORTH);
            notificationPanel.add(notifyScrollPane, BorderLayout.CENTER);
            
            formPanel.add(notificationPanel);
            
            // Buttons panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            cancelButton.addActionListener(e -> dispose());
            
            JButton updateButton = new JButton("Update Status");
            updateButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            updateButton.setBackground(new Color(0, 120, 215));
            updateButton.setForeground(Color.WHITE);
            updateButton.addActionListener(e -> updateStatus(notificationTextArea.getText()));
            
            buttonPanel.add(cancelButton);
            buttonPanel.add(updateButton);
            
            // Add all panels
            add(headerPanel, BorderLayout.NORTH);
            add(formPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        private void loadStatusOptions() {
            // Load status names from database
            List<StatusName> statusNames = Data.StatusName.getAllStatusNames();
            for (StatusName status : statusNames) {
                statusComboBox.addItem(status.getStatusName());
            }
        }
        
        private void loadCurrentStatus() {
            IDStatus currentStatus = Data.IDStatus.getStatusByCitizenId(citizen.getCitizenId());
            if (currentStatus != null) {
                String status = currentStatus.getStatus();
                currentStatusLabel.setText("Current Status: " + (status != null ? status : "Not set"));
                
                // Set combo box to current status
                if (status != null) {
                    statusComboBox.setSelectedItem(status);
                }
            }
        }
        
        private void updateStatus(String citizenNotification) {
            String newStatus = (String) statusComboBox.getSelectedItem();
            String notes = notesTextArea.getText().trim();

            if (newStatus == null || newStatus.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a status", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Get status name from database
                List<StatusName> allStatuses = Data.StatusName.getAllStatusNames();
                StatusName selectedStatus = null;
                
                for (StatusName status : allStatuses) {
                    if (newStatus.equals(status.getStatusName())) {
                        selectedStatus = status;
                        break;
                    }
                }
                
                if (selectedStatus == null) {
                    JOptionPane.showMessageDialog(this, "Invalid status selected", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Get existing transaction ID or generate new one
                IDStatus existingStatus = Data.IDStatus.getStatusByCitizenId(citizen.getCitizenId());
                String transactionId = Data.IDStatus.generateTransactionId(citizen.getCitizenId());

                if (existingStatus != null && existingStatus.getTransactionId() != null) {
                    transactionId = existingStatus.getTransactionId();
                }

                // Create new status record
                IDStatus newStatusRecord = new IDStatus();
                newStatusRecord.setCitizenId(citizen.getCitizenId());
                newStatusRecord.setStatusNameId(selectedStatus.getStatusNameId());
                newStatusRecord.setTransactionId(transactionId);
                newStatusRecord.setUpdateDate(new java.sql.Date(System.currentTimeMillis()));
                newStatusRecord.setNotes("Updated by " + staffUser.getUsername() + 
                    " (" + staffUser.getFullName() + "): " + notes);

                // Save to database
                boolean success = Data.IDStatus.addStatus(newStatusRecord);

                if (success) {
                    updated = true;

                    // Log activity
                    Data.ActivityLog.logActivity(staffUser.getUserId(),
                        "Updated status for citizen " + citizen.getCitizenId() + 
                        " (" + citizen.getFullName() + ") to: " + newStatus);

                    // Send notification to citizen if email exists
                    if (citizen.getEmail() != null && !citizen.getEmail().isEmpty()) {
                        sendStatusNotification(citizen, newStatus, citizenNotification);
                    }

                    JOptionPane.showMessageDialog(this,
                        "Status updated successfully to: " + newStatus,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to update status. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error updating status: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        
        private void sendStatusNotification(Citizen citizen, String newStatus, String message) {
            // Create notification record
            Notification notification = new Notification();
            notification.setCitizenId(citizen.getCitizenId());
            notification.setNotificationDate(new java.sql.Date(System.currentTimeMillis()));
            notification.setNotificationTime(new SimpleDateFormat("HH:mm").format(new java.util.Date()));
            notification.setMessage("Status Update: " + message);
            notification.setType("Status Update");
            notification.setReadStatus("Unread");
            
            Data.Notification.addNotification(notification);
        }
        
        public boolean isUpdated() {
            return updated;
        }
    }
    
    // Public method to refresh data (can be called from outside)
    public void refreshData() {
        loadAllData();
    }
    
    // Public method to get active filter
    public String getActiveFilter() {
        return customFilterBar1 != null ? customFilterBar1.getActiveFilter() : "ALL";
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MainContentsTabbedPane = new component.NoTabJTabbedPane();
        MainIDStatusDetails = new javax.swing.JPanel();
        customScrollPane1 = new component.Scroll.CustomScrollPane();
        customTable1 = new component.Table.CustomTable();
        searchField = new sys.main.CustomTextField();
        RefreshButton = new component.Button.FlatButton();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        MyApplicationStatusBoxPanel = new javax.swing.JPanel();
        MyApplicationStatusValueLabel = new javax.swing.JLabel();
        MyApplicationStatusTitleLabel = new javax.swing.JLabel();
        DaySinceApplicationBoxPanel = new javax.swing.JPanel();
        DaySinceApplicationValueLabel = new javax.swing.JLabel();
        DaySinceApplicationTitleLabel = new javax.swing.JLabel();
        MyAppointmentBoxPanel = new javax.swing.JPanel();
        MyAppointmentCountLabel = new javax.swing.JLabel();
        MyAppointmentTitleLabel = new javax.swing.JLabel();
        NotificationsBoxPanel = new javax.swing.JPanel();
        NotificationsValueLabel = new javax.swing.JLabel();
        NotificationsTitleLabel = new javax.swing.JLabel();
        customFilterBar1 = new component.filter.CustomFilterBar();

        setBackground(new java.awt.Color(250, 250, 250));
        setPreferredSize(new java.awt.Dimension(1000, 550));

        MainContentsTabbedPane.setPreferredSize(new java.awt.Dimension(1000, 550));

        MainIDStatusDetails.setBackground(new java.awt.Color(255, 255, 255));

        customScrollPane1.setPreferredSize(new java.awt.Dimension(1000, 300));

        customTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1", "Juan Dela Cruz", "1234-5678-9123-4578-9123-4567-89", "Male", "2024-01-15", "25", "⚪ Processing", "2024-02-10", "✏️"},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Full Name", "Transaction ID", "Gender", "App Date", "Days", "Status", "Appointment", "Actions"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        customTable1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        customTable1.setRowHeight(40);
        customScrollPane1.setViewportView(customTable1);
        if (customTable1.getColumnModel().getColumnCount() > 0) {
            customTable1.getColumnModel().getColumn(0).setResizable(false);
            customTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
            customTable1.getColumnModel().getColumn(1).setResizable(false);
            customTable1.getColumnModel().getColumn(1).setPreferredWidth(150);
            customTable1.getColumnModel().getColumn(2).setResizable(false);
            customTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
            customTable1.getColumnModel().getColumn(3).setResizable(false);
            customTable1.getColumnModel().getColumn(3).setPreferredWidth(50);
            customTable1.getColumnModel().getColumn(4).setResizable(false);
            customTable1.getColumnModel().getColumn(4).setPreferredWidth(80);
            customTable1.getColumnModel().getColumn(5).setResizable(false);
            customTable1.getColumnModel().getColumn(5).setPreferredWidth(50);
            customTable1.getColumnModel().getColumn(6).setResizable(false);
            customTable1.getColumnModel().getColumn(6).setPreferredWidth(80);
            customTable1.getColumnModel().getColumn(7).setResizable(false);
            customTable1.getColumnModel().getColumn(7).setPreferredWidth(50);
            customTable1.getColumnModel().getColumn(8).setResizable(false);
            customTable1.getColumnModel().getColumn(8).setPreferredWidth(50);
        }

        searchField.setExpandedHeight(50);
        searchField.setFocusable(false);
        searchField.setPlaceholder("Search");
        searchField.setPlaceholderColor(null);
        searchField.setRequestFocusEnabled(false);

        RefreshButton.setText("Refresh");
        RefreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RefreshButtonActionPerformed(evt);
            }
        });

        jLayeredPane1.setPreferredSize(new java.awt.Dimension(1000, 100));
        jLayeredPane1.setLayout(new java.awt.GridLayout());

        MyApplicationStatusBoxPanel.setBackground(new java.awt.Color(254, 161, 156));
        MyApplicationStatusBoxPanel.setPreferredSize(new java.awt.Dimension(200, 100));

        MyApplicationStatusValueLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        MyApplicationStatusValueLabel.setForeground(new java.awt.Color(25, 25, 25));
        MyApplicationStatusValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        MyApplicationStatusValueLabel.setText("0");
        MyApplicationStatusValueLabel.setToolTipText("");
        MyApplicationStatusValueLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        MyApplicationStatusTitleLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        MyApplicationStatusTitleLabel.setForeground(new java.awt.Color(25, 25, 25));
        MyApplicationStatusTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        MyApplicationStatusTitleLabel.setText("Total Applications");
        MyApplicationStatusTitleLabel.setToolTipText("");
        MyApplicationStatusTitleLabel.setPreferredSize(new java.awt.Dimension(140, 43));

        javax.swing.GroupLayout MyApplicationStatusBoxPanelLayout = new javax.swing.GroupLayout(MyApplicationStatusBoxPanel);
        MyApplicationStatusBoxPanel.setLayout(MyApplicationStatusBoxPanelLayout);
        MyApplicationStatusBoxPanelLayout.setHorizontalGroup(
            MyApplicationStatusBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MyApplicationStatusBoxPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(MyApplicationStatusBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(MyApplicationStatusValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(MyApplicationStatusTitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        MyApplicationStatusBoxPanelLayout.setVerticalGroup(
            MyApplicationStatusBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MyApplicationStatusBoxPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(MyApplicationStatusValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(MyApplicationStatusTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLayeredPane1.add(MyApplicationStatusBoxPanel);

        DaySinceApplicationBoxPanel.setBackground(new java.awt.Color(249, 254, 156));
        DaySinceApplicationBoxPanel.setPreferredSize(new java.awt.Dimension(200, 100));

        DaySinceApplicationValueLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        DaySinceApplicationValueLabel.setForeground(new java.awt.Color(25, 25, 25));
        DaySinceApplicationValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        DaySinceApplicationValueLabel.setText("0");
        DaySinceApplicationValueLabel.setToolTipText("");
        DaySinceApplicationValueLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        DaySinceApplicationTitleLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        DaySinceApplicationTitleLabel.setForeground(new java.awt.Color(25, 25, 25));
        DaySinceApplicationTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        DaySinceApplicationTitleLabel.setText("Pending");
        DaySinceApplicationTitleLabel.setToolTipText("");
        DaySinceApplicationTitleLabel.setPreferredSize(new java.awt.Dimension(140, 43));

        javax.swing.GroupLayout DaySinceApplicationBoxPanelLayout = new javax.swing.GroupLayout(DaySinceApplicationBoxPanel);
        DaySinceApplicationBoxPanel.setLayout(DaySinceApplicationBoxPanelLayout);
        DaySinceApplicationBoxPanelLayout.setHorizontalGroup(
            DaySinceApplicationBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DaySinceApplicationBoxPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(DaySinceApplicationBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DaySinceApplicationValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(DaySinceApplicationTitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        DaySinceApplicationBoxPanelLayout.setVerticalGroup(
            DaySinceApplicationBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DaySinceApplicationBoxPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(DaySinceApplicationValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(DaySinceApplicationTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jLayeredPane1.add(DaySinceApplicationBoxPanel);

        MyAppointmentBoxPanel.setBackground(new java.awt.Color(200, 254, 156));
        MyAppointmentBoxPanel.setPreferredSize(new java.awt.Dimension(200, 100));

        MyAppointmentCountLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        MyAppointmentCountLabel.setForeground(new java.awt.Color(25, 25, 25));
        MyAppointmentCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        MyAppointmentCountLabel.setText("0");
        MyAppointmentCountLabel.setToolTipText("");
        MyAppointmentCountLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        MyAppointmentTitleLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        MyAppointmentTitleLabel.setForeground(new java.awt.Color(25, 25, 25));
        MyAppointmentTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        MyAppointmentTitleLabel.setText("Processing");
        MyAppointmentTitleLabel.setToolTipText("");
        MyAppointmentTitleLabel.setPreferredSize(new java.awt.Dimension(140, 43));

        javax.swing.GroupLayout MyAppointmentBoxPanelLayout = new javax.swing.GroupLayout(MyAppointmentBoxPanel);
        MyAppointmentBoxPanel.setLayout(MyAppointmentBoxPanelLayout);
        MyAppointmentBoxPanelLayout.setHorizontalGroup(
            MyAppointmentBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MyAppointmentBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MyAppointmentBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(MyAppointmentCountLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(MyAppointmentTitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        MyAppointmentBoxPanelLayout.setVerticalGroup(
            MyAppointmentBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MyAppointmentBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MyAppointmentCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(MyAppointmentTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLayeredPane1.add(MyAppointmentBoxPanel);

        NotificationsBoxPanel.setBackground(new java.awt.Color(156, 200, 254));
        NotificationsBoxPanel.setPreferredSize(new java.awt.Dimension(200, 100));

        NotificationsValueLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        NotificationsValueLabel.setForeground(new java.awt.Color(25, 25, 25));
        NotificationsValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        NotificationsValueLabel.setText("0");
        NotificationsValueLabel.setToolTipText("");
        NotificationsValueLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        NotificationsTitleLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        NotificationsTitleLabel.setForeground(new java.awt.Color(25, 25, 25));
        NotificationsTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        NotificationsTitleLabel.setText("Today's Updates");
        NotificationsTitleLabel.setToolTipText("");
        NotificationsTitleLabel.setPreferredSize(new java.awt.Dimension(140, 43));

        javax.swing.GroupLayout NotificationsBoxPanelLayout = new javax.swing.GroupLayout(NotificationsBoxPanel);
        NotificationsBoxPanel.setLayout(NotificationsBoxPanelLayout);
        NotificationsBoxPanelLayout.setHorizontalGroup(
            NotificationsBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NotificationsBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(NotificationsBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(NotificationsValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(NotificationsTitleLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        NotificationsBoxPanelLayout.setVerticalGroup(
            NotificationsBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NotificationsBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(NotificationsValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(NotificationsTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLayeredPane1.add(NotificationsBoxPanel);

        customFilterBar1.setMinimumSize(new java.awt.Dimension(500, 48));
        customFilterBar1.setPreferredSize(new java.awt.Dimension(500, 48));

        javax.swing.GroupLayout MainIDStatusDetailsLayout = new javax.swing.GroupLayout(MainIDStatusDetails);
        MainIDStatusDetails.setLayout(MainIDStatusDetailsLayout);
        MainIDStatusDetailsLayout.setHorizontalGroup(
            MainIDStatusDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainIDStatusDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(customFilterBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(RefreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(customScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        MainIDStatusDetailsLayout.setVerticalGroup(
            MainIDStatusDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainIDStatusDetailsLayout.createSequentialGroup()
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addGroup(MainIDStatusDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(searchField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(RefreshButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(customFilterBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        MainContentsTabbedPane.addTab("tab2", MainIDStatusDetails);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainContentsTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainContentsTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void RefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshButtonActionPerformed
        loadAllData();
    }//GEN-LAST:event_RefreshButtonActionPerformed

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel DaySinceApplicationBoxPanel;
    private javax.swing.JLabel DaySinceApplicationTitleLabel;
    private javax.swing.JLabel DaySinceApplicationValueLabel;
    private component.NoTabJTabbedPane MainContentsTabbedPane;
    private javax.swing.JPanel MainIDStatusDetails;
    private javax.swing.JPanel MyApplicationStatusBoxPanel;
    private javax.swing.JLabel MyApplicationStatusTitleLabel;
    private javax.swing.JLabel MyApplicationStatusValueLabel;
    private javax.swing.JPanel MyAppointmentBoxPanel;
    private javax.swing.JLabel MyAppointmentCountLabel;
    private javax.swing.JLabel MyAppointmentTitleLabel;
    private javax.swing.JPanel NotificationsBoxPanel;
    private javax.swing.JLabel NotificationsTitleLabel;
    private javax.swing.JLabel NotificationsValueLabel;
    private component.Button.FlatButton RefreshButton;
    private component.filter.CustomFilterBar customFilterBar1;
    private component.Scroll.CustomScrollPane customScrollPane1;
    private component.Table.CustomTable customTable1;
    private javax.swing.JLayeredPane jLayeredPane1;
    private sys.main.CustomTextField searchField;
    // End of variables declaration//GEN-END:variables
}
