package sys.effect;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;

public class GlassPaneEffect extends JPanel {
    
    private float alpha = 0.0f;
    private Animator animator;
    
    public GlassPaneEffect() {
        setOpaque(false);
        initAnimator();
    }
    
    private void initAnimator() {
        animator = new Animator(300, new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                alpha = fraction * 0.7f; // Max 70% opacity
                repaint();
            }
        });
        animator.setResolution(5);
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
    }
    
    public void showOverlay() {
        setVisible(true);
        animator.start();
    }
    
    public void hideOverlay() {
        animator.stop();
        animator.setStartFraction(alpha / 0.7f);
        animator.setStartDirection(Animator.Direction.BACKWARD);
        animator.start();
        
        // Hide after animation completes
        javax.swing.Timer timer = new javax.swing.Timer(300, e -> {
            setVisible(false);
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Create glass effect
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(new Color(50, 50, 50));
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Add subtle blur effect
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.5f));
        g2.setColor(Color.BLACK);
        for (int i = 0; i < getWidth(); i += 10) {
            for (int j = 0; j < getHeight(); j += 10) {
                g2.fillOval(i, j, 5, 5);
            }
        }
        
        g2.dispose();
    }
}