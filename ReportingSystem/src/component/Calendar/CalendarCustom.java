package component.Calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import component.Calendar.swing.PanelSlide;
import java.awt.Component;
import java.text.SimpleDateFormat;
import backend.objects.Data;
import java.awt.Color;

public class CalendarCustom extends javax.swing.JPanel {

    private int month;
    private int year;
    private Date selectedDate;
    private HashSet<Date> bookedDates;
    private HashSet<Date> availableDates;
    private HashSet<Date> holidayDates;
    private CalendarCustomListener listener;

    public interface CalendarCustomListener {
        void dateSelected(Date date);
        void monthChanged(int month, int year);
    }

    public CalendarCustom() {
        initComponents();
        thisMonth();
        System.out.println("CalendarCustom created - PanelDate will be shown");

        // Create PanelDate and set parent
        PanelDate panelDate = new PanelDate(month, year);
        panelDate.setParentCalendar(this);
        panelSlide1.show(panelDate, PanelSlide.AnimateType.TO_RIGHT);

        showMonthYear();
        bookedDates = new HashSet<>();
        availableDates = new HashSet<>();
        holidayDates = new HashSet<>();
        initHolidays();
        loadBookedDates();
        updateAvailableDates();
    }
    
    private void loadBookedDates() {
        bookedDates.clear();
        try {
            List<Data.Appointment> appointments = Data.Appointment.getAllAppointments();
            
            for (Data.Appointment appointment : appointments) {
                java.util.Date appDate = new java.util.Date(appointment.getAppDate().getTime());
                bookedDates.add(appDate);
            }
            
            System.out.println("Total booked dates loaded: " + bookedDates.size());
        } catch (Exception e) {
            System.err.println("Error loading booked dates: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void reloadBookedDates() {
        loadBookedDates();
        repaint();
    }
    
    public boolean isHolidayDate(Date date) {
        for (Date holiday : holidayDates) {
            if (isSameDay(holiday, date)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isBookedDate(Date date) {
        for (Date booked : bookedDates) {
            if (isSameDay(booked, date)) {
                return true;
            }
        }
        return false;
    }
    
    public void setListener(CalendarCustomListener listener) {
        this.listener = listener;
    }
    
    public Date getSelectedDate() {
        return selectedDate;
    }
    
    public void setSelectedDate(Date date) {
        this.selectedDate = date;
        System.out.println("Selected date set globally to: " + date);
        
        // Update all PanelDate instances to show the selection
        highlightSelectedDate();
        
        if (listener != null) {
            listener.dateSelected(date);
        }
    }

    public void addBookedDate(Date date) {
        bookedDates.add(date);
        repaint();
    }
    
    public void removeBookedDate(Date date) {
        bookedDates.remove(date);
        repaint();
    }
    
    public void addAvailableDate(Date date) {
        availableDates.add(date);
        repaint();
    }
    
    public void clearAvailableDates() {
        availableDates.clear();
        repaint();
    }
    
    public void updateAvailableDates() {
        availableDates.clear();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        
        for (int i = 1; i <= 60; i++) {
            cal.add(Calendar.DATE, 1);
            Date testDate = cal.getTime();
            if (isAvailableDate(testDate)) {
                availableDates.add(testDate);
            }
        }
        repaint();
    }
    
    public boolean isAvailableDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (cal.before(today)) {
            return false;
        }

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return false;
        }

        if (isHolidayDate(date)) {
            return false;
        }

        if (isBookedDate(date)) {
            return false;
        }

        return true;
    }
    
    public boolean isPastDate(Date date) {
        if (date == null) return false;

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        dateCal.set(Calendar.HOUR_OF_DAY, 0);
        dateCal.set(Calendar.MINUTE, 0);
        dateCal.set(Calendar.SECOND, 0);
        dateCal.set(Calendar.MILLISECOND, 0);

        return dateCal.before(today);
    }

    private void initHolidays() {
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        
        addHoliday(currentYear, 1, 1);   // New Year
        addHoliday(currentYear, 4, 9);   // Araw ng Kagitingan
        addHoliday(currentYear, 5, 1);   // Labor Day
        addHoliday(currentYear, 6, 12);  // Independence Day
        addHoliday(currentYear, 8, 26);  // National Heroes Day
        addHoliday(currentYear, 11, 30); // Bonifacio Day
        addHoliday(currentYear, 12, 25); // Christmas
        addHoliday(currentYear, 12, 30); // Rizal Day
        
        System.out.println("Holidays initialized for year: " + currentYear);
    }
    
    private void addHoliday(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        holidayDates.add(cal.getTime());
    }
    
    private void highlightSelectedDate() {
        System.out.println("Highlighting selected date: " + selectedDate);

        // Get ALL PanelDate components from PanelSlide
        Component[] components = panelSlide1.getComponents();
        for (Component panel : components) {
            if (panel instanceof PanelDate) {
                PanelDate panelDate = (PanelDate) panel;
                System.out.println("Updating PanelDate cells");

                // Update all cells in this PanelDate
                for (Component comp : panelDate.getComponents()) {
                    if (comp instanceof Cell) {
                        Cell cell = (Cell) comp;
                        if (!cell.isTitle() && cell.getDate() != null) {
                            boolean shouldSelect = selectedDate != null && 
                                isSameDay(selectedDate, cell.getDate());

                            if (shouldSelect) {
                                System.out.println("Selecting cell with date: " + cell.getDate());
                            }

                            cell.setSelected(shouldSelect);

                            // Update ALL statuses for ALL cells (current month and preview)
                            Date cellDate = cell.getDate();

                            // Get today's date
                            Calendar today = Calendar.getInstance();
                            today.set(Calendar.HOUR_OF_DAY, 0);
                            today.set(Calendar.MINUTE, 0);
                            today.set(Calendar.SECOND, 0);
                            today.set(Calendar.MILLISECOND, 0);

                            // Check if date is in the past
                            Calendar cellCal = Calendar.getInstance();
                            cellCal.setTime(cellDate);
                            cellCal.set(Calendar.HOUR_OF_DAY, 0);
                            cellCal.set(Calendar.MINUTE, 0);
                            cellCal.set(Calendar.SECOND, 0);
                            cellCal.set(Calendar.MILLISECOND, 0);

                            boolean isPastDate = cellCal.before(today);

                            // Check day of week
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(cellDate);
                            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                            boolean isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);

                            // Check if holiday
                            boolean isHoliday = isHolidayDate(cellDate);

                            // Check if booked
                            boolean isBooked = isBookedDate(cellDate);

                            // Check if available
                            boolean isAvailable = isAvailableDate(cellDate);

                            // Check if today
                            Calendar todayCal = Calendar.getInstance();
                            todayCal.setTime(new Date());
                            boolean isToday = isSameDay(cellDate, todayCal.getTime());

                            // Set all properties
                            cell.setWeekend(isWeekend);
                            cell.setHoliday(isHoliday);
                            cell.setBooked(isBooked);
                            cell.setAvailable(isAvailable);

                            // Set today status
                            if (isToday) {
                                cell.setAsToDay();
                            } else {
                                cell.resetToday();
                            }

                            // Set foreground based on month and status
                            Calendar cellMonthCal = Calendar.getInstance();
                            cellMonthCal.setTime(cellDate);
                            boolean isCurrentMonthCell = (cellMonthCal.get(Calendar.MONTH) + 1) == month && 
                                                         cellMonthCal.get(Calendar.YEAR) == year;

                            if (isCurrentMonthCell) {
                                if (isHoliday || isBooked || isWeekend || isPastDate) {
                                    cell.setForeground(new Color(200, 200, 200)); // Gray out unavailable dates
                                } else if (isAvailable && !isToday) {
                                    cell.setForeground(new Color(0, 150, 0)); // Green for available dates
                                } else if (!isToday) {
                                    cell.setForeground(new Color(68, 68, 68)); // Default for current month
                                }
                            } else {
                                // Preview cells (next/previous month)
                                cell.setForeground(new Color(169, 169, 169)); // Gray for preview
                            }

                            cell.repaint();
                        }
                    }
                }

                panelDate.repaint();
            }
        }

        panelSlide1.repaint();
        repaint();

        System.out.println("Highlighting complete");
    }
    
    public boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
               cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    public void goToDate(Date date) {
        if (date == null) return;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int newMonth = cal.get(Calendar.MONTH) + 1;
        int newYear = cal.get(Calendar.YEAR);

        // Check if we're already in the target month and year
        if (newMonth == month && newYear == year) {
            System.out.println("Already in target month: " + month + "/" + year);

            // Just update the selected date without creating new panel
            setSelectedDate(date);
            return;
        }

        // Determine animation direction
        PanelSlide.AnimateType animateType = PanelSlide.AnimateType.TO_LEFT;
        if (newYear < year || (newYear == year && newMonth < month)) {
            animateType = PanelSlide.AnimateType.TO_RIGHT;
        }

        month = newMonth;
        year = newYear;

        // Create new PanelDate with parent reference
        PanelDate panelDate = createPanelDate();
        panelSlide1.show(panelDate, animateType);

        showMonthYear();

        // Set selected date and highlight it - IMPORTANT: This will highlight in the new month
        setSelectedDate(date);

        // Notify listener if exists
        if (listener != null) {
            listener.monthChanged(month, year);
        }
    }
    
    private PanelDate createPanelDate() {
        PanelDate panelDate = new PanelDate(month, year);
        panelDate.setParentCalendar(this);

        // Initialize the panel date to set dates
        panelDate.init();

        // Initialize all cells with their status
        initializePanelDateCells(panelDate);

        return panelDate;
    }
    
    public void goToEarliestAvailable() {
        if (!availableDates.isEmpty()) {
            Date earliest = null;
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            for (Date date : availableDates) {
                // Skip dates that are today or in the past
                Calendar dateCal = Calendar.getInstance();
                dateCal.setTime(date);
                dateCal.set(Calendar.HOUR_OF_DAY, 0);
                dateCal.set(Calendar.MINUTE, 0);
                dateCal.set(Calendar.SECOND, 0);
                dateCal.set(Calendar.MILLISECOND, 0);

                if (dateCal.before(today)) {
                    continue; // Skip past dates
                }

                if (earliest == null || date.before(earliest)) {
                    earliest = date;
                }
            }
            if (earliest != null) {
                goToDate(earliest);
            }
        }
    }

    public void goToThisWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // Start from tomorrow (skip today and past dates)
        cal.add(Calendar.DATE, 1);

        // Find next available weekday this week (next 7 days)
        for (int i = 0; i < 7; i++) {
            Date testDate = cal.getTime();
            if (isAvailableDate(testDate)) {
                goToDate(testDate);
                return;
            }
            cal.add(Calendar.DATE, 1);
        }
    }

    public void goToNextWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, 7); // Start from next week

