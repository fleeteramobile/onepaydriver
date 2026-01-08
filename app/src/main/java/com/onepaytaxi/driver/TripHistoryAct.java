package com.onepaytaxi.driver;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.onepaytaxi.driver.fragments.TripDetailNewFrag;
import com.onepaytaxi.driver.utils.NetworkStatus;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

/**
 * Created by developer on 15/11/16.
 */


/**
 * This is class is used to show driver trip history
 */
public class TripHistoryAct extends BaseActivity {

    private TextView backtext;
    private NetworkStatus networkStatus;
CardView back_trip_details;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_history);
        NetworkStatus.appContext = this;
        networkStatus = new NetworkStatus();
        registerReceiver(networkStatus, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        backtext = findViewById(R.id.slideImg);
        back_trip_details = findViewById(R.id.back_trip_details);
        backtext.setVisibility(View.VISIBLE);

        backtext.setOnClickListener(v -> onBackPressed());
        back_trip_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed()  ;
            }
        });

        boolean isFromFareScreen = false;
        String tripId = "";
        String tripDetailResponse = null;
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            isFromFareScreen = bundle.getBoolean("isFromFareScreen", false);
            tripId = bundle.getString("trip_id");
            tripDetailResponse = bundle.getString("tripDetailResponse");
        }

        if (isFromFareScreen) {
            TripDetailNewFrag ff = new TripDetailNewFrag();
            Bundle b = new Bundle();
            b.putBoolean("isFromFareScreen", true);
            b.putString("trip_id", tripId);
            b.putString("tripDetailResponse", tripDetailResponse);
            ff.setArguments(b);
            getSupportFragmentManager().beginTransaction().add(R.id.mainFrag, ff).commit();
        }

    }

    @Override
    protected void onResume() {


        super.onResume();
        NetworkStatus.isOnline(TripHistoryAct.this);
    }

    public void setTitle(String s) {
        try {
            ((TextView) findViewById(R.id.headerTxt)).setText(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(networkStatus);
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
