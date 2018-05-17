package id.wahyu.teststickearn.utility;

import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 0426591017 on 5/16/2018.
 */

public class InternetConnection implements Interceptor {
    private static String site = "https://api.github.com/users";
    private static Random random = new Random();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Log.d("intercept: ", "INTERCEPT");
        if (!hasActiveInternetConnection()) {
            Log.e("intercept: ", "INTERNET CONN CHECK");
            throw new ConnectivityException();
        }

        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }

    // Check Internet Connection by Ping Site
    public static boolean hasActiveInternetConnection() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Response resp = null;
        int code = 400;

        HttpURLConnection urlc = null;
        try {
            URL url = new URL(site + "?idrandom=" + random.nextInt());
            urlc = (HttpURLConnection) url.openConnection();
            urlc.setRequestProperty("User-Agent", "test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setRequestMethod("GET");
            urlc.setDoInput(true);
            urlc.setReadTimeout(60000);
            urlc.setConnectTimeout(60000); // mTimeout is one minutes
            urlc.connect();
            Log.d("Info", "Response Code : "+urlc.getResponseCode());
            code = urlc.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("warning", "Error checking internet connection", e);
        } finally {
            if(urlc != null){
                try {
                    urlc.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return code == 200;
    }
}
