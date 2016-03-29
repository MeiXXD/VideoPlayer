package activity.listview.model;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/29
 * Time: 16:14
 * Description: 章节实体类
 */
public class Chapter {
    //id
    private String id;
    //title=标题， thumbnailUrl=图片地址
    private String title;
    //播放地址
    private String playUrl;
    //时长
    private String length;
    //开始时间
    private String startTime;

    public Chapter(String id, String title, String playUrl, String length, String startTime) {
        this.id = id;
        this.title = title;
        this.playUrl = playUrl;
        this.length = length;
        this.startTime = startTime;
    }

    public Chapter() {
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
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

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }
}
