package id.wahyu.teststickearn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.wahyu.teststickearn.model.GithubUser;
import id.wahyu.teststickearn.utility.ApiInterface;
import id.wahyu.teststickearn.utility.ConnectivityException;
import id.wahyu.teststickearn.utility.DownloadImageTask;
import id.wahyu.teststickearn.utility.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {
    private String uName;

    private ApiInterface apiInterface;
    private ProgressDialog loadingDialog;

    private Gson gson = new Gson();
    private GithubUser githubUser = new GithubUser();

    private Button btn_repos;
    private TextView text_view_name, text_view_user, text_view_blog, text_view_loc,
            text_view_create, text_view_update;
    private CircleImageView avatar_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        apiInterface = RetrofitClient.getClient().create(ApiInterface.class);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getString(R.string.message_loading));
        loadingDialog.setCancelable(false);

        btn_repos = (Button)findViewById(R.id.btn_repo);
        avatar_image = (CircleImageView)findViewById(R.id.github_image);

        text_view_name = (TextView)findViewById(R.id.github_name);
        text_view_user = (TextView)findViewById(R.id.github_user);
        text_view_blog = (TextView)findViewById(R.id.github_blog);
        text_view_loc = (TextView)findViewById(R.id.github_location);
        text_view_create = (TextView)findViewById(R.id.repo_create_date);
        text_view_update = (TextView)findViewById(R.id.repo_update_date);

        uName = getIntent().getStringExtra(getString(R.string.github_username));
        Log.d("User Activity", uName);

        getUser(uName);

        btn_repos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserActivity.this, RepoActivity.class);
                i.putExtra(getString(R.string.github_username), uName);
                startActivity(i);
                Log.d("button", "clicked");
            }
        });
    }

    public void bindView(GithubUser githubUser){
        new DownloadImageTask(avatar_image).execute(githubUser.getAvatar_url());
        text_view_name.setText(githubUser.getName());
        text_view_user.setText(githubUser.getLogin());
        text_view_blog.setText(getString(R.string.github_blog)+" "+githubUser.getBlog());
        text_view_loc.setText(getString(R.string.github_location)+" "+githubUser.getLocation());
        text_view_create.setText(getString(R.string.github_created)+" "+githubUser.getCreated_at());
        text_view_update.setText(getString(R.string.github_update)+" "+githubUser.getUpdated_at());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void getUser(String uname) {
        loadingDialog.show();
        final String TAG = "Get User";

        Call<GithubUser> user = apiInterface.getGitHubUserByUname(uname);
        user.enqueue(new Callback<GithubUser>() {
            @Override
            public void onResponse(Call<GithubUser> call, Response<GithubUser> response) {
                if (response.isSuccessful()) {
                    githubUser = response.body();

                    Log.d(TAG, gson.toJson(githubUser.getId()));
                    bindView(githubUser);

                } else if (response.code() == 404) {
                    try {
                        Log.d(TAG, gson.toJson(response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(UserActivity.this, getString(R.string.message_other_retrofit_exception), Toast.LENGTH_SHORT).show();
                }

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<GithubUser> call, Throwable t) {
                // When No Internet by intercept
                if (t instanceof ConnectivityException) {
                    Toast.makeText(UserActivity.this, getString(R.string.message_connectivity_exception), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserActivity.this, getString(R.string.message_other_retrofit_exception), Toast.LENGTH_SHORT).show();
                }

                loadingDialog.dismiss();
            }
        });
    }
}
