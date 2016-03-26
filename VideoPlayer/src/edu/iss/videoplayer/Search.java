package edu.iss.videoplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/15
 * Time: 10:18
 * Description: 搜索界面
 */
@ContentView(R.layout.search)
public class Search extends RoboActivity {
    @InjectView(R.id.back)
    private ImageButton back;
    @InjectView(R.id.search)
    private ImageButton search;
    @InjectView(R.id.search_edit_text)
    private EditText searchedittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        back.setOnClickListener(new BackOnClickListener());
        search.setOnClickListener(new SearchOnClickListener());
    }

    private class BackOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class SearchOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
        }
    }
}
