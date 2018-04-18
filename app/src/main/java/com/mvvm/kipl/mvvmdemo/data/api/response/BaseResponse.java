package com.mvvm.kipl.mvvmdemo.data.api.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mvvm.kipl.mvvmdemo.data.api.Status;

/**
 * Created by kipl104 on 04-Apr-17.
 */

public class BaseResponse {
    @SerializedName("message")
    @Expose
    public String message;

    /*@SerializedName("status")
    @Expose
    public int status;*/

    @SerializedName("meta")
    @Expose
    public Meta meta;

    private Status mStatus = Status.LOADING;

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mStatus = status;
    }

    public class Meta {

        @SerializedName("has_update")
        @Expose
        public Boolean hasUpdate;
        @SerializedName("force_update")
        @Expose
        public Boolean forceUpdate;
        @SerializedName("version")
        @Expose
        public String version;
        @SerializedName("link")
        @Expose
        public String link;

    }



}
