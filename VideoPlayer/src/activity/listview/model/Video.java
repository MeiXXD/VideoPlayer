package activity.listview.model;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/16
 * Time: 10:40
 * Description: Video实体类
 */
public class Video {
    //id
    private String id;
    //title=标题， thumbnailUrl=图片地址
    private String title, thumbnailUrl;
    //播放地址
    private String playUrl;
    //点击次数
    private String crt;
    //更新章节
    private String update_course;

    public Video(String id, String title, String thumbnailUrl, String playUrl, String crt, String update_course) {
        this.id = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.playUrl = playUrl;
        this.crt = crt;
        this.update_course = update_course;
    }

    public Video() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getCrt() {
        return crt;
    }

    public void setCrt(String crt) {
        this.crt = crt;
    }

    public String getUpdate_course() {
        return update_course;
    }

    public void setUpdate_course(String update_course) {
        this.update_course = update_course;
    }
}