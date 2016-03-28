package edu.iss.videoplayer.utils;

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
        boolean isEnd = false;
        try {
            if (jsonObject.getString("flag").equalsIgnoreCase("Success")) {
                JSONArray array = jsonObject.getJSONObject("data").getJSONArray("course");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    Video video = new Video();
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
}