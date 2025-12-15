package sys.main;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CustomTextField extends JPanel implements FocusListener {
    private JTextField textField;
    private String placeholder;
    private Color placeholderColor = new Color(150, 150, 150);
    private Color normalColor = Color.BLACK;
    private Color focusedBorderColor = new Color(0, 120, 215);
    private Color unfocusedBorderColor = new Color(200, 200, 200);
    private Color formatGuideColor = new Color(180, 180, 180, 150);
    private TitledBorder titledBorder;
    private Timer focusTimer;
    private Timer heightTimer;
    private float borderThickness = 1.0f;
    private float currentHeight = 40.0f;
    private float maxHeight = 50.0f;
    private float minHeight = 40.0f;
    private boolean isFocused = false;
    private boolean showFormatGuide = false;
    private String formatPattern = "";
    private String formatDelimiter = "-";
    private int[] formatSegments;
    private boolean isAutoInserting = false; // Flag to prevent recursive calls
    private int maxLength = -1; // -1 means no limit
    
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
        
        titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(unfocusedBorderColor, 1),
            ""
        );
        titledBorder.setTitleColor(placeholderColor);
        titledBorder.setTitlePosition(TitledBorder.TOP);
        titledBorder.setTitleJustification(TitledBorder.LEFT);
        titledBorder.setTitleFont(new Font("Segoe UI", Font.PLAIN, 10));
        setBorder(titledBorder);
        
        textField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                String currentText = getText();
                boolean isEmpty = currentText.isEmpty();
                
                if (showFormatGuide && isFocused && !isFormatComplete(currentText)) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(formatGuideColor);
                    g2.setFont(getFont().deriveFont(Font.PLAIN));
                    
                    FontMetrics fm = g2.getFontMetrics();
                    int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    
                    String guideText = buildFormatGuide(currentText);
                    g2.drawString(guideText, 5, textY);
                    g2.dispose();
                }
                else if (!isFocused && isEmpty && placeholder != null && !placeholder.isEmpty()) {
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
        
        textField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textField.setBackground(new Color(249, 241, 240));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        textField.addFocusListener(this);
        
        // Use a DocumentFilter for auto-formatting and length limiting
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new FormatDocumentFilter());
        
        add(textField, BorderLayout.CENTER);
        setPreferredSize(new Dimension(300, (int) currentHeight));
        
        focusTimer = new Timer(10, e -> animateBorder());
        focusTimer.setRepeats(true);
        
        heightTimer = new Timer(10, e -> animateHeight());
        heightTimer.setRepeats(true);
    }
    
    // ========== FORMAT GUIDE FUNCTIONS ==========
    
    public void enableFormatGuide(String pattern) {
        this.showFormatGuide = true;
        this.formatPattern = pattern;
        parseFormatPattern(pattern);
        textField.repaint();
    }
    
    public void disableFormatGuide() {
        this.showFormatGuide = false;
        this.formatPattern = "";
        this.formatSegments = null;
        textField.repaint();
    }
    
    private void parseFormatPattern(String pattern) {
        String[] segments = pattern.split("[^X]");
        formatSegments = new int[segments.length];
        for (int i = 0; i < segments.length; i++) {
            formatSegments[i] = segments[i].length();
        }
        
        if (pattern.length() > 0 && !pattern.startsWith("X")) {
            formatDelimiter = pattern.substring(0, 1);
        }
    }
    
    private String buildFormatGuide(String currentText) {
        if (formatSegments == null || formatPattern.isEmpty()) {
            return "";
        }
        
        StringBuilder guide = new StringBuilder();
        int charIndex = 0;
        String cleanText = currentText.replace(formatDelimiter, "");
        
        for (int i = 0; i < formatSegments.length; i++) {
            if (i > 0) {
                guide.append(formatDelimiter);
            }
            
            for (int j = 0; j < formatSegments[i]; j++) {
                if (charIndex < cleanText.length()) {
                    guide.append(" ");
                } else {
                    guide.append("X");
                }
                charIndex++;
            }
        }
        
        return guide.toString();
    }
    
    private boolean isFormatComplete(String text) {
        if (formatSegments == null) return false;
        
        String cleanText = text.replace(formatDelimiter, "");
        int totalCharsNeeded = 0;
        for (int segment : formatSegments) {
            totalCharsNeeded += segment;
        }
        
        return cleanText.length() >= totalCharsNeeded;
    }
    
    /**
     * Document filter that handles auto-formatting, prevents delimiter input, and enforces length limits
     */
    private class FormatDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) 
                throws BadLocationException {
            if (string == null || string.isEmpty()) return;
            
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            
            // Handle format guide if enabled
            if (showFormatGuide && formatSegments != null) {
                handleFormatGuideInsert(fb, offset, string, attr, currentText);
            } else {
                // Handle regular text with length limit
                handleRegularInsert(fb, offset, string, attr, currentText);
            }
        }
        
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                throws BadLocationException {
            if (text == null) {
                super.replace(fb, offset, length, text, attrs);
                return;
            }
            
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            
            // Handle format guide if enabled
            if (showFormatGuide && formatSegments != null) {
                handleFormatGuideReplace(fb, offset, length, text, attrs, currentText);
            } else {
                // Handle regular text with length limit
                handleRegularReplace(fb, offset, length, text, attrs, currentText);
            }
        }
        
        @Override
        public void remove(FilterBypass fb, int offset, int length) 
                throws BadLocationException {
            if (!showFormatGuide || formatSegments == null) {
                // Handle regular deletion
                if (maxLength > 0) {
                    // Check if we can remove (always can remove)
                    super.remove(fb, offset, length);
                } else {
                    super.remove(fb, offset, length);
                }
                return;
            }
            
            // Handle format guide deletion
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            
            // Calculate actual characters to delete (excluding delimiters)
            int actualDeleteStart = getCharacterPosition(currentText, offset);
            int actualDeleteEnd = getCharacterPosition(currentText, offset + length);
            int charsToDelete = Math.max(0, actualDeleteEnd - actualDeleteStart);
            
            // Get clean text (without delimiters)
            String cleanText = currentText.replace(formatDelimiter, "");
            
            // Remove characters from clean text
            String newCleanText = cleanText.substring(0, actualDeleteStart) + 
                                 cleanText.substring(Math.min(actualDeleteEnd, cleanText.length()));
            
            // Format the new text
            String formatted = formatText(newCleanText);
            
            // Replace entire document
            fb.replace(0, currentText.length(), formatted, null);
            
            // Set cursor to appropriate position
            int newCursorPos = getFormattedPosition(formatted, actualDeleteStart);
            textField.setCaretPosition(newCursorPos);
        }
        
        private void handleFormatGuideInsert(FilterBypass fb, int offset, String string, AttributeSet attr, String currentText) 
                throws BadLocationException {
            // Only allow alphanumeric characters
            String filtered = string.replaceAll("[^A-Za-z0-9]", "");
            if (filtered.isEmpty()) return;
            
            // Get clean text (without delimiters)
            String cleanText = currentText.replace(formatDelimiter, "");
            
            // Calculate where to insert in clean text
            int insertPosition = getCharacterPosition(currentText, offset);
            
            // Check max length for format guide
            int totalCharsNeeded = 0;
            for (int segment : formatSegments) {
                totalCharsNeeded += segment;
            }
            
            // Limit input to available slots
            int availableSlots = totalCharsNeeded - cleanText.length();
            if (availableSlots <= 0) return;
            
            filtered = filtered.substring(0, Math.min(filtered.length(), availableSlots));
            
            // Insert into clean text
            String newCleanText = cleanText.substring(0, insertPosition) + 
                                 filtered + 
                                 cleanText.substring(insertPosition);
            
            // Format the new text
            String formatted = formatText(newCleanText);
            
            // Replace entire document
            fb.replace(0, currentText.length(), formatted, attr);
            
            // Set cursor to appropriate position (after inserted characters)
            int newCursorPos = getFormattedPosition(formatted, insertPosition + filtered.length());
            textField.setCaretPosition(newCursorPos);
        }
        
        private void handleFormatGuideReplace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs, String currentText) 
                throws BadLocationException {
            // Only allow alphanumeric characters
            String filtered = text.replaceAll("[^A-Za-z0-9]", "");
            
            // Get clean text (without delimiters)
            String cleanText = currentText.replace(formatDelimiter, "");
            
            // Calculate replace positions in clean text
            int replaceStart = getCharacterPosition(currentText, offset);
            int replaceEnd = getCharacterPosition(currentText, offset + length);
            
            // Check max length for format guide
            int totalCharsNeeded = 0;
            for (int segment : formatSegments) {
                totalCharsNeeded += segment;
            }
            
            // Calculate available space
            int charsBeingReplaced = Math.max(0, replaceEnd - replaceStart);
            int availableSlots = totalCharsNeeded - (cleanText.length() - charsBeingReplaced);
            
            // Limit filtered text to available slots
            if (availableSlots <= 0 && !filtered.isEmpty()) return;
            filtered = filtered.substring(0, Math.min(filtered.length(), Math.max(0, availableSlots)));
            
            // Replace in clean text
            String newCleanText = cleanText.substring(0, replaceStart) + 
                                 filtered + 
                                 cleanText.substring(Math.min(replaceEnd, cleanText.length()));
            
            // Format the new text
            String formatted = formatText(newCleanText);
            
            // Replace entire document
            fb.replace(0, currentText.length(), formatted, attrs);
            
            // Set cursor to appropriate position
            int newCursorPos = getFormattedPosition(formatted, replaceStart + filtered.length());
            textField.setCaretPosition(newCursorPos);
        }
        
        private void handleRegularInsert(FilterBypass fb, int offset, String string, AttributeSet attr, String currentText) 
                throws BadLocationException {
            if (maxLength > 0) {
                // Calculate how many characters we can insert
                int available = maxLength - currentText.length();
                if (available <= 0) return;
                
                // Limit the string to available characters
                String limited = string.substring(0, Math.min(string.length(), available));
                super.insertString(fb, offset, limited, attr);
            } else {
                super.insertString(fb, offset, string, attr);
            }
        }
        
        private void handleRegularReplace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs, String currentText) 
                throws BadLocationException {
            if (maxLength > 0) {
                // Calculate new length after replacement
                int newLength = currentText.length() - length + text.length();
                if (newLength > maxLength) {
                    // Need to trim the text
                    int available = maxLength - (currentText.length() - length);
                    if (available <= 0) {
                        // Can't add any characters, just remove if needed
                        if (length > 0) {
                            super.replace(fb, offset, length, "", attrs);
                        }
                        return;
                    }
                    
                    String limited = text.substring(0, Math.min(text.length(), available));
                    super.replace(fb, offset, length, limited, attrs);
                } else {
                    super.replace(fb, offset, length, text, attrs);
                }
            } else {
                super.replace(fb, offset, length, text, attrs);
            }
        }
        
        /**
         * Get character position in clean text from formatted position
         */
        private int getCharacterPosition(String formattedText, int formattedPos) {
            int charCount = 0;
            for (int i = 0; i < Math.min(formattedPos, formattedText.length()); i++) {
                if (!String.valueOf(formattedText.charAt(i)).equals(formatDelimiter)) {
                    charCount++;
                }
            }
            return charCount;
        }
        
        /**
         * Get formatted position from character position in clean text
         */
        private int getFormattedPosition(String formattedText, int charPosition) {
            int charCount = 0;
            for (int i = 0; i < formattedText.length(); i++) {
                if (charCount >= charPosition) {
                    return i;
                }
                if (!String.valueOf(formattedText.charAt(i)).equals(formatDelimiter)) {
                    charCount++;
                }
            }
            return formattedText.length();
        }
    }
    
    /**
     * Format raw text according to the pattern (only adds delimiters when segments are complete)
     */
    private String formatText(String rawText) {
        if (formatSegments == null || rawText == null) return rawText;
        
        StringBuilder formatted = new StringBuilder();
        int charIndex = 0;
        
        for (int i = 0; i < formatSegments.length; i++) {
            if (i > 0 && charIndex > 0) {
                // Only add delimiter if we have characters in this segment
                formatted.append(formatDelimiter);
            }
            
            int segmentSize = formatSegments[i];
            int endIndex = Math.min(charIndex + segmentSize, rawText.length());
            
            if (charIndex < rawText.length()) {
                formatted.append(rawText.substring(charIndex, endIndex));
                charIndex = endIndex;
            }
        }
        
        return formatted.toString();
    }
    
    public String getRawText() {
        if (textField.getText() == null) return "";
        return textField.getText().replace(formatDelimiter, "");
    }
    
    // ========== LENGTH LIMIT FUNCTIONS ==========
    
    /**
     * Set maximum number of characters allowed in the text field
     * @param maxLength maximum number of characters (<= 0 for no limit)
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
    
    /**
     * Get maximum number of characters allowed
     * @return maximum length or -1 if no limit
     */
    public int getMaxLength() {
        return maxLength;
    }
    
    /**
     * Get current character count (excluding delimiters if format guide is enabled)
     */
    public int getCharacterCount() {
        String text = textField.getText();
        if (text == null) return 0;
        
        if (showFormatGuide && formatSegments != null) {
            return text.replace(formatDelimiter, "").length();
        }
        return text.length();
    }
    
    // ========== EXISTING FUNCTIONS ==========
    
    private void animateBorder() {
        if (isFocused) {
            if (borderThickness < 2.0f) {
                borderThickness += 0.2f;
                updateBorder(true);
            } else {
                focusTimer.stop();
            }
        } else {
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
            if (currentHeight < maxHeight) {
                currentHeight += 0.5f;
                updateSize();
            } else {
                heightTimer.stop();
            }
        } else {
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
        
        Container parent = getParent();
        if (parent != null) {
            parent.revalidate();
            parent.repaint();
        }
    }
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        textField.repaint();
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
    
    public void setFormatGuideColor(Color color) {
        this.formatGuideColor = color;
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
            titledBorder.setTitle(placeholder);
            titledBorder.setBorder(BorderFactory.createLineBorder(focusedBorderColor, Math.round(borderThickness)));
            titledBorder.setTitleColor(focusedBorderColor);
            titledBorder.setTitleFont(new Font("Segoe UI", Font.PLAIN, 10));
        } else {
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
        textField.repaint();
    }
    
    @Override
    public void focusLost(FocusEvent e) {
        isFocused = false;
        focusTimer.start();
        heightTimer.start();
        textField.repaint();
    }
    
    public String getText() {
        return textField.getText();
    }
    
    public void setText(String text) {
        // Auto-format the text if format guide is enabled
        if (showFormatGuide && formatSegments != null && text != null) {
            text = formatText(text.replace(formatDelimiter, ""));
        }
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
    
    public void setFont(Font font) {
        super.setFont(font);
        if (textField != null) {
            textField.setFont(font);
        }
        if (titledBorder != null) {
            titledBorder.setTitleFont(font.deriveFont(10f));
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }
    
    // ========== UTILITY METHODS ==========
    
    public boolean isFormatFilled() {
        return isFormatComplete(textField.getText());
    }
    
    public String getFormatPattern() {
        return formatPattern;
    }
    
    public void setFormatDelimiter(String delimiter) {
        this.formatDelimiter = delimiter;
    }
    
    /**
     * Check if the field has reached its maximum length
     */
    public boolean isAtMaxLength() {
        if (maxLength <= 0) return false;
        
        if (showFormatGuide && formatSegments != null) {
            int totalCharsNeeded = 0;
            for (int segment : formatSegments) {
                totalCharsNeeded += segment;
            }
            return getCharacterCount() >= totalCharsNeeded;
        }
        
        return textField.getText().length() >= maxLength;
    }
}