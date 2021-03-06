package edu.iss.videoplayer.utils;

import activity.listview.model.Category;
import activity.listview.model.Chapter;
import activity.listview.model.Comment;
import activity.listview.model.Video;
import edu.iss.videoplayer.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/28
 * Time: 17:22
 * Description: JSON解析工具类
 */
public class JsonUtils {
    //json视频数据解析
    public static final boolean JSONObjectTOVideoList(JSONObject jsonObject, ArrayList<Video> list) {
        list.clear();
        boolean isEnd = false;
        try {
            if (jsonObject.getString("flag").equalsIgnoreCase("Success")) {
                JSONArray array = jsonObject.getJSONObject("data").getJSONArray("course");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    Video video = new Video();
                    video.setId(object.getString("id"));
                    video.setThumbnailUrl(Constants.SERVER + object.getString("image"));
                    video.setPlayUrl(object.getString("link"));
                    video.setTitle(object.getString("title"));
                    video.setUpdate_course(object.getString("update_course"));
                    video.setCrt(object.getString("crt"));
                    list.add(video);
                }
                isEnd = jsonObject.getJSONObject("data").getBoolean("last");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return isEnd;
    }

    //json解析章节信息
    public static final void JSONObjectTOChapterList(JSONObject jsonObject, ArrayList<Chapter> list) {
        list.clear();
        try {
            if (jsonObject.getString("flag").equalsIgnoreCase("Success")) {
                JSONArray array = jsonObject.getJSONObject("data").getJSONArray("chart_section");
                // TODO: 16/3/29 空判断
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    JSONArray jsonArray = object.getJSONArray("section");
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject temp = jsonArray.getJSONObject(j);
                        Chapter chapter = new Chapter();
                        chapter.setTitle(temp.getString("title"));
                        chapter.setId(temp.getString("id"));
                        chapter.setPlayUrl(temp.getString("link"));
                        chapter.setLength(temp.getString("time"));
                        chapter.setStartTime(temp.getString("start_at"));
                        list.add(chapter);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //json解析评论信息
    public static final void JSONObjectTOCommentList(JSONObject jsonObject, ArrayList<Comment> list) {
        list.clear();
        try {
            if (jsonObject.getString("flag").equalsIgnoreCase("Success")) {
                JSONArray array = jsonObject.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONArray temp = array.getJSONArray(i);
                    for (int j = 0; j < temp.length(); j++) {
                        JSONObject object = temp.getJSONObject(j);
                        Comment comment = new Comment();
                        comment.setComment(object.getString("comments"));
                        comment.setUserName(object.getString("username") + ":");
                        list.add(comment);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //相关课程的解析
    public static final void JSONObjectTORelatedCourseList(JSONObject jsonObject, ArrayList<Video> relatedVideoList) {
        relatedVideoList.clear();
        try {
            if (jsonObject.getString("flag").equalsIgnoreCase("Success")) {
                JSONArray array = jsonObject.getJSONObject("data").getJSONArray("relate_course");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    Video video = new Video();
                    video.setId(object.getString("id"));
                    video.setThumbnailUrl(Constants.SERVER + object.getString("image"));
                    video.setPlayUrl(object.getString("link"));
                    video.setTitle(object.getString("title"));
                    video.setUpdate_course(object.getString("update_course"));
                    video.setCrt(object.getString("crt"));
                    relatedVideoList.add(video);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //分类列表的解析
    public static final void JSONObjectTOCategoriesList(JSONObject jsonObject, ArrayList<Category> categoryArrayList) {
        try {
            if (jsonObject.getString("flag").equalsIgnoreCase("Success")) {
                JSONArray array = jsonObject.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    Category category = new Category();
                    category.setId(object.getString("id"));
                    category.setName(object.getString("title"));
                    categoryArrayList.add(category);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分类和搜索json结果解析
     *
     * @param jsonObject
     * @param list
     */
    public static final void JSONObjectTOCategoryResultList(JSONObject jsonObject, ArrayList<Video> list) {
        try {
            if (jsonObject.getString("flag").equalsIgnoreCase("Success")) {
                JSONArray array = jsonObject.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    Video video = new Video();
                    video.setId(object.getString("id"));
                    video.setThumbnailUrl(Constants.SERVER + object.getString("image"));
                    video.setPlayUrl(object.getString("link"));
                    video.setTitle(object.getString("title"));
                    video.setUpdate_course(object.getString("update_course"));
                    video.setCrt(object.getString("crt"));
                    list.add(video);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
