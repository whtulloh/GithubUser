package id.wahyu.teststickearn.utility;

import java.util.List;

import id.wahyu.teststickearn.model.GithubRepo;
import id.wahyu.teststickearn.model.GithubUser;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by 0426591017 on 5/16/2018.
 */

public interface ApiInterface {
    @GET("users")
    Call<List<GithubUser>> getGithubUsers();

    @GET("users/{userName}")
    Call<GithubUser> getGitHubUserByUname(@Path("userName") String userName);

    @GET("users/{userName}/repos")
    Call<List<GithubRepo>> getGithubReposByUser(@Path("userName") String userName);
}
