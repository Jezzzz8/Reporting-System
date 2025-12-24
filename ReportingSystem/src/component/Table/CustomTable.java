package component.Table;

import component.Button.FlatButton;
import component.DropdownButton.CustomDropdownButton;
import component.Scroll.CustomScrollPane;
import sys.main.CustomCheckBox;
import component.CustomDatePicker.CustomDatePicker;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomTable extends JTable {
    
    private Color headerColor = new Color(142, 217, 255);
    private Color oddRowColor = new Color(250, 250, 250);
    private Color evenRowColor = new Color(142, 217, 255);
    private Color hoverColor = new Color(225, 240, 255);
    private Color selectedRowColor = new Color(0, 120, 215);
    private Color selectedRowTextColor = Color.WHITE;
    private Color normalTextColor = Color.BLACK;
    private Font headerFont = new Font("Segoe UI", Font.BOLD, 12);
    private Font cellFont = new Font("Segoe UI", Font.PLAIN, 12);
    private int rowHeight = 30;
    private boolean showGrid = true;
    private boolean initialized = false;
    private Dimension viewportSize = new Dimension(400, 100);
    private boolean autoResizeVertical = false;
    private boolean autoResizeHorizontal = true;
    private int hoveredRow = -1;
    
    // Reference to the scroll pane
    private CustomScrollPane scrollPane;
    private boolean scrollPaneCreated = false;
    
    // Cell editors for custom components
    private CustomDropdownCellEditor dropdownCellEditor;
    private CustomDatePickerCellEditor datePickerCellEditor;
    private CustomCheckBoxCellEditor checkBoxCellEditor;
    
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
        super.setShowGrid(showGrid);  // Use super.setShowGrid instead of setShowGrid
        setGridColor(new Color(220, 220, 220));
        setFont(cellFont);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set preferred viewport size for initial display
        setPreferredScrollableViewportSize(viewportSize);
        
        // Configure table header
        customizeTableHeader();
        
        // Configure cell renderer for alternating row colors and custom components
        setDefaultRenderer(Object.class, new CustomTableCellRenderer());
        
        // Configure cell editors for custom components
        initCellEditors();
        
        // Add hover effects
        addHoverEffects();
        
        // Disable column reordering
        getTableHeader().setReorderingAllowed(false);
        
        // Set selection colors
        setSelectionBackground(selectedRowColor);
        setSelectionForeground(selectedRowTextColor);
        
        // Set auto resize mode
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // IMPORTANT: Create scroll pane immediately
        createAndConfigureScrollPane();
        
        initialized = true;
    }
    
    private void initCellEditors() {
        // Create cell editors for custom components
        dropdownCellEditor = new CustomDropdownCellEditor();
        datePickerCellEditor = new CustomDatePickerCellEditor();
        checkBoxCellEditor = new CustomCheckBoxCellEditor();
    }
    
    // PUBLIC METHOD to create scroll pane
    public void createAndConfigureScrollPane() {
        if (scrollPaneCreated && scrollPane != null) {
            return; // Already created
        }
        
        scrollPane = new CustomScrollPane(this);
        scrollPane.applySecondaryStyle();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Configure scroll bar policies based on auto-resize settings
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
        
        // Set smooth scrolling
        scrollPane.enableSmoothScrolling(true);
        
        // Update view background to match table
        scrollPane.setViewBackground(oddRowColor);
        
        scrollPaneCreated = true;
    }
    
    // Override addNotify to ensure scroll pane is created when added to container
    @Override
    public void addNotify() {
        super.addNotify();
        if (!scrollPaneCreated) {
            createAndConfigureScrollPane();
        }
    }
    
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return calculateOptimalSize();
    }
    
    @Override
    public Dimension getPreferredSize() {
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
                width += column.getPreferredWidth();
            }
            width += 10; // Add padding
        }
        return Math.max(width, 100);
    }
    
    private int calculateContentHeight() {
        int rowCount = getRowCount();
        JTableHeader header = getTableHeader();
        int headerHeight = header != null ? header.getPreferredSize().height : 0;
        
        if (rowCount > 0) {
            return (rowCount * getRowHeight()) + headerHeight + 5;
        }
        return headerHeight + getRowHeight() * 3;
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
                    BorderFactory.createMatteBorder(0, 0, 2, 1, Color.WHITE),
                    BorderFactory.createEmptyBorder(8, 5, 8, 5)
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
                int row = rowAtPoint(evt.getPoint());
                if (row != hoveredRow) {
                    hoveredRow = row;
                    repaint();
                }
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                if (row != hoveredRow) {
                    hoveredRow = row;
                    repaint();
                }
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                repaint();
            }
        });
    }
    
    // Custom cell renderer with proper text color handling
    private class CustomTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            // Handle custom components separately
            if (value instanceof Component) {
                Component comp = (Component) value;
                
                // Set background based on selection and row
                if (isSelected) {
                    comp.setBackground(selectedRowColor);
                } else {
                    comp.setBackground(row % 2 == 0 ? oddRowColor : evenRowColor);
                }
                
                // Apply hover effect for non-selected rows
                if (!isSelected && row == hoveredRow && table.isEnabled()) {
                    comp.setBackground(hoverColor);
                }
                
                return comp;
            }
            
            // Default text cell handling
            Component component = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
            
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(cellFont);
                
                if (isSelected) {
                    label.setBackground(selectedRowColor);
                    label.setForeground(selectedRowTextColor);
                } else {
                    label.setBackground(row % 2 == 0 ? oddRowColor : evenRowColor);
                    label.setForeground(normalTextColor); // BLACK FOR NON-SELECTED
                    
                    if (row == hoveredRow && table.isEnabled()) {
                        label.setBackground(hoverColor);
                        label.setForeground(normalTextColor); // Keep black text on hover
                    }
                }
            }
            
            return component;
        }
    }
    
    // Custom cell editors for custom components
    private class CustomDropdownCellEditor extends DefaultCellEditor {
        private CustomDropdownButton dropdownButton;
        
        public CustomDropdownCellEditor() {
            super(new JTextField());
            dropdownButton = new CustomDropdownButton("Select option");
            editorComponent = dropdownButton;
            
            // Configure dropdown button
            dropdownButton.setPreferredSize(new Dimension(150, 30));
            dropdownButton.setOptions(new String[]{"Option 1", "Option 2", "Option 3"});
            
            // Set click count to start editing
            setClickCountToStart(2);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (value instanceof CustomDropdownButton) {
                dropdownButton = (CustomDropdownButton) value;
            } else if (value instanceof String) {
                dropdownButton.setText((String) value);
            }
            
            // Set background to match selection
            if (isSelected) {
                dropdownButton.setBackground(selectedRowColor);
                dropdownButton.setForeground(selectedRowTextColor);
            } else {
                dropdownButton.setBackground(row % 2 == 0 ? oddRowColor : evenRowColor);
                dropdownButton.setForeground(normalTextColor);
            }
            
            return dropdownButton;
        }
        
        @Override
        public Object getCellEditorValue() {
            return dropdownButton.getText();
        }
    }
    
    private class CustomDatePickerCellEditor extends DefaultCellEditor {
        private CustomDatePicker datePicker;
        
        public CustomDatePickerCellEditor() {
            super(new JTextField());
            datePicker = new CustomDatePicker("Select date");
            editorComponent = datePicker;
            
            // Configure date picker
            datePicker.setPreferredSize(new Dimension(150, 30));
            
            setClickCountToStart(2);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (value instanceof CustomDatePicker) {
                datePicker = (CustomDatePicker) value;
            } else if (value instanceof Date) {
                datePicker.setDate((Date) value);
            } else if (value instanceof String) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    datePicker.setDate(sdf.parse((String) value));
                } catch (Exception e) {
                    datePicker.setDate(new Date());
                }
            }
            
            // Set background to match selection
            if (isSelected) {
                datePicker.setBackgroundColor(selectedRowColor);
            } else {
                datePicker.setBackgroundColor(row % 2 == 0 ? oddRowColor : evenRowColor);
            }
            
            return datePicker;
        }
        
        @Override
        public Object getCellEditorValue() {
            return datePicker.getDate();
        }
    }
    
    private class CustomCheckBoxCellEditor extends DefaultCellEditor {
        private CustomCheckBox checkBox;
        
        public CustomCheckBoxCellEditor() {
            super(new JCheckBox());
            checkBox = new CustomCheckBox("Agree to terms", "View Terms");
            editorComponent = checkBox;
            
            // Configure checkbox
            checkBox.setPreferredSize(new Dimension(200, 25));
            
            setClickCountToStart(1);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (value instanceof CustomCheckBox) {
                checkBox = (CustomCheckBox) value;
            } else if (value instanceof Boolean) {
                checkBox.setSelected((Boolean) value);
            }
            
            // Set background to match selection
            if (isSelected) {
                checkBox.setBackground(selectedRowColor);
            } else {
                checkBox.setBackground(row % 2 == 0 ? oddRowColor : evenRowColor);
            }
            
            return checkBox;
        }
        
        @Override
        public Object getCellEditorValue() {
            return checkBox.isSelected();
        }
    }
    
    // Public methods to configure column editors
    public void setColumnAsDropdown(int columnIndex, String[] options) {
        TableColumn column = getColumnModel().getColumn(columnIndex);
        
        // Set renderer
        column.setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                String displayText = value != null ? value.toString() : "Select option";
                CustomDropdownButton button = new CustomDropdownButton(displayText);
                button.setOptions(options);
                button.setPreferredSize(new Dimension(150, 30));
                
                if (isSelected) {
                    button.setBackground(selectedRowColor);
                    button.setForeground(selectedRowTextColor);
                } else {
                    button.setBackground(row % 2 == 0 ? oddRowColor : evenRowColor);
                    button.setForeground(normalTextColor);
                }
                
                // Apply hover effect
                if (!isSelected && row == hoveredRow && table.isEnabled()) {
                    button.setBackground(hoverColor);
                    button.setForeground(normalTextColor);
                }
                
                return button;
            }
        });
        
        // Set editor
        column.setCellEditor(dropdownCellEditor);
    }
    
    public void setColumnAsDatePicker(int columnIndex) {
        TableColumn column = getColumnModel().getColumn(columnIndex);
        
        // Set renderer
        column.setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                CustomDatePicker datePicker = new CustomDatePicker("Select date");
                if (value instanceof Date) {
                    datePicker.setDate((Date) value);
                }
                datePicker.setPreferredSize(new Dimension(150, 30));
                
                if (isSelected) {
                    datePicker.setBackgroundColor(selectedRowColor);
                } else {
                    datePicker.setBackgroundColor(row % 2 == 0 ? oddRowColor : evenRowColor);
                }
                
                // Apply hover effect
                if (!isSelected && row == hoveredRow && table.isEnabled()) {
                    datePicker.setBackgroundColor(hoverColor);
                }
                
                return datePicker;
            }
        });
        
        // Set editor
        column.setCellEditor(datePickerCellEditor);
    }
    
    public void setColumnAsCheckBox(int columnIndex, String termsText, String linkText) {
        TableColumn column = getColumnModel().getColumn(columnIndex);
        
        // Set renderer
        column.setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                CustomCheckBox checkBox = new CustomCheckBox(
                    termsText != null ? termsText : "Agree to terms",
                    linkText != null ? linkText : "View Terms"
                );
                
                if (value instanceof Boolean) {
                    checkBox.setSelected((Boolean) value);
                }
                checkBox.setPreferredSize(new Dimension(200, 25));
                
                if (isSelected) {
                    checkBox.setBackground(selectedRowColor);
                } else {
                    checkBox.setBackground(row % 2 == 0 ? oddRowColor : evenRowColor);
                }
                
                // Apply hover effect
                if (!isSelected && row == hoveredRow && table.isEnabled()) {
                    checkBox.setBackground(hoverColor);
                }
                
                return checkBox;
            }
        });
        
        // Set editor
        column.setCellEditor(checkBoxCellEditor);
    }
    
    public void setColumnAsButton(int columnIndex, String buttonText) {
        TableColumn column = getColumnModel().getColumn(columnIndex);
        
        // Set renderer
        column.setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                FlatButton button = new FlatButton(buttonText != null ? buttonText : "Click");
                button.setPreferredSize(new Dimension(100, 25));
                
                if (isSelected) {
                    button.setNormalColor(selectedRowColor);
                    button.setTextColor(selectedRowTextColor);
                } else {
                    button.setAsPrimary();
                }
                
                // Apply hover effect (button has its own hover)
                if (!isSelected && row == hoveredRow && table.isEnabled()) {
                    button.setNormalColor(hoverColor);
                }
                
                return button;
            }
        });
        
        // Note: Buttons are typically not editable, so we don't set a cell editor
        column.setCellEditor(null);
    }
    
    // Override setModel to reinitialize if needed
    @Override
    public void setModel(TableModel dataModel) {
        super.setModel(dataModel);  // Call super.setModel first
        if (initialized) {
            // Reapply customizations
            super.setRowHeight(rowHeight);
            customizeTableHeader();
            setDefaultRenderer(Object.class, new CustomTableCellRenderer());
            revalidate();
            repaint();
        }
    }
    
    // ========== GETTERS AND SETTERS ==========
    
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
            if (scrollPane != null) {
                scrollPane.setViewBackground(oddRowColor);
            }
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
            repaint();
        }
    }
    
    public void setSelectedRowColor(Color selectedRowColor) {
        this.selectedRowColor = selectedRowColor;
        if (initialized) {
            setSelectionBackground(selectedRowColor);
            repaint();
        }
    }
    
    public void setSelectedRowTextColor(Color selectedRowTextColor) {
        this.selectedRowTextColor = selectedRowTextColor;
        if (initialized) {
            setSelectionForeground(selectedRowTextColor);
            repaint();
        }
    }
    
    public void setNormalTextColor(Color normalTextColor) {
        this.normalTextColor = normalTextColor;
        if (initialized) {
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
            super.setFont(cellFont);  // Use super.setFont instead of setFont
            repaint();
        }
    }
    
    public void setCustomRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
        if (initialized) {
            super.setRowHeight(rowHeight);  // Use super.setRowHeight instead of setRowHeight
            revalidate();
            repaint();
        }
    }
    
    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        if (initialized) {
            super.setShowGrid(showGrid);  // Use super.setShowGrid instead of setShowGrid
        }
    }
    
    // Auto-resize control methods
    public void setAutoResizeVertical(boolean autoResizeVertical) {
        this.autoResizeVertical = autoResizeVertical;
        if (initialized && scrollPane != null) {
            if (autoResizeVertical) {
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            } else {
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            }
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
            if (autoResizeHorizontal) {
                setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                if (scrollPane != null) {
                    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                }
            } else {
                setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                if (scrollPane != null) {
                    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                }
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
            
            if (scrollPane != null) {
                if (vertical) {
                    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                } else {
                    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                }
                
                if (horizontal) {
                    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                } else {
                    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                }
            }
            
            revalidate();
            repaint();
        }
    }
    
    // ========== SCROLL PANE METHODS ==========
    
    // Get the scroll pane - creates it if not already created
    public CustomScrollPane getScrollPane() {
        if (scrollPane == null || !scrollPaneCreated) {
            createAndConfigureScrollPane();
        }
        return scrollPane;
    }
    
    // Method to check if scroll pane is created
    public boolean isScrollPaneCreated() {
        return scrollPaneCreated;
    }
    
    // Method to create a panel with the table inside a scroll pane
    public JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(getScrollPane(), BorderLayout.CENTER);
        return panel;
    }
    
    // Method to create a complete table panel with proper sizing
    public JPanel createCompleteTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create scroll pane if not already created
        CustomScrollPane scrollPane = getScrollPane();
        
        // Set preferred size for the panel
        panel.setPreferredSize(new Dimension(600, 400));
        
        // Add scroll pane to panel
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Factory method to create table with scroll pane
    public static JPanel createCompleteTablePanel(CustomTable table) {
        return table.createCompleteTablePanel();
    }
    
    public static JPanel createCompleteTablePanel(TableModel model) {
        CustomTable table = new CustomTable(model);
        return table.createCompleteTablePanel();
    }
    
    public static JPanel createCompleteTablePanel(Object[][] rowData, Object[] columnNames) {
        CustomTable table = new CustomTable(rowData, columnNames);
        return table.createCompleteTablePanel();
    }
    
    // Method to apply a specific scroll pane style
    public void applyScrollPaneStyle(String style) {
        CustomScrollPane scrollPane = getScrollPane();
        
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

    public void setColumnAsIconButton(int columnIndex, ImageIcon icon, String tooltip) {
        TableColumn column = getColumnModel().getColumn(columnIndex);

        // Set renderer
        column.setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                JButton button = new JButton();
                if (icon != null) {
                    button.setIcon(icon);
                    button.setText("");
                } else if (value != null) {
                    button.setText(value.toString());
                }

                button.setToolTipText(tooltip != null ? tooltip : "Click");
                button.setPreferredSize(new Dimension(30, 25));
                button.setFont(new Font("Segoe UI", Font.PLAIN, 11));

                if (isSelected) {
                    button.setBackground(new Color(0, 100, 180));
                    button.setForeground(Color.WHITE);
                    button.setBorder(BorderFactory.createLineBorder(new Color(0, 80, 160), 1));
                } else {
                    button.setBackground(new Color(0, 120, 215));
                    button.setForeground(Color.WHITE);
                    button.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 180), 1));
                }

                button.setOpaque(true);
                button.setFocusPainted(false);
                button.setContentAreaFilled(true);

                // Apply hover effect
                if (!isSelected && row == hoveredRow && table.isEnabled()) {
                    button.setBackground(new Color(0, 140, 230));
                }

                return button;
            }
        });

        // Note: Icon buttons are typically not editable unless you want them to be
        // If you want them clickable, you'll need to set a cell editor
        column.setCellEditor(null);
    }

    // Method to update scroll pane when table colors change
    public void updateScrollPaneColors() {
        if (scrollPane != null) {
            scrollPane.setViewBackground(oddRowColor);
        }
    }
    
    // Override setEnabled to update custom components
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);  // Call super.setEnabled first
        repaint();
    }
    
    // Method to get the table wrapped in a scroll pane (for direct use)
    public JScrollPane getWrappedTable() {
        return getScrollPane();
    }
    
    public int getHoveredRow() {
        return hoveredRow;
    }
}