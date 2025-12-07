package sys.main;

import component.Dashboard;
import component.DefaultForm;
import component.Scheduling;
import backend.objects.Data;
import backend.objects.Data.User;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.border.Border;

public class Main extends javax.swing.JFrame {

    private User currentUser;
    
    public Main() {
        setUndecorated(true);
        initComponents();
        setFullScreenMode();
        
        // Debug: Print body panel info
        System.out.println("Main constructor - Body initialized");
        System.out.println("Body layout: " + body.getLayout());
        System.out.println("Body size: " + body.getWidth() + "x" + body.getHeight());
        
        // First show login dialog
        showLogin();
    }
    
    public class BorderlessButton extends JButton {
        public BorderlessButton(String text) {
            super(text);
            setBorderPainted(false);
            setFocusPainted(false);
            setContentAreaFilled(true);
            setOpaque(true);
        }

        @Override
        public void setBorder(Border border) {
            // Prevent any border from being set
            super.setBorder(null);
        }
    }
    
    private void showLogin() {
        // For testing, use hardcoded credentials
        String username = "maria";
        String password = "password123";
        
        System.out.println("Attempting login with: " + username);
        
        // Authenticate user
        currentUser = Data.User.authenticate(username, password);
        
        if (currentUser == null) {
            // Try alternative credentials
            username = JOptionPane.showInputDialog(this, "Enter Username:", "National ID System Login", JOptionPane.PLAIN_MESSAGE);
            if (username == null || username.trim().isEmpty()) {
                System.exit(0);
            }
            
            password = JOptionPane.showInputDialog(this, "Enter Password:", "National ID System Login", JOptionPane.PLAIN_MESSAGE);
            if (password == null) {
                System.exit(0);
            }
            
            currentUser = Data.User.authenticate(username, password);
            
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
        
        // Check if user is citizen
        if (!currentUser.getRole().equals("citizen")) {
            JOptionPane.showMessageDialog(this, "Access denied. Citizen portal only.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        
        System.out.println("Login successful for: " + currentUser.getFullName());
        System.out.println("Role: " + currentUser.getRole());
        
        // Show citizen dashboard
        showDashboard();
        
        // Setup menu events
        setupMenu();
    }
    
    private void setupMenu() {
        menu.setEvent(new sys.menu.MenuEvent() {
            @Override
            public void selected(int index, int subIndex) {
                System.out.println("Menu clicked: index=" + index + ", subIndex=" + subIndex);
                
                switch (index) {
                    case 0: // Dashboard
                        showDashboard();
                        break;
                        
                    case 1: // Profile
                        showForm(new DefaultForm("My Profile - Coming Soon"));
                        break;
                        
                    case 2: // ID Status
                        showIDStatusMenu(subIndex);
                        break;
                        
                    case 3: // Appointments
                        showAppointmentsMenu(subIndex);
                        break;
                        
                    case 4: // Documents
                        showDocumentsMenu(subIndex);
                        break;
                        
                    case 5: // Help & Support
                        showHelpSupportMenu(subIndex);
                        break;
                }
            }
        });
    }
    
    private void showIDStatusMenu(int subIndex) {
        switch (subIndex) {
            case 1: // Current Status
                showForm(new DefaultForm("Current ID Status - Coming Soon"));
                break;
            case 2: // Renewal History
                showForm(new DefaultForm("Renewal History - Coming Soon"));
                break;
            case 3: // Complaints/Issues
                showForm(new DefaultForm("Complaints/Issues - Coming Soon"));
                break;
        }
    }
    
    private void showAppointmentsMenu(int subIndex) {
        Scheduling newSchedule = new Scheduling(currentUser);
        
        switch (subIndex) {
            case 1: // Schedule New
                showForm(newSchedule);
                break;
            case 2: // Upcoming
                showForm(new DefaultForm("Upcoming Appointments - Coming Soon"));
                break;
            case 3: // Cancel/Reschedule
                showForm(new DefaultForm("Cancel/Reschedule Appointment - Coming Soon"));
                break;
        }
    }
    
    private void showDocumentsMenu(int subIndex) {
        switch (subIndex) {
            case 1: // Upload Documents
                showForm(new DefaultForm("Upload Documents - Coming Soon"));
                break;
            case 2: // View Documents
                showForm(new DefaultForm("View Documents - Coming Soon"));
                break;
            case 3: // Document History
                showForm(new DefaultForm("Document History - Coming Soon"));
                break;
        }
    }
    
    private void showHelpSupportMenu(int subIndex) {
        switch (subIndex) {
            case 1: // FAQs
                showForm(new DefaultForm("Frequently Asked Questions - Coming Soon"));
                break;
            case 2: // Contact Us
                showForm(new DefaultForm("Contact Support - Coming Soon"));
                break;
        }
    }
    
    private void setFullScreenMode() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize);
        setLocationRelativeTo(null);
        
        // Force layout update
        revalidate();
        repaint();
    }
    
    private void showDashboard() {
        System.out.println("showDashboard() called");
        
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Creating Dashboard for user: " + currentUser.getFullName());
                Dashboard dashboard = new Dashboard(currentUser);
                System.out.println("Dashboard created successfully");
                
                // Show the dashboard
                showForm(dashboard);
                
            } catch (Exception e) {
                System.err.println("Error creating dashboard: " + e.getMessage());
                e.printStackTrace();
                showError("Failed to load dashboard: " + e.getMessage());
            }
        });
    }
    
    private void showForm(Component com) {
        System.out.println("showForm() called with component: " + com.getClass().getSimpleName());
        
        // Remove all components from body
        body.removeAll();
        
        // Set preferred size
        com.setPreferredSize(new Dimension(body.getWidth(), body.getHeight()));
        
        // Add component to body
        body.add(com, BorderLayout.CENTER);
        
        // Debug info
        System.out.println("Body child count: " + body.getComponentCount());
        System.out.println("Component size: " + com.getWidth() + "x" + com.getHeight());
        System.out.println("Component visible: " + com.isVisible());
        System.out.println("Body size: " + body.getWidth() + "x" + body.getHeight());
        
        // Force layout update
        body.revalidate();
        body.repaint();
        
        // Also update the main panel
        jPanel1.revalidate();
        jPanel1.repaint();
        
        System.out.println("Form should be visible now");
    }
    
    private void showError(String message) {
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.setBackground(Color.WHITE);
        
        javax.swing.JLabel errorLabel = new javax.swing.JLabel("<html><div style='text-align: center; color: red;'>" +
                                                              "<h2>Error</h2><p>" + message + "</p></div></html>");
        errorLabel.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        
        showForm(errorPanel);
    }
    
    // Add logout functionality
    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Logout Confirmation", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            currentUser = null;
            dispose();
            new Main().setVisible(true);
        }
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        shadowRenderer = new sys.swing.shadow.ShadowRenderer();
        menuAnimation = new sys.menu.MenuAnimation();
        jPanel1 = new javax.swing.JPanel();
        body = new javax.swing.JPanel();
        footer2 = new component.Footer();
        menu = new sys.menu.Menu();
        header1 = new component.Header();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(1100, 600));

        jPanel1.setBackground(new java.awt.Color(245, 245, 245));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(163, 163, 163)));
        jPanel1.setPreferredSize(new java.awt.Dimension(1100, 600));

        body.setBackground(new java.awt.Color(245, 245, 245));
        body.setLayout(new java.awt.BorderLayout());

        footer2.setPreferredSize(new java.awt.Dimension(500, 25));

        menu.setPreferredSize(new java.awt.Dimension(42, 500));

        header1.setPreferredSize(new java.awt.Dimension(540, 60));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(menu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(body, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(footer2, javax.swing.GroupLayout.DEFAULT_SIZE, 1098, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(header1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(header1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(body, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(menu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addComponent(footer2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel body;
    private component.Footer footer2;
    private component.Header header1;
    private javax.swing.JPanel jPanel1;
    private sys.menu.Menu menu;
    private sys.menu.MenuAnimation menuAnimation;
    private sys.swing.shadow.ShadowRenderer shadowRenderer;
    // End of variables declaration//GEN-END:variables
}
