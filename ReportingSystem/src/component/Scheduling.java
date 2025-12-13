package component;

import backend.objects.Data.*;
import component.Calendar.CalendarCustom.CalendarCustomListener;
import backend.objects.Data.IDStatus;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class Scheduling extends javax.swing.JPanel {

    private User currentUser;
    private Citizen citizen;
    private Date selectedDate;
    private String selectedTime;
    private int currentStep = 1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
    private SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
    
    public Scheduling(User user) {
        this.currentUser = user;
        initComponents();
        configureProgressBar();
        initCitizenData();
        initCalendar();
        initTimeSlots();
        updateUIForStep(currentStep);
        forceCalendarRefresh();

        // Add tab change listener to ensure calendar is visible
        SchedulingTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                if (SchedulingTabbedPane.getSelectedIndex() == 0) { // Date selection tab
                    System.out.println("Date tab selected - refreshing calendar");
                    refreshCalendar();
                }
            }
        });

        // Add component listener to handle when component becomes visible
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                System.out.println("Scheduling component shown");
                refreshCalendar();
            }
        });
    }
    
    private void configureProgressBar() {
        // Set progress bar to 3 steps
        customProgressBar.setTotalSteps(3);
        
        // Set step labels for scheduling process
        String[] schedulingLabels = {
            "Select Date",
            "Select Time",
            "Confirm Schedule"
        };
        customProgressBar.setStepLabels(schedulingLabels);
        
        // Set current step to 1 (Select Date)
        customProgressBar.setCurrentStep(1);
    }
    
    public void refreshCalendar() {
        if (calendarCustom != null) {
            System.out.println("Refreshing calendar...");

            // Reload booked dates from database
            calendarCustom.reloadBookedDates();

            // Force reinitialization
            calendarCustom.revalidate();
            calendarCustom.repaint();

            // Force the parent panel to update
            SelectDatePanel.revalidate();
            SelectDatePanel.repaint();

            // If there's a selected date, make sure it's highlighted
            if (selectedDate != null) {
                calendarCustom.setSelectedDate(selectedDate);
            }

            System.out.println("Calendar refreshed");
        }
    }

    private void initCitizenData() {
        // Get citizen data for the current user
        this.citizen = Citizen.getCitizenByUserId(currentUser.getUserId());
        if (citizen != null) {
            // Get transaction ID from IDStatus
            IDStatus idStatus = IDStatus.getStatusByCitizenId(citizen.getCitizenId());
            if (idStatus != null) {
                System.out.println("Transaction ID found: " + idStatus.getTransactionId());
            }
            updateSummaryPanel();
        } else {
            System.err.println("ERROR: No citizen found for user ID: " + currentUser.getUserId());
        }
    }
    
        private void initCalendar() {
        System.out.println("Initializing calendar...");

        // Make sure calendar is visible and properly sized
        calendarCustom.setVisible(true);
        calendarCustom.setSize(SelectDatePanel.getSize());

        // Initialize with a listener
        calendarCustom.setListener(new CalendarCustomListener() {
            @Override
            public void dateSelected(Date date) {
                System.out.println("Date selected: " + date);
                selectedDate = date;
                updateSummaryPanel();
                updateProgress();
                ContinueButton.setEnabled(isStepComplete(currentStep));
                
                // Check if date is available
                if (!calendarCustom.isAvailableDate(date)) {
                    JOptionPane.showMessageDialog(Scheduling.this, 
                        "Selected date is not available. Please choose another date.", 
                        "Date Not Available", JOptionPane.WARNING_MESSAGE);
                }
            }

            @Override
            public void monthChanged(int month, int year) {
                System.out.println("Month changed to: " + month + "/" + year);
                // Reload booked dates when month changes
                calendarCustom.reloadBookedDates();
                calendarCustom.repaint();
                SelectDatePanel.repaint();
            }
        });

        // Force initial repaint
        calendarCustom.repaint();
        SelectDatePanel.repaint();

        System.out.println("Calendar initialized");
    }
    
    public void forceCalendarRefresh() {
        if (calendarCustom != null) {
            // Reload booked dates from database
            calendarCustom.reloadBookedDates();
            
            // Force the calendar to reinitialize
            calendarCustom.revalidate();
            calendarCustom.repaint();

            // Update the selected date if there is one
            if (selectedDate != null) {
                calendarCustom.setSelectedDate(selectedDate);
            }

            // Force the parent containers to update
            SelectDatePanel.revalidate();
            SelectDatePanel.repaint();
            revalidate();
            repaint();

            System.out.println("Calendar forcefully refreshed");
        }
    }
    
    private void updateUIForStep(int step) {
        System.out.println("Updating UI for step: " + step);

        // Update progress bar
        customProgressBar.setCurrentStep(step);

        // Set the tab index
        SchedulingTabbedPane.setSelectedIndex(step - 1);

        // Update button states
        PreviousButton.setVisible(step > 1);
        PreviousButton.setEnabled(step > 1);
        ContinueButton.setEnabled(isStepComplete(step));

        if (step == 3) {
            ContinueButton.setText("Confirm");
            updateConfirmDetailsPanel();
        } else {
            ContinueButton.setText("Continue");
        }

        System.out.println("Continue button enabled: " + ContinueButton.isEnabled());
        System.out.println("Step " + step + " complete: " + isStepComplete(step));
    }
    
    private boolean isStepComplete(int step) {
        boolean complete = false;
        switch (step) {
            case 1:
                complete = selectedDate != null;
                break;
            case 2:
                complete = selectedTime != null;
                break;
            case 3:
                complete = selectedDate != null && selectedTime != null;
                break;
            default:
                complete = false;
        }
        System.out.println("Step " + step + " complete check: " + complete);
        return complete;
    }
    
    private void updateSummaryPanel() {
        if (citizen != null) {
            // Get the transaction ID from IDStatus and format it
            IDStatus idStatus = IDStatus.getStatusByCitizenId(citizen.getCitizenId());
            String transactionId = (idStatus != null && idStatus.getTransactionId() != null) 
                ? IDStatus.formatTransactionId(idStatus.getTransactionId())
                : "Not assigned";

            // Update citizen info labels
            lblName.setText(citizen.getFullName());
            // Show formatted Transaction ID
            lblTransactionId.setText(transactionId);
            lblPhone.setText(citizen.getPhone());

            if (selectedDate != null) {
                lblSelectedDate.setText(dateFormat.format(selectedDate));
                lblSelectedDay.setText(dayFormat.format(selectedDate));
            } else {
                lblSelectedDate.setText("Not selected");
                lblSelectedDay.setText("");
            }

            if (selectedTime != null) {
                lblSelectedTime.setText(selectedTime);
            } else {
                lblSelectedTime.setText("Not selected");
            }
        }
    }

    private void updateProgress() {
        // Remove the automatic calculation since we're setting fixed values
        // This method is called when date/time is selected, but we don't want it to change progress bar
        // Only updateUIForStep() should change the progress bar
    }
    
    private void updateConfirmDetailsPanel() {
        ConfirmDetailsPanel.removeAll();
        ConfirmDetailsPanel.setLayout(new java.awt.BorderLayout());
        
        JButton confirmButton = new JButton("Confirm Appointment");
        confirmButton.setBackground(new Color(97, 49, 237));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFont(new java.awt.Font("Segoe UI", 1, 14));
        confirmButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        confirmButton.addActionListener(e -> {
            boolean success = saveAppointment();
            if (success) {
                // Reset form
                selectedDate = null;
                selectedTime = null;
                currentStep = 1;
                updateUIForStep(currentStep);
                updateSummaryPanel();
                resetTimeSlots();
            }
        });
        
        ConfirmDetailsPanel.add(confirmButton, java.awt.BorderLayout.CENTER);
        ConfirmDetailsPanel.revalidate();
        ConfirmDetailsPanel.repaint();
    }
    
    private void resetTimeSlots() {
        // Reset all time slot buttons
        if (timeSlotsPanel != null) {
            for (java.awt.Component comp : timeSlotsPanel.getComponents()) {
                if (comp instanceof JButton) {
                    JButton btn = (JButton) comp;
                    btn.setBackground(new Color(240, 240, 240));
                    btn.setForeground(Color.BLACK);
                }
            }
        }
        
        // Also reset the calendar selection
        if (calendarCustom != null) {
            calendarCustom.setSelectedDate(null);
            forceCalendarRefresh();
        }
        customProgressBar.setCurrentStep(1);
    }

    private boolean saveAppointment() {
        if (citizen == null || selectedDate == null || selectedTime == null) {
            JOptionPane.showMessageDialog(this, "Please complete all appointment details.", 
                "Incomplete Information", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Get transaction ID
        IDStatus idStatus = IDStatus.getStatusByCitizenId(citizen.getCitizenId());
        String transactionId = (idStatus != null && idStatus.getTransactionId() != null) 
            ? idStatus.getTransactionId() 
            : "Not assigned";

        // Additional validation: Check if date is in the past
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar appointmentDate = Calendar.getInstance();
        appointmentDate.setTime(selectedDate);
        appointmentDate.set(Calendar.HOUR_OF_DAY, 0);
        appointmentDate.set(Calendar.MINUTE, 0);
        appointmentDate.set(Calendar.SECOND, 0);
        appointmentDate.set(Calendar.MILLISECOND, 0);

        if (appointmentDate.before(today)) {
            JOptionPane.showMessageDialog(this, 
                "Cannot schedule appointment for a past date.\nPlease select a future date.", 
                "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check if date is a weekend
        int dayOfWeek = appointmentDate.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            JOptionPane.showMessageDialog(this, 
                "Cannot schedule appointment on weekends.\nPlease select a weekday.", 
                "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check if date is a holiday
        if (calendarCustom != null && calendarCustom.isHolidayDate(selectedDate)) {
            JOptionPane.showMessageDialog(this, 
                "Cannot schedule appointment on a public holiday.\nPlease select a different date.", 
                "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check if date is already booked
        if (calendarCustom != null && calendarCustom.isBookedDate(selectedDate)) {
            JOptionPane.showMessageDialog(this, 
                "Selected date is fully booked.\nPlease select a different date.", 
                "Date Fully Booked", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Appointment appointment = new Appointment();
        appointment.setCitizenId(citizen.getCitizenId());
        appointment.setAppDate(new java.sql.Date(selectedDate.getTime()));
        appointment.setAppTime(selectedTime);
        appointment.setStatus("Scheduled");
        appointment.setCreatedDate(new java.sql.Date(System.currentTimeMillis()));

        boolean success = Appointment.addAppointment(appointment);

        if (success) {
            // Log activity with transaction ID
            ActivityLog.logActivity(currentUser.getUserId(), 
                "Scheduled appointment for Transaction ID: " + transactionId + 
                " on " + dateFormat.format(selectedDate) + " at " + selectedTime);

            // Send notification to citizen with transaction ID
            if (citizen.getUserId() != null) {
                String message = "Your appointment for Transaction ID: " + transactionId + 
                    " has been scheduled for " + dateFormat.format(selectedDate) + " at " + selectedTime;
                Notification.addNotification(citizen.getCitizenId(), message, "Appointment");
            }

            JOptionPane.showMessageDialog(this, 
                "Appointment scheduled successfully!\n\n" +
                "Transaction ID: " + transactionId + "\n" +
                "Name: " + citizen.getFullName() + "\n" +
                "Date: " + dateFormat.format(selectedDate) + "\n" +
                "Time: " + selectedTime + "\n" +
                "Status: Scheduled\n\n" +
                "Please arrive 15 minutes before your appointment time.\n" +
                "Bring your Transaction ID: " + transactionId,
                "Appointment Confirmed", JOptionPane.INFORMATION_MESSAGE);

            // Refresh calendar to show the new booking
            if (calendarCustom != null) {
                calendarCustom.reloadBookedDates();
                calendarCustom.repaint();
            }

            return true;
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to schedule appointment. Please try again.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private void initTimeSlots() {
        // Remove existing components and set layout
        SelectTimePanel.removeAll();
        SelectTimePanel.setLayout(new java.awt.BorderLayout());

        // Create main panel
        javax.swing.JPanel mainPanel = new javax.swing.JPanel();
        mainPanel.setLayout(new java.awt.BorderLayout());
        mainPanel.setBackground(new java.awt.Color(255, 255, 255));

        // Add title
        javax.swing.JLabel titleLabel = new javax.swing.JLabel("Select Time Slot", javax.swing.SwingConstants.CENTER);
        titleLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        titleLabel.setForeground(new java.awt.Color(97, 49, 237));
        titleLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 10, 0));
        mainPanel.add(titleLabel, java.awt.BorderLayout.NORTH);

        // Create time slots panel
        timeSlotsPanel = new javax.swing.JPanel();
        timeSlotsPanel.setLayout(new GridLayout(0, 2, 10, 10));
        timeSlotsPanel.setBackground(new java.awt.Color(255, 255, 255));

        // Available time slots
        String[] timeSlots = {
            "8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM",
            "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM",
            "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM",
            "3:00 PM", "3:30 PM", "4:00 PM"
        };

        for (String time : timeSlots) {
            JButton timeButton = new JButton(time);
            timeButton.setPreferredSize(new java.awt.Dimension(120, 40));
            timeButton.setBackground(new Color(240, 240, 240));
            timeButton.setForeground(Color.BLACK);
            timeButton.setFocusPainted(false);
            timeButton.addActionListener(e -> {
                JButton button = (JButton) e.getSource();
                selectedTime = button.getText();
                System.out.println("Time selected: " + selectedTime);
                updateSummaryPanel();
                updateProgress();
                ContinueButton.setEnabled(isStepComplete(currentStep));

                // Highlight selected button
                for (java.awt.Component comp : timeSlotsPanel.getComponents()) {
                    if (comp instanceof JButton) {
                        JButton btn = (JButton) comp;
                        if (btn.getText().equals(selectedTime)) {
                            btn.setBackground(new Color(97, 49, 237));
                            btn.setForeground(Color.WHITE);
                        } else {
                            btn.setBackground(new Color(240, 240, 240));
                            btn.setForeground(Color.BLACK);
                        }
                    }
                }
            });
            timeSlotsPanel.add(timeButton);
        }

        // Create scroll pane
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(timeSlotsPanel);
        scrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 50, 20, 50));
        scrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(new java.awt.Color(255, 255, 255));

        mainPanel.add(scrollPane, java.awt.BorderLayout.CENTER);

        // Add to SelectTimePanel
        SelectTimePanel.add(mainPanel);

        // Revalidate
        SelectTimePanel.revalidate();
        SelectTimePanel.repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ProgressHeaderPanel = new javax.swing.JPanel();
        customProgressBar = new component.Progress.CustomStepProgressBar();
        SummaryConfirmationPanel = new javax.swing.JPanel();
        PreviousButton = new javax.swing.JButton();
        ContinueButton = new javax.swing.JButton();
        lblName = new javax.swing.JLabel();
        lblTransactionId = new javax.swing.JLabel();
        lblPhone = new javax.swing.JLabel();
        lblSelectedDate = new javax.swing.JLabel();
        lblSelectedDay = new javax.swing.JLabel();
        lblSelectedTime = new javax.swing.JLabel();
        timeSlotsPanel = new javax.swing.JPanel();
        JLabel = new javax.swing.JLabel();
        JLabel1 = new javax.swing.JLabel();
        JLabel2 = new javax.swing.JLabel();
        JLabel3 = new javax.swing.JLabel();
        JLabel4 = new javax.swing.JLabel();
        JLabel5 = new javax.swing.JLabel();
        SchedulingTabbedPane = new component.NoTabJTabbedPane();
        SelectDatePanel = new javax.swing.JPanel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        EarliestAvailableButton = new javax.swing.JButton();
        ThisWeekButton = new javax.swing.JButton();
        NextWeekButton = new javax.swing.JButton();
        calendarCustom = new component.Calendar.CalendarCustom();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        SelectTimePanel = new javax.swing.JPanel();
        ConfirmDetailsPanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(250, 250, 250));
        setPreferredSize(new java.awt.Dimension(850, 550));

        ProgressHeaderPanel.setBackground(new java.awt.Color(255, 255, 255));
        ProgressHeaderPanel.setPreferredSize(new java.awt.Dimension(850, 60));

        javax.swing.GroupLayout ProgressHeaderPanelLayout = new javax.swing.GroupLayout(ProgressHeaderPanel);
        ProgressHeaderPanel.setLayout(ProgressHeaderPanelLayout);
        ProgressHeaderPanelLayout.setHorizontalGroup(
            ProgressHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(customProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE)
        );
        ProgressHeaderPanelLayout.setVerticalGroup(
            ProgressHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ProgressHeaderPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(customProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        SummaryConfirmationPanel.setBackground(new java.awt.Color(255, 255, 255));
        SummaryConfirmationPanel.setPreferredSize(new java.awt.Dimension(250, 500));

        PreviousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/previous.png"))); // NOI18N
        PreviousButton.setBorderPainted(false);
        PreviousButton.setContentAreaFilled(false);
        PreviousButton.setFocusPainted(false);
        PreviousButton.setPreferredSize(new java.awt.Dimension(50, 50));
        PreviousButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/previous_pressed.png"))); // NOI18N
        PreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PreviousButtonActionPerformed(evt);
            }
        });

        ContinueButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/continue.png"))); // NOI18N
        ContinueButton.setBorderPainted(false);
        ContinueButton.setContentAreaFilled(false);
        ContinueButton.setFocusPainted(false);
        ContinueButton.setPreferredSize(new java.awt.Dimension(50, 50));
        ContinueButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/continue_pressed.png"))); // NOI18N
        ContinueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ContinueButtonActionPerformed(evt);
            }
        });

        lblName.setPreferredSize(new java.awt.Dimension(90, 30));

        lblTransactionId.setPreferredSize(new java.awt.Dimension(90, 30));

        lblPhone.setPreferredSize(new java.awt.Dimension(90, 30));

        lblSelectedDate.setPreferredSize(new java.awt.Dimension(90, 30));

        lblSelectedDay.setPreferredSize(new java.awt.Dimension(90, 30));

        lblSelectedTime.setPreferredSize(new java.awt.Dimension(90, 30));

        timeSlotsPanel.setPreferredSize(new java.awt.Dimension(200, 35));

        javax.swing.GroupLayout timeSlotsPanelLayout = new javax.swing.GroupLayout(timeSlotsPanel);
        timeSlotsPanel.setLayout(timeSlotsPanelLayout);
        timeSlotsPanelLayout.setHorizontalGroup(
            timeSlotsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        timeSlotsPanelLayout.setVerticalGroup(
            timeSlotsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 35, Short.MAX_VALUE)
        );

        JLabel.setText("Name");
        JLabel.setPreferredSize(new java.awt.Dimension(100, 30));

        JLabel1.setText("Phone");
        JLabel1.setPreferredSize(new java.awt.Dimension(100, 30));

        JLabel2.setText("TRN");
        JLabel2.setPreferredSize(new java.awt.Dimension(100, 30));

        JLabel3.setText("Selected Time");
        JLabel3.setPreferredSize(new java.awt.Dimension(100, 30));

        JLabel4.setText("Selected Date");
        JLabel4.setPreferredSize(new java.awt.Dimension(100, 30));

        JLabel5.setText("Selected Day");
        JLabel5.setPreferredSize(new java.awt.Dimension(100, 30));

        javax.swing.GroupLayout SummaryConfirmationPanelLayout = new javax.swing.GroupLayout(SummaryConfirmationPanel);
        SummaryConfirmationPanel.setLayout(SummaryConfirmationPanelLayout);
        SummaryConfirmationPanelLayout.setHorizontalGroup(
            SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SummaryConfirmationPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(SummaryConfirmationPanelLayout.createSequentialGroup()
                        .addComponent(PreviousButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ContinueButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(timeSlotsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, SummaryConfirmationPanelLayout.createSequentialGroup()
                        .addGroup(SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(JLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(JLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(JLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(JLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTransactionId, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblPhone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblSelectedDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblSelectedTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, SummaryConfirmationPanelLayout.createSequentialGroup()
                        .addComponent(JLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSelectedDay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, SummaryConfirmationPanelLayout.createSequentialGroup()
                        .addComponent(JLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        SummaryConfirmationPanelLayout.setVerticalGroup(
            SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SummaryConfirmationPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(JLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTransactionId, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(JLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblPhone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(JLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblSelectedDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(JLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblSelectedDay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(JLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblSelectedTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(JLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(timeSlotsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ContinueButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PreviousButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        SchedulingTabbedPane.setBorder(null);
        SchedulingTabbedPane.setPreferredSize(new java.awt.Dimension(595, 500));

        SelectDatePanel.setBackground(new java.awt.Color(142, 217, 255));
        SelectDatePanel.setPreferredSize(new java.awt.Dimension(595, 500));

        jLayeredPane1.setPreferredSize(new java.awt.Dimension(300, 120));
        jLayeredPane1.setLayout(new java.awt.GridLayout(3, 3, 5, 5));

        EarliestAvailableButton.setText("Earliest Available");
        EarliestAvailableButton.setBorder(null);
        EarliestAvailableButton.setBorderPainted(false);
        EarliestAvailableButton.setPreferredSize(new java.awt.Dimension(150, 45));
        EarliestAvailableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EarliestAvailableButtonActionPerformed(evt);
            }
        });
        jLayeredPane1.add(EarliestAvailableButton);

        ThisWeekButton.setText("This Week");
        ThisWeekButton.setBorder(null);
        ThisWeekButton.setBorderPainted(false);
        ThisWeekButton.setPreferredSize(new java.awt.Dimension(150, 45));
        ThisWeekButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ThisWeekButtonActionPerformed(evt);
            }
        });
        jLayeredPane1.add(ThisWeekButton);

        NextWeekButton.setText("Next Week");
        NextWeekButton.setBorder(null);
        NextWeekButton.setBorderPainted(false);
        NextWeekButton.setPreferredSize(new java.awt.Dimension(150, 45));
        NextWeekButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NextWeekButtonActionPerformed(evt);
            }
        });
        jLayeredPane1.add(NextWeekButton);

        calendarCustom.setPreferredSize(new java.awt.Dimension(400, 300));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/component/Calendar/icon/circle_today.png"))); // NOI18N
        jLabel1.setText("Today");
        jLabel1.setIconTextGap(2);
        jLabel1.setPreferredSize(new java.awt.Dimension(16, 16));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/component/Calendar/icon/circle_available.png"))); // NOI18N
        jLabel2.setText("Available");
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel2.setIconTextGap(2);
        jLabel2.setPreferredSize(new java.awt.Dimension(16, 16));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/component/Calendar/icon/circle_selected.png"))); // NOI18N
        jLabel3.setText("Selected");
        jLabel3.setIconTextGap(2);
        jLabel3.setPreferredSize(new java.awt.Dimension(16, 16));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/component/Calendar/icon/circle_weekend.png"))); // NOI18N
        jLabel4.setText("Weekend");
        jLabel4.setIconTextGap(2);
        jLabel4.setPreferredSize(new java.awt.Dimension(16, 16));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/component/Calendar/icon/circle_holiday.png"))); // NOI18N
        jLabel5.setText("Holiday");
        jLabel5.setIconTextGap(2);
        jLabel5.setPreferredSize(new java.awt.Dimension(16, 16));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/component/Calendar/icon/circle_booked.png"))); // NOI18N
        jLabel6.setText("Booked");
        jLabel6.setIconTextGap(2);
        jLabel6.setPreferredSize(new java.awt.Dimension(16, 16));

        javax.swing.GroupLayout SelectDatePanelLayout = new javax.swing.GroupLayout(SelectDatePanel);
        SelectDatePanel.setLayout(SelectDatePanelLayout);
        SelectDatePanelLayout.setHorizontalGroup(
            SelectDatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SelectDatePanelLayout.createSequentialGroup()
                .addGap(145, 145, 145)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(145, 145, 145))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SelectDatePanelLayout.createSequentialGroup()
                .addGap(95, 95, 95)
                .addGroup(SelectDatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SelectDatePanelLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(SelectDatePanelLayout.createSequentialGroup()
                        .addComponent(calendarCustom, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                        .addGap(2, 2, 2)))
                .addGap(95, 95, 95))
        );
        SelectDatePanelLayout.setVerticalGroup(
            SelectDatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SelectDatePanelLayout.createSequentialGroup()
                .addGap(104, 104, 104)
                .addGroup(SelectDatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addComponent(calendarCustom, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );

        SchedulingTabbedPane.addTab("tab1", SelectDatePanel);

        SelectTimePanel.setBackground(new java.awt.Color(142, 217, 255));

        javax.swing.GroupLayout SelectTimePanelLayout = new javax.swing.GroupLayout(SelectTimePanel);
        SelectTimePanel.setLayout(SelectTimePanelLayout);
        SelectTimePanelLayout.setHorizontalGroup(
            SelectTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        SelectTimePanelLayout.setVerticalGroup(
            SelectTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        SchedulingTabbedPane.addTab("tab2", SelectTimePanel);

        ConfirmDetailsPanel.setBackground(new java.awt.Color(142, 217, 255));

        javax.swing.GroupLayout ConfirmDetailsPanelLayout = new javax.swing.GroupLayout(ConfirmDetailsPanel);
        ConfirmDetailsPanel.setLayout(ConfirmDetailsPanelLayout);
        ConfirmDetailsPanelLayout.setHorizontalGroup(
            ConfirmDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 612, Short.MAX_VALUE)
        );
        ConfirmDetailsPanelLayout.setVerticalGroup(
            ConfirmDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 485, Short.MAX_VALUE)
        );

        SchedulingTabbedPane.addTab("tab3", ConfirmDetailsPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ProgressHeaderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(SchedulingTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(SummaryConfirmationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(ProgressHeaderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SchedulingTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                    .addComponent(SummaryConfirmationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void PreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PreviousButtonActionPerformed
        if (currentStep > 1) {
            currentStep--;
            updateUIForStep(currentStep);
        }
    }//GEN-LAST:event_PreviousButtonActionPerformed

    private void EarliestAvailableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EarliestAvailableButtonActionPerformed
        calendarCustom.goToEarliestAvailable();
    }//GEN-LAST:event_EarliestAvailableButtonActionPerformed

    private void NextWeekButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NextWeekButtonActionPerformed
        calendarCustom.goToNextWeek();
    }//GEN-LAST:event_NextWeekButtonActionPerformed

    private void ThisWeekButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ThisWeekButtonActionPerformed
        calendarCustom.goToThisWeek();
    }//GEN-LAST:event_ThisWeekButtonActionPerformed

    private void ContinueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ContinueButtonActionPerformed
        if (currentStep < 3) {
            if (isStepComplete(currentStep)) {
                currentStep++;
                updateUIForStep(currentStep);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please complete the current step before continuing.", 
                    "Incomplete Step", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            // Get transaction ID
            IDStatus idStatus = IDStatus.getStatusByCitizenId(citizen.getCitizenId());
            String transactionId = (idStatus != null && idStatus.getTransactionId() != null) 
                ? idStatus.getTransactionId() 
                : "Not assigned";

            // Final confirmation step
            int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm appointment details:\n\n" +
                "Transaction ID: " + transactionId + "\n" +
                "Date: " + dateFormat.format(selectedDate) + "\n" +
                "Time: " + selectedTime + "\n" +
                "Citizen: " + citizen.getFullName() + "\n" +
                "First Name: " + citizen.getFname() + "\n" +
                "Last Name: " + citizen.getLname() + "\n\n" +
                "Are you sure you want to schedule this appointment?",
                "Confirm Appointment", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = saveAppointment();
                if (success) {
                    // Reset form for new appointment
                    selectedDate = null;
                    selectedTime = null;
                    currentStep = 1;
                    updateUIForStep(currentStep);
                    updateSummaryPanel();
                    resetTimeSlots();
                }
            }
        }

    }//GEN-LAST:event_ContinueButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ConfirmDetailsPanel;
    private javax.swing.JButton ContinueButton;
    private javax.swing.JButton EarliestAvailableButton;
    private javax.swing.JLabel JLabel;
    private javax.swing.JLabel JLabel1;
    private javax.swing.JLabel JLabel2;
    private javax.swing.JLabel JLabel3;
    private javax.swing.JLabel JLabel4;
    private javax.swing.JLabel JLabel5;
    private javax.swing.JButton NextWeekButton;
    private javax.swing.JButton PreviousButton;
    private javax.swing.JPanel ProgressHeaderPanel;
    private component.NoTabJTabbedPane SchedulingTabbedPane;
    private javax.swing.JPanel SelectDatePanel;
    private javax.swing.JPanel SelectTimePanel;
    private javax.swing.JPanel SummaryConfirmationPanel;
    private javax.swing.JButton ThisWeekButton;
    private component.Calendar.CalendarCustom calendarCustom;
    private component.Progress.CustomStepProgressBar customProgressBar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPhone;
    private javax.swing.JLabel lblSelectedDate;
    private javax.swing.JLabel lblSelectedDay;
    private javax.swing.JLabel lblSelectedTime;
    private javax.swing.JLabel lblTransactionId;
    private javax.swing.JPanel timeSlotsPanel;
    // End of variables declaration//GEN-END:variables
}
