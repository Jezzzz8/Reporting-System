package component;

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
import java.util.List;
import javax.swing.table.TableRowSorter;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;

public class UpdateIDStatus extends javax.swing.JPanel {
    
    private User user;
    private List<Citizen> allCitizens;
    private Map<String, Integer> statusCounts;
    
    // For edit icon
    private ImageIcon editIcon;
    // Status icons
    private ImageIcon pendingIcon;
    private ImageIcon processingIcon;
    private ImageIcon readyIcon;
    private ImageIcon completedIcon;
    private ImageIcon rejectedIcon;
    private ImageIcon defaultStatusIcon;
    private ImageIcon productionIcon;
    
    // Custom cell renderer for status column
    private StatusCellRenderer statusCellRenderer;
    
    // Cache for citizen statuses to avoid repeated database calls
    private Map<Integer, String> citizenStatusCache;
    private Map<Integer, ImageIcon> citizenStatusIconCache;
    
    public UpdateIDStatus(User user) {
        this.user = user;
        this.citizenStatusCache = new HashMap<>();
        this.citizenStatusIconCache = new HashMap<>();
        
        // Load icons
        loadIcons();
        
        // Initialize custom renderer
        statusCellRenderer = new StatusCellRenderer();
        
        initComponents();
        initializeComponents();
        loadAllData();
    }
    
    // LOAD ICONS METHOD
    private void loadIcons() {
        try {
            // Load edit icon
            editIcon = loadIcon("/images/edit.png");

            // Load status icons
            pendingIcon = loadIcon("/images/pending.png");
            processingIcon = loadIcon("/images/processing.png");
            productionIcon = loadIcon("/images/production.png");
            readyIcon = loadIcon("/images/ready.png");
            completedIcon = loadIcon("/images/completed.png");
            rejectedIcon = loadIcon("/images/rejected.png");
            defaultStatusIcon = loadIcon("/images/default.png");

        } catch (Exception e) {
            System.err.println("Error loading icons: " + e.getMessage());
            // Create default icons if image loading fails
            createFallbackIcons();
        }
    }
    
