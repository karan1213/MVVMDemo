package com.mvvm.kipl.mvvmdemo.data.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kipl104 on 04-Apr-17.
 */

public class JsonObjectResponse<T> extends BaseResponse {

    @SerializedName("data")
    public T body;

}
