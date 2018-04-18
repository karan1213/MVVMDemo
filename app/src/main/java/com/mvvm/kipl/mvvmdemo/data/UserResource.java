package com.mvvm.kipl.mvvmdemo.data;

import android.arch.lifecycle.MediatorLiveData;

import com.mvvm.kipl.mvvmdemo.data.api.ApiService;
import com.mvvm.kipl.mvvmdemo.data.api.Status;
import com.mvvm.kipl.mvvmdemo.vo.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 4/16/2018.
 */

public class UserResource {
    ApiService mApiService;
    static UserResource mUserResource;

    public UserResource(ApiService apiService) {
        mApiService = apiService;
    }

    public static UserResource getInstance(ApiService apiService) {
        if (mUserResource == null) {
            mUserResource = new UserResource(apiService);
        }
        return new UserResource(apiService);
    }

    public MediatorLiveData<Resource<User>> login(String userName) {
        final MediatorLiveData<Resource<User>> mUserLiveData = new MediatorLiveData<>();
        mUserLiveData.postValue(new Resource<User>(Status.LOADING));
        if (mApiService == null) {
            mUserLiveData.postValue(new Resource<User>(Status.ERROR));
            return mUserLiveData;
        }
        mApiService.getUser(userName)
            .enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    mUserLiveData.postValue(new Resource<>(response));
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    mUserLiveData.postValue(new Resource<User>(t));
                }
            });

        return mUserLiveData;
    }
}
