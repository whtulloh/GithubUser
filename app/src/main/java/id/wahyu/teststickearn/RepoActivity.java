package id.wahyu.teststickearn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import id.wahyu.teststickearn.R;
import id.wahyu.teststickearn.model.GithubRepo;
import id.wahyu.teststickearn.model.GithubUser;
import id.wahyu.teststickearn.utility.ApiInterface;
import id.wahyu.teststickearn.utility.ConnectivityException;
import id.wahyu.teststickearn.utility.RetrofitClient;
import id.wahyu.teststickearn.viewadapter.GridViewAdapter;
import id.wahyu.teststickearn.viewadapter.ListViewAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepoActivity extends AppCompatActivity {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ApiInterface apiInterface;
    private ProgressDialog loadingDialog;
    private String uName;

    private List<GithubRepo> githubRepos;
    private Gson gson = new Gson();
    private GridView gridView;
    private GridViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo);

        apiInterface = RetrofitClient.getClient().create(ApiInterface.class);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getString(R.string.message_loading));
        loadingDialog.setCancelable(false);

        uName = getIntent().getStringExtra(getString(R.string.github_username));
        gridView = (GridView) findViewById(R.id.gridView);
        getRepos(uName);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GithubRepo githubRepo = (GithubRepo) gridView.getAdapter().getItem(position);

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(githubRepo.getHtml_url()));
                startActivity(i);
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRepos(uName);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void bindGridView(GridViewAdapter adapter) {
        gridView.setAdapter(adapter);
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

    public void getRepos(String uname) {
        loadingDialog.show();
        final String TAG = "Get Repos";

        Call<List<GithubRepo>> repo = apiInterface.getGithubReposByUser(uname);
        repo.enqueue(new Callback<List<GithubRepo>>() {
            @Override
            public void onResponse(Call<List<GithubRepo>> call, Response<List<GithubRepo>> response) {
                if (response.isSuccessful()) {
                    githubRepos = response.body();

                    for (GithubRepo githubRepo : githubRepos) {
                        Log.d(TAG, gson.toJson(githubRepo.getId()));
                    }

                    adapter = new GridViewAdapter(RepoActivity.this, githubRepos);
                    bindGridView(adapter);

                } else if (response.code() == 404) {
                    try {
                        Log.d(TAG, gson.toJson(response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(RepoActivity.this, getString(R.string.message_other_retrofit_exception), Toast.LENGTH_SHORT).show();
                }

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<GithubRepo>> call, Throwable t) {
                // When No Internet by intercept
                if (t instanceof ConnectivityException) {
                    Toast.makeText(RepoActivity.this, getString(R.string.message_connectivity_exception), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RepoActivity.this, getString(R.string.message_other_retrofit_exception), Toast.LENGTH_SHORT).show();
                }

                loadingDialog.dismiss();
            }
        });
    }
}
