package com.onepaytaxi.driver.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.onepaytaxi.driver.BaseActivity;
import com.onepaytaxi.driver.R;

import org.json.JSONObject;

import static com.onepaytaxi.driver.utils.GpsStatus.mDialog;

 /**
 * Created by developer on 6/3/18.
 */

public class SignUpWebviewAct extends BaseActivity {

     WebView simpleWebView;
      ProgressDialog progressDialog;
     String driverId, encodeSTr, link_1, link_2_attach, lang_Str;
     String post_params;
     private Uri uri;
     private ImageView iv;


     @SuppressLint("SetJavaScriptEnabled")
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.webview_act);
         NetworkStatus.appContext = this;

         if (getIntent() != null)
             post_params = getIntent().getStringExtra("post_params");


         simpleWebView = findViewById(R.id.simpleWebView);
         iv = findViewById(R.id.giff);
         DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(iv);
         Glide.with(SignUpWebviewAct.this)
                 .load(R.raw.loading_anim)
                 .into(imageViewTarget);
         simpleWebView.setWebViewClient(new MyWebViewClient());
//         simpleWebView.getSettings().setJavaScriptEnabled(true);

         driverId = SessionSave.getSession("Id", SignUpWebviewAct.this);
//         encodeSTr = HttpRequest.Base64.encodeBytes(driverId.getBytes());
         encodeSTr = driverId;
         lang_Str = SessionSave.getSession("Lang", SignUpWebviewAct.this);
         uri = Uri.parse(SessionSave.getSession("api_base", SignUpWebviewAct.this));

         link_1 = SessionSave.getSession("api_base", SignUpWebviewAct.this) + "package_plan.html/";
         Systems.out.println("Link11" + link_1);
         link_2_attach = link_1 + encodeSTr + "/?lang=" + lang_Str;
          Systems.out.println("cherry_chk" + "--" + SessionSave.getSession("Id", SignUpWebviewAct.this) + "--lan"
                 + SessionSave.getSession("Lang", SignUpWebviewAct.this) + "--" + driverId + "--" + encodeSTr + "--p;'--  **"
                 + link_2_attach+"__"+post_params);
//         try {
             if (progressDialog == null) {
                 if(simpleWebView!=null)
                     simpleWebView.setVisibility(View.GONE);

             }
             simpleWebView.postUrl("http://192.168.2.140:1012/booking_new.html", /*(new AA().ee(post_params)).getBytes()*/post_params.getBytes());
//         } catch (GeneralSecurityException e) {
//             e.printStackTrace();
//         }


     }

     @Override
     public void onBackPressed() {
         super.onBackPressed();
     }


     public void closeDialog() {
         try {
             if(simpleWebView!=null)
                 simpleWebView.setVisibility(View.VISIBLE);
             if (mDialog != null)
                 if (mDialog.isShowing())
                     mDialog.dismiss();
         } catch (Exception e) {
             e.printStackTrace();
         }
     }

     private class MyWebViewClient extends WebViewClient {
         @Override
         public boolean shouldOverrideUrlLoading(WebView view, String url) {
              Systems.out.println("chkkkk_urll_" + uri.getHost() + ":" + uri.getPort() + "___" + url);


             if (url.contains(SessionSave.getSession("api_base", SignUpWebviewAct.this) + "package_plan_success")) {
                  try {
                     String s = url.replaceAll(SessionSave.getSession("api_base", SignUpWebviewAct.this) + "package_plan_success/", "");
//                     s = new AA().dd(s);
//                     Systems.out.println("decrypted text" + s);

                     final JSONObject json = new JSONObject(s);
                     if (json.getString("status").equals("1")) {
                         new Handler().postDelayed(new Runnable() {
                             @Override
                             public void run() {


                                     try {

                                         if (true) {
                                             SessionSave.saveSession("trip_id", json.getJSONObject("detail").getString("passenger_tripid"), SignUpWebviewAct.this);
                                             SessionSave.saveSession("Pass_Tripid", json.getJSONObject("detail").getString("passenger_tripid"), SignUpWebviewAct.this);
                                             SessionSave.saveSession("request_time", json.getJSONObject("detail").getString("total_request_time"), SignUpWebviewAct.this);
                                             SessionSave.saveSession("Credit_Card", json.getJSONObject("detail").getString("credit_card_status"), SignUpWebviewAct.this);

                                         }

                                     } catch (final Exception e) {
                                          SessionSave.saveSession("trip_id", "", SignUpWebviewAct.this);
                                          e.printStackTrace();
                                     }
                             }
                         }, 500);

                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             } else if (url.contains(SessionSave.getSession("api_base", SignUpWebviewAct.this) + "back")) {
                 finish();
             } else {


                 view.loadUrl(url);
             }
             return true;
         }



         @Override
         public void onPageFinished(WebView view, String url) {

             super.onPageFinished(view, url);
             try {
                 if(simpleWebView!=null)
                     iv.setVisibility(View.GONE);
                 simpleWebView.setVisibility(View.VISIBLE);

             } catch (Exception exception) {
                 exception.printStackTrace();
             }

         }

         @Override
         public void onLoadResource(WebView view, String url) {

         }

         public void onPageStarted(WebView webView, String url, Bitmap favicon) {

             super.onPageStarted(webView, url, favicon);

         }

     }

 }
