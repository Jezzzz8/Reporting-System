package component.DropdownButton;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;

public class CustomDropdownButton extends JPanel implements FocusListener {
    private JButton dropdownButton;
    private JPopupMenu dropdownMenu;
    private Color placeholderColor = new Color(150, 150, 150);
    private Color normalColor = Color.BLACK;
    private Color focusedBorderColor = new Color(0, 120, 215);
    private Color unfocusedBorderColor = new Color(200, 200, 200);
    private Color backgroundColor = new Color(249, 241, 240);
    private Color hoverColor = new Color(0, 120, 215, 30);
    private TitledBorder titledBorder;
    private javax.swing.Timer focusTimer;
    private javax.swing.Timer heightTimer;
    private float borderThickness = 1.0f;
    private float currentHeight = 40.0f;
    private float maxHeight = 50.0f;
    private float minHeight = 40.0f;
    private boolean isFocused = false;
    private boolean isOpen = false;
    private Animator glowAnimator;
    private float glowAlpha = 0.0f;
    private DropdownContent currentContent;
    private List<DropdownContent> contentList = new ArrayList<>();
    private String placeholder = "Select option";
    
    // Content types
    public enum ContentType {
        NOTIFICATIONS,
        CALENDAR,
        QUICK_ACTIONS,
        CUSTOM
    }
    
    // Interface for dropdown content
    public interface DropdownContent {
        Component getContent();
        String getTitle();
        ContentType getType();
    }
    
    public CustomDropdownButton() {
        super();
        initComponents();
    }
    
    // Changed: Only one constructor with String parameter
    public CustomDropdownButton(String textOrPlaceholder) {
        super();
        // If it looks like a placeholder (starts with lowercase or contains "Select")
        if (textOrPlaceholder.toLowerCase().contains("select") || 
            Character.isLowerCase(textOrPlaceholder.charAt(0))) {
            this.placeholder = textOrPlaceholder;
        } else {
            setText(textOrPlaceholder);
        }
        initComponents();
    }
    
    // New constructor for both text and placeholder
    public CustomDropdownButton(String text, String placeholder) {
        super();
        setText(text);
        this.placeholder = placeholder;
        initComponents();
    }
    
    public CustomDropdownButton(String text, String placeholder, Icon icon) {
        super();
        setText(text);
        this.placeholder = placeholder;
        initComponents();
        dropdownButton.setIcon(icon);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(backgroundColor);

        // Create the initial border with empty title - EXACTLY like CustomTextField
        titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(unfocusedBorderColor, 1),
            ""
        );
        titledBorder.setTitleColor(placeholderColor);
        titledBorder.setTitlePosition(TitledBorder.TOP);
        titledBorder.setTitleJustification(TitledBorder.LEFT);
        titledBorder.setTitleFont(new Font("Segoe UI", Font.PLAIN, 10));
        setBorder(titledBorder);

        // Create inner button
        dropdownButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Paint placeholder text if empty and not focused - EXACTLY like CustomTextField
                String buttonText = getText();
                if (!isFocused && !isOpen && (buttonText == null || buttonText.isEmpty()) && 
                    placeholder != null && !placeholder.isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(placeholderColor);
                    g2.setFont(getFont().deriveFont(Font.PLAIN));

                    FontMetrics fm = g2.getFontMetrics();
                    int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

                    g2.drawString(placeholder, 5, textY);
                    g2.dispose();
                }
            }
        };

        dropdownButton.setLayout(new BorderLayout());
        dropdownButton.setBackground(backgroundColor);
        dropdownButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        dropdownButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dropdownButton.setFocusPainted(false);
        dropdownButton.setContentAreaFilled(false);
        dropdownButton.setOpaque(false);
        dropdownButton.setHorizontalAlignment(SwingConstants.LEFT);
        dropdownButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add mouse listener for hover effect
        dropdownButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isOpen) {
                    setBackground(new Color(
                        backgroundColor.getRed(),
                        backgroundColor.getGreen(),
                        backgroundColor.getBlue(),
                        200
                    ));
                    startGlowAnimation();
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isOpen) {
                    setBackground(backgroundColor);
                    stopGlowAnimation();
                    repaint();
                }
            }
        });

        // Create dropdown menu with matching style
        dropdownMenu = new JPopupMenu() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background matching the text field
                g2.setColor(backgroundColor);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Border similar to text field when focused
                g2.setColor(focusedBorderColor);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        dropdownMenu.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Setup focus animation timers - EXACTLY like CustomTextField
        focusTimer = new javax.swing.Timer(10, e -> animateBorder());
        focusTimer.setRepeats(true);

        heightTimer = new javax.swing.Timer(10, e -> animateHeight());
        heightTimer.setRepeats(true);

        // Setup glow animation for hover
        setupGlowAnimation();

        // Add mouse listener to the entire panel
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Check if click is on the arrow icon area (right side)
                int clickX = e.getX();
                int arrowStartX = getWidth() - 30; // Arrow area starts 30px from right

                if (clickX >= arrowStartX) {
                    // Clicked on arrow icon - toggle dropdown immediately
                    requestFocusInWindow();
                    toggleDropdown();
                } else {
                    // Clicked on text area - just focus
                    requestFocusInWindow();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isOpen) {
                    startGlowAnimation();
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isOpen) {
                    stopGlowAnimation();
                    repaint();
                }
            }
        });

        // Add focus listener
        addFocusListener(this);

        // Also make the button clickable
        dropdownButton.addActionListener(e -> {
            // Check if click is on arrow area
            Point mousePoint = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(mousePoint, dropdownButton);
            int clickX = mousePoint.x;
            int arrowStartX = dropdownButton.getWidth() - 30;

            if (clickX >= arrowStartX) {
                // Clicked on arrow - toggle dropdown
                requestFocusInWindow();
                toggleDropdown();
            } else {
                // Clicked on text - just focus
                requestFocusInWindow();
            }
        });

        // Close dropdown when clicking outside
        addHierarchyListener(e -> {
            if (dropdownMenu.isVisible()) {
                Window window = SwingUtilities.getWindowAncestor(CustomDropdownButton.this);
                if (window != null) {
                    window.addWindowFocusListener(new WindowAdapter() {
                        @Override
                        public void windowLostFocus(WindowEvent e) {
                            closeDropdown();
                        }
                    });
                }
            }
        });

        // Add the button to the panel
        add(dropdownButton, BorderLayout.CENTER);

        // Set initial preferred size - EXACTLY like CustomTextField
        setPreferredSize(new Dimension(300, (int) currentHeight));
    }

    public void setOptions(String[] options) {
        // Clear existing content
        contentList.clear();
        dropdownMenu.removeAll();

        // Create a simple content panel with the options
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(backgroundColor);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Add each option as a button
        for (String option : options) {
            JButton optionButton = new JButton(option);
            optionButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            optionButton.setHorizontalAlignment(SwingConstants.LEFT);
            optionButton.setBackground(backgroundColor);
            optionButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            optionButton.setFocusPainted(false);
            optionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            optionButton.addActionListener(e -> {
                setText(option);
                closeDropdown();
            });

            optionButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    optionButton.setBackground(hoverColor);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    optionButton.setBackground(backgroundColor);
                }
            });

            optionsPanel.add(optionButton);
            if (!option.equals(options[options.length - 1])) {
                optionsPanel.add(new JSeparator());
            }
        }

        // Add the options panel to the dropdown menu
        dropdownMenu.add(optionsPanel);
        dropdownMenu.pack();
    }
    
    private void setupGlowAnimation() {
        glowAnimator = new Animator(800, new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                glowAlpha = 0.1f + 0.1f * (float) Math.sin(fraction * Math.PI * 4);
                repaint();
            }
        });
        glowAnimator.setRepeatCount(Animator.INFINITE);
    }
    
    private void startGlowAnimation() {
        if (glowAnimator != null && !glowAnimator.isRunning()) {
            glowAnimator.start();
        }
    }
    
    private void stopGlowAnimation() {
        if (glowAnimator != null && glowAnimator.isRunning()) {
            glowAnimator.stop();
            glowAlpha = 0.0f;
            repaint();
        }
    }
    
    private void animateBorder() {
        if (isFocused || isOpen) {
            // Animate border thickness in - EXACTLY like CustomTextField
            if (borderThickness < 2.0f) {
                borderThickness += 0.2f;
                updateBorder(true);
            } else {
                focusTimer.stop();
            }
        } else {
            // Animate border thickness out - EXACTLY like CustomTextField
            if (borderThickness > 1.0f) {
                borderThickness -= 0.2f;
                if (borderThickness <= 1.0f) {
                    borderThickness = 1.0f;
                    updateBorder(false);
                    focusTimer.stop();
                } else {
                    updateBorder(false);
                }
            }
        }
    }
    
    private boolean isClickOnArrowArea(int x) {
        int arrowStartX = getWidth() - 30; // Arrow area is last 30px
        return x >= arrowStartX;
    }
    
    private void animateHeight() {
        if (isFocused || isOpen) {
            // Animate height increase - EXACTLY like CustomTextField
            if (currentHeight < maxHeight) {
                currentHeight += 0.5f;
                updateSize();
            } else {
                heightTimer.stop();
            }
        } else {
            // Animate height decrease - EXACTLY like CustomTextField
            if (currentHeight > minHeight) {
                currentHeight -= 0.5f;
                updateSize();
            } else {
                heightTimer.stop();
            }
        }
    }
    
    private void updateSize() {
        setPreferredSize(new Dimension(300, (int) currentHeight));
        revalidate();
        repaint();
        
        // Notify parent container to adjust layout - EXACTLY like CustomTextField
        Container parent = getParent();
        if (parent != null) {
            parent.revalidate();
            parent.repaint();
        }
    }
    
    private void updateBorder(boolean focused) {
        if (titledBorder == null) return;
        
        if (focused && placeholder != null && !placeholder.isEmpty()) {
            // Show placeholder in titled border when focused - EXACTLY like CustomTextField
            titledBorder.setTitle(placeholder);
            titledBorder.setBorder(BorderFactory.createLineBorder(focusedBorderColor, Math.round(borderThickness)));
            titledBorder.setTitleColor(focusedBorderColor);
            titledBorder.setTitleFont(new Font("Segoe UI", Font.PLAIN, 10));
        } else {
            // Hide title and use normal border when not focused - EXACTLY like CustomTextField
            titledBorder.setTitle("");
            titledBorder.setBorder(BorderFactory.createLineBorder(unfocusedBorderColor, 1));
            titledBorder.setTitleColor(placeholderColor);
        }
        revalidate();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fill background - EXACTLY like CustomTextField
        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Add hover glow effect
        if (glowAlpha > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, glowAlpha));
            g2.setColor(hoverColor);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setComposite(AlphaComposite.SrcOver);
        }
        
        g2.dispose();
        
        // Draw dropdown arrow
        drawDropdownArrow(g);
    }
    
    private void drawDropdownArrow(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arrowSize = 8;
        int x = getWidth() - arrowSize - 10;
        int y = getHeight() / 2;

        // Draw a subtle background for the arrow area
        if (isOpen || isFocused) {
            g2.setColor(new Color(focusedBorderColor.getRed(), focusedBorderColor.getGreen(), focusedBorderColor.getBlue(), 20));
            g2.fillRect(getWidth() - 40, 0, 40, getHeight());
        }

        // Use focused color when dropdown is open or focused
        if (isOpen || isFocused) {
            g2.setColor(focusedBorderColor);
        } else {
            g2.setColor(placeholderColor);
        }

        if (isOpen) {
            // Draw upward arrow
            int[] xPoints = {x, x + arrowSize, x + arrowSize / 2};
            int[] yPoints = {y + arrowSize / 2, y + arrowSize / 2, y - arrowSize / 2};
            g2.fillPolygon(xPoints, yPoints, 3);
        } else {
            // Draw downward arrow
            int[] xPoints = {x, x + arrowSize, x + arrowSize / 2};
            int[] yPoints = {y - arrowSize / 2, y - arrowSize / 2, y + arrowSize / 2};
            g2.fillPolygon(xPoints, yPoints, 3);
        }

        g2.dispose();
    }

    // ========== PUBLIC METHODS ==========
    
    public void setText(String text) {
        dropdownButton.setText(text);
        dropdownButton.repaint();
    }
    
    public String getText() {
        return dropdownButton.getText();
    }
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        dropdownButton.repaint();
        if (isFocused || isOpen) {
            updateBorder(true);
        }
    }
    
    public String getPlaceholder() {
        return placeholder;
    }
    
    public void setPlaceholderColor(Color color) {
        this.placeholderColor = color;
        if (titledBorder != null) {
            titledBorder.setTitleColor(placeholderColor);
            repaint();
        }
        dropdownButton.repaint();
    }
    
    public void setFocusedBorderColor(Color color) {
        this.focusedBorderColor = color;
    }
    
    public void setUnfocusedBorderColor(Color color) {
        this.unfocusedBorderColor = color;
        if (!isFocused && !isOpen) {
            updateBorder(false);
        }
    }
    
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        setBackground(color);
        dropdownButton.setBackground(color);
        repaint();
    }
    
    public void setHoverColor(Color color) {
        this.hoverColor = color;
        repaint();
    }
    
    public void setExpandedHeight(int height) {
        this.maxHeight = height;
    }
    
    public void setNormalHeight(int height) {
        this.minHeight = height;
        this.currentHeight = height;
        updateSize();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        dropdownButton.setEnabled(enabled);
        if (!enabled) {
            setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            dropdownButton.setForeground(new Color(150, 150, 150));
        } else {
            updateBorder(isFocused || isOpen);
            dropdownButton.setForeground(normalColor);
        }
        repaint();
    }
    
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (dropdownButton != null) {
            dropdownButton.setFont(font);
        }
        if (titledBorder != null) {
            titledBorder.setTitleFont(font.deriveFont(10f));
        }
    }
    
    @Override
    public void focusGained(FocusEvent e) {
        isFocused = true;
        borderThickness = 1.0f;
        focusTimer.start();
        heightTimer.start();
        repaint();
    }
    
    @Override
    public void focusLost(FocusEvent e) {
        isFocused = false;
        focusTimer.start();
        heightTimer.start();
        repaint();
    }
    
    @Override
    public void requestFocus() {
        super.requestFocus();
    }
    
    // ========== DROPDOWN METHODS ==========
    
    public void addContent(DropdownContent content) {
        contentList.add(content);
        if (currentContent == null) {
            currentContent = content;
            updateButtonIcon(content);
        }
    }
    
    public void setCurrentContent(ContentType contentType) {
        for (DropdownContent content : contentList) {
            if (content.getType() == contentType) {
                currentContent = content;
                updateButtonIcon(content);
                updateDropdownContent();
                break;
            }
        }
    }
    
    private void updateButtonIcon(DropdownContent content) {
        switch (content.getType()) {
            case NOTIFICATIONS:
                dropdownButton.setIcon(createNotificationIcon());
                break;
            case CALENDAR:
                dropdownButton.setIcon(createCalendarIcon());
                break;
            case QUICK_ACTIONS:
                dropdownButton.setIcon(createQuickActionsIcon());
                break;
            default:
                // Keep current icon
                break;
        }
    }
    
    private Icon createNotificationIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(focusedBorderColor);
                g2.setStroke(new BasicStroke(2f));
                
                g2.drawArc(x + 2, y + 2, 12, 10, 0, 180);
                g2.drawLine(x + 8, y + 7, x + 8, y + 12);
                g2.fillOval(x + 7, y + 12, 2, 2);
                
                g2.dispose();
            }
            
            @Override
            public int getIconWidth() { return 16; }
            
            @Override
            public int getIconHeight() { return 16; }
        };
    }
    
    private Icon createCalendarIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(focusedBorderColor);
                g2.setStroke(new BasicStroke(2f));
                
                g2.drawRect(x + 2, y + 6, 12, 10);
                g2.fillRect(x + 2, y + 2, 12, 4);
                g2.drawLine(x + 6, y + 6, x + 6, y + 16);
                g2.drawLine(x + 10, y + 6, x + 10, y + 16);
                
                g2.dispose();
            }
            
            @Override
            public int getIconWidth() { return 16; }
            
            @Override
            public int getIconHeight() { return 16; }
        };
    }
    
    private Icon createQuickActionsIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(focusedBorderColor);
                g2.fillOval(x + 3, y + 5, 3, 3);
                g2.fillOval(x + 7, y + 5, 3, 3);
                g2.fillOval(x + 11, y + 5, 3, 3);
                
                g2.dispose();
            }
            
            @Override
            public int getIconWidth() { return 16; }
            
            @Override
            public int getIconHeight() { return 16; }
        };
    }
    
    private void updateDropdownContent() {
        dropdownMenu.removeAll();
        
        if (currentContent != null) {
            JLabel titleLabel = new JLabel(currentContent.getTitle());
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            titleLabel.setForeground(focusedBorderColor);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            dropdownMenu.add(titleLabel);
            
            JSeparator separator = new JSeparator();
            separator.setForeground(unfocusedBorderColor);
            dropdownMenu.add(separator);
            
            dropdownMenu.add(currentContent.getContent());
            
            if (contentList.size() > 1) {
                dropdownMenu.add(new JSeparator());
                JPanel switcherPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
                switcherPanel.setBackground(backgroundColor);
                
                for (DropdownContent content : contentList) {
                    JButton switchBtn = new JButton(getContentIcon(content.getType()));
                    switchBtn.setToolTipText(content.getTitle());
                    switchBtn.setPreferredSize(new Dimension(30, 30));
                    switchBtn.setBackground(backgroundColor);
                    switchBtn.setFocusPainted(false);
                    switchBtn.setBorder(BorderFactory.createLineBorder(unfocusedBorderColor, 1));
                    switchBtn.addActionListener(e -> {
                        currentContent = content;
                        updateButtonIcon(content);
                        updateDropdownContent();
                        dropdownMenu.pack();
                    });
                    switcherPanel.add(switchBtn);
                }
                dropdownMenu.add(switcherPanel);
            }
        }
        
        dropdownMenu.pack();
    }
    
    private Icon getContentIcon(ContentType type) {
        switch (type) {
            case NOTIFICATIONS:
                return createSmallNotificationIcon();
            case CALENDAR:
                return createSmallCalendarIcon();
            case QUICK_ACTIONS:
                return createSmallQuickActionsIcon();
            default:
                return null;
        }
    }
    
    private Icon createSmallNotificationIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(focusedBorderColor);
                g2.fillOval(x + 1, y + 1, 8, 8);
                g2.dispose();
            }
            
            @Override
            public int getIconWidth() { return 10; }
            
            @Override
            public int getIconHeight() { return 10; }
        };
    }
    
    private Icon createSmallCalendarIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(focusedBorderColor);
                g2.fillRect(x + 1, y + 1, 8, 8);
                g2.dispose();
            }
            
            @Override
            public int getIconWidth() { return 10; }
            
            @Override
            public int getIconHeight() { return 10; }
        };
    }
    
    private Icon createSmallQuickActionsIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(focusedBorderColor);
                g2.fillOval(x + 1, y + 1, 2, 2);
                g2.fillOval(x + 4, y + 1, 2, 2);
                g2.fillOval(x + 7, y + 1, 2, 2);
                g2.dispose();
            }
            
            @Override
            public int getIconWidth() { return 10; }
            
            @Override
            public int getIconHeight() { return 10; }
        };
    }
    
    private void toggleDropdown() {
        if (isOpen) {
            closeDropdown();
        } else {
            // Force focus before opening
            if (!isFocused) {
                isFocused = true;
                borderThickness = 1.0f;
                focusTimer.start();
                heightTimer.start();
            }
            openDropdown();
        }
    }
    
    private void openDropdown() {
        if (currentContent == null && !contentList.isEmpty()) {
            currentContent = contentList.get(0);
            updateButtonIcon(currentContent);
        }

        updateDropdownContent();
        dropdownMenu.show(this, 0, getHeight());
        isOpen = true;
        isFocused = true;
        borderThickness = 1.0f;
        focusTimer.start();
        heightTimer.start();
        startGlowAnimation();
        repaint();
    }
    
    private void closeDropdown() {
        dropdownMenu.setVisible(false);
        isOpen = false;
        // Don't reset focus here - keep focus state
        focusTimer.start();
        heightTimer.start();
        stopGlowAnimation();
        repaint();
    }

    // Getters and setters
    public boolean isDropdownOpen() {
        return isOpen;
    }
    
    public List<DropdownContent> getContentList() {
        return contentList;
    }
    
    public DropdownContent getCurrentContent() {
        return currentContent;
    }
    
    public JButton getButton() {
        return dropdownButton;
    }
}
