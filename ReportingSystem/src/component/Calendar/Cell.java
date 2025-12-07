package component.Calendar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ToolTipManager;
import java.util.Calendar;

public class Cell extends JButton {

    private Date date;
    private boolean title;
    private boolean isToDay;
    private boolean isSelected;
    private boolean isAvailable;
    private boolean isBooked;
    private boolean isHoliday;
    private String tooltipText;
    private PanelDate parentPanel; // Add this field

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
    
    // Add this setter method
    public void setParentPanel(PanelDate parent) {
        this.parentPanel = parent;
    }
    
    // Add this getter method (optional but useful)
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
                // You could show a tooltip or change appearance for past dates
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
        if (booked) {
            tooltipText = "Fully Booked";
        }
        repaint();
    }
    
    public void setHoliday(boolean holiday) {
        this.isHoliday = holiday;
        if (holiday) {
            tooltipText = "Public Holiday - PSA Closed";
        }
        repaint();
    }
    
    public void setWeekend(boolean weekend) {
        if (weekend) {
            tooltipText = "Weekend - PSA Closed";
        }
    }

    public void currentMonth(boolean act) {
        if (act) {
            if (isHoliday || isBooked) {
                setForeground(new Color(200, 200, 200));
            } else if (isAvailable) {
                setForeground(new Color(0, 150, 0)); // Green for available dates
            } else {
                setForeground(new Color(68, 68, 68));
            }
        } else {
            setForeground(new Color(169, 169, 169));
        }
    }

    public void setAsToDay() {
        isToDay = true;
        setForeground(Color.WHITE);
    }
    
    @Override
    public String getToolTipText() {
        return tooltipText;
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (title) {
            grphcs.setColor(new Color(213, 213, 213));
            grphcs.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
        }
        
        if (isSelected) {
            g2.setColor(new Color(200,254,156)); // Selected date with transparency
            int x = getWidth() / 2 - 17;
            int y = getHeight() / 2 - 17;
            g2.fillRoundRect(x, y, 35, 35, 100, 100);
        } else if (isToDay) {
            g2.setColor(new Color(156,200,254));
            int x = getWidth() / 2 - 17;
            int y = getHeight() / 2 - 17;
            g2.fillRoundRect(x, y, 35, 35, 100, 100);
        } else if (isHoliday) {
            // Draw gray background for holidays
            g2.setColor(new Color(240, 240, 240));
            int x = getWidth() / 2 - 17;
            int y = getHeight() / 2 - 17;
            g2.fillRoundRect(x, y, 35, 35, 100, 100);
            
            // Draw diagonal line through holiday dates
            g2.setColor(new Color(254,161,156));
            g2.drawLine(5, 5, getWidth() - 5, getHeight() - 5);
            g2.drawLine(5, getHeight() - 5, getWidth() - 5, 5);
        } else if (isBooked) {
            // Draw red background for booked dates
            g2.setColor(new Color(249,254,156));
            int x = getWidth() / 2 - 17;
            int y = getHeight() / 2 - 17;
            g2.fillRoundRect(x, y, 35, 35, 100, 100);
        } else if (isAvailable) {
            // Draw green border for available dates
            g2.setColor(new Color(0, 200, 0, 100));
            int x = getWidth() / 2 - 17;
            int y = getHeight() / 2 - 17;
            g2.drawRoundRect(x, y, 35, 35, 100, 100);
        }
        
        super.paintComponent(grphcs);
    }
}