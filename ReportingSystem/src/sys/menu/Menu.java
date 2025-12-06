package sys.menu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;

public class Menu extends JComponent {

    public MenuEvent getEvent() {
        return event;
    }

    public void setEvent(MenuEvent event) {
        this.event = event;
    }

    public interface MenuToggleListener {
        void onMenuToggle(boolean isCollapsed);
        void onMenuWidthChanged(int width);
    }
    
    private MenuToggleListener toggleListener;
    private MenuEvent event;
    private MigLayout layout;
    private MenuToggleButton leftToggleButton;
    private MenuToggleButton rightToggleButton;
    private JPanel togglePanel;
    private AlphaPanel menuItemsPanel; // Changed to AlphaPanel type
    private boolean isCollapsed = true; // Start in collapsed mode
    private Animator scrollAnimator;
    private int targetWidth = 200;
    private int collapsedWidth = 42;
    private float scrollAnimate = 0f;
    private boolean animating = false;
    
    private String[][] menuItems = new String[][]{
        {"Dashboard"},
        {"My Profile"},
        {"ID Status", "Current Status", "Renewal History", "Complaints/Issues"},
        {"Appointments", "Schedule New", "Upcoming", "Cancel/Reschedule"},
        {"Documents", "Upload Documents", "View Documents", "Document History"},
        {"Help & Support", "FAQs", "Contact Us"}
    };

    // Custom panel with alpha support
    private class AlphaPanel extends JPanel {
        private float alpha = 1f;
        
        public AlphaPanel(MigLayout layout) {
            super(layout);
            setOpaque(false);
        }
        
        public void setAlpha(float alpha) {
            this.alpha = alpha;
            repaint();
        }
        
        public float getAlpha() {
            return alpha;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    public Menu() {
        init();
        initScrollAnimation();
    }
    
    private void initScrollAnimation() {
        scrollAnimator = new Animator(300, new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                animating = true;
                scrollAnimate = fraction;
                updateMenuWidth();
                if (toggleListener != null) {
                    toggleListener.onMenuToggle(isCollapsed);
                }
            }
            
            @Override
            public void begin() {
                animating = true;
            }
            
            @Override
            public void end() {
                animating = false;
                scrollAnimate = isCollapsed ? 0f : 1f;
                updateMenuWidth();
                updateMenuItemsVisibility();
                
                // Force final state after animation
                setPreferredSize(new Dimension(isCollapsed ? collapsedWidth : targetWidth, getHeight()));
                revalidate();
                if (getParent() != null) {
                    getParent().revalidate();
                    getParent().repaint();
                }
            }
        });
        scrollAnimator.setResolution(5);
        scrollAnimator.setAcceleration(0.5f);
        scrollAnimator.setDeceleration(0.5f);
    }
    
    private void updateMenuWidth() {
        int currentWidth;
        if (isCollapsed) {
            // Collapsing: from expanded to collapsed
            currentWidth = collapsedWidth + (int)((targetWidth - collapsedWidth) * (1f - scrollAnimate));
        } else {
            // Expanding: from collapsed to expanded
            currentWidth = collapsedWidth + (int)((targetWidth - collapsedWidth) * scrollAnimate);
        }
        
        setPreferredSize(new Dimension(currentWidth, getHeight()));
        
        // Also notify listener of current width for body adjustment
        if (toggleListener != null) {
            toggleListener.onMenuWidthChanged(currentWidth);
        }
        
        // Smoothly show/hide menu items during animation
        if (menuItemsPanel != null) {
            float alphaValue = isCollapsed ? (1f - scrollAnimate) : scrollAnimate;
            menuItemsPanel.setVisible(scrollAnimate > 0.1f || !isCollapsed);
            menuItemsPanel.setAlpha(alphaValue);
            
            // Also update alpha for individual menu items
            for (Component comp : menuItemsPanel.getComponents()) {
                if (comp instanceof MenuItem) {
                    ((MenuItem) comp).setAlpha(alphaValue);
                }
            }
        }
        
        revalidate();
        if (getParent() != null) {
            getParent().revalidate();
            getParent().repaint();
        }
    }
    
    private void updateMenuItemsVisibility() {
        if (menuItemsPanel != null) {
            menuItemsPanel.setVisible(!isCollapsed);
            menuItemsPanel.setAlpha(isCollapsed ? 0f : 1f);
        }
    }
    
    public void setToggleListener(MenuToggleListener listener) {
        this.toggleListener = listener;
    }
    
    public boolean isCollapsed() {
        return isCollapsed;
    }
    
    public void toggleMenu() {
        if (animating) {
            return; // Don't toggle if animation is already running
        }
        
        isCollapsed = !isCollapsed;
        updateToggleButtons();
        scrollAnimator.start();
    }
    
    private void updateToggleButtons() {
        if (isCollapsed) {
            // Show hamburger on left, hide X on right
            leftToggleButton.setVisible(true);
            leftToggleButton.setToggled(false);
            leftToggleButton.setShowXOnRight(false);
            rightToggleButton.setVisible(false);
        } else {
            // Hide hamburger on left, show X on right
            leftToggleButton.setVisible(false);
            rightToggleButton.setVisible(true);
            rightToggleButton.setToggled(true);
            rightToggleButton.setShowXOnRight(true);
        }
    }
    
    private void updateMenuLayout() {
        if (isCollapsed) {
            setPreferredSize(new Dimension(collapsedWidth, getHeight()));
        } else {
            setPreferredSize(new Dimension(targetWidth, getHeight()));
        }
        revalidate();
        repaint();
    }

