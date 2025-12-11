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
    private AlphaPanel menuItemsPanel;
    private boolean isCollapsed = true;
    private Animator scrollAnimator;
    private int targetWidth = 200;
    private int collapsedWidth = 42;
    private float scrollAnimate = 0f;
    private boolean animating = false;
    private Timer repaintTimer;
    
    private String[][] menuItems = new String[][]{
        {"Dashboard"},
        {"My Profile"},
        {"ID Status"},
        {"Appointments", "Schedule New", "Upcoming", "Cancel/Reschedule"},
        {"Documents", "Upload Documents", "View Documents", "Document History"},
        {"Help & Support", "FAQs", "Contact Us"},
        {"Logout"}
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
        initRepaintTimer();
    }
    
    private void initRepaintTimer() {
        // Use a timer to batch repaint requests during animation
        repaintTimer = new Timer(16, new ActionListener() { // ~60fps
            @Override
            public void actionPerformed(ActionEvent e) {
                if (animating) {
                    updateMenuWidth();
                }
            }
        });
        repaintTimer.setRepeats(true);
    }
    
    private void initScrollAnimation() {
        scrollAnimator = new Animator(300, new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                scrollAnimate = fraction;
                // Don't update UI here, let the timer handle it
            }
            
            @Override
            public void begin() {
                animating = true;
                repaintTimer.start();
                updateToggleButtons();
                
                // If collapsing, close all submenus
                if (isCollapsed) {
                    collapseAllSubmenus();
                }
            }
            
            @Override
            public void end() {
                animating = false;
                repaintTimer.stop();
                scrollAnimate = isCollapsed ? 0f : 1f;
                updateMenuWidthFinal();
                updateMenuItemsVisibility();
                
                // Force final state after animation
                setPreferredSize(new Dimension(isCollapsed ? collapsedWidth : targetWidth, getHeight()));
                if (toggleListener != null) {
                    toggleListener.onMenuToggle(isCollapsed);
                    toggleListener.onMenuWidthChanged(isCollapsed ? collapsedWidth : targetWidth);
                }
                
                revalidate();
                if (getParent() != null) {
                    getParent().revalidate();
                    getParent().repaint();
                }
            }
        });
        scrollAnimator.setResolution(0); // Let the timer control updates
        scrollAnimator.setAcceleration(0.5f);
        scrollAnimator.setDeceleration(0.5f);
    }
    
    private void collapseAllSubmenus() {
        if (menuItemsPanel != null) {
            // Reset all menu items' selected state and close their submenus
            for (Component comp : menuItemsPanel.getComponents()) {
                if (comp instanceof MenuItem) {
                    MenuItem menuItem = (MenuItem) comp;
                    if (menuItem.isSelected()) {
                        menuItem.setSelected(false);
                        menuItem.setAnimate(0f);
                    }
                } else if (comp instanceof JPanel) {
                    // This is a submenu panel - hide it immediately
                    comp.setVisible(false);
                }
            }
            
            // Remove all submenu panels
            Component[] components = menuItemsPanel.getComponents();
            for (int i = components.length - 1; i >= 0; i--) {
                if (components[i] instanceof JPanel && !(components[i] instanceof AlphaPanel)) {
                    menuItemsPanel.remove(components[i]);
                }
            }
            
            menuItemsPanel.revalidate();
            menuItemsPanel.repaint();
        }
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
        
        // Batch notify listener
        if (toggleListener != null) {
            toggleListener.onMenuWidthChanged(currentWidth);
        }
        
        // Batch revalidation
        revalidate();
        repaint();
        if (getParent() != null) {
            getParent().revalidate();
        }
    }
    
    private void updateMenuWidthFinal() {
        int currentWidth = isCollapsed ? collapsedWidth : targetWidth;
        setPreferredSize(new Dimension(currentWidth, getHeight()));
        
        if (menuItemsPanel != null) {
            float alphaValue = isCollapsed ? 0f : 1f;
            menuItemsPanel.setVisible(!isCollapsed);
            menuItemsPanel.setAlpha(alphaValue);
            
            for (Component comp : menuItemsPanel.getComponents()) {
                if (comp instanceof MenuItem) {
                    ((MenuItem) comp).setAlpha(alphaValue);
                }
            }
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
            return;
        }
        
        isCollapsed = !isCollapsed;
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
        leftToggleButton.repaint();
        rightToggleButton.repaint();
    }
    
    private void init() {
        layout = new MigLayout("wrap 1, fillx, gapy 0, inset 2", "fill");
        setLayout(layout);
        setOpaque(true);
        
        // Create toggle panel at the top with proper height
        togglePanel = new JPanel(new MigLayout("inset 2, gap 0, fill", "[left][grow][right]", ""));
        togglePanel.setOpaque(false);
        togglePanel.setName("togglePanel");
        togglePanel.setPreferredSize(new Dimension(targetWidth, 40));
        
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
        rightToggleButton.setVisible(false);
        
        // Add buttons to toggle panel
        togglePanel.add(leftToggleButton, "left, w 32!, h 32!");
        togglePanel.add(new javax.swing.JLabel(), "grow");
        togglePanel.add(rightToggleButton, "right, w 32!, h 32!");
        
        add(togglePanel, "span, h 40!, wrap");
        
        // Add separator
        add(new javax.swing.JSeparator(), "span, growx, wrap 2");
        
        // Create a panel to hold all menu items
        menuItemsPanel = new AlphaPanel(new MigLayout("wrap 1, fillx, gapy 0, inset 0", "fill"));
        menuItemsPanel.setName("menuItemsPanel");
        
        // Init MenuItem in the menuItemsPanel
        for (int i = 0; i < menuItems.length; i++) {
            addMenu(menuItems[i][0], i);
        }
        
        // Add the menu items panel to the main layout
        add(menuItemsPanel, "span, grow, push, wrap");
        
        // Set initial collapsed state
        setPreferredSize(new Dimension(collapsedWidth, getHeight()));
        menuItemsPanel.setVisible(false);
        menuItemsPanel.setAlpha(0f);
    }

    private Icon getIcon(int index) {
        String[] iconNames = {
            "dashboard", "profile", "status", "appointment", "documents", "help", "logout"
        };
        if (index < iconNames.length) {
            URL url = getClass().getResource("/sys/menu/" + iconNames[index] + ".png");
            if (url != null) {
                return new ImageIcon(url);
            }
        }
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
                if (isCollapsed) {
                    toggleMenu();
                    return;
                }
                
                if (length > 1) {
                    if (!item.isSelected()) {
                        item.setSelected(true);
                        addSubMenu(item, index, length, menuItemsPanel.getComponentZOrder(item));
                    } else {
                        hideMenu(item, index);
                        item.setSelected(false);
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
    }

    private void addSubMenu(MenuItem item, int index, int length, int indexZorder) {
        if (isCollapsed) {
            return;
        }
        
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
        
        menuItemsPanel.add(panel, "h 0!", indexZorder + 1);
        MenuAnimation.showMenu(panel, item, ((MigLayout)menuItemsPanel.getLayout()), true);
    }

    private void hideMenu(MenuItem item, int index) {
        for (Component com : menuItemsPanel.getComponents()) {
            if (com instanceof JPanel && com.getName() != null && com.getName().equals(index + "")) {
                com.setName(null);
                MenuAnimation.showMenu(com, item, ((MigLayout)menuItemsPanel.getLayout()), false);
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
        g2.setColor(new Color(142, 217, 255));
        g2.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        super.paintComponent(grphcs);
    }
    
    @Override
    public Dimension getPreferredSize() {
        if (animating) {
            return super.getPreferredSize();
        }
        return new Dimension(isCollapsed ? collapsedWidth : targetWidth, super.getPreferredSize().height);
    }
}