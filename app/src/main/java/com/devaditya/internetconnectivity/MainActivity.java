package com.devaditya.internetconnectivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ConnectionLiveData networkConnection = new ConnectionLiveData(getApplicationContext());
    networkConnection.observe(this, new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean aBoolean) {
            if (aBoolean){
                Toast.makeText(MainActivity.this, "Online",Toast.LENGTH_LONG).show();
                Log.i("TAG", "onChanged: TRUE");
            }else {
                Toast.makeText(MainActivity.this, "Offline",Toast.LENGTH_LONG).show();
                Log.i("TAG", "onChanged: FALSE");
            }
        }
    });


    }
}