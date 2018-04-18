package com.mvvm.kipl.mvvmdemo.base.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mvvm.kipl.mvvmdemo.Application;
import com.mvvm.kipl.mvvmdemo.BuildConfig;
import com.mvvm.kipl.mvvmdemo.R;
import com.mvvm.kipl.mvvmdemo.data.api.response.BaseResponse;
import com.mvvm.kipl.mvvmdemo.ui.activity.LoginActivity;
import com.mvvm.kipl.mvvmdemo.util.Constant;
import com.mvvm.kipl.mvvmdemo.util.NetworkUtil;
import com.mvvm.kipl.mvvmdemo.util.StringUtility;
import com.mvvm.kipl.mvvmdemo.util.SystemUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;


public abstract class BaseActivity extends AppCompatActivity {

    public final static int REQUEST_PICKUP_LOCATION = 101;
    public final static int REQUEST_DROP_LOCATION = 102;
    public final static int REQUEST_ADD_WORK = 103;
    public final static int REQUEST_ADD_HOME = 104;
    public final static int REQUEST_EDIT_WORK = 107;
    public final static int REQUEST_EDIT_HOME = 108;
    public final static int REQUEST_SEARCH_LOCATION = 105;
    public final static int REQUEST_SET_PICKUP_LOCATION = 106;
    public static final int REQUEST_LOCATION = 109;
    public static final int REQ_CHANGE_PAYMENT_METHOD = 110;
    public static final int REQUEST_LOCATION_SUCCESS = 111;
    public static final int REQUEST_GPS = 114;
    private static final long POLLING_FREQ = 1000 * 30;
    private static final long FASTEST_UPDATE_FREQ = 1000 * 5;
    public static final int REQUEST_CAPTURE = 112;
    public static final int REQUEST_GALLERY = 113;
    public static final int REQUEST_ADD_CARD = 114;
    public static final String ACTIVITY_STATUS = "activity_status";

