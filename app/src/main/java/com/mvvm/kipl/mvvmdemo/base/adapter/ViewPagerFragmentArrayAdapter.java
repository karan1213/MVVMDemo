package com.mvvm.kipl.mvvmdemo.base.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import com.mvvm.kipl.mvvmdemo.base.fragment.BaseFragment;

import java.util.List;

public class ViewPagerFragmentArrayAdapter extends FragmentStatePagerAdapter {

    private final FragmentManager fm;
    List<? extends BaseFragment> fragments;
    Context context;

    public ViewPagerFragmentArrayAdapter(Context context, FragmentManager fm, List<? extends BaseFragment> fragments) {
        super(fm);
        this.fm = fm;
        this.context = context;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments != null) {
            return fragments.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        return fragments != null ? fragments.size() : 0;
    }


}
