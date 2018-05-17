package id.wahyu.teststickearn.utility;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 0426591017 on 5/16/2018.
 */

public class RetrofitClient {
    public static final String BASE_URL = "https://api.github.com/";
    private static Retrofit retrofit = null;
    private static OkHttpClient client = null;

    public static Retrofit getClient() {
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(new InternetConnection());

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();

        return retrofit;
    }
}
