package component.Table;

import component.Scroll.CustomScrollPane;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class CustomTable extends JTable {
    
    private Color headerColor = new Color(142, 217, 255);
    private Color oddRowColor = new Color(250, 250, 250);
    private Color evenRowColor = new Color(142, 217, 255);
    private Color hoverColor = new Color(225, 240, 255);
    private Font headerFont = new Font("Times New Roman", Font.BOLD, 12);
    private Font cellFont = new Font("Times New Roman", Font.PLAIN, 12);
    private int rowHeight = 30;
    private boolean showGrid = true;
    private boolean initialized = false;
    private Dimension viewportSize = new Dimension(400, 100);
    private boolean autoResizeVertical = true;
    private boolean autoResizeHorizontal = true;
    
    // Reference to the scroll pane (if created through factory methods)
    private CustomScrollPane scrollPane;
    
    public CustomTable() {
        super();
        initCustomTable();
    }
    
    public CustomTable(TableModel model) {
        super(model);
        initCustomTable();
    }
    
    public CustomTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
        initCustomTable();
    }
    
    private void initCustomTable() {
        if (initialized) return;
        
        // Set basic properties
        setRowHeight(rowHeight);
        setShowGrid(showGrid);
        setGridColor(new Color(220, 220, 220));
        setFont(cellFont);
        
        // Set preferred viewport size for initial display
        setPreferredScrollableViewportSize(viewportSize);
        
        // Configure table header
        customizeTableHeader();
        
        // Configure cell renderer for alternating row colors
        setDefaultRenderer(Object.class, new CustomTableCellRenderer());
        
        // Add hover effects
        addHoverEffects();
        
        // Disable column reordering
        getTableHeader().setReorderingAllowed(false);
        
        // Set selection colors
        setSelectionBackground(hoverColor);
        setSelectionForeground(Color.BLACK);
        
        // Set auto resize mode
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        initialized = true;
    }
    
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        // Return flexible size based on content and container
        return calculateOptimalSize();
    }
    
    @Override
    public Dimension getPreferredSize() {
        // Calculate size that fits content but also respects container
        return calculateOptimalSize();
    }
    
    private Dimension calculateOptimalSize() {
        Container parent = getParent();
        Dimension containerSize = parent != null ? parent.getSize() : null;
        
        // Calculate content-based size
        int contentWidth = calculateContentWidth();
        int contentHeight = calculateContentHeight();
        
        // If we have a container, adjust to fit
        if (containerSize != null && containerSize.width > 0 && containerSize.height > 0) {
            // For horizontal: use container width if autoResizeHorizontal is true
            int width = autoResizeHorizontal ? 
                Math.max(containerSize.width, contentWidth) : contentWidth;
            
            // For vertical: use content height, but cap if needed
            int height = autoResizeVertical ? contentHeight : 
                Math.min(contentHeight, containerSize.height);
            
            // Ensure minimum size
            width = Math.max(width, viewportSize.width);
            height = Math.max(height, viewportSize.height);
            
            return new Dimension(width, height);
        }
        
        // No container yet, return content size with minimums
        return new Dimension(
            Math.max(contentWidth, viewportSize.width),
            Math.max(contentHeight, viewportSize.height)
        );
    }
    
    private int calculateContentWidth() {
        int width = 0;
        TableColumnModel columnModel = getColumnModel();
        if (columnModel != null) {
            for (int i = 0; i < columnModel.getColumnCount(); i++) {
                TableColumn column = columnModel.getColumn(i);
                // Use preferred width for calculation
                width += column.getPreferredWidth();
            }
            // Add some padding
            width += 10;
        }
        return Math.max(width, 100); // Minimum width
    }
    
    private int calculateContentHeight() {
        int rowCount = getRowCount();
        JTableHeader header = getTableHeader();
        int headerHeight = header != null ? header.getPreferredSize().height : 0;
        
        if (rowCount > 0) {
            return (rowCount * getRowHeight()) + headerHeight + 5; // Add small margin
        }
        return headerHeight + getRowHeight() * 3; // Show at least 3 rows
    }
    
    private void customizeTableHeader() {
        JTableHeader header = getTableHeader();
        if (header == null) return;
        
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(headerColor);
                setForeground(Color.BLACK);
                setFont(headerFont);
                setHorizontalAlignment(CENTER);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                return this;
            }
        });
        
        // Set header height
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
    }
    
    private void addHoverEffects() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent evt) {
                if (!initialized) return;
                
                int row = rowAtPoint(evt.getPoint());
                int col = columnAtPoint(evt.getPoint());
                
                if (row >= 0 && col >= 0) {
                    setSelectionBackground(hoverColor);
                    setSelectionForeground(Color.BLACK);
                }
            }
        });
    }
    
    // Override addNotify to ensure initialization
    @Override
    public void addNotify() {
        super.addNotify();
        if (!initialized) {
            initCustomTable();
        }
    }
    
    // Override setModel to reinitialize if needed
    @Override
    public void setModel(TableModel dataModel) {
        super.setModel(dataModel);
        if (initialized) {
            // Reapply customizations
            setRowHeight(rowHeight);
            customizeTableHeader();
            setDefaultRenderer(Object.class, new CustomTableCellRenderer());
            // Trigger revalidation for proper sizing
            revalidate();
            repaint();
        }
    }
    
    // Custom cell renderer for alternating row colors
    private class CustomTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
            
            if (!initialized) return c;
            
            if (!isSelected) {
                if (row % 2 == 0) {
                    c.setBackground(oddRowColor);
                } else {
                    c.setBackground(evenRowColor);
                }
            }
            
            // Center align all cells
            ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
            
            return c;
        }
    }
    
    // Enable vertical auto-resizing
    @Override
    public boolean getScrollableTracksViewportHeight() {
        // Return true to track viewport height (no vertical scrollbar)
        // Return false to allow vertical scrolling
        return autoResizeVertical;
    }
    
    // Enable horizontal auto-resizing
    @Override
    public boolean getScrollableTracksViewportWidth() {
        // Return true to track viewport width (no horizontal scrollbar)
        // Return false to allow horizontal scrolling
        return autoResizeHorizontal;
    }
    
    @Override
    public void doLayout() {
        // Let the table do its normal layout
        super.doLayout();
        
        // If we're tracking viewport width, adjust columns
        if (autoResizeHorizontal && getAutoResizeMode() != AUTO_RESIZE_OFF) {
            adjustColumnWidthsToFill();
        }
    }
    
    private void adjustColumnWidthsToFill() {
        TableColumnModel columnModel = getColumnModel();
        if (columnModel == null) return;
        
        int columnCount = columnModel.getColumnCount();
        if (columnCount == 0) return;
        
        Container parent = getParent();
        if (!(parent instanceof JViewport)) return;
        
        int viewportWidth = parent.getWidth();
        if (viewportWidth <= 0) return;
        
        // Calculate total current width
        int totalWidth = 0;
        for (int i = 0; i < columnCount; i++) {
            totalWidth += columnModel.getColumn(i).getWidth();
        }
        
        // If there's extra space, distribute it proportionally
        if (totalWidth < viewportWidth && totalWidth > 0) {
            int extraWidth = viewportWidth - totalWidth;
            int widthPerColumn = extraWidth / columnCount;
            int remainder = extraWidth % columnCount;
            
            for (int i = 0; i < columnCount; i++) {
                TableColumn column = columnModel.getColumn(i);
                int currentWidth = column.getWidth();
                int newWidth = currentWidth + widthPerColumn;
                if (i < remainder) {
                    newWidth++; // Distribute remainder
                }
                
                // Set new width
                column.setWidth(newWidth);
                column.setPreferredWidth(newWidth);
            }
        }
    }
    
    // Getters and setters for customization
    public void setHeaderColor(Color headerColor) {
        this.headerColor = headerColor;
        if (initialized) {
            customizeTableHeader();
            repaint();
        }
    }
    
    public void setOddRowColor(Color oddRowColor) {
        this.oddRowColor = oddRowColor;
        if (initialized) {
            repaint();
        }
    }
    
    public void setEvenRowColor(Color evenRowColor) {
        this.evenRowColor = evenRowColor;
        if (initialized) {
            repaint();
        }
    }
    
    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
        if (initialized) {
            setSelectionBackground(hoverColor);
            repaint();
        }
    }
    
    public void setHeaderFont(Font headerFont) {
        this.headerFont = headerFont;
        if (initialized) {
            customizeTableHeader();
            repaint();
        }
    }
    
    public void setCellFont(Font cellFont) {
        this.cellFont = cellFont;
        if (initialized) {
            setFont(cellFont);
            repaint();
        }
    }
    
    public void setCustomRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
        if (initialized) {
            setRowHeight(rowHeight);
            revalidate();
            repaint();
        }
    }
    
    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        if (initialized) {
            setShowGrid(showGrid);
        }
    }
    
    // Auto-resize control methods
    public void setAutoResizeVertical(boolean autoResizeVertical) {
        this.autoResizeVertical = autoResizeVertical;
        if (initialized) {
            revalidate();
            repaint();
        }
    }
    
    public boolean isAutoResizeVertical() {
        return autoResizeVertical;
    }
    
    public void setAutoResizeHorizontal(boolean autoResizeHorizontal) {
        this.autoResizeHorizontal = autoResizeHorizontal;
        if (initialized) {
            // Update auto-resize mode
            if (autoResizeHorizontal) {
                setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            } else {
                setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            }
            revalidate();
            repaint();
        }
    }
    
    public boolean isAutoResizeHorizontal() {
        return autoResizeHorizontal;
    }
    
    public void setAutoResize(boolean vertical, boolean horizontal) {
        this.autoResizeVertical = vertical;
        this.autoResizeHorizontal = horizontal;
        if (initialized) {
            if (horizontal) {
                setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            } else {
                setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            }
            revalidate();
            repaint();
        }
    }
    
    // Method to set custom preferred viewport size
    public void setPreferredViewportSize(Dimension size) {
        if (size != null) {
            this.viewportSize = size;
            if (initialized) {
                setPreferredScrollableViewportSize(size);
                revalidate();
                repaint();
            }
        }
    }
    
    public void setPreferredViewportSize(int width, int height) {
        setPreferredViewportSize(new Dimension(width, height));
    }
    
    // Method to check if table is initialized
    public boolean isInitialized() {
        return initialized;
    }
    
    // Method to reinitialize table
    public void reinitialize() {
        initialized = false;
        initCustomTable();
    }
    
    // Method to apply table effects (for compatibility with existing code)
    public void applyTableEffects() {
        reinitialize();
    }
    
    // Override paintComponent to ensure proper initialization
    @Override
    protected void paintComponent(Graphics g) {
        if (!initialized && getParent() != null) {
            initCustomTable();
        }
        super.paintComponent(g);
    }
    
    // Helper method to safely update table
    public void safeUpdate(Runnable updateAction) {
        if (initialized) {
            updateAction.run();
        }
    }
    
    // Getter for scroll pane
    public CustomScrollPane getScrollPane() {
        return scrollPane;
    }
    
    // Set scroll pane style based on table auto-resize settings
    public void configureScrollPaneStyle(CustomScrollPane scrollPane) {
        if (scrollPane == null) return;
        
        // Set scroll bar policies based on auto-resize settings
        if (autoResizeVertical) {
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        } else {
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        }
        
        if (autoResizeHorizontal) {
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        } else {
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }
        
        // Apply a style that matches the table
        scrollPane.applySecondaryStyle();
        
        // Update view background to match table background
        scrollPane.setViewBackground(oddRowColor);
        
        // Enable smooth scrolling for better user experience
        scrollPane.enableSmoothScrolling(true);
    }
    
    // Static helper method to create a scroll pane with CustomTable using CustomScrollPane
    public static CustomScrollPane createScrollableTable(CustomTable table) {
        CustomScrollPane scrollPane = new CustomScrollPane(table);
        
        // Configure the scroll pane based on table settings
        table.configureScrollPaneStyle(scrollPane);
        
        // Store reference to scroll pane in table
        table.scrollPane = scrollPane;
        
        return scrollPane;
    }
    
    // Factory method to create a complete scrollable table setup
    public static CustomScrollPane createScrollableTable(TableModel model, 
                                                        boolean autoResizeVertical, 
                                                        boolean autoResizeHorizontal) {
        CustomTable table = new CustomTable(model);
        table.setAutoResize(autoResizeVertical, autoResizeHorizontal);
        return createScrollableTable(table);
    }
    
    public static CustomScrollPane createScrollableTable(Object[][] rowData, Object[] columnNames,
                                                        boolean autoResizeVertical, 
                                                        boolean autoResizeHorizontal) {
        CustomTable table = new CustomTable(rowData, columnNames);
        table.setAutoResize(autoResizeVertical, autoResizeHorizontal);
        return createScrollableTable(table);
    }
    
    // Convenience methods for common configurations
    public static CustomScrollPane createAutoResizingTable(TableModel model) {
        return createScrollableTable(model, true, true);
    }
    
    public static CustomScrollPane createAutoResizingTable(Object[][] rowData, Object[] columnNames) {
        return createScrollableTable(rowData, columnNames, true, true);
    }
    
    public static CustomScrollPane createScrollableOnlyTable(TableModel model) {
        return createScrollableTable(model, false, false);
    }
    
    public static CustomScrollPane createScrollableOnlyTable(Object[][] rowData, Object[] columnNames) {
        return createScrollableTable(rowData, columnNames, false, false);
    }
    
    // Method to apply a specific scroll pane style
    public void applyScrollPaneStyle(String style) {
        if (scrollPane == null) return;
        
        switch (style.toLowerCase()) {
            case "primary":
                scrollPane.applyPrimaryStyle();
                break;
            case "secondary":
                scrollPane.applySecondaryStyle();
                break;
            case "minimal":
                scrollPane.applyMinimalStyle();
                break;
            default:
                scrollPane.applySecondaryStyle();
        }
        
        // Update view background to match table background
        scrollPane.setViewBackground(oddRowColor);
    }
    
    // Method to update scroll pane when table colors change
    public void updateScrollPaneColors() {
        if (scrollPane != null) {
            scrollPane.setViewBackground(oddRowColor);
        }
    }
    
    // Override color setters to also update scroll pane
    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (scrollPane != null) {
            scrollPane.setViewBackground(bg);
        }
    }
    
    public void setTableAndScrollColors(Color oddRow, Color evenRow, Color header, Color hover) {
        setOddRowColor(oddRow);
        setEvenRowColor(evenRow);
        setHeaderColor(header);
        setHoverColor(hover);
        
        if (scrollPane != null) {
            scrollPane.setViewBackground(oddRow);
        }
    }
}