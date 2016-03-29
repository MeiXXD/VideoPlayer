package activity.listview.adater;

import activity.listview.model.Chapter;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.iss.videoplayer.R;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/29
 * Time: 16:10
 * Description: 章节适配器
 */
public class ChapterListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Chapter> chapterItems;

    public ChapterListAdapter(Activity activity, List<Chapter> chapterItems) {
        this.chapterItems = chapterItems;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return chapterItems.size();
    }

    @Override
    public Object getItem(int position) {
        return chapterItems.get(position);
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
            convertView = inflater.inflate(R.layout.chapterlist_row, null);
        TextView title = (TextView) convertView.findViewById(R.id.chapter_title);
        TextView length = (TextView) convertView.findViewById(R.id.chapter_length);

        Chapter chapter = chapterItems.get(position);
        title.setText(chapter.getTitle());
        length.setText("时长:" + chapter.getLength());

        return convertView;
    }
}
