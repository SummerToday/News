package News;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;

public class NewsDAO {
	Connection conn = null;
	PreparedStatement pstmt;

	// 데이터베이스 연결을 위한 open 메서드
	public void open() {
		// H2 Database 연결 정보
		String url = "jdbc:h2:tcp://localhost/~/news";
		String user = "news";
		String password = "123";

		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
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
	// 뉴스를 추가하는 메소드
    public void addNews(News news) throws SQLException {
        String sql = "INSERT INTO news (title, image, content, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, news.getTitle());
            pstmt.setBytes(2, news.getImage());
            pstmt.setString(3, news.getContent());
            pstmt.executeUpdate();
        }
    }

 // 뉴스 기사 목록 전체를 가지고 오기 위한 메소드
    public List<News> getAll() throws SQLException {
        List<News> newsList = new ArrayList<>();
        String sql = "SELECT id, title, image, content, created_at FROM news";
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                News news = new News();
                news.setId(rs.getInt("id"));
                news.setTitle(rs.getString("title"));
                news.setImage(rs.getBytes("image"));
                news.setContent(rs.getString("content"));
                news.setCreatedAt(rs.getTimestamp("created_at"));
                newsList.add(news);
            }
        }
        return newsList;
    }

    // 뉴스 목록에서 뉴스를 선택했을 때, 특정 뉴스 기사의 세부 내용을 보여주는 메소드
    public News getNews(int id) throws SQLException {
        String sql = "SELECT id, title, image, content, created_at FROM news WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    News news = new News();
                    news.setId(rs.getInt("id"));
                    news.setTitle(rs.getString("title"));
                    news.setImage(rs.getBytes("image"));
                    news.setContent(rs.getString("content"));
                    news.setCreatedAt(rs.getTimestamp("created_at"));
                    return news;
                } else {
                    throw new SQLException("News with id " + id + " not found");
                }
            }
        }
    }

    // 삭제할 뉴스의 id를 받아 뉴스를 삭제하는 메소드
    public void delNews(int id) throws SQLException {
        String sql = "DELETE FROM news WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("News with id " + id + " not found");
            }
        }
    }
    
    public byte[] getImageById(int id) throws SQLException {
        String sql = "SELECT image FROM news WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("image");
                } else {
                    throw new SQLException("Image not found for id: " + id);
                }
            }
        }
    }

}

