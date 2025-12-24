package component.Button;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.border.Border;

public class FlatButton extends JButton {
    
    private Color normalColor = new Color(51, 122, 183);
    private Color hoverColor = new Color(40, 96, 144);
    private Color pressedColor = new Color(31, 73, 110);
    private Color textColor = Color.WHITE;
    private boolean isHovered = false;
    private boolean isPressed = false;
    
    // Default constructor for NetBeans visual designer
    public FlatButton() {
        this("");
    }
    
    public FlatButton(String text) {
        super(text);
        init();
    }
    
    public FlatButton(String text, Color color) {
        super(text);
        this.normalColor = color;
        this.hoverColor = color.darker();
        this.pressedColor = color.darker().darker();
        init();
    }
    
    public FlatButton(String text, Color normalColor, Color hoverColor, Color pressedColor) {
        super(text);
        this.normalColor = normalColor;
        this.hoverColor = hoverColor;
        this.pressedColor = pressedColor;
        init();
    }
    
    private void init() {
        // Configure for flat design
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        
        // Set cursor
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Default font
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setForeground(textColor);
        
        // Add mouse listeners
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
        
        // Default size
        setPreferredSize(new Dimension(100, 40));
        setMinimumSize(new Dimension(80, 35));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Determine color based on state
        Color buttonColor;
        if (!isEnabled()) {
            buttonColor = Color.GRAY;
        } else if (isPressed) {
            buttonColor = pressedColor;
        } else if (isHovered) {
            buttonColor = hoverColor;
        } else {
            buttonColor = normalColor;
        }
        
        // Draw flat rectangle (no rounded corners)
        g2.setColor(buttonColor);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Optional: Draw border on hover/press
        if (isEnabled() && (isHovered || isPressed)) {
            g2.setColor(buttonColor.darker());
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
        
        g2.dispose();
        
        // Paint text
        super.paintComponent(g);
    }
    
    @Override
    public void setBorder(Border border) {
        // Prevent any border from being set
        super.setBorder(null);
    }
    
    // Property getters and setters for NetBeans property editor
    public Color getNormalColor() {
        return normalColor;
    }
    
    public void setNormalColor(Color normalColor) {
        this.normalColor = normalColor;
        this.hoverColor = normalColor.darker();
        this.pressedColor = normalColor.darker().darker();
        repaint();
    }
    
    public Color getHoverColor() {
        return hoverColor;
    }
    
    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
        repaint();
    }
    
    public Color getPressedColor() {
        return pressedColor;
    }
    
    public void setPressedColor(Color pressedColor) {
        this.pressedColor = pressedColor;
        repaint();
    }
    
    public Color getTextColor() {
        return textColor;
    }
    
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
        setForeground(textColor);
        repaint();
    }
    
    // Convenience methods for common button types
    public void setAsPrimary() {
        setNormalColor(new Color(51, 122, 183));
        setTextColor(Color.WHITE);
    }
    
    public void setAsSuccess() {
        setNormalColor(new Color(92, 184, 92));
        setTextColor(Color.WHITE);
    }
    
    public void setAsDanger() {
        setNormalColor(new Color(217, 83, 79));
        setTextColor(Color.WHITE);
    }
    
    public void setAsWarning() {
        setNormalColor(new Color(240, 173, 78));
        setTextColor(Color.BLACK);
    }
    
    public void setAsInfo() {
        setNormalColor(new Color(91, 192, 222));
        setTextColor(Color.WHITE);
    }
    
    public void setAsDark() {
        setNormalColor(new Color(51, 51, 51));
        setTextColor(Color.WHITE);
    }
}