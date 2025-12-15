package component.CustomTextArea;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class CustomTextArea extends JPanel implements FocusListener {
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private String placeholder;
    private Color placeholderColor = new Color(150, 150, 150);
    private Color normalColor = Color.BLACK;
    private Color focusedBorderColor = new Color(0, 120, 215);
    private Color unfocusedBorderColor = new Color(200, 200, 200);
    private Color backgroundColor = new Color(249, 241, 240);
    private TitledBorder titledBorder;
    private javax.swing.Timer focusTimer;
    private javax.swing.Timer heightTimer;
    private float borderThickness = 1.0f;
    private float currentHeight = 70.0f; // Taller default for text area
    private float maxHeight = 100.0f;     // Expanded height when focused
    private float minHeight = 70.0f;     // Normal height when not focused
    private boolean isFocused = false;
    
    public CustomTextArea() {
        initComponents();
    }
    
    public CustomTextArea(String placeholder) {
        this.placeholder = placeholder;
        initComponents();
    }
    
    public CustomTextArea(String placeholder, int rows, int columns) {
        this.placeholder = placeholder;
        initComponents();
        textArea.setRows(rows);
        textArea.setColumns(columns);
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
        
        // Create text area with custom painting for placeholder
        textArea = new JTextArea() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Paint placeholder text if empty and not focused
                if (!isFocused && getText().isEmpty() && placeholder != null && !placeholder.isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(placeholderColor);
                    g2.setFont(getFont().deriveFont(Font.PLAIN));
                    
                    // Draw placeholder with some padding
                    FontMetrics fm = g2.getFontMetrics();
                    Insets insets = getInsets();
                    int x = insets.left + 5;
                    int y = insets.top + fm.getAscent() + 5;
                    
                    // Split placeholder into multiple lines if needed
                    String[] lines = splitTextToLines(placeholder, fm, getWidth() - x - insets.right - 10);
                    for (String line : lines) {
                        g2.drawString(line, x, y);
                        y += fm.getHeight();
                    }
                    
                    g2.dispose();
                }
            }
        };
        
        textArea.setBackground(backgroundColor);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setForeground(normalColor);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        textArea.addFocusListener(this);
        textArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        // Create scroll pane
        scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(backgroundColor);
        scrollPane.getViewport().setBackground(backgroundColor);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Style the scroll bars
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setBackground(backgroundColor);
        verticalScrollBar.setBorder(BorderFactory.createEmptyBorder());
        verticalScrollBar.setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Set initial preferred size
        setPreferredSize(new Dimension(300, (int) currentHeight));
        
        // Initialize animation timers - EXACTLY like CustomTextField
        focusTimer = new javax.swing.Timer(10, e -> animateBorder());
        focusTimer.setRepeats(true);
        
        heightTimer = new javax.swing.Timer(10, e -> animateHeight());
        heightTimer.setRepeats(true);
    }
    
    private String[] splitTextToLines(String text, FontMetrics fm, int maxWidth) {
        if (fm.stringWidth(text) <= maxWidth) {
            return new String[]{text};
        }
        
        // Split into words
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        java.util.List<String> lines = new java.util.ArrayList<>();
        
        for (String word : words) {
            String testLine = currentLine.length() > 0 ? 
                currentLine.toString() + " " + word : word;
            
            if (fm.stringWidth(testLine) <= maxWidth) {
                currentLine.append(currentLine.length() > 0 ? " " + word : word);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                }
                currentLine = new StringBuilder(word);
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines.toArray(new String[0]);
    }
    
    private void animateBorder() {
        if (isFocused) {
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
    
    private void animateHeight() {
        if (isFocused) {
            // Animate height increase
            if (currentHeight < maxHeight) {
                currentHeight += 1.0f; // Faster animation for larger component
                updateSize();
            } else {
                heightTimer.stop();
            }
        } else {
            // Animate height decrease
            if (currentHeight > minHeight) {
                currentHeight -= 1.0f;
                updateSize();
            } else {
                heightTimer.stop();
            }
        }
    }
    
    private void updateSize() {
        setPreferredSize(new Dimension(getWidth(), (int) currentHeight));
        revalidate();
        repaint();
        
        // Notify parent container to adjust layout
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
    
    // ========== PUBLIC METHODS ==========
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        textArea.repaint();
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
        textArea.repaint();
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
    
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        setBackground(color);
        textArea.setBackground(color);
        scrollPane.setBackground(color);
        scrollPane.getViewport().setBackground(color);
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
    
    public void setText(String text) {
        textArea.setText(text);
        textArea.setForeground(normalColor);
        textArea.repaint();
    }
    
    public String getText() {
        return textArea.getText();
    }
    
    public void clear() {
        setText("");
    }
    
    public void append(String text) {
        textArea.append(text);
    }
    
    public void setRows(int rows) {
        textArea.setRows(rows);
    }
    
    public void setColumns(int columns) {
        textArea.setColumns(columns);
    }
    
    public void setLineWrap(boolean wrap) {
        textArea.setLineWrap(wrap);
    }
    
    public void setWrapStyleWord(boolean wrap) {
        textArea.setWrapStyleWord(wrap);
    }
    
    public void setEditable(boolean editable) {
        textArea.setEditable(editable);
    }
    
    public boolean isEditable() {
        return textArea.isEditable();
    }
    
    @Override
    public void focusGained(FocusEvent e) {
        isFocused = true;
        borderThickness = 1.0f;
        focusTimer.start();
        heightTimer.start();
        textArea.repaint();
    }
    
    @Override
    public void focusLost(FocusEvent e) {
        isFocused = false;
        focusTimer.start();
        heightTimer.start();
        textArea.repaint();
    }
    
    @Override
    public void requestFocus() {
        textArea.requestFocus();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textArea.setEnabled(enabled);
        scrollPane.setEnabled(enabled);
        if (!enabled) {
            setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            textArea.setForeground(new Color(150, 150, 150));
        } else {
            updateBorder(textArea.hasFocus());
            textArea.setForeground(normalColor);
        }
    }
    
    public void setFont(Font font) {
        super.setFont(font);
        if (textArea != null) {
            textArea.setFont(font);
        }
        if (titledBorder != null) {
            titledBorder.setTitleFont(font.deriveFont(10f));
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Ensure background is painted
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }
    
    // ========== SCROLLBAR CUSTOMIZATION ==========
    
    public void setScrollBarColor(Color color) {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setBackground(color);
        verticalScrollBar.setForeground(color);
    }
    
    public void setScrollBarThumbColor(Color color) {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        
        // Custom scroll bar UI
        verticalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = color;
                this.trackColor = backgroundColor;
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
            
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                    return;
                }
                
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(thumbColor);
                g2.fillRoundRect(
                    thumbBounds.x + 2, 
                    thumbBounds.y + 2, 
                    thumbBounds.width - 4, 
                    thumbBounds.height - 4, 
                    8, 
                    8
                );
                
                g2.dispose();
            }
            
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(trackColor);
                g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                
                g2.dispose();
            }
        });
    }
    
    // ========== UTILITY METHODS ==========
    
    public JTextArea getTextArea() {
        return textArea;
    }
    
    public JScrollPane getScrollPane() {
        return scrollPane;
    }
    
    public int getLineCount() {
        return textArea.getLineCount();
    }
    
    public void setMaxLength(int maxLength) {
        // You can implement document filter for max length if needed
        ((javax.swing.text.AbstractDocument) textArea.getDocument()).setDocumentFilter(
            new javax.swing.text.DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String string, 
                                        javax.swing.text.AttributeSet attr) 
                        throws javax.swing.text.BadLocationException {
                    if (string == null) return;
                    
                    String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                    int newLength = currentText.length() + string.length();
                    
                    if (newLength <= maxLength) {
                        super.insertString(fb, offset, string, attr);
                    } else {
                        int available = maxLength - currentText.length();
                        if (available > 0) {
                            super.insertString(fb, offset, string.substring(0, available), attr);
                        }
                    }
                }
                
                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, 
                                   javax.swing.text.AttributeSet attrs) 
                        throws javax.swing.text.BadLocationException {
                    if (text == null) {
                        super.replace(fb, offset, length, text, attrs);
                        return;
                    }
                    
                    String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                    int newLength = currentText.length() - length + text.length();
                    
                    if (newLength <= maxLength) {
                        super.replace(fb, offset, length, text, attrs);
                    } else {
                        int available = maxLength - (currentText.length() - length);
                        if (available > 0) {
                            super.replace(fb, offset, length, text.substring(0, available), attrs);
                        }
                    }
                }
            }
        );
    }
}