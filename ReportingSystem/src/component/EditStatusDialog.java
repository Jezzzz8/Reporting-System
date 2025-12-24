package component;

import backend.objects.Data;
import backend.objects.Data.Citizen;
import backend.objects.Data.Notification;
import backend.objects.Data.StatusName;
import backend.objects.Data.User;
import backend.objects.Data.IDStatus;
import component.CustomTextArea.CustomTextArea;
import component.DropdownButton.CustomDropdownButton;
import component.Button.FlatButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class EditStatusDialog extends JDialog {
    private Citizen citizen;
    private User staffUser;
    private boolean updated = false;
    
    private CustomDropdownButton statusDropdown;
    private CustomTextArea notesTextArea;
    private CustomTextArea notificationTextArea;
    private JLabel citizenInfoLabel;
    private JLabel currentStatusLabel;
    private JLabel transactionLabel;
    
    public EditStatusDialog(JFrame parent, Citizen citizen, User staffUser) {
        super(parent, "Update Citizen Status", true);
        this.citizen = citizen;
        this.staffUser = staffUser;
        
        initComponents();
        loadCurrentStatus();
        setSize(500, 550);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        citizenInfoLabel = new JLabel("Citizen: " + citizen.getFullName() + " (ID: " + citizen.getCitizenId() + ")");
        citizenInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        citizenInfoLabel.setForeground(new Color(70, 70, 70));
        citizenInfoLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        currentStatusLabel = new JLabel();
        currentStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        currentStatusLabel.setForeground(new Color(100, 100, 100));
        currentStatusLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        transactionLabel = new JLabel();
        transactionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        transactionLabel.setForeground(new Color(120, 120, 120));
        transactionLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        headerPanel.add(citizenInfoLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(currentStatusLabel);
        headerPanel.add(Box.createVerticalStrut(2));
        headerPanel.add(transactionLabel);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Status selection
        JPanel statusPanel = new JPanel(new BorderLayout(0, 5));
        statusPanel.setBackground(Color.WHITE);
        JLabel statusLabel = new JLabel("New Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(new Color(70, 70, 70));
        
        // Get actual status names from database
        List<StatusName> statusNames = Data.StatusName.getAllStatusNames();
        String[] statusOptions = new String[statusNames.size()];
        for (int i = 0; i < statusNames.size(); i++) {
            statusOptions[i] = statusNames.get(i).getStatusName();
        }
        
        statusDropdown = new CustomDropdownButton("Select status");
        statusDropdown.setOptions(statusOptions);
        statusDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusDropdown.setFocusedBorderColor(new Color(0, 120, 215));
        statusDropdown.setBackgroundColor(Color.WHITE);
        statusDropdown.setNormalHeight(45);
        statusDropdown.setExpandedHeight(50);
        
        statusPanel.add(statusLabel, BorderLayout.NORTH);
        statusPanel.add(statusDropdown, BorderLayout.CENTER);
        
        // Notes field
        JPanel notesPanel = new JPanel(new BorderLayout(0, 5));
        notesPanel.setBackground(Color.WHITE);
        JLabel notesLabel = new JLabel("Internal Notes:");
        notesLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        notesLabel.setForeground(new Color(70, 70, 70));
        
        notesTextArea = new CustomTextArea("Enter internal notes...", 4, 30);
        notesTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        notesTextArea.setFocusedBorderColor(new Color(0, 120, 215));
        notesTextArea.setBackgroundColor(Color.WHITE);
        notesTextArea.setNormalHeight(80);
        notesTextArea.setExpandedHeight(100);
        
        notesPanel.add(notesLabel, BorderLayout.NORTH);
        notesPanel.add(notesTextArea, BorderLayout.CENTER);
        
        // Citizen notification
        JPanel notificationPanel = new JPanel(new BorderLayout(0, 5));
        notificationPanel.setBackground(Color.WHITE);
        JLabel notifyLabel = new JLabel("Notification to Citizen:");
        notifyLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        notifyLabel.setForeground(new Color(70, 70, 70));
        
        notificationTextArea = new CustomTextArea("Enter notification message for citizen...", 3, 30);
        notificationTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        notificationTextArea.setFocusedBorderColor(new Color(0, 120, 215));
        notificationTextArea.setBackgroundColor(Color.WHITE);
        notificationTextArea.setNormalHeight(60);
        notificationTextArea.setExpandedHeight(80);
        notificationTextArea.setText("Your National ID application status has been updated.");
        
        notificationPanel.add(notifyLabel, BorderLayout.NORTH);
        notificationPanel.add(notificationTextArea, BorderLayout.CENTER);
        
        // Add form panels
        formPanel.add(statusPanel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(notesPanel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(notificationPanel);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        FlatButton cancelButton = new FlatButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelButton.setNormalColor(new Color(108, 117, 125));
        cancelButton.setHoverColor(new Color(90, 98, 104));
        cancelButton.setPressedColor(new Color(74, 80, 86));
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        FlatButton updateButton = new FlatButton("Update Status");
        updateButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        updateButton.setAsPrimary();
        updateButton.setPreferredSize(new Dimension(120, 40));
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStatus(notificationTextArea.getText());
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(updateButton);
        
        // Add all panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void loadCurrentStatus() {
        IDStatus currentStatus = Data.IDStatus.getStatusByCitizenId(citizen.getCitizenId());
        if (currentStatus != null) {
            String status = currentStatus.getStatus();
            String transactionId = currentStatus.getTransactionId();
            
            currentStatusLabel.setText("Current Status: " + (status != null ? status : "Not set"));
            
            if (transactionId != null && !transactionId.isEmpty()) {
                // Format the transaction ID for display
                String formattedId = Data.IDStatus.formatTransactionId(transactionId);
                transactionLabel.setText("Transaction ID: " + formattedId);
            }
            
            // Set dropdown to current status
            if (status != null) {
                statusDropdown.setText(status);
            }
        } else {
            currentStatusLabel.setText("Current Status: No status set");
            transactionLabel.setText("Transaction ID: Not assigned");
        }
    }
    
    private void updateStatus(String citizenNotification) {
        String newStatus = statusDropdown.getText();
        String notes = notesTextArea.getText().trim();

        if (newStatus == null || newStatus.isEmpty() || newStatus.equals("Select status")) {
            JOptionPane.showMessageDialog(this, 
                "Please select a status from the dropdown", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Find the status name ID from database
            StatusName statusName = null;
            List<StatusName> allStatuses = Data.StatusName.getAllStatusNames();

            for (StatusName sn : allStatuses) {
                if (newStatus.equalsIgnoreCase(sn.getStatusName())) {
                    statusName = sn;
                    break;
                }
            }

            if (statusName == null) {
                JOptionPane.showMessageDialog(this,
                    "Invalid status selected. Please choose a valid status.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get existing status record
            IDStatus existingStatus = Data.IDStatus.getStatusByCitizenId(citizen.getCitizenId());

            if (existingStatus == null) {
                // If no status exists, create a new one
                String transactionId = Data.IDStatus.generateTransactionId(citizen.getCitizenId());

                IDStatus newStatusRecord = new IDStatus();
                newStatusRecord.setCitizenId(citizen.getCitizenId());
                newStatusRecord.setStatusNameId(statusName.getStatusNameId());
                newStatusRecord.setTransactionId(transactionId);
                newStatusRecord.setUpdateDate(new java.sql.Date(System.currentTimeMillis()));
                newStatusRecord.setNotes("Created by " + staffUser.getUsername() + 
                    " (" + staffUser.getFullName() + "): " + notes);

                // Save to database
                boolean success = Data.IDStatus.addStatus(newStatusRecord);

                if (success) {
                    handleSuccessUpdate(statusName, transactionId, notes, citizenNotification);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to create status. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Update the existing status record - just change status_name_id
                existingStatus.setStatusNameId(statusName.getStatusNameId());
                existingStatus.setUpdateDate(new java.sql.Date(System.currentTimeMillis()));

                // Keep existing notes and append new ones
                String updatedNotes = existingStatus.getNotes();
                if (updatedNotes == null) updatedNotes = "";
                if (!notes.isEmpty()) {
                    updatedNotes += "\n[" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()) + 
                                  "] Updated by " + staffUser.getUsername() + " (" + staffUser.getFullName() + "): " + notes;
                }
                existingStatus.setNotes(updatedNotes);

                // Update in database using the correct method
                boolean success = Data.IDStatus.updateStatus(existingStatus);

                if (success) {
                    handleSuccessUpdate(statusName, existingStatus.getTransactionId(), notes, citizenNotification);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to update status. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error updating status: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleSuccessUpdate(StatusName statusName, String transactionId, String notes, String citizenNotification) {
        updated = true;

        // Log activity
        Data.ActivityLog.logActivity(staffUser.getUserId(),
            "Updated status for citizen " + citizen.getCitizenId() + 
            " (" + citizen.getFullName() + ") to: " + statusName.getStatusName());

        // Send notification to citizen if exists
        if (citizen.getEmail() != null && !citizen.getEmail().isEmpty()) {
            sendStatusNotification(citizen, statusName.getStatusName(), citizenNotification);
        }

        // Also send system notification
        String systemMessage = "Your application status has been updated to: " + statusName.getStatusName();
        if (notes != null && !notes.isEmpty()) {
            systemMessage += " - Note: " + notes;
        }

        Data.Notification.addNotification(citizen.getCitizenId(), systemMessage, "Status Update");

        JOptionPane.showMessageDialog(this,
            "✓ Status updated successfully to: " + statusName.getStatusName() + "\n" +
            "Transaction ID: " + Data.IDStatus.formatTransactionId(transactionId),
            "Success",
            JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }
    
    private void sendStatusNotification(Citizen citizen, String newStatus, String message) {
        try {
            // Create notification record
            Notification notification = new Notification();
            notification.setCitizenId(citizen.getCitizenId());
            notification.setNotificationDate(new java.sql.Date(System.currentTimeMillis()));
            notification.setNotificationTime(new SimpleDateFormat("HH:mm").format(new java.util.Date()));
            notification.setMessage(message);
            notification.setType("Status Update");
            notification.setReadStatus("Unread");
            
            Data.Notification.addNotification(notification);
        } catch (Exception e) {
            System.err.println("Error sending notification: " + e.getMessage());
        }
    }
    
    public boolean isUpdated() {
        return updated;
    }
}