    ProgressDialog progressDialog;;
    /*LocationRequest mLocationRequest;*/
    LocationListener locationListener = null;
    private Snackbar snackbar;
    /*private GoogleApiClient mGoogleApiClient;*/
    private Location mLastLocation;
    public boolean isGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!NetworkUtil.isOnline(this)) {
            snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.error_interent_message, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        } else {
            if (snackbar != null) {
                snackbar.dismiss();
            }
        }
        NetworkUtil.addNetworkListener(new NetworkUtil.INetworkUtil() {
            @Override
            public void onNetworkChange(boolean available) {
                if (!available) {
                    snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.error_interent_message, Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                } else {
                    if (snackbar != null) {
                        snackbar.dismiss();
                    }
                }
            }
        });
        if (BuildConfig.DEBUG) {
            setVersionOnScreen();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressBar();
        //destroyGoogleApiClient();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GPS && resultCode == RESULT_OK) {
//            initLocationListener(this.locationListener);
        } else if (requestCode == REQUEST_CAPTURE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    FileOutputStream out = null;
                    try {
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        String mediaPath = File.createTempFile("image", ".png", new File(SystemUtility.getTempMediaDirectory(this))).getAbsolutePath();
                        out = new FileOutputStream(mediaPath);
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                        cropImage(Uri.fromFile(new File(mediaPath)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (out != null) {
                                out.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (captureMediaFile != null) {
                    cropImage(captureMediaFile);
                }
            }
        } else if (requestCode == REQUEST_GALLERY && data != null) {
            cropImage(data.getData());
        } else {

        }

    }

    //Get Application Class Instance
    public Application getApplicationClass() {
        return (Application) getApplication();
    }

    //Get Shared Preferences
    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    //Set Version on Screen
    public void setVersionOnScreen() {
        TextView tvSample = new TextView(this);
        tvSample.setTextColor(Color.RED);
        tvSample.setPadding(4, 4, 4, 4);
        tvSample.setGravity(Gravity.BOTTOM);
        tvSample.setText(BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")");
        ((ViewGroup) findViewById(android.R.id.content)).addView(tvSample);
    }

    //Get Class Name
    public String getTag() {
        return getClass().getSimpleName();
    }

    //Log
    public void log(String message) {
        Log.d(getTag(), message);
    }

    //Alert Dialog
    public AlertDialog.Builder getAlertDialogBuilder(String title, String message, boolean cancellable) {
        return new AlertDialog.Builder(this, R.style.AppTheme_AlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(cancellable);
    }

    public void enableLoadingBar(boolean enable) {
        if (enable) {
            loadProgressBar(null, getString(R.string.loading), false);
        } else {
            dismissProgressBar();
        }
    }

    public void loadProgressBar(String title, String message, boolean cancellable) {
        if (progressDialog == null && !this.isFinishing())
            progressDialog = ProgressDialog.show(this, title, message, false, cancellable);
    }

    public void dismissProgressBar() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }



    public void onError(String reason) {
        onError(reason, false);
    }

    public void onError(String reason, final boolean finishOnOk) {
        if (!((Activity) this).isFinishing()) {
            if (StringUtility.validateString(reason)) {
                getAlertDialogBuilder(null, reason, false).setPositiveButton(getString(R.string.ok), finishOnOk ? new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                } : null).show();
            } else {
                getAlertDialogBuilder(null, getString(R.string.default_error), false)
                        .setPositiveButton(getString(R.string.ok), finishOnOk ? new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        } : null).show();
            }
        }
        enableLoadingBar(false);
    }

    public void onInfo(String message) {
        onInfo(message, false);
    }

    public void onInfo(String message, boolean finishOnOk) {
        if (!((Activity) this).isFinishing()) {
            getAlertDialogBuilder(null, message, false).setPositiveButton(getString(R.string.ok), finishOnOk ? new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            } : null).show();
        }
    }

    /*Pass null errorMessage to disable error view*/
    public void setFieldError(TextInputLayout tilField, String errorMessage) {
        if (tilField != null) {
            if (StringUtility.validateString(errorMessage)) {
                tilField.setError(errorMessage);
            } else {
                tilField.setErrorEnabled(false);
            }
        }
    }

    public void hideKeyBoard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void configureToolBar(@NonNull final Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
//                Typeface myCustomFont = Typeface.createFromAsset(getAssets(), "fonts/" + getString(R.string.circular_std_bold));
//                textView.setTypeface(myCustomFont);
//                textView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            }
        }

    }

    public void configureToolBarBlack(@NonNull final Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
//                Typeface myCustomFont = Typeface.createFromAsset(getAssets(), "fonts/" + getString(R.string.circular_std_bold));
//                textView.setTypeface(myCustomFont);
//                textView.setTextColor(ContextCompat.getColor(this, R.color.black));
            }
        }

    }


    public void handleBackButtonEvent(@NonNull final Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    /*returns true if error is handled*/
    public boolean handleError(BaseResponse response, boolean success) {
        if (response != null && response.meta != null) {
            if (response.meta.forceUpdate) {
                getAlertDialogBuilder(null, getString(R.string.force_update_message), false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).show();
                return true;
            } /*else if (response.meta.hasUpdate) {
                getAlertDialogBuilder(null, getString(R.string.force_update_message), false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton(R.string.cancel, null).show();
                return true;
            }*/
        }
        if (success){
            return false;
        }else {
            onError(response != null ? response.message : null);
            return true;
        }

    }

    /*returns true if error is handled*/
    public boolean handleError(retrofit2.Response response) {
        if (response.code() == 203) {
            return handleError(((BaseResponse) response.body()), false);
        } else if (response.code() == 440) {
            getPreferences().edit().clear().commit();
            startActivity(new Intent(this, LoginActivity.class).addFlags(Intent
                .FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
            return true;
        } else if (response.errorBody() != null) {
            try {
                String error = response.errorBody().string();
                BaseResponse errorResponse = new Gson().fromJson(error, BaseResponse.class);
                return handleError(errorResponse, false);
            } catch (Exception e) {
                e.printStackTrace();
                onError(null);
                return true;
            }
        } else {
            return handleError(((BaseResponse) response.body()), response.code() == 200);
        }
    }

    protected String getCurrentLanguage() {
        return getResources().getConfiguration().locale.getLanguage();
    }

    public Location getLastLocation() {
        return mLastLocation;
    }

    /*protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, locationListener);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, locationListener);
    }

    *//*call this method in onCreate to start and listen to location updates
    * then call getLastLocation to get location*//*
    protected void initLocationListener(LocationListener locationListener) {
        if (locationListener != null) {
            this.locationListener = locationListener;
        } else {
            this.locationListener = new com.google.android.gms.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    log(location.toString());
                    mLastLocation = location;
                }
            };
        }
        if (!askListOfPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION))
            return;
        destroyGoogleApiClient();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(connectionCallbacks)
                    .addOnConnectionFailedListener(connectionFailedListener)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(BaseActivity.this, REQUEST_GPS);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SUCCESS:
                        try {
                            status.startResolutionForResult(BaseActivity.this, REQUEST_GPS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
    }

    private void destroyGoogleApiClient() {
        if (mGoogleApiClient != null) {
            try {
                stopLocationUpdates();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mGoogleApiClient.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                mGoogleApiClient = null;
                mLastLocation = null;
            }
        }
    }*/

    public boolean askListOfPermissions(String[] permissions, int requestCode) {
        boolean isOk = true;
        StringBuilder perNeed = new StringBuilder();
        for (String per : permissions) {
            if (!(ActivityCompat.checkSelfPermission(this, per) == PackageManager.PERMISSION_GRANTED)) {
                if (isOk)
                    isOk = false;
                perNeed.append(per);
                perNeed.append(",");
            }
        }
        if (isOk) {
            return true;
        }

        String reqPermissions = (perNeed.length() > 1 ? perNeed.substring(0, perNeed.length() - 1).toString() : "");
        if (!reqPermissions.isEmpty()) {
            String arrPer[] = reqPermissions.split(",");
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }
        return false;
    }

    /*private GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            if (ActivityCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(POLLING_FREQ);
            mLocationRequest.setFastestInterval(FASTEST_UPDATE_FREQ);
            startLocationUpdates();
        }

        @Override
        public void onConnectionSuspended(int i) {
            //do nothing
        }
    };
    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        }
    };*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        isGranted = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
                break;
            }
        }
        if (isGranted) {
            if (requestCode == REQUEST_LOCATION) {
                //initLocationListener(this.locationListener);
            } else if (requestCode == REQUEST_CAPTURE) {
                pickImageFromCamera();
            } else if (requestCode == REQUEST_GALLERY) {
                pickImageFromGallery();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    // ------------------------------ Current Location Code End --------------------------------//


    Uri captureMediaFile = null;

    //Capture Image
    public void dialogImagePicker() { //TO pick image from gallery and camera
        AlertDialog.Builder imagePicker = new AlertDialog.Builder(this, R.style.AppTheme_AlertDialog);
        imagePicker.setItems(new String[]{getString(R.string.gallery), getString(R.string.camera)}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                switch (which) {
                    case 0: //Gallery
                        pickImageFromGallery();
                        break;
                    case 1: // Camera
                        pickImageFromCamera();
                        break;
                    default:
                        break;
                }
            }
        }).setCancelable(true).setTitle(getString(R.string.select_image)).show();
    }

    //Pick Image From Gallery
    public void pickImageFromGallery() {
        if (!askListOfPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY))
            return;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image*//**//*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), REQUEST_GALLERY);
    }

    //Pick Image From Camera
    public void pickImageFromCamera() {
        if (!askListOfPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CAPTURE))
            return;
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File tempFile = File.createTempFile("image", ".png", new File(SystemUtility.getTempMediaDirectory(this)));
            captureMediaFile = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, captureMediaFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    //Crop Image
    public void cropImage(Uri sourceFileUri) {
        try {
            Uri sourceURI = sourceFileUri;
            Uri destinationURI = Uri.fromFile(File.createTempFile("cropped", ".png", this.getCacheDir()));
            //Crop.of(sourceURI, destinationURI).asSquare().start(this);
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
    }

    public void toast(final String message) {
        Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void toast(@StringRes final int message) {
        Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void replaceFragment(@IdRes int container, Fragment fragment) {
        replaceFragment(container, fragment, null);
    }

    public void replaceFragment(@IdRes final int container, final Fragment fragment, Bundle arguments) {
        if (arguments != null) {
            fragment.setArguments(arguments);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction().replace(container, fragment).commit();
            }
        }, 300);
    }

    public boolean isCurrentUser(String id) {
        return getPreferences().getString(Constant.KEY_USER_ID, "").equals(id);
    }

    public void snackBar(View view, @StringRes final int message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public void snackBarLong(View view, @StringRes final int message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    /*public String GetCountryZipCode(){
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        if (!StringUtility.validateString(CountryID)) {
            CountryID = manager.getNetworkCountryIso().toUpperCase();
        }
        if (!StringUtility.validateString(CountryID)) {
            CountryID = Locale.getDefault().getCountry();
        }
        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode.startsWith("+") ? CountryZipCode : "+" + CountryZipCode;
    }*/

    public static void updateLanguage(Context ctx, String lang)
    {
        Configuration cfg = new Configuration();
        if (!TextUtils.isEmpty(lang))
            cfg.locale = new Locale(lang);
        else
            cfg.locale = Locale.getDefault();

        ctx.getResources().updateConfiguration(cfg, null);
    }
}
