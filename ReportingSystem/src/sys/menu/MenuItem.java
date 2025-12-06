package sys.menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import sys.effect.RippleEffect;
import sys.swing.shadow.ShadowRenderer;

public class MenuItem extends JButton {
    private float alpha = 1f;
    
    public float getAnimate() {
        return animate;
    }

    public void setAnimate(float animate) {
        this.animate = animate;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isSubMenuAble() {
        return subMenuAble;
    }

    public void setSubMenuAble(boolean subMenuAble) {
        this.subMenuAble = subMenuAble;
    }

    public int getSubMenuIndex() {
        return subMenuIndex;
    }

    public void setSubMenuIndex(int subMenuIndex) {
        this.subMenuIndex = subMenuIndex;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }
    
    private RippleEffect rippleEffect;
    private BufferedImage shadow;
    private int shadowWidth;
    private int shadowSize = 10;
    private int index;
    private boolean subMenuAble;
    private float animate;
    //  Submenu
    private int subMenuIndex;
    private int length;
    private boolean selected = false;

    public MenuItem(String name, int index, boolean subMenuAble) {
        super(name);
        this.index = index;
        this.subMenuAble = subMenuAble;
        setContentAreaFilled(false);
        setForeground(new Color(25, 25, 25));
        setFont(new java.awt.Font("Times New Roman", 0, 12)); 
        setHorizontalAlignment(SwingConstants.LEFT);
        setBorder(new EmptyBorder(9, 10, 9, 10));
        setIconTextGap(10);
        rippleEffect = new RippleEffect(this);
        rippleEffect.setRippleColor(new Color(25, 25, 25));
    }

    private void createShadowImage() {
        int widht = getWidth();
        int height = 5;
        BufferedImage img = new BufferedImage(widht, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.BLACK);
        g2.fill(new Rectangle2D.Double(0, 0, widht, height));
        shadow = new ShadowRenderer(shadowSize, 0.2f, Color.BLACK).createShadow(img);
        g2.dispose();
    }

    public void initSubMenu(int subMenuIndex, int length) {
        this.subMenuIndex = subMenuIndex;
        this.length = length;
        setBorder(new EmptyBorder(9, 33, 9, 10));
        setBackground(new Color(114, 163, 187));
        setForeground(new Color(250, 250, 250));
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
        super.paintComponent(g2);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Only draw submenu indicators if we have text (not collapsed)
        if (getText() != null && !getText().trim().isEmpty()) {
            if (length != 0) {
                g2.setColor(new Color(43, 98, 141));
                if (subMenuIndex == 1) {
                    //  First Index
                    g2.drawImage(shadow, -shadowSize, -20, null);
                    g2.drawLine(18, 0, 18, getHeight());
                    g2.drawLine(18, getHeight() / 2, 26, getHeight() / 2);
                } else if (subMenuIndex == length - 1) {
                    //  Last Index
                    g2.drawImage(shadow, -shadowSize, getHeight() - 6, null);
                    g2.drawLine(18, 0, 18, getHeight() / 2);
                    g2.drawLine(18, getHeight() / 2, 26, getHeight() / 2);
                } else {
                    g2.drawLine(18, 0, 18, getHeight());
                    g2.drawLine(18, getHeight() / 2, 26, getHeight() / 2);
                }
            } else if (subMenuAble) {
                g2.setColor(getForeground());
                int arrowWidth = 8;
                int arrowHeight = 4;
                Path2D p = new Path2D.Double();
                p.moveTo(0, arrowHeight * animate);
                p.lineTo(arrowWidth / 2, (1f - animate) * arrowHeight);
                p.lineTo(arrowWidth, arrowHeight * animate);
                g2.translate(getWidth() - arrowWidth - 15, (getHeight() - arrowHeight) / 2);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g2.draw(p);
            }
        }

        g2.dispose();
        rippleEffect.reder(grphcs, new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
    }
    
    @Override
    public void setText(String text) {
        super.setText(text);
        // Adjust horizontal alignment based on whether we have text
        if (text == null || text.trim().isEmpty()) {
            setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            setHorizontalAlignment(SwingConstants.LEFT);
        }
    }
    
    @Override
    public void setBounds(int i, int i1, int i2, int i3) {
        super.setBounds(i, i1, i2, i3);
        createShadowImage();
    }
    
    public void setAlpha(float alpha) {
        this.alpha = alpha;
        repaint();
    }

}