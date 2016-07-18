package com.os.operando.datasaver.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.os.operando.datasaver.sample.databinding.ActivityMainBinding;

import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED;
import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED;
import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_WHITELISTED;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DataSaverChangedBroadcastReceiver dataSaverChangedBroadcastReceiver = new DataSaverChangedBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Checks if the device is on a metered network
        if (connectivityManager.isActiveNetworkMetered()) {
            // Checks user’s Data Saver settings.
            switch (connectivityManager.getRestrictBackgroundStatus()) {
                case RESTRICT_BACKGROUND_STATUS_ENABLED:
                    // Background data usage is blocked for this app. Wherever possible,
                    // the app should also use less data in the foreground.
                    binding.textView.setText("Enabled Data Saver.");
                    break;
                case RESTRICT_BACKGROUND_STATUS_WHITELISTED:
                    // The app is whitelisted. Wherever possible,
                    // the app should use less data in the foreground and background.
                    binding.textView.setText("The app is whitelisted.");
                    break;
                case RESTRICT_BACKGROUND_STATUS_DISABLED:
                    // Data Saver is disabled. Since the device is connected to a
                    // metered network, the app should use less data wherever possible.
                    binding.textView.setText("Disabled Data Saver.");
                    break;
            }
        } else {
            // The device is not on a metered network.
            // Use data as required to perform syncs, downloads, and updates.
            binding.textView.setText("The device is not on a metered network.");
        }

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        });

        registerReceiver(dataSaverChangedBroadcastReceiver, new IntentFilter(ConnectivityManager.ACTION_RESTRICT_BACKGROUND_CHANGED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dataSaverChangedBroadcastReceiver);
    }

    private static class DataSaverChangedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Log.d("onReceive", "DataSaverChangedBroadcastReceiver");
            Log.d("onReceive", "getRestrictBackgroundStatus : " + connectivityManager.getRestrictBackgroundStatus());
        }
    }
}
