package sys.main;

import component.Dashboard;
import component.DefaultForm;
import component.IDStatus;
import backend.objects.Data;
import backend.objects.Data.User;
import component.Scheduling;
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
    private Landing loginFrame;
    
    public Main() {
        setUndecorated(true);
        initComponents();
        setFullScreenMode();
        
        System.out.println("Main constructor - Body initialized");
        System.out.println("Body layout: " + body.getLayout());
        System.out.println("Body size: " + body.getWidth() + "x" + body.getHeight());
        
        showLoginForm();
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
    
    private void showLoginForm() {
        loginFrame = new Landing();
        loginFrame.setVisible(true);
        
        loginFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                System.out.println("Login window closed");
                
                // Check if login was successful
                if (loginFrame.isLoginSuccessful()) {
                    currentUser = loginFrame.getLoggedInUser();
                    if (currentUser != null) {
                        // Check if user is citizen
                        if (!currentUser.getRole().equals("citizen")) {
                            JOptionPane.showMessageDialog(Main.this, 
                                "Access denied. Citizen portal only.", 
                                "Access Denied", JOptionPane.ERROR_MESSAGE);
                            System.exit(0);
                        }
                        
                        System.out.println("Login successful for: " + currentUser.getFullName());
                        System.out.println("Role: " + currentUser.getRole());
                        
                        // Show main application
                        setVisible(true);
                        
                        // Show citizen dashboard
                        showDashboard();
                        
                        // Setup menu events
                        setupMenu();
                    }
                } else {
                    // Landing was cancelled or failed
                    System.exit(0);
                }
            }
        });
        
        // Hiding the main window while login is showing
        setVisible(false);
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
                        showIDStatus();
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
                    case 6: // Logout
                        logout();
                        break;

                }
            }
        });
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
    
    private void showIDStatus() {
        System.out.println("showIDStatus() called");
        
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Creating ID Status for user: " + currentUser.getFullName());
                IDStatus idstatus = new IDStatus(currentUser);
                System.out.println("ID Status created successfully");
                
                // Show the id status
                showForm(idstatus);
                
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

        // Force layout update first
        body.revalidate();
        body.repaint();

        // If it's a Scheduling component, force initialization AFTER it's visible
        if (com instanceof Scheduling) {
            System.out.println("Scheduling component detected - forcing initialization");
            Scheduling scheduling = (Scheduling) com;

            // Use SwingUtilities.invokeLater to ensure component is fully visible
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // Wait a bit to ensure everything is rendered
                    try {
                        Thread.sleep(100); // Small delay for rendering
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    scheduling.refreshCalendar(); // Force calendar refresh

                    // Also force repaint
                    scheduling.revalidate();
                    scheduling.repaint();
                    body.revalidate();
                    body.repaint();
                    main.revalidate();
                    main.repaint();

                    System.out.println("Scheduling calendar initialized and repainted");
                }
            });
        }
        
        System.out.println("Body child count: " + body.getComponentCount());
        System.out.println("Component size: " + com.getWidth() + "x" + com.getHeight());
        System.out.println("Component visible: " + com.isVisible());
        System.out.println("Body size: " + body.getWidth() + "x" + body.getHeight());

        // Force layout update
        body.revalidate();
        body.repaint();

        // Also update the main panel
        main.revalidate();
        main.repaint();

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
            setVisible(false);
            showLoginForm();
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
        main = new javax.swing.JPanel();
        body = new javax.swing.JPanel();
        footer = new component.Footer();
        menu = new sys.menu.Menu();
        header = new component.Header();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        main.setBackground(new java.awt.Color(245, 245, 245));
        main.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(163, 163, 163)));
        main.setPreferredSize(new java.awt.Dimension(1100, 600));

        body.setBackground(new java.awt.Color(245, 245, 245));
        body.setLayout(new java.awt.BorderLayout());

        footer.setPreferredSize(new java.awt.Dimension(500, 25));

        menu.setPreferredSize(new java.awt.Dimension(42, 500));

        header.setPreferredSize(new java.awt.Dimension(540, 60));

        javax.swing.GroupLayout mainLayout = new javax.swing.GroupLayout(main);
        main.setLayout(mainLayout);
        mainLayout.setHorizontalGroup(
            mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainLayout.createSequentialGroup()
                .addComponent(menu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(body, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(footer, javax.swing.GroupLayout.DEFAULT_SIZE, 1098, Short.MAX_VALUE)
            .addComponent(header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainLayout.setVerticalGroup(
            mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainLayout.createSequentialGroup()
                .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(body, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(menu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addComponent(footer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(main, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(main, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                Main main = new Main();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel body;
    private component.Footer footer;
    private component.Header header;
    private javax.swing.JPanel main;
    private sys.menu.Menu menu;
    private sys.menu.MenuAnimation menuAnimation;
    private sys.swing.shadow.ShadowRenderer shadowRenderer;
    // End of variables declaration//GEN-END:variables
}
