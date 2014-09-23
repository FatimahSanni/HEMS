
package hems.optical;

import hems.Billing.Billing;
import HEMS.DBUtil;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;


class ItemTableModel extends DefaultTableModel{
    public ItemTableModel(){    
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        
        switch(column){
            case 0:
                return false;
            case 1:
                return false;
            case 2:
                return false;
            case 3:
                return true;
        }
        return false;
    }
    
}
class OptiPaymentTableModel extends DefaultTableModel{
    public OptiPaymentTableModel(){
        
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        
        switch(column){
            case 0:
                return false;
            case 1:
                return false;
            case 2:
                return true;
            case 3:
                return false;
        }
        return false;
    }

    
}

public class Optical extends javax.swing.JFrame {
    private Connection conn = null;
    private PreparedStatement pst, pst1, pst4 = null;
    private ResultSet rs, rs2 = null;
    private OptiPaymentTableModel paymentTableModel;
    private final ItemTableModel itemTableModel;
    private final JMenuItem deleteItem;
    private final JPopupMenu popupMenu;
    
    public Optical() throws SQLException {
        initComponents();
        conn = DBUtil.getConnection();
        setLocationRelativeTo(null);
        deleteItem = new JMenuItem("Delete Item");
        popupMenu = new JPopupMenu();
        popupMenu.add(deleteItem);
        paymentTableModel = new OptiPaymentTableModel();
        itemTableModel = new ItemTableModel();
        itemTable.setRowMargin(5);
        itemTable.setRowHeight(20);
        paymentTable.setRowHeight(20);
        showDate();
        itemTableModel.addColumn("Description");
        itemTableModel.addColumn("Rate");
        itemTableModel.addColumn("In stock");
        itemTableModel.addColumn("Quantity");
        opticalTab.setVisible(false);
        paymentTableModel.addColumn("Description");
        paymentTableModel.addColumn("Rate");
        paymentTableModel.addColumn("Quantity");
        paymentTableModel.addColumn("Amount");
        itemTable.setModel(itemTableModel);
        paymentTable.setModel(paymentTableModel);
        populateItemTable();
        deleteItem.addActionListener((ActionEvent e) -> {
        int row = paymentTable.getSelectedRow();
        paymentTableModel.removeRow(row);
        totalField.setText(null);
//          if(totalField.getText().length() != 0){
//              calcTotal();
//        }
        });
        paymentTableModel.addTableModelListener((TableModelEvent e) -> {
            calcTotal();
        });
        showDate();
    }
    
