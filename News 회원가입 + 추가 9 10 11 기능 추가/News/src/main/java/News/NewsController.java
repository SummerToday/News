package News;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@MultipartConfig
public class NewsController extends HttpServlet {
    private UserDAO userDao = new UserDAO();
    private NewsDAO newsDao = new NewsDAO();

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            switch (action) {
                case "signup":
                    signup(request, response);
                    break;
                case "login":
                    login(request, response);
                    break;
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
                case "getImage":
                    getImage(request, response);
                    break;
                case "logout":
                    logout(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (Exception e) {
            throw new ServletException("Database error", e);
        }
    }

    // 로그아웃 메소드
    protected void logout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 세션 정보 삭제
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 로그아웃 메시지를 출력하고 로그인 페이지로 이동
        response.sendRedirect("logout.jsp");
    }


    protected void signup(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);

        try {
            userDao.open();
            userDao.signup(user);
            userDao.close();
        } catch (SQLException e) {
            throw new ServletException("Database error during signup", e);
        }

        response.sendRedirect("login.jsp");
    }


    protected void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");

        User user;
        try {
            userDao.open();
            user = userDao.login(username, password);
            userDao.close();
        } catch (SQLException e) {
            throw new ServletException("Database error during login", e);
        }

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);

            // 방문 횟수 증가 - 사용자별 쿠키 사용
            int visitCount = 1; // 기본값을 1로 설정
            boolean cookieExists = false;
            Cookie[] cookies = request.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("visitCount_" + username)) {
                        visitCount = Integer.parseInt(cookie.getValue()) + 1; // 방문 횟수 증가
                        cookie.setValue(String.valueOf(visitCount));
                        response.addCookie(cookie); // 쿠키 업데이트
                        cookieExists = true;
                        break;
                    }
                }
            }

            if (!cookieExists) {
                // 쿠키가 없는 경우 새로운 쿠키 생성
                Cookie visitCountCookie = new Cookie("visitCount_" + username, String.valueOf(visitCount));
                visitCountCookie.setMaxAge(7 * 24 * 60 * 60); // 7일 동안 유지
                response.addCookie(visitCountCookie);
            }
            
            session.setAttribute("nickname", user.getNickname());
            session.setAttribute("username", username);
            session.setAttribute("loginTime", new Date());
            response.sendRedirect("newsList.jsp");
        } else {
            response.sendRedirect("login.jsp?error=1");
        }
    }


    // 뉴스 기사를 등록하는 메소드
    protected void addNews(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        byte[] imageData = null;
        try {
            Part imagePart = request.getPart("imageFile");
            if (imagePart != null && imagePart.getSize() > 0) {
                try (InputStream inputStream = imagePart.getInputStream();
                     ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    imageData = outputStream.toByteArray();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServletException("Failed to upload image", e);
        }

        News news = new News();
        news.setTitle(title);
        news.setContent(content);
        news.setImage(imageData);

        try {
            newsDao.open();
            newsDao.addNews(news);
            newsDao.close();
        } catch (SQLException e) {
            throw new ServletException("Database error during adding news", e);
        }

        response.sendRedirect("NewsController?action=getAll");
    }

    // 뉴스 목록을 가져와 newsList.jsp로 포워딩하는 메소드
    protected void getAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            newsDao.open();
            List<News> newsList = newsDao.getAll();
            newsDao.close();
            request.setAttribute("newslist", newsList);
            getServletContext().getRequestDispatcher("/newsList.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error during fetching news list", e);
        }
    }

    // 특정 뉴스 기사를 가져와 newsView.jsp로 포워딩하는 메소드
    protected void getNews(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int newsId = Integer.parseInt(request.getParameter("id"));

        try {
            newsDao.open();
            News news = newsDao.getNews(newsId);
            newsDao.close();
            request.setAttribute("news", news);
            getServletContext().getRequestDispatcher("/newsView.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error during fetching news details", e);
        }
    }

    // 특정 뉴스 기사를 삭제하는 메소드
    protected void delNews(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int newsId = Integer.parseInt(request.getParameter("id"));

        try {
            newsDao.open();
            newsDao.delNews(newsId);
            newsDao.close();
        } catch (SQLException e) {
            throw new ServletException("Database error during deleting news", e);
        }

        response.sendRedirect("NewsController?action=getAll");
    }
    
    private void getImage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int newsId = Integer.parseInt(request.getParameter("id"));
        try {
            newsDao.open();
            byte[] imageData = newsDao.getImageById(newsId);
            newsDao.close();

            if (imageData != null) {
                response.setContentType("image/jpeg");
                OutputStream os = response.getOutputStream();
                os.write(imageData);
                os.flush();
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND); // 이미지가 없는 경우
            }
        } catch (SQLException e) {
            throw new ServletException("Database error while retrieving image", e);
        }
    }
}
