package component;

import backend.objects.Data.*;
import component.Calendar.CalendarCustom.CalendarCustomListener;
import backend.objects.Data.IDStatus;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Scheduling extends javax.swing.JPanel {

    private User currentUser;
    private Citizen citizen;
    private Date selectedDate;
    private String selectedTime;
    private int currentStep = 1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
    private SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
    private SimpleDateFormat fullDayFormat = new SimpleDateFormat("EEEE"); // For lblSelectedDay
    private boolean calendarInitialized = false;
    
    private Appointment existingAppointment; // Add this field
    private boolean isRescheduling;
    
        public Scheduling(User user) {
        this(user, null, null);
    }

    // Main constructor for both new appointments and rescheduling
    public Scheduling(User user, Citizen citizen, Appointment existingAppointment) {
        this.currentUser = user;
        this.citizen = citizen;
        this.existingAppointment = existingAppointment;
        this.isRescheduling = existingAppointment != null;

        initComponents();

        // Check eligibility before proceeding
        if (!checkIDStatusEligibility() && !isRescheduling) {
            showNotEligibleMessage();
            disableForm();
            return;
        }

        configureProgressBar();
        initCitizenData();
        updateUIForStep(currentStep);

        // Add tab change listener FIRST - BEFORE any initialization
        SchedulingTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                int selectedIndex = SchedulingTabbedPane.getSelectedIndex();
                System.out.println("Tab changed to index: " + selectedIndex + " (Step: " + (selectedIndex + 1) + ")");

                // Update current step based on tab
                currentStep = selectedIndex + 1;
                updateUIForStep(currentStep);

                // Initialize calendar when date tab is selected
                if (selectedIndex == 0) {
                    SwingUtilities.invokeLater(() -> {
                        initCalendarIfNeeded();
                    });
                }
            }
        });
        
        // Initialize time slots immediately (lightweight)
        initTimeSlots();
        
        // Set initial tab to step 1
        SchedulingTabbedPane.setSelectedIndex(0);
        
        // Initialize calendar immediately (not lazy) with proper timing
        SwingUtilities.invokeLater(() -> {
            initCalendar();
            calendarInitialized = true;
        });
        
        // If rescheduling, pre-populate with existing appointment data
        if (isRescheduling && existingAppointment != null) {
            prePopulateAppointmentData();
        }
    }
    
    private void showNotEligibleMessage() {
        String currentStatus = getCurrentIDStatus();

        String message = "You cannot schedule a pickup appointment at this time.\n\n" +
                        "Your current ID status is: " + currentStatus + "\n" +
                        "You can only schedule an appointment when your ID status is 'Ready for Pickup'.\n\n" +
                        "Please check your ID Status page for updates on your application progress.";

        JOptionPane.showMessageDialog(this, 
            message, 
            "Appointment Not Available", 
            JOptionPane.WARNING_MESSAGE);

        // Update UI to show current status
        lblName.setText("Appointment Not Available");
        lblTransactionId.setText("Status: " + currentStatus);
        lblPhone.setText("");
        lblSelectedDate.setText("");
        lblSelectedDay.setText("");
        lblSelectedTime.setText("");
    }

    private void disableForm() {
        // Disable all interactive components
        SchedulingTabbedPane.setEnabled(false);
        PreviousButton.setEnabled(false);
        ContinueButton.setEnabled(false);
        EarliestAvailableButton.setEnabled(false);
        ThisWeekButton.setEnabled(false);
        NextWeekButton.setEnabled(false);

        if (calendarCustom != null) {
            calendarCustom.setEnabled(false);
        }

        // Disable time slots
        if (timeSlotsPanel != null) {
            for (java.awt.Component comp : timeSlotsPanel.getComponents()) {
                if (comp instanceof JButton) {
                    comp.setEnabled(false);
                }
            }
        }
    }
    
    private boolean checkIDStatusEligibility() {
        if (citizen == null) {
            return false;
        }

        try {
            IDStatus idStatus = IDStatus.getStatusByCitizenId(citizen.getCitizenId());
            if (idStatus == null) {
                System.out.println("No ID status found for citizen ID: " + citizen.getCitizenId());
                return false;
            }

            String currentStatus = idStatus.getStatus();
            System.out.println("Current ID Status: " + currentStatus);

            // Check if status is "Ready for Pickup" or "Ready"
            if (currentStatus != null) {
                String normalizedStatus = currentStatus.toUpperCase().trim();
                return normalizedStatus.contains("READY") || 
                       normalizedStatus.contains("READY FOR PICKUP") ||
                       normalizedStatus.equals("READY_FOR_PICKUP") ||
                       normalizedStatus.equals("STAT-009");
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error checking ID status eligibility: " + e.getMessage());
            return false;
        }
    }
    
    public void refreshEligibility() {
        SwingUtilities.invokeLater(() -> {
            boolean isEligible = checkIDStatusEligibility() || isRescheduling;

            // Enable/disable form based on eligibility
            SchedulingTabbedPane.setEnabled(isEligible);
            PreviousButton.setEnabled(isEligible && currentStep > 1);
            ContinueButton.setEnabled(isEligible && isStepComplete(currentStep));
            EarliestAvailableButton.setEnabled(isEligible);
            ThisWeekButton.setEnabled(isEligible);
            NextWeekButton.setEnabled(isEligible);

            if (calendarCustom != null) {
                calendarCustom.setEnabled(isEligible);
            }

            // Enable/disable time slots
            if (timeSlotsPanel != null) {
                for (java.awt.Component comp : timeSlotsPanel.getComponents()) {
                    comp.setEnabled(isEligible);
                }
            }

            // Update summary panel
            updateSummaryPanel();

            if (!isEligible && !isRescheduling) {
                // Show message in the form
                javax.swing.JLabel messageLabel = new javax.swing.JLabel(
                    "⚠️ Your ID is not ready for pickup. Current status: " + getCurrentIDStatus());
                messageLabel.setForeground(Color.RED);
                messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

                // Add message to top of the panel
                this.add(messageLabel, java.awt.BorderLayout.NORTH);
                this.revalidate();
                this.repaint();
            }
        });
    }

    private String getCurrentIDStatus() {
        if (citizen == null) {
            return "No citizen data";
        }

        try {
            IDStatus idStatus = IDStatus.getStatusByCitizenId(citizen.getCitizenId());
            return (idStatus != null && idStatus.getStatus() != null) ? 
                   idStatus.getStatus() : "No status found";
        } catch (Exception e) {
            return "Error retrieving status";
        }
    }
    
    private void prePopulateAppointmentData() {
        if (existingAppointment != null) {
            // Pre-select the date from existing appointment
            selectedDate = existingAppointment.getAppDate();
            selectedTime = existingAppointment.getAppTime();
            
            // Update summary panel
            updateSummaryPanel();
            
            // Highlight selected time slot
            if (selectedTime != null && timeSlotsPanel != null) {
                for (java.awt.Component comp : timeSlotsPanel.getComponents()) {
                    if (comp instanceof JButton) {
                        JButton btn = (JButton) comp;
                        if (btn.getText().equals(selectedTime)) {
                            btn.setBackground(new Color(97, 49, 237));
                            btn.setForeground(Color.WHITE);
                        }
                    }
                }
            }
            
            // Update progress bar to show current step is complete
            ContinueButton.setEnabled(true);
            
            System.out.println("Pre-populated with existing appointment data");
        }
    }
    
    private void ensureCalendarVisible() {
        if (calendarCustom != null) {
            // Check if calendar is properly sized
            if (calendarCustom.getWidth() == 0 || calendarCustom.getHeight() == 0) {
                System.out.println("Calendar has zero size - resizing");
                calendarCustom.setSize(SelectDatePanel.getSize());
            }

            // Force component hierarchy update
            calendarCustom.invalidate();
            calendarCustom.validate();
            calendarCustom.repaint();

            // Force parent update
            SelectDatePanel.invalidate();
            SelectDatePanel.validate();
            SelectDatePanel.repaint();

            // Force tab update
            SchedulingTabbedPane.invalidate();
            SchedulingTabbedPane.validate();
            SchedulingTabbedPane.repaint();
            
            System.out.println("Forced calendar visibility update");
        }
    }
    
    private void initCalendarIfNeeded() {
        if (!calendarInitialized) {
            System.out.println("Lazy initializing calendar...");
            initCalendar();
            calendarInitialized = true;
        } else {
            System.out.println("Calendar already initialized - just refreshing");
            refreshCalendar();
        }

        // Force the calendar to become visible and properly sized
        SwingUtilities.invokeLater(() -> {
            calendarCustom.setVisible(true);
            calendarCustom.setSize(SelectDatePanel.getSize());

            // Force layout
            calendarCustom.revalidate();
            calendarCustom.repaint();
            SelectDatePanel.revalidate();
            SelectDatePanel.repaint();

            System.out.println("Calendar forced to repaint");
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
        try {
            // If citizen is passed in constructor, use it
            if (citizen == null) {
                // Otherwise get citizen data for the current user
                this.citizen = Citizen.getCitizenByUserId(currentUser.getUserId());
            }

            if (citizen != null) {
                System.out.println("Found citizen: " + citizen.getFullName() + " (ID: " + citizen.getCitizenId() + ")");

                // Check ID status eligibility (except for rescheduling)
                if (!isRescheduling && !checkIDStatusEligibility()) {
                    System.out.println("Citizen not eligible for scheduling - ID status not ready");
                    // Show status in summary panel
                    lblName.setText(citizen.getFullName());
                    lblTransactionId.setText("Status: " + getCurrentIDStatus());
                    lblPhone.setText(citizen.getPhone() != null ? citizen.getPhone() : "Not provided");
                    return;
                }

                // Try to get transaction ID from IDStatus
                try {
                    IDStatus idStatus = IDStatus.getStatusByCitizenId(citizen.getCitizenId());
                    if (idStatus != null) {
                        String transactionId = idStatus.getTransactionId();
                        if (transactionId != null && !transactionId.trim().isEmpty()) {
                            System.out.println("Transaction ID found: " + transactionId);
                            System.out.println("Formatted Transaction ID: " + IDStatus.formatTransactionId(transactionId));
                        } else {
                            System.out.println("No transaction ID found for citizen ID: " + citizen.getCitizenId());
                        }
                    } else {
                        System.out.println("No ID status found for citizen ID: " + citizen.getCitizenId());
                        // Try to create a default status if none exists
                        createDefaultIDStatus();
                    }
                } catch (Exception e) {
                    System.err.println("Error getting ID status: " + e.getMessage());
                    e.printStackTrace();
                }

                updateSummaryPanel();
            } else {
                System.err.println("ERROR: No citizen found for user ID: " + currentUser.getUserId());
                // Try to create a default citizen
                createDefaultCitizen();
            }
        } catch (Exception e) {
            System.err.println("Error in initCitizenData: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createDefaultIDStatus() {
        try {
            if (citizen == null) return;

            IDStatus defaultStatus = new IDStatus();
            defaultStatus.setCitizenId(citizen.getCitizenId());
            defaultStatus.setTransactionId(IDStatus.generateTransactionId(citizen.getCitizenId()));

            // Set status_name_id to 1 for "Submitted" status (from your status_names table)
            defaultStatus.setStatusNameId(1);
            defaultStatus.setUpdateDate(new java.sql.Date(System.currentTimeMillis()));
            defaultStatus.setNotes("Initial application submitted");

            boolean success = IDStatus.addStatus(defaultStatus);
            if (success) {
                System.out.println("Created default ID status for citizen ID: " + citizen.getCitizenId());
            } else {
                System.err.println("Failed to create default ID status");
            }
        } catch (Exception e) {
            System.err.println("Error creating default ID status: " + e.getMessage());
        }
    }

    private void createDefaultCitizen() {
        try {
            // Create a new citizen record for the user
            Citizen newCitizen = new Citizen();
            newCitizen.setUserId(currentUser.getUserId());
            newCitizen.setFname(currentUser.getFname() != null ? currentUser.getFname() : "Unknown");
            newCitizen.setMname(currentUser.getMname());
            newCitizen.setLname(currentUser.getLname() != null ? currentUser.getLname() : "User");
            newCitizen.setGender("Not Specified");
            newCitizen.setApplicationDate(new java.sql.Date(System.currentTimeMillis()));

            // Add citizen
            int citizenId = Citizen.addCitizenAndGetId(newCitizen);
            if (citizenId > 0) {
                this.citizen = Citizen.getCitizenByUserId(currentUser.getUserId());
                System.out.println("Created default citizen with ID: " + citizenId);

                // Create default ID status
                createDefaultIDStatus();
            }
        } catch (Exception e) {
            System.err.println("Error creating default citizen: " + e.getMessage());
        }
    }
    
    private void initCalendar() {
        try {
            System.out.println("Initializing calendar...");

            // Make sure calendar is visible and properly sized
            calendarCustom.setVisible(true);

            // Set cursor for the entire calendar
            calendarCustom.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            // Initialize with a listener
            calendarCustom.setListener(new CalendarCustomListener() {
                @Override
                public void dateSelected(Date date) {
                    if (date == null) {
                        System.out.println("Date selection cleared (null)");
                        selectedDate = null;
                        updateSummaryPanel();
                        updateProgress();
                        ContinueButton.setEnabled(isStepComplete(currentStep));
                        return;
                    }

                    System.out.println("Date selected: " + date);
                    selectedDate = date;

                    // Update the summary panel with formatted date and day
                    updateSummaryPanel();
                    updateProgress();
                    ContinueButton.setEnabled(isStepComplete(currentStep));

                    // Check if date is available
                    try {
                        if (!calendarCustom.isAvailableDate(date)) {
                            JOptionPane.showMessageDialog(Scheduling.this, 
                                "Selected date is not available. Please choose another date.", 
                                "Date Not Available", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (Exception e) {
                        System.err.println("Error checking date availability: " + e.getMessage());
                    }
                }

                @Override
                public void monthChanged(int month, int year) {
                    System.out.println("Month changed to: " + month + "/" + year);
                    // Reload booked dates when month changes
                    try {
                        calendarCustom.reloadBookedDates();
                        calendarCustom.repaint();
                        SelectDatePanel.repaint();
                    } catch (Exception e) {
                        System.err.println("Error reloading booked dates: " + e.getMessage());
                    }
                }
            });

            // Load data and go to earliest available
            try {
                calendarCustom.reloadBookedDates();
                calendarCustom.goToEarliestAvailable();
            } catch (Exception e) {
                System.err.println("Error loading calendar data: " + e.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Error loading calendar data. Please try again.", 
                    "Calendar Error", JOptionPane.ERROR_MESSAGE);
            }

            // Add a small delay to ensure PanelSlide is ready
            SwingUtilities.invokeLater(() -> {
                try {
                    // Force the PanelSlide to initialize its components
                    java.awt.Component[] components = calendarCustom.getComponents();
                    for (java.awt.Component comp : components) {
                        if (comp instanceof component.Calendar.swing.PanelSlide) {
                            System.out.println("Found PanelSlide component - forcing repaint");
                            comp.revalidate();
                            comp.repaint();
                        }
                    }

                    // Force initial repaint
                    calendarCustom.repaint();
                    SelectDatePanel.repaint();

                    System.out.println("Calendar initialization complete");
                } catch (Exception e) {
                    System.err.println("Error in calendar initialization: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Error initializing calendar: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Failed to initialize calendar. Please restart the application.", 
                "Initialization Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void forceCalendarRefresh() {
        if (calendarCustom != null) {
            System.out.println("Forcing calendar refresh...");

            // First ensure calendar is visible
            ensureCalendarVisible();

            // Reload booked dates
            calendarCustom.reloadBookedDates();

            // Force PanelSlide to reinitialize
            java.awt.Component[] components = calendarCustom.getComponents();
            for (java.awt.Component comp : components) {
                if (comp instanceof component.Calendar.swing.PanelSlide) {
                    System.out.println("Refreshing PanelSlide");
                    comp.revalidate();
                    comp.repaint();

                    // Also refresh any PanelDate components inside PanelSlide
                    java.awt.Component[] panelSlideComponents = ((java.awt.Container)comp).getComponents();
                    for (java.awt.Component panelDateComp : panelSlideComponents) {
                        panelDateComp.revalidate();
                        panelDateComp.repaint();
                    }
                }
            }

            // Update selected date if exists
            if (selectedDate != null) {
                calendarCustom.setSelectedDate(selectedDate);
            }

            // Force repaints
            calendarCustom.revalidate();
            calendarCustom.repaint();
            SelectDatePanel.revalidate();
            SelectDatePanel.repaint();

            System.out.println("Calendar refresh complete");
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
            // Update citizen info labels
            lblName.setText(citizen.getFullName());
            lblPhone.setText(citizen.getPhone() != null ? citizen.getPhone() : "Not provided");

            // Get the transaction ID from IDStatus
            String transactionId = "Not assigned";
            try {
                IDStatus idStatus = IDStatus.getStatusByCitizenId(citizen.getCitizenId());
                if (idStatus != null && idStatus.getTransactionId() != null && !idStatus.getTransactionId().trim().isEmpty()) {
                    transactionId = IDStatus.formatTransactionId(idStatus.getTransactionId());
                }
            } catch (Exception e) {
                System.err.println("Error getting transaction ID: " + e.getMessage());
            }
            lblTransactionId.setText(transactionId);

            // Add status indicator
            String currentStatus = getCurrentIDStatus();
            boolean isReady = checkIDStatusEligibility();

            // Update panel colors based on eligibility
            if (isReady || isRescheduling) {
                SummaryConfirmationPanel.setBackground(new Color(255, 255, 255));
                SchedulingTabbedPane.setBackground(new Color(142, 217, 255));
            } else {
                SummaryConfirmationPanel.setBackground(new Color(240, 240, 240));
                SchedulingTabbedPane.setBackground(new Color(220, 220, 220));

                // Add status message
                JLabel statusLabel = new JLabel("Status: " + currentStatus + " (Not Ready for Pickup)");
                statusLabel.setForeground(Color.RED);
                statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

                // Add to summary panel if not already there
                if (SummaryConfirmationPanel.getComponentCount() < 10) {
                    SummaryConfirmationPanel.add(statusLabel);
                }
            }

            if (selectedDate != null) {
                // Format date and day properly
                String formattedDate = dateFormat.format(selectedDate);
                String formattedDay = fullDayFormat.format(selectedDate);

                lblSelectedDate.setText(formattedDate);
                lblSelectedDay.setText(formattedDay);

                System.out.println("Selected date: " + formattedDate + " (" + formattedDay + ")");
            } else {
                lblSelectedDate.setText("Not selected");
                lblSelectedDay.setText(""); // Clear the day label
            }

            if (selectedTime != null) {
                lblSelectedTime.setText(selectedTime);
            } else {
                lblSelectedTime.setText("Not selected");
            }
        } else {
            System.out.println("Citizen is null - cannot update summary");
            lblName.setText("No citizen data");
            lblTransactionId.setText("Not assigned");
            lblPhone.setText("Not available");
            lblSelectedDate.setText("Not selected");
            lblSelectedDay.setText("");
            lblSelectedTime.setText("Not selected");
        }

        // Revalidate the panel
        SummaryConfirmationPanel.revalidate();
        SummaryConfirmationPanel.repaint();
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
                // Reset form WITHOUT calling resetTimeSlots()
                selectedDate = null;
                selectedTime = null;
                currentStep = 1;
                updateUIForStep(currentStep);
                updateSummaryPanel();

                // Clear time slots visually without triggering calendar
                if (timeSlotsPanel != null) {
                    for (java.awt.Component comp : timeSlotsPanel.getComponents()) {
                        if (comp instanceof JButton) {
                            JButton btn = (JButton) comp;
                            btn.setBackground(new Color(240, 240, 240));
                            btn.setForeground(Color.BLACK);
                        }
                    }
                }

                // Refresh calendar without setting selected date to null
                if (calendarCustom != null) {
                    calendarCustom.reloadBookedDates();
                    calendarCustom.repaint();
                }

                customProgressBar.setCurrentStep(1);
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

        // Don't call this line - it causes the NPE
        // if (calendarCustom != null) {
        //     calendarCustom.setSelectedDate(null); // REMOVE THIS LINE
        // }

        // Just reset the date variable
        selectedDate = null;
        selectedTime = null;

        customProgressBar.setCurrentStep(1);
        updateSummaryPanel();
        forceCalendarRefresh();
    }
    
    private boolean saveAppointment() {
        // Check eligibility before saving
        if (!isRescheduling && !checkIDStatusEligibility()) {
            String currentStatus = getCurrentIDStatus();
            JOptionPane.showMessageDialog(this, 
                "Cannot schedule appointment. Your ID status is: " + currentStatus + "\n" +
                "You can only schedule when your ID is 'Ready for Pickup'.",
                "ID Not Ready", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (citizen == null || selectedDate == null || selectedTime == null) {
            JOptionPane.showMessageDialog(this, "Please complete all appointment details.", 
                "Incomplete Information", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Get transaction ID with error handling
        String transactionId = "Not assigned";
        try {
            IDStatus idStatus = IDStatus.getStatusByCitizenId(citizen.getCitizenId());
            if (idStatus != null && idStatus.getTransactionId() != null && !idStatus.getTransactionId().trim().isEmpty()) {
                transactionId = idStatus.getTransactionId();
            }
        } catch (Exception e) {
            System.err.println("Error getting transaction ID: " + e.getMessage());
        }

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

        // Check if date is already booked (excluding current appointment if rescheduling)
        if (calendarCustom != null) {
            boolean success;
            String actionType;
            
            // For rescheduling, we need to check if it's a different date
            if (isRescheduling && existingAppointment != null) {
                // Check if ID is still ready for rescheduling
                if (!checkIDStatusEligibility()) {
                    String currentStatus = getCurrentIDStatus();
                    JOptionPane.showMessageDialog(this, 
                        "Cannot reschedule appointment. Your ID status is: " + currentStatus + "\n" +
                        "You can only reschedule when your ID is 'Ready for Pickup'.",
                        "ID Not Ready", 
                        JOptionPane.WARNING_MESSAGE);
                    return false;
                }

                // Update existing appointment
                existingAppointment.setAppDate(new java.sql.Date(selectedDate.getTime()));
                existingAppointment.setAppTime(selectedTime);
                existingAppointment.setStatus("Rescheduled");
                existingAppointment.setCreatedDate(new java.sql.Date(System.currentTimeMillis()));

                success = Appointment.updateAppointment(existingAppointment);
                actionType = "Rescheduled";
            } else {
                // Create new appointment
                Appointment appointment = new Appointment();
                appointment.setCitizenId(citizen.getCitizenId());
                appointment.setAppDate(new java.sql.Date(selectedDate.getTime()));
                appointment.setAppTime(selectedTime);
                appointment.setStatus("Scheduled");
                appointment.setCreatedDate(new java.sql.Date(System.currentTimeMillis()));

                success = Appointment.addAppointment(appointment);
                actionType = "Scheduled";
            }
        }

        boolean success;
        String actionType;

        if (isRescheduling && existingAppointment != null) {
            // Update existing appointment
            existingAppointment.setAppDate(new java.sql.Date(selectedDate.getTime()));
            existingAppointment.setAppTime(selectedTime);
            existingAppointment.setStatus("Rescheduled");
            existingAppointment.setCreatedDate(new java.sql.Date(System.currentTimeMillis()));

            success = Appointment.updateAppointment(existingAppointment);
            actionType = "Rescheduled";
        } else {
            // Create new appointment
            Appointment appointment = new Appointment();
            appointment.setCitizenId(citizen.getCitizenId());
            appointment.setAppDate(new java.sql.Date(selectedDate.getTime()));
            appointment.setAppTime(selectedTime);
            appointment.setStatus("Scheduled");
            appointment.setCreatedDate(new java.sql.Date(System.currentTimeMillis()));

            success = Appointment.addAppointment(appointment);
            actionType = "Scheduled";
        }

        if (success) {
            // Format transaction ID for display
            String formattedTransactionId = IDStatus.formatTransactionId(transactionId);

            // Log activity with transaction ID
            ActivityLog.logActivity(currentUser.getUserId(), 
                actionType + " appointment for " + citizen.getFullName() + 
                " on " + dateFormat.format(selectedDate) + " at " + selectedTime);

            // Send notification to citizen with transaction ID
            String message = "Your appointment has been " + actionType.toLowerCase() + " for " + 
                dateFormat.format(selectedDate) + " at " + selectedTime;
            if (!"Not assigned".equals(transactionId)) {
                message += "\nTransaction ID: " + formattedTransactionId;
            }
            Notification.addNotification(citizen.getCitizenId(), message, "Appointment");

            JOptionPane.showMessageDialog(this, 
                "Appointment " + actionType.toLowerCase() + " successfully!\n\n" +
                "Name: " + citizen.getFullName() + "\n" +
                "Date: " + dateFormat.format(selectedDate) + "\n" +
                "Time: " + selectedTime + "\n" +
                (!"Not assigned".equals(transactionId) ? "Transaction ID: " + formattedTransactionId + "\n" : "") +
                "Status: " + (isRescheduling ? "Rescheduled" : "Scheduled") + "\n\n" +
                "Please arrive 15 minutes before your appointment time.\n" +
                (!"Not assigned".equals(transactionId) ? "Bring your Transaction ID: " + formattedTransactionId + "\n" : ""),
                "Appointment " + actionType, JOptionPane.INFORMATION_MESSAGE);

            // Refresh calendar to show the new booking
            if (calendarCustom != null) {
                calendarCustom.reloadBookedDates();
                calendarCustom.repaint();
            }

            return true;
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to " + actionType.toLowerCase() + " appointment. Please try again.", 
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
        jSeparator1 = new javax.swing.JSeparator();
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
        EarliestAvailableButton = new component.Button.FlatButton();
        ThisWeekButton = new component.Button.FlatButton();
        NextWeekButton = new component.Button.FlatButton();
        DateIndicatorsPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        calendarCustom = new component.Calendar.CalendarCustom();
        SelectTimePanel = new javax.swing.JPanel();
        ConfirmDetailsPanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(250, 250, 250));
        setPreferredSize(new java.awt.Dimension(850, 550));

        ProgressHeaderPanel.setBackground(new java.awt.Color(255, 255, 255));
        ProgressHeaderPanel.setPreferredSize(new java.awt.Dimension(850, 60));

        customProgressBar.setCurrentStep(1);
        customProgressBar.setTotalSteps(3);

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

        PreviousButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        PreviousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/previous.png"))); // NOI18N
        PreviousButton.setText("Go Back");
        PreviousButton.setBorderPainted(false);
        PreviousButton.setContentAreaFilled(false);
        PreviousButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        PreviousButton.setFocusPainted(false);
        PreviousButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        PreviousButton.setPreferredSize(new java.awt.Dimension(120, 50));
        PreviousButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/previous_pressed.png"))); // NOI18N
        PreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PreviousButtonActionPerformed(evt);
            }
        });

        ContinueButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ContinueButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/continue.png"))); // NOI18N
        ContinueButton.setText("Continue");
        ContinueButton.setBorderPainted(false);
        ContinueButton.setContentAreaFilled(false);
        ContinueButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ContinueButton.setFocusPainted(false);
        ContinueButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        ContinueButton.setPreferredSize(new java.awt.Dimension(120, 50));
        ContinueButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/continue_pressed.png"))); // NOI18N
        ContinueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ContinueButtonActionPerformed(evt);
            }
        });

        lblName.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblName.setPreferredSize(new java.awt.Dimension(90, 30));

        lblTransactionId.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTransactionId.setPreferredSize(new java.awt.Dimension(90, 30));

        lblPhone.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblPhone.setPreferredSize(new java.awt.Dimension(90, 30));

        lblSelectedDate.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSelectedDate.setPreferredSize(new java.awt.Dimension(90, 30));

        lblSelectedDay.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSelectedDay.setPreferredSize(new java.awt.Dimension(90, 30));

        lblSelectedTime.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSelectedTime.setPreferredSize(new java.awt.Dimension(90, 30));

        jSeparator1.setBackground(new java.awt.Color(0, 120, 215));
        jSeparator1.setForeground(new java.awt.Color(0, 120, 215));
        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

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

        JLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        JLabel.setText("Name:");
        JLabel.setToolTipText("");
        JLabel.setPreferredSize(new java.awt.Dimension(100, 30));

        JLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        JLabel1.setText("Phone:");
        JLabel1.setPreferredSize(new java.awt.Dimension(100, 30));

        JLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        JLabel2.setText("TRN:");
        JLabel2.setPreferredSize(new java.awt.Dimension(100, 30));

        JLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        JLabel3.setText("Selected Time:");
        JLabel3.setPreferredSize(new java.awt.Dimension(100, 30));

        JLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        JLabel4.setText("Selected Date:");
        JLabel4.setPreferredSize(new java.awt.Dimension(100, 30));

        JLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        JLabel5.setText("Selected Day:");
        JLabel5.setPreferredSize(new java.awt.Dimension(100, 30));

        javax.swing.GroupLayout SummaryConfirmationPanelLayout = new javax.swing.GroupLayout(SummaryConfirmationPanel);
        SummaryConfirmationPanel.setLayout(SummaryConfirmationPanelLayout);
        SummaryConfirmationPanelLayout.setHorizontalGroup(
            SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SummaryConfirmationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SummaryConfirmationPanelLayout.createSequentialGroup()
                        .addGroup(SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(JLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(JLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(JLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(JLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(JLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(JLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblSelectedDay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblPhone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblSelectedDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblSelectedTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTransactionId, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(SummaryConfirmationPanelLayout.createSequentialGroup()
                        .addGroup(SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(timeSlotsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                            .addGroup(SummaryConfirmationPanelLayout.createSequentialGroup()
                                .addComponent(PreviousButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ContinueButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        SummaryConfirmationPanelLayout.setVerticalGroup(
            SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SummaryConfirmationPanelLayout.createSequentialGroup()
                .addGroup(SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                        .addGap(34, 34, 34))
                    .addGroup(SummaryConfirmationPanelLayout.createSequentialGroup()
                        .addComponent(jSeparator1)
                        .addGap(8, 8, 8)))
                .addComponent(timeSlotsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ContinueButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PreviousButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        SchedulingTabbedPane.setBorder(null);
        SchedulingTabbedPane.setPreferredSize(new java.awt.Dimension(595, 500));

        SelectDatePanel.setBackground(new java.awt.Color(142, 217, 255));
        SelectDatePanel.setPreferredSize(new java.awt.Dimension(595, 500));

        jLayeredPane1.setPreferredSize(new java.awt.Dimension(300, 120));
        jLayeredPane1.setLayout(new java.awt.GridLayout(3, 3, 5, 5));

        EarliestAvailableButton.setText("Earliest Available");
        EarliestAvailableButton.setPreferredSize(new java.awt.Dimension(100, 45));
        EarliestAvailableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EarliestAvailableButtonActionPerformed(evt);
            }
        });
        jLayeredPane1.add(EarliestAvailableButton);

        ThisWeekButton.setText("This Week");
        ThisWeekButton.setPreferredSize(new java.awt.Dimension(100, 45));
        ThisWeekButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ThisWeekButtonActionPerformed(evt);
            }
        });
        jLayeredPane1.add(ThisWeekButton);

        NextWeekButton.setText("Next Week");
        NextWeekButton.setPreferredSize(new java.awt.Dimension(100, 45));
        NextWeekButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NextWeekButtonActionPerformed(evt);
            }
        });
        jLayeredPane1.add(NextWeekButton);

        DateIndicatorsPanel.setBackground(new java.awt.Color(255, 255, 255));
        DateIndicatorsPanel.setOpaque(false);
        DateIndicatorsPanel.setLayout(new java.awt.GridLayout(1, 0));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/component/Calendar/icon/circle_booked.png"))); // NOI18N
        jLabel6.setText("Booked");
        jLabel6.setIconTextGap(2);
        jLabel6.setPreferredSize(new java.awt.Dimension(16, 16));
        DateIndicatorsPanel.add(jLabel6);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/component/Calendar/icon/circle_weekend.png"))); // NOI18N
        jLabel4.setText("Weekend");
        jLabel4.setIconTextGap(2);
        jLabel4.setPreferredSize(new java.awt.Dimension(16, 16));
        DateIndicatorsPanel.add(jLabel4);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/component/Calendar/icon/circle_available.png"))); // NOI18N
        jLabel2.setText("Available");
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel2.setIconTextGap(2);
        jLabel2.setPreferredSize(new java.awt.Dimension(16, 16));
        DateIndicatorsPanel.add(jLabel2);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/component/Calendar/icon/circle_selected.png"))); // NOI18N
        jLabel3.setText("Selected");
        jLabel3.setIconTextGap(2);
        jLabel3.setPreferredSize(new java.awt.Dimension(16, 16));
        DateIndicatorsPanel.add(jLabel3);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/component/Calendar/icon/circle_holiday.png"))); // NOI18N
        jLabel5.setText("Holiday");
        jLabel5.setIconTextGap(2);
        jLabel5.setPreferredSize(new java.awt.Dimension(16, 16));
        DateIndicatorsPanel.add(jLabel5);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/component/Calendar/icon/circle_today.png"))); // NOI18N
        jLabel1.setText("Today");
        jLabel1.setIconTextGap(2);
        jLabel1.setPreferredSize(new java.awt.Dimension(16, 16));
        DateIndicatorsPanel.add(jLabel1);

        javax.swing.GroupLayout SelectDatePanelLayout = new javax.swing.GroupLayout(SelectDatePanel);
        SelectDatePanel.setLayout(SelectDatePanelLayout);
        SelectDatePanelLayout.setHorizontalGroup(
            SelectDatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SelectDatePanelLayout.createSequentialGroup()
                .addContainerGap(60, Short.MAX_VALUE)
                .addGroup(SelectDatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                    .addComponent(DateIndicatorsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(62, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SelectDatePanelLayout.createSequentialGroup()
                .addGap(95, 95, 95)
                .addComponent(calendarCustom, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(95, 95, 95))
        );
        SelectDatePanelLayout.setVerticalGroup(
            SelectDatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SelectDatePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DateIndicatorsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(calendarCustom, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
            .addGap(0, 0, Short.MAX_VALUE)
        );
        ConfirmDetailsPanelLayout.setVerticalGroup(
            ConfirmDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        SchedulingTabbedPane.addTab("tab3", ConfirmDetailsPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ProgressHeaderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(SchedulingTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(SummaryConfirmationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE))
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
            // Final confirmation step
            // Get transaction ID with error handling
            String transactionId = "Not assigned";
            String formattedTransactionId = "Not assigned";
            try {
                IDStatus idStatus = IDStatus.getStatusByCitizenId(citizen.getCitizenId());
                if (idStatus != null && idStatus.getTransactionId() != null && !idStatus.getTransactionId().trim().isEmpty()) {
                    transactionId = idStatus.getTransactionId();
                    formattedTransactionId = IDStatus.formatTransactionId(transactionId);
                }
            } catch (Exception e) {
                System.err.println("Error getting transaction ID for confirmation: " + e.getMessage());
            }

            String actionType = isRescheduling ? "Reschedule" : "Schedule";

            StringBuilder confirmMessage = new StringBuilder();
            confirmMessage.append("Confirm appointment details:\n\n");

            if (isRescheduling && existingAppointment != null) {
                confirmMessage.append("Rescheduling appointment\n");
                confirmMessage.append("Previous Date: ").append(existingAppointment.getAppDate()).append("\n");
                confirmMessage.append("Previous Time: ").append(existingAppointment.getAppTime()).append("\n\n");
            }

            confirmMessage.append("NEW APPOINTMENT DETAILS:\n");
            if (!"Not assigned".equals(transactionId)) {
                confirmMessage.append("Transaction ID: ").append(formattedTransactionId).append("\n");
            }
            confirmMessage.append("Date: ").append(dateFormat.format(selectedDate)).append("\n");
            confirmMessage.append("Time: ").append(selectedTime).append("\n");
            confirmMessage.append("Citizen: ").append(citizen.getFullName()).append("\n");
            confirmMessage.append("First Name: ").append(citizen.getFname()).append("\n");
            confirmMessage.append("Last Name: ").append(citizen.getLname()).append("\n\n");
            confirmMessage.append("Are you sure you want to ").append(actionType.toLowerCase()).append(" this appointment?");

            int confirm = JOptionPane.showConfirmDialog(this,
                confirmMessage.toString(),
                "Confirm Appointment " + actionType, JOptionPane.YES_NO_OPTION);

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

    private void EarliestAvailableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EarliestAvailableButtonActionPerformed
        calendarCustom.goToEarliestAvailable();
    }//GEN-LAST:event_EarliestAvailableButtonActionPerformed

    private void ThisWeekButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ThisWeekButtonActionPerformed
        calendarCustom.goToThisWeek();
    }//GEN-LAST:event_ThisWeekButtonActionPerformed

    private void NextWeekButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NextWeekButtonActionPerformed
        calendarCustom.goToNextWeek();
    }//GEN-LAST:event_NextWeekButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ConfirmDetailsPanel;
    private javax.swing.JButton ContinueButton;
    private javax.swing.JPanel DateIndicatorsPanel;
    private component.Button.FlatButton EarliestAvailableButton;
    private javax.swing.JLabel JLabel;
    private javax.swing.JLabel JLabel1;
    private javax.swing.JLabel JLabel2;
    private javax.swing.JLabel JLabel3;
    private javax.swing.JLabel JLabel4;
    private javax.swing.JLabel JLabel5;
    private component.Button.FlatButton NextWeekButton;
    private javax.swing.JButton PreviousButton;
    private javax.swing.JPanel ProgressHeaderPanel;
    private component.NoTabJTabbedPane SchedulingTabbedPane;
    private javax.swing.JPanel SelectDatePanel;
    private javax.swing.JPanel SelectTimePanel;
    private javax.swing.JPanel SummaryConfirmationPanel;
    private component.Button.FlatButton ThisWeekButton;
    private component.Calendar.CalendarCustom calendarCustom;
    private component.Progress.CustomStepProgressBar customProgressBar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPhone;
    private javax.swing.JLabel lblSelectedDate;
    private javax.swing.JLabel lblSelectedDay;
    private javax.swing.JLabel lblSelectedTime;
    private javax.swing.JLabel lblTransactionId;
    private javax.swing.JPanel timeSlotsPanel;
    // End of variables declaration//GEN-END:variables
}
