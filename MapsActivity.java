package be.retailsonar.retailsonar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.SphericalUtil;
import com.google.android.gms.location.LocationServices;



import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import be.retailsonar.be.retailsonar.objects.AfstandIngevuld;


public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener {

    //Our Map
    private GoogleMap mMap;

    //To store longitude and latitude from map
    private double longitudeFrom;
    private double latitudeFrom;

    private double longitude;
    private double latitude;

    private double longitudeTo;
    private double latitudeTo;
    private int filiaalId;

    private Marker mfrom;
    private Marker mTo;
    //Buttons
    private RadioButton buttonFrom;
    private RadioButton buttonTo;
    private Button buttonCalculate;
    private Bitmap bitmap;


    //Google ApiClient
    private GoogleApiClient googleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initializing googleapi client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        //Initializing views and adding onclick listeners
        buttonTo = findViewById(R.id.buttonSetTo);
        buttonFrom = findViewById(R.id.buttonSetFrom);

        buttonCalculate= findViewById(R.id.buttonCalcDistance);
        buttonTo.setOnClickListener(this);
        buttonFrom.setOnClickListener(this);
        buttonCalculate.setOnClickListener(this);

        filiaalId=((GlobaleVariabelen) getApplication()).getFiliaalid();



    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    private void getCurrentLocation() {
        mMap.clear();

            longitudeFrom = 3.7167;
            latitudeFrom = 51.05;

            //moving the map to location
            moveMap();
        }


    //Getting current location


    //Function to move the map
    private void moveMap() {
        //String to display current latitude and longitude
        String msg = latitudeFrom + ", "+longitudeFrom;

        //Creating a LatLng Object to store Coordinates
        LatLng latLng = new LatLng(latitudeFrom, longitudeFrom);




        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //Displaying current coordinates in toast
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //Clearing all the markers


        //Adding a new marker to the current pressed position
        if(buttonFrom.isChecked()) {
            mMap.clear();

            if(mTo!=null) {
                mTo = mMap.addMarker(new MarkerOptions()
                        .position(mTo.getPosition())
                        .draggable(true));
            }
            mfrom = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .draggable(true));
        }
        else if(buttonTo.isChecked()){

            mMap.clear();
            if(mfrom!=null)  {
                mfrom = mMap.addMarker(new MarkerOptions()
                        .position(mfrom.getPosition())
                        .draggable(true));

            }
            mTo = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .draggable(true));
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //Getting the coordinates
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //Moving the map

    }
    public void eerstePointer(){
        latitudeFrom=latitude;
        longitudeFrom=longitude;

    }
    public void tweedePointer(){
        latitudeTo=latitude;
        longitudeTo=longitude;
    }

    @Override
    public void onClick(View v) {
        if(v == buttonFrom){
            buttonTo.setChecked(false);
        }
        else if (v==buttonTo){
            buttonFrom.setChecked(false);
        }
        else if (v==buttonCalculate){
            double afstand=SphericalUtil.computeDistanceBetween(mfrom.getPosition(),mTo.getPosition());
            Toast.makeText(this, String.valueOf(afstand), Toast.LENGTH_LONG).show();

            PostAfmetingenTask task = new PostAfmetingenTask(afstand);
            task.execute();

        }
    }
    public class PostAfmetingenTask extends AsyncTask<Void, Void, Boolean> {

        public HashMap<String, Object> claims;
        public String ipServer = ((GlobaleVariabelen) getApplication()).getIpServer();
        private JWT jwt = new JWT(((GlobaleVariabelen) getApplication()).getJWTKey());

        public PostAfmetingenTask(double afstand) {
            claims=new HashMap<String, Object>();
            //AfstandIngevuld a=new AfstandIngevuld(filiaalId,afstand);
            claims.put("afstands",afstand);
            claims.put("filiaalid",filiaalId);
        }

        @Override
        public void onPreExecute(){
            ((GlobaleVariabelen) getApplication()).setTaakUitgevoerd(false);
            System.out.println("preExecute");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String requestUrl = "http://" + ((GlobaleVariabelen) getApplication()).getIpServer() + ":8080/RetailSonarREST/rest_service/bedrijf/afstandx/set/";
                String payload = "Bearer " + jwt.maakJWTVoorPostEigenschappen(claims);
                URL url = new URL(requestUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + jwt.maakJWTMetLoginEnId(((GlobaleVariabelen) getApplication()).getLoggedInUser(), ((GlobaleVariabelen)getApplication()).getHuidigFiliaalId()));
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                writer.write(payload);
                writer.close();
                int response = connection.getResponseCode();
                if(response != HttpURLConnection.HTTP_OK) {
                    System.out.println("doInBackground in MapsActivity returnt response code: " + response);
                    return false;
                }

                connection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
            } else {
                System.out.println("error in MapsActivity onPostExecute()");
            }
        }

        @Override
        protected void onCancelled() {
            ((GlobaleVariabelen) getApplication()).setTaakUitgevoerd(true);
            System.out.println("UserActivity cancelled");
        }
    }
}