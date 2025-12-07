package component.Calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.awt.event.MouseEvent;
import component.Calendar.swing.PanelSlide;
import java.awt.Component;
import java.text.SimpleDateFormat;

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
        updateAvailableDates();
    }
    
    public boolean isHolidayDate(Date date) {
        return holidayDates.contains(date);
    }
    
    public void setListener(CalendarCustomListener listener) {
        this.listener = listener;
    }
    
    public Date getSelectedDate() {
        return selectedDate;
    }
    
    public void setSelectedDate(Date date) {
        this.selectedDate = date;
        highlightSelectedDate(); // This should highlight the cell
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
        
        // Generate available dates for next 30 days (excluding weekends and holidays)
        for (int i = 1; i <= 30; i++) {
            cal.add(Calendar.DATE, 1);
            if (isAvailableDate(cal.getTime())) {
                availableDates.add(cal.getTime());
            }
        }
        repaint();
    }
    
    public boolean isAvailableDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        // Check if date is in the past
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
            return false; // Past dates are not available
        }

        // Check if weekend
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return false;
        }

        // Check if holiday (using same-day comparison)
        for (Date holiday : holidayDates) {
            if (isSameDay(holiday, date)) {
                return false;
            }
        }

        // Check if already booked
        for (Date booked : bookedDates) {
            if (isSameDay(booked, date)) {
                return false;
            }
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
        
        // Add major holidays (example - adjust as needed)
        addHoliday(currentYear, 1, 1);   // New Year
        addHoliday(currentYear, 4, 9);   // Araw ng Kagitingan
        addHoliday(currentYear, 5, 1);   // Labor Day
        addHoliday(currentYear, 6, 12);  // Independence Day
        addHoliday(currentYear, 8, 26);  // National Heroes Day
        addHoliday(currentYear, 11, 30); // Bonifacio Day
        addHoliday(currentYear, 12, 25); // Christmas
        addHoliday(currentYear, 12, 30); // Rizal Day
    }
    
    private void addHoliday(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        holidayDates.add(cal.getTime());
    }
    
    private void handleDateClick(MouseEvent e) {
        // Get the clicked component
        java.awt.Component comp = getComponentAt(e.getPoint());
        if (comp instanceof Cell) {
            Cell cell = (Cell) comp;
            if (!cell.isTitle() && cell.getDate() != null) {
                Date clickedDate = cell.getDate();
                
                // Check if date is available
                if (isAvailableDate(clickedDate)) {
                    setSelectedDate(clickedDate);
                    
                    // Highlight the cell
                    highlightSelectedDate();
                }
            }
        }
    }
    
    private void highlightSelectedDate() {
        System.out.println("Highlighting selected date: " + selectedDate);

        // Get the current PanelDate from PanelSlide
        Component[] components = panelSlide1.getComponents();
        for (Component panel : components) {
            if (panel instanceof PanelDate) {
                PanelDate panelDate = (PanelDate) panel;
                System.out.println("Found PanelDate - highlighting cells");

                // Highlight cells in this PanelDate
                for (Component comp : panelDate.getComponents()) {
                    if (comp instanceof Cell) {
                        Cell cell = (Cell) comp;
                        if (!cell.isTitle() && cell.getDate() != null) {
                            boolean shouldSelect = selectedDate != null && 
                                isSameDay(selectedDate, cell.getDate());

                            System.out.println("Cell date: " + cell.getDate() + 
                                              ", shouldSelect: " + shouldSelect);

                            cell.setSelected(shouldSelect);

                            // Also set available status based on date
                            if (!cell.isTitle() && cell.getDate() != null) {
                                cell.setAvailable(isAvailableDate(cell.getDate()));
                            }

                            // Force repaint of the cell
                            cell.repaint();
                        }
                    }
                }

                // Force repaint of the panel
                panelDate.repaint();
            }
        }

        // Force repaint of the entire calendar
        panelSlide1.repaint();
        repaint();

        System.out.println("Highlighting complete");
    }

    private boolean isSameDay(Date date1, Date date2) {
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
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int newMonth = cal.get(Calendar.MONTH) + 1;
        int newYear = cal.get(Calendar.YEAR);

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

        // Set selected date and highlight it
        if (date != null) {
            setSelectedDate(date);
        }

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

        // If there's a selected date in this month, highlight it
        if (selectedDate != null) {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.setTime(selectedDate);

            // Check if selected date is in the current month
            if (selectedCal.get(Calendar.YEAR) == year && 
                (selectedCal.get(Calendar.MONTH) + 1) == month) {

                // Force highlight update
                highlightSelectedDate();
            }
        }

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
                .addComponent(lbMonthYear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
            .addComponent(panelSlide1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(panelSlide1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        PanelDate panelDate = createPanelDate();
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

        PanelDate panelDate = createPanelDate();
        panelSlide1.show(panelDate, PanelSlide.AnimateType.TO_LEFT);

        showMonthYear();

        // Notify listener if exists
        if (listener != null) {
            listener.monthChanged(month, year);
        }
    }//GEN-LAST:event_cmdNextActionPerformed

private void thisMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());   //  today
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdBack;
    private javax.swing.JButton cmdNext;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLabel lbMonthYear;
    private component.Calendar.swing.PanelSlide panelSlide1;
    // End of variables declaration//GEN-END:variables
}
