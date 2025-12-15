package component.DropdownButton;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import backend.objects.Data.Notification;

public class DropdownContentFactory {
    
    // Notification content
    public static CustomDropdownButton.DropdownContent createNotificationContent(
            List<Notification> notifications, Runnable onRefresh) {
        return new CustomDropdownButton.DropdownContent() {
            @Override
            public Component getContent() {
                JPanel panel = new JPanel(new BorderLayout());
                panel.setPreferredSize(new Dimension(300, 200));
                
                if (notifications == null || notifications.isEmpty()) {
                    JLabel noNotifications = new JLabel("No new notifications", SwingConstants.CENTER);
                    noNotifications.setForeground(Color.GRAY);
                    panel.add(noNotifications, BorderLayout.CENTER);
                } else {
                    DefaultListModel<String> listModel = new DefaultListModel<>();
                    for (Notification n : notifications) {
                        String statusIcon = "Unread".equals(n.getReadStatus()) ? "🔴 " : "⚪ ";
                        listModel.addElement(statusIcon + n.getMessage() + 
                            " (" + n.getNotificationTime() + ")");
                    }
                    
                    JList<String> notificationList = new JList<>(listModel);
                    notificationList.setCellRenderer(new NotificationCellRenderer());
                    JScrollPane scrollPane = new JScrollPane(notificationList);
                    panel.add(scrollPane, BorderLayout.CENTER);
                    
                    // Add mark all as read button
                    JButton markAllRead = new JButton("Mark All as Read");
                    markAllRead.addActionListener(e -> {
                        if (onRefresh != null) onRefresh.run();
                    });
                    panel.add(markAllRead, BorderLayout.SOUTH);
                }
                
                return panel;
            }
            
            @Override
            public String getTitle() {
                int unreadCount = (int) notifications.stream()
                    .filter(n -> "Unread".equals(n.getReadStatus()))
                    .count();
                return "Notifications" + (unreadCount > 0 ? " (" + unreadCount + ")" : "");
            }
            
            @Override
            public CustomDropdownButton.ContentType getType() {
                return CustomDropdownButton.ContentType.NOTIFICATIONS;
            }
        };
    }
    
    // Calendar content
    public static CustomDropdownButton.DropdownContent createCalendarContent() {
        return new CustomDropdownButton.DropdownContent() {
            @Override
            public Component getContent() {
                JPanel panel = new JPanel(new BorderLayout());
                panel.setPreferredSize(new Dimension(250, 250));
                
                // Create calendar
                JPanel calendarPanel = new JPanel(new BorderLayout());
                
                // Month navigation
                JPanel navPanel = new JPanel(new BorderLayout());
                JButton prevMonth = new JButton("<");
                JButton nextMonth = new JButton(">");
                JLabel monthLabel = new JLabel(
                    LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    SwingConstants.CENTER
                );
                
                navPanel.add(prevMonth, BorderLayout.WEST);
                navPanel.add(monthLabel, BorderLayout.CENTER);
                navPanel.add(nextMonth, BorderLayout.EAST);
                
                // Calendar grid
                JPanel gridPanel = new JPanel(new GridLayout(7, 7));
                
                // Day headers
                String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
                for (String day : days) {
                    JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
                    dayLabel.setFont(dayLabel.getFont().deriveFont(Font.BOLD));
                    gridPanel.add(dayLabel);
                }
                
                // Fill calendar days
                LocalDate today = LocalDate.now();
                LocalDate firstDay = today.withDayOfMonth(1);
                int startDay = firstDay.getDayOfWeek().getValue() % 7; // 0=Sunday
                
                // Empty cells for days before the first day of month
                for (int i = 0; i < startDay; i++) {
                    gridPanel.add(new JLabel(""));
                }
                
                // Days of month
                int daysInMonth = today.lengthOfMonth();
                for (int day = 1; day <= daysInMonth; day++) {
                    JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
                    if (day == today.getDayOfMonth()) {
                        dayLabel.setOpaque(true);
                        dayLabel.setBackground(new Color(41, 128, 185));
                        dayLabel.setForeground(Color.WHITE);
                        dayLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
                    }
                    gridPanel.add(dayLabel);
                }
                
                calendarPanel.add(navPanel, BorderLayout.NORTH);
                calendarPanel.add(gridPanel, BorderLayout.CENTER);
                
                panel.add(calendarPanel, BorderLayout.CENTER);
                
                // Add quick date buttons
                JPanel quickPanel = new JPanel(new FlowLayout());
                JButton todayBtn = new JButton("Today");
                JButton scheduleBtn = new JButton("Schedule");
                quickPanel.add(todayBtn);
                quickPanel.add(scheduleBtn);
                
                panel.add(quickPanel, BorderLayout.SOUTH);
                
                return panel;
            }
            
            @Override
            public String getTitle() {
                return "Calendar";
            }
            
            @Override
            public CustomDropdownButton.ContentType getType() {
                return CustomDropdownButton.ContentType.CALENDAR;
            }
        };
    }
    
    // Quick Actions content
    public static CustomDropdownButton.DropdownContent createQuickActionsContent(
            Runnable onRefresh, Runnable onPrint, Runnable onExport) {
        return new CustomDropdownButton.DropdownContent() {
            @Override
            public Component getContent() {
                JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
                panel.setPreferredSize(new Dimension(200, 150));
                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                // Create action buttons
                JButton refreshBtn = createActionButton("🔄 Refresh Dashboard", onRefresh);
                JButton printBtn = createActionButton("🖨️ Print Summary", onPrint);
                JButton exportBtn = createActionButton("📤 Export Data", onExport);
                JButton settingsBtn = createActionButton("⚙️ Settings", () -> {
                    JOptionPane.showMessageDialog(panel, "Settings dialog would open here");
                });
                
                panel.add(refreshBtn);
                panel.add(printBtn);
                panel.add(exportBtn);
                panel.add(settingsBtn);
                
                return panel;
            }
            
            private JButton createActionButton(String text, Runnable action) {
                JButton button = new JButton(text);
                button.setHorizontalAlignment(SwingConstants.LEFT);
                button.setFocusPainted(false);
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.addActionListener(e -> {
                    if (action != null) action.run();
                });
                return button;
            }
            
            @Override
            public String getTitle() {
                return "Quick Actions";
            }
            
            @Override
            public CustomDropdownButton.ContentType getType() {
                return CustomDropdownButton.ContentType.QUICK_ACTIONS;
            }
        };
    }
    
    // Custom cell renderer for notifications
    private static class NotificationCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
            
            // Style based on read status
            String text = value.toString();
            if (text.startsWith("🔴")) {
                label.setFont(label.getFont().deriveFont(Font.BOLD));
                label.setForeground(Color.BLACK);
            } else {
                label.setForeground(Color.DARK_GRAY);
            }
            
            label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            return label;
        }
    }
}
