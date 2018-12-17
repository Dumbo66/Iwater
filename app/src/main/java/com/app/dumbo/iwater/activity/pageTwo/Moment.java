package com.app.dumbo.iwater.activity.pageTwo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by dumbo on 2018/7/23
 **/

public class Moment implements Parcelable {
    public String avatarUrl;
    public String nickName;
    public String time;
    public String content;
    public ArrayList<String> photos;
    public String address;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatarUrl);
        dest.writeString(this.nickName);
        dest.writeString(this.time);
        dest.writeString(this.content);
        dest.writeStringList(this.photos);
        dest.writeString(this.address);
    }

    public Moment() {
    }

    public Moment(String avatarUrl, String nickName, String time,
                  String content, ArrayList<String> photos, String address) {
        this.avatarUrl = avatarUrl;
        this.nickName = nickName;
        this.time=time;
        this.content = content;
        this.photos = photos;
        this.address=address;
    }

    protected Moment(Parcel in) {
        this.avatarUrl =in.readString();
        this.nickName =in.readString();
        this.time=in.readString();
        this.content = in.readString();
        this.photos = in.createStringArrayList();
        this.address=in.readString();
    }

    public static final Parcelable.Creator<Moment> CREATOR = new Parcelable.Creator<Moment>() {
        @Override
        public Moment createFromParcel(Parcel source) {
            return new Moment(source);
        }

        @Override
        public Moment[] newArray(int size) {
            return new Moment[size];
        }
    };
}