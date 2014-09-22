package HEMS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Fatimah
 */
class PaymentTableModel extends DefaultTableModel {

    public PaymentTableModel() {

    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

}

public class Payment extends javax.swing.JFrame {

    private Connection conn = null;
    private ResultSet rs, rs2 = null;
    private PreparedStatement pst, pst2 = null;
    private final PaymentTableModel paymentTableModel;

    public Payment() throws SQLException {
        initComponents();
        setLocationRelativeTo(null);
        conn = DBUtil.getConnection();
        billLabel.setVisible(false);
        billnoField.setVisible(false);
        paymentMode();
        paymentTableModel = new PaymentTableModel();
        paymentTableModel.addColumn("Description");
        paymentTableModel.addColumn("Amount (=N=)");
        paymentTable.setModel(paymentTableModel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    //Reset all fields
    public void reset(){
        mrnField.setText(null);
        nameField.setText(null);
        billnoField.setText(null);
        billnoLabel.setText(null);
        paymentTableModel.setRowCount(0);
        chargesField.setText(null);
        balanceField.setText(null);
        depositField.setText(null);
        receivedField.setText(null);
        DefaultListModel dlm  = (DefaultListModel)billList.getModel();
        dlm.removeAllElements();
    }
    //Get Consultant
    private void Consultant(){
        String billno = billList.getSelectedValue().toString();
        String consultantQuery = "SELECT * FROM TINVOICE WHERE BILLNO = ?";
        try {
            pst = conn.prepareStatement(consultantQuery);
            pst.setString(1, billno);
            rs = pst.executeQuery();
            if(rs.next()){
                consultantLabel.setText(rs.getString("consultant"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Payment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //Deposit remaining change to reserve
    private void addtoReserve() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String balString = balanceField.getText();
        double balance = Double.parseDouble(balString);
        int option = JOptionPane.showConfirmDialog(null, "Do you want to reserve the balance on account?", "Reserve Alert", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                String addFund = "INSERT INTO RESERVEFUND (mrn, date, amount, paymenttype) VALUES (?,?,?,?)";
                long id;
                pst = conn.prepareStatement(addFund, PreparedStatement.RETURN_GENERATED_KEYS);
                int col = 1;
                pst.setString(col++, mrnField.getText());
                pst.setString(col++, dateFormat.format(date));
                pst.setDouble(col++, balance);
                pst.setString(col++, paymentBox.getSelectedItem().toString());
                pst.executeUpdate();
                rs = pst.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    id = rs.getLong(1);
                    String receipt = "DEP" + id;
                    String receiptno = "UPDATE reservefund set receiptno = ? WHERE id = ?";
                    pst = conn.prepareStatement(receiptno);
                    pst.setString(1, receipt);
                    pst.setLong(2, id);
                    pst.executeUpdate();
                }
                JOptionPane.showMessageDialog(null, "Money reserved on account", "Reserve Status", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                Logger.getLogger(Payment.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            dispose();
        }
    }

    private void populateList() {
        DefaultListModel dlm = new DefaultListModel();
        String mrn = mrnField.getText();
        String getbills = "SELECT * FROM TINVOICE WHERE MRN =?";
        try {
            pst = conn.prepareStatement(getbills);
            pst.setString(1, mrn);
            rs = pst.executeQuery();
            while(rs.next()){
                String billno = rs.getString("billno");
                String paymentQuery = "SELECT * FROM PAYMENTDETAILS WHERE INVOICENO =?";
                pst2 = conn.prepareStatement(paymentQuery);
                pst2.setString(1, billno);
                rs2 = pst2.executeQuery();
                if(rs2.next()){
                    rs2.last();
                    switch(rs2.getString("paid")){
                        case "P":
                            dlm.addElement(rs2.getString("invoiceno"));
                            break;   
                    }
                }
                else{
                    dlm.addElement(rs.getString("billno"));
                }
            }
            billList.setModel(dlm);
            if(billList.getModel().getSize() == 0){
                JOptionPane.showMessageDialog(null, "Patient has no pending bills");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Payment.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //populate combobox with payment modes
    private void paymentMode() {
        String paymenttypeQuery = "SELECT * FROM PAYMENTTYPE";
        try {
            pst = conn.prepareStatement(paymenttypeQuery);
            rs = pst.executeQuery();
            while (rs.next()) {
                paymentBox.addItem(rs.getString("type"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReserveCash.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Retrive patient due details from db
    private void dueDetails() {
        String dueQuery = "SELECT * FROM PAYMENTDETAILS WHERE INVOICENO = ?";
        try {
            pst = conn.prepareStatement(dueQuery);
            pst.setString(1, billList.getSelectedValue().toString());
            rs = pst.executeQuery();
            if (rs.next()) {
                rs.last();
                String description = "PATIENT DUE";
                Double due = rs.getDouble("due");
                paymentTableModel.addRow(new Object[]{description, due});
                chargesField.setText(Double.toString(due));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Payment.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //Retrieve invoice details from db
    private void getInvoiceDetails() {
        try {
            String invoiceQuery = "SELECT * FROM TINVOICEDETAILS WHERE billNo = ?";
            pst = conn.prepareStatement(invoiceQuery);
            pst.setString(1, billnoLabel.getText());
            rs = pst.executeQuery();
            while (rs.next()) {
                String description = rs.getString("description");
                double amount = rs.getDouble("amount");
                paymentTableModel.addRow(new Object[]{description, amount});
            }
            getActualCharge();

        } catch (SQLException ex) {
            Logger.getLogger(Payment.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //Retrieve original amount billed to patient at the billing section
    private void getActualCharge() {
        String chargeQuery = "SELECT * FROM TINVOICE WHERE billNo = ?";
        try {
            pst = conn.prepareStatement(chargeQuery);
            pst.setString(1, billnoLabel.getText());
            rs = pst.executeQuery();
            if (rs.next()) {
                chargesField.setText(rs.getString("amount"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Payment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Retrieve deposit details if patient has money on account
    private void getDeposit() {
        String depositQuery = "SELECT * FROM RESERVEFUND WHERE MRN = ?";
        try {
            pst = conn.prepareStatement(depositQuery);
            pst.setString(1, mrnField.getText().toUpperCase());
            rs = pst.executeQuery();
            if (rs.next()) {
                depositField.setText(rs.getString("amount"));
            } else {
                depositField.setText("0.00");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Payment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Retrieve billno from database using the MRNO
    private void billNo() {
        if (mrnField.getText().equalsIgnoreCase("001h")) {
        } else {
            String billno = billList.getSelectedValue().toString();
            billnoLabel.setText(billno);
        }

    }

    //Get Patient details from db:PatientRegister
    private void getPatient() {
        String mrn = "SELECT * FROM patientregister WHERE MRN = ?";
        try {

            pst = conn.prepareStatement(mrn);
            pst.setString(1, mrnField.getText());
            rs = pst.executeQuery();
            if (rs.next()) {
                String firstname = rs.getString("firstname");
                String surname = rs.getString("surname");
                String middlename = rs.getString("middlename");
                String patient = firstname + " " + middlename + " " + surname;
                nameField.setText(patient);
            } else if (mrnField.getText().equalsIgnoreCase("001h")) {

            } else {
                JOptionPane.showMessageDialog(null, "No patient in our database has this MR No");
                mrnField.setText(null);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    //Calculate balance based on actual amount charged, money on account and money collected from patient
    private void calcBalance() {
        String chargeString = chargesField.getText();
        String reserveString = depositField.getText();
        String receivedString = receivedField.getText();
        Double charge = Double.parseDouble(chargeString);
        Double reserve = Double.parseDouble(reserveString);
        Double received = Double.parseDouble(receivedString);
        if (charge > 0) {
            Double bal = (reserve + received) - charge;
            String balanceString = Double.toString(bal);
            balanceField.setText(balanceString);
        } else if (charge < 0) {
            Double bal = (reserve + received) + charge;
            String balanceString = Double.toString(bal);
            balanceField.setText(balanceString);
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

        PaymentScrollPane = new javax.swing.JScrollPane();
        payPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        mrnField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        paymentTable = new javax.swing.JTable();
        submitButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        chargesField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        depositField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        receivedField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        balanceField = new javax.swing.JTextField();
        reprinButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        billnoLabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        consultantLabel = new javax.swing.JLabel();
        paymentBox = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        remarkArea = new javax.swing.JTextArea();
        billnoField = new javax.swing.JTextField();
        billLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        billList = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        payPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Accept Payment"));

        jLabel1.setText("MRN: ");

        mrnField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mrnFieldActionPerformed(evt);
            }
        });

        jLabel2.setText("Patient Name: ");

        nameField.setEditable(false);

        paymentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Description", "Amount"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(paymentTable);

        submitButton.setText("SUBMIT");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        resetButton.setText("RESET");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        chargesField.setEditable(false);

        jLabel3.setText("Actual Charges:");

        jLabel4.setText("Money on Account:");

        depositField.setEditable(false);

        jLabel5.setText("Amount received:");

        receivedField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                receivedFieldActionPerformed(evt);
            }
        });

        jLabel6.setText("Balance: ");

        reprinButton.setText("REPRINT");
        reprinButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reprinButtonActionPerformed(evt);
            }
        });

        jLabel7.setText("Consultant: ");

        jLabel8.setText("Mode of Payment: ");

        consultantLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jLabel9.setText("Remark: ");

        remarkArea.setColumns(20);
        remarkArea.setRows(5);
        jScrollPane1.setViewportView(remarkArea);

        billnoField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                billnoFieldActionPerformed(evt);
            }
        });

        billLabel.setLabelFor(billnoField);
        billLabel.setText("BILL:");

        billList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                billListMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(billList);

        javax.swing.GroupLayout payPanelLayout = new javax.swing.GroupLayout(payPanel);
        payPanel.setLayout(payPanelLayout);
        payPanelLayout.setHorizontalGroup(
            payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, payPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(resetButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reprinButton)
                .addGap(148, 148, 148))
            .addGroup(payPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(payPanelLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(consultantLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chargesField, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, payPanelLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(receivedField, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(payPanelLayout.createSequentialGroup()
                        .addGroup(payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane1)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, payPanelLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(paymentBox, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(191, 300, Short.MAX_VALUE)
                        .addGroup(payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, payPanelLayout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(depositField, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(billnoLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, payPanelLayout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(balanceField, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(payPanelLayout.createSequentialGroup()
                        .addGroup(payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(payPanelLayout.createSequentialGroup()
                                .addGroup(payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(billLabel))
                                .addGap(18, 18, 18)
                                .addGroup(payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(billnoField)
                                    .addComponent(mrnField, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE))
                                .addGap(27, 27, 27)
                                .addComponent(jLabel2))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(nameField))))
                .addContainerGap())
        );

        payPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {reprinButton, resetButton, submitButton});

        payPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {consultantLabel, jLabel7});

        payPanelLayout.setVerticalGroup(
            payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(payPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(billnoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(mrnField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(billnoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(billLabel))
                .addGap(18, 18, 18)
                .addGroup(payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE))
                .addGap(26, 26, 26)
                .addGroup(payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(consultantLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chargesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(payPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(depositField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(receivedField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(balanceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)))
                    .addGroup(payPanelLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(paymentBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 81, Short.MAX_VALUE)
                .addGroup(payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resetButton)
                    .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reprinButton))
                .addGap(38, 38, 38))
        );

        payPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {reprinButton, resetButton, submitButton});

        payPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {consultantLabel, jLabel7});

        PaymentScrollPane.setViewportView(payPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PaymentScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 803, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PaymentScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void reprinButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reprinButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_reprinButtonActionPerformed

    private void receivedFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_receivedFieldActionPerformed
        calcBalance();
    }//GEN-LAST:event_receivedFieldActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        // TODO add your handling code here:
        reset();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        double balance = Double.parseDouble(balanceField.getText());
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String insertPayment = "INSERT INTO PAYMENTDETAILS (PaymentType, MRN, PAYMENTDATE, AMOUNT, INVOICENO, RECEIVED, DUE, PAID, consultant) VALUES (?,?,?,?,?,?,?,?,?)";
        long id;
        String receivedString = receivedField.getText();
        String depositString = depositField.getText();
        Double charge = Double.parseDouble(chargesField.getText());
        Double received = Double.parseDouble(receivedString);
        Double deposit = Double.parseDouble(depositString);
        Double paid = received + deposit;

        try {
            pst = conn.prepareStatement(insertPayment, PreparedStatement.RETURN_GENERATED_KEYS);
            int col = 1;
            pst.setString(col++, paymentBox.getSelectedItem().toString());
            pst.setString(col++, mrnField.getText().toUpperCase());
            pst.setString(col++, dateFormat.format(date));
            pst.setDouble(col++, charge);
            pst.setString(col++, billnoLabel.getText());
            pst.setDouble(col++, paid);
            pst.setDouble(col++, balance);
            if (balance == 0) {
                pst.setString(col++, "Y");
                pst.setString(col++, consultantLabel.getText());
                pst.execute();
                rs = pst.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    id = rs.getLong(1);
                    String receipt = "EFRCPT" + id;
                    String receiptno = "UPDATE paymentdetails set receiptno = ? WHERE id = ?";
                    pst = conn.prepareStatement(receiptno);
                    pst.setString(1, receipt);
                    pst.setLong(2, id);
                    pst.executeUpdate();
                }
            } else if (balance > 0) {
                pst.setString(col++, "Y");
                pst.setString(col++, consultantLabel.getText());
                pst.execute();
                rs = pst.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    id = rs.getLong(1);
                    String receipt = "EFRCPT" + id;
                    String receiptno = "UPDATE paymentdetails set receiptno = ? WHERE id = ?";
                    pst = conn.prepareStatement(receiptno);
                    pst.setString(1, receipt);
                    pst.setLong(2, id);
                    pst.executeUpdate();
                }
                addtoReserve();
            } else {
                pst.setString(col++, "P");
                pst.setString(col++, consultantLabel.getText());
                pst.execute();
                rs = pst.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    id = rs.getLong(1);
                    String receipt = "EFRCPT" + id;
                    String receiptno = "UPDATE paymentdetails set receiptno = ? WHERE id = ?";
                    pst = conn.prepareStatement(receiptno);
                    pst.setString(1, receipt);
                    pst.setLong(2, id);
                    pst.executeUpdate();
                }
            }
            reset();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }//GEN-LAST:event_submitButtonActionPerformed

    private void mrnFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mrnFieldActionPerformed
        //Delete rows in table when a new MRN is entered
        if (paymentTable.getRowCount() != 0) {
            paymentTableModel.setRowCount(0);
        } else {
            getPatient(); //Get patient name from database
            populateList();
        }
    }//GEN-LAST:event_mrnFieldActionPerformed
        //populate other fields on bill clicked
    private void billClick(){
        String billno = billList.getSelectedValue().toString();
        String billQuery = "SELECT * FROM PAYMENTDETAILS WHERE INVOICENO = ?";
        try {
            pst = conn.prepareStatement(billQuery);
            pst.setString(1, billno);
            rs = pst.executeQuery();
            if(rs.next()){
                rs.last();
                dueDetails();
            }
            else{
                getInvoiceDetails();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Payment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void billListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_billListMouseClicked
        // Get patient invoice details based on invoice no clicked
        paymentTableModel.setRowCount(0);
        consultantLabel.setText(null);
        chargesField.setText(null);
        depositField.setText(null);
        receivedField.setText(null);
        balanceField.setText(null);
        billNo();
        billClick();
        Consultant(); //Get Consultant from db
        getDeposit();//Get money patient has on account
    }//GEN-LAST:event_billListMouseClicked

    private void billnoFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_billnoFieldActionPerformed

        String billno = billnoField.getText().toUpperCase();
        billnoLabel.setText(billno);
        String invoiceQuery = "SELECT * FROM TINVOICEDETAILS WHERE billno = ?";
        try {
            pst = conn.prepareStatement(invoiceQuery);
            pst.setString(1, billno);
            rs = pst.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                double amount = rs.getDouble("amount");
                nameField.setText(name);
                paymentTableModel.addRow(new Object[]{description, amount});
            }
            getActualCharge();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

    }//GEN-LAST:event_billnoFieldActionPerformed


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
            java.util.logging.Logger.getLogger(Payment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Payment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Payment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Payment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Payment().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(Payment.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane PaymentScrollPane;
    private javax.swing.JTextField balanceField;
    private javax.swing.JLabel billLabel;
    private javax.swing.JList billList;
    private javax.swing.JTextField billnoField;
    private javax.swing.JLabel billnoLabel;
    private javax.swing.JTextField chargesField;
    private javax.swing.JLabel consultantLabel;
    private javax.swing.JTextField depositField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField mrnField;
    private javax.swing.JTextField nameField;
    private javax.swing.JPanel payPanel;
    private javax.swing.JComboBox paymentBox;
    private javax.swing.JTable paymentTable;
    private javax.swing.JTextField receivedField;
    private javax.swing.JTextArea remarkArea;
    private javax.swing.JButton reprinButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton submitButton;
    // End of variables declaration//GEN-END:variables
}
