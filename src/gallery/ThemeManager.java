package gallery;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.IOException; 
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection; 
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;

import connection.ConnectionProvider; 
import sequence.Sequencer; 

public class ThemeManager { 
    
    private static ThemeManager instance = new ThemeManager();
    
    public static ThemeManager getInstance() {
        return instance;
    }
    
    private ThemeManager() {}
    private static String POOLNAME = "pool";
    
    /** 
     * 새로운 글을 삽입한다.
     */ 
    public void insert(Theme theme) throws ThemeManagerException {
        Connection conn = null; 
        // 새로운 글의 그룹 번호를 구할 때 사용된다.
        Statement stmtGroup = null; 
        ResultSet rsGroup = null;
        
        // 특정 글의 답글에 대한 출력 순서를 구할 때 사용된다.
        PreparedStatement pstmtOrder = null;
        ResultSet rsOrder = null;
        PreparedStatement pstmtOrderUpdate = null;
        
        // 글을 삽입할 때 사용된다. 
        PreparedStatement pstmtInsertMessage = null;
        PreparedStatement pstmtInsertContent = null;
        
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);
            
            if (theme.getParentId() == 0) { 
                // 답글이 아닌 경우 그룹번호를 새롭게 구한다.
                stmtGroup = conn.createStatement(); 
                rsGroup = stmtGroup.executeQuery(
                    "select max(GROUP_ID) from THEME_MESSAGE"); 
                int maxGroupId = 0; 
                if (rsGroup.next()) {
                    maxGroupId = rsGroup.getInt(1); 
                }
                maxGroupId++;
                
                theme.setGroupId(maxGroupId);
                theme.setOrderNo(0);
            } else {
                // 특정 글의 답글인 경우,
                // 같은 그룹 번호 내에서의 출력 순서를 구한다.  
                pstmtOrder = conn.prepareStatement( 
                "select max(ORDER_NO) from THEME_MESSAGE "+ 
                "where PARENT_ID = ? or THEME_MESSAGE_ID = ?"); 
                pstmtOrder.setInt(1, theme.getParentId());
                pstmtOrder.setInt(2, theme.getParentId());
                rsOrder = pstmtOrder.executeQuery();
                int maxOrder = 0;
                if (rsOrder.next()) {
                    maxOrder = rsOrder.getInt(1);
                }
                maxOrder ++;
                theme.setOrderNo(maxOrder); 
            }
            
            // 특정 글의 답변 글인 경우 같은 그룹 내에서
            // 순서 번호를 변경한다.
            if (theme.getOrderNo() > 0) {
                pstmtOrderUpdate = conn.prepareStatement(
                "update THEME_MESSAGE set ORDER_NO = ORDER_NO + 1 "+
                "where GROUP_ID = ? and ORDER_NO >= ?");
                pstmtOrderUpdate.setInt(1, theme.getGroupId()); 
                pstmtOrderUpdate.setInt(2, theme.getOrderNo()); 
                pstmtOrderUpdate.executeUpdate();
            }
            // 새로운 글의 번호를 구한다.
            theme.setId(Sequencer.nextId(conn, "THEME_MESSAGE"));
            // 글을 삽입한다.
            pstmtInsertMessage = conn.prepareStatement( 
            "insert into THEME_MESSAGE values (?,?,?,?,?,?,?,?,?,?,?)");
            pstmtInsertMessage.setInt(1, theme.getId());
            pstmtInsertMessage.setInt(2, theme.getGroupId());
            pstmtInsertMessage.setInt(3, theme.getOrderNo());
            pstmtInsertMessage.setInt(4, theme.getLevels()); 
            pstmtInsertMessage.setInt(5, theme.getParentId());
            pstmtInsertMessage.setTimestamp(6, theme.getRegister());
            pstmtInsertMessage.setString(7, theme.getName());
            pstmtInsertMessage.setString(8, theme.getEmail());
            pstmtInsertMessage.setString(9, theme.getImage());
            pstmtInsertMessage.setString(10, theme.getPassword());
            pstmtInsertMessage.setString(11, theme.getTitle()); 
            pstmtInsertMessage.executeUpdate(); 
            
            pstmtInsertContent = conn.prepareStatement( 
            "insert into THEME_CONTENT values (?,?)");
            pstmtInsertContent.setInt(1, theme.getId());
            pstmtInsertContent.setCharacterStream(2, 
                new StringReader(theme.getContent()),
                theme.getContent().length());
            pstmtInsertContent.executeUpdate(); 
            
