package com.onepaytaxi.driver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.onepaytaxi.driver.Login.DriverLoginActivity;
import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.earnings.EarningsActivity;
import com.onepaytaxi.driver.homepage.HomePageActivity;
import com.onepaytaxi.driver.service.LocationUpdate;
import com.onepaytaxi.driver.utils.CL;
import com.onepaytaxi.driver.utils.CToast;

import com.onepaytaxi.driver.utils.NC;
import com.onepaytaxi.driver.utils.NetworkStatus;
import com.onepaytaxi.driver.utils.SessionSave;
import com.onepaytaxi.driver.utils.Systems;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import static com.onepaytaxi.driver.utils.GpsStatus.mDialog;

/**
 * Created by developer on 2/1/18.
 */
public class WebviewAct extends BaseActivity {
    WebView simpleWebView;
    String driverId, encodeSTr, link_1, link_2, link_2_attach, lang_Str;
    private String fromAct = "";
    private String type;
    private NetworkStatus networkStatus;
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR = 1;
    private boolean isFromEarningsAct = false;



    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_act);
        NetworkStatus.appContext = this;
        networkStatus = new NetworkStatus();
        registerReceiver(networkStatus, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        // initiate buttons and a web view
        if (getIntent() != null) {
            fromAct = getIntent().getStringExtra("fromMyStatus");
            isFromEarningsAct = getIntent().getBooleanExtra(CommonData.IS_FROM_EARNINGS, false);
            type = getIntent().getStringExtra("type");
        }
        simpleWebView = findViewById(R.id.simpleWebView);
        WebSettings webSettings = simpleWebView.getSettings();
        webSettings.setAllowFileAccess(true);
        simpleWebView.getSettings().setDomStorageEnabled(true);
        simpleWebView.getSettings().setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(0);
            simpleWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 19) {
            simpleWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < 19) {
            simpleWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webSettings.setJavaScriptEnabled(true);
        simpleWebView.setWebViewClient(new MyWebViewClient());
        simpleWebView.setWebChromeClient(new WebChromeClient() {
            //For Android 5.0+
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {
                if (mUMA != null) {
                    mUMA.onReceiveValue(null);
                }
                mUMA = filePathCallback;
                Uri imageUri;

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(WebviewAct.this.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCM);
                    } catch (IOException ex) {
                        Log.e("TAG ingrete", "Image file creation failed", ex);
                    }
                    if (photoFile != null) {
                        mCM = "file:" + photoFile.getAbsolutePath();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            imageUri = FileProvider.getUriForFile(WebviewAct.this,
                                    WebviewAct.this.getPackageName().concat(".files_root"),
                                    photoFile);
                        } else {
                            imageUri = Uri.fromFile(photoFile);
                        }
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    } else {
                        takePictureIntent = null;
                    }
                }
                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");
                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }
                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, FCR);
                return true;
            }
        });
        driverId = SessionSave.getSession("Id", WebviewAct.this);
        encodeSTr =driverId;
        String base64;

        byte[] data = new byte[0];
        data = driverId.getBytes(StandardCharsets.UTF_8);
        base64 = Base64.encodeToString(data, Base64.DEFAULT);
        encodeSTr = base64;

        lang_Str = SessionSave.getSession("Lang", WebviewAct.this);
        switch (type) {
            case "1":
                link_1 = SessionSave.getSession("api_base", WebviewAct.this) + "walletrecharge_syberpay.html/";
                break;
            case "12":
                Systems.out.println("chery_chkng_url_webview" + SessionSave.getSession("api_base", WebviewAct.this));
                link_2 = SessionSave.getSession("api_base", WebviewAct.this) + "become_driver_mobile.html" + "?v=1";
                break;
            case "delete":
                link_1 = SessionSave.getSession("api_base", WebviewAct.this) + "delete_driver_account.html";
                break;
            case CommonData.HELP_URL:
                link_1 = SessionSave.getSession(CommonData.HELP_URL, this);
                break;
            default:
                link_1 = SessionSave.getSession("api_base", WebviewAct.this) + "current_plan.html/";
                break;
        }
        String btnAct = "";
        String btnRjt = "";
        try {
            btnAct = Integer.toHexString(CL.getColor(R.color.btn_accept_primary)).substring(2);
            btnRjt = Integer.toHexString(CL.getColor(R.color.btn_reject_secondary)).substring(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (btnAct.equals(""))
                btnAct = Integer.toHexString(ContextCompat.getColor(this, R.color.btn_accept_primary)).substring(2);
            if (btnRjt.equals(""))
                btnRjt = Integer.toHexString(ContextCompat.getColor(this, R.color.btn_reject_secondary)).substring(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String colorCode = "&b_act=" + btnAct + "&b_cal=" + btnRjt + "&new=1";
        if (type.equals("delete")) {
            link_2_attach = link_1 + "/" + encodeSTr + "?lang=" + lang_Str;
        } else if (type.equals(CommonData.HELP_URL)) {
            link_2_attach = link_1 + "?" + encodeSTr + "&lang=" + lang_Str;
        } else {
            link_2_attach = link_1 + encodeSTr + "/?lang=" + lang_Str;
        }
        link_2_attach = link_2_attach + colorCode;
        if (type.equals("12")) {
            simpleWebView.loadUrl(link_2 + "&lang=" + lang_Str + colorCode);
        } else {
            simpleWebView.loadUrl(link_2_attach);
        }
        showDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;
            //Check if response is positive
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FCR) {
                    if (null == mUMA) {
                        return;
                    }
                    if (intent == null || intent.getData() == null) {
                        //Capture Photo if no image available
                        if (mCM != null) {
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        } else {
            if (requestCode == FCR) {
                if (null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }

    public void showDialog() {
        try {
            if (NetworkStatus.isOnline(WebviewAct.this)) {
                if (mDialog != null)
                    mDialog.dismiss();
                View view = View.inflate(WebviewAct.this, R.layout.progress_bar, null);
                mDialog = new Dialog(WebviewAct.this, R.style.dialogwinddow);

                mDialog.setContentView(view);
                mDialog.setCancelable(false);
                try {
                    mDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ImageView iv = mDialog.findViewById(R.id.giff);
                DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(iv);
                Glide.with(WebviewAct.this)
                        .load(R.raw.loading_anim)
                        .into(imageViewTarget);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        //To check whether it is came from MyStatus Activity
        if (!TextUtils.isEmpty(fromAct)) {
            startActivity(new Intent(WebviewAct.this, HomePageActivity.class));
            finish();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && simpleWebView.canGoBack()) {
            if (type.equals("12")) {
                simpleWebView.goBack();
            } else if (type.equals("1")) {
                startActivity(new Intent(WebviewAct.this, EarningsActivity.class));
                finish();
            }
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    public void closeDialog() {
        try {
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
            if (url.contains(SessionSave.getSession("api_base", WebviewAct.this) + "back.html")) {
                if (!TextUtils.isEmpty(fromAct)) {
                    startActivity(new Intent(WebviewAct.this, HomePageActivity.class));
                    finish();
                } else if (isFromEarningsAct) {
                    startActivity(new Intent(WebviewAct.this, EarningsActivity.class));
                    finish();
                } else if (!isFromEarningsAct) {
                    startActivity(new Intent(WebviewAct.this, FarecalcAct.class));
                    finish();
                } else {
                    callProfile();
                }
            } else if (url.contains(SessionSave.getSession("api_base", WebviewAct.this) + "success.html")) {
                if (!TextUtils.isEmpty(fromAct)) {
                    startActivity(new Intent(WebviewAct.this, HomePageActivity.class));
                    finish();
                } else if (isFromEarningsAct) {
                    startActivity(new Intent(WebviewAct.this, EarningsActivity.class));
                    finish();
                } else if (!isFromEarningsAct) {
                    startActivity(new Intent(WebviewAct.this, FarecalcAct.class));
                    finish();
                } else {
                    callProfile();
                }
            } else if (url.contains(SessionSave.getSession("api_base", WebviewAct.this) + "add_success.html")) {
                try {
                    Uri uri = Uri.parse(url);
                    String msg = uri.getQueryParameter("msg");
                    msg = NC.nfields_byName.get(msg);
                    final Intent i = new Intent();
                    i.putExtra("bok_driver", msg);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (url.contains(SessionSave.getSession("api_base", WebviewAct.this) + "delete_success")) {
                Toast.makeText(WebviewAct.this, NC.getString(R.string.account_deleted_success), Toast.LENGTH_LONG).show();
                SessionSave.saveSession(CommonData.LOGOUT, true, WebviewAct.this);
                stopService(new Intent(WebviewAct.this, LocationUpdate.class));
                int length = CommonData.mActivitylist.size();
                if (length != 0) {
                    for (int i = 0; i < length; i++) {
                        CommonData.mActivitylist.get(i).finish();
                    }
                }
                Intent intent = new Intent(WebviewAct.this, DriverLoginActivity.class);
                startActivity(intent);
                finish();
            } else if (url.contains(SessionSave.getSession("api_base", WebviewAct.this) + "back")) {
                finish();
            } else if (url.contains(SessionSave.getSession("api_base", WebviewAct.this) + "cancel")) {
                finish();
            } else if (url.contains(SessionSave.getSession("api_base", WebviewAct.this) + "drivertermsconditions.html")) {
                openBrowserAndLoadURL(SessionSave.getSession("api_base", WebviewAct.this) + "drivertermsconditions.html");
            } else if (url.contains(SessionSave.getSession("api_base", WebviewAct.this) + "driverprivacypolicy.html")) {
                openBrowserAndLoadURL(SessionSave.getSession("api_base", WebviewAct.this) + "driverprivacypolicy.html");
            } else {
                view.loadUrl(url);
            }
            return true;
        }

        private void callProfile() {

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            try {
                closeDialog();
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

    private void openBrowserAndLoadURL(String urlToLoad) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToLoad));
            startActivity(intent);
        } catch (Exception e) {
            CToast.ShowToast(WebviewAct.this, e.getMessage());
        }
    }

    private File createImageFile() throws IOException {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = CommonData.getDateForCreateImageFile();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(networkStatus);
        super.onDestroy();
    }
}