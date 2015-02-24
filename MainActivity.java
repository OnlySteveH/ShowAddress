package bigdogconsultants.co.uk.showaddress;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends FragmentActivity implements
GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    GoogleApiClient mLocationClient;
    private TextView addressLabel;
    private TextView locationLabel;
    private Button getLocationBtn;
    private Button disconnectBtn;
    private Button connectBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();

        locationLabel = (TextView) findViewById(R.id.locationLabel);
        addressLabel = (TextView) findViewById(R.id.addressLabel);

        getLocationBtn = (Button) findViewById(R.id.getLocationBtn);
        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCurrentLocation();
            }
        });

        disconnectBtn = (Button) findViewById(R.id.disconnectBtn);
        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationClient.disconnect();
                locationLabel.setText("Got disconnected ... ");
            }
        });

        connectBtn = (Button) findViewById(R.id.connectBtn);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationClient.connect();
                locationLabel.setText("Got connected ... ");
            }
        });

    }

    public void displayCurrentLocation() {
        // Get the current location's latitude & longitude
        //Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);

        if(currentLocation == null){
            Log.d(TAG, "currentLocation is null");
        } else {
            String msg = "Current Location: " +
                    Double.toString(currentLocation.getLatitude()) + "," +
                    Double.toString(currentLocation.getLongitude());

            // Display the current location in the UI
            locationLabel.setText(msg);
        }
        // To display the current address in the UI
        (new GetAddressTask(this)).execute(currentLocation);
    }

    protected synchronized void buildGoogleApiClient() {
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private class GetAddressTask extends AsyncTask<Location, Void, String> {
        Context mContext;
        public GetAddressTask(Context context) {
            super();
            mContext = context;
        }
        protected void onPostExecute(String address) {
            // Display the current address in the UI
            addressLabel.setText(address);
        }
        @Override
        protected String doInBackground(Location... params) {
            Geocoder geocoder =
                    new Geocoder(mContext, Locale.getDefault());
            // Get the current location from the input parameter list
            Location loc = params[0];
            // Create a list to contain the result address
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
            } catch (IOException e1) {
                Log.e("LocationSampleActivity",
                        "IO Exception in getFromLocation()");
                e1.printStackTrace();
                return ("IO Exception trying to get address");
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                String errorString = "Illegal arguments " +
                        Double.toString(loc.getLatitude()) +
                        " , " +
                        Double.toString(loc.getLongitude()) +
                        " passed to address service";
                Log.e("LocationSampleActivity", errorString);
                e2.printStackTrace();
                return errorString;
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                // Get the first address
                Address address = addresses.get(0);
            /*
            * Format the first line of address (if available),
            * city, and country name.
            */
                String addressText = String.format(
                        "%s, %s, %s",
                        // If there's a street address, add it
                        address.getMaxAddressLineIndex() > 0 ?
                                address.getAddressLine(0) : "",
                        // Locality is usually a city
                        address.getLocality(),
                        // The country of the address
                        address.getCountryName());
                // Return the text
                return addressText;
            } else {
                return "No address found";
            }
        }
    }// AsyncTask class


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
