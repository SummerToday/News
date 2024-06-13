package news;


import java.sql.Blob;
import java.sql.Timestamp;

public class News {
    private int id;
    private String title;
    private Blob image;
    private Timestamp date;
    private String content;

    // 생성자
    public News() {
    }

    public News(String title, Blob image, Timestamp date, String content) {
        this.title = title;
        this.image = image;
        this.date = date;
        this.content = content;
    }

	// Getter 및 Setter 메서드
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // 객체를 문자열로 표현하기 위한 toString() 메서드
    @Override
    public String toString() {
        return "News [id=" + id + ", title=" + title + ", image=" + image + ", date=" + date + ", content=" + content
                + "]";
    }
}
