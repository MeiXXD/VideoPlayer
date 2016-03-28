package activity.listview.adater;

import activity.listview.app.AppController;
import activity.listview.model.Video;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import edu.iss.videoplayer.R;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/16
 * Time: 10:36
 * Description: item布局数据加载
 */
public class CustomListAdapter extends BaseAdapter {
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private Activity activity;
    private LayoutInflater inflater;
    private List<Video> videoItems;

    public CustomListAdapter(Activity activity, List<Video> movieItems) {
        this.activity = activity;
        this.videoItems = movieItems;
    }

    @Override
    public int getCount() {
        return videoItems.size();
    }

    @Override
    public Object getItem(int location) {
        return videoItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView crt = (TextView) convertView.findViewById(R.id.crt);
        TextView update_course = (TextView) convertView.findViewById(R.id.update_course);

        // getting video data for the row
        Video m = videoItems.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);

        // title
        title.setText(m.getTitle());

        // crt
        crt.setText("热度: " + m.getCrt());

        //update_course
        update_course.setText(m.getUpdate_course());
        return convertView;
    }
}