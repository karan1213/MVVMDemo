/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mvvm.kipl.mvvmdemo.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvvm.kipl.mvvmdemo.R;
import com.mvvm.kipl.mvvmdemo.data.api.Status;
import com.mvvm.kipl.mvvmdemo.data.Resource;
import com.mvvm.kipl.mvvmdemo.base.adapter.RecyclerViewArrayAdapter;
import com.mvvm.kipl.mvvmdemo.base.fragment.BaseFragment;
import com.mvvm.kipl.mvvmdemo.base.listener.EndlessRecyclerOnScrollListener;
import com.mvvm.kipl.mvvmdemo.databinding.UserFragmentBinding;
import com.mvvm.kipl.mvvmdemo.ui.activity.RepoActivity;
import com.mvvm.kipl.mvvmdemo.util.Constant;
import com.mvvm.kipl.mvvmdemo.viewmodel.UserRepositoryViewModel;
import com.mvvm.kipl.mvvmdemo.vo.Repo;

import java.util.List;


public class UserFragment extends BaseFragment implements RecyclerViewArrayAdapter
    .OnItemClickListener<Repo> {

    private UserRepositoryViewModel mUserRepositoryViewModel;
    UserFragmentBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.user_fragment,
            container, false);


        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mUserRepositoryViewModel = ViewModelProviders.of(this).get(UserRepositoryViewModel.class);
        mUserRepositoryViewModel.setUserName(getPreferences().getString(Constant.KEY_USER_ID,
            ""));
        mUserRepositoryViewModel.getUserRepositoriesLiveData().observe(this, new
            Observer<Resource<List<Repo>>>() {
                @Override
                public void onChanged(@Nullable Resource<List<Repo>> resource) {
                    //TODO check for login
                    if (resource != null) {
                        if (resource.getStatus() == Status.LOADING) {
                            enableLoadingBar(true);
                        } else if (resource.getStatus() == Status.SUCCESS) {
                            enableLoadingBar(false);
                            updateView(resource);
                        } else if (resource.getStatus() == Status.ERROR) {
                            enableLoadingBar(false);
                            onError(resource.errorMessage);
                        }
                    }
                }
            });

        initRepoList();
    }

    private void initRepoList() {
        mUserRepositoryViewModel.fetchUserRepos();
    }

    //Without pagination
    private void updateView(Resource<List<Repo>> response) {
        /*if (response.currentPage != 1 && response.list.size() == 0) {
            return;
        }*/
        if (mBinding.recyclerView.getAdapter() != null) {
            /*if (response.currentPage <= this.mResponse.currentPage) {
                ((RecyclerViewArrayAdapter) mBinding.recyclerViewRides.getAdapter()).update(response.list, true);
            } else {*/
            ((RecyclerViewArrayAdapter) mBinding.recyclerView.getAdapter()).addAll(response.body);
            /*}*/
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            mBinding.recyclerView.setLayoutManager(linearLayoutManager);
            RecyclerViewArrayAdapter adapter = new RecyclerViewArrayAdapter(response.body, this);
//            adapter.setEmptyTextView(mBinding.emptyView, R.string.default_empty_list_info);
            adapter.setFragment(this);
            mBinding.recyclerView.setAdapter(adapter);
            mBinding.recyclerView.getItemAnimator().setChangeDuration(0);
            mBinding.recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore() {
                    //Fetch next page
                }
            });
        }
    }


    @Override
    public void onItemClick(View view, Repo repo) {
        startActivity(new Intent(getActivity(), RepoActivity.class)
        .putExtra(RepoActivity.EXTRA_OWNER, repo.owner.login)
            .putExtra(RepoActivity.EXTRA_REPO_NAME, repo.name));
    }
}
