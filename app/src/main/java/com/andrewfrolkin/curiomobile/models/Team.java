package com.andrewfrolkin.curiomobile.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by andrewfrolkin on 2016-07-13.
 */
public class Team implements Parcelable {
    public final int id;
    public final String owner;

    public Team(int id, String owner) {
        this.id = id;
        this.owner = owner;
    }

    protected Team(Parcel in) {
        id = in.readInt();
        owner = in.readString();
    }

    public static final Creator<Team> CREATOR = new Creator<Team>() {
        @Override
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        @Override
        public Team[] newArray(int size) {
            return new Team[size];
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
    }
}

  /*
        "id": 2,
        "owner": "shaishav1"
        }
*/