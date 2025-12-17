package component.CustomDatePicker;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.Timer;

public class CustomDatePicker extends JPanel implements FocusListener {
    private JButton dropdownButton;
    private JPopupMenu calendarPopup;
    private Date selectedDate;
    private String placeholder;
    private Color placeholderColor = new Color(150, 150, 150);
    private Color normalColor = Color.BLACK;
    private Color focusedBorderColor = new Color(0, 120, 215);
    private Color unfocusedBorderColor = new Color(200, 200, 200);
    private Color backgroundColor = new Color(249, 241, 240);
    private Color hoverColor = new Color(0, 120, 215, 30);
    private Color todayColor = new Color(0, 120, 215);
    private Color selectedColor = new Color(41, 128, 185);
    private Color disabledColor = new Color(200, 200, 200);
    private Color monthYearLabelColor = new Color(0, 120, 215);
    private Color monthYearHoverColor = new Color(0, 100, 180);
    private TitledBorder titledBorder;
    private Timer focusTimer;
    private Timer heightTimer;
    private float borderThickness = 1.0f;
    private float currentHeight = 40.0f;
    private float maxHeight = 50.0f;
    private float minHeight = 40.0f;
    private boolean isFocused = false;
    private boolean isOpen = false;
    private SimpleDateFormat displayFormat;
    private Calendar calendar;
    private JLabel monthLabel;
    private JLabel yearLabel;
    private JPanel calendarPanel;
    private JPanel gridPanel;
    
    // Date selection listener
    public interface DateSelectionListener {
        void dateSelected(Date date);
    }
    
    private DateSelectionListener dateListener;
    
    public CustomDatePicker() {
        this("Select date", new Date());
    }
    
    public CustomDatePicker(String placeholder) {
        this(placeholder, new Date());
    }
    
    public CustomDatePicker(String placeholder, Date initialDate) {
        this.placeholder = placeholder;
        this.selectedDate = initialDate;
        this.displayFormat = new SimpleDateFormat("MMM dd, yyyy");
        this.calendar = Calendar.getInstance();
        if (initialDate != null) {
            calendar.setTime(initialDate);
        }
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        
        // Create the initial border with empty title
        titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(unfocusedBorderColor, 1),
            ""
        );
        titledBorder.setTitleColor(placeholderColor);
        titledBorder.setTitlePosition(TitledBorder.TOP);
        titledBorder.setTitleJustification(TitledBorder.LEFT);
        titledBorder.setTitleFont(new Font("Segoe UI", Font.PLAIN, 10));
        setBorder(titledBorder);
        
