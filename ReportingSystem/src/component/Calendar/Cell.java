package component.Calendar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Calendar;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ToolTipManager;

public class Cell extends JButton {

    private Date date;
    private boolean title;
    private boolean isToDay;
    private boolean isSelected;
    private boolean isAvailable;
    private boolean isBooked;
    private boolean isHoliday;
    private boolean isWeekend;
    private String tooltipText;
    private PanelDate parentPanel;

    public Cell() {
        setContentAreaFilled(false);
        setBorder(null);
        setHorizontalAlignment(JLabel.CENTER);
        ToolTipManager.sharedInstance().registerComponent(this);

        // Add mouse listener for click events
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleCellClick();
            }
        });
    }
    
    public void setParentPanel(PanelDate parent) {
        this.parentPanel = parent;
    }
    
    public PanelDate getParentPanel() {
        return parentPanel;
    }
    
    private void handleCellClick() {
        if (!title && date != null) {
            System.out.println("Cell clicked: " + date);

            // Check if date is in the past
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            Calendar clickedDate = Calendar.getInstance();
            clickedDate.setTime(date);
            clickedDate.set(Calendar.HOUR_OF_DAY, 0);
            clickedDate.set(Calendar.MINUTE, 0);
            clickedDate.set(Calendar.SECOND, 0);
            clickedDate.set(Calendar.MILLISECOND, 0);

            if (clickedDate.before(today)) {
                System.out.println("Cannot select past date: " + date);
                return;
            }

            // Check if date is available
            boolean available = false;
            if (parentPanel != null && parentPanel.getParentCalendar() != null) {
                CalendarCustom calendar = parentPanel.getParentCalendar();
                available = calendar.isAvailableDate(date);
            }

            if (available) {
                // Notify parent calendar that this cell was clicked
                if (parentPanel != null && parentPanel.getParentCalendar() != null) {
                    parentPanel.getParentCalendar().setSelectedDate(date);
                    setSelected(true);
                    System.out.println("Date selected and highlighted: " + date);
                }
            } else {
                System.out.println("Date not available: " + date);
            }
        }
    }

    public void asTitle() {
        title = true;
    }

    public boolean isTitle() {
        return title;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    public Date getDate() {
        return date;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        repaint();
    }
    
    public void setAvailable(boolean available) {
        this.isAvailable = available;
        repaint();
    }
    
    public void setBooked(boolean booked) {
        this.isBooked = booked;
        repaint();
    }
    
    public void setHoliday(boolean holiday) {
        this.isHoliday = holiday;
        repaint();
    }
    
    public void setWeekend(boolean weekend) {
        this.isWeekend = weekend;
        repaint();
    }

    public void currentMonth(boolean act) {
        if (act) {
            if (isHoliday || isBooked || isWeekend) {
                setForeground(new Color(200, 200, 200)); // Gray out unavailable dates
            } else if (isAvailable) {
                setForeground(new Color(0, 150, 0)); // Green for available dates
            } else {
                setForeground(new Color(68, 68, 68));
            }
        } else {
            setForeground(new Color(169, 169, 169));
        }
    }

    public void resetToday() {
        isToDay = false;
        setForeground(Color.BLACK); // Default foreground
    }
    
    public void setAsToDay() {
        isToDay = true;
        setForeground(Color.WHITE);
    }
    
    @Override
    public String getToolTipText() {
        return tooltipText;
    }
    
    public void setTooltipText(String text) {
        this.tooltipText = text;
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int x = getWidth() / 2 - 17;
        int y = getHeight() / 2 - 17;
        int width = 35;
        int height = 35;
        int arc = 100;
        
        // Draw background based on cell state
        if (isSelected) {
            // Fill with Green (200,254,156) if selected
            g2.setColor(new Color(200, 254, 156));
            g2.fillRoundRect(x, y, width, height, arc, arc);
        } else if (isToDay) {
            // Fill with Blue (156,200,254) if today
            g2.setColor(new Color(156, 200, 254));
            g2.fillRoundRect(x, y, width, height, arc, arc);
        } else if (isBooked) {
            // Fill with Yellow (249,254,156) if booked
            g2.setColor(new Color(249, 254, 156));
            g2.fillRoundRect(x, y, width, height, arc, arc);
        } else if (isHoliday) {
            // Fill with Red (254,161,156) for holidays
            g2.setColor(new Color(254, 161, 156));
            g2.fillRoundRect(x, y, width, height, arc, arc);
        } else if (isWeekend) {
            // Fill with light gray for weekends
            g2.setColor(new Color(240, 240, 240));
            g2.fillRoundRect(x, y, width, height, arc, arc);
        } else if (isAvailable) {
            // Draw Green (200,254,156) border for available dates
            g2.setColor(new Color(200, 254, 156));
            g2.drawRoundRect(x, y, width, height, arc, arc);
        }
        
        // Draw title separator line
        if (title) {
            grphcs.setColor(new Color(213, 213, 213));
            grphcs.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
        }
        
        super.paintComponent(grphcs);
    }
}