package component;

import backend.objects.Data.IDStatus;
import backend.objects.Data.Citizen;
import backend.objects.Data.User;

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
import java.util.Date;

public class StatusHistory extends javax.swing.JPanel {
    
    private User user;
    private List<IDStatus> allStatusHistory;
    private Map<String, Integer> statusCounts;
    
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
    
    // Cache for status icons
    private Map<Integer, ImageIcon> statusIconCache;
    
    // Table sorter
    private TableRowSorter<DefaultTableModel> tableSorter;
    
    public StatusHistory(Data.User user) {
        this.user = user;
        this.statusIconCache = new HashMap<>();
        
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
                return createDefaultStatusIcon();
            }
        } catch (Exception e) {
            System.err.println("Error loading icon from " + path + ": " + e.getMessage());
            return createDefaultStatusIcon();
        }
    }
    
    private void createFallbackIcons() {
        // Create fallback icons if image loading fails
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
    
    private ImageIcon createDefaultStatusIcon() {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.GRAY);
        g2d.fillOval(2, 2, 12, 12);
        g2d.setColor(Color.WHITE);
        g2d.drawOval(2, 2, 12, 12);
        g2d.dispose();
        return new ImageIcon(image);
    }
    
    private void initializeComponents() {
        // Configure table first (this sets up the model and sorter)
        configureTable();

        // Configure filter bar
        configureFilterBar();

        // Configure search field
        configureSearchField();

        // Configure refresh button
        configureRefreshButton();

        // UPDATE FILTER BAR PROPERTIES
        updateFilterBarProperties();

        // Load data after everything is configured
        loadAllData();
    }

    private void updateFilterBarProperties() {
        if (customFilterBar != null) {
            // Set auto-resize to true
            customFilterBar.setAutoResizeButtons(true);

            // Adjust custom widths for DATE filters
            Map<String, Integer> customWidths = new HashMap<>();
            customWidths.put("ALL", 100);
            customWidths.put("TODAY", 120);
            customWidths.put("THIS WEEK", 150);
            customWidths.put("THIS MONTH", 160);
            customWidths.put("THIS YEAR", 150);

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
        customTable1.setAutoResize(true, false);

        // Initialize with empty table
        String[] columnNames = {"#", "Date", "Status", "Updated By", "Notes", "Remarks"};
        Object[][] data = {};
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, 
                java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        };

        customTable1.setModel(model);
        customTable1.getColumnModel().getColumn(2).setCellRenderer(statusCellRenderer);

        // Initialize sorter after model is set
        tableSorter = new TableRowSorter<>(model);
        customTable1.setRowSorter(tableSorter);
    }
    
    private void configureFilterBar() {
        customFilterBar.loadFiltersFromConfiguration("DATE");

        // Set the filter listener
        customFilterBar.setFilterListener(new CustomFilterBar.FilterListener() {
            @Override
            public void onFilterSelected(String filterName) {
                applyFilter(filterName);
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
    
    private void applyFilter(String filterName) {
        System.out.println("Applying filter: " + filterName);

        if (allStatusHistory == null || allStatusHistory.isEmpty()) {
            return;
        }

        List<IDStatus> filteredList;

        if ("ALL".equalsIgnoreCase(filterName)) {
            // Show all records
            filteredList = allStatusHistory;
        } else if (isDateFilter(filterName)) {
            // Apply date-based filtering
            filteredList = filterByDate(filterName);
        } else {
            // Apply status-based filtering (though we shouldn't get here with DATE filter bar)
            filteredList = filterByStatus(filterName);
        }

        // Update the table with filtered data
        updateTableWithFilteredData(filteredList);
    }

    private boolean isDateFilter(String filterName) {
        String upper = filterName.toUpperCase();
        return upper.equals("TODAY") || 
               upper.equals("THIS WEEK") || 
               upper.equals("THIS MONTH") || 
               upper.equals("THIS YEAR");
    }

    private List<IDStatus> filterByDate(String dateFilter) {
        List<IDStatus> filteredList = new ArrayList<>();

        Calendar now = Calendar.getInstance();

        for (IDStatus status : allStatusHistory) {
            if (status.getUpdateDate() == null) {
                continue;
            }

            Calendar statusCal = Calendar.getInstance();
            statusCal.setTime(status.getUpdateDate());

            boolean include = false;

            switch (dateFilter.toUpperCase()) {
                case "TODAY":
                    include = (now.get(Calendar.YEAR) == statusCal.get(Calendar.YEAR) &&
                              now.get(Calendar.MONTH) == statusCal.get(Calendar.MONTH) &&
                              now.get(Calendar.DAY_OF_MONTH) == statusCal.get(Calendar.DAY_OF_MONTH));
                    break;
                case "THIS WEEK":
                    include = (now.get(Calendar.YEAR) == statusCal.get(Calendar.YEAR) &&
                              now.get(Calendar.WEEK_OF_YEAR) == statusCal.get(Calendar.WEEK_OF_YEAR));
                    break;
                case "THIS MONTH":
                    include = (now.get(Calendar.YEAR) == statusCal.get(Calendar.YEAR) &&
                              now.get(Calendar.MONTH) == statusCal.get(Calendar.MONTH));
                    break;
                case "THIS YEAR":
                    include = (now.get(Calendar.YEAR) == statusCal.get(Calendar.YEAR));
                    break;
                default:
                    include = true;
                    break;
            }

            if (include) {
                filteredList.add(status);
            }
        }

        System.out.println("Date filter '" + dateFilter + "' found " + filteredList.size() + " records");
        return filteredList;
    }

    private List<IDStatus> filterByStatus(String statusFilter) {
        List<IDStatus> filteredList = new ArrayList<>();

        if ("ALL".equalsIgnoreCase(statusFilter)) {
            return allStatusHistory; // Return all records
        }

        for (IDStatus status : allStatusHistory) {
            String statusText = status.getStatus();
            if (statusText == null) continue;

            String statusUpper = statusText.toUpperCase();
            boolean include = false;

            switch (statusFilter.toUpperCase()) {
                case "PENDING":
                    include = statusUpper.contains("PENDING") || statusUpper.contains("SUBMITTED");
                    break;
                case "PROCESSING":
                    include = statusUpper.contains("PROCESSING") || 
                             statusUpper.contains("VERIFICATION") ||
                             statusUpper.contains("BACKGROUND") ||
                             statusUpper.contains("BIOMETRICS");
                    break;
                case "PRODUCTION":
                    include = statusUpper.contains("PRODUCTION");
                    break;
                case "READY":
                    include = statusUpper.contains("READY");
                    break;
                case "COMPLETED":
                    include = statusUpper.contains("COMPLETED");
                    break;
                case "REJECTED":
                    include = statusUpper.contains("REJECTED");
                    break;
                default:
                    include = true;
                    break;
            }

            if (include) {
                filteredList.add(status);
            }
        }

        System.out.println("Status filter '" + statusFilter + "' found " + filteredList.size() + " records");
        return filteredList;
    }
    
    private void updateTableWithFilteredData(List<IDStatus> statusList) {
        if (statusList == null) {
            // Show all data
            statusList = allStatusHistory;
        }

        // Create a final reference for use in the SwingUtilities.invokeLater
        final List<IDStatus> finalStatusList = statusList;

        // Now update the table on the EDT
        SwingUtilities.invokeLater(() -> {
            try {
                DefaultTableModel model = (DefaultTableModel) customTable1.getModel();

                // IMPORTANT: Temporarily remove the sorter before modifying the table
                customTable1.setRowSorter(null);

                // Clear the table
                model.setRowCount(0);

                if (finalStatusList == null || finalStatusList.isEmpty()) {
                    // Add a single row indicating no records found
                    model.addRow(new Object[]{"No records found", "", "", "", "", ""});
                } else {
                    // Collect data first
                    List<Object[]> tableData = new ArrayList<>();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    int sequence = 1;
                    for (IDStatus status : finalStatusList) {
                        int citizenId = status.getCitizenId();
                        String transactionId = status.getTransactionId() != null ? 
                            formatTransactionIdShort(status.getTransactionId()) : "N/A";

                        // Get citizen details
                        Citizen citizen = Data.Citizen.getCitizenById(citizenId);
                        String citizenName = citizen != null ? citizen.getFullName() : "Unknown Citizen";

                        // Get status name
                        String statusText = status.getStatus();

                        // Format date
                        String dateStr = status.getUpdateDate() != null ? 
                            dateFormat.format(status.getUpdateDate()) : "N/A";

                        // Get notes
                        String notes = status.getNotes() != null ? 
                            (status.getNotes().length() > 50 ? 
                             status.getNotes().substring(0, 47) + "..." : 
                             status.getNotes()) : "";

                        // Get user who updated (if available)
                        String updatedBy = "System";
                        if (citizen != null && citizen.getUserId() != null) {
                            User updater = Data.User.getUserById(citizen.getUserId());
                            if (updater != null) {
                                updatedBy = updater.getFullName();
                            }
                        }

                        // Calculate days since update
                        int daysSince = 0;
                        if (status.getUpdateDate() != null) {
                            long diff = new java.util.Date().getTime() - status.getUpdateDate().getTime();
                            daysSince = (int) (diff / (1000 * 60 * 60 * 24));
                        }

                        // Add to table data
                        tableData.add(new Object[]{
                            String.valueOf(sequence++),
                            dateStr,
                            statusText,
                            updatedBy,
                            notes,
                            transactionId + " - " + citizenName + " (" + daysSince + " days ago)"
                        });
                    }

                    // Add all rows at once
                    for (Object[] rowData : tableData) {
                        model.addRow(rowData);
                    }

                    System.out.println("Loaded " + tableData.size() + " records into table");
                }

                // Re-enable sorting after data is loaded
                tableSorter = new TableRowSorter<>(model);
                customTable1.setRowSorter(tableSorter);

                // Adjust column widths after loading data
                adjustColumnWidths();

                // Force UI update
                customTable1.revalidate();
                customTable1.repaint();

            } catch (Exception e) {
                System.err.println("Error updating table: " + e.getMessage());
                e.printStackTrace();

                // Try to recover by clearing and showing error
                try {
                    DefaultTableModel model = (DefaultTableModel) customTable1.getModel();
                    customTable1.setRowSorter(null);
                    model.setRowCount(0);
                    model.addRow(new Object[]{"Error loading data", e.getMessage(), "", "", "", ""});
                    customTable1.revalidate();
                    customTable1.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    
    private void configureSearchField() {
        searchField.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty() && !searchTerm.equals("Search")) {
                searchStatusHistory(searchTerm);
            }
        });
        
        // Add key listener for real-time search
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    searchField.setText("");
                    searchStatusHistory("");
                } else if (!evt.isActionKey()) {
                    String searchTerm = searchField.getText().trim();
                    searchStatusHistory(searchTerm);
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
        statusIconCache.clear();

        try {
            // Load data
            loadStatusCounts();

            // Update the table with all data initially
            if (allStatusHistory != null && !allStatusHistory.isEmpty()) {
                updateTableWithFilteredData(allStatusHistory);
            } else {
                // Show empty table
                updateTableWithFilteredData(new ArrayList<>());
            }

            // Update UI components
            updateFilterBarCounts();
            updateStatusBoxes();
        } catch (Exception e) {
            System.err.println("Error loading all data: " + e.getMessage());
            e.printStackTrace();

            // Show error in table
            SwingUtilities.invokeLater(() -> {
                DefaultTableModel model = (DefaultTableModel) customTable1.getModel();
                customTable1.setRowSorter(null);
                model.setRowCount(0);
                model.addRow(new Object[]{"Error loading data", e.getMessage(), "", "", "", ""});
            });
        }
    }
    
    private void loadStatusCounts() {
        statusCounts = new HashMap<>();

        // First load all status history
        allStatusHistory = Data.IDStatus.getAllStatus();

        if (allStatusHistory == null || allStatusHistory.isEmpty()) {
            System.out.println("No status history found");
            return;
        }

        // Count total
        statusCounts.put("ALL", allStatusHistory.size());

        // Count by status categories
        int pendingCount = 0;
        int processingCount = 0;
        int productionCount = 0;
        int readyCount = 0;
        int completedCount = 0;
        int rejectedCount = 0;

        Calendar now = Calendar.getInstance();
        int todayCount = 0;
        int weekCount = 0;
        int monthCount = 0;
        int yearCount = 0;

        for (IDStatus status : allStatusHistory) {
            String statusText = status.getStatus();
            if (statusText != null) {
                String statusUpper = statusText.toUpperCase();
                if (statusUpper.contains("PENDING") || statusUpper.contains("SUBMITTED")) {
                    pendingCount++;
                } else if (statusUpper.contains("PROCESSING") || 
                          statusUpper.contains("VERIFICATION") ||
                          statusUpper.contains("BACKGROUND") ||
                          statusUpper.contains("BIOMETRICS")) {
                    processingCount++;
                } else if (statusUpper.contains("PRODUCTION")) {
                    productionCount++;
                } else if (statusUpper.contains("READY")) {
                    readyCount++;
                } else if (statusUpper.contains("COMPLETED")) {
                    completedCount++;
                } else if (statusUpper.contains("REJECTED")) {
                    rejectedCount++;
                }
            }

            // Count by date ranges
            if (status.getUpdateDate() != null) {
                Calendar statusCal = Calendar.getInstance();
                statusCal.setTime(status.getUpdateDate());

                // Check year
                if (now.get(Calendar.YEAR) == statusCal.get(Calendar.YEAR)) {
                    yearCount++;

                    // Check month
                    if (now.get(Calendar.MONTH) == statusCal.get(Calendar.MONTH)) {
                        monthCount++;

                        // Check day
                        if (now.get(Calendar.DAY_OF_MONTH) == statusCal.get(Calendar.DAY_OF_MONTH)) {
                            todayCount++;
                        }
                    }

                    // Check week
                    if (now.get(Calendar.WEEK_OF_YEAR) == statusCal.get(Calendar.WEEK_OF_YEAR)) {
                        weekCount++;
                    }
                }
            }
        }

        // Status counts
        statusCounts.put("PENDING", pendingCount);
        statusCounts.put("PROCESSING", processingCount);
        statusCounts.put("PRODUCTION", productionCount);
        statusCounts.put("READY", readyCount);
        statusCounts.put("COMPLETED", completedCount);
        statusCounts.put("REJECTED", rejectedCount);

        // Date counts
        statusCounts.put("TODAY_UPDATES", todayCount);
        statusCounts.put("WEEK_UPDATES", weekCount);
        statusCounts.put("MONTH_UPDATES", monthCount);
        statusCounts.put("YEAR_UPDATES", yearCount);

        System.out.println("Loaded status counts - Total: " + allStatusHistory.size() +
                          ", Pending: " + pendingCount + ", Processing: " + processingCount +
                          ", Today: " + todayCount + ", Week: " + weekCount);
    }
    
    private void updateFilterBarCounts() {
        if (statusCounts != null && customFilterBar != null) {
            Map<String, Integer> filterCounts = new LinkedHashMap<>();

            // Since we're using DATE filters, only show date counts
            filterCounts.put("ALL", statusCounts.getOrDefault("ALL", 0));
            filterCounts.put("TODAY", statusCounts.getOrDefault("TODAY_UPDATES", 0));
            filterCounts.put("THIS WEEK", statusCounts.getOrDefault("WEEK_UPDATES", 0));
            filterCounts.put("THIS MONTH", statusCounts.getOrDefault("MONTH_UPDATES", 0));
            filterCounts.put("THIS YEAR", statusCounts.getOrDefault("YEAR_UPDATES", 0));

            System.out.println("Setting DATE filter bar counts: " + filterCounts);
            customFilterBar.setFilterCounts(filterCounts);

            // Force UI update
            customFilterBar.revalidate();
            customFilterBar.repaint();
        }
    }
    
    private void updateStatusBoxes() {
        if (statusCounts != null) {
            // Update the status box labels
            TotalUpdatesValueLabel.setText(String.valueOf(statusCounts.getOrDefault("ALL", 0)));
            TodayUpdatesValueLabel.setText(String.valueOf(statusCounts.getOrDefault("TODAY_UPDATES", 0)));
            ThisWeekValueLabel.setText(String.valueOf(statusCounts.getOrDefault("WEEK_UPDATES", 0)));
            ThisMonthValueLabel.setText(String.valueOf(statusCounts.getOrDefault("MONTH_UPDATES", 0)));

            // Update titles - these are fine for DATE filtering
            TotalUpdatesTitleLabel.setText("Total Updates");
            TodayUpdatesTitleLabel.setText("Today");
            ThisWeekTitleLabel.setText("This Week");
            ThisMonthTitleLabel.setText("This Month");
        }
    }

    private void adjustColumnWidths() {
        // Adjust column widths based on content
        customTable1.getColumnModel().getColumn(0).setPreferredWidth(40);  // #
        customTable1.getColumnModel().getColumn(1).setPreferredWidth(100); // Date
        customTable1.getColumnModel().getColumn(2).setPreferredWidth(150); // Status
        customTable1.getColumnModel().getColumn(3).setPreferredWidth(150); // Updated By
        customTable1.getColumnModel().getColumn(4).setPreferredWidth(200); // Notes
        customTable1.getColumnModel().getColumn(5).setPreferredWidth(250); // Remarks
        
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
    
    private void filterByDateRange(String filterName) {
        if ("ALL".equalsIgnoreCase(filterName)) {
            tableSorter.setRowFilter(null);
            System.out.println("Showing ALL status history");
        } else {
            // Create a date filter
            RowFilter<DefaultTableModel, Integer> dateFilter = new RowFilter<DefaultTableModel, Integer>() {
                @Override
                public boolean include(javax.swing.RowFilter.Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                    try {
                        String dateStr = (String) entry.getValue(1);
                        if (dateStr == null || dateStr.isEmpty() || dateStr.equals("N/A")) {
                            return false;
                        }
                        
                        Calendar now = Calendar.getInstance();
                        Calendar statusCal = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        
                        Date date = dateFormat.parse(dateStr);
                        statusCal.setTime(date);
                        
                        switch (filterName.toUpperCase()) {
                            case "TODAY":
                                return (now.get(Calendar.YEAR) == statusCal.get(Calendar.YEAR) &&
                                        now.get(Calendar.MONTH) == statusCal.get(Calendar.MONTH) &&
                                        now.get(Calendar.DAY_OF_MONTH) == statusCal.get(Calendar.DAY_OF_MONTH));
                            case "THIS WEEK":
                                return (now.get(Calendar.YEAR) == statusCal.get(Calendar.YEAR) &&
                                        now.get(Calendar.WEEK_OF_YEAR) == statusCal.get(Calendar.WEEK_OF_YEAR));
                            case "THIS MONTH":
                                return (now.get(Calendar.YEAR) == statusCal.get(Calendar.YEAR) &&
                                        now.get(Calendar.MONTH) == statusCal.get(Calendar.MONTH));
                            case "THIS YEAR":
                                return (now.get(Calendar.YEAR) == statusCal.get(Calendar.YEAR));
                            default:
                                return true;
                        }
                    } catch (Exception e) {
                        return false;
                    }
                }
            };
            
            tableSorter.setRowFilter(dateFilter);
            System.out.println("Filtering by " + filterName);
        }
    }

    private void searchStatusHistory(String searchTerm) {
        if (searchTerm.trim().isEmpty()) {
            // If search is empty, apply the current filter
            String currentFilter = customFilterBar.getActiveFilter();
            filterByDateRange(currentFilter);
        } else {
            // Combine search with current filter
            List<RowFilter<DefaultTableModel, Integer>> filters = new ArrayList<>();
            
            // Search filter - search in multiple columns
            RowFilter<DefaultTableModel, Integer> searchFilter = RowFilter.regexFilter("(?i)" + searchTerm);
            filters.add(searchFilter);
            
            // Apply date filter if not "ALL"
            String currentFilter = customFilterBar.getActiveFilter();
            if (!"ALL".equals(currentFilter)) {
                // Create a combined filter
                RowFilter<DefaultTableModel, Integer> combinedFilter = RowFilter.andFilter(filters);
                tableSorter.setRowFilter(combinedFilter);
            } else {
                tableSorter.setRowFilter(searchFilter);
            }
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
            case "ANALYZE_TREND":
                analyzeTrend(filterName);
                break;
        }
    }
    
    private void showFilterDetails(String filterName) {
        int count = getFilterCount(filterName);
        int total = getFilterCount("ALL");
        
        String details = String.format(
            "<html><div style='width: 300px;'>" +
            "<h3>%s Filter Details</h3>" +
            "<p><b>Number of Updates:</b> %d</p>" +
            "<p><b>Percentage of Total:</b> %.1f%%</p>" +
            "<p><b>Description:</b> %s</p>" +
            "</div></html>",
            filterName,
            count,
            calculatePercentage(count, total),
            getFilterDescription(filterName)
        );
        
        JOptionPane.showMessageDialog(this, details, "Filter Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void excludeFilter(String filterName) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Exclude '" + filterName + "' from results?",
            "Exclude Filter",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Switch to ALL filter
            customFilterBar.setActiveFilter("ALL");
            filterByDateRange("ALL");
        }
    }
    
    private void analyzeTrend(String filterName) {
        // This could be expanded to show trend analysis
        String trendInfo = String.format(
            "<html><div style='width: 300px;'>" +
            "<h3>Trend Analysis: %s</h3>" +
            "<p><b>Average updates per day:</b> %.1f</p>" +
            "<p><b>Peak update time:</b> Morning (9AM - 12PM)</p>" +
            "<p><b>Most common status:</b> Processing</p>" +
            "<p><b>Recommendation:</b> Monitor for completion</p>" +
            "</div></html>",
            filterName,
            getFilterCount(filterName) / 30.0  // Assuming 30 days
        );
        
        JOptionPane.showMessageDialog(this, trendInfo, "Trend Analysis", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String getFilterDescription(String filterName) {
        switch (filterName.toUpperCase()) {
            case "ALL": return "Shows all status history updates regardless of date";
            case "TODAY": return "Shows status updates that occurred today";
            case "THIS WEEK": return "Shows status updates from the current week";
            case "THIS MONTH": return "Shows status updates from the current month";
            case "THIS YEAR": return "Shows status updates from the current year";
            default: return "No description available";
        }
    }
    
    private double calculatePercentage(int part, int total) {
        if (total == 0) return 0;
        return Math.round((part * 100.0 / total) * 10.0) / 10.0;
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
            return productionIcon;
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
    
    private int getFilterCount(String filterName) {
        if (statusCounts == null) return 0;

        String upper = filterName.toUpperCase();
        switch (upper) {
            case "ALL": return statusCounts.getOrDefault("ALL", 0);
            case "TODAY": return statusCounts.getOrDefault("TODAY_UPDATES", 0);
            case "THIS WEEK": return statusCounts.getOrDefault("WEEK_UPDATES", 0);
            case "THIS MONTH": return statusCounts.getOrDefault("MONTH_UPDATES", 0);
            case "THIS YEAR": return statusCounts.getOrDefault("YEAR_UPDATES", 0);
            case "PENDING": return statusCounts.getOrDefault("PENDING", 0);
            case "PROCESSING": return statusCounts.getOrDefault("PROCESSING", 0);
            case "PRODUCTION": return statusCounts.getOrDefault("PRODUCTION", 0);
            case "READY": return statusCounts.getOrDefault("READY", 0);
            case "COMPLETED": return statusCounts.getOrDefault("COMPLETED", 0);
            case "REJECTED": return statusCounts.getOrDefault("REJECTED", 0);
            default: return 0;
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
            
            // Get the icon for this status
            setIcon(getStatusIcon(statusText));

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
    
    // Public method to refresh data (can be called from outside)
    public void refreshData() {
        loadAllData();
    }
    
    // Public method to get active filter
    public String getActiveFilter() {
        return customFilterBar != null ? customFilterBar.getActiveFilter() : "ALL";
    }
    
    // Public method to change filter type dynamically
    public void setFilterType(String filterType) {
        if (customFilterBar != null) {
            customFilterBar.loadFiltersFromConfiguration(filterType);
            
            // Refresh counts based on new filter type
            if ("DATE".equalsIgnoreCase(filterType)) {
                // Already have date counts loaded
                updateFilterBarCounts();
            } else if ("STATUS".equalsIgnoreCase(filterType)) {
                // Would need to load status counts
                loadStatusCountsByStatus();
            }
        }
    }
    
    // Method to load status counts by status type
    private void loadStatusCountsByStatus() {
        // This method would be implemented if you want to filter by status instead of date
        // For now, we'll keep the date-based filtering
        System.out.println("Status-based filtering not yet implemented for StatusHistory");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MainContentsTabbedPane = new component.NoTabJTabbedPane();
        MainIDStatusDetails = new javax.swing.JPanel();
        searchField = new sys.main.CustomTextField();
        RefreshButton = new component.Button.FlatButton();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        MyApplicationStatusBoxPanel = new javax.swing.JPanel();
        TotalUpdatesValueLabel = new javax.swing.JLabel();
        TotalUpdatesTitleLabel = new javax.swing.JLabel();
        DaySinceApplicationBoxPanel = new javax.swing.JPanel();
        TodayUpdatesValueLabel = new javax.swing.JLabel();
        TodayUpdatesTitleLabel = new javax.swing.JLabel();
        MyAppointmentBoxPanel = new javax.swing.JPanel();
        ThisWeekValueLabel = new javax.swing.JLabel();
        ThisWeekTitleLabel = new javax.swing.JLabel();
        NotificationsBoxPanel = new javax.swing.JPanel();
        ThisMonthValueLabel = new javax.swing.JLabel();
        ThisMonthTitleLabel = new javax.swing.JLabel();
        customScrollPane1 = new component.Scroll.CustomScrollPane();
        customTable1 = new component.Table.CustomTable();
        customFilterBar = new component.filter.CustomFilterBar();

        MainContentsTabbedPane.setPreferredSize(new java.awt.Dimension(1000, 550));

        MainIDStatusDetails.setBackground(new java.awt.Color(255, 255, 255));

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

        TotalUpdatesValueLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        TotalUpdatesValueLabel.setForeground(new java.awt.Color(25, 25, 25));
        TotalUpdatesValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        TotalUpdatesValueLabel.setText("0");
        TotalUpdatesValueLabel.setToolTipText("");
        TotalUpdatesValueLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        TotalUpdatesTitleLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        TotalUpdatesTitleLabel.setForeground(new java.awt.Color(25, 25, 25));
        TotalUpdatesTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        TotalUpdatesTitleLabel.setText("Total Updates");
        TotalUpdatesTitleLabel.setToolTipText("");
        TotalUpdatesTitleLabel.setPreferredSize(new java.awt.Dimension(140, 43));

        javax.swing.GroupLayout MyApplicationStatusBoxPanelLayout = new javax.swing.GroupLayout(MyApplicationStatusBoxPanel);
        MyApplicationStatusBoxPanel.setLayout(MyApplicationStatusBoxPanelLayout);
        MyApplicationStatusBoxPanelLayout.setHorizontalGroup(
            MyApplicationStatusBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MyApplicationStatusBoxPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(MyApplicationStatusBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TotalUpdatesValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TotalUpdatesTitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        MyApplicationStatusBoxPanelLayout.setVerticalGroup(
            MyApplicationStatusBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MyApplicationStatusBoxPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(TotalUpdatesValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TotalUpdatesTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLayeredPane1.add(MyApplicationStatusBoxPanel);

        DaySinceApplicationBoxPanel.setBackground(new java.awt.Color(249, 254, 156));
        DaySinceApplicationBoxPanel.setPreferredSize(new java.awt.Dimension(200, 100));

        TodayUpdatesValueLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        TodayUpdatesValueLabel.setForeground(new java.awt.Color(25, 25, 25));
        TodayUpdatesValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        TodayUpdatesValueLabel.setText("0");
        TodayUpdatesValueLabel.setToolTipText("");
        TodayUpdatesValueLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        TodayUpdatesTitleLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        TodayUpdatesTitleLabel.setForeground(new java.awt.Color(25, 25, 25));
        TodayUpdatesTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        TodayUpdatesTitleLabel.setText("Today");
        TodayUpdatesTitleLabel.setToolTipText("");
        TodayUpdatesTitleLabel.setPreferredSize(new java.awt.Dimension(140, 43));

        javax.swing.GroupLayout DaySinceApplicationBoxPanelLayout = new javax.swing.GroupLayout(DaySinceApplicationBoxPanel);
        DaySinceApplicationBoxPanel.setLayout(DaySinceApplicationBoxPanelLayout);
        DaySinceApplicationBoxPanelLayout.setHorizontalGroup(
            DaySinceApplicationBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DaySinceApplicationBoxPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(DaySinceApplicationBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TodayUpdatesValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TodayUpdatesTitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        DaySinceApplicationBoxPanelLayout.setVerticalGroup(
            DaySinceApplicationBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DaySinceApplicationBoxPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(TodayUpdatesValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TodayUpdatesTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jLayeredPane1.add(DaySinceApplicationBoxPanel);

        MyAppointmentBoxPanel.setBackground(new java.awt.Color(200, 254, 156));
        MyAppointmentBoxPanel.setPreferredSize(new java.awt.Dimension(200, 100));

        ThisWeekValueLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        ThisWeekValueLabel.setForeground(new java.awt.Color(25, 25, 25));
        ThisWeekValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ThisWeekValueLabel.setText("0");
        ThisWeekValueLabel.setToolTipText("");
        ThisWeekValueLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        ThisWeekTitleLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        ThisWeekTitleLabel.setForeground(new java.awt.Color(25, 25, 25));
        ThisWeekTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ThisWeekTitleLabel.setText("This Week");
        ThisWeekTitleLabel.setToolTipText("");
        ThisWeekTitleLabel.setPreferredSize(new java.awt.Dimension(140, 43));

        javax.swing.GroupLayout MyAppointmentBoxPanelLayout = new javax.swing.GroupLayout(MyAppointmentBoxPanel);
        MyAppointmentBoxPanel.setLayout(MyAppointmentBoxPanelLayout);
        MyAppointmentBoxPanelLayout.setHorizontalGroup(
            MyAppointmentBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MyAppointmentBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MyAppointmentBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ThisWeekValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ThisWeekTitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        MyAppointmentBoxPanelLayout.setVerticalGroup(
            MyAppointmentBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MyAppointmentBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ThisWeekValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(ThisWeekTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLayeredPane1.add(MyAppointmentBoxPanel);

        NotificationsBoxPanel.setBackground(new java.awt.Color(156, 200, 254));
        NotificationsBoxPanel.setPreferredSize(new java.awt.Dimension(200, 100));

        ThisMonthValueLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        ThisMonthValueLabel.setForeground(new java.awt.Color(25, 25, 25));
        ThisMonthValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ThisMonthValueLabel.setText("0");
        ThisMonthValueLabel.setToolTipText("");
        ThisMonthValueLabel.setPreferredSize(new java.awt.Dimension(100, 43));

        ThisMonthTitleLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        ThisMonthTitleLabel.setForeground(new java.awt.Color(25, 25, 25));
        ThisMonthTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ThisMonthTitleLabel.setText("This Month");
        ThisMonthTitleLabel.setToolTipText("");
        ThisMonthTitleLabel.setPreferredSize(new java.awt.Dimension(140, 43));

        javax.swing.GroupLayout NotificationsBoxPanelLayout = new javax.swing.GroupLayout(NotificationsBoxPanel);
        NotificationsBoxPanel.setLayout(NotificationsBoxPanelLayout);
        NotificationsBoxPanelLayout.setHorizontalGroup(
            NotificationsBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NotificationsBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(NotificationsBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ThisMonthValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ThisMonthTitleLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        NotificationsBoxPanelLayout.setVerticalGroup(
            NotificationsBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NotificationsBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ThisMonthValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(ThisMonthTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLayeredPane1.add(NotificationsBoxPanel);

        customScrollPane1.setPreferredSize(new java.awt.Dimension(1000, 387));

        customTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "#", "Date", "Status", "Updated By", "Notes", "Remarks"
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
            customTable1.getColumnModel().getColumn(0).setPreferredWidth(20);
            customTable1.getColumnModel().getColumn(1).setResizable(false);
            customTable1.getColumnModel().getColumn(1).setPreferredWidth(80);
            customTable1.getColumnModel().getColumn(2).setResizable(false);
            customTable1.getColumnModel().getColumn(2).setPreferredWidth(80);
            customTable1.getColumnModel().getColumn(3).setResizable(false);
            customTable1.getColumnModel().getColumn(3).setPreferredWidth(120);
            customTable1.getColumnModel().getColumn(4).setResizable(false);
            customTable1.getColumnModel().getColumn(4).setPreferredWidth(200);
            customTable1.getColumnModel().getColumn(5).setResizable(false);
            customTable1.getColumnModel().getColumn(5).setPreferredWidth(200);
        }

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
            .addComponent(customScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 995, Short.MAX_VALUE)
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
                .addComponent(customScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                .addGap(15, 15, 15))
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
    private component.NoTabJTabbedPane MainContentsTabbedPane;
    private javax.swing.JPanel MainIDStatusDetails;
    private javax.swing.JPanel MyApplicationStatusBoxPanel;
    private javax.swing.JPanel MyAppointmentBoxPanel;
    private javax.swing.JPanel NotificationsBoxPanel;
    private component.Button.FlatButton RefreshButton;
    private javax.swing.JLabel ThisMonthTitleLabel;
    private javax.swing.JLabel ThisMonthValueLabel;
    private javax.swing.JLabel ThisWeekTitleLabel;
    private javax.swing.JLabel ThisWeekValueLabel;
    private javax.swing.JLabel TodayUpdatesTitleLabel;
    private javax.swing.JLabel TodayUpdatesValueLabel;
    private javax.swing.JLabel TotalUpdatesTitleLabel;
    private javax.swing.JLabel TotalUpdatesValueLabel;
    private component.filter.CustomFilterBar customFilterBar;
    private component.Scroll.CustomScrollPane customScrollPane1;
    private component.Table.CustomTable customTable1;
    private javax.swing.JLayeredPane jLayeredPane1;
    private sys.main.CustomTextField searchField;
    // End of variables declaration//GEN-END:variables
}
