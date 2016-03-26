package activity.listview.model;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/16
 * Time: 10:40
 * Description: 数据实体类
 */
public class Movie {
    //title=标题， thumbnailUrl=图片地址
    private String title, thumbnailUrl;
    //播放地址
    private String playUrl;
    //年份
    private int year;
    //评分
    private double rating;
    //类别
    private ArrayList<String> genre;

    public Movie() {
    }

    public Movie(String name, String thumbnailUrl, int year, double rating, String playUrl,
                 ArrayList<String> genre) {
        this.title = name;
        this.thumbnailUrl = thumbnailUrl;
        this.playUrl = playUrl;
        this.year = year;
        this.rating = rating;
        this.genre = genre;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public ArrayList<String> getGenre() {
        return genre;
    }

    public void setGenre(ArrayList<String> genre) {
        this.genre = genre;
    }
}
