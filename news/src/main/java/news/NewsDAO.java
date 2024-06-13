package news;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NewsDAO {
	Connection conn = null;
	PreparedStatement pstmt;

	// 데이터베이스 연결을 위한 open 메서드
	public void open() throws SQLException {
		// H2 Database 연결 정보
		String url = "jdbc:h2:tcp://localhost/~/practice1";
		String user = "practice1";
		String password = "1234";

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

	// 뉴스를 추가하는 addNews 메서드
	public void addNews(News news) throws SQLException {
		open();
		try {

			String sql = "INSERT INTO news (title, image, date, content) VALUES (?, ?, CURRENT_TIMESTAMP(), ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, news.getTitle());
			pstmt.setBlob(2, news.getImage());
			pstmt.setString(3, news.getContent());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	// 뉴스 기사 목록 전체를 가져오는 getAll 메서드
	public List<News> getAll() throws SQLException {
		List<News> newsList = new ArrayList<>();
		String sql = "SELECT * FROM news";
		try (Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
			while (resultSet.next()) {
				News news = new News();
				news.setId(resultSet.getInt("id"));
				news.setTitle(resultSet.getString("title"));
				news.setImage(resultSet.getBlob("image"));
				news.setDate(resultSet.getTimestamp("date"));
				news.setContent(resultSet.getString("content"));

				newsList.add(news);
			}
		}
		return newsList;
	}

	// 특정 뉴스를 가져오는 getNews 메서드
	public News getNews(int id) throws SQLException {
		String sql = "SELECT * FROM news WHERE id = ?";
		try (PreparedStatement statement = conn.prepareStatement(sql)) {
			statement.setInt(1, id);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					News news = new News();
					news.setId(resultSet.getInt("id"));
					news.setTitle(resultSet.getString("title"));
					news.setImage(resultSet.getBlob("image"));
					news.setDate(resultSet.getTimestamp("Date"));
					news.setContent(resultSet.getString("content"));
					return news;
				} else {
					throw new SQLException("News with ID " + id + " not found");
				}
			}
		}
	}

	// 뉴스 삭제를 위한 delNews 메서드
	public void delNews(int id) throws SQLException {
		String sql = "DELETE FROM news WHERE id = ?";
		try (PreparedStatement statement = conn.prepareStatement(sql)) {
			statement.setInt(1, id);
			int rowsAffected = statement.executeUpdate();
			if (rowsAffected == 0) {
				throw new SQLException("News with ID " + id + " not found");
			}
		}
	}

	public byte[] getImageById(int id) throws SQLException {
	    byte[] img = null;
	    String sql = "SELECT image FROM news WHERE id = ?";
	    ResultSet rs = null;
	    try {
	        open();
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setInt(1, id);
	        rs = pstmt.executeQuery();
	        if (rs.next()) {
	            Blob image = rs.getBlob("image");
	            img = image.getBytes(1, (int) image.length());
	            image.free();
	        } else {
	            throw new SQLException("No image found with id " + id);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        if (rs != null) {
	            rs.close();
	        }
	        if (pstmt != null) {
	            pstmt.close();
	        }
	        close();
	    }
	    return img;
	}

}
