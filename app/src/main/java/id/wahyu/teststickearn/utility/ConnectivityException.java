package id.wahyu.teststickearn.utility;

import java.io.IOException;

import id.wahyu.teststickearn.R;

/**
 * Created by 0426591017 on 5/16/2018.
 */

public class ConnectivityException extends IOException {
    @Override
    public String getMessage() {
        return String.valueOf(R.string.internet_connectivity_exception);
    }
}
