package com.onepaytaxi.driver.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.onepaytaxi.driver.MapZoomAct;
import com.onepaytaxi.driver.MyApplication;
import com.onepaytaxi.driver.R;
import com.onepaytaxi.driver.TripHistoryAct;
import com.onepaytaxi.driver.data.apiData.ApiRequestData;
import com.onepaytaxi.driver.data.apiData.TripDetailResponse;

import com.onepaytaxi.driver.route.StopData;
import com.onepaytaxi.driver.service.CoreClient;
import com.onepaytaxi.driver.service.RetrofitCallbackClass;
import com.onepaytaxi.driver.service.ServiceGenerator;
import com.onepaytaxi.driver.utils.CL;

import com.onepaytaxi.driver.utils.NC;
import com.onepaytaxi.driver.utils.RoundedImageView;
import com.onepaytaxi.driver.utils.SessionSave;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by developer on 2/11/16.
 */


/**
 * This class is used to show driver trip details
 */
public class TripDetailNewFrag extends Fragment {
    private TextView details_trip_id, distance, dfare,
            vdfare, waiting, wcost, vWait, sub, tax, promo, total, estimateTxt,
            wallet, cash, min_fare, min_fare_per, min_total_fare, pay_type;
    private String trip_id = " ";
    private ImageView trip_map_view;
    private RoundedImageView driverImg;
    private TextView fares, total_travel_distance_txt, incentive_amount;
    private LinearLayout total_travel_lay, incentive_amount_lay;
    private ImageView driverRat;
    private LinearLayout tripdetails, help_lay, paymentByLayout;
    private BottomSheetBehavior<View> mBottomSheetBehavior;

    private View eve_night_sep;
    private LinearLayout loading;
    private TextView user;
    private TextView night_fare;
    private TextView evefare;
    private LinearLayout distance_lay;
    private LinearLayout miniutes_lay;
    private LinearLayout night_lay, evening_lay, promo_lay, estimatelay;
    private TextView tax_label;
    private TextView distance_fare_txt;
    private TextView payment_type_c;

    //outstation receipt variables
    private TextView tv_additional_time;
    private TextView tv_additional_distance;
    private TextView parkingTxt;
    private TextView convenience_fee_fare_txt, service_charge_txt;
    private TextView tollTxt;
    private LinearLayout layoutNormal, normal_trip_lay, outstation_trip_lay, notes_lay, normal_fare_layout;
    private LinearLayout layoutOutstation;
    private LinearLayout driver_beta_lay;
    private LinearLayout nor_distance_fare_lay, add_distance_lay;

    private TextView baseFare, waitingFare, walletAmount, paidAmount, paymentType, notes_Txt;
    private TextView night_val, evefare_val, promotion_val, tax_val, taxLabel, tripcost_val, trip_duration,
            trip_per_minuten, subtotal_val, nettotal_val, trip_type, additional_dist_lable, os_driver_beta,
            add_dfare, add_dfare_travelled;

    private LinearLayout Nightfare;
    private LinearLayout Eveningfare;
    private final int count = 0;

    private String mapImageUri = "";
    private TextView base_fare;
    private LinearLayout base_fare_lay;

    //private PickupDropView pickUpDropLayout;
    private boolean isFromFareScreen = false;
    private TripDetailResponse tripDetailResponse;

