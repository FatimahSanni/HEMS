

package hems.Billing;

import HEMS.DBUtil;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
//import net.proteanit.sql.DbUtils;

class BillingTableModel extends DefaultTableModel{
    public BillingTableModel(){
        
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        
        if(getColumnCount()>2){
              switch(column){
                  case 2: 
                      return true;
              }  
              return false;
        }
        return false;
    }
    
}

public class Billing extends javax.swing.JFrame{
    private Connection conn = null;
    private ResultSet rs, rs2 = null;
    private PreparedStatement pst, pst1, pst2, pst3= null;
    private BillingTableModel billingTableModel;
    private final JPopupMenu popupMenu;
    private final JMenuItem deleteItem;
    
    public Billing() throws SQLException{
        initComponents();
        setLocationRelativeTo(null);
        conn = DBUtil.getConnection();
        discountDialog.setLocationRelativeTo(null);
        discountDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        billingTableModel = new BillingTableModel();
        billingTableModel.addColumn("Description");
        billingTableModel.addColumn("Amount (=N=)");
        billingTable.setModel(billingTableModel);
        deleteItem = new JMenuItem("Delete Item");
        popupMenu = new JPopupMenu();
        popupMenu.add(deleteItem);
        
        corporate();
        docBox();
        patientType();
        populateList();
        deleteItem.addActionListener((ActionEvent e) -> {
          int row = billingTable.getSelectedRow();
          billingTableModel.removeRow(row);
          if(totalField.getText().length() != 0){
              calcTotal();
        }
        });
        pNameField.setEditable(false);
        
        billingTableModel.addTableModelListener((TableModelEvent e) -> {
            calcTotal();
        });
        showDate();
        
    }
    
