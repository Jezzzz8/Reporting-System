package component.filter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class CustomFilterBar extends JPanel {
    
    // Configuration Properties - Can be easily modified
    public static class FilterProperties {
        // Button dimensions
        public int defaultButtonWidth = 110;
        public int buttonHeight = 36;
        
        // Button padding
        public int horizontalPadding = 12;
        public int verticalPadding = 8;
        
        // Spacing between buttons
        public int buttonSpacing = 8;
        
        // Font sizes
        public Font normalFont = new Font("Segoe UI", Font.PLAIN, 13);
        public Font selectedFont = new Font("Segoe UI", Font.BOLD, 13);
        
        // Border radius
        public int borderRadius = 8;
        
        // Colors
        public Color normalColor = new Color(70, 70, 70);
        public Color primaryColor = new Color(0, 120, 215);
        public Color hoverColor = new Color(0, 100, 180);
        public Color backgroundColor = Color.WHITE;
        public Color borderColor = new Color(150, 150, 150);
        public Color errorColor = new Color(220, 53, 69);
        public Color selectedBackground = new Color(0, 120, 215, 20);
        
        // Individual filter colors (can be customized)
        public Color pendingColor = new Color(255, 193, 7); // Amber
        public Color processingColor = new Color(0, 120, 215); // Blue
        public Color productionColor = new Color(204, 85, 0); // Orange
        public Color readyColor = new Color(40, 167, 69); // Green
        public Color completedColor = new Color(111, 66, 193); // Purple
        public Color rejectedColor = new Color(220, 53, 69); // Red
        public Color allColor = new Color(70, 70, 70); // Dark gray
        
        // Date filter colors (for date range filters)
        public Color todayColor = new Color(33, 150, 243); // Blue
        public Color weekColor = new Color(76, 175, 80); // Green
        public Color monthColor = new Color(255, 152, 0); // Orange
        public Color yearColor = new Color(156, 39, 176); // Purple
        
        // Show counts option
        public boolean showCounts = true;
        
        // Show tooltips option
        public boolean showTooltips = true;
        
        // Enable hover effects
        public boolean enableHover = true;
        
        // Enable context menu
        public boolean enableContextMenu = true;
        
        // Auto-resize buttons based on text length
        public boolean autoResizeButtons = true; // Changed to true by default
        
        // Minimum button width when auto-resizing
        public int minButtonWidth = 80;
        
        // Maximum button width when auto-resizing
        public int maxButtonWidth = 160;
        
        // Additional padding when showing counts
        public int countExtraPadding = 15;
        
        // Calculate button width dynamically for specific filters
        public Map<String, Integer> customWidths = new HashMap<>();
        
        // Text width multiplier for safety margin
        public float textWidthMultiplier = 1.2f;
    }
    
    // Filter definition class for customizable filter contents
    public static class FilterDefinition {
        private String id;
        private String displayText;
        private Color color;
        private int order;
        private boolean enabled;
        private String category; // Optional: "status", "date", "priority", etc.
        
        public FilterDefinition(String id, String displayText, Color color) {
            this(id, displayText, color, 0, true, "custom");
        }
        
        public FilterDefinition(String id, String displayText, Color color, int order, 
                                boolean enabled, String category) {
            this.id = id.toUpperCase();
            this.displayText = displayText;
            this.color = color;
            this.order = order;
            this.enabled = enabled;
            this.category = category;
        }
        
        // Getters and setters
        public String getId() { return id; }
        public String getDisplayText() { return displayText; }
        public Color getColor() { return color; }
        public int getOrder() { return order; }
        public boolean isEnabled() { return enabled; }
        public String getCategory() { return category; }
        
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public void setOrder(int order) { this.order = order; }
    }
    
    // Instance variables
    private List<FilterButton> filterButtons;
    private FilterModel filterModel;
    private FilterListener listener;
    private String activeFilter;
    private FilterProperties properties;
    private List<FilterDefinition> filterDefinitions;
    
    // Default constructor with default properties
    public CustomFilterBar() {
        this(new FilterProperties());
    }
    
    // Constructor with custom properties
    public CustomFilterBar(FilterProperties properties) {
        this.properties = properties;
        this.filterDefinitions = new ArrayList<>();
        initComponents();
        setupDefaults();
    }
    
    private void initComponents() {
        // Use FlowLayout with LEFT alignment
        setLayout(new FlowLayout(FlowLayout.LEFT, properties.buttonSpacing, 0));
        setBackground(properties.backgroundColor);
        setBorder(new EmptyBorder(8, 12, 8, 12));
        
        filterButtons = new ArrayList<>();
        filterModel = new FilterModel();
        
        setupDefaultFilters();
    }
    
    private void setupDefaults() {
        activeFilter = "ALL";
        
        // Set custom widths for longer filter names
        properties.customWidths.put("ALL", 75);
        properties.customWidths.put("PENDING", 95);
        properties.customWidths.put("PROCESSING", 110);
        properties.customWidths.put("PRODUCTION", 105);
        properties.customWidths.put("READY", 85);
        properties.customWidths.put("COMPLETED", 105);
        properties.customWidths.put("REJECTED", 100);
        properties.customWidths.put("TODAY", 85);
        properties.customWidths.put("THIS WEEK", 105);
        properties.customWidths.put("THIS MONTH", 115);
        properties.customWidths.put("THIS YEAR", 105);
    }

    private void setupDefaultFilters() {
        // Default status filters - will be overridden if custom filters are set
        String[] filters = {"ALL", "PENDING", "PROCESSING", "PRODUCTION", "READY", "COMPLETED", "REJECTED"};

        for (String filter : filters) {
            addFilterOption(filter, getDisplayText(filter), getFilterColor(filter));
        }
    }

    private String getDisplayText(String filter) {
        switch (filter.toUpperCase()) {
            case "PENDING": return "Pending";
            case "PROCESSING": return "Processing";
            case "PRODUCTION": return "Production";
            case "READY": return "Ready";
            case "COMPLETED": return "Completed";
            case "REJECTED": return "Rejected";
            case "TODAY": return "Today";
            case "THIS WEEK": return "This Week";
            case "THIS MONTH": return "This Month";
            case "THIS YEAR": return "This Year";
            default: return "All";
        }
    }
    
    private Color getFilterColor(String filter) {
        String upperFilter = filter.toUpperCase();
        switch (upperFilter) {
            case "PENDING": return properties.pendingColor;
            case "PROCESSING": return properties.processingColor;
            case "PRODUCTION": return properties.productionColor;
            case "READY": return properties.readyColor;
            case "COMPLETED": return properties.completedColor;
            case "REJECTED": return properties.rejectedColor;
            case "TODAY": return properties.todayColor;
            case "THIS WEEK": return properties.weekColor;
            case "THIS MONTH": return properties.monthColor;
            case "THIS YEAR": return properties.yearColor;
            default: return properties.allColor;
        }
    }
    
    // ============== NEW METHODS FOR CUSTOMIZABLE FILTER CONTENTS ==============
    
    /**
     * Set custom filter definitions for the filter bar.
     * This completely replaces the existing filters.
     */
    public void setFilterDefinitions(List<FilterDefinition> definitions) {
        this.filterDefinitions.clear();
        this.filterDefinitions.addAll(definitions);
        
        // Sort by order
        filterDefinitions.sort(Comparator.comparingInt(FilterDefinition::getOrder));
        
        // Clear existing filters
        clearFilters();
        
        // Add new filters based on definitions
        for (FilterDefinition def : filterDefinitions) {
            if (def.isEnabled()) {
                addFilterOption(def.getId(), def.getDisplayText(), def.getColor());
            }
        }
    }
    
    /**
     * Add a single filter definition.
     */
    public void addFilterDefinition(FilterDefinition definition) {
        filterDefinitions.add(definition);
        
        // Sort by order
        filterDefinitions.sort(Comparator.comparingInt(FilterDefinition::getOrder));
        
        if (definition.isEnabled()) {
            addFilterOption(definition.getId(), definition.getDisplayText(), definition.getColor());
        }
    }
    
    /**
     * Remove a filter definition by ID.
     */
    public void removeFilterDefinition(String id) {
        filterDefinitions.removeIf(def -> def.getId().equalsIgnoreCase(id));
        removeFilterOption(id);
    }
    
    /**
     * Update an existing filter definition.
     */
    public void updateFilterDefinition(FilterDefinition updatedDef) {
        for (int i = 0; i < filterDefinitions.size(); i++) {
            if (filterDefinitions.get(i).getId().equalsIgnoreCase(updatedDef.getId())) {
                filterDefinitions.set(i, updatedDef);
                
                // Update the button if it exists
                FilterButton button = getFilterButton(updatedDef.getId());
                if (button != null) {
                    button.displayText = updatedDef.getDisplayText();
                    button.color = updatedDef.getColor();
                    button.setEnabled(updatedDef.isEnabled());
                    button.updateButtonText();
                    button.updateAppearance();
                }
                break;
            }
        }
    }
    
    /**
     * Get all filter definitions.
     */
    public List<FilterDefinition> getFilterDefinitions() {
        return new ArrayList<>(filterDefinitions);
    }
    
    /**
     * Get filter definitions by category.
     */
    public List<FilterDefinition> getFilterDefinitionsByCategory(String category) {
        List<FilterDefinition> result = new ArrayList<>();
        for (FilterDefinition def : filterDefinitions) {
            if (def.getCategory().equalsIgnoreCase(category)) {
                result.add(def);
            }
        }
        return result;
    }
    
    /**
     * Create predefined status filter definitions.
     */
    public static List<FilterDefinition> createStatusFilterDefinitions() {
        List<FilterDefinition> definitions = new ArrayList<>();
        
        definitions.add(new FilterDefinition("ALL", "All", new Color(70, 70, 70), 0, true, "status"));
        definitions.add(new FilterDefinition("PENDING", "Pending", new Color(255, 193, 7), 1, true, "status"));
        definitions.add(new FilterDefinition("PROCESSING", "Processing", new Color(0, 120, 215), 2, true, "status"));
        definitions.add(new FilterDefinition("PRODUCTION", "Production", new Color(204, 85, 0), 3, true, "status"));
        definitions.add(new FilterDefinition("READY", "Ready", new Color(40, 167, 69), 4, true, "status"));
        definitions.add(new FilterDefinition("COMPLETED", "Completed", new Color(111, 66, 193), 5, true, "status"));
        definitions.add(new FilterDefinition("REJECTED", "Rejected", new Color(220, 53, 69), 6, true, "status"));
        
        return definitions;
    }
    
    /**
     * Create predefined date filter definitions.
     */
    public static List<FilterDefinition> createDateFilterDefinitions() {
        List<FilterDefinition> definitions = new ArrayList<>();
        
        definitions.add(new FilterDefinition("ALL", "All", new Color(70, 70, 70), 0, true, "date"));
        definitions.add(new FilterDefinition("TODAY", "Today", new Color(33, 150, 243), 1, true, "date"));
        definitions.add(new FilterDefinition("THIS WEEK", "This Week", new Color(76, 175, 80), 2, true, "date"));
        definitions.add(new FilterDefinition("THIS MONTH", "This Month", new Color(255, 152, 0), 3, true, "date"));
        definitions.add(new FilterDefinition("THIS YEAR", "This Year", new Color(156, 39, 176), 4, true, "date"));
        
        return definitions;
    }
    
    /**
     * Create priority filter definitions.
     */
    public static List<FilterDefinition> createPriorityFilterDefinitions() {
        List<FilterDefinition> definitions = new ArrayList<>();
        
        definitions.add(new FilterDefinition("ALL", "All", new Color(70, 70, 70), 0, true, "priority"));
        definitions.add(new FilterDefinition("HIGH", "High", new Color(220, 53, 69), 1, true, "priority"));
        definitions.add(new FilterDefinition("MEDIUM", "Medium", new Color(255, 193, 7), 2, true, "priority"));
        definitions.add(new FilterDefinition("LOW", "Low", new Color(40, 167, 69), 3, true, "priority"));
        
        return definitions;
    }
    
    /**
     * Create document status filter definitions.
     */
    public static List<FilterDefinition> createDocumentFilterDefinitions() {
        List<FilterDefinition> definitions = new ArrayList<>();
        
        definitions.add(new FilterDefinition("ALL", "All", new Color(70, 70, 70), 0, true, "document"));
        definitions.add(new FilterDefinition("UPLOADED", "Uploaded", new Color(40, 167, 69), 1, true, "document"));
        definitions.add(new FilterDefinition("PENDING", "Pending", new Color(255, 193, 7), 2, true, "document"));
        definitions.add(new FilterDefinition("REVIEWING", "Reviewing", new Color(0, 120, 215), 3, true, "document"));
        definitions.add(new FilterDefinition("APPROVED", "Approved", new Color(111, 66, 193), 4, true, "document"));
        definitions.add(new FilterDefinition("REJECTED", "Rejected", new Color(220, 53, 69), 5, true, "document"));
        
        return definitions;
    }
    
    /**
     * Load filters from configuration.
     */
    public void loadFiltersFromConfiguration(String configType) {
        clearFilters();
        
        switch (configType.toUpperCase()) {
            case "STATUS":
                setFilterDefinitions(createStatusFilterDefinitions());
                break;
            case "DATE":
                setFilterDefinitions(createDateFilterDefinitions());
                break;
            case "PRIORITY":
                setFilterDefinitions(createPriorityFilterDefinitions());
                break;
            case "DOCUMENT":
                setFilterDefinitions(createDocumentFilterDefinitions());
                break;
            case "CUSTOM":
                // Use already set definitions
                setFilterDefinitions(filterDefinitions);
                break;
            default:
                // Fallback to status filters
                setFilterDefinitions(createStatusFilterDefinitions());
                break;
        }
    }
    
    /**
     * Export current filter configuration as JSON string.
     */
    public String exportFilterConfiguration() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"filters\": [\n");
        
        for (int i = 0; i < filterDefinitions.size(); i++) {
            FilterDefinition def = filterDefinitions.get(i);
            Color color = def.getColor();
            sb.append("    {\n");
            sb.append("      \"id\": \"").append(def.getId()).append("\",\n");
            sb.append("      \"displayText\": \"").append(def.getDisplayText()).append("\",\n");
            sb.append("      \"color\": \"").append(String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue())).append("\",\n");
            sb.append("      \"order\": ").append(def.getOrder()).append(",\n");
            sb.append("      \"enabled\": ").append(def.isEnabled()).append(",\n");
            sb.append("      \"category\": \"").append(def.getCategory()).append("\"\n");
            sb.append("    }");
            if (i < filterDefinitions.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        
        sb.append("  ]\n");
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * Import filter configuration from JSON string.
     */
    public void importFilterConfiguration(String jsonConfig) {
        // This is a simplified version - in production you would use a JSON parser
        // For now, we'll clear and add default status filters
        clearFilters();
        setFilterDefinitions(createStatusFilterDefinitions());
        
        // TODO: Implement proper JSON parsing
        System.out.println("JSON configuration import would be implemented here.");
    }
    
    // Helper method to get filter button by ID
    private FilterButton getFilterButton(String id) {
        for (FilterButton button : filterButtons) {
            if (button.getName().equalsIgnoreCase(id)) {
                return button;
            }
        }
        return null;
    }
    
    // ============== END OF NEW METHODS ==============
    
    // Calculate optimal width for a button based on text
    private int calculateOptimalWidth(String text, Font font, boolean hasCount) {
        FontMetrics fm = getFontMetrics(font);
        int textWidth = fm.stringWidth(text);
        
        // Add extra padding if showing counts
        int extraPadding = hasCount ? properties.countExtraPadding : 0;
        
        // Calculate required width with padding
        int requiredWidth = textWidth + (properties.horizontalPadding * 2) + extraPadding;
        
        // Apply multiplier for safety margin
        requiredWidth = (int)(requiredWidth * properties.textWidthMultiplier);
        
        // Clamp to min/max width
        return Math.max(properties.minButtonWidth, Math.min(requiredWidth, properties.maxButtonWidth));
    }
    
    // Public methods for adding filters
    public void addFilterOption(String name, String displayText) {
        addFilterOption(name, displayText, getFilterColor(name));
    }
    
    public void addFilterOption(String name, String displayText, Color color) {
        FilterButton button = new FilterButton(name, displayText, color);
        filterButtons.add(button);
        add(button);
        
        filterModel.addFilter(name, displayText, color);
        
        revalidate();
        repaint();
    }
    
    // Method to remove a filter
    public void removeFilterOption(String name) {
        FilterButton toRemove = null;
        for (FilterButton button : filterButtons) {
            if (button.getName().equalsIgnoreCase(name)) {
                toRemove = button;
                break;
            }
        }
        
        if (toRemove != null) {
            filterButtons.remove(toRemove);
            remove(toRemove);
            filterModel.removeFilter(name);
            revalidate();
            repaint();
        }
    }
    
    public void setFilterCounts(Map<String, Integer> counts) {
        System.out.println("CustomFilterBar: Setting filter counts: " + counts);
        
        // Store counts in the model first
        filterModel.setCounts(counts);
        
        // Update each button
        for (FilterButton button : filterButtons) {
            Integer count = counts.get(button.getName());
            if (count != null) {
                button.setCount(count);
                if (properties.showTooltips) {
                    button.setToolTipText(button.getDisplayText() + ": " + count + " items");
                }
            } else {
                button.setCount(0);
                if (properties.showTooltips) {
                    button.setToolTipText(button.getDisplayText() + ": 0 items");
                }
            }
            
            // Update button text and width
            button.updateButtonText();
        }
        
        // Force UI update
        revalidate();
        repaint();
        
        System.out.println("CustomFilterBar: Filter counts updated and UI refreshed");
    }
    
    public void setActiveFilter(String filterName) {
        String previousFilter = activeFilter;
        activeFilter = filterName.toUpperCase();
        
        for (FilterButton button : filterButtons) {
            boolean isActive = button.getName().equalsIgnoreCase(filterName);
            button.setActive(isActive);
        }
        
        if (listener != null && !activeFilter.equals(previousFilter)) {
            listener.onFilterSelected(activeFilter);
        }
        
        repaint();
    }
    
    public void resetFilters() {
        setActiveFilter("ALL");
    }
    
    public String getActiveFilter() {
        return activeFilter;
    }
    
    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
    }
    
    // Getter for properties
    public FilterProperties getProperties() {
        return properties;
    }
    
    // Method to update properties and refresh UI
    public void updateProperties(FilterProperties newProperties) {
        this.properties = newProperties;
        refreshUI();
    }
    
    private void refreshUI() {
        // Update layout spacing
        ((FlowLayout) getLayout()).setHgap(properties.buttonSpacing);
        
        // Update all buttons
        for (FilterButton button : filterButtons) {
            button.updateProperties();
        }
        
        revalidate();
        repaint();
    }
    
    // Filter Button class
    class FilterButton extends JButton {
        private String name;
        private String displayText;
        private Color color;
        private boolean active = false;
        private boolean hover = false;
        private int count = 0;
        
        public FilterButton(String name, String displayText, Color color) {
            this.name = name;
            this.displayText = displayText;
            this.color = color;
            
            initButton();
            setupListeners();
        }
        
        private void initButton() {
            updateButtonText();
            setFont(properties.normalFont);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(properties.verticalPadding, properties.horizontalPadding, 
                                properties.verticalPadding, properties.horizontalPadding));
            setHorizontalAlignment(SwingConstants.CENTER);
            updateAppearance();
        }
        
        public void updateProperties() {
            setFont(properties.normalFont);
            setMargin(new Insets(properties.verticalPadding, properties.horizontalPadding, 
                                properties.verticalPadding, properties.horizontalPadding));
            updateButtonText();
            updateAppearance();
            repaint();
        }
        
        private void setupListeners() {
            if (properties.enableHover) {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        updateAppearance();
                        if (listener != null) {
                            listener.onFilterHover(name);
                        }
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        updateAppearance();
                    }
                    
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            setActiveFilter(name);
                        } else if (SwingUtilities.isRightMouseButton(e) && properties.enableContextMenu) {
                            showContextMenu(e);
                        }
                    }
                });
            }
        }
        
        private String getFullButtonText() {
            if (properties.showCounts) {
                return displayText + " (" + count + ")";
            } else {
                return displayText;
            }
        }
        
        public void updateButtonText() {
            String fullText = getFullButtonText();
            setText(fullText);
            
            // Calculate optimal button width
            int optimalWidth;
            
            if (properties.autoResizeButtons) {
                // Calculate based on text length
                Font font = active ? properties.selectedFont : properties.normalFont;
                optimalWidth = calculateOptimalWidth(fullText, font, properties.showCounts);
            } else if (properties.customWidths.containsKey(name.toUpperCase())) {
                // Use custom width if specified
                optimalWidth = properties.customWidths.get(name.toUpperCase());
            } else {
                // Use default width
                optimalWidth = properties.defaultButtonWidth;
            }
            
            // Update button size
            setPreferredSize(new Dimension(optimalWidth, properties.buttonHeight));
            
            repaint();
        }
        
        public void setActive(boolean active) {
            this.active = active;
            updateAppearance();
            // Recalculate width when active state changes (font changes)
            updateButtonText();
        }
        
        public void setCount(int count) {
            this.count = count;
            // Recalculate width when count changes
            updateButtonText();
        }
        
        public String getName() {
            return name;
        }
        
        public String getDisplayText() {
            return displayText;
        }
        
        private void updateAppearance() {
            if (active) {
                setForeground(color);
                setFont(properties.selectedFont);
            } else if (hover && properties.enableHover) {
                setForeground(properties.hoverColor);
                setFont(properties.normalFont);
            } else {
                setForeground(properties.normalColor);
                setFont(properties.normalFont);
            }
            repaint();
        }
        
        private void showContextMenu(MouseEvent e) {
            if (!properties.enableContextMenu) return;
            
            JPopupMenu menu = new JPopupMenu();
            
            JMenuItem viewItem = new JMenuItem("View Details");
            viewItem.addActionListener(ev -> {
                if (listener != null) listener.onFilterContextMenu(name, "VIEW_DETAILS");
            });
            
            JMenuItem excludeItem = new JMenuItem("Exclude");
            excludeItem.addActionListener(ev -> {
                if (listener != null) listener.onFilterContextMenu(name, "EXCLUDE");
            });
            
            menu.add(viewItem);
            menu.add(excludeItem);
            menu.show(this, e.getX(), e.getY());
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            
            // Only draw background for active state (transparent otherwise)
            if (active) {
                // Light background for active state
                g2.setColor(properties.selectedBackground);
                g2.fillRoundRect(0, 0, width, height, properties.borderRadius, properties.borderRadius);
                
                // Color border
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, width - 3, height - 3, properties.borderRadius - 2, properties.borderRadius - 2);
            } else if (hover && properties.enableHover) {
                // Very light hover background (semi-transparent)
                g2.setColor(new Color(248, 249, 250, 100));
                g2.fillRoundRect(0, 0, width, height, properties.borderRadius, properties.borderRadius);
                
                // Hover color border
                g2.setColor(properties.hoverColor);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, width - 1, height - 1, properties.borderRadius - 1, properties.borderRadius - 1);
            }
            
            g2.dispose();
            
            // Paint text
            super.paintComponent(g);
            
            // Add subtle checkmark for active state
            if (active) {
                g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2f));
                
                // Draw checkmark in bottom right corner
                int checkSize = 10;
                int x = width - checkSize - 4;
                int y = height - checkSize - 4;
                
                g2.drawLine(x, y + checkSize/2, x + checkSize/3, y + checkSize);
                g2.drawLine(x + checkSize/3, y + checkSize, x + checkSize, y);
                
                g2.dispose();
            }
        }
        
        @Override
        public Dimension getPreferredSize() {
            // Return calculated size
            String fullText = getFullButtonText();
            Font font = active ? properties.selectedFont : properties.normalFont;
            FontMetrics fm = getFontMetrics(font);
            int textWidth = fm.stringWidth(fullText);
            int extraPadding = properties.showCounts ? properties.countExtraPadding : 0;
            int requiredWidth = textWidth + (properties.horizontalPadding * 2) + extraPadding;
            requiredWidth = (int)(requiredWidth * properties.textWidthMultiplier);
            
            int optimalWidth = Math.max(properties.minButtonWidth, Math.min(requiredWidth, properties.maxButtonWidth));
            
            // Use custom width if specified
            if (properties.customWidths.containsKey(name.toUpperCase())) {
                optimalWidth = properties.customWidths.get(name.toUpperCase());
            }
            
            return new Dimension(optimalWidth, properties.buttonHeight);
        }
        
        @Override
        public Dimension getMinimumSize() {
            return new Dimension(properties.minButtonWidth, properties.buttonHeight);
        }
        
        @Override
        public Dimension getMaximumSize() {
            return new Dimension(properties.maxButtonWidth, properties.buttonHeight);
        }
    }
    
    // Filter model class
    class FilterModel {
        private Map<String, FilterData> filters = new LinkedHashMap<>();
        private Map<String, Integer> counts = new HashMap<>();
        
        class FilterData {
            String displayText;
            Color color;
            
            FilterData(String displayText, Color color) {
                this.displayText = displayText;
                this.color = color;
            }
        }
        
        public void addFilter(String name, String displayText, Color color) {
            filters.put(name.toUpperCase(), new FilterData(displayText, color));
        }
        
        public void removeFilter(String name) {
            filters.remove(name.toUpperCase());
            counts.remove(name.toUpperCase());
        }
        
        public void setCounts(Map<String, Integer> counts) {
            this.counts.clear();
            this.counts.putAll(counts);
        }
        
        public int getCount(String filter) {
            Integer count = counts.get(filter.toUpperCase());
            return count != null ? count : 0;
        }
    }
    
    // Listener interface
    public interface FilterListener {
        void onFilterSelected(String filterName);
        void onFilterHover(String filterName);
        void onFilterContextMenu(String filterName, String action);
    }
    
    // Factory method for flexible status filter bar
    public static CustomFilterBar createFlexibleStatusFilterBar() {
        FilterProperties props = new FilterProperties();
        
        // Enable auto-resize
        props.autoResizeButtons = true;
        
        // Set appropriate min/max widths
        props.minButtonWidth = 85;
        props.maxButtonWidth = 140;
        
        // Set custom widths for specific filters
        props.customWidths.put("PENDING", 100);
        props.customWidths.put("PROCESSING", 115);
        props.customWidths.put("PRODUCTION", 115);
        props.customWidths.put("READY", 90);
        props.customWidths.put("COMPLETED", 115);
        props.customWidths.put("REJECTED", 105);
        props.customWidths.put("ALL", 80);
        
        // Button dimensions
        props.buttonHeight = 38;
        
        // Padding
        props.horizontalPadding = 12;
        props.verticalPadding = 10;
        
        // Font
        props.normalFont = new Font("Segoe UI", Font.PLAIN, 14);
        props.selectedFont = new Font("Segoe UI", Font.BOLD, 14);
        
        CustomFilterBar bar = new CustomFilterBar(props);
        
        // Clear default filters first
        bar.clearFilters();
        
        // Add status filters using the new definition system
        bar.setFilterDefinitions(createStatusFilterDefinitions());
        
        // Initialize all counts to 0
        Map<String, Integer> initialCounts = new HashMap<>();
        for (FilterDefinition def : bar.getFilterDefinitions()) {
            initialCounts.put(def.getId(), 0);
        }
        bar.setFilterCounts(initialCounts);
        
        return bar;
    }
    
    // Method to manually adjust button widths
    public void adjustButtonWidths(Map<String, Integer> widths) {
        for (Map.Entry<String, Integer> entry : widths.entrySet()) {
            properties.customWidths.put(entry.getKey().toUpperCase(), entry.getValue());
        }
        refreshUI();
    }
    
    // Method to recalculate all button widths
    public void recalculateButtonWidths() {
        for (FilterButton button : filterButtons) {
            button.updateButtonText();
        }
        revalidate();
        repaint();
    }
    
    public void clearFilters() {
        for (FilterButton button : filterButtons) {
            remove(button);
        }
        filterButtons.clear();
        filterModel = new FilterModel();
        revalidate();
        repaint();
    }
    
    public void highlightFilter(String filterName, boolean highlight) {
        for (FilterButton button : filterButtons) {
            if (button.getName().equalsIgnoreCase(filterName)) {
                if (highlight) {
                    button.color = button.color.brighter();
                } else {
                    // Reset to original color
                    button.color = getFilterColor(filterName);
                }
                button.updateAppearance();
                break;
            }
        }
    }
    
    public void setEnabled(String filterName, boolean enabled) {
        for (FilterButton button : filterButtons) {
            if (button.getName().equalsIgnoreCase(filterName)) {
                button.setEnabled(enabled);
                button.setForeground(enabled ? properties.normalColor : properties.borderColor);
                break;
            }
        }
    }
    
    // New method to refresh counts display
    public void refreshCountsDisplay() {
        for (FilterButton button : filterButtons) {
            button.updateButtonText();
        }
        revalidate();
        repaint();
    }
    
    // New method to get count for specific filter
    public int getFilterCount(String filterName) {
        return filterModel.getCount(filterName);
    }
    
    // Method to change auto-resize setting
    public void setAutoResizeButtons(boolean autoResize) {
        properties.autoResizeButtons = autoResize;
        refreshUI();
    }
    
    // Method to toggle count display
    public void setShowCounts(boolean show) {
        properties.showCounts = show;
        refreshCountsDisplay();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Draw clean white background
        g2.setColor(properties.backgroundColor);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw subtle top border
        g2.setColor(new Color(240, 240, 240));
        g2.drawLine(0, 0, getWidth(), 0);
        
        g2.dispose();
        super.paintComponent(g);
    }
    
    @Override
    public Dimension getPreferredSize() {
        // Calculate preferred height based on button height and padding
        int preferredHeight = properties.buttonHeight + 16;
        return new Dimension(super.getPreferredSize().width, Math.max(48, preferredHeight));
    }
}