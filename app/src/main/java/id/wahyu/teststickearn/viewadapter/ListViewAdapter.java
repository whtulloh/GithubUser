package id.wahyu.teststickearn.viewadapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import id.wahyu.teststickearn.R;
import id.wahyu.teststickearn.model.GithubUser;
import id.wahyu.teststickearn.utility.DownloadImageTask;

/**
 * Created by 0426591017 on 5/17/2018.
 */

public class ListViewAdapter extends BaseAdapter {

    private Context context;
    private List<GithubUser> githubUsers;
    private ArrayList<GithubUser> githubUserList = new ArrayList<GithubUser>();
    private TextView text_view_userid;
    private TextView text_view_username;
    private CircleImageView image_view_github;

    public ListViewAdapter(Context context, List<GithubUser> githubUsers) {
        this.context = context;
        this.githubUsers = githubUsers;
        this.githubUserList.addAll(githubUsers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.custom_list, parent,false);
        }

        GithubUser currentItem = (GithubUser) getItem(position);

        image_view_github = (CircleImageView)convertView.findViewById(R.id.github_image);
        text_view_userid = (TextView)convertView.findViewById(R.id.github_userid);
        text_view_username = (TextView)convertView.findViewById(R.id.github_name);

        new DownloadImageTask(image_view_github).execute(currentItem.getAvatar_url());
        text_view_userid.setText(context.getString(R.string.github_id)+currentItem.getId());
        text_view_username.setText(currentItem.getLogin());

        return convertView;
    }

    // Filter Class
    public void filter(String charText) {
        Log.d("content", charText);
        charText = charText.toLowerCase(Locale.getDefault());
        githubUsers.clear();
        if (charText.length() == 0) {
            githubUsers.addAll(githubUserList);

        } else {
            for (GithubUser githubUser : githubUserList) {
                if (githubUser.getLogin().toLowerCase(Locale.getDefault()).contains(charText)) {
                    githubUsers.add(githubUser);
                }else if (githubUser.getId().toLowerCase(Locale.getDefault()).contains(charText)) {
                    githubUsers.add(githubUser);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return githubUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return githubUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
