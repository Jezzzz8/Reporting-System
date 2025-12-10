package component.Progress;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class CustomStepProgressBar extends JPanel {
    
    private int currentStep = 2;
    private int totalSteps = 5; // Changed to 5
    private String[] stepLabels = {"Application Submitted", "Processing & Validation", "Printing & Packaging", "Ready for Pickup", "ID Claimed"};
    private Color completedColor = new Color(200,254,156); // Green for completed
    private Color borderCompletedDarkColor = new Color(0, 150, 0); // Dark Green for completed border
    private Color currentColor = new Color(0, 120, 215); // Blue for current
    private Color pendingColor = new Color(200, 200, 200); // Light gray for pending
    private Color lineColor = new Color(200, 200, 200); // Light gray for lines
    private Color completedTextColor = new Color(0, 150, 0); // Dark Green text inside green circles
    private Color currentTextColor = new Color(0, 120, 215); // Blue text inside current circle
    private Color pendingTextColor = new Color(150, 150, 150); // Dark gray for pending
    private Color completedLabelColor = new Color(0, 120, 215); // Blue for completed labels
    private Color currentLabelColor = new Color(0, 120, 215); // Blue for current label
    private Color pendingLabelColor = new Color(150, 150, 150); // Gray for pending labels
    
    private int circleDiameter = 30;
    private int lineHeight = 4;
    private int labelSpacing = 5;
    private int topSpacing = 5;
    
    private JLabel[] stepLabelComponents;
    private boolean initialized = false;
    
    public CustomStepProgressBar() {
        setOpaque(false);
        setLayout(null);
        
        // Initialize step label array
        stepLabelComponents = new JLabel[totalSteps];
        
        // Set preferred size AFTER initialization
        initialized = true;
        setPreferredSize(new Dimension(800, 80)); // Increased height for 5 steps
    }
    
    private void initComponents() {
        if (!initialized) return;
        
        // Remove existing components
        removeAll();
        
        // Create step labels
        for (int i = 0; i < totalSteps; i++) {
            stepLabelComponents[i] = new JLabel(stepLabels[i], SwingConstants.CENTER);
            stepLabelComponents[i].setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Smaller font for 5 steps
            add(stepLabelComponents[i]);
        }
        
        updateLabels();
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        // Initialize components when added to container
        initComponents();
    }
    
    public void setCurrentStep(int step) {
        if (step >= 0 && step <= totalSteps) { // Allow 0 for no progress
            this.currentStep = step;
            if (initialized) {
                updateLabels();
                repaint();
            }
        }
    }
    
    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
        this.stepLabels = new String[totalSteps];
        this.stepLabelComponents = new JLabel[totalSteps];
        if (initialized) {
            initComponents();
            revalidate();
            repaint();
        }
    }

    public int getTotalSteps() {
        return totalSteps;
    }
    
    public int getCurrentStep() {
        return currentStep;
    }
    
    public void setStepLabels(String[] labels) {
        if (labels.length == totalSteps) {
            this.stepLabels = labels;
            if (initialized && stepLabelComponents != null) {
                for (int i = 0; i < totalSteps; i++) {
                    if (stepLabelComponents[i] != null) {
                        stepLabelComponents[i].setText(labels[i]);
                    }
                }
                updateLabels();
                repaint();
            }
        }
    }
    
    public String[] getStepLabels() {
        return stepLabels;
    }
    
    public void setColors(Color completed, Color current, Color pending) {
        this.completedColor = completed;
        this.currentColor = current;
        this.pendingColor = pending;
        if (initialized) {
            repaint();
        }
    }
    
    private void updateLabels() {
        if (!initialized || stepLabelComponents == null) return;
        
        int panelWidth = getWidth();
        if (panelWidth <= 0) {
            panelWidth = getPreferredSize().width;
        }
        
        int stepWidth = panelWidth / totalSteps;
        
        for (int i = 0; i < totalSteps; i++) {
            JLabel label = stepLabelComponents[i];
            if (label == null) continue;
            
            // Set label position
            int labelX = (i * stepWidth) + (stepWidth / 2) - (panelWidth / (totalSteps * 2));
            int labelY = circleDiameter + topSpacing + labelSpacing;
            label.setBounds(labelX, labelY, panelWidth / totalSteps, 20);
            
            // Set label color based on step status
            if (i + 1 < currentStep) {
                label.setForeground(completedLabelColor); // Blue for completed steps
            } else if (i + 1 == currentStep) {
                label.setForeground(currentLabelColor); // Blue for current step
            } else {
                label.setForeground(pendingLabelColor); // Gray for pending steps
            }
            
            // Make current step bold
            if (i + 1 == currentStep) {
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            } else {
                label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (!initialized) return;
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int stepWidth = panelWidth / totalSteps;
        
        // Draw connecting lines between circles
        g2d.setStroke(new BasicStroke(lineHeight));
        int lineY = circleDiameter / 2 + topSpacing;
        
        for (int i = 0; i < totalSteps - 1; i++) {
            int lineStartX = (i * stepWidth) + (stepWidth / 2) + (circleDiameter / 2);
            int lineEndX = ((i + 1) * stepWidth) + (stepWidth / 2) - (circleDiameter / 2);
            
            // Determine line color
            Color linePaintColor;
            if (i + 1 < currentStep) {
                linePaintColor = completedColor; // Green line between completed steps
            } else {
                linePaintColor = lineColor; // Gray line to pending steps
            }
            
            g2d.setColor(linePaintColor);
            g2d.drawLine(lineStartX, lineY, lineEndX, lineY);
        }
        
        // Draw circles and step numbers
        for (int i = 0; i < totalSteps; i++) {
            int circleX = (i * stepWidth) + (stepWidth / 2) - (circleDiameter / 2);
            int circleY = topSpacing;
            int stepNumber = i + 1;
            
            // Determine circle and text colors based on step status
            Color circleFillColor;
            Color circleBorderColor;
            Color textColor;
            
            if (stepNumber < currentStep) {
                // Completed step: Green filled
                circleFillColor = completedColor;
                circleBorderColor = borderCompletedDarkColor;
                textColor = completedTextColor;
            } else if (stepNumber == currentStep) {
                // Current step: Blue outline (not filled)
                circleFillColor = Color.WHITE;
                circleBorderColor = currentColor;
                textColor = currentTextColor;
            } else {
                // Pending step: Gray outline
                circleFillColor = Color.WHITE;
                circleBorderColor = pendingColor;
                textColor = pendingTextColor;
            }
            
            // Draw circle fill
            g2d.setColor(circleFillColor);
            Ellipse2D.Double circle = new Ellipse2D.Double(circleX, circleY, circleDiameter, circleDiameter);
            g2d.fill(circle);
            
            // Draw circle border (thicker for current step)
            g2d.setColor(circleBorderColor);
            int borderWidth = (stepNumber == currentStep) ? 3 : 2;
            g2d.setStroke(new BasicStroke(borderWidth));
            g2d.draw(circle);
            
            // Draw step number
            String stepNumberText = String.valueOf(stepNumber);
            g2d.setColor(textColor);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(stepNumberText);
            int textHeight = fm.getHeight();
            
            int textX = circleX + (circleDiameter - textWidth) / 2;
            int textY = circleY + ((circleDiameter - textHeight) / 2) + fm.getAscent();
            
            g2d.drawString(stepNumberText, textX, textY);
        }
        
        g2d.dispose();
    }
    
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        if (initialized) {
            updateLabels();
        }
    }
    
    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        if (initialized) {
            updateLabels();
        }
    }
    
    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        // Don't update labels here - it's called too early
    }
    
    @Override
    public void doLayout() {
        super.doLayout();
        if (initialized) {
            updateLabels();
        }
    }
    
    // Optional: Add methods to customize spacing
    public void setTopSpacing(int spacing) {
        this.topSpacing = spacing;
        if (initialized) {
            updateLabels();
            repaint();
        }
    }
    
    public int getTopSpacing() {
        return topSpacing;
    }
    
    public void setCircleDiameter(int diameter) {
        this.circleDiameter = diameter;
        if (initialized) {
            updateLabels();
            repaint();
        }
    }
    
    public int getCircleDiameter() {
        return circleDiameter;
    }
    
    // Optional: Add methods to customize colors further
    public void setCompletedColor(Color color) {
        this.completedColor = color;
        repaint();
    }
    
    public void setCurrentColor(Color color) {
        this.currentColor = color;
        repaint();
    }
    
    public void setPendingColor(Color color) {
        this.pendingColor = color;
        repaint();
    }
    
    public void setCompletedLabelColor(Color color) {
        this.completedLabelColor = color;
        if (initialized) {
            updateLabels();
            repaint();
        }
    }
    
    public void setCurrentLabelColor(Color color) {
        this.currentLabelColor = color;
        if (initialized) {
            updateLabels();
            repaint();
        }
    }
    
    public void setPendingLabelColor(Color color) {
        this.pendingLabelColor = color;
        if (initialized) {
            updateLabels();
            repaint();
        }
    }
}