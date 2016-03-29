package activity.flashview;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/28
 * Time: 14:55
 * Description: 轮播实体类
 */
public class FlashViewVideo {
    private String id;
    private String link;
    private String image;

    public FlashViewVideo() {
    }

    public FlashViewVideo(String id, String link, String image) {
        this.id = id;
        this.link = link;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
