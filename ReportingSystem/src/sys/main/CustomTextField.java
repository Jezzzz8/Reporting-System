package sys.main;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class CustomTextField extends JPanel implements FocusListener {
    private JTextField textField;
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
    private float maxHeight = 46.0f;     // Expanded height when focused
    private float minHeight = 40.0f;     // Normal height when not focused
    private boolean isFocused = false;
    
    public CustomTextField() {
        initComponents();
    }
    
    public CustomTextField(String placeholder) {
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
        
        // Create text field with padding
        textField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Paint placeholder text if empty and not focused
                if (!isFocused && getText().isEmpty() && placeholder != null && !placeholder.isEmpty()) {
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
        
        textField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textField.setBackground(new Color(249, 241, 240));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        textField.addFocusListener(this);
        
        // Add text field
        add(textField, BorderLayout.CENTER);
        
        // Set initial preferred size
        setPreferredSize(new Dimension(300, (int) currentHeight));
        
        // Initialize animation timers
        focusTimer = new Timer(10, e -> animateBorder());
        focusTimer.setRepeats(true);
        
        heightTimer = new Timer(10, e -> animateHeight());
        heightTimer.setRepeats(true);
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
        textField.repaint();
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
        textField.repaint();
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
        textField.repaint(); // Repaint to remove internal placeholder
    }
    
    @Override
    public void focusLost(FocusEvent e) {
        isFocused = false;
        focusTimer.start();
        heightTimer.start();
        textField.repaint(); // Repaint to show internal placeholder if empty
    }
    
    public String getText() {
        return textField.getText();
    }
    
    public void setText(String text) {
        textField.setText(text);
        textField.setForeground(normalColor);
        textField.repaint();
    }
    
    public void clear() {
        setText("");
    }
    
    @Override
    public void requestFocus() {
        textField.requestFocus();
    }
    
    public JTextField getTextField() {
        return textField;
    }
    
    public void addActionListener(java.awt.event.ActionListener l) {
        textField.addActionListener(l);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
        if (!enabled) {
            setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        } else {
            updateBorder(textField.hasFocus());
        }
    }
    
    // Add this method to set the font of the text field
    public void setFont(Font font) {
        super.setFont(font);
        if (textField != null) {
            textField.setFont(font);
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
}