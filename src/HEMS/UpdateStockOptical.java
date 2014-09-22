
package HEMS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

class MediStockTableModel extends DefaultTableModel{
    public MediStockTableModel(){
        
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch(column){
            case 0:
                return false;
            case 1:
                return true;
        }
        return false;
    }
    
}
public class UpdateStockOptical extends javax.swing.JFrame {

    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    private final MediStockTableModel stockTableModel;
    private final TableRowSorter<MediStockTableModel> sorter;
    
    public UpdateStockOptical() throws SQLException {
        initComponents();
        conn = DBUtil.getConnection();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        stockTableModel = new MediStockTableModel();
        stockTableModel.addColumn("Item");
        stockTableModel.addColumn("Quantity");
        stockTable.setModel(stockTableModel);
        sorter = new TableRowSorter<>(stockTableModel);
        stockTable.setRowSorter(sorter);
        populateTable();
    }

    private void populateTable(){
    String populateQuery = "SELECT * FROM OPTICAL";
        try {
            pst = conn.prepareStatement(populateQuery);
            rs = pst.executeQuery();
            while(rs.next()){
                String name = rs.getString("description").toUpperCase();
                int qty = rs.getInt("quantity");
                stockTableModel.addRow(new Object[] {name, qty});
            }
        } catch (SQLException ex) {
            Logger.getLogger(UpdateStockOptical.class.getName()).log(Level.SEVERE, null, ex);
        }
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        updateStockPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        stockTable = new javax.swing.JTable();
        searchField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        updateStockPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Update Stock"));

        stockTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        stockTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                stockTableKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(stockTable);

        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchFieldKeyReleased(evt);
            }
        });

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout updateStockPanelLayout = new javax.swing.GroupLayout(updateStockPanel);
        updateStockPanel.setLayout(updateStockPanelLayout);
        updateStockPanelLayout.setHorizontalGroup(
            updateStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(updateStockPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(updateStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                    .addGroup(updateStockPanelLayout.createSequentialGroup()
                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        updateStockPanelLayout.setVerticalGroup(
            updateStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, updateStockPanelLayout.createSequentialGroup()
                .addContainerGap(31, Short.MAX_VALUE)
                .addGroup(updateStockPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(updateStockPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(updateStockPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        // TODO add your handling code here:
        String search = searchField.getText().trim().toUpperCase();
        if(search.length() == 0){
            sorter.setRowFilter(null);
        }
        else{
            sorter.setRowFilter(RowFilter.regexFilter(search));
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void searchFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyReleased
        // TODO add your handling code here:
        String search = searchField.getText().trim().toUpperCase();
        if(search.length() == 0){
            sorter.setRowFilter(null);
        }
        else{
            sorter.setRowFilter(RowFilter.regexFilter(search));
        }
    }//GEN-LAST:event_searchFieldKeyReleased

    private void stockTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_stockTableKeyReleased
        // TODO add your handling code here:
        String insertQuantity = "UPDATE OPTICAL SET quantity = ? WHERE DESCRIPTION = ?";
        String value = (String)stockTable.getValueAt(stockTable.getSelectedRow(), 0);
        String qtyString = (String)stockTable.getValueAt(stockTable.getSelectedRow(), 1);
        int qty = Integer.parseInt(qtyString);
        try {
            pst = conn.prepareStatement(insertQuantity);
            int col = 1;
            pst.setInt(col++, qty);
            pst.setString(col++, value);
            pst.execute();
            JOptionPane.showMessageDialog(null, value+" now has "+qty+" pieces.", "Stock Update Successful", JOptionPane.INFORMATION_MESSAGE);
        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex);
        }
    }//GEN-LAST:event_stockTableKeyReleased

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
            java.util.logging.Logger.getLogger(UpdateStockOptical.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UpdateStockOptical.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UpdateStockOptical.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UpdateStockOptical.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new UpdateStockOptical().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(UpdateStockOptical.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JTable stockTable;
    private javax.swing.JPanel updateStockPanel;
    // End of variables declaration//GEN-END:variables
}