        // Create dropdown button
        dropdownButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Paint placeholder text if empty and not focused
                String buttonText = getText();
                if (!isFocused && !isOpen && (buttonText == null || buttonText.isEmpty()) && 
                    placeholder != null && !placeholder.isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(placeholderColor);
                    g2.setFont(getFont().deriveFont(Font.PLAIN));
                    
                    FontMetrics fm = g2.getFontMetrics();
                    int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    
                    // Adjust for calendar icon
                    g2.drawString(placeholder, 10, textY);
                    g2.dispose();
                }
            }
        };
        
        dropdownButton.setLayout(new BorderLayout());
        dropdownButton.setBackground(backgroundColor);
        dropdownButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 30)); // Right padding for arrow
        dropdownButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dropdownButton.setFocusPainted(false);
        dropdownButton.setContentAreaFilled(false);
        dropdownButton.setOpaque(false);
        dropdownButton.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Set initial text
        updateButtonText();
        
        // Add mouse listener for hover effect
        dropdownButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isOpen) {
                    setBackground(new Color(
                        backgroundColor.getRed(),
                        backgroundColor.getGreen(),
                        backgroundColor.getBlue(),
                        200
                    ));
                    repaint();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!isOpen) {
                    setBackground(backgroundColor);
                    repaint();
                }
            }
        });
        
        // Add action listener to open calendar
        dropdownButton.addActionListener(e -> toggleCalendar());
        
        // Add focus listener to panel
        addFocusListener(this);
        
        // Make the button focusable through the panel
        dropdownButton.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                borderThickness = 1.0f;
                focusTimer.start();
                heightTimer.start();
                repaint();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                focusTimer.start();
                heightTimer.start();
                repaint();
            }
        });
        
        add(dropdownButton, BorderLayout.CENTER);
        
        // Create calendar popup
        calendarPopup = new JPopupMenu() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background matching the text field
                g2.setColor(backgroundColor);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Border similar to text field when focused
                g2.setColor(focusedBorderColor);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        calendarPopup.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        calendarPopup.setBackground(backgroundColor);
        
        // Initialize calendar UI
        initCalendarPanel();
        
        // Set initial preferred size
        setPreferredSize(new Dimension(300, (int) currentHeight));
        
        // Initialize animation timers
        focusTimer = new Timer(10, e -> animateBorder());
        focusTimer.setRepeats(true);
        
        heightTimer = new Timer(10, e -> animateHeight());
        heightTimer.setRepeats(true);
        
        // Add global mouse listener to close popup when clicking outside
        setupGlobalMouseListener();
    }
    
    private void setupGlobalMouseListener() {
        // This listens for clicks anywhere in the application
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (event.getID() == MouseEvent.MOUSE_PRESSED && isOpen) {
                    MouseEvent mouseEvent = (MouseEvent) event;
                    Component source = (Component) mouseEvent.getSource();
                    
                    // Check if the click is outside our component and popup
                    boolean clickInPopup = isInPopup(source, mouseEvent.getPoint());
                    boolean clickInComponent = isInComponent(source, mouseEvent.getPoint());
                    
                    if (!clickInPopup && !clickInComponent) {
                        // Clicked outside, close the calendar
                        SwingUtilities.invokeLater(() -> closeCalendar());
                    }
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK);
    }
    
    private boolean isInPopup(Component source, Point point) {
        // Check if the click is inside the popup
        if (calendarPopup != null && calendarPopup.isVisible()) {
            Point popupLocation = calendarPopup.getLocationOnScreen();
            Rectangle popupBounds = new Rectangle(popupLocation, calendarPopup.getSize());
            Point screenPoint = new Point(point);
            
            // Convert component-relative point to screen coordinates
            if (source != null) {
                Point compLocation = source.getLocationOnScreen();
                screenPoint.translate(compLocation.x, compLocation.y);
            }
            
            return popupBounds.contains(screenPoint);
        }
        return false;
    }
    
    private boolean isInComponent(Component source, Point point) {
        // Check if the click is inside our component
        if (this.isShowing()) {
            Point compLocation = this.getLocationOnScreen();
            Rectangle compBounds = new Rectangle(compLocation, this.getSize());
            Point screenPoint = new Point(point);
            
            // Convert component-relative point to screen coordinates
            if (source != null) {
                Point sourceLocation = source.getLocationOnScreen();
                screenPoint.translate(sourceLocation.x, sourceLocation.y);
            }
            
            return compBounds.contains(screenPoint);
        }
        return false;
    }
    
    private void initCalendarPanel() {
        calendarPanel = new JPanel(new BorderLayout(0, 10));
        calendarPanel.setBackground(backgroundColor);
        calendarPanel.setPreferredSize(new Dimension(300, 320));
        
        // Create month/year navigation panel
        JPanel monthYearPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        monthYearPanel.setBackground(backgroundColor);
        monthYearPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Month navigation panel (top)
        JPanel monthPanel = new JPanel(new BorderLayout(5, 0));
        monthPanel.setBackground(backgroundColor);
        
        JButton prevMonth = createArrowButton("◀", "Previous month");
        JButton nextMonth = createArrowButton("▶", "Next month");
        
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        monthLabel.setForeground(monthYearLabelColor);
        
        // Year navigation panel (bottom)
        JPanel yearPanel = new JPanel(new BorderLayout(5, 0));
        yearPanel.setBackground(backgroundColor);
        
        JButton prevYear = createArrowButton("◀", "Previous year");
        JButton nextYear = createArrowButton("▶", "Next year");
        
        yearLabel = new JLabel("", SwingConstants.CENTER);
        yearLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        yearLabel.setForeground(monthYearLabelColor);
        
        // Update labels
        updateMonthYearLabels();
        
        // Add action listeners
        prevMonth.addActionListener(e -> {
            calendar.add(Calendar.MONTH, -1);
            updateMonthYearLabels();
            updateCalendarGrid();
        });
        
        nextMonth.addActionListener(e -> {
            calendar.add(Calendar.MONTH, 1);
            updateMonthYearLabels();
            updateCalendarGrid();
        });
        
        prevYear.addActionListener(e -> {
            calendar.add(Calendar.YEAR, -1);
            updateMonthYearLabels();
            updateCalendarGrid();
        });
        
        nextYear.addActionListener(e -> {
            calendar.add(Calendar.YEAR, 1);
            updateMonthYearLabels();
            updateCalendarGrid();
        });
        
        // Assemble month panel
        monthPanel.add(prevMonth, BorderLayout.WEST);
        monthPanel.add(monthLabel, BorderLayout.CENTER);
        monthPanel.add(nextMonth, BorderLayout.EAST);
        
        // Assemble year panel
        yearPanel.add(prevYear, BorderLayout.WEST);
        yearPanel.add(yearLabel, BorderLayout.CENTER);
        yearPanel.add(nextYear, BorderLayout.EAST);
        
        // Add to monthYearPanel
        monthYearPanel.add(monthPanel);
        monthYearPanel.add(yearPanel);
        
        // Calendar grid
        gridPanel = new JPanel(new GridLayout(7, 7, 5, 5));
        gridPanel.setBackground(backgroundColor);
        gridPanel.setName("calendarGrid");
        
        // Fill calendar grid
        updateCalendarGrid();
        
        // Button panel at bottom right
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Today button
        JButton todayButton = new JButton("Today");
        todayButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        todayButton.setBackground(todayColor);
        todayButton.setForeground(Color.WHITE);
        todayButton.setFocusPainted(false);
        todayButton.setBorderPainted(false);
        todayButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        todayButton.addActionListener(e -> {
            setDate(new Date());
            closeCalendar();
        });
        
        // Clear button
        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clearButton.setBackground(unfocusedBorderColor);
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setBorderPainted(false);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.addActionListener(e -> {
            setDate(null);
            closeCalendar();
        });
        
        // OK button
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        okButton.setBackground(focusedBorderColor);
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.setBorderPainted(false);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.addActionListener(e -> {
            if (selectedDate == null) {
                // If no date is selected, select current day in displayed month
                setDate(calendar.getTime());
            }
            closeCalendar();
        });
        
        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelButton.setBackground(unfocusedBorderColor);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> closeCalendar());
        
        buttonPanel.add(todayButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        // Assemble calendar
        calendarPanel.add(monthYearPanel, BorderLayout.NORTH);
        calendarPanel.add(gridPanel, BorderLayout.CENTER);
        calendarPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JButton createArrowButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(focusedBorderColor);
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(focusedBorderColor.getRed(), 
                    focusedBorderColor.getGreen(), 
                    focusedBorderColor.getBlue(), 30));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }
    
    private void updateMonthYearLabels() {
        if (monthLabel != null) {
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
            monthLabel.setText(monthFormat.format(calendar.getTime()));
        }
        if (yearLabel != null) {
            yearLabel.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        }
    }
    
    private void updateCalendarGrid() {
        if (gridPanel == null) return;
        
        gridPanel.removeAll();
        
        // Day headers
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : days) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            dayLabel.setForeground(focusedBorderColor);
            gridPanel.add(dayLabel);
        }
        
        // Get first day of month
        Calendar tempCal = (Calendar) calendar.clone();
        tempCal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Adjust firstDayOfWeek to be 0-based (Sunday = 0)
        firstDayOfWeek = firstDayOfWeek - 1;
        if (firstDayOfWeek < 0) firstDayOfWeek = 6;
        
        // Fill empty cells for days before the first day
        for (int i = 0; i < firstDayOfWeek; i++) {
            gridPanel.add(new JLabel(""));
        }
        
        // Today's date
        Calendar today = Calendar.getInstance();
        
        // Create day buttons
        for (int day = 1; day <= daysInMonth; day++) {
            final int currentDay = day;
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            dayButton.setFocusPainted(false);
            dayButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            dayButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Create a calendar instance for this specific day
            Calendar dayCal = (Calendar) calendar.clone();
            dayCal.set(Calendar.DAY_OF_MONTH, currentDay);
            
            // Check if this is today
            boolean isToday = isSameDay(dayCal.getTime(), today.getTime());
            
            // Check if this is selected date
            boolean isSelected = (selectedDate != null && isSameDay(dayCal.getTime(), selectedDate));
            
            // Set button appearance
            if (isSelected) {
                dayButton.setBackground(selectedColor);
                dayButton.setForeground(Color.WHITE);
            } else if (isToday) {
                dayButton.setBackground(todayColor);
                dayButton.setForeground(Color.WHITE);
            } else {
                dayButton.setBackground(backgroundColor);
                dayButton.setForeground(normalColor);
                
                // Gray out days from other months
                if (dayCal.get(Calendar.MONTH) != calendar.get(Calendar.MONTH)) {
                    dayButton.setForeground(disabledColor);
                }
            }
            
            // Add hover effect
            dayButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!isSelected && !isToday) {
                        dayButton.setBackground(hoverColor);
                    }
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    if (!isSelected && !isToday) {
                        dayButton.setBackground(backgroundColor);
                    }
                }
            });
            
            // Add action listener
            dayButton.addActionListener(e -> {
                Calendar selectedCal = (Calendar) calendar.clone();
                selectedCal.set(Calendar.DAY_OF_MONTH, currentDay);
                setDate(selectedCal.getTime());
            });
            
            gridPanel.add(dayButton);
        }
        
        // Fill remaining cells to complete 6 weeks (42 cells total)
        int totalCells = 42;
        int usedCells = firstDayOfWeek + daysInMonth;
        for (int i = usedCells; i < totalCells; i++) {
            gridPanel.add(new JLabel(""));
        }
        
        gridPanel.revalidate();
        gridPanel.repaint();
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
    
    private void toggleCalendar() {
        if (isOpen) {
            closeCalendar();
        } else {
            openCalendar();
        }
    }
    
    private void openCalendar() {
        // Update calendar to selected date or today
        if (selectedDate != null) {
            calendar.setTime(selectedDate);
        } else {
            calendar.setTime(new Date());
        }
        
        // Update labels
        updateMonthYearLabels();
        
        // Clear and add calendar panel
        calendarPopup.removeAll();
        calendarPopup.add(calendarPanel);
        
        // Update the display
        updateCalendarGrid();
        
        // Show the popup
        calendarPopup.show(this, 0, getHeight());
        isOpen = true;
        isFocused = true;
        borderThickness = 1.0f;
        focusTimer.start();
        heightTimer.start();
        repaint();
    }
    
    private void closeCalendar() {
        if (calendarPopup.isVisible()) {
            calendarPopup.setVisible(false);
        }
        isOpen = false;
        isFocused = false;
        borderThickness = 1.0f;
        focusTimer.start();
        heightTimer.start();
        repaint();
    }
    
    private void animateBorder() {
        if (isFocused || isOpen) {
            if (borderThickness < 2.0f) {
                borderThickness += 0.2f;
                updateBorder(true);
            } else {
                focusTimer.stop();
            }
        } else {
            if (borderThickness > 1.0f) {
                borderThickness -= 0.2f;
                if (borderThickness <= 1.0f) {
                    borderThickness = 1.0f;
                    updateBorder(false);
                    focusTimer.stop();
                } else {
                    updateBorder(false);
                }
            }
        }
    }
    
    private void animateHeight() {
        if (isFocused || isOpen) {
            if (currentHeight < maxHeight) {
                currentHeight += 0.5f;
                updateSize();
            } else {
                heightTimer.stop();
            }
        } else {
            if (currentHeight > minHeight) {
                currentHeight -= 0.5f;
                updateSize();
            } else {
                heightTimer.stop();
            }
        }
    }
    
    private void updateSize() {
        setPreferredSize(new Dimension(getWidth(), (int) currentHeight));
        revalidate();
        repaint();
        
        Container parent = getParent();
        if (parent != null) {
            parent.revalidate();
            parent.repaint();
        }
    }
    
    private void updateBorder(boolean focused) {
        if (titledBorder == null) return;
        
        if (focused && placeholder != null && !placeholder.isEmpty()) {
            titledBorder.setTitle(placeholder);
            titledBorder.setBorder(BorderFactory.createLineBorder(focusedBorderColor, Math.round(borderThickness)));
            titledBorder.setTitleColor(focusedBorderColor);
            titledBorder.setTitleFont(new Font("Segoe UI", Font.PLAIN, 10));
        } else {
            titledBorder.setTitle("");
            titledBorder.setBorder(BorderFactory.createLineBorder(unfocusedBorderColor, 1));
            titledBorder.setTitleColor(placeholderColor);
        }
        revalidate();
        repaint();
    }
    
    private void updateButtonText() {
        if (selectedDate != null) {
            dropdownButton.setText(displayFormat.format(selectedDate));
            dropdownButton.setForeground(normalColor);
        } else {
            dropdownButton.setText("");
            dropdownButton.setForeground(placeholderColor);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fill background
        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        g2.dispose();
        
        // Draw calendar icon/arrow
        drawCalendarIcon(g);
    }
    
    private void drawCalendarIcon(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int iconSize = 16;
        int x = getWidth() - iconSize - 10;
        int y = getHeight() / 2 - iconSize / 2;
        
        // Use focused color when dropdown is open or focused
        if (isOpen || isFocused) {
            g2.setColor(focusedBorderColor);
        } else {
            g2.setColor(placeholderColor);
        }
        
        // Draw calendar icon
        g2.setStroke(new BasicStroke(1.5f));
        
        // Calendar body
        g2.drawRoundRect(x + 2, y + 6, 12, 10, 2, 2);
        
        // Calendar top
        g2.fillRect(x + 2, y + 2, 12, 4);
        
        // Calendar lines
        g2.drawLine(x + 6, y + 6, x + 6, y + 16);
        g2.drawLine(x + 10, y + 6, x + 10, y + 16);
        
        g2.dispose();
    }
    
    // ========== PUBLIC METHODS ==========
    
    public void setDate(Date date) {
        this.selectedDate = date;
        if (date != null) {
            calendar.setTime(date);
        }
        updateButtonText();
        
        if (dateListener != null) {
            dateListener.dateSelected(date);
        }
        
        repaint();
    }
    
    public Date getDate() {
        return selectedDate;
    }
    
    public void setDateSelectionListener(DateSelectionListener listener) {
        this.dateListener = listener;
    }
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        dropdownButton.repaint();
        if (isFocused || isOpen) {
            updateBorder(true);
        }
    }
    
    public String getPlaceholder() {
        return placeholder;
    }
    
    public void setDisplayFormat(SimpleDateFormat format) {
        this.displayFormat = format;
        updateButtonText();
    }
    
    public SimpleDateFormat getDisplayFormat() {
        return displayFormat;
    }
    
    public void setPlaceholderColor(Color color) {
        this.placeholderColor = color;
        if (titledBorder != null) {
            titledBorder.setTitleColor(placeholderColor);
            repaint();
        }
        dropdownButton.repaint();
    }
    
    public void setFocusedBorderColor(Color color) {
        this.focusedBorderColor = color;
    }
    
    public void setUnfocusedBorderColor(Color color) {
        this.unfocusedBorderColor = color;
        if (!isFocused && !isOpen) {
            updateBorder(false);
        }
    }
    
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        setBackground(color);
        dropdownButton.setBackground(color);
        repaint();
    }
    
    public void setExpandedHeight(int height) {
        this.maxHeight = height;
    }
    
    public void setNormalHeight(int height) {
        this.minHeight = height;
        this.currentHeight = height;
        updateSize();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        dropdownButton.setEnabled(enabled);
        if (!enabled) {
            setBorder(BorderFactory.createLineBorder(disabledColor, 1));
            dropdownButton.setForeground(disabledColor);
        } else {
            updateBorder(isFocused || isOpen);
            updateButtonText();
        }
        repaint();
    }
    
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (dropdownButton != null) {
            dropdownButton.setFont(font);
        }
        if (titledBorder != null) {
            titledBorder.setTitleFont(font.deriveFont(10f));
        }
    }
    
    @Override
    public void focusGained(FocusEvent e) {
        isFocused = true;
        borderThickness = 1.0f;
        focusTimer.start();
        heightTimer.start();
        repaint();
    }
    
    @Override
    public void focusLost(FocusEvent e) {
        isFocused = false;
        focusTimer.start();
        heightTimer.start();
        repaint();
    }
    
    @Override
    public void requestFocus() {
        dropdownButton.requestFocus();
    }
    
    public void clear() {
        setDate(null);
    }
    
    // Utility method to parse date from string
    public void setDateFromString(String dateString, SimpleDateFormat format) {
        try {
            setDate(format.parse(dateString));
        } catch (Exception e) {
            setDate(null);
        }
    }
    
    // Get the calendar instance
    public Calendar getCalendar() {
        return (Calendar) calendar.clone();
    }
    
    // Set the calendar directly
    public void setCalendar(Calendar cal) {
        this.calendar = (Calendar) cal.clone();
        updateMonthYearLabels();
    }
}