            conn.commit();
        } catch(SQLException ex) {
            try {
                conn.rollback();
            } catch(SQLException ex1) {}
            
            throw new ThemeManagerException("insert", ex);
        } finally { 
            if (rsGroup != null)
                try { rsGroup.close(); } catch(SQLException ex) {}  
            if (stmtGroup != null)
                try { stmtGroup.close(); } catch(SQLException ex) {} 
            if (rsOrder != null)
                try { rsOrder.close(); } catch(SQLException ex) {}  
            if (pstmtOrder != null) 
                try { pstmtOrder.close(); } catch(SQLException ex) {} 
            if (pstmtOrderUpdate != null)
                try { pstmtOrderUpdate.close(); } catch(SQLException ex) {} 
            if (pstmtInsertMessage!= null)
                try { pstmtInsertMessage.close(); } catch(SQLException ex) {} 
            if (pstmtInsertContent != null) 
                try { pstmtInsertContent.close(); } catch(SQLException ex) {} 
            if (conn != null)
                try {
                    conn.setAutoCommit(true); 
                    conn.close(); 
                } catch(SQLException ex) {} 
        }
    }
    
    /** 
     * 제목과 내용만 변경한다.
     */ 
    public void update(Theme theme) throws ThemeManagerException {
        Connection conn = null; 
        PreparedStatement pstmtUpdateMessage = null;
        PreparedStatement pstmtUpdateContent = null;
        
        try {
        	conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);
            
            pstmtUpdateMessage = conn.prepareStatement( 
                "update THEME_MESSAGE set NAME=?,EMAIL=?,IMAGE=?,TITLE=? "+ 
                "where THEME_MESSAGE_ID=?");
            pstmtUpdateContent = conn.prepareStatement( 
                "update THEME_CONTENT set CONTENT=? "+
                "where THEME_MESSAGE_ID=?"); 
            
            pstmtUpdateMessage.setString(1, theme.getName());
            pstmtUpdateMessage.setString(2, theme.getEmail());
            pstmtUpdateMessage.setString(3, theme.getImage());
            pstmtUpdateMessage.setString(4, theme.getTitle());
            pstmtUpdateMessage.setInt(5, theme.getId());
            pstmtUpdateMessage.executeUpdate(); 
            
            pstmtUpdateContent.setCharacterStream(1, 
                new StringReader(theme.getContent()),
                theme.getContent().length());
            pstmtUpdateContent.setInt(2, theme.getId());
            pstmtUpdateContent.executeUpdate(); 
            
            conn.commit();
        } catch(SQLException ex) {
            try {
                conn.rollback();
            } catch(SQLException ex1) {}
            
            throw new ThemeManagerException("update", ex);
        } finally { 
            if (pstmtUpdateMessage != null) 
                try { pstmtUpdateMessage.close(); } catch(SQLException ex) {} 
            if (pstmtUpdateContent != null) 
                try { pstmtUpdateContent.close(); } catch(SQLException ex) {} 
            if (conn != null)
                try {
                    conn.setAutoCommit(true); 
                    conn.close(); 
                } catch(SQLException ex) {} 
        }
    }
    
    /** 
     * 등록된 글의 개수를 구한다.
     */ 
    public int count(List whereCond, Map valueMap) 
    throws ThemeManagerException {
        if (valueMap == null) valueMap = Collections.EMPTY_MAP; 
        
        Connection conn = null; 
        PreparedStatement pstmt = null; 
        ResultSet rs = null;
        
        try {
        	conn = ConnectionProvider.getConnection();
            StringBuffer query = new StringBuffer(200); 
            query.append("select count(*) from THEME_MESSAGE ");
            if (whereCond != null && whereCond.size() > 0) {
                query.append("where "); 
                for (int i = 0 ; i < whereCond.size() ; i++) {
                    query.append(whereCond.get(i)); 
                    if (i < whereCond.size() -1 ) { 
                        query.append(" or ");
                    }
                }
            }
            pstmt = conn.prepareStatement(query.toString());
            
            Iterator keyIter = valueMap.keySet().iterator();
            while(keyIter.hasNext()) {
                Integer key = (Integer)keyIter.next();
                Object obj = valueMap.get(key); 
                if (obj instanceof String) {
                    pstmt.setString(key.intValue(), (String)obj);
                } else if (obj instanceof Integer) {
                    pstmt.setInt(key.intValue(), ((Integer)obj).intValue());
                } else if (obj instanceof Timestamp) {
                    pstmt.setTimestamp(key.intValue(), (Timestamp)obj); 
                }
            }
            
            rs = pstmt.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        } catch(SQLException ex) {
            throw new ThemeManagerException("count", ex);
        } finally { 
            if (rs != null) try { rs.close(); } catch(SQLException ex) {} 
            if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {} 
            if (conn != null) try { conn.close(); } catch(SQLException ex) {} 
        }
    }
    
    /** 
     * 목록을 읽어온다. 
     */ 
    public List selectList(List whereCond, Map valueMap, 
                           int startRow, int endRow)
    throws ThemeManagerException {
        if (valueMap == null) valueMap = Collections.EMPTY_MAP; 
        
        Connection conn = null; 
        PreparedStatement pstmtMessage = null;
        ResultSet rsMessage = null; 
        
        try {
            StringBuffer query = new StringBuffer(200); 
            query.append("select * from (select a.*, rownum b from (select * from THEME_MESSAGE ");
            if (whereCond != null && whereCond.size() > 0) {
                query.append("where "); 
                for (int i = 0 ; i < whereCond.size() ; i++) {
                    query.append(whereCond.get(i)); 
                    if (i < whereCond.size() -1 ) { 
                        query.append(" or ");
                    }
                }
            }
            query.append("order by GROUP_ID desc, ORDER_NO asc) a) where b >= ? and b <= ?");
            
            conn = ConnectionProvider.getConnection();
            
            pstmtMessage = conn.prepareStatement(query.toString()); 
            Iterator keyIter = valueMap.keySet().iterator();
            while(keyIter.hasNext()) {
                Integer key = (Integer)keyIter.next();
                Object obj = valueMap.get(key); 
                if (obj instanceof String) {
                    pstmtMessage.setString(key.intValue(), (String)obj);
                } else if (obj instanceof Integer) {
                    pstmtMessage.setInt(key.intValue(), 
                                        ((Integer)obj).intValue()); 
                } else if (obj instanceof Timestamp) {
                    pstmtMessage.setTimestamp(key.intValue(),
                                             (Timestamp)obj);
                }
            }
            
            pstmtMessage.setInt(valueMap.size()+1, startRow);
            pstmtMessage.setInt(valueMap.size()+2, endRow-startRow+1);
            
            rsMessage = pstmtMessage.executeQuery();
            if (rsMessage.next()) { 
                List list = new java.util.ArrayList(endRow-startRow+1); 
                
                do {
                    Theme theme = new Theme();
                    theme.setId(rsMessage.getInt("THEME_MESSAGE_ID"));
                    theme.setGroupId(rsMessage.getInt("GROUP_ID")); 
                    theme.setOrderNo(rsMessage.getInt("ORDER_NO")); 
                    theme.setLevels(rsMessage.getInt("LEVELS"));
                    theme.setParentId(rsMessage.getInt("PARENT_ID"));
                    theme.setRegister(rsMessage.getTimestamp("REGISTER"));
                    theme.setName(rsMessage.getString("NAME")); 
                    theme.setEmail(rsMessage.getString("EMAIL"));
                    theme.setImage(rsMessage.getString("IMAGE"));
                    theme.setPassword(rsMessage.getString("PASSWORD")); 
                    theme.setTitle(rsMessage.getString("TITLE"));
                    list.add(theme);
                } while(rsMessage.next());
                
                return list;
                
            } else {
                return Collections.EMPTY_LIST;
            }
            
        } catch(SQLException ex) {
            throw new ThemeManagerException("selectList", ex);
        } finally { 
            if (rsMessage != null)  
                try { rsMessage.close(); } catch(SQLException ex) {} 
            if (pstmtMessage != null) 
                try { pstmtMessage.close(); } catch(SQLException ex) {} 
            if (conn != null) try { conn.close(); } catch(SQLException ex) {} 
        }
    }
    
    /** 
     * 지정한 글을 읽어온다.
     */ 
    public Theme select(int id) throws ThemeManagerException {
        Connection conn = null; 
        PreparedStatement pstmtMessage = null;
        ResultSet rsMessage = null; 
        PreparedStatement pstmtContent = null;
        ResultSet rsContent = null; 
        
        try {
            Theme theme = null; 
            
            conn = ConnectionProvider.getConnection();
            pstmtMessage = conn.prepareStatement(
                "select * from THEME_MESSAGE "+
                "where THEME_MESSAGE_ID = ?");
            pstmtMessage.setInt(1, id); 
            rsMessage = pstmtMessage.executeQuery();
            if (rsMessage.next()) { 
                theme = new Theme();
                theme.setId(rsMessage.getInt("THEME_MESSAGE_ID"));
                theme.setGroupId(rsMessage.getInt("GROUP_ID")); 
                theme.setOrderNo(rsMessage.getInt("ORDER_NO")); 
                theme.setLevels(rsMessage.getInt("LEVELS"));
                theme.setParentId(rsMessage.getInt("PARENT_ID"));
                theme.setRegister(rsMessage.getTimestamp("REGISTER"));
                theme.setName(rsMessage.getString("NAME")); 
                theme.setEmail(rsMessage.getString("EMAIL"));
                theme.setImage(rsMessage.getString("IMAGE"));
                theme.setPassword(rsMessage.getString("PASSWORD")); 
                theme.setTitle(rsMessage.getString("TITLE"));

                pstmtContent = conn.prepareStatement(
                    "select CONTENT from THEME_CONTENT "+
                    "where THEME_MESSAGE_ID = ?");
                pstmtContent.setInt(1, id); 
                rsContent = pstmtContent.executeQuery();
                if (rsContent.next()) { 
                    Reader reader = null;
                    try {
                        reader = rsContent.getCharacterStream("CONTENT");
                        char[] buff = new char[512];
                        int len = -1;
                        StringBuffer buffer = new StringBuffer(512);
                        while( (len = reader.read(buff)) != -1) {
                            buffer.append(buff, 0, len);
                        }
                        theme.setContent(buffer.toString());
                    } catch(IOException iex) {
                        throw new ThemeManagerException("select", iex); 
                    } finally { 
                        if (reader != null) 
                            try {
                                reader.close(); 
                            } catch(IOException iex) {} 
                    }           
                } else {
                    return null;
                }
                return theme;
            } else {
                return null;
            }
        } catch(SQLException ex) {
            throw new ThemeManagerException("select", ex);
        } finally { 
            if (rsMessage != null)  
                try { rsMessage.close(); } catch(SQLException ex) {} 
            if (pstmtMessage != null) 
                try { pstmtMessage.close(); } catch(SQLException ex) {} 
            if (rsContent != null)  
                try { rsContent.close(); } catch(SQLException ex) {} 
            if (pstmtContent != null) 
                try { pstmtContent.close(); } catch(SQLException ex) {} 
            if (conn != null) try { conn.close(); } catch(SQLException ex) {} 
        }
    }
    
    public void delete(int id) throws ThemeManagerException {
        Connection conn = null; 
        PreparedStatement pstmtMessage = null;
        PreparedStatement pstmtContent = null;
        
        try {
        	conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);
            
            pstmtMessage = conn.prepareStatement(
                "delete from THEME_MESSAGE where THEME_MESSAGE_ID = ?");
            pstmtContent = conn.prepareStatement(
                "delete from THEME_CONTENT where THEME_MESSAGE_ID = ?");
            
            pstmtMessage.setInt(1, id); 
            pstmtContent.setInt(1, id); 
            
            int updatedCount1 = pstmtMessage.executeUpdate();
            int updatedCount2 = pstmtContent.executeUpdate();
            
            if (updatedCount1 + updatedCount2 == 2) {
                conn.commit();
            } else {
                conn.rollback();
                throw new ThemeManagerException("invalid id:"+id);
            }
        } catch(SQLException ex) {
            try {
                conn.rollback();
            } catch(SQLException ex1) {}
            throw new ThemeManagerException("delete", ex);
        } finally { 
            if (pstmtMessage != null) 
                try { pstmtMessage.close(); } catch(SQLException ex) {} 
            if (pstmtContent != null) 
                try { pstmtContent.close(); } catch(SQLException ex) {} 
            if (conn != null)
                try {
                    conn.setAutoCommit(true);
                    conn.close(); 
                } catch(SQLException ex) {} 
        }
    } 
}
