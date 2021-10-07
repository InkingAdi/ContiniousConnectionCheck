package com.devaditya.internetconnectivity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class ConnectionLiveData extends LiveData<Boolean> {

    private Context context;
    private ConnectivityManager.NetworkCallback networkCallback;
    private ConnectivityManager connectivityManager;

    public ConnectionLiveData(Context context) {
        this.context = context;
        connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onActive() {
        super.onActive();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(getNetworkCallback());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            lollipopNetworkRequest();
        } else {
            context.registerReceiver(
                    broadcastReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            );
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                connectivityManager.unregisterNetworkCallback(getNetworkCallback());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        } else {
            context.unregisterReceiver(broadcastReceiver);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void lollipopNetworkRequest() {
        NetworkRequest.Builder requestBuilder = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET);

        connectivityManager.registerNetworkCallback(requestBuilder.build(), getNetworkCallback());
    }
    private ConnectivityManager.NetworkCallback getNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    postValue(false);
                }

                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    postValue(true);
                }
            };
            return networkCallback;
        } else {
            throw  new IllegalAccessError("Error");
        }
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateConnection();
        }
    };

    private void updateConnection() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        postValue(activeNetwork.isConnected());
    }

}
