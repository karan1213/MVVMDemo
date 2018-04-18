package com.mvvm.kipl.mvvmdemo.data.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by kipl104 on 04-Apr-17.
 */

public class JsonArrayResponse<T> extends BaseResponse {

    @SerializedName("data")
    public List<T> list;
}
