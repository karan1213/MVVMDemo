package com.mvvm.kipl.mvvmdemo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mvvm.kipl.mvvmdemo.data.api.Status;
import com.mvvm.kipl.mvvmdemo.data.Resource;
import com.mvvm.kipl.mvvmdemo.data.GitRepoResource;
import com.mvvm.kipl.mvvmdemo.util.StringUtility;
import com.mvvm.kipl.mvvmdemo.vo.Repo;

import java.util.List;

/**
 * Created by Admin on 2/22/2018.
 */

public class UserRepositoryViewModel extends AndroidViewModel {

    MediatorLiveData<Resource<List<Repo>>> mUserRepositoriesLiveData;

    private String userName;
    private GitRepoResource mGitRepoResource;

    public UserRepositoryViewModel(@NonNull Application application) {
        super(application);

        mGitRepoResource = GitRepoResource.getInstance(((com.mvvm.kipl.mvvmdemo.Application)
            application).getApiService());

        //initializing observable
        mUserRepositoriesLiveData = new MediatorLiveData<>();

        //setting empty value in observable
        mUserRepositoriesLiveData.setValue(new Resource<List<Repo>>(Status.IDLE));

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public MediatorLiveData<Resource<List<Repo>>> getUserRepositoriesLiveData() {
        return mUserRepositoriesLiveData;
    }

    public void setUserRepositoriesLiveData(MediatorLiveData<Resource<List<Repo>>> userRepositoriesLiveData) {
        mUserRepositoriesLiveData = userRepositoriesLiveData;
    }

    public void fetchUserRepos() {
        if (mGitRepoResource == null) {
            mUserRepositoriesLiveData.postValue(new Resource<List<Repo>>(Status.ERROR));
            return;
        }
        if (!StringUtility.validateString(userName)){
            mUserRepositoriesLiveData.postValue(new Resource<List<Repo>>(Status.ERROR,
                "Invalid User " + userName));
            return;
        }
        mUserRepositoriesLiveData.addSource(mGitRepoResource.fetchUserRepos(userName), new
            Observer<Resource<List<Repo>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Repo>> userResource) {
                mUserRepositoriesLiveData.postValue(userResource);
            }
        });

    }
}
