package component.Scroll;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomScrollPane extends JScrollPane {
    
    // Custom color palette matching your design
    private static final Color PRIMARY_BLUE = new Color(0, 120, 215);
    private static final Color LIGHT_BLUE = new Color(142, 217, 255);
    private static final Color BEIGE_BACKGROUND = new Color(249, 241, 240);
    private static final Color WHITE = new Color(255, 255, 255);
    private static final Color GRAY_BORDER = new Color(200, 200, 200);
    private static final Color PLACEHOLDER_GRAY = new Color(150, 150, 150);
    private static final Color DARK_GRAY = new Color(70, 70, 70);
    
    // Custom scrollbar colors (not final so they can be modified)
    private Color scrollbarThumb = new Color(180, 180, 180);
    private Color scrollbarThumbHover = new Color(150, 150, 150);
    private Color scrollbarThumbDrag = PRIMARY_BLUE;
    private Color scrollbarTrack = new Color(245, 245, 245);
    private Color scrollbarBackground = new Color(240, 240, 240);
    
    private boolean showBorder = true;
    private int borderRadius = 8;
    private Color customBorderColor = GRAY_BORDER;
    private int borderThickness = 1;
    
    public CustomScrollPane() {
        super();
        initCustomStyle();
    }
    
    public CustomScrollPane(Component view) {
        super(view);
        initCustomStyle();
    }
    
    public CustomScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
        super(view, vsbPolicy, hsbPolicy);
        initCustomStyle();
    }
    
    private void initCustomStyle() {
        // Set viewport background
        getViewport().setBackground(BEIGE_BACKGROUND);
        
        // Remove default border
        setBorder(BorderFactory.createEmptyBorder());
        
        // Customize vertical scrollbar
        JScrollBar verticalScrollBar = getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setBlockIncrement(50);
        verticalScrollBar.setUI(new CustomScrollBarUI());
        verticalScrollBar.setBackground(scrollbarBackground);
        verticalScrollBar.setForeground(scrollbarThumb);
        
        // Customize horizontal scrollbar
        JScrollBar horizontalScrollBar = getHorizontalScrollBar();
        horizontalScrollBar.setUI(new CustomScrollBarUI());
        horizontalScrollBar.setBackground(scrollbarBackground);
        horizontalScrollBar.setForeground(scrollbarThumb);
        
        // Set scrollpane background
        setBackground(BEIGE_BACKGROUND);
        
        // Set corner
        setCorner(JScrollPane.LOWER_RIGHT_CORNER, new CornerPanel());
        
        // Remove focus border
        setFocusable(false);
        
        // Smooth scrolling
        putClientProperty("JScrollPane.smoothScrolling", true);
    }
    
    // Custom scrollbar UI
    private class CustomScrollBarUI extends BasicScrollBarUI {
        
        private boolean thumbHovered = false;
        private boolean thumbPressed = false;
        
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = scrollbarThumb;
            this.thumbDarkShadowColor = scrollbarThumb;
            this.thumbHighlightColor = scrollbarThumb;
            this.thumbLightShadowColor = scrollbarThumb;
            this.trackColor = scrollbarTrack;
            this.trackHighlightColor = scrollbarTrack;
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
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Paint track with rounded corners
            g2.setColor(trackColor);
            g2.fillRoundRect(trackBounds.x, trackBounds.y, 
                           trackBounds.width, trackBounds.height, 
                           borderRadius, borderRadius);
            
            g2.dispose();
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color thumbColorToUse;
            if (thumbPressed) {
                thumbColorToUse = scrollbarThumbDrag;
            } else if (thumbHovered) {
                thumbColorToUse = scrollbarThumbHover;
            } else {
                thumbColorToUse = scrollbarThumb;
            }
            
            // Paint thumb with rounded corners
            g2.setColor(thumbColorToUse);
            
            int margin = 3;
            int width = scrollbar.getOrientation() == JScrollBar.VERTICAL ? 
                       Math.max(8, thumbBounds.width - margin * 2) : 
                       thumbBounds.width - margin * 2;
            int height = scrollbar.getOrientation() == JScrollBar.VERTICAL ? 
                        thumbBounds.height - margin * 2 : 
                        Math.max(8, thumbBounds.height - margin * 2);
            
            int x = scrollbar.getOrientation() == JScrollBar.VERTICAL ? 
                   thumbBounds.x + margin : 
                   thumbBounds.x + margin;
            int y = scrollbar.getOrientation() == JScrollBar.VERTICAL ? 
                   thumbBounds.y + margin : 
                   thumbBounds.y + margin;
            
            g2.fillRoundRect(x, y, width, height, borderRadius, borderRadius);
            
            // Add subtle inner shadow/highlight for depth
            g2.setColor(new Color(255, 255, 255, 30));
            g2.drawRoundRect(x, y, width, height, borderRadius, borderRadius);
            
            g2.dispose();
        }
        
        @Override
        protected TrackListener createTrackListener() {
            return new CustomTrackListener();
        }
        
        @Override
        protected ArrowButtonListener createArrowButtonListener() {
            return new CustomArrowButtonListener();
        }
        
        @Override
        protected void installListeners() {
            super.installListeners();
            
            // Add hover listeners to thumb
            scrollbar.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    thumbHovered = isMouseOverThumb(e);
                    scrollbar.repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    thumbHovered = false;
                    scrollbar.repaint();
                }
                
                @Override
                public void mousePressed(MouseEvent e) {
                    thumbPressed = isMouseOverThumb(e);
                    scrollbar.repaint();
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    thumbPressed = false;
                    scrollbar.repaint();
                }
            });
            
            scrollbar.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    thumbHovered = isMouseOverThumb(e);
                    scrollbar.repaint();
                }
                
                @Override
                public void mouseDragged(MouseEvent e) {
                    thumbHovered = isMouseOverThumb(e);
                    scrollbar.repaint();
                }
            });
        }
        
        private boolean isMouseOverThumb(MouseEvent e) {
            Rectangle thumbRect = getThumbBounds();
            return thumbRect.contains(e.getPoint());
        }
        
        @Override
        protected Dimension getMinimumThumbSize() {
            return scrollbar.getOrientation() == JScrollBar.VERTICAL ?
                   new Dimension(12, 40) : new Dimension(40, 12);
        }
        
        @Override
        protected Dimension getMaximumThumbSize() {
            return scrollbar.getOrientation() == JScrollBar.VERTICAL ?
                   new Dimension(12, 100) : new Dimension(100, 12);
        }
        
        private class CustomTrackListener extends TrackListener {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                thumbPressed = true;
                scrollbar.repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                thumbPressed = false;
                scrollbar.repaint();
            }
        }
        
        private class CustomArrowButtonListener extends ArrowButtonListener {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                scrollbar.repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                scrollbar.repaint();
            }
        }
    }
    
    // Custom corner panel
    private class CornerPanel extends JPanel {
        public CornerPanel() {
            setBackground(scrollbarTrack);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Paint rounded corner
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            g2.dispose();
        }
    }
    
    // Override paint method to add custom border
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (showBorder) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Paint border with rounded corners
            g2.setColor(customBorderColor);
            g2.setStroke(new BasicStroke(borderThickness));
            g2.drawRoundRect(borderThickness / 2, borderThickness / 2,
                           getWidth() - borderThickness, getHeight() - borderThickness,
                           borderRadius, borderRadius);
            
            g2.dispose();
        }
    }
    
    @Override
    protected void paintBorder(Graphics g) {
        // Don't paint default border
    }
    
    // ========== CUSTOMIZATION METHODS ==========
    
    public void setShowBorder(boolean show) {
        this.showBorder = show;
        repaint();
    }
    
    public boolean isShowBorder() {
        return showBorder;
    }
    
    public void setBorderRadius(int radius) {
        this.borderRadius = Math.max(0, radius);
        repaint();
    }
    
    public int getBorderRadius() {
        return borderRadius;
    }
    
    public void setCustomBorderColor(Color color) {
        this.customBorderColor = color;
        repaint();
    }
    
    public Color getCustomBorderColor() {
        return customBorderColor;
    }
    
    public void setBorderThickness(int thickness) {
        this.borderThickness = Math.max(1, thickness);
        repaint();
    }
    
    public int getBorderThickness() {
        return borderThickness;
    }
    
    public void setScrollBarWidth(int width) {
        getVerticalScrollBar().setPreferredSize(new Dimension(width, 0));
        getHorizontalScrollBar().setPreferredSize(new Dimension(0, width));
        revalidate();
    }
    
    public void setScrollSpeed(int unitIncrement, int blockIncrement) {
        getVerticalScrollBar().setUnitIncrement(unitIncrement);
        getVerticalScrollBar().setBlockIncrement(blockIncrement);
        getHorizontalScrollBar().setUnitIncrement(unitIncrement);
        getHorizontalScrollBar().setBlockIncrement(blockIncrement);
    }
    
    public void setScrollBarColors(Color thumb, Color thumbHover, Color thumbDrag, Color track) {
        this.scrollbarThumb = thumb;
        this.scrollbarThumbHover = thumbHover;
        this.scrollbarThumbDrag = thumbDrag;
        this.scrollbarTrack = track;
        
        // Update scrollbars
        getVerticalScrollBar().setUI(new CustomScrollBarUI());
        getHorizontalScrollBar().setUI(new CustomScrollBarUI());
        repaint();
    }
    
    public void setScrollBarBackground(Color background) {
        this.scrollbarBackground = background;
        getVerticalScrollBar().setBackground(background);
        getHorizontalScrollBar().setBackground(background);
        repaint();
    }
    
    public Color getScrollBarThumbColor() {
        return scrollbarThumb;
    }
    
    public Color getScrollBarThumbHoverColor() {
        return scrollbarThumbHover;
    }
    
    public Color getScrollBarThumbDragColor() {
        return scrollbarThumbDrag;
    }
    
    public Color getScrollBarTrackColor() {
        return scrollbarTrack;
    }
    
    public Color getScrollBarBackgroundColor() {
        return scrollbarBackground;
    }
    
    public void enableSmoothScrolling(boolean enable) {
        putClientProperty("JScrollPane.smoothScrolling", enable);
    }
    
    public void setViewBackground(Color color) {
        getViewport().setBackground(color);
        setBackground(color);
    }
    
    public Color getViewBackground() {
        return getViewport().getBackground();
    }
    
    // Convenience methods for common styles
    public void applyPrimaryStyle() {
        setCustomBorderColor(PRIMARY_BLUE);
        setBorderThickness(2);
        setBorderRadius(10);
        setShowBorder(true);
        setViewBackground(WHITE);
        setScrollBarColors(
            new Color(200, 200, 200),  // thumb
            new Color(170, 170, 170),  // thumb hover
            PRIMARY_BLUE,              // thumb drag
            new Color(240, 240, 240)   // track
        );
    }
    
    public void applySecondaryStyle() {
        setCustomBorderColor(GRAY_BORDER);
        setBorderThickness(1);
        setBorderRadius(8);
        setShowBorder(true);
        setViewBackground(BEIGE_BACKGROUND);
        setScrollBarColors(
            new Color(180, 180, 180),  // thumb
            new Color(150, 150, 150),  // thumb hover
            PRIMARY_BLUE,              // thumb drag
            new Color(245, 245, 245)   // track
        );
    }
    
    public void applyMinimalStyle() {
        setCustomBorderColor(GRAY_BORDER);
        setBorderThickness(0);
        setBorderRadius(0);
        setShowBorder(false);
        setViewBackground(WHITE);
        setScrollBarWidth(10);
        setScrollBarColors(
            new Color(210, 210, 210),  // thumb
            new Color(180, 180, 180),  // thumb hover
            new Color(100, 100, 100),  // thumb drag
            new Color(250, 250, 250)   // track
        );
    }
    
    // Animation methods for interactive effects
    public void flashBorder(Color flashColor, int duration) {
        Color originalColor = customBorderColor;
        setCustomBorderColor(flashColor);
        
        Timer timer = new Timer(duration, e -> {
            setCustomBorderColor(originalColor);
            ((Timer) e.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    // Shadow effect method (optional)
    public void setDropShadow(boolean enabled) {
        if (enabled) {
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
        } else {
            setBorder(BorderFactory.createEmptyBorder());
        }
    }
    
    // Method to refresh the UI after style changes
    public void refreshUI() {
        revalidate();
        repaint();
        getVerticalScrollBar().revalidate();
        getVerticalScrollBar().repaint();
        getHorizontalScrollBar().revalidate();
        getHorizontalScrollBar().repaint();
    }
}