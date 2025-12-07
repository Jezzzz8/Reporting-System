package component;

import backend.objects.Data.*;

public class Scheduling extends javax.swing.JPanel {

    public Scheduling(User user) {
        initComponents();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ProgressHeaderPanel = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        SelectDateLabel = new javax.swing.JLabel();
        SelectTimeLabel = new javax.swing.JLabel();
        SelectDetailsLabel = new javax.swing.JLabel();
        SummaryConfirmationPanel = new javax.swing.JPanel();
        PreviousButton = new javax.swing.JButton();
        ContinueButton = new javax.swing.JButton();
        SchedulingTabbedPane = new component.NoTabJTabbedPane();
        SelectDatePanel = new javax.swing.JPanel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        EarliestAvailableButton = new javax.swing.JButton();
        ThisWeekButton = new javax.swing.JButton();
        NextWeekButton = new javax.swing.JButton();
        calendarCustom2 = new component.Calendar.CalendarCustom();
        ConfirmDetailsPanel = new javax.swing.JPanel();
        SelectTimePanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(250, 250, 250));
        setPreferredSize(new java.awt.Dimension(850, 550));

        ProgressHeaderPanel.setPreferredSize(new java.awt.Dimension(850, 50));

        jProgressBar1.setValue(50);

        SelectDateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SelectDateLabel.setText("1. Select Date");
        SelectDateLabel.setPreferredSize(new java.awt.Dimension(100, 16));

        SelectTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SelectTimeLabel.setText("2. Select Time");
        SelectTimeLabel.setPreferredSize(new java.awt.Dimension(100, 16));

        SelectDetailsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SelectDetailsLabel.setText("3. Confirm Details");
        SelectDetailsLabel.setPreferredSize(new java.awt.Dimension(100, 16));

        javax.swing.GroupLayout ProgressHeaderPanelLayout = new javax.swing.GroupLayout(ProgressHeaderPanel);
        ProgressHeaderPanel.setLayout(ProgressHeaderPanelLayout);
        ProgressHeaderPanelLayout.setHorizontalGroup(
            ProgressHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProgressHeaderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(ProgressHeaderPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(SelectDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(SelectTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(SelectDetailsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ProgressHeaderPanelLayout.setVerticalGroup(
            ProgressHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ProgressHeaderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ProgressHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SelectDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SelectTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SelectDetailsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
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

        javax.swing.GroupLayout SummaryConfirmationPanelLayout = new javax.swing.GroupLayout(SummaryConfirmationPanel);
        SummaryConfirmationPanel.setLayout(SummaryConfirmationPanelLayout);
        SummaryConfirmationPanelLayout.setHorizontalGroup(
            SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SummaryConfirmationPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(PreviousButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 105, Short.MAX_VALUE)
                .addComponent(ContinueButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        SummaryConfirmationPanelLayout.setVerticalGroup(
            SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SummaryConfirmationPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(SummaryConfirmationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SummaryConfirmationPanelLayout.createSequentialGroup()
                        .addComponent(ContinueButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SummaryConfirmationPanelLayout.createSequentialGroup()
                        .addComponent(PreviousButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25))))
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

        calendarCustom2.setPreferredSize(new java.awt.Dimension(450, 350));

        javax.swing.GroupLayout SelectDatePanelLayout = new javax.swing.GroupLayout(SelectDatePanel);
        SelectDatePanel.setLayout(SelectDatePanelLayout);
        SelectDatePanelLayout.setHorizontalGroup(
            SelectDatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SelectDatePanelLayout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addComponent(calendarCustom2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(72, 72, 72))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SelectDatePanelLayout.createSequentialGroup()
                .addGap(145, 145, 145)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(145, 145, 145))
        );
        SelectDatePanelLayout.setVerticalGroup(
            SelectDatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SelectDatePanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(calendarCustom2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        SchedulingTabbedPane.addTab("tab1", SelectDatePanel);

        ConfirmDetailsPanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout ConfirmDetailsPanelLayout = new javax.swing.GroupLayout(ConfirmDetailsPanel);
        ConfirmDetailsPanel.setLayout(ConfirmDetailsPanelLayout);
        ConfirmDetailsPanelLayout.setHorizontalGroup(
            ConfirmDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 590, Short.MAX_VALUE)
        );
        ConfirmDetailsPanelLayout.setVerticalGroup(
            ConfirmDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 489, Short.MAX_VALUE)
        );

        SchedulingTabbedPane.addTab("tab3", ConfirmDetailsPanel);

        SelectTimePanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setText("jLabel4");

        javax.swing.GroupLayout SelectTimePanelLayout = new javax.swing.GroupLayout(SelectTimePanel);
        SelectTimePanel.setLayout(SelectTimePanelLayout);
        SelectTimePanelLayout.setHorizontalGroup(
            SelectTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SelectTimePanelLayout.createSequentialGroup()
                .addGap(160, 160, 160)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        SelectTimePanelLayout.setVerticalGroup(
            SelectTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SelectTimePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        SchedulingTabbedPane.addTab("tab2", SelectTimePanel);

        SchedulingTabbedPane.setSelectedIndex(1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ProgressHeaderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(SchedulingTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(SummaryConfirmationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(ProgressHeaderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SchedulingTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
                    .addComponent(SummaryConfirmationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void PreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PreviousButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PreviousButtonActionPerformed

    private void EarliestAvailableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EarliestAvailableButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EarliestAvailableButtonActionPerformed

    private void NextWeekButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NextWeekButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NextWeekButtonActionPerformed

    private void ThisWeekButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ThisWeekButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ThisWeekButtonActionPerformed

    private void ContinueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ContinueButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ContinueButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ConfirmDetailsPanel;
    private javax.swing.JButton ContinueButton;
    private javax.swing.JButton EarliestAvailableButton;
    private javax.swing.JButton NextWeekButton;
    private javax.swing.JButton PreviousButton;
    private javax.swing.JPanel ProgressHeaderPanel;
    private component.NoTabJTabbedPane SchedulingTabbedPane;
    private javax.swing.JLabel SelectDateLabel;
    private javax.swing.JPanel SelectDatePanel;
    private javax.swing.JLabel SelectDetailsLabel;
    private javax.swing.JLabel SelectTimeLabel;
    private javax.swing.JPanel SelectTimePanel;
    private javax.swing.JPanel SummaryConfirmationPanel;
    private javax.swing.JButton ThisWeekButton;
    private component.Calendar.CalendarCustom calendarCustom2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JProgressBar jProgressBar1;
    // End of variables declaration//GEN-END:variables
}
