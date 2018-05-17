package id.wahyu.teststickearn;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import id.wahyu.teststickearn.viewadapter.ListViewAdapter;
import id.wahyu.teststickearn.model.GithubUser;
import id.wahyu.teststickearn.utility.ApiInterface;
import id.wahyu.teststickearn.utility.ConnectivityException;
import id.wahyu.teststickearn.utility.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private SearchView searchView;

    private ApiInterface apiInterface;
    private ProgressDialog loadingDialog;

    private List<GithubUser> githubUsers;
    private Gson gson = new Gson();
    private ListViewAdapter adapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiInterface = RetrofitClient.getClient().create(ApiInterface.class);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getString(R.string.message_loading));
        loadingDialog.setCancelable(false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        listView = findViewById(R.id.list);
        getUsers();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GithubUser githubUser = (GithubUser) listView.getAdapter().getItem(position);

                Intent i = new Intent(MainActivity.this, UserActivity.class);
                i.putExtra(getString(R.string.github_username), githubUser.getLogin());
                startActivity(i);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsers();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void bindListView(ListViewAdapter adapter) {
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.app_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onQueryTextSubmit(String query) {
                // when input search submited
                adapter.filter(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String nextText) {
                // when input search changed
                if(!nextText.equals("")) {
                    adapter.filter(nextText);
                }
                return true;
            }
        });
        return true;
    }

    public void getUsers() {
        loadingDialog.show();

        final String TAG = "Get Users";

        Call<List<GithubUser>> users = apiInterface.getGithubUsers();
        users.enqueue(new Callback<List<GithubUser>>() {

            @Override
            public void onResponse(@NonNull Call<List<GithubUser>> call, Response<List<GithubUser>> response) {

                if (response.isSuccessful()) {
                    githubUsers = response.body();

                    for (GithubUser githubUser : githubUsers) {
                        Log.d(TAG, gson.toJson(githubUser.getId()));
                    }

                    adapter = new ListViewAdapter(MainActivity.this, githubUsers);
                    bindListView(adapter);

                } else if (response.code() == 404) {

                    try {
                        Log.d(TAG, gson.toJson(response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else{
                    Toast.makeText(MainActivity.this, getString(R.string.message_other_retrofit_exception), Toast.LENGTH_SHORT).show();
                }

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<List<GithubUser>> call, Throwable t) {
                // When No Internet by intercept
                if (t instanceof ConnectivityException) {
                    Toast.makeText(MainActivity.this, getString(R.string.message_connectivity_exception), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.message_other_retrofit_exception), Toast.LENGTH_SHORT).show();
                }

                loadingDialog.dismiss();
            }
        });
    }
}