    private ImageIcon loadIcon(String path) {
        try {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                ImageIcon originalIcon = new ImageIcon(imgURL);
                // Resize to appropriate dimensions (16x16 for table cells)
                Image scaledImage = originalIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            } else {
                System.err.println("Couldn't find icon: " + path);
                return createDefaultEditIcon(); // Fallback to drawn icon
            }
        } catch (Exception e) {
            System.err.println("Error loading icon from " + path + ": " + e.getMessage());
            return createDefaultEditIcon(); // Fallback to drawn icon
        }
    }
    
    private void createFallbackIcons() {
        // Create fallback icons if image loading fails
        editIcon = createDefaultEditIcon();
        pendingIcon = createStatusIcon(Color.ORANGE, "P");
        processingIcon = createStatusIcon(Color.BLUE, "R");
        productionIcon = createStatusIcon(new Color(204,85,0), "PD");
        readyIcon = createStatusIcon(Color.GREEN, "Y");
        completedIcon = createStatusIcon(new Color(0, 128, 0), "C");
        rejectedIcon = createStatusIcon(Color.RED, "X");
        defaultStatusIcon = createStatusIcon(Color.GRAY, "?");
    }
    
    private ImageIcon createStatusIcon(Color color, String text) {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw circle background
        g2d.setColor(color);
        g2d.fillOval(2, 2, 12, 12);
        
        // Draw white border
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawOval(2, 2, 12, 12);
        
        // Draw text
        g2d.setFont(new Font("Arial", Font.BOLD, 8));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, (16 - textWidth) / 2, (16 + textHeight) / 2 - 2);
        
        g2d.dispose();
        return new ImageIcon(image);
    }
    
    // CREATE DEFAULT EDIT ICON METHOD (fallback)
    private ImageIcon createDefaultEditIcon() {
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

        // Configure filter bar - UPDATE PROPERTIES AFTER INIT
        configureFilterBar();

        // Configure search field
        configureSearchField();

        // Configure refresh button
        configureRefreshButton();

        // Setup auto-resizing
        setupAutoResizing();

        // UPDATE FILTER BAR PROPERTIES
        updateFilterBarProperties();
    }

    private void updateFilterBarProperties() {
        if (customFilterBar != null) {
            // Set auto-resize to true
            customFilterBar.setAutoResizeButtons(true);

            // Adjust custom widths for better display
            Map<String, Integer> customWidths = new HashMap<>();
            customWidths.put("PENDING", 130);
            customWidths.put("PROCESSING", 140);
            customWidths.put("PRODUCTION", 145);
            customWidths.put("READY", 120);
            customWidths.put("COMPLETED", 140);
            customWidths.put("REJECTED", 125);
            customWidths.put("ALL", 90);

            customFilterBar.adjustButtonWidths(customWidths);
            customFilterBar.recalculateButtonWidths();

            // Force UI update
            customFilterBar.revalidate();
            customFilterBar.repaint();
        }
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
        
        // Configure status column renderer
        setupStatusColumn();
        
        // Configure scroll pane
        configureScrollPane();
    }
    
    // NEW METHOD: Setup status column renderer
    private void setupStatusColumn() {
        int statusColumn = 6; // Status column index
        customTable1.getColumnModel().getColumn(statusColumn).setCellRenderer(statusCellRenderer);
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
        customTable1.getColumnModel().getColumn(actionColumn).setCellEditor(new ButtonEditor(new JCheckBox(), customTable1));

        // Set preferred width for action column
        customTable1.getColumnModel().getColumn(actionColumn).setPreferredWidth(60);
    }
    
    private void configureFilterBar() {
        customFilterBar.setFilterListener(new CustomFilterBar.FilterListener() {
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
        // Clear caches
        citizenStatusCache.clear();
        citizenStatusIconCache.clear();
        
        loadStatusCounts();
        loadCitizensTable();
        updateFilterBarCounts();
        updateStatusBoxes();
    }
    
    private void loadStatusCounts() {
        statusCounts = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Total applications
            String totalQuery = "SELECT COUNT(*) as total FROM citizens";
            try (PreparedStatement stmt = conn.prepareStatement(totalQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    statusCounts.put("ALL", total);
                    System.out.println("Total applications: " + total);
                }
            }

            // 2. SIMPLIFIED QUERY: Get counts for each status category
            String statusQuery = 
                "SELECT " +
                "  COUNT(CASE WHEN COALESCE(latest_status.status_name, 'Submitted') IN ('Submitted', 'Pending') THEN 1 END) as pending, " +
                "  COUNT(CASE WHEN COALESCE(latest_status.status_name, 'Submitted') IN ('Processing', 'Document Verification', " +
                "        'Biometrics Appointment', 'Biometrics Completed', " +
                "        'Background Check', 'Background Check Completed') THEN 1 END) as processing, " +
                "  COUNT(CASE WHEN COALESCE(latest_status.status_name, 'Submitted') = 'ID Card Production' THEN 1 END) as production, " +
                "  COUNT(CASE WHEN COALESCE(latest_status.status_name, 'Submitted') IN ('Ready for Pickup', 'Ready') THEN 1 END) as ready, " +
                "  COUNT(CASE WHEN COALESCE(latest_status.status_name, 'Submitted') IN ('Completed', 'Claimed', 'Delivered') THEN 1 END) as completed, " +
                "  COUNT(CASE WHEN COALESCE(latest_status.status_name, 'Submitted') IN ('Rejected', 'Cancelled', 'Failed') THEN 1 END) as rejected " +
                "FROM citizens c " +
                "LEFT JOIN ( " +
                "  SELECT ist.citizen_id, sn.status_name " +
                "  FROM id_status ist " +
                "  JOIN status_names sn ON ist.status_name_id = sn.status_name_id " +
                "  WHERE (ist.citizen_id, ist.update_date, ist.status_id) IN ( " +
                "    SELECT citizen_id, MAX(update_date) as max_date, MAX(status_id) as max_id " +
                "    FROM id_status " +
                "    GROUP BY citizen_id " +
                "  ) " +
                ") latest_status ON c.citizen_id = latest_status.citizen_id";

            System.out.println("Executing status query...");
            try (PreparedStatement stmt = conn.prepareStatement(statusQuery);
                 ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    statusCounts.put("PENDING", rs.getInt("pending"));
                    statusCounts.put("PROCESSING", rs.getInt("processing"));
                    statusCounts.put("PRODUCTION", rs.getInt("production"));
                    statusCounts.put("READY", rs.getInt("ready"));
                    statusCounts.put("COMPLETED", rs.getInt("completed"));
                    statusCounts.put("REJECTED", rs.getInt("rejected"));

                    System.out.println("=== STATUS COUNTS ===");
                    System.out.println("Pending: " + statusCounts.get("PENDING"));
                    System.out.println("Processing: " + statusCounts.get("PROCESSING"));
                    System.out.println("Production: " + statusCounts.get("PRODUCTION"));
                    System.out.println("Ready: " + statusCounts.get("READY"));
                    System.out.println("Completed: " + statusCounts.get("COMPLETED"));
                    System.out.println("Rejected: " + statusCounts.get("REJECTED"));
                    System.out.println("=====================");
                }
            }

            // 3. TODAY'S UPDATES: Changed from DISTINCT citizen_id to COUNT(*) for TOTAL updates
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = dateFormat.format(new java.util.Date());
            String todayQuery = 
                "SELECT COUNT(*) as today_updates FROM id_status WHERE DATE(update_date) = ?";

            try (PreparedStatement stmt = conn.prepareStatement(todayQuery)) {
                stmt.setString(1, today);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int todayUpdates = rs.getInt("today_updates");
                        statusCounts.put("TODAY_UPDATES", todayUpdates);
                        System.out.println("Today's TOTAL updates (all records): " + todayUpdates);

                        // Optional: Also count distinct citizens if needed for comparison
                        String distinctQuery = "SELECT COUNT(DISTINCT citizen_id) as distinct_updates FROM id_status WHERE DATE(update_date) = ?";
                        try (PreparedStatement distinctStmt = conn.prepareStatement(distinctQuery)) {
                            distinctStmt.setString(1, today);
                            try (ResultSet distinctRs = distinctStmt.executeQuery()) {
                                if (distinctRs.next()) {
                                    System.out.println("Today's DISTINCT citizen updates: " + distinctRs.getInt("distinct_updates"));
                                }
                            }
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading status counts: " + e.getMessage());
            e.printStackTrace();
            // Set default values
            statusCounts.put("ALL", 0);
            statusCounts.put("PENDING", 0);
            statusCounts.put("PROCESSING", 0);
            statusCounts.put("PRODUCTION", 0);
            statusCounts.put("READY", 0);
            statusCounts.put("COMPLETED", 0);
            statusCounts.put("REJECTED", 0);
            statusCounts.put("TODAY_UPDATES", 0);
        }
    }
    
    private void updateFilterBarCounts() {
        if (statusCounts != null && customFilterBar != null) {
            Map<String, Integer> filterCounts = new LinkedHashMap<>(); // Use LinkedHashMap to maintain order

            // Set the order to match your database query groups
            filterCounts.put("ALL", statusCounts.getOrDefault("ALL", 0));
            filterCounts.put("PENDING", statusCounts.getOrDefault("PENDING", 0));
            filterCounts.put("PROCESSING", statusCounts.getOrDefault("PROCESSING", 0));
            filterCounts.put("PRODUCTION", statusCounts.getOrDefault("PRODUCTION", 0)); // NEW
            filterCounts.put("READY", statusCounts.getOrDefault("READY", 0));
            filterCounts.put("COMPLETED", statusCounts.getOrDefault("COMPLETED", 0));
            filterCounts.put("REJECTED", statusCounts.getOrDefault("REJECTED", 0));

            System.out.println("Setting filter bar counts: " + filterCounts);
            customFilterBar.setFilterCounts(filterCounts);

            // FORCE RECALCULATION OF BUTTON WIDTHS
            customFilterBar.recalculateButtonWidths();

            // Force UI update
            customFilterBar.revalidate();
            customFilterBar.repaint();
        }
    }
    
    private void updateStatusBoxes() {
        if (statusCounts != null) {
            // Update the status box labels to match your database categories
            MyApplicationStatusValueLabel.setText(String.valueOf(statusCounts.getOrDefault("ALL", 0)));
            DaySinceApplicationValueLabel.setText(String.valueOf(statusCounts.getOrDefault("PENDING", 0)));
            MyAppointmentCountLabel.setText(String.valueOf(statusCounts.getOrDefault("PROCESSING", 0)));
            NotificationsValueLabel.setText(String.valueOf(statusCounts.getOrDefault("TODAY_UPDATES", 0)));

            // Update titles
            MyApplicationStatusTitleLabel.setText("Total Applications");
            DaySinceApplicationTitleLabel.setText("Pending");
            MyAppointmentTitleLabel.setText("Processing");
            NotificationsTitleLabel.setText("Today's Updates");
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
                
                // Get current status - using cached version
                String statusText = getLatestStatusForCitizen(citizenId);
                
                // Get transaction ID if available
                String transactionId = "N/A";
                IDStatus status = Data.IDStatus.getStatusByCitizenId(citizenId);
                if (status != null && status.getTransactionId() != null) {
                    transactionId = formatTransactionIdShort(status.getTransactionId());
                }
                
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
                
                // Store in the table model
                model.addRow(new Object[]{
                    String.valueOf(citizenId),
                    fullName,
                    transactionId,
                    gender,
                    appDate,
                    String.valueOf(daysSince),
                    statusText,
                    appointmentInfo,
                    ""
                });
            }
            
            // Adjust column widths after loading data
            adjustColumnWidths();
            
            System.out.println("Loaded " + allCitizens.size() + " citizens into table");
            
        } catch (Exception e) {
            System.err.println("Error loading citizens table: " + e.getMessage());
            e.printStackTrace();
            model.addRow(new Object[]{"Error loading data", e.getMessage(), "", "", "", "", "", "", ""});
        }
    }
    
    private String getLatestStatusForCitizen(int citizenId) {
        // First check cache
        if (citizenStatusCache.containsKey(citizenId)) {
            return citizenStatusCache.get(citizenId);
        }

        // SIMPLIFIED QUERY
        String query = 
            "SELECT sn.status_name " +
            "FROM id_status ist " +
            "JOIN status_names sn ON ist.status_name_id = sn.status_name_id " +
            "WHERE ist.citizen_id = ? " +
            "ORDER BY ist.update_date DESC, ist.status_id DESC " +
            "LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, citizenId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status_name");
                citizenStatusCache.put(citizenId, status);
                // Also cache the icon
                citizenStatusIconCache.put(citizenId, getStatusIcon(status));
                return status;
            } else {
                // No status found, default to "Submitted"
                citizenStatusCache.put(citizenId, "Submitted");
                citizenStatusIconCache.put(citizenId, pendingIcon);
                return "Submitted";
            }
        } catch (SQLException e) {
            System.err.println("Error getting latest status for citizen " + citizenId + ": " + e.getMessage());
            // Default if error
            citizenStatusCache.put(citizenId, "Submitted");
            citizenStatusIconCache.put(citizenId, pendingIcon);
            return "Submitted";
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
        customTable1.getColumnModel().getColumn(6).setPreferredWidth(150); // Status (needs more space for icon+text)
        customTable1.getColumnModel().getColumn(7).setPreferredWidth(100); // Appointment
        customTable1.getColumnModel().getColumn(8).setPreferredWidth(80);  // Actions
        
        // Auto resize all columns
        customTable1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
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
            System.out.println("Showing ALL applications");
        } else {
            // Filter by status - look for status text in column 6
            List<String> statusPatterns = new ArrayList<>();
            switch (status.toUpperCase()) {
                case "PENDING":
                    statusPatterns.add("(?i).*submitted.*");
                    statusPatterns.add("(?i).*pending.*");
                    break;
                case "PROCESSING":
                    statusPatterns.add("(?i).*processing.*");
                    statusPatterns.add("(?i).*under review.*");
                    statusPatterns.add("(?i).*verification.*");
                    statusPatterns.add("(?i).*biometrics.*");
                    statusPatterns.add("(?i).*background check.*");
                    statusPatterns.add("(?i).*document verification.*");
                    break;
                case "PRODUCTION": // UPDATED: Production filter
                    statusPatterns.add("(?i).*id card production.*");
                    statusPatterns.add("(?i).*production.*");
                    break;
                case "READY":
                    statusPatterns.add("(?i).*ready.*");
                    statusPatterns.add("(?i).*ready for pickup.*");
                    break;
                case "COMPLETED":
                    statusPatterns.add("(?i).*completed.*");
                    statusPatterns.add("(?i).*claimed.*");
                    statusPatterns.add("(?i).*delivered.*");
                    break;
                case "REJECTED":
                    statusPatterns.add("(?i).*rejected.*");
                    statusPatterns.add("(?i).*cancelled.*");
                    statusPatterns.add("(?i).*failed.*");
                    break;
                default:
                    statusPatterns.add("(?i).*" + status + ".*");
            }

            List<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();
            for (String pattern : statusPatterns) {
                filters.add(RowFilter.regexFilter(pattern, 6));
            }

            RowFilter<DefaultTableModel, Object> rowFilter = RowFilter.orFilter(filters);
            sorter.setRowFilter(rowFilter);
            System.out.println("Filtering by " + status + " - patterns: " + statusPatterns);
        }
    }

    private void searchCitizens(String searchTerm) {
        DefaultTableModel model = (DefaultTableModel) customTable1.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        customTable1.setRowSorter(sorter);

        if (searchTerm.trim().isEmpty()) {
            // If search is empty, apply the current filter
            String currentFilter = customFilterBar.getActiveFilter();
            if (!"ALL".equals(currentFilter)) {
                filterTableByStatus(currentFilter);
            } else {
                sorter.setRowFilter(null);
            }
        } else {
            // Combine search with current filter
            List<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();
            
            // Search filter
            filters.add(RowFilter.orFilter(Arrays.asList(
                RowFilter.regexFilter("(?i)" + searchTerm, 0), // ID
                RowFilter.regexFilter("(?i)" + searchTerm, 1), // Name
                RowFilter.regexFilter("(?i)" + searchTerm, 2), // Transaction ID
                RowFilter.regexFilter("(?i)" + searchTerm, 6)  // Status text
            )));
            
            // Apply status filter if not "ALL"
            String currentFilter = customFilterBar.getActiveFilter();
            if (!"ALL".equals(currentFilter)) {
                // Add status filter patterns
                List<String> statusPatterns = new ArrayList<>();
                switch (currentFilter.toUpperCase()) {
                    case "PENDING":
                        statusPatterns.add("(?i).*submitted.*");
                        statusPatterns.add("(?i).*pending.*");
                        break;
                    case "PROCESSING":
                        statusPatterns.add("(?i).*processing.*");
                        statusPatterns.add("(?i).*under review.*");
                        break;
                    case "PRODUCTION":
                        statusPatterns.add("(?i).*id card production.*");
                        statusPatterns.add("(?i).*production.*");
                        break;
                    case "READY":
                        statusPatterns.add("(?i).*ready.*");
                        break;
                    case "COMPLETED":
                        statusPatterns.add("(?i).*completed.*");
                        statusPatterns.add("(?i).*claimed.*");
                        break;
                    case "REJECTED":
                        statusPatterns.add("(?i).*rejected.*");
                        statusPatterns.add("(?i).*cancelled.*");
                        break;
                }
                
                List<RowFilter<DefaultTableModel, Object>> statusFilters = new ArrayList<>();
                for (String pattern : statusPatterns) {
                    statusFilters.add(RowFilter.regexFilter(pattern, 6));
                }
                
                if (!statusFilters.isEmpty()) {
                    filters.add(RowFilter.orFilter(statusFilters));
                }
            }
            
            // Combine all filters with AND logic
            RowFilter<DefaultTableModel, Object> combinedFilter = RowFilter.andFilter(filters);
            sorter.setRowFilter(combinedFilter);
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
        String activeFilter = customFilterBar.getActiveFilter();
        
        // If excluded filter is active, switch to ALL
        if (excludedFilter.equals(activeFilter)) {
            customFilterBar.setActiveFilter("ALL");
        }
        
        // Apply complex filter that excludes the specified status
        filterTableByStatus(activeFilter);
    }
    
    private double calculatePercentage(int part, int total) {
        if (total == 0) return 0;
        return Math.round((part * 100.0 / total) * 100.0) / 100.0;
    }
    
    private ImageIcon getStatusIcon(String status) {
        if (status == null) return defaultStatusIcon;

        String statusUpper = status.toUpperCase();
        if (statusUpper.contains("SUBMITTED") || statusUpper.contains("PENDING")) {
            return pendingIcon;
        } else if (statusUpper.contains("PROCESSING") || 
                   statusUpper.contains("DOCUMENT VERIFICATION") ||
                   statusUpper.contains("BIOMETRICS APPOINTMENT") ||
                   statusUpper.contains("BIOMETRICS COMPLETED") ||
                   statusUpper.contains("BACKGROUND CHECK") ||
                   statusUpper.contains("UNDER REVIEW") || 
                   statusUpper.contains("VERIFICATION")) {
            return processingIcon;
        } else if (statusUpper.contains("ID CARD PRODUCTION") || 
                   statusUpper.contains("PRODUCTION")) {
            return productionIcon; // Use production icon
        } else if (statusUpper.contains("READY") || 
                   statusUpper.contains("READY FOR PICKUP")) {
            return readyIcon;
        } else if (statusUpper.contains("COMPLETED") || 
                   statusUpper.contains("CLAIMED") || 
                   statusUpper.contains("DELIVERED")) {
            return completedIcon;
        } else if (statusUpper.contains("REJECTED") || 
                   statusUpper.contains("CANCELLED") || 
                   statusUpper.contains("FAILED")) {
            return rejectedIcon;
        } else {
            return defaultStatusIcon;
        }
    }   
    
    class StatusCellData {
        private String statusText;
        private ImageIcon statusIcon;

        public StatusCellData(String statusText, ImageIcon statusIcon) {
            this.statusText = statusText;
            this.statusIcon = statusIcon;
        }

        public String getStatusText() {
            return statusText;
        }

        public ImageIcon getStatusIcon() {
            return statusIcon;
        }

        @Override
        public String toString() {
            return statusText;
        }
    }
    
    class StatusCellRenderer extends JLabel implements TableCellRenderer {

        public StatusCellRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setHorizontalAlignment(SwingConstants.LEFT);
            setVerticalAlignment(SwingConstants.CENTER);
            setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            String statusText = value != null ? value.toString() : "";
            setText(statusText);
            
            // Get the icon for this status (using cache if available)
            int modelRow = table.convertRowIndexToModel(row);
            Object idValue = table.getModel().getValueAt(modelRow, 0);
            if (idValue != null) {
                try {
                    int citizenId = Integer.parseInt(idValue.toString());
                    if (citizenStatusIconCache.containsKey(citizenId)) {
                        setIcon(citizenStatusIconCache.get(citizenId));
                    } else {
                        setIcon(getStatusIcon(statusText));
                    }
                } catch (NumberFormatException e) {
                    setIcon(getStatusIcon(statusText));
                }
            } else {
                setIcon(getStatusIcon(statusText));
            }

            // Handle selection background
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                // Use alternating row colors
                if (row % 2 == 0) {
                    setBackground(customTable1.getEvenRowColor());
                } else {
                    setBackground(customTable1.getOddRowColor());
                }
                setForeground(customTable1.getNormalTextColor());
            }

            return this;
        }
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
        private JTable table;

        public ButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            this.table = table;
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
                // Use SwingUtilities to ensure this runs on the EDT
                SwingUtilities.invokeLater(() -> {
                    editCitizenStatus(currentRow);
                });
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
                // Open the new EditStatusDialog
                component.EditStatusDialog dialog = new component.EditStatusDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    true,
                    citizen,
                    user
                );
                dialog.setVisible(true);

                // Refresh data if updated
                if (dialog.isUpdated()) {
                    // Clear cache for this citizen
                    citizenStatusCache.remove(citizenId);
                    citizenStatusIconCache.remove(citizenId);

                    // Refresh the table and counts
                    loadAllData();

                    // Optional: Show success message
                    JOptionPane.showMessageDialog(this,
                        "✓ Status updated successfully!\nThe table has been refreshed.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Citizen not found with ID: " + citizenIdStr,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid citizen ID: " + citizenIdStr,
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Edit Status Dialog (inner class) - You need to implement this based on your needs
    class EditStatusDialog extends JDialog {
        private boolean updated = false;
        
        public EditStatusDialog(JFrame parent, Citizen citizen, User user) {
            super(parent, "Edit Status - " + citizen.getFullName(), true);
            setSize(400, 300);
            setLocationRelativeTo(parent);
            // Add your dialog components here
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
        return customFilterBar != null ? customFilterBar.getActiveFilter() : "ALL";
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
        customFilterBar = new component.filter.CustomFilterBar();

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
        customTable1.setHeaderColor(new java.awt.Color(150, 220, 240));
        customTable1.setHoverColor(java.awt.Color.white);
        customTable1.setSelectedRowColor(java.awt.Color.white);
        customTable1.setSelectedRowTextColor(java.awt.Color.black);
        customTable1.setShowGrid(false);
        customTable1.getTableHeader().setResizingAllowed(false);
        customTable1.getTableHeader().setReorderingAllowed(false);
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
            customTable1.getColumnModel().getColumn(8).setPreferredWidth(15);
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
        jLayeredPane1.setLayout(new java.awt.GridLayout(1, 0));

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
                .addComponent(MyApplicationStatusTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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

        customFilterBar.setAutoResizeButtons(true);

        javax.swing.GroupLayout MainIDStatusDetailsLayout = new javax.swing.GroupLayout(MainIDStatusDetails);
        MainIDStatusDetails.setLayout(MainIDStatusDetailsLayout);
        MainIDStatusDetailsLayout.setHorizontalGroup(
            MainIDStatusDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainIDStatusDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customFilterBar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RefreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 995, Short.MAX_VALUE)
            .addComponent(customScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        MainIDStatusDetailsLayout.setVerticalGroup(
            MainIDStatusDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainIDStatusDetailsLayout.createSequentialGroup()
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(MainIDStatusDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MainIDStatusDetailsLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(MainIDStatusDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(searchField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(RefreshButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(MainIDStatusDetailsLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(customFilterBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 381, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    private component.filter.CustomFilterBar customFilterBar;
    private component.Scroll.CustomScrollPane customScrollPane1;
    private component.Table.CustomTable customTable1;
    private javax.swing.JLayeredPane jLayeredPane1;
    private sys.main.CustomTextField searchField;
    // End of variables declaration//GEN-END:variables
}
