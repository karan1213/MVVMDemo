package com.mvvm.kipl.mvvmdemo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mvvm.kipl.mvvmdemo.data.api.Status;
import com.mvvm.kipl.mvvmdemo.data.Resource;
import com.mvvm.kipl.mvvmdemo.data.GitRepoResource;
import com.mvvm.kipl.mvvmdemo.util.StringUtility;
import com.mvvm.kipl.mvvmdemo.vo.Contributor;
import com.mvvm.kipl.mvvmdemo.vo.Repo;

import java.util.List;

/**
 * Created by Admin on 2/22/2018.
 */

public class RepoViewModel extends AndroidViewModel {

    private MediatorLiveData<Resource<Repo>> mResponseLiveData;



    private MediatorLiveData<Resource<List<Contributor>>> mContributorLiveData;
    private String repoOwner, repoName;
    private GitRepoResource mGitRepoResource;



    public RepoViewModel(@NonNull Application application) {
        super(application);

        mGitRepoResource = GitRepoResource.getInstance(((com.mvvm.kipl.mvvmdemo.Application)
            application).getApiService());

        //initializing observable
        mResponseLiveData = new MediatorLiveData<>();
        mContributorLiveData = new MediatorLiveData<>();

        //setting empty value in observable
        mResponseLiveData.setValue(new Resource<Repo>(Status.IDLE));
        mContributorLiveData.setValue(new Resource<List<Contributor>>(Status.IDLE));

    }

    public LiveData<Resource<Repo>> getResponseLiveData() {
        return mResponseLiveData;
    }

    public MediatorLiveData<Resource<List<Contributor>>> getContributorLiveData() {
        return mContributorLiveData;
    }

    public void setResponseLiveData(MediatorLiveData<Resource<Repo>> responseLiveData) {
        mResponseLiveData = responseLiveData;
    }

    public void setRepoOwner(String repoOwner) {
        this.repoOwner = repoOwner;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public void fetchRepo() {
        if (mGitRepoResource == null) {
            mResponseLiveData.postValue(new Resource<Repo>(Status.ERROR));
            return;
        }
        if (!StringUtility.validateString(repoOwner) || !StringUtility.validateString(repoName)){
            mResponseLiveData.postValue(new Resource<Repo>(Status.ERROR,
                "Invalid RepoOwner " + repoOwner + "  or RepoName " + repoName));
            return;
        }
        mResponseLiveData.addSource(mGitRepoResource.fetchRepo(repoOwner, repoName), new
            Observer<Resource<Repo>>() {
                @Override
                public void onChanged(@Nullable Resource<Repo> userResource) {
                    mResponseLiveData.postValue(userResource);
                }
            });

    }

    public void fetchContributors() {
        if (mGitRepoResource == null) {
            mContributorLiveData.postValue(new Resource<List<Contributor>>(Status.ERROR));
            return;
        }
        if (!StringUtility.validateString(repoOwner) || !StringUtility.validateString(repoName)){
            mContributorLiveData.postValue(new Resource<List<Contributor>>(Status.ERROR,
                "Invalid RepoOwner " + repoOwner + "  or RepoName " + repoName));
            return;
        }
        mContributorLiveData.addSource(mGitRepoResource.fetchContributors(repoOwner, repoName), new
            Observer<Resource<List<Contributor>>>() {
                @Override
                public void onChanged(@Nullable Resource<List<Contributor>> userResource) {
                    mContributorLiveData.postValue(userResource);
                }
            });


    }
}
