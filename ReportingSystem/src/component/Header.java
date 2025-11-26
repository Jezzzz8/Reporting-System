package component;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Jess
 */
public class Header extends javax.swing.JPanel {

    public Header() {
        initComponents();
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setPaint(new GradientPaint(0, 0, new Color(14, 49, 76), 0, getHeight(), new Color(142, 217, 255)));
        g2.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        g2.dispose();
        super.paintComponent(grphcs);
    }

    public void setHeaderText(String text) {
        jLabel2.setText(text);
    }
    
    public void setHeaderColor(Color startColor, Color endColor) {
        // Custom gradient colors can be set if needed
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        shadowRenderer1 = new sys.swing.shadow.ShadowRenderer();
        jLabel2 = new javax.swing.JLabel();

        jLabel2.setBackground(new java.awt.Color(25, 25, 25));
        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(25, 25, 25));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/component/psa_logo.png"))); // NOI18N
        jLabel2.setText("PSA REPORT");
        jLabel2.setMaximumSize(new java.awt.Dimension(484, 75));
        jLabel2.setMinimumSize(new java.awt.Dimension(484, 75));
        jLabel2.setPreferredSize(new java.awt.Dimension(500, 75));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private sys.swing.shadow.ShadowRenderer shadowRenderer1;
    // End of variables declaration//GEN-END:variables
}
