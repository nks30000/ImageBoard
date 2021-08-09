package sequence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * �� ���̺��� Ű������ ����ϴ�
 * �Ϸ� ��ȣ�� �����ִ� Ŭ����.
 * @author �ֹ���
 */
public class Sequencer {
    public synchronized static int nextId(Connection conn, String tableName) 
    throws SQLException {
        PreparedStatement pstmtSelect = null;
        ResultSet rsSelect = null;
        
        PreparedStatement pstmtUpdate = null;
        
        try {
            pstmtSelect = conn.prepareStatement(
            "select MESSAGE_ID from ID_SEQUENCES where TABLE_NAME = ?");
            pstmtSelect.setString(1, tableName);
            
            rsSelect = pstmtSelect.executeQuery();
            
            if (rsSelect.next()) {
                int id = rsSelect.getInt(1);
                id++;
                
                pstmtUpdate = conn.prepareStatement(
                  "update ID_SEQUENCES set MESSAGE_ID = ? "+
                  "where TABLE_NAME = ?");
                pstmtUpdate.setInt(1, id);
                pstmtUpdate.setString(2, tableName);
                pstmtUpdate.executeUpdate();
                
                return id;
                
            } else {
                pstmtUpdate = conn.prepareStatement(
                "insert into ID_SEQUENCES values (?, ?)");
                pstmtUpdate.setString(1, tableName);
                pstmtUpdate.setInt(2, 1);
                pstmtUpdate.executeUpdate();
                
                return 1;
            }
        } finally {
            if (rsSelect != null) 
                try { rsSelect.close(); } catch(SQLException ex) {}
            if (pstmtSelect != null) 
                try { pstmtSelect.close(); } catch(SQLException ex) {}
            if (pstmtUpdate != null) 
                try { pstmtUpdate.close(); } catch(SQLException ex) {}
        }
    }
}
