package id.wahyu.teststickearn.viewadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import id.wahyu.teststickearn.R;
import id.wahyu.teststickearn.model.GithubRepo;

/**
 * Created by 0426591017 on 5/17/2018.
 */

public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private List<GithubRepo> githubRepos;
    private TextView text_view_reponame;
    private TextView text_view_repodes;

    public GridViewAdapter(Context context, List<GithubRepo> githubRepos) {
        this.context = context;
        this.githubRepos = githubRepos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.custom_grid, parent,false);
        }

        GithubRepo currentItem = (GithubRepo) getItem(position);

        text_view_reponame = (TextView)convertView.findViewById(R.id.repo_name);
//        text_view_repodes = (TextView)convertView.findViewById(R.id.repo_desc);

        text_view_reponame.setText(currentItem.getName());
//        text_view_repodes.setText(currentItem.getDescription());

        return convertView;
    }

    @Override
    public int getCount() {
        return githubRepos.size();
    }

    @Override
    public Object getItem(int position) {
        return githubRepos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
