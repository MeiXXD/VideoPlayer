package activity.listview.adater;

import activity.listview.model.Comment;
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
 * Date: 16/4/2
 * Time: 12:25
 * Description: 评论适配器
 */
public class CommentListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Comment> commentItems;

    public CommentListAdapter(Activity activity, List<Comment> commentItems) {
        this.activity = activity;
        this.commentItems = commentItems;
    }

    @Override
    public int getCount() {
        return commentItems.size();
    }

    @Override
    public Object getItem(int position) {
        return commentItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.commentlist_row, null);
        }
        TextView userName = (TextView) convertView.findViewById(R.id.user_name);
        TextView userComment = (TextView) convertView.findViewById(R.id.user_comment);

        Comment comment = commentItems.get(position);
        userName.setText(comment.getUserName());
        userComment.setText(comment.getComment());
        return convertView;
    }
}
