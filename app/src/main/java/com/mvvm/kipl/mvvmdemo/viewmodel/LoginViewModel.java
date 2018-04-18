package com.mvvm.kipl.mvvmdemo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.mvvm.kipl.mvvmdemo.data.api.Status;
import com.mvvm.kipl.mvvmdemo.data.Resource;
import com.mvvm.kipl.mvvmdemo.data.UserResource;
import com.mvvm.kipl.mvvmdemo.vo.User;

/**
 * Created by Admin on 2/21/2018.
 */

public class LoginViewModel extends AndroidViewModel {


    MediatorLiveData<Resource<User>> mUserLiveData;
    UserResource mUserResource = null;

    public LoginViewModel(Application application) {
        super(application);

        mUserResource = UserResource.getInstance(((com.mvvm.kipl.mvvmdemo.Application)
            getApplication()).getApiService());

        //initializing observable
        mUserLiveData = new MediatorLiveData<>();

        //setting empty value in observable
        mUserLiveData.setValue(new Resource<User>(Status.IDLE));

        // observe the changes of the products from the database and forward them
        //mUserRepositoriesLiveData.addSource(products, mObservableProducts::setValue);
    }

    public MediatorLiveData<Resource<User>> getUserLiveData() {
        return mUserLiveData;
    }

    public void login(String userName) {
        if (mUserResource == null) {
            mUserLiveData.postValue(new Resource<User>(Status.ERROR, "Initialize " +
                "UserResource " + mUserResource));
            return;
        }
        mUserLiveData.addSource(mUserResource.login(userName), new Observer<Resource<User>>() {
            @Override
            public void onChanged(@Nullable Resource<User> userResource) {
                mUserLiveData.postValue(userResource);
            }
        });
    }
}
