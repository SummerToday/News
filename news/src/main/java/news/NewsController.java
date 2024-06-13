package news;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.beanutils.BeanUtils;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/NewsController")
@MultipartConfig
public class NewsController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private NewsDAO newsDAO;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		newsDAO = new NewsDAO();
	}

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		if (action == null) {
			try {
				byte[] imageBytes = newsDAO.getImageById(Integer.parseInt(request.getParameter("uid")));
				response.setContentType("image/jpeg");
				OutputStream ouputStream = response.getOutputStream();
				ouputStream.write(imageBytes);
				ouputStream.close();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			switch (action) {
			case "addNews":
				addNews(request, response);
				break;
			case "getAll":
				getAll(request, response);
				break;
			case "getNews":
				getNews(request, response);
				break;
			case "delNews":
				delNews(request, response);
				break;
			default:
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action parameter");
			}
		}
	}

	private void addNews(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		News news = new News();
		// HTML 폼으로부터 입력된 데이터를 가져옴
		try {
			BeanUtils.populate(news, request.getParameterMap());
			Part imagePart = request.getPart("image");
			byte[] imageData = new byte[4096];
			if (imagePart != null && imagePart.getSize() > 0) {
				// 이미지를 업로드하는 경우 처리
				InputStream inputStream = imagePart.getInputStream();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[4096];
				int bytesRead = -1;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				imageData = outputStream.toByteArray();
				Blob image = new SerialBlob(imageData);
				news.setImage(image);

				inputStream.close();
				outputStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			newsDAO.open();
			newsDAO.addNews(news);
			// 뉴스 등록 후 목록 페이지로 이동
			getAll(request, response);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to add news");
		} finally {
			try {
				newsDAO.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void getAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			newsDAO.open();
			List<News> newsList = newsDAO.getAll();
			request.setAttribute("newsList", newsList);
			request.getRequestDispatcher("newsList.jsp").forward(request, response);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to get news list");
		} finally {
			try {
				newsDAO.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void getNews(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int id = Integer.parseInt(request.getParameter("id"));
		try {
			newsDAO.open();
			News news = newsDAO.getNews(id);
			request.setAttribute("news", news);
			request.getRequestDispatcher("newsView.jsp").forward(request, response);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to get news");
		} 
	}

	private void delNews(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int id = Integer.parseInt(request.getParameter("id"));
		try {
			newsDAO.open();
			newsDAO.delNews(id);
			// 뉴스 삭제 후 목록 페이지로 이동
			getAll(request, response);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete news");
		} finally {
			try {
				newsDAO.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
