package com.minhld.job2p.jobs;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

/**
 * Created by minhld on 11/2/2015.
 */
public class Job {
    String result = "";

    public Object exec(Object input) {
        Context c = (Context) input;
        LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);

        // if GPS is disable, suggest to open the GPS dialog to enable it
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            return "GPS disabled";
        }

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // when location data is available
                result = location.getLatitude() + ", " + location.getLongitude();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        try {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, Looper.getMainLooper());
        } catch (SecurityException e) {
            return "exception: " + e.getMessage();
        }

        // check if value is available or not
        while (true) {
            if (!result.equals("")) {
                return result;
            }

            // wait for a few milliseconds
            try {
                Thread.sleep(100);
            } catch(Exception e) { }
        }

    }
}
