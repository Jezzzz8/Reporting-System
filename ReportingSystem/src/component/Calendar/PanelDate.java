package component.Calendar;

import java.awt.Color;
import java.awt.Component;
import java.util.Calendar;
import java.util.Date;

public class PanelDate extends javax.swing.JLayeredPane {

    private int month;
    private int year;
    private CalendarCustom parentCalendar;

    public PanelDate(int month, int year) {
        initComponents();
        this.month = month;
        this.year = year;

        // Set parent for all cells
        for (Component com : getComponents()) {
            if (com instanceof Cell) {
                Cell cell = (Cell) com;
                cell.setParentPanel(this);
            }
        }

        init(); 
    }
    
    public void setParentCalendar(CalendarCustom parent) {
        this.parentCalendar = parent;
    }

    public void init() {
        sun.asTitle();
        mon.asTitle();
        tue.asTitle();
        wed.asTitle();
        thu.asTitle();
        fri.asTitle();
        sat.asTitle();
        setDate();

        // Set parent for all cells (in case some were added after construction)
        for (Component com : getComponents()) {
            if (com instanceof Cell) {
                Cell cell = (Cell) com;
                cell.setParentPanel(this);
            }
        }
    }

    public CalendarCustom getParentCalendar() {
        return parentCalendar;
    }

