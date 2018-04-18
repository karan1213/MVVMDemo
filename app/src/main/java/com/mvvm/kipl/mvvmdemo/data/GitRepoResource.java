package com.mvvm.kipl.mvvmdemo.data;

import android.arch.lifecycle.MediatorLiveData;

import com.mvvm.kipl.mvvmdemo.data.api.ApiService;
import com.mvvm.kipl.mvvmdemo.data.api.Status;
import com.mvvm.kipl.mvvmdemo.vo.Contributor;
import com.mvvm.kipl.mvvmdemo.vo.Repo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 4/16/2018.
 */

public class GitRepoResource {

    ApiService mApiService;
    static GitRepoResource sGitRepoResource;

    public GitRepoResource(ApiService apiService) {
        mApiService = apiService;
    }

    public static GitRepoResource getInstance(ApiService apiService) {
        if (sGitRepoResource == null) {
            sGitRepoResource = new GitRepoResource(apiService);
        }
        return sGitRepoResource;
    }


    public MediatorLiveData<Resource<List<Repo>>> fetchUserRepos(String userName){
        final MediatorLiveData<Resource<List<Repo>>> mUserRepositoriesLiveData = new
            MediatorLiveData<>();
        mUserRepositoriesLiveData.postValue(new Resource<List<Repo>>(Status.LOADING));
        if (mApiService == null) {
            mUserRepositoriesLiveData.postValue(new Resource<List<Repo>>(Status.ERROR));
            return mUserRepositoriesLiveData;
        }
        mApiService.getRepos(userName)
            .enqueue(new Callback<List<Repo>>() {
                @Override
                public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
                    mUserRepositoriesLiveData.postValue(new Resource<>(response));
                }

                @Override
                public void onFailure(Call<List<Repo>> call, Throwable t) {
                    mUserRepositoriesLiveData.postValue(new Resource<List<Repo>>(t));
                }
            });

        return mUserRepositoriesLiveData;
    }

    public MediatorLiveData<Resource<Repo>> fetchRepo(String repoOwner, String repoName){
        final MediatorLiveData<Resource<Repo>> mUserRepositoryLiveData = new
            MediatorLiveData<>();
        mUserRepositoryLiveData.postValue(new Resource<Repo>(Status.LOADING));
        if (mApiService == null) {
            mUserRepositoryLiveData.postValue(new Resource<Repo>(Status.ERROR));
            return mUserRepositoryLiveData;
        }
        mApiService.getRepo(repoOwner, repoName)
            .enqueue(new Callback<Repo>() {
                @Override
                public void onResponse(Call<Repo> call, Response<Repo> response) {
                    mUserRepositoryLiveData.postValue(new Resource<>(response));
                }

                @Override
                public void onFailure(Call<Repo> call, Throwable t) {
                    mUserRepositoryLiveData.postValue(new Resource<Repo>(t));
                }
            });

        return mUserRepositoryLiveData;
    }

    public MediatorLiveData<Resource<List<Contributor>>> fetchContributors(String repoOwner,
                                                                           String repoName){
        final MediatorLiveData<Resource<List<Contributor>>> mUserRepositoriesLiveData = new
            MediatorLiveData<>();
        mUserRepositoriesLiveData.postValue(new Resource<List<Contributor>>(Status.LOADING));
        if (mApiService == null) {
            mUserRepositoriesLiveData.postValue(new Resource<List<Contributor>>(Status.ERROR));
            return mUserRepositoriesLiveData;
        }
        mApiService.getContributors(repoOwner, repoName)
            .enqueue(new Callback<List<Contributor>>() {
                @Override
                public void onResponse(Call<List<Contributor>> call, Response<List<Contributor>> response) {
                    mUserRepositoriesLiveData.postValue(new Resource<>(response));
                }

                @Override
                public void onFailure(Call<List<Contributor>> call, Throwable t) {
                    mUserRepositoriesLiveData.postValue(new Resource<List<Contributor>>(t));
                }
            });

        return mUserRepositoriesLiveData;
    }
}
