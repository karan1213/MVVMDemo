package com.mvvm.kipl.mvvmdemo.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;

import com.mvvm.kipl.mvvmdemo.R;
import com.mvvm.kipl.mvvmdemo.data.api.Status;
import com.mvvm.kipl.mvvmdemo.data.Resource;
import com.mvvm.kipl.mvvmdemo.base.activity.BaseActivity;
import com.mvvm.kipl.mvvmdemo.databinding.ActivityLoginBinding;
import com.mvvm.kipl.mvvmdemo.util.Constant;
import com.mvvm.kipl.mvvmdemo.util.StringUtility;
import com.mvvm.kipl.mvvmdemo.viewmodel.LoginViewModel;
import com.mvvm.kipl.mvvmdemo.vo.User;

public class LoginActivity extends BaseActivity {

    ActivityLoginBinding mBinding;
    LoginViewModel mLoginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mLoginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        mLoginViewModel.getUserLiveData().observe(this, new Observer<Resource<User>>() {
                @Override
                public void onChanged(@Nullable Resource<User> resource) {
                    //TODO check for login
                    if (resource != null) {
                        if (resource.getStatus() == Status.LOADING) {
                            enableLoadingBar(true);
                        }else if (resource.getStatus() == Status.SUCCESS){
                            enableLoadingBar(false);
                            getAlertDialogBuilder(null, "User loggedin: " + resource.body
                                .name, false).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(LoginActivity.this,
                                        UserRepositoryActivity.class));
                                }
                            }).show();
                            getPreferences().edit().putString(Constant.KEY_USER_ID, resource
                                .body.login).commit();
                        }else if (resource.getStatus() == Status.ERROR){
                            enableLoadingBar(false);
                            onError(resource.errorMessage);
                        }
                    }
                }
            });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                if (validateForm()) {
                    mLoginViewModel.login(mBinding.edtUsername.getText().toString());
                }
                break;
        }
    }

    private boolean validateForm(){
        boolean result = true;
        if (!StringUtility.validateEditText(mBinding.edtUsername)){
            mBinding.edtUsername.setError("Please enter user name");
            result = false;
        }else{
            mBinding.edtUsername.setError(null);
        }
        //check other fields here
        return result;
    }
}
