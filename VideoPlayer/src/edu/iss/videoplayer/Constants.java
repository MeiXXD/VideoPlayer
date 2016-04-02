package edu.iss.videoplayer;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/28
 * Time: 10:04
 * Description: 常量类
 */
public class Constants {
    //语音笔记目录
    public static final String VOICE_NOTE_DIRECTORY = "/TestRecord";
    //服务器地址
    public static final String SERVER = "http://121.40.186.28:8089/mediaResource/";
    //public static final String SERVER = "http://192.168.11.41/mediaResource/";

    //轮播地址
    public static final String FLASHVIEW = "app/index/getCarousel";
    //首页视频列表地址
    public static final String VIDEOLISTVIEW = "app/index/getCourse/page/";
    //直播视频列表地址

    //视频详情章节地址
    public static final String CHAPTERS = "app/index/getCourseIndex/id/";
    //获取评论
    public static final String COMMENTS = "app/index/getComments/id/";
    //笔记提交地址
    public static final String COMMITNOTES = "app/index/getnote";
    //上传接口
    public static final String UPLOADAMRFILE = "app/index/uploader";
    //视频详情,相关课程接口
    public static final String DETAILS = "app/index/getCourseDetails/id/";
}