    private void init() {
        layout = new MigLayout("wrap 1, fillx, gapy 0, inset 2", "fill");
        setLayout(layout);
        setOpaque(true);
        
        // Create toggle panel at the top with proper height
        togglePanel = new JPanel(new MigLayout("inset 2, gap 0, fill", "[left][grow][right]", ""));
        togglePanel.setOpaque(false);
        togglePanel.setName("togglePanel");
        togglePanel.setPreferredSize(new Dimension(targetWidth, 40)); // Fixed height
        
        // Left toggle button (hamburger, visible when collapsed)
        leftToggleButton = new MenuToggleButton();
        leftToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleMenu();
            }
        });
        leftToggleButton.setShowXOnRight(false);
        leftToggleButton.setToggled(false);
        
        // Right toggle button (X, visible when expanded)
        rightToggleButton = new MenuToggleButton();
        rightToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleMenu();
            }
        });
        rightToggleButton.setShowXOnRight(true);
        rightToggleButton.setToggled(true);
        rightToggleButton.setVisible(false); // Hidden initially
        
        // Add buttons to toggle panel - use fixed sizes
        togglePanel.add(leftToggleButton, "left, w 32!, h 32!");
        togglePanel.add(new javax.swing.JLabel(), "grow"); // Spacer
        togglePanel.add(rightToggleButton, "right, w 32!, h 32!");
        
        add(togglePanel, "span, h 40!, wrap");
        
        // Add separator with less wrap space
        add(new javax.swing.JSeparator(), "span, growx, wrap 2");
        
        // Create a panel to hold all menu items (for easy hide/show)
        menuItemsPanel = new AlphaPanel(new MigLayout("wrap 1, fillx, gapy 0, inset 0", "fill"));
        menuItemsPanel.setName("menuItemsPanel");
        
        // Init MenuItem in the menuItemsPanel
        for (int i = 0; i < menuItems.length; i++) {
            addMenu(menuItems[i][0], i);
        }
        
        // Add the menu items panel to the main layout
        add(menuItemsPanel, "span, grow, push, wrap");
        
        // Set initial collapsed state (hide menu items)
        setPreferredSize(new Dimension(collapsedWidth, getHeight()));
        menuItemsPanel.setVisible(false); // Start with menu items hidden
        menuItemsPanel.setAlpha(0f); // Start with 0 alpha
        updateMenuLayout();
    }

    private Icon getIcon(int index) {
        String[] iconNames = {
            "dashboard", "profile", "status", "appointment", "documents", "help"
        };
        if (index < iconNames.length) {
            URL url = getClass().getResource("/sys/menu/" + iconNames[index] + ".png");
            if (url != null) {
                return new ImageIcon(url);
            }
        }
        // Fallback to numeric icons if named icons don't exist
        URL url = getClass().getResource("/sys/menu/" + index + ".png");
        if (url != null) {
            return new ImageIcon(url);
        } else {
            return null;
        }
    }

    private void addMenu(String menuName, int index) {
        int length = menuItems[index].length;
        MenuItem item = new MenuItem(menuName, index, length > 1);
        Icon icon = getIcon(index);
        if (icon != null) {
            item.setIcon(icon);
        }
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // In collapsed mode, clicking any menu item expands the menu
                if (isCollapsed) {
                    toggleMenu();
                    return;
                }
                
                if (length > 1) {
                    if (!item.isSelected()) {
                        item.setSelected(true);
                        addSubMenu(item, index, length, menuItemsPanel.getComponentZOrder(item));
                    } else {
                        // Hide menu
                        hideMenu(item, index);
                        item.setSelected(false);
                        // Reset arrow animation when closing submenu
                        item.setAnimate(0f);
                    }
                } else {
                    if (event != null) {
                        event.selected(index, 0);
                    }
                }
            }
        });
        menuItemsPanel.add(item);
        revalidate();
        repaint();
    }

    private void addSubMenu(MenuItem item, int index, int length, int indexZorder) {
        // Don't show submenu if menu is collapsed
        if (isCollapsed) {
            return;
        }
        
        // First, remove any existing submenu for this index
        hideMenu(item, index);
        
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, inset 0, gapy 0", "fill"));
        panel.setName(index + "");
        panel.setBackground(new Color(18, 63, 99));
        for (int i = 1; i < length; i++) {
            MenuItem subItem = new MenuItem(menuItems[index][i], i, false);
            subItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (event != null) {
                        event.selected(index, subItem.getIndex());
                    }
                }
            });
            subItem.initSubMenu(i, length);
            panel.add(subItem);
        }
        
        // Add the submenu panel to menuItemsPanel with the correct index
        menuItemsPanel.add(panel, "h 0!", indexZorder + 1);
        revalidate();
        repaint();
        MenuAnimation.showMenu(panel, item, ((MigLayout)menuItemsPanel.getLayout()), true);
    }

    private void hideMenu(MenuItem item, int index) {
        for (Component com : menuItemsPanel.getComponents()) {
            if (com instanceof JPanel && com.getName() != null && com.getName().equals(index + "")) {
                com.setName(null);
                MenuAnimation.showMenu(com, item, ((MigLayout)menuItemsPanel.getLayout()), false);
                // Schedule removal of the panel after animation completes
                new Timer(350, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        menuItemsPanel.remove(com);
                        menuItemsPanel.revalidate();
                        menuItemsPanel.repaint();
                        ((Timer)e.getSource()).stop();
                    }
                }).start();
                break;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setColor(new Color(142, 217, 255)); // PSA blue color
        g2.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        super.paintComponent(grphcs);
    }
    
    @Override
    public Dimension getPreferredSize() {
        // Return the actual current size when collapsed/expanded
        if (animating) {
            return super.getPreferredSize();
        }
        return new Dimension(isCollapsed ? collapsedWidth : targetWidth, super.getPreferredSize().height);
    }
}