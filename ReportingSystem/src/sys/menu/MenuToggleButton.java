package sys.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

public class MenuToggleButton extends JButton {
    
    private boolean toggled = false;
    private boolean showXOnRight = false;
    
    public MenuToggleButton() {
        setContentAreaFilled(false);
        setBorder(new EmptyBorder(3, 3, 3, 3));
        setBackground(new Color(18, 63, 99));
        setFocusPainted(false);
        // Set a minimum size to ensure full clickable area
        setMinimumSize(new Dimension(40, 40));
        setPreferredSize(new Dimension(40, 40));
        setMaximumSize(new Dimension(40, 40));
    }
    
    public boolean isToggled() {
        return toggled;
    }
    
    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        repaint();
    }
    
    public void setShowXOnRight(boolean showXOnRight) {
        this.showXOnRight = showXOnRight;
        repaint();
    }
    
    public void toggle() {
        setToggled(!toggled);
    }
    
    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background if hovered or pressed
        if (getModel().isRollover() || getModel().isPressed()) {
            g2.setColor(new Color(18, 63, 99, 150));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
        }
        
        // Draw hamburger icon on LEFT or X icon on RIGHT
        g2.setColor(Color.WHITE);
        int centerY = getHeight() / 2;
        int lineWidth = 20;
        int lineHeight = 2;
        int spacing = 6;
        
        if (toggled && showXOnRight) {
            // Draw X icon (close icon) on RIGHT side when toggled
            int rightCenterX = getWidth() - 17;
            Path2D path = new Path2D.Float();
            path.moveTo(rightCenterX - lineWidth/2, centerY - lineWidth/2);
            path.lineTo(rightCenterX + lineWidth/2, centerY + lineWidth/2);
            
            Path2D path2 = new Path2D.Float();
            path2.moveTo(rightCenterX + lineWidth/2, centerY - lineWidth/2);
            path2.lineTo(rightCenterX - lineWidth/2, centerY + lineWidth/2);
            
            g2.setStroke(new java.awt.BasicStroke(2));
            g2.draw(path);
            g2.draw(path2);
        } else if (!toggled && !showXOnRight) {
            // Draw hamburger icon on LEFT side when not toggled
            int leftCenterX = 15;
            for (int i = 0; i < 3; i++) {
                int y = centerY - spacing + (i * spacing);
                g2.fillRoundRect(leftCenterX - lineWidth/2, y, lineWidth, lineHeight, 2, 2);
            }
        }
        
        g2.dispose();
        super.paintComponent(grphcs);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(40, 40);
    }
}