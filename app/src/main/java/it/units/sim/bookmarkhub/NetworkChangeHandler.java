package it.units.sim.bookmarkhub;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeHandler extends BroadcastReceiver {
    private final AlertDialog alert;

    public NetworkChangeHandler(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.no_internet_connection))
                .setMessage(context.getString(R.string.no_internet_connection_msg))
                .setCancelable(false);
        alert = builder.create();
    }

    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (alert != null && alert.isShowing()) {
                alert.dismiss();
            }
        } else {
            alert.show();
        }
    }

}
