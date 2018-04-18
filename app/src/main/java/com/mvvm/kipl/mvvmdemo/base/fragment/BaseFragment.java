package com.mvvm.kipl.mvvmdemo.base.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mvvm.kipl.mvvmdemo.Application;
import com.mvvm.kipl.mvvmdemo.R;
import com.mvvm.kipl.mvvmdemo.data.api.response.BaseResponse;
import com.mvvm.kipl.mvvmdemo.ui.activity.LoginActivity;
import com.mvvm.kipl.mvvmdemo.util.Constant;
import com.mvvm.kipl.mvvmdemo.util.StringUtility;

import retrofit2.Response;

public class BaseFragment extends Fragment {

    public static final int REQUEST_LOCATION = 1006;
    private static final long POLLING_FREQ = 1000 * 30;
    private static final long FASTEST_UPDATE_FREQ = 1000 * 5;
    ProgressDialog progressDialog;
    LocationListener locationListener = null;
//    LocationRequest mLocationRequest;

    public BaseFragment() {

    }

    public static <T extends BaseFragment> T getInstance(@Nullable Bundle args, Class<T> tClass) {
        try {
            T t = tClass.newInstance();
            t.setArguments(args);
            return t;
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
    }

    public void replaceChildFragment(@IdRes int container, Fragment fragment) {
        getChildFragmentManager().beginTransaction().replace(container, fragment).commit();
    }

    public void replaceFragment(@IdRes final int container, final Fragment fragment) {
        final FrameLayout frameLayout = (FrameLayout) getActivity().findViewById(container);
        frameLayout.removeAllViewsInLayout();
        frameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(frameLayout.getId(), fragment).addToBackStack(null).commit();
                frameLayout.forceLayout();
            }
        }, 300);
    }

    public void setTitle(CharSequence title) {
        getActivity().setTitle(StringUtility.validateString(title.toString()) ? title : "");
    }

    public void setTitle(@StringRes int title) {
        getActivity().setTitle(title);
    }

    public void log(String message) {
        Log.d(getClass().getSimpleName(), message);
    }

    public Application getApplicationClass() {
        return (Application) getActivity().getApplication();
    }

    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    public boolean isCurrentUser(String id) {
        return getPreferences().getString(Constant.KEY_USER_ID, "").equals(id);
    }

    public void toast(final String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void toast(@StringRes final int message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public ContentResolver getContentResolver() {
        return getContext().getContentResolver();
    }


    public void enableLoadingBar(boolean enable) {
        if (enable) {
            loadProgressBar(null, getString(R.string.loading), false);
        } else {
            dismissProgressBar();
        }
    }

    public void loadProgressBar(String title, String message, boolean cancellable) {
        if (progressDialog == null && !getActivity().isFinishing())
            progressDialog = ProgressDialog.show(getActivity(), title, message, false, cancellable);
    }

    public void dismissProgressBar() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    public AlertDialog.Builder getAlertDialogBuilder(String title, String message, boolean cancellable) {
        return new AlertDialog.Builder(getActivity(), R.style.AppTheme_AlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(cancellable);
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

    /*Pass null errorMessage to disable error view*/
    public void setFieldError(EditText edtField, String errorMessage) {
        if (edtField != null) {
            edtField.setError(errorMessage);
        }
    }

    public void onError(String reason) {
        if (getContext() == null) {
            return;
        }
        if (getUserVisibleHint()) {
            if (StringUtility.validateString(reason)) {
                getAlertDialogBuilder(null, reason, false).setPositiveButton(getString(R.string.ok), null).show();
            } else {
                getAlertDialogBuilder(null, getString(R.string.default_error), false)
                        .setPositiveButton(getString(R.string.ok), null).show();
            }
        }
    }

    public void onInfo(String message) {
        onInfo(message, false);
    }

    public void onInfo(String message, boolean finishOnOk) {
        getAlertDialogBuilder(null, message, false).setPositiveButton(getString(R.string.ok), finishOnOk ? new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().finish();
            }
        } : null).show();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }


    // ----------------------------- Current Location Code Start --------------------------------//
    /*private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
    };
    public Location getLastLocation() {
        return mLastLocation;
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
    * then call getLastLocation to get location
    * passing LocationListener as parameter will use our listener
    * passing null as parameter will initialise its own listener*//*

    protected void initLocationListener(com.google.android.gms.location.LocationListener locationListener) {

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
                mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                        .addConnectionCallbacks(connectionCallbacks)
                        .addOnConnectionFailedListener(connectionFailedListener)
                        .addApi(LocationServices.API)
                        .build();
            }
            mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //locationRequest.setInterval(30 * 1000);
        //locationRequest.setFastestInterval(5 * 1000);
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
                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                         getActivity().finish();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }

    public boolean askListOfPermissions(String[] permissions, int requestCode) {
        boolean isOk = true;
        StringBuilder perNeed = new StringBuilder();
        for (String per : permissions) {
            if (!(ActivityCompat.checkSelfPermission(getActivity(), per) == PackageManager.PERMISSION_GRANTED)) {
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
            ActivityCompat.requestPermissions(getActivity(), permissions, requestCode);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isGranted = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
                break;
            }
        }
        if (isGranted) {
            if (requestCode == REQUEST_LOCATION) {
                //initLocationListener(this.locationListener);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // ------------------------------ Current Location Code End --------------------------------//

    public void hideKeyBoard() {
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().findViewById(android.R.id.content).getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String getCurrentLanguage() {
        return getResources().getConfiguration().locale.getCountry();
    }

    public void handleError(BaseResponse response) {
        //todo handle error codes
        if (response != null) {
            try {
                onError(response.message);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            onError(null);
        }
    }

    public void handleError(Response response) {
        //todo handle error codes
        if(response.code()==203){
            handleError(((BaseResponse) response.body()));
        }else if(response.code() == 440){
            getPreferences().edit().clear().commit();
            startActivity(new Intent(getActivity(), LoginActivity.class).addFlags(Intent
                .FLAG_ACTIVITY_CLEAR_TASK));
        }else if (response.errorBody() != null) {
            try {
                String error = response.errorBody().string();
                BaseResponse errorResponse = new Gson().fromJson(error, BaseResponse.class);
                handleError(errorResponse);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            onError(null);
        }
    }

}


