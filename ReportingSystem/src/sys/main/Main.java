package sys.main;

import component.Dashboard; // Your citizen dashboard
import component.DefaultForm;
import backend.objects.Data;
import backend.objects.Data.User;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import sys.menu.MenuEvent;

public class Main extends javax.swing.JFrame {

    private User currentUser;
    
    public Main() {
        setUndecorated(true);
        
        initComponents();
        
        setFullScreenMode();
        
        // First show login dialog
        showLogin();
    }
    
    private void setupMenuToggle() {
        menu.setToggleListener(new sys.menu.Menu.MenuToggleListener() {
            @Override
            public void onMenuToggle(boolean isCollapsed) {
                // This is called during animation
                jPanel1.revalidate();
                jPanel1.repaint();
            }

            @Override
            public void onMenuWidthChanged(int width) {
                // Update body width based on current menu width
                body.setPreferredSize(new java.awt.Dimension(
                    jPanel1.getWidth() - width, 
                    body.getHeight()
                ));
                body.revalidate();
                body.repaint();
            }
        });
    }

    private void showLogin() {
        // Create a simple login dialog
        String username = JOptionPane.showInputDialog(this, "Enter Username:", "National ID System Login", JOptionPane.PLAIN_MESSAGE);
        if (username == null || username.trim().isEmpty()) {
            System.exit(0); // Exit if cancelled
        }
        
        String password = JOptionPane.showInputDialog(this, "Enter Password:", "National ID System Login", JOptionPane.PLAIN_MESSAGE);
        if (password == null) {
            System.exit(0); // Exit if cancelled
        }
        
        // Authenticate user
        currentUser = Data.User.authenticate(username, password);
        
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        
        // Check if user is citizen
        if (!currentUser.getRole().equals("citizen")) {
            JOptionPane.showMessageDialog(this, "Access denied. Citizen portal only.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        
        // Show citizen dashboard
        showCitizenDashboard();
        
        // Setup menu events
        setupMenu();
        setupMenuToggle();
    }
    
    private void setupMenu() {
        menu.setEvent(new MenuEvent() {
            @Override
            public void selected(int index, int subIndex) {
                System.out.println("Menu clicked: index=" + index + ", subIndex=" + subIndex);
                
                switch (index) {
                    case 0: // Dashboard
                        showCitizenDashboard();
                        break;
                        
                    case 1: // Profile
                        showProfileMenu();
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
    
    private void showProfileMenu() {
        showForm(new DefaultForm("My Profile - Coming Soon"));
    }
    
    private void showIDStatusMenu(int subIndex) {
        switch (subIndex) {
            case 0: // Main ID Status item clicked (shows submenu)
                // No action needed - submenu will show
                break;
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
        switch (subIndex) {
            case 0: // Main Appointments item clicked (shows submenu)
                // No action needed - submenu will show
                break;
            case 1: // Schedule New
                showForm(new DefaultForm("Schedule New Appointment - Coming Soon"));
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
            case 0: // Main Documents item clicked (shows submenu)
                // No action needed - submenu will show
                break;
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
            case 0: // Main Help & Support item clicked (shows submenu)
                // No action needed - submenu will show
                break;
            case 1: // FAQs
                showForm(new DefaultForm("Frequently Asked Questions - Coming Soon"));
                break;
            case 2: // Contact Us
                showForm(new DefaultForm("Contact Support - Coming Soon"));
                break;
        }
    }
    
    private void showCitizenDashboard() {
        Dashboard dashboard = new Dashboard(currentUser);
        showForm(dashboard);
    }
    
    private void setFullScreenMode() {
        // Get the graphics environment
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        
        // Check if fullscreen is supported
        if (gd.isFullScreenSupported()) {
            // Use true fullscreen exclusive mode
            gd.setFullScreenWindow(this);
        } else {
            // Fallback: Maximize window and set to screen size
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setSize(screenSize);
            setLocationRelativeTo(null);
        }
    }
    
    private void showForm(Component com) {
        // Remove all components from body
        body.removeAll();
        
        // Set the form to fill the entire body area
        com.setPreferredSize(body.getSize());
        
        // Add the form with BorderLayout to fill the entire space
        body.add(com, java.awt.BorderLayout.CENTER);
        
        // Refresh the display
        body.revalidate();
        body.repaint();
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
