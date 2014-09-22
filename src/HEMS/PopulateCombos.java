
package HEMS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class PopulateCombos {
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    public PopulateCombos() throws SQLException{
        conn = Database.getConnection();
    }
    public String religion() throws SQLException{
        String religionquery = "SELECT * from religion";
        try {
            pst = conn.prepareStatement(religionquery);
            rs = pst.executeQuery();
            while (rs.next()){
                String religion = rs.getString("religion");
                return religion;
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        String religion = rs.getString("religion");
        return religion;
    }
}
