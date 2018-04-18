package com.mvvm.kipl.mvvmdemo.ui.activity;

import android.arch.lifecycle.ReportFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mvvm.kipl.mvvmdemo.R;
import com.mvvm.kipl.mvvmdemo.ui.fragment.RepoFragment;

public class RepoActivity extends AppCompatActivity {

    public static final String EXTRA_OWNER = "owner";
    public static final String EXTRA_REPO_NAME = "repo_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo);


        RepoFragment repoFragment = new RepoFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_OWNER, getIntent().getStringExtra(EXTRA_OWNER));
        args.putString(EXTRA_REPO_NAME, getIntent().getStringExtra(EXTRA_REPO_NAME));
        repoFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, repoFragment)
            .commitAllowingStateLoss();
    }
}
