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
                cell.currentMonth(calendar.get(Calendar.MONTH) == month - 1);

                // Check if date is in the past
                Calendar cellCalendar = Calendar.getInstance();
                cellCalendar.setTime(cellDate);
                cellCalendar.set(Calendar.HOUR_OF_DAY, 0);
                cellCalendar.set(Calendar.MINUTE, 0);
                cellCalendar.set(Calendar.SECOND, 0);
                cellCalendar.set(Calendar.MILLISECOND, 0);

                boolean isPastDate = cellCalendar.before(today);

                // Check day properties
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                    cell.setWeekend(true);
                    cell.setAvailable(false);
                } else if (isPastDate) {
                    // Past dates should not be available
                    cell.setAvailable(false);
                    // You can add visual indication for past dates
                    cell.setForeground(new Color(200, 200, 200)); // Gray out past dates
                    cell.setToolTipText("Past date - Not available");
                } else {
                    // Check with parent calendar if date is available
                    if (parentCalendar != null) {
                        boolean isAvailable = parentCalendar.isAvailableDate(cellDate);
                        cell.setAvailable(isAvailable);

                        // Also check if it's a holiday
                        boolean isHoliday = parentCalendar.isHolidayDate(cellDate);
                        cell.setHoliday(isHoliday);
                    }
                }

                // Check if today
                if (toDay.isToDay(new ToDay(calendar.get(Calendar.DATE), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)))) {
                    cell.setAsToDay();
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
        add(sun);

        mon.setText("Mon");
        add(mon);

        tue.setText("Tue");
        add(tue);

        wed.setText("Wed");
        add(wed);

        thu.setText("Thu");
        add(thu);

        fri.setText("Fri");
        add(fri);

        sat.setText("Sat");
        sat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                satActionPerformed(evt);
            }
        });
        add(sat);
        add(cell8);
        add(cell9);
        add(cell10);
        add(cell11);
        add(cell12);
        add(cell13);
        add(cell14);
        add(cell15);
        add(cell16);
        add(cell17);
        add(cell18);
        add(cell19);
        add(cell20);
        add(cell21);
        add(cell22);
        add(cell23);
        add(cell24);
        add(cell25);
        add(cell26);
        add(cell27);
        add(cell28);
        add(cell29);
        add(cell30);
        add(cell31);
        add(cell32);
        add(cell33);
        add(cell34);
        add(cell35);
        add(cell36);
        add(cell37);
        add(cell38);
        add(cell39);
        add(cell40);
        add(cell41);
        add(cell42);
        add(cell43);
        add(cell44);
        add(cell45);
        add(cell46);
        add(cell47);
        add(cell48);
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
