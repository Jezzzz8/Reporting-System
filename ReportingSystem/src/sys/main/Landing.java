package sys.main;

import backend.objects.Data;
import backend.objects.Data.User;
import javax.swing.JOptionPane;

public class Landing extends javax.swing.JFrame {
    
    private boolean isLoggingIn = false;
    private User loggedInUser = null;
    private boolean loginSuccessful = false;

    public Landing() {
        initComponents();
        setupLoginAction();
    }
    
    private void setupLoginAction() {
        // Get the LoginButton from the Login panel
        javax.swing.JButton loginButton = login.getLoginButton();
        CustomTextField usernameField = login.getUsernameText();
        sys.main.CustomPasswordField passwordField = login.getPasswordText();
        
        // Set up login button action
        loginButton.addActionListener(e -> performLogin());
        
        // Set up Enter key action for password field
        passwordField.addActionListener(e -> performLogin());
        
        // Set up Enter key action for username field
        usernameField.addActionListener(e -> passwordField.requestFocus());
    }
    
    private void performLogin() {
        if (isLoggingIn) {
            return; // Prevent multiple login attempts
        }

        CustomTextField usernameField = login.getUsernameText();
        sys.main.CustomPasswordField passwordField = login.getPasswordText();
        javax.swing.JButton loginButton = login.getLoginButton();

        String username = usernameField.getText().trim();
        String password = passwordField.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password.", 
                "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        isLoggingIn = true;
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        // Try to authenticate
        loggedInUser = Data.User.authenticate(username, password);

        if (loggedInUser != null) {
            loginSuccessful = true;
            System.out.println("Login successful for: " + loggedInUser.getFullName());

            // Close the login window
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid username or password!", 
                "Login Failed", JOptionPane.ERROR_MESSAGE);

            // Reset form - Use setPassword() instead of setText()
            passwordField.setPassword("");
            loginButton.setEnabled(true);
            loginButton.setText("LOGIN");
            isLoggingIn = false;
        }
    }
    
    // Add getter methods for Main to check login status
    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }
    
    public User getLoggedInUser() {
        return loggedInUser;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollBar1 = new javax.swing.JScrollBar();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        MainPanel = new javax.swing.JPanel();
        LEFT = new javax.swing.JPanel();
        LogoLabel = new javax.swing.JLabel();
        RIGHT = new javax.swing.JPanel();
        BodyTabbedPane = new component.NoTabJTabbedPane();
        login = new sys.main.Login();
        registration1 = new sys.main.Registration();

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Login");
        setBackground(new java.awt.Color(201, 177, 158));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setSize(new java.awt.Dimension(800, 500));

        MainPanel.setBackground(new java.awt.Color(255, 255, 255));
        MainPanel.setPreferredSize(new java.awt.Dimension(800, 500));

        LEFT.setBackground(new java.awt.Color(142, 217, 255));
        LEFT.setPreferredSize(new java.awt.Dimension(400, 500));

        LogoLabel.setBackground(new java.awt.Color(142, 217, 255));
        LogoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LogoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/psa_logo_image.png"))); // NOI18N
        LogoLabel.setMinimumSize(new java.awt.Dimension(2089, 2048));
        LogoLabel.setPreferredSize(new java.awt.Dimension(300, 300));

        javax.swing.GroupLayout LEFTLayout = new javax.swing.GroupLayout(LEFT);
        LEFT.setLayout(LEFTLayout);
        LEFTLayout.setHorizontalGroup(
            LEFTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LEFTLayout.createSequentialGroup()
                .addContainerGap(50, Short.MAX_VALUE)
                .addComponent(LogoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );
        LEFTLayout.setVerticalGroup(
            LEFTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LEFTLayout.createSequentialGroup()
                .addContainerGap(100, Short.MAX_VALUE)
                .addComponent(LogoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(100, Short.MAX_VALUE))
        );

        RIGHT.setBackground(new java.awt.Color(255, 255, 255));
        RIGHT.setPreferredSize(new java.awt.Dimension(400, 500));

        BodyTabbedPane.addTab("tab1", login);
        BodyTabbedPane.addTab("tab2", registration1);

        javax.swing.GroupLayout RIGHTLayout = new javax.swing.GroupLayout(RIGHT);
        RIGHT.setLayout(RIGHTLayout);
        RIGHTLayout.setHorizontalGroup(
            RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(BodyTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        RIGHTLayout.setVerticalGroup(
            RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(BodyTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout MainPanelLayout = new javax.swing.GroupLayout(MainPanel);
        MainPanel.setLayout(MainPanelLayout);
        MainPanelLayout.setHorizontalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addComponent(LEFT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(RIGHT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        MainPanelLayout.setVerticalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(RIGHT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(LEFT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.NoTabJTabbedPane BodyTabbedPane;
    private javax.swing.JPanel LEFT;
    private javax.swing.JLabel LogoLabel;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JPanel RIGHT;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JScrollBar jScrollBar1;
    private sys.main.Login login;
    private sys.main.Registration registration1;
    // End of variables declaration//GEN-END:variables
}
