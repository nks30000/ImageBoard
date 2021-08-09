package imageboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Jdbc.JdbcUtil;


public class Sequencer {

	 public synchronized static int nextId(Connection conn, String tableName)
			    throws SQLException {
			        PreparedStatement pstmtSelect = null;
			        ResultSet rsSelect = null;
			       
			        PreparedStatement pstmt = null;
			       
			        try {
			            pstmtSelect = conn.prepareStatement(
			            "select MESSAGE_ID from ID_SEQUENCES where TABLE_NAME = ?");
			            pstmtSelect.setString(1, tableName);
			           
			            rsSelect = pstmtSelect.executeQuery();
			           
			            if (rsSelect.next()) {
			                int id = rsSelect.getInt(1);
			                id++;
			               
			                pstmt = conn.prepareStatement(
			                  "update ID_SEQUENCES set MESSAGE_ID = ? "+
			                  "where TABLE_NAME = ?");
			                pstmt.setInt(1, id);
			                pstmt.setString(2, tableName);
			                pstmt.executeUpdate();
			               
			                return id;
			               
			            } else {
			                pstmt = conn.prepareStatement(
			                "insert into ID_SEQUENCES values (?, ?)");
			                pstmt.setString(1, tableName);
			                pstmt.setInt(2, 1);
			                pstmt.executeUpdate();
			               
			                return 1;
			            }
			        } finally {
			        	JdbcUtil.close(rsSelect);
						JdbcUtil.close(pstmt);
			        }
			    }
			}