        // Find available weekday next week (7 days from start)
        for (int i = 0; i < 7; i++) {
            Date testDate = cal.getTime();
            if (isAvailableDate(testDate)) {
                goToDate(testDate);
                return;
            }
            cal.add(Calendar.DATE, 1);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        lbMonthYear = new javax.swing.JLabel();
        cmdBack = new javax.swing.JButton();
        cmdNext = new javax.swing.JButton();
        panelSlide1 = new component.Calendar.swing.PanelSlide();

        setBackground(new java.awt.Color(255, 255, 255));

        jLayeredPane1.setBackground(new java.awt.Color(255, 255, 255));
        jLayeredPane1.setPreferredSize(new java.awt.Dimension(500, 50));

        lbMonthYear.setBackground(new java.awt.Color(255, 255, 255));
        lbMonthYear.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lbMonthYear.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbMonthYear.setText("Month - Year");
        lbMonthYear.setPreferredSize(new java.awt.Dimension(400, 50));

        cmdBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/component/Calendar/icon/back.png"))); // NOI18N
        cmdBack.setPreferredSize(new java.awt.Dimension(50, 50));
        cmdBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdBackActionPerformed(evt);
            }
        });

        cmdNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/component/Calendar/icon/next.png"))); // NOI18N
        cmdNext.setPreferredSize(new java.awt.Dimension(50, 50));
        cmdNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdNextActionPerformed(evt);
            }
        });

        jLayeredPane1.setLayer(lbMonthYear, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(cmdBack, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(cmdNext, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(cmdBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(lbMonthYear, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(cmdNext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmdBack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbMonthYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdNext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        panelSlide1.setBackground(new java.awt.Color(255, 255, 255));
        panelSlide1.setPreferredSize(new java.awt.Dimension(500, 350));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelSlide1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(panelSlide1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBackActionPerformed
        if (month == 1) {
            month = 12;
            year--;
        } else {
            month--;
        }

        // Pre-initialize the new panel before showing it
        PanelDate panelDate = createPanelDate();

        // Force initialization of all cells BEFORE showing the panel
        initializePanelDateCells(panelDate);

        panelSlide1.show(panelDate, PanelSlide.AnimateType.TO_RIGHT);

        showMonthYear();

        // Notify listener if exists
        if (listener != null) {
            listener.monthChanged(month, year);
        }
    }//GEN-LAST:event_cmdBackActionPerformed

    private void cmdNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdNextActionPerformed
        if (month == 12) {
            month = 1;
            year++;
        } else {
            month++;
        }

        // Pre-initialize the new panel before showing it
        PanelDate panelDate = createPanelDate();

        // Force initialization of all cells BEFORE showing the panel
        initializePanelDateCells(panelDate);

        panelSlide1.show(panelDate, PanelSlide.AnimateType.TO_LEFT);

        showMonthYear();

        // Notify listener if exists
        if (listener != null) {
            listener.monthChanged(month, year);
        }
    }//GEN-LAST:event_cmdNextActionPerformed

    private void thisMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);
    }

    private void showMonthYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DATE, 1);
        SimpleDateFormat df = new SimpleDateFormat("MMMM-yyyy");
        lbMonthYear.setText(df.format(calendar.getTime()));
    }
    
    private void initializePanelDateCells(PanelDate panelDate) {
        if (panelDate == null) return;

        // Get all cells from the panel
        for (Component comp : panelDate.getComponents()) {
            if (comp instanceof Cell) {
                Cell cell = (Cell) comp;
                if (!cell.isTitle() && cell.getDate() != null) {
                    Date cellDate = cell.getDate();

                    // Initialize cell properties
                    Calendar cellCal = Calendar.getInstance();
                    cellCal.setTime(cellDate);

                    // Check if date is in the past
                    Calendar today = Calendar.getInstance();
                    today.set(Calendar.HOUR_OF_DAY, 0);
                    today.set(Calendar.MINUTE, 0);
                    today.set(Calendar.SECOND, 0);
                    today.set(Calendar.MILLISECOND, 0);

                    cellCal.set(Calendar.HOUR_OF_DAY, 0);
                    cellCal.set(Calendar.MINUTE, 0);
                    cellCal.set(Calendar.SECOND, 0);
                    cellCal.set(Calendar.MILLISECOND, 0);

                    boolean isPastDate = cellCal.before(today);

                    // Check day of week
                    int dayOfWeek = cellCal.get(Calendar.DAY_OF_WEEK);
                    boolean isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);

                    // Check if holiday
                    boolean isHoliday = isHolidayDate(cellDate);

                    // Check if booked
                    boolean isBooked = isBookedDate(cellDate);

                    // Check if available
                    boolean isAvailable = isAvailableDate(cellDate);

                    // Check if today
                    boolean isToday = isSameDay(cellDate, new Date());

                    // Set all properties
                    cell.setWeekend(isWeekend);
                    cell.setHoliday(isHoliday);
                    cell.setBooked(isBooked);
                    cell.setAvailable(isAvailable);

                    // Set today status
                    if (isToday) {
                        cell.setAsToDay();
                    } else {
                        // Use reflection to call resetToday if it exists
                        try {
                            java.lang.reflect.Method resetTodayMethod = cell.getClass().getMethod("resetToday");
                            resetTodayMethod.invoke(cell);
                        } catch (Exception e) {
                            // Method doesn't exist, just set foreground
                            cell.setForeground(java.awt.Color.BLACK);
                        }
                    }

                    // Check if this cell should be selected
                    if (selectedDate != null) {
                        boolean shouldSelect = isSameDay(selectedDate, cellDate);
                        cell.setSelected(shouldSelect);
                    }

                    // Set foreground
                    boolean isCurrentMonthCell = (cellCal.get(Calendar.MONTH) + 1) == month && 
                                                 cellCal.get(Calendar.YEAR) == year;

                    if (isCurrentMonthCell) {
                        if (isHoliday || isBooked || isWeekend || isPastDate) {
                            cell.setForeground(new java.awt.Color(200, 200, 200));
                        } else if (isAvailable && !isToday) {
                            cell.setForeground(new java.awt.Color(0, 150, 0));
                        } else if (!isToday) {
                            cell.setForeground(new java.awt.Color(68, 68, 68));
                        }
                    } else {
                        cell.setForeground(new java.awt.Color(169, 169, 169));
                    }

                    // Set tooltip
                    if (isPastDate) {
                        cell.setTooltipText("Past date - Not available");
                    } else if (isHoliday) {
                        cell.setTooltipText("Public Holiday - PSA Closed");
                    } else if (isWeekend) {
                        cell.setTooltipText("Weekend - PSA Closed");
                    } else if (isBooked) {
                        cell.setTooltipText("Fully Booked");
                    } else if (!isAvailable && !isPastDate) {
                        cell.setTooltipText("Not Available");
                    } else if (isAvailable) {
                        cell.setTooltipText("Available - Click to select");
                    }

                    // Force repaint
                    cell.repaint();
                }
            }
        }

        // Force the panel to repaint
        panelDate.repaint();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdBack;
    private javax.swing.JButton cmdNext;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLabel lbMonthYear;
    private component.Calendar.swing.PanelSlide panelSlide1;
    // End of variables declaration//GEN-END:variables
}