    private void setDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, 1);
        int startDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DATE, -startDay);
        ToDay toDay = getToDay();

        // Get today's date for comparison
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        for (Component com : getComponents()) {
            Cell cell = (Cell) com;
            if (!cell.isTitle()) {
                cell.setText(calendar.get(Calendar.DATE) + "");
                Date cellDate = calendar.getTime();
                cell.setDate(cellDate);

                // Check if this cell belongs to the current month
                Calendar cellCal = Calendar.getInstance();
                cellCal.setTime(cellDate);
                boolean isCurrentMonth = (cellCal.get(Calendar.MONTH) + 1) == month && 
                                         cellCal.get(Calendar.YEAR) == year;

                cell.currentMonth(isCurrentMonth);

                if (parentCalendar != null) {
                    // Check if date is in the past
                    Calendar cellCalendar = Calendar.getInstance();
                    cellCalendar.setTime(cellDate);
                    cellCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    cellCalendar.set(Calendar.MINUTE, 0);
                    cellCalendar.set(Calendar.SECOND, 0);
                    cellCalendar.set(Calendar.MILLISECOND, 0);

                    boolean isPastDate = cellCalendar.before(today);

                    // Check day of week
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    boolean isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);

                    // Check if holiday (safe call)
                    boolean isHoliday = parentCalendar.isHolidayDate(cellDate);

                    // Check if booked (safe call)
                    boolean isBooked = parentCalendar.isBookedDate(cellDate);

                    // Check if available (only for future dates that aren't weekend, holiday, or booked)
                    boolean isAvailable = !isPastDate && !isWeekend && !isHoliday && !isBooked;

                    // ALWAYS set all cell properties, regardless of whether it's current month or preview
                    cell.setWeekend(isWeekend);
                    cell.setHoliday(isHoliday);
                    cell.setBooked(isBooked);
                    cell.setAvailable(isAvailable);

                    // Check if this cell should be selected
                    if (parentCalendar.getSelectedDate() != null) {
                        boolean shouldSelect = parentCalendar.isSameDay(parentCalendar.getSelectedDate(), cellDate);
                        cell.setSelected(shouldSelect);
                    }

                    // Set visual appearance for past dates
                    if (isPastDate) {
                        cell.setForeground(new Color(200, 200, 200));
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
                } else {
                    // If parentCalendar is null, set defaults
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    boolean isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);
                    cell.setWeekend(isWeekend);
                    cell.setHoliday(false);
                    cell.setBooked(false);
                    cell.setAvailable(false);
                    cell.setTooltipText("Calendar not initialized");
                }

                // Check if today - ALWAYS check regardless of month
                if (toDay.isToDay(new ToDay(calendar.get(Calendar.DATE), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)))) {
                    cell.setAsToDay();
                } else {
                    // Reset today status if it's not today
                    cell.setForeground(Color.BLACK); // Reset foreground if not today
                }

                calendar.add(Calendar.DATE, 1);
            }
        }
    }

    private ToDay getToDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return new ToDay(calendar.get(Calendar.DATE), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sun = new component.Calendar.Cell();
        mon = new component.Calendar.Cell();
        tue = new component.Calendar.Cell();
        wed = new component.Calendar.Cell();
        thu = new component.Calendar.Cell();
        fri = new component.Calendar.Cell();
        sat = new component.Calendar.Cell();
        cell8 = new component.Calendar.Cell();
        cell9 = new component.Calendar.Cell();
        cell10 = new component.Calendar.Cell();
        cell11 = new component.Calendar.Cell();
        cell12 = new component.Calendar.Cell();
        cell13 = new component.Calendar.Cell();
        cell14 = new component.Calendar.Cell();
        cell15 = new component.Calendar.Cell();
        cell16 = new component.Calendar.Cell();
        cell17 = new component.Calendar.Cell();
        cell18 = new component.Calendar.Cell();
        cell19 = new component.Calendar.Cell();
        cell20 = new component.Calendar.Cell();
        cell21 = new component.Calendar.Cell();
        cell22 = new component.Calendar.Cell();
        cell23 = new component.Calendar.Cell();
        cell24 = new component.Calendar.Cell();
        cell25 = new component.Calendar.Cell();
        cell26 = new component.Calendar.Cell();
        cell27 = new component.Calendar.Cell();
        cell28 = new component.Calendar.Cell();
        cell29 = new component.Calendar.Cell();
        cell30 = new component.Calendar.Cell();
        cell31 = new component.Calendar.Cell();
        cell32 = new component.Calendar.Cell();
        cell33 = new component.Calendar.Cell();
        cell34 = new component.Calendar.Cell();
        cell35 = new component.Calendar.Cell();
        cell36 = new component.Calendar.Cell();
        cell37 = new component.Calendar.Cell();
        cell38 = new component.Calendar.Cell();
        cell39 = new component.Calendar.Cell();
        cell40 = new component.Calendar.Cell();
        cell41 = new component.Calendar.Cell();
        cell42 = new component.Calendar.Cell();
        cell43 = new component.Calendar.Cell();
        cell44 = new component.Calendar.Cell();
        cell45 = new component.Calendar.Cell();
        cell46 = new component.Calendar.Cell();
        cell47 = new component.Calendar.Cell();
        cell48 = new component.Calendar.Cell();
        cell49 = new component.Calendar.Cell();

        setPreferredSize(new java.awt.Dimension(300, 150));
        setLayout(new java.awt.GridLayout(7, 7));

        sun.setForeground(new java.awt.Color(255, 51, 51));
        sun.setText("Sun");
        sun.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(sun);

        mon.setText("Mon");
        mon.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(mon);

        tue.setText("Tue");
        tue.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(tue);

        wed.setText("Wed");
        wed.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(wed);

        thu.setText("Thu");
        thu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(thu);

        fri.setText("Fri");
        fri.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(fri);

        sat.setText("Sat");
        sat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        sat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                satActionPerformed(evt);
            }
        });
        add(sat);

        cell8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell8);

        cell9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell9);

        cell10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell10);

        cell11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell11);

        cell12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell12);

        cell13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell13);

        cell14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell14);

        cell15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell15);

        cell16.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell16);

        cell17.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell17);

        cell18.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell18);

        cell19.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell19);

        cell20.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell20);

        cell21.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell21);

        cell22.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell22);

        cell23.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell23);

        cell24.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell24);

        cell25.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell25);

        cell26.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell26);

        cell27.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell27);

        cell28.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell28);

        cell29.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell29);

        cell30.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell30);

        cell31.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell31);

        cell32.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell32);

        cell33.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell33);

        cell34.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell34);

        cell35.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell35);

        cell36.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell36);

        cell37.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell37);

        cell38.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell38);

        cell39.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell39);

        cell40.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell40);

        cell41.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell41);

        cell42.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell42);

        cell43.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell43);

        cell44.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell44);

        cell45.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell45);

        cell46.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell46);

        cell47.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell47);

        cell48.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell48);

        cell49.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        add(cell49);
    }// </editor-fold>//GEN-END:initComponents

    private void sunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_monActionPerformed

    private void wedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_wedActionPerformed

    private void thuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_thuActionPerformed

    private void monActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cell1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cell1ActionPerformed

    private void satActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_satActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_satActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.Calendar.Cell cell10;
    private component.Calendar.Cell cell11;
    private component.Calendar.Cell cell12;
    private component.Calendar.Cell cell13;
    private component.Calendar.Cell cell14;
    private component.Calendar.Cell cell15;
    private component.Calendar.Cell cell16;
    private component.Calendar.Cell cell17;
    private component.Calendar.Cell cell18;
    private component.Calendar.Cell cell19;
    private component.Calendar.Cell cell20;
    private component.Calendar.Cell cell21;
    private component.Calendar.Cell cell22;
    private component.Calendar.Cell cell23;
    private component.Calendar.Cell cell24;
    private component.Calendar.Cell cell25;
    private component.Calendar.Cell cell26;
    private component.Calendar.Cell cell27;
    private component.Calendar.Cell cell28;
    private component.Calendar.Cell cell29;
    private component.Calendar.Cell cell30;
    private component.Calendar.Cell cell31;
    private component.Calendar.Cell cell32;
    private component.Calendar.Cell cell33;
    private component.Calendar.Cell cell34;
    private component.Calendar.Cell cell35;
    private component.Calendar.Cell cell36;
    private component.Calendar.Cell cell37;
    private component.Calendar.Cell cell38;
    private component.Calendar.Cell cell39;
    private component.Calendar.Cell cell40;
    private component.Calendar.Cell cell41;
    private component.Calendar.Cell cell42;
    private component.Calendar.Cell cell43;
    private component.Calendar.Cell cell44;
    private component.Calendar.Cell cell45;
    private component.Calendar.Cell cell46;
    private component.Calendar.Cell cell47;
    private component.Calendar.Cell cell48;
    private component.Calendar.Cell cell49;
    private component.Calendar.Cell cell8;
    private component.Calendar.Cell cell9;
    private component.Calendar.Cell fri;
    private component.Calendar.Cell mon;
    private component.Calendar.Cell sat;
    private component.Calendar.Cell sun;
    private component.Calendar.Cell thu;
    private component.Calendar.Cell tue;
    private component.Calendar.Cell wed;
    // End of variables declaration//GEN-END:variables
}
