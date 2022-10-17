package CSE218;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


import org.w3c.dom.Text;

import java.util.Random;

import CSE218.wearapp2.R;

public class MainActivity extends WearableActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private WifiManager wifiManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView mTextView = (TextView) findViewById(R.id.text);
        ImageButton ib = (ImageButton) findViewById(R.id.myButton);
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ib.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TextView mainTextView = (TextView) findViewById(R.id.mainText);
                mainTextView.setText("Updating...");
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    getCurrentLocation();
                }
            }
        });
        setAmbientEnabled();
    }

    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestlocIndex = locationResult.getLocations().size() - 1;
                            double lati = locationResult.getLocations().get(latestlocIndex).getLatitude();
                            double longi = locationResult.getLocations().get(latestlocIndex).getLongitude();
                            Log.v("location", String.format("Latitude : %s\n Longitude: %s", lati, longi));

                            TextView mTextView = (TextView) findViewById(R.id.text);
                            TextView mTextView2 = (TextView) findViewById(R.id.text3);
                            mTextView.setText("" + lati + "");
                            mTextView2.setText("" + longi + "");
                            TextView mainTextView = (TextView) findViewById(R.id.mainText);
                            mainTextView.setText("Click to update");

                            int rssi = wifiManager.getConnectionInfo().getRssi();
                            int level = WifiManager.calculateSignalLevel(rssi,5);
                            String ssid = wifiManager.getConnectionInfo().getSSID();
                            String MacAddr = wifiManager.getConnectionInfo().getMacAddress();

                            Log.v("location", "updated");
                            TextView wifiTextView = (TextView) findViewById(R.id.mainText2);
                            wifiTextView.setText(rssi + "dBm level " + level +"\n ssid " + ssid);
                            Log.v("wifi", "rssi " + rssi + " level " + level);

                        }
                    }
                }, Looper.getMainLooper());
        Log.v("debugmessage->", "Comes here");
    }

}
