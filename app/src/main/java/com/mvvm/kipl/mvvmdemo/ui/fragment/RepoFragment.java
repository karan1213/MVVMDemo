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

import com.mvvm.kipl.mvvmdemo.R;
import com.mvvm.kipl.mvvmdemo.data.api.Status;
import com.mvvm.kipl.mvvmdemo.data.Resource;
import com.mvvm.kipl.mvvmdemo.base.adapter.RecyclerViewArrayAdapter;
import com.mvvm.kipl.mvvmdemo.base.fragment.BaseFragment;
import com.mvvm.kipl.mvvmdemo.base.listener.EndlessRecyclerOnScrollListener;
import com.mvvm.kipl.mvvmdemo.databinding.RepoFragmentBinding;
import com.mvvm.kipl.mvvmdemo.ui.activity.RepoActivity;
import com.mvvm.kipl.mvvmdemo.viewmodel.RepoViewModel;
import com.mvvm.kipl.mvvmdemo.vo.Contributor;
import com.mvvm.kipl.mvvmdemo.vo.Repo;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


/**
 * The UI Controller for displaying a Github Repo's information with its contributors.
 */
public class RepoFragment extends BaseFragment implements RecyclerViewArrayAdapter.OnItemClickListener {


    RepoFragmentBinding mBinding;
    RepoViewModel mRepoViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil
                .inflate(inflater, R.layout.repo_fragment, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRepoViewModel = ViewModelProviders.of(this).get(RepoViewModel.class);
        Bundle args = getArguments();
        if (args != null && args.containsKey(RepoActivity.EXTRA_OWNER) &&
            args.containsKey(RepoActivity.EXTRA_REPO_NAME)) {
            mRepoViewModel.setRepoOwner(args.getString(RepoActivity.EXTRA_OWNER));
            mRepoViewModel.setRepoName(args.getString(RepoActivity.EXTRA_REPO_NAME));
        }

        initRepo();
        initContributorList();
    }

    private void initRepo(){
        mRepoViewModel.getResponseLiveData().observe(this, new
            Observer<Resource<Repo>>() {
                @Override
                public void onChanged(@Nullable Resource<Repo> resource) {
                    //TODO check for login
                    if (resource != null) {
                        if (resource.getStatus() == Status.LOADING) {
                            enableLoadingBar(true);
                        } else if (resource.getStatus() == Status.SUCCESS) {
                            enableLoadingBar(false);
                            mBinding.setRepo(resource.body);
                        } else if (resource.getStatus() == Status.ERROR) {
                            enableLoadingBar(false);
                            onError(resource.errorMessage);
                        }
                    }
                }
            });
        mRepoViewModel.fetchRepo();
    }

    private void initContributorList() {
        mRepoViewModel.getContributorLiveData().observe(this, new
            Observer<Resource<List<Contributor>>>() {
                @Override
                public void onChanged(@Nullable Resource<List<Contributor>> resource) {
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
        mRepoViewModel.fetchContributors();
    }

    //Without pagination
    private void updateView(Resource<List<Contributor>> response) {
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
    public void onItemClick(View view, Object object) {

    }
}
