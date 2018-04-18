package com.mvvm.kipl.mvvmdemo.base.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.mvvm.kipl.mvvmdemo.BR;

/**
 * Created by kipl104 on 04-Apr-17.
 */

public class BaseModel extends BaseObservable implements Parcelable {

    public BaseModel() {
    }


    public boolean selected = false;

    @Bindable
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        notifyPropertyChanged(BR.selected);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    protected BaseModel(Parcel in) {
        this.selected = in.readByte() != 0;
    }

}
