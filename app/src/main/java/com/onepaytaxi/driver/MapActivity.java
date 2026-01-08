package com.onepaytaxi.driver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.data.MapWrapperLayout;
import com.onepaytaxi.driver.route.Route;

import com.onepaytaxi.driver.utils.DirectionsJSONParser;
import com.onepaytaxi.driver.utils.FontHelper;
import com.onepaytaxi.driver.utils.SessionSave;
import com.onepaytaxi.driver.utils.Systems;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends MainActivity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    public static boolean GEOCODE_EXPIRY = false;
    private static TextView TappedLocation;
    public String DropAddress = "";
    private ImageView btn_back;
    private Button Complete_trip, butt_reset, butt_submit;
    GoogleMap googleMap;
    private MapWrapperLayout mapWrapperLayout;
    private LatLng dropLocation;
    private final String type = "";
    private static final String LocationRequestedBy = "P";

    public Double drop_latitude = 0.0, drop_longitude = 0.0;
    public Double pickup_latitude = 0.0, pickup_longitude = 0.0;

    ArrayList<LatLng> markerPoints;
    String overViewPolyLine = "";

    private String route_is = "1";
    private Route route = null;


    @Override
    public int setLayout() {

        return R.layout.maplayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);


    }

    /**
     * Get the google map pixels from xml density independent pixel.
     */
    public static int getPixelsFromDp(final Context context, final float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    public void Initialize() {

        FontHelper.applyFont(this, findViewById(R.id.select_drop_location));

        btn_back = findViewById(R.id.back_icon);
        Complete_trip = findViewById(R.id.butt_onboard);
        butt_reset = findViewById(R.id.butt_reset);
        butt_submit = findViewById(R.id.butt_submit);
        //Complete_trip.setVisibility(View.GONE);
      //  TappedLocation = findViewById(R.id.tapped_location);
        findViewById(R.id.header_titleTxt).setVisibility(View.GONE);
        Glide.with(this).load(SessionSave.getSession("image_path", this) + "headerLogo_driver.png").apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)).into((ImageView) findViewById(R.id.header_titleTxt));
        btn_back.setOnClickListener(this);
        Complete_trip.setOnClickListener(this);

        markerPoints = new ArrayList<LatLng>();

        route = new Route();



        Bundle bun = getIntent().getExtras();
        if (bun != null) {

            try {
                pickup_latitude = Double.parseDouble(bun.getString("start_latitude"));
                pickup_longitude = Double.parseDouble(bun.getString("start_longitude"));
                drop_latitude = Double.parseDouble(bun.getString("end_latitude"));
                drop_longitude = Double.parseDouble(bun.getString("end_longitude"));
                route_is = bun.getString("route_is");
                markerPoints.add(new LatLng(pickup_latitude, pickup_longitude));
                markerPoints.add(new LatLng(drop_latitude, drop_longitude));

               /* googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pickup_latitude,pickup_longitude), 12f));


                googleMap.addMarker(new MarkerOptions().position(new LatLng(pickup_latitude, pickup_longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_green)).draggable(false));
                googleMap.addMarker(new MarkerOptions().position(new LatLng(drop_latitude,drop_longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.back_black_to_white)).draggable(false));


                String url = getDirectionsUrl(new LatLng(pickup_latitude, pickup_longitude), new LatLng(drop_latitude, drop_longitude));

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);*/

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        Complete_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (markerPoints.size() >= 2) {
                    googleMap.clear();
                    LatLng origin = markerPoints.get(0);
                    LatLng dest = markerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);



                    try {

                        if (pickup_latitude != null) {

                            int height = 100;
                            int width = 100;
                            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.map_icon_red);
                            BitmapDrawable bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.map_icon_red);
                            Bitmap b = bitmapdraw.getBitmap();
                            Bitmap b2 = bitmapdraw2.getBitmap();
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                            Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, width, height, false);

                            googleMap.addMarker(new MarkerOptions().position(new LatLng(pickup_latitude, pickup_longitude)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).draggable(false));
                            googleMap.addMarker(new MarkerOptions().position(new LatLng(drop_latitude, drop_longitude)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker2)).draggable(false));

                            markerPoints.add(new LatLng(pickup_latitude, pickup_longitude));
                            markerPoints.add(new LatLng(drop_latitude, drop_longitude));


                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });




        butt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                googleMap.clear();
                markerPoints.clear();

                try {

                    if (pickup_latitude != null) {

                        int height = 100;
                        int width = 100;
                        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.map_icon_red);
                        BitmapDrawable bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.map_icon_red);
                        Bitmap b = bitmapdraw.getBitmap();
                        Bitmap b2 = bitmapdraw2.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                        Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, width, height, false);

                        googleMap.addMarker(new MarkerOptions().position(new LatLng(pickup_latitude, pickup_longitude)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).draggable(false));
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(drop_latitude, drop_longitude)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker2)).draggable(false));

                        markerPoints.add(new LatLng(pickup_latitude, pickup_longitude));
                        markerPoints.add(new LatLng(drop_latitude, drop_longitude));


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        butt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    /**
     * A method to download json data from url
     */
    @SuppressLint("LongLogTag")
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Waypoints
        String waypoints = "";
        for (int i = 2; i < markerPoints.size(); i++) {
            LatLng point = markerPoints.get(i);
            if (i == 2)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + SessionSave.getSession(CommonData.GOOGLE_KEY, MapActivity.this);

        return url;

    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service

            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);


        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            JSONArray jRoutes = null;
            JSONObject jOverviewPoly = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                jRoutes = jObject.getJSONArray("routes");

                jOverviewPoly = ((JSONObject) jRoutes.get(0)).getJSONObject("overview_polyline");
                overViewPolyLine = jOverviewPoly.getString("points");


                if (route_is.equalsIgnoreCase("1")) {
                    SessionSave.saveSession("polyline1", overViewPolyLine, MapActivity.this);
                }else if(route_is.equalsIgnoreCase("2")){
                    SessionSave.saveSession("polyline2", overViewPolyLine, MapActivity.this);
                }else if(route_is.equalsIgnoreCase("3")){
                    SessionSave.saveSession("polyline3", overViewPolyLine, MapActivity.this);
                }

               // SessionSave.saveSession("polyline", overViewPolyLine, MapActivity.this);


                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(7);
                lineOptions.color(getResources().getColor(R.color.map_marker_green));
            }

            // Drawing polyline in the Google Map for the i-th route
            googleMap.addPolyline(lineOptions);
        }
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        CameraUpdate cameraUpdate = null;
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setZoomControlsEnabled(false);
        MapsInitializer.initialize(MapActivity.this);
        mapWrapperLayout = findViewById(R.id.map_relative_layout);
        mapWrapperLayout.init(googleMap, getPixelsFromDp(MapActivity.this, 39 + 20));
        mapWrapperLayout.setVisibility(View.VISIBLE);

        try {
// Customise the styling of the base map using a JSON object defined
// in a raw resource file.
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapActivity.this, R.raw.map_style));
            if (!success) {
                Systems.out.println("Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Systems.out.println("Can't find style. Error: ");
        }

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
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setOnMapClickListener(this);

      /*  this.googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                // Remove the marker
                markerPoints.remove(marker.getPosition());
                marker.remove();
            }
        });*/


        //  movetoCurrentloc();


        try {

            if (pickup_latitude != null) {

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pickup_latitude, pickup_longitude), 16f));


                // googleMap.addMarker(new MarkerOptions().position(new LatLng(pickup_latitude, pickup_longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_green)).draggable(false));
                // googleMap.addMarker(new MarkerOptions().position(new LatLng(drop_latitude, drop_longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_red)).draggable(false));

                int height = 100;
                int width = 100;
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.map_icon_red);
                BitmapDrawable bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.map_icon_red);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap b2 = bitmapdraw2.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, width, height, false);

                googleMap.addMarker(new MarkerOptions().position(new LatLng(pickup_latitude, pickup_longitude)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).draggable(false));
                googleMap.addMarker(new MarkerOptions().position(new LatLng(drop_latitude, drop_longitude)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker2)).draggable(false));

               try{


                if (route_is.equalsIgnoreCase("1")) {
                    if (!SessionSave.getSession("polyline1",  MapActivity.this).equalsIgnoreCase("")) {

                        String mroute =SessionSave.getSession("polyline1",  MapActivity.this);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                    route.drawRouteFromPolyline(googleMap, mroute, markerPoints);

                            }
                        }, 500);

                    }else{
                        String url = getDirectionsUrl(new LatLng(pickup_latitude, pickup_longitude), new LatLng(drop_latitude, drop_longitude));

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                    }
                }else if(route_is.equalsIgnoreCase("2")){
                    if (!SessionSave.getSession("polyline2",  MapActivity.this).equalsIgnoreCase("")) {
                        String mroute =SessionSave.getSession("polyline2",  MapActivity.this);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                route.drawRouteFromPolyline(googleMap, mroute, markerPoints);

                            }
                        }, 500);
                    }else{
                        String url = getDirectionsUrl(new LatLng(pickup_latitude, pickup_longitude), new LatLng(drop_latitude, drop_longitude));

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                    }
                }else if(route_is.equalsIgnoreCase("3")){
                    if (!SessionSave.getSession("polyline3",  MapActivity.this).equalsIgnoreCase("")) {
                        String mroute =SessionSave.getSession("polyline3",  MapActivity.this);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                route.drawRouteFromPolyline(googleMap, mroute, markerPoints);

                            }
                        }, 500);
                    }else{
                        String url = getDirectionsUrl(new LatLng(pickup_latitude, pickup_longitude), new LatLng(drop_latitude, drop_longitude));

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                    }
                }

               }catch (Exception e){
                   e.printStackTrace();
               }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        if (cameraUpdate != null)
            googleMap.moveCamera(cameraUpdate);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                enableListener();
            }
        }, 1000);


        // googleMap.setOnMapClickListener(this);
    }


    public void enableListener() {
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.e("camera", "==camera idle==" + googleMap.getCameraPosition().target);


            }
        });
    }

    @Override
    public void onMapClick(LatLng point) {
        // Already 10 locations with 8 waypoints and 1 start location and 1 end location.
        // Upto 8 waypoints are allowed in a query for non-business users
        if (markerPoints.size() >= 10) {
            return;
        }

        // Adding new item to the ArrayList
        markerPoints.add(point);

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(point);

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED and
         * for the rest of markers, the color is AZURE
         */
        if (markerPoints.size() == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else if (markerPoints.size() == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        } else {

            int height = 100;
            int width = 100;
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.map_icon_blue);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

            //   googleMap.addMarker(new MarkerOptions().position(new LatLng(pickup_latitude, pickup_longitude)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).draggable(false));

            options.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        }


        //options.snippet("Tap here to remove this marker");
        //options.title("Marker");



        // Add new marker to the Google Map Android API V2
        this.googleMap.addMarker(options).setDraggable(true);
    }
}