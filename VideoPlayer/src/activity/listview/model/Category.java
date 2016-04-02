package activity.listview.model;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/4/2
 * Time: 16:55
 * Description: 类别实体类
 */
public class Category {
    private String name;
    private String id;

    public Category(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public Category() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
