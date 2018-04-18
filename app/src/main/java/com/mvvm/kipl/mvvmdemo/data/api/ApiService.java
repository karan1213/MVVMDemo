package com.mvvm.kipl.mvvmdemo.data.api;


import com.mvvm.kipl.mvvmdemo.vo.Contributor;
import com.mvvm.kipl.mvvmdemo.vo.Repo;
import com.mvvm.kipl.mvvmdemo.vo.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by kipl104 on 04-Apr-17.
 */

public interface ApiService {

    @GET("users/{login}")
    Call<User> getUser(@Path("login") String login);

    @GET("users/{login}/repos")
    Call<List<Repo>> getRepos(@Path("login") String login);

    @GET("repos/{owner}/{name}")
    Call<Repo> getRepo(@Path("owner") String owner, @Path("name") String name);

    @GET("repos/{owner}/{name}/contributors")
    Call<List<Contributor>> getContributors(@Path("owner") String owner, @Path("name") String name);

    /*@GET("search/repositories")
    LiveData<Resource<RepoSearchResponse>> searchRepos(@Query("q") String query);

    @GET("search/repositories")
    Call<RepoSearchResponse> searchRepos(@Query("q") String query, @Query("page") int page);*/
}