    private TextView cancel_fee;
    private LinearLayout cancelFareLay;
    //for outstation and rental
    private TextView cancelFeeVal;
    private LinearLayout cancelFareLayOut, walletLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle mBundle = getArguments();
            isFromFareScreen = mBundle.getBoolean("isFromFareScreen");
            trip_id = mBundle.getString("trip_id");
            Type type = new TypeToken<TripDetailResponse>() {
            }.getType();
            String response = mBundle.getString("tripDetailResponse");
            tripDetailResponse = new Gson().fromJson(response, type);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.include_details, container, false);
        loading = v.findViewById(R.id.loading);
      ;
        ImageView iv = v.findViewById(R.id.giff);
        DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(iv);
        Glide.with(getActivity())
                .load(R.raw.loading_anim)
                .into(imageViewTarget);
      //  pickUpDropLayout = v.findViewById(R.id.pd_view);
        tv_additional_time = v.findViewById(R.id.additonal_time_fare);
        tv_additional_distance = v.findViewById(R.id.additonal_distance_fare);


        parkingTxt = v.findViewById(R.id.parkingTxt);
        convenience_fee_fare_txt = v.findViewById(R.id.convenience_fee_fare_txt);
        service_charge_txt = v.findViewById(R.id.service_charge_txt);
        tollTxt = v.findViewById(R.id.tollTxt);


        walletAmount = v.findViewById(R.id.WalletAmt);
        paidAmount = v.findViewById(R.id.PaidAmt);
        paymentType = v.findViewById(R.id.Paymenttype);
        night_val = v.findViewById(R.id.night_val);
        evefare_val = v.findViewById(R.id.evefare_val);
        tripcost_val = v.findViewById(R.id.tripcost_val);
        subtotal_val = v.findViewById(R.id.subtotal_val);
        trip_duration = v.findViewById(R.id.trip_duration);
        trip_per_minuten = v.findViewById(R.id.trip_per_minuten);
        nettotal_val = v.findViewById(R.id.nettotal_val);
        trip_type = v.findViewById(R.id.details_trip_type);
        additional_dist_lable = v.findViewById(R.id.additonal_distance_lable);
        os_driver_beta = v.findViewById(R.id.os_driver_beta);


        add_dfare = v.findViewById(R.id.add_dfare);
        add_dfare_travelled = v.findViewById(R.id.add_dfare_travelled);


        cancel_fee = v.findViewById(R.id.cancel_fee);
        cancelFareLay = v.findViewById(R.id.cancelFareLay);
        //for outstation and rental
        cancelFeeVal = v.findViewById(R.id.cancelFeeVal);
        cancelFareLayOut = v.findViewById(R.id.cancelFareLayOut);
        walletLayout = v.findViewById(R.id.walletLayout);

        layoutNormal = v.findViewById(R.id.layout_normalreceipt);
        layoutOutstation = v.findViewById(R.id.layout_outstationreceipt);
        driver_beta_lay = v.findViewById(R.id.driver_beta_lay);
        nor_distance_fare_lay = v.findViewById(R.id.nor_distance_fare_lay);

        add_distance_lay = v.findViewById(R.id.add_distance_lay);
        baseFare = v.findViewById(R.id.BaseFare);
        normal_trip_lay = v.findViewById(R.id.normal_trip_lay);
        normal_fare_layout = v.findViewById(R.id.normal_fare_layout);
        outstation_trip_lay = v.findViewById(R.id.outstation_trip_lay);

        promotion_val = v.findViewById(R.id.promotion_val);
        tax_val = v.findViewById(R.id.tax_val);
        taxLabel = v.findViewById(R.id.taxLabel);


        distance_lay = v.findViewById(R.id.distance_lay);
        miniutes_lay = v.findViewById(R.id.miniutes_lay);
        distance_fare_txt = v.findViewById(R.id.distance_fare_txt);
        driverImg = v.findViewById(R.id.driverImg);
        trip_map_view = v.findViewById(R.id.trip_map_view);
        night_lay = v.findViewById(R.id.night_lay);
        evening_lay = v.findViewById(R.id.evening_lay);
        promo_lay = v.findViewById(R.id.promo_lay);
        driverRat = v.findViewById(R.id.rating);
        tripdetails = v.findViewById(R.id.tripdetails);
        paymentByLayout = v.findViewById(R.id.paymentByLayout);
        help_lay = v.findViewById(R.id.help_lay);
        user = v.findViewById(R.id.user);
        details_trip_id = v.findViewById(R.id.details_trip_id);
        distance = v.findViewById(R.id.dist);
        dfare = v.findViewById(R.id.dfare);
        vdfare = v.findViewById(R.id.vehicle_detail_fare);
        waiting = v.findViewById(R.id.wait);
        wcost = v.findViewById(R.id.wcost);
        vWait = v.findViewById(R.id.vwcost);
        sub = v.findViewById(R.id.sTotal);
        tax = v.findViewById(R.id.tax);
        tax_label = v.findViewById(R.id.tax_label);
        promo = v.findViewById(R.id.promo);
        total = v.findViewById(R.id.total);
        wallet = v.findViewById(R.id.wallet);
        cash = v.findViewById(R.id.cash);
        eve_night_sep = v.findViewById(R.id.eve_night_sep);
        fares = v.findViewById(R.id.fares);
        total_travel_distance_txt = v.findViewById(R.id.total_travel_distance_txt);
        total_travel_lay = v.findViewById(R.id.total_travel_lay);
        incentive_amount_lay = v.findViewById(R.id.incentive_amount_lay);
        incentive_amount = v.findViewById(R.id.incentive_amount);
        min_fare = v.findViewById(R.id.min_fare);
        min_fare_per = v.findViewById(R.id.min_fare_per);
        min_total_fare = v.findViewById(R.id.min_total_fare);
        pay_type = v.findViewById(R.id.pay_type);
        Nightfare = v.findViewById(R.id.night_farelay);
        Eveningfare = v.findViewById(R.id.evening_farelay);
        base_fare = v.findViewById(R.id.base_fare);
        base_fare_lay = v.findViewById(R.id.base_fare_lay);
        evefare = v.findViewById(R.id.evefare);
        night_fare = v.findViewById(R.id.night_fare);
        View bottomSheet = v.findViewById(R.id.tripdetails_scroll);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        payment_type_c = v.findViewById(R.id.payment_type_c);
        estimateTxt = v.findViewById(R.id.estimateTxt);
        estimatelay = v.findViewById(R.id.estimatelay);
        notes_Txt = v.findViewById(R.id.notes_Txt);
        notes_lay = v.findViewById(R.id.notes_lay);

        base_fare_lay.setVisibility(View.VISIBLE);

      /*  driverImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count < 10) {
                    count++;
                } else {

                }

            }
        });*/

        if (isFromFareScreen) {
            mBottomSheetBehavior.setHideable(false);
            bottomSheet.post(() -> {
                mBottomSheetBehavior.setPeekHeight(bottomSheet.getHeight());
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            });
            showFare(tripDetailResponse);
        } else {
            callDetail();
        }


        trip_map_view.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), MapZoomAct.class);
            intent.putExtra("IMAGE_URI", mapImageUri);
            getActivity().startActivity(intent);
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();


        try {
            ((TripHistoryAct) getActivity()).setTitle(NC.getString(R.string.trip_summary));

            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * TripDetail method API call and response parsing.
     */
    private void callDetail() {
        System.out.println("TripDetailResponse" + " " + "2");

        CoreClient client = MyApplication.getInstance().getApiManagerWithEncryptBaseUrl();
        ApiRequestData.getTripDetailRequest request = new ApiRequestData.getTripDetailRequest();
        request.setTrip_id(trip_id);

        Call<TripDetailResponse> LoginResponse = client.callData(ServiceGenerator.COMPANY_KEY, request, SessionSave.getSession("Lang", getActivity()));
        LoginResponse.enqueue(new RetrofitCallbackClass<>(getActivity(), new Callback<TripDetailResponse>() {
            @Override
            public void onResponse(Call<TripDetailResponse> call, Response<TripDetailResponse> response) {
                loading.setVisibility(View.GONE);
                if (getView() != null && response.isSuccessful()) {
                    TripDetailResponse data = response.body();
                    if (data != null) {
                        if (data.status == 1) {
                            tripdetails.setOnClickListener(view -> mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
                            if (data.detail.trip_type.equals("3") || data.detail.trip_type.equals("2")) {
                                normal_trip_lay.setVisibility(View.GONE);
                                outstation_trip_lay.setVisibility(View.VISIBLE);
                                layoutNormal.setVisibility(View.GONE);
                                layoutOutstation.setVisibility(View.VISIBLE);
                                Nightfare.setVisibility(View.GONE);
                                Eveningfare.setVisibility(View.GONE);

                                base_fare_lay.setVisibility(View.VISIBLE);

                             /*   if (data.detail.trip_type.equals("3"))
                                    trip_type.setText(NC.getString(R.string.trip_type_outstation));//+ " Trip"
                                else if (data.detail.trip_type.equals("2"))
                                    trip_type.setText(NC.getString(R.string.trip_type_rental));//+ " Trip"*/

//                                if (data.detail.is_on_my_way_trip.equals("1"))
//                                    trip_type.setText(NC.getString(R.string.trip_onmyway));//+ " Trip"
//                                else if (data.detail.is_on_my_way_trip.equals("0"))
//                                    trip_type.setText(NC.getString(R.string.trip_normal));//+ " Trip"

                                additional_dist_lable.setText(NC.getString(R.string.additonal_distance_fare) + " " + data.detail.metric.toLowerCase());

                                tv_additional_distance.setText(SessionSave.getSession("site_currency", getActivity()) + data.detail.additional_distance_fare);
                                tv_additional_time.setText(SessionSave.getSession("site_currency", getActivity()) + data.detail.additional_time_fare);
                                baseFare.setText(SessionSave.getSession("site_currency", getActivity()) + String.format(Locale.UK, "%.2f", Float.valueOf(data.detail.base_fare)));

                                subtotal_val.setText(SessionSave.getSession("site_currency", getActivity()) + String.format(Locale.UK, "%.2f", Float.parseFloat(data.detail.additional_distance_fare) + Float.valueOf(data.detail.additional_time_fare) + Float.parseFloat(data.detail.base_fare)));
                                promotion_val.setText("- " + SessionSave.getSession("site_currency", getActivity()) + String.format(Locale.UK, "%.2f", Float.valueOf(data.detail.promotion)));

                                float subTotal = Float.parseFloat(data.detail.additional_distance_fare) + Float.parseFloat(data.detail.additional_time_fare) + Float.parseFloat(data.detail.base_fare);
                                float withoutPromo = Float.parseFloat(data.detail.promotion);
                                tripcost_val.setText(SessionSave.getSession("site_currency", getActivity()) + String.format(Locale.UK, "%.2f", (subTotal - withoutPromo)));
                                tax_val.setText(SessionSave.getSession("site_currency", getActivity()) + String.format(Locale.UK, "%.2f", Float.valueOf(data.detail.tax_fare)));
                                taxLabel.setText(NC.getString(R.string.Tax) + " (" + data.detail.tax_percentage + "%)");
                                nettotal_val.setText(SessionSave.getSession("site_currency", getActivity()) + String.format(Locale.UK, "%.2f", ((subTotal - withoutPromo)) + (Float.parseFloat(data.detail.tax_fare))));
                                walletAmount.setText("- " + SessionSave.getSession("site_currency", getActivity()) + String.format(Locale.UK, "%.2f", Float.valueOf(data.detail.used_wallet_amount)));

                                paidAmount.setText(SessionSave.getSession("site_currency", getActivity()) + String.format(Locale.UK, "%.2f", (Float.valueOf(data.detail.paid_amount))));
                                paymentType.setText(data.detail.payment_type_label);

                                details_trip_id.setText(NC.getString(R.string.trip_id) + ": " + data.detail.trip_id);
                                user.setText(data.detail.driver_name);
                                fares.setText(SessionSave.getSession("site_currency", getActivity()) + data.detail.paid_amount);
                                if (data.detail.total_travelled_distance != null && data.detail.total_travelled_distance.isEmpty()) {
                                    total_travel_lay.setVisibility(View.VISIBLE);
                                    incentive_amount_lay.setVisibility(View.VISIBLE);
                                } else {
                                    total_travel_lay.setVisibility(View.GONE);
                                    incentive_amount_lay.setVisibility(View.GONE);
                                }
                                total_travel_distance_txt.setText(data.detail.total_travelled_distance);
                                incentive_amount.setText(SessionSave.getSession("site_currency", getActivity()) + data.detail.incentive_amount);
                                Picasso.get().load(data.detail.driver_image).resize(100, 100).into(driverImg);
                                Picasso.get().load(data.detail.map_image).into(trip_map_view);
                                mapImageUri = data.detail.map_image;

                       //         createPickAndStopView(data.detail.current_location, data.detail.pickup_latitude, data.detail.pickup_longitude, data.detail.drop_location, data.detail.drop_latitude, data.detail.drop_longitude);

                                if (data.detail.pending_cancel_amount != null && (Float.parseFloat(data.detail.pending_cancel_amount) > 0.0)) {
                                    cancelFeeVal.setText(SessionSave.getSession("site_currency", getActivity()) + (data.detail.pending_cancel_amount));
                                } else {
                                    cancelFareLayOut.setVisibility(View.GONE);
                                }

                                int driver_rating = (int) Float.parseFloat(data.detail.rating);
                                if (driver_rating == 0)
                                    driverRat.setImageResource(R.drawable.star6);
                                else if (driver_rating == 1)
                                    driverRat.setImageResource(R.drawable.star1);
                                else if (driver_rating == 2)
                                    driverRat.setImageResource(R.drawable.star2);
                                else if (driver_rating == 3)
                                    driverRat.setImageResource(R.drawable.star3);
                                else if (driver_rating == 4)
                                    driverRat.setImageResource(R.drawable.star4);
                                else if (driver_rating == 5)
                                    driverRat.setImageResource(R.drawable.star5);

                            } else {
                                showFare(data);

                            }

                            if (data.detail.payment_type != null && data.detail.payment_type.equals("5")) {
                                walletLayout.setVisibility(View.VISIBLE);
                            } else {
                                walletLayout.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(getActivity(), data.message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), NC.getString(R.string.please_check_internet), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Toast.makeText(getActivity(), NC.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TripDetailResponse> call, Throwable t) {
                t.printStackTrace();
                loading.setVisibility(View.GONE);
                // Toast.makeText(getActivity(), NC.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void showFare(TripDetailResponse data) {
        System.out.println("TripDetailResponse" + " " + "1");
        loading.setVisibility(View.GONE);
        normal_trip_lay.setVisibility(View.VISIBLE);
        outstation_trip_lay.setVisibility(View.GONE);
        layoutNormal.setVisibility(View.VISIBLE);
        layoutOutstation.setVisibility(View.GONE);


        if (data.detail.trip_type.equals("1")) {
            trip_type.setText(R.string.trip_type_normal);//+ " Trip"
            driver_beta_lay.setVisibility(View.GONE);
            nor_distance_fare_lay.setVisibility(View.VISIBLE);
            add_distance_lay.setVisibility(View.GONE);

        } else if (data.detail.trip_type.equals("2")) {
            trip_type.setText(R.string.trip_type_rental);//+ " Trip"

            driver_beta_lay.setVisibility(View.VISIBLE);
            add_distance_lay.setVisibility(View.VISIBLE);
            nor_distance_fare_lay.setVisibility(View.GONE);
        } else if (data.detail.trip_type.equals("3")) {
            driver_beta_lay.setVisibility(View.VISIBLE);
            add_distance_lay.setVisibility(View.VISIBLE);
            trip_type.setText(R.string.trip_type_outstation);//+ " Trip"
            nor_distance_fare_lay.setVisibility(View.GONE);

            if (data.detail.roundtrip.equalsIgnoreCase("no")) {
                trip_type.setText(getActivity().getString(R.string.trip_type_outstation) + "\nOne way trip");
            } else {
                trip_type.setText(getActivity().getString(R.string.trip_type_outstation) + "\nRound trip");
            }
        }

        add_dfare.setText(SessionSave.getSession("site_currency", getActivity()) + " " + data.detail.new_distance_fare);
        double osAdditionalKm = data.detail.os_addtinal_km; // Assuming this is the additional KM value
        add_dfare_travelled.setText(String.format(Locale.getDefault(), "%.2f Km", osAdditionalKm));



//        if (data.detail.is_on_my_way_trip.equals("1"))
//            trip_type.setText(NC.getString(R.string.trip_onmyway));//+ " Trip"
//        else if (data.detail.is_on_my_way_trip.equals("0"))
//            trip_type.setText(NC.getString(R.string.trip_normal));//+ " Trip"
        normal_fare_layout.setVisibility(View.VISIBLE);
        details_trip_id.setText(NC.getString(R.string.trip_id) + ": " + data.detail.trip_id);
        user.setText(data.detail.passenger_name);
        fares.setText(SessionSave.getSession("site_currency", getActivity()) + " " + data.detail.amt);

        if (data.detail.passenger_image != null && !data.detail.passenger_image.isEmpty())
            Picasso.get().load(data.detail.passenger_image).into(driverImg);

        mapImageUri = data.detail.map_image;
        if (mapImageUri != null && !mapImageUri.isEmpty())
            Picasso.get().load(mapImageUri).into(trip_map_view);

        distance.setText(data.detail.distance + " " + data.detail.metric.toLowerCase());
        base_fare.setText(SessionSave.getSession("site_currency", getActivity()) + " " + data.detail.new_base_fare);
        os_driver_beta.setText(SessionSave.getSession("site_currency", getActivity()) + " " + data.detail.os_driver_beta);






        trip_per_minuten.setText(SessionSave.getSession("site_currency", getActivity()) + " " + data.detail.fare_per_minute);
        dfare.setText(SessionSave.getSession("site_currency", getActivity()) + " " + data.detail.new_distance_fare);
        vdfare.setText(SessionSave.getSession("site_currency", getActivity()) + " " + (data.detail.distance_fare));
        min_fare.setText(data.detail.trip_minutes + " " + NC.getString(R.string.mins));
        min_fare_per.setText(SessionSave.getSession("site_currency", getActivity()) + " " + data.detail.fare_per_minute);
        min_total_fare.setText(SessionSave.getSession("site_currency", getActivity()) + " " + (data.detail.minutes_fare));


        tollTxt.setText(SessionSave.getSession("site_currency", getActivity()) + " " + (data.detail.toll_amount));
        parkingTxt.setText(SessionSave.getSession("site_currency", getActivity()) + " " + (data.detail.parking_amount));
        convenience_fee_fare_txt.setText(SessionSave.getSession("site_currency", getActivity()) + " " + (data.detail.booking_fare));
        service_charge_txt.setText(SessionSave.getSession("site_currency", getActivity()) + " " + (data.detail.service_fare));


        waiting.setText(String.valueOf(data.detail.waiting_time));
        if (data.detail.total_travelled_distance != null && data.detail.total_travelled_distance.isEmpty()) {
            total_travel_lay.setVisibility(View.VISIBLE);
            incentive_amount_lay.setVisibility(View.VISIBLE);
        } else {
            total_travel_lay.setVisibility(View.GONE);
            incentive_amount_lay.setVisibility(View.GONE);
        }
        total_travel_distance_txt.setText(data.detail.total_travelled_distance);
        incentive_amount.setText(SessionSave.getSession("site_currency", getActivity()) + data.detail.incentive_amount);
//        System.out.println("trip_duration_final" + " "+ data.detail.toString());
//
        trip_duration.setText(data.detail.trip_duration + " " + "Mins");

        if (data.detail != null) {
            System.out.println("Trip Duration: " + data.detail.trip_duration);
        } else {
            System.out.println("Detail is null");
        }

        if (data.detail.payment_type_label != null) {
            payment_type_c.setText(data.detail.payment_type_label);
            pay_type.setText(data.detail.payment_type_label);
            if (data.detail.payment_type_label.trim().equalsIgnoreCase(NC.getString(R.string.cash).trim())) {
                payment_type_c.setTextColor(CL.getResources().getColor(R.color.trip_cash_highlight));

            } else if (data.detail.payment_type_label.trim().equalsIgnoreCase(NC.getString(R.string.wallet).trim())) {
                payment_type_c.setTextColor(CL.getResources().getColor(R.color.trip_cash_highlight));
            } else {
                payment_type_c.setTextColor(CL.getResources().getColor(R.color.past_booking_card));
            }
        } else {
            payment_type_c.setVisibility(View.GONE);
            pay_type.setVisibility(View.GONE);
        }

        wcost.setText(SessionSave.getSession("site_currency", getActivity()) + " " + data.detail.waiting_fare_minutes);
        vWait.setText(SessionSave.getSession("site_currency", getActivity()) + " " + (data.detail.waiting_fare));
        sub.setText(SessionSave.getSession("site_currency", getActivity()) + " " + (data.detail.subtotal));

        if (data.detail.approx_fare != null) {
            estimateTxt.setText(SessionSave.getSession("site_currency", getActivity()) + " " + (data.detail.approx_fare));

        } else {
            estimatelay.setVisibility(View.GONE);
        }

        if (data.detail.notes != null && !data.detail.notes.isEmpty()) {
            notes_Txt.setText(data.detail.notes);
            notes_lay.setVisibility(View.VISIBLE);
        } else {
            notes_lay.setVisibility(View.GONE);
        }
        tax.setText(SessionSave.getSession("site_currency", getActivity()) + " " + (data.detail.tax_fare));
        tax_label.setText(NC.getString(R.string.Tax) + " (" + data.detail.tax_percentage + "%)");

        promo.setText("- " + SessionSave.getSession("site_currency", getActivity()) + " " + (data.detail.promocode_fare));
        total.setText(SessionSave.getSession("site_currency", getActivity()) + " " + (data.detail.amt));
        wallet.setText(SessionSave.getSession("site_currency", getActivity()) + " " + (data.detail.used_wallet_amount));

        if (data.detail.actual_paid_amount != null)
            cash.setText(SessionSave.getSession("site_currency", getActivity()) + " " + (data.detail.actual_paid_amount));
        else
            cash.setVisibility(View.GONE);

        if (data.detail.pending_cancel_amount != null && (Float.parseFloat(data.detail.pending_cancel_amount) > 0.0)) {
            cancel_fee.setText(SessionSave.getSession("site_currency", getActivity()) + (data.detail.pending_cancel_amount));
        } else {
            cancelFareLay.setVisibility(View.GONE);
        }
        if (data.detail.min_distance_status == 0)
            distance_fare_txt.setText(NC.getString(R.string.dist_fare) + " " + NC.getString(R.string.per) + " " + data.detail.metric.toLowerCase());
        else {
            distance_fare_txt.setText(NC.getString(R.string.minimum_fare));
            base_fare_lay.setVisibility(View.GONE);
        }

        if (night_fare != null) {
            night_fare.setText(SessionSave.getSession("site_currency", getActivity()) + " " + (data.detail.nightfare));
            evefare.setText(SessionSave.getSession("site_currency", getActivity()) + " " + (data.detail.eveningfare));
        }
        if (data.detail.fare_calculation_type != null) {
            if (data.detail.fare_calculation_type.trim().equals("1"))
                miniutes_lay.setVisibility(View.VISIBLE);
            else if (data.detail.fare_calculation_type.trim().equals("2"))
                distance_lay.setVisibility(View.GONE);
        }
        if (data.detail.nightfare != null && !data.detail.nightfare.isEmpty()) {
            night_lay.setVisibility(View.VISIBLE);
            if (Double.parseDouble(data.detail.nightfare) <= 0.0)
                night_lay.setVisibility(View.GONE);
            if (data.detail.eveningfare != null && !data.detail.eveningfare.isEmpty()) {
                if (Double.parseDouble(data.detail.nightfare) <= 0.0 && Double.parseDouble(data.detail.eveningfare) <= 0.0) {
                    eve_night_sep.setVisibility(View.GONE);
                }
            } else
                eve_night_sep.setVisibility(View.GONE);

        } else {
            eve_night_sep.setVisibility(View.GONE);
            night_lay.setVisibility(View.GONE);
        }

        if (data.detail.eveningfare != null && !data.detail.eveningfare.isEmpty() && Double.parseDouble(data.detail.eveningfare) > 0.0) {
            evening_lay.setVisibility(View.VISIBLE);
        } else
            evening_lay.setVisibility(View.GONE);

        if (data.detail.promocode_fare != null && !data.detail.promocode_fare.isEmpty() && !data.detail.promocode_fare.equals("0") && !data.detail.promocode_fare.equals("0.00")) {
            promo_lay.setVisibility(View.VISIBLE);
        } else promo_lay.setVisibility(View.GONE);

        ArrayList<StopData> stops = data.detail.stops;

//        if (stops != null && stops.size() > 0)
//            pickUpDropLayout.setData(stops, "ONGOING", SessionSave.getSession("Lang", getActivity()));
//        else if (!isFromFareScreen)
//            createPickAndStopView(data.detail.current_location, data.detail.pickup_latitude, data.detail.pickup_longitude, data.detail.drop_location, data.detail.drop_latitude, data.detail.drop_longitude);


        if (data.detail.payment_type != null) {
            if (data.detail.payment_type.equals("1")) {
                if (SessionSave.getSession("Lang", getActivity()).equals("ar")) {
                    pay_type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cash, 0);

                } else {
                    pay_type.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cash, 0, 0, 0);

                }
            } else if (data.detail.payment_type.equals("5")) {
                if (SessionSave.getSession("Lang", getActivity()).equals("ar")) {
                    pay_type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cash, 0);

                } else {
                    pay_type.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cash, 0, 0, 0);

                }
                // pay_type.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cash, 0, 0, 0);
                pay_type.setVisibility(View.GONE);
                cash.setVisibility(View.GONE);
            } else {
                if (SessionSave.getSession("Lang", getActivity()).equals("ar")) {
                    pay_type.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.credit_card, 0);

                } else {
                    pay_type.setCompoundDrawablesWithIntrinsicBounds(R.drawable.credit_card, 0, 0, 0);

                }
                //   pay_type.setCompoundDrawablesWithIntrinsicBounds(R.drawable.credit_card, 0, 0, 0);
            }
        } else if (isFromFareScreen)
            paymentByLayout.setVisibility(View.GONE);


        if (data.detail.corporate_booking != null && data.detail.corporate_booking.equalsIgnoreCase("1") && !isFromFareScreen) {
            paymentByLayout.setVisibility(View.VISIBLE);
            pay_type.setText(getString(R.string.corporate));
            payment_type_c.setText(getString(R.string.corporate));
            pay_type.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }


        if (data.detail.rating != null) {
            int driver_rating = (int) Float.parseFloat(data.detail.rating);
            if (driver_rating == 0)
                driverRat.setImageResource(R.drawable.star6);
            else if (driver_rating == 1)
                driverRat.setImageResource(R.drawable.star1);
            else if (driver_rating == 2)
                driverRat.setImageResource(R.drawable.star2);
            else if (driver_rating == 3)
                driverRat.setImageResource(R.drawable.star3);
            else if (driver_rating == 4)
                driverRat.setImageResource(R.drawable.star4);
            else if (driver_rating == 5)
                driverRat.setImageResource(R.drawable.star5);
        } else
            driverRat.setVisibility(View.GONE);

    }

    /**
     * Method to create views dynamically if ArrayList<StopData> value not available (ie., Normal flow)
     * <p>
     * New ArrayList of StopData values created with pickup and drop(if available) and dynamic views created based on that ArrayList
     *
     * @param pickup_location
     * @param pickup_latitude
     * @param pickup_longitude
     * @param drop_location
     * @param drop_latitude
     * @param drop_longitude
     */
    private void createPickAndStopView(String pickup_location, String pickup_latitude, String pickup_longitude, String drop_location, String drop_latitude, String drop_longitude) {
        ArrayList<StopData> pickUpDropList = new ArrayList<>();
        StopData pickUpData = new StopData(0, Double.parseDouble(pickup_latitude), Double.parseDouble(pickup_longitude), pickup_location, "", "");
        pickUpDropList.add(pickUpData);
        if (drop_location != null && !drop_location.isEmpty()) {
            StopData dropData = new StopData((1 + new Random().nextInt()), Double.parseDouble(drop_latitude), Double.parseDouble(drop_longitude), drop_location, "", "");
            pickUpDropList.add(dropData);
        }
//        if (getActivity() != null)
//            pickUpDropLayout.setData(pickUpDropList, "ONGOING", SessionSave.getSession("Lang", getActivity()));
    }
}