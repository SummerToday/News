package News;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
	Connection conn = null;
	PreparedStatement pstmt;

	// 데이터베이스 연결을 위한 open 메서드
	public void open() throws SQLException {
		// H2 Database 연결 정보
		String url = "jdbc:h2:tcp://localhost/~/news";
		String user = "news";
		String password = "123";

		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// 데이터베이스 연결을 해제하기 위한 close 메서드
	public void close() throws SQLException {
		try {
			if (pstmt != null && !pstmt.isClosed()) {
				pstmt.close();
			}
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// 회원 가입 (새 사용자 생성)
	public void signup(User user) throws SQLException {
        String sql = "INSERT INTO Users (username, password, nickname, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, user.getUsername());
        pstmt.setString(2, user.getPassword());
        pstmt.setString(3, user.getNickname());
        pstmt.executeUpdate();
    }

    // 로그인 (사용자 인증)
	public User login(String username, String password) throws SQLException {
	    String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
	    pstmt = conn.prepareStatement(sql);
	    pstmt.setString(1, username);
	    pstmt.setString(2, password);
	    ResultSet rs = pstmt.executeQuery();

	    if (rs.next()) {
	        User user = new User();
	        user.setId(rs.getInt("id"));
	        user.setUsername(rs.getString("username"));
	        user.setPassword(rs.getString("password"));
	        user.setNickname(rs.getString("nickname")); // 닉네임 설정
	        user.setCreatedAt(rs.getTimestamp("created_at"));
	        return user;
	    }
	    return null;
	}

}
