package component.filter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class CustomFilterBar extends JPanel {
    
    // Properties (using your color scheme)
    private List<FilterButton> filterButtons;
    private FilterModel filterModel;
    private FilterListener listener;
    private String activeFilter;
    
    // Colors from your CustomCheckBox
    private final Color NORMAL_COLOR = new Color(70, 70, 70); // #464646
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215); // #0078D7
    private final Color HOVER_COLOR = new Color(0, 100, 180); // #0064B4
    private final Color BACKGROUND_COLOR = Color.WHITE;
    private final Color BORDER_COLOR = new Color(150, 150, 150); // #969696
    private static final Color ERROR_COLOR = new Color(220, 53, 69); // #DC3545
    private final Color SELECTED_BG = new Color(0, 120, 215, 20); // Light blue background
    
    // Fonts from your CustomCheckBox
    private final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private final Font SELECTED_FONT = new Font("Segoe UI", Font.BOLD, 12);
    
    public CustomFilterBar() {
        initComponents();
        setupDefaults();
    }
    
    private void initComponents() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(8, 12, 8, 12));
        
        filterButtons = new ArrayList<>();
        filterModel = new FilterModel();
        
        setupDefaultFilters();
    }
    
    private void setupDefaults() {
        activeFilter = "ALL";
    }
    
    private void setupDefaultFilters() {
        // Default status filters
        String[] filters = {"ALL", "PENDING", "PROCESSING", "READY", "COMPLETED", "REJECTED"};
        
        for (String filter : filters) {
            addFilterOption(filter, getDisplayText(filter), getFilterColor(filter));
        }
    }
    
    private String getDisplayText(String filter) {
        switch (filter.toUpperCase()) {
            case "PENDING": return "Pending";
            case "PROCESSING": return "Processing";
            case "READY": return "Ready";
            case "COMPLETED": return "Completed";
            case "REJECTED": return "Rejected";
            default: return "All";
        }
    }
    
    private Color getFilterColor(String filter) {
        switch (filter.toUpperCase()) {
            case "PENDING": return new Color(255, 193, 7); // Amber
            case "PROCESSING": return PRIMARY_COLOR; // Blue
            case "READY": return new Color(40, 167, 69); // Green
            case "COMPLETED": return new Color(111, 66, 193); // Purple
            case "REJECTED": return ERROR_COLOR; // Red
            default: return PRIMARY_COLOR;
        }
    }
    
    public void addFilterOption(String name, String displayText) {
        addFilterOption(name, displayText, PRIMARY_COLOR);
    }
    
    public void addFilterOption(String name, String displayText, Color color) {
        FilterButton button = new FilterButton(name, displayText, color);
        filterButtons.add(button);
        add(button);
        
        filterModel.addFilter(name, displayText, color);
        
        revalidate();
        repaint();
    }
    
    public void setFilterCounts(Map<String, Integer> counts) {
        for (FilterButton button : filterButtons) {
            Integer count = counts.get(button.getName());
            if (count != null) {
                button.setCount(count);
                button.setToolTipText(button.getDisplayText() + ": " + count + " items");
            }
        }
        filterModel.setCounts(counts);
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
    
    // Filter Button class with your exact styling
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
            setText(getButtonText());
            setFont(TEXT_FONT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(true);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(6, 12, 6, 12));
            updateAppearance();
        }
        
        private void setupListeners() {
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
                        // Scale effect like your checkbox
                        setSize((int)(getWidth() * 0.98), (int)(getHeight() * 0.98));
                    }
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        setSize((int)(getWidth() / 0.98), (int)(getHeight() / 0.98));
                        setActiveFilter(name);
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        showContextMenu(e);
                    }
                }
            });
        }
        
        private String getButtonText() {
            if (count > 0) {
                return displayText + " (" + count + ")";
            }
            return displayText;
        }
        
        public void setActive(boolean active) {
            this.active = active;
            updateAppearance();
        }
        
        public void setCount(int count) {
            this.count = count;
            setText(getButtonText());
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
                setFont(SELECTED_FONT);
            } else if (hover) {
                setForeground(HOVER_COLOR);
                setFont(TEXT_FONT);
            } else {
                setForeground(NORMAL_COLOR);
                setFont(TEXT_FONT);
            }
            repaint();
        }
        
        private void showContextMenu(MouseEvent e) {
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
            
            // Draw background based on state
            if (active) {
                // Light blue background for active state (like your selected checkbox)
                g2.setColor(SELECTED_BG);
                g2.fillRoundRect(0, 0, width, height, 6, 6);
                
                // Primary color border (like your checkbox selected border)
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, width - 3, height - 3, 4, 4);
            } else if (hover) {
                // Light hover background
                g2.setColor(new Color(248, 249, 250));
                g2.fillRoundRect(0, 0, width, height, 6, 6);
                
                // Hover color border
                g2.setColor(HOVER_COLOR);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, width - 1, height - 1, 4, 4);
            } else {
                // Normal state
                g2.setColor(BACKGROUND_COLOR);
                g2.fillRoundRect(0, 0, width, height, 6, 6);
                
                // Light gray border (like your checkbox normal border)
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, width - 1, height - 1, 4, 4);
            }
            
            g2.dispose();
            
            // Paint text
            super.paintComponent(g);
            
            // Add subtle checkmark for active state (like your checkbox)
            if (active) {
                g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2f));
                
                // Draw checkmark in bottom right corner
                int checkSize = 8;
                int x = width - checkSize - 4;
                int y = height - checkSize - 4;
                
                g2.drawLine(x, y + checkSize/2, x + checkSize/3, y + checkSize);
                g2.drawLine(x + checkSize/3, y + checkSize, x + checkSize, y);
                
                g2.dispose();
            }
        }
        
        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            return new Dimension(size.width + 8, 32); // Fixed height matching your checkbox style
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
    
    // Factory methods for common filter types
    public static CustomFilterBar createStatusFilterBar() {
        CustomFilterBar bar = new CustomFilterBar();
        
        // Clear default filters first
        bar.clearFilters();
        
        // Add status filters with your color scheme
        Map<String, Color> statusColors = new LinkedHashMap<>();
        statusColors.put("ALL", new Color(70, 70, 70)); // Dark gray like your normal text
        statusColors.put("PENDING", new Color(255, 193, 7)); // Amber
        statusColors.put("PROCESSING", PRIMARY_COLOR); // Using your primary blue
        statusColors.put("READY", new Color(40, 167, 69)); // Green
        statusColors.put("COMPLETED", new Color(111, 66, 193)); // Purple
        statusColors.put("REJECTED", ERROR_COLOR); // Using your error red
        
        for (Map.Entry<String, Color> entry : statusColors.entrySet()) {
            String displayText = entry.getKey().charAt(0) + 
                               entry.getKey().substring(1).toLowerCase();
            bar.addFilterOption(entry.getKey(), displayText, entry.getValue());
        }
        
        return bar;
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
                    switch (filterName.toUpperCase()) {
                        case "PENDING": button.color = new Color(255, 193, 7); break;
                        case "PROCESSING": button.color = PRIMARY_COLOR; break;
                        case "READY": button.color = new Color(40, 167, 69); break;
                        case "COMPLETED": button.color = new Color(111, 66, 193); break;
                        case "REJECTED": button.color = ERROR_COLOR; break;
                        default: button.color = new Color(70, 70, 70);
                    }
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
                button.setForeground(enabled ? NORMAL_COLOR : BORDER_COLOR);
                break;
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Draw clean white background (like your CustomCheckBox)
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw subtle top border (optional)
        g2.setColor(new Color(240, 240, 240));
        g2.drawLine(0, 0, getWidth(), 0);
        
        g2.dispose();
        super.paintComponent(g);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width, 48);
    }
}