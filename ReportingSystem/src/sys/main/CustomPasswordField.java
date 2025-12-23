package sys.main;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class CustomPasswordField extends JPanel implements FocusListener {
    private JPasswordField passwordField;
    private JToggleButton toggleButton;
    private String placeholder;
    private Color placeholderColor = new Color(150, 150, 150);
    private Color normalColor = Color.BLACK;
    private Color focusedBorderColor = new Color(0, 120, 215);
    private Color unfocusedBorderColor = new Color(200, 200, 200);
    private TitledBorder titledBorder;
    private Timer focusTimer;
    private Timer heightTimer;
    private float borderThickness = 1.0f;
    private float currentHeight = 40.0f; // Starting height
    private float maxHeight = 50.0f;     // Expanded height when focused
    private float minHeight = 40.0f;     // Normal height when not focused
    private boolean isFocused = false;
    private boolean showToggleButton = true; // Flag to control toggle button visibility
    
    public CustomPasswordField() {
        initComponents();
    }
    
    public CustomPasswordField(String placeholder) {
        this.placeholder = placeholder;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(249, 241, 240));
        
        // Create the initial border with empty title
        titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(unfocusedBorderColor, 1),
            ""
        );
        titledBorder.setTitleColor(placeholderColor);
        titledBorder.setTitlePosition(TitledBorder.TOP);
        titledBorder.setTitleJustification(TitledBorder.LEFT);
        // Use smaller font for title
        titledBorder.setTitleFont(new Font("Segoe UI", Font.PLAIN, 10));
        setBorder(titledBorder);
        
        // Create password field with custom painting
        passwordField = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Paint placeholder text if empty and not focused
                if (!isFocused && getPassword().length == 0 && placeholder != null && !placeholder.isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(placeholderColor);
                    g2.setFont(getFont().deriveFont(Font.PLAIN));
                    // Center the placeholder vertically
                    FontMetrics fm = g2.getFontMetrics();
                    int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(placeholder, 5, textY);
                    g2.dispose();
                }
            }
        };
        
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        passwordField.setBackground(new Color(249, 241, 240));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        passwordField.addFocusListener(this);
        passwordField.setEchoChar('•');
        
        // Create toggle button
        toggleButton = new JToggleButton();
        try {
            ImageIcon unseenIcon = new ImageIcon(getClass().getResource("/images/unsee.png"));
            ImageIcon seenIcon = new ImageIcon(getClass().getResource("/images/see.png"));
            // Scale icons to fit better
            unseenIcon = scaleIcon(unseenIcon, 18, 18);
            seenIcon = scaleIcon(seenIcon, 18, 18);
            toggleButton.setIcon(unseenIcon);
            toggleButton.setSelectedIcon(seenIcon);
        } catch (Exception e) {
            toggleButton.setText("👁");
            toggleButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }
        
        toggleButton.setBackground(new Color(249, 241, 240));
        toggleButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        toggleButton.setBorderPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setFocusable(false);
        toggleButton.setFocusPainted(false);
        toggleButton.setRolloverEnabled(false);
        toggleButton.setCursor(Cursor.getDefaultCursor());
        toggleButton.setPreferredSize(new Dimension(30, 30));
        
        // Add action listener for toggle
        toggleButton.addActionListener(e -> {
            if (toggleButton.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        
        // Create panel to hold password field and toggle button
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setBackground(new Color(249, 241, 240));
        fieldPanel.setBorder(null);
        fieldPanel.add(passwordField, BorderLayout.CENTER);
        if (showToggleButton) {
            fieldPanel.add(toggleButton, BorderLayout.EAST);
        }
        
        // Add fieldPanel
        add(fieldPanel, BorderLayout.CENTER);
        
        // Set initial preferred size
        setPreferredSize(new Dimension(300, (int) currentHeight));
        
        // Initialize animation timers
        focusTimer = new Timer(10, e -> animateBorder());
        focusTimer.setRepeats(true);
        
        heightTimer = new Timer(10, e -> animateHeight());
        heightTimer.setRepeats(true);
    }
    
    private ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
    
    public void setBorderColor(Color color) {
        this.focusedBorderColor = color;
        this.unfocusedBorderColor = color;
        if (!isFocused) {
            updateBorder(false);
        } else {
            updateBorder(true);
        }
    }

    public void setBorderColor(Color focusedColor, Color unfocusedColor) {
        this.focusedBorderColor = focusedColor;
        this.unfocusedBorderColor = unfocusedColor;
        updateBorder(isFocused);
    }

    public Color getFocusedBorderColor() {
        return focusedBorderColor;
    }

    public Color getUnfocusedBorderColor() {
        return unfocusedBorderColor;
    }
    
    private void animateBorder() {
        if (isFocused) {
            // Animate border thickness in
            if (borderThickness < 2.0f) {
                borderThickness += 0.2f;
                updateBorder(true);
            } else {
                focusTimer.stop();
            }
        } else {
            // Animate border thickness out
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
    
    private void animateHeight() {
        if (isFocused) {
            // Animate height increase
            if (currentHeight < maxHeight) {
                currentHeight += 0.5f;
                updateSize();
            } else {
                heightTimer.stop();
            }
        } else {
            // Animate height decrease
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
        
        // Notify parent container to adjust layout
        Container parent = getParent();
        if (parent != null) {
            parent.revalidate();
            parent.repaint();
        }
    }
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        // Repaint to show placeholder
        passwordField.repaint();
        // Update border if currently focused
        if (isFocused) {
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
        passwordField.repaint();
    }
    
    public void setFocusedBorderColor(Color color) {
        this.focusedBorderColor = color;
    }
    
    public void setUnfocusedBorderColor(Color color) {
        this.unfocusedBorderColor = color;
        if (!isFocused) {
            updateBorder(false);
        }
    }
    
    public void setExpandedHeight(int height) {
        this.maxHeight = height;
    }
    
    public void setNormalHeight(int height) {
        this.minHeight = height;
        this.currentHeight = height;
        updateSize();
    }
    
    private void updateBorder(boolean focused) {
        if (titledBorder == null) return;
        
        if (focused && placeholder != null && !placeholder.isEmpty()) {
            // Show placeholder in titled border when focused
            titledBorder.setTitle(placeholder);
            titledBorder.setBorder(BorderFactory.createLineBorder(focusedBorderColor, Math.round(borderThickness)));
            titledBorder.setTitleColor(focusedBorderColor);
            titledBorder.setTitleFont(new Font("Segoe UI", Font.PLAIN, 10));
        } else {
            // Hide title and use normal border when not focused
            titledBorder.setTitle("");
            titledBorder.setBorder(BorderFactory.createLineBorder(unfocusedBorderColor, 1));
            titledBorder.setTitleColor(placeholderColor);
        }
        revalidate();
        repaint();
    }
    
    @Override
    public void focusGained(FocusEvent e) {
        isFocused = true;
        borderThickness = 1.0f;
        focusTimer.start();
        heightTimer.start();
        passwordField.repaint(); // Repaint to remove internal placeholder
    }
    
    @Override
    public void focusLost(FocusEvent e) {
        isFocused = false;
        focusTimer.start();
        heightTimer.start();
        passwordField.repaint(); // Repaint to show internal placeholder if empty
    }
    
    public String getPassword() {
        return new String(passwordField.getPassword());
    }
    
    public void setPassword(String password) {
        passwordField.setText(password);
        passwordField.repaint();
    }
    
    public void setText(String text) {
        setPassword(text);
    }
    
    public void clear() {
        setPassword("");
    }
    
    @Override
    public void requestFocus() {
        passwordField.requestFocus();
    }
    
    public JPasswordField getPasswordField() {
        return passwordField;
    }
    
    public JToggleButton getToggleButton() {
        return toggleButton;
    }
    
    public void addActionListener(java.awt.event.ActionListener l) {
        passwordField.addActionListener(l);
    }
    
    public void setEchoChar(char c) {
        passwordField.setEchoChar(c);
    }
    
    public char getEchoChar() {
        return passwordField.getEchoChar();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        toggleButton.setEnabled(enabled);
        if (!enabled) {
            setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 1));
        } else {
            updateBorder(passwordField.hasFocus());
        }
    }
    
    // Add font setting method
    public void setFont(Font font) {
        super.setFont(font);
        if (passwordField != null) {
            passwordField.setFont(font);
        }
        if (titledBorder != null) {
            titledBorder.setTitleFont(font.deriveFont(10f));
        }
    }
    
    // Override to provide custom painting for the panel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Ensure background is painted
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }
    
    // ========== NEW METHODS FOR TOGGLE BUTTON VISIBILITY CONTROL ==========
    
    /**
     * Show the toggle button (default state)
     */
    public void showToggleButton() {
        this.showToggleButton = true;
        updateToggleButtonVisibility();
    }
    
    /**
     * Hide the toggle button
     */
    public void hideToggleButton() {
        this.showToggleButton = false;
        updateToggleButtonVisibility();
    }
    
    /**
     * Set the visibility of the toggle button
     * @param visible true to show the toggle button, false to hide it
     */
    public void setToggleButtonVisible(boolean visible) {
        this.showToggleButton = visible;
        updateToggleButtonVisibility();
    }
    
    /**
     * Check if toggle button is currently visible
     * @return true if toggle button is visible, false otherwise
     */
    public boolean isToggleButtonVisible() {
        return this.showToggleButton;
    }
    
    /**
     * Completely remove the toggle button from the layout
     * This is different from hiding as it removes the component
     */
    public void removeToggleButton() {
        this.showToggleButton = false;
        // Find and remove the toggle button from the layout
        Container fieldPanel = passwordField.getParent();
        if (fieldPanel instanceof JPanel) {
            ((JPanel) fieldPanel).remove(toggleButton);
            fieldPanel.revalidate();
            fieldPanel.repaint();
        }
    }
    
    /**
     * Add back the toggle button if it was removed
     */
    public void addToggleButton() {
        this.showToggleButton = true;
        // Find the field panel and add toggle button back
        Container fieldPanel = passwordField.getParent();
        if (fieldPanel instanceof JPanel) {
            ((JPanel) fieldPanel).add(toggleButton, BorderLayout.EAST);
            fieldPanel.revalidate();
            fieldPanel.repaint();
        }
    }
    
    /**
     * Internal method to update toggle button visibility
     */
    private void updateToggleButtonVisibility() {
        Container fieldPanel = passwordField.getParent();
        if (fieldPanel instanceof JPanel) {
            // Remove existing toggle button if present
            for (Component comp : fieldPanel.getComponents()) {
                if (comp == toggleButton) {
                    fieldPanel.remove(comp);
                    break;
                }
            }
            
            // Add toggle button back if it should be visible
            if (showToggleButton) {
                fieldPanel.add(toggleButton, BorderLayout.EAST);
            }
            
            // Refresh the layout
            fieldPanel.revalidate();
            fieldPanel.repaint();
            revalidate();
            repaint();
        }
    }
    
    /**
     * Convenience method to disable password visibility toggle
     * This hides the toggle button and ensures password is always hidden
     */
    public void disablePasswordVisibilityToggle() {
        setToggleButtonVisible(false);
        passwordField.setEchoChar('•'); // Ensure password is hidden
    }
    
    /**
     * Convenience method to enable password visibility toggle
     * This shows the toggle button and allows password to be shown/hidden
     */
    public void enablePasswordVisibilityToggle() {
        setToggleButtonVisible(true);
        // Reset echo char based on current toggle state
        if (toggleButton.isSelected()) {
            passwordField.setEchoChar((char) 0);
        } else {
            passwordField.setEchoChar('•');
        }
    }
}