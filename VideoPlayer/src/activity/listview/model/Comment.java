package activity.listview.model;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/4/2
 * Time: 12:21
 * Description: 评论实体类
 */
public class Comment {
    private String comment;
    private String userName;

    public Comment() {
    }

    public Comment(String comment, String userName) {
        this.comment = comment;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
