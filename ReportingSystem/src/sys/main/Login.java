package sys.main;

import javax.swing.JOptionPane;

public class Login extends javax.swing.JPanel {
    
    public Login() {
        initComponents();
    }

    private void performLogin() {
        String username = UsernameText.getText().trim();
        String password = String.valueOf(PasswordText.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username/email and password.",
                "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Try to authenticate
        backend.objects.Data.User user = backend.objects.Data.User.authenticate(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this,
                "Login successful!\nWelcome " + user.getFullName(),
                "Success", JOptionPane.INFORMATION_MESSAGE);

            // Clear form
            clearForm();

            // Note: In a real application, you would navigate to main dashboard here
            // For now, just show success message
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid username/email or password!",
                "Login Failed", JOptionPane.ERROR_MESSAGE);

            // Clear password field
            PasswordText.setText("");
            PasswordText.requestFocus();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        RIGHT = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        ForgotPasswordButton = new javax.swing.JButton();
        RegisterButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        UsernameText = new sys.main.CustomTextField();
        PasswordText = new sys.main.CustomPasswordField();
        LoginButton = new component.Button.FlatButton();

        setPreferredSize(new java.awt.Dimension(450, 500));

        RIGHT.setBackground(new java.awt.Color(255, 255, 255));
        RIGHT.setPreferredSize(new java.awt.Dimension(450, 500));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 120, 215));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("SIGN IN");

        ForgotPasswordButton.setForeground(new java.awt.Color(100, 100, 100));
        ForgotPasswordButton.setText("Forgot your password?");
        ForgotPasswordButton.setBorder(null);
        ForgotPasswordButton.setBorderPainted(false);
        ForgotPasswordButton.setContentAreaFilled(false);
        ForgotPasswordButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ForgotPasswordButton.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        ForgotPasswordButton.setPreferredSize(new java.awt.Dimension(150, 20));
        ForgotPasswordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ForgotPasswordButtonActionPerformed(evt);
            }
        });

        RegisterButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        RegisterButton.setForeground(new java.awt.Color(0, 120, 215));
        RegisterButton.setText("Register now");
        RegisterButton.setBorder(null);
        RegisterButton.setBorderPainted(false);
        RegisterButton.setContentAreaFilled(false);
        RegisterButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        RegisterButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        RegisterButton.setPreferredSize(new java.awt.Dimension(100, 20));
        RegisterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RegisterButtonActionPerformed(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("Don't have an account?");
        jLabel3.setPreferredSize(new java.awt.Dimension(150, 20));

        UsernameText.setPlaceholder("Username or Email");

        PasswordText.setPlaceholder("Password");

        LoginButton.setBackground(new java.awt.Color(0, 120, 215));
        LoginButton.setText("SIGN IN");
        LoginButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        LoginButton.setNormalColor(new java.awt.Color(0, 120, 215));
        LoginButton.setPreferredSize(new java.awt.Dimension(91, 35));

        javax.swing.GroupLayout RIGHTLayout = new javax.swing.GroupLayout(RIGHT);
        RIGHT.setLayout(RIGHTLayout);
        RIGHTLayout.setHorizontalGroup(
            RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(RIGHTLayout.createSequentialGroup()
                .addContainerGap(75, Short.MAX_VALUE)
                .addGroup(RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(RIGHTLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(RegisterButton, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ForgotPasswordButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(PasswordText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(UsernameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(75, Short.MAX_VALUE))
            .addGroup(RIGHTLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LoginButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        RIGHTLayout.setVerticalGroup(
            RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RIGHTLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1)
                .addGap(18, 113, Short.MAX_VALUE)
                .addComponent(UsernameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(PasswordText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(ForgotPasswordButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(LoginButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addGroup(RIGHTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RegisterButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(84, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(RIGHT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(RIGHT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void RegisterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RegisterButtonActionPerformed
        clearForm();
    }//GEN-LAST:event_RegisterButtonActionPerformed

    private void ForgotPasswordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ForgotPasswordButtonActionPerformed
        JOptionPane.showMessageDialog(this,
            "Password Reset not available yet",
            "Forgot Password", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_ForgotPasswordButtonActionPerformed

    public javax.swing.JButton getLoginButton() {
        return LoginButton;
    }

    public sys.main.CustomTextField getUsernameText() {
        return UsernameText;
    }

    public sys.main.CustomPasswordField getPasswordText() {
        return PasswordText;
    }
    
    public javax.swing.JButton getRegisterButton() {
        return RegisterButton;
    }
    
    // Helper method to clear form
    private void clearForm() {
        UsernameText.clear();
        PasswordText.setText(""); // Changed from clear() to setText("")
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ForgotPasswordButton;
    private component.Button.FlatButton LoginButton;
    private sys.main.CustomPasswordField PasswordText;
    private javax.swing.JPanel RIGHT;
    private javax.swing.JButton RegisterButton;
    private sys.main.CustomTextField UsernameText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}
