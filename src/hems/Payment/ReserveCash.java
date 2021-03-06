
package hems.Payment;

import HEMS.DBUtil;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

 
public class ReserveCash extends javax.swing.JFrame {

    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pst = null;
    
    public ReserveCash() throws SQLException {
        initComponents();
        setLocationRelativeTo(null);
        conn = DBUtil.getConnection();
        showDate();
        paymentType();
        chequeDialog.setLocationRelativeTo(null);
        chequeDialog.setModal(true);
        PaymentTypeBox.addItemListener((ItemEvent e) -> {
            if(e.getStateChange() == ItemEvent.SELECTED){
                modals();
            }
        });
    }
    private void modals(){
        int modal = PaymentTypeBox.getSelectedIndex();
        switch(modal){
            case 1:
                chequeAmountField.setText(amountField.getText());
                chequeDialog.setVisible(true);
                break;      
        }
    }
    private void showDate(){
        Calendar cal = new GregorianCalendar();
        dateField.setDateFormatString("yyyy-MM-dd");
        dateField.setDate(cal.getTime());
    }
    private void getPatient(){
        String patientQuery = "SELECT * FROM PATIENT WHERE MRN = ?";
        try {
            pst = conn.prepareStatement(patientQuery);
            pst.setString(1, mrnField.getText());
            rs = pst.executeQuery();
            if(rs.next()){
                String fname = rs.getString("firstname");
                String lname = rs.getString("surname");
                nameField.setText(fname+" "+lname);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReserveCash.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    private void paymentType(){
        String paymenttypeQuery = "SELECT * FROM PAYMENTTYPE";
        try {
            pst = conn.prepareStatement(paymenttypeQuery);
            rs = pst.executeQuery();
            while(rs.next()){
                PaymentTypeBox.addItem(rs.getString("type"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReserveCash.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chequeDialog = new javax.swing.JDialog();
        chequePanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        chequeNoField = new javax.swing.JTextField();
        bankNameField = new javax.swing.JTextField();
        chequeResetButton = new javax.swing.JButton();
        chequeSubmitButton = new javax.swing.JButton();
        dateChooser = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        chequeAmountField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        branchField = new javax.swing.JTextField();
        depositPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        PaymentTypeBox = new javax.swing.JComboBox();
        mrnField = new javax.swing.JTextField();
        amountField = new javax.swing.JTextField();
        saveButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        reprintButton = new javax.swing.JButton();
        dateField = new com.toedter.calendar.JTextFieldDateEditor();
        operatorLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();

        chequeDialog.setTitle("ENTER CHEQUE DETAILS");
        chequeDialog.setMinimumSize(new java.awt.Dimension(455, 282));

        chequePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Enter cheque details"));

        jLabel5.setText("Cheque Number:");

        jLabel6.setText("Bank Name:");

        jLabel7.setText("Date:");

        chequeResetButton.setText("RESET");
        chequeResetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chequeResetButtonActionPerformed(evt);
            }
        });

        chequeSubmitButton.setText("SAVE");
        chequeSubmitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chequeSubmitButtonActionPerformed(evt);
            }
        });

        dateChooser.setDateFormatString("yyyy-MM-dd");

        jLabel8.setText("Amount:");

        chequeAmountField.setEditable(false);

        jLabel9.setText("Branch:");

        javax.swing.GroupLayout chequePanelLayout = new javax.swing.GroupLayout(chequePanel);
        chequePanel.setLayout(chequePanelLayout);
        chequePanelLayout.setHorizontalGroup(
            chequePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chequePanelLayout.createSequentialGroup()
                .addGap(97, 97, 97)
                .addComponent(chequeSubmitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chequeResetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(chequePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(chequePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(chequePanelLayout.createSequentialGroup()
                        .addGroup(chequePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(chequePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bankNameField)
                            .addComponent(chequeNoField, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)))
                    .addGroup(chequePanelLayout.createSequentialGroup()
                        .addGroup(chequePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(chequePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(chequePanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(branchField, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(chequePanelLayout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(dateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chequeAmountField)))))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        chequePanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {bankNameField, chequeNoField});

        chequePanelLayout.setVerticalGroup(
            chequePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chequePanelLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(chequePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(chequeNoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(chequePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bankNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(chequePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(branchField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(chequePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(chequePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chequeAmountField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(chequePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chequeResetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chequeSubmitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        chequePanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {chequeAmountField, dateChooser});

        javax.swing.GroupLayout chequeDialogLayout = new javax.swing.GroupLayout(chequeDialog.getContentPane());
        chequeDialog.getContentPane().setLayout(chequeDialogLayout);
        chequeDialogLayout.setHorizontalGroup(
            chequeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chequeDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chequePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        chequeDialogLayout.setVerticalGroup(
            chequeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chequeDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chequePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        depositPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Deposit Fund"));

        jLabel1.setText("MRN: ");

        jLabel2.setText("Amount: ");

        jLabel3.setText("Payment Type:");

        mrnField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mrnFieldActionPerformed(evt);
            }
        });

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        reprintButton.setText("Reprint");

        dateField.setEditable(false);

        nameField.setEditable(false);

        jLabel4.setText("NAME:");

        javax.swing.GroupLayout depositPanelLayout = new javax.swing.GroupLayout(depositPanel);
        depositPanel.setLayout(depositPanelLayout);
        depositPanelLayout.setHorizontalGroup(
            depositPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(depositPanelLayout.createSequentialGroup()
                .addGroup(depositPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(depositPanelLayout.createSequentialGroup()
                        .addGap(160, 160, 160)
                        .addComponent(operatorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(50, 50, 50)
                        .addComponent(dateField, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, depositPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(depositPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(depositPanelLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(mrnField, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(nameField))
                            .addGroup(depositPanelLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(amountField, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(PaymentTypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(29, Short.MAX_VALUE))
            .addGroup(depositPanelLayout.createSequentialGroup()
                .addGap(101, 101, 101)
                .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resetButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(reprintButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        depositPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {reprintButton, resetButton, saveButton});

        depositPanelLayout.setVerticalGroup(
            depositPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(depositPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(depositPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dateField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(operatorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(depositPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(mrnField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(25, 25, 25)
                .addGroup(depositPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(amountField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(PaymentTypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                .addGroup(depositPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reprintButton)
                    .addComponent(resetButton))
                .addGap(31, 31, 31))
        );

        depositPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {reprintButton, resetButton, saveButton});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(depositPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(depositPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mrnFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mrnFieldActionPerformed
        getPatient();
    }//GEN-LAST:event_mrnFieldActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        mrnField.setText(null);
        nameField.setText(null);
        amountField.setText(null);
        PaymentTypeBox.setSelectedIndex(0);
    }//GEN-LAST:event_resetButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        paymentInput();
    }//GEN-LAST:event_saveButtonActionPerformed
    private void paymentInput(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(dateField.getDate());
        try {
            String insertDeposit = "INSERT INTO RESERVEFUND (mrn, date, amount, paymenttype) VALUES (?,?,?,?)";
            pst = conn.prepareStatement(insertDeposit);
            long id;
            pst = conn.prepareStatement(insertDeposit, PreparedStatement.RETURN_GENERATED_KEYS);
            String amountString = amountField.getText();
            double amount = Double.parseDouble(amountString);
            pst.setString(1, mrnField.getText());
            pst.setString(2, date);   
            pst.setDouble(3, amount);
            pst.setString(4, PaymentTypeBox.getSelectedItem().toString());
            pst.executeUpdate();
            rs = pst.getGeneratedKeys();
            if(rs != null && rs.next()){
                    id = rs.getLong(1);
                    String receipt = "DEP"+id;
                    String receiptno = "UPDATE reservefund set receiptno = ? WHERE id = ?";
                    pst = conn.prepareStatement(receiptno);
                    pst.setString(1, receipt);
                    pst.setLong(2, id);
                    pst.executeUpdate();
                    }
            JOptionPane.showMessageDialog(null, "Money reserved on account", "Reserve Status", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    private void paymentCheque(){
        String insertCheque = "INSERT INTO CHEQUEDETAILS(chequeno, bankname, branch, mrn, date) VALUES (?,?,?,?,?)";
        try {
            pst = conn.prepareStatement(insertCheque);
            int col = 1;
            pst.setString(col++, chequeNoField.getText());
            pst.setString(col++, bankNameField.getText());
            pst.setString(col++, branchField.getText());
            pst.setString(col++, mrnField.getText());
            pst.setString(col++, dateChooser.getDate().toString());
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ReserveCash.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    private void chequeSubmitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chequeSubmitButtonActionPerformed
        
        double chequeAmount = Double.parseDouble(chequeAmountField.getText());
        double amount = Double.parseDouble(amountField.getText());
        if(chequeAmount == amount){
            paymentCheque();
            chequeDialog.dispose();
        }
        else if (chequeAmount < amount){
            JOptionPane.showMessageDialog(null, "Amount on cheque is less than stated deposit");
        }
        else{
             JOptionPane.showMessageDialog(null, "Amount on cheque is more than stated deposit");
        }
    }//GEN-LAST:event_chequeSubmitButtonActionPerformed

    private void chequeResetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chequeResetButtonActionPerformed
        chequeNoField.setText(null);
        branchField.setText(null);
        bankNameField.setText(null);
        dateChooser.setDate(null);
    }//GEN-LAST:event_chequeResetButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows Classic".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ReserveCash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReserveCash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReserveCash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReserveCash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new ReserveCash().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(ReserveCash.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox PaymentTypeBox;
    private javax.swing.JTextField amountField;
    private javax.swing.JTextField bankNameField;
    private javax.swing.JTextField branchField;
    private javax.swing.JTextField chequeAmountField;
    private javax.swing.JDialog chequeDialog;
    private javax.swing.JTextField chequeNoField;
    private javax.swing.JPanel chequePanel;
    private javax.swing.JButton chequeResetButton;
    private javax.swing.JButton chequeSubmitButton;
    private com.toedter.calendar.JDateChooser dateChooser;
    private com.toedter.calendar.JTextFieldDateEditor dateField;
    private javax.swing.JPanel depositPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField mrnField;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel operatorLabel;
    private javax.swing.JButton reprintButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables
}
