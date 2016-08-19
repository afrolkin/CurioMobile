package com.andrewfrolkin.curiomobile.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by andrewfrolkin on 2016-07-14.
 */
public class User implements Parcelable{
    public final int id;
    public final String owner;
    public final String nick_name;
    public final String email;
    public final String avatar;
    public final String bio;
    public final String title;

    public User(int id, String owner, String nick_name, String email, String avatar, String bio, String title) {
        this.id = id;
        this.owner = owner;
        this.nick_name = nick_name;
        this.email = email;
        this.avatar = avatar;
        this.bio = bio;
        this.title = title;
    }

    protected User(Parcel in) {
        id = in.readInt();
        owner = in.readString();
        nick_name = in.readString();
        email = in.readString();
        avatar = in.readString();
        bio = in.readString();
        title = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(owner);
        dest.writeString(nick_name);
        dest.writeString(email);
        dest.writeString(avatar);
        dest.writeString(bio);
        dest.writeString(title);
    }
}

/*
        "id": 1,
        "owner": "jeff1",
        "nickname": "Jeff Avery",
        "email": "jeff1",
        "avatar": "https://pbs.twimg.com/profile_images/569208018844127232/LrFxEvoT.jpeg",
        "bio": "Jeff is Jeff!",
        "title": "Lecturer @ UWaterloo"
        }
        */