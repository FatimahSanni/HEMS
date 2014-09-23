
package hems.Registration;

import HEMS.DBUtil;
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

/**
 *
 * @author Fatimah
 */
public class Followup extends javax.swing.JFrame {

    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pst, pst1 = null;
    
    public Followup() throws SQLException {
        initComponents();
        conn = DBUtil.getConnection();
        setLocationRelativeTo(null);
        showDate();
        ConsultantLists();
    }
    private void showDate(){
        Calendar cal = new GregorianCalendar();
        dateField.setDateFormatString("yyyy-MM-dd");
        dateField.setDate(cal.getTime());
    }
    private void getCost(){
        String amountQuery = "SELECT * FROM PATIENTCATEGORY WHERE CATEGORY = ?";
        try {
            pst = conn.prepareStatement(amountQuery);
            pst.setString(1, pcatField.getText());
            rs = pst.executeQuery();
            if(rs.next()){
                costField.setText(Double.toString(rs.getDouble("fup")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Followup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void ConsultantLists(){
        String consultantQuery = "SELECT * FROM CONSULTANTS";
        try {
            pst = conn.prepareStatement(consultantQuery);
            rs = pst.executeQuery();
            while(rs.next()){
                consultantBox.addItem(rs.getString("consultant"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Followup.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    private void Invoice(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(dateField.getDate());
        String insertdetails = "INSERT INTO TINVOICE (mrn, NAME, description, amount, consultant, MODULE, date) VALUES (?,?,?,?,?,?,?)";
        String mrn  = mrnField.getText().toUpperCase();
        String name = fnameField.getText()+" "+surnameField.getText();
        try {
            long id;
            pst = conn.prepareStatement(insertdetails, PreparedStatement.RETURN_GENERATED_KEYS);
            int col = 1;
            pst.setString(col++, mrn);
            pst.setString(col++, name);
            pst.setString(col++, "Follow Up");
            pst.setDouble(col++, Double.parseDouble(costField.getText()));
            pst.setString(col++, consultantBox.getSelectedItem().toString());
            pst.setString(col++, "FOLLOWUP");
            pst.setString(col++, date);
            pst.executeUpdate();
             rs = pst.getGeneratedKeys();
                    if(rs != null && rs.next()){
                        id = rs.getLong(1);
                        ///////Update Invoice////////////
                        String invoiceno = "EFH"+id;
                        String updateinvoice = "UPDATE TINVOICE SET BillNo = ? WHERE ID = ?";
                        pst1 = conn.prepareStatement(updateinvoice);
                        pst1.setString(1, invoiceno);
                        pst1.setLong(2, id);
                        pst1.executeUpdate();
                        pst1.close();
                    }
        } catch (SQLException ex) {
            Logger.getLogger(Followup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void InvoiceDetails(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(dateField.getDate());
        String insertdetails = "INSERT INTO TINVOICEDETAILS (Billno,mrn, NAME, description, amount, module,date) VALUES (?,?,?,?,?,?,?)";
        String mrn  = mrnField.getText().toUpperCase();
        String name = fnameField.getText()+" "+surnameField.getText();
        String billnoQuery = "SELECT * FROM TINVOICE";
                          
        try {
            pst1 = conn.prepareStatement(billnoQuery);
            rs = pst1.executeQuery();
            rs.last();
            String billno = rs.getString("billno");//Get Bill no
            ///Insert followup details into invoice
            pst = conn.prepareStatement(insertdetails);
            int col = 1;
            pst.setString(col++, billno);
            pst.setString(col++, mrn);
            pst.setString(col++, name);
            pst.setString(col++, "Follow Up");
            pst.setDouble(col++, Double.parseDouble(costField.getText()));
            pst.setString(col++, "FOLLOWUP");
            pst.setString(col++, date);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Followup.class.getName()).log(Level.SEVERE, null, ex);
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

        fuPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        pcatField = new javax.swing.JTextField();
        surnameField = new javax.swing.JTextField();
        fnameField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        mrnField = new javax.swing.JTextField();
        dateField = new com.toedter.calendar.JTextFieldDateEditor();
        costField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        consultantBox = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        submitButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        reprintButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        fuPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("FollowUp"));

        jLabel1.setText(" Category: ");

        jLabel2.setText("Surname: ");

        jLabel3.setText("First Name: ");

        pcatField.setEditable(false);

        surnameField.setEditable(false);

        fnameField.setEditable(false);

        jLabel6.setText("MR No: ");

        mrnField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mrnFieldActionPerformed(evt);
            }
        });

        dateField.setEditable(false);

        costField.setEditable(false);

        jLabel4.setText("Amount: ");

        jLabel5.setText("Consultant: ");

        submitButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        submitButton.setText("SUBMIT");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        resetButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        resetButton.setText("RESET");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        reprintButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        reprintButton.setText("REPRINT");

        javax.swing.GroupLayout fuPanelLayout = new javax.swing.GroupLayout(fuPanel);
        fuPanel.setLayout(fuPanelLayout);
        fuPanelLayout.setHorizontalGroup(
            fuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fuPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fuPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(dateField, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(fuPanelLayout.createSequentialGroup()
                        .addGroup(fuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5))
                        .addGap(14, 14, 14)
                        .addGroup(fuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(consultantBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(mrnField)
                            .addComponent(surnameField, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                        .addGroup(fuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fuPanelLayout.createSequentialGroup()
                                .addGroup(fuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(fuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(fnameField, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(pcatField, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fuPanelLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(costField, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fuPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reprintButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resetButton)
                .addGap(85, 85, 85))
        );

        fuPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {resetButton, submitButton});

        fuPanelLayout.setVerticalGroup(
            fuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fuPanelLayout.createSequentialGroup()
                .addComponent(dateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(fuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pcatField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mrnField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(fuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(surnameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fnameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(fuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(consultantBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(costField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addGroup(fuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(resetButton)
                    .addComponent(reprintButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46))
        );

        fuPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {reprintButton, resetButton, submitButton});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fuPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fuPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mrnFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mrnFieldActionPerformed
        try{
            String patientQuery = "SELECT * FROM PATIENTREGISTER WHERE MRN = ?";
            pst = conn.prepareStatement(patientQuery);
            pst.setString(1, mrnField.getText().toUpperCase());
            rs = pst.executeQuery();
            if(rs.next()){
                pcatField.setText(rs.getString("category"));
                surnameField.setText(rs.getString("surname"));
                fnameField.setText(rs.getString("firstname"));
                getCost();
            }
            else{
                JOptionPane.showMessageDialog(null, "Check MRN. Patient not found.");
            }
            
        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex);
        }
    }//GEN-LAST:event_mrnFieldActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        mrnField.setText(null);
        surnameField.setText(null);
        fnameField.setText(null);
        pcatField.setText(null);
        costField.setText(null);
        consultantBox.setSelectedIndex(0);
    }//GEN-LAST:event_resetButtonActionPerformed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        Invoice();
        InvoiceDetails();
        this.dispose();
    }//GEN-LAST:event_submitButtonActionPerformed

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
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Followup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Followup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Followup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Followup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Followup().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(Followup.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox consultantBox;
    private javax.swing.JTextField costField;
    private com.toedter.calendar.JTextFieldDateEditor dateField;
    private javax.swing.JTextField fnameField;
    private javax.swing.JPanel fuPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField mrnField;
    private javax.swing.JTextField pcatField;
    private javax.swing.JButton reprintButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton submitButton;
    private javax.swing.JTextField surnameField;
    // End of variables declaration//GEN-END:variables
}
