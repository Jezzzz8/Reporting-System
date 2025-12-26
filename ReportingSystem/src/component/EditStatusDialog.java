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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

public class EditStatusDialog extends javax.swing.JDialog {
    
    private Citizen citizen;
    private User staffUser;
    private boolean updated = false;
    private JPopupMenu statusMenu;
    
    public EditStatusDialog(java.awt.Frame parent, boolean modal, Citizen citizen, User staffUser) {
        super(parent, modal);
        this.citizen = citizen;
        this.staffUser = staffUser;
        initComponents();
        setupStatusDropdown();
        loadCurrentStatus();
        setupEventListeners();
        
        // Set location relative to parent
        setLocationRelativeTo(parent);
        
        // Set title with citizen name
        setTitle("Update Status - " + citizen.getFullName());
    }
    
    private void setupStatusDropdown() {
        // Load status names from database
        List<StatusName> statusNames = Data.StatusName.getAllStatusNames();
        
        // Create a popup menu for status options
        statusMenu = new JPopupMenu();
        
        // Add status options to the menu
        for (StatusName statusName : statusNames) {
            String status = statusName.getStatusName();
            JMenuItem menuItem = new JMenuItem(status);
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Set the selected status in the dropdown button
                    NewStatusCustomDropdownButton.setText(status);
                    
                    // Also set the notification text with the new status
                    customTextArea2.setText("Your National ID application status has been updated to: " + status);
                }
            });
            statusMenu.add(menuItem);
        }
        
        // Configure dropdown appearance
        NewStatusCustomDropdownButton.setBackgroundColor(java.awt.Color.white);
        NewStatusCustomDropdownButton.setFocusedBorderColor(new java.awt.Color(0, 120, 215));
        NewStatusCustomDropdownButton.setPlaceholder("Select Status");
        NewStatusCustomDropdownButton.setPlaceholderColor(new java.awt.Color(150, 150, 150));
        NewStatusCustomDropdownButton.setNormalHeight(45);
        NewStatusCustomDropdownButton.setExpandedHeight(50);
        
        // Configure dropdown font
        NewStatusCustomDropdownButton.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        
        // Add action listener to the dropdown button to show the menu
        NewStatusCustomDropdownButton.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show the popup menu below the dropdown button
                statusMenu.show(NewStatusCustomDropdownButton, 
                    0, 
                    NewStatusCustomDropdownButton.getHeight());
            }
        });
    }
    
    private void setupEventListeners() {
        // Cancel button
        flatButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Update Status button
        flatButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStatus();
            }
        });
        
        // Configure flatButton1 (Cancel)
        flatButton1.setNormalColor(new java.awt.Color(108, 117, 125));
        flatButton1.setHoverColor(new java.awt.Color(90, 98, 104));
        flatButton1.setPressedColor(new java.awt.Color(74, 80, 86));
        flatButton1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        
        // Configure flatButton2 (Update Status) as primary button
        flatButton2.setAsPrimary();
        flatButton2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
    }
    
    private void loadCurrentStatus() {
        // Update citizen information
        CitizenInformationLabel.setText("Citizen: " + citizen.getFullName() + " (ID: " + citizen.getCitizenId() + ")");
        
        // Configure labels
        CitizenInformationLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        CitizenInformationLabel.setForeground(new java.awt.Color(51, 51, 51));
        
        IDStatusCurrentStatusLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        IDStatusCurrentStatusLabel.setForeground(new java.awt.Color(100, 100, 100));
        
        TransactionIDLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        TransactionIDLabel.setForeground(new java.awt.Color(120, 120, 120));
        
        // Get current status
        IDStatus currentStatus = Data.IDStatus.getStatusByCitizenId(citizen.getCitizenId());
        if (currentStatus != null) {
            String status = currentStatus.getStatus();
            String transactionId = currentStatus.getTransactionId();
            
            IDStatusCurrentStatusLabel.setText("Current Status: " + (status != null ? status : "Not set"));
            
            if (transactionId != null && !transactionId.isEmpty()) {
                String formattedId = Data.IDStatus.formatTransactionId(transactionId);
                TransactionIDLabel.setText("Transaction ID: " + formattedId);
            } else {
                TransactionIDLabel.setText("Transaction ID: Not assigned");
            }
            
            // Set dropdown to current status if it exists in our menu
            if (status != null) {
                NewStatusCustomDropdownButton.setText(status);
            }
        } else {
            IDStatusCurrentStatusLabel.setText("Current Status: No status set");
            TransactionIDLabel.setText("Transaction ID: Not assigned");
            
            // Set default status to "Submitted" if no status exists
            NewStatusCustomDropdownButton.setText("Submitted");
        }
        
        // Configure text areas
        customTextArea1.setPlaceholder("Enter internal notes...");
        customTextArea1.setBackgroundColor(java.awt.Color.white);
        customTextArea1.setFocusedBorderColor(new java.awt.Color(0, 120, 215));
        customTextArea1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        
        customTextArea2.setPlaceholder("Enter notification message for citizen...");
        customTextArea2.setBackgroundColor(java.awt.Color.white);
        customTextArea2.setFocusedBorderColor(new java.awt.Color(0, 120, 215));
        customTextArea2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        customTextArea2.setText("Your National ID application status has been updated.");
        
        // Configure labels
        jLabel4.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        jLabel4.setForeground(new java.awt.Color(70, 70, 70));
        
        jLabel5.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        jLabel5.setForeground(new java.awt.Color(70, 70, 70));
        
        jLabel6.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        jLabel6.setForeground(new java.awt.Color(70, 70, 70));
    }
    
    private void updateStatus() {
        String newStatus = NewStatusCustomDropdownButton.getText();
        String notes = customTextArea1.getText().trim();
        String citizenNotification = customTextArea2.getText().trim();

        // Validate status selection
        if (newStatus == null || newStatus.isEmpty() || 
            newStatus.equals("Select Status") || newStatus.equals("Select status")) {
            JOptionPane.showMessageDialog(this, 
                "Please select a status from the dropdown", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate notification message
        if (citizenNotification == null || citizenNotification.isEmpty() || 
            citizenNotification.equals("Enter notification message for citizen...")) {
            citizenNotification = "Your National ID application status has been updated to: " + newStatus;
        }

        try {
            // Find the status name ID from database
            StatusName statusName = findStatusNameByName(newStatus);

            if (statusName == null) {
                JOptionPane.showMessageDialog(this,
                    "Invalid status selected. Please choose a valid status.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get existing status record
            IDStatus existingStatus = Data.IDStatus.getStatusByCitizenId(citizen.getCitizenId());
            String transactionId = null;

            // First, try to find any existing transaction ID for this citizen
            transactionId = findExistingTransactionId(citizen.getCitizenId());

            if (existingStatus == null) {
                // No current status record exists

                if (transactionId == null) {
                    // No transaction ID found anywhere, generate a new one
                    transactionId = Data.IDStatus.generateTransactionId(citizen.getCitizenId());
                    System.out.println("Generated new TRN for citizen " + citizen.getCitizenId() + ": " + transactionId);
                } else {
                    System.out.println("Using existing TRN from history for citizen " + citizen.getCitizenId() + ": " + transactionId);
                }

                // Truncate notes if too long
                String finalNotes = truncateNotes(
                    notes.isEmpty() ? "Status created by " + staffUser.getUsername() : 
                                    "Status created by " + staffUser.getUsername() + ": " + notes,
                    255
                );

                IDStatus newStatusRecord = new IDStatus(
                    0, // status_id will be auto-generated
                    transactionId, // Use existing or new transaction ID
                    citizen.getCitizenId(),
                    statusName.getStatusNameId(),
                    new java.sql.Date(System.currentTimeMillis()),
                    finalNotes
                );

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
                // Update the existing status record - PRESERVE TRANSACTION ID

                if (transactionId == null) {
                    // No transaction ID found in history, use the current one
                    transactionId = existingStatus.getTransactionId();

                    if (transactionId == null || transactionId.isEmpty()) {
                        // Still no TRN, generate one
                        transactionId = Data.IDStatus.generateTransactionId(citizen.getCitizenId());
                        System.out.println("Generated new TRN for existing status: " + transactionId);
                        existingStatus.setTransactionId(transactionId);
                    }
                } else if (existingStatus.getTransactionId() == null || existingStatus.getTransactionId().isEmpty()) {
                    // Found TRN in history but not in current record, update it
                    existingStatus.setTransactionId(transactionId);
                    System.out.println("Updated existing status with TRN from history: " + transactionId);
                } else {
                    // Use the current transaction ID
                    transactionId = existingStatus.getTransactionId();
                    System.out.println("Using current TRN: " + transactionId);
                }

                // Update status fields
                existingStatus.setStatusNameId(statusName.getStatusNameId());
                existingStatus.setUpdateDate(new java.sql.Date(System.currentTimeMillis()));

                // Keep existing notes and append new ones, but TRUNCATE if too long
                String updatedNotes = existingStatus.getNotes();
                if (updatedNotes == null || updatedNotes.isEmpty()) {
                    updatedNotes = "Status updated by " + staffUser.getUsername();
                } else {
                    // Check if adding new info would exceed limit
                    String newEntry = "\n---\n" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()) + 
                                    ": Updated by " + staffUser.getUsername();

                    // If adding the new entry would exceed 255 chars, truncate or compress
                    if ((updatedNotes.length() + newEntry.length()) > 250) { // Leave some room for note
                        // Keep only the most recent entries - remove oldest parts
                        updatedNotes = compressNotes(updatedNotes, 150);
                    }
                    updatedNotes += newEntry;
                }

                // Add the user's note if provided
                if (!notes.isEmpty()) {
                    String noteEntry = "\nNote: " + notes;
                    if ((updatedNotes.length() + noteEntry.length()) > 255) {
                        // Truncate the new note to fit
                        int availableSpace = 255 - updatedNotes.length() - 4; // 4 for "..." and newline
                        if (availableSpace > 10) { // Only add if we have reasonable space
                            noteEntry = "\nNote: " + truncateString(notes, availableSpace - 7) + "...";
                            updatedNotes += noteEntry;
                        }
                    } else {
                        updatedNotes += noteEntry;
                    }
                }

                // Final truncation to ensure it fits in database
                updatedNotes = truncateNotes(updatedNotes, 255);
                existingStatus.setNotes(updatedNotes);

                // Update in database
                boolean success = Data.IDStatus.updateStatus(existingStatus);

                if (success) {
                    handleSuccessUpdate(statusName, transactionId, notes, citizenNotification);
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

    // Helper method to find existing transaction ID
    private String findExistingTransactionId(int citizenId) {
        try {
            // Check current status first
            IDStatus currentStatus = Data.IDStatus.getStatusByCitizenId(citizenId);
            if (currentStatus != null && currentStatus.getTransactionId() != null && 
                !currentStatus.getTransactionId().isEmpty()) {
                return currentStatus.getTransactionId();
            }

            // Check status history
            List<IDStatus> statusHistory = Data.IDStatus.getStatusHistoryByCitizenId(citizenId);
            if (statusHistory != null && !statusHistory.isEmpty()) {
                for (IDStatus status : statusHistory) {
                    if (status.getTransactionId() != null && !status.getTransactionId().isEmpty()) {
                        return status.getTransactionId();
                    }
                }
            }

            return null;
        } catch (Exception e) {
            System.err.println("Error finding existing transaction ID: " + e.getMessage());
            return null;
        }
    }

    // Helper method to truncate notes to maximum length
    private String truncateNotes(String notes, int maxLength) {
        if (notes == null || notes.length() <= maxLength) {
            return notes;
        }
        return notes.substring(0, maxLength - 3) + "...";
    }

    // Helper method to compress notes by keeping only recent entries
    private String compressNotes(String notes, int targetLength) {
        if (notes == null || notes.length() <= targetLength) {
            return notes;
        }

        // Split by separator and keep only the last few entries
        String[] entries = notes.split("\n---\n");
        StringBuilder compressed = new StringBuilder();

        // Start from the end and add entries until we reach target length
        for (int i = entries.length - 1; i >= 0; i--) {
            String entry = entries[i].trim();
            if (!entry.isEmpty()) {
                if (compressed.length() + entry.length() + 5 > targetLength) { // +5 for separator
                    break;
                }
                if (compressed.length() > 0) {
                    compressed.insert(0, "\n---\n");
                }
                compressed.insert(0, entry);
            }
        }

        // If still too long, truncate
        if (compressed.length() > targetLength) {
            compressed = new StringBuilder(truncateString(compressed.toString(), targetLength));
        }

        return compressed.toString();
    }

    // Helper method to truncate string
    private String truncateString(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    private StatusName findStatusNameByName(String name) {
        List<StatusName> allStatuses = Data.StatusName.getAllStatusNames();
        for (StatusName sn : allStatuses) {
            if (sn.getStatusName().equalsIgnoreCase(name)) {
                return sn;
            }
        }
        return null;
    }

    private void handleSuccessUpdate(StatusName statusName, String transactionId, String notes, String citizenNotification) {
        updated = true;

        // Log activity
        Data.ActivityLog.logActivity(staffUser.getUserId(),
            "Updated status for citizen " + citizen.getCitizenId() + 
            " (" + citizen.getFullName() + ") to: " + statusName.getStatusName());

        // Send notification to citizen
        sendStatusNotification(citizen, statusName.getStatusName(), citizenNotification);

        JOptionPane.showMessageDialog(this,
            "✓ Status updated successfully to: " + statusName.getStatusName() + "\n" +
            "Transaction ID: " + (transactionId != null ? Data.IDStatus.formatTransactionId(transactionId) : "N/A"),
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
            
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            notification.setNotificationTime(timeFormat.format(new java.util.Date()));
            
            notification.setMessage(message);
            notification.setType("Status Update");
            notification.setReadStatus("Unread");
            
            // Use the static addNotification method
            Data.Notification.addNotification(notification);
            
        } catch (Exception e) {
            System.err.println("Error sending notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public boolean isUpdated() {
        return updated;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        EditStatusDialog = new javax.swing.JPanel();
        CitizenInformationLabel = new javax.swing.JLabel();
        IDStatusCurrentStatusLabel = new javax.swing.JLabel();
        TransactionIDLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        NewStatusCustomDropdownButton = new component.DropdownButton.CustomDropdownButton();
        jLabel5 = new javax.swing.JLabel();
        customTextArea1 = new component.CustomTextArea.CustomTextArea();
        customTextArea2 = new component.CustomTextArea.CustomTextArea();
        jLabel6 = new javax.swing.JLabel();
        flatButton1 = new component.Button.FlatButton();
        flatButton2 = new component.Button.FlatButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Update Citizen Status");
        setBackground(new java.awt.Color(255, 255, 255));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(480, 522));
        setResizable(false);

        EditStatusDialog.setBackground(new java.awt.Color(255, 255, 255));
        EditStatusDialog.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 120, 215), 2, true));
        EditStatusDialog.setPreferredSize(new java.awt.Dimension(480, 500));

        CitizenInformationLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        CitizenInformationLabel.setForeground(new java.awt.Color(51, 51, 51));
        CitizenInformationLabel.setText("Citizen: Citizen Full Name (ID: 0)");

        IDStatusCurrentStatusLabel.setForeground(new java.awt.Color(51, 51, 51));
        IDStatusCurrentStatusLabel.setText("Current Status: (Status)");

        TransactionIDLabel.setForeground(new java.awt.Color(51, 51, 51));
        TransactionIDLabel.setText("Transaction ID: (Transaction Reference Number)");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 51, 51));
        jLabel4.setText("New Status:");

        NewStatusCustomDropdownButton.setBackgroundColor(java.awt.Color.white);
        NewStatusCustomDropdownButton.setExpandedHeight(50);
        NewStatusCustomDropdownButton.setPlaceholder("Select Status");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setText("Internal Notes:");

        customTextArea1.setBackgroundColor(java.awt.Color.white);
        customTextArea1.setPlaceholder("Enter internal notes...");

        customTextArea2.setBackgroundColor(java.awt.Color.white);
        customTextArea2.setPlaceholder("Enter notification message for citizen...");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setText("Notifications to Citizen:");

        flatButton1.setText("Cancel");
        flatButton1.setNormalColor(new java.awt.Color(120, 120, 125));

        flatButton2.setText("Update Status");

        javax.swing.GroupLayout EditStatusDialogLayout = new javax.swing.GroupLayout(EditStatusDialog);
        EditStatusDialog.setLayout(EditStatusDialogLayout);
        EditStatusDialogLayout.setHorizontalGroup(
            EditStatusDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EditStatusDialogLayout.createSequentialGroup()
                .addGroup(EditStatusDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(EditStatusDialogLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(EditStatusDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(customTextArea1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(NewStatusCustomDropdownButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(IDStatusCurrentStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(CitizenInformationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(TransactionIDLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(customTextArea2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(EditStatusDialogLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(flatButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(flatButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(25, 25, 25))
        );
        EditStatusDialogLayout.setVerticalGroup(
            EditStatusDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EditStatusDialogLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(CitizenInformationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(IDStatusCurrentStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(TransactionIDLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 23, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(NewStatusCustomDropdownButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 23, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(customTextArea1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 23, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(customTextArea2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addGroup(EditStatusDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(flatButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(flatButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(EditStatusDialog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(EditStatusDialog, javax.swing.GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CitizenInformationLabel;
    private javax.swing.JPanel EditStatusDialog;
    private javax.swing.JLabel IDStatusCurrentStatusLabel;
    private component.DropdownButton.CustomDropdownButton NewStatusCustomDropdownButton;
    private javax.swing.JLabel TransactionIDLabel;
    private component.CustomTextArea.CustomTextArea customTextArea1;
    private component.CustomTextArea.CustomTextArea customTextArea2;
    private component.Button.FlatButton flatButton1;
    private component.Button.FlatButton flatButton2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    // End of variables declaration//GEN-END:variables
}