    /////////Generate a list of invoice to be checked against Payment//////////
    private void invoiceList(){
        String invoiceQuery = "SELECT * FROM TINVOICE WHERE MRN = ?";
        String paymentQuery = "SELECT * FROM PAYMENTDETAILS WHERE INVOICENO = ?";
        try {
            pst = conn.prepareStatement(invoiceQuery);
            pst2 = conn.prepareStatement(paymentQuery);
            pst.setString(1, mrnField.getText());
            rs = pst.executeQuery();
            while(rs.next()){
                String billno = rs.getString("billno");
                pst2.setString(1, billno);
                rs2 = pst2.executeQuery();
                if(rs2.next()){
                    rs2.last();
                    switch(rs2.getString("paid")){
                        case "P":
                            JOptionPane.showMessageDialog(null, "Patient has a pending bill");
                            outPatient();
                            break;
                    }
                }
//                else{
//                    JOptionPane.showMessageDialog(null, "Patient has an outstanding bill");
//                    outPatient();
//                }    
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /////Test if outpatient  001h is not used and make field uneditable
    private void textEditStatus(){
        if(mrnField.getText().equalsIgnoreCase("001H")){
            pNameField.setEditable(true);
        }
        else{
            pNameField.setEditable(false);
        }
    }
    //////Raise invoice as outpatient 001H///////
    private void outPatient(){
        int option = JOptionPane.showConfirmDialog(null, "Do you want to raise invoice using 001H?", "Invoice Status", JOptionPane.YES_NO_OPTION);
        if(option == JOptionPane.YES_OPTION){
            mrnField.setText("001H");
        }
        else if(option == JOptionPane.NO_OPTION){
            dispose();
        }
    }
    ////Output date in the datefield///////
    private void showDate(){
        Calendar cal = new GregorianCalendar();
        dateField.setDateFormatString("yyyy-MM-dd");
        dateField.setDate(cal.getTime());
    }
    
    private void getPatient(){
        String mrn = mrnField.getText();      
        try {
            String patientQuery = "SELECT  FIRSTNAME, MIDDLENAME, SURNAME FROM patientregister WHERE MRN = ?";
            pst = conn.prepareStatement(patientQuery);
            pst.setString(1, mrn);
            rs = pst.executeQuery();   
            if(rs.next()){
                String FirstName = rs.getString("firstname");
                String Middlename = rs.getString("MiddleName");
                String Surname = rs.getString("surname");
                pNameField.setText(FirstName+" "+Middlename+" "+Surname);
            }
            else if(mrnField.getText().equalsIgnoreCase("001H")){
                pNameField.setEditable(true);
            }
            else{
                JOptionPane.showMessageDialog(null, "MRN not in hospital database");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void reset(){
        mrnField.setText(null);
        pNameField.setText(null);
        totalField.setText(null);
        billingTableModel.setRowCount(0);
    }
    private void populateList(){
        
        try {
             DefaultListModel dlm = new DefaultListModel();
            String tablequery = "select * from testrepository";
            pst = conn.prepareStatement(tablequery);
            rs = pst.executeQuery();
            while(rs.next()){
                String test = rs.getString("testname");
                dlm.addElement(test);   
            }
            testList.setModel(dlm);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
    }
    
    private void docBox(){
        String tablequery = "SELECT * from consultants";
        try {
            pst = conn.prepareStatement(tablequery);
            rs = pst.executeQuery();
            while (rs.next()){
                String docname =rs.getString("consultant");
                drBox.addItem(docname);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
    }
    private void patientType(){
        String tablequery = "SELECT * from billinggrade";
        try {
            pst = conn.prepareStatement(tablequery);
           rs = pst.executeQuery();
            while (rs.next()){
                String ptype = rs.getString("grade");
                pTypeBox.addItem(ptype);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
    }
    
    private void corporate(){
        String companyQuery = "SELECT * FROM CORPORATE";
        try {
            pst = conn.prepareStatement(companyQuery);
            rs = pst.executeQuery();
            while(rs.next()){
                corporateComboBox.addItem(rs.getString("company"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
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

        discountDialog = new javax.swing.JDialog();
        discountPanel = new javax.swing.JPanel();
        flatField = new javax.swing.JTextField();
        percentSpinner = new javax.swing.JSpinner();
        okButton = new javax.swing.JButton();
        flatOption = new javax.swing.JCheckBox();
        percentOption = new javax.swing.JCheckBox();
        cancelButton = new javax.swing.JButton();
        discountGroup = new javax.swing.ButtonGroup();
        billingPanel = new javax.swing.JPanel();
        patientLabel = new javax.swing.JLabel();
        mrnLabel = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        operatorLabel = new javax.swing.JLabel();
        drLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        billingTable = new javax.swing.JTable();
        pTypeBox = new javax.swing.JComboBox();
        mrnField = new javax.swing.JTextField();
        pNameField = new javax.swing.JTextField();
        operatorField = new javax.swing.JTextField();
        drBox = new javax.swing.JComboBox();
        submitButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        printButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        testList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        corporateCheckBox = new javax.swing.JCheckBox();
        corporateComboBox = new javax.swing.JComboBox();
        discountButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        totalField = new javax.swing.JLabel();
        dateField = new com.toedter.calendar.JTextFieldDateEditor();

        discountDialog.setTitle("Apply Discount");
        discountDialog.setMinimumSize(new java.awt.Dimension(422, 306));
        discountDialog.setModal(true);

        discountPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Discount Options"));

        flatField.setEnabled(false);

        percentSpinner.setEnabled(false);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        discountGroup.add(flatOption);
        flatOption.setText("Fixed flat discount for the whole invoice");
        flatOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                flatOptionActionPerformed(evt);
            }
        });

        discountGroup.add(percentOption);
        percentOption.setText("Fixed percentage discount for the whole invoice");
        percentOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                percentOptionActionPerformed(evt);
            }
        });

        cancelButton.setText("CANCEL");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout discountPanelLayout = new javax.swing.GroupLayout(discountPanel);
        discountPanel.setLayout(discountPanelLayout);
        discountPanelLayout.setHorizontalGroup(
            discountPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(discountPanelLayout.createSequentialGroup()
                .addGroup(discountPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(discountPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, discountPanelLayout.createSequentialGroup()
                            .addGap(310, 310, 310)
                            .addComponent(percentSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(flatField, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(flatOption)
                    .addComponent(percentOption)
                    .addGroup(discountPanelLayout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        discountPanelLayout.setVerticalGroup(
            discountPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(discountPanelLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(flatOption)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(flatField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(percentOption)
                .addGap(6, 6, 6)
                .addComponent(percentSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(discountPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout discountDialogLayout = new javax.swing.GroupLayout(discountDialog.getContentPane());
        discountDialog.getContentPane().setLayout(discountDialogLayout);
        discountDialogLayout.setHorizontalGroup(
            discountDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(discountDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(discountPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        discountDialogLayout.setVerticalGroup(
            discountDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, discountDialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(discountPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        billingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Create invoice", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14), new java.awt.Color(204, 0, 0))); // NOI18N

        patientLabel.setText("Patient Type: ");

        mrnLabel.setText("MR No: ");

        nameLabel.setText(" Name: ");

        operatorLabel.setText("Operator: ");

        drLabel.setText("Doctor: ");

        billingTable.setModel(new javax.swing.table.DefaultTableModel(
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
        billingTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                billingTableMousePressed(evt);
            }
        });
        jScrollPane2.setViewportView(billingTable);

        mrnField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mrnFieldActionPerformed(evt);
            }
        });

        operatorField.setEditable(false);
        operatorField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        submitButton.setText("Submit");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        printButton.setText("Print");

        testList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                testListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(testList);

        jLabel1.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel1.setText("PROCEDURES/SURGERIES");

        corporateCheckBox.setText("Corporate");
        corporateCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                corporateCheckBoxActionPerformed(evt);
            }
        });

        corporateComboBox.setEnabled(false);

        discountButton.setText("Add Discount");
        discountButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discountButtonActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Total ");

        totalField.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        totalField.setText("0.00");

        javax.swing.GroupLayout billingPanelLayout = new javax.swing.GroupLayout(billingPanel);
        billingPanel.setLayout(billingPanelLayout);
        billingPanelLayout.setHorizontalGroup(
            billingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(billingPanelLayout.createSequentialGroup()
                .addGroup(billingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(billingPanelLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(billingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(billingPanelLayout.createSequentialGroup()
                                .addComponent(drLabel)
                                .addGap(18, 18, 18)
                                .addComponent(drBox, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(discountButton, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(148, 148, 148)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(totalField, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(billingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, billingPanelLayout.createSequentialGroup()
                                    .addGroup(billingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, billingPanelLayout.createSequentialGroup()
                                            .addComponent(mrnLabel)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(mrnField)))
                                    .addGap(39, 39, 39)
                                    .addGroup(billingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, billingPanelLayout.createSequentialGroup()
                                            .addComponent(nameLabel)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(pNameField))
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, billingPanelLayout.createSequentialGroup()
                                    .addComponent(corporateCheckBox)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(corporateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(patientLabel)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(billingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(billingPanelLayout.createSequentialGroup()
                                            .addGap(0, 0, Short.MAX_VALUE)
                                            .addComponent(dateField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(billingPanelLayout.createSequentialGroup()
                                            .addComponent(pTypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                                            .addComponent(operatorLabel)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(operatorField, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                    .addGroup(billingPanelLayout.createSequentialGroup()
                        .addGap(184, 184, 184)
                        .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(resetButton)
                        .addGap(18, 18, 18)
                        .addComponent(printButton)))
                .addContainerGap(68, Short.MAX_VALUE))
        );

        billingPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {printButton, resetButton, submitButton});

        billingPanelLayout.setVerticalGroup(
            billingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(billingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(billingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(billingPanelLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(billingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(corporateCheckBox)
                            .addComponent(corporateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(patientLabel)
                            .addComponent(pTypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(billingPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(billingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(operatorLabel)
                            .addComponent(operatorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(28, 28, 28)
                .addGroup(billingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mrnLabel)
                    .addComponent(nameLabel)
                    .addComponent(mrnField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(billingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(32, 32, 32)
                .addGroup(billingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(totalField, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(drLabel)
                    .addComponent(drBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(discountButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                .addGroup(billingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(resetButton)
                    .addComponent(printButton))
                .addGap(23, 23, 23))
        );

        billingPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {printButton, resetButton, submitButton});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(billingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(billingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void testListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_testListMouseClicked
        // TODO add your handling code here:
        
        String testName = testList.getSelectedValue().toString();
        String testCostQuery = "SELECT TESTCOST FROM TESTREPOSITORY WHERE TESTNAME = ? ";
        try {
            pst = conn.prepareStatement(testCostQuery);
            pst.setString(1, testName);
            rs = pst.executeQuery();
            if(rs.next()){
                Object selectedItem = testList.getSelectedValue();
                if(Integer.parseInt(rs.getString("testCost")) == 0){
                    String newTestCost  = JOptionPane.showInputDialog(null, "Enter Test Cost", "Test Cost", JOptionPane.QUESTION_MESSAGE);
                    billingTableModel.addRow(new Object[] {selectedItem, newTestCost});
                }
                else{
                    billingTableModel.addRow(new Object[] {selectedItem, rs.getString("testCost")});
                }
                
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_testListMouseClicked

    private void billingTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_billingTableMousePressed
        // TODO add your handling code here:
                int row = billingTable.rowAtPoint(evt.getPoint());
                billingTable.getSelectionModel().addSelectionInterval(row, row);
                if(evt.getButton() == MouseEvent.BUTTON3){           
                popupMenu.show(billingTable, evt.getX(), evt.getY());
                }
    }//GEN-LAST:event_billingTableMousePressed
    private void calcTotal(){
        DecimalFormat df = new DecimalFormat("0.00");
        int rowCount = billingTable.getRowCount(); 
        double total = 0.00;
        if(billingTable.getColumnCount() == 2){
            for(int i = 0; i < rowCount; i++){
                double amount = Double.parseDouble(billingTable.getValueAt(i, 1).toString());
                total = total + amount;
            
        }
        String totalResult = df.format(total);
        totalField.setText("=N= "+totalResult);
        }
        
    }
    
    private void mrnFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mrnFieldActionPerformed
        if(mrnField.getText().equalsIgnoreCase("001h")){
            pNameField.setEditable(true);
        }else{
            pNameField.setEditable(false);
            textEditStatus();
            getPatient();
            invoiceList();
        } 
    }//GEN-LAST:event_mrnFieldActionPerformed
    private void updateInvoice(){
        try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(dateField.getDate());
                String totalString = totalField.getText();
                Double total = Double.parseDouble(totalString.substring(3));
                String invoiceUpdate = "INSERT INTO TINVOICE(MRN,Name, Description, AMOUNT, date, consultant) VALUES (?,?,?,?,?,?)";
                long id;
                pst = conn.prepareStatement(invoiceUpdate, PreparedStatement.RETURN_GENERATED_KEYS);
                pst.setString(1, mrnField.getText().toUpperCase());
                pst.setString(2, pNameField.getText());
                pst.setString(3, "ITEM PURCHASED");
                pst.setDouble(4, total);
                pst.setString(5, date);
                pst.setString(6, drBox.getSelectedItem().toString());
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
                        
                    }
        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex, "Invoice Updation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void updateInvoiceDetails(){
        try {
                String billnoQuery = "SELECT * FROM TINVOICE WHERE MRN = ?";
                pst3 = conn.prepareStatement(billnoQuery);
                pst3.setString(1, mrnField.getText());
                rs = pst3.executeQuery();
                rs.last();
                String billno = rs.getString("billno");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(dateField.getDate());
                int rowCount = billingTable.getRowCount();
                for(int i = 0; i < rowCount; i++){
                    String descriptions = (String)billingTableModel.getValueAt(i, 0);
                    double cost = (Double.parseDouble((String)billingTableModel.getValueAt(i, 1)));
                    String invoiceDetails = "INSERT INTO TINVOICEDETAILS(BillNo, MRN, Name, AMOUNT, DESCRIPTION, DATE) VALUES (?,?,?,?,?,?)";
                    pst = conn.prepareStatement(invoiceDetails);
                    int col = 1;
                    pst.setString(col++, billno);
                    pst.setString(col++, mrnField.getText().toUpperCase());
                    pst.setString(col++, pNameField.getText());
                    pst.setDouble(col++, cost);
                    pst.setString(col++, descriptions);
                    pst.setString(col++, date);
                    pst.executeUpdate(); 
                    pst.close();
            }
                
            JOptionPane.showMessageDialog(null, "Invoice updated to respective databases", "Invoice status", JOptionPane.INFORMATION_MESSAGE);
            reset();
        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex, "Invoice Details Updation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        updateInvoice();
        updateInvoiceDetails();
        reset();
    }//GEN-LAST:event_submitButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        reset();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void corporateCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_corporateCheckBoxActionPerformed
        boolean isChecked = corporateCheckBox.isSelected();
        corporateComboBox.setEnabled(isChecked);
    }//GEN-LAST:event_corporateCheckBoxActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
       
        if(flatOption.isSelected()){
           String discountString = flatField.getText();
           double discount = Double.parseDouble(discountString);
           billingTableModel.addRow(new Object[] {"Discount", "-"+discount, "-"+discount});
       }
        else if(percentOption.isSelected()){
            String amtString = totalField.getText();
            String amountString = amtString.substring(3);
            double amount = Double.parseDouble(amountString);
            String discountString = percentSpinner.getValue().toString();
            double value = Double.parseDouble(discountString);
            double percent = (value)/100;
            double discount = percent*amount;
            billingTableModel.addRow(new Object[] {"Discount", "-"+discount, "-"+discount});
        }
        discountDialog.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void discountButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discountButtonActionPerformed
        discountDialog.setVisible(true);
    }//GEN-LAST:event_discountButtonActionPerformed

    private void flatOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flatOptionActionPerformed
        boolean isChecked = flatOption.isSelected();
       flatField.setEnabled(isChecked);
       percentSpinner.setEnabled(false);
    }//GEN-LAST:event_flatOptionActionPerformed

    private void percentOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_percentOptionActionPerformed
        boolean isChecked = percentOption.isSelected();
        flatField.setEnabled(false);
       percentSpinner.setEnabled(isChecked);
       
    }//GEN-LAST:event_percentOptionActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        discountDialog.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

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
            java.util.logging.Logger.getLogger(Billing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Billing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Billing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Billing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Billing().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel billingPanel;
    private javax.swing.JTable billingTable;
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox corporateCheckBox;
    private javax.swing.JComboBox corporateComboBox;
    private com.toedter.calendar.JTextFieldDateEditor dateField;
    private javax.swing.JButton discountButton;
    private javax.swing.JDialog discountDialog;
    private javax.swing.ButtonGroup discountGroup;
    private javax.swing.JPanel discountPanel;
    private javax.swing.JComboBox drBox;
    private javax.swing.JLabel drLabel;
    private javax.swing.JTextField flatField;
    private javax.swing.JCheckBox flatOption;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField mrnField;
    private javax.swing.JLabel mrnLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JButton okButton;
    public static javax.swing.JTextField operatorField;
    private javax.swing.JLabel operatorLabel;
    private javax.swing.JTextField pNameField;
    private javax.swing.JComboBox pTypeBox;
    private javax.swing.JLabel patientLabel;
    private javax.swing.JCheckBox percentOption;
    private javax.swing.JSpinner percentSpinner;
    private javax.swing.JButton printButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton submitButton;
    private javax.swing.JList testList;
    private javax.swing.JLabel totalField;
    // End of variables declaration//GEN-END:variables
}
