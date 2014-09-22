
package HEMS;

import java.awt.HeadlessException;
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
import javax.swing.JTextField;


public class Registration extends javax.swing.JFrame {
    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pst,pst2, pst3 = null;
    
   
    public Registration() throws SQLException{
        initComponents();
        conn = DBUtil.getConnection();
        showDate();
        state();
        occupation();
        Nationality();
        religion();
        patientType();
        gender();
        stateOR();
        costField.setEditable(false);
        costField.setText("10000.0");
        pcatBox.addItemListener((ItemEvent e) -> {
            cost();
        });
    }
    private void reset(){
        pcatBox.setSelectedIndex(0);
        surnameField.setText(null);
        fnameField.setText(null);
        mnameField.setText(null);
        genderBox.setSelectedIndex(0);
        DOBChooser.setDate(null);
        addField.setText(null);
        telField.setText(null);
        emailField.setText(null);
        stateBox.setSelectedIndex(0);
        religionBox.setSelectedIndex(0);
        occupationBox.setSelectedIndex(0);
        residentStateBox.setSelectedIndex(0);
        nationalityBox.setSelectedIndex(0);
        nokField.setText(null);
        telnokField.setText(null);
        addnokField.setText(null);
    }
    private void cost(){
        String category = pcatBox.getSelectedItem().toString();
        String costquery = "SELECT * FROM PATIENTCATEGORY WHERE CATEGORY = ?";
        try {
            pst = conn.prepareStatement(costquery);
            pst.setString(1, category);
            rs = pst.executeQuery();
            if(rs.next()){
                    Double amount = rs.getDouble("registration");
                    costField.setText(Double.toString(amount));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void showDate(){
        Calendar cal = new GregorianCalendar();
        dateField.setDateFormatString("yyyy-MM-dd");
        dateField.setDate(cal.getTime());
    }
    private void occupation(){
        String occquery = "SELECT * from occupation";
        try {
            pst = conn.prepareStatement(occquery);
            rs = pst.executeQuery();
            while (rs.next()){
                String occ = rs.getString("occupation");
                occupationBox.addItem(occ);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    private void state(){
        String statequery = "SELECT * from states";
        try {
            pst = conn.prepareStatement(statequery);
            rs = pst.executeQuery();
            while (rs.next()){
                String state = rs.getString("state");
                stateBox.addItem(state);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    private void stateOR(){
        String statequery = "SELECT * from states";
        try {
            pst = conn.prepareStatement(statequery);
            rs = pst.executeQuery();
            while (rs.next()){
                String state = rs.getString("state");
                residentStateBox.addItem(state);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
     private void Nationality(){
        String nationquery = "SELECT * from nationality";
        try {
            pst = conn.prepareStatement(nationquery);
            rs = pst.executeQuery();
            while (rs.next()){
                String nation = rs.getString("nationality");
                nationalityBox.addItem(nation);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    
     private void religion(){
        String religionquery = "SELECT * from religion";
        try {
            pst = conn.prepareStatement(religionquery);
            rs = pst.executeQuery();
            while (rs.next()){
                String religion = rs.getString("religion");
                religionBox.addItem(religion);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
     private void gender(){
         String genderquery = "SELECT * FROM GENDER";
         try {
            pst = conn.prepareStatement(genderquery);
            rs = pst.executeQuery();
            while (rs.next()){
                String gender = rs.getString("gender");
                genderBox.addItem(gender);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
     }
     private void patientType(){
        String ptypequery = "SELECT * from patientcategory";
        try {
            pst = conn.prepareStatement(ptypequery);
            rs = pst.executeQuery();
            while (rs.next()){
                String ptype = rs.getString("category");
                pcatBox.addItem(ptype);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
    }  
    private void generateBill(){
        
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        pcatBox = new javax.swing.JComboBox();
        surnameField = new javax.swing.JTextField();
        fnameField = new javax.swing.JTextField();
        addField = new javax.swing.JTextField();
        telField = new javax.swing.JTextField();
        emailField = new javax.swing.JTextField();
        mnameField = new javax.swing.JTextField();
        occupationBox = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        stateBox = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        nationalityBox = new javax.swing.JComboBox();
        religionBox = new javax.swing.JComboBox();
        nokField = new javax.swing.JTextField();
        telnokField = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        addnokField = new javax.swing.JTextField();
        submitButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        reprintButton = new javax.swing.JButton();
        genderBox = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        residentStateBox = new javax.swing.JComboBox();
        jLabel20 = new javax.swing.JLabel();
        lgBox = new javax.swing.JComboBox();
        operatorLabel = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        costField = new javax.swing.JTextField();
        DOBChooser = new com.toedter.calendar.JDateChooser();
        dateField = new com.toedter.calendar.JTextFieldDateEditor();

        jTextField1.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Register Patient", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N

        jLabel1.setText("Patient Category: ");

        jLabel2.setText("Surname: ");

        jLabel3.setText("First name: ");

        jLabel4.setText("Middle Name: ");

        jLabel6.setText("DOB: ");

        jLabel7.setText("Home Address: ");

        jLabel8.setText("Tel: ");

        jLabel9.setText("Email: ");

        jLabel10.setText("Occupation: ");

        jLabel11.setText("Religion: ");

        jLabel12.setText("Next of Kin: ");

        jLabel13.setText("Tel (Next of Kin):");

        jLabel17.setText("State of Origin: ");

        jLabel18.setText("Nationality: ");

        jLabel19.setText("Address (Next of Kin): ");

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

        reprintButton.setText("Reprint");

        jLabel5.setText("Sex: ");

        jLabel15.setText("State of Residence: ");

        residentStateBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                residentStateBoxItemStateChanged(evt);
            }
        });

        jLabel20.setText("LGA: ");

        operatorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        operatorLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        operatorLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel21.setText("Amount: ");

        DOBChooser.setDateFormatString("yyyy-MM-dd");

        dateField.setEditable(false);
        dateField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        dateField.setToolTipText("yyyy-MM-dd");
        dateField.setDateFormatString("yyyy-MM-dd");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(religionBox, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(occupationBox, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stateBox, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nationalityBox, 0, 133, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nokField))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(telnokField, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(resetButton)
                                .addGap(18, 18, 18)
                                .addComponent(reprintButton)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(addnokField))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(surnameField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addGap(16, 16, 16)
                                .addComponent(fnameField, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(pcatBox, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addComponent(jLabel4)
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mnameField, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dateField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(10, 10, 10)
                                .addComponent(telField, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DOBChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(addField))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(genderBox, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(residentStateBox, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lgBox, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(costField, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(operatorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(19, 19, 19))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {reprintButton, resetButton, submitButton});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(pcatBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(dateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(surnameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(mnameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(fnameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)))
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(jLabel7)
                        .addComponent(addField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(DOBChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(genderBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(telField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15)
                        .addComponent(residentStateBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel20)
                        .addComponent(lgBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)
                        .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(stateBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(nationalityBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(religionBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(occupationBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(nokField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(telnokField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(addnokField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(costField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(resetButton)
                    .addComponent(reprintButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(operatorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {reprintButton, resetButton, submitButton});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed

             try {  
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String date = sdf.format(dateField.getDate());
                    String dob = ((JTextField)DOBChooser.getDateEditor().getUiComponent()).getText();
                    String save = "INSERT INTO patientregister(Surname,firstname,middlename,Address,religion, stateoforigin, nationality, sex, "
                            + "telephone, email, nextofkin,telnok,addnok, occupation, stateofresidence, lga,dateofreg, dateofbirth, category) "
                            + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    long id = -1L;
                    pst = conn.prepareStatement(save, PreparedStatement.RETURN_GENERATED_KEYS);
                    int col = 1;
                    pst.setString(col++, surnameField.getText());
                    pst.setString(col++, fnameField.getText());
                    pst.setString(col++, mnameField.getText());
                    pst.setString(col++, addField.getText());
                    pst.setString(col++, religionBox.getSelectedItem().toString());
                    pst.setString(col++, stateBox.getSelectedItem().toString());
                    pst.setString(col++, nationalityBox.getSelectedItem().toString());
                    pst.setString(col++, genderBox.getSelectedItem().toString());
                    pst.setString(col++, telField.getText());
                    pst.setString(col++, emailField.getText());
                    pst.setString(col++, nokField.getText());
                    pst.setString(col++, telnokField.getText());
                    pst.setString(col++, addnokField.getText());
                    pst.setString(col++, occupationBox.getSelectedItem().toString());
                    pst.setString(col++, residentStateBox.getSelectedItem().toString());
                    pst.setString(col++, lgBox.getSelectedItem().toString());
                    pst.setString(col++, date);
                    pst.setString(col++, dob);
                    pst.setString(col++, pcatBox.getSelectedItem().toString());
                    pst.executeUpdate();
                    rs = pst.getGeneratedKeys();
                    if(rs != null && rs.next()){
                        id = rs.getLong(1);
                        String mrno = "P"+id+"H";
                        String mrn = "UPDATE patientregister set mrn = ? WHERE id = ?";
                        try {
                            pst = conn.prepareStatement(mrn); 
                            int column = 1;
                            pst.setString(column++,mrno);
                            pst.setLong(column++,id);
                            pst.executeUpdate();
                            ///////////Invoice///////////
                            String invoice = "INSERT INTO TINVOICE (mrn, NAME, amount, description, date, module) VALUES (?,?,?,?,?,?)";
                            long invoiceid;
                            pst3 = conn.prepareStatement(invoice, PreparedStatement.RETURN_GENERATED_KEYS);
                            int coll = 1;
                            String name = fnameField.getText()+" "+mnameField.getText()+" "+surnameField.getText();
                            pst3.setString(coll++,mrno);
                            pst3.setString(coll++, name);
                            pst3.setDouble(coll++, Double.parseDouble(costField.getText()));
                            pst3.setString(coll++, "REGISTRATION AND CONSULTATION");
                            pst3.setString(coll++, date);
                            pst3.setString(coll++, "REGISTRATION");
                            pst3.executeUpdate();
                            rs = pst3.getGeneratedKeys();
                            ///////Update Invoice////////////
                            if(rs != null && rs.next()){
                                invoiceid = rs.getLong(1);
                                String invoiceno = "EFH"+invoiceid;
                                String updateinvoice = "UPDATE TINVOICE SET BillNo = ? WHERE ID = ?";
                                pst3 = conn.prepareStatement(updateinvoice, PreparedStatement.RETURN_GENERATED_KEYS);
                                pst3.setString(1, invoiceno);
                                pst3.setLong(2, invoiceid);
                                pst3.executeUpdate();
                                rs = pst3.getGeneratedKeys();
                            }
                           ///////////InvoiceDetails///////////
                            String billnoQuery = "SELECT * FROM TINVOICE";
                            pst3 = conn.prepareStatement(billnoQuery);
                            rs = pst3.executeQuery();
                            rs.last();
                            String billno = rs.getString("billno");
                            String invoicedetails = "INSERT INTO TINVOICEDetails (BillNo, name, mrn, amount, description, date) VALUES (?,?,?,?,?,?)";
                            pst2 = conn.prepareStatement(invoicedetails);
                            int cell = 1;
                            pst2.setString(cell++, billno);
                            pst2.setString(cell++, name);
                            pst2.setString(cell++,mrno);
                            pst2.setDouble(cell++, Double.parseDouble(costField.getText()));
                            pst2.setString(cell++, "REGISTRATION AND CONSULTATION");
                            pst2.setString(cell++, date);
                            pst2.executeUpdate(); 
                        } catch (SQLException ex) {
                            Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
                        } 
            }

            JOptionPane.showMessageDialog(null, "Patient registered", "Registration Status", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_submitButtonActionPerformed
    
    private void residentStateBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_residentStateBoxItemStateChanged
       if(residentStateBox.isFocusOwner()){
          lgBox.removeAllItems();
        String state = residentStateBox.getSelectedItem().toString();
        String lgquery = "SELECT * FROM LGA JOIN STATES ON LGA.STATEID = STATES.ID WHERE STATE = ?";
        try {
            pst = conn.prepareStatement(lgquery);
            pst.setString(1, state);
            rs = pst.executeQuery();
            while(rs.next()){
                String lga = rs.getString("lga");
                lgBox.addItem(lga);
            }
        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex);
        }
       }
        
       
    }//GEN-LAST:event_residentStateBoxItemStateChanged

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        reset();
    }//GEN-LAST:event_resetButtonActionPerformed

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
            java.util.logging.Logger.getLogger(Registration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Registration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Registration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Registration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Registration().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DOBChooser;
    private javax.swing.JTextField addField;
    private javax.swing.JTextField addnokField;
    private javax.swing.JTextField costField;
    private com.toedter.calendar.JTextFieldDateEditor dateField;
    private javax.swing.JTextField emailField;
    private javax.swing.JTextField fnameField;
    private javax.swing.JComboBox genderBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JComboBox lgBox;
    private javax.swing.JTextField mnameField;
    private javax.swing.JComboBox nationalityBox;
    private javax.swing.JTextField nokField;
    private javax.swing.JComboBox occupationBox;
    public static javax.swing.JLabel operatorLabel;
    private javax.swing.JComboBox pcatBox;
    private javax.swing.JComboBox religionBox;
    private javax.swing.JButton reprintButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JComboBox residentStateBox;
    private javax.swing.JComboBox stateBox;
    private javax.swing.JButton submitButton;
    private javax.swing.JTextField surnameField;
    private javax.swing.JTextField telField;
    private javax.swing.JTextField telnokField;
    // End of variables declaration//GEN-END:variables
}
