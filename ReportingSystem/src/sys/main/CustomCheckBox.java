package sys.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomCheckBox extends JPanel {
    private JCheckBox checkBox;
    private JLabel textLabel;
    private JLabel linkLabel;
    private String termsText = "I agree to the ";
    private String linkText = "Terms and Conditions";
    private Color normalColor = new Color(70, 70, 70);
    private Color linkColor = new Color(0, 120, 215);
    private Color hoverColor = new Color(0, 100, 180);
    private Color disabledColor = new Color(180, 180, 180);
    private Color errorColor = new Color(220, 53, 69); // Red color for errors
    private Font textFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font linkFont = new Font("Segoe UI", Font.BOLD, 12);
    private boolean isLinkHovered = false;
    private boolean showError = false;
    
    public CustomCheckBox() {
        initComponents();
    }
    
    public CustomCheckBox(String termsText, String linkText) {
        this.termsText = termsText;
        this.linkText = linkText;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        setBackground(Color.WHITE);
        setOpaque(false);
        
        // Create checkbox with custom icon
        checkBox = new JCheckBox();
        checkBox.setOpaque(false);
        checkBox.setBackground(Color.WHITE);
        checkBox.setFocusPainted(false);
        checkBox.setIcon(new CheckBoxIcon(false));
        checkBox.setSelectedIcon(new CheckBoxIcon(true));
        checkBox.setPressedIcon(new CheckBoxIcon(true));
        checkBox.setRolloverEnabled(false);
        
        // Create text label
        textLabel = new JLabel(termsText);
        textLabel.setFont(textFont);
        textLabel.setForeground(normalColor);
        textLabel.setOpaque(false);
        
        // Create link label
        linkLabel = new JLabel(linkText);
        linkLabel.setFont(linkFont);
        linkLabel.setForeground(linkColor);
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkLabel.setOpaque(false);
        
        // Add mouse listener for link hover effect
        linkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isLinkHovered = true;
                updateLinkAppearance();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isLinkHovered = false;
                updateLinkAppearance();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                fireLinkClicked();
            }
        });
        
        // Add components
        add(checkBox);
        add(textLabel);
        add(linkLabel);
        
        // Set preferred size
        updatePreferredSize();
    }
    
    private void updateLinkAppearance() {
        if (showError) {
            linkLabel.setForeground(errorColor);
            linkLabel.setText(linkText);
        } else if (isLinkHovered) {
            linkLabel.setForeground(hoverColor);
            linkLabel.setText("<html><u>" + linkText + "</u></html>");
        } else {
            linkLabel.setForeground(linkColor);
            linkLabel.setText(linkText);
        }
    }
    
    // Custom checkbox icon class
    private class CheckBoxIcon implements Icon {
        private boolean selected;
        private Color borderColor = new Color(150, 150, 150);
        private Color selectedColor = new Color(0, 120, 215);
        private Color backgroundColor = Color.WHITE;
        private Color errorBorderColor = new Color(220, 53, 69);
        
        public CheckBoxIcon(boolean selected) {
            this.selected = selected;
        }
        
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int size = getIconHeight();
            
            // Draw background
            g2.setColor(backgroundColor);
            g2.fillRoundRect(x, y, size, size, 4, 4);
            
            // Draw border - use error color if error is shown
            Color currentBorderColor = showError ? errorBorderColor : 
                                      (selected ? selectedColor : borderColor);
            g2.setColor(currentBorderColor);
            g2.setStroke(new BasicStroke(selected ? 2.0f : 1.0f));
            g2.drawRoundRect(x, y, size - 1, size - 1, 4, 4);
            
            // Draw checkmark if selected
            if (selected) {
                g2.setColor(selectedColor);
                g2.setStroke(new BasicStroke(2.0f));
                
                // Draw checkmark (√ shape)
                int padding = size / 4;
                int[] xPoints = {x + padding, x + size/3, x + size - padding};
                int[] yPoints = {y + size/2, y + size - padding, y + padding};
                
                for (int i = 0; i < xPoints.length - 1; i++) {
                    g2.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
                }
            }
            
            g2.dispose();
        }
        
        @Override
        public int getIconWidth() {
            return getIconHeight();
        }
        
        @Override
        public int getIconHeight() {
            // Dynamic height based on font size
            FontMetrics fm = textLabel.getFontMetrics(textFont);
            return Math.max(16, fm.getHeight() - 2);
        }
    }
    
    private void updatePreferredSize() {
        FontMetrics textFM = textLabel.getFontMetrics(textFont);
        FontMetrics linkFM = linkLabel.getFontMetrics(linkFont);
        
        int textWidth = textFM.stringWidth(termsText);
        int linkWidth = linkFM.stringWidth(linkText);
        int checkboxWidth = checkBox.getPreferredSize().width;
        int totalWidth = checkboxWidth + textWidth + linkWidth + 20; // + padding
        
        int height = Math.max(
            checkBox.getPreferredSize().height,
            Math.max(textFM.getHeight(), linkFM.getHeight())
        );
        
        setPreferredSize(new Dimension(totalWidth, height));
        revalidate();
        repaint();
    }
    
    // ERROR HANDLING METHODS - ADD THESE
    
    /**
     * Set error state for the checkbox
     * @param error true to show error state, false to clear it
     */
    public void setError(boolean error) {
        this.showError = error;
        updateAppearance();
    }
    
    /**
     * Check if checkbox is in error state
     * @return true if in error state
     */
    public boolean hasError() {
        return showError;
    }
    
    /**
     * Set the color for error state
     * @param color the error color
     */
    public void setErrorColor(Color color) {
        this.errorColor = color;
        if (showError) {
            updateAppearance();
        }
    }
    
    private void updateAppearance() {
        if (showError) {
            textLabel.setForeground(errorColor);
            linkLabel.setForeground(errorColor);
        } else {
            textLabel.setForeground(normalColor);
            updateLinkAppearance();
        }
        checkBox.repaint(); // Repaint to update checkbox border color
        repaint();
    }
    
    // Public methods
    
    public boolean isSelected() {
        return checkBox.isSelected();
    }
    
    public void setSelected(boolean selected) {
        checkBox.setSelected(selected);
        repaint();
    }
    
    public void setTermsText(String text) {
        this.termsText = text;
        textLabel.setText(text);
        updatePreferredSize();
    }
    
    public void setLinkText(String text) {
        this.linkText = text;
        linkLabel.setText(text);
        updatePreferredSize();
    }
    
    public void setTextFont(Font font) {
        this.textFont = font;
        textLabel.setFont(font);
        updatePreferredSize();
    }
    
    public void setLinkFont(Font font) {
        this.linkFont = font;
        linkLabel.setFont(font);
        updatePreferredSize();
    }
    
    public void setNormalColor(Color color) {
        this.normalColor = color;
        if (!showError) {
            textLabel.setForeground(color);
        }
    }
    
    public void setLinkColor(Color color) {
        this.linkColor = color;
        if (!showError && !isLinkHovered) {
            linkLabel.setForeground(color);
        }
    }
    
    public void setHoverColor(Color color) {
        this.hoverColor = color;
    }
    
    public void setDisabledColor(Color color) {
        this.disabledColor = color;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        checkBox.setEnabled(enabled);
        textLabel.setEnabled(enabled);
        linkLabel.setEnabled(enabled);
        
        if (enabled) {
            updateAppearance();
        } else {
            textLabel.setForeground(disabledColor);
            linkLabel.setForeground(disabledColor);
            linkLabel.setCursor(Cursor.getDefaultCursor());
        }
    }
    
    // Event handling
    public void addActionListener(ActionListener listener) {
        checkBox.addActionListener(listener);
    }
    
    public void removeActionListener(ActionListener listener) {
        checkBox.removeActionListener(listener);
    }
    
    public void addLinkClickListener(Runnable listener) {
        // Store link click listeners
        linkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                listener.run();
            }
        });
    }
    
    private void fireLinkClicked() {
        // Fire link clicked event
        // You can extend this to support multiple listeners
    }
    
    // Override to handle resizing
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        adjustTextForWidth(width);
    }
    
    private void adjustTextForWidth(int availableWidth) {
        FontMetrics textFM = textLabel.getFontMetrics(textFont);
        FontMetrics linkFM = linkLabel.getFontMetrics(linkFont);
        
        int checkboxWidth = checkBox.getPreferredSize().width;
        int textWidth = textFM.stringWidth(termsText);
        int linkWidth = linkFM.stringWidth(linkText);
        int totalContentWidth = checkboxWidth + textWidth + linkWidth + 20;
        
        // If content fits, use normal text
        if (totalContentWidth <= availableWidth || availableWidth <= 0) {
            textLabel.setText(termsText);
            linkLabel.setText(linkText);
            return;
        }
        
        // Calculate available space for text
        int availableTextWidth = availableWidth - checkboxWidth - 20;
        
        // Try to fit both text and link
        if (textWidth + linkWidth <= availableTextWidth) {
            textLabel.setText(termsText);
            linkLabel.setText(linkText);
        } 
        // If not, try abbreviating the link text
        else if (textWidth + 30 <= availableTextWidth) { // Minimum link width
            String abbreviatedLink = abbreviateText(linkText, linkFM, availableTextWidth - textWidth - 10);
            textLabel.setText(termsText);
            linkLabel.setText(abbreviatedLink);
        }
        // If still not, abbreviate both
        else {
            // Reserve at least 30px for link
            String abbreviatedText = abbreviateText(termsText, textFM, availableTextWidth - 30);
            String abbreviatedLink = abbreviateText(linkText, linkFM, 30);
            textLabel.setText(abbreviatedText);
            linkLabel.setText(abbreviatedLink);
        }
        
        updatePreferredSize();
    }
    
    private String abbreviateText(String text, FontMetrics fm, int maxWidth) {
        if (fm.stringWidth(text) <= maxWidth) {
            return text;
        }
        
        // Try with ellipsis
        String ellipsis = "...";
        int ellipsisWidth = fm.stringWidth(ellipsis);
        
        if (ellipsisWidth >= maxWidth) {
            return ellipsis;
        }
        
        // Binary search for fitting text
        int low = 0;
        int high = text.length();
        String result = "";
        
        while (low <= high) {
            int mid = (low + high) / 2;
            String test = text.substring(0, mid) + ellipsis;
            int width = fm.stringWidth(test);
            
            if (width <= maxWidth) {
                result = test;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        
        return result.isEmpty() ? ellipsis : result;
    }
    
    // Override to handle font changes
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (textLabel != null && linkLabel != null) {
            Font derivedFont = font.deriveFont(Font.PLAIN, 12);
            textLabel.setFont(derivedFont);
            linkLabel.setFont(derivedFont.deriveFont(Font.BOLD));
            updatePreferredSize();
        }
    }
    
    // Get the underlying checkbox for direct manipulation
    public JCheckBox getCheckBox() {
        return checkBox;
    }
    
    public JLabel getTextLabel() {
        return textLabel;
    }
    
    public JLabel getLinkLabel() {
        return linkLabel;
    }
}