    private void stockNotifier(){
        int row = itemTable.getSelectedRow();
        String desc = (String)itemTable.getValueAt(row, 0);
        String qtyString = (String)itemTable.getValueAt(row, 3);
        int qty = Integer.parseInt(qtyString);
        String stockQuery = "SELECT * FROM OPTICAL WHERE DESCRIPTION = ?";
        try {
            pst = conn.prepareStatement(stockQuery);
            pst.setString(1, desc);
            rs = pst.executeQuery();
            if(rs.next()){
                int qtydb = rs.getInt("quantity");
                if(qtydb == 0){
                    JOptionPane.showMessageDialog(null, desc+" out of stock");
                    itemTableModel.setValueAt(null, row, 3);
                }
                else if(qtydb < qty){
                    JOptionPane.showMessageDialog(null, "You have only "+qtydb+" "+desc+" in stock");
                    itemTableModel.setValueAt(null, row, 3);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Optical.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    ////Output date in the datefield///////
    private void showDate(){
        Calendar cal = new GregorianCalendar();
        dateField.setDateFormatString("yyyy-MM-dd hh:mm:a");
        dateField.setDate(cal.getTime());
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
    /////Test if outpatient  001h is not used and make field uneditable
    private void textEditStatus(){
        if(mrnField.getText().equalsIgnoreCase("001H")){
            pNameField.setEditable(true);
        }
        else{
            pNameField.setEditable(false);
        }
    }
    /////////Generate a list of invoice to be checked against Payment//////////
    private void invoiceList(){
        String invoiceQuery = "SELECT * FROM TINVOICE WHERE MRN = ?";
        
        String paymentQuery = "SELECT * FROM PAYMENTDETAILS WHERE INVOICENO = ?";
        try {
            pst = conn.prepareStatement(invoiceQuery);
            pst1 = conn.prepareStatement(paymentQuery);
            pst.setString(1, mrnField.getText());
            rs = pst.executeQuery();
            while(rs.next()){
                String billno = rs.getString("billno");
                pst1.setString(1, billno);
                rs2 = pst1.executeQuery();
                if(rs2.next()){
                    rs2.last();
                    switch(rs2.getString("paid")){
                        case "P":
                            JOptionPane.showMessageDialog(null, "Patient has a pending bill");
                            outPatient();
                            break;
                    }
                }
                else if (mrnField.getText().equalsIgnoreCase("001h")){
                    
                }
                else{
                    JOptionPane.showMessageDialog(null, "Patient has an outstanding bill");
                    outPatient();
                }
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void updateStock() throws SQLException{
        UpdateStockOptical update = new UpdateStockOptical();
        update.setVisible(true);
    }
    private void newInvoice(){
        
        if(opticalTab.isShowing()){
            JOptionPane.showMessageDialog(null, "The invoice tab is already open", "Invoice Tab Status", JOptionPane.ERROR_MESSAGE);
        }
        else{
            opticalTab.setVisible(true);
            opticalTab.add("Raise New Invoice", new JScrollPane(subPanel));
        }    
    }
    private void populateItemTable(){
        String populateQuery = "SELECT * FROM OPTICAL";
        try {
            pst = conn.prepareStatement(populateQuery);
            rs = pst.executeQuery();
            while(rs.next()){
                String item = rs.getString("description");
                Double amount = rs.getDouble("amount");
                int stock = rs.getInt("quantity");
                itemTableModel.addRow(new Object[] {item, amount,stock, null});
               
            }
        } catch (SQLException ex) {
            Logger.getLogger(Optical.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    private void calcTotal(){
        DecimalFormat df = new DecimalFormat("0.00");
        int rowCount = paymentTable.getRowCount(); 
        double total = 0.00;
            for(int i = 0; i < rowCount; i++){
                try{
                    double amount = Double.parseDouble(paymentTable.getValueAt(i, 3).toString());
                    total = total + amount;
                    String totalResult = df.format(total);
                    totalField.setText("=N= "+totalResult);
                }
                catch(NullPointerException nx){
//                    Logger.getLogger(Optical.class.getName()).log(Level.SEVERE, null, nx);
                }
                
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

        jScrollPane4 = new javax.swing.JScrollPane();
        opticalPanel = new javax.swing.JPanel();
        opticalToolBar = new javax.swing.JToolBar();
        invoiceButton = new javax.swing.JButton();
        addItemButton = new javax.swing.JButton();
        updateStockButton = new javax.swing.JButton();
        editItemButton = new javax.swing.JButton();
        reportButton = new javax.swing.JButton();
        opticalTab = new javax.swing.JTabbedPane();
        subPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        itemTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        paymentTable = new javax.swing.JTable();
        saveButton = new javax.swing.JButton();
        reprintButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        mrnField = new javax.swing.JTextField();
        pNameField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        totalField = new javax.swing.JLabel();
        dateField = new com.toedter.calendar.JTextFieldDateEditor();
        opticalMenuBar = new javax.swing.JMenuBar();
        transactionMenu = new javax.swing.JMenu();
        invoiceItem = new javax.swing.JMenuItem();
        saveItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        printItem = new javax.swing.JMenuItem();
        reprintItem = new javax.swing.JMenuItem();
        exitItem = new javax.swing.JMenuItem();
        opticalMasterMenu = new javax.swing.JMenu();
        addItem = new javax.swing.JMenuItem();
        updateItem = new javax.swing.JMenuItem();
        editItem = new javax.swing.JMenuItem();
        salesReportsMenu = new javax.swing.JMenu();
        dailySalesItem = new javax.swing.JMenuItem();
        stockMovementItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        opticalPanel.setBackground(new java.awt.Color(255, 255, 255));

        opticalToolBar.setRollover(true);

        invoiceButton.setText("New Invoice  ");
        invoiceButton.setFocusable(false);
        invoiceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        invoiceButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        invoiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invoiceButtonActionPerformed(evt);
            }
        });
        opticalToolBar.add(invoiceButton);

        addItemButton.setText("Add Item  ");
        addItemButton.setFocusable(false);
        addItemButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addItemButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addItemButtonActionPerformed(evt);
            }
        });
        opticalToolBar.add(addItemButton);

        updateStockButton.setText("Update Stock  ");
        updateStockButton.setFocusable(false);
        updateStockButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        updateStockButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        updateStockButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateStockButtonActionPerformed(evt);
            }
        });
        opticalToolBar.add(updateStockButton);

        editItemButton.setText("Edit Item details  ");
        editItemButton.setFocusable(false);
        editItemButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editItemButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        editItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editItemButtonActionPerformed(evt);
            }
        });
        opticalToolBar.add(editItemButton);

        reportButton.setText(" Report  ");
        reportButton.setFocusable(false);
        reportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        reportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        opticalToolBar.add(reportButton);

        itemTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        itemTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                itemTableKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(itemTable);

        paymentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        paymentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                paymentTableMousePressed(evt);
            }
        });
        paymentTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                paymentTableKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(paymentTable);

        saveButton.setText("SUBMIT");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        reprintButton.setText("REPRINT");

        resetButton.setText("RESET");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("MRN: ");

        jLabel2.setText("Name: ");

        mrnField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mrnFieldActionPerformed(evt);
            }
        });

        pNameField.setEditable(false);

        jLabel3.setText("TOTAL");

        totalField.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        totalField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalField.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout subPanelLayout = new javax.swing.GroupLayout(subPanel);
        subPanel.setLayout(subPanelLayout);
        subPanelLayout.setHorizontalGroup(
            subPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelLayout.createSequentialGroup()
                .addGap(324, 324, 324)
                .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(reprintButton)
                .addGap(18, 18, 18)
                .addComponent(resetButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(subPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(subPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(subPanelLayout.createSequentialGroup()
                        .addGroup(subPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(subPanelLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(pNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(subPanelLayout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 588, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, subPanelLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(totalField, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(subPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(mrnField, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(dateField, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29))))
        );

        subPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {reprintButton, resetButton, saveButton});

        subPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2});

        subPanelLayout.setVerticalGroup(
            subPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(subPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(mrnField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(subPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(pNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(subPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(subPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalField))
                .addGap(50, 50, 50)
                .addGroup(subPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reprintButton)
                    .addComponent(resetButton))
                .addContainerGap())
        );

        subPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {reprintButton, resetButton, saveButton});

        subPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel3, totalField});

        opticalTab.addTab("Create New Invoice", subPanel);

        javax.swing.GroupLayout opticalPanelLayout = new javax.swing.GroupLayout(opticalPanel);
        opticalPanel.setLayout(opticalPanelLayout);
        opticalPanelLayout.setHorizontalGroup(
            opticalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(opticalToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(opticalTab)
        );
        opticalPanelLayout.setVerticalGroup(
            opticalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(opticalPanelLayout.createSequentialGroup()
                .addComponent(opticalToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(opticalTab, javax.swing.GroupLayout.PREFERRED_SIZE, 721, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane4.setViewportView(opticalPanel);

        transactionMenu.setText("Transaction Module");

        invoiceItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        invoiceItem.setText("New Invoice");
        invoiceItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invoiceItemActionPerformed(evt);
            }
        });
        transactionMenu.add(invoiceItem);

        saveItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveItem.setText("Save");
        transactionMenu.add(saveItem);
        transactionMenu.add(jSeparator1);

        printItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        printItem.setText("Print");
        transactionMenu.add(printItem);

        reprintItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        reprintItem.setText("Reprint Invoice");
        transactionMenu.add(reprintItem);

        exitItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
        exitItem.setText("Exit");
        exitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitItemActionPerformed(evt);
            }
        });
        transactionMenu.add(exitItem);

        opticalMenuBar.add(transactionMenu);

        opticalMasterMenu.setText("Master Module");
        opticalMasterMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opticalMasterMenuActionPerformed(evt);
            }
        });

        addItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        addItem.setText("Add Item");
        addItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addItemActionPerformed(evt);
            }
        });
        opticalMasterMenu.add(addItem);

        updateItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        updateItem.setText("Update Stock");
        updateItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateItemActionPerformed(evt);
            }
        });
        opticalMasterMenu.add(updateItem);

        editItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        editItem.setText("Edit Item details");
        opticalMasterMenu.add(editItem);

        opticalMenuBar.add(opticalMasterMenu);

        salesReportsMenu.setText("Sales reports");

        dailySalesItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        dailySalesItem.setText("Daily Sales report");
        salesReportsMenu.add(dailySalesItem);

        stockMovementItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        stockMovementItem.setText("Stock Movement");
        salesReportsMenu.add(stockMovementItem);

        opticalMenuBar.add(salesReportsMenu);

        setJMenuBar(opticalMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1032, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addItemButtonActionPerformed
        try {
            // TODO add your handling code here:
            AddOptical addnewItem = new AddOptical();
            addnewItem.setVisible(true);
        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex);
        }
    }//GEN-LAST:event_addItemButtonActionPerformed

    private void invoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invoiceButtonActionPerformed
        // TODO add your handling code here:
       newInvoice();
    }//GEN-LAST:event_invoiceButtonActionPerformed

    private void exitItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitItemActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_exitItemActionPerformed

    private void invoiceItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invoiceItemActionPerformed
        // TODO add your handling code here:
        newInvoice();
    }//GEN-LAST:event_invoiceItemActionPerformed
   
    private void paymentTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paymentTableMousePressed
        int row = paymentTable.rowAtPoint(evt.getPoint());
        paymentTable.getSelectionModel().addSelectionInterval(row, row);
        if(evt.getButton() == MouseEvent.BUTTON3){
            popupMenu.show(paymentTable, evt.getX(), evt.getY());
        }

    }//GEN-LAST:event_paymentTableMousePressed

    private void paymentTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_paymentTableKeyReleased
        // DELETE AN ITEM WHEN A USER PRESSES THE DELETE BUTTON (ASK FOR CONFIRMATION FIRST)
            if(evt.getKeyCode() == KeyEvent.VK_DELETE){
                int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this item", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
                if(option == JOptionPane.YES_OPTION){
                    paymentTableModel.removeRow(paymentTable.getSelectedRow());
                    totalField.setText(null);
                }
                
        }
            ////CALCULATE AMOUNT WHEN RATE AND QTY IS CHOSEN/ENTERED
            else{
                int selectedrow = paymentTable.getSelectedRow();
                Double rate = (Double)paymentTableModel.getValueAt(selectedrow, 1);
                String qtyString = (String)paymentTableModel.getValueAt(selectedrow, 2);
                try{
                    int quantity = Integer.parseInt(qtyString);
                    Double amount = rate*quantity;
                    paymentTableModel.setValueAt(amount, selectedrow, 3);
                }
                 catch(NumberFormatException ex){
                    JOptionPane.showMessageDialog(null, "Please enter a number");
                    paymentTableModel.setValueAt(null, selectedrow, 2);
                    paymentTableModel.setValueAt(null, selectedrow, 3);
                    totalField.setText(null);
        }
            }  
    }//GEN-LAST:event_paymentTableKeyReleased

    private void itemTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_itemTableKeyReleased
        stockNotifier();
        int row = itemTable.getSelectedRow();
        String item = (String)itemTableModel.getValueAt(row, 0);
        Double rate = (double)itemTableModel.getValueAt(row, 1);
        String quantityString = (String)itemTableModel.getValueAt(row, 3);
        try{
            int quantity = Integer.parseInt(quantityString);
            Double amount = rate*quantity;
            paymentTableModel.addRow(new Object[] {item, rate, quantity, amount});
            itemTableModel.setValueAt(null, row, 3);
                
        } catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(null, "Please enter a number");
            itemTableModel.setValueAt(null, row, 3);
        }  
    }//GEN-LAST:event_itemTableKeyReleased

    private void updateStockButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateStockButtonActionPerformed
        try {
            // TODO add your handling code here:
            updateStock();
        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex);
        }
    }//GEN-LAST:event_updateStockButtonActionPerformed

    private void updateItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateItemActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            updateStock();
        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex);
        }
    }//GEN-LAST:event_updateItemActionPerformed

    private void addItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addItemActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            AddOptical addnewItem = new AddOptical();
            addnewItem.setVisible(true);
        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex);
        }
    }//GEN-LAST:event_addItemActionPerformed
    private void editItem() throws SQLException{
        EditOpticalItem edit = new EditOpticalItem();
        edit.setVisible(true);
    }
    private void editItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editItemButtonActionPerformed
       try{
           editItem();
       }catch(SQLException ex){
           JOptionPane.showMessageDialog(null, ex);
       }
        
    }//GEN-LAST:event_editItemButtonActionPerformed

    private void opticalMasterMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opticalMasterMenuActionPerformed
        try{
           editItem();
       }catch(SQLException ex){
           JOptionPane.showMessageDialog(null, ex);
       }
    }//GEN-LAST:event_opticalMasterMenuActionPerformed

    private void mrnFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mrnFieldActionPerformed
        textEditStatus();
        try {
            String patientQuery = "SELECT * FROM PATIENTREGISTER WHERE MRN = ?";
            pst = conn.prepareStatement(patientQuery);
            pst.setString(1, mrnField.getText());
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
        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex);
        }
        invoiceList();
    }//GEN-LAST:event_mrnFieldActionPerformed
    //Update Optical Invoice Details by Item
    private void updateInvoiceDetails(){
        try {
                String billnoQuery = "SELECT * FROM TINVOICE WHERE MRN = ?";
                pst1 = conn.prepareStatement(billnoQuery);
                pst1.setString(1, mrnField.getText());
                rs = pst1.executeQuery();
                rs.last();
                String billno = rs.getString("billno");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(dateField.getDate());
                int rowCount = paymentTable.getRowCount();
                for(int i = 0; i < rowCount; i++){
                    String descriptions = (String)paymentTableModel.getValueAt(i, 0);
                    double cost = (double)paymentTableModel.getValueAt(i, 3);
//                    double cost = (Double.parseDouble());
                    String invoiceDetails = "INSERT INTO TINVOICEDETAILS(BillNo, MRN, AMOUNT, DESCRIPTION, DATE) VALUES (?,?,?,?,?)";
                    pst = conn.prepareStatement(invoiceDetails);
                    int col = 1;
                    pst.setString(col++, billno);
                    pst.setString(col++, mrnField.getText().toUpperCase());
                    pst.setDouble(col++, cost);
                    pst.setString(col++, descriptions);
                    pst.setString(col++, date);
                    pst.executeUpdate();    
            }    
                
            JOptionPane.showMessageDialog(null, "Invoice updated to respective databases", "Invoice status", JOptionPane.INFORMATION_MESSAGE);
            reset();
        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex, "Invoice Details Updation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateInvoice(){
        // Save invoice details to database
        try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(dateField.getDate());
                String totalString = totalField.getText();
                Double total = Double.parseDouble(totalString.substring(3));
                String invoiceUpdate = "INSERT INTO TINVOICE(MRN, Description, AMOUNT, date) VALUES (?,?,?,?)";
                long id;
                pst = conn.prepareStatement(invoiceUpdate, PreparedStatement.RETURN_GENERATED_KEYS);
                pst.setString(1, mrnField.getText().toUpperCase());
                pst.setString(2, "OPTICAL PURCHASED");
                pst.setDouble(3, total);
                pst.setString(4, date);
                pst.executeUpdate();
                rs = pst.getGeneratedKeys();
                    if(rs != null && rs.next()){
                        id = rs.getLong(1);
                        ///////Update Invoice////////////
                        String invoiceno = "OPT"+id;
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
    
    //Reset all values to null
    private void reset(){
        mrnField.setText(null);
        totalField.setText(null);
        pNameField.setText(null);
        paymentTableModel.setRowCount(0);
        
    }
    ///Reduce quantity of product after each order
    private void deductQuantity(){
        int rowCount = paymentTable.getRowCount();
            for(int i = 0; i < rowCount; i++){
                String quantityString = (String)paymentTable.getValueAt(i, 2);
                int quantity = Integer.parseInt(quantityString);
                String item = (String)paymentTable.getValueAt(i, 0);
                try {
                    String qtyQuery = "SELECT * FROM OPTICAL WHERE DESCRIPTION = ?";
                    pst = conn.prepareStatement(qtyQuery);
                    pst.setString(1, item);
                    rs = pst.executeQuery();
                    if(rs.next()){
                        int qtydb = rs.getInt("Quantity");
                        if(qtydb >= quantity){
                            int newQty = qtydb - quantity;
                            String updateQty = "UPDATE OPTICAL SET QUANTITY = ? WHERE DESCRIPTION = ?";
                            pst1 = conn.prepareStatement(updateQty);
                            pst1.setInt(1, newQty);
                            pst1.setString(2, item);
                            pst1.executeUpdate();
                            updateInvoice();
                            updateInvoiceDetails();
                        }
                        else if(qtydb == 0){
                            JOptionPane.showMessageDialog(null, item+" out of stock");
                        }
                        else{
                            JOptionPane.showMessageDialog(null, "You have only "+qtydb+" "+item+" in stock");
                        }
                        
                        
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, ex);
                }
                
            } 
    }
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        deductQuantity();
        
    }//GEN-LAST:event_saveButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        // TODO add your handling code here:
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
            java.util.logging.Logger.getLogger(Optical.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Optical.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Optical.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Optical.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Optical().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(Optical.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addItem;
    private javax.swing.JButton addItemButton;
    private javax.swing.JMenuItem dailySalesItem;
    private com.toedter.calendar.JTextFieldDateEditor dateField;
    private javax.swing.JMenuItem editItem;
    private javax.swing.JButton editItemButton;
    private javax.swing.JMenuItem exitItem;
    private javax.swing.JButton invoiceButton;
    private javax.swing.JMenuItem invoiceItem;
    private javax.swing.JTable itemTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTextField mrnField;
    private javax.swing.JMenu opticalMasterMenu;
    private javax.swing.JMenuBar opticalMenuBar;
    private javax.swing.JPanel opticalPanel;
    private javax.swing.JTabbedPane opticalTab;
    private javax.swing.JToolBar opticalToolBar;
    private javax.swing.JTextField pNameField;
    private javax.swing.JTable paymentTable;
    private javax.swing.JMenuItem printItem;
    private javax.swing.JButton reportButton;
    private javax.swing.JButton reprintButton;
    private javax.swing.JMenuItem reprintItem;
    private javax.swing.JButton resetButton;
    private javax.swing.JMenu salesReportsMenu;
    private javax.swing.JButton saveButton;
    private javax.swing.JMenuItem saveItem;
    private javax.swing.JMenuItem stockMovementItem;
    private javax.swing.JPanel subPanel;
    private javax.swing.JLabel totalField;
    private javax.swing.JMenu transactionMenu;
    private javax.swing.JMenuItem updateItem;
    private javax.swing.JButton updateStockButton;
    // End of variables declaration//GEN-END:variables